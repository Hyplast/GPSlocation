package fi.infinitygrow.gpslocation.presentation.di

import fi.infinitygrow.gpslocation.presentation.WeatherViewModel
//import fi.infinitygrow.gpslocation.presentation.permission.AndroidLocationService
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private val viewModelModule = module {
    viewModel { WeatherViewModel(get(), get(), get()) }
    //single<LocationService> { getLocationService() }
    single { LocationService(androidApplication()) }
    //single { AndroidLocationService(androidApplication(),get()) }
    single<HttpClientEngine> { OkHttp.create() }
}

actual fun sharedViewModelModule(): Module = viewModelModule