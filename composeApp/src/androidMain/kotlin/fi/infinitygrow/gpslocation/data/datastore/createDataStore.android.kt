package fi.infinitygrow.gpslocation.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

// in src/androidMain/kotlin
fun dataStore(context: Context): DataStore<Preferences> =
    createDataStore(
        producePath = { context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath }
    )


