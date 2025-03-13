package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.domain.repository.WeatherService

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class WeatherServiceImpl : WeatherService {
    actual override fun startWeatherUpdates() {
    }

    actual override fun stopWeatherUpdates() {
    }

}

// Expect declaration that will be implemented differently on each platform
actual class WeatherServiceController actual constructor() {
    actual fun isServiceRunning(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun startWeatherService() {
    }

    actual fun stopWeatherService() {
    }

    actual fun toggleWeatherService() {
    }
}