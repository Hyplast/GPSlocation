package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.observation.SoundingDTO
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import nl.adaptivity.xmlutil.serialization.XML

data class Quadruple(
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val time: Long,
)

fun parseSounding(xmlString: String, fetchedFromLocation: Location): List<SoundingData> {
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
            deserializer = SoundingDTO.serializer(),
            string = cleanedXmlString
        )

        val size = observation.data.size
        val observationDataList = mutableListOf<SoundingData>()



        for (i in 0 until size) {

            val placeOfSounding = observation.data[i].trajectoryObservation.featureOfInterest.sam.shape.multiPoint.pointMembers[0].point.locationName
            val timeOfSounding = observation.data[i].trajectoryObservation.resultTime.timeInstant.timePosition

            val locationMeasurements =
                cleanString(observation.data[i].trajectoryObservation.result.multiPointCoverage.domainSet.simpleMultiPoint.positions)
                    .trim()
                    .split(" ")
                    .chunked(4) { (lon, lat, alt, time) ->
                        Quadruple(
                            lon.toDouble(),
                            lat.toDouble(),
                            alt.toDouble(),
                            time.toLong()
                        )
                    }

            val measurements =
                cleanString(observation.data[i].trajectoryObservation.result.multiPointCoverage.rangeSet.dataBlock.tupleList)
                    .trim()
                    .split(" ")
                    .chunked(5)
                    .map { chunk ->
                        listOf(
                            chunk[0].toDouble(),
                            chunk[1].toDouble(),
                            chunk[2].toDouble(),
                            chunk[3].toDouble(),
                            chunk[4].toDouble()
                        )
                    }

            val measurementIndex = 0
            val numberOfElements = locationMeasurements.size
            for (j in 0 until numberOfElements) {

                repeat(numberOfElements) { k ->
                    val currentIndex = measurementIndex + k
                    if (currentIndex < locationMeasurements.size &&
                        currentIndex < measurements.size
                    ) {
                        val (lon, lat, alt, unixTime) = locationMeasurements[currentIndex]
                        observationDataList.add(
                            SoundingData(
                                name = placeOfSounding,
                                timeOfSounding = timeOfSounding,
                                coordinates = fetchedFromLocation,
                                longitude = lon,
                                latitude = lat,
                                altitude = alt,
                                unixTime = unixTime,
                                pressure = measurements[currentIndex][0],
                                windSpeed = measurements[currentIndex][1],
                                windDirection = measurements[currentIndex][2],
                                temperature = measurements[currentIndex][3],
                                dewPoint = measurements[currentIndex][4]
                            )
                        )
                    }
                }
            }
        }

        observationDataList

    }
    catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
