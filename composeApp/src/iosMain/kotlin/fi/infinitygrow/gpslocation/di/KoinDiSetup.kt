package fi.infinitygrow.gpslocation.di

import fi.infinitygrow.gpslocation.domain.di.domainModule
import fi.infinitygrow.gpslocation.presentation.di.sharedViewModelModule
import fi.infinitygrow.gpslocation.data.di.dataModule
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(dataModule + domainModule + sharedViewModelModule())
    }
}