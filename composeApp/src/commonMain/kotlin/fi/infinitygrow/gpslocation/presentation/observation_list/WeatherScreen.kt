package fi.infinitygrow.gpslocation.presentation.observation_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import fi.infinitygrow.gpslocation.core.presentation.SkyBlueColor
import fi.infinitygrow.gpslocation.domain.model.locations
import fi.infinitygrow.gpslocation.presentation.observation_list.components.LocationSearchScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.components.LocationSearchWithSnackbar
import fi.infinitygrow.gpslocation.presentation.observation_list.components.ObservationsList
import fi.infinitygrow.gpslocation.presentation.observation_list.components.WeatherSummary
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeatherScreen(
    //modifier: Modifier = Modifier,
    //viewModel: WeatherViewModel,
    //locationService: LocationService,
    //prefs: DataStore<Preferences>,
    onNavigateToSettings: () -> Unit,
    weatherViewModel: WeatherViewModel
) {
    val scope = rememberCoroutineScope()
    val uiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect preferences from DataStore. You might eventually move this to a separate
    // settings-view model.
//    val isDarkTheme by prefs.data
//        .map { it[booleanPreferencesKey("dark_theme")] ?: false }
//        .collectAsState(initial = false)
//    val isLocationOn by prefs.data
//        .map { it[booleanPreferencesKey("location")] ?: true }
//        .collectAsState(initial = true)

    // If the screen needs to refresh
    LaunchedEffect(Unit) {
        weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SkyBlueColor)
                .padding(padding)
        ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = SkyBlueColor),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            PreferencesSection(
//                isDarkTheme = isDarkTheme,
//                isLocationOn = isLocationOn,
//                onDarkThemeToggle = {
//                    scope.launch {
//                        prefs.edit { datastore ->
//                            val key = booleanPreferencesKey("dark_theme")
//                            datastore[key] = !(datastore[key] ?: false)
//                        }
//                    }
//                },
//                onLocationToggle = {
//                    scope.launch {
//                        // Check permission if needed, then update
//                        if (!locationService.isPermissionGranted()) {
//                            snackbarHostState.showSnackbar(
//                                message = "Location permission denied. Please change location settings."
//                            )
//                        }
//                        prefs.edit { datastore ->
//                            val key = booleanPreferencesKey("location")
//                            datastore[key] = !(datastore[key] ?: false)
//                        }
//                    }
//                }
//            )
            WeatherSummary(currentWeather = uiState.currentWeather, isDarkTheme = weatherViewModel.isDarkTheme.value)
            LocationSearchScreen(
                modifier = Modifier,
                locations = locations,
                onLocationSelected = { location ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message = "${location.name} sääasema lisätty.")
                    }
                    //onLocationSelected(location)
                },
                observationLocations = weatherViewModel.selectedLocations
            )
            Spacer(modifier = Modifier.height(24.dp))
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing, // You could have that within your uiState
                onRefresh = { weatherViewModel.refreshWeather(weatherViewModel.selectedLocations) }
            ) {
                uiState.observationInfo?.let { observations ->
                    ObservationsList(
                        observations = observations,
                        viewModel = weatherViewModel,
                        isDarkTheme = weatherViewModel.isDarkTheme.value
                    )
                }
            }
        }
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)  // adjust padding as needed
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Navigate to Settings",
                    tint = Color.White
                )
            }
        }
    }
}
