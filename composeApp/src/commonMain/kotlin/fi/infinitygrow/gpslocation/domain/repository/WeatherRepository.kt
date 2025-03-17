package fi.infinitygrow.gpslocation.domain.repository

import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.RadiationData
import fi.infinitygrow.gpslocation.domain.model.RoadObservationData
import fi.infinitygrow.gpslocation.domain.model.SoundingData
import fi.infinitygrow.gpslocation.domain.model.Weather


interface WeatherRepository {

    suspend fun getCurrentWeatherInfo(lat: Double, long: Double): Weather

    suspend fun getForecastInfo(lat: Double, long: Double): List<ForeCast>

    suspend fun getObservation(latitude: Double?, longitude: Double?, observationList: List<ObservationLocation>): List<ObservationData>


    // https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=livi::observations::road::default::multipointcoverage&bbox=23.11,59.97,24.41,60.67
    suspend fun getRoadObservation(latitude: Double?, longitude: Double?, observationList: List<ObservationLocation>): List<RoadObservationData>

    //"wfs/fin?service=WFS&version=2.0.0&request=GetFeature&storedquery_id=fmi::observations::radiation::multipointcoverage&parameters=GLOB_1MIN&timestep=10&
    suspend fun getSunRadiation(latitude: Double, longitude: Double): List<RadiationData>
    //wfs?service=WFS&version=2.0.0&request=GetFeature&storedquery_id=fmi::observations::lightning::multipointcoverage&
    suspend fun getLightningStrikes(latitude: Double, longitude: Double): String

    // https://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::sounding::multipointcoverage
    suspend fun getSounding(): List<SoundingData>

    suspend fun getSoilTemperature(latitude: Double, longitude: Double): List<ObservationData>

}

//interface FmiWeatherRepository {
//    suspend fun getObservation(latitude: Double, longitude: Double): List<ObservationData>
//}