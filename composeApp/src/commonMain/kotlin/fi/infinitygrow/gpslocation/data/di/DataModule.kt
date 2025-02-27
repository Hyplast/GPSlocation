package fi.infinitygrow.gpslocation.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.core.data.HttpClientFactory
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME
//import fi.infinitygrow.gpslocation.data.PreferencesFactory
import fi.infinitygrow.gpslocation.data.datastore.PreferencesManager
import fi.infinitygrow.gpslocation.data.datastore.createDataStore
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.FmiApiService
import fi.infinitygrow.gpslocation.data.remote.KtorClient
import fi.infinitygrow.gpslocation.data.remote.KtorFmiApiService
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.data.repository.WeatherRepositoryImpl
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {

   // single { PreferencesManager(get()) }

    single<DataStore<Preferences>> {
        // Provide the DataStore instance
        createDataStore { DATA_STORE_FILE_NAME }
    }
    //single { createDataStore(get())}
   // single { PreferencesFactory.createDataStore() }
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorFmiApiService).bind<FmiApiService>()
    factory { KtorClient.client }
    factory <ApiService>{ ApiService(get()) }
    factory<WeatherRepository> { WeatherRepositoryImpl(get(), get()) }
    single { SettingsRepository(get()) }
//    single(
//        LocationService,
//        createdAtStart = TODO(),
//        definition = TODO()
//    )
}