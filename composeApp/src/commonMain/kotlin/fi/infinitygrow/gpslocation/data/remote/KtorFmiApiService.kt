package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import nl.adaptivity.xmlutil.serialization.XML
import io.ktor.client.call.body
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
import io.ktor.http.path

//https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&parameters=t2m,ws_10min,wg_10min,wd_10min,p_sea&bbox=23,60,24,61&maxlocations=1&
//https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&parameters=t2m,ws_10min,wg_10min,wd_10min,p_sea&bbox=59.92,23.41,60.72,24.21&maxlocations=1
private const val BASE_URL = "https://opendata.fmi.fi"

class KtorFmiApiService(
    private val client: HttpClient
): FmiApiService {

    override suspend fun observation(
        latitude: Double, longitude: Double
    ): List<ObservationData> {
        return try {
            val bbox = "${(longitude - 0.4)},${(latitude - 0.4)},${(longitude + 0.4)},${(latitude + 0.4)}"
            // protocol = URLProtocol.HTTPS

            val requestBuilder = FMIRequestBuilder()
            val url = requestBuilder.buildUrl(bbox)
            println("url to get")
            println(url)

            val response = client.get(url)

//            {
//                url {
//                    protocol = URLProtocol.HTTPS
//                    host = "opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&parameters=t2m,ws_10min,wg_10min,wd_10min,p_sea&"
//                    //path("wfs")
//
//                    parameters {
////                        append("service", "WFS")
////                        append("version", "2.0.0")
////                        append("request", "getFeature")
////                        append("storedquery_id", "fmi::observations::weather::multipointcoverage")
////                        append("parameters", "t2m,ws_10min,wg_10min,wd_10min,p_sea")//parameters)
//                        append("bbox", bbox)
//                        append("maxlocations", "1") //maxLocations.toString())
//                    }
//                }
//            }

//            val response: HttpResponse = client.get(
//
//                    host = "$BASE_URL/wfs"
//
//                parameter("service", "WFS")
//                parameter("version", "2.0.0")
//                parameter("request", "getFeature")
//                parameter("storedquery_id", "fmi::observations::weather::multipointcoverage")
//                parameter("timestep", 10)
//                parameter("bbox", bbox)
//            }

            //println("REspONSE was:")
            //println(response)


            val xmlString = response.bodyAsText()
            //println(xmlString)
            deserializeObservation(xmlString)
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
    private val defaultParams = mapOf(
        "service" to "WFS",
        "version" to "2.0.0",
        "request" to "getFeature",
        "storedquery_id" to "fmi::observations::weather::multipointcoverage",
        "parameters" to "t2m,ws_10min,wg_10min,wd_10min,p_sea",
        "maxlocations" to "1"
    )

    fun buildUrl(bbox: String): String {
        val stringBuilder = StringBuilder(baseUrl)
        stringBuilder.append("?")

        defaultParams.forEach { (key, value) ->
            stringBuilder.append("$key=$value&")
        }

        stringBuilder.append("bbox=$bbox")

        return stringBuilder.toString()
    }
}


fun deserializeObservation(xmlString: String): List<ObservationData> {
    return try {
        val xml = XML {
            defaultPolicy {
                pedantic = false
                ignoreUnknownChildren()
            }
            repairNamespaces = true
        }

        val cleanedXmlString = xmlString.trim()

        val observation = xml.decodeFromString(
            deserializer = ObservationDTO.serializer(),
            string = cleanedXmlString
        )

        val names = observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers.map { it.point.locationName }
        val locations = observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers.map { it.point.locationPosition }

        val locationMeasurements = cleanString(observation.data.gridSeriesObservation.result.multiPointCoverage.domainSet.simpleMultiPoint.positions)
            .trim()
            .split(" ")
            .chunked(3) { (lon, lat, time) -> Triple(lon.toDouble(), lat.toDouble(), time.toLong()) }

        val measurements = cleanString(observation.data.gridSeriesObservation.result.multiPointCoverage.rangeSet.dataBlock.tupleList)
            .trim()
            .split(" ")
            .chunked(5) { (temp, wind, windMax, windDir, pressure) ->
                listOf(temp.toDouble(), wind.toDouble(), windMax.toDouble(), windDir.toDouble(), pressure.toDouble())
            }

        val chunkSize = if (names.isNotEmpty()) locationMeasurements.size / names.size else 1
        names.indices.flatMap { i ->
            List(chunkSize) { j ->
                val index = i * chunkSize + j
                ObservationData(
                    name = names[i],
                    coordinates = locations[i],
                    longitude = locationMeasurements[index].first,
                    latitude = locationMeasurements[index].second,
                    unixTime = locationMeasurements[index].third,
                    temperature = measurements[index][0],
                    windSpeed = measurements[index][1],
                    windMax = measurements[index][2],
                    windDirection = measurements[index][3],
                    pressure = measurements[index][4]
                )
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

fun cleanString(input: String): String {
    // Replace all Unicode 10 (Line Feed) with Unicode 32 (Space)
    val cleaned = input.replace("\u000A", " ")

    // Replace multiple consecutive spaces with a single space
    return cleaned.replace(Regex("\\s+"), " ")
}

fun cleanXmlString(xmlString: String): String {
    return xmlString
        .replace("\uFEFF", "")  // Remove BOM
        .replace(Regex("[^\\P{C}\n\r\t]"), "")  // Remove non-printable characters
        .replace("\r\n", "\n")  // Normalize line endings
        .replace("\r", "\n")
        .trim()  // Final trim
}

/*
fun deserializeObservation(xmlString: String): List<ObservationData> {

    return try {

    val xml = XML {
        defaultPolicy {
            pedantic = false
            ignoreUnknownChildren()
        }
        repairNamespaces = true
    }

    val cleanedXmlString = xmlString.dropWhile { it != '<' }

    val observation = xml.decodeFromString(
        deserializer = ObservationDTO.serializer(),
        string = cleanedXmlString
    )

    println("PointMembers")
    println(observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers)

    val names = observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers.map { it.point.locationName }

    val locations = observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers.map { it.point.locationPosition }

    val measureTypes = observation.data.gridSeriesObservation.result.multiPointCoverage.rangeType.dataRecord

    //val locationMeasurements = observation.data.gridSeriesObservation.result.multiPointCoverage.domainSet.simpleMultiPoint.positions
    val locationMeasurements = observation.data.gridSeriesObservation.result.multiPointCoverage.domainSet.simpleMultiPoint.positions
        .trim()
        .split(" ")
        .chunked(3) { (lon, lat, time) -> Triple(lon.toDouble(), lat.toDouble(), time.toLong()) }


    val measurements = observation.data.gridSeriesObservation.result.multiPointCoverage.rangeSet.dataBlock.tupleList
        .trim()
        .split(" ")
        .chunked(5) { (temp, wind, windMax, windDir, pressure) ->
            listOf(temp.toDouble(), wind.toDouble(), windMax.toDouble(), windDir.toDouble(), pressure.toDouble())
        }

    val chunkSize = locationMeasurements.size / names.size
    names.indices.flatMap { i ->
        List(chunkSize) { j ->
            val index = i * chunkSize + j
            ObservationData(
                name = names[i],
                coordinates = locations[i],
                longitude = locationMeasurements[index].first,
                latitude = locationMeasurements[index].second,
                unixTime = locationMeasurements[index].third,
                temperature = measurements[index][0],
                windSpeed = measurements[index][1],
                windMax = measurements[index][2],
                windDirection = measurements[index][3],
                pressure = measurements[index][4]
            )

    } catch (e: Exception) {
            e.printStackTrace()
            emptyList()  // Return empty list if parsing fails
        }
    }

//    return observation.data.gridSeriesObservation.featureOfInterest.sam.sampledFeature.locationCollection {
//    //report.data.periodCollection.periods.first().rates.rates.mapNotNull { rate ->
//        if (tenor != null) {
//            EuriborRate(
//                date = report.updatedDate,
//                type = tenor,
//                interest = rate.intr.value.replace(",", ".").toDouble()
//            )
//        } else {
//            null
//        }
//    }


 */