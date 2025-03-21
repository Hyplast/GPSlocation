package fi.infinitygrow.gpslocation.presentation.di

import fi.infinitygrow.gpslocation.data.database.DatabaseFactory
import fi.infinitygrow.gpslocation.data.datastore.dataStore
import fi.infinitygrow.gpslocation.data.repository.TextToSpeechHelperImpl
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceController
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceImpl
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

private val viewModelModule = module {
    single { LocationService(androidApplication()) }
    single<HttpClientEngine> { OkHttp.create() }
    single { dataStore(androidApplication())}
    single { DatabaseFactory(androidApplication()) }
    factory { TextToSpeechHelperImpl(androidApplication()) }
    single { WeatherServiceImpl() }
    single { WeatherServiceController(androidApplication()) }
}


actual fun sharedViewModelModule(): Module = viewModelModule