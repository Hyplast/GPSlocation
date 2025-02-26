package fi.infinitygrow.gpslocation.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME
import fi.infinitygrow.gpslocation.data.datastore.createDataStore

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}