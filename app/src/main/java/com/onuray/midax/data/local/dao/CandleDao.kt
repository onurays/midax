package com.onuray.midax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onuray.midax.data.local.entity.CandleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CandleDao {
    @Query("""
        SELECT * FROM candles
        WHERE symbol = :symbol AND t BETWEEN :fromSec AND :toSec
        ORDER BY t
    """)
    fun observeRange(symbol: String, fromSec: Long, toSec: Long): Flow<List<CandleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CandleEntity>)

    @Query("DELETE FROM candles WHERE symbol = :symbol")
    suspend fun deleteForSymbol(symbol: String)
}
