package fi.infinitygrow.gpslocation.data.mapper


import fi.infinitygrow.gpslocation.domain.model.ObservationData
import io.ktor.http.ContentType.Application.Xml
import io.ktor.http.ContentType.Text.Xml
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
//import org.xmlpull.v1.XmlPullParser
//import org.xmlpull.v1.XmlPullParserFactory


/*
@Serializable
@XmlSerialName("root") // A wrapper for the root node
data class WeatherXml(
    @XmlElement(true) val A: List<String> = emptyList(),  // Names
    @XmlElement(true) val B: List<String> = emptyList(),  // Locations
    @XmlElement(true) val C: String = "",  // Location Coordinates + Timestamps
    @XmlElement(true) val D: String = ""   // Measurements
)


fun parseXml(xmlString: String): List<ObservationData> {
    val xmlParser = Xml { indent = 2 }  // Pretty printing enabled
    val rawData: WeatherXml = xmlParser.decodeFromString(xmlString)

    val locationMeasurements = rawData.C.trim().split(" ")
        .chunked(3) { (lon, lat, time) -> Triple(lon.toDouble(), lat.toDouble(), time.toLong()) }

    val measurements = rawData.D.trim().split(" ")
        .chunked(5) { (temp, wind, windMax, windDir, pressure) ->
            listOf(temp.toDouble(), wind.toDouble(), windMax.toDouble(), windDir.toDouble(), pressure.toDouble())
        }

    val chunkSize = locationMeasurements.size / rawData.A.size
    return rawData.A.indices.map { i ->
        val measurementEntries = List(chunkSize) { j ->
            val index = i * chunkSize + j
            MeasurementEntry(
                locationMeasurements[index].first,
                locationMeasurements[index].second,
                locationMeasurements[index].third,
                measurements[index][0],
                measurements[index][1],
                measurements[index][2],
                measurements[index][3],
                measurements[index][4]
            )
        }
        ObservationData(rawData.A[i], rawData.B[i], measurementEntries)
    }
}


/*


fun parseXml(xml: String): List<LocationData> {
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(xml.reader())

    val names = mutableListOf<String>()
    val locations = mutableListOf<String>()
    val locationMeasurements = mutableListOf<Triple<Double, Double, Long>>()  // (longitude, latitude, unixTime)
    val measurements = mutableListOf<List<Double>>() // [temp, wind, windMax, windDir, pressure]

    var currentTag: String? = null

    while (parser.next() != XmlPullParser.END_DOCUMENT) {
        when (parser.eventType) {
            XmlPullParser.START_TAG -> currentTag = parser.name
            XmlPullParser.TEXT -> {
                val text = parser.text.trim()
                if (text.isNotEmpty()) {
                    when (currentTag) {
                        "A" -> names.add(text)
                        "B" -> locations.add(text)
                        "C" -> {
                            val values = text.split(" ")
                            for (i in values.indices step 3) {
                                locationMeasurements.add(
                                    Triple(values[i].toDouble(), values[i + 1].toDouble(), values[i + 2].toLong())
                                )
                            }
                        }
                        "D" -> {
                            val values = text.split(" ")
                            for (i in values.indices step 5) {
                                measurements.add(
                                    listOf(
                                        values[i].toDouble(),
                                        values[i + 1].toDouble(),
                                        values[i + 2].toDouble(),
                                        values[i + 3].toDouble(),
                                        values[i + 4].toDouble()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Combine everything logically
    val result = mutableListOf<LocationData>()
    val chunkSize = locationMeasurements.size / names.size  // Number of measurements per location

    for (i in names.indices) {
        val measurementList = mutableListOf<MeasurementEntry>()
        for (j in 0 until chunkSize) {
            val index = i * chunkSize + j
            measurementList.add(
                MeasurementEntry(
                    locationMeasurements[index].first,
                    locationMeasurements[index].second,
                    locationMeasurements[index].third,
                    measurements[index][0],
                    measurements[index][1],
                    measurements[index][2],
                    measurements[index][3],
                    measurements[index][4]
                )
            )
        }
        result.add(LocationData(names[i], locations[i], measurementList))
    }

    return result
}
 */