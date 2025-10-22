package com.onuray.midax.ui.screen.detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onuray.midax.R
import com.onuray.midax.data.local.dao.StockWithQuote
import com.onuray.midax.data.local.entity.CandleEntity
import com.onuray.midax.data.local.entity.NewsEntity
import com.onuray.midax.ui.screen.list.formatPrice
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailScreen(
    symbol: String,
    onBack: () -> Unit,
    onNewsClick: (String) -> Unit,
) {
    val viewModel: StockDetailViewModel = hiltViewModel()
    val stockWithQuote by viewModel.stockWithQuote.collectAsStateWithLifecycle()
    val candles by viewModel.candles.collectAsStateWithLifecycle()
    val news by viewModel.news.collectAsStateWithLifecycle()
    val resolution by viewModel.resolution.collectAsStateWithLifecycle()

    var selectedCandle by remember { mutableStateOf<CandleEntity?>(null) }
    var isChartScrubbing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(symbol) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_content_description)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (stockWithQuote == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = !isChartScrubbing
        ) {
            item {
                StockHeader(
                    stockWithQuote = stockWithQuote!!,
                    selectedCandle = selectedCandle
                )
            }
            item {
                StockChart(
                    candles = candles,
                    resolution = resolution,
                    onResolutionChange = viewModel::setResolution,
                    onCandleSelected = { selectedCandle = it },
                    onScrubStateChanged = { isChartScrubbing = it }
                )
            }
            item {
                Text(
                    text = stringResource(R.string.related_news_header),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (news.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.no_news_found),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            } else {
                items(news) { newsItem ->
                    NewsRow(newsItem = newsItem, onNewsClick = onNewsClick)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun StockHeader(
    stockWithQuote: StockWithQuote,
    selectedCandle: CandleEntity?
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stockWithQuote.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            if (selectedCandle != null) {
                Text(
                    text = formatPrice(selectedCandle.close ?: 0.0),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(Date(selectedCandle.t * 1000)),
                    style = MaterialTheme.typography.headlineSmall
                )
            } else {
                Text(
                    text = formatPrice(stockWithQuote.price ?: 0.0),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                val changePct = stockWithQuote.changePct ?: 0.0
                val color = if (changePct > 0) Color.Green else Color.Red
                Text(
                    text = String.format(Locale.US, "%+.2f%%", changePct),
                    style = MaterialTheme.typography.headlineSmall,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun StockChart(
    candles: List<CandleEntity>,
    resolution: String,
    onResolutionChange: (String) -> Unit,
    onCandleSelected: (CandleEntity?) -> Unit,
    onScrubStateChanged: (Boolean) -> Unit
) {
    if (candles.isEmpty()) return

    Column {
        val min = candles.minOf { it.low ?: 0.0 }
        val max = candles.maxOf { it.high ?: 0.0 }
        var touchX by remember { mutableStateOf<Float?>(null) }

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        onScrubStateChanged(true)
                        touchX = down.position.x

                        do {
                            val event = awaitPointerEvent()
                            touchX = event.changes.first().position.x
                            event.changes.forEach { it.consume() }
                        } while (event.changes.any { it.pressed })

                        onScrubStateChanged(false)
                        touchX = null
                        onCandleSelected(null)
                    }
                }
            }) { 
            val path = Path()
            var selectedIndex: Int? = null
            candles.forEachIndexed { index, candle ->
                val x = size.width / candles.size * index
                val y = size.height - (((candle.close ?: 0.0) - min) / (max - min) * size.height).toFloat()
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                if (touchX != null && touchX!! >= x && touchX!! < (size.width / candles.size * (index + 1))) {
                    selectedIndex = index
                }
            }
            drawPath(
                path = path,
                color = if (candles.last().close ?: 0.0 > candles.first().close ?: 0.0) Color.Green else Color.Red,
                style = Stroke(width = 2.dp.toPx())
            )
            selectedIndex?.let { i ->
                val candle = candles[i]
                val x = size.width / candles.size * i
                val y = size.height - (((candle.close ?: 0.0) - min) / (max - min) * size.height).toFloat()
                drawLine(
                    color = Color.Gray,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
                onCandleSelected(candle)
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val resolutions = listOf("1D" to "D", "1W" to "W", "1M" to "M", "1Y" to "Y", "5Y" to "5Y")
            resolutions.forEach { (label, res) ->
                TextButton(
                    onClick = { onResolutionChange(res) },
                    colors = if (resolution == res) ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.textButtonColors()
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun NewsRow(
    newsItem: NewsEntity,
    onNewsClick: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { newsItem.url?.let(onNewsClick) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = newsItem.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(newsItem.publishedAtSec ?: 0)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
