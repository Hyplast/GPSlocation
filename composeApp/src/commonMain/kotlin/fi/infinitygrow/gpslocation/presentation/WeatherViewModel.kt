package fi.infinitygrow.gpslocation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.use_case.GetCurrentWeatherInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetForecastInfoUseCase
import fi.infinitygrow.gpslocation.presentation.utils.common
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getCurrentWeatherInfoUseCase: GetCurrentWeatherInfoUseCase,
    private val getForecastInfoUseCase: GetForecastInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow().common()

    fun getCurrentWeatherInfo(lat: Double, long: Double) = viewModelScope.launch {
        println("Fetching current weather for lat: $lat, lon: $long")
        val response = getCurrentWeatherInfoUseCase.invoke(lat, long)
        if (response.isSuccess) {
            println("Weather fetched successfully: $response")
            _uiState.update { it.copy(currentWeather = response.getOrNull()) }
        } else {
            println("Error fetching weather: $response")
            _uiState.update { it.copy(error = response.exceptionOrNull().toString()) }
        }
    }

    fun getForecastInfo(lat: Double,long:Double) = viewModelScope.launch {
        val response = getForecastInfoUseCase.invoke(lat, long)
        if(response.isSuccess){
            _uiState.update { it.copy(forecastInfo = response.getOrNull()) }
        }else{
            _uiState.update { it.copy(error = response.exceptionOrNull().toString()) }
        }
    }

}

data class UiState(
    val error: String = "",
    val currentWeather: Weather? = null,
    val forecastInfo: List<ForeCast>? = null
)