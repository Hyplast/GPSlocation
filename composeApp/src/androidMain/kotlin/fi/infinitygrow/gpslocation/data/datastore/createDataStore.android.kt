package fi.infinitygrow.gpslocation.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME
import fi.infinitygrow.gpslocation.data.datastore.createDataStore
import okio.Path.Companion.toPath

//// AndroidMain
//actual fun createPlatformDataStore(context: Context): DataStore<Preferences> {
//    return createDataStore {
//        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
//    }
//}

//fun createDataStore(producePath: () -> String): DataStore<Preferences> =
//    PreferenceDataStoreFactory.createWithPath(
//        produceFile = { producePath().toPath() }
//    )

// in src/androidMain/kotlin
fun dataStore(context: Context): DataStore<Preferences> =
    createDataStore(
        producePath = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath }
    )



//internal const val dataStoreFileName = "dice.preferences_pb"