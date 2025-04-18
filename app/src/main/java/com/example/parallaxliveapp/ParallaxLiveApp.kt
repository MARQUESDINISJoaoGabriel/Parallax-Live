package com.example.parallaxliveapp

import android.app.Application
import com.example.parallaxliveapp.presentation.di.appModule
import com.example.parallaxliveapp.presentation.di.dataModule
import com.example.parallaxliveapp.presentation.di.domainModule
import com.example.parallaxliveapp.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class for ParallaxLive app.
 * Sets up Koin for dependency injection and initializes Firebase.
 */
class ParallaxLiveApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin for dependency injection
        startKoin {
            // Log Koin into Android logger
            androidLogger(Level.ERROR) // Use ERROR level to avoid Koin's logs flooding

            // Reference Android context
            androidContext(this@ParallaxLiveApp)

            // Load modules
            modules(
                listOf(
                    appModule,
                    dataModule,
                    domainModule,
                    presentationModule
                )
            )
        }
    }
}