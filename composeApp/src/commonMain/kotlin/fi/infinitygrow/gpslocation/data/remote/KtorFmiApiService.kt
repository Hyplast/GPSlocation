package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.data.mapper.deserializeObservation
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.presentation.permission.Location
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
        longitude: Double?, latitude: Double?, observationList: List<ObservationLocation>
    ): List<ObservationData> {
        return try {
            var bbox = null.toString()
            var newLongitude = longitude
            var newlatitude = latitude
            if (longitude == null && latitude == null) {
                //bbox = null.toString()
                newLongitude = 999.9
                newlatitude = 999.9
            } else {
                if (longitude != null) {
                    bbox = "${(latitude?.minus(0.4))},${(longitude - 0.4)},${(latitude?.plus(0.4))},${(longitude + 0.4)}"
                }
            }
            val requestBuilder = FMIRequestBuilder()
            val url = requestBuilder.buildUrl(
                bbox,
                observationList = observationList
            )
            println("GETTINg this url: ")
            println(url)
            val response = client.get(url)
            val xmlString = response.bodyAsText()
            val fetchedFromLocation = Location(newlatitude!!, newLongitude!!)
            deserializeObservation(xmlString, fetchedFromLocation)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun sunRadiation(
        longitude: Double, latitude: Double
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun lightningStrikes(
        longitude: Double, latitude: Double
    ) {
        TODO("Not yet implemented")
    }
}

class FMIRequestBuilder {
    private val baseUrl = "https://opendata.fmi.fi/wfs"

    private val time = getCurrentTimeInUTCWithOffset(1)

    private val defaultParams = mapOf(
        "service" to "WFS",
        "version" to "2.0.0",
        "request" to "getFeature",
        "storedquery_id" to "fmi::observations::weather::multipointcoverage",
        //"parameters" to "t2m,ws_10min,wg_10min,wd_10min,p_sea",
        "starttime" to time.second,
        "endtime" to time.first,
        "maxlocations" to "1"
    )

    fun buildUrl(bbox: String, observationList: List<ObservationLocation>): String {
        val stringBuilder = StringBuilder(baseUrl)
        stringBuilder.append("?")

        defaultParams.forEach { (key, value) ->
            stringBuilder.append("$key=$value&")
        }

        if (bbox != "null") stringBuilder.append("bbox=$bbox&")
        observationList.forEach { observationLocation ->
            stringBuilder.append("fmisid=${observationLocation.fmiId}&")

        }
        return stringBuilder.toString()
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
}
