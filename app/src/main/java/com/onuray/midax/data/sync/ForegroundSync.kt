package com.onuray.midax.data.sync

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.onuray.midax.data.reposityory.StockRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class ForegroundSync @Inject constructor(
    private val repo: StockRepository
) {
    private val periodMs = 10_000L

    fun init() {
        val owner = ProcessLifecycleOwner.get()

        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    runCatching { repo.seedIfEmpty() }
                        .onFailure { Log.e("ForegroundSync", "Failed to seed database", it) }
                }

                while (isActive) {
                    val ok = runCatching { repo.refreshQuotes() }.isSuccess
                    delay(if (ok) periodMs else (periodMs * 2).coerceAtMost(5 * periodMs))
                }
            }
        }
    }
}