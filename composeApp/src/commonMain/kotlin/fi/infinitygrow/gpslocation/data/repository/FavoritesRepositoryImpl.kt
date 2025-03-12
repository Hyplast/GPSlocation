package fi.infinitygrow.gpslocation.data.repository

import androidx.sqlite.SQLiteException
import fi.infinitygrow.gpslocation.data.database.StationDao
import fi.infinitygrow.gpslocation.data.database.StationEntity
import fi.infinitygrow.gpslocation.data.mapper.toStationEntity
import fi.infinitygrow.gpslocation.domain.model.ObservationLocation
import fi.infinitygrow.gpslocation.domain.model.getObservationLocationByName
import fi.infinitygrow.gpslocation.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import fi.infinitygrow.gpslocation.data.mapper.toObservationLocation
import kotlinx.coroutines.flow.mapNotNull

class FavoritesRepositoryImpl(
    private val stationDao: StationDao
) : FavoritesRepository {
    override suspend fun addFavorite(favorite: ObservationLocation) {
        return try {
            stationDao.upsertStation(favorite.toStationEntity())
        }
        catch (e: SQLiteException) {
            throw e
        }
    }

    override suspend fun removeFavorite(name: String) {
        stationDao.deleteStation(name)
    }

    override fun observeFavorites(): Flow<List<ObservationLocation>> {
        return stationDao.getStations()
            .map { stations ->
             stations.mapNotNull { it.toObservationLocation() }
        }
    }
}

