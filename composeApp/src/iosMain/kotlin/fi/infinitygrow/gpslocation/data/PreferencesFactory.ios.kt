package fi.infinitygrow.gpslocation.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

//@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
//@OptIn(ExperimentalForeignApi::class)
//actual class PreferencesFactory {
//
//    actual fun createDataStore(): DataStore<Preferences> {
//        return createDataStore {
//            val directory = NSFileManager.defaultManager.URLForDirectory(
//                directory = NSDocumentDirectory,
//                inDomain = NSUserDomainMask,
//                appropriateForURL = null,
//                create = false,
//                error = null
//            )
//            requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
//        }
//    }
//}

//
//fun createDataStore(): DataStore<Preferences> {
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
//}