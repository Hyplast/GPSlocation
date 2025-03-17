package fi.infinitygrow.gpslocation.data.mapper

import fi.infinitygrow.gpslocation.data.model.observation.ObservationDTO
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.presentation.permission.Location
import nl.adaptivity.xmlutil.serialization.XML


// https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::radiation::multipointcoverage&starttime=2025-03-04T12:31:24Z&endtime=2025-03-04T13:31:24Z&fmisid=100908&fmisid=100968&fmisid=101004&fmisid=101104&fmisid=101339&fmisid=101756&fmisid=101932&fmisid=102035&&timestep=5&
// https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::radiation::multipointcoverage&starttime=2025-03-04T12:31:24Z&endtime=2025-03-04T13:31:24Z&fmisid=100908&timestep=5&
// https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::radiation::multipointcoverage&starttime=2025-03-04T12:31:24Z&endtime=2025-03-04T13:31:24Z&timestep=5&

fun deserializeRadiation(xmlString: String, fetchedFromLocation: Location): List<RadiationData> {
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
                    chunk[8].toDouble()
                )
            }

        // Step 1: Count occurrences of each (longitude, latitude)
        val locationCounts = locationMeasurements
            .groupingBy { it.first to it.second }  // Group by (lon, lat)
            .eachCount()

        // Step 2: Build the list
        val observationDataList = mutableListOf<RadiationData>()
        var measurementIndex = 0


        // If 1 measurement every minute 1, keep only 1 out of 10,
        // if 1 out of 10, keep them
        for ((i, location) in locations.withIndex()) {
            val (lon, lat) = locationMeasurements[measurementIndex].first to locationMeasurements[measurementIndex].second
            val count = locationCounts[lon to lat] ?: 1  // Default to 1 if missing

//            val isHighFrequency = count > 10  // More than 10 means every minute
//            val filteredMeasurements = if (isHighFrequency) {
//                locationMeasurements
//                    .filter { it.first == lon && it.second == lat }
//                    .filterIndexed { index, _ -> index % 10 == 0 }  // Keep only 1 out of 10
//            } else {
//                locationMeasurements.filter { it.first == lon && it.second == lat }
//            }

            locationMeasurements.forEachIndexed { j, measurement ->
                val index = measurementIndex + j
                if (index < measurements.size) {
                    observationDataList.add(
                        RadiationData(
                            name = names[i],
                            coordinates = fetchedFromLocation,
                            longitude = measurement.first,
                            latitude = measurement.second,
                            unixTime = measurement.third,
                            longWaveIn = measurements[index][0],
                            longWaveOut = measurements[index][1],
                            globalRadiation = measurements[index][2],
                            directRadiation = measurements[index][3],
                            reflectedRadiation = measurements[index][4],
                            sunshineDuration = measurements[index][5],
                            diffuseRadiation = measurements[index][6],
                            radiationBalance = measurements[index][7],
                            uvRadiation = measurements[index][8]
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