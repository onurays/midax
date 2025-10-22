package com.onuray.midax.data.reposityory

import androidx.room.withTransaction
import com.onuray.midax.data.local.AppDb
import com.onuray.midax.data.local.dao.CandleDao
import com.onuray.midax.data.local.dao.NewsDao
import com.onuray.midax.data.local.dao.StockDao
import com.onuray.midax.data.local.dao.QuoteDao
import com.onuray.midax.data.local.dao.StockWithQuote
import com.onuray.midax.data.local.entity.CandleEntity
import com.onuray.midax.data.local.entity.NewsEntity
import com.onuray.midax.data.mapper.toEntity
import com.onuray.midax.data.remote.api.StocksApi
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class StockRepository @Inject constructor(
    private val api: StocksApi,
    private val db: AppDb,
    private val stockDao: StockDao,
    private val quoteDao: QuoteDao,
    private val candleDao: CandleDao,
    private val newsDao: NewsDao,
) {

    private var lastQuotesRefreshMs: Long = 0L

    fun observeStocksWithQuotes(): Flow<List<StockWithQuote>> =
        quoteDao.observeWithQuotes()

    fun observeStockWithQuote(symbol: String): Flow<StockWithQuote?> =
        quoteDao.observeWithQuote(symbol)

    fun observeCandles(symbol: String): Flow<List<CandleEntity>> =
        candleDao.observeAll(symbol)

    fun observeNews(symbol: String): Flow<List<NewsEntity>> =
        newsDao.observeAll(symbol)

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

    suspend fun refreshCandles(symbol: String, resolution: String) {
        val quote = quoteDao.getQuote(symbol)
        if (quote == null || quote.price == null || quote.changePct == null) return

        val candles = generateFakeCandles(symbol, resolution, quote.price, quote.changePct)
        db.withTransaction {
            candleDao.deleteAll(symbol)
            candleDao.upsertAll(candles)
        }
    }

    suspend fun refreshNews(symbol: String) {
        val calendar = Calendar.getInstance()
        val toDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
        calendar.add(Calendar.MONTH, -1)
        val fromDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

        val news = api.companyNews(symbol, fromDate, toDate).map { it.toEntity(symbol) }
        db.withTransaction {
            newsDao.deleteAll(symbol)
            newsDao.upsertAll(news)
        }
    }

    private fun generateFakeCandles(
        symbol: String,
        resolution: String,
        price: Double,
        changePct: Double
    ): List<CandleEntity> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        val (count, timeUnit) = when (resolution) {
            "D" -> 30 to Calendar.DAY_OF_YEAR
            "W" -> 52 to Calendar.WEEK_OF_YEAR
            "M" -> 12 to Calendar.MONTH
            else -> 30 to Calendar.DAY_OF_YEAR
        }

        val startingPrice = price / (1 + changePct / 100)
        var currentPrice = startingPrice

        return (0 until count).map { i ->
            calendar.timeInMillis = now
            calendar.add(timeUnit, -(count - 1 - i))
            val t = calendar.timeInMillis / 1000

            val open = currentPrice * (1 + Random.nextDouble(-0.02, 0.02))
            val close = currentPrice * (1 + Random.nextDouble(-0.02, 0.02))
            val high = maxOf(open, close) * (1 + Random.nextDouble(0.0, 0.01))
            val low = minOf(open, close) * (1 - Random.nextDouble(0.0, 0.01))
            val volume = Random.nextLong(100_000, 10_000_000)

            currentPrice *= (1 + (changePct / count / 100) + Random.nextDouble(-0.01, 0.01))

            CandleEntity(
                symbol = symbol,
                t = t,
                open = open,
                high = high,
                low = low,
                close = close,
                volume = volume,
            )
        }
    }
}
