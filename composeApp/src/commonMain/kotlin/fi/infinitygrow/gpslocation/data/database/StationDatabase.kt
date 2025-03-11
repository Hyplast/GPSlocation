package fi.infinitygrow.gpslocation.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [StationEntity::class],
    version = 1
)
@ConstructedBy(
    StationDatabaseConstructor::class
)
abstract class StationDatabase : RoomDatabase() {
    abstract val stationDao: StationDao

    companion object {
        const val DB_NAME = "station.db"
    }
}
