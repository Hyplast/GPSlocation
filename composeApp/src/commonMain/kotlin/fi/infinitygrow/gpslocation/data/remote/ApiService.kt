package fi.infinitygrow.gpslocation.data.remote

import fi.infinitygrow.gpslocation.data.model.WeatherResponse
import fi.infinitygrow.gpslocation.data.model.forecast.ForecastResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.path

private const val APP_ID = "628839691ea3a9c9f032097d4de5f028"

class ApiService(val client: HttpClient) {

    // https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid=5a62a80b19dd9d3e3d6663f485720f83
    // https://api.openweathermap.org/data/2.5/weather?lat=20.34&lon=60.99&appid=628839691ea3a9c9f032097d4de5f028

    suspend fun currentWeatherInfo(lat: Double, long: Double): WeatherResponse {
        return client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.openweathermap.org"
                path("data/2.5/weather")
                parameters.append("lat", lat.toString())
                parameters.append("lon", long.toString())
                parameters.append("appid", APP_ID)
            }
        }.body<WeatherResponse>()
        //println(response)
        //return response
    }

    // https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=5a62a80b19dd9d3e3d6663f485720f83
    suspend fun forecastInfo(lat: Double, long: Double): ForecastResponse {
        return client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.openweathermap.org"
                path("data/2.5/forecast")
                parameters.append("lat", lat.toString())
                parameters.append("lon", long.toString())
                parameters.append("appid", APP_ID)
            }
        }.body<ForecastResponse>()
    }

    suspend fun fetchHolfuyData(): String {
        val client = HttpClient()
        val url = "https://holfuy.com/en/weather/546"

        return try {
            val response: HttpResponse = client.get(url)
            response.bodyAsText() // Get the full HTML
        } finally {
            client.close()
        }
    }

}