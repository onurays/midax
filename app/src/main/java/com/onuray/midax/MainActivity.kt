package com.onuray.midax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.onuray.midax.ui.navigation.Routes
import com.onuray.midax.ui.screen.detail.NewsDetailScreen
import com.onuray.midax.ui.screen.detail.StockDetailScreen
import com.onuray.midax.ui.screen.list.StockListScreen
import com.onuray.midax.ui.screen.list.StockListViewModel
import com.onuray.midax.ui.theme.MidaxTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Midax()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Midax() {
    val nav = rememberNavController()

    MidaxTheme {
        NavHost(
            navController = nav,
            startDestination = Routes.StocksList,
        ) {
            composable(Routes.StocksList) {
                val viewModel: StockListViewModel = hiltViewModel()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.main_top_bar_title)) }
                        )
                    }
                ) {
                    StockListScreen(
                        modifier = Modifier.padding(it),
                        stockListStateFlow = viewModel.uiState,
                        onItemClick = { symbol ->
                            nav.navigate(Routes.stockDetail(symbol))
                        },
                    )
                }
            }
            composable(
                route = Routes.StockDetail,
                arguments = listOf(navArgument("symbol") { type = NavType.StringType }),
            ) { backStackEntry ->
                val symbol = backStackEntry.arguments?.getString("symbol") ?: ""
                StockDetailScreen(
                    symbol = symbol,
                    onBack = { nav.popBackStack() },
                    onNewsClick = { newsUrl ->
                        val encodedUrl = URLEncoder.encode(newsUrl, StandardCharsets.UTF_8.toString())
                        nav.navigate(Routes.newsDetail(encodedUrl))
                    }
                )
            }
            composable(
                route = Routes.NewsDetail,
                arguments = listOf(navArgument("newsUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val newsUrl = backStackEntry.arguments?.getString("newsUrl") ?: ""
                NewsDetailScreen(newsUrl = newsUrl)
            }
        }
    }
}
