package fi.infinitygrow.gpslocation.data

//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//


//import androidx.datastore.core.DataStore
//import androidx.datastore.core.DataStoreFactory
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.PreferenceDataStoreFactory
//import androidx.datastore.preferences.core.PreferenceDataStoreFactory
//import org.koin.core.qualifier.Qualifier
//import kotlin.io.path.toPath
//
//internal class DataStoreFactoryImpl(private val preferenceBasePath: String) : DataStoreFactory {
//    companion object {
//        private const val FILE_EXTENSION = ".preferences_pb"
//    }
//
//    override fun makeDataStore(name: Qualifier): DataStore<Preferences> {
//        return PreferenceDataStoreFactory.createWithPath {
//            "$preferenceBasePath${File.separator}${name.value}$FILE_EXTENSION".toPath()
//        }
//    }
//}

//
//object PreferencesFactory {
//
//    fun createDataStore() {}
//
//}

//@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
//expect class PreferencesFactory {
//    fun createDataStore(): DataStore<Preferences>
//
//}