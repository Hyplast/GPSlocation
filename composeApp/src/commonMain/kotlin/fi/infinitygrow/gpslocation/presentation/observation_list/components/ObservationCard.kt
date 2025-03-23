package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.multiplatform.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.multiplatform.cartesian.Scroll
import com.patrykandpatrick.vico.multiplatform.cartesian.Zoom
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.Axis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis.Companion.rememberBottom
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.Position
import com.patrykandpatrick.vico.multiplatform.common.component.LineComponent
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import com.patrykandpatrick.vico.multiplatform.common.fill
import com.patrykandpatrick.vico.multiplatform.common.shape.CorneredShape
import com.patrykandpatrick.vico.multiplatform.common.vicoTheme
import fi.infinitygrow.gpslocation.core.presentation.LeafGreenColor
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.domain.model.getObservationLocation
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.utils.calculateCloudBaseHeight
import fi.infinitygrow.gpslocation.presentation.utils.convertUnixTimeToHHMM
import fi.infinitygrow.gpslocation.presentation.utils.estimateLCL
import fi.infinitygrow.gpslocation.presentation.utils.estimateMaxAltitude
import fi.infinitygrow.gpslocation.presentation.utils.estimateMaxAltitudeFromGround
import fi.infinitygrow.gpslocation.presentation.utils.estimateMaxAltitudeNoLCL
import fi.infinitygrow.gpslocation.presentation.utils.formatValue
import fi.infinitygrow.gpslocation.presentation.utils.getWeatherDescriptionString
import fi.infinitygrow.gpslocation.presentation.utils.rememberMarker
import fi.infinitygrow.gpslocation.presentation.utils.selectClosestLatestSoundingProfile
import gpslocation.composeapp.generated.resources.ground
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.air_road_temp
import gpslocation.composeapp.generated.resources.baseline_lock_open_24
import gpslocation.composeapp.generated.resources.cloudiness
import gpslocation.composeapp.generated.resources.diffuse
import gpslocation.composeapp.generated.resources.direct
import gpslocation.composeapp.generated.resources.global_radiation
import gpslocation.composeapp.generated.resources.humidity
import gpslocation.composeapp.generated.resources.long_wave
import gpslocation.composeapp.generated.resources.no_valid_data_available
import gpslocation.composeapp.generated.resources.radiation
import gpslocation.composeapp.generated.resources.rh
import gpslocation.composeapp.generated.resources.snow
import gpslocation.composeapp.generated.resources.sunshine
import gpslocation.composeapp.generated.resources.surface
import gpslocation.composeapp.generated.resources.temp_n_dew
import gpslocation.composeapp.generated.resources.twotone_lock_24
import gpslocation.composeapp.generated.resources.wind_n_gust
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.Padding
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * Gets wind data from all observations matching the given name
 * @param observationsList The list of all observations
 * @param stationName The name to match
 * @return List of wind data triples (direction, speed, gust)
 */
