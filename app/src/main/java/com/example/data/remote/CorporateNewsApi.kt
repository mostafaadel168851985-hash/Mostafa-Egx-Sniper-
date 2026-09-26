package com.example.data.remote

import com.example.data.model.CorporateCategory
import com.example.data.model.CorporateNews
import com.example.data.model.NewsImpact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class CorporateNewsApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun fetchCorporateNews(): List<CorporateNews> = withContext(Dispatchers.IO) {
        // Fetch concurrently from live Mubasher/EGX and Investing.com feeds
        val liveResults = coroutineScope {
            val egxDeferred = async { fetchMubasherAndEgxLiveNews() }
            val investingDeferred = async { fetchInvestingComLiveNews() }

            val list = mutableListOf<CorporateNews>()
            try {
                list.addAll(egxDeferred.await())
            } catch (_: Exception) {}

            try {
                list.addAll(investingDeferred.await())
            } catch (_: Exception) {}

            list
        }

        // If online fetch returned items, prioritize them at the top!
        val combined = (liveResults + getCuratedCorporateActions())
            .distinctBy { it.title.trim().take(40) }

        combined
    }

    private fun fetchMubasherAndEgxLiveNews(): List<CorporateNews> {
        val list = mutableListOf<CorporateNews>()
        val url = "https://news.google.com/rss/search?q=(site:mubasher.info+OR+site:alborsanews.com+OR+site:almalnews.com+OR+%22%D8%A7%D9%84%D8%A8%D9%88%D8%B1%D8%B5%D8%A9+%D8%A7%D9%84%D9%85%D8%B5%D8%B1%D9%8A%D8%A9%22)+(%D8%A7%D9%83%D8%AA%D8%AA%D8%A7%D8%A8+OR+%D8%A7%D9%86%D8%AF%D9%85%D8%A7%D8%AC+OR+%D8%A7%D8%B3%D8%AA%D8%AD%D9%88%D8%A7%D8%B0+OR+%22%D8%B1%D8%A3%D8%B3+%D8%A7%D9%84%D9%85%D8%A7%D9%84%22+OR+%D8%A3%D8%B3%D9%87%D9%85+OR+%D8%A3%D8%B1%D8%A8%D8%A7%D8%AD)&hl=ar&gl=EG&ceid=EG:ar"

        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val xml = response.body?.string().orEmpty()
                    list.addAll(parseRssXml(xml, defaultSource = "معلومات مباشر / البورصة المصرية"))
                }
            }
        } catch (_: Exception) {
            // Network fallback
        }
        return list
    }

    private fun fetchInvestingComLiveNews(): List<CorporateNews> {
        val list = mutableListOf<CorporateNews>()
        val url = "https://sa.investing.com/rss/news_25.rss"

        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val xml = response.body?.string().orEmpty()
                    list.addAll(parseRssXml(xml, defaultSource = "Investing.com عربي"))
                }
            }
        } catch (_: Exception) {
            // Network fallback
        }
        return list
    }

    private fun parseRssXml(xml: String, defaultSource: String): List<CorporateNews> {
        val list = mutableListOf<CorporateNews>()
        val itemPattern = "<item>(.*?)</item>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matches = itemPattern.findAll(xml).take(20)

        for ((idx, match) in matches.withIndex()) {
            val content = match.value

            val rawTitle = extractXmlTag(content, "title") ?: continue
            val link = extractXmlTag(content, "link")
            val pubDateRaw = extractXmlTag(content, "pubDate")
            val sourceName = extractXmlTag(content, "source") ?: defaultSource
            val rawDesc = extractXmlTag(content, "description") ?: ""

            // Split title and source if present (e.g., "عنوان الخبر - معلومات مباشر")
            val parts = rawTitle.split(" - ")
            val cleanTitle = if (parts.size > 1 && parts.last().length < 30) {
                parts.dropLast(1).joinToString(" - ").trim()
            } else {
                rawTitle.trim()
            }

            val finalSource = if (parts.size > 1 && parts.last().length < 30) {
                parts.last().trim()
            } else {
                sourceName
            }

            val cleanDesc = rawDesc
                .replace("&lt;.*?&gt;".toRegex(), "")
                .replace("<.*?>".toRegex(), "")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .trim()

            // Classify category by content
            val combinedText = "$cleanTitle $cleanDesc"
            val category = when {
                combinedText.contains("اكتتاب") || combinedText.contains("طرح") || combinedText.contains("IPO") -> CorporateCategory.IPO
                combinedText.contains("استحواذ") || combinedText.contains("اندماج") || combinedText.contains("شراء إجباري") || combinedText.contains("صفقة") -> CorporateCategory.MERGER_ACQUISITION
                combinedText.contains("رأس المال") || combinedText.contains("رأسمال") || combinedText.contains("تجزئة") || combinedText.contains("زيادة رأس") -> CorporateCategory.CAPITAL_INCREASE
                combinedText.contains("مجانية") || combinedText.contains("أسهم مجانية") -> CorporateCategory.BONUS_SHARES
                combinedText.contains("أرباح") || combinedText.contains("توزيعات") || combinedText.contains("كوبون") -> CorporateCategory.DIVIDENDS
                else -> CorporateCategory.DISCLOSURE
            }

            // Identify company / symbol
            val (companyName, symbol) = detectCompanyAndSymbol(cleanTitle)

            // Format date to friendly Arabic relative time
            val formattedDate = formatPubDate(pubDateRaw)

            val impact = when {
                combinedText.contains("أرباح") || combinedText.contains("صعود") || combinedText.contains("نمو") || combinedText.contains("قفزة") || combinedText.contains("مجانية") -> NewsImpact.BULLISH
                combinedText.contains("خسائر") || combinedText.contains("تراجع") || combinedText.contains("هبوط") || combinedText.contains("حذر") -> NewsImpact.WATCH
                else -> NewsImpact.NEUTRAL
            }

            list.add(
                CorporateNews(
                    id = "live_${defaultSource.take(4)}_${idx}_${cleanTitle.hashCode()}",
                    title = cleanTitle,
                    companyName = companyName,
                    symbol = symbol,
                    category = category,
                    date = formattedDate,
                    summary = cleanDesc.ifBlank { cleanTitle },
                    fullDetails = if (cleanDesc.length > 30) "$cleanDesc\n\n📌 المصدر الرسمي: $finalSource" else cleanTitle,
                    status = "خبر عاجل ومباشر 🔴",
                    source = finalSource,
                    impact = impact,
                    articleUrl = link
                )
            )
        }
        return list
    }

    private fun extractXmlTag(content: String, tag: String): String? {
        val cdataPattern = "<$tag><!\\[CDATA\\[(.*?)\\]\\]></$tag>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val simplePattern = "<$tag>(.*?)</$tag>".toRegex(RegexOption.DOT_MATCHES_ALL)
        return cdataPattern.find(content)?.groupValues?.get(1)
            ?: simplePattern.find(content)?.groupValues?.get(1)
    }

    private fun formatPubDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return "مباشر الآن ⚡"
        return try {
            // Google News RFC 822 format: "Thu, 24 Sep 2026 12:32:49 GMT"
            val rfcFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
            rfcFormat.timeZone = TimeZone.getTimeZone("GMT")
            val date = rfcFormat.parse(rawDate)

            if (date != null) {
                val now = System.currentTimeMillis()
                val diffHours = (now - date.time) / (1000 * 60 * 60)
                when {
                    diffHours < 1 -> "منذ قليل ⚡"
                    diffHours < 24 -> "اليوم (منذ $diffHours ساعة)"
                    diffHours < 48 -> "أمس"
                    else -> {
                        val outFormat = SimpleDateFormat("dd MMM yyyy", Locale("ar"))
                        outFormat.format(date)
                    }
                }
            } else {
                rawDate.take(16)
            }
        } catch (_: Exception) {
            try {
                // Investing.com format: "2026-09-26 01:21:02"
                val invFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val date = invFormat.parse(rawDate)
                if (date != null) {
                    val outFormat = SimpleDateFormat("dd MMM - hh:mm a", Locale("ar"))
                    outFormat.format(date)
                } else {
                    rawDate.take(16)
                }
            } catch (_: Exception) {
                rawDate.take(16)
            }
        }
    }

    private fun detectCompanyAndSymbol(title: String): Pair<String, String> {
        val map = listOf(
            Triple("التجاري الدولي", "COMI", "البنك التجاري الدولي"),
            Triple("طلعت مصطفى", "TMGH", "مجموعة طلعت مصطفى"),
            Triple("فوري", "FWRY", "فوري لتكنولوجيا البنوك"),
            Triple("بلتون", "BTFH", "بلتون المالية القابضة"),
            Triple("السويدي", "SWDY", "السويدي إليكتريك"),
            Triple("الشرقية للدخان", "EAST", "إيسترن كومباني"),
            Triple("إيسترن", "EAST", "الشرقية - إيسترن"),
            Triple("أبو قير", "ABUK", "أبو قير للأسمدة"),
            Triple("موبكو", "MFPC", "مصر لإنتاج الأسمدة"),
            Triple("مدينة مصر", "MASR", "مدينة مصر للإسكان"),
            Triple("بالم هيلز", "PHDC", "بالم هيلز للتعمير"),
            Triple("مصر الجديدة", "HELI", "مصر الجديدة للإسكان"),
            Triple("سيدي كرير", "SKPC", "سيدي كرير للبتروكيماويات"),
            Triple("سيدبك", "SKPC", "سيدي كرير"),
            Triple("إيديتا", "EFID", "إيديتا للصناعات الغذائية"),
            Triple("جهينة", "JUFO", "جهينة للصناعات الغذائية"),
            Triple("أوراسكوم", "ORAS", "أوراسكوم للإنشاء"),
            Triple("راية", "RAYA", "راية القابضة"),
            Triple("باكين", "PACH", "باكين للبويات"),
            Triple("أكت فاينانشال", "ACTF", "أكت فاينانشال"),
            Triple("المصرف المتحد", "UBEE", "المصرف المتحد")
        )

        for (item in map) {
            if (title.contains(item.first)) {
                return Pair(item.third, item.second)
            }
        }

        return Pair("البورصة المصرية", "EGX")
    }

    fun getCuratedCorporateActions(): List<CorporateNews> {
        return listOf(
            // 1. الاكتتابات والطروحات
            CorporateNews(
                id = "ipo_united_bank",
                title = "طرح المصرف المتحد في البورصة المصرية (IPO) واستقبال طلبات الاكتتاب",
                companyName = "المصرف المتحد (United Bank)",
                symbol = "UBEE",
                category = CorporateCategory.IPO,
                date = "طرح جاري 🎯",
                summary = "طرح ما يصل إلى 30% من أسهم المصرف المتحد في البورصة المصرية للمؤسسات والأفراد ضمن خطة توسيع قاعدة الملكية المصرفية.",
                fullDetails = "أعلنت البورصة المصرية والبنك المركزي المصري عن موعد فتح باب الاكتتاب العام والخاص لشريحة من أسهم المصرف المتحد. ويشمل الطرح شريحة للأفراد وأخرى للمؤسسات عبر آلية بناء سجل الأوامر (Book Building).",
                status = "جاري تنفيذ الطرح 🎯",
                source = "معلومات مباشر والرقابة المالية",
                impact = NewsImpact.BULLISH
            ),
            CorporateNews(
                id = "ipo_act_financial",
                title = "نجاح اكتتاب أكت فاينانشال وتغطية الطرح العام 55 مرة في البورصة",
                companyName = "أكت فاينانشال للاستشارات",
                symbol = "ACTF",
                category = CorporateCategory.IPO,
                date = "مؤخراً 🟢",
                summary = "إتمام الاكتتاب في أسهم زيادة رأس مال شركة أكت فاينانشال بنجاح قياسي وتغطية الشريحة العامة بأكثر من 54 مرة.",
                fullDetails = "شهد الاكتتاب العام لشركة أكت فاينانشال إقبالاً تاريخياً من المستثمرين في مصر، وجمعت الشركة أكثر من مليار جنيه لتمويل خطط الاستثمار المباشر.",
                status = "تم الطرح وبدء التداول 🟢",
                source = "البورصة المصرية",
                impact = NewsImpact.BULLISH
            ),
            CorporateNews(
                id = "ipo_gov_program",
                title = "برنامج الطروحات الحكومية: تجهيز 4 شركات استراتيجية للطرح بالبورصة",
                companyName = "برنامج الطروحات (وطنية - صافي - محطات الرياح)",
                symbol = "EGX",
                category = CorporateCategory.IPO,
                date = "قيد الإعداد 🟡",
                summary = "مصر تعتزم طرح حصص من شركات وطنية ومحطات توليد الكهرباء والرياح بجبل الزيت أمام مستثمري البورصة والتحالفات الاستراتيجية.",
                fullDetails = "أكدت اللجنة الوزارية لإدارة الأصول العامة ومستشاري الطروحات الانتهاء من الفحص النافي للجهالة لشركتي وطنية للمنتجات البترولية ومحطات طاقة الرياح تمهيداً للإدراج.",
                status = "قيد الإعداد والاعتماد 🟡",
                source = "معلومات مباشر / مجلس الوزراء",
                impact = NewsImpact.BULLISH
            ),

            // 2. الاندماج والاستحواذ
            CorporateNews(
                id = "mna_tmgh_hotels",
                title = "طلعت مصطفى القابضة تستحوذ على 51% من شركة الفنادق التاريخية (Legacy Hotels)",
                companyName = "مجموعة طلعت مصطفى القابضة",
                symbol = "TMGH",
                category = CorporateCategory.MERGER_ACQUISITION,
                date = "صفقة كبرى 🏨",
                summary = "إتمام صفقة الاستحواذ التاريخية على فنادق تاريخية تضم مينا هاوس، سوفيتيل الجزيرة، سيسيل، وسان ستيفانو بقيمة تتجاوز 800 مليون دولار.",
                fullDetails = "قامت الذراع الاستثمارية لمجموعة طلعت مصطفى (أيكون) بالاستحواذ الفعلي على حصة الأغلبية والإدارة لـ 7 فنادق تاريخية مصرية، وتتوقع المجموعة مضاعفة الإيرادات الدولارية.",
                status = "مكتمل وجاري التشغيل 🏨",
                source = "إفصاح رسمي - معلومات مباشر",
                impact = NewsImpact.BULLISH
            ),
            CorporateNews(
                id = "mna_swdy_energy",
                title = "السويدي إليكتريك توسع استحواذاتها في محطات الطاقة والمحولات بالخليج وأفريقيا",
                companyName = "السويدي إليكتريك",
                symbol = "SWDY",
                category = CorporateCategory.MERGER_ACQUISITION,
                date = "نشط ⚡",
                summary = "الشركة تبرم صفقات استحواذ على مصانع كابلات ومحولات في السعودية وتوقع عقود ربط كهربائي بمليارات الجنيهات.",
                fullDetails = "أعلنت السويدي إليكتريك عن استكمال الاستحواذ على شركات متخصصة في خطوط نقل الطاقة الذكية والمحولات في منطقة الشرق الأوسط لتعزيز مكانتها كمورد عالمي للبنية التحتية.",
                status = "معتمد وساري ⚡",
                source = "Investing.com والبورصة",
                impact = NewsImpact.BULLISH
            ),

            // 3. زيادة رأس المال
            CorporateNews(
                id = "cap_beltone_10b",
                title = "بلتون القابضة (BTFH) تكمل أضخم زيادة رأس مال في تاريخ البورصة بـ 10 مليارات جنيه",
                companyName = "بلتون المالية القابضة",
                symbol = "BTFH",
                category = CorporateCategory.CAPITAL_INCREASE,
                date = "معتمد 📈",
                summary = "نجاح تغطية زيادة رأس المال بنسبة 100% وتحول الشركة للتوسع في التمويل العقاري والتمويل الاستهلاكي ورأس المال المخاطر.",
                fullDetails = "اعتمدت الهيئة العامة للرقابة المالية زيادة رأس المال المصدر والمدفوع لشركة بلتون من 926 مليون جنيه إلى 10.9 مليار جنيه عبر إصدار 5 مليارات سهم لقدامى المساهمين.",
                status = "مسجل بالكامل في البورصة 📈",
                source = "معلومات مباشر والرقابة المالية",
                impact = NewsImpact.BULLISH
            ),
            CorporateNews(
                id = "cap_fawry_digital",
                title = "فوري تقر زيادة رأس المال لتمويل إطلاق البنك الرقمي والتوسع في التمويل متناهي الصغر",
                companyName = "فوري للمدفوعات الإلكترونية",
                symbol = "FWRY",
                category = CorporateCategory.CAPITAL_INCREASE,
                date = "جاري التنفيذ 📱",
                summary = "الجمعية العمومية توافق على زيادة رأس المال المرخص به إلى 5 مليارات جنيه لدعم رخصة البنك الرقمي الجديدة.",
                fullDetails = "وافقت الجمعية العامة غير العادية لشركة فوري على زيادة رأس المال لتمويل متطلبات البنك المركزي للحصول على رخصة بنك رقمي متكامل وتطوير البنية التحتية.",
                status = "موافقات الجهات الرقابية 📱",
                source = "معلومات مباشر",
                impact = NewsImpact.BULLISH
            )
        )
    }
}
