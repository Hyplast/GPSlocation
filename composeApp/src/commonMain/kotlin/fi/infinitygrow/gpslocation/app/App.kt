package fi.infinitygrow.gpslocation.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import fi.infinitygrow.gpslocation.core.presentation.LeafGreenColor
import fi.infinitygrow.gpslocation.core.presentation.SkyBlueColor
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.getObservationLocation
import fi.infinitygrow.gpslocation.domain.model.locations
import fi.infinitygrow.gpslocation.presentation.observation_list.components.LocationSearchScreen
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import fi.infinitygrow.gpslocation.presentation.observation_list.components.CompassArrow
import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageString
import fi.infinitygrow.gpslocation.presentation.utils.convertUnixTimeToHHMM
import fi.infinitygrow.gpslocation.presentation.utils.formatValue
import fi.infinitygrow.gpslocation.presentation.utils.getWeatherDescription
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.baseline_lock_open_24
import gpslocation.composeapp.generated.resources.twotone_lock_24
import org.jetbrains.compose.ui.tooling.preview.Preview


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {

        val viewModel = koinViewModel<WeatherViewModel>()
        val testViewModel = koinViewModel<TestViewModel>()
        val locationService = koinInject<LocationService>()

        //val prefs = koinInject<>()
        val prefs: DataStore<Preferences> = koinInject()
        val EXAMPLE_COUNTER = intPreferencesKey("example_counter")

        val counter by prefs
            .data
            .map {
                val counterKey = intPreferencesKey("counter")
                it[counterKey] ?: 0
            }
            .collectAsState(0)
        val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = counter.toString(),
                    textAlign = TextAlign.Center,
                    fontSize = 50.sp
                )
                Button(onClick = {
                    scope.launch {
                        prefs.edit { dataStore ->
                            val counterKey = intPreferencesKey("counter")
                            dataStore[counterKey] = counter + 1
                        }
                    }
                }) {
                    Text("Increment!")
                }
            }

        //MyLazyList(testViewModel)

        //Image(painterResource(Res.drawable.ic_cloudy), null)
        WeatherApp(
            modifier = Modifier
                .fillMaxSize(),
            viewModel = viewModel,
            locationService = locationService,
        )

        /*
        val factory = rememberPermissionsControllerFactory()
        val controller = remember(factory) {
            factory.createPermissionsController()
        }

        BindEffect(controller)

        val viewModel = viewModel {
            PermissionsViewModel(controller)
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when(viewModel.state) {
                PermissionState.Granted -> {
                    Text("Location permission granted!")
                }
                PermissionState.DeniedAlways -> {
                    Text("Permission was permanently declined.")
                    Button(onClick = {
                        controller.openAppSettings()
                    }) {
                        Text("Open app settings")
                    }
                }
                else -> {
                    Button(
                        onClick = {
                            viewModel.provideOrRequestRecordAudioPermission()
                        }
                    ) {
                        Text("Request permission")
                    }
                }
            }
        }

         */
    }
}

class TestViewModel : ViewModel() {
    // State to keep track of long-pressed items
    val longPressedItems = mutableStateListOf<ObservationLocation>()

