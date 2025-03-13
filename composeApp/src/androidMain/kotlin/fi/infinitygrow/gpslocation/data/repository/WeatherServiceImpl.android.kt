package fi.infinitygrow.gpslocation.data.repository

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import fi.infinitygrow.gpslocation.R
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.FmiApiService
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.repository.TextToSpeechHelper
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import fi.infinitygrow.gpslocation.domain.repository.WeatherService
import fi.infinitygrow.gpslocation.presentation.permission.Location
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
//import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageString
import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageStringNonComposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes
import org.koin.android.ext.android.inject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class WeatherServiceImpl() : Service(), WeatherService {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var weatherJob: Job? = null
    private val weatherRepository: WeatherRepository by inject()
    private val locationService: LocationService by inject()
    private val textToSpeechHelper: TextToSpeechHelperImpl by inject()
    private val context: Context by inject()


    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "weather_channel"
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
            while (isActive) {
                try {
                    // Check for location permission
                    if (locationService.isPermissionGranted()) {
                        val location = locationService.getLocation()
                        location?.let { loc ->
                            // Get observation data
                            val observationLocations = emptyList<ObservationLocation>()//getObservationLocations() // Implement or inject this
                            val observations = weatherRepository.getObservation(
                                loc.latitude,
                                loc.longitude,
                                observationLocations
                            )

                            // Pick the closest or most relevant observation
                            val observation = observations.firstOrNull()
                            println(observation)

                            // Construct the language string using your existing function
                            val weatherSpeech = if (observation != null) {
                                constructLocalizedString(context, observation, loc)
                            } else {
                                ""
                                // Fallback to basic weather info
//                                "Lämpötila ${weather.temperature} astetta, " +
//                                        "olosuhde on ${weather.condition} ja " +
//                                        "ilmankosteus ${weather.humidity} prosenttia. " +
//                                        "Tuulen nopeus ${weather.windSpeed} metriä sekunnissa."
                            }

                            // Speak the weather information
                            weatherSpeech?.let {
                                textToSpeechHelper.speak(it)
                            }

                            // Update the notification
                            val notificationText = weatherSpeech ?: "Sää päivitys saatavilla"
                            val notification = createNotification(notificationText)
                            val notificationManager = getSystemService(
                                Context.NOTIFICATION_SERVICE
                            ) as NotificationManager
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }
                    } else {
                        // Handle permission not granted
                        val notification = createNotification(
                            context.getString(R.string.no_location_permission)
                        )
                        val notificationManager = getSystemService(
                            Context.NOTIFICATION_SERVICE
                        ) as NotificationManager
                        notificationManager.notify(NOTIFICATION_ID, notification)
                    }
                } catch (e: Exception) {
                    println(e)
                    // Log the error
                    println("WeatherService, Error fetching weather")
                }

                // Wait for a minute
                delay(1.minutes.inWholeMilliseconds)


            }
        }
    }


//    actual override fun startWeatherUpdates() {
//        weatherJob?.cancel()
//        weatherJob = serviceScope.launch {
//            while (isActive) {
//                try {
//                    val location = locationProvider.getCurrentLocation()
//                    val weather = weatherApi.getWeatherForLocation(
//                        location.latitude,
//                        location.longitude
//                    )
//
//                    val weatherSpeech = "Current weather: ${weather.temperature} degrees, " +
//                            "condition is ${weather.condition} with " +
//                            "${weather.humidity}% humidity and wind speed of " +
//                            "${weather.windSpeed} miles per hour."
//
//                    textToSpeechHelper.speak(weatherSpeech)
//
//                    // Update the notification
//                    val notification = createNotification(
//                        "Weather: ${weather.temperature}°, ${weather.condition}"
//                    )
//                    val notificationManager = getSystemService(
//                        Context.NOTIFICATION_SERVICE
//                    ) as NotificationManager
//                    notificationManager.notify(NOTIFICATION_ID, notification)
//
//                } catch (e: Exception) {
//                    // Log the error
//                }
//
//                // Wait for a minute
//                delay(TimeUnit.MINUTES.toMillis(1))
//            }
//        }
//    }

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


    private fun constructLocalizedString(context: Context, data: ObservationData?, location: Location): String {
        val parts = constructLanguageStringNonComposable(data, location)

        return parts.joinToString(" ") { (key, value) ->
            when (key) {
                "weather_station_name" -> context.getString(R.string.weather_station_name, value.toString())
                "distance_km" -> context.getString(R.string.distance_km, value.toString())
                "direction" -> context.getString(R.string.direction, value.toString())
                "rain_mm" -> context.getString(R.string.rain_mm, value.toString())
                "wind_speed" -> context.getString(R.string.wind_speed, value.toString())
                "wind_gust" -> context.getString(R.string.wind_gust, value.toString())
                "wind_direction" -> context.getString(R.string.wind_direction, value.toString())
                "clouds" -> context.getString(R.string.cloud_base, value.toString())
                else -> value.toString() // Fallback if no translation exists
            }
        }
    }

}

// Expect declaration that will be implemented differently on each platform
// In androidMain
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

//    actual fun isServiceRunning(): Boolean {
//        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        return manager.getRunningServices(Integer.MAX_VALUE)
//            .any { it.service.className == WeatherServiceImpl::class.java.name }
//    }

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
