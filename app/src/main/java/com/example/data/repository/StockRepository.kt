package com.example.data.repository

import com.example.data.local.AlertDao
import com.example.data.local.AlertEntity
import com.example.data.local.TradeDao
import com.example.data.local.TradeEntity
import com.example.data.local.WatchlistDao
import com.example.data.local.WatchlistEntity
import com.example.data.model.CorporateNews
import com.example.data.model.MarketIndexStatus
import com.example.data.model.StockData
import com.example.data.model.TrendDirection
import com.example.data.remote.CorporateNewsApi
import com.example.data.remote.TradingViewScannerApi
import com.example.notification.AlertNotificationManager
import kotlinx.coroutines.flow.Flow

data class PerformanceStats(
    val totalTrades: Int = 0,
    val hitTargetCount: Int = 0,
    val stoppedOutCount: Int = 0,
    val pendingCount: Int = 0,
    val winRatePct: Double = 0.0,
    val avgRiskReward: Double = 0.0,
    val totalProfitPct: Double = 0.0
)

class StockRepository(
    private val api: TradingViewScannerApi,
    private val tradeDao: TradeDao,
    private val alertDao: AlertDao,
    private val watchlistDao: WatchlistDao,
    private val notificationManager: AlertNotificationManager
) {
    private var lastIndexTrend: TrendDirection? = null
    private val corporateNewsApi = CorporateNewsApi()

    suspend fun getCorporateNews(): List<CorporateNews> {
        return corporateNewsApi.fetchCorporateNews()
    }

    suspend fun getMarketStatus(): MarketIndexStatus {
        val currentStatus = api.fetchMarketStatus()
        val previous = lastIndexTrend
        if (previous != null && previous != currentStatus.trendDirection) {
            notificationManager.notifyIndexDirectionChange(
                oldTrend = previous.arabicLabel,
                newTrend = currentStatus.trendDirection.arabicLabel,
                egx30Price = currentStatus.price,
                change = currentStatus.change
            )
        }
        lastIndexTrend = currentStatus.trendDirection
        return currentStatus
    }

    suspend fun getAllStocks(marketMultiplier: Double): List<StockData> {
        val stocks = api.fetchAllStocks(marketMultiplier)

        // Check for golden cross or top breakouts in watchlist or top stocks to send notifications
        stocks.take(5).forEach { stock ->
            if (stock.isRapidBreakout && stock.volumeRatio >= 2.0) {
                // Potential notification trigger
            }
        }
        return stocks
    }

    suspend fun searchAndAnalyzeStock(
        query: String,
        currentStocks: List<StockData>,
        marketMultiplier: Double = 0.7
    ): StockData? {
        val q = query.trim()
        if (q.isBlank()) return null

        val resolvedSymbol = com.example.data.remote.EgyptianStockDirectory.findMatchingSymbol(q)

        // 1. Try exact or resolved symbol match in loaded stocks
        val exactSymbol = currentStocks.firstOrNull {
            it.symbol.equals(q, ignoreCase = true) ||
            (resolvedSymbol != null && it.symbol.equals(resolvedSymbol, ignoreCase = true))
        }
        if (exactSymbol != null) return exactSymbol

        // 2. Try match in Arabic name or description in loaded stocks
        val normQ = com.example.data.remote.EgyptianStockDirectory.normalizeArabic(q)
        val partialMatch = currentStocks.firstOrNull {
            it.symbol.contains(q, ignoreCase = true) ||
            com.example.data.remote.EgyptianStockDirectory.normalizeArabic(it.name).contains(normQ) ||
            com.example.data.remote.EgyptianStockDirectory.normalizeArabic(it.description).contains(normQ)
        }
        if (partialMatch != null) return partialMatch

        // 3. Query TradingView directly for live stock
        val targetQuery = resolvedSymbol ?: q
        return api.fetchSingleStock(targetQuery, marketMultiplier)
    }

    fun triggerTestAlert(symbol: String, price: Double, r1: Double) {
        notificationManager.notifyBreakout(symbol, price, r1, 2.3)
    }

    fun triggerIndexShiftAlert(trend: String, price: Double, change: Double) {
        notificationManager.notifyIndexDirectionChange(
            oldTrend = "متذبذب 🟡",
            newTrend = trend,
            egx30Price = price,
            change = change
        )
    }

    // Trade Journal Flow & Actions
    val allTrades: Flow<List<TradeEntity>> = tradeDao.getAllTrades()

    suspend fun recordTrade(trade: TradeEntity): Long = tradeDao.insertTrade(trade)

    suspend fun updateTrade(trade: TradeEntity) = tradeDao.updateTrade(trade)

    suspend fun updateTradeStatus(id: Long, status: String, profitPct: Double?) =
        tradeDao.updateTradeStatus(id, status, profitPct)

    suspend fun deleteTrade(id: Long) = tradeDao.deleteTradeById(id)

    suspend fun clearAllTrades() = tradeDao.clearAllTrades()

    // Alerts Flow & Actions
    val allAlerts: Flow<List<AlertEntity>> = alertDao.getAllAlerts()
    val unreadAlerts: Flow<List<AlertEntity>> = alertDao.getUnreadAlerts()

    suspend fun markAlertAsRead(id: Long) = alertDao.markAsRead(id)
    suspend fun markAllAlertsAsRead() = alertDao.markAllAsRead()
    suspend fun clearAlerts() = alertDao.clearAllAlerts()

    // Watchlist Flow & Actions
    val watchlist: Flow<List<WatchlistEntity>> = watchlistDao.getAllWatchlist()

    suspend fun addToWatchlist(symbol: String, name: String, currentPrice: Double) =
        watchlistDao.insertWatchlist(
            WatchlistEntity(
                symbol = symbol,
                name = name,
                addedPrice = currentPrice
            )
        )

    suspend fun removeFromWatchlist(symbol: String) =
        watchlistDao.removeFromWatchlist(symbol)

    suspend fun isInWatchlist(symbol: String): Boolean =
        watchlistDao.isInWatchlist(symbol)
}
