package com.onuray.midax.data.reposityory

import androidx.room.withTransaction
import com.onuray.midax.data.local.AppDb
import com.onuray.midax.data.local.dao.StockDao
import com.onuray.midax.data.local.dao.QuoteDao
import com.onuray.midax.data.local.dao.StockWithQuote
import com.onuray.midax.data.mapper.toEntity
import com.onuray.midax.data.remote.api.StocksApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val api: StocksApi,
    private val db: AppDb,
    private val stockDao: StockDao,
    private val quoteDao: QuoteDao,
) {

    private var lastQuotesRefreshMs: Long = 0L

    fun observeStocksWithQuotes(): Flow<List<StockWithQuote>> =
        quoteDao.observeWithQuotes()

    suspend fun seedIfEmpty(limit: Int = 50) {
        val existing = stockDao.getSymbols()
        if (existing.isNotEmpty()) return

        val dtos = api.getSymbols(exchange = "US")
        val items = dtos.take(limit).mapNotNull { it.toEntity() }

        db.withTransaction {
            stockDao.upsertAll(items)
        }
    }

    suspend fun refreshQuotes(minIntervalMs: Long = 30_000) {
        val now = System.currentTimeMillis()
        if (now - lastQuotesRefreshMs < minIntervalMs) return

        val symbols = stockDao.getSymbols()
        if (symbols.isEmpty()) return

        val quoteEntities = symbols.mapNotNull { s ->
            runCatching {
                api.quote(s).toEntity(s)
            }.getOrNull()
        }

        db.withTransaction {
            quoteDao.upsertAll(quoteEntities)
        }

        lastQuotesRefreshMs = now
    }
}
