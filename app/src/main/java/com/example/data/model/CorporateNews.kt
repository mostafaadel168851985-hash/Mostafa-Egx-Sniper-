package com.example.data.model

enum class CorporateCategory(val arabicLabel: String, val icon: String) {
    ALL("الكل 🌐", "🌐"),
    IPO("اكتتابات وطروحات 🎯", "🎯"),
    MERGER_ACQUISITION("اندماج واستحواذ 🤝", "🤝"),
    CAPITAL_INCREASE("زيادة رأس المال 📈", "📈"),
    BONUS_SHARES("أسهم مجانية 🎁", "🎁"),
    DIVIDENDS("توزيعات أرباح 💵", "💵"),
    DISCLOSURE("إفصاحات جوهرية 📢", "📢")
}

enum class NewsImpact(val label: String, val colorHex: Long) {
    BULLISH("إيجابي 🟢", 0xFF00E676),
    NEUTRAL("محايد ⚪", 0xFF90A4AE),
    WATCH("هام للمتابعة 🟡", 0xFFFFD600)
}

data class CorporateNews(
    val id: String,
    val title: String,
    val companyName: String,
    val symbol: String,
    val category: CorporateCategory,
    val date: String,
    val summary: String,
    val fullDetails: String,
    val status: String,
    val source: String = "البورصة المصرية EGX / الرقابة المالية",
    val impact: NewsImpact = NewsImpact.BULLISH
)
