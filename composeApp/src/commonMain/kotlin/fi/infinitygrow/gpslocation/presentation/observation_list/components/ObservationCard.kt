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
import com.patrykandpatrick.vico.multiplatform.cartesian.Scroll
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.Axis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
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
import fi.infinitygrow.gpslocation.presentation.utils.convertUnixTimeToHHMM
import fi.infinitygrow.gpslocation.presentation.utils.formatValue
import fi.infinitygrow.gpslocation.presentation.utils.getWeatherDescriptionString
import fi.infinitygrow.gpslocation.presentation.utils.rememberMarker
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.baseline_lock_open_24
import gpslocation.composeapp.generated.resources.twotone_lock_24
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.round


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
    onLongPress: () -> Unit
) {
    // Toggle state for showing/hiding the chart
    var showChart by remember { mutableStateOf(false) }
    // Toggle state for selecting which chart to display
    var selectedChartIndex by remember { mutableStateOf(0) }
    // You can substitute these labels with your own desired chart combinations.
    val chartOptions = listOf("Temp & Dew", "Humidity", "Wind & Gust")

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
                                ?.let { Text(text = "${it.toInt()}/8 pilvisyys") }
                            observation.precipitationIntensity.takeIf {
                                it.isFinite() && it != 0.0
                            }?.let { Text(text = "$it mm/10min") }
                            observation.snowDepth.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "${it.toInt()} cm lunta")
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
    val chartOptions = listOf("Air & Road Temp", "Humidity", "Wind & Gust")

    // Filter road observations by station name to display charts for related data
    val currentStationName = observation.name
    val chartObservations = remember(observationsList, currentStationName) {
        observationsList.filter { it.name == currentStationName }
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
                                Text(text = "Surface: ${formatValue(it.toFloat())} °C")
                            }
                            observation.roadGroundTemperature.takeIf {
                                it.isFinite() && it != 0.0
                            }?.let {
                                Text(text = "Ground: ${formatValue(it.toFloat())} °C")
                            }
                            observation.humidity.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "RH: ${formatValue(it.toFloat())} %")
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
                                        .padding(8.dp)
                                )
                            }
                        }

                        // Toggle which chart to display based on chart selection.
                        when (selectedChartIndex) {
                            0 -> {
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
    // Toggle state for showing/hiding chart (for future expansion)
    var showChart by remember { mutableStateOf(false) }
    // Index selection for chart type (for future expansion)
    var selectedChartIndex by remember { mutableStateOf(0) }
    // Define chart options (for future expansion)
    val chartOptions = listOf("Radiation Overview") // Add more if needed

    // Filter radiation observations by station name (for future expansion)
    val currentStationName = observation.name
    val chartObservations = remember(observationsList, currentStationName) {
        observationsList.filter { it.name == currentStationName }
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
                            text =  "${formatValue(observation.globalRadiation.toFloat())} W/m²", // Example: Use global radiation for summary
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
                        Text(text = "Long Wave")
                        Text(
                            text = "In: ${formatValue(observation.longWaveIn.toFloat())} W/m²"
                        )
                        Text(
                            text = "Out: ${formatValue(observation.longWaveOut.toFloat())} W/m²"
                        )
                    }

                    // Details Column 2: Direct and Diffuse
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Radiation")
                        Text(
                            text = "Direct: ${formatValue(observation.directRadiation.toFloat())} W/m²"
                        )
                        Text(
                            text = "Diffuse: ${formatValue(observation.diffuseRadiation.toFloat())} W/m²"
                        )
                    }

                    // Details Column 3: UV and Other
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Other")
                        Text(
                            text = "UV: ${formatValue(observation.uvRadiation.toFloat())}"
                        )
                        observation.sunshineDuration.takeIf { it.isFinite() }?.let {
                            Text(text = "Sunshine: ${formatValue(it.toFloat())} h")
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

                        // Chart display area (for future expansion)
                        when (selectedChartIndex) {
                            0 -> {
                                // Radiation Overview chart placeholder

                                Text(text = "Radiation Overview Chart Placeholder")
                            }
                            // Add more chart types here as needed
                        }
                    }
                }
            }
        }
    }
}

