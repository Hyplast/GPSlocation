package fi.infinitygrow.gpslocation.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Upsert
    suspend fun upsertStation(station: StationEntity)

    @Query("SELECT * FROM StationEntity")
    fun getStations(): Flow<List<StationEntity>>

    @Query("SELECT * FROM StationEntity WHERE name = :name")
    suspend fun getStationByName(name: String): StationEntity?

    @Query("DELETE FROM StationEntity WHERE name = :name")
    suspend fun deleteStation(name: String)

}