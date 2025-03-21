package fi.infinitygrow.gpslocation.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import fi.infinitygrow.gpslocation.core.data.HttpClientFactory
import fi.infinitygrow.gpslocation.data.database.DatabaseFactory
import fi.infinitygrow.gpslocation.data.database.StationDatabase
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME
import fi.infinitygrow.gpslocation.data.datastore.createDataStore
import fi.infinitygrow.gpslocation.data.remote.ApiService
import fi.infinitygrow.gpslocation.data.remote.FmiApiService
import fi.infinitygrow.gpslocation.data.remote.KtorClient
import fi.infinitygrow.gpslocation.data.remote.KtorFmiApiService
import fi.infinitygrow.gpslocation.data.repository.FavoritesRepositoryImpl
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.data.repository.WeatherRepositoryImpl
import fi.infinitygrow.gpslocation.domain.repository.FavoritesRepository
import fi.infinitygrow.gpslocation.domain.repository.WeatherRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<StationDatabase>().stationDao }
    single<DataStore<Preferences>> {
        createDataStore { DATA_STORE_FILE_NAME }
    }
    single { HttpClientFactory.create(get()) }
    singleOf(::KtorFmiApiService).bind<FmiApiService>()
    singleOf(::FavoritesRepositoryImpl).bind<FavoritesRepository>()
    factory { KtorClient.client }
    factory <ApiService>{ ApiService(get()) }
    factory<WeatherRepository> { WeatherRepositoryImpl(get(), get(), get()) }
    single { SettingsRepository(get()) }

    //factory<WeatherService>  { WeatherServiceImpl() }

    //singleOf(::WeatherServiceImpl).bind<WeatherService>()
//    single<WeatherService> { WeatherServiceImpl(androidApplication()) }
//    single<WeatherServiceController> { WeatherServiceController(androidApplication()) }


}

// single { PreferencesManager(get()) }
// Provide the DataStore instance
//single { createDataStore(get())}
// single { PreferencesFactory.createDataStore() }
//    single(
//        LocationService,
//        createdAtStart = TODO(),
//        definition = TODO()
//    )
//singleOf(::WeatherRepositoryImpl).bind<WeatherRepository>()
//singleOf(::TextToSpeechHelperImpl)
//singleOf(::TextToSpeechHelperImpl).bind<TextToSpeechHelper>()