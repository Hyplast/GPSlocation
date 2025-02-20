package fi.infinitygrow.gpslocation.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {

    val client = HttpClient {
        install(ContentNegotiation){
            json(json = Json {
                ignoreUnknownKeys = true
            })
        }
    }
}