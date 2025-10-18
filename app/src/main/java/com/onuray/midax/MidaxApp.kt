package com.onuray.midax

import android.app.Application
import com.onuray.midax.data.sync.ForegroundSync
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MidaxApp : Application() {

    @Inject lateinit var foregroundSync: ForegroundSync

    override fun onCreate() {
        super.onCreate()

        foregroundSync.init()
    }
}
