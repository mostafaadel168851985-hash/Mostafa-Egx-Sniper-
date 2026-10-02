package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CorporateNews
import com.example.data.model.NewsImpact
import com.example.data.model.StockData
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenBg
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StockRelatedNewsCard(
    stock: StockData,
    allNews: List<CorporateNews>,
    modifier: Modifier = Modifier
) {
    val stockNews = remember(stock, allNews) {
        val sym = stock.symbol.trim()
        val name = stock.name.trim()
        allNews.filter { news ->
            news.symbol.equals(sym, ignoreCase = true) ||
            news.companyName.contains(name, ignoreCase = true) ||
            name.contains(news.companyName, ignoreCase = true) ||
            news.title.contains(sym, ignoreCase = true) ||
            news.title.contains(name, ignoreCase = true)
        }.take(3)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, if (stockNews.isNotEmpty()) AccentCyan.copy(alpha = 0.5f) else OutlineDark, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Newspaper,
                        contentDescription = null,
                        tint = if (stockNews.isNotEmpty()) AccentCyan else GoldenAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📰 الأخبار والإفصاحات الجوهرية للسهم",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (stockNews.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(BullishGreenBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${stockNews.size} إفصاح متاح",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullishGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (stockNews.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariantDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لا توجد إفصاحات غير اعتيادية اليوم. يعتمد تحليل السهم على القوة الفنية وحجم السيولة وتوافق المؤشر العام.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            } else {
                stockNews.forEachIndexed { index, news ->
                    if (index > 0) {
                        HorizontalDivider(color = OutlineDark, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${news.category.icon} ${news.category.arabicLabel}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
                            )
                            Text(
                                text = news.date,
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = news.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )

                        if (news.summary.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = news.summary,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 2,
                                lineHeight = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(news.impact.colorHex).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = news.impact.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(news.impact.colorHex)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = news.source,
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
