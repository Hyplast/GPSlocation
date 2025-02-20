package fi.infinitygrow.gpslocation.data.mapper

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone

actual fun formatDate(millis: Long): String {

    val formatter = NSDateFormatter().apply {
        dateFormat = "dd MMM hh:mm a"
        timeZone = NSTimeZone.localTimeZone
    }

    val date = NSDate(millis.times(1000).toDouble())
    return formatter.stringFromDate(date)

}