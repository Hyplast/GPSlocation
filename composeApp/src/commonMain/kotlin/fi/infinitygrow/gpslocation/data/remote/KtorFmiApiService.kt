package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.data.mapper.deserializeObservation
import fi.infinitygrow.gpslocation.data.mapper.deserializeRadiation
import fi.infinitygrow.gpslocation.data.mapper.deserializeRoadObservation
import fi.infinitygrow.gpslocation.data.mapper.parseSounding
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import fi.infinitygrow.gpslocation.presentation.utils.BoundingBox
import fi.infinitygrow.gpslocation.presentation.utils.getBoundingBox
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

//https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&parameters=t2m,ws_10min,wg_10min,wd_10min,p_sea&bbox=23,60,24,61&maxlocations=1&
//https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&parameters=t2m,ws_10min,wg_10min,wd_10min,p_sea&bbox=59.92,23.41,60.72,24.21&maxlocations=1

class KtorFmiApiService(
    private val client: HttpClient
): FmiApiService {

    override suspend fun observation(
        longitude: Double?,
        latitude: Double?,
        location: Boolean,
        radiusKm: Int?,
        observationList: List<ObservationLocation>
    ): List<ObservationData> {
        return try {
            val safeLongitude = longitude ?: run {
                println("Longitude is null!")
                999.9
            }
            val safeLatitude = latitude ?: run {
                println("Latitude is null!")
                999.9
            }

            // If both coordinates are provided and radiusKm is provided,
            // create bbox; otherwise, leave bbox empty.
            val bbox = if (longitude != null && latitude != null && radiusKm != null) {
                createBboxString(latitude, longitude, radiusKm.toDouble())
            } else {
                ""
            }

            val time = getCurrentTimeInUTCWithOffset(1)

            val url = buildFMIUrl(
                queryId = "fmi::observations::weather::multipointcoverage",
                bbox = bbox,
                observationList = observationList,
                startTime = time.second,
                endTime = time.first
            )

            println("GETTING this url: $url")
            println("with radius: $radiusKm")

            val response = client.get(url)
            val xmlString = response.bodyAsText()
            val fetchedFromLocation = Location(safeLatitude, safeLongitude)
            deserializeObservation(xmlString, fetchedFromLocation)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun createBboxString(
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double?
    ): String {
        // Check for null latitude, longitude, or invalid radius
        if (latitude == null || longitude == null || radiusKm == null || radiusKm == -1.0) {
            return ""
        }

        // Compute the bounding box using the provided valid radius
        val boundingBox = getBoundingBox(latitude, longitude, radiusKm)

        // Format the bounding box values with two-decimal precision.
        return "${(boundingBox.lat1 * 100).toInt() / 100.0}," +
                "${(boundingBox.lon1 * 100).toInt() / 100.0}," +
                "${(boundingBox.lat2 * 100).toInt() / 100.0}," +
                "${(boundingBox.lon2 * 100).toInt() / 100.0}"
    }


    // livi::observations::road::default::multipointcoverage
    override suspend fun roadObservation(
        longitude: Double?,
        latitude: Double?,
        location: Boolean,
        radiusKm: Int?,
        observationList: List<ObservationLocation>
    ): List<RoadObservationData> {
        return try {
            val safeLongitude = longitude ?: run {
                println("Longitude is null!")
                999.9
            }
            val safeLatitude = latitude ?: run {
                println("Latitude is null!")
                999.9
            }

            // If both coordinates are provided and radiusKm is provided,
            // create bbox; otherwise, leave bbox empty.
            val bbox = if (longitude != null && latitude != null && radiusKm != null) {
                createBboxString(latitude, longitude, radiusKm.toDouble())
            } else {
                ""
            }

            val url = buildFMIUrl(
                queryId = "livi::observations::road::default::multipointcoverage",
                bbox = bbox,
                observationList = observationList
            )

            println("GETTING this url: $url")
            println("with radius: $radiusKm")

            val response = client.get(url)
            val xmlString = response.bodyAsText()
            val fetchedFromLocation = Location(safeLatitude, safeLongitude)
            deserializeRoadObservation(xmlString, fetchedFromLocation)
        }
        catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun sunRadiation(
        longitude: Double, latitude: Double
    ): List<RadiationData> {
        return try {
            var newLongitude = longitude
            var newlatitude = latitude
            if (longitude == null && latitude == null) {
                //bbox = null.toString()
                newLongitude = 999.9
                newlatitude = 999.9
            } else {

            }
//            val requestBuilder = FMIRequestBuilder()
//            val time = requestBuilder.getCurrentTimeInUTCWithOffset(1)
           // val url = "https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::radiation::multipointcoverage&starttime=${time.second}&endtime=${time.first}&timestep=5&"
            val queryId = "fmi::observations::radiation::multipointcoverage"

            val url = buildFMIUrl(queryId)

            val response = client.get(url)
            val xmlString = response.bodyAsText()
            val fetchedFromLocation = Location(newlatitude!!, newLongitude!!)
            deserializeRadiation(xmlString, fetchedFromLocation)

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun lightningStrikes(
        longitude: Double, latitude: Double
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getSounding(): List<SoundingData> {
        val url = "https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::sounding::multipointcoverage"
        val response = client.get(url)
        val xmlString = response.bodyAsText()
        //val fetchedFromLocation = Location(newlatitude!!, newLongitude!!)
        return parseSounding(xmlString)
    }
}

fun buildFMIUrl(
    queryId: String,
    startTime: String? = null,
    endTime: String? = null,
    maxLocations: String? = null,
    bbox: String? = null,
    observationList: List<ObservationLocation>? = null
):String {
    val baseUrl = "https://opendata.fmi.fi/wfs"
    val time = getCurrentTimeInUTCWithOffset(1)

    // Build your default params map. If the optional values are provided,
    // they override the default values from `time` or a hardcoded value.
    val defaultParams = mutableMapOf(
        "service" to "WFS",
        "version" to "2.0.0",
        "request" to "getFeature",
        "storedquery_id" to queryId,
    )

    // Add time and maxlocations only if they're provided.
    startTime?.let { defaultParams["starttime"] = it }
    endTime?.let { defaultParams["endtime"] = it }
    maxLocations?.let { defaultParams["maxlocations"] = it }

    val stringBuilder = StringBuilder(baseUrl).apply {
        append("?")
        // Append the default parameters.
        defaultParams.forEach { (key, value) ->
            append("$key=$value&")
        }
        // Append bbox if it's provided and not blank.
        bbox?.takeIf { it.isNotBlank() }?.let {
            append("bbox=$it&")
        }
        // Append each fmisid from observationList if provided.
        observationList?.forEach { observationLocation ->
            append("fmisid=${observationLocation.fmiId}&")
        }
    }

    // Remove the last "&" if needed.
    return stringBuilder.toString().removeSuffix("&")
}

fun getCurrentTimeInUTCWithOffset(hoursOffset: Int): Pair<String, String> {
    val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) // Get local time
    val nowUTC = nowLocal.toInstant(TimeZone.currentSystemDefault()) // Convert to UTC

    val utcDateTime = nowUTC.toLocalDateTime(TimeZone.UTC) // Convert to UTC DateTime
    val offsetDateTime = nowUTC.minus(hoursOffset * 60 * 60, DateTimeUnit.SECOND)
        .toLocalDateTime(TimeZone.UTC) // Subtract offset and convert

    fun LocalDateTime.toIsoFormat(): String =
        "${year}-${monthNumber.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}T" +
                "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}Z"

    return utcDateTime.toIsoFormat() to offsetDateTime.toIsoFormat()
}