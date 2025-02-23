package fi.infinitygrow.gpslocation.data.di

import fi.infinitygrow.gpslocation.core.data.HttpClientFactory
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.FmiApiService
import fi.infinitygrow.gpslocation.data.remote.KtorClient
import fi.infinitygrow.gpslocation.data.remote.KtorFmiApiService
import fi.infinitygrow.gpslocation.data.repository.WeatherRepositoryImpl
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorFmiApiService).bind<FmiApiService>()
    factory { KtorClient.client }
    factory <ApiService>{ ApiService(get()) }
    factory<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
//    single(
//        LocationService,
//        createdAtStart = TODO(),
//        definition = TODO()
//    )
}