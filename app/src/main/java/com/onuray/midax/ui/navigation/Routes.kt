package com.onuray.midax.ui.navigation

object Routes {

    const val StocksList = "stocks_list"
    const val StockDetail = "stock_detail/{symbol}"
    const val NewsDetail = "news_detail/{newsUrl}"

    fun stockDetail(symbol: String) = "stock_detail/$symbol"
    fun newsDetail(newsUrl: String) = "news_detail/$newsUrl"
}
