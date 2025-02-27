package fi.infinitygrow.gpslocation.presentation.settings_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val locationService: LocationService
) : ViewModel() {

    val isPermissionGranted = locationService.isPermissionGranted()

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

    suspend fun toggleTalkService() {
        viewModelScope.launch {
            repository.setDarkTheme(!darkTheme.value)
        }
    }
    // Call repository function to update location setting.
//    fun toggleLocation() {
//        viewModelScope.launch {
//            repository.setLocationOn(!isLocationOn.value)
//        }
//    }
}
