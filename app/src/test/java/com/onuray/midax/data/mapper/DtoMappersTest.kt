package com.onuray.midax.data.mapper

import com.onuray.midax.data.remote.dto.CandleDto
import com.onuray.midax.data.remote.dto.NewsDto
import com.onuray.midax.data.remote.dto.QuoteDto
import com.onuray.midax.data.remote.dto.StockDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DtoMappersTest {

    @Test
    fun `StockDto_toEntity() should map correctly`() {
        // Given
        val stockDto = StockDto(
            symbol = "AAPL",
            description = "Apple Inc.",
            currency = "USD"
        )

        // When
        val stockEntity = stockDto.toEntity()

        // Then
        assertEquals("AAPL", stockEntity?.symbol)
        assertEquals("Apple Inc.", stockEntity?.name)
        assertEquals("USD", stockEntity?.currency)
        assertNull(stockEntity?.logoUrl)
    }

    @Test
    fun `StockDto_toEntity() with null symbol should return null`() {
        // Given
        val stockDto = StockDto(
            symbol = null,
            description = "Apple Inc.",
            currency = "USD"
        )

        // When
        val stockEntity = stockDto.toEntity()

        // Then
        assertNull(stockEntity)
    }

    @Test
    fun `QuoteDto_toEntity() should map correctly`() {
        // Given
        val quoteDto = QuoteDto(
            current = 150.0,
            change = 2.0,
            changePercent = 1.5,
            high = 152.0,
            low = 148.0,
            open = 149.0,
            prevClose = 148.0,
            timestampSec = 1672531200
        )
        val symbol = "AAPL"

        // When
        val quoteEntity = quoteDto.toEntity(symbol)

        // Then
        assertEquals(symbol, quoteEntity.symbol)
        assertEquals(150.0, quoteEntity.price!!, 0.0)
        assertEquals(1.5, quoteEntity.changePct!!, 0.0)
        assertEquals(1672531200L, quoteEntity.updatedAtSec)
    }

    @Test
    fun `CandleDto_toEntities() should map correctly`() {
        // Given
        val candleDto = CandleDto(
            timestampsSec = listOf(1672531200, 1672534800),
            close = listOf(150.0, 151.0),
            open = listOf(149.0, 150.5),
            high = listOf(151.0, 151.5),
            low = listOf(148.5, 150.0),
            volume = listOf(1000000L, 1200000L),
            status = "ok"
        )
        val symbol = "AAPL"

        // When
        val candleEntities = candleDto.toEntities(symbol)

        // Then
        assertEquals(2, candleEntities.size)
        assertEquals(symbol, candleEntities[0].symbol)
        assertEquals(1672531200L, candleEntities[0].t)
        assertEquals(150.0, candleEntities[0].close!!, 0.0)
        assertEquals(149.0, candleEntities[0].open!!, 0.0)
        assertEquals(151.0, candleEntities[0].high!!, 0.0)
        assertEquals(148.5, candleEntities[0].low!!, 0.0)
        assertEquals(1000000L, candleEntities[0].volume)
    }

    @Test
    fun `CandleDto_toEntities() with empty lists should return empty list`() {
        // Given
        val candleDto = CandleDto(
            timestampsSec = emptyList(),
            close = emptyList(),
            open = emptyList(),
            high = emptyList(),
            low = emptyList(),
            volume = emptyList(),
            status = "no_data"
        )
        val symbol = "AAPL"

        // When
        val candleEntities = candleDto.toEntities(symbol)

        // Then
        assertEquals(0, candleEntities.size)
    }

    @Test
    fun `NewsDto_toEntity() should map correctly`() {
        // Given
        val newsDto = NewsDto(
            id = 123,
            headline = "News Headline",
            source = "News Source",
            url = "http://example.com",
            summary = "News Summary",
            image = "http://example.com/image.png",
            datetimeSec = 1672531200,
            category = "business"
        )
        val symbol = "AAPL"

        // When
        val newsEntity = newsDto.toEntity(symbol)

        // Then
        assertEquals(123L, newsEntity.id)
        assertEquals(symbol, newsEntity.symbol)
        assertEquals("News Headline", newsEntity.title)
        assertEquals("News Source", newsEntity.source)
        assertEquals("http://example.com", newsEntity.url)
        assertEquals("News Summary", newsEntity.summary)
        assertEquals("http://example.com/image.png", newsEntity.imageUrl)
        assertEquals(1672531200L, newsEntity.publishedAtSec)
    }

    @Test
    fun `NewsDto_toEntity() with null id should generate id from symbol and url`() {
        // Given
        val newsDto = NewsDto(
            id = null,
            headline = "News Headline",
            source = "News Source",
            url = "http://example.com",
            summary = "News Summary",
            image = "http://example.com/image.png",
            datetimeSec = 1672531200,
            category = "business"
        )
        val symbol = "AAPL"

        // When
        val newsEntity = newsDto.toEntity(symbol)

        // Then
        assertEquals((symbol + "http://example.com").hashCode().toLong(), newsEntity.id)
    }
}