fun getMatchingWindData(
    observationsList: List<ObservationData>,
    stationName: String
): List<Triple<Double, Double, Double>> {
    return observationsList
        .filter { it.name == stationName }
        .map { observation ->
            Triple(
                observation.windDirection,
                observation.windSpeed,
                observation.windGust
            )
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ObservationCard(
    observation: ObservationData,
    observationsList: List<ObservationData>,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    viewModel: WeatherViewModel,
) {
    // Toggle state for showing/hiding the chart
    var showChart by remember { mutableStateOf(false) }
    // Toggle state for selecting which chart to display
    var selectedChartIndex by remember { mutableStateOf(0) }
    // You can substitute these labels with your own desired chart combinations.
    val chartOptions = listOf(stringResource(Res.string.temp_n_dew), stringResource(Res.string.humidity), stringResource(Res.string.wind_n_gust))

    val cardBackground = if (isLongPressed) LeafGreenColor else Color.LightGray

    // Filter and prepare the observations based on the station name
    val currentStationName = observation.name
    val chartObservations = remember(observationsList, currentStationName) {
        observationsList.filter { it.name == currentStationName }.mapNotNull { observationData ->
            WeatherObservation(
                timestamp = observationData.unixTime,
                temperature = observationData.temperature.toFloat(),
                dewPoint = observationData.dewPoint.toFloat(),
                humidity = observationData.humidity.toFloat(),
                pressure = observationData.pressure.toFloat(),
                windSpeed = observationData.windSpeed.toFloat(),
                windGust = observationData.windGust.toFloat(),
                rainIntensity = observationData.precipitationIntensity.toFloat()
            )
        }
    }

    // Matching wind data remains as in your original code.
    val matchingWindData = remember(observationsList, currentStationName) {
        getMatchingWindData(observationsList, currentStationName)
    }

    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        showChart = !showChart
                        onShortPress()
                    },
                    onLongClick = onLongPress
                ),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top row with basic observation summary information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.unixTime.convertUnixTimeToHHMM(),
                            fontSize = 16.sp
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.name.split(" ", "-", "_", "/")
                                .take(2)
                                .joinToString(" "),
                            fontSize = 20.sp
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.temperature.takeIf { it.isFinite() }
                                ?.let { "${formatValue(it.toFloat())} °C" } ?: ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Second row with additional observation details.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        observation.presentWeather.takeIf { it.isFinite() }?.let { weatherVal ->
                            val (description, iconRes) = getWeatherDescriptionString(weatherVal.toInt())
                            var showDescription by remember { mutableStateOf(false) }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (iconRes != null) {
                                    Image(
                                        painter = painterResource(iconRes),
                                        contentDescription = description,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clickable { showDescription = !showDescription }
                                    )
                                    AnimatedVisibility(
                                        visible = showDescription,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                    ) {
                                        Text(
                                            text = description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(top = 4.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (
                            !observation.windDirection.isNaN() &&
                            !observation.windSpeed.isNaN() &&
                            !observation.windGust.isNaN()
                        ) {
                            var isAnimating by remember { mutableStateOf(false) }
                            var currentDataIndex by remember { mutableStateOf(0) }
                            val (bearing, speed, gust) = if (
                                isAnimating &&
                                matchingWindData.size > currentDataIndex
                            ) {
                                matchingWindData[currentDataIndex]
                            } else {
                                Triple(
                                    observation.windDirection,
                                    observation.windSpeed,
                                    observation.windGust
                                )
                            }
                            LaunchedEffect(isAnimating) {
                                if (isAnimating && matchingWindData.size > 1) {
                                    val delayPerSet = 3000L / matchingWindData.size.coerceAtMost(6)
                                    for (i in matchingWindData.indices.take(6)) {
                                        currentDataIndex = i
                                        delay(delayPerSet)
                                    }
                                    isAnimating = false
                                    currentDataIndex = 0
                                }
                            }
                            Box(
                                modifier = Modifier.clickable {
                                    if (matchingWindData.size > 1) {
                                        isAnimating = true
                                    }
                                }
                            ) {
                                CompassArrow(
                                    bearing = bearing,
                                    speed = speed,
                                    gust = gust
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            observation.pressure.takeIf { it.isFinite() && it != 0.0 }
                                ?.let { Text(text = "$it hPa") }
                            observation.cloudAmount.takeIf { it.isFinite() && it != 0.0 }
                                ?.let { Text(text = "${it.toInt()}/8 ${stringResource(Res.string.cloudiness)}") }
                            observation.precipitationIntensity.takeIf {
                                it.isFinite() && it != 0.0
                            }?.let { Text(text = "$it mm/10min") }
                            observation.snowDepth.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "${it.toInt()} cm ${stringResource(Res.string.snow)}")
                            }
                        }
                    }
                }

                // Show charts based on the toggle state
                AnimatedVisibility(visible = showChart) {
                    Column {
                        // A divider to separate the observation summary from the charts
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )

                        // Chart type selection buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            chartOptions.forEachIndexed { index, title ->
                                Text(
                                    text = title,
                                    color = if (index == selectedChartIndex)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .clickable { selectedChartIndex = index }
                                        .padding(8.dp)
                                )
                            }
                        }

                        // Toggle which chart to display based on selectedChartIndex
                        when (selectedChartIndex) {
                            0 -> {
                                // Temperature & Dew Point Chart
                                WeatherChart2(
                                    observations1 = chartObservations,
                                    observations2 = chartObservations,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    dataType1 = WeatherDataType.TEMPERATURE,
                                    dataType2 = WeatherDataType.DEW_POINT
                                )
                            }
                            1 -> {
                                // Humidity Chart (using just one data series)
                                WeatherChart(
                                    observations = chartObservations,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    dataType = WeatherDataType.HUMIDITY
                                )
                            }
                            2 -> {
                                // Wind Chart example, you could show wind speed vs. wind gust
                                WeatherChart2(
                                    observations1 = chartObservations,
                                    observations2 = chartObservations,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    dataType1 = WeatherDataType.WIND_SPEED,
                                    dataType2 = WeatherDataType.WIND_GUST
                                )
                            }
                        }
                    }
                }
                // Calculate LCL altitude
                val calculateCloudBaseHeights = getObservationLocation(observation)?.altitude?.let { calculateCloudBaseHeight(observation.temperature, observation.dewPoint, it.toDouble()) }
                val lclAltitude = estimateLCL(observation.temperature, observation.dewPoint)
                //val lclAltitude = groundAltitude + 38.1 * (groundTemperature - groundDewPoint)
                val lclTemp = observation.temperature - 9.8 * (lclAltitude / 1000.0)

                Text("temp:${observation.temperature} dew:${observation.dewPoint} alt:${getObservationLocation(observation)?.altitude}")

                if (calculateCloudBaseHeights != null) {
                    Text("CBH-${calculateCloudBaseHeights.roundToInt()}-")
                }
                Text("LCL:-${lclAltitude.roundToInt()}-${lclTemp.roundToInt()}-°C")

                // Get the sounding data from the view model (if available) and map to SoundingPoint.
                viewModel.uiState.value.soundingInfo?.let { soundingInfo ->
                    // Select the best sounding profile (full vertical profile as a list)
                    val selectedProfile = selectClosestLatestSoundingProfile(
                        soundingInfo,
                        observation.latitude,
                        observation.longitude
                    )

                    println("Altitude and size of sounding Points0")
                    println(selectedProfile?.get(0)?.altitude)
                    println(selectedProfile?.get(0)?.timeOfSounding)
                    println(selectedProfile?.get(0)?.name)
                    selectedProfile?.get(0)?.timeOfSounding?.let { Text(it) }
                    selectedProfile?.get(0)?.name?.let { Text(it) }

                    println(selectedProfile?.size)

                    val maxAltitud =
                        selectedProfile?.let {
                            getObservationLocation(observation)?.altitude?.let { it1 ->
                                estimateMaxAltitudeFromGround(it, observation.temperature, observation.dewPoint,
                                    it1.toDouble())
                            }
                        }

                    // Estimate max altitude
                    val maxAltitude =
                        selectedProfile?.let {
                            estimateMaxAltitude(lclAltitude, lclTemp,
                                it
                            )
                        }
                    val maxAltitude2 =
                        selectedProfile?.let {
                            getObservationLocation(observation)?.altitude?.let { altitude ->
                                estimateMaxAltitudeNoLCL(it, observation.temperature,
                                     selectedProfile[0].altitude //altitude.toDouble()
                                )

                            }

                        }
                    val maxAltitude3 =
                        selectedProfile?.let {
                            getObservationLocation(observation)?.altitude?.let { altitude ->
                                estimateMaxAltitudeNoLCL(it, observation.temperature,
                                    altitude.toDouble()
                                )

                            }

                        }

                     println(getObservationLocation(observation)?.altitude?.toDouble())
                     println("<-altitude.toDouble()--- selectedProfile[0].altitude->")
                     println(selectedProfile?.get(0)?.altitude)

                    if (maxAltitud != null) {
                        Text("FrGR-:${maxAltitud.roundToInt()}")

                    }
                    if (maxAltitude != null) {
                        if (maxAltitude2 != null) {
                            Text("EsMA-${maxAltitude.roundToInt()}")
                            Text("NoLCL-${maxAltitude2.roundToInt()}")
                            if (maxAltitude3 != null) {
                                Text("NoLCL-${maxAltitude3.roundToInt()}")
                            }
                        }
                    } else {
                        Text("No equilibrium level found")
                    }

                }

            }
        }
        // Overlay a lock icon at the top-end corner of the card.
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
                .padding(8.dp),
            painter = if (isLongPressed)
                painterResource(Res.drawable.twotone_lock_24)
            else
                painterResource(Res.drawable.baseline_lock_open_24),
            contentDescription = if (isLongPressed) "Locked" else "Unlocked"
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoadObservationCard(
    observation: RoadObservationData,
    observationsList: List<RoadObservationData>,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    backgroundColor: Color
) {
    // Toggle state for showing/hiding chart
    var showChart by remember { mutableStateOf(false) }
    // Index selection for chart type
    var selectedChartIndex by remember { mutableStateOf(0) }
    // Define chart options (feel free to adjust titles and charts)
    val chartOptions = listOf(
        stringResource(Res.string.air_road_temp), stringResource(Res.string.humidity), stringResource(Res.string.wind_n_gust))

    // Filter road observations by station name to display charts for related data
    val currentStationName = observation.name
    val chartObservations = remember(observationsList, currentStationName) {
        observationsList.filter { it.name == currentStationName }
    }


//    println("SIZE OF ObservationsRoadList")
//    println(chartObservations.size)

    val modifier = Modifier

    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        showChart = !showChart
                        onShortPress()
                    },
                    onLongClick = onLongPress
                ),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top row with basic summary info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Unix time display in HH:MM converted format
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.unixTime.convertUnixTimeToHHMM(),
                            fontSize = 16.sp
                        )
                    }
                    // Location name (only first two words/delimiters)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.name.split(" ", "-", "_", "/")
                                .take(3)
                                .joinToString(" "),
                            fontSize = 20.sp
                        )
                    }
                    // Display air temperature (or road surface temperature as preferred)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.airTemperature.takeIf { it.isFinite() }
                                ?.let { "${formatValue(it.toFloat())} °C" } ?: ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Second row with additional observation details.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Weather icon placeholder using precipitation codes field
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            observation.alertRoadCondition.takeIf { it.isFinite() }
                                ?.let { Text(text = "$it RC") }
                            observation.friction.takeIf { it.isFinite()  }
                                ?.let { Text(text = "${it} friction") }
                            observation.airTemperature2.takeIf { it.isFinite() }
                                ?.let { Text(text = "${it} airtemp2") }
                            observation.precipitationAmount .takeIf { it.isFinite() && it != 0.0}
                                ?.let { Text(text = "${it} preciAMoun") }
                            observation.precipitationIntensity.takeIf { it.isFinite() && it != 0.0}
                                ?.let { Text(text = "${it} preciInt") }
                            observation.precipitationCodes.takeIf { it.isFinite()}
                                ?.let { Text(text = "${it} precicodes") }
                        }
                    }
                    // Display wind data if available
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (
                            !observation.windDirection.isNaN() &&
                            !observation.windSpeed.isNaN() &&
                            !observation.windGust.isNaN()
                        ) {
                            // For this example, we use the current wind data.
                            CompassArrow(
                                bearing = observation.windDirection,
                                speed = observation.windSpeed,
                                gust = observation.windGust
                            )
                        }
                    }
                    // Additional details: multiple road temperatures and humidity related fields
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            observation.roadSurfaceTemperature.takeIf {
                                it.isFinite() && it != 0.0
                            }?.let {
                                val surface = stringResource(Res.string.surface)
                                Text(text = "$surface: ${formatValue(it.toFloat())} °C")
                            }
                            observation.roadGroundTemperature.takeIf {
                                it.isFinite() && it != 0.0
                            }?.let {
                                val ground = stringResource(Res.string.ground)
                                Text(text = "$ground: ${formatValue(it.toFloat())} °C")
                            }
                            observation.humidity.takeIf { it.isFinite() && it != 0.0 }?.let {
                                val rh = stringResource(Res.string.rh)
                                Text(text = "$rh: ${formatValue(it.toFloat())} %")
                            }
                        }
                    }
                }

                // Optionally show charts for selected data sets
                AnimatedVisibility(visible = showChart) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                        // Chart type selection buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            chartOptions.forEachIndexed { index, title ->
                                Text(
                                    text = title,
                                    color = if (index == selectedChartIndex)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .clickable { selectedChartIndex = index }
                                        .padding(2.dp)
                                )
                            }
                        }

                        // Toggle which chart to display based on chart selection.
                        when (selectedChartIndex) {
                            0 -> {
                                val temp3 = chartObservations
                                    .mapNotNull { observation ->
                                        observation.airTemperature?.takeIf { !it.isNaN() }
                                        observation.roadSurfaceTemperature?.takeIf { !it.isNaN() }
                                    }
                                RoadObservationDataGraph(modifier = modifier, roadDataList = chartObservations, temp3)
                                // Chart combining air temperature with road surface temperature
//                                RoadWeatherChart2(
//                                    observations1 = chartObservations,
//                                    observations2 = chartObservations,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(200.dp),
//                                    dataType1 = RoadWeatherDataType.AIR_TEMPERATURE,
//                                    dataType2 = RoadWeatherDataType.ROAD_SURFACE_TEMPERATURE
//                                )
                            }
                            1 -> {


                                // Humidity chart
//                                RoadWeatherChart(
//                                    observations = chartObservations,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(200.dp),
//                                    dataType = RoadWeatherDataType.HUMIDITY
//                                )
                            }
                            2 -> {
                                val temp5 = chartObservations
                                    .mapNotNull { observation ->
                                        observation.airTemperature?.takeIf { !it.isNaN() }
                                        observation.roadSurfaceTemperature?.takeIf { !it.isNaN() }
                                    }
                                if (
                                    !observation.windDirection.isNaN() &&
                                    !observation.windSpeed.isNaN() &&
                                    !observation.windGust.isNaN()
                                ) {
                                    WindGustChart(modifier = modifier, roadDataList = chartObservations, temp5)
                                }
                                // Wind chart example: wind speed vs. wind gust.
//                                RoadWeatherChart2(
//                                    observations1 = chartObservations,
//                                    observations2 = chartObservations,
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(200.dp),
//                                    dataType1 = RoadWeatherDataType.WIND_SPEED,
//                                    dataType2 = RoadWeatherDataType.WIND_GUST
//                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RadiationObservationCard(
    observation: RadiationData,
    observationsList: List<RadiationData>,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit,
    backgroundColor: Color
) {
    // Toggle state for showing/hiding chart
    var showChart by remember { mutableStateOf(false) }
    // Index selection for chart type
    var selectedChartIndex by remember { mutableStateOf(0) }
    // Define chart options
    val chartOptions = listOf(stringResource(Res.string.radiation), "UV Index", stringResource(Res.string.sunshine))

    // Filter radiation observations by station name (for future expansion)
    val currentStationName = observation.name
    val chartObservations = remember(observationsList, currentStationName) {
        observationsList.filter { it.name == currentStationName }
    }

    val modifier = Modifier

//    println("Size of RadiationObs ${chartObservations[0].name} card ${chartObservations.size}")
//    println("Name ${observation.name} unixTime ${observation.unixTime} globalRadiation ${observation.uvRadiation}")

    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        showChart = !showChart
                        onShortPress()
                    },
                    onLongClick = onLongPress
                ),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top row with basic summary info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Unix time display in HH:MM converted format
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.unixTime.convertUnixTimeToHHMM(),
                            fontSize = 16.sp
                        )
                    }
                    // Location name (only first two words/delimiters)
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.name.split(" ", "-", "_", "/")
                                .take(3)
                                .joinToString(" "),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    // Placeholder for a key summary value
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text =  "${stringResource(Res.string.global_radiation)}:${formatValue(observation.globalRadiation.toFloat())} W/m²", // Example: Use global radiation for summary
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Second row with detailed radiation information
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Details Column 1: Long wave radiation
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(Res.string.long_wave))
                        Text(
                            text = "In: ${formatValue(observation.longWaveIn.toFloat())}"
                        )
                        Text(
                            text = "Out: ${formatValue(observation.longWaveOut.toFloat())}"
                        )
                    }

                    // Details Column 2: Direct and Diffuse
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(Res.string.radiation))
                        Text(
                            text = "${stringResource(Res.string.direct)}: ${formatValue(observation.directRadiation.toFloat())}"
                        )
                        Text(
                            text = "${stringResource(Res.string.diffuse)}: ${formatValue(observation.diffuseRadiation.toFloat())}"
                        )
                    }

                    // Details Column 3: UV and Other
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "UV: ${formatValue(observation.uvRadiation.toFloat())}"
                        )
                        observation.sunshineDuration.takeIf { it.isFinite() }?.let {
                            Text(text = "${stringResource(Res.string.sunshine)}: ${formatValue(it.toFloat())}/60 s")
                        }
                    }
                }

                // Optionally show charts for selected data sets (for future expansion)
                AnimatedVisibility(visible = showChart) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )
                        // Chart type selection buttons (for future expansion)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            chartOptions.forEachIndexed { index, title ->
                                Text(
                                    text = title,
                                    color = if (index == selectedChartIndex)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .clickable { selectedChartIndex = index }
                                        .padding(8.dp)
                                )
                            }
                        }

                        when (selectedChartIndex) {
                            0 -> {
                                // Radiation Overview chart
                                val yGlob1min = chartObservations
                                    .mapNotNull { observation ->
                                        observation.globalRadiation?.takeIf { !it.isNaN() }
                                    }
                                if (yGlob1min.isNotEmpty()) {
                                    RadiationDataGraph(
                                        modifier = modifier,
                                        radiationDataList = chartObservations.filter {
                                            it.globalRadiation != null && !it.globalRadiation.isNaN()
                                        },
                                        yGlob1min
                                    )
                                } else {
                                    Text(text = "No valid global radiation data available")
                                }
                            }
                            1 -> {
                                val yUV1min = chartObservations
                                    .mapNotNull { observation ->
                                        observation.uvRadiation?.takeIf { !it.isNaN() }
                                    }
                                if (yUV1min.isNotEmpty()) {
                                    RadiationDataGraph(
                                        modifier = modifier,
                                        radiationDataList = chartObservations.filter {
                                            it.uvRadiation != null && !it.uvRadiation.isNaN()
                                        },
                                        yUV1min
                                    )
                                } else {
                                    Text(text = "No valid UV radiation data available")
                                }
                            }
                            2 -> {
                                val yDirect1min = chartObservations
                                    .mapNotNull { observation ->
                                        observation.directRadiation?.takeIf { !it.isNaN() }
                                    }
                                if (yDirect1min.isNotEmpty()) {
                                    RadiationDataGraph(
                                        modifier = modifier,
                                        radiationDataList = chartObservations.filter {
                                            it.directRadiation != null && !it.directRadiation.isNaN()
                                        },
                                        yDirect1min
                                    )
                                } else {
                                    Text(text = "No valid direct radiation data available")
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

//val temps2 = listOf(-10.4, -8.5, -8.4, -8.3, -8.3, -8.3, -8.3, -8.3, -8.1, -8.1, -8.2, -8.2, -8.0, -7.9, -7.9, -8.0, -8.0, -8.0, -8.1, -8.1, -8.1, -8.1, -8.2, -8.2, -8.2, -8.2, -8.1, -8.1, -8.1, -8.0, -8.0, -8.0, -8.1, -8.2, -8.3, -8.4, -8.5, -8.6, -8.7, -8.6, -8.5, -8.3, -8.2, -8.1, -8.2, -8.2, -8.3, -8.3, -8.4, -8.5, -8.6, -8.7, -8.8, -8.9, -9.0, -9.0, -9.1, -9.2, -9.2, -9.3, -9.4, -9.4, -9.5, -9.5, -9.6, -9.7, -9.8, -9.9, -10.0, -10.1, -10.1, -10.2, -10.3, -10.3, -10.4, -10.4, -10.6, -10.6, -10.7, -10.8, -10.8, -10.9, -11.0, -11.1, -11.1, -11.2, -11.3, -11.4, -11.5, -11.6, -11.7, -11.8, -11.9, -11.9, -12.0, -12.0, -12.1, -12.2, -12.3, -12.4, -12.5, -12.6, -12.7, -12.7, -12.8, -12.9, -13.0, -13.1, -13.2, -13.3, -13.4, -13.5, -13.6, -13.7, -13.7, -13.8, -13.8, -13.8, -13.9, -13.9, -13.9, -13.9, -14.0, -14.1, -14.1, -14.2, -14.2, -14.3, -14.4, -14.5, -14.5, -14.7, -14.7, -14.8, -14.9, -15.0, -15.1, -15.2, -15.3, -15.5, -15.6, -15.7, -15.8, -15.9, -16.0, -16.2, -16.3, -16.4, -16.4, -16.5)
//val des2 = listOf(-13.8, -12.9, -12.8, -12.9, -12.9, -12.8, -12.8, -12.9, -12.7, -12.7, -12.7, -12.7, -12.5, -12.5, -12.6, -12.6, -12.6, -12.5, -12.6, -12.5, -12.5, -12.5, -12.6, -12.6, -12.6, -12.6, -12.6, -12.7, -12.9, -13.0, -13.1, -13.1, -13.1, -13.1, -13.2, -13.2, -13.2, -13.2, -13.3, -13.6, -14.0, -14.4, -14.9, -15.3, -15.4, -15.4, -15.4, -15.5, -15.6, -15.7, -15.9, -16.1, -16.2, -16.5, -16.6, -16.8, -17.2, -17.2, -16.9, -16.6, -16.6, -16.6, -16.6, -16.8, -17.0, -17.0, -16.9, -17.0, -17.0, -17.2, -17.4, -17.6, -17.6, -17.5, -17.5, -17.6, -17.6, -17.8, -18.0, -18.1, -18.1, -18.2, -18.2, -18.3, -18.3, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.5, -18.8, -19.4, -19.9, -20.1, -20.1, -20.1, -20.1, -20.2, -20.2, -20.2, -20.2, -20.3, -20.4, -20.5, -20.6, -20.7, -20.8, -20.8, -20.9, -21.2, -21.4, -21.4, -21.5, -21.8, -22.2, -22.5, -22.7, -23.1, -23.4, -23.6, -23.6, -24.0, -24.1, -24.1, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.3, -24.2, -24.2, -24.3, -24.1, -24.2, -24.2, -24.2, -24.2, -24.3, -24.3, -24.4, -24.3, -24.3, -24.4)
//val alt2 = listOf(180.3, 202.2, 210.8, 220.4, 232.2, 244.4, 255.4, 266.0, 276.6, 286.2, 295.4, 305.4, 316.6, 328.5, 341.4, 354.5, 367.8, 380.6, 391.8, 401.6, 411.9, 423.9, 436.1, 446.2, 456.6, 469.4, 478.7, 486.8, 495.3, 503.2, 512.5, 523.5, 534.8, 545.8, 554.9, 563.3, 573.2, 582.2, 592.8, 605.0, 615.6, 627.2, 637.1, 644.4, 652.9, 659.2, 662.7, 672.5, 683.0, 692.8, 702.2, 713.4, 725.0, 734.8, 743.2, 753.3, 765.0, 775.3, 783.2, 790.5, 797.9, 805.1, 813.1, 822.1, 832.4, 842.6, 852.7, 864.2, 875.1, 884.6, 893.4, 902.9, 912.2, 920.7, 929.6, 940.9, 951.9, 961.1, 969.3, 978.2, 987.5, 996.2, 1004.2, 1011.8, 1020.8, 1031.1, 1041.2, 1050.1, 1057.6, 1065.5, 1074.1, 1082.5, 1092.2, 1103.2, 1114.1, 1124.6, 1134.3, 1143.7, 1154.0, 1165.5, 1176.4, 1185.2, 1193.3, 1201.4, 1210.9, 1222.7, 1234.3, 1245.4, 1256.9, 1267.3, 1277.2, 1287.1, 1297.0, 1307.6, 1308.7, 1318.3, 1327.8, 1337.4, 1347.2, 1355.7, 1364.8, 1375.3, 1385.7, 1396.0, 1405.9, 1416.9, 1429.1, 1440.3, 1450.3, 1459.7, 1469.3, 1480.0, 1490.4, 1501.8, 1513.3, 1523.6, 1534.6, 1546.7, 1558.9, 1570.7, 1581.0, 1591.2, 1602.8, 1615.9, 1628.4, 1639.9, 1649.6, 1658.4, 1666.6, 1676.0)


@Composable
fun SkewTChart(
    soundingData: List<SoundingData>,
    modifier: Modifier = Modifier
) {
    if (soundingData.isEmpty()) return

    val maxPoints = 300
    val truncatedData = soundingData.take(maxPoints)


    Box(
        modifier = modifier
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "${truncatedData[0].name} ${truncatedData[0].timeOfSounding}",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp)
        )

        // Find min and max values for temperature and altitude
        val minTemp = truncatedData.minOfOrNull { minOf(it.temperature, it.dewPoint) }?.toFloat()?.minus(5f) ?: 0f
        val maxTemp = truncatedData.maxOfOrNull { maxOf(it.temperature, it.dewPoint) }?.toFloat()?.plus(5f) ?: 0f
        val minAltitude = truncatedData.minOfOrNull { it.altitude }?.toFloat() ?: 0f
        val maxAltitude = truncatedData.maxOfOrNull { it.altitude }?.toFloat() ?: 0f


        // Temperature range
        val tempRange = maxTemp - minTemp
        // Altitude range
        val altitudeRange = maxAltitude - minAltitude

        //println("SkewTChart Min Altitude: $minAltitude, Max Altitude: $maxAltitude, Range: $altitudeRange")

        // X-axis (temperature) labels
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 40.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Temperature labels
            val steps = 5
            (0..steps).forEach { i ->
                val temp = minTemp + (tempRange * i / steps)
                Text(
                    text = "${temp.toInt()}°C",
                    style = TextStyle(fontSize = 12.sp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Y-axis (altitude) labels
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(top = 40.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Altitude labels
            val steps = 10
            (steps downTo 0).forEach { i ->
                val altitude = minAltitude + (altitudeRange * i / steps)
                Text(
                    text = "${altitude.toInt()}m",
                    style = TextStyle(fontSize = 12.sp),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Main chart area
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxSize()
                .padding(start = 40.dp, top = 40.dp, bottom = 30.dp)
        ) {
            val width = size.width
            val height = size.height

            //println("SkewTChartm Canvas Height: $height")

            // Draw x and y axis lines
            drawLine(
                color = Color.Gray,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 2f
            )

            if (truncatedData.size > 1) {
                val tempPath = Path()
                val dewPointPath = Path()

                val sortedData = truncatedData.sortedBy { it.altitude }
                //println("SkewTChart Sorted Altitudes: ${sortedData.map { it.altitude }}")
                // Find the X-coordinate for 0°C
                val zeroTempX = ((0f - minTemp) / tempRange) * width

                // Find the altitude closest to 850 hPa
                val closestTo850 = sortedData.minByOrNull { abs(it.pressure - 850) }
                val altitudeFor850 = closestTo850?.altitude ?: minAltitude
                val altitudeYFor850 = height - ((altitudeFor850.toFloat() - minAltitude) / altitudeRange) * height

                // Find the altitude closest to 796.81 hPa
                val closestTo797 = sortedData.minByOrNull { abs(it.pressure - 796.81) }
                val altitudeFor797 = closestTo797?.altitude ?: minAltitude
                val altitudeYFor797 = height - ((altitudeFor797.toFloat() - minAltitude) / altitudeRange) * height

                // Find the altitude closest to 710.44 hPa
                val closestTo710 = sortedData.minByOrNull { abs(it.pressure - 710.44) }
                val altitudeFor710 = closestTo710?.altitude ?: minAltitude
                val altitudeYFor710 = height - ((altitudeFor710.toFloat() - minAltitude) / altitudeRange) * height

                // Draw the dotted line for 0°C
                drawLine(
                    color = Color.Gray,
                    start = Offset(zeroTempX, 0f),
                    end = Offset(zeroTempX, height),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted line
                )

                // Draw the dotted line for altitude closest to 850 hPa
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, altitudeYFor850),
                    end = Offset(width, altitudeYFor850),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted line
                )

                // Draw the dotted line for altitude closest to 797 hPa
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, altitudeYFor797),
                    end = Offset(width, altitudeYFor797),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted line
                )

                // Draw the dotted line for altitude closest to 710 hPa
                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, altitudeYFor710),
                    end = Offset(width, altitudeYFor710),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted line
                )

                sortedData.forEachIndexed { index, data ->
                    // Calculate position for temperature
                    val tempX = ((data.temperature.toFloat() - minTemp) / tempRange) * width
                    val altY = height - ((data.altitude.toFloat() - minAltitude) / altitudeRange) * height

                    // Calculate position for dew point
                    val dewX = ((data.dewPoint.toFloat() - minTemp) / tempRange) * width

                    //println("SkewTChart Index: $index, TempX: $tempX, AltY: $altY, DewX: $dewX, Altitude: ${data.altitude}")

                    // Start or continue paths
                    if (index == 0) {
                        tempPath.moveTo(tempX, altY)
                        dewPointPath.moveTo(dewX, altY)
                    } else {
                        tempPath.lineTo(tempX, altY)
                        dewPointPath.lineTo(dewX, altY)
                    }

                    // Draw data points
                    drawCircle(
                        color = Color.Red,
                        radius = 3f,
                        center = Offset(tempX, altY)
                    )

                    drawCircle(
                        color = Color.Blue,
                        radius = 3f,
                        center = Offset(dewX, altY)
                    )
                }

                // Draw temperature line (red)
                drawPath(
                    path = tempPath,
                    color = Color.Red,
                    style = Stroke(width = 2f)
                )

                // Draw dew point line (blue)
                drawPath(
                    path = dewPointPath,
                    color = Color.Blue,
                    style = Stroke(width = 2f)
                )
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Temperature legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(color = Color.Red)
                }
                Text("Temperature", fontSize = 12.sp)
            }

            // Dew point legend
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(color = Color.Blue)
                }
                Text("Dew Point", fontSize = 12.sp)
            }
        }
    }
}



    private const val RANGE_PROVIDER_BASE = 0.1

    private val RangeProvider =
    object : CartesianLayerRangeProvider {
        override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) =
            -RANGE_PROVIDER_BASE * ceil(max(abs(minY), maxY) / RANGE_PROVIDER_BASE)

        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) =
            -getMinY(minY, maxY, extraStore)
    }

    private val StartAxisValueFormatter = CartesianValueFormatter.decimal(decimalCount = 1, suffix = " °C")
    private val StartAxisValueFormatterTemp = CartesianValueFormatter.decimal(decimalCount = 1, suffix = " °C")
    private val StartAxisValueFormatterRadiation = CartesianValueFormatter.decimal(decimalCount = 1, suffix = "W/m2")
    private val StartAxisValueFormatterWind = CartesianValueFormatter.decimal(decimalCount = 1, suffix = "m/s")
    //private val StartAxisValueFormatterSunshine = CartesianValueFormatter. .decimal(suffix = "s")
    private val StartAxisValueFormatterUV = CartesianValueFormatter.decimal(decimalCount = 1, suffix = "s")
    private val StartAxisValueFormatterAltitude = CartesianValueFormatter.decimal(decimalCount = 1, suffix = "m")


    private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(decimalCount = 1, suffix = " °C")
    private val MarkerValueFormatterRadiation = DefaultCartesianMarker.ValueFormatter.default(decimalCount = 1, suffix = "W/m2")
    private val MarkerValueFormatterWind = DefaultCartesianMarker.ValueFormatter.default(decimalCount = 1, suffix = "m/s")
    private val MarkerValueFormatterTemp = DefaultCartesianMarker.ValueFormatter.default(decimalCount = 1, suffix = " °C")
    private val MarkerValueFormatterSunshine = DefaultCartesianMarker.ValueFormatter.default(decimalCount = 1, suffix = "s")
    private val MarkerValueFormatterAltitude = DefaultCartesianMarker.ValueFormatter.default(decimalCount = 1, suffix = "m")

    private val BottomAxisValueFormatter =
    CartesianValueFormatter { _, value, _ -> value.toLong().convertUnixTimeToHHMM() }

    private fun getColumnProvider(positive: LineComponent, negative: LineComponent) =
    object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore,
        ) = if (entry.y >= 0) positive else negative

        override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) = positive
    }


@Composable
fun SoundingDataGraph(modifier: Modifier = Modifier, soundingDataList: List<SoundingData>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val maxPoints = 300
    val truncatedData = soundingDataList.take(maxPoints)

    val xTemp = truncatedData.map { it.temperature }
    val xDew = truncatedData.map { it.dewPoint }
    val yAlt = truncatedData.map { it.altitude }

    // Find the altitude closest to 796.81 hPa
    val closestTo797 = truncatedData.minByOrNull { abs(it.pressure - 796.81) }
    val altitudeFor797 = closestTo797?.altitude ?: truncatedData[0].altitude

    val closestTo710 = truncatedData.minByOrNull { abs(it.pressure - 710.44) }
    val altitudeFor710 = closestTo710?.altitude ?: truncatedData[0].altitude

    val minTemp = truncatedData.minOfOrNull { minOf(it.temperature, it.dewPoint) }?.toFloat()?.minus(5f) ?: 0f
    val maxTemp = truncatedData.maxOfOrNull { maxOf(it.temperature, it.dewPoint) }?.toFloat()?.plus(5f) ?: 0f




    val horizontalLine = HorizontalLine(
        y = { altitudeFor797 },
        line = rememberAxisGuidelineComponent(fill(Color.Cyan)),
        // Optionally, you can specify a label component:
        labelComponent = rememberAxisLabelComponent(),
        // Customize the label text if needed:
        label = { "Altitude: $altitudeFor797" },
        horizontalLabelPosition = Position.Horizontal.End,
        verticalLabelPosition = Position.Vertical.Top,
        labelRotationDegrees = 0.0f
    )
    val horizontalLine2 = HorizontalLine(
        y = { altitudeFor710 },
        line = rememberAxisGuidelineComponent(fill(Color.Cyan)),
        // Optionally, you can specify a label component:
        labelComponent = rememberAxisLabelComponent(),
        // Customize the label text if needed:
        label = { "Altitude: $altitudeFor710" },
        horizontalLabelPosition = Position.Horizontal.End,
        verticalLabelPosition = Position.Vertical.Top,
        labelRotationDegrees = 0.0f
    )


    // Example altitude (Pa) and temperature (°C) values
//    val pressures = y // hPa
//    val temperatures = x//listOf(15.0, 12.0, 9.0, 5.0, 0.0, -5.0, -10.0, -20.0, -30.0, -40.0) // °C

    // Transform to log scale for the Y-axis
    //val logPressures = pressures.map { kotlin.math.log10(it) }

    // Create a custom range provider for the specified ranges
//    val customRangeProvider = remember {
//        object : CartesianLayerRangeProvider {
//            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
//                return -70.0
//            }
//
//            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
//                return 30.0
//            }
//
//            override fun getMinX(minX: Double, maxX: Double, extraStore: ExtraStore): Double {
//                return 100.0
//            }
//
//            override fun getMaxX(minX: Double, maxX: Double, extraStore: ExtraStore): Double {
//                return 3500.0
//            }
//        }
//    }

    // Create scroll and zoom states with scrolling and zooming disabled
    val scrollState = rememberVicoScrollState(scrollEnabled = true)
    val zoomState = rememberVicoZoomState(zoomEnabled = true)

    LaunchedEffect(soundingDataList) {
        modelProducer.runTransaction {
            lineSeries {
                series(y = yAlt, x = xTemp)
                series(y = yAlt, x = xDew)
                //series(y = listOf(altitudeFor797, altitudeFor797), x = listOf(minTemp, maxTemp))
            }
        }
    }

    val lineColorRed = Color(0xffe32636)
    val lineColorBlue = Color(0xff0048ba)
    val lineColorCyan = Color.Cyan
    val lineColors = listOf(lineColorRed, lineColorBlue, lineColorCyan)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColorRed))),
                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColorBlue))),
                    //LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColorCyan))),
                ),
                rangeProvider = remember { CartesianLayerRangeProvider.auto() }
            ),
            decorations = remember { listOf(horizontalLine, horizontalLine2) }, // Add threshold line
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = StartAxisValueFormatterAltitude
            ),
            bottomAxis = rememberBottom(
                valueFormatter = { _, value, _ ->
                    // Use roundToInt() if you'd like to round, or toInt() to truncate.
                    value.roundToInt().toString()
                }
            ),
            marker = rememberMarker(MarkerValueFormatterAltitude),
            //fadingEdges = null,
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(534.dp).fillMaxWidth()
    )
}


private val BottomAxisValueFormatter00 =
    object : CartesianValueFormatter {
        private val dateTimeFormat =
            LocalTime.Format {
                amPmHour(Padding.SPACE)
                amPmMarker(" AM", " PM")
            }

        override fun format(
            context: CartesianMeasuringContext,
            value: Double,
            verticalAxisPosition: Axis.Position.Vertical?,
        ) = dateTimeFormat.format(LocalTime(value.toInt(), 0))
    }


@Composable
fun WindGustChart(modifier: Modifier = Modifier, roadDataList: List<RoadObservationData>, yParameterList: List<Double>) {
    if (roadDataList.isEmpty() || yParameterList.isEmpty() || roadDataList[0].windSpeed.isNaN()) {
        Box(
            modifier = modifier.height(234.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.no_valid_data_available))
        }
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    val xTime = roadDataList.map { it.unixTime }
    val yWind = roadDataList.map { it.windSpeed }
    val yGust = roadDataList.map { it.windGust }
    val yDirection = roadDataList.map { it.windDirection }

    val scrollState = rememberVicoScrollState(
        scrollEnabled = true,  // Enable scrolling for better user experience
        initialScroll = Scroll.Absolute.End,
        autoScrollCondition = AutoScrollCondition.Never
    )

    val zoomState = rememberVicoZoomState(
        zoomEnabled = true,  // Enable zooming for better user experience
        minZoom = Zoom.Content,
        maxZoom = Zoom.Content,
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Clear previous data
            lineSeries {
                series(
                    y = yWind,
                    x = xTime
                )
                series(y = yGust, x = xTime)
                //series(y = yDirection, x = xTime)
            }
        }
    }

    val lineColor = Color.Cyan
    val lineColor2 = Color.Green
    val lineColor3 = Color.Magenta
    val lineColors = listOf(lineColor, lineColor2)//, lineColor3)

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(

                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColor))),
                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColor2))),
//                            areaFill = LineCartesianLayer.AreaFill.single(
//                                fill(Brush.verticalGradient(listOf(color.copy(alpha = 0.4f), Color.Transparent)))
//                            ),
                ),
                rangeProvider = remember {
                    CartesianLayerRangeProvider.auto()
                } //RangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = StartAxisValueFormatterWind
            ),
            bottomAxis = rememberBottom(
                valueFormatter =  BottomAxisValueFormatter
            ),
            marker = rememberMarker(MarkerValueFormatterWind),
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(234.dp),
    )
}


@Composable
fun RoadObservationDataGraph(modifier: Modifier = Modifier, roadDataList: List<RoadObservationData>, yParameterList: List<Double>) {
    if (roadDataList.isEmpty() || yParameterList.isEmpty()) {
        Box(
            modifier = modifier.height(234.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.no_valid_data_available))
        }
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    val validDataPoints = min(roadDataList.size, yParameterList.size)
    val xTime = roadDataList.map { it.unixTime }
    val yAirTemp = roadDataList.map { it.airTemperature }
    val ySurfaceTemp = roadDataList.map { it.roadSurfaceTemperature }
    val yGroundTemp = roadDataList.map { it.roadGroundTemperature }
    val yDewPoints = roadDataList.map { it.dewPoint }
    val yValues = yParameterList.take(validDataPoints)

    val scrollState = rememberVicoScrollState(
        scrollEnabled = true,  // Enable scrolling for better user experience
        initialScroll = Scroll.Absolute.End,
        autoScrollCondition = AutoScrollCondition.OnModelGrowth
    )

    val zoomState = rememberVicoZoomState(
        zoomEnabled = true,  // Enable zooming for better user experience
        minZoom = Zoom.Content,
        maxZoom = Zoom.Content,
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Clear previous data
            lineSeries {
                series(
                    y = yAirTemp,
                    x = xTime
                )
                series(y = ySurfaceTemp, x = xTime)
                series(y = yGroundTemp, x = xTime)
                series(y = yDewPoints, x = xTime)
            }
        }
    }

    val lineColor = Color.Red
    val lineColor2 = Color.Black
    val lineColor3 = Color.Cyan
    val lineColor4 = Color.Blue
    val lineColors = listOf(lineColor, lineColor2, lineColor3, lineColor4)

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    lines = lineColors.map { color ->
                        LineCartesianLayer.Line(
                            LineCartesianLayer.LineFill.single(fill(color)),
//                            areaFill = LineCartesianLayer.AreaFill.single(
//                                fill(Brush.verticalGradient(listOf(color.copy(alpha = 0.4f), Color.Transparent)))
//                            ),
                        )

                    }
                ),
                rangeProvider = remember { CartesianLayerRangeProvider.auto() } //RangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = StartAxisValueFormatter
            ),
            bottomAxis = rememberBottom(
                valueFormatter =  BottomAxisValueFormatter
            ),
            marker = rememberMarker(MarkerValueFormatter),
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(234.dp),
    )
}


