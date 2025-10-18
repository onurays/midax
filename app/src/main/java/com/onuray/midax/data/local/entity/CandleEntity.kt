package com.onuray.midax.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "candles",
    indices = [Index(value = ["symbol", "t"], unique = true)]
)
data class CandleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val t: Long,
    val open: Double?,
    val high: Double?,
    val low: Double?,
    val close: Double?,
    val volume: Long?
)