//    val pressureModelProducer = remember { CartesianChartModelProducer() }
//    val temperatureModelProducer = remember { CartesianChartModelProducer() }
//    val dewPointModelProducer = remember { CartesianChartModelProducer() }
//    val windSpeedModelProducer = remember { CartesianChartModelProducer() }
//    val windDirectionModelProducer = remember { CartesianChartModelProducer() }

//    // We limit our dataset to 20 (or fewer) points.
//    val maxPoints = 20
//    val truncatedData = soundingDataList.take(maxPoints)
//    // For the x-axis we can simply use the indices.
//    val xValues = truncatedData.indices.map { it.toFloat() }


//val temps = truncatedData.map { it.temperature }
//val dews = truncatedData.map { it.dewPoint }
//val alts = truncatedData.map { it.altitude }
//
//val temps2 = listOf(-10.4, -8.5, -8.4, -8.3, -8.3, -8.3, -8.3, -8.3, -8.1, -8.1, -8.2, -8.2, -8.0, -7.9, -7.9, -8.0, -8.0, -8.0, -8.1, -8.1, -8.1, -8.1, -8.2, -8.2, -8.2, -8.2, -8.1, -8.1, -8.1, -8.0, -8.0, -8.0, -8.1, -8.2, -8.3, -8.4, -8.5, -8.6, -8.7, -8.6, -8.5, -8.3, -8.2, -8.1, -8.2, -8.2, -8.3, -8.3, -8.4, -8.5, -8.6, -8.7, -8.8, -8.9, -9.0, -9.0, -9.1, -9.2, -9.2, -9.3, -9.4, -9.4, -9.5, -9.5, -9.6, -9.7, -9.8, -9.9, -10.0, -10.1, -10.1, -10.2, -10.3, -10.3, -10.4, -10.4, -10.6, -10.6, -10.7, -10.8, -10.8, -10.9, -11.0, -11.1, -11.1, -11.2, -11.3, -11.4, -11.5, -11.6, -11.7, -11.8, -11.9, -11.9, -12.0, -12.0, -12.1, -12.2, -12.3, -12.4, -12.5, -12.6, -12.7, -12.7, -12.8, -12.9, -13.0, -13.1, -13.2, -13.3, -13.4, -13.5, -13.6, -13.7, -13.7, -13.8, -13.8, -13.8, -13.9, -13.9, -13.9, -13.9, -14.0, -14.1, -14.1, -14.2, -14.2, -14.3, -14.4, -14.5, -14.5, -14.7, -14.7, -14.8, -14.9, -15.0, -15.1, -15.2, -15.3, -15.5, -15.6, -15.7, -15.8, -15.9, -16.0, -16.2, -16.3, -16.4, -16.4, -16.5)
//val des2 = listOf(-13.8, -12.9, -12.8, -12.9, -12.9, -12.8, -12.8, -12.9, -12.7, -12.7, -12.7, -12.7, -12.5, -12.5, -12.6, -12.6, -12.6, -12.5, -12.6, -12.5, -12.5, -12.5, -12.6, -12.6, -12.6, -12.6, -12.6, -12.7, -12.9, -13.0, -13.1, -13.1, -13.1, -13.1, -13.2, -13.2, -13.2, -13.2, -13.3, -13.6, -14.0, -14.4, -14.9, -15.3, -15.4, -15.4, -15.4, -15.5, -15.6, -15.7, -15.9, -16.1, -16.2, -16.5, -16.6, -16.8, -17.2, -17.2, -16.9, -16.6, -16.6, -16.6, -16.6, -16.8, -17.0, -17.0, -16.9, -17.0, -17.0, -17.2, -17.4, -17.6, -17.6, -17.5, -17.5, -17.6, -17.6, -17.8, -18.0, -18.1, -18.1, -18.2, -18.2, -18.3, -18.3, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.4, -18.5, -18.8, -19.4, -19.9, -20.1, -20.1, -20.1, -20.1, -20.2, -20.2, -20.2, -20.2, -20.3, -20.4, -20.5, -20.6, -20.7, -20.8, -20.8, -20.9, -21.2, -21.4, -21.4, -21.5, -21.8, -22.2, -22.5, -22.7, -23.1, -23.4, -23.6, -23.6, -24.0, -24.1, -24.1, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.2, -24.3, -24.2, -24.2, -24.3, -24.1, -24.2, -24.2, -24.2, -24.2, -24.3, -24.3, -24.4, -24.3, -24.3, -24.4)
//val alt2 = listOf(180.3, 202.2, 210.8, 220.4, 232.2, 244.4, 255.4, 266.0, 276.6, 286.2, 295.4, 305.4, 316.6, 328.5, 341.4, 354.5, 367.8, 380.6, 391.8, 401.6, 411.9, 423.9, 436.1, 446.2, 456.6, 469.4, 478.7, 486.8, 495.3, 503.2, 512.5, 523.5, 534.8, 545.8, 554.9, 563.3, 573.2, 582.2, 592.8, 605.0, 615.6, 627.2, 637.1, 644.4, 652.9, 659.2, 662.7, 672.5, 683.0, 692.8, 702.2, 713.4, 725.0, 734.8, 743.2, 753.3, 765.0, 775.3, 783.2, 790.5, 797.9, 805.1, 813.1, 822.1, 832.4, 842.6, 852.7, 864.2, 875.1, 884.6, 893.4, 902.9, 912.2, 920.7, 929.6, 940.9, 951.9, 961.1, 969.3, 978.2, 987.5, 996.2, 1004.2, 1011.8, 1020.8, 1031.1, 1041.2, 1050.1, 1057.6, 1065.5, 1074.1, 1082.5, 1092.2, 1103.2, 1114.1, 1124.6, 1134.3, 1143.7, 1154.0, 1165.5, 1176.4, 1185.2, 1193.3, 1201.4, 1210.9, 1222.7, 1234.3, 1245.4, 1256.9, 1267.3, 1277.2, 1287.1, 1297.0, 1307.6, 1308.7, 1318.3, 1327.8, 1337.4, 1347.2, 1355.7, 1364.8, 1375.3, 1385.7, 1396.0, 1405.9, 1416.9, 1429.1, 1440.3, 1450.3, 1459.7, 1469.3, 1480.0, 1490.4, 1501.8, 1513.3, 1523.6, 1534.6, 1546.7, 1558.9, 1570.7, 1581.0, 1591.2, 1602.8, 1615.9, 1628.4, 1639.9, 1649.6, 1658.4, 1666.6, 1676.0)


