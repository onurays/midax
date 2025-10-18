package com.onuray.midax.data.remote.dto

import com.squareup.moshi.Json

data class QuoteDto(
    @Json(name = "c") val current: Double?,
    @Json(name = "d") val change: Double?,
    @Json(name = "dp") val changePercent: Double?,
    @Json(name = "h") val high: Double?,
    @Json(name = "l") val low: Double?,
    @Json(name = "o") val open: Double?,
    @Json(name = "pc") val prevClose: Double?,
    @Json(name = "t") val timestampSec: Long?,
)
