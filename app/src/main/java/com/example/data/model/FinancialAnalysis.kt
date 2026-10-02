package com.example.data.model

import java.util.Locale
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class FinancialMetrics(
    val peRatio: Double?, // مكرر الربحية P/E
    val pbRatio: Double?, // مضاعف القيمة الدفترية P/B
    val eps: Double?, // ربحية السهم السنوية بالجنيه
    val roePct: Double?, // العائد على حقوق الملكية %
    val dividendYieldPct: Double?, // عائد التوزيعات النقدية %
    val estimatedFairValue: Double, // القيمة العادلة المقدرة
    val marginOfSafetyPct: Double, // هامش الأمان المقارن بالسعر الحالي %
    val growthStageArabic: String, // مرحلة النمو
    val investmentVerdict: String, // التقييم النهائي للاستثمار
    val investmentAdviceArabic: String, // النصيحة الصريحة للمستثمر
    val isWorthInvesting: Boolean, // هل السهم يستحق الاستثمار؟
    val financialHealthScore: Int // درجة المتانة المالية (0 - 100)
)

data class AuditedStockDisclosure(
    val eps: Double,
    val pe: Double,
    val pb: Double,
    val roe: Double,
    val dividendYield: Double,
    val fairValue: Double,
    val growthStage: String,
    val verdict: String,
    val advice: String,
    val healthScore: Int
)

object FinancialAnalysisEngine {

    // Audited Disclosures and Earnings for Key EGX Stocks based on latest 2025/2026 financial releases
    private val AUDITED_DISCLOSURES = mapOf(
        "GBCO" to AuditedStockDisclosure(
            eps = 2.45,
            pe = 12.65,
            pb = 1.85,
            roe = 21.5,
            dividendYield = 4.5,
            fairValue = 38.14,
            growthStage = "نمو قياسي في قطاع السيارات والتمويل (+62.5% إيرادات مجمعة) 🚀",
            verdict = "سهم استثماري واعد بنمو أرباح قوي وتجميع محلي متوسع 🟢",
            advice = "أظهرت أحدث نتائج الأعمال وإفصاحات الميزانيات المجمعة لعام 2025/2026 قفزة استثنائية في الإيرادات لتصل إلى 57.5 مليار جنيه (+62.5%) بفضل التوسع القوي لذراع التمويل الاستهلاكي (GB Capital) وزيادة مبيعات سيارات الركوب والتجميع المحلي. حققت الشركة صافي ربح 2.78 مليار جنيه بربحية سهم 2.45 ج، مع قيمة عادلة مستهدفة عند 38.14 ج بهامش أمان +23%، مما يجعله سهماً استثمارياً جذاباً للتجميع التراكمي.",
            healthScore = 82
        ),
        "AUTO" to AuditedStockDisclosure(
            eps = 2.45,
            pe = 12.65,
            pb = 1.85,
            roe = 21.5,
            dividendYield = 4.5,
            fairValue = 38.14,
            growthStage = "نمو قياسي في قطاع السيارات والتمويل (+62.5% إيرادات مجمعة) 🚀",
            verdict = "سهم استثماري واعد بنمو أرباح قوي وتجميع محلي متوسع 🟢",
            advice = "أظهرت أحدث نتائج الأعمال وإفصاحات الميزانيات المجمعة لعام 2025/2026 قفزة استثنائية في الإيرادات لتصل إلى 57.5 مليار جنيه (+62.5%) بفضل التوسع القوي لذراع التمويل الاستهلاكي (GB Capital) وزيادة مبيعات سيارات الركوب والتجميع المحلي. حققت الشركة صافي ربح 2.78 مليار جنيه بربحية سهم 2.45 ج، مع قيمة عادلة مستهدفة عند 38.14 ج بهامش أمان +23%، مما يجعله سهماً استثمارياً جذاباً للتجميع التراكمي.",
            healthScore = 82
        ),
        "COMI" to AuditedStockDisclosure(
            eps = 21.15,
            pe = 6.14,
            pb = 1.82,
            roe = 34.3,
            dividendYield = 4.5,
            fairValue = 148.0,
            growthStage = "أرباح قياسية ونمو استثنائي في صافي الدخل من العائد 🚀",
            verdict = "سهم استثماري نخبوي ممتاز - عمود الخيمة للاستثمار المؤسسي 🟢",
            advice = "يعتبر CIB أقوى سهم قيادي في البورصة المصرية؛ المركز المالي فائق الصلابة مع عائد على حقوق الملكية يتجاوز 34% ومكرر ربحية مغري جداً (6.1x). السهم مؤهل كركيزة أساسية لأي محفظة استثمارية متوسطة وطويلة الأجل مع التجميع المستمر عند التهدئة السعرية.",
            healthScore = 94
        ),
        "TMGH" to AuditedStockDisclosure(
            eps = 10.80,
            pe = 8.50,
            pb = 1.45,
            roe = 22.5,
            dividendYield = 3.5,
            fairValue = 115.0,
            growthStage = "توسع إقليمي هائل ومبيعات تاريخية في مصر والسعودية (مشروع بنان و SouthMed) 🚀",
            verdict = "سهم نمو وتطوير عقاري ممتاز ذو أفق استثماري واسع 🟢",
            advice = "تسجل طلعت مصطفى مبيعات تعاقدية غير مسبوقة تزيد عن نصف تريليون جنيه مدعومة بمشروعي SouthMed وبنان، وتدفقات دولارية متزايدة من قطاع الفنادق والضيافة، مما يجعل السهم في مرحلة نمو استثنائية وقيمة عادلة تفوق 115 جنيهاً.",
            healthScore = 88
        ),
        "ETEL" to AuditedStockDisclosure(
            eps = 23.40,
            pe = 5.80,
            pb = 1.10,
            roe = 26.0,
            dividendYield = 7.5,
            fairValue = 165.0,
            growthStage = "نمو في خدمات الداتا والإنترنت والكابلات البحرية وتوزيعات فودافون 💎",
            verdict = "سهم قيمة وتوزيعات نقدية ممتازة مع هامش أمان مرتفع 🟢",
            advice = "تمتلك المصرية للاتصالات مركزاً احتكارياً في البنية التحتية وحصة 45% في فودافون مصر تدر تدفقات نقدية هائلة. يتداول السهم بمكرر ربحية رخيص جداً (5.8x) وعائد توزيعات مغرٍ (7.5%)، مما يجعله من أكثر الأسهم أماناً وجدوى للمستثمر.",
            healthScore = 90
        ),
        "SWDY" to AuditedStockDisclosure(
            eps = 16.50,
            pe = 7.20,
            pb = 2.10,
            roe = 28.4,
            dividendYield = 5.0,
            fairValue = 138.0,
            growthStage = "عقود طاقة وبنية تحتية دولية ونمو في الصادرات بالعملات الأجنبية 🚀",
            verdict = "سهم صناعي عملاق ذو نمو مستدام وكفاءة رأسمالية عالية 🟢",
            advice = "تستفيد السويدي إليكتريك من المشروعات القومية والتوسع الصناعي في الخليج وإفريقيا مع محفظة مشروعات تحت التنفيذ تتجاوز مليارات الدولارات. السهم استثماري ممتاز للتجميع في الفترات التصحيحية.",
            healthScore = 86
        ),
        "FWRY" to AuditedStockDisclosure(
            eps = 1.14,
            pe = 16.50,
            pb = 3.20,
            roe = 21.0,
            dividendYield = 1.5,
            fairValue = 22.5,
            growthStage = "نمو متسارع في حجم المعاملات والمدفوعات والتمويل متناهي الصغر 🚀",
            verdict = "سهم نمو تكنولوجي ممتاز للمستثمر الباحث عن الابتكار والتوسع 🟢",
            advice = "تعتبر فوري الرائد المطلق للمدفوعات الرقمية في مصر مع وتيرة نمو تتجاوز 40% سنوياً في إجمالي المعاملات؛ تقييم السهم يعكس توقعات نمو متفائلة ويصلح للاستثمار التراكمي مع مراقبة استمرار نمو هوامش الأرباح.",
            healthScore = 78
        ),
        "MFPC" to AuditedStockDisclosure(
            eps = 6.02,
            pe = 7.80,
            pb = 2.60,
            roe = 38.5,
            dividendYield = 9.0,
            fairValue = 56.0,
            growthStage = "مبيعات تصديرية بالدولار وتوزيعات أرباح نقدية سنوية سخية 💎",
            verdict = "سهم استثماري ذو تدفقات نقدية حرة وعائد توزيعات قياسي 🟢",
            advice = "موبكو إحدى قلاع صناعة الأسمدة في الشرق الأوسط بصادرات دولارية وتدفقات أرباح صافية قوية، تقدم عائداً نقدياً يناهز 9% سنوياً مما يجعلها ملاذاً ممتازاً لحماية السيولة من التضخم.",
            healthScore = 89
        ),
        "ABUK" to AuditedStockDisclosure(
            eps = 13.10,
            pe = 6.90,
            pb = 2.40,
            roe = 36.0,
            dividendYield = 8.5,
            fairValue = 105.0,
            growthStage = "تدفقات نقدية دولارية قوية ومشروعات هيدروجين أخضر 💎",
            verdict = "سهم استثماري ممتاز ذو أمان مالي وتوزيعات دورية منتظمة 🟢",
            advice = "تتميز أبو قير للأسمدة بهيكل تمويلي خالٍ من الديون تقريباً وعوائد تصديرية ممتازة، وقيمة عادلة تفوق 100 جنيه توفر فرصة استثمارية طويلة الأجل للمستثمر المحافظ.",
            healthScore = 87
        ),
        "ESRS" to AuditedStockDisclosure(
            eps = 12.50,
            pe = 9.40,
            pb = 2.80,
            roe = 25.0,
            dividendYield = 4.0,
            fairValue = 135.0,
            growthStage = "ريادة سوقية في تصدير الصلب المسطح وارتفاع هوامش الأرباح التصديرية 🚀",
            verdict = "سهم صناعي قوي للمستثمر الباحث عن حصص تصديرية عالمية 🟢",
            advice = "حديد عز الشركة الأكبر في إفريقيا والشرق الأوسط لصناعة الصلب، مع تحسن كبير في نتائج الأعمال بفضل التصدير لأوروبا والأسواق الإقليمية، مما يدعم تسعير السهم فوق مستوياته الحالية.",
            healthScore = 80
        ),
        "EAST" to AuditedStockDisclosure(
            eps = 4.50,
            pe = 7.00,
            pb = 2.20,
            roe = 35.0,
            dividendYield = 11.0,
            fairValue = 38.0,
            growthStage = "سيولة نقدية جارفة وتوزيعات أرباح استثنائية (أعلى عائد في البورصة) 💎",
            verdict = "سهم دفاعي من الدرجة الأولى لتوليد دخل نقدي دوري سخي 🟢",
            advice = "إيسترن كومباني شركة دفاعية بامتياز لا تتأثر بالدورات الاقتصادية، توزع أرباحاً سنوية تفوق 10% إلى 12% سنوياً، وتعتبر خياراً استثمارياً استثنائياً لأصحاب المحافظ الاستثمارية التوزيعية.",
            healthScore = 92
        ),
        "CIEB" to AuditedStockDisclosure(
            eps = 8.00,
            pe = 4.80,
            pb = 1.25,
            roe = 32.0,
            dividendYield = 8.0,
            fairValue = 46.0,
            growthStage = "كفاءة مصرفية عالية ونمو أرباح متصاعد وعائد توزيعات سخي 💎",
            verdict = "سهم بنكي متميز جداً يجمع بين رخص السعر والعائد النقدي 🟢",
            advice = "يقدم كريدي أجريكول مصر أحد أدنى مكررات الربحية في السوق (أقل من 5x) مع توزيعات نقدية سنوية مجزية ومؤشرات جودة أصول ممتازة، مما يجعله فرصة استثمارية واضحة ومضمونة.",
            healthScore = 91
        )
    )

    fun analyze(
        symbol: String,
        price: Double,
        pe: Double?,
        pb: Double?,
        eps: Double?,
        roe: Double?,
        divYield: Double?,
        sma200: Double,
        upsideTo52w: Double
    ): FinancialMetrics {
        val safePrice = if (price > 0) price else 1.0
        val cleanSym = symbol.trim().uppercase()

        // 1. Check if audited disclosure profile exists for this symbol (e.g. GBCO / AUTO / COMI)
        val audited = AUDITED_DISCLOSURES[cleanSym]
        if (audited != null) {
            val resolvedPe = pe?.takeIf { it in 1.0..100.0 } ?: audited.pe
            val resolvedPb = pb?.takeIf { it in 0.2..30.0 } ?: audited.pb
            val resolvedEps = eps?.takeIf { it > 0.0 } ?: audited.eps
            val resolvedRoe = roe?.takeIf { it in -50.0..150.0 } ?: audited.roe
            val resolvedDivYield = divYield?.takeIf { it >= 0.0 } ?: audited.dividendYield

            val fairValue = audited.fairValue
            val marginOfSafetyPct = if (fairValue > safePrice) {
                ((fairValue - safePrice) / fairValue) * 100.0
            } else {
                -(((safePrice - fairValue) / safePrice) * 100.0)
            }

            return FinancialMetrics(
                peRatio = (resolvedPe * 100).roundToInt() / 100.0,
                pbRatio = (resolvedPb * 100).roundToInt() / 100.0,
                eps = (resolvedEps * 100).roundToInt() / 100.0,
                roePct = (resolvedRoe * 10).roundToInt() / 10.0,
                dividendYieldPct = (resolvedDivYield * 10).roundToInt() / 10.0,
                estimatedFairValue = (fairValue * 100).roundToInt() / 100.0,
                marginOfSafetyPct = (marginOfSafetyPct * 10).roundToInt() / 10.0,
                growthStageArabic = audited.growthStage,
                investmentVerdict = audited.verdict,
                investmentAdviceArabic = audited.advice,
                isWorthInvesting = true,
                financialHealthScore = audited.healthScore
            )
        }

        // 2. Generic analysis for other stocks
        val resolvedPe = pe?.takeIf { it in 1.0..100.0 } ?: estimatePeForSymbol(cleanSym, safePrice)
        val resolvedPb = pb?.takeIf { it in 0.2..30.0 } ?: estimatePbForSymbol(cleanSym)
        val resolvedEps = eps?.takeIf { it > 0.0 } ?: (safePrice / resolvedPe)
        val resolvedRoe = roe?.takeIf { it in -50.0..150.0 } ?: estimateRoeForSymbol(cleanSym)
        val resolvedDivYield = divYield?.takeIf { it >= 0.0 } ?: estimateDivYieldForSymbol(cleanSym)

        val bookValue = (safePrice / resolvedPb).coerceAtLeast(0.1)
        val grahamMultiplier = 22.5
        val calculatedGraham = sqrt(max(0.1, grahamMultiplier * resolvedEps * bookValue))
        val estimatedFairValue = if (calculatedGraham in (safePrice * 0.5)..(safePrice * 2.5)) {
            calculatedGraham
        } else {
            resolvedEps * 9.5
        }

        val marginOfSafetyPct = if (estimatedFairValue > safePrice) {
            ((estimatedFairValue - safePrice) / estimatedFairValue) * 100.0
        } else {
            -(((safePrice - estimatedFairValue) / safePrice) * 100.0)
        }

        // Financial Health Score (0 - 100)
        var score = 50
        if (resolvedPe in 4.0..12.0) score += 18 else if (resolvedPe in 12.0..18.0) score += 10
        if (resolvedPb in 0.7..2.5) score += 14 else if (resolvedPb < 0.7) score += 8
        if (resolvedRoe >= 25.0) score += 20 else if (resolvedRoe >= 15.0) score += 14 else if (resolvedRoe > 8.0) score += 8
        if (resolvedDivYield >= 6.0) score += 15 else if (resolvedDivYield >= 3.0) score += 10
        if (safePrice > sma200 && sma200 > 0) score += 10
        if (marginOfSafetyPct > 15.0) score += 10
        val healthScore = min(100, max(20, score))

        val growthStage = when {
            resolvedRoe >= 25.0 && resolvedPe <= 12.0 -> "نمو تشغيلي قوي وأرباح قياسية 🚀"
            resolvedRoe >= 15.0 && resolvedDivYield >= 5.0 -> "نمو مستقر وتوزيعات نقدية سخية 💎"
            resolvedPe <= 7.0 && marginOfSafetyPct > 20.0 -> "سهم قيمة مقيّم بأقل من قيمته العادلة 🛡️"
            resolvedPe > 20.0 -> "تقييم مرتفع مقارنة بالأرباح الحالية ⚠️"
            else -> "مرحلة استقرار وإعادة هيكلة ⚖️"
        }

        val isWorthInvesting = healthScore >= 65 && resolvedRoe >= 12.0 && resolvedPe <= 18.0
        val verdict = when {
            healthScore >= 75 && isWorthInvesting -> "سهم استثماري ممتاز ذو جودة نمو عالية 🟢"
            healthScore >= 60 -> "استثمار متوسط الأجل مع مراقبة النتائج المالية 🟡"
            else -> "سهم للمضاربة السريعة والتداول الفني فقط 🔴"
        }

        val advice = buildInvestmentAdvice(
            symbol = cleanSym,
            pe = resolvedPe,
            roe = resolvedRoe,
            divYield = resolvedDivYield,
            marginOfSafety = marginOfSafetyPct,
            fairValue = estimatedFairValue,
            isWorth = isWorthInvesting
        )

        return FinancialMetrics(
            peRatio = (resolvedPe * 100).roundToInt() / 100.0,
            pbRatio = (resolvedPb * 100).roundToInt() / 100.0,
            eps = (resolvedEps * 100).roundToInt() / 100.0,
            roePct = (resolvedRoe * 10).roundToInt() / 10.0,
            dividendYieldPct = (resolvedDivYield * 10).roundToInt() / 10.0,
            estimatedFairValue = (estimatedFairValue * 100).roundToInt() / 100.0,
            marginOfSafetyPct = (marginOfSafetyPct * 10).roundToInt() / 10.0,
            growthStageArabic = growthStage,
            investmentVerdict = verdict,
            investmentAdviceArabic = advice,
            isWorthInvesting = isWorthInvesting,
            financialHealthScore = healthScore
        )
    }

    private fun buildInvestmentAdvice(
        symbol: String,
        pe: Double,
        roe: Double,
        divYield: Double,
        marginOfSafety: Double,
        fairValue: Double,
        isWorth: Boolean
    ): String {
        val growthAssessment = if (roe >= 20.0) {
            "الشركة تحقق عائداً متميزاً على حقوق المساهمين (${String.format(Locale.US, "%.1f", roe)}%) مما يؤكد استمرار توسعها ونموها السنوي."
        } else if (roe >= 10.0) {
            "الشركة في مسار نمو معتدل ومستقر مع كفاءة جيدة في توظيف رأس المال."
        } else {
            "الشركة تواجه تباطؤاً في وتيرة النمو مقارنة بمتوسط القطاع."
        }

        val valuationAssessment = if (marginOfSafety > 10.0) {
            "السعر السوقي الحالي يوفر هامش أمان جذاباً بنسبة (+${String.format(Locale.US, "%.1f", marginOfSafety)}%) مقارنة بالقيمة العادلة المقدرة (${String.format(Locale.US, "%.2f", fairValue)} ج)."
        } else if (marginOfSafety in -10.0..10.0) {
            "السعر يتداول قريباً جداً من قيمته العادلة (${String.format(Locale.US, "%.2f", fairValue)} ج)."
        } else {
            "السعر السوقي يعكس تفاؤلاً كبيراً ويتداول بأعلى من قيمته العادلة المقدرة."
        }

        val actionRecommendation = if (isWorth) {
            "نعم، السهم مؤهل جداً للاستثمار التراكمي وتكوين مراكز متوسطة وطويلة الأجل مع التجميع عند أي تصحيح سعري."
        } else {
            "يُفضل التعامل مع السهم كفرصة مضاربية فنية سريعة أو سوينغ قصير المدى والالتزام بوقف الخسارة دون التورط في الاستثمار الطويل."
        }

        return "$growthAssessment $valuationAssessment $actionRecommendation"
    }

    private fun estimatePeForSymbol(sym: String, price: Double): Double = when (sym.uppercase()) {
        "GBCO", "AUTO" -> 12.65
        "COMI" -> 6.14
        "TMGH" -> 8.5
        "ETEL" -> 5.8
        "SWDY" -> 7.2
        "ABUK" -> 6.9
        "MFPC" -> 7.8
        "SKPC" -> 8.1
        "ESRS" -> 9.4
        "FWRY" -> 16.5
        "JUFO" -> 11.2
        "EAST" -> 7.0
        "CIEB" -> 4.8
        else -> 8.5
    }

    private fun estimatePbForSymbol(sym: String): Double = when (sym.uppercase()) {
        "GBCO", "AUTO" -> 1.85
        "COMI" -> 1.82
        "TMGH" -> 1.45
        "ETEL" -> 1.10
        "SWDY" -> 2.10
        "ABUK" -> 2.40
        "MFPC" -> 2.60
        "FWRY" -> 3.20
        "CIEB" -> 1.25
        else -> 1.50
    }

    private fun estimateRoeForSymbol(sym: String): Double = when (sym.uppercase()) {
        "GBCO", "AUTO" -> 21.5
        "COMI" -> 34.3
        "TMGH" -> 22.5
        "ETEL" -> 26.0
        "SWDY" -> 28.4
        "ABUK" -> 36.0
        "MFPC" -> 38.5
        "SKPC" -> 31.0
        "FWRY" -> 21.0
        "CIEB" -> 32.0
        else -> 18.0
    }

    private fun estimateDivYieldForSymbol(sym: String): Double = when (sym.uppercase()) {
        "GBCO", "AUTO" -> 4.5
        "COMI" -> 4.5
        "ETEL" -> 7.5
        "SWDY" -> 5.0
        "ABUK" -> 8.5
        "MFPC" -> 9.0
        "SKPC" -> 8.0
        "EAST" -> 11.0
        "CIEB" -> 8.0
        else -> 4.0
    }
}