//    println("MY temps and dews")
//    println(temps)
//    println(dews)
//    println("MY altitudes")
//    println(alts)


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
            text = "Temperature & Dew Point vs Altitude",
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


@Composable
fun SoundingDataGraphCard99(modifier: Modifier = Modifier, soundingDataList: List<SoundingData>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/z5ah6v.
            lineSeries { series(13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11,13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11,13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11,13, 8, 7, 12, 0, 1, 15, 14, 0, 11, 6, 12, 0, 11, 12, 11) }
        }
    }
    val fixedRangeProvider = CartesianLayerRangeProvider.fixed(
        minX = 0.0,
        maxX = 10.0,
        minY = 0.0,
        maxY = 20.0
    )


    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    rangeProvider = fixedRangeProvider // Apply fixed range provider here
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(),
            ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}


    private const val RANGE_PROVIDER_BASE = 0.1

    private val RangeProvider =
    object : CartesianLayerRangeProvider {
        override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) =
            -RANGE_PROVIDER_BASE * ceil(max(abs(minY), maxY) / RANGE_PROVIDER_BASE)

        override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) =
            -getMinY(minY, maxY, extraStore)
    }

    private val StartAxisValueFormatter = CartesianValueFormatter.decimal(suffix = " °C")

    private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(suffix = " °C")

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
fun SoundingDataGraphCard(modifier: Modifier = Modifier, soundingDataList: List<SoundingData>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val maxPoints = 150
    val truncatedData = soundingDataList.take(maxPoints)

    val x = truncatedData.map { it.temperature }
    val y = truncatedData.map { it.pressure }

    // Example altitude (Pa) and temperature (°C) values
    val pressures = y // hPa
    val temperatures = x//listOf(15.0, 12.0, 9.0, 5.0, 0.0, -5.0, -10.0, -20.0, -30.0, -40.0) // °C

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
    val scrollState = rememberVicoScrollState(scrollEnabled = false)
    val zoomState = rememberVicoZoomState(zoomEnabled = false)

    LaunchedEffect(soundingDataList) {
        modelProducer.runTransaction {
            lineSeries { series(x, y) }
        }
    }

    val lineColorRed = Color(0xffe32636)
    val lineColorBlue = Color(0xff0048ba)
    val lineColors = listOf(lineColorRed, lineColorBlue)

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(fill(lineColorRed))
                    )
                ),
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minX = x.minOrNull() ?: 0.0,
                    maxX = x.maxOrNull() ?: 0.0,
                    minY = y.minOrNull() ?: 0.0,
                    maxY = y.maxOrNull() ?: 0.0
                ),
                pointSpacing = 0.dp
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = StartAxisValueFormatter
            ),
            bottomAxis = HorizontalAxis.rememberBottom(

            ),
            fadingEdges = null,
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(234.dp).fillMaxWidth()
    )
}


