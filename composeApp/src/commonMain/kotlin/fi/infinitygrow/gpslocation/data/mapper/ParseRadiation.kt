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
            .chunked(9)
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
                        RadiationData(
                            name = names[i],
                            coordinates = fetchedFromLocation,
                            longitude = lon,
                            latitude = lat,
                            unixTime = unixTime,
                            longWaveIn = measurements[currentIndex][0],
                            longWaveOut = measurements[currentIndex][1],
                            globalRadiation = measurements[currentIndex][2],
                            directRadiation = measurements[currentIndex][3],
                            reflectedRadiation = measurements[currentIndex][4],
                            sunshineDuration = measurements[currentIndex][5],
                            diffuseRadiation = measurements[currentIndex][6],
                            radiationBalance = measurements[currentIndex][7],
                            uvRadiation = measurements[currentIndex][8]
                        )
                    )
                }
            }
            measurementIndex += count  // Move index forward
            if (measurementIndex >= locationMeasurements.size) break
        }

        observationDataList
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}