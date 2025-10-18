package com.onuray.midax.data.sync

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.onuray.midax.data.reposityory.StockRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Singleton
class ForegroundSync @Inject constructor(
    private val repo: StockRepository
) {
    private val periodMs = 10_000L

    init {
        val owner = ProcessLifecycleOwner.get()

        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                runCatching { repo.seedIfEmpty() }

                while (isActive) {
                    val ok = runCatching { repo.refreshQuotes() }.isSuccess
                    delay(if (ok) periodMs else (periodMs * 2).coerceAtMost(5 * periodMs))
                }
            }
        }
    }
}
