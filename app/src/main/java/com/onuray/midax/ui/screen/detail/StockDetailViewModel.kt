package com.onuray.midax.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onuray.midax.data.local.dao.StockWithQuote
import com.onuray.midax.data.local.entity.CandleEntity
import com.onuray.midax.data.local.entity.NewsEntity
import com.onuray.midax.data.reposityory.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val repository: StockRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val symbol: String = savedStateHandle.get<String>("symbol")!!

    private val _resolution = MutableStateFlow("D")
    val resolution = _resolution.asStateFlow()

    val stockWithQuote: StateFlow<StockWithQuote?> = repository.observeStockWithQuote(symbol)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val candles: StateFlow<List<CandleEntity>> = repository.observeCandles(symbol)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val news: StateFlow<List<NewsEntity>> = repository.observeNews(symbol)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshCandles()
        viewModelScope.launch(Dispatchers.IO) {
            repository.refreshNews(symbol)
        }
    }

    fun setResolution(resolution: String) {
        _resolution.value = resolution
        refreshCandles()
    }

    private fun refreshCandles() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.refreshCandles(symbol, _resolution.value)
        }
    }
}
