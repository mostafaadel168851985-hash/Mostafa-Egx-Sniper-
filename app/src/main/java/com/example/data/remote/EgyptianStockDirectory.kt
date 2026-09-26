package com.example.data.remote

object EgyptianStockDirectory {

    data class StockInfo(
        val symbol: String,
        val arabicName: String,
        val sector: String,
        val keywords: List<String> = emptyList()
    )

    val STOCKS: List<StockInfo> = listOf(
        StockInfo("COMI", "البنك التجاري الدولي - مصر (CIB)", "🏦 البنوك", listOf("تجاري", "التجاري", "الدولي", "cib", "comi")),
        StockInfo("TMGH", "مجموعة طلعت مصطفى القابضة", "🏗️ العقارات", listOf("طلعت", "مصطفى", "مصطفي", "هشام", "tmgh")),
        StockInfo("ETEL", "الشركة المصرية للاتصالات (WE)", "📡 الاتصالات والتكنولوجيا", listOf("المصرية للاتصالات", "اتصالات", "we", "وي", "تليكوم", "etel")),
        StockInfo("SWDY", "السويدي إليكتريك", "🏭 البتروكيماويات والصناعات", listOf("السويدي", "سويدي", "إليكتريك", "كابلات", "swdy", "elsewedy")),
        StockInfo("FWRY", "فوري لتكنولوجيا المدفوعات الإلكترونية", "📡 الاتصالات والتكنولوجيا", listOf("فوري", "مدفوعات", "fwry", "fawry")),
        StockInfo("CCAP", "القلعة للاستشارات المالية", "🏗️ العقارات", listOf("القلعة", "قلعة", "استشارات", "ccap", "qalaa")),
        StockInfo("ABUK", "أبو قير للأسمدة والصناعات الكيماوية", "🏭 البتروكيماويات والصناعات", listOf("ابو قير", "أبو قير", "ابوقير", "أبوقير", "اسمدة", "أسمدة", "abuk")),
        StockInfo("MFPC", "مصر لإنتاج الأسمدة (موبكو)", "🏭 البتروكيماويات والصناعات", listOf("موبكو", "مصر لانتاج الاسمدة", "أسمدة", "mfpc", "mopco")),
        StockInfo("SKPC", "سيدي كرير للبتروكيماويات (سيدبك)", "🏭 البتروكيماويات والصناعات", listOf("سيدي كرير", "سيدبك", "بتروكيماويات", "skpc", "sidpec")),
        StockInfo("ESRS", "حديد عز", "🏭 البتروكيماويات والصناعات", listOf("عز", "حديد عز", "صلب", "esrs", "ezz")),
        StockInfo("BTFH", "بلتون المالية القابضة", "📡 الاتصالات والتكنولوجيا", listOf("بلتون", "مالية", "btfh", "beltone")),
        StockInfo("HELI", "مصر الجديدة للإسكان والتعمير", "🏗️ العقارات", listOf("مصر الجديدة", "اسكان", "إسكان", "heli", "heliopolis")),
        StockInfo("PHDC", "بالم هيلز للتعمير", "🏗️ العقارات", listOf("بالم هيلز", "بالم", "هيلز", "phdc", "palm hills")),
        StockInfo("ORAS", "أوراسكوم للإنشاء بي إل سي", "🏗️ العقارات", listOf("اوراسكوم", "أوراسكوم", "إنشاء", "oras", "orascom")),
        StockInfo("JUFO", "جهينة للصناعات الغذائية", "🍔 الأغذية والمشروبات", listOf("جهينة", "البان", "ألبان", "عصير", "jufo", "juhayna")),
        StockInfo("AMOC", "الإسكندرية للزيوت المعدنية (أموك)", "🏭 البتروكيماويات والصناعات", listOf("اموك", "أموك", "زيوت", "بترول", "amoc")),
        StockInfo("CIEB", "بنك كريدي أجريكول مصر", "🏦 البنوك", listOf("كريدي", "كريدي اجريكول", "أجريكول", "cieb")),
        StockInfo("MNHD", "مدينة مصر للإسكان والتعمير (مدينة نصر)", "🏗️ العقارات", listOf("مدينة مصر", "مدينة نصر", "اسكان", "mnhd", "madinet masr")),
        StockInfo("ISPH", "ابن سينا فارما", "💊 الرعاية الصحية والأدوية", listOf("ابن سينا", "أدوية", "فارما", "isph", "ibnsina")),
        StockInfo("EKHO", "القابضة المصرية الكويتية", "🏭 البتروكيماويات والصناعات", listOf("المصرية الكويتية", "كويتية", "قابضة", "ekho")),
        StockInfo("EFIH", "إي فاينانس للاستثمارات المالية", "📡 الاتصالات والتكنولوجيا", listOf("اي فاينانس", "إي فاينانس", "efih", "efinance")),
        StockInfo("ORWE", "النساجون الشرقيون للسجاد", "🍔 الأغذية والمشروبات", listOf("النساجون", "نساجون", "سجاد", "orwe", "oriental")),
        StockInfo("EAST", "الشرقية - إيسترن كومباني", "🍔 الأغذية والمشروبات", listOf("الشرقية للدخان", "إيسترن", "دخان", "سجائر", "east", "eastern")),
        StockInfo("AUTO", "جي بي كورب (غبور أوتو)", "🛒 التجارة والخدمات", listOf("غبور", "جي بي", "سيارات", "auto", "gb corp")),
        StockInfo("DOMT", "الصناعات الغذائية العربية (دومتي)", "🍔 الأغذية والمشروبات", listOf("دومتي", "جبنة", "domt", "domty")),
        StockInfo("CLHO", "مستشفى كليوباترا", "💊 الرعاية الصحية والأدوية", listOf("كليوباترا", "مستشفى", "clho", "cleopatra")),
        StockInfo("EMFD", "إعمار مصر للتنمية", "🏗️ العقارات", listOf("اعمار", "إعمار", "emfd", "emaar")),
        StockInfo("EGTS", "المصرية للمنتجعات السياحية", "🏗️ العقارات", listOf("منتجعات", "سهل حشيش", "سياحية", "egts")),
        StockInfo("RAYA", "راية القابضة للاستثمارات المالية", "📡 الاتصالات والتكنولوجيا", listOf("راية", "تكنولوجيا", "raya")),
        StockInfo("RMDA", "العاشر من رمضان للأدوية (راميدا)", "💊 الرعاية الصحية والأدوية", listOf("راميدا", "أدوية", "رمضان", "rmda", "rameda")),
        StockInfo("SPMD", "سبيد ميديكال", "💊 الرعاية الصحية والأدوية", listOf("سبيد", "ميديكال", "تحاليل", "spmd")),
        StockInfo("EGCH", "الصناعات الكيماوية المصرية (كيما)", "🏭 البتروكيماويات والصناعات", listOf("كيما", "كيماويات", "egch", "kima")),
        StockInfo("CIRA", "القاهرة للخدمات التعليمية (سيرا)", "🛒 التجارة والخدمات", listOf("سيرا", "تعليم", "جامعة بدر", "cira")),
        StockInfo("ADIB", "مصرف أبوظبي الإسلامي - مصر", "🏦 البنوك", listOf("ابوظبي", "أبوظبي الإسلامي", "adib")),
        StockInfo("FAIT", "بنك فيصل الإسلامي المصري", "🏦 البنوك", listOf("فيصل", "فيصل الإسلامي", "fait")),
        StockInfo("QNBA", "بنك قطر الوطني الأهلي (QNB)", "🏦 البنوك", listOf("قطر الوطني", "qnb", "qnba")),
        StockInfo("POUL", "القاهرة للدواجن", "🍔 الأغذية والمشروبات", listOf("دواجن", "فراخ", "poul")),
        StockInfo("ACAMD", "العربية لإدارة وتطوير الأصول", "🏗️ العقارات", listOf("العربية للاصول", "أصول", "acamd")),
        StockInfo("MCQE", "مصر للأسمنت قنا", "🏭 البتروكيماويات والصناعات", listOf("اسمنت قنا", "أسمنت", "mcqe")),
        StockInfo("OCDI", "السادس من أكتوبر للتنمية (سوديك)", "🏗️ العقارات", listOf("سوديك", "اكتوبر", "ocdi", "sodic")),
        StockInfo("EFID", "إيديتا للصناعات الغذائية", "🍔 الأغذية والمشروبات", listOf("ايديتا", "إيديتا", "مولتو", "efid", "edita")),
        StockInfo("OBRI", "العبور للاستثمار العقاري", "🏗️ العقارات", listOf("العبور", "عقاري", "obri")),
        StockInfo("ALEX", "بنك الإسكندرية", "🏦 البنوك", listOf("اسكندرية", "الإسكندرية", "alex")),
        StockInfo("EGAL", "مصر للألومنيوم", "🏭 البتروكيماويات والصناعات", listOf("الومنيوم", "ألومنيوم", "نجع حمادي", "egal")),
        StockInfo("B investments", "بي إنفستمنتس القابضة", "📡 الاتصالات والتكنولوجيا", listOf("بي انفستمنتس", "binv")),
        StockInfo("BINV", "بي إنفستمنتس القابضة", "📡 الاتصالات والتكنولوجيا", listOf("بي انفستمنتس", "binv")),
        StockInfo("PRDC", "رواد السياحة", "🛒 التجارة والخدمات", listOf("رواد", "سياحة", "prdc")),
        StockInfo("MOIL", "الخدمات الملاحية والبترولية (ماريديف)", "🛒 التجارة والخدمات", listOf("ماريديف", "ملاحية", "moil")),
        StockInfo("DSCW", "دايس للملابس الجاهزة", "🛒 التجارة والخدمات", listOf("دايس", "ملابس", "dscw")),
        StockInfo("ORWE", "النساجون الشرقيون", "🍔 الأغذية والمشروبات", listOf("نساجون", "orwe")),
        StockInfo("AIH", "العروبة للتجارة والتعدين", "🏭 البتروكيماويات والصناعات", listOf("عروبة", "تعدين", "aih")),
        StockInfo("ARAB", "المطورون العرب القابضة", "🏗️ العقارات", listOf("المطورون العرب", "بورتو", "arab")),
        StockInfo("ASCM", "أسيك للتعدين (أسكوم)", "🏭 البتروكيماويات والصناعات", listOf("اسكوم", "أسيك", "تعدين", "ascm"))
    )

    val SHARIAH_STOCKS: Set<String> = setOf(
        "TMGH", "SWDY", "FWRY", "ABUK", "MFPC", "SKPC", "ESRS", "ETEL",
        "JUFO", "AMOC", "CCAP", "HELI", "PHDC", "ORWE", "EFID", "ISPH",
        "DOMT", "CLHO", "MNHD", "ORAS", "EMFD", "RAYA", "SPMD", "RMDA",
        "EGCH", "CIRA", "POUL", "ACAMD", "MCQE", "OCDI", "OBRI", "EGAL",
        "DSCW", "AIH", "ARAB", "ASCM"
    )

    val EGX30_SYMBOLS: Set<String> = setOf(
        "COMI", "TMGH", "ETEL", "SWDY", "FWRY", "ABUK", "MFPC", "SKPC",
        "ESRS", "BTFH", "HELI", "PHDC", "ORAS", "JUFO", "AMOC", "CIEB",
        "MNHD", "EKHO", "EFIH", "EAST", "AUTO", "EMFD", "EFID", "EGAL",
        "OCDI", "CLHO", "ORWE", "RAYA", "ADIB", "QNBA"
    )

    fun isShariahCompliant(symbol: String): Boolean {
        return SHARIAH_STOCKS.contains(symbol.uppercase())
    }

    fun getIndexBelonging(symbol: String): String {
        val sym = symbol.uppercase()
        return when {
            EGX30_SYMBOLS.contains(sym) -> "EGX30"
            STOCKS.any { it.symbol.uppercase() == sym } -> "EGX70"
            else -> "EGX100"
        }
    }

    private val SYMBOL_MAP = STOCKS.associateBy { it.symbol.uppercase() }

    fun getArabicName(symbol: String): String {
        return SYMBOL_MAP[symbol.uppercase()]?.arabicName ?: symbol
    }

    fun getSector(symbol: String): String {
        return SYMBOL_MAP[symbol.uppercase()]?.sector ?: "📌 قطاعات أخرى"
    }

    fun normalizeArabic(text: String): String {
        return text.trim()
            .lowercase()
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("ة", "ه")
            .replace("ى", "ي")
            .replace("ؤ", "و")
            .replace("ئ", "ي")
            .replace("ـ", "") // remove tatweel
            .replace("\\s+".toRegex(), " ")
    }

    fun findMatchingSymbol(query: String): String? {
        val qClean = query.trim().uppercase()
        if (SYMBOL_MAP.containsKey(qClean)) return qClean

        val normalizedQ = normalizeArabic(query)
        if (normalizedQ.isBlank()) return null

        // 1. Direct symbol check
        for (stock in STOCKS) {
            if (stock.symbol.equals(qClean, ignoreCase = true)) {
                return stock.symbol
            }
        }

        // 2. Exact keyword match across all stocks first
        for (stock in STOCKS) {
            for (kw in stock.keywords) {
                val normKw = normalizeArabic(kw)
                if (normKw.equals(normalizedQ, ignoreCase = true)) {
                    return stock.symbol
                }
            }
        }

        // 3. Exact Arabic name match or prefix match
        for (stock in STOCKS) {
            val normName = normalizeArabic(stock.arabicName)
            if (normName.equals(normalizedQ, ignoreCase = true) || normName.startsWith(normalizedQ)) {
                return stock.symbol
            }
        }

        // 4. Keyword containment (only for keywords with length >= 3 to avoid false positives like "وي")
        for (stock in STOCKS) {
            for (kw in stock.keywords) {
                val normKw = normalizeArabic(kw)
                if (normKw.length >= 3 && (normalizedQ.contains(normKw) || normKw.contains(normalizedQ))) {
                    return stock.symbol
                }
            }
        }

        // 5. Arabic name contains
        for (stock in STOCKS) {
            val normName = normalizeArabic(stock.arabicName)
            if (normName.contains(normalizedQ)) {
                return stock.symbol
            }
        }

        return null
    }

    val POPULAR_PICKS = listOf(
        "COMI" to "التجاري الدولي",
        "TMGH" to "طلعت مصطفى",
        "FWRY" to "فوري",
        "SWDY" to "السويدي",
        "ETEL" to "المصرية للاتصالات",
        "SKPC" to "سيدي كرير",
        "ABUK" to "أبو قير",
        "CCAP" to "القلعة",
        "ESRS" to "حديد عز",
        "MFPC" to "موبكو",
        "BTFH" to "بلتون",
        "HELI" to "مصر الجديدة",
        "PHDC" to "بالم هيلز",
        "JUFO" to "جهينة",
        "AMOC" to "أموك",
        "MNHD" to "مدينة مصر"
    )
}
