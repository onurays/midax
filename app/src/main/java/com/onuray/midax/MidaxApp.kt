package com.onuray.midax

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.onuray.midax.data.sync.ForegroundSync
import com.onuray.midax.work.QuotesSyncWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MidaxApp : Application(), Configuration.Provider {

    @Inject lateinit var foregroundSync: ForegroundSync
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        foregroundSync.init()

        val workManager = WorkManager.getInstance(this)

        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<QuotesSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .addTag(QuotesSyncWorker.TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            QuotesSyncWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}