package fi.infinitygrow.gpslocation.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.log10
import kotlin.math.pow

data class SoundingDataPoint(
    val pressureMb: Double,
    val temperatureC: Double,
    val dewPointC: Double
)

val soundingData = listOf(
    SoundingDataPoint(1000.0, 25.0, 20.0),
    SoundingDataPoint(950.0, 22.0, 18.0),
    SoundingDataPoint(900.0, 20.0, 15.0),
    SoundingDataPoint(850.0, 17.0, 12.0),
    SoundingDataPoint(800.0, 14.0, 9.0),
    SoundingDataPoint(700.0, 8.0, 3.0),
    SoundingDataPoint(600.0, 1.0, -2.0),
    SoundingDataPoint(500.0, -6.0, -10.0),
    SoundingDataPoint(400.0, -15.0, -20.0),
    SoundingDataPoint(300.0, -30.0, -35.0),
    SoundingDataPoint(250.0, -40.0, -45.0),
    SoundingDataPoint(200.0, -50.0, -55.0),
    SoundingDataPoint(150.0, -60.0, -65.0),
    SoundingDataPoint(100.0, -70.0, -75.0)
)

@Composable
fun SkewTDemo() {
    SkewTLogPChart(
        soundingData = soundingData,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun SkewTLogPChart(
    soundingData: List<SoundingDataPoint>,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(900.dp, 800.dp)
            .background(Color.White)
            .padding(16.dp)
    ) {
        val chartWidth = size.width - 100f
        val chartHeight = size.height - 100f
        val startX = 50f
        val startY = 50f

        // Constants for the chart
        val minPressure = 100.0  // hPa, top of chart
        val maxPressure = 1050.0 // hPa, bottom of chart
        val minTemperature = -40.0 // °C, leftmost temp at bottom
        val maxTemperature = 40.0  // °C, rightmost temp at bottom
        val skewFactor = 2.5f     // Controls the amount of skew

        // Draw reference frame
        drawReferenceFrame(
            startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure, minTemperature, maxTemperature
        )

        // Draw pressure lines
        drawPressureLines(
            startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure
        )

        // Draw temperature lines
        drawTemperatureLines(
            startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure, minTemperature, maxTemperature, skewFactor
        )

        // Draw dry adiabats
        drawDryAdiabats(
            startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure, minTemperature, maxTemperature, skewFactor
        )

        // Draw mixing ratio lines
        drawMixingRatioLines(
            startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure, minTemperature, maxTemperature, skewFactor
        )

        // Draw temperature profile
        drawTemperatureProfile(
            soundingData, startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure, minTemperature, maxTemperature, skewFactor,
            Color.Red, 3f
        )

        // Draw dew point profile
        drawTemperatureProfile(
            soundingData.map {
                SoundingDataPoint(it.pressureMb, it.dewPointC, it.dewPointC)
            },
            startX, startY, chartWidth, chartHeight,
            minPressure, maxPressure, minTemperature, maxTemperature, skewFactor,
            Color.Blue, 3f
        )
    }
}

private fun DrawScope.drawReferenceFrame(
    startX: Float, startY: Float, width: Float, height: Float,
    minPressure: Double, maxPressure: Double, minTemperature: Double, maxTemperature: Double
) {
    // Draw box around the chart
    drawRect(
        color = Color.Black,
        topLeft = Offset(startX, startY),
        size = androidx.compose.ui.geometry.Size(width, height),
        style = Stroke(width = 2f)
    )

    // Draw pressure labels on y-axis
    val pressures = listOf(1000, 850, 700, 500, 400, 300, 250, 200, 150, 100)
    pressures.forEach { pressure ->
        if (pressure.toDouble() in minPressure..maxPressure) {
            val y = pressureToY(pressure.toDouble(), startY, height, minPressure, maxPressure)
            drawLine(
                color = Color.Black,
                start = Offset(startX - 10, y),
                end = Offset(startX, y),
                strokeWidth = 1.5f
            )
            drawContext.canvas.nativeCanvas.drawText(
                "$pressure",
                startX - 40,
                y + 5,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
                }
            )
        }
    }

    // Draw temperature labels on x-axis
    val temperatures = (-40..40 step 10).toList()
    temperatures.forEach { temp ->
        if (temp.toDouble() in minTemperature..maxTemperature) {
            val x = temperatureToX(
                temp.toDouble(),
                1000.0, // Use surface pressure for labels
                startX,
                width,
                minTemperature,
                maxTemperature,
                2.5f // skew factor
            )
            drawLine(
                color = Color.Black,
                start = Offset(x, startY + height),
                end = Offset(x, startY + height + 10),
                strokeWidth = 1.5f
            )
            drawContext.canvas.nativeCanvas.drawText(
                "$temp°C",
                x - 15,
                startY + height + 30,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 30f
                }
            )
        }
    }
}

private fun DrawScope.drawPressureLines(
    startX: Float, startY: Float, width: Float, height: Float,
    minPressure: Double, maxPressure: Double
) {
    val pressures = listOf(1000, 850, 700, 500, 400, 300, 250, 200, 150, 100)
    pressures.forEach { pressure ->
        if (pressure.toDouble() in minPressure..maxPressure) {
            val y = pressureToY(pressure.toDouble(), startY, height, minPressure, maxPressure)
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(startX, y),
                end = Offset(startX + width, y),
                strokeWidth = 1f
            )
        }
    }
}

private fun DrawScope.drawTemperatureLines(
    startX: Float, startY: Float, width: Float, height: Float,
    minPressure: Double, maxPressure: Double, minTemperature: Double, maxTemperature: Double,
    skewFactor: Float
) {
    val temperatures = (-40..40 step 10).toList()
    val pressureSteps = listOf(1000.0, 100.0)

    temperatures.forEach { temp ->
        if (temp.toDouble() in minTemperature..maxTemperature) {
            val path = Path()
            var firstPoint = true

            // Calculate several points to draw the skewed line
            for (pressure in 1000 downTo 100 step 10) {
                if (pressure.toDouble() in minPressure..maxPressure) {
                    val y = pressureToY(pressure.toDouble(), startY, height, minPressure, maxPressure)
                    val x = temperatureToX(
                        temp.toDouble(),
                        pressure.toDouble(),
                        startX,
                        width,
                        minTemperature,
                        maxTemperature,
                        skewFactor
                    )

                    if (firstPoint) {
                        path.moveTo(x, y)
                        firstPoint = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }

            drawPath(
                path = path,
                color = Color.Gray.copy(alpha = 0.5f),
                style = Stroke(width = 1f)
            )
        }
    }
}

private fun DrawScope.drawDryAdiabats(
    startX: Float, startY: Float, width: Float, height: Float,
    minPressure: Double, maxPressure: Double, minTemperature: Double, maxTemperature: Double,
    skewFactor: Float
) {
    // Simplified approximation of dry adiabats
    val potentialTemperatures = (250..400 step 20).toList()  // Potential temperature in K

    potentialTemperatures.forEach { theta ->
        val path = Path()
        var firstPoint = true

        for (pressure in 1000 downTo 100 step 10) {
            if (pressure.toDouble() in minPressure..maxPressure) {
                // Calculate temperature from potential temperature (simplified)
                val p0 = 1000.0  // Reference pressure
                val kappa = 0.286 // R/cp for dry air
                val tempK = theta * (pressure / p0).pow(kappa)
                val tempC = tempK - 273.15

                if (tempC in minTemperature..maxTemperature) {
                    val y = pressureToY(pressure.toDouble(), startY, height, minPressure, maxPressure)
                    val x = temperatureToX(
                        tempC,
                        pressure.toDouble(),
                        startX,
                        width,
                        minTemperature,
                        maxTemperature,
                        skewFactor
                    )

                    if (firstPoint) {
                        path.moveTo(x, y)
                        firstPoint = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }
        }

        drawPath(
            path = path,
            color = Color.Green.copy(alpha = 0.5f),
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawMixingRatioLines(
    startX: Float, startY: Float, width: Float, height: Float,
    minPressure: Double, maxPressure: Double, minTemperature: Double, maxTemperature: Double,
    skewFactor: Float
) {
    // Simplified approximation of mixing ratio lines
    val mixingRatios = listOf(0.1, 0.5, 1.0, 2.0, 4.0, 8.0, 12.0, 16.0, 20.0)

    mixingRatios.forEach { mixingRatio ->
        val path = Path()
        var firstPoint = true

        for (pressure in 1000 downTo 300 step 10) {
            if (pressure.toDouble() in minPressure..maxPressure) {
                // Simplified formula to convert mixing ratio to temperature
                // This is not meteorologically accurate but shows the concept
                val e = (mixingRatio * pressure) / (622.0 + mixingRatio)
                val tempC = (243.5 * log10(e / 6.112)) / (17.67 - log10(e / 6.112))

                if (tempC in minTemperature..maxTemperature) {
                    val y = pressureToY(pressure.toDouble(), startY, height, minPressure, maxPressure)
                    val x = temperatureToX(
                        tempC,
                        pressure.toDouble(),
                        startX,
                        width,
                        minTemperature,
                        maxTemperature,
                        skewFactor
                    )

                    if (firstPoint) {
                        path.moveTo(x, y)
                        firstPoint = false
                    } else {
                        path.lineTo(x, y)
                    }
                }
            }
        }

        drawPath(
            path = path,
            color = Color.Cyan.copy(alpha = 0.5f),
            style = Stroke(width = 1f)
        )
    }
}

private fun DrawScope.drawTemperatureProfile(
    soundingData: List<SoundingDataPoint>,
    startX: Float, startY: Float, width: Float, height: Float,
    minPressure: Double, maxPressure: Double, minTemperature: Double, maxTemperature: Double,
    skewFactor: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    var firstPoint = true

    for (point in soundingData) {
        if (point.pressureMb in minPressure..maxPressure &&
            point.temperatureC in minTemperature..maxTemperature) {

            val y = pressureToY(point.pressureMb, startY, height, minPressure, maxPressure)
            val x = temperatureToX(
                point.temperatureC,
                point.pressureMb,
                startX,
                width,
                minTemperature,
                maxTemperature,
                skewFactor
            )

            if (firstPoint) {
                path.moveTo(x, y)
                firstPoint = false
            } else {
                path.lineTo(x, y)
            }
        }
    }

    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

// Helper functions to convert between data values and screen coordinates
private fun pressureToY(
    pressure: Double,
    startY: Float,
    height: Float,
    minPressure: Double,
    maxPressure: Double
): Float {
    val logMinP = log10(minPressure)
    val logMaxP = log10(maxPressure)
    val logP = log10(pressure)

    // Normalize to 0-1 range, then map to screen coordinates
    val normalizedY = (logP - logMinP) / (logMaxP - logMinP)
    return startY + (height * normalizedY).toFloat()
}

private fun temperatureToX(
    tempC: Double,
    pressureMb: Double,
    startX: Float,
    width: Float,
    minTempC: Double,
    maxTempC: Double,
    skewFactor: Float
): Float {
    // Calculate skew amount based on pressure
    val logMinP = log10(100.0)  // Top of chart
    val logMaxP = log10(1050.0) // Bottom of chart
    val logP = log10(pressureMb)

    val normalizedP = (logP - logMaxP) / (logMinP - logMaxP) // 0 at bottom, 1 at top
    val skewAmount = normalizedP * skewFactor * (maxTempC - minTempC)

    // Apply skew and normalize temperature
    val adjustedTemp = tempC + skewAmount
    val normalizedX = (adjustedTemp - minTempC) / (maxTempC - minTempC)

    return startX + (width * normalizedX).toFloat()
}
