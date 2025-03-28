package fi.infinitygrow.gpslocation

import android.app.Application
import fi.infinitygrow.gpslocation.data.di.dataModule
import fi.infinitygrow.gpslocation.domain.di.domainModule
import fi.infinitygrow.gpslocation.presentation.di.sharedViewModelModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)
            modules(dataModule + domainModule + sharedViewModelModule())
        }

        // Initialize logging (using Napier as an example)
        Napier.base(DebugAntilog())
    }

}

