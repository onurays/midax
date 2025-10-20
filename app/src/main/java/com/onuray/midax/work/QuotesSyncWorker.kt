package com.onuray.midax.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.onuray.midax.data.reposityory.StockRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class QuotesSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val stockRepository: StockRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            stockRepository.seedIfEmpty()
            stockRepository.refreshQuotes(minIntervalMs = 0L)
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }

    companion object {
        const val UNIQUE_NAME = "midax_quotes_sync"
        const val TAG = "quotes_sync"
    }
}
