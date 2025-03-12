package fi.infinitygrow.gpslocation.presentation.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import fi.infinitygrow.gpslocation.data.createDataStore
import fi.infinitygrow.gpslocation.data.datastore.DATA_STORE_FILE_NAME
import fi.infinitygrow.gpslocation.presentation.observation_list.WeatherViewModel
import fi.infinitygrow.gpslocation.presentation.permission.LocationService
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import fi.infinitygrow.gpslocation.data.datastore.dataStore
import fi.infinitygrow.gpslocation.data.database.DatabaseFactory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
private val viewModelModule = module {
//    single<DataStore<Preferences>> { createDataStore(
//        function = get()
//    ) }
    single { dataStore()}
        //single<DataStore<Preferences>> { createDataStore {  }
        //createDataStore {  }
//        createDataStore {
//            val directory = NSFileManager.defaultManager.URLForDirectory(
//                directory = NSDocumentDirectory,
//                inDomain = NSUserDomainMask,
//                appropriateForURL = null,
//                create = false,
//                error = null
//            )
//            requireNotNull(directory).path + "/$DATA_STORE_FILE_NAME"
//        }
    
    single<HttpClientEngine> { Darwin.create() }
    //single { WeatherViewModel(get(),get(),get(), get(), get()) }
    single { LocationService() }
    single { DatabaseFactory() }
}

//fun <T> single(definition: T) {
//
//}

actual fun sharedViewModelModule(): Module = viewModelModule

object ProvideViewModel: KoinComponent{

    fun getWeatherViewModel() : WeatherViewModel = get()

}