package fi.infinitygrow.gpslocation.data.mapper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


actual fun formatDate(millis: Long): String {
    val locale = Locale.getDefault()
    val formatter = SimpleDateFormat("dd MMM hh:mm a", locale)
    return formatter.format(Date(millis * 1000))
}