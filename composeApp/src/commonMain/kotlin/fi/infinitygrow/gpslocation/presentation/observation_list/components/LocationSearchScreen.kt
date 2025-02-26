package fi.infinitygrow.gpslocation.presentation.observation_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation

@Composable
fun LocationSearchScreen(
    modifier: Modifier = Modifier,
    locations: List<ObservationLocation>,
    observationLocations: MutableList<ObservationLocation>, // List to store selected locations
    onLocationSelected: (ObservationLocation) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isLocationSelected by remember { mutableStateOf(false) }

    val filteredLocations = locations.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    // Get the keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxWidth()) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isLocationSelected = false // Hide list when typing
            },
            label = { Text("Search Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Only show location list if user has typed something or hasn't selected a location
        if (searchQuery.isNotEmpty() && !isLocationSelected) {
            LazyColumn {
                items(filteredLocations) { location ->
                    Text(
                        text = location.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = ""
                                val observationLocation = ObservationLocation(
                                    name = location.name,
                                    fmiId = location.fmiId,
                                    lpnnId = location.lpnnId,
                                    wmoId = location.wmoId,
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    altitude = location.altitude,
                                    type = location.type,
                                    year = location.year
                                )
                                observationLocations.add(observationLocation)
                                onLocationSelected(location)
                                isLocationSelected = true // Hide list after selecting
                                keyboardController?.hide() // Hide the keyboard
                            }
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

