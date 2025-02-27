package fi.infinitygrow.gpslocation.presentation.observation_list

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import fi.infinitygrow.gpslocation.core.presentation.SkyBlueColor
import fi.infinitygrow.gpslocation.domain.model.locations
import fi.infinitygrow.gpslocation.presentation.observation_list.components.LocationSearchScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.components.ObservationsList
import fi.infinitygrow.gpslocation.presentation.observation_list.components.WeatherSummary
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToSettings: () -> Unit,
    weatherViewModel: WeatherViewModel
) {
    val scope = rememberCoroutineScope()
    val uiState by weatherViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
                WeatherSummary(
                    currentWeather = uiState.currentWeather,
                    isDarkTheme = weatherViewModel.isDarkTheme.value
                )
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
