package fi.infinitygrow.gpslocation.presentation.di

import fi.infinitygrow.gpslocation.presentation.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module

private val viewModelModule = module {
    single { WeatherViewModel(get(),get()) }
    single { LocationService() }
}

actual fun sharedViewModelModule(): Module = viewModelModule

object ProvideViewModel: KoinComponent{

    fun getWeatherViewModel() : WeatherViewModel = get()

}