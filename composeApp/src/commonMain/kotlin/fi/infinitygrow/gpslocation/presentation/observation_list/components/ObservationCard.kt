package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.infinitygrow.gpslocation.core.presentation.LeafGreenColor
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.presentation.utils.convertUnixTimeToHHMM
import fi.infinitygrow.gpslocation.presentation.utils.formatValue
import fi.infinitygrow.gpslocation.presentation.utils.getWeatherDescription
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.baseline_lock_open_24
import gpslocation.composeapp.generated.resources.twotone_lock_24
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

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
                            .border(1.dp, Color.Gray),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = observation.unixTime.convertUnixTimeToHHMM(),
                            fontSize = 16.sp
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Gray),
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
                            .border(1.dp, Color.Gray),
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
                        .border(1.dp, Color.Blue),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                )  {
                    Box(
                        modifier = Modifier.weight(1f)
                            .border(1.dp, Color.Gray),
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
                            .border(1.dp, Color.Gray),
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
                            println("doing som")
                            println(matchingWindData)
                            // Create effect to handle animation
                            LaunchedEffect(isAnimating) {
                                println("IsANimatin")
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