package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.remote.TradingViewScannerApi
import com.example.data.repository.StockRepository
import com.example.notification.AlertNotificationManager
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.AnalyzeStockScreen
import com.example.ui.screens.AveragePriceCalculatorScreen
import com.example.ui.screens.CorporateNewsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NextDayScreenerScreen
import com.example.ui.screens.PerformanceJournalScreen
import com.example.ui.screens.StockDetailScreen
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.NavScreen
import com.example.viewmodel.StockViewModel
import com.example.viewmodel.StockViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val api = TradingViewScannerApi()
        val notificationManager = AlertNotificationManager(this, database.alertDao())
        val repository = StockRepository(
            api = api,
            tradeDao = database.tradeDao(),
            alertDao = database.alertDao(),
            watchlistDao = database.watchlistDao(),
            notificationManager = notificationManager
        )
        val factory = StockViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                // Request Notification Permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { _ -> }

                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                // Force Right-to-Left (RTL) layout direction for Arabic experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    EgxSniperApp(factory = factory)
                }
            }
        }
    }
}

@Composable
fun EgxSniperApp(
    factory: StockViewModelFactory,
    viewModel: StockViewModel = viewModel(factory = factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()
    val allAlerts by viewModel.allAlerts.collectAsStateWithLifecycle()
    val unreadAlertsCount by viewModel.unreadAlertsCount.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    if (uiState.currentScreen != NavScreen.HOME) {
        BackHandler {
            viewModel.navigateTo(NavScreen.HOME)
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = BackgroundDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.currentScreen != NavScreen.STOCK_DETAIL) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = uiState.currentScreen == NavScreen.HOME,
                        onClick = { viewModel.navigateTo(NavScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullishGreen,
                            indicatorColor = BullishGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == NavScreen.NEXT_DAY_SCREENER,
                        onClick = { viewModel.navigateTo(NavScreen.NEXT_DAY_SCREENER) },
                        icon = { Icon(Icons.Default.FilterAlt, contentDescription = "أسهم الغد") },
                        label = { Text("أسهم الغد", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullishGreen,
                            indicatorColor = BullishGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_screener")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == NavScreen.ANALYZE_STOCK,
                        onClick = { viewModel.navigateTo(NavScreen.ANALYZE_STOCK) },
                        icon = { Icon(Icons.Default.QueryStats, contentDescription = "تحليل سهم") },
                        label = { Text("تحليل سهم", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullishGreen,
                            indicatorColor = BullishGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_analyze")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == NavScreen.CORPORATE_NEWS,
                        onClick = { viewModel.navigateTo(NavScreen.CORPORATE_NEWS) },
                        icon = { Icon(Icons.Default.Campaign, contentDescription = "الاكتتابات") },
                        label = { Text("الاكتتابات", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullishGreen,
                            indicatorColor = BullishGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_news")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == NavScreen.ALERTS,
                        onClick = { viewModel.navigateTo(NavScreen.ALERTS) },
                        icon = {
                            if (unreadAlertsCount > 0) {
                                BadgedBox(badge = { Badge { Text("$unreadAlertsCount") } }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "التنبيهات")
                                }
                            } else {
                                Icon(Icons.Default.Notifications, contentDescription = "التنبيهات")
                            }
                        },
                        label = { Text("التنبيهات", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullishGreen,
                            indicatorColor = BullishGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_alerts")
                    )

                    NavigationBarItem(
                        selected = uiState.currentScreen == NavScreen.JOURNAL,
                        onClick = { viewModel.navigateTo(NavScreen.JOURNAL) },
                        icon = { Icon(Icons.Default.History, contentDescription = "سجل الأداء") },
                        label = { Text("سجل الأداء", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = BullishGreen,
                            indicatorColor = BullishGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag("nav_item_journal")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDark)
        ) {
            when (uiState.currentScreen) {
                NavScreen.HOME -> HomeScreen(
                    uiState = uiState,
                    onNavigate = { viewModel.navigateTo(it) },
                    onSelectStock = { viewModel.selectStock(it) },
                    onRecordTrade = { viewModel.recordTrade(it, "الرئيسية") },
                    onCalculateAverage = { viewModel.navigateTo(NavScreen.AVERAGE_CALCULATOR, it) },
                    onToggleAlert = { viewModel.triggerTestAlert() },
                    onRefresh = { viewModel.refreshData() },
                    onTestAlert = { viewModel.triggerTestAlert() },
                    onSelectFilter = { viewModel.setScreenerFilter(it) },
                    onSelectSector = { viewModel.setSelectedSector(it) },
                    onToggleAutoRefresh = { viewModel.toggleAutoRefresh() },
                    onQuickAnalyze = { query ->
                        viewModel.setAnalyzeInput(query)
                        viewModel.searchAndAnalyzeStock(query)
                        viewModel.navigateTo(NavScreen.ANALYZE_STOCK)
                    }
                )

                NavScreen.NEXT_DAY_SCREENER -> NextDayScreenerScreen(
                    uiState = uiState,
                    onSelectFilter = { viewModel.setScreenerFilter(it) },
                    onSelectSector = { viewModel.setSelectedSector(it) },
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onSelectStock = { viewModel.selectStock(it) },
                    onRecordTrade = { viewModel.recordTrade(it, "مرشح الغد") },
                    onCalculateAverage = { viewModel.navigateTo(NavScreen.AVERAGE_CALCULATOR, it) },
                    onToggleAlert = { viewModel.triggerTestAlert() },
                    onNavigateToAnalyze = { query ->
                        viewModel.setAnalyzeInput(query)
                        viewModel.searchAndAnalyzeStock(query)
                        viewModel.navigateTo(NavScreen.ANALYZE_STOCK)
                    }
                )

                NavScreen.ANALYZE_STOCK -> AnalyzeStockScreen(
                    uiState = uiState,
                    onQueryChange = { viewModel.setAnalyzeInput(it) },
                    onSearch = { viewModel.searchAndAnalyzeStock(it) },
                    onClear = { viewModel.clearAnalyzedStock() },
                    onSelectStock = { viewModel.selectStock(it) },
                    onRecordTrade = { viewModel.recordTrade(it, "تحليل فوري") },
                    onCalculateAverage = { viewModel.navigateTo(NavScreen.AVERAGE_CALCULATOR, it) },
                    onToggleAlert = { viewModel.triggerTestAlert() },
                    onBack = { viewModel.navigateTo(NavScreen.HOME) }
                )

                NavScreen.CORPORATE_NEWS -> CorporateNewsScreen(
                    uiState = uiState,
                    onCategorySelect = { viewModel.setCorporateCategory(it) },
                    onSearchChange = { viewModel.setCorporateNewsSearch(it) },
                    onRefresh = { viewModel.loadCorporateNews() },
                    onAnalyzeStock = { symbol ->
                        viewModel.setAnalyzeInput(symbol)
                        viewModel.searchAndAnalyzeStock(symbol)
                        viewModel.navigateTo(NavScreen.ANALYZE_STOCK)
                    },
                    onBack = { viewModel.navigateTo(NavScreen.HOME) }
                )

                NavScreen.AVERAGE_CALCULATOR -> AveragePriceCalculatorScreen(
                    prefilledStock = uiState.selectedStock
                )

                NavScreen.ALERTS -> AlertsScreen(
                    alerts = allAlerts,
                    onTriggerTestAlert = { viewModel.triggerTestAlert() },
                    onMarkAllRead = { viewModel.markAllAlertsRead() },
                    onClearAll = { viewModel.clearAllAlerts() },
                    onMarkRead = { viewModel.markAlertRead(it) }
                )

                NavScreen.JOURNAL -> PerformanceJournalScreen(
                    trades = allTrades,
                    onUpdateStatus = { id, status, profit -> viewModel.updateTradeStatus(id, status, profit) },
                    onDeleteTrade = { viewModel.deleteTrade(it) }
                )

                NavScreen.STOCK_DETAIL -> {
                    uiState.selectedStock?.let { stock ->
                        StockDetailScreen(
                            stock = stock,
                            onBack = { viewModel.navigateTo(NavScreen.HOME) },
                            onCalculateAverage = { viewModel.navigateTo(NavScreen.AVERAGE_CALCULATOR, stock) },
                            onRecordTrade = { viewModel.recordTrade(stock, "تحليل مفصل") }
                        )
                    } ?: run {
                        viewModel.navigateTo(NavScreen.HOME)
                    }
                }
            }
        }
    }
}
