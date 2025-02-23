package fi.infinitygrow.gpslocation.domain.model

//import kotlinx.datetime.Instant
//import kotlinx.datetime.TimeZone
//import kotlinx.datetime.toLocalDateTime


data class ObservationData(
    val name: String,
    val coordinates: String,
    val longitude: Double,
    val latitude: Double,
    val unixTime: Long,
    val temperature: Double,
    val windSpeed: Double,
    val windMax: Double,
    val windDirection: Double,
    val pressure: Double
) //{
//    fun getFormattedTime(): String {
//        val instant = Instant.fromEpochSeconds(unixTime)
//        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
//
//        return "${localDateTime.date} ${localDateTime.time}"
//    }
//}
