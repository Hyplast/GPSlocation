package fi.infinitygrow.gpslocation.domain.repository

import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.Weather


interface WeatherRepository {

    suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Weather

    suspend fun getForecastInfo(lat: Double, long: Double): List<ForeCast>

    suspend fun getObservation(latitude: Double?, longitude: Double?, observationList: List<ObservationLocation>): List<ObservationData>

    //"wfs/fin?service=WFS&version=2.0.0&request=GetFeature&storedquery_id=fmi::observations::radiation::multipointcoverage&parameters=GLOB_1MIN&timestep=10&
    suspend fun getSunRadiation(latitude: Double, longitude: Double): List<RadiationData>
    //wfs?service=WFS&version=2.0.0&request=GetFeature&storedquery_id=fmi::observations::lightning::multipointcoverage&
    suspend fun getLightningStrikes(latitude: Double, longitude: Double): String
}

//interface FmiWeatherRepository {
//    suspend fun getObservation(latitude: Double, longitude: Double): List<ObservationData>
//}