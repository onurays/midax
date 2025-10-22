package com.onuray.midax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onuray.midax.data.local.entity.CandleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CandleDao {

    @Query("SELECT * FROM candles WHERE symbol = :symbol ORDER BY t")
    fun observeAll(symbol: String): Flow<List<CandleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CandleEntity>)

    @Query("DELETE FROM candles WHERE symbol = :symbol")
    suspend fun deleteAll(symbol: String)
}
