package fi.infinitygrow.gpslocation.presentation.observation_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.icerock.moko.permissions.PermissionState
import fi.infinitygrow.gpslocation.core.presentation.SkyBlueColor
import fi.infinitygrow.gpslocation.domain.model.locations
import fi.infinitygrow.gpslocation.presentation.observation_list.components.LocationSearchScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.components.ObservationsList
import fi.infinitygrow.gpslocation.presentation.observation_list.components.ObservationsRoadList
import fi.infinitygrow.gpslocation.presentation.observation_list.components.RadiationList
import fi.infinitygrow.gpslocation.presentation.observation_list.components.SoundingDataListScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.components.WeatherSummary
import fi.infinitygrow.gpslocation.presentation.permissions_page.PermissionsViewModel
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.circlearrow_green
import gpslocation.composeapp.generated.resources.icon_balloon
import gpslocation.composeapp.generated.resources.icon_road_celsius
import gpslocation.composeapp.generated.resources.icon_thermometer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToSettings: () -> Unit,
    weatherViewModel: WeatherViewModel,
    permissionsViewModel: PermissionsViewModel
) {
    val permissionState = permissionsViewModel.state // Observe the state

    val scope = rememberCoroutineScope()
    val uiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var state by remember { mutableStateOf(0) }
    val icons = listOf(Icons.Filled.Face, Icons.Filled.AccountBox, Icons.Filled.Warning)
    val icons2 = listOf(painterResource(Res.drawable.icon_thermometer), painterResource(Res.drawable.icon_road_celsius), painterResource(Res.drawable.icon_balloon), painterResource(Res.drawable.circlearrow_green))
    val modifier = Modifier.fillMaxSize()

    LaunchedEffect(Unit) {
//        if (permissionState == PermissionState.NotDetermined) {
//            permissionsViewModel.provideOrRequestLocationPermission()
//        }
        delay(1000)
        weatherViewModel.refreshSounding()
        delay(1000)
        weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
        delay(1000)
        weatherViewModel.refreshRoadWeather(selectedLocations = weatherViewModel.selectedLocations)
        delay(1000)
        weatherViewModel.refreshRadiation()

    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SkyBlueColor)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SkyBlueColor)
            ) {
                when (permissionState) {
                    PermissionState.Granted -> {
                        // Top part (fixed content): WeatherSummary and LocationSearchScreen
                        if (weatherViewModel.getLocationPermission() &&
                            weatherViewModel.useLocation.value
                        ) {
                            WeatherSummary(
                                currentWeather = uiState.currentWeather,
                                isDarkTheme = weatherViewModel.isDarkTheme.value
                            )
                        }
                        LocationSearchScreen(
                            modifier = Modifier.fillMaxWidth(),
                            locations = locations,
                            onLocationSelected = { location ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "${location.name} +++."
                                    )
                                }
                                weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
                            },
                            observationLocations = weatherViewModel.selectedLocations
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Tabs row stays fixed.
                        SecondaryTabRow(selectedTabIndex = state) {
                            icons2.forEachIndexed { index, icon ->
                                Tab(
                                    selected = state == index,
                                    onClick = { state = index },
                                    icon = { Icon(
                                        icon,
                                        contentDescription = "Tab $index",
                                        modifier = Modifier.size(24.dp)
                                    ) }
                                )
                            }
                        }

                        // Content container with weighted modifier to occupy the remaining space.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                            //.weight(1f)
                        ) {
                            when (state) {
                                0 -> {
                                    // Content for the first tab with PullToRefreshBox
                                    PullToRefreshBox(
                                        isRefreshing = uiState.isRefreshing,
                                        onRefresh = {
                                            weatherViewModel.refreshWeather(
                                                weatherViewModel.selectedLocations
                                            )
                                        }
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
                                1 -> {

                                    PullToRefreshBox(
                                        isRefreshing = uiState.isRefreshing,
                                        onRefresh = {
                                            weatherViewModel.refreshRoadWeather(
                                                weatherViewModel.selectedLocations
                                            )
                                        }
                                    ) {
                                        uiState.roadObservationInfo?.let { observations ->
                                            ObservationsRoadList(
                                                observations = observations,
                                                viewModel = weatherViewModel,
                                                isDarkTheme = weatherViewModel.isDarkTheme.value
                                            )
                                        }
                                    }
                                }
                                2 -> {
                                    PullToRefreshBox(
                                        isRefreshing = uiState.isRefreshing,
                                        onRefresh = {
                                            weatherViewModel.refreshRadiation()
                                        }
                                    ) {
                                        uiState.radiationInfo?.let { observations ->
                                            RadiationList(
                                                observations = observations,
                                                viewModel = weatherViewModel,
                                                isDarkTheme = weatherViewModel.isDarkTheme.value
                                            )
                                        }
                                    }
                                }
                                3 -> {
                                    PullToRefreshBox(
                                        isRefreshing = uiState.isRefreshing,
                                        onRefresh = {
                                            weatherViewModel.refreshSounding()
                                        }
                                    ) {
                                        uiState.soundingInfo?.let { observations ->
                                            SoundingDataListScreen(
                                                modifier = modifier,
                                                soundingDataList = observations,
                                                isDarkTheme = weatherViewModel.isDarkTheme.value
                                            )
                                        }
                                    }
                                }
                                else -> {
                                    // Content for other tabs remains centered
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Icon tab ${state + 1} selected",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                    }
                    PermissionState.DeniedAlways -> {
                        Text(
                            "Location permission is required for this feature. " +
                                    "Please enable it in the app settings."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { permissionsViewModel.openAppSettings() }) {
                            Text("Open App Settings")
                        }
                    }
                    PermissionState.Denied, PermissionState.NotDetermined -> {
                        // --- Permission Denied or Not Yet Requested ---
                        Text("This app needs location permission to show local weather.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            permissionsViewModel.provideOrRequestLocationPermission()
                        }) {
                            Text("Request Location Permission")
                        }
                    }

                    PermissionState.NotGranted -> {
                        Text(
                            "Location permission is required for this feature. " +
                                    "Please enable it in the app settings."
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { permissionsViewModel.openAppSettings() }) {
                            Text("Open App Settings")
                        }
                    }
                }
            }

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Navigate to Settings",
                    tint = if (weatherViewModel.isDarkTheme.value) Color.Black else Color.White
                )
            }
        }
    }
}



