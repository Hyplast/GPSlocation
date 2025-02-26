package fi.infinitygrow.gpslocation.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath



//expect fun createPlatformDataStore(producePath: () -> String): DataStore<Preferences> {
//
//}

//
//fun createDataStore(producePath: () -> String): DataStore<Preferences> {
//    return PreferenceDataStoreFactory.createWithPath(
//        produceFile = { producePath().toPath() }
//    )
//
//}


fun createDataStore(
    producePath: () -> String,
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = emptyList(),
    produceFile = { producePath().toPath() },
)


internal const val DATA_STORE_FILE_NAME = "prefs.preferences_pb"


//// Function to create DataStore
//fun createDataStore2(context: Context): DataStore<Preferences> {
//    return preferencesDataStore(name = "settings").getValue(context)
//}

