package fi.infinitygrow.gpslocation.domain.di

import fi.infinitygrow.gpslocation.presentation.permissions_page.PermissionsViewModel
import fi.infinitygrow.gpslocation.app.TestViewModel
import fi.infinitygrow.gpslocation.domain.use_case.GetCurrentWeatherInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetForecastInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetObservationUseCase
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val domainModule = module {
    factory { GetCurrentWeatherInfoUseCase(get()) }
    factory {GetForecastInfoUseCase(get())}
    factory { GetObservationUseCase(get()) }
    //viewModelOf(::WeatherViewModel)
    viewModelOf(::PermissionsViewModel)
    viewModelOf(::TestViewModel)
    //single { appConfig } }
}