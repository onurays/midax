package com.onuray.midax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onuray.midax.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    @Query("""
        SELECT * FROM news
        WHERE symbol = :symbol
        ORDER BY publishedAtSec DESC
    """)
    fun observeForSymbol(symbol: String): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<NewsEntity>)
}
