package com.onuray.midax.data.remote.dto

import com.squareup.moshi.Json

data class NewsDto(
    @Json(name = "id") val id: Long?,
    @Json(name = "headline") val headline: String?,
    @Json(name = "source") val source: String?,
    @Json(name = "datetime") val datetimeSec: Long?,
    @Json(name = "url") val url: String?,
    @Json(name = "summary") val summary: String?,
    @Json(name = "image") val image: String?,
    @Json(name = "category") val category: String?,
)
