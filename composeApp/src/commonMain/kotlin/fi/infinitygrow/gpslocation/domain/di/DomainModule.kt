package fi.infinitygrow.gpslocation.domain.di

import fi.infinitygrow.gpslocation.PermissionsViewModel
import fi.infinitygrow.gpslocation.domain.use_case.GetCurrentWeatherInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetForecastInfoUseCase
import fi.infinitygrow.gpslocation.presentation.WeatherViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val domainModule = module {
    factory { GetCurrentWeatherInfoUseCase(get()) }
    factory {GetForecastInfoUseCase(get())}
    //viewModelOf(::WeatherViewModel)
    viewModelOf(::PermissionsViewModel)
}