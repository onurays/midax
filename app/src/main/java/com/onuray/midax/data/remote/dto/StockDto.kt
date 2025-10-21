package com.onuray.midax.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StockDto(
    @Json(name = "symbol") val symbol: String?,
    @Json(name = "displaySymbol") val displaySymbol: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "mic") val mic: String? = null,
    @Json(name = "figi") val figi: String? = null,
)
