package com.onuray.midax.data.remote.api

import com.onuray.midax.data.remote.dto.CandleDto
import com.onuray.midax.data.remote.dto.NewsDto
import com.onuray.midax.data.remote.dto.QuoteDto
import com.onuray.midax.data.remote.dto.StockDto
import retrofit2.http.GET
import retrofit2.http.Query

interface StocksApi {

    /**
     * Returns a list of symbols.
     * @param exchange is to filter the exchange. Default value is US (Nasdaq).
     */
    @GET("stock/symbol")
    suspend fun getSymbols(
        @Query("exchange") exchange: String = "US",
    ): List<StockDto>

    /**
     * Returns real time quote for the given symbol.
     * @param symbol is the stock symbol.
     */
    @GET("quote")
    suspend fun quote(
        @Query("symbol") symbol: String,
    ): QuoteDto

    @GET("stock/candle")
    suspend fun candles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") fromUnixSec: Long,
        @Query("to") toUnixSec: Long,
    ): CandleDto

    /**
     * Returns news for the given symbol.
     * @param symbol is the stock symbol.
     * @param fromDate is the start date in format of YYYY-MM-DD.
     * @param toDate is the end date in format of YYYY-MM-DD.
     */
    @GET("company-news")
    suspend fun companyNews(
        @Query("symbol") symbol: String,
        @Query("from") fromDate: String,
        @Query("to") toDate: String,
    ): List<NewsDto>
}
