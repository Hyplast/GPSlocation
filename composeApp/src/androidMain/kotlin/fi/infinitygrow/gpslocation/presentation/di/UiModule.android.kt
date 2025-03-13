package fi.infinitygrow.gpslocation.presentation.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
//import fi.infinitygrow.gpslocation.presentation.permission.AndroidLocationService
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
//import fi.infinitygrow.gpslocation.data.PreferencesFactory
import fi.infinitygrow.gpslocation.data.createDataStore
import fi.infinitygrow.gpslocation.data.database.DatabaseFactory
import fi.infinitygrow.gpslocation.data.datastore.dataStore
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.data.repository.TextToSpeechHelperImpl
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceController
import fi.infinitygrow.gpslocation.data.repository.WeatherServiceImpl
import fi.infinitygrow.gpslocation.presentation.settings_page.components.WeatherServiceControls

private val viewModelModule = module {
    single { LocationService(androidApplication()) }
    single<HttpClientEngine> { OkHttp.create() }
    single { dataStore(androidApplication())}
    single { DatabaseFactory(androidApplication()) }
    single { TextToSpeechHelperImpl(androidApplication()) }
    //single { WeatherServiceImpl(androidApplication()) }
    single { WeatherServiceImpl() }
    single { WeatherServiceController(androidApplication()) }


}

//viewModel { WeatherViewModel(get(), get(), get(), get(), get()) }
//single<LocationService> { getLocationService() }
//single { AndroidLocationService(androidApplication(),get()) }
//single { SettingsRepository(get()) }
//single { PrefencesFactory(androidApplication()) }
//    single<DataStore<Preferences>> {
//        createDataStore(androidApplication())
//    }

actual fun sharedViewModelModule(): Module = viewModelModule