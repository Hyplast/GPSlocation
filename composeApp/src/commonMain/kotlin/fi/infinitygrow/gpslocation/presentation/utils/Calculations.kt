package fi.infinitygrow.gpslocation.presentation.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gpslocation.composeapp.generated.resources.Res
import gpslocation.composeapp.generated.resources.arrow_upwards
import gpslocation.composeapp.generated.resources.compose_multiplatform
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.math.*

@Composable
fun CompassArrow2(bearing: Double) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.White, CircleShape)
    ) {
        Image(
            painter = painterResource(Res.drawable.arrow_upwards),
            contentDescription = "Wind direction arrow",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = (bearing  + 180f).toFloat(),
                    rotationX = 0.5f,
                    rotationY = 0.5f
                )
        )
    }
}

//fun getDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
//    val r = 6371 // Earth radius in km
//    val dLat = Math.toRadians(lat2 - lat1)
//    val dLong = Math.toRadians(long2 - long1)
//    val a = (sin(dLat / 2) * sin(dLat / 2) +
//            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
//            sin(dLong / 2) * sin(dLong / 2))
//    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
//    return r * c
//}

fun getDistance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
    val r = 6371.0 // Earth radius in km
    val dLat = (lat2 - lat1) * (PI / 180) // Convert degrees to radians
    val dLong = (long2 - long1) * (PI / 180)

    val a = sin(dLat / 2).pow(2) +
            cos(lat1 * (PI / 180)) * cos(lat2 * (PI / 180)) *
            sin(dLong / 2).pow(2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

/**
 * Calculates the initial bearing from point 1 to point 2 using the great circle path.
 *
 * @param lat1 Latitude of first point in degrees
 * @param long1 Longitude of first point in degrees
 * @param lat2 Latitude of second point in degrees
 * @param long2 Longitude of second point in degrees
 * @return Initial bearing in degrees (0-360°)
 */
fun getBearing(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
    val lat1Rad = lat1 * (PI / 180)
    val lat2Rad = lat2 * (PI / 180)
    val dLon = (long2 - long1) * (PI / 180)

    val y = sin(dLon) * cos(lat2Rad)
    val x = cos(lat1Rad) * sin(lat2Rad) -
            sin(lat1Rad) * cos(lat2Rad) * cos(dLon)

    var bearing = atan2(y, x) * (180 / PI)
    bearing = (bearing + 360) % 360 // Normalize to 0-360°

    return bearing
}

// Errpr version
//fun getBearing(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
//    val dLon = (long2 - long1) * (PI / 180)
//    val y = sin(dLon) * cos(lat2 * (PI / 180))
//    val x = cos(lat1 * (PI / 180)) * sin(lat2 * (PI / 180)) -
//            sin(lat1 * (PI / 180)) * cos(lat2 * (PI / 180)) * cos(dLon)
//
//    return (atan2(y, x) * (180 / PI) + 360) % 360  // Normalize to 0-360°
//}

//fun getBearing(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
//    val dLon = Math.toRadians(long2 - long1)
//    val y = sin(dLon) * cos(Math.toRadians(lat2))
//    val x = cos(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) - sin(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * cos(dLon)
//    return Math.toDegrees(atan2(y, x))
//}



//fun bearingToDirection(bearing: Double): String {
//    val directions = arrayOf("pohjoiseen", "koiliseen", "itään", "kaakkoon", "etelään", "lounaaseen", "länteen", "luoteeseen")
//    val index = ((bearing + 22.5) / 45).toInt() and 7
//    return directions[index]
//}

fun calculateTimeToWait(currentTime: Long): Long {
    val millisSinceCurrentHour = currentTime % 3600000
    return when {
        millisSinceCurrentHour < 120000 -> 120000 - (millisSinceCurrentHour)
        millisSinceCurrentHour < 720000 -> 720000 - (millisSinceCurrentHour)
        millisSinceCurrentHour< 1320000 -> 1320000 - (millisSinceCurrentHour)
        millisSinceCurrentHour < 1920000 -> 1920000 - (millisSinceCurrentHour)
        millisSinceCurrentHour < 2520000 -> 2520000 - (millisSinceCurrentHour)
        else -> 3120000 - (millisSinceCurrentHour) + 600000 // TODO This needs fixing at 23.52
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