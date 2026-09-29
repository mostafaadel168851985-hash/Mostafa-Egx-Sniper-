package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AlertEntity
import com.example.data.local.TradeEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.CorporateCategory
import com.example.data.model.CorporateNews
import com.example.data.model.MarketIndexStatus
import com.example.data.model.StockData
import com.example.data.repository.StockRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class NavScreen {
    HOME,
    NEXT_DAY_SCREENER,
    ANALYZE_STOCK,
    CORPORATE_NEWS,
    AVERAGE_CALCULATOR,
    ALERTS,
    JOURNAL,
    STOCK_DETAIL,
    PORTFOLIO_ALLOCATION
}

enum class ScreenerFilter(val arabicTitle: String) {
    TOMORROW_PICKS("🎯 مرشحات الغد"),
    SHARIAH("☪️ أسهم الشريعة"),
    CORRECTIONS("🔻 صائد التصحيحات"),
    BREAKOUTS("⚡ قناص الاختراق"),
    SUPPORT_BOUNCE("🛡️ دعم وارتداد"),
    EARLY_UPTREND("🚀 بداية صعود (≥20%)"),
    ALL("🌍 كل الأسهم")
}

data class StockUiState(
    val isLoading: Boolean = false,
    val marketStatus: MarketIndexStatus = MarketIndexStatus(),
    val selectedMarketIndexId: String = "EGX30",
    val allStocks: List<StockData> = emptyList(),
    val selectedSector: String = "الكل 🌍",
    val searchQuery: String = "",
    val activeScreenerFilter: ScreenerFilter = ScreenerFilter.TOMORROW_PICKS,
    val currentScreen: NavScreen = NavScreen.HOME,
    val selectedStock: StockData? = null,
    val userMessage: String? = null,
    val lastUpdatedTime: String = "",
    val isAutoRefreshActive: Boolean = true,
    // Portfolio Capital Management:
    val portfolioCapital: Double = 50000.0,
    val riskPerTradePct: Double = 2.0,
    val maxSingleStockAllocPct: Double = 20.0,
    // Corporate Actions & News State:
    val corporateNews: List<CorporateNews> = emptyList(),
    val selectedCorporateCategory: CorporateCategory = CorporateCategory.ALL,
    val corporateNewsSearchQuery: String = "",
    val isLoadingNews: Boolean = false,
    // Dedicated analyze screen state:
    val analyzeInputSymbol: String = "",
    val analyzedStock: StockData? = null,
    val isSearchingStock: Boolean = false,
    val analyzeErrorMessage: String? = null
) {
    val defaultDealBudget: Double
        get() = portfolioCapital * (maxSingleStockAllocPct / 100.0)

    val maxRiskPerTradeEgp: Double
        get() = portfolioCapital * (riskPerTradePct / 100.0)
    val filteredCorporateNews: List<CorporateNews>
        get() {
            var list = corporateNews
            if (selectedCorporateCategory != CorporateCategory.ALL) {
                list = list.filter { it.category == selectedCorporateCategory }
            }
            if (corporateNewsSearchQuery.isNotBlank()) {
                val q = corporateNewsSearchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(q) ||
                    it.companyName.lowercase().contains(q) ||
                    it.symbol.lowercase().contains(q) ||
                    it.summary.lowercase().contains(q)
                }
            }
            return list
        }

    val filteredStocks: List<StockData>
        get() {
            var list = allStocks

            // Filter by search query
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().uppercase()
                list = list.filter {
                    it.symbol.contains(q) || it.name.contains(q) || it.description.contains(q)
                }
            }

            // Filter by sector
            if (selectedSector != "الكل 🌍") {
                list = list.filter { it.sector == selectedSector }
            }

            // Filter by screener strategy
            return when (activeScreenerFilter) {
                ScreenerFilter.TOMORROW_PICKS -> list.filter { it.isTomorrowPick }
                    .sortedByDescending { it.smartScore }
                ScreenerFilter.SHARIAH -> list.filter { it.isShariahCompliant }
                    .sortedByDescending { it.smartScore }
                ScreenerFilter.CORRECTIONS -> list.filter { it.isCorrectionHunter }
                    .sortedByDescending { it.smartScore }
                ScreenerFilter.BREAKOUTS -> list.filter { it.isRapidBreakout }
                    .sortedByDescending { it.breakoutQuality.score }
                ScreenerFilter.SUPPORT_BOUNCE -> list.filter { it.isSupportBounce }
                    .sortedByDescending { it.smartScore }
                ScreenerFilter.EARLY_UPTREND -> list.filter { it.isEarlyUptrend }
                    .sortedByDescending { it.upsideTo52wHigh }
                ScreenerFilter.ALL -> list.sortedByDescending { it.change }
            }
        }
}

class StockViewModel(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockUiState(isLoading = true))
    val uiState: StateFlow<StockUiState> = _uiState.asStateFlow()

    val allTrades: StateFlow<List<TradeEntity>> = repository.allTrades
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlerts: StateFlow<List<AlertEntity>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAlertsCount: StateFlow<Int> = repository.unreadAlerts
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val watchlist: StateFlow<List<WatchlistEntity>> = repository.watchlist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var autoRefreshJob: kotlinx.coroutines.Job? = null

    init {
        refreshData()
        loadCorporateNews()
        startAutoRefreshLoop()
    }

    fun setCorporateCategory(category: CorporateCategory) {
        _uiState.update { it.copy(selectedCorporateCategory = category) }
    }

    fun setCorporateNewsSearch(query: String) {
        _uiState.update { it.copy(corporateNewsSearchQuery = query) }
    }

    fun loadCorporateNews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingNews = true) }
            try {
                val news = repository.getCorporateNews()
                _uiState.update { it.copy(isLoadingNews = false, corporateNews = news) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoadingNews = false) }
            }
        }
    }

    private fun startAutoRefreshLoop() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                // Wait 15 minutes between automatic background updates
                delay(15 * 60 * 1000L)
                if (_uiState.value.isAutoRefreshActive) {
                    refreshData(isAuto = true)
                }
            }
        }
    }

    fun toggleAutoRefresh() {
        _uiState.update { current ->
            val newState = !current.isAutoRefreshActive
            current.copy(
                isAutoRefreshActive = newState,
                userMessage = if (newState) "تم تفعيل التحديث التلقائي كل 15 دقيقة 🟢" else "تم إيقاف التحديث التلقائي مؤقتاً ⏸️"
            )
        }
    }

    fun refreshData(isAuto: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val marketStatus = repository.getMarketStatus()
                val stocks = repository.getAllStocks(marketStatus.marketMultiplier)
                val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale("ar"))
                val currentTimeStr = timeFormat.format(java.util.Date())

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        marketStatus = marketStatus,
                        allStocks = stocks,
                        lastUpdatedTime = currentTimeStr,
                        userMessage = if (isAuto) "🔄 تحديث تلقائي دوري: تم تحديث البيانات بنجاح (${stocks.size} سهم)" else "تم تحديث البيانات بنجاح (${stocks.size} سهم)"
                    )
                }
                autoAuditAllTradesAgainstLivePrices(silent = true)
            } catch (e: Exception) {
                val timeFormat = java.text.SimpleDateFormat("hh:mm a", java.util.Locale("ar"))
                val currentTimeStr = timeFormat.format(java.util.Date())
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lastUpdatedTime = currentTimeStr,
                        userMessage = "تم استخدام البيانات المحفوظة محلياً"
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedSector(sector: String) {
        _uiState.update { it.copy(selectedSector = sector) }
    }

    fun setScreenerFilter(filter: ScreenerFilter) {
        _uiState.update { it.copy(activeScreenerFilter = filter) }
    }

    fun selectMarketIndex(indexId: String) {
        _uiState.update { it.copy(selectedMarketIndexId = indexId) }
    }

    fun setAnalyzeInput(query: String) {
        _uiState.update { it.copy(analyzeInputSymbol = query, analyzeErrorMessage = null) }
    }

    fun searchAndAnalyzeStock(customQuery: String? = null) {
        val query = (customQuery ?: _uiState.value.analyzeInputSymbol).trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(analyzeErrorMessage = "يرجى كتابة رمز السهم أو اسمه أولاً (مثال: TMGH أو طلعت مصطفى)") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearchingStock = true,
                    analyzeErrorMessage = null,
                    analyzeInputSymbol = query
                )
            }
            try {
                val stock = repository.searchAndAnalyzeStock(
                    query = query,
                    currentStocks = _uiState.value.allStocks,
                    marketMultiplier = _uiState.value.marketStatus.marketMultiplier
                )
                if (stock != null) {
                    _uiState.update {
                        it.copy(
                            isSearchingStock = false,
                            analyzedStock = stock,
                            analyzeErrorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSearchingStock = false,
                            analyzedStock = null,
                            analyzeErrorMessage = "لم يتم العثور على سهم يطابق \"$query\". تأكد من كتابة الرمز الإنجليزي (مثل COMI, TMGH, FWRY, SKPC) أو جزء من الاسم العربي."
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSearchingStock = false,
                        analyzeErrorMessage = "حدث خطأ أثناء فحص السهم، يرجى المحاولة مرة أخرى."
                    )
                }
            }
        }
    }

    fun clearAnalyzedStock() {
        _uiState.update { it.copy(analyzedStock = null, analyzeInputSymbol = "", analyzeErrorMessage = null) }
    }

    fun navigateTo(screen: NavScreen, stock: StockData? = null) {
        _uiState.update {
            it.copy(
                currentScreen = screen,
                selectedStock = stock ?: it.selectedStock
            )
        }
    }

    fun selectStock(stock: StockData) {
        _uiState.update {
            it.copy(
                selectedStock = stock,
                currentScreen = NavScreen.STOCK_DETAIL
            )
        }
    }

    fun recordTrade(stock: StockData, tradeType: String = "مرشح الغد") {
        viewModelScope.launch {
            val trade = TradeEntity(
                symbol = stock.symbol,
                name = stock.name,
                entryPrice = stock.entryPrice,
                target = stock.target1,
                stopLoss = stock.stopLoss,
                targetPct = stock.targetPct,
                riskPct = stock.riskPct,
                rr = stock.riskRewardRatio,
                tradeType = tradeType,
                dateRecorded = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                status = "pending",
                smartScore = stock.smartScore
            )
            repository.recordTrade(trade)
            _uiState.update { it.copy(userMessage = "تم تسجيل الصفقة بنجاح في سجل الأداء: ${stock.symbol}") }
        }
    }

    fun updateTradeStatus(id: Long, status: String, profitPct: Double?) {
        viewModelScope.launch {
            repository.updateTradeStatus(id, status, profitPct)
            _uiState.update { it.copy(userMessage = "تم تحديث حالة الصفقة") }
        }
    }

    fun deleteTrade(id: Long) {
        viewModelScope.launch {
            repository.deleteTrade(id)
            _uiState.update { it.copy(userMessage = "تم حذف الصفقة") }
        }
    }

    fun updatePortfolioCapital(capital: Double, riskPct: Double = _uiState.value.riskPerTradePct) {
        _uiState.update {
            it.copy(
                portfolioCapital = capital.coerceAtLeast(1000.0),
                riskPerTradePct = riskPct.coerceIn(0.5, 10.0),
                userMessage = "تم حفظ رأس مال المحفظة: ${String.format(java.util.Locale.US, "%,.0f", capital)} ج بنجاح 💼"
            )
        }
    }

    fun clearAllAuditTrades() {
        viewModelScope.launch {
            repository.clearAllTrades()
            _uiState.update { it.copy(userMessage = "تم مسح جميع سجلات الصفقات والتدقيق بالكامل 🗑️") }
        }
    }

    fun snapshotTomorrowPicksOnly() {
        viewModelScope.launch {
            val stocks = _uiState.value.allStocks
            if (stocks.isEmpty()) {
                _uiState.update { it.copy(userMessage = "لا توجد بيانات أسهم حالياً") }
                return@launch
            }

            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            val existing = allTrades.value

            // Select only the top elite picks for Tomorrow (Top 3 to 5 highest smartScore and R:R)
            val tomorrowCandidates = stocks
                .filter { it.isTomorrowPick }
                .sortedWith(compareByDescending<StockData> { it.smartScore }.thenByDescending { it.riskRewardRatio })
                .take(5)

            if (tomorrowCandidates.isEmpty()) {
                _uiState.update { it.copy(userMessage = "لم يجتز أي سهم شروط مرشح الغد الصارمة اليوم (حفاظاً على رأس المال)") }
                return@launch
            }

            var addedCount = 0
            for (stock in tomorrowCandidates) {
                val alreadySaved = existing.any { it.symbol == stock.symbol && it.tradeType == "مرشح الغد 🎯" && it.dateRecorded == today }
                if (!alreadySaved) {
                    val trade = TradeEntity(
                        symbol = stock.symbol,
                        name = stock.name,
                        entryPrice = stock.entryPrice,
                        target = stock.target1,
                        stopLoss = stock.stopLoss,
                        targetPct = stock.targetPct,
                        riskPct = stock.riskPct,
                        rr = stock.riskRewardRatio,
                        tradeType = "مرشح الغد 🎯",
                        dateRecorded = today,
                        status = "pending",
                        smartScore = stock.smartScore
                    )
                    repository.recordTrade(trade)
                    addedCount++
                }
            }

            _uiState.update {
                it.copy(
                    userMessage = if (addedCount > 0)
                        "تم حفظ صفوة مرشحات الغد لليوم القادم ($addedCount أسهم نارية 🎯) للتدقيق وقياس النتيجة"
                    else
                        "مرشحات الغد لليوم مسجلة مسبقاً في السجل لنفس التاريخ"
                )
            }
        }
    }

    fun snapshotAllCurrentScreenerRecommendations() {
        viewModelScope.launch {
            val stocks = _uiState.value.allStocks
            if (stocks.isEmpty()) {
                _uiState.update { it.copy(userMessage = "لا توجد أسهم حالياً لحفظ لقطة الفلاتر") }
                return@launch
            }

            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            val existing = allTrades.value

            var addedCount = 0
            val candidates = mutableListOf<Pair<StockData, String>>()

            // 1. مرشح الغد (صفوة 5 أسهم)
            stocks.filter { it.isTomorrowPick }
                .sortedByDescending { it.smartScore }
                .take(5)
                .forEach { stock -> candidates.add(Pair(stock, "مرشح الغد 🎯")) }

            // 2. بداية الصعود (صفوة 3 أسهم)
            stocks.filter { it.isEarlyUptrend }
                .sortedByDescending { it.upsideTo52wHigh }
                .take(3)
                .forEach { stock -> candidates.add(Pair(stock, "بداية الصعود 🚀")) }

            // 3. فرص الاختراق (صفوة 3 أسهم)
            stocks.filter { it.isRapidBreakout }
                .sortedByDescending { it.breakoutQuality.score }
                .take(3)
                .forEach { stock -> candidates.add(Pair(stock, "فرص الاختراق ⚡")) }

            // 4. صيد القيعان والارتداد (صفوة 3 أسهم)
            stocks.filter { it.isSupportBounce }
                .sortedByDescending { it.smartScore }
                .take(3)
                .forEach { stock -> candidates.add(Pair(stock, "صيد القيعان 💎")) }

            // 5. صيد التصحيح (صفوة 3 أسهم)
            stocks.filter { it.isCorrectionHunter }
                .sortedByDescending { it.smartScore }
                .take(3)
                .forEach { stock -> candidates.add(Pair(stock, "صيد التصحيح 🌊")) }

            for ((stock, category) in candidates) {
                val alreadySaved = existing.any { it.symbol == stock.symbol && it.tradeType == category && it.dateRecorded == today }
                if (!alreadySaved) {
                    val trade = TradeEntity(
                        symbol = stock.symbol,
                        name = stock.name,
                        entryPrice = stock.entryPrice,
                        target = stock.target1,
                        stopLoss = stock.stopLoss,
                        targetPct = stock.targetPct,
                        riskPct = stock.riskPct,
                        rr = stock.riskRewardRatio,
                        tradeType = category,
                        dateRecorded = today,
                        status = "pending",
                        smartScore = stock.smartScore
                    )
                    repository.recordTrade(trade)
                    addedCount++
                }
            }

            _uiState.update {
                it.copy(
                    userMessage = if (addedCount > 0)
                        "تم حفظ صفوة كافة الفلاتر بنجاح ($addedCount فرصة نارية مسجلة للتدقيق 📸)"
                    else
                        "فلاتر اليوم مسجلة مسبقاً في نظام التدقيق لنفس التاريخ"
                )
            }
        }
    }

    fun autoAuditAllTradesAgainstLivePrices(silent: Boolean = false) {
        viewModelScope.launch {
            val stocks = _uiState.value.allStocks
            val trades = allTrades.value
            if (stocks.isEmpty() || trades.isEmpty()) return@launch

            var hitCount = 0
            var stopCount = 0
            val stockMap = stocks.associateBy { it.symbol }

            for (trade in trades) {
                // Only evaluate pending trades
                if (trade.status != "pending") continue

                val currentStock = stockMap[trade.symbol] ?: continue
                val entryPrice = trade.entryPrice
                if (entryPrice <= 0.0) continue

                val livePrice = currentStock.price
                val dayHigh = currentStock.high
                val dayLow = currentStock.low

                // High precision check: Did price reach target during session?
                val targetReached = (dayHigh >= trade.target) || (livePrice >= trade.target)
                // Did price break stop loss during session?
                val stopLossBroken = (dayLow <= trade.stopLoss) || (livePrice <= trade.stopLoss)

                if (targetReached && !stopLossBroken) {
                    val gainPct = ((trade.target - entryPrice) / entryPrice) * 100.0
                    repository.updateTradeStatus(trade.id, "hit_target", gainPct)
                    hitCount++
                } else if (stopLossBroken && !targetReached) {
                    val lossPct = ((trade.stopLoss - entryPrice) / entryPrice) * 100.0
                    repository.updateTradeStatus(trade.id, "stopped_out", lossPct)
                    stopCount++
                } else if (targetReached && stopLossBroken) {
                    // Both extremes reached: settle based on where the live price closed/stands
                    if (livePrice >= entryPrice) {
                        val gainPct = ((trade.target - entryPrice) / entryPrice) * 100.0
                        repository.updateTradeStatus(trade.id, "hit_target", gainPct)
                        hitCount++
                    } else {
                        val lossPct = ((trade.stopLoss - entryPrice) / entryPrice) * 100.0
                        repository.updateTradeStatus(trade.id, "stopped_out", lossPct)
                        stopCount++
                    }
                } else {
                    // Actively moving: calculate live floating P&L %
                    val floatingPct = ((livePrice - entryPrice) / entryPrice) * 100.0
                    repository.updateTradeStatus(trade.id, "pending", floatingPct)
                }
            }

            val totalSettled = hitCount + stopCount
            if (!silent) {
                _uiState.update {
                    it.copy(
                        userMessage = if (totalSettled > 0)
                            "تم تدقيق الأداء: $hitCount حققت الهدف 🎯 و $stopCount كسرت الوقف"
                        else
                            "تم تحديث الأرباح والخسائر اللحظية لجميع الصفقات قيد الحركة ⚡"
                    )
                }
            }
        }
    }

    fun markAlertRead(id: Long) {
        viewModelScope.launch {
            repository.markAlertAsRead(id)
        }
    }

    fun markAllAlertsRead() {
        viewModelScope.launch {
            repository.markAllAlertsAsRead()
            _uiState.update { it.copy(userMessage = "تم تحديد جميع التنبيهات كمقروءة") }
        }
    }

    fun clearAllAlerts() {
        viewModelScope.launch {
            repository.clearAlerts()
            _uiState.update { it.copy(userMessage = "تم مسح سجل التنبيهات") }
        }
    }

    fun triggerTestAlert() {
        val topStock = _uiState.value.allStocks.firstOrNull { it.isRapidBreakout }
            ?: _uiState.value.allStocks.firstOrNull()

        if (topStock != null) {
            repository.triggerTestAlert(topStock.symbol, topStock.price, topStock.r1)
            _uiState.update { it.copy(userMessage = "تم إرسال إشعار فوري باختراق سهم ${topStock.symbol}") }
        } else {
            repository.triggerIndexShiftAlert("صاعد بقوة 🟢", 31920.0, 1.2)
            _uiState.update { it.copy(userMessage = "تم إرسال إشعار فوري بتغير اتجاه المؤشر") }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}

class StockViewModelFactory(
    private val repository: StockRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
