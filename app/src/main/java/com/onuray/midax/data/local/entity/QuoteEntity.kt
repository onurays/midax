package com.onuray.midax.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey val symbol: String,
    val price: Double?,
    val changePct: Double?,
    val updatedAtSec: Long?,
)
