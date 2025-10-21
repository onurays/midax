package com.onuray.midax.ui.screen.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onuray.midax.R
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StockListScreen(
    stockListStateFlow: StateFlow<List<UIStockItem>>,
    onItemClick: (String) -> Unit,
) {
    val stockList by stockListStateFlow.collectAsStateWithLifecycle()

    if (stockList.isEmpty()) {
        EmptyLoading()
        return
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        items(stockList.size) { index ->
            StockRow(stockList[index], onItemClick)
            HorizontalDivider()
        }
        item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) }
    }
}

@Composable
private fun StockRow(
    item: UIStockItem,
    onItemClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { onItemClick(item.symbol) }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.symbol, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(item.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(item.price.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            val color = if (item.isRising) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.error
            }
            Text(item.change.toString(), style = MaterialTheme.typography.bodySmall, color = color)
        }
    }
}

@Composable
private fun EmptyLoading() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.stock_list_empty_message))
        }
    }
}
