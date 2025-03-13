package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.domain.repository.WeatherService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class WeatherServiceImpl : WeatherService {
//    override fun startWeatherUpdates()
//
//    override fun stopWeatherUpdates()
}

// Expect declaration that will be implemented differently on each platform
expect class WeatherServiceController {
    fun isServiceRunning(): Boolean
    fun startWeatherService()
    fun stopWeatherService()
    fun toggleWeatherService()
}