package fi.infinitygrow.gpslocation.data.repository

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.NotificationCompat
import fi.infinitygrow.gpslocation.R
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import fi.infinitygrow.gpslocation.domain.repository.WeatherService
import fi.infinitygrow.gpslocation.presentation.permission.Location
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageStringNonComposable
import fi.infinitygrow.gpslocation.presentation.utils.getDistance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.minutes

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class WeatherServiceImpl : Service(), WeatherService {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var weatherJob: Job? = null
    private val weatherRepository: WeatherRepository by inject()
    private val locationService: LocationService by inject()
    private val textToSpeechHelper: TextToSpeechHelperImpl by inject()
    private val context: Context by inject()
    private val favoritesRepository: FavoritesRepositoryImpl by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val favorites: StateFlow<List<ObservationLocation>> =
        favoritesRepository.observeFavorites()
            .stateIn(serviceScope, SharingStarted.Lazily, emptyList())

    private val selectedLocations = mutableStateListOf<ObservationLocation>()

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "weather_channel"
        private val LOCATION_UPDATE_MUTEX = Mutex() // Mutex to control concurrent access to selectedLocations
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationName = context.getString(R.string.weather_service_running)
        val notification = createNotification(notificationName)
        startForeground(NOTIFICATION_ID, notification)
        startWeatherUpdates()
        return START_STICKY
    }

    override fun startWeatherUpdates() {
        weatherJob?.cancel()
        weatherJob = serviceScope.launch {
            try {
                launch {
                    favorites.collect { favList ->
                        updateSelectedLocations(favList)
                    }
                }

                val ttsSettings = async {
                    // fetch the settings from the repository only once
                    settingsRepository.ttsSettingsFlow.first().also {
                        println("Fetched TTS settings: $it")
                    }
                }.await()

                while (isActive) {
                    if (!locationService.isPermissionGranted()) {
                        notifyNoLocationPermission()
                    } else {
                        updateWeatherAndNotify(ttsSettings)
                    }
                    waitUntilNextObservation()
                }
            } catch (e: CancellationException) {
                println("Weather updates cancelled.")
            } catch (e: Exception) {
                handleWeatherUpdateError(e)
            }
        }
    }


    private suspend fun updateSelectedLocations(favList: List<ObservationLocation>) {
        LOCATION_UPDATE_MUTEX.withLock {
            selectedLocations.clear()
            selectedLocations.addAll(favList)
        }
    }

    private suspend fun updateWeatherAndNotify(ttsSettings: TtsSettings) {
        val location = locationService.getLocation()
        if (location != null) {
            val observations = fetchObservations(location)
            if (!ttsSettings.includeAllOrClosest)  {
                val closestObservation = getClosestObservationWithWind(observations, location.latitude, location.longitude)
                val favoriteList = getNewestObservationsForSelectedLocations(observations)
                val closestObservationList: List<ObservationData> =
                    listOfNotNull(closestObservation) + favoriteList
                val weatherSpeech = constructWeatherSpeech(closestObservationList, location, ttsSettings)
                if (weatherSpeech.isNotBlank()) {
                    textToSpeechHelper.speak(weatherSpeech)
                }
                notifyWeatherUpdate(weatherSpeech)
            } else {
                val weatherSpeech = constructWeatherSpeech(observations, location, ttsSettings)
                if (weatherSpeech.isNotBlank()) {
                    textToSpeechHelper.speak(weatherSpeech)
                }
                notifyWeatherUpdate(weatherSpeech)
            }
        }
    }

    /**
     * Returns the newest observation for each weather station that is in the selectedLocations list.
     *
     * @param observations the list of observations to search through.
     * @return a list of ObservationData representing the latest observation
     *         from each selected location.
     */
    private fun getNewestObservationsForSelectedLocations(
        observations: List<ObservationData>
    ): List<ObservationData> {
        // Create a set of station names from selectedLocations for quick lookup.
        val selectedStationNames = selectedLocations.map { it.name }.toSet()

        // Filter observations to include only those whose name is in the selected list.
        val filteredObservations = observations.filter { it.name in selectedStationNames }

        // Group the filtered observations by station name.
        val groupedObservations = filteredObservations.groupBy { it.name }

        // For each group, select the observation with the latest unixTime.
        return groupedObservations.mapNotNull { (_, obsList) ->
            obsList.maxByOrNull { it.unixTime }
        }
    }


    private suspend fun fetchObservations(location: Location): List<ObservationData> {
        return LOCATION_UPDATE_MUTEX.withLock {
            val observations = weatherRepository.getObservation(
                location.latitude,
                location.longitude,
                selectedLocations
            )
            getNewestObservationsWithWind(observations)
        }
    }


    private fun constructWeatherSpeech(observations: List<ObservationData>, location: Location, ttsSettings: TtsSettings): String {
        return observations.joinToString(separator = ". ") { observation ->
            constructLocalizedString(context, observation, location, ttsSettings)
        }
    }

    private fun notifyWeatherUpdate(weatherSpeech: String) {
        val notificationText = weatherSpeech.ifBlank { context.getString(R.string.weather_updates) }
        val notification = createNotification(notificationText)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun notifyNoLocationPermission() {
        val notification = createNotification(context.getString(R.string.no_location_permission))
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun handleWeatherUpdateError(e: Exception) {
        println(e)
        println("WeatherService, Error fetching weather")
        // Consider more robust error handling, like logging to a crash reporting service,
        // or displaying a user-friendly error message
    }

    private var sounding12ZUpdated = false  // Tracks whether the function was called in the time range

    private suspend fun waitUntilNextObservation() {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinutes = now.minute
        val nextUpdateMinutes = listOf(2, 12, 22, 32, 42, 52).firstOrNull { it > currentMinutes } ?: 2
        val minutesToWait = (nextUpdateMinutes - currentMinutes).let { if (it < 0) it + 60 else it }
//        println(minutesToWait)
//        println("Waiting this amount of minutes")

        // Check if the time is between UTC 12:00 and UTC 13:30
        if (now.hour in 12..13 && (now.hour != 13 || now.minute <= 30)) {
            if (!sounding12ZUpdated) {
                //getSounding()  // Call the function only once
                sounding12ZUpdated = getSounding()
            }
        } else {
            sounding12ZUpdated = false  // Reset flag after 13:30 UTC
        }

        delay(minutesToWait.minutes.inWholeMilliseconds)
    }


    private suspend fun getSounding(): Boolean {
        return true
    }

    override fun stopWeatherUpdates() {
        weatherJob?.cancel()
        textToSpeechHelper.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        weatherJob?.cancel()
        textToSpeechHelper.stop()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.weather_updates)
            val descriptionText = context.getString(R.string.weather_updates_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(content: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.weather_service))
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun constructLocalizedString(
        context: Context,
        data: ObservationData?,
        location: Location,
        ttsSettings: TtsSettings
    ): String {
        val parts = constructLanguageStringNonComposable(data, location, ttsSettings)

//        println("printing parts")
        println(parts)
//        println("DONE with parts")

        return parts.joinToString(" ") { (key, value) ->
            when (key) {
                "weather_station_name" -> context.getString(R.string.weather_station_name, value.toString())
                "distance_km" -> context.getString(R.string.distance_km, value.toString())
                //"direction" -> context.getString(R.string.direction, bearingToDirection(value).toString())
                "rain_mm" -> context.getString(R.string.rain_mm, value.toString())
                "wind_speed" -> context.getString(R.string.wind_speed, value.toString())
                "wind_gust" -> context.getString(R.string.wind_gust, value.toString())
                "wind_direction" -> context.getString(R.string.wind_direction, value.toString())
                "cloud_base" -> context.getString(R.string.cloud_base, value.toString())
                "thermal_height" -> context.getString(R.string.thermal_height, value.toString())
                "fl_65" -> context.getString(R.string.fl_65, value.toString())
                "fl_95" -> context.getString(R.string.fl_95, value.toString())
                else -> value.toString() // Fallback if no translation exists
            }
        }
    }

    private fun getNewestObservations(observations: List<ObservationData>): List<ObservationData> {
        // Group observations by name
        val groupedObservations = observations.groupBy { it.name }

        // Select the newest observation for each location
        return groupedObservations.map { (_, obsList) ->
            obsList.maxByOrNull { it.unixTime } // Get the observation with the latest unixTime
        }.filterNotNull() // Remove any nulls in case there were empty groups
    }

    private fun getNewestObservationsWithWind(observations: List<ObservationData>): List<ObservationData> {
        // Group observations by name
        val groupedObservations = observations.groupBy { it.name }

        // Select the newest observation for each location, filtering out those with invalid windSpeed
        return groupedObservations.mapNotNull { (_, obsList) ->
            obsList
                .filter { it.windSpeed.isFinite() && it.windSpeed != 0.0 } // Keep only valid windSpeed
                .maxByOrNull { it.unixTime } // Get the observation with the latest unixTime
        }
    }

    private fun getClosestObservationWithWind(
        observations: List<ObservationData>,
        userLat: Double,
        userLong: Double
    ): ObservationData? {
        return observations
            .filter { it.windSpeed.isFinite() && it.windSpeed != 0.0 } // Keep only valid wind speed
            .minByOrNull { getDistance(userLong, userLat, it.latitude, it.longitude) } // Find closest
    }


    private fun bearingToDirection(bearing: Double): String {
        val directions = arrayOf(context.getString(R.string.north), context.getString(R.string.north_east), context.getString(R.string.east), context.getString(R.string.south_east), context.getString(R.string.south), context.getString(R.string.south_west), context.getString(R.string.west), context.getString(R.string.north_west))
        val index = ((bearing + 22.5) / 45).toInt() and 7
        return directions[index]
    }


}

// Expect declaration that will be implemented differently on each platform
// In androidMain
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class WeatherServiceController(
    private val context: Context
) {
    companion object {
        // Static flag to track service state
        private var isRunning = false
    }

    actual fun isServiceRunning(): Boolean {
        return isRunning
    }

    actual fun startWeatherService() {
        val serviceIntent = Intent(context, WeatherServiceImpl::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        isRunning = true
    }

    actual fun stopWeatherService() {
        val serviceIntent = Intent(context, WeatherServiceImpl::class.java)
        context.stopService(serviceIntent)
        isRunning = false
    }

    actual fun toggleWeatherService() {
        if (isServiceRunning()) {
            stopWeatherService()
        } else {
            startWeatherService()
        }
    }
}
