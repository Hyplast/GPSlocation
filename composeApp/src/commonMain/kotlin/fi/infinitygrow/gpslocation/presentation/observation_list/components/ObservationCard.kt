package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ObservationCard(
    observation: ObservationData,
    isLongPressed: Boolean,
    onShortPress: () -> Unit,
    onLongPress: () -> Unit
) {
    // Choose a background color based on the state.
    val cardBackground = if (isLongPressed) LeafGreenColor else Color.White

    // Build a list of rows with observation info.
    //val infoRows = listOf(
//        listOfNotNull(
//            observation.name.takeIf { it.isNotBlank() },
//            observation.temperature.takeIf { it.isFinite() }?.let { "${formatValue(it.toFloat())} °C" },
//            observation.unixTime.convertUnixTimeToHHMM()
//        ),
//        listOfNotNull(
//            observation.windSpeed.takeIf { it.isFinite() }?.let { "$it m/s" },
//            observation.windGust.takeIf { it.isFinite() }?.let { "$it m/s" },
//            observation.windDirection.takeIf { it.isFinite() }?.let { "${it.toInt()} °" }
//        ),
//        listOfNotNull(
//            observation.precipitationAmount.takeIf { it.isFinite() }?.let { "$it mm/1h" },
//            observation.precipitationIntensity.takeIf { it.isFinite() }?.let { "$it mm/10min" },
//            observation.snowDepth.takeIf { it.isFinite() }?.let { "${it.toInt()} cm" }
//        ),
//        listOfNotNull(
//            observation.pressure.takeIf { it.isFinite() }?.let { "$it hPa" },
//            observation.cloudAmount.takeIf { it.isFinite() }?.let { "${it.toInt()}/8" },
//            observation.presentWeather.takeIf { it.isFinite() }?.let {
//                val descriptionPair = getWeatherDescription(it.toInt())
//                descriptionPair.first
//            }
//        )
    //).filter { it.isNotEmpty() }

    // Only display a card if there is at least one row of data.
   // if (infoRows.isEmpty()) return

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically // Add this line
                        )  {
                            Text(
                                text = observation.name,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            observation.presentWeather.takeIf { it.isFinite() }?.let { weatherVal ->
                                val (description, iconRes) = getWeatherDescription(weatherVal.toInt())
                                iconRes?.let { resId ->
                                    Image(
                                        painter = painterResource(resId),
                                        contentDescription = description,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = observation.temperature.takeIf { it.isFinite() }?.let { "${formatValue(it.toFloat())} °C" } ?: ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                )  {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                text = observation.precipitationIntensity.takeIf { it.isFinite() }?.let { "$it mm/10min" } ?: ""
                            )
                            Text(
                                text = observation.precipitationAmount.takeIf { it.isFinite() }?.let { "$it mm/1h" } ?: ""
                            )
                            Text(
                                text = observation.snowDepth.takeIf { it.isFinite() }?.let { "${it.toInt()} lumensyvyys (cm) " }  ?: ""
                            )
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
                            // The CompassArrow composable can be customized as needed.
                            CompassArrow(
                                bearing = observation.windDirection,
                                speed = observation.windSpeed,
                                gust = observation.windGust
                            )
                        }
                    }
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Text(
                                text = observation.pressure.takeIf { it.isFinite() }?.let { "$it hPa" } ?: ""
                            )
                            Text(
                                text = observation.cloudAmount.takeIf { it.isFinite() }?.let { "${it.toInt()}/8 pilvisyys" } ?: ""
                            )
                            Text(
                                text = observation.presentWeather.takeIf { it.isFinite() }?.let {
                                    val descriptionPair = getWeatherDescription(it.toInt())
                                    descriptionPair.first
                                } ?: ""
                            )
                        }


                    }
                }

                // Show each row with spacing.
//                infoRows.forEach { row ->
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceEvenly
//                    ) {
//                        row.forEach { text ->
//                            Text(
//                                text = text,
//                                fontSize = 16.sp,
//                                modifier = Modifier.weight(1f),
//                                maxLines = 1
//                            )
//                        }
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                }
                // Bottom row containing the wind icon and weather image.
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//
//                    // If presentWeather is finite, show a descriptive icon.
//
//                }
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
}}