package fi.infinitygrow.gpslocation.presentation.observation_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import fi.infinitygrow.gpslocation.core.presentation.SkyBlueColor
import fi.infinitygrow.gpslocation.domain.model.locations
import fi.infinitygrow.gpslocation.presentation.observation_list.components.LocationSearchScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.components.ObservationsList
//import fi.infinitygrow.gpslocation.presentation.observation_list.components.WeatherDashboard
//import fi.infinitygrow.gpslocation.presentation.observation_list.components.WeatherObservation
import fi.infinitygrow.gpslocation.presentation.observation_list.components.WeatherSummary
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import fi.infinitygrow.gpslocation.presentation.observation_list.components.ObservationsRoadList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToSettings: () -> Unit,
    weatherViewModel: WeatherViewModel
) {
    val scope = rememberCoroutineScope()
    val uiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var state by remember { mutableStateOf(0) }
    val icons = listOf(Icons.Filled.Face, Icons.Filled.AccountBox, Icons.Filled.Warning)

    LaunchedEffect(Unit) {
        weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
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
                                message = "${location.name} sääasema lisätty."
                            )
                        }
                        weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
                    },
                    observationLocations = weatherViewModel.selectedLocations
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Tabs row stays fixed.
                SecondaryTabRow(selectedTabIndex = state) {
                    icons.forEachIndexed { index, icon ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            icon = { Icon(icon, contentDescription = "Tab $index") }
                        )
                    }
                }

                // Content container with weighted modifier to occupy the remaining space.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
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
                            weatherViewModel.refreshRoadWeather(selectedLocations = weatherViewModel.selectedLocations)
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

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
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

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToSettings: () -> Unit,
    weatherViewModel: WeatherViewModel
) {
    val scope = rememberCoroutineScope()
    val uiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var state by remember { mutableStateOf(0) }
    val icons = listOf(Icons.Filled.Face, Icons.Filled.AccountBox, Icons.Filled.Warning)

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
                if (weatherViewModel.getLocationPermission() && weatherViewModel.useLocation.value) {
                    WeatherSummary(
                        currentWeather = uiState.currentWeather,
                        isDarkTheme = weatherViewModel.isDarkTheme.value
                    )
                }
                LocationSearchScreen(
                    modifier = Modifier,
                    locations = locations,
                    onLocationSelected = { location ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "${location.name} sääasema lisätty.")
                        }
                        weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
                        //onLocationSelected(location)
                    },
                    observationLocations = weatherViewModel.selectedLocations
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    SecondaryTabRow(selectedTabIndex = state) {
                        icons.forEachIndexed { index, icon ->
                            Tab(
                                selected = state == index,
                                onClick = { state = index },
                                icon = { Icon(icon, contentDescription = "Favorite") }
                            )
                        }
                    }

                    when (state) {
                        0 -> {
                            // Content for the first tab with PullToRefreshBox
                            PullToRefreshBox(
                                isRefreshing = uiState.isRefreshing,
                                onRefresh = {
                                    weatherViewModel.refreshWeather(weatherViewModel.selectedLocations)
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
                        else -> {
                            // Content for other tabs
                            Text(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                text = "Icon tab ${state + 1} selected",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
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

 */


//                val weatherData = listOf(
//                    WeatherObservation(1678916600, 22.5f, 65.0f, 1013.2f, 3.2f),
//                    WeatherObservation(1678917200, 23.1f, 62.5f, 1012.8f, 4.1f),
//                    WeatherObservation(1678917800, 24.2f, 60.0f, 1012.5f, 4.5f),
//                    WeatherObservation(1678918400, 22.5f, 65.0f, 1013.2f, 3.2f),
//                    WeatherObservation(1678919000, 23.1f, 62.5f, 1012.8f, 4.1f),
//                    WeatherObservation(1678919600, 24.2f, 60.0f, 1012.5f, 4.5f),
//                    WeatherObservation(1678920200, 22.5f, 65.0f, 1013.2f, 3.2f),
//                    WeatherObservation(1678920800, 23.1f, 62.5f, 1012.8f, 4.1f),
//                    WeatherObservation(1678921400, 24.2f, 60.0f, 1012.5f, 4.5f),
//                    WeatherObservation(1678922000, 22.5f, 65.0f, 1013.2f, 3.2f),
//                    WeatherObservation(1678922600, 23.1f, 62.5f, 1012.8f, 4.1f),
//                    WeatherObservation(1678923200, 24.2f, 60.0f, 1012.5f, 4.5f),
//                )
//                WeatherDashboard(weatherData)
//Spacer(modifier = Modifier.height(24.dp))