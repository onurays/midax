package com.onuray.midax.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CandleDto(
    @Json(name = "t") val timestampsSec: List<Long>?,
    @Json(name = "c") val close: List<Double>?,
    @Json(name = "o") val open: List<Double>?,
    @Json(name = "h") val high: List<Double>?,
    @Json(name = "l") val low: List<Double>?,
    @Json(name = "v") val volume: List<Long>?,
    @Json(name = "s") val status: String?,
)
