package com.onuray.midax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onuray.midax.data.local.entity.StockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM stocks ORDER BY symbol")
    fun observeAll(): Flow<List<StockEntity>>

    @Query("SELECT symbol FROM stocks")
    suspend fun getSymbols(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<StockEntity>)
}
