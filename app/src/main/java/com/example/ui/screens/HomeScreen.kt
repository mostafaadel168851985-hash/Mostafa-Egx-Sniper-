package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockData
import com.example.data.remote.EgyptianStockDirectory
import com.example.ui.components.IndexStatusBanner
import com.example.ui.components.StockCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentPurple
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.NavScreen
import com.example.viewmodel.ScreenerFilter
import com.example.viewmodel.StockUiState

@Composable
fun HomeScreen(
    uiState: StockUiState,
    onNavigate: (NavScreen) -> Unit,
    onSelectStock: (StockData) -> Unit,
    onRecordTrade: (StockData) -> Unit,
    onCalculateAverage: (StockData) -> Unit,
    onToggleAlert: (StockData) -> Unit,
    onRefresh: () -> Unit,
    onTestAlert: () -> Unit,
    onSelectFilter: (ScreenerFilter) -> Unit,
    onSelectSector: (String) -> Unit,
    onQuickAnalyze: (String) -> Unit = {},
    onToggleAutoRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var homeStockQuery by remember { mutableStateOf("") }
    val sectors = listOf(
        "الكل 🌍",
        "🏦 البنوك",
        "🏗️ العقارات",
        "🍔 الأغذية والمشروبات",
        "📡 الاتصالات والتكنولوجيا",
        "🏭 البتروكيماويات والصناعات",
        "🛒 التجارة والخدمات"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("home_screen_lazy_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Index Status Banner
            IndexStatusBanner(
                status = uiState.marketStatus,
                onRefresh = onRefresh,
                onTestAlert = onTestAlert,
                lastUpdatedTime = uiState.lastUpdatedTime,
                isAutoRefreshActive = uiState.isAutoRefreshActive,
                onToggleAutoRefresh = onToggleAutoRefresh
            )
        }

        // Corporate Actions & IPOs Banner
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldenAmber.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .clickable { onNavigate(NavScreen.CORPORATE_NEWS) }
                    .testTag("banner_corporate_news"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(GoldenAmber.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📢", fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "رادار الاكتتابات وزيادة رأس المال والاندماج",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenAmber
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(BullishGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${uiState.corporateNews.size} إفصاح جديد",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullishGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        val latestTitle = uiState.corporateNews.firstOrNull()?.title
                            ?: "طروحات المصرف المتحد، وزيادات رؤوس الأموال، وصفقات الاستحواذ والاندماج"
                        Text(
                            text = latestTitle,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "عرض التفاصيل",
                        tint = GoldenAmber,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(180f)
                    )
                }
            }
        }

        // Quick Stats Row
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "⚡ نظرة عامة على فرص البورصة المصرية",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            val breakoutsCount = uiState.allStocks.count { it.isRapidBreakout }
            val correctionsCount = uiState.allStocks.count { it.isCorrectionHunter }
            val earlyUptrendCount = uiState.allStocks.count { it.isEarlyUptrend }
            val tomorrowPicksCount = minOf(5, uiState.allStocks.count { it.isTomorrowPick }.takeIf { it > 0 } ?: 5)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = "صفوة الغد (Top 5)",
                    count = "$tomorrowPicksCount",
                    color = BullishGreen,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectFilter(ScreenerFilter.TOMORROW_PICKS)
                            onNavigate(NavScreen.NEXT_DAY_SCREENER)
                        }
                )
                QuickStatCard(
                    title = "قناص الاختراق",
                    count = "$breakoutsCount",
                    color = AccentCyan,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectFilter(ScreenerFilter.BREAKOUTS)
                            onNavigate(NavScreen.NEXT_DAY_SCREENER)
                        }
                )
                QuickStatCard(
                    title = "صائد التصحيحات",
                    count = "$correctionsCount",
                    color = GoldenAmber,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectFilter(ScreenerFilter.CORRECTIONS)
                            onNavigate(NavScreen.NEXT_DAY_SCREENER)
                        }
                )
                QuickStatCard(
                    title = "بداية صعود ≥20%",
                    count = "$earlyUptrendCount",
                    color = AccentPurple,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelectFilter(ScreenerFilter.EARLY_UPTREND)
                            onNavigate(NavScreen.NEXT_DAY_SCREENER)
                        }
                )
            }
        }

        // Quick Action Shortcuts
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShortcutCard(
                    title = "تحليل أي سهم",
                    subtitle = "كارت فني شامل",
                    icon = Icons.Default.QueryStats,
                    accentColor = AccentCyan,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(NavScreen.ANALYZE_STOCK) }
                )
                ShortcutCard(
                    title = "أسهم الغد",
                    subtitle = "مستويات الدخول",
                    icon = Icons.Default.FilterAlt,
                    accentColor = BullishGreen,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(NavScreen.NEXT_DAY_SCREENER) }
                )
                ShortcutCard(
                    title = "متوسط السعر",
                    subtitle = "التبريد الذكي",
                    icon = Icons.Default.Calculate,
                    accentColor = GoldenAmber,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(NavScreen.AVERAGE_CALCULATOR) }
                )
                ShortcutCard(
                    title = "سجل الأداء",
                    subtitle = "متابعة الصفقات",
                    icon = Icons.Default.History,
                    accentColor = AccentPurple,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(NavScreen.JOURNAL) }
                )
            }
        }

        // Portfolio Capital & Allocation Hub Card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldenAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable { onNavigate(NavScreen.PORTFOLIO_ALLOCATION) }
                    .testTag("card_portfolio_hub"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141F1A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(GoldenAmber.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "رأس مال المحفظة: ${String.format(java.util.Locale.US, "%,.0f", uiState.portfolioCapital)} ج.م 💼",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "ميزانية الصفقة: ${String.format(java.util.Locale.US, "%,.0f", uiState.defaultDealBudget)} ج • اضغط للتوزيع الأمثل",
                            fontSize = 11.sp,
                            color = GoldenAmber
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = TextMuted)
                }
            }
        }

        // Direct Instant Stock Analyzer Card
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineDark, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تحليل فوري لأي سهم بالبورصة المصرية:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = homeStockQuery,
                            onValueChange = { homeStockQuery = it },
                            placeholder = { Text("اكتب اسم أو رمز السهم (مثال: طلعت مصطفى, TMGH)...", fontSize = 11.sp, color = TextMuted) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceVariantDark,
                                unfocusedContainerColor = SurfaceVariantDark,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = OutlineDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (homeStockQuery.isNotBlank()) {
                                    onQuickAnalyze(homeStockQuery)
                                    onNavigate(NavScreen.ANALYZE_STOCK)
                                } else {
                                    onNavigate(NavScreen.ANALYZE_STOCK)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("حلل ⚡", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    // Quick pick chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("TMGH" to "طلعت مصطفى", "COMI" to "التجاري الدولي", "FWRY" to "فوري", "SWDY" to "السويدي", "ABUK" to "أبو قير", "SKPC" to "سيدي كرير", "CCAP" to "القلعة").forEach { (sym, lbl) ->
                            Box(
                                modifier = Modifier
                                    .background(SurfaceVariantDark, RoundedCornerShape(16.dp))
                                    .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                                    .clickable {
                                        onQuickAnalyze(sym)
                                        onNavigate(NavScreen.ANALYZE_STOCK)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$lbl ($sym)",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sectors Chips
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sectors.forEach { sector ->
                    val isSelected = uiState.selectedSector == sector
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) AccentCyan else SurfaceDark,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectSector(sector) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = sector,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }
        }

        // Featured Opportunities Header
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = BullishGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🏆 أفضل فرص السوق المختارة (Smart Score)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${uiState.filteredStocks.size} فرصة",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (uiState.isLoading && uiState.allStocks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = BullishGreen)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "جاري مسح وتحليل أسهم البورصة المصرية...",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else if (uiState.filteredStocks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد أسهم مطابقة لمعايير الفلترة الحالية",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(uiState.filteredStocks.take(20), key = { it.symbol }) { stock ->
                StockCard(
                    stock = stock,
                    onSelectStock = onSelectStock,
                    onRecordTrade = onRecordTrade,
                    onCalculateAverage = onCalculateAverage,
                    onToggleAlert = onToggleAlert,
                    portfolioCapital = uiState.portfolioCapital,
                    allNews = uiState.corporateNews
                )
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = count,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ShortcutCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Icon(
                icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}
