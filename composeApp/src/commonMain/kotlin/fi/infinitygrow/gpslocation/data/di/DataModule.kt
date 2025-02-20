package fi.infinitygrow.gpslocation.data.di

import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.KtorClient
import fi.infinitygrow.gpslocation.data.repository.WeatherRepositoryImpl
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import org.koin.dsl.module

val dataModule = module {
    factory { KtorClient.client }
    factory <ApiService>{ ApiService(get()) }
    factory<WeatherRepository> { WeatherRepositoryImpl(get()) }
//    single(
//        LocationService,
//        createdAtStart = TODO(),
//        definition = TODO()
//    )
}