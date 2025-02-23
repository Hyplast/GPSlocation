package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import nl.adaptivity.xmlutil.serialization.XML

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

        // Step 1: Count occurrences of each (longitude, latitude)
        val locationCounts = locationMeasurements
            .groupingBy { it.first to it.second }  // Group by (lon, lat)
            .eachCount()

        // Step 2: Build the list
        val observationDataList = mutableListOf<ObservationData>()
        var measurementIndex = 0


        // If 1 measurement every minute 1, keep only 1 out of 10,
        // if 1 out of 10, keep them
        for ((i, location) in locations.withIndex()) {
            val (lon, lat) = locationMeasurements[measurementIndex].first to locationMeasurements[measurementIndex].second
            val count = locationCounts[lon to lat] ?: 1  // Default to 1 if missing

            val isHighFrequency = count > 10  // More than 10 means every minute
            val filteredMeasurements = if (isHighFrequency) {
                locationMeasurements
                    .filter { it.first == lon && it.second == lat }
                    .filterIndexed { index, _ -> index % 10 == 0 }  // Keep only 1 out of 10
            } else {
                locationMeasurements.filter { it.first == lon && it.second == lat }
            }

            filteredMeasurements.forEachIndexed { j, measurement ->
                val index = measurementIndex + j
                if (index < measurements.size) {
                    observationDataList.add(
                        ObservationData(
                            name = names[i],
                            coordinates = location,
                            longitude = measurement.first,
                            latitude = measurement.second,
                            unixTime = measurement.third,
                            temperature = measurements[index][0],
                            windSpeed = measurements[index][1],
                            windMax = measurements[index][2],
                            windDirection = measurements[index][3],
                            pressure = measurements[index][4]
                        )
                    )
                }
            }

            measurementIndex += count  // Move index forward
        }

        // Keep all measurements, 10 out of 10 for
//        for ((i, location) in locations.withIndex()) {
//            val parts = location.split(" ")  // Assuming it's stored as "60.46415 23.64976"
//            if (parts.size >= 2) {
//                val lon = parts[0].toDoubleOrNull() ?: continue
//                val lat = parts[1].toDoubleOrNull() ?: continue
//
//                val count = locationCounts[lon to lat] ?: 1  // Default to 1 if missing
//                repeat(count) { j ->
//                    val index = measurementIndex + j
//                    if (index < locationMeasurements.size && index < measurements.size) {
//                        observationDataList.add(
//                            ObservationData(
//                                name = names[i],
//                                coordinates = location,
//                                longitude = locationMeasurements[index].first,
//                                latitude = locationMeasurements[index].second,
//                                unixTime = locationMeasurements[index].third,
//                                temperature = measurements[index][0],
//                                windSpeed = measurements[index][1],
//                                windMax = measurements[index][2],
//                                windDirection = measurements[index][3],
//                                pressure = measurements[index][4]
//                            )
//                        )
//                    }
//                }
//                measurementIndex += count
//            }
//        }

        observationDataList
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
