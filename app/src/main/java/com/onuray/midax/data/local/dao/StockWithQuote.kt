package com.onuray.midax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onuray.midax.data.local.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

data class StockWithQuote(
    val symbol: String,
    val name: String,
    val price: Double?,
    val changePct: Double?,
    val updatedAtSec: Long?
)

@Dao
interface QuoteDao {

    @Query("""
        SELECT s.symbol AS symbol,
               s.name   AS name,
               q.price  AS price,
               q.changePct AS changePct,
               q.updatedAtSec AS updatedAtSec
        FROM stocks s
        LEFT JOIN quotes q ON s.symbol = q.symbol
        ORDER BY s.symbol
    """)
    fun observeWithQuotes(): Flow<List<StockWithQuote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<QuoteEntity>)
}
