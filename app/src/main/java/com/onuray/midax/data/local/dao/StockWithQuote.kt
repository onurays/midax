package com.onuray.midax.data.local.dao

data class StockWithQuote(
    val symbol: String,
    val name: String,
    val price: Double?,
    val changePct: Double?,
    val updatedAtSec: Long?
)
