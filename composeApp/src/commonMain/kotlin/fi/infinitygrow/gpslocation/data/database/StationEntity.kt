package fi.infinitygrow.gpslocation.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StationEntity(
    @PrimaryKey(autoGenerate = false) val name: String,
    val fmisid: Int,
    val latitude: Double,
    val longitude: Double,
    val url: String
)