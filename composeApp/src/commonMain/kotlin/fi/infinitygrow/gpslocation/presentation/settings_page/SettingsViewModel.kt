package fi.infinitygrow.gpslocation.presentation.settings_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.data.repository.TextToSpeechHelperImpl
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceController
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val repository: SettingsRepository,
    private val locationService: LocationService,
    private val serviceController: WeatherServiceController,
    private val textToSpeechEngine: TextToSpeechHelperImpl
) : ViewModel() {

    val isPermissionGranted = locationService.isPermissionGranted()

    private val _talkServiceSwitch = MutableStateFlow(false)
    val talkServiceSwitch: StateFlow<Boolean> = _talkServiceSwitch

    // Service state
    private val _isServiceRunning = MutableStateFlow(serviceController.isServiceRunning())
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning


    // Expose dark theme as a StateFlow.
    val darkTheme: StateFlow<Boolean> = repository.darkThemeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Expose location toggle as a StateFlow.
    val isLocationOn: StateFlow<Boolean> = repository.locationFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val radius: StateFlow<Int> = repository.radiusFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 50)

    val ttsName: StateFlow<Boolean> = repository.ttsNameFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsDistance: StateFlow<Boolean> = repository.ttsDistanceFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsOneOrAll: StateFlow<Boolean> = repository.ttsOneOrAllFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsRoadObservations: StateFlow<Boolean> = repository.ttsRoadObservationsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val ttsTemperature: StateFlow<Boolean> = repository.ttsTemperatureFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val ttsHumidity: StateFlow<Boolean> = repository.ttsHumidityFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val ttsWindSpeed: StateFlow<Boolean> = repository.ttsWindSpeedFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsWindGust: StateFlow<Boolean> = repository.ttsWindGustFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsWindDirection: StateFlow<Boolean> = repository.ttsWindDirectionFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsCloudBase: StateFlow<Boolean> = repository.ttsCloudBaseFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsThermalHeight: StateFlow<Boolean> = repository.ttsThermalHeightFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsFlightLevel65: StateFlow<Boolean> = repository.ttsFlightLevel65Flow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val ttsFlightLevel95: StateFlow<Boolean> = repository.ttsFlightLevel95Flow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val textToSpeech: StateFlow<Boolean> = repository.textToSpeechFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)



    fun setTalkService(value: Boolean) {
        _talkServiceSwitch.value = value
    }

    // Call repository function to update dark theme.
    fun toggleDarkTheme() {
        viewModelScope.launch {
            repository.setDarkTheme(!darkTheme.value)
        }
    }

    fun toggleTtsName() {
        viewModelScope.launch {
            repository.setTtsName(!ttsName.value)
        }
    }
    fun toggleTtsDistance() {
        viewModelScope.launch {
            repository.setTtsDistance(!ttsDistance.value)
        }
    }
    fun toggleTtsOneOrAll() {
        viewModelScope.launch {
            repository.setTtsOneOrAll(!ttsOneOrAll.value)
        }
    }
    fun toggleTtsRoadObservations() {
        viewModelScope.launch {
            repository.setTtsRoadObservations(!ttsRoadObservations.value)
        }
    }
    fun toggleTtsTemperature() {
        viewModelScope.launch {
            repository.setTtsTemperature(!ttsTemperature.value)
        }
    }
    fun toggleTtsHumidity() {
        viewModelScope.launch {
            repository.setTtsHumidity(!ttsHumidity.value)
        }
    }
    fun toggleTtsWindSpeed() {
        viewModelScope.launch {
            repository.setTtsWindSpeed(!ttsWindSpeed.value)
        }
    }
    fun toggleTtsWindGust() {
        viewModelScope.launch {
            repository.setTtsWindGust(!ttsWindGust.value)
        }
    }
    fun toggleTtsWindDirection() {
        viewModelScope.launch {
            repository.setTtsWindDirection(!ttsWindDirection.value)
        }
    }
    fun toggleTtsCloudBase() {
        viewModelScope.launch {
            repository.setTtsCloudBase(!ttsCloudBase.value)
        }
    }
    fun toggleTtsThermalHeight() {
        viewModelScope.launch {
            repository.setTtsThermalHeight(!ttsThermalHeight.value)
        }
    }
    fun toggleTtsFlightLevel65() {
        viewModelScope.launch {
            repository.setTtsFlightLevel65(!ttsFlightLevel65.value)
        }
    }
    fun toggleTtsFlightLevel95() {
        viewModelScope.launch {
            repository.setTtsFlightLevel95(!ttsFlightLevel95.value)
        }
    }
    fun toggleTextToSpeech() {
        viewModelScope.launch {
            repository.setTtsFlightLevel95(!textToSpeech.value)
        }
    }
    fun clearPreferences() {
        viewModelScope.launch {
            repository.clearPreferences()
        }
    }



    // Updated function that returns success/failure
    suspend fun toggleLocation(): Boolean {
        // Check permission first
        if (!locationService.isPermissionGranted()) {
            return false // Permission denied
        }

        // Permission granted, toggle the setting
        repository.setLocationOn(!isLocationOn.value)
        return true // Success
    }

    suspend fun setRadius(radius: Int) {
        repository.setRadius(radius)
    }

    // Toggle service state
    fun toggleWeatherService() {
        serviceController.toggleWeatherService()
        _isServiceRunning.value = serviceController.isServiceRunning()
    }

    fun toggleTalkService() {
        viewModelScope.launch {
            serviceController.toggleWeatherService()
        }
    }

    fun speak() {
        textToSpeechEngine.speak("Tämän päivän sää. Todays weather.")
    }
    // Call repository function to update location setting.
//    fun toggleLocation() {
//        viewModelScope.launch {
//            repository.setLocationOn(!isLocationOn.value)
//        }
//    }
}
