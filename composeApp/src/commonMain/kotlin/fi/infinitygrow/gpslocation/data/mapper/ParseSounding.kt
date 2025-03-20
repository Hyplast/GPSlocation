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

        val observationDataList = mutableListOf<SoundingData>()

        for (i in observation.data.indices) { // Iterate over observations

            //println("I number equals to: $i")
//            val placeOfSounding = observation.data[i].trajectoryObservation.featureOfInterest.sam.shape.multiPoint.pointMembers[0].point.locationName
//            val placeOfSounding = observation.data[i].trajectoryObservation.featureOfInterest.sam.shape.multiPoint.pointMembers[0].point.locationName
            val timeOfSounding = observation.data[i].trajectoryObservation.resultTime.timeInstant.timePosition
            val placeOfSounding = observation.data[i].trajectoryObservation.featureOfInterest.sam.relatedSamplingFeature.samplingFeatureComplex.relatedSamplingFeature.sFSpatialSamplingFeature.sampledFeature.location.names[0]

            val locationMeasurements = cleanString(observation.data[i].trajectoryObservation.result.multiPointCoverage.domainSet.simpleMultiPoint.positions)
                .trim()
                .split(" ")
                .chunked(4) { (lon, lat, alt, time) -> Quadruple(lon.toDouble(), lat.toDouble(), alt.toDouble(), time.toLong()) }

            //println("locationMeasurements size: ${locationMeasurements.size}")

            val trimmedMeasurements = cleanString(observation.data[i].trajectoryObservation.result.multiPointCoverage.rangeSet.dataBlock.tupleList).trim()
            // Split the trimmed measurements into tokens.
            val splittedMeasurements = trimmedMeasurements.split(" ").filter { it.isNotBlank() }

            // Calculate the remainder when the number of tokens is divided by 5.
            val remainder = splittedMeasurements.size % 5

            // Drop the last `remainder` elements if there are any extra elements.
            val validMeasurements = if (remainder == 0) {
                splittedMeasurements
            } else {
                splittedMeasurements.dropLast(remainder)
            }

                // Now that validMeasurements has a number of elements divisible by 5, we can chunk them.
            val measurements = validMeasurements
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

           // println("Chunked to: $i")
            // Pair up each location with measurement data.
            val measurementsByLocation = locationMeasurements.zip(measurements)

            // Iterate over measurements associated with this location
            for ((location, measurement) in measurementsByLocation) {
                val (lon, lat, alt, unixTime) = location

                observationDataList.add(
                    SoundingData(
                        name = placeOfSounding,
                        timeOfSounding = timeOfSounding,
                        coordinates = fetchedFromLocation,
                        longitude = lon,
                        latitude = lat,
                        altitude = alt,
                        unixTime = unixTime,
                        pressure = measurement[0],
                        windSpeed = measurement[1],
                        windDirection = measurement[2],
                        temperature = measurement[3],
                        dewPoint = measurement[4]
                    )
                )
            }
        }
        //println("observationDataList size: ${observationDataList.size}")

        observationDataList

    } catch (e: Exception) {
        e.printStackTrace()
        println("ERROR")
        println(e)
        emptyList()
    }
}
