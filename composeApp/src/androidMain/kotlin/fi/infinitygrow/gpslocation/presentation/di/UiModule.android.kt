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
import fi.infinitygrow.gpslocation.data.datastore.dataStore
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository

private val viewModelModule = module {
    viewModel { WeatherViewModel(get(), get(), get()) }
    //single<LocationService> { getLocationService() }
    single { LocationService(androidApplication()) }
    //single { AndroidLocationService(androidApplication(),get()) }
    single<HttpClientEngine> { OkHttp.create() }
    //single { SettingsRepository(get()) }
    //single { PrefencesFactory(androidApplication()) }
//    single<DataStore<Preferences>> {
//        createDataStore(androidApplication())
//    }
    single { dataStore(androidApplication())}

}

actual fun sharedViewModelModule(): Module = viewModelModule