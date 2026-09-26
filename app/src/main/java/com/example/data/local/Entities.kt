package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val name: String,
    val entryPrice: Double,
    val target: Double,
    val stopLoss: Double,
    val targetPct: Double = 0.0,
    val riskPct: Double = 0.0,
    val rr: Double = 0.0,
    val tradeType: String,
    val dateRecorded: String,
    val status: String = "pending", // "pending", "hit_target", "stopped_out"
    val profitPct: Double? = null,
    val smartScore: Int = 0,
    val notes: String = ""
)

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val symbol: String? = null,
    val alertType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val importance: String = "HIGH" // "HIGH", "NORMAL"
)

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val symbol: String,
    val name: String = "",
    val addedPrice: Double = 0.0,
    val targetPrice: Double? = null,
    val stopLossPrice: Double? = null,
    val alertOnSmaCross: Boolean = true,
    val alertOnBreakout: Boolean = true,
    val addedTimestamp: Long = System.currentTimeMillis()
)
