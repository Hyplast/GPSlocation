package fi.infinitygrow.gpslocation.domain.model

import fi.infinitygrow.gpslocation.presentation.permission.Location

data class RoadObservationData(
        val name: String,
        val coordinates: Location,
        val longitude: Double,
        val latitude: Double,
        val unixTime: Long,
        val airTemperature: Double,
        val humidity: Double,
        val dewPoint: Double,
        val windSpeed: Double,
        val windDirection: Double,
        val windGust: Double,
        val visibility: Double,
        val precipitationCodes: Double,
        val precipitationIntensity: Double,
        val precipitationAmount: Double,
        val precipitationCodes2: Double,
        val roadSurfaceTemperature: Double,
        val roadSurfaceTemperature2: Double,
        val roadGroundTemperature: Double,
        val roadGroundTemperature2: Double,
        val airTemperature2: Double,
        val roadSurfaceTemperature3: Double,
        val humidity3: Double,
        val stateRoadCondition: Double,
        val alertRoadCondition: Double,
        val friction: Double,
        val waterLayer: Double,
        val snowLayer: Double,
        val iceLayer: Double,
    )

/*

Tie 110 Lohja, Oinola tänään klo 17.39, tuuli 5, ilma -0.2 C tie -0.9 - Tienpinta on märkä ja suolattu - precipitationCodes=17.0 precipitationIntensity=0.1 precipitationCodes2=11.0

 */