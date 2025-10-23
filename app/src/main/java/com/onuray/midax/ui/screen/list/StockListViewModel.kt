package com.onuray.midax.ui.screen.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onuray.midax.data.reposityory.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class UIStockItem(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val isRising: Boolean,
)

@HiltViewModel
class StockListViewModel @Inject constructor(
    stockRepository: StockRepository,
) : ViewModel() {

    val uiState: StateFlow<List<UIStockItem>> =
        stockRepository
            .observeStocksWithQuotes()
            .map { stocksWithQuotes ->
                stocksWithQuotes.map {
                    UIStockItem(
                        symbol = it.symbol,
                        name = it.name,
                        price = it.price ?: 0.0,
                        change = it.changePct ?: 0.0,
                        isRising = (it.changePct ?: 0.0) > 0.0,
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


}