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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockData
import com.example.ui.components.StockCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ScreenerFilter
import com.example.viewmodel.StockUiState

@Composable
fun NextDayScreenerScreen(
    uiState: StockUiState,
    onSelectFilter: (ScreenerFilter) -> Unit,
    onSelectSector: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectStock: (StockData) -> Unit,
    onRecordTrade: (StockData) -> Unit,
    onCalculateAverage: (StockData) -> Unit,
    onToggleAlert: (StockData) -> Unit,
    onNavigateToAnalyze: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        ScreenerFilter.TOMORROW_PICKS,
        ScreenerFilter.SHARIAH,
        ScreenerFilter.CORRECTIONS,
        ScreenerFilter.BREAKOUTS,
        ScreenerFilter.SUPPORT_BOUNCE,
        ScreenerFilter.EARLY_UPTREND,
        ScreenerFilter.ALL
    )

    val sectors = listOf(
        "الكل 🌍",
        "🏦 البنوك",
        "🏗️ العقارات",
        "🍔 الأغذية والمشروبات",
        "📡 الاتصالات والتكنولوجيا",
        "🏭 البتروكيماويات والصناعات",
        "🛒 التجارة والخدمات"
    )

    val filterDescription = when (uiState.activeScreenerFilter) {
        ScreenerFilter.TOMORROW_PICKS ->
            "🎯 مرشحات جلسة الغد: أسهم بأعلى نسب نجاح، ومستويات دخول دقيقة مع نسبة عائد لمخاطرة R:R لا تقل عن 1.8 وتأكيد المتوسطات التاريخية 50 و 200 يوم."
        ScreenerFilter.SHARIAH ->
            "☪️ أسهم الشريعة الإسلامية: أسهم منتقاة ومتوافقة مع المعايير والضوابط الشرعية المعتمدة في البورصة المصرية."
        ScreenerFilter.CORRECTIONS ->
            "🔻 صائد التصحيحات: أسهم في اتجاه عام صاعد تصحح حالياً بمؤشر RSI بين 28 و 55 مع بداية ارتداد شرائي وسيولة ممتازة."
        ScreenerFilter.BREAKOUTS ->
            "⚡ قناص الاختراق السريع: أسهم تخترق المقاومة R1 بسيولة استثنائية وإغلاق قوي بالقرب من أعلى سعر لليوم."
        ScreenerFilter.SUPPORT_BOUNCE ->
            "🛡️ دعم وارتداد: أسهم بالقرب من الدعم الأول S1 أو الثاني S2 (أقل من 1.5%) مع إشارة ارتداد وتماسك واضحة."
        ScreenerFilter.EARLY_UPTREND ->
            "🚀 بداية موجة صاعدة: أسهم في بداية الانعكاس تخترق SMA20 مع مساحة صعود لا تقل عن 20% حتى القمة السنوية (52 أسبوع)."
        ScreenerFilter.ALL ->
            "🌍 جميع أسهم البورصة المصرية مرتبة حسب الأداء ونسبة التغير والسيولة."
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("next_day_screener_column"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Title & Description
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = null,
                    tint = BullishGreen,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "فلتر اختيار أسهم الغد",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "تحديد مستويات الدخول ووقف الخسارة والمستهدفات بدقة",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Search Bar
        item {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("screener_search_input"),
                placeholder = { Text("بحث برمز السهم أو الاسم (مثال: COMI, TMGH, حديد عز)...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "مسح", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BullishGreen,
                    unfocusedBorderColor = SurfaceVariantDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                )
            )
        }

        // Strategy Filter Tabs
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isSelected = uiState.activeScreenerFilter == filter
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) BullishGreen else SurfaceDark,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectFilter(filter) }
                            .padding(horizontal = 14.dp, vertical = 9.dp)
                            .testTag("filter_tab_${filter.name}")
                    ) {
                        Text(
                            text = filter.arabicTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }
        }

        // Strategy explanation card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "💡 معايير الفلتر الحالية:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenAmber
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${uiState.filteredStocks.size} سهم مطابق",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = filterDescription,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Sector Filter Chips
        item {
            Spacer(modifier = Modifier.height(10.dp))
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
                                if (isSelected) AccentCyan else SurfaceVariantDark,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectSector(sector) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = sector,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Results List
        if (uiState.filteredStocks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "لا توجد أسهم مطابقة للفلتر في هذا القطاع حالياً",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                        if (uiState.searchQuery.isNotBlank() && onNavigateToAnalyze != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onNavigateToAnalyze(uiState.searchQuery) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "استخراج كارت تحليل شامل لسهم \"${uiState.searchQuery}\"",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(uiState.filteredStocks, key = { it.symbol }) { stock ->
                StockCard(
                    stock = stock,
                    onSelectStock = onSelectStock,
                    onRecordTrade = onRecordTrade,
                    onCalculateAverage = onCalculateAverage,
                    onToggleAlert = onToggleAlert,
                    portfolioCapital = uiState.portfolioCapital
                )
            }
        }
    }
}
