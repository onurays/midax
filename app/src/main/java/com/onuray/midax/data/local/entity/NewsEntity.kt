package com.onuray.midax.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "news",
    indices = [Index(value = ["symbol", "publishedAtSec"])]
)
data class NewsEntity(
    @PrimaryKey val id: Long,
    val symbol: String,
    val title: String?,
    val source: String?,
    val url: String?,
    val summary: String?,
    val imageUrl: String?,
    val publishedAtSec: Long?,
)