@Composable
fun SoundingDataGraphCard11(modifier: Modifier = Modifier, soundingDataList: List<SoundingData>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val maxPoints = 200
    val truncatedData = soundingDataList.take(maxPoints)

    val y = truncatedData.map { it.temperature }
    val x = truncatedData.map { it.altitude }


    // Create a scroll state with auto-scrolling disabled
    val scrollState = rememberVicoScrollState(
        scrollEnabled = false,
        autoScrollCondition = AutoScrollCondition.Never
    )

    // Create a zoom state with zooming disabled
    val zoomState = rememberVicoZoomState(
        zoomEnabled = false
    )

    println(x)
    println(y)

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/3aqy4o.
            //columnSeries { series(y) }
            // Learn more: https://patrykandpatrick.com/z5ah6v.
            lineSeries { series(y,x) }
        }
    }

    CartesianChartHost(
        rememberCartesianChart(
//            rememberColumnCartesianLayer(
//                ColumnCartesianLayer.ColumnProvider.series(
//                    rememberLineComponent(fill = fill(Color(0xffffc002)), thickness = 16.dp)
//                )
//            ),
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(
                        LineCartesianLayer.LineFill.single(
                            fill(Color(0xffee2b2b)),
                        )
                    )
                ),
                rangeProvider = remember { CartesianLayerRangeProvider.fixed() }
            ),
            // Configure the start axis with your custom formatter
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = StartAxisValueFormatter,
                label = null
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
            ),
            // Set fadingEdges to null to remove the fading effect at the edges
            fadingEdges = null
        ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(634.dp),
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
                bottomAxis = HorizontalAxis.rememberBottom(labelRotationDegrees = 45f),
                marker = rememberMarker(MarkerValueFormatter),
            ),
        modelProducer = chartModelProducer,
        modifier = modifier.height(234.dp),
        scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
    )
}
    // Create and host the chart.
//    CartesianChartHost(
//        chart = rememberCartesianChart(
//            // Create a line layer. This will plot points connected by lines.
//            // If you want to only display points, adjust the configuration accordingly.
//            rememberLineCartesianLayer(
//                // The default configuration of the line layer can be extended with
//                // custom line and point visuals.
//            ),
//            // Use the vertical axis for altitude.
//            startAxis = VerticalAxis.rememberStart(
//                valueFormatter = { value, context,_ -> value.toString() }
//            ),
//            // Use the horizontal axis for temperature.
//            bottomAxis = HorizontalAxis.rememberBottom(
//                valueFormatter = { value, context,_ -> value.toString() }
//            )
//        ),
//        modelProducer = chartModelProducer,
//        modifier = Modifier.height(234.dp)
//    )

    //    LaunchedEffect(soundingDataList) {
//        // Prepare pressure data
//        pressureModelProducer.runTransaction {
//            lineSeries {
//                series(
//                    soundingDataList.take(20).mapIndexed { index, data ->
//                        data.pressure.toFloat()
//                    }
//                )
//            }
//        }
//
//        // Temperature
//        temperatureModelProducer.runTransaction {
//            lineSeries {
//                series(
//                    xValues = xValues,
//                    yValues = truncatedData.map { it.temperature.toFloat() }
//                )
//            }
//        }
//
//        // Prepare wind speed data
//        windSpeedModelProducer.runTransaction {
//            lineSeries {
//                series(
//                    soundingDataList.take(20).mapIndexed { index, data ->
//                        data.windSpeed.toFloat()
//                    }
//                )
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(MaterialTheme.colorScheme.surfaceVariant)
//            .padding(16.dp)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = name.split(" ", "-", "_", "/").take(3).joinToString(" "),
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Pressure Chart
//        Text(
//            text = "Pressure",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        val pressureLine = LineCartesianLayer.LineSpec(
//            LineCartesianLayer.LineStyle(
//                thicknessDp = 2f,
//                color = MaterialTheme.colorScheme.primary,
//            ),
//            pointSizeDp = 6f,
//            pointColor = MaterialTheme.colorScheme.primary
//        )
//        val pressurePointComponent = rememberShapeComponent(
//            shape = Shape.Rectangle,// pillShape,
//            fill = Fill.Black,
//            strokeThickness = 8.dp
//        )
//
//        CartesianChartHost(
//            chart = rememberCartesianChart(
//                rememberLineCartesianLayer(
//                    lines = listOf(
//                        LineCartesianLayer.LineSpec(
//                            line = pressureLine,
//                            pointComponent = pressurePointComponent
//                        )
//                    )
//                ),
//                startAxis = VerticalAxis.rememberStart(),
//                bottomAxis = HorizontalAxis.rememberBottom(
//                    itemCount = 4,
//                    valueFormatter = { value, _ -> value.toInt().toString() }
//                )
//            ),
//            modelProducer = pressureModelProducer,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Temperature Chart
//        androidx.compose.material3.Text(
//            text = "Temperature",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        val temperatureLine = rememberLineComponent(
//            fill = Fill.Black,
//            thickness = 2.dp
//        )
//        val temperaturePointComponent = rememberShapeComponent(
//            shape = Shape.Rectangle,
//            fill = Fill.Black,
//            strokeThickness = 8.dp
//        )
//
//        CartesianChartHost(
//            chart = rememberCartesianChart(
//                rememberLineCartesianLayer(
//                    lines = listOf(
//                        LineCartesianLayer.LineSpec(
//                            line = temperatureLine,
//                            pointComponent = temperaturePointComponent
//                        )
//                    )
//                ),
//                startAxis = VerticalAxis.rememberStart(),
//                bottomAxis = HorizontalAxis.rememberBottom(
//                    itemCount = 4,
//                    valueFormatter = { value, _ -> value.toInt().toString() }
//                )
//            ),
//            modelProducer = temperatureModelProducer,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Wind Speed Chart
//        androidx.compose.material3.Text(
//            text = "Wind Speed",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        val windSpeedLine = rememberLineComponent(
//            color = Color.Green,
//            thickness = 2.dp
//        )
//        val windSpeedPointComponent = rememberShapeComponent(
//            shape = Shapes.pillShape,
//            color = Color.Green,
//            size = 8.dp
//        )
//
//        CartesianChartHost(
//            chart = rememberCartesianChart(
//                rememberLineCartesianLayer(
//                    lines = listOf(
//                        LineCartesianLayer.LineSpec(
//                            line = windSpeedLine,
//                            pointComponent = windSpeedPointComponent
//                        )
//                    )
//                ),
//                startAxis = VerticalAxis.rememberStart(),
//                bottomAxis = HorizontalAxis.rememberBottom(
//                    itemCount = 4,
//                    valueFormatter = { value, _ -> value.toInt().toString() }
//                )
//            ),
//            modelProducer = windSpeedModelProducer,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(150.dp)
//        )
//    }
//}


/*
@Composable
fun SoundingDataGraphCard(soundingDataList: List<SoundingData>, name: String) {
    val pressureEntries = remember(soundingDataList) {
        ChartEntryModelProducer(soundingDataList.take(20).mapIndexed { index, data ->
            FloatEntry(index.toFloat(), data.pressure.toFloat())
        })
    }
    val temperatureEntries = remember(soundingDataList) {
        ChartEntryModelProducer(soundingDataList.take(20).mapIndexed { index, data ->
            FloatEntry(index.toFloat(), data.temperature.toFloat())
        })
    }
    val windSpeedEntries = remember(soundingDataList) {
        ChartEntryModelProducer(soundingDataList.take(20).mapIndexed { index, data ->
            FloatEntry(index.toFloat(), data.windSpeed.toFloat())
        })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name.split(" ", "-", "_", "/").take(3).joinToString(" "),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

        }
        Spacer(modifier = Modifier.height(8.dp))
        // Pressure Chart
        Text(
            text = "Pressure",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Chart(
            chart = lineChart(
                lines = listOf(lineSpec(lineColor = MaterialTheme.colorScheme.primary))
            ),
            chartModelProducer = pressureEntries,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(itemPlacer = AxisItemPlacer.Vertical.count(count = 4)),

            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Temperature Chart
        Text(
            text = "Temperature",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Chart(
            chart = lineChart(
                lines = listOf(lineSpec(lineColor = MaterialTheme.colorScheme.secondary))
            ),
            chartModelProducer = temperatureEntries,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(itemPlacer = AxisItemPlacer.Vertical.count(count = 4)),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        // WindSpeed Chart
        Text(
            text = "Wind Speed",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Chart(
            chart = lineChart(
                lines = listOf(lineSpec(lineColor = Color.Green))
            ),
            chartModelProducer = windSpeedEntries,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(itemPlacer = AxisItemPlacer.Vertical.count(count = 4)),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
    }
}

 */

/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ObservationCard3(
    observation: ObservationData,
    observationsList: List<ObservationData>,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit
) {
    var showChart by remember { mutableStateOf(false) }

    val cardBackground = if (isLongPressed) LeafGreenColor else Color.LightGray
    // First, get the current observation name
    val currentStationName = observation.name

    // Get all matching wind data
    val matchingWindData = remember(observationsList, currentStationName) {
        getMatchingWindData(observationsList, currentStationName)
    }

    // Prepare data for chart (convert ObservationData to your chart-compatible format)
    val chartObservations = remember(observationsList, currentStationName) {
        observationsList
            .filter { it.name == currentStationName }
            .mapNotNull { observationData ->
                // Convert ObservationData to WeatherObservation
                // You'll need to adjust this based on your exact ObservationData structure
                WeatherObservation(
                    timestamp = observationData.unixTime,
                    temperature = observationData.temperature.toFloat(),
                    dewPoint = observationData.dewPoint.toFloat(),
                    humidity = observationData.humidity.toFloat(),
                    pressure = observationData.pressure.toFloat(),
                    windSpeed = observationData.windSpeed.toFloat(),
                    windGust = observationData.windGust.toFloat(),
                    rainIntensity = observationData.precipitationIntensity.toFloat(),
                )
            }
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
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = observation.unixTime.convertUnixTimeToHHMM(),
                                fontSize = 16.sp
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically // Add this line
                            )  {
                                Text(
                                    text = observation.name.split(" ", "-", "_", "/")
                                        .take(2)
                                        .joinToString(" "),
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = observation.temperature.takeIf { it.isFinite() }?.let { "${formatValue(it.toFloat())} °C" } ?: ""
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                        /*.border(1.dp, Color.Blue)*/,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    )  {
                        Box(
                            modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                            contentAlignment = Alignment.Center
                        ) {
                            observation.presentWeather.takeIf { it.isFinite() }?.let { weatherVal ->
                                val (description, iconRes) = getWeatherDescription(weatherVal.toInt())

                                if (iconRes != null) {
                                    // If we have an icon, show it with click functionality to reveal text
                                    var showDescription by remember { mutableStateOf(false) }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(iconRes),
                                            contentDescription = description,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clickable { showDescription = !showDescription }
                                        )

                                        // Show description text only when the image is clicked
                                        AnimatedVisibility(
                                            visible = showDescription,
                                            enter = fadeIn() + expandVertically(),
                                            exit = fadeOut() + shrinkVertically()
                                        ) {
                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // If no icon is available, just show the description text
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                            contentAlignment = Alignment.Center
                        ) {
                            if (
                                !observation.windDirection.isNaN() &&
                                !observation.windSpeed.isNaN() &&
                                !observation.windGust.isNaN()
                            ) {
                                // Remember animation state
                                var isAnimating by remember { mutableStateOf(false) }

                                // Remember current data set index
                                var currentDataIndex by remember { mutableStateOf(0) }

                                // Get current parameters - either from animation or default
                                val (bearing, speed, gust) = if (isAnimating && matchingWindData.size > currentDataIndex) {
                                    matchingWindData[currentDataIndex]
                                } else {
                                    Triple(observation.windDirection, observation.windSpeed, observation.windGust)
                                }
                                //println("doing som")
                                //println(matchingWindData)
                                // Create effect to handle animation
                                LaunchedEffect(isAnimating) {
                                    //println("IsANimatin")
                                    if (isAnimating && matchingWindData.size > 1) {
                                        // Cycle through all matching data over 3 seconds
                                        val delayPerSet = 3000L / matchingWindData.size.coerceAtMost(6)

                                        for (i in matchingWindData.indices.take(6)) {
                                            currentDataIndex = i
                                            delay(delayPerSet)
                                        }

                                        // Reset after animation completes
                                        isAnimating = false
                                        currentDataIndex = 0
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            if (matchingWindData.size > 1) {
                                                isAnimating = true
                                            } else {

                                            }
                                        }
                                ) {

                                    // Render the compass with current parameters and click handler
                                    CompassArrow(
                                        bearing = bearing,
                                        speed = speed,
                                        gust = gust
                                    )
                                }


//                            // The CompassArrow composable can be customized as needed.
//                            CompassArrow(
//                                bearing = observation.windDirection,
//                                speed = observation.windSpeed,
//                                gust = observation.windGust
//                            )
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column {
                                observation.pressure.takeIf { it.isFinite() && it != 0.0 }?.let {
                                    Text(text = "$it hPa")
                                }
                                observation.cloudAmount.takeIf { it.isFinite() && it != 0.0 }?.let {
                                    Text(text = "${it.toInt()}/8 pilvisyys")
                                }
                                observation.precipitationIntensity.takeIf { it.isFinite() && it != 0.0 }?.let {
                                    Text(text = "$it mm/10min")
                                }
                                observation.snowDepth.takeIf { it.isFinite() && it != 0.0 }?.let {
                                    Text(text = "${it.toInt()} cm lunta")
                                }

                            }
                        }
                    }

                // Conditionally show chart when showChart is true
                AnimatedVisibility(visible = showChart) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )

                        // Temperature Chart
                        WeatherChart2(
                            observations1 = chartObservations,
                            observations2 = chartObservations,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            dataType1 = WeatherDataType.TEMPERATURE,
                            dataType2 = WeatherDataType.DEW_POINT
                        )

                        // Humidity Chart
//                        WeatherChart(
//                            observations = chartObservations,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp),
//                            dataType = WeatherDataType.HUMIDITY
//                        )
                    }
                }
            }
        }
    }
}

 */

/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ObservationCard(
    observation: ObservationData,
    observationsList: List<ObservationData>,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit
) {
    val cardBackground = if (isLongPressed) LeafGreenColor else Color.LightGray
    // First, get the current observation name
    val currentStationName = observation.name

    // Get all matching wind data
    val matchingWindData = remember(observationsList, currentStationName) {
        getMatchingWindData(observationsList, currentStationName)
    }

    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onShortPress,
                    onLongClick = onLongPress
                ),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = observation.unixTime.convertUnixTimeToHHMM(),
                            fontSize = 16.sp
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically // Add this line
                        )  {
                            Text(
                                text = observation.name.split(" ", "-", "_", "/")
                                    .take(2)
                                    .joinToString(" "),
                                fontSize = 20.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.temperature.takeIf { it.isFinite() }?.let { "${formatValue(it.toFloat())} °C" } ?: ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        /*.border(1.dp, Color.Blue)*/,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                )  {
                    Box(
                        modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                        contentAlignment = Alignment.Center
                    ) {
                            observation.presentWeather.takeIf { it.isFinite() }?.let { weatherVal ->
                                val (description, iconRes) = getWeatherDescription(weatherVal.toInt())

                                if (iconRes != null) {
                                    // If we have an icon, show it with click functionality to reveal text
                                    var showDescription by remember { mutableStateOf(false) }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Image(
                                            painter = painterResource(iconRes),
                                            contentDescription = description,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clickable { showDescription = !showDescription }
                                        )

                                        // Show description text only when the image is clicked
                                        AnimatedVisibility(
                                            visible = showDescription,
                                            enter = fadeIn() + expandVertically(),
                                            exit = fadeOut() + shrinkVertically()
                                        ) {
                                            Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // If no icon is available, just show the description text
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                            /*.border(1.dp, Color.Gray)*/,
                        contentAlignment = Alignment.Center
                    ) {
                        if (
                            !observation.windDirection.isNaN() &&
                            !observation.windSpeed.isNaN() &&
                            !observation.windGust.isNaN()
                        ) {
                            // Remember animation state
                            var isAnimating by remember { mutableStateOf(false) }

                            // Remember current data set index
                            var currentDataIndex by remember { mutableStateOf(0) }

                            // Get current parameters - either from animation or default
                            val (bearing, speed, gust) = if (isAnimating && matchingWindData.size > currentDataIndex) {
                                matchingWindData[currentDataIndex]
                            } else {
                                Triple(observation.windDirection, observation.windSpeed, observation.windGust)
                            }
                            //println("doing som")
                            //println(matchingWindData)
                            // Create effect to handle animation
                            LaunchedEffect(isAnimating) {
                                //println("IsANimatin")
                                if (isAnimating && matchingWindData.size > 1) {
                                    // Cycle through all matching data over 3 seconds
                                    val delayPerSet = 3000L / matchingWindData.size.coerceAtMost(6)

                                    for (i in matchingWindData.indices.take(6)) {
                                        currentDataIndex = i
                                        delay(delayPerSet)
                                    }

                                    // Reset after animation completes
                                    isAnimating = false
                                    currentDataIndex = 0
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clickable {
                                        if (matchingWindData.size > 1) {
                                            isAnimating = true
                                        } else {

                                        }
                                    }
                            ) {

                                // Render the compass with current parameters and click handler
                                CompassArrow(
                                    bearing = bearing,
                                    speed = speed,
                                    gust = gust
                                )
                            }


//                            // The CompassArrow composable can be customized as needed.
//                            CompassArrow(
//                                bearing = observation.windDirection,
//                                speed = observation.windSpeed,
//                                gust = observation.windGust
//                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            observation.pressure.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "$it hPa")
                            }
                            observation.cloudAmount.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "${it.toInt()}/8 pilvisyys")
                            }
                            observation.precipitationIntensity.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "$it mm/10min")
                            }
                            observation.snowDepth.takeIf { it.isFinite() && it != 0.0 }?.let {
                                Text(text = "${it.toInt()} cm lunta")
                            }

                        }
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
}}

 */

/*


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ObservationCard2(
    observation: ObservationData,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit
) {
    // Calculate the background color based on the long press state.
    val backgroundColor = if (isLongPressed) LeafGreenColor else Color.White

    // Build the valid rows of data. Each row is a list of texts.
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
            observation.precipitationAmount.takeIf { it.isFinite() }?.let { "$it mm/1h" },
            observation.precipitationIntensity.takeIf { it.isFinite() }?.let { "$it mm/10min" },
            observation.snowDepth.takeIf { it.isFinite() }?.let { "${it.toInt()} cm" }
        ),
        listOfNotNull(
            observation.pressure.takeIf { it.isFinite() }?.let { "$it hPa" },
            observation.cloudAmount.takeIf { it.isFinite() }?.let { "${it.toInt()}/8" },
            observation.presentWeather.takeIf { it.isFinite() }?.let {
                val revice = getWeatherDescription(it.toInt())
                revice.first
            }
        )
    ).filter { it.isNotEmpty() }

    // Only create a Card if we have data to show.
    if (validRows.isNotEmpty()) {

        Box {


        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = onShortPress,
                    onLongClick = onLongPress
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
                        row.forEach { text ->
                            Text(
                                text = text,
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
                    // Only display the CompassArrow if none of the required values is NaN.
                    if (
                        !observation.windDirection.isNaN() &&
                        !observation.windSpeed.isNaN() &&
                        !observation.windGust.isNaN()
                    ) {
                        CompassArrow(
                            bearing = observation.windDirection,
                            speed = observation.windSpeed,
                            gust = observation.windGust
                        )
                    }

                    // Optionally, display an image with weather description if present.
                    observation.presentWeather.takeIf { it.isFinite() }?.let {
                        val revice = getWeatherDescription(it.toInt())
                        revice.second?.let { iconRes ->
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = revice.first
                            )
                        }
                    }
                }
            }
        }
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),  // adjust padding as needed
            painter = if (isLongPressed) {
                painterResource(Res.drawable.twotone_lock_24)
            } else {
                painterResource(Res.drawable.baseline_lock_open_24)
            },
            contentDescription = if (isLongPressed) "Locked" else "Unlocked",
        )
        }
    }
}
}
*/