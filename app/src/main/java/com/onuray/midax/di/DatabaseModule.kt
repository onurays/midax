package com.onuray.midax.di

import android.content.Context
import androidx.room.Room
import com.onuray.midax.data.local.AppDb
import com.onuray.midax.data.local.dao.CandleDao
import com.onuray.midax.data.local.dao.NewsDao
import com.onuray.midax.data.local.dao.QuoteDao
import com.onuray.midax.data.local.dao.StockDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDb =
        Room.databaseBuilder(context, AppDb::class.java, "midax.db")
            // .addMigrations(...)
            .build()

    @Provides fun provideStockDao(db: AppDb): StockDao = db.stockDao()
    @Provides fun provideQuoteDao(db: AppDb): QuoteDao = db.quoteDao()
    @Provides fun provideCandleDao(db: AppDb): CandleDao = db.candleDao()
    @Provides fun provideNewsDao(db: AppDb): NewsDao = db.newsDao()
}
