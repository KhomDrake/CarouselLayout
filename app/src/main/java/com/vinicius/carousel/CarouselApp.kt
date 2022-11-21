package com.vinicius.carousel

import android.app.Application
import com.vinicius.carousel.collapse.CharacterViewModel
import com.vinicius.carousel.normal.NormalViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CarouselApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@CarouselApp)
            modules(
                module {
                    viewModel { NormalViewModel() }
                    viewModel { CharacterViewModel() }
                }
            )
        }
    }

}