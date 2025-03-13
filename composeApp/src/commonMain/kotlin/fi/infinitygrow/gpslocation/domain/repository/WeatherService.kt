package fi.infinitygrow.gpslocation.domain.repository

interface WeatherService {
    fun startWeatherUpdates()
    fun stopWeatherUpdates()
}

