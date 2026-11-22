package com.example.opencity.model

/**
 * Tiers of cars in «مدينة المحركات» (Motor City) dealership as requested:
 * 1. Weak & old cars starting at $200
 * 2. Mid-range cars up to $1,000,000
 * 3. Rare cars starting at $6,000,000
 * 4. Exclusive cars at $8,000,000
 * 5. Super cars at $9,000,000
 * 6. Rarest cars at $10,000,000
 */
enum class CarTier(
    val arabicName: String,
    val badgeEmoji: String,
    val priceDescription: String
) {
    WEAK_OLD("سيارات قديمة مسجلة (كلاسيك)", "🚗", "تبدأ من 200$"),
    MID_RANGE("سيارات جديدة عصرية (Orange)", "🚙", "تصل إلى 1,000,000$"),
    RARE("سيارات جديدة نادرة (Orange)", "🏎️", "تبدأ من 6,000,000$"),
    EXCLUSIVE("سيارات جديدة حصرية (Orange)", "💎", "8,000,000$"),
    SUPER("سيارات جديدة فائقة (Orange)", "⚡", "9,000,000$"),
    ULTIMATE_RARE("أندر السيارات الجديدة (Orange)", "👑", "10,000,000$")
}

enum class CarMeshStyle {
    VINTAGE_OLD,      // Rounded classic beetle / boxy vintage
    SEDAN_MODERN,     // Sleek modern sedan
    SUV_OFFROAD,      // High ride 4x4 rugged truck
    MUSCLE_GT,        // Long hood muscle coupe
    SUPER_SPORT,      // Low wedge mid-engine supercar
    HYPER_AERO,       // Extreme wings, diffusers, roof scoop hypercar
    ULTIMATE_STEALTH, // Carbon-fiber ultra aerodynamic jet design
    ROBLOX_GAMER_BLOX // Authentic Roblox blocky supercar with glowing blue LEDs and neon underglow
}

data class DealershipCar(
    val id: String,
    val nameArabic: String,
    val nameEnglish: String,
    val tier: CarTier,
    val price: Int, // In-game currency ($)
    val topSpeedKmH: Float,
    val accelRate: Float, // Higher = faster acceleration
    val brakeRate: Float, // Higher = stops faster
    val horsePower: Int,
    val engineType: String,
    val meshStyle: CarMeshStyle,
    val primaryColor: FloatArray,   // RGBA
    val secondaryColor: FloatArray, // RGBA
    val soundPitch: Float, // 0.6f (deep rumble) to 1.6f (screaming V12)
    val descriptionArabic: String,
    // Showroom coordinates for interactive display
    val displayPosX: Float,
    val displayPosZ: Float,
    val displayYaw: Float = 0f,
    val hasNitro: Boolean = false,
    val seatCount: Int = 2,
    val underglowColor: FloatArray? = null
) {
    // 0 to 100 normalized scores for UI radar/stat bars
    val speedScore: Int
        get() = ((topSpeedKmH / 480f) * 100).toInt().coerceIn(10, 100)

    val accelScore: Int
        get() = ((accelRate / 95f) * 100).toInt().coerceIn(15, 100)

    val brakeScore: Int
        get() = ((brakeRate / 120f) * 100).toInt().coerceIn(20, 100)
}

object MotorCityData {

    val CATALOG: List<DealershipCar> = listOf(
        // === سيارة الروبوتات المجانية (مصفوفة بالشارع مجاناً منذ بداية اللعبة) ===
        DealershipCar(
            id = "car_robot_cyber_free",
            nameArabic = "سيارة الروبوتات السايبر (ببلاش 🤖)",
            nameEnglish = "Cyber Robot Street Edition [FREE]",
            tier = CarTier.WEAK_OLD,
            price = 0,
            topSpeedKmH = 295f,
            accelRate = 70f,
            brakeRate = 92f,
            horsePower = 780,
            engineType = "محرك روبوت كهربائي سايبر 0$",
            meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
            primaryColor = floatArrayOf(0.08f, 0.70f, 0.98f, 1f),
            secondaryColor = floatArrayOf(0.10f, 0.12f, 0.16f, 1f),
            soundPitch = 1.18f,
            descriptionArabic = "نفس سيارة الروبوتات السريعة المصفوفة بجانبك في الشارع مجاناً منذ أول لحظة تدخل فيها اللعبة!",
            displayPosX = 286f,
            displayPosZ = 72f,
            displayYaw = 155f,
            hasNitro = true,
            seatCount = 2,
            underglowColor = floatArrayOf(0.05f, 0.95f, 1.0f, 1f)
        ),

        // === الفئة 1: سيارات ضعيفة وقديمة (تبدأ من 200$) ===
        DealershipCar(
            id = "car_beetle_1974",
            nameArabic = "فولكس فاجن بيتل 1974",
            nameEnglish = "Classic Beetle 1974",
            tier = CarTier.WEAK_OLD,
            price = 200,
            topSpeedKmH = 85f,
            accelRate = 18f,
            brakeRate = 40f,
            horsePower = 50,
            engineType = "4 سلندر مبرد بالهواء 1.3L",
            meshStyle = CarMeshStyle.VINTAGE_OLD,
            primaryColor = floatArrayOf(0.85f, 0.75f, 0.55f, 1f), // Vintage sand beige
            secondaryColor = floatArrayOf(0.95f, 0.95f, 0.95f, 1f),
            soundPitch = 0.72f,
            descriptionArabic = "سيارة تاريخية كلاسيكية اقتصادية بمحرك خلفي بسيط، سهلة الصيانة وتعتبر بداية متواضعة لكنها مليئة بالذكريات.",
            displayPosX = 294f,
            displayPosZ = 72f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_lada_riva",
            nameArabic = "لادا ريفا كلاسيك 1985",
            nameEnglish = "Lada Riva 1500",
            tier = CarTier.WEAK_OLD,
            price = 650,
            topSpeedKmH = 100f,
            accelRate = 22f,
            brakeRate = 45f,
            horsePower = 75,
            engineType = "4 سلندر متتالي 1.5L",
            meshStyle = CarMeshStyle.VINTAGE_OLD,
            primaryColor = floatArrayOf(0.2f, 0.35f, 0.55f, 1f), // Dull blue
            secondaryColor = floatArrayOf(0.25f, 0.25f, 0.25f, 1f),
            soundPitch = 0.78f,
            descriptionArabic = "سيارة صلبة من الحقبة القديمة تتحمل الطرق الصعبة وتمنحك قيادة ميكانيكية خالصة بدون تعقيدات إلكترونية.",
            displayPosX = 304f,
            displayPosZ = 72f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_civic_dx",
            nameArabic = "هوندا سيفيك هاتشباك 1992",
            nameEnglish = "Civic DX Hatchback",
            tier = CarTier.WEAK_OLD,
            price = 1500,
            topSpeedKmH = 130f,
            accelRate = 28f,
            brakeRate = 50f,
            horsePower = 102,
            engineType = "1.5L VTEC 16V",
            meshStyle = CarMeshStyle.VINTAGE_OLD,
            primaryColor = floatArrayOf(0.85f, 0.25f, 0.2f, 1f), // Cherry red
            secondaryColor = floatArrayOf(0.15f, 0.15f, 0.15f, 1f),
            soundPitch = 0.88f,
            descriptionArabic = "سيارة هاتشباك خفيفة ومحبوبة، اقتصادية في استهلاك الوقود ولها حضور مميز في شوارع المدينة.",
            displayPosX = 314f,
            displayPosZ = 72f,
            displayYaw = 155f
        ),

        // === الفئة 2: سيارات متوسطة (تصل إلى 1,000,000$) ===
        DealershipCar(
            id = "car_camry_sport",
            nameArabic = "تويوتا كامري سبورت V6",
            nameEnglish = "Camry Sport V6",
            tier = CarTier.MID_RANGE,
            price = 45000,
            topSpeedKmH = 210f,
            accelRate = 42f,
            brakeRate = 65f,
            horsePower = 301,
            engineType = "3.5L V6 DOHC",
            meshStyle = CarMeshStyle.SEDAN_MODERN,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.1f, 0.1f, 0.1f, 1f),
            soundPitch = 0.95f,
            descriptionArabic = "سيدان عائلية رياضية بلون برتقالي مشرق، تجمع بين الراحة الفائقة والاعتمادية وقوة محرك V6 ناعم وقوي.",
            displayPosX = 290f,
            displayPosZ = 84f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_landcruiser_gr",
            nameArabic = "تويوتا لاند كروزر GR سبورت",
            nameEnglish = "Land Cruiser GR Sport",
            tier = CarTier.MID_RANGE,
            price = 140000,
            topSpeedKmH = 220f,
            accelRate = 46f,
            brakeRate = 68f,
            horsePower = 409,
            engineType = "3.5L توين توربو V6",
            meshStyle = CarMeshStyle.SUV_OFFROAD,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.12f, 0.12f, 0.12f, 1f), // Black trim
            soundPitch = 0.85f,
            descriptionArabic = "وحش الطرق الوعرة والشوارع السريعة بلون برتقالي جذاب، نظام دفع رباعي متطور وهيكل مرتفع وقوي يجوب كل التضاريس.",
            displayPosX = 300f,
            displayPosZ = 84f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_mustang_darkhorse",
            nameArabic = "فورد موستانج دارك هورس V8",
            nameEnglish = "Mustang Dark Horse",
            tier = CarTier.MID_RANGE,
            price = 320000,
            topSpeedKmH = 270f,
            accelRate = 56f,
            brakeRate = 78f,
            horsePower = 500,
            engineType = "5.0L Coyote V8 تنفس طبيعي",
            meshStyle = CarMeshStyle.MUSCLE_GT,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.1f, 0.1f, 0.1f, 1f),
            soundPitch = 0.82f,
            descriptionArabic = "سيارة عضلات أمريكية بلون برتقالي ناري وهدير محرك V8 مخيف، تسارع مذهل على الخطوط المستقيمة وجناح هوائي لتعزيز الثبات.",
            displayPosX = 310f,
            displayPosZ = 84f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_maybach_s680",
            nameArabic = "مرسيدس مايباخ S680 V12",
            nameEnglish = "Mercedes-Maybach S680",
            tier = CarTier.MID_RANGE,
            price = 1000000,
            topSpeedKmH = 280f,
            accelRate = 60f,
            brakeRate = 82f,
            horsePower = 621,
            engineType = "6.0L BiTurbo V12",
            meshStyle = CarMeshStyle.SEDAN_MODERN,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.12f, 0.12f, 0.12f, 1f),
            soundPitch = 0.92f,
            descriptionArabic = "قمة الفخامة والرفاهية لرجال الأعمال؛ محرك 12 أسطوانة فائق الهدوء وعزل صوتي لا مثيل له مع طلاء برتقالي كربوني.",
            displayPosX = 320f,
            displayPosZ = 84f,
            displayYaw = 155f
        ),

        // === الفئة 3: سيارات نادرة (تبدأ من 6,000,000$) ===
        DealershipCar(
            id = "car_gtr_nismo",
            nameArabic = "نيسان جي تي آر نيسمو R35",
            nameEnglish = "GT-R Nismo R35",
            tier = CarTier.RARE,
            price = 6000000,
            topSpeedKmH = 330f,
            accelRate = 72f,
            brakeRate = 92f,
            horsePower = 600,
            engineType = "3.8L VR38DETT توين توربو",
            meshStyle = CarMeshStyle.SUPER_SPORT,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.10f, 0.10f, 0.12f, 1f),
            soundPitch = 1.15f,
            descriptionArabic = "الوحش الياباني جودزيلا بنسخة برتقالية نيسمو مخصصة للحلبات، انطلاقة صاروخية بنظام دفع كلي ودقة مذهلة بالمنعطفات.",
            displayPosX = 294f,
            displayPosZ = 96f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_porsche_gt3rs",
            nameArabic = "بورشه 911 جي تي 3 آر إس",
            nameEnglish = "Porsche 911 GT3 RS",
            tier = CarTier.RARE,
            price = 6800000,
            topSpeedKmH = 320f,
            accelRate = 76f,
            brakeRate = 96f,
            horsePower = 518,
            engineType = "4.0L بوكسر 6 سلندر 9000 RPM",
            meshStyle = CarMeshStyle.SUPER_SPORT,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.10f, 0.10f, 0.12f, 1f),
            soundPitch = 1.35f,
            descriptionArabic = "أعجوبة الهندسة الألمانية بلون برتقالي مميز؛ جناح خلفي ضخم بنظام DRS وصوت محرك يصرخ حتى 9000 دورة.",
            displayPosX = 304f,
            displayPosZ = 96f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_ferrari_f40",
            nameArabic = "فيراري F40 الأيقونية 1987",
            nameEnglish = "Ferrari F40 Legend",
            tier = CarTier.RARE,
            price = 7500000,
            topSpeedKmH = 340f,
            accelRate = 78f,
            brakeRate = 94f,
            horsePower = 471,
            engineType = "2.9L توين توربو V8 كلاسيك",
            meshStyle = CarMeshStyle.SUPER_SPORT,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.08f, 0.08f, 0.08f, 1f),
            soundPitch = 1.25f,
            descriptionArabic = "تحفة كربونية نادرة بلون برتقالي رياضي مذهل وخفيفة الوزن بصوت محرك توربيني عاصف.",
            displayPosX = 314f,
            displayPosZ = 96f,
            displayYaw = 155f
        ),

        // === الفئة 4: سيارات حصرية (8,000,000$) ===
        DealershipCar(
            id = "car_gamer_blox_r1",
            nameArabic = "جيمر بلوكس آر-1 (Gamer Blox R-1)",
            nameEnglish = "Gamer Blox R-1 [Roblox Edition]",
            tier = CarTier.EXCLUSIVE,
            price = 8000000,
            topSpeedKmH = 320f,
            accelRate = 78f, // 0 - 100 km/h during ~3 sec
            brakeRate = 96f,
            horsePower = 880,
            engineType = "Blox Racing Twin-Turbo V10 + نيترو N2O بلهب أزرق",
            meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange Blox
            secondaryColor = floatArrayOf(0.10f, 0.10f, 0.12f, 1f), // Black racing carbon
            soundPitch = 1.25f,
            descriptionArabic = "سيارة سباق رياضية ثلاثية الأبعاد بتصميم روبلوكس بلوكي (Low-Poly)! هيكل برتقالي رياضي مع تفاصيل سباق سوداء، مصابيح أمامية LED زرقاء ساطعة، إضاءة نيون زرقاء أسفل السيارة، عجلات سوداء بحواف زرقاء متوهجة، 4 مقاعد للاعبين، جناح خلفي كبير، نظام نيترو مع لهب أزرق، ودرفت سلس للغاية.",
            displayPosX = 314f,
            displayPosZ = 108f,
            displayYaw = 155f,
            hasNitro = true,
            seatCount = 4,
            underglowColor = floatArrayOf(0.08f, 0.85f, 1.0f, 1.0f) // Electric Blue Neon
        ),
        DealershipCar(
            id = "car_bugatti_centodieci",
            nameArabic = "بوغاتي سينتوديتشي الحصرية",
            nameEnglish = "Bugatti Centodieci",
            tier = CarTier.EXCLUSIVE,
            price = 8000000,
            topSpeedKmH = 390f,
            accelRate = 84f,
            brakeRate = 104f,
            horsePower = 1577,
            engineType = "8.0L W16 رباعي التوربو Quad-Turbo",
            meshStyle = CarMeshStyle.HYPER_AERO,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.12f, 0.12f, 0.15f, 1f), // Matte Carbon
            soundPitch = 1.22f,
            descriptionArabic = "إصدار حصري بلون برتقالي وقوة 1600 حصان بتصميم تكريمي للأسطورة EB110.",
            displayPosX = 294f,
            displayPosZ = 108f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_venom_f5",
            nameArabic = "هينيسي فينوم F5 ريفولوشن",
            nameEnglish = "Hennessey Venom F5",
            tier = CarTier.EXCLUSIVE,
            price = 8000000,
            topSpeedKmH = 430f,
            accelRate = 88f,
            brakeRate = 106f,
            horsePower = 1817,
            engineType = "6.6L توين توربو V8 'Fury'",
            meshStyle = CarMeshStyle.HYPER_AERO,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.08f, 0.08f, 0.08f, 1f), // Exposed Carbon
            soundPitch = 1.30f,
            descriptionArabic = "صاروخ أمريكي برتقالي مخصص لكسر حاجز 500 كم/س بمحرك 'فيوري' خارق بقدرة تفوق 1800 حصان.",
            displayPosX = 304f,
            displayPosZ = 108f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_new_phantom_x8",
            nameArabic = "السيارة الجديدة فانتوم X8 الحصرية 🚀",
            nameEnglish = "Phantom X8 New Edition [8M]",
            tier = CarTier.EXCLUSIVE,
            price = 8000000,
            topSpeedKmH = 445f,
            accelRate = 91f,
            brakeRate = 112f,
            horsePower = 1920,
            engineType = "8.2L Hyperturbo V12 + نيترو أزرق",
            meshStyle = CarMeshStyle.HYPER_AERO,
            primaryColor = floatArrayOf(0.06f, 0.72f, 0.98f, 1f),
            secondaryColor = floatArrayOf(0.09f, 0.10f, 0.14f, 1f),
            soundPitch = 1.42f,
            descriptionArabic = "أحدث سيارة جديدة حصرية بسعر 8,000,000$! تصميم هوائي فائق السرعة مع إضاءة نيون زرقاء وتسارع مذهل.",
            displayPosX = 322f,
            displayPosZ = 108f,
            displayYaw = 155f,
            hasNitro = true,
            seatCount = 2,
            underglowColor = floatArrayOf(0.0f, 0.90f, 1.0f, 1f)
        ),

        // === الفئة 5: سيارات فائقة (9,000,000$) ===
        DealershipCar(
            id = "car_chiron_supersport",
            nameArabic = "بوغاتي شيرون سوبر سبورت 300+",
            nameEnglish = "Chiron Super Sport 300+",
            tier = CarTier.SUPER,
            price = 9000000,
            topSpeedKmH = 450f,
            accelRate = 92f,
            brakeRate = 110f,
            horsePower = 1600,
            engineType = "8.0L W16 كواد توربو ممتد الذيل",
            meshStyle = CarMeshStyle.HYPER_AERO,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.12f, 0.12f, 0.14f, 1f), // Carbon Black
            soundPitch = 1.32f,
            descriptionArabic = "السيارة الخارقة بلون برتقالي ناري التي تخطت حاجز 300 ميل/ساعة بجسم انسيابي ممتد Longtail وتوازن هوائي استثنائي.",
            displayPosX = 290f,
            displayPosZ = 120f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_valkyrie_amr",
            nameArabic = "أستون مارتن فالكيري AMR برو",
            nameEnglish = "Aston Martin Valkyrie AMR",
            tier = CarTier.SUPER,
            price = 9000000,
            topSpeedKmH = 415f,
            accelRate = 94f,
            brakeRate = 115f,
            horsePower = 1000,
            engineType = "6.5L كوزورث V12 تنفس طبيعي 11,100 RPM",
            meshStyle = CarMeshStyle.HYPER_AERO,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.12f, 0.12f, 0.12f, 1f),
            soundPitch = 1.55f,
            descriptionArabic = "سيارة سباق لومان بلون برتقالي؛ محرك V12 يصرخ فوق 11,000 دورة وقوة ضغط سفلية تلتصق بالأرض.",
            displayPosX = 300f,
            displayPosZ = 120f,
            displayYaw = 155f
        ),

        // === الفئة 6: أندر السيارات (10,000,000$) ===
        DealershipCar(
            id = "car_jesko_absolut",
            nameArabic = "كوينيجسيج جيسكو أبسولوت 500",
            nameEnglish = "Jesko Absolut 500",
            tier = CarTier.ULTIMATE_RARE,
            price = 10000000,
            topSpeedKmH = 480f,
            accelRate = 96f,
            brakeRate = 120f,
            horsePower = 1600,
            engineType = "5.0L توين توربو V8 بناقل LST ذي 9 سرعات",
            meshStyle = CarMeshStyle.ULTIMATE_STEALTH,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.10f, 0.10f, 0.12f, 1f), // Carbon Black
            soundPitch = 1.45f,
            descriptionArabic = "أسرع سيارة صنعتها كوينيجسيج بلون برتقالي بركاني، معامل جر هوائي منخفض وسرعة تتجاوز 500 كم/س.",
            displayPosX = 310f,
            displayPosZ = 120f,
            displayYaw = 155f
        ),
        DealershipCar(
            id = "car_la_voiture_noire",
            nameArabic = "بوغاتي لافواتور نوار النادرة",
            nameEnglish = "Bugatti La Voiture Noire",
            tier = CarTier.ULTIMATE_RARE,
            price = 10000000,
            topSpeedKmH = 440f,
            accelRate = 95f,
            brakeRate = 118f,
            horsePower = 1500,
            engineType = "8.0L W16 مع 6 مخارج عادم كربونية",
            meshStyle = CarMeshStyle.ULTIMATE_STEALTH,
            primaryColor = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
            secondaryColor = floatArrayOf(0.08f, 0.08f, 0.08f, 1f), // Carbon spine
            soundPitch = 1.38f,
            descriptionArabic = "تحفة فنية برتقالية فريدة من نوعها في تاريخ السيارات بـ 6 مخارج عادم وهيكل منحوت يدوياً بدون أي فواصل.",
            displayPosX = 320f,
            displayPosZ = 120f,
            displayYaw = 155f
        )
    )

    fun getCarById(id: String): DealershipCar? {
        return CATALOG.find { it.id == id }
    }
}
