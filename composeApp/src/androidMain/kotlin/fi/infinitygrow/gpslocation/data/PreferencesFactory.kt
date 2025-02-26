package fi.infinitygrow.gpslocation.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME

//@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
//actual class PreferencesFactory(
//    private val context: Context
//) {
//
//    actual fun createDataStore(): DataStore<Preferences> {
//        return fi.infinitygrow.gpslocation.data.datastore.createDataStore {
//            context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
//        }
//    }
//
//}
