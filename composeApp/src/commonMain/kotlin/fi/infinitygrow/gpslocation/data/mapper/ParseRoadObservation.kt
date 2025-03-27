package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import io.github.aakira.napier.Napier
import nl.adaptivity.xmlutil.serialization.XML


fun deserializeRoadObservation(
    xmlString: String,
    fetchedFromLocation: Location
): List<RoadObservationData> {
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

        val names = observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers
            .map { it.point.locationName }
        val locations = observation.data.gridSeriesObservation.featureOfInterest.sam.shape.multiPoint.pointMembers
            .map { it.point.locationPosition }

        // Create a list of triples: (lon, lat, unixTime) (from the positions string)
        val locationMeasurements = cleanString(
            observation.data.gridSeriesObservation.result.multiPointCoverage.domainSet.simpleMultiPoint.positions
        )
            .trim()
            .split(" ")
            .chunked(3) { (lon, lat, time) ->
                Triple(lon.toDouble(), lat.toDouble(), time.toLong())
            }

        // Parse measurements in groups of 24 values.
        val measurements = cleanString(
            observation.data.gridSeriesObservation.result.multiPointCoverage.rangeSet.dataBlock.tupleList
        )
            .trim()
            .split(" ")
            .chunked(24)
            .map { chunk ->
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
                    chunk[12].toDouble(),
                    chunk[13].toDouble(),
                    chunk[14].toDouble(),
                    chunk[15].toDouble(),
                    chunk[16].toDouble(),
                    chunk[17].toDouble(),
                    chunk[18].toDouble(),
                    chunk[19].toDouble(),
                    chunk[20].toDouble(),
                    chunk[21].toDouble(),
                    chunk[22].toDouble(),
                    chunk[23].toDouble()
                )
            }

        // Step 1: Count each occurrence of a (lon, lat) in the locationMeasurements list.
        val locationCounts = locationMeasurements
            .groupingBy { it.first to it.second }  // Group by (lon, lat)
            .eachCount()

        val observationDataList = mutableListOf<RoadObservationData>()
        var measurementIndex = 0

        // Instead of nested loops over `locations`, iterate over each measurement,
        // matching it with its (lon, lat) and, if needed, repeat based on count.
        for (i in names.indices) {
            // Assuming names and locations are aligned, get the (lon, lat) from
            // the second source (land-based positions) if needed.
            val locParts = locations[i].split(" ")
            if (locParts.size < 2) continue
            val nameLon = locParts[0].toDoubleOrNull() ?: continue
            val nameLat = locParts[1].toDoubleOrNull() ?: continue

            val count = locationCounts[nameLon to nameLat] ?: 1

            // For each occurrence, add the observation data.
            repeat(count) { j ->
                val currentIndex = measurementIndex + j
                if (currentIndex < locationMeasurements.size &&
                    currentIndex < measurements.size
                ) {
                    val (lon, lat, unixTime) = locationMeasurements[currentIndex]
                    observationDataList.add(
                        RoadObservationData(
                            name = names[i],
                            coordinates = fetchedFromLocation,
                            longitude = lon,
                            latitude = lat,
                            unixTime = unixTime,
                            airTemperature = measurements[currentIndex][0],
                            humidity = measurements[currentIndex][1],
                            dewPoint = measurements[currentIndex][2],
                            windSpeed = measurements[currentIndex][3],
                            windDirection = measurements[currentIndex][4],
                            windGust = measurements[currentIndex][5],
                            visibility = measurements[currentIndex][6],
                            precipitationCodes = measurements[currentIndex][7],
                            precipitationIntensity = measurements[currentIndex][8],
                            precipitationAmount = measurements[currentIndex][9],
                            precipitationCodes2 = measurements[currentIndex][10],
                            roadSurfaceTemperature = measurements[currentIndex][11],
                            roadSurfaceTemperature2 = measurements[currentIndex][12],
                            roadGroundTemperature = measurements[currentIndex][13],
                            roadGroundTemperature2 = measurements[currentIndex][14],
                            airTemperature2 = measurements[currentIndex][15],
                            roadSurfaceTemperature3 = measurements[currentIndex][16],
                            humidity3 = measurements[currentIndex][17],
                            stateRoadCondition = measurements[currentIndex][18],
                            alertRoadCondition = measurements[currentIndex][19],
                            friction = measurements[currentIndex][20],
                            waterLayer = measurements[currentIndex][21],
                            snowLayer = measurements[currentIndex][22],
                            iceLayer = measurements[currentIndex][23]
                        )
                    )
                }
            }
            measurementIndex += count  // move forward by the count for this location
            if (measurementIndex >= locationMeasurements.size) break
        }

        observationDataList
    } catch (e: Exception) {
        Napier.e("ERROR + ${e.message}", tag = "ERROR")
        e.printStackTrace()
        emptyList()
    }
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
////                        observationDataList.add(
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