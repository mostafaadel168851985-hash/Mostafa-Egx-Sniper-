package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CorporateCategory
import com.example.data.model.CorporateNews
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ShareHelper
import com.example.viewmodel.StockUiState

@Composable
fun CorporateNewsScreen(
    uiState: StockUiState,
    onCategorySelect: (CorporateCategory) -> Unit,
    onSearchChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onAnalyzeStock: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories = CorporateCategory.entries.toTypedArray()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp)
            .testTag("corporate_news_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_news_back")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Icon(
                    Icons.Default.Campaign,
                    contentDescription = null,
                    tint = GoldenAmber,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "أخبار الاكتتابات والإفصاحات",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "بث حي: معلومات مباشر • إنفستنج عربي • البورصة المصرية • المال",
                        fontSize = 10.sp,
                        color = AccentCyan
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .background(SurfaceVariantDark, RoundedCornerShape(10.dp))
                        .size(38.dp)
                        .testTag("btn_refresh_news")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "تحديث الأخبار", tint = AccentCyan)
                }
            }
        }

        // Search Bar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.corporateNewsSearchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_news"),
                placeholder = { Text("ابحث في الأخبار أو رمز الشركة (مثل: TMGH, أكت, بنك)...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan) },
                trailingIcon = {
                    if (uiState.corporateNewsSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldenAmber,
                    unfocusedBorderColor = OutlineDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Horizontal Category Filter Chips
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = uiState.selectedCorporateCategory == cat
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) GoldenAmber else SurfaceDark,
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) GoldenAmber else OutlineDark,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onCategorySelect(cat) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("chip_news_cat_${cat.name}")
                    ) {
                        Text(
                            text = cat.arabicLabel,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else TextSecondary
                        )
                    }
                }
            }
        }

        // News Count Bar
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "عدد الأخبار المعروضة: ${uiState.filteredCorporateNews.size}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                if (uiState.isLoadingNews) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = GoldenAmber, strokeWidth = 2.dp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // News List
        if (uiState.filteredCorporateNews.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Newspaper, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("لا توجد أخبار تطابق البحث أو التصنيف المختار", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(uiState.filteredCorporateNews, key = { it.id }) { news ->
                CorporateNewsItemCard(
                    news = news,
                    onShareWhatsApp = { ShareHelper.shareCorporateNewsToWhatsApp(context, news) },
                    onAnalyzeStock = { onAnalyzeStock(news.symbol) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun CorporateNewsItemCard(
    news: CorporateNews,
    onShareWhatsApp: () -> Unit,
    onAnalyzeStock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, OutlineDark, RoundedCornerShape(14.dp))
            .clickable { expanded = !expanded }
            .testTag("news_card_${news.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category & Impact Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .background(GoldenAmber.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = news.category.arabicLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                }

                // Impact & Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = news.impact.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(news.impact.colorHex)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = news.date,
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Company & Symbol Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = news.companyName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentCyan
                )
                if (news.symbol.isNotBlank() && news.symbol != "EGX") {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(SurfaceVariantDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = news.symbol,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // News Title
            Text(
                text = news.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = news.summary,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            // Status Badge
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "الحالة: ", fontSize = 11.sp, color = TextMuted)
                Text(text = news.status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BullishGreen)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded Full Details & Actions
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "تفاصيل الإفصاح والخطة المالية:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = news.fullDetails,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🏛️ المصدر: ${news.source}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onShareWhatsApp,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_share_news_${news.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("واتساب 📲", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (!news.articleUrl.isNullOrBlank()) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.articleUrl))
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_open_browser_${news.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("المصدر 🌐", color = AccentCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (news.symbol.isNotBlank() && news.symbol != "EGX") {
                            Button(
                                onClick = onAnalyzeStock,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_analyze_news_stock_${news.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.QueryStats, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${news.symbol} 📊", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
