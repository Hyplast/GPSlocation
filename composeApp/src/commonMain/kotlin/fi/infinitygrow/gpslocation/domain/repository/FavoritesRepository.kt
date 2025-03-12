package fi.infinitygrow.gpslocation.domain.repository

import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addFavorite(favorite: ObservationLocation)
    suspend fun removeFavorite(name: String)
    fun observeFavorites(): Flow<List<ObservationLocation>>
}