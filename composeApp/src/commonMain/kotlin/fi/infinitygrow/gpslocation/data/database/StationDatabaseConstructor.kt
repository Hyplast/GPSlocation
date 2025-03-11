@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fi.infinitygrow.gpslocation.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object StationDatabaseConstructor: RoomDatabaseConstructor<StationDatabase> {
    override fun initialize(): StationDatabase
}