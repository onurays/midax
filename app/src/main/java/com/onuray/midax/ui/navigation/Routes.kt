package com.onuray.midax.ui.navigation

object Routes {

    const val StocksList = "stocks_list"
    const val StockDetail = "stock_detail/{symbol}"
    fun stockDetail(symbol: String) = "stock_detail/$symbol"
}
