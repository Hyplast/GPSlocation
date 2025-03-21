package fi.infinitygrow.gpslocation.domain.di

import fi.infinitygrow.gpslocation.presentation.permissions_page.PermissionsViewModel
import fi.infinitygrow.gpslocation.domain.use_case.GetCurrentWeatherInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetForecastInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetObservationUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetRadiationUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetRoadObservationUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetSoundingUseCase
import fi.infinitygrow.gpslocation.presentation.settings_page.SettingsViewModel
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val domainModule = module {
    factory { GetCurrentWeatherInfoUseCase(get()) }
    factory {GetForecastInfoUseCase(get())}
    factory { GetObservationUseCase(get()) }
    factory { GetRoadObservationUseCase(get()) }
    factory { GetRadiationUseCase(get()) }
    factory { GetSoundingUseCase(get()) }
    viewModelOf(::PermissionsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::WeatherViewModel)
}