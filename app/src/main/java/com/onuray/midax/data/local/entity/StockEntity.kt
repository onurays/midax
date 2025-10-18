package com.onuray.midax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks")
data class StockEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val currency: String? = null,
    val logoUrl: String? = null,
)
