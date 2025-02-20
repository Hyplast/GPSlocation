package fi.infinitygrow.gpslocation.presentation.di

import fi.infinitygrow.gpslocation.presentation.WeatherViewModel
//import fi.infinitygrow.gpslocation.presentation.permission.AndroidLocationService
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private val viewModelModule = module {
    viewModel { WeatherViewModel(get(), get()) }
    //single<LocationService> { getLocationService() }
    single { LocationService(androidApplication()) }
    //single { AndroidLocationService(androidApplication(),get()) }
}

actual fun sharedViewModelModule(): Module = viewModelModule