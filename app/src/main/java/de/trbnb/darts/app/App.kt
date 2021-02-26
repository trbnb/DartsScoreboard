package de.trbnb.darts.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import de.trbnb.mvvmbase.MvvmBase

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        MvvmBase.autoInit()
            .disableViewModelLifecycleThreadConstraints()
    }
}