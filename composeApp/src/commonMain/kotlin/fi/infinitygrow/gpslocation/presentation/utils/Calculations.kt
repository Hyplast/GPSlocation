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
import fi.infinitygrow.gpslocation.domain.model.SoundingData
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


/**
 * Selects the sounding data that is both the latest for its station and
 * geographically closest to the target coordinates.
 *
 * @param soundingList List of available sounding data.
 * @param targetLatitude The reference latitude.
 * @param targetLongitude The reference longitude.
 * @return The selected SoundingData or null if the provided list is empty.
 */
fun selectClosestLatestSoundingProfile(
    soundingList: List<SoundingData>,
    targetLatitude: Double,
    targetLongitude: Double
): List<SoundingData>? {
    // Group by station name and time-of-sounding.
    //val groupedSoundings = soundingList.groupBy { it.name to it.timeOfSounding }

    // Group by station name and select the latest sounding for each station
    val latestSoundings = soundingList.groupBy { it.name }
        .mapValues { (_, profiles) ->
            profiles.maxByOrNull { Instant.parse(it.timeOfSounding) } // Parse and compare timestamps
        }.values.filterNotNull() // Remove null values

    // Find the closest latest sounding profile
    val closest = latestSoundings.minByOrNull {
        getDistance(targetLatitude, targetLongitude, it.latitude, it.longitude)
    } ?: return null

    // Retrieve the full profile for the selected sounding
    return soundingList.filter { it.name == closest.name && it.timeOfSounding == closest.timeOfSounding }

//    // For each distinct profile, pick one representative to indicate its time
//    // (we select the one with the max unixTime; usually all points in a profile have the same unixTime)
//    val representativeSoundings = groupedSoundings.map { (key, group) ->
//        group.maxByOrNull { it.unixTime } ?: group.first()
//    }
//
//    // From the profiles, select the one that is closest to the observation location.
//    // Here we assume that all points in a profile have the same station coordinates,
//    // so using the first point is appropriate.
//    val selectedKey = representativeSoundings.minByOrNull {
//        getDistance(targetLatitude, targetLongitude, it.latitude, it.longitude)
//    }?.let { rep ->
//        // create a key using the same grouping criteria
//        rep.name to rep.timeOfSounding
//    } ?: return null
//
//    // Return the full sounding profile for the selected key.
//    return groupedSoundings[selectedKey]
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


/**
 * A data class representing a bounding box defined by its southwest (lat1, lon1)
 * and northeast (lat2, lon2) corners.
 */
data class BoundingBox(
    val lat1: Double, // Southwest corner latitude
    val lon1: Double, // Southwest corner longitude
    val lat2: Double, // Northeast corner latitude
    val lon2: Double  // Northeast corner longitude
)


/**
 * Extension function to convert an angle in radians to degrees.
 */
fun Double.toDegrees(): Double = this * 180.0 / PI

/**
 * Extension function to convert an angle in degrees to radians.
 */
fun Double.toRadians(): Double = this * PI / 180.0

/**
 * Calculates a bounding box (defined by two corners) around a center coordinate
 * given a radius (in kilometers). This is useful for finding stations within a given
 * distance from a position.
 *
 * We assume a spherical Earth with an average radius of 6371 km.
 *
 * @param centerLat the center latitude in degrees.
 * @param centerLon the center longitude in degrees.
 * @param radiusKm the search radius in kilometers.
 * @return a [BoundingBox] defined by:
 *  - lat1, lon1: the coordinates of the southwest corner.
 *  - lat2, lon2: the coordinates of the northeast corner.
 */
fun getBoundingBox(centerLat: Double, centerLon: Double, radiusKm: Double): BoundingBox {
    val earthRadius = 6371.0 // Earth's radius in kilometers

    // Calculate degree difference for latitude: 1 degree is approximately 111 km
    val latDelta = (radiusKm / earthRadius).toDegrees()

    // Calculate degree difference for longitude at the given latitude.
    // Cosine adjustment is needed because the length of a degree of longitude varies
    // depending on the latitude.
    val lonDelta = (radiusKm / (earthRadius * cos(centerLat.toRadians()))).toDegrees()


    // Compute the southwest and northeast corners of the bounding box.
    val southWestLat = centerLat - latDelta
    val southWestLon = centerLon - lonDelta
    val northEastLat = centerLat + latDelta
    val northEastLon = centerLon + lonDelta

    return BoundingBox(
        lat1 = southWestLat,
        lon1 = southWestLon,
        lat2 = northEastLat,
        lon2 = northEastLon
    )
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