package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.round

// Weather observation data model
data class WeatherObservation(
    val timestamp: Long, // Unix timestamp
    val temperature: Float, // in Celsius
    val dewPoint: Float, // in Celsius
    val humidity: Float, // percentage
    val pressure: Float, // hPa
    val windSpeed: Float, // m/s
    val windGust: Float, // m/s
    val rainIntensity: Float // mm/10min
)


@Composable
fun WeatherChart2(
    observations1: List<WeatherObservation>,
    observations2: List<WeatherObservation>,
    modifier: Modifier = Modifier,
    dataType1: WeatherDataType = WeatherDataType.TEMPERATURE,
    dataType2: WeatherDataType = WeatherDataType.DEW_POINT // or other type
) {
    // If one (or both) of the datasets is empty, we might return or handle appropriately
    if (observations1.isEmpty() || observations2.isEmpty()) return

    Box(
        modifier = modifier
            .background(Color(0xFFF8F8F8))
            .padding(16.dp)
    ) {
        // Title based on data type
        Text(
            text = "${dataType1.name.lowercase()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} ${dataType2.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} Chart",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp)
        )

        // The left Y-axis labels we draw are a bit tricky when we have two different data ranges.
        // One approach is to use the union of the two datasets' ranges.
        val dataValues1 = observations1.map { getValueForType(it, dataType1) }
        val dataValues2 = observations2.map { getValueForType(it, dataType2) }
        val maxValue = maxOf(dataValues1.maxOrNull() ?: 0f, dataValues2.maxOrNull() ?: 0f)
        val minValue = minOf(dataValues1.minOrNull() ?: 0f, dataValues2.minOrNull() ?: 0f)
        val range = if (maxValue - minValue < 0.1f) 1f else maxValue - minValue

        // Y-axis labels
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Create 5 division labels on the y-axis
            val steps = 5
            var prev = 99999
            (steps downTo 0).forEach { i ->
                val value = minValue + (range * i / steps)
                val textValue = (round(value * 10) / 10).toInt()
                var textString = ""
                if (textValue != prev) textString = textValue.toString() else {
                }
                Text(
                    text = textString,
                    //text = formatValueWithUnit(round(value * 10) / 10, dataType1),
                    style = TextStyle(fontSize = 12.sp),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                prev = textValue
            }
        }

        // Main chart area for both series.
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxSize()
                .padding(start = 40.dp, top = 40.dp, bottom = 30.dp)
        ) {
            val width = size.width
            val height = size.height

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

            // Draw first data series
            if (observations1.size > 1) {
                val path1 = Path()
                observations1.forEachIndexed { index, observation ->
                    val value = getValueForType(observation, dataType1)
                    val x = index * width / (observations1.size - 1)
                    val y = height - ((value - minValue) / range * height)

                    if (index == 0) {
                        path1.moveTo(x, y)
                    } else {
                        path1.lineTo(x, y)
                    }
                    // Draw data point
                    drawCircle(
                        color = getColorForType(dataType1),
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
                // Draw connecting line
                drawPath(
                    path = path1,
                    color = getColorForType(dataType1),
                    style = Stroke(width = 2f)
                )
            }

            // Draw second data series
            if (observations2.size > 1) {
                val path2 = Path()
                observations2.forEachIndexed { index, observation ->
                    // For this series, use dataType2 extraction
                    val value = getValueForType(observation, dataType2)
                    val x = index * width / (observations2.size - 1)
                    val y = height - ((value - minValue) / range * height)

                    if (index == 0) {
                        path2.moveTo(x, y)
                    } else {
                        path2.lineTo(x, y)
                    }
                    // Draw data point in the second color
                    drawCircle(
                        color = getColorForType(dataType2),
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }
                // Draw connecting line for second series
                drawPath(
                    path = path2,
                    color = getColorForType(dataType2),
                    style = Stroke(width = 2f)
                )
            }
        }

        // X-axis time labels
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                //.border(1.dp, Color.Magenta)
                .padding(start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // For simplicity we assume both datasets share the same timestamps scheme.
            // Here we use observations1.
            if (observations1.size > 1) {
                val indicesToShow = when {
                    observations1.size <= 5 -> observations1.indices.toList()
                    else -> {
                        val step = observations1.size / 5
                        (observations1.indices step step).toList() +
                                listOf(observations1.lastIndex)
                    }
                }
                indicesToShow.forEach { index ->
                    val observation = observations1[index]
                    Text(
                        text = formatTimestamp(observation.timestamp),
                        style = TextStyle(fontSize = 10.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


// Usage example
@Composable
fun WeatherDashboard2(weatherData: List<WeatherObservation>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Temperature chart
        WeatherChart(
            observations = weatherData,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            dataType = WeatherDataType.TEMPERATURE
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Humidity chart
        WeatherChart(
            observations = weatherData,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            dataType = WeatherDataType.HUMIDITY
        )

        // You can add more charts for pressure and wind speed
    }
}

@Composable
fun WeatherChart(
    observations: List<WeatherObservation>,
    modifier: Modifier = Modifier,
    dataType: WeatherDataType = WeatherDataType.TEMPERATURE
) {
    if (observations.isEmpty()) return

    Box(
        modifier = modifier
            .background(Color(0xFFF8F8F8))
            //.padding(16.dp)
    ) {
        // Title based on data type
        Text(
            text = "Weather ${dataType.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }} Chart",
            style = TextStyle(
                fontSize = 18.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp)
        )

        // Y-axis labels
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val dataValues = observations.map { getValueForType(it, dataType) }
            val maxValue = dataValues.maxOrNull() ?: 0f
            val minValue = dataValues.minOrNull() ?: 0f
            val range = if (maxValue - minValue < 0.1f) 1f else maxValue - minValue

            // Create 5 labels for the y-axis
            val steps = 5
            (steps downTo 0).forEach { i ->
                val value = minValue + (range * i / steps)
                Text(
                    text =  (round(value * 10) / 10).toInt().toString(),//formatValueWithUnit(round(value * 10) / 10, dataType),
                    style = TextStyle(fontSize = 12.sp),
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(vertical = 4.dp)
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

            val dataValues = observations.map { getValueForType(it, dataType) }
            val maxValue = dataValues.maxOrNull() ?: 0f
            val minValue = dataValues.minOrNull() ?: 0f
            val range = if (maxValue - minValue < 0.1f) 1f else maxValue - minValue

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

            // Draw data points and line
            if (observations.size > 1) {
                val path = Path()
                val linePath = Path()

                observations.forEachIndexed { index, observation ->
                    val value = getValueForType(observation, dataType)
                    val x = index * width / (observations.size - 1)
                    val y = height - ((value - minValue) / range * height)

                    if (index == 0) {
                        path.moveTo(x, y)
                        linePath.moveTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                    }

                    // Draw data point
                    drawCircle(
                        color = getColorForType(dataType),
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }

                // Draw the line connecting data points
                drawPath(
                    path = linePath,
                    color = getColorForType(dataType),
                    style = Stroke(width = 2f)
                )
            }
        }

        // X-axis time labels
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                //.border(1.dp, Color.Magenta)
                .padding(top = 4.dp, start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (observations.size > 1) {
                // Display a subset of timestamps for readability
                val indicesToShow = when {
                    observations.size <= 5 -> observations.indices.toList()
                    else -> {
                        val step = observations.size / 5
                        (observations.indices step step).toList() + listOf(observations.lastIndex)
                    }
                }

               // Box(modifier = Modifier.fillMaxWidth()) {
                    indicesToShow.forEach { index ->
                        val observation = observations[index]
                        Text(
                            text = formatTimestamp(observation.timestamp),
                            style = TextStyle(fontSize = 10.sp),
                            modifier = Modifier
                                //.align(Alignment.BottomStart)
                                .offset(
                                    x = (index.toFloat() / (observations.size - 1) * 60).toInt().dp - 5.dp,
                                    y = 0.dp
                                ),
                            textAlign = TextAlign.Center
                        )
                    }
               // }
            }
        }
    }
}

enum class WeatherDataType {
    TEMPERATURE,
    DEW_POINT,
    HUMIDITY,
    PRESSURE,
    WIND_SPEED,
    WIND_GUST,
    RAIN_INTENSITY
}

// Helper functions
private fun getValueForType(observation: WeatherObservation, type: WeatherDataType): Float {
    return when (type) {
        WeatherDataType.TEMPERATURE -> observation.temperature
        WeatherDataType.DEW_POINT -> observation.dewPoint
        WeatherDataType.HUMIDITY -> observation.humidity
        WeatherDataType.PRESSURE -> observation.pressure
        WeatherDataType.WIND_SPEED -> observation.windSpeed
        WeatherDataType.WIND_GUST -> observation.windGust
        WeatherDataType.RAIN_INTENSITY -> observation.rainIntensity
    }
}

private fun getColorForType(type: WeatherDataType): Color {
    return when (type) {
        WeatherDataType.TEMPERATURE -> Color(0xFFE57373) // Red
        WeatherDataType.DEW_POINT -> Color(0xFF64B5F6) // Red
        WeatherDataType.HUMIDITY -> Color(0xFF64B5F6) // Blue
        WeatherDataType.PRESSURE -> Color(0xFF81C784) // Green
        WeatherDataType.WIND_SPEED -> Color(0xFFFFB74D) // Orange
        WeatherDataType.WIND_GUST -> Color(0xFFEBADB2) // Purple
        WeatherDataType.RAIN_INTENSITY -> Color(0xFF1473C8) // Blue
    }
}

private fun formatValueWithUnit(value: Float, type: WeatherDataType): String {
    return when (type) {
        WeatherDataType.TEMPERATURE -> "$value°C"
        WeatherDataType.HUMIDITY -> "$value%"
        WeatherDataType.PRESSURE -> "$value hPa"
        WeatherDataType.WIND_SPEED -> "$value m/s"
        WeatherDataType.DEW_POINT -> "$value°C"
        WeatherDataType.WIND_GUST -> "$value m/s"
        WeatherDataType.RAIN_INTENSITY -> "$value mm/10m"
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val dateTime = Instant.fromEpochSeconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}


//private fun formatTimestamp(timestamp: Long): String {
//    // Simple formatting - in a real app, use DateFormatter
//    val hour = (timestamp / 3600) % 24
//    val minute = (timestamp / 60) % 60
//    return "$hour:${minute.toString().padStart(2, '0')}"
//}