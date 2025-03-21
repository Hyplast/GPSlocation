package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.multiplatform.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.lineSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.fill
import fi.infinitygrow.gpslocation.core.presentation.LeafGreenColor
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.utils.rememberMarker
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.no_valid_data_available
import org.jetbrains.compose.resources.stringResource

//import fi.infinitygrow.gpslocation.presentation.utils.constructLanguageString


@Composable
fun ObservationsList(
    observations: List<ObservationData>,
    viewModel: WeatherViewModel,
    isDarkTheme: Boolean
) {
    val favorites by viewModel.favorites.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(324.dp), // You can adjust the number of columns
        modifier = Modifier
            .background(if (isDarkTheme) Color.Black else Color.White)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(viewModel.getNewestObservations(observations)) { observation ->
            val isLongPressed2 = viewModel.longPressedItems.contains(observation)
            val isLongPressed = favorites.any { it.name.equals(observation.name, ignoreCase = true) }
            val backgroundColor = if (isLongPressed) LeafGreenColor else Color.White

//            println("ObservationData on card")
//            println(observation)
            Column {
                ObservationCard(
                    observation = observation,
                    observationsList = observations,
                    isLongPressed = isLongPressed,
                    onShortPress = { /* update short pressed location */ },
                    onLongPress = {
                        viewModel.toggleLongPress(observation)
                        // update long pressed location if needed
                    },
                    viewModel = viewModel
                )

            }
        }
    }
}


//                constructLanguageString(observation, location = observation.coordinates)?.let { langString ->
//                    Text(
//                        text = langString,
//                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
//                    )
//                }

@Composable
fun ObservationsRoadList(
    observations: List<RoadObservationData>,
    viewModel: WeatherViewModel,
    isDarkTheme: Boolean
) {
    val favorites by viewModel.favorites.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(324.dp), // You can adjust the number of columns
        modifier = Modifier
            .background(if (isDarkTheme) Color.Black else Color.White)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(viewModel.getNewestRoadObservations(observations)) { observation ->
            //val isLongPressed2 = viewModel.longPressedItems.contains(observation)
            val isLongPressed = favorites.any { it.name.equals(observation.name, ignoreCase = true) }
            val backgroundColor = if (isLongPressed) LeafGreenColor else Color.LightGray

//            println("RoadObservationData on card")
//            println(observation)

            Column {
                RoadObservationCard(
                    observation = observation,
                    observationsList = observations,
                    isLongPressed = isLongPressed,
                    onShortPress = { /* update short pressed location */ },
                    onLongPress = {
                        viewModel.toggleLongRoadPress(observation)
                        // update long pressed location if needed
                    },
                    backgroundColor = backgroundColor
                )
//                constructLanguageString(observation, location = observation.coordinates)?.let { langString ->
//                    Text(
//                        text = langString,
//                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
//                    )
//                }
            }
        }
    }
}


@Composable
fun RadiationList(
    observations: List<RadiationData>,
    viewModel: WeatherViewModel,
    isDarkTheme: Boolean
) {
    val favorites by viewModel.favorites.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(324.dp), // You can adjust the number of columns
        modifier = Modifier
            .background(if (isDarkTheme) Color.Black else Color.White)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(viewModel.getNewestRadiationObservations(observations)) { observation ->
            //val isLongPressed2 = viewModel.longPressedItems.contains(observation)
            val isLongPressed = favorites.any { it.name.equals(observation.name, ignoreCase = true) }
            val backgroundColor = if (isLongPressed) LeafGreenColor else Color.LightGray

//            println("Radiation on card")
//            println(observation)

            Column {
                RadiationObservationCard(
                    observation = observation,
                    observationsList = observations,
                    isLongPressed = isLongPressed,
                    onShortPress = { /* update short pressed location */ },
                    onLongPress = {
                        viewModel.toggleLongRadiationPress(observation)
                        // update long pressed location if needed
                    },
                    backgroundColor = backgroundColor
                )
            }
        }
    }
}

@Composable
fun SoundingDataListScreen(modifier: Modifier, soundingDataList: List<SoundingData>, isDarkTheme: Boolean) {

//    println("Sounding datalist")
//    println(soundingDataList.size)
//    println(soundingDataList.lastIndex)

    if (soundingDataList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDarkTheme) Color.Black else Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(Res.string.no_valid_data_available))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDarkTheme) Color.Black else Color.LightGray),
        ) {
            items(soundingDataList.groupBy { it.name to it.timeOfSounding }.toList()) { (key, data) ->
                val (name, time) = key

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
//                    Text(
//                        text = "$name - $time",
//                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray),
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    )

                    SkewTChart(
                        soundingData = data,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(600.dp)
                    )
                    SoundingDataGraph(soundingDataList = data)
                }
            }
        }

    }
}


private val RangeProvider = CartesianLayerRangeProvider.fixed(
    minX = -15.0,
    maxX = 5.0,
    minY = 0.0,
    maxY = 1000.0
)
private val StartAxisValueFormatter = CartesianValueFormatter.decimal(suffix = "m")
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(suffix = "m") //°C
//private val x = (2010..2023).toList()
private val x = listOf(-1.7, -1.9, -2.0, -2.2, -2.3, -2.4, -2.5, -2.7, -2.9, -3.0, -3.2, -3.3, -3.4, -3.5, -3.5, -3.7, -3.7, -3.6, -3.6, -3.7, -3.7, -3.7, -3.8, -3.8, -3.8, -3.8, -3.8, -3.7, -3.6, -3.3, -2.6, -2.3, -1.9, -1.8, -1.6, -1.3, -0.7, -0.3, 0.2, 0.2, 0.2, 0.2, 0.2, 0.1, 0.0, -0.1, -0.1, -0.2, -0.2, -0.3)
private val y = listOf(180.3, 205.6, 218.8, 232.6, 247.7, 260.9, 268.4, 281.1, 301.5, 322.0, 336.6, 347.0, 359.0, 372.8, 385.7, 400.1, 413.1, 425.6, 437.2, 446.6, 457.9, 472.4, 486.2, 500.0, 514.2, 529.9, 543.4, 555.6, 556.3, 570.5, 583.0, 594.0, 605.8, 619.2, 632.7, 645.2, 656.0, 665.6, 675.7, 688.5, 702.8, 715.4, 726.7, 738.2, 749.0, 759.6, 770.6, 781.3, 792.1, 803.4)



// SkewTLogPChart
@Composable
fun SkewTLogPChart(modifier: Modifier = Modifier, soundingDataList: List<SoundingData>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val maxPoints = 50
    val truncatedData = soundingDataList.take(maxPoints)

    val x_temp = truncatedData.map { it.temperature }
    val y_pres = truncatedData.map { it.pressure }

    // Example altitude (Pa) and temperature (°C) values
    val pressures = y_pres//listOf(1000.0, 900.0, 800.0, 700.0, 600.0, 500.0, 400.0, 300.0, 200.0, 100.0) // hPa
    val temperatures = x_temp//listOf(15.0, 12.0, 9.0, 5.0, 0.0, -5.0, -10.0, -20.0, -30.0, -40.0) // °C



    // Transform to log scale for the Y-axis
    val logPressures = pressures.map { kotlin.math.log10(it).round(2) }


    // Apply skew transformation
    val skewFactor = 1.35 // Typical value, adjust as needed
    val skewedTemperatures = temperatures.zip(logPressures).map { (temp, logP) ->
        temp + skewFactor * (logP - logPressures.maxOrNull()!!)
    }
    val skewTemps = skewedTemperatures.map { value ->
        value.round(2)
    }


    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(skewTemps, logPressures, ) }
        }
    }

    val scrollState = rememberVicoScrollState(
        scrollEnabled = false,
        autoScrollCondition = AutoScrollCondition.Never
    )

    // Create a zoom state with zooming disabled
    val zoomState = rememberVicoZoomState(
        zoomEnabled = false
    )

    val lineColor = Color(0xffe32636)

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColor)))
                    ),
                    rangeProvider = CartesianLayerRangeProvider.fixed(
                        minX = temperatures.minOrNull() ?: 0.0,
                        maxX = temperatures.maxOrNull() ?: 0.0,
                        minY = logPressures.minOrNull() ?: 0.0,
                        maxY = logPressures.maxOrNull() ?: 0.0
                    )
                ),
                startAxis = VerticalAxis.rememberStart(valueFormatter = StartAxisValueFormatter),
                bottomAxis = HorizontalAxis.rememberBottom(),
                marker = rememberMarker(MarkerValueFormatter),
                // Set fadingEdges to null to remove the fading effect at the edges
                fadingEdges = null
            ),
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier.height(634.dp),
    )
}

// Extension function to round Double to specified decimal places
fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
//
//@Composable
//fun ComposeMultiplatformElectricCarSales24(modifier: Modifier = Modifier) {
//    val modelProducer = remember { CartesianChartModelProducer() }
//
//    // Pair altitude with temperature and sort by altitude
//    val sortedPairs = x.zip(y).sortedBy { it.first }
//    val sortedAltitudes = sortedPairs.map { it.first } // Sorted Y-axis values (altitude)
//    val sortedTemperatures = sortedPairs.map { it.second } // Corresponding X-axis values (temperature)
//
//
//    LaunchedEffect(Unit) {
//        modelProducer.runTransaction {
//            // Learn more: https://patrykandpatrick.com/z5ah6v.
//            lineSeries { series(x,y) } // x = temperatures, y = altitudes
//        }
//    }
//    val lineColorRed = Color(0xffe32636)
//    CartesianChartHost(
//        chart =
//            rememberCartesianChart(
//                rememberLineCartesianLayer(
//                    LineCartesianLayer.LineProvider.series(
//                        LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColorRed)))
//                    )
//                ),
//                startAxis = VerticalAxis.rememberStart(),
//                bottomAxis = HorizontalAxis.rememberBottom(),
//            ),
//        modelProducer = modelProducer,
//        modifier = modifier,
//    )
//}
//
//@Composable
//fun ComposeMultiplatformElectricCarSales2(
//    modifier: Modifier = Modifier,
//    //soundingDataList: List<SoundingData>
//) {
//    val maxPoints = 50
//    //val truncatedData = soundingDataList.take(maxPoints)
//
////    val x = truncatedData.map { it.temperature }
////    val x2 = truncatedData.map { it.dewPoint }
////    val y = truncatedData.map { it.altitude }
//
//    println("Size of x and y lists")
//    println(x.size)
//    println(y.size)
//
//    val modelProducer = remember { CartesianChartModelProducer() }
//    LaunchedEffect(Unit) {
//        modelProducer.runTransaction {
//            // Learn more: https://patrykandpatrick.com/z5ah6v.
//            lineSeries {
//                series(y, x)
//                //series(x2, y)
//            }
//        }
//    }
//    val lineColorRed = Color(0xffe32636)
//    val lineColorBlue = Color(0xff0048ba)
//    val lineColors = listOf(lineColorRed, lineColorBlue)
//
//    CartesianChartHost(
//        rememberCartesianChart(
//            rememberLineCartesianLayer(
//                lineProvider =
//                    LineCartesianLayer.LineProvider.series(
//                       // lineColors.map { color ->
//                            LineCartesianLayer.rememberLine(
//                                fill = LineCartesianLayer.LineFill.single(fill(lineColorRed)),
//    //                            areaFill =
//    //                                LineCartesianLayer.AreaFill.single(
//    //                                    fill(
//    //                                        Brush.verticalGradient(listOf(lineColor.copy(alpha = 0.4f), Color.Transparent))
//    //                                    )
//    //                                ),
//                            )
//                       // }
//                    ),
//                rangeProvider = RangeProvider,
//            ),
////            rememberLineCartesianLayer(
////                LineCartesianLayer.LineProvider.series(
////                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(lineColorBlue)))
////                )
////            ),
//            startAxis = VerticalAxis.rememberStart(valueFormatter = StartAxisValueFormatter),
//            bottomAxis = HorizontalAxis.rememberBottom(),
//            marker = rememberMarker(MarkerValueFormatter),
//        ),
//        modelProducer,
//        modifier.height(216.dp),
//        rememberVicoScrollState(scrollEnabled = false),
//    )
//}

