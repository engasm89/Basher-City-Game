package com.example.opencity.model

enum class ClothingBrand(
    val arabicName: String,
    val badgeText: String,
    val accentColorHex: Long
) {
    CLASSIC("أساسي", "CLASSIC", 0xFF64748B),
    NIKE("نايكي", "NIKE ✓", 0xFFFF6B00),
    ADIDAS("أديداس", "ADIDAS ☰", 0xFF2563EB),
    JORDAN("جوردان", "JORDAN 🏀", 0xFFE11D48),
    PUMA("بوما", "PUMA 🐆", 0xFF16A34A)
}

enum class ClothingSlot(
    val arabicName: String,
    val emoji: String
) {
    TOP("تيشيرت وهودي", "👕"),
    PANTS("بنطلون وشورت", "👖"),
    SHOES("أحذية وسنيكرز", "👟"),
    ACCESSORY("قبعة ونظارات", "🧢")
}

enum class AccessoryKind {
    NONE,
    CAP,
    BEANIE,
    SUNGLASSES,
    GOLD_WATCH
}

data class ClothingItem(
    val id: String,
    val arabicName: String,
    val brand: ClothingBrand,
    val slot: ClothingSlot,
    val price: Int,
    val primaryColor: FloatArray,
    val accentColor: FloatArray,
    val swatchColorHex: Long,
    val accentSwatchHex: Long,
    val descriptionArabic: String,
    val emoji: String,
    val hasLongSleeves: Boolean = false,
    val hasSideStripes: Boolean = false,
    val isShorts: Boolean = false,
    val accessoryKind: AccessoryKind = AccessoryKind.NONE
)

data class PlayerOutfit(
    val top: ClothingItem,
    val pants: ClothingItem,
    val shoes: ClothingItem,
    val accessory: ClothingItem
)

object ClothingCatalog {

    const val DEFAULT_TOP_ID = "top_robot_cyber_jacket"
    const val ROBLOX_AMBITION_TOP_ID = "top_roblox_ambition_hoodie"
    const val DEFAULT_PANTS_ID = "pants_classic_green"
    const val DEFAULT_SHOES_ID = "shoes_classic_white"
    const val DEFAULT_ACCESSORY_ID = "acc_none"

    val defaultOwnedIds: Set<String> = setOf(
        DEFAULT_TOP_ID,
        ROBLOX_AMBITION_TOP_ID,
        "top_classic_blue",
        DEFAULT_PANTS_ID,
        DEFAULT_SHOES_ID,
        DEFAULT_ACCESSORY_ID
    )

    val allItems: List<ClothingItem> = listOf(
        // ==================== TOPS / SHIRTS / HOODIES ====================
        ClothingItem(
            id = DEFAULT_TOP_ID,
            arabicName = "صُرّة وسترة الروبوت السيبرانية 🤖🧥",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.TOP,
            price = 0,
            primaryColor = floatArrayOf(0.09f, 0.14f, 0.24f, 1f),
            accentColor = floatArrayOf(0.05f, 0.95f, 1.0f, 1f),
            swatchColorHex = 0xFF17243D,
            accentSwatchHex = 0xFF0DF2FF,
            descriptionArabic = "سترة الروبوت السيبرانية الرسمية التي يرتديها روبوتات المدينة، جاهزة ومفتوحة لك مجاناً منذ أول دخول!",
            emoji = "🤖",
            hasLongSleeves = true,
            hasSideStripes = true
        ),
        ClothingItem(
            id = ROBLOX_AMBITION_TOP_ID,
            arabicName = "هودي طموح بلا حدود الأزرق (R)",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.TOP,
            price = 0,
            primaryColor = floatArrayOf(0.08f, 0.36f, 0.88f, 1f),
            accentColor = floatArrayOf(0.95f, 0.16f, 0.20f, 1f),
            swatchColorHex = 0xFF145CE0,
            accentSwatchHex = 0xFFF22933,
            descriptionArabic = "هودي أزرق مع قميص أبيض داخلي وسلسلة ذهبية وشعار R الأحمر والأبيض",
            emoji = "✨",
            hasLongSleeves = true
        ),
        ClothingItem(
            id = "top_classic_blue",
            arabicName = "تيشيرت أزرق أساسي",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.TOP,
            price = 0,
            primaryColor = floatArrayOf(0.02f, 0.38f, 0.88f, 1f),
            accentColor = floatArrayOf(0.85f, 0.92f, 1f, 1f),
            swatchColorHex = 0xFF0561E0,
            accentSwatchHex = 0xFFD9EBFF,
            descriptionArabic = "التيشيرت الافتراضي الأزرق المريح",
            emoji = "👕",
            hasLongSleeves = false
        ),
        ClothingItem(
            id = "top_nike_black_hoodie",
            arabicName = "هودي نايكي أسود Nike Swoosh",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.TOP,
            price = 120,
            primaryColor = floatArrayOf(0.10f, 0.11f, 0.14f, 1f),
            accentColor = floatArrayOf(1.0f, 0.42f, 0.0f, 1f),
            swatchColorHex = 0xFF191C24,
            accentSwatchHex = 0xFFFF6B00,
            descriptionArabic = "هودي رياضي أسود بشعار نايكي البرتقالي المضيء",
            emoji = "🧥",
            hasLongSleeves = true
        ),
        ClothingItem(
            id = "top_nike_red_air",
            arabicName = "تيشيرت نايكي إير أحمر Nike Air",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.TOP,
            price = 95,
            primaryColor = floatArrayOf(0.86f, 0.12f, 0.16f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.98f, 1f),
            swatchColorHex = 0xFFDC1E29,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "تيشيرت رياضي أحمر بعلامة Nike Air البيضاء",
            emoji = "👕",
            hasLongSleeves = false
        ),
        ClothingItem(
            id = "top_nike_tech_grey",
            arabicName = "جاكيت نايكي تيك فليس رمادي",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.TOP,
            price = 180,
            primaryColor = floatArrayOf(0.48f, 0.52f, 0.58f, 1f),
            accentColor = floatArrayOf(0.10f, 0.11f, 0.13f, 1f),
            swatchColorHex = 0xFF7A8594,
            accentSwatchHex = 0xFF1A1C21,
            descriptionArabic = "جاكيت نايكي تيك فليس العصري بسحاب أمامي",
            emoji = "🧥",
            hasLongSleeves = true
        ),
        ClothingItem(
            id = "top_adidas_blue_3stripes",
            arabicName = "جاكيت أديداس أزرق 3 خطوط",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.TOP,
            price = 140,
            primaryColor = floatArrayOf(0.08f, 0.34f, 0.82f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.99f, 1f),
            swatchColorHex = 0xFF1457D1,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "جاكيت Adidas Originals بالخطوط الثلاثة الشهيرة على الأكمام",
            emoji = "🧥",
            hasLongSleeves = true,
            hasSideStripes = true
        ),
        ClothingItem(
            id = "top_adidas_black_track",
            arabicName = "بدلة وتوب أديداس أسود ذهبي",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.TOP,
            price = 160,
            primaryColor = floatArrayOf(0.08f, 0.09f, 0.11f, 1f),
            accentColor = floatArrayOf(0.96f, 0.78f, 0.15f, 1f),
            swatchColorHex = 0xFF14171C,
            accentSwatchHex = 0xFFF5C726,
            descriptionArabic = "توب أديداس أسود مع 3 خطوط ذهبية فاخرة",
            emoji = "🧥",
            hasLongSleeves = true,
            hasSideStripes = true
        ),
        ClothingItem(
            id = "top_adidas_white_tee",
            arabicName = "تيشيرت أديداس أبيض كلاسيك",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.TOP,
            price = 80,
            primaryColor = floatArrayOf(0.94f, 0.95f, 0.97f, 1f),
            accentColor = floatArrayOf(0.08f, 0.10f, 0.14f, 1f),
            swatchColorHex = 0xFFF0F2F7,
            accentSwatchHex = 0xFF141A24,
            descriptionArabic = "تيشيرت قطني أبيض بشعار أديداس الأسود",
            emoji = "👕",
            hasLongSleeves = false,
            hasSideStripes = true
        ),
        ClothingItem(
            id = "top_jordan_crimson_hoodie",
            arabicName = "هودي جوردان أحمر وأسود",
            brand = ClothingBrand.JORDAN,
            slot = ClothingSlot.TOP,
            price = 210,
            primaryColor = floatArrayOf(0.78f, 0.08f, 0.18f, 1f),
            accentColor = floatArrayOf(0.08f, 0.08f, 0.10f, 1f),
            swatchColorHex = 0xFFC7142E,
            accentSwatchHex = 0xFF14141A,
            descriptionArabic = "هودي جوردان الرياضي بألوان البطولات",
            emoji = "🏀",
            hasLongSleeves = true
        ),
        ClothingItem(
            id = "top_puma_emerald_jacket",
            arabicName = "جاكيت بوما أخضر رياضي",
            brand = ClothingBrand.PUMA,
            slot = ClothingSlot.TOP,
            price = 135,
            primaryColor = floatArrayOf(0.06f, 0.52f, 0.32f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.98f, 1f),
            swatchColorHex = 0xFF0F8552,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "جاكيت بوما السريع للقيادة والسباقات",
            emoji = "🐆",
            hasLongSleeves = true
        ),

        // ==================== PANTS / JEANS / SHORTS ====================
        ClothingItem(
            id = DEFAULT_PANTS_ID,
            arabicName = "بنطلون أخضر أساسي",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.PANTS,
            price = 0,
            primaryColor = floatArrayOf(0.10f, 0.72f, 0.18f, 1f),
            accentColor = floatArrayOf(0.08f, 0.52f, 0.12f, 1f),
            swatchColorHex = 0xFF1AB82E,
            accentSwatchHex = 0xFF14851F,
            descriptionArabic = "البنطلون الأساسي الكلاسيكي",
            emoji = "👖"
        ),
        ClothingItem(
            id = "pants_nike_tech_black",
            arabicName = "بنطلون نايكي تيك أسود",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.PANTS,
            price = 110,
            primaryColor = floatArrayOf(0.11f, 0.12f, 0.15f, 1f),
            accentColor = floatArrayOf(1.0f, 0.45f, 0.05f, 1f),
            swatchColorHex = 0xFF1C1F26,
            accentSwatchHex = 0xFFFF730D,
            descriptionArabic = "بنطلون نايكي رياضي مريح بجيوب سحاب جانبية",
            emoji = "👖",
            hasSideStripes = true
        ),
        ClothingItem(
            id = "pants_nike_grey_jogger",
            arabicName = "بنطلون نايكي جوجرز رمادي",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.PANTS,
            price = 95,
            primaryColor = floatArrayOf(0.55f, 0.58f, 0.62f, 1f),
            accentColor = floatArrayOf(0.95f, 0.95f, 0.96f, 1f),
            swatchColorHex = 0xFF8C949E,
            accentSwatchHex = 0xFFF2F2F5,
            descriptionArabic = "بنطلون قطني ناعم للمشي والجري",
            emoji = "👖"
        ),
        ClothingItem(
            id = "pants_nike_shorts_navy",
            arabicName = "شورت نايكي كحلي صيفي",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.PANTS,
            price = 65,
            primaryColor = floatArrayOf(0.08f, 0.18f, 0.40f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.99f, 1f),
            swatchColorHex = 0xFF142E66,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "شورت رياضي خفيف للبحر والمسبح والجري",
            emoji = "🩳",
            isShorts = true
        ),
        ClothingItem(
            id = "pants_adidas_black_3stripes",
            arabicName = "بنطلون أديداس 3 خطوط أبيض",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.PANTS,
            price = 115,
            primaryColor = floatArrayOf(0.08f, 0.09f, 0.12f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.99f, 1f),
            swatchColorHex = 0xFF14171F,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "بنطلون Adidas الكلاسيكي مع 3 خطوط بيضاء على الجانبين",
            emoji = "👖",
            hasSideStripes = true
        ),
        ClothingItem(
            id = "pants_adidas_navy_track",
            arabicName = "بنطلون أديداس كحلي مخطط",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.PANTS,
            price = 100,
            primaryColor = floatArrayOf(0.08f, 0.22f, 0.48f, 1f),
            accentColor = floatArrayOf(0.35f, 0.80f, 1.0f, 1f),
            swatchColorHex = 0xFF14387A,
            accentSwatchHex = 0xFF59CCFF,
            descriptionArabic = "بنطلون رياضي كحلي بخطوط سماوية جانبية",
            emoji = "👖",
            hasSideStripes = true
        ),
        ClothingItem(
            id = "pants_classic_denim_jeans",
            arabicName = "بنطلون جينز أزرق كلاسيك",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.PANTS,
            price = 75,
            primaryColor = floatArrayOf(0.14f, 0.30f, 0.56f, 1f),
            accentColor = floatArrayOf(0.85f, 0.65f, 0.22f, 1f),
            swatchColorHex = 0xFF244D8F,
            accentSwatchHex = 0xFFD9A638,
            descriptionArabic = "بنطلون جينز يومي أنيق يناسب كل القمصان",
            emoji = "👖"
        ),
        ClothingItem(
            id = "pants_cargo_beige",
            arabicName = "بنطلون كارجو بيج بجيوب",
            brand = ClothingBrand.PUMA,
            slot = ClothingSlot.PANTS,
            price = 85,
            primaryColor = floatArrayOf(0.74f, 0.64f, 0.46f, 1f),
            accentColor = floatArrayOf(0.30f, 0.24f, 0.16f, 1f),
            swatchColorHex = 0xFFBFA375,
            accentSwatchHex = 0xFF4D3D29,
            descriptionArabic = "بنطلون كارجو عملي بجيوب جانبية كبيرة",
            emoji = "👖",
            hasSideStripes = true
        ),

        // ==================== SHOES & SNEAKERS ====================
        ClothingItem(
            id = DEFAULT_SHOES_ID,
            arabicName = "حذاء رياضي أبيض أساسي",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.SHOES,
            price = 0,
            primaryColor = floatArrayOf(0.92f, 0.93f, 0.95f, 1f),
            accentColor = floatArrayOf(0.20f, 0.24f, 0.30f, 1f),
            swatchColorHex = 0xFFEBEDF2,
            accentSwatchHex = 0xFF333D4D,
            descriptionArabic = "حذاء يومي أبيض خفيف",
            emoji = "👟"
        ),
        ClothingItem(
            id = "shoes_nike_air_red",
            arabicName = "حذاء نايكي إير ماكس أحمر/أبيض",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.SHOES,
            price = 150,
            primaryColor = floatArrayOf(0.88f, 0.12f, 0.18f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.99f, 1f),
            swatchColorHex = 0xFFE01F2E,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "سنيكرز Nike Air Max بوسادة هوائية ونعل أبيض",
            emoji = "👟"
        ),
        ClothingItem(
            id = "shoes_nike_volt_neon",
            arabicName = "حذاء نايكي فولت نيون",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.SHOES,
            price = 140,
            primaryColor = floatArrayOf(0.65f, 0.95f, 0.12f, 1f),
            accentColor = floatArrayOf(0.10f, 0.10f, 0.12f, 1f),
            swatchColorHex = 0xFFA6F21F,
            accentSwatchHex = 0xFF1A1A1F,
            descriptionArabic = "حذاء جري نايكي بلون نيون ساطع",
            emoji = "👟"
        ),
        ClothingItem(
            id = "shoes_adidas_superstar",
            arabicName = "حذاء أديداس سوبرستار",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.SHOES,
            price = 135,
            primaryColor = floatArrayOf(0.97f, 0.97f, 0.98f, 1f),
            accentColor = floatArrayOf(0.08f, 0.08f, 0.10f, 1f),
            swatchColorHex = 0xFFF7F7FA,
            accentSwatchHex = 0xFF14141A,
            descriptionArabic = "الأيقونة البيضاء الشهيرة بثلاثة خطوط سوداء",
            emoji = "👟"
        ),
        ClothingItem(
            id = "shoes_adidas_ultraboost_blue",
            arabicName = "حذاء أديداس ألترابوست أزرق",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.SHOES,
            price = 175,
            primaryColor = floatArrayOf(0.10f, 0.45f, 0.92f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.99f, 1f),
            swatchColorHex = 0xFF1A73EB,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "حذاء Ultraboost مريح فائق السرعة",
            emoji = "👟"
        ),
        ClothingItem(
            id = "shoes_jordan_retro_gold",
            arabicName = "حذاء جوردان ريترو أسود وذهبي",
            brand = ClothingBrand.JORDAN,
            slot = ClothingSlot.SHOES,
            price = 220,
            primaryColor = floatArrayOf(0.10f, 0.10f, 0.12f, 1f),
            accentColor = floatArrayOf(0.98f, 0.78f, 0.12f, 1f),
            swatchColorHex = 0xFF1A1A1F,
            accentSwatchHex = 0xFFFAC71F,
            descriptionArabic = "سنيكرز جوردان هاي توب فاخر بحواف ذهبية",
            emoji = "👟"
        ),

        // ==================== CAPS / GLASSES / WATCHES ====================
        ClothingItem(
            id = DEFAULT_ACCESSORY_ID,
            arabicName = "بدون إكسسوار",
            brand = ClothingBrand.CLASSIC,
            slot = ClothingSlot.ACCESSORY,
            price = 0,
            primaryColor = floatArrayOf(0.5f, 0.5f, 0.5f, 1f),
            accentColor = floatArrayOf(0.5f, 0.5f, 0.5f, 1f),
            swatchColorHex = 0xFF64748B,
            accentSwatchHex = 0xFF94A3B8,
            descriptionArabic = "المظهر الطبيعي بدون قبعة أو نظارات",
            emoji = "🙂",
            accessoryKind = AccessoryKind.NONE
        ),
        ClothingItem(
            id = "acc_nike_cap_black",
            arabicName = "كاب نايكي أسود Nike",
            brand = ClothingBrand.NIKE,
            slot = ClothingSlot.ACCESSORY,
            price = 55,
            primaryColor = floatArrayOf(0.09f, 0.10f, 0.12f, 1f),
            accentColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f),
            swatchColorHex = 0xFF171A1F,
            accentSwatchHex = 0xFFFF7300,
            descriptionArabic = "قبعة رياضية سوداء بشعار نايكي برتقالي",
            emoji = "🧢",
            accessoryKind = AccessoryKind.CAP
        ),
        ClothingItem(
            id = "acc_adidas_cap_blue",
            arabicName = "كاب أديداس أزرق 3 خطوط",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.ACCESSORY,
            price = 60,
            primaryColor = floatArrayOf(0.08f, 0.36f, 0.86f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.99f, 1f),
            swatchColorHex = 0xFF145CDB,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "كاب أديداس أزرق رياضي أنيق",
            emoji = "🧢",
            accessoryKind = AccessoryKind.CAP
        ),
        ClothingItem(
            id = "acc_jordan_beanie_red",
            arabicName = "طوق / قبعة جوردان حمراء",
            brand = ClothingBrand.JORDAN,
            slot = ClothingSlot.ACCESSORY,
            price = 70,
            primaryColor = floatArrayOf(0.84f, 0.10f, 0.18f, 1f),
            accentColor = floatArrayOf(0.98f, 0.98f, 0.98f, 1f),
            swatchColorHex = 0xFFD61A2E,
            accentSwatchHex = 0xFFFFFFFF,
            descriptionArabic = "قبعة جوردان شتوية أنيقة باللون الأحمر",
            emoji = "🧶",
            accessoryKind = AccessoryKind.BEANIE
        ),
        ClothingItem(
            id = "acc_sports_sunglasses",
            arabicName = "نظارات شمسية رياضية",
            brand = ClothingBrand.PUMA,
            slot = ClothingSlot.ACCESSORY,
            price = 85,
            primaryColor = floatArrayOf(0.06f, 0.07f, 0.10f, 1f),
            accentColor = floatArrayOf(0.0f, 0.85f, 1.0f, 1f),
            swatchColorHex = 0xFF0F121A,
            accentSwatchHex = 0xFF00D9FF,
            descriptionArabic = "نظارات قيادة وشمس عاكسة أنيقة",
            emoji = "🕶️",
            accessoryKind = AccessoryKind.SUNGLASSES
        ),
        ClothingItem(
            id = "acc_gold_watch",
            arabicName = "ساعة يد رياضية ذهبية",
            brand = ClothingBrand.ADIDAS,
            slot = ClothingSlot.ACCESSORY,
            price = 125,
            primaryColor = floatArrayOf(0.96f, 0.78f, 0.18f, 1f),
            accentColor = floatArrayOf(0.10f, 0.12f, 0.16f, 1f),
            swatchColorHex = 0xFFF5C72E,
            accentSwatchHex = 0xFF1A1F29,
            descriptionArabic = "ساعة يد ذهبية فاخرة تلمع على معصم شخصيتك",
            emoji = "⌚",
            accessoryKind = AccessoryKind.GOLD_WATCH
        )
    )

    fun findById(id: String): ClothingItem? = allItems.firstOrNull { it.id == id }

    fun resolveOutfit(
        topId: String,
        pantsId: String,
        shoesId: String,
        accessoryId: String
    ): PlayerOutfit {
        val top = findById(topId)?.takeIf { it.slot == ClothingSlot.TOP }
            ?: findById(DEFAULT_TOP_ID)
            ?: allItems.first { it.slot == ClothingSlot.TOP }
        val pants = findById(pantsId)?.takeIf { it.slot == ClothingSlot.PANTS }
            ?: findById(DEFAULT_PANTS_ID)
            ?: allItems.first { it.slot == ClothingSlot.PANTS }
        val shoes = findById(shoesId)?.takeIf { it.slot == ClothingSlot.SHOES }
            ?: findById(DEFAULT_SHOES_ID)
            ?: allItems.first { it.slot == ClothingSlot.SHOES }
        val acc = findById(accessoryId)?.takeIf { it.slot == ClothingSlot.ACCESSORY }
            ?: findById(DEFAULT_ACCESSORY_ID)
            ?: allItems.first { it.slot == ClothingSlot.ACCESSORY }
        return PlayerOutfit(top = top, pants = pants, shoes = shoes, accessory = acc)
    }
}
