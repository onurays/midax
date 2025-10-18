package com.onuray.midax.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.onuray.midax.data.local.dao.CandleDao
import com.onuray.midax.data.local.dao.NewsDao
import com.onuray.midax.data.local.dao.QuoteDao
import com.onuray.midax.data.local.dao.StockDao
import com.onuray.midax.data.local.entity.CandleEntity
import com.onuray.midax.data.local.entity.NewsEntity
import com.onuray.midax.data.local.entity.QuoteEntity
import com.onuray.midax.data.local.entity.StockEntity

@Database(
    entities = [
        StockEntity::class,
        QuoteEntity::class,
        CandleEntity::class,
        NewsEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDb : RoomDatabase() {
    abstract fun stockDao(): StockDao
    abstract fun quoteDao(): QuoteDao
    abstract fun candleDao(): CandleDao
    abstract fun newsDao(): NewsDao
}
