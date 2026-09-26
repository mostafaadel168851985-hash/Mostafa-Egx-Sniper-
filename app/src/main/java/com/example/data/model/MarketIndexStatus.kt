package com.example.data.model

data class IndexDetail(
    val id: String,
    val symbol: String,
    val shortTitle: String,
    val fullTitle: String,
    val price: Double,
    val change: Double,
    val rsi: Double = 40.0,
    val sma50: Double = 0.0,
    val sma200: Double = 0.0,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val volume: Long = 0L,
    val statusText: String = "",
    val badgeTag: String = ""
)

data class MarketIndexStatus(
    val symbol: String = "EGX30",
    val price: Double = 53776.7,
    val change: Double = -0.83,
    val rsi: Double = 36.5,
    val sma50: Double = 54788.3,
    val sma200: Double = 49813.5,
    val open: Double = 54277.1,
    val high: Double = 54287.2,
    val low: Double = 52793.2,
    val volume: Long = 289980000L,
    val egx70Price: Double = 19835.0,
    val egx70Change: Double = -1.92,
    val egx100Price: Double = 26233.0,
    val egx100Change: Double = -1.70,
    val egx33ShariahPrice: Double = 5420.5,
    val egx33ShariahChange: Double = -0.75,
    val egx30CappedPrice: Double = 67157.8,
    val egx30CappedChange: Double = -1.01,
    val tamayuzPrice: Double = 44947.0,
    val tamayuzChange: Double = -2.82,
    val statusText: String = "🟡 سوق متذبذب وتصحيحي فوق SMA200 - تداول بحذر وانتقائية",
    val statusColorHex: Long = 0xFFF59E0B,
    val marketMultiplier: Double = 0.7,
    val trendDirection: TrendDirection = TrendDirection.CHOPPY,
    val lastUpdate: String = "",
    val allIndices: List<IndexDetail> = defaultIndicesList()
) {
    val isGoldenCross: Boolean
        get() = sma50 > 0 && sma200 > 0 && sma50 > sma200

    val isPriceAboveSma50: Boolean
        get() = price > 0 && sma50 > 0 && price > sma50

    val isPriceAboveSma200: Boolean
        get() = price > 0 && sma200 > 0 && price > sma200

    companion object {
        fun defaultIndicesList(): List<IndexDetail> = listOf(
            IndexDetail(
                id = "EGX30",
                symbol = "EGX30",
                shortTitle = "EGX 30",
                fullTitle = "مؤشر البورصة المصرية الرئيسي EGX30",
                price = 53776.7,
                change = -0.83,
                rsi = 36.5,
                sma50 = 54788.3,
                sma200 = 49813.5,
                open = 54277.1,
                high = 54287.2,
                low = 52793.2,
                volume = 289980000L,
                statusText = "متذبذب وتصحيحي فوق SMA200",
                badgeTag = "الرئيسي 🏆"
            ),
            IndexDetail(
                id = "EGX70",
                symbol = "EGX70EWI",
                shortTitle = "EGX 70",
                fullTitle = "مؤشر الأسهم الصغيرة والمتوسطة EGX70 EWI",
                price = 19835.0,
                change = -1.92,
                rsi = 36.7,
                sma50 = 20243.2,
                sma200 = 15241.5,
                open = 20222.3,
                high = 20226.7,
                low = 19513.7,
                volume = 1601880000L,
                statusText = "تصحيح مؤقت أعلى متوسط 200 يوم",
                badgeTag = "المتوسطة 📈"
            ),
            IndexDetail(
                id = "EGX100",
                symbol = "EGX100EWI",
                shortTitle = "EGX 100",
                fullTitle = "مؤشر المائة الأوسع نطاقاً EGX100 EWI",
                price = 26233.0,
                change = -1.70,
                rsi = 38.0,
                sma50 = 26495.1,
                sma200 = 20602.8,
                open = 26695.0,
                high = 26698.7,
                low = 25792.6,
                volume = 1891860000L,
                statusText = "اتجاه عام صاعد 200 يوم",
                badgeTag = "الأوسع 🌐"
            ),
            IndexDetail(
                id = "EGX33_SHARIAH",
                symbol = "EGX33SHR",
                shortTitle = "الشريعة ☪️",
                fullTitle = "مؤشر الشريعة الإسلامية EGX 33 Shariah",
                price = 5420.5,
                change = -0.75,
                rsi = 41.2,
                sma50 = 5510.0,
                sma200 = 4890.0,
                open = 5450.0,
                high = 5462.0,
                low = 5380.0,
                volume = 410000000L,
                statusText = "متوافق مع معايير الهيئة الشرعية",
                badgeTag = "إسلامي ☪️"
            ),
            IndexDetail(
                id = "EGX30_CAPPED",
                symbol = "EGX30CAPPED",
                shortTitle = "30 Capped",
                fullTitle = "مؤشر EGX30 محدد الأوزان Capped",
                price = 67157.8,
                change = -1.01,
                rsi = 37.1,
                sma50 = 68420.0,
                sma200 = 61200.0,
                open = 67800.0,
                high = 67850.0,
                low = 66500.0,
                volume = 320000000L,
                statusText = "سقف ترجيحي لأكبر الشركات",
                badgeTag = "محدد الأوزان ⚖️"
            ),
            IndexDetail(
                id = "TAMAYUZ",
                symbol = "TAMAYUZ",
                shortTitle = "تميز",
                fullTitle = "مؤشر الشركات الصغيرة الواعدة (تميز)",
                price = 44947.0,
                change = -2.82,
                rsi = 33.5,
                sma50 = 46800.0,
                sma200 = 38500.0,
                open = 46100.0,
                high = 46250.0,
                low = 44300.0,
                volume = 125000000L,
                statusText = "بورصة النيل والمشروعات الناشئة",
                badgeTag = "شركات واعدة 🚀"
            )
        )
    }
}

enum class TrendDirection(val arabicLabel: String) {
    BULLISH("صاعد 🟢"),
    CHOPPY("متذبذب 🟡"),
    BEARISH("هابط 🔴")
}
