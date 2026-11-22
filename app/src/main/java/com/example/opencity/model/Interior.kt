package com.example.opencity.model

/**
 * Furniture or item inside an enterable building.
 */
data class InteriorProp(
    val name: String,
    val arabicName: String,
    val posX: Float,
    val posY: Float,
    val posZ: Float,
    val width: Float,
    val height: Float,
    val depth: Float,
    val color: FloatArray,
    val isInteractive: Boolean = false,
    val actionText: String = "",
    val price: Int = 0
)

/**
 * Interior representation for an enterable building.
 */
data class BuildingInterior(
    val type: InteriorType,
    val titleArabic: String,
    val descriptionArabic: String,
    val floorColor: FloatArray,
    val wallColor: FloatArray,
    val roomWidth: Float = 14f,
    val roomLength: Float = 14f,
    val roomHeight: Float = 4f,
    val props: List<InteriorProp>,
    val npcGreeting: String = "",
    val npcAvatarEmoji: String = "👤"
) {
    companion object {
        fun getInterior(type: InteriorType): BuildingInterior {
            return when (type) {
                InteriorType.PLAYER_VILLA -> BuildingInterior(
                    type = type,
                    titleArabic = "فيلا النيوكلاسيك الفاخرة (طابقان • غرف نوم • حمامات • مسبح)",
                    descriptionArabic = "فيلا عصرية من طابقين بتصميم متكامل ومحاطة بجدران مصمتة مع 6 شبابيك بانورامية: الطابق الأول يضم الصالة العائلية، المطبخ الأمريكي، مجلس الضيوف، المسبح الداخلي الدافئ، حمام الضيوف، والدرج الفاخر. الطابق الثاني يضم جناح النوم الرئيسي، غرفة النوم الثانية، والحمام الرئيسي بالجاكوزي والشاور.",
                    floorColor = floatArrayOf(0.92f, 0.90f, 0.86f, 1f), // Travertine & marble
                    wallColor = floatArrayOf(0.97f, 0.96f, 0.93f, 1f),
                    roomWidth = 28f,
                    roomLength = 26f,
                    roomHeight = 9.6f,
                    npcGreeting = "أهلاً بك في فيلتك الفاخرة ذات الطابقين! استكشف الغرف، الحمامات، المسبح الداخلي، والشبابيك الستة.",
                    npcAvatarEmoji = "🏛️",
                    props = listOf(
                        InteriorProp("StairsFloor2", "الدرج الملكي (صعود / نزول الطابق 2) 🪜", 11.4f, 2.3f, 7.2f, 3.2f, 4.6f, 8.2f, floatArrayOf(0.92f, 0.90f, 0.86f, 1f), true, "الانتقال بين الطابق الأول والطابق الثاني 🪜"),
                        InteriorProp("Pool", "مسبح الفيلا الداخلي الخاص 🏊", -9.8f, 0.25f, -5.2f, 4.4f, 0.6f, 8.4f, floatArrayOf(0.08f, 0.78f, 0.95f, 1f), true, "السباحة والاستجمام في المسبح الداخلي 🏊"),
                        InteriorProp("BathroomSink", "حمام الضيوف والمغسلة الرخامية (ط1) 🚰", -12.4f, 0.9f, 10.6f, 1.4f, 1.8f, 2.2f, floatArrayOf(0.94f, 0.94f, 0.92f, 1f), true, "استخدام حمام الطابق الأول وغسل اليدين 🧼"),
                        InteriorProp("Bed", "غرفة النوم الرئيسية — سرير كينغ (ط2) 🛏️", -8.5f, 5.2f, -9.5f, 3.2f, 1.2f, 3.6f, floatArrayOf(0.88f, 0.72f, 0.28f, 1f), true, "النوم في الجناح الرئيسي والاستيقاظ صباحاً ☀️"),
                        InteriorProp("SecondBed", "غرفة النوم الثانية — الأطفال والضيوف (ط2) 🛏️", 10.8f, 5.2f, -9.6f, 2.2f, 1.2f, 3.2f, floatArrayOf(0.35f, 0.55f, 0.85f, 1f), true, "الاستراحة في غرفة النوم الثانية 😴"),
                        InteriorProp("Jacuzzi", "الحمام الرئيسي والجاكوزي الدافئ (ط2) 🛁", -9.8f, 5.1f, 9.6f, 3.2f, 1.0f, 3.2f, floatArrayOf(0.08f, 0.80f, 0.95f, 1f), true, "الاسترخاء في الجاكوزي الرخامي الدافئ 🛁"),
                        InteriorProp("Shower", "كابينة الشاور والاستحمام الزجاجية (ط2) 🚿", -11.8f, 5.8f, 3.6f, 2.2f, 2.4f, 2.2f, floatArrayOf(0.65f, 0.88f, 0.98f, 1f), true, "أخذ حمام منعش تحت الدش الساخن 🚿"),
                        InteriorProp("TV", "شاشة 98\" على الجدار الرخامي (ط1) 📺", 0f, 2.0f, -5.8f, 5.2f, 2.8f, 0.3f, floatArrayOf(0.08f, 0.08f, 0.10f, 1f), true, "تشغيل الشاشة العملاقة ومشاهدة السباقات 🏎️"),
                        InteriorProp("Couch", "طقم أريكة الصالة العائلية الفاخر (ط1) 🛋️", 0f, 0.5f, 2.0f, 4.4f, 1.1f, 1.5f, floatArrayOf(0.91f, 0.89f, 0.84f, 1f), true, "الجلوس والاسترخاء في الصالة العائلية"),
                        InteriorProp("KitchenIsland", "جزيرة المطبخ الرخامية (ط1) 🍽️", 6.8f, 0.9f, -6.0f, 2.8f, 1.1f, 4.5f, floatArrayOf(0.94f, 0.94f, 0.92f, 1f), true, "تناول إفطار شهي على جزيرة المطبخ الرخامية"),
                        InteriorProp("Fridge", "الثلاجة المزدوجة والمأكولات (ط1) 🍕", 12.6f, 1.5f, -1.8f, 1.6f, 3.0f, 1.6f, floatArrayOf(0.92f, 0.92f, 0.95f, 1f), true, "فتح الثلاجة وتناول وجبة طازجة"),
                        InteriorProp("Coffee", "صانعة القهوة والكابتشينو (ط1) ☕", 12.4f, 1.05f, -4.0f, 0.8f, 0.8f, 0.8f, floatArrayOf(0.85f, 0.2f, 0.2f, 1f), true, "تحضير وشرب كوب كابتشينو ساخن"),
                        InteriorProp("Majlis", "مجلس الضيوف ودلة القهوة العربية (ط1) ☕", 0f, 0.5f, -11.0f, 3.8f, 0.9f, 1.2f, floatArrayOf(0.88f, 0.72f, 0.28f, 1f), true, "احتساء القهوة العربية الأصيلة مع الضيوف"),
                        InteriorProp("StudyDeskVilla", "مكتب الدراسة والكمبيوتر (ط2) 💻", 3.2f, 5.1f, -4.6f, 2.0f, 1.0f, 1.2f, floatArrayOf(0.30f, 0.25f, 0.20f, 1f), true, "استخدام الكمبيوتر وإنجاز الأعمال والمذاكرة 💻"),
                        InteriorProp("Wardrobe", "غرفة وخزانة الملابس (ط2) 👔", -12.6f, 6.0f, -4.5f, 1.4f, 2.8f, 3.6f, floatArrayOf(0.45f, 0.3f, 0.2f, 1f), true, "تنسيق مظهر وتبديل ملابس الشخصية"),
                        InteriorProp("Safe", "خزنة الفيلا المصفحة (ط2) 🔐", -13.1f, 5.6f, -9.0f, 0.6f, 1.2f, 1.2f, floatArrayOf(0.2f, 0.25f, 0.3f, 1f), true, "تفقد الرصيد المحفوظ داخل خزنة الفيلا 💰"),
                        InteriorProp("Chandelier", "ثريا الكريستال الملكية 💡", 0f, 3.8f, 0f, 2.2f, 1.2f, 2.2f, floatArrayOf(1.0f, 0.95f, 0.6f, 1f), true, "تشغيل / إطفاء إنارة الفيلا وثريا الكريستال"),
                        InteriorProp("RobotBed", "منصة شحن الروبوتات الذكية (ط1) 🤖⚡", 12.2f, 0.8f, -10.8f, 2.2f, 1.6f, 2.2f, floatArrayOf(0.1f, 0.85f, 1.0f, 1f), true, "شحن واستراحة الروبوتات في الفيلا")
                    )
                )
                InteriorType.SUPERMARKET -> BuildingInterior(
                    type = type,
                    titleArabic = "سوبرماركت المدينة المركزي",
                    descriptionArabic = "تسوّق لشراء المواد الغذائية والمشروبات والوجبات الخفيفة لشحن طاقتك.",
                    floorColor = floatArrayOf(0.9f, 0.9f, 0.92f, 1f),
                    wallColor = floatArrayOf(0.96f, 0.96f, 0.97f, 1f),
                    npcGreeting = "مرحباً بك في السوبرماركت! العروض اليومية متوفرة.",
                    npcAvatarEmoji = "🛒",
                    props = listOf(
                        InteriorProp("Shelf1", "رف المشروبات والعصائر", -3.5f, 1f, 0f, 1.4f, 2.2f, 6f, floatArrayOf(0.2f, 0.6f, 0.8f, 1f), true, "شراء مشروب طاقة (5$)", price = 5),
                        InteriorProp("Shelf2", "رف الوجبات الخفيفة والشوكولاتة", 0f, 1f, 0f, 1.4f, 2.2f, 6f, floatArrayOf(0.9f, 0.5f, 0.2f, 1f), true, "شراء وجبة خفيفة (10$)", price = 10),
                        InteriorProp("Shelf3", "رف الفواكه الطازجة", 3.5f, 1f, 0f, 1.4f, 2.2f, 6f, floatArrayOf(0.3f, 0.75f, 0.3f, 1f), true, "شراء تفاح طازج (4$)", price = 4),
                        InteriorProp("Cashier", "صندوق المحاسبة (الكاشير)", -4.0f, 0.6f, 4.5f, 2.6f, 1.1f, 1.2f, floatArrayOf(0.3f, 0.4f, 0.5f, 1f), true, "الدفع ومغادرة المتجر")
                    )
                )
                InteriorType.TRAVEL_TERMINAL -> BuildingInterior(
                    type = type,
                    titleArabic = "محطة السفر المركزية - صالة المغادرة",
                    descriptionArabic = "صالة المسافرين الكبرى لحافلات السفر، التذاكر، وجداول مواعيد الرحلات إلى كافة أنحاء المدينة والضواحي.",
                    floorColor = floatArrayOf(0.85f, 0.85f, 0.88f, 1f), // Polished granite
                    wallColor = floatArrayOf(0.92f, 0.94f, 0.96f, 1f),
                    roomWidth = 20f,
                    roomLength = 20f,
                    roomHeight = 6f,
                    npcGreeting = "أهلاً بك في محطة السفر المركزية! أين وجهتك اليوم؟",
                    npcAvatarEmoji = "🚌",
                    props = listOf(
                        InteriorProp("TicketCounter1", "شباك تذاكر رحلات الضواحي والريف", -4f, 0.7f, -7f, 2.8f, 1.4f, 1.2f, floatArrayOf(0.15f, 0.45f, 0.75f, 1f), true, "حجز تذكرة إلى الريف والضواحي (15$)", price = 15),
                        InteriorProp("TicketCounter2", "شباك تذاكر وسط المدينة السريع", 0f, 0.7f, -7f, 2.8f, 1.4f, 1.2f, floatArrayOf(0.85f, 0.55f, 0.15f, 1f), true, "حجز تذكرة السريع إلى وسط المدينة (20$)", price = 20),
                        InteriorProp("TicketCounter3", "شباك تذاكر محطة القطار والمطار", 4f, 0.7f, -7f, 2.8f, 1.4f, 1.2f, floatArrayOf(0.25f, 0.7f, 0.35f, 1f), true, "حجز رحلة إلى المطار (25$)", price = 25),
                        InteriorProp("Board", "لوحة مواعيد وانطلاق الرحلات", 0f, 3.5f, -8.8f, 8f, 2.5f, 0.2f, floatArrayOf(0.08f, 0.08f, 0.1f, 1f), true, "استعراض جدول الرحلات"),
                        InteriorProp("Benches", "مقاعد انتظار المسافرين", 0f, 0.3f, 0f, 6f, 0.6f, 1.5f, floatArrayOf(0.4f, 0.4f, 0.45f, 1f)),
                        InteriorProp("CafeKiosk", "كشك قهوة ومخبوزات للمسافرين", -7f, 0.8f, 3f, 2.5f, 1.5f, 2.5f, floatArrayOf(0.6f, 0.35f, 0.2f, 1f), true, "شراء كوب كابتشينو (6$)", price = 6)
                    )
                )
                InteriorType.POLICE_STATION -> BuildingInterior(
                    type = type,
                    titleArabic = "مركز الشرطة وحبس الحرامية",
                    descriptionArabic = "غرفة العمليات المركزية وزنزانات الحبس، مجهزة لحبس اللصوص والمجرمين ومتابعة الرادار.",
                    floorColor = floatArrayOf(0.78f, 0.80f, 0.84f, 1f),
                    wallColor = floatArrayOf(0.88f, 0.90f, 0.95f, 1f),
                    npcGreeting = "مأمور المركز: أهلاً بك يا بطل! زنزانات الحبس جاهزة لكل حرامي يتم القبض عليه.",
                    npcAvatarEmoji = "👮",
                    props = listOf(
                        InteriorProp("JailCell", "زنزانة حبس الحرامية 🔒", -4.5f, 1.5f, 2f, 3.5f, 3.0f, 4f, floatArrayOf(0.18f, 0.20f, 0.25f, 1f), true, "إغلاق باب الزنزانة وتأمين الحراسة"),
                        InteriorProp("Handcuffs", "كلبشات ومفاتيح السجن 🗝️", 4.2f, 0.8f, -2.5f, 0.8f, 0.4f, 0.8f, floatArrayOf(0.9f, 0.8f, 0.2f, 1f), true, "تجهيز الكلبشات والمفاتيح للمطاردة"),
                        InteriorProp("InterrogationDesk", "غرفة التحقيق مع اللصوص 📋", -4f, 0.65f, -3f, 2.5f, 1.2f, 1.5f, floatArrayOf(0.35f, 0.25f, 0.15f, 1f), true, "مراجعة ملفات التحقيق والمخالفات"),
                        InteriorProp("PoliceRadar", "رادار مراقبة الشوارع 📡", 0f, 2.5f, -6.5f, 7f, 2f, 0.2f, floatArrayOf(0.05f, 0.1f, 0.2f, 1f), true, "مسح شوارع المدينة بالرادار لرصد الحرامية"),
                        InteriorProp("FrontDesk", "مكتب مأمور المركز وضابط النوبات 👮", 0f, 0.6f, -3f, 4f, 1.2f, 1.4f, floatArrayOf(0.2f, 0.3f, 0.45f, 1f), true, "بدء مهمة دورية أمنية ومطاردة الحرامية")
                    )
                )
                InteriorType.HOSPITAL -> BuildingInterior(
                    type = type,
                    titleArabic = "مستشفى الأمل المركزي",
                    descriptionArabic = "قسم الطوارئ والاستقبال الطبي. احصل على إسعاف سريع واستعد صحتك بنسبة 100%.",
                    floorColor = floatArrayOf(0.92f, 0.95f, 0.95f, 1f),
                    wallColor = floatArrayOf(0.95f, 0.98f, 0.98f, 1f),
                    npcGreeting = "الطبيب المعالج: مرحباً، نحن هنا لرعايتك. صحتك هي أولويتنا.",
                    npcAvatarEmoji = "🩺",
                    props = listOf(
                        InteriorProp("Reception", "مكتب الفرز والاستقبال الطبي", 0f, 0.6f, -3f, 4f, 1.2f, 1.4f, floatArrayOf(0.9f, 0.95f, 0.95f, 1f), true, "فحص طبي شامل واستعادة كامل الصحة (مجانًا)"),
                        InteriorProp("HospitalBed", "سرير عناية وطوارئ", -4.5f, 0.5f, 1f, 2f, 0.8f, 3.2f, floatArrayOf(0.8f, 0.9f, 0.95f, 1f), true, "أخذ قسط من الراحة"),
                        InteriorProp("Pharmacy", "صيدلية المستشفى", 4.5f, 1f, 1f, 1.5f, 2f, 3f, floatArrayOf(0.2f, 0.7f, 0.6f, 1f), true, "شراء مجموعة إسعافات أولية (15$)", price = 15)
                    )
                )
                InteriorType.CAFE -> BuildingInterior(
                    type = type,
                    titleArabic = "مطعم واستراحة المدينة للأكل والشرب 🍖🥤",
                    descriptionArabic = "أشهى المأكولات، المشاوي الفاخرة، المشروبات المنعشة مع وجبة الطعام المتكاملة (200$) لشحن كامل الصحة والطاقة.",
                    floorColor = floatArrayOf(0.7f, 0.55f, 0.42f, 1f),
                    wallColor = floatArrayOf(0.92f, 0.88f, 0.82f, 1f),
                    npcGreeting = "كبير الطهاة: مرحباً بك في مطعم واستراحة المدينة! لدينا أشهى وجبات الطعام المشاوي والمشروبات الباردة والساخنة.",
                    npcAvatarEmoji = "🍖",
                    props = listOf(
                        InteriorProp("SignatureMeal200", "وجبة أكل وشرب فاخرة ومتكاملة 🍖🥤 (200$)", 0f, 0.75f, -3.5f, 2.2f, 0.8f, 1.4f, floatArrayOf(0.95f, 0.45f, 0.1f, 1f), true, "شراء وتناول وجبة طعام وشرب فاخرة (-200$)", price = 200),
                        InteriorProp("BBQMeat", "طبق مشاوي كباب وريش فاخرة 🥩 (50$)", -3.5f, 0.75f, 1.2f, 1.4f, 0.6f, 1.2f, floatArrayOf(0.75f, 0.25f, 0.15f, 1f), true, "طلب طبق مشاوي ساخن (50$)", price = 50),
                        InteriorProp("JuiceAndSoda", "ثلاجة العصائر والمشروبات الباردة 🥤 (15$)", 4.2f, 1.2f, -3.5f, 1.5f, 2.4f, 1.2f, floatArrayOf(0.2f, 0.6f, 0.95f, 1f), true, "شراء عصير فواكه مثلج منعش (15$)", price = 15),
                        InteriorProp("BarCounter", "بار تحضير الوجبات والمشروبات الساخنة ☕", 0f, 0.65f, -4f, 5f, 1.3f, 1.4f, floatArrayOf(0.4f, 0.25f, 0.15f, 1f), true, "طلب قهوة مختصة وشاي مثلج (8$)", price = 8),
                        InteriorProp("CafeTable1", "طاولات طعام عائلية مريحة 🪑", -3.5f, 0.4f, -1f, 1.6f, 0.8f, 1.6f, floatArrayOf(0.6f, 0.4f, 0.25f, 1f), true, "الجلوس وتناول الطعام والاستراحة"),
                        InteriorProp("CafeTable2", "طاولات طعام وجلسات خارجية 🪑", 3.5f, 0.4f, 1f, 1.6f, 0.8f, 1.6f, floatArrayOf(0.6f, 0.4f, 0.25f, 1f), true, "الجلوس وتناول الطعام"),
                        InteriorProp("DessertDisplay", "واجهة الحلويات والآيس كريم 🍰🍦 (25$)", -4.2f, 0.8f, 3.5f, 1.6f, 1.2f, 1.2f, floatArrayOf(0.95f, 0.7f, 0.8f, 1f), true, "شراء حلويات وآيس كريم بارد (25$)", price = 25)
                    )
                )
                InteriorType.CAR_PARTS_SHOP -> BuildingInterior(
                    type = type,
                    titleArabic = "سوق قطع ولوازم وتعديل السيارات 🔧🏎️",
                    descriptionArabic = "متجر متكامل لبيع قطع غيار السيارات، مستلزمات الصيانة، إطارات السباقات، أسطوانات النايترو، والتعديل الرياضي.",
                    floorColor = floatArrayOf(0.18f, 0.18f, 0.22f, 1f), // Carbon dark
                    wallColor = floatArrayOf(0.85f, 0.90f, 0.95f, 1f),
                    npcGreeting = "كبير الفنيين: أهلاً بك في متجر تعديل ولوازم السيارات! جهز سيارتك بأقوى التوربو والنايترو والإطارات الرياضية.",
                    npcAvatarEmoji = "🔧",
                    props = listOf(
                        InteriorProp("NitroRefill", "أسطوانة نايترو توربو خارقة 🚀 (50$)", -4.2f, 0.8f, -3.5f, 1.2f, 1.6f, 1.2f, floatArrayOf(0.1f, 0.65f, 1f, 1f), true, "شراء وشحن أسطوانة نايترو كاملة (50$)", price = 50),
                        InteriorProp("SportTires", "طقم إطارات رياضية فائقة التماسك 🛞 (100$)", 4.2f, 0.8f, -3.5f, 1.6f, 1.6f, 1.2f, floatArrayOf(0.15f, 0.15f, 0.15f, 1f), true, "شراء وتركيب طقم إطارات سباق وتفحيط (100$)", price = 100),
                        InteriorProp("TurboEngine", "تيربو سوبر تشارجر لزيادة التسارع ⚡ (150$)", -4.2f, 0.8f, 2f, 1.4f, 1.3f, 1.2f, floatArrayOf(0.95f, 0.45f, 0.1f, 1f), true, "شراء محرك تيربو مضاعف القوة (150$)", price = 150),
                        InteriorProp("NeonUnderglow", "أشرطة إضاءة نيون سفلية للسيارة 💡 (80$)", 0f, 0.7f, -4.5f, 2.5f, 0.6f, 1.2f, floatArrayOf(0.1f, 0.9f, 1f, 1f), true, "تركيب إضاءة نيون زرقاء سفلية مثل Gamer Blox (80$)", price = 80),
                        InteriorProp("CarWashKit", "طقم غسيل وتلميع وحماية الطلاء 🧽 (25$)", 4.2f, 0.8f, 2f, 1.4f, 1.1f, 1.2f, floatArrayOf(0.2f, 0.8f, 0.4f, 1f), true, "شراء طقم غسيل وتلميع الهيكل (25$)", price = 25),
                        InteriorProp("TuningComputer", "طاولة مهندس تعديل وفحص السيارات 💻🔧 (35$)", 0f, 0.7f, 3.8f, 3.2f, 1.2f, 1.2f, floatArrayOf(0.25f, 0.3f, 0.38f, 1f), true, "فحص إلكتروني وبرمجة كمبيوتر السيارة (35$)", price = 35)
                    )
                )
                InteriorType.REAL_ESTATE_AGENCY -> BuildingInterior(
                    type = type,
                    titleArabic = "شركة ومقر بيع الفلل الفاخرة (Luxury Real Estate)",
                    descriptionArabic = "المقر الرئيسي لعقارات المدينة، تصفح واشترِ أفخم 10 فلل وقصور سكنية من أرباح قيادة سيارتك.",
                    floorColor = floatArrayOf(0.92f, 0.90f, 0.85f, 1f), // Polished Italian Marble
                    wallColor = floatArrayOf(0.95f, 0.96f, 0.98f, 1f),
                    npcGreeting = "مستشار العقارات الفاخرة: مرحباً بك! قد سيارتك في شوارع المدينة لتربح المال ثم اختر فيلتك أو قصرك هنا.",
                    npcAvatarEmoji = "💼",
                    props = listOf(
                        InteriorProp("OpenVillaCatalog", "كتالوج ومعرض الفلل الفاخرة للبيع 🏘️📖", 0f, 0.8f, -4f, 4.5f, 1.3f, 1.4f, floatArrayOf(0.1f, 0.45f, 0.75f, 1f), true, "فتح كتالوج شراء الفلل والقصور"),
                        InteriorProp("ScaleModel", "مجسم معماري ثلاثي الأبعاد لفلل المدينة 🏡🔍", 0f, 0.6f, 0f, 3.2f, 0.8f, 2.2f, floatArrayOf(0.85f, 0.75f, 0.4f, 1f), true, "معاينة مجسم الفلل والقصور والمواقع"),
                        InteriorProp("TitleDeedsSafe", "خزنة صكوك الملكية ومفاتيح الفلل 🔐📜", 4.5f, 1f, -3.5f, 1.5f, 2f, 1.2f, floatArrayOf(0.2f, 0.25f, 0.3f, 1f), true, "تسليم مفاتيح وصكوك الفلل المملوكة"),
                        InteriorProp("VipLounge", "صالة انتظار العملاء ☕🛋️", -4f, 0.4f, 2.5f, 2.8f, 0.8f, 1.5f, floatArrayOf(0.45f, 0.25f, 0.15f, 1f), true, "الجلوس وارتشاف قهوة الضيافة الفاخرة"),
                        InteriorProp("ContractTable", "طاولة توقيع عقود البيع والشراء ✒️📋", 4f, 0.7f, 2.5f, 2.2f, 1.1f, 1.5f, floatArrayOf(0.3f, 0.35f, 0.45f, 1f), true, "توقيع عقد شراء ونقل ملكية الفيلا")
                    )
                )
                InteriorType.VILLAS_FOOD_WATER_MALL -> BuildingInterior(
                    type = type,
                    titleArabic = "مكان شراء الفلل والطعام والشراب والماء",
                    descriptionArabic = "المركز الشامل والحديث في المدينة: شراء أفخم الفلل والقصور السكنية، ومطاعم المأكولات والمشاوي اللذيذة، ومحطات المشروبات والعصائر، وبرادات المياه العذبة النقية.",
                    floorColor = floatArrayOf(0.92f, 0.94f, 0.98f, 1f), // Bright Polished Aqua-White Marble
                    wallColor = floatArrayOf(0.96f, 0.98f, 1.0f, 1f),
                    npcGreeting = "مدير المركز: مرحباً بك في «مكان شراء الفلل والطعام والشراب والماء»! كل ما تحتاجه من الفلل والوجبات والمشروبات تجده هنا.",
                    npcAvatarEmoji = "🛍️",
                    props = listOf(
                        InteriorProp("VillasDeskMall", "مكتب وشباك شراء الفلل والقصور الفاخرة 🏡👑", -4.5f, 0.8f, -4f, 2.5f, 1.3f, 1.5f, floatArrayOf(0.1f, 0.5f, 0.8f, 1f), true, "تصفح وشراء الفلل الفاخرة والقصور"),
                        InteriorProp("FoodGrillCorner", "ركن وبوفيه المشاوي والوجبات الساخنة 🍖🍔 (50$)", 0f, 0.8f, -4.5f, 3.8f, 1.2f, 1.4f, floatArrayOf(0.85f, 0.35f, 0.15f, 1f), true, "شراء وتناول وجبة مشاوي ولحوم ساخنة ولذيذة (50$)", price = 50),
                        InteriorProp("BeverageJuiceBar", "بار العصائر الطبيعية والمشروبات المنعشة 🥤🍹 (15$)", 4.5f, 0.8f, -4f, 2.5f, 1.2f, 1.4f, floatArrayOf(0.95f, 0.65f, 0.1f, 1f), true, "شراء وارتشاف عصير فواكه طازج ومشروبات مثلجة (15$)", price = 15),
                        InteriorProp("PureWaterCooler", "برادة ومحطة مياه الشرب النقية العذبة 💧🧊 (مجاناً)", 4.5f, 0.9f, -1.5f, 1.6f, 1.8f, 1.2f, floatArrayOf(0.1f, 0.75f, 0.95f, 1f), true, "شرب ماء نقي عذب مثلج يروي العطش (مجاناً)"),
                        InteriorProp("WaterBottlesCrate", "صناديق وقوارير مياه معدنية نقية للمنزل والسيارة 💧📦 (5$)", 4.5f, 0.5f, 1.5f, 1.8f, 1.0f, 1.4f, floatArrayOf(0.2f, 0.6f, 0.9f, 1f), true, "شراء قوارير مياه شرب نقية عذبة (5$)", price = 5),
                        InteriorProp("DiningTableMall", "طاولات تناول الطعام والمشروبات والراحة 🍽️🪑", 0f, 0.5f, 0.5f, 3.2f, 0.8f, 2.2f, floatArrayOf(0.35f, 0.25f, 0.18f, 1f), true, "الجلوس وتناول الطعام والشراب مع الاستراحة"),
                        InteriorProp("ExitDoorMall", "باب الخروج إلى شوارع المدينة 🚪❌ (X)", 0f, 0.05f, 5.2f, 3.5f, 0.1f, 1.2f, floatArrayOf(0.9f, 0.2f, 0.2f, 1f), true, "الخروج إلى شوارع المدينة (X)")
                    )
                )
                InteriorType.STREET_HOUSES_MARKET -> BuildingInterior(
                    type = type,
                    titleArabic = "مكان ومكتب شراء بيوت الشارع 🏘️🔑",
                    descriptionArabic = "المعرض والمكتب الرسمي لشراء جميع البيوت المنتشرة في كل شوارع المدينة بأرباح قيادة سيارتك، مع إمكانية الانتقال المباشر لأي بيت اشتريته.",
                    floorColor = floatArrayOf(0.92f, 0.95f, 0.93f, 1f),
                    wallColor = floatArrayOf(0.96f, 0.98f, 0.97f, 1f),
                    npcGreeting = "وكيل عقارات الشوارع: أهلاً بك! قد سيارتك في شوارع المدينة لتربح المال ثم اشترِ أي بيت من بيوت الشارع هنا.",
                    npcAvatarEmoji = "🔑",
                    props = listOf(
                        InteriorProp("OpenStreetHousesCatalog", "منصة وكتالوج شراء بيوت الشارع 🏘️🔑", 0f, 0.8f, -4.2f, 4.6f, 1.3f, 1.4f, floatArrayOf(0.06f, 0.55f, 0.40f, 1f), true, "فتح كتالوج شراء بيوت الشارع (من 15,000$)"),
                        InteriorProp("StreetMapTable", "طاولة خريطة مواقع البيوت بالمدينة 🗺️🏡", 0f, 0.6f, 0.2f, 3.2f, 0.8f, 2.2f, floatArrayOf(0.85f, 0.72f, 0.35f, 1f), true, "تصفح مواقع البيوت في شوارع المدينة"),
                        InteriorProp("KeysCabinet", "خزنة مفاتيح وصكوك بيوت الشارع 🔐🗝️", 4.5f, 1.0f, -3.5f, 1.6f, 2.0f, 1.2f, floatArrayOf(0.20f, 0.25f, 0.32f, 1f), true, "عرض عدد بيوت الشارع المملوكة لك"),
                        InteriorProp("HospitalityCoffee", "ركن القهوة والضيافة للعملاء ☕🛋️", -4.0f, 0.5f, 2.5f, 2.6f, 0.9f, 1.5f, floatArrayOf(0.45f, 0.28f, 0.16f, 1f), true, "شرب قهوة الضيافة العربية")
                    )
                )
                InteriorType.STANDARD_HOUSE -> BuildingInterior(
                    type = type,
                    titleArabic = "بيت سكني عصري عملي (6 شبابيك)",
                    descriptionArabic = "بيت عائلي مريح بتصميم عملي منظم مع 6 شبابيك زجاجية بإطارات مطلة على المدينة: صالة معيشة مركزية بكنب مريح، مطبخ شبه مفتوح مجهز، غرف نوم بأسرة خشبية متينة، ومساحات تخزين عملية تناسب الحياة اليومية.",
                    floorColor = floatArrayOf(0.82f, 0.72f, 0.58f, 1f), // Warm ceramic wood tiles
                    wallColor = floatArrayOf(0.96f, 0.95f, 0.92f, 1f),
                    roomWidth = 18f,
                    roomLength = 16f,
                    roomHeight = 4.2f,
                    npcGreeting = "مرحباً بك في البيت العائلي العصري! استمتع بالإطلالة عبر الشبابيك الستة وتصميمه المريح.",
                    npcAvatarEmoji = "🏠",
                    props = listOf(
                        InteriorProp("LivingSofa", "كنب صالة المعيشة المريح 🛋️", 0f, 0.45f, 2.5f, 3.8f, 0.85f, 1.2f, floatArrayOf(0.35f, 0.38f, 0.42f, 1f), true, "الجلوس والاسترخاء في الصالة المركزية"),
                        InteriorProp("LivingTV", "شاشة التلفاز العائلية 55\" 📺", 0f, 1.4f, -4.5f, 2.4f, 1.4f, 0.15f, floatArrayOf(0.1f, 0.1f, 0.12f, 1f), true, "مشاهدة التلفاز مع العائلة"),
                        InteriorProp("CoffeeTable", "طاولة وسطية خشبية عملية 🪵", 0f, 0.35f, 0.5f, 1.6f, 0.45f, 0.9f, floatArrayOf(0.45f, 0.30f, 0.18f, 1f), true, "وضع المشروبات والمجلات"),
                        InteriorProp("KitchenOpen", "مطبخ شبه مفتوح بأرفف وثلاجة 🍳", 5.5f, 0.8f, -2.5f, 2.2f, 1.4f, 4.5f, floatArrayOf(0.90f, 0.90f, 0.92f, 1f), true, "إعداد وجبة عائلية خفيفة وتناول طعام"),
                        InteriorProp("DiningTable", "طاولة طعام عملية مع كراسي 🍽️", 4.5f, 0.45f, 2.5f, 2.2f, 0.85f, 1.4f, floatArrayOf(0.50f, 0.35f, 0.22f, 1f), true, "تناول الطعام مع أفراد الأسرة"),
                        InteriorProp("BedWood", "سرير خشبي متين ومريح 🛏️", -5.5f, 0.55f, -3.5f, 2.2f, 0.9f, 3.0f, floatArrayOf(0.55f, 0.38f, 0.22f, 1f), true, "النوم وأخذ قسط من الراحة والاسترخاء 😴"),
                        InteriorProp("WardrobeWood", "خزانة ملابس جدارية عملية 🚪", -5.5f, 1.4f, 1.5f, 1.8f, 2.4f, 0.9f, floatArrayOf(0.40f, 0.28f, 0.16f, 1f), true, "ترتيب الملابس وحفظ الأغراض الشخصية"),
                        InteriorProp("StudyDesk", "مكتب دراسي للأطفال ومذاكرة 📚", -5.5f, 0.65f, 4.5f, 1.6f, 1.0f, 0.9f, floatArrayOf(0.35f, 0.25f, 0.15f, 1f), true, "مذاكرة الدروس واستخدام الكمبيوتر")
                    )
                )
                InteriorType.DINING_AVENUE -> BuildingInterior(
                    type = type,
                    titleArabic = "مجمع المطاعم والخدمات (Dining Avenue)",
                    descriptionArabic = "وجهة الطعام الأولى في الجانب الجنوبي: مطاعم عالمية راقية، وجبات سريعة وعصرية، ومقهى ومخبز يقدم أشهى المخبوزات والقهوة الطازجة يومياً بأعلى معايير الجودة والنظافة.",
                    floorColor = floatArrayOf(0.88f, 0.85f, 0.82f, 1f), // Terrazzo polished restaurant floor
                    wallColor = floatArrayOf(0.96f, 0.94f, 0.90f, 1f),
                    roomWidth = 24f,
                    roomLength = 22f,
                    roomHeight = 5.5f,
                    npcGreeting = "الشيف التنفيذي: مرحباً بك في مجمع المطاعم (Dining Avenue)! نقدم أشهى الأطباق العالمية والمخبوزات الطازجة بأعلى جودة.",
                    npcAvatarEmoji = "👨‍🍳",
                    props = listOf(
                        // 1. مطعم المأكولات العالمية والرئيسية
                        InteriorProp("GlobalKitchen", "مطعم المأكولات العالمية الفاخر 🍲🥩", -7.5f, 0.9f, -6.5f, 4.5f, 1.5f, 2.2f, floatArrayOf(0.85f, 0.25f, 0.2f, 1f), true, "طلب وجبة ستيك لحم غنية وأطباق عالمية (45$)", price = 45),
                        // 2. مطعم الوجبات السريعة والعصرية
                        InteriorProp("FastFoodCounter", "مطعم الوجبات السريعة العصرية 🍔🍟", 0f, 0.9f, -7.5f, 4.5f, 1.5f, 2.0f, floatArrayOf(0.95f, 0.65f, 0.1f, 1f), true, "طلب كومبو برجر وسماش وبطاطس مقرمشة (20$)", price = 20),
                        // 3. مقهى ومخبز (Cafe & Bakery)
                        InteriorProp("BakeryCafe", "مقهى ومخبز المخبوزات والقهوة الطازجة 🥐☕", 7.5f, 0.9f, -6.5f, 4.5f, 1.5f, 2.2f, floatArrayOf(0.65f, 0.40f, 0.25f, 1f), true, "شراء كرواسون فرنسي ساخن وكوب لاتيه طازج (10$)", price = 10),
                        // الجلسات الداخلية والخارجية الفاخرة
                        InteriorProp("DiningLounge1", "جلسات صالة الطعام المركزية 🍽️🪑", -4.5f, 0.45f, 0f, 3.2f, 0.85f, 2.0f, floatArrayOf(0.25f, 0.25f, 0.28f, 1f), true, "الجلوس والاستمتاع بتجربة الطعام المتكاملة"),
                        InteriorProp("DiningLounge2", "طاولات تناول الطعام العائلية 🍽️🪑", 4.5f, 0.45f, 0f, 3.2f, 0.85f, 2.0f, floatArrayOf(0.25f, 0.25f, 0.28f, 1f), true, "الجلوس مع العائلة والأصدقاء"),
                        InteriorProp("OutdoorTerrace", "جلسات التراس الخارجية المفتوحة ☀️🌴", 0f, 0.45f, 6.5f, 6.0f, 0.85f, 2.5f, floatArrayOf(0.2f, 0.55f, 0.45f, 1f), true, "الجلوس في الجلسات الخارجية المطلة على حدائق المدينة"),
                        InteriorProp("JuiceFountain", "محطة مياه نقية وعصائر فريش 🥤💧", -8.5f, 0.8f, 4.5f, 1.8f, 1.4f, 1.4f, floatArrayOf(0.1f, 0.75f, 0.95f, 1f), true, "ارتشاف مياه شرب نقية وعصائر مثلجة (مجاناً)"),
                        InteriorProp("QualityInspect", "شهادة الجودة والنظافة العالية ⭐🥇", 8.5f, 1.8f, 4.5f, 1.4f, 1.4f, 0.2f, floatArrayOf(0.95f, 0.85f, 0.2f, 1f), true, "معايير النظافة العالية وجودة المكونات المعتمدة 100%")
                    )
                )
                InteriorType.CLOTHING_STORE -> BuildingInterior(
                    type = type,
                    titleArabic = "متجر الأواعي والماركات (Nike • Adidas) 👕👖👟",
                    descriptionArabic = "متجر متكامل لشركات الملابس العالمية مثل نايكي (Nike) وأديداس (Adidas) وجوردان وبوما: تيشيرتات، هوديات، بناطيل، جينز، أحذية سنيكرز، قبعات، وإكسسوارات تُلبس فوراً على شخصيتك!",
                    floorColor = floatArrayOf(0.94f, 0.94f, 0.96f, 1f),
                    wallColor = floatArrayOf(0.14f, 0.16f, 0.22f, 1f),
                    roomWidth = 20f,
                    roomLength = 20f,
                    roomHeight = 5.4f,
                    npcGreeting = "منسق الأزياء: أهلاً بك في متجر الماركات! جرب أواعي Nike وAdidas والبناطيل والأحذية، وعند شرائها تلبسها شخصيتك فوراً!",
                    npcAvatarEmoji = "👕",
                    props = listOf(
                        InteriorProp("NikeBoutiqueRack", "قسم شركة نايكي (Nike ✓) تيشيرت وهودي 🧡", -6.0f, 1.1f, -5.5f, 4.2f, 2.2f, 1.6f, floatArrayOf(0.98f, 0.42f, 0.05f, 1f), true, "تصفح وشراء ملابس نايكي Nike ✓"),
                        InteriorProp("AdidasBoutiqueRack", "قسم شركة أديداس (Adidas ☰) بدلات وجاكيتات 💙", 0f, 1.1f, -6.5f, 4.4f, 2.2f, 1.6f, floatArrayOf(0.10f, 0.42f, 0.92f, 1f), true, "تصفح وشراء ملابس أديداس Adidas ☰"),
                        InteriorProp("PantsJeansRack", "قسم البناطيل والجينز والشورتات 👖🩳", 6.0f, 1.1f, -5.5f, 4.2f, 2.2f, 1.6f, floatArrayOf(0.14f, 0.30f, 0.58f, 1f), true, "اختيار بنطلون وجينز وشورت للشخصية 👖"),
                        InteriorProp("SneakersWall", "واجهة الأحذية والسنيكرز (Air Max & Superstar) 👟", -6.5f, 1.1f, 0.8f, 2.0f, 2.2f, 5.2f, floatArrayOf(0.88f, 0.16f, 0.24f, 1f), true, "شراء ولبس أحذية وسنيكرز نايكي وأديداس 👟"),
                        InteriorProp("CapsGlassesDisplay", "ركن الكابات والنظارات والساعات 🧢🕶️⌚", 6.5f, 1.0f, 0.8f, 2.0f, 2.0f, 5.2f, floatArrayOf(0.95f, 0.78f, 0.16f, 1f), true, "شراء ولبس الكابات والنظارات والساعات 🧢"),
                        InteriorProp("WardrobeFittingMirror", "غرفة القياس وخزانة ملابسي للتبديل 🪞👔", 0f, 1.1f, 0.5f, 3.0f, 2.2f, 2.0f, floatArrayOf(0.55f, 0.86f, 0.98f, 1f), true, "فتح خزانة ملابسي وتبديل الطقم فوراً 🪞"),
                        InteriorProp("ClothingCashier", "كاشير الأواعي والخروج 🧾🚪", -4.5f, 0.7f, 6.2f, 3.0f, 1.3f, 1.4f, floatArrayOf(0.20f, 0.24f, 0.32f, 1f), true, "فتح كتالوج الماركات وخزانتي")
                    )
                )
                InteriorType.GRAND_MALL -> BuildingInterior(
                    type = type,
                    titleArabic = "المول التجاري الكبير (أواعي • خضار • مطاعم • طاولات) 🏬🛍️",
                    descriptionArabic = "مول ضخم يحتوي على محلات الأواعي والماركات، سوق الخضار والفواكه الطازجة، المطاعم والمشاوي، المخبز والسوبرماركت، وساحة طعام واسعة مليئة بالطاولات الكثيرة للأكل والجلوس.",
                    floorColor = floatArrayOf(0.93f, 0.91f, 0.88f, 1f),
                    wallColor = floatArrayOf(0.96f, 0.95f, 0.97f, 1f),
                    roomWidth = 30f,
                    roomLength = 28f,
                    roomHeight = 6.0f,
                    npcGreeting = "أهلاً بك في المول الكبير! اشترِ والبس الأواعي، تسوق الخضار الطازجة والأكل، واجلس على أي طاولة لتناول طعامك!",
                    npcAvatarEmoji = "🏬",
                    props = listOf(
                        // Commercial Shops along North Wing (Z = -10.5) & Side Wings
                        InteriorProp("MallClothesShop", "محل الأواعي والماركات (Nike • Adidas) 👕👖👟", -10.5f, 1.1f, -9.8f, 5.2f, 2.2f, 2.0f, floatArrayOf(0.18f, 0.35f, 0.85f, 1f), true, "شراء ولبس الأواعي فوراً على شخصيتك 👕"),
                        InteriorProp("MallVegFruitMarket", "سوق الخضار والفواكه الطازجة 🥬🍅🍎🍌", -3.5f, 0.85f, -10.4f, 5.2f, 1.7f, 2.2f, floatArrayOf(0.14f, 0.68f, 0.30f, 1f), true, "شراء وأكل الخضار والفواكه الطازجة 🥬"),
                        InteriorProp("MallRestaurantsFood", "ركن المطاعم والمشاوي والبرجر والبيتزا 🍔🍖🍕", 3.5f, 0.95f, -10.4f, 5.2f, 1.9f, 2.2f, floatArrayOf(0.92f, 0.36f, 0.12f, 1f), true, "شراء وتناول أشهى الوجبات الساخنة 🍽️"),
                        InteriorProp("MallBakerySupermarket", "السوبرماركت والمخبز والعصائر والإلكترونيات 🛒🥐🥤", 10.5f, 1.05f, -9.8f, 5.2f, 2.1f, 2.0f, floatArrayOf(0.82f, 0.56f, 0.14f, 1f), true, "فتح السوبرماركت والمخبز وكل محلات المول 🛒"),
                        InteriorProp("MallOpenAllShops", "مكتب دليل المول وكل المحلات التجارية 🏬✨", -12.6f, 0.85f, -1.5f, 2.0f, 1.7f, 4.2f, floatArrayOf(0.58f, 0.22f, 0.78f, 1f), true, "فتح كتالوج المول الشامل (أواعي • خضار • أكل) 🛍️"),
                        InteriorProp("MallJuiceDessertBar", "ركن القهوة والحلويات والعصائر والماء 🥤🍰💧", 12.6f, 0.85f, -1.5f, 2.0f, 1.7f, 4.2f, floatArrayOf(0.10f, 0.68f, 0.82f, 1f), true, "شرب عصائر طبيعية وماء وتناول الحلويات 🥤"),
                        // 12 Large Dining Tables with Chairs arranged neatly in the Food Court ("طاولات كثيرة لكي ياكلوا عليها")
                        InteriorProp("MallDiningTable_1", "طاولة طعام رقم 1 🍽️🪑", -8.8f, 0.45f, -2.6f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 1 🍽️"),
                        InteriorProp("MallDiningTable_2", "طاولة طعام رقم 2 🍽️🪑", -3.0f, 0.45f, -2.6f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 2 🍽️"),
                        InteriorProp("MallDiningTable_3", "طاولة طعام رقم 3 🍽️🪑", 3.0f, 0.45f, -2.6f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 3 🍽️"),
                        InteriorProp("MallDiningTable_4", "طاولة طعام رقم 4 🍽️🪑", 8.8f, 0.45f, -2.6f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 4 🍽️"),
                        InteriorProp("MallDiningTable_5", "طاولة طعام رقم 5 🍽️🪑", -8.8f, 0.45f, 2.2f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 5 🍽️"),
                        InteriorProp("MallDiningTable_6", "طاولة طعام رقم 6 🍽️🪑", -3.0f, 0.45f, 2.2f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 6 🍽️"),
                        InteriorProp("MallDiningTable_7", "طاولة طعام رقم 7 🍽️🪑", 3.0f, 0.45f, 2.2f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 7 🍽️"),
                        InteriorProp("MallDiningTable_8", "طاولة طعام رقم 8 🍽️🪑", 8.8f, 0.45f, 2.2f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 8 🍽️"),
                        InteriorProp("MallDiningTable_9", "طاولة طعام رقم 9 🍽️🪑", -8.8f, 0.45f, 7.0f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 9 🍽️"),
                        InteriorProp("MallDiningTable_10", "طاولة طعام رقم 10 🍽️🪑", -3.0f, 0.45f, 7.0f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 10 🍽️"),
                        InteriorProp("MallDiningTable_11", "طاولة طعام رقم 11 🍽️🪑", 3.0f, 0.45f, 7.0f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 11 🍽️"),
                        InteriorProp("MallDiningTable_12", "طاولة طعام رقم 12 🍽️🪑", 8.8f, 0.45f, 7.0f, 2.1f, 0.85f, 1.5f, floatArrayOf(0.54f, 0.36f, 0.22f, 1f), true, "الجلوس والأكل على طاولة 12 🍽️"),
                        InteriorProp("ExitDoorMall", "بوابة الخروج إلى المدينة 🚪", 0f, 0.05f, 13.0f, 3.2f, 0.1f, 1.2f, floatArrayOf(0.16f, 0.78f, 0.42f, 1f), true, "الخروج من المول إلى المدينة")
                    )
                )
                InteriorType.UNDERGROUND_GARAGE -> BuildingInterior(
                    type = type,
                    titleArabic = "كراج المدينة تحت الأرض (لتصفية السيارات) 🅿️⬇️",
                    descriptionArabic = "كراج سيارات مركزي حديث تحت الأرض يضم 14 موقفاً منظماً لصف جميع سيارات المدينة بعيداً عن الشوارع، مع شاحن كهربائي مجاني ومغسلة سيارات ومنحدر خروج مباشر إلى الشارع.",
                    floorColor = floatArrayOf(0.20f, 0.22f, 0.25f, 1f),
                    wallColor = floatArrayOf(0.32f, 0.35f, 0.40f, 1f),
                    roomWidth = 36f,
                    roomLength = 44f,
                    roomHeight = 5.4f,
                    npcGreeting = "أهلاً بك في كراج المدينة تحت الأرض! اركن سيارتك في أي موقف أو قد أي سيارة مصفوفة واخرج عبر المنحدر.",
                    npcAvatarEmoji = "🅿️",
                    props = listOf(
                        InteriorProp("GarageValetDesk", "مكتب الكراج واستدعاء/تنظيم السيارات 🅿️🔑", 0f, 0.65f, -20.6f, 4.2f, 1.3f, 1.5f, floatArrayOf(0.10f, 0.45f, 0.85f, 1f), true, "صف جميع السيارات المتوقفة بالشارع هنا تلقائياً"),
                        InteriorProp("UndergroundEVCharger", "محطة تزويد الوقود والنايترو المجانية ⚡⛽", -16.2f, 1.1f, -20.5f, 2.2f, 2.2f, 1.6f, floatArrayOf(0.08f, 0.80f, 0.45f, 1f), true, "شحن الوقود والنايترو 100% مجاناً"),
                        InteriorProp("UndergroundCarWash", "محطة غسيل وتلميع السيارات 🧽✨", 16.2f, 1.1f, -20.5f, 2.2f, 2.2f, 1.6f, floatArrayOf(0.12f, 0.72f, 0.96f, 1f), true, "غسيل وتلميع السيارات المصفوفة"),
                        InteriorProp("GarageExitRamp", "منحدر الصعود والخروج للشوارع ⬆️🚗", 0f, 0.08f, 20.4f, 8.6f, 0.16f, 2.4f, floatArrayOf(0.16f, 0.78f, 0.42f, 1f), true, "الخروج من الكراج تحت الأرض إلى شارع المدينة")
                    )
                )
                InteriorType.METRO_CARD_SHOP -> BuildingInterior(
                    type = InteriorType.METRO_CARD_SHOP,
                    titleArabic = "مركزي لبيع كروت المترو 💳🚇",
                    descriptionArabic = "محلك الخاص لبيع وشحن كروت المترو الذكية (عادي، فضي، ذهبي VIP، بلاتيني) لركاب وزبائن المدينة وكسب الأرباح مباشرة!",
                    floorColor = floatArrayOf(0.86f, 0.92f, 0.96f, 1f), // Clean Metro Transit Porcelain Tile
                    wallColor = floatArrayOf(0.92f, 0.96f, 0.99f, 1f),
                    roomWidth = 22f,
                    roomLength = 18f,
                    roomHeight = 5.2f,
                    npcGreeting = "أهلاً يا مدير المحل! الزبائن بالخارج ينتظرون شراء كروت المترو عند الشباك 💳💰",
                    npcAvatarEmoji = "💳",
                    props = listOf(
                        InteriorProp(
                            name = "MetroCardSalesCounter",
                            arabicName = "شباك وكونتر بيع كروت المترو للزبائن 💳",
                            posX = 0f,
                            posY = 0.65f,
                            posZ = -4.5f,
                            width = 8.0f,
                            height = 1.3f,
                            depth = 1.8f,
                            color = floatArrayOf(0.06f, 0.44f, 0.68f, 1f),
                            isInteractive = true,
                            actionText = "💳 بيع كروت المترو للزبائن وكسب المال"
                        ),
                        InteriorProp(
                            name = "MetroCardPrinterTerminal",
                            arabicName = "جهاز طباعة وشحن كروت المترو الذكية 🖨️",
                            posX = -6.8f,
                            posY = 0.85f,
                            posZ = -4.5f,
                            width = 2.2f,
                            height = 1.7f,
                            depth = 1.6f,
                            color = floatArrayOf(0.14f, 0.22f, 0.34f, 1f),
                            isInteractive = true,
                            actionText = "🖨️ طباعة +10 كروت مترو جديدة للمخزون"
                        ),
                        InteriorProp(
                            name = "MetroCardDisplayWall",
                            arabicName = "حائط عرض باقات كروت المترو 🚇",
                            posX = 0f,
                            posY = 1.5f,
                            posZ = -8.1f,
                            width = 9.0f,
                            height = 3.0f,
                            depth = 0.8f,
                            color = floatArrayOf(0.08f, 0.26f, 0.44f, 1f),
                            isInteractive = true,
                            actionText = "📋 عرض أسعار وباقات كروت المترو (5 - 500 جنيه)"
                        ),
                        InteriorProp(
                            name = "MetroWaitingBench",
                            arabicName = "مقاعد استراحة وانتظار الركاب 🪑",
                            posX = 6.8f,
                            posY = 0.45f,
                            posZ = 1.8f,
                            width = 2.2f,
                            height = 0.9f,
                            depth = 4.5f,
                            color = floatArrayOf(0.10f, 0.58f, 0.72f, 1f),
                            isInteractive = true,
                            actionText = "🪑 استراحة انتظار ركاب المترو"
                        ),
                        InteriorProp(
                            name = "ExitMetroShopDoor",
                            arabicName = "خروج إلى الشارع الرئيسي 🚪",
                            posX = 0f,
                            posY = 0.08f,
                            posZ = 8.2f,
                            width = 2.8f,
                            height = 0.16f,
                            depth = 0.5f,
                            color = floatArrayOf(0.85f, 0.2f, 0.2f, 1f),
                            isInteractive = true,
                            actionText = "🚪 الخروج من مركز بيع الكروت إلى المدينة"
                        )
                    )
                )
                else -> BuildingInterior(
                    type = type,
                    titleArabic = "مبنى داخلي",
                    descriptionArabic = "استكشف تفاصيل هذا المكان.",
                    floorColor = floatArrayOf(0.8f, 0.8f, 0.8f, 1f),
                    wallColor = floatArrayOf(0.9f, 0.9f, 0.9f, 1f),
                    props = emptyList()
                )
            }
        }

        /**
         * Returns a clear unobstructed spawn position inside the doorway facing into the room (-Z).
         */
        fun getSpawnPosition(type: InteriorType): Vector3 {
            return when (type) {
                InteriorType.PLAYER_VILLA -> Vector3(0f, 0f, 10.2f)
                InteriorType.UNDERGROUND_GARAGE -> Vector3(0f, 0f, 15.0f)
                InteriorType.GRAND_MALL -> Vector3(0f, 0f, 11.4f)
                InteriorType.DINING_AVENUE -> Vector3(0f, 0f, 9.2f)
                InteriorType.CLOTHING_STORE -> Vector3(0f, 0f, 7.6f)
                InteriorType.TRAVEL_TERMINAL -> Vector3(0f, 0f, 7.0f)
                InteriorType.METRO_CARD_SHOP -> Vector3(0f, 0f, 5.8f)
                InteriorType.STANDARD_HOUSE -> Vector3(0f, 0f, 6.0f)
                else -> Vector3(0f, 0f, 5.4f)
            }
        }

        private data class FurnitureCollider(
            val cx: Float,
            val cz: Float,
            val w: Float,
            val d: Float
        )

        // Ground Floor (Y < 2.6f) Villa Colliders
        private val VILLA_FLOOR1_COLLIDERS = listOf(
            // Living Salon L-shaped sofa + chaise
            FurnitureCollider(0f, 2.1f, 4.5f, 1.6f),
            FurnitureCollider(-2.1f, 1.0f, 1.5f, 2.4f),
            // Coffee table
            FurnitureCollider(0f, -0.4f, 2.4f, 1.4f),
            // TV Marble Wall & credenza
            FurnitureCollider(0f, -5.8f, 5.4f, 1.0f),
            // Formal Majlis sofa & coffee table
            FurnitureCollider(0f, -11.0f, 3.8f, 1.2f),
            FurnitureCollider(0f, -9.4f, 1.4f, 1.4f),
            // Kitchen waterfall island & bar stools
            FurnitureCollider(6.8f, -6.0f, 2.8f, 4.5f),
            // Rear kitchen counter
            FurnitureCollider(12.6f, -6.0f, 1.6f, 5.0f),
            // Double-door refrigerator
            FurnitureCollider(12.6f, -1.8f, 1.6f, 1.6f),
            // Robot Charging Dock
            FurnitureCollider(12.2f, -10.8f, 2.2f, 2.2f),
            // Indoor Swimming Pool is recessed and open so the character can step in & swim inside still water!
            // Poolside Loungers
            FurnitureCollider(-6.6f, -3.5f, 1.1f, 2.1f),
            FurnitureCollider(-6.6f, -6.8f, 1.1f, 2.1f),
            // Ground Floor Guest Bathroom Partition Walls (Doorway on X = -7.2 at Z = 6.0..8.4)
            FurnitureCollider(-10.35f, 4.0f, 6.3f, 0.35f),
            FurnitureCollider(-7.2f, 4.9f, 0.35f, 1.8f),
            FurnitureCollider(-7.2f, 10.6f, 0.35f, 4.2f),
            // Guest Bathroom Fixtures
            FurnitureCollider(-12.5f, 10.6f, 1.2f, 2.2f),
            FurnitureCollider(-12.6f, 5.4f, 1.0f, 1.2f),
            FurnitureCollider(-9.2f, 11.3f, 2.2f, 2.2f),
            // Staircase West Enclosure Wall on Floor 1 (prevents walking under floating stairs; enter stairs from Z = +11.4)
            FurnitureCollider(9.5f, 6.6f, 0.35f, 7.2f)
        )

        // Second Floor (Y >= 2.6f) Villa Colliders
        private val VILLA_FLOOR2_COLLIDERS = listOf(
            // Master Bedroom Partition Walls (Doorways: X = -2.2 at Z = -4.2..-1.4; Z = -1.0 at X = -6.5..-3.5)
            FurnitureCollider(-2.2f, -8.4f, 0.35f, 8.4f),
            FurnitureCollider(-10.1f, -1.0f, 7.2f, 0.35f),
            FurnitureCollider(-2.85f, -1.0f, 1.3f, 0.35f),
            // Master King Bed + Nightstands + Vanity + Wardrobe
            FurnitureCollider(-8.5f, -9.5f, 3.2f, 3.6f),
            FurnitureCollider(-10.6f, -10.6f, 0.9f, 0.8f),
            FurnitureCollider(-6.4f, -10.6f, 0.9f, 0.8f),
            FurnitureCollider(-3.8f, -9.5f, 1.1f, 2.1f),
            FurnitureCollider(-12.6f, -4.5f, 1.4f, 3.6f),
            // Master Luxury Bathroom Partition Walls (Doorway on X = -3.0 at Z = 4.2..7.0)
            FurnitureCollider(-8.3f, 1.8f, 10.6f, 0.35f),
            FurnitureCollider(-3.0f, 3.0f, 0.35f, 2.4f),
            FurnitureCollider(-3.0f, 9.8f, 0.35f, 5.6f),
            // Master Bathroom Fixtures (Jacuzzi, Glass Shower, Vanity Sink, Toilet)
            FurnitureCollider(-9.8f, 9.6f, 3.2f, 3.2f),
            FurnitureCollider(-11.8f, 3.6f, 2.2f, 2.2f),
            FurnitureCollider(-5.6f, 11.8f, 2.8f, 1.0f),
            FurnitureCollider(-5.2f, 3.4f, 1.0f, 1.2f),
            // Second Bedroom Partition Walls (Doorway on Z = -2.4 at X = 5.2..8.2)
            FurnitureCollider(1.5f, -7.5f, 0.35f, 10.2f),
            FurnitureCollider(3.35f, -2.4f, 3.7f, 0.35f),
            FurnitureCollider(10.9f, -2.4f, 5.4f, 0.35f),
            // Second Bedroom Furniture (2 Beds, Study Desk, Wardrobe)
            FurnitureCollider(10.8f, -9.6f, 2.2f, 3.2f),
            FurnitureCollider(4.2f, -9.6f, 2.2f, 3.2f),
            FurnitureCollider(3.2f, -4.6f, 2.0f, 1.2f),
            FurnitureCollider(12.6f, -5.2f, 1.2f, 3.0f),
            // Upper Family Lounge Armchair & Table
            FurnitureCollider(2.5f, 8.8f, 2.4f, 1.6f),
            // Stairwell Guardrails (West side X = 9.5 & South side Z = 11.8)
            FurnitureCollider(9.5f, 7.5f, 0.35f, 8.8f),
            FurnitureCollider(11.6f, 11.8f, 4.2f, 0.35f)
        )

        /**
         * Returns the walkable floor / recessed pool elevation at (px, pz) inside PLAYER_VILLA so walking along
         * the 3D stairs climbs smoothly between Floor 1 (0f) and Floor 2 (4.6f), and stepping into the indoor
         * swimming pool smoothly lowers the character into chest-deep still water (-0.75f).
         */
        fun getVillaFloorHeight(px: Float, currentY: Float, pz: Float): Float {
            // Staircase zone: X in 9.65..13.5, Z in 3.0..11.4
            if (px in 9.65f..13.5f && pz in 3.0f..11.4f) {
                val t = ((11.4f - pz) / (11.4f - 3.0f)).coerceIn(0f, 1f)
                return t * 4.6f
            }
            // Indoor recessed swimming pool zone: X in -11.75..-7.85, Z in -9.15..-1.25
            if (currentY < 2.5f && px in -11.75f..-7.85f && pz in -9.15f..-1.25f) {
                return when {
                    px > -8.25f -> -0.28f
                    px > -8.70f -> -0.52f
                    else -> -0.75f
                }
            }
            return if (currentY >= 2.5f) 4.6f else 0f
        }

        /**
         * True if (px, py, pz) is currently submerged inside the Villa indoor swimming pool water.
         */
        fun isPointInVillaIndoorPool(px: Float, py: Float, pz: Float): Boolean {
            return py < 1.0f && px in -11.75f..-7.85f && pz in -9.15f..-1.25f
        }

        /**
         * Returns the maximum safe foot elevation Y so jumping indoors NEVER touches or penetrates ceilings or floor slabs.
         */
        fun getIndoorCeilingLimitY(type: InteriorType, px: Float, currentY: Float, pz: Float): Float {
            val interior = getInterior(type)
            val charHeight = 1.90f
            if (type == InteriorType.PLAYER_VILLA && currentY < 2.5f) {
                val inStairwellOpening = (px in 9.5f..14.0f && pz in 2.6f..12.1f)
                if (!inStairwellOpening) {
                    return (4.42f - charHeight).coerceAtLeast(0.5f)
                }
            }
            return (interior.roomHeight - charHeight).coerceAtLeast(0.5f)
        }

        /**
         * Checks if (px, py, pz) inside this interior overlaps solid furniture or interior walls.
         */
        fun checkPropCollision(
            type: InteriorType,
            px: Float,
            pz: Float,
            margin: Float = 0.56f,
            py: Float = 0f
        ): Boolean {
            if (type == InteriorType.PLAYER_VILLA) {
                val colliders = if (py >= 2.5f) VILLA_FLOOR2_COLLIDERS else VILLA_FLOOR1_COLLIDERS
                for (box in colliders) {
                    val halfW = box.w / 2f + margin
                    val halfD = box.d / 2f + margin
                    if (px >= (box.cx - halfW) && px <= (box.cx + halfW) &&
                        pz >= (box.cz - halfD) && pz <= (box.cz + halfD)
                    ) {
                        return true
                    }
                }
                return false
            }

            val interior = getInterior(type)
            for (prop in interior.props) {
                // Skip flat floor mats (ExitDoorMall/GarageExitRamp) and overhead/wall-mounted fixtures (Chandelier, Boards)
                val topY = prop.posY + prop.height / 2f
                val bottomY = prop.posY - prop.height / 2f
                if (topY <= 0.25f || bottomY >= 2.0f) continue

                val halfW = prop.width / 2f + margin
                val halfD = prop.depth / 2f + margin
                if (px >= (prop.posX - halfW) && px <= (prop.posX + halfW) &&
                    pz >= (prop.posZ - halfD) && pz <= (prop.posZ + halfD)
                ) {
                    return true
                }
            }
            if (type == InteriorType.UNDERGROUND_GARAGE) {
                for (colX in listOf(-6.5f, 6.5f)) {
                    for (colZ in listOf(-14.0f, -4.0f, 6.0f)) {
                        val half = 0.65f + margin
                        if (px in (colX - half)..(colX + half) && pz in (colZ - half)..(colZ + half)) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        /**
         * Resolves overlap with indoor partition walls and furniture along minimum penetration axis
         * so no character can ever push into walls or corners.
         */
        fun resolvePropCollisionPush(
            type: InteriorType,
            px: Float,
            pz: Float,
            margin: Float = 0.56f,
            py: Float = 0f
        ): Pair<Float, Float>? {
            var rx = px
            var rz = pz
            var collided = false

            if (type == InteriorType.PLAYER_VILLA) {
                val colliders = if (py >= 2.5f) VILLA_FLOOR2_COLLIDERS else VILLA_FLOOR1_COLLIDERS
                for (box in colliders) {
                    val halfW = box.w / 2f + margin
                    val halfD = box.d / 2f + margin
                    val dx = rx - box.cx
                    val dz = rz - box.cz
                    val ox = halfW - kotlin.math.abs(dx)
                    val oz = halfD - kotlin.math.abs(dz)
                    if (ox > 0f && oz > 0f) {
                        collided = true
                        if (ox < oz) {
                            rx = if (dx >= 0f) box.cx + halfW + 0.02f else box.cx - halfW - 0.02f
                        } else {
                            rz = if (dz >= 0f) box.cz + halfD + 0.02f else box.cz - halfD - 0.02f
                        }
                    }
                }
            } else {
                val interior = getInterior(type)
                for (prop in interior.props) {
                    val topY = prop.posY + prop.height / 2f
                    val bottomY = prop.posY - prop.height / 2f
                    if (topY <= 0.25f || bottomY >= 2.0f) continue
                    val halfW = prop.width / 2f + margin
                    val halfD = prop.depth / 2f + margin
                    val dx = rx - prop.posX
                    val dz = rz - prop.posZ
                    val ox = halfW - kotlin.math.abs(dx)
                    val oz = halfD - kotlin.math.abs(dz)
                    if (ox > 0f && oz > 0f) {
                        collided = true
                        if (ox < oz) {
                            rx = if (dx >= 0f) prop.posX + halfW + 0.02f else prop.posX - halfW - 0.02f
                        } else {
                            rz = if (dz >= 0f) prop.posZ + halfD + 0.02f else prop.posZ - halfD - 0.02f
                        }
                    }
                }
                if (type == InteriorType.UNDERGROUND_GARAGE) {
                    for (colX in listOf(-6.5f, 6.5f)) {
                        for (colZ in listOf(-14.0f, -4.0f, 6.0f)) {
                            val half = 0.65f + margin
                            val dx = rx - colX
                            val dz = rz - colZ
                            val ox = half - kotlin.math.abs(dx)
                            val oz = half - kotlin.math.abs(dz)
                            if (ox > 0f && oz > 0f) {
                                collided = true
                                if (ox < oz) {
                                    rx = if (dx >= 0f) colX + half + 0.02f else colX - half - 0.02f
                                } else {
                                    rz = if (dz >= 0f) colZ + half + 0.02f else colZ - half - 0.02f
                                }
                            }
                        }
                    }
                }
            }
            return if (collided) Pair(rx, rz) else null
        }
    }
}
