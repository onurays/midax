package com.onuray.midax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.onuray.midax.data.local.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

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
        WHERE q.price > 0
        ORDER BY s.symbol
    """)
    fun observeWithQuotes(): Flow<List<StockWithQuote>>

    @Query("""
        SELECT s.symbol AS symbol,
               s.name   AS name,
               q.price  AS price,
               q.changePct AS changePct,
               q.updatedAtSec AS updatedAtSec
        FROM stocks s
        LEFT JOIN quotes q ON s.symbol = q.symbol
        WHERE s.symbol = :symbol
    """)
    fun observeWithQuote(symbol: String): Flow<StockWithQuote?>

    @Query("SELECT * FROM quotes WHERE symbol = :symbol")
    suspend fun getQuote(symbol: String): QuoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<QuoteEntity>)
}
