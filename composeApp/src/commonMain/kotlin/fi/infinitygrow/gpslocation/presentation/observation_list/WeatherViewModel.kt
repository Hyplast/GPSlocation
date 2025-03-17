package fi.infinitygrow.gpslocation.presentation.observation_list

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fi.infinitygrow.gpslocation.data.repository.SettingsRepository
import fi.infinitygrow.gpslocation.domain.model.ForeCast
import fi.infinitygrow.gpslocation.domain.model.ObservationData
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.Weather
import fi.infinitygrow.gpslocation.domain.model.getObservationLocation
import fi.infinitygrow.gpslocation.domain.repository.FavoritesRepository
import fi.infinitygrow.gpslocation.domain.use_case.GetCurrentWeatherInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetForecastInfoUseCase
import fi.infinitygrow.gpslocation.domain.use_case.GetObservationUseCase
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import fi.infinitygrow.gpslocation.presentation.utils.common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getCurrentWeatherInfoUseCase: GetCurrentWeatherInfoUseCase,
    private val getForecastInfoUseCase: GetForecastInfoUseCase,
    private val getObservationUseCase: GetObservationUseCase,
    private val locationService: LocationService,
    private val favoritesRepository: FavoritesRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow().common()

    val longPressedItems =  mutableStateListOf<ObservationData>()
    //val longPressedItems2 = getObservationLocation(favorites)
    val selectedLocations = mutableStateListOf<ObservationLocation>()

    val isDarkTheme: StateFlow<Boolean> = settingsRepository.darkThemeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    //val useLocation = settingsRepository.locationFlow.collectAsState()
    val useLocation: StateFlow<Boolean> = settingsRepository.locationFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    // Expose observable favorites
    val favorites: StateFlow<List<ObservationLocation>> =
        favoritesRepository.observeFavorites()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    init {
        // Synchronize the selectedLocations list whenever favorites updates.
        viewModelScope.launch {
            favorites.collect { favList ->
                selectedLocations.clear()
                selectedLocations.addAll(favList)
            }
        }
    }

    fun addFavorite(favorite: ObservationLocation) {
        viewModelScope.launch {
            favoritesRepository.addFavorite(favorite)
        }
    }

    fun removeFavorite(name: String) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(name)
        }
    }

    fun getLocationPermission(): Boolean {
        return locationService.isPermissionGranted()
    }

    fun refreshWeather(selectedLocations: List<ObservationLocation>) {
        println("Selected Locations are being fecthed from API")
        println(selectedLocations)
        viewModelScope.launch(Dispatchers.IO) {
            if (locationService.isPermissionGranted()) {
                locationService.getLocation()?.let { location ->
                    println("fetching weather is PermissionGranted!")
                    getCurrentWeatherInfo(location.latitude, location.longitude)
                    getForecastInfo(location.latitude, location.longitude)
                    getObservation(
                        if (useLocation.value) location.latitude else null,
                        if (useLocation.value) location.longitude else null,
                        selectedLocations
                    )
                }
            } else {
                locationService.requestLocationPermission { granted ->
                    if (granted) {
                        viewModelScope.launch(Dispatchers.IO) {
                            locationService.getLocation()?.let { location ->
                                println("fetching weather requestLocationPermission!")
                                getCurrentWeatherInfo(location.latitude, location.longitude)
                                getForecastInfo(location.latitude, location.longitude)
                                getObservation(
                                    if (useLocation.value) location.latitude else null,
                                    if (useLocation.value) location.longitude else null,
                                    selectedLocations
                                )
                            }
                        }
                    } else {
                        viewModelScope.launch(Dispatchers.IO) {
                            println("fething weather, location permission denied!")
                            getObservation(null, null, selectedLocations)
                        }
                    }
                    // You might want to update the UI state to indicate permission denied.
                }
            }
        }
    }

    fun toggleLongPress(item: ObservationData) {
        if (longPressedItems.contains(item)) {
            longPressedItems.remove(item) // Remove if already long-pressed
            getObservationLocation(item)?.let {
                removeFavorite(item.name)
            }
        } else {
            longPressedItems.add(item) // Add if not already long-pressed
            getObservationLocation(item)?.let {
                addFavorite(favorite = it)
            }
        }
    }

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

    fun getObservation(lat: Double?, long: Double?, observationList: List<ObservationLocation>) = viewModelScope.launch {
        val response = getObservationUseCase.invoke(lat, long, observationList)
        if(response.isSuccess){
            println("Observation fetched successfully")//: $response")
            _uiState.update { it.copy(observationInfo = response.getOrNull()) }
        }else{
            println("Error fetching observation:")// $response")
            _uiState.update { it.copy(error = response.exceptionOrNull().toString()) }
        }
    }

    fun getNewestObservations(observations: List<ObservationData>): List<ObservationData> {
        // Group observations by name
        val groupedObservations = observations.groupBy { it.name }

        // Select the newest observation for each location
        return groupedObservations.map { (_, obsList) ->
            obsList.maxByOrNull { it.unixTime } // Get the observation with the latest unixTime
        }.filterNotNull() // Remove any nulls in case there were empty groups
    }

}

data class UiState(
    val error: String = "",
    val isRefreshing: Boolean = false, //update copy?
    val currentWeather: Weather? = null,
    val forecastInfo: List<ForeCast>? = null,
    val observationInfo: List<ObservationData>? = null,
)