@Composable
fun RadiationDataGraph(modifier: Modifier = Modifier, radiationDataList: List<RadiationData>, yParameterList: List<Double>) {
    if (radiationDataList.isEmpty() || yParameterList.isEmpty()) {
        Box(
            modifier = modifier.height(234.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(Res.string.no_valid_data_available))
        }
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    val validDataPoints = min(radiationDataList.size, yParameterList.size)
    val xTime = radiationDataList.map { it.unixTime }
    val yValues = yParameterList.take(validDataPoints)

    val scrollState = rememberVicoScrollState(
        scrollEnabled = true,  // Enable scrolling for better user experience
        initialScroll = Scroll.Absolute.End,
        autoScrollCondition = AutoScrollCondition.OnModelGrowth
    )

    val zoomState = rememberVicoZoomState(
        zoomEnabled = true,  // Enable zooming for better user experience
        minZoom = Zoom.Content,
        maxZoom = Zoom.Content,
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Clear previous data
            lineSeries {
                series(
                    y = yValues,
                    x = xTime
                )
            }
        }
    }

    val lineColor = Color(0xffa485e0)

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(
                        LineCartesianLayer.LineFill.single(
                            fill(Color.Red)
                        ),
                        areaFill =
                            LineCartesianLayer.AreaFill.single(
                                fill(
                                    Brush.verticalGradient(listOf(lineColor.copy(alpha = 0.4f), Color.Transparent))
                                )
                            ),
                    )
                ),
                rangeProvider = remember { CartesianLayerRangeProvider.auto() } //RangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = StartAxisValueFormatterRadiation
            ),
            bottomAxis = rememberBottom(
                valueFormatter =  BottomAxisValueFormatter
            ),
            marker = rememberMarker(MarkerValueFormatterRadiation),
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(234.dp),
    )
}




@Composable
fun SoundingDataGraphCard2(modifier: Modifier,
    soundingDataList: List<SoundingData>) {
    // Create the model producer for the chart.
    val chartModelProducer = remember { CartesianChartModelProducer() }

    val maxPoints = 20
    val truncatedData = soundingDataList.take(maxPoints)

    val y = truncatedData.map { it.temperature }
    val x = truncatedData.map { it.altitude }

    println("Sounding parts 2")
    println(truncatedData[0])
//    val xValues = truncatedData.indices.map { it.toFloat() }
//    val yValues = truncatedData.map { it.altitude.toFloat() }

    // Prepare the chart data once the sounding data list is available.
    LaunchedEffect(soundingDataList) {
        chartModelProducer.runTransaction {
            // In this example we map each data point's temperature (for x-axis)
            // and altitude (for y-axis). Adjust the conversion if needed.
            lineSeries {
                series(x,y)
//                    truncatedData.map { it.temperature },
//                    truncatedData.map { it.altitude }
//                )
            }
        }
    }
    val positiveColumn =
        rememberLineComponent(
            fill = fill(Color(0xff0ac285)),
            thickness = 8.dp,
            shape = CorneredShape.rounded(topLeftPercent = 40, topRightPercent = 40),
        )
    val negativeColumn =
        rememberLineComponent(
            fill = fill(Color(0xffe8304f)),
            thickness = 8.dp,
            shape = CorneredShape.rounded(bottomLeftPercent = 40, bottomRightPercent = 40),
        )
    val lineComponent = rememberLineComponent(
        fill = fill(Color(0xff0ac285)),
        thickness = 3.dp,
    )

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            vicoTheme.lineCartesianLayerColors.map { color ->
                LineCartesianLayer.rememberLine(LineCartesianLayer.LineFill.single(fill(color)))
            }
        ),
        rangeProvider = RangeProvider
    )

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                lineLayer,
                startAxis = VerticalAxis.rememberStart(valueFormatter = StartAxisValueFormatter),
                bottomAxis = rememberBottom(labelRotationDegrees = 45f),
                marker = rememberMarker(MarkerValueFormatter),
            ),
        modelProducer = chartModelProducer,
        modifier = modifier.height(234.dp),
        scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
    )
}
