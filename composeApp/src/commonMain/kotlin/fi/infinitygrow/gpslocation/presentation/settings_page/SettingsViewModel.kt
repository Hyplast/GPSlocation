package fi.infinitygrow.gpslocation.presentation.settings_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceController
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceImpl
import fi.infinitygrow.gpslocation.domain.repository.WeatherService
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow


class SettingsViewModel(
    private val repository: SettingsRepository,
    private val locationService: LocationService,
    //private val weatherService: WeatherServiceImpl,
    private val serviceController: WeatherServiceController,
) : ViewModel() {

    val isPermissionGranted = locationService.isPermissionGranted()

    // Service state
    private val _isServiceRunning = MutableStateFlow(serviceController.isServiceRunning())
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning


    // Expose dark theme as a StateFlow.
    val darkTheme: StateFlow<Boolean> = repository.darkThemeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Expose location toggle as a StateFlow.
    val isLocationOn: StateFlow<Boolean> = repository.locationFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // Call repository function to update dark theme.
    fun toggleDarkTheme() {
        viewModelScope.launch {
            repository.setDarkTheme(!darkTheme.value)
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
    // Call repository function to update location setting.
//    fun toggleLocation() {
//        viewModelScope.launch {
//            repository.setLocationOn(!isLocationOn.value)
//        }
//    }
}