    fun toggleLongPress(item: ObservationLocation) {
        if (longPressedItems.contains(item)) {
            longPressedItems.remove(item) // Remove if already long-pressed
        } else {
            longPressedItems.add(item) // Add if not already long-pressed
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyLazyList(viewModel: TestViewModel) {
    //val items = locations//List(20) { ListItem(it, "Item #$it") }
    //val colors = remember { mutableStateListOf(*Array(items.size) { Color.White }) }
    val haptics = LocalHapticFeedback.current

    LazyColumn {
        items(locations) { item ->
            val isLongPressed = viewModel.longPressedItems.contains(item)
            val backgroundColor = if (isLongPressed) Color.Red else Color.White

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(backgroundColor)
                    .combinedClickable(
                        onClick = {
                            // Navigate to the item on short click
                            //navController.navigate("itemDetail/${item.fmiId}") // Adjust the route as needed
                            // Short click changes color to a random color
                            //colors[index] = Color((0xFF000000..0xFFFFFFFF).random().toInt())
                        },
                        onLongClick = {
                            viewModel.toggleLongPress(item)
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            //colors[index] = Color.White
                        }
                    )
//                    .pointerInput(Unit) {
//                        detectTapGestures(
//                            onLongPress = {
//                                println("Long Clicked")
//                                println(item.name)
//                                colors[index] = Color((0xFF000000..0xFFFFFFFF).random().toInt())
//                            },
//                        )
//                    },
            )
            {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WeatherApp(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel,
    locationService: LocationService
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }  // Refresh state
    val selectedLocations = remember { mutableListOf<ObservationLocation>() }

    var shortPressedLocation by remember { mutableStateOf<ObservationLocation?>(null) }
    var longPressedLocation by remember { mutableStateOf<ObservationLocation?>(null) }

    var checked by remember { mutableStateOf(true) }
    //val colors = remember { mutableStateListOf(*Array(items.size) { Color.White }) }


    fun refreshWeather(selectedLocations: List<ObservationLocation>) {
        scope.launch(Dispatchers.IO) {
            println("WeatherApp Checking location permission")
            if (locationService.isPermissionGranted()) {
                println("Permission granted, fetching location")
                val location = locationService.getLocation()
                location?.let {
                    println("Location received: $location")
                    viewModel.getCurrentWeatherInfo(it.latitude, it.longitude)
                    viewModel.getForecastInfo(it.latitude, it.longitude)
                    if (checked) {
                        viewModel.getObservation(it.latitude, it.longitude, selectedLocations)
                    } else {
                        viewModel.getObservation(null, null, selectedLocations)
                    }
                }
            } else {
                println("Permission not granted, asking for permission")
                locationService.requestLocationPermission { granted ->
                    if (granted) {
                        scope.launch(Dispatchers.IO) {
                            val location = locationService.getLocation()
                            location?.let {
                                viewModel.getCurrentWeatherInfo(it.latitude, it.longitude)
                                viewModel.getForecastInfo(it.latitude, it.longitude)
                                if (checked) {
                                    viewModel.getObservation(it.latitude, it.longitude, selectedLocations)
                                } else {
                                    viewModel.getObservation(null, null, selectedLocations)
                                }
                            }

                        }
                    }
                    println("Permission denied")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshWeather(selectedLocations)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SkyBlueColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.error.isNotBlank()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.error, color = Color.Red)
            }
        }

        uiState.currentWeather?.let { weather ->
            Spacer(modifier = Modifier.height(32.dp))
//            AsyncImage(
//                model = weather.iconUrl,
//                contentDescription = "Weather Icon",
//                modifier = Modifier
//                    .width(200.dp)
//                    .height(100.dp)
//            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = formatValue(weather.temperature.toFloat()) + " C",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 60.sp,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = weather.name,
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
            )
            Spacer(modifier = Modifier.height(12.dp))


        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            LocationSearchScreen(
                modifier = Modifier
                    .weight(7f),
                locations,
                onLocationSelected = { location ->
                    // Handle location selection, e.g., show a toast or navigate
                    println("Selected location: ${location.name}")
                },
                observationLocations = selectedLocations
            )
            Switch(
                modifier = Modifier
                    .weight(3f)
                    .semantics { contentDescription = "Paikannus" },
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                refreshWeather(selectedLocations) }
        ) {

        uiState.observationInfo?.let { list ->
            isRefreshing = false
            LazyColumn(
                modifier = Modifier
                    .background(Color(0xFFF0F0F0)) // Light gray background for the list
                    .fillMaxSize()
            ) {
                items(viewModel.getNewestObservations(list)) { observation ->
                    val isLongPressed = viewModel.longPressedItems.contains(observation)
                    val backgroundColor = if (isLongPressed) LeafGreenColor else Color.White

                    val validRows = listOf(
                        listOfNotNull(
                            observation.name.takeIf { it.isNotBlank() },
                            observation.temperature.takeIf { it.isFinite() }
                                ?.let { formatValue(it.toFloat()) + " °C" },
                            observation.unixTime.convertUnixTimeToHHMM()
                        ),
                        listOfNotNull(
                            observation.windSpeed.takeIf { it.isFinite() }?.let { "$it m/s" },
                            observation.windGust.takeIf { it.isFinite() }?.let { "$it m/s" },
                            observation.windDirection.takeIf { it.isFinite() }
                                ?.let { "${it.toInt()} °" }
                        ),
                        listOfNotNull(
                            observation.precipitationAmount.takeIf { it.isFinite() }
                                ?.let { "$it mm/1h" },
                            observation.precipitationIntensity.takeIf { it.isFinite() }
                                ?.let { "$it mm/10min" },
                            observation.snowDepth.takeIf { it.isFinite() }
                                ?.let { "${it.toInt()} cm" }
                        ),
                        listOfNotNull(
                            observation.pressure.takeIf { it.isFinite() }?.let { "$it hPa" },
                            observation.cloudAmount.takeIf { it.isFinite() }
                                ?.let { "${it.toInt()}/8" },
                            observation.presentWeather.takeIf { it.isFinite() }
                                ?.let {
                                    val revice = getWeatherDescription(it.toInt())
                                    revice.first
                                    //getWeatherDescription(it.toInt())
                                }
                        )
                    ).filter { it.isNotEmpty() } // Remove empty rows

                    if (validRows.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                                .combinedClickable(
                                    onClick = {
                                        shortPressedLocation = getObservationLocation(observation)
                                    },
                                    onLongClick = {
                                        viewModel.toggleLongPress(observation)
                                        longPressedLocation = getObservationLocation(observation)
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                validRows.forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        row.forEachIndexed { index, text ->
                                            Text(
                                                text = text.toString(),
                                                fontSize = 16.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (observation.windDirection.isNaN() ||
                                        observation.windSpeed.isNaN() ||
                                        observation.windGust.isNaN()
                                    ) {
                                        // Don't call CompassArrow, or show a fallback UI.
                                    } else {
                                        CompassArrow(observation.windDirection, observation.windSpeed, observation.windGust)
                                    }
//                                    Icon(
//                                        painter = if (isLongPressed) {
//                                            painterResource(Res.drawable.baseline_lock_open_24)
//                                        } else painterResource(Res.drawable.twotone_lock_24),
//                                        contentDescription = if (isLongPressed) "Locked" else "Unlocked",
//                                        modifier = Modifier.padding(start = 8.dp)
//                                    )
//                                    observation.presentWeather.takeIf { it.isFinite() }
//                                        ?.let {
//                                            val revice = getWeatherDescription(it.toInt())
//                                            if (revice.second != null) Image(
//                                                painterResource(revice.second!!),
//                                                revice.first
//                                            )
//                                        }
                                }
                            }
                        }
                    }
                    constructLanguageString(
                        observation,
                        location = observation.coordinates
                    )?.let { Text(text = it, modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)) }
                }
            }
        }
        }


        //                        GlideImage()
        //                        (
        //                            model = forecast.iconUrl,
        //                            contentDescription = "Forecast Icon",
        //                            modifier = Modifier.size(30.dp)
        //                        )
//        Spacer(modifier = Modifier.height(24.dp))
//        uiState.forecastInfo?.let { list ->
//            LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                item {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        textAlign = TextAlign.Center,
//                        text = "Upcoming forecast",
//                        style = MaterialTheme.typography.headlineSmall.copy(color = Color.White)
//                    )
//                }
//                items(list) { forecast ->
//                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp, vertical = 8.dp)
//                            .fillMaxWidth()
//                            .background(Color.White, shape = MaterialTheme.shapes.medium)
//                            .padding(horizontal = 12.dp, vertical = 4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(text = forecast.date)
//                        Spacer(modifier = Modifier.weight(1f))
//                        Text(text = formatValue(forecast.temperature.toFloat()) + " C")
//                        Spacer(modifier = Modifier.weight(1f))
////                        GlideImage()
////                        (
////                            model = forecast.iconUrl,
////                            contentDescription = "Forecast Icon",
////                            modifier = Modifier.size(30.dp)
////                        )
//                        Spacer(modifier = Modifier.width(12.dp))
//                    }
//                }
//            }
//        }

    }
}


//fun formatValue(float: Float): String {
//    return String.format(Locale.getDefault(), "%.2f", float)
//}