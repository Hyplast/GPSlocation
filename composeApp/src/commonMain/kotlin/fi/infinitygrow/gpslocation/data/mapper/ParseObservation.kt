package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import io.github.aakira.napier.Napier
import nl.adaptivity.xmlutil.serialization.XML

fun deserializeObservation(xmlString: String, fetchedFromLocation: Location): List<ObservationData> {
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
            .chunked(13)
            .map { chunk -> //(temp, wind, windGust, windDir, humidity, dewPoint, rainAmount, rainIntensity, snowAmount, pressure, visibility, clouds, autoWeather) ->
                listOf(
                    chunk[0].toDouble(),
                    chunk[1].toDouble(),
                    chunk[2].toDouble(),
                    chunk[3].toDouble(),
                    chunk[4].toDouble(),
                    chunk[5].toDouble(),
                    chunk[6].toDouble(),
                    chunk[7].toDouble(),
                    chunk[8].toDouble(),
                    chunk[9].toDouble(),
                    chunk[10].toDouble(),
                    chunk[11].toDouble(),
                    chunk[12].toDouble())
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
                            coordinates = fetchedFromLocation,
                            longitude = measurement.first,
                            latitude = measurement.second,
                            unixTime = measurement.third,
                            temperature = measurements[index][0],
                            windSpeed = measurements[index][1],
                            windGust = measurements[index][2],
                            windDirection = measurements[index][3],
                            humidity = measurements[index][4],
                            dewPoint = measurements[index][5],
                            precipitationAmount = measurements[index][6],
                            precipitationIntensity = measurements[index][7],
                            snowDepth = measurements[index][8],
                            pressure = measurements[index][9],
                            visibility = measurements[index][10],
                            cloudAmount = measurements[index][11],
                            presentWeather = measurements[index][12]
                        )
                    )
                }
            }
//(temp, wind, windGust, windDir, humidity, dewPoint, rainAmount, rainIntensity, snowAmount, pressure, visibility, clouds, autoWeather)
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
        Napier.e("ERROR + ${e.message}", tag = "ERROR")
        e.printStackTrace()
        emptyList()
    }
}