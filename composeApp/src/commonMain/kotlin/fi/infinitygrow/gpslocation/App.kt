package fi.infinitygrow.gpslocation

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.infinitygrow.gpslocation.presentation.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import fi.infinitygrow.gpslocation.presentation.utils.CompassArrow
import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageString
import fi.infinitygrow.gpslocation.presentation.utils.getWeatherDescription
import org.jetbrains.compose.ui.tooling.preview.Preview

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.round

@Composable
@Preview
fun App() {
    MaterialTheme {

        val viewModel = koinViewModel<WeatherViewModel>()
        val locationService = koinInject<LocationService>()

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

val SkyBlueColor = Color(0xFF448EE4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel,
    locationService: LocationService
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }  // Refresh state

    fun refreshWeather() {
        scope.launch(Dispatchers.IO) {
            println("WeatherApp Checking location permission")
            if (locationService.isPermissionGranted()) {
                println("Permission granted, fetching location")
                val location = locationService.getLocation()
                location?.let {
                    println("Location received: $location")
                    viewModel.getCurrentWeatherInfo(it.latitude, it.longitude)
                    viewModel.getForecastInfo(it.latitude, it.longitude)
                    viewModel.getObservation(it.latitude, it.longitude)
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
                                viewModel.getObservation(it.latitude, it.longitude)
                            }
                        }
                    }
                    println("Permission denied")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshWeather()
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
        }
        Spacer(modifier = Modifier.height(24.dp))


        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { refreshWeather() }
        ) {

        uiState.observationInfo?.let { list ->

            LazyColumn {
                items(list) { observation ->
                    val validRows = listOf(
                        listOfNotNull(
                            observation.name.takeIf { it.isNotBlank() },
                            observation.temperature.takeIf { it.isFinite() }?.let { formatValue(it.toFloat()) + " C" },
                            observation.unixTime.convertUnixTimeToHHMM()
                        ),
                        listOfNotNull(
                            observation.windSpeed.takeIf { it.isFinite() }?.let { "$it m/s" },
                            observation.windGust.takeIf { it.isFinite() }?.let { "$it m/s" },
                            observation.windDirection.takeIf { it.isFinite() }?.let { "${it.toInt()} °" }
                        ),
                        listOfNotNull(
                            observation.precipitationAmount.takeIf { it.isFinite() }?.let { "$it mm/1h" },
                            observation.precipitationIntensity.takeIf { it.isFinite() }?.let { "$it mm/10min" },
                            observation.snowDepth.takeIf { it.isFinite() }?.let { "${it.toInt()} cm" }
                        ),
                        listOfNotNull(
                            observation.pressure.takeIf { it.isFinite() }?.let { "$it hPa" },
                            observation.cloudAmount.takeIf { it.isFinite() }?.let { "${it.toInt()}/8" },
                            observation.presentWeather.takeIf { it.isFinite() }?.let { getWeatherDescription(it.toInt()) }
                        )
                    ).filter { it.isNotEmpty() } // Remove empty rows

                    if (validRows.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .fillMaxWidth()
                                .background(Color.White, shape = MaterialTheme.shapes.medium)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            validRows.forEach { row ->
                                Row {
                                    row.forEachIndexed { index, text ->
                                        Text(text = text.toString())
                                        if (index != row.lastIndex) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                            CompassArrow(observation.windDirection)
                        }
                    }
                    constructLanguageString(
                        observation,
                        location = observation.coordinates
                    )?.let { Text (text = it) }
                }

//                    Column(
//                        modifier = Modifier
//                            .padding(horizontal = 12.dp, vertical = 8.dp)
//                            .fillMaxWidth()
//                            .background(Color.White, shape = MaterialTheme.shapes.medium)
//                            .padding(horizontal = 12.dp, vertical = 4.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Row{
//                            Text(text = observation.name)
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = formatValue(observation.temperature.toFloat()) + " C")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.unixTime.convertUnixTimeToISO8601())
//                            Spacer(modifier = Modifier.width(12.dp))
//                        }
//                        Row {
//                            Text(text = observation.windSpeed.toString() + " m/s")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.windGust.toString() + " m/s")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.windDirection.toString() + " °")
//                        }
//                        Row {
//                            Text(text = observation.precipitationAmount.toString() + " mm/1h")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.precipitationIntensity.toString() + " mm/10min")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.snowDepth.toString() + "cm")
//                        }
//                        Row {
//                            Text(text = observation.pressure.toString() + " hPa")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.cloudAmount.toInt().toString() + "/8")
//                            Spacer(modifier = Modifier.weight(1f))
//                            Text(text = observation.presentWeather.toString() + " auto")
//                        }
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

fun Long.convertUnixTimeToISO8601(): String {
    val dateTime = Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())  // Convert to local timezone


    return "${dateTime.year}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}T" +
            "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}:${dateTime.second.toString().padStart(2, '0')}"
}

fun Long.convertUnixTimeToHHMM(): String {
    val dateTime = Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())  // Convert to local timezone

    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

fun formatValue(float: Float): String {
    return (round(float * 10.0) / 10.0).toString()
}

//fun formatValue(float: Float): String {
//    return String.format(Locale.getDefault(), "%.2f", float)
//}