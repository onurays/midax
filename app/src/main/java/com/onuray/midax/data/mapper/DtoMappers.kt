package com.onuray.midax.data.mapper

import com.onuray.midax.data.local.entity.CandleEntity
import com.onuray.midax.data.local.entity.NewsEntity
import com.onuray.midax.data.local.entity.QuoteEntity
import com.onuray.midax.data.local.entity.StockEntity
import com.onuray.midax.data.remote.dto.CandleDto
import com.onuray.midax.data.remote.dto.NewsDto
import com.onuray.midax.data.remote.dto.QuoteDto
import com.onuray.midax.data.remote.dto.StockDto

fun StockDto.toEntity(): StockEntity =
    StockEntity(
        symbol = symbol,
        name = description ?: "symbol",
        currency = currency,
        logoUrl = null,
    )

fun QuoteDto.toEntity(symbol: String): QuoteEntity =
    QuoteEntity(
        symbol = symbol,
        price = current,
        changePct = changePercent,
        updatedAtSec = timestampSec
    )

fun CandleDto.toEntities(symbol: String): List<CandleEntity> {
    val t = timestampsSec ?: return emptyList()
    val c = close ?: List(t.size) { null }
    val o = open ?: List(t.size) { null }
    val h = high ?: List(t.size) { null }
    val l = low ?: List(t.size) { null }
    val v = volume ?: List(t.size) { null }

    val size = listOf(t.size, c.size, o.size, h.size, l.size, v.size).minOrNull() ?: 0
    if (size <= 0) return emptyList()

    return buildList(size) {
        var i = 0
        while (i < size) {
            add(
                CandleEntity(
                    symbol = symbol,
                    t = t[i],
                    open = o[i],
                    high = h[i],
                    low = l[i],
                    close = c[i],
                    volume = v[i]
                )
            )
            i++
        }
    }
}

fun NewsDto.toEntity(symbol: String): NewsEntity =
    NewsEntity(
        id = id ?: (symbol + url).hashCode().toLong(),
        symbol = symbol,
        title = headline,
        source = source,
        url = url,
        summary = summary,
        imageUrl = image,
        publishedAtSec = datetimeSec
    )
