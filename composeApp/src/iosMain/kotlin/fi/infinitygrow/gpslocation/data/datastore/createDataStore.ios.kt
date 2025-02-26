package fi.infinitygrow.gpslocation.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

//// iOSMain
//@OptIn(ExperimentalForeignApi::class, ExperimentalForeignApi::class)
//actual fun createPlatformDataStore(): DataStore<Preferences> {
//    return createDataStore {
//        val directory = NSFileManager.defaultManager.URLForDirectory(
//            directory = NSDocumentDirectory,
//            inDomain = NSUserDomainMask,
//            appropriateForURL = null,
//            create = false,
//            error = null
//        )
//        requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
//    }
//}


// in src/iosMain/kotlin
@OptIn(ExperimentalForeignApi::class)
fun dataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        requireNotNull(documentDirectory).path + "/$$DATA_STORE_FILE_NAME"
    }
)