package com.example.opencity.model

/**
 * Traffic light at an intersection.
 */
enum class TrafficLightColor {
    GREEN, YELLOW, RED
}

data class TrafficLight(
    val id: String,
    val posX: Float,
    val posZ: Float,
    var state: TrafficLightColor = TrafficLightColor.GREEN,
    var timer: Float = 0f
)

/**
 * Complete procedural city layout definition.
 */
object CityData {

    fun generateBuildings(): List<Building> {
        val list = mutableListOf<Building>()

        // 0. JABHATI GRAND CITY GATEWAY («بوابة جبهتي» — على الساحة الخضراء بوسط المدينة بدون قطع أي شارع)
        list.add(
            Building(
                id = "gate_jabhati",
                name = "Jabhati Grand Gateway",
                arabicName = "«جبهتي» — بوابة المدينة الشامخة والواجهة الرئيسية 🏛️👑",
                type = BuildingType.PARK_GAZEBO,
                district = District.COMMERCIAL,
                posX = -17.5f, posZ = -15f,
                width = 13.0f, height = 22f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.96f, 0.94f, 0.90f, 1f), // Imperial Marble
                colorSecondary = floatArrayOf(1.0f, 0.85f, 0.25f, 1f), // 24K Pure Gold
                roofType = Building.RoofType.FLAT,
                isEnterable = false,
                iconEmoji = "🏛️"
            )
        )

        // 1. DOWNTOWN DISTRICT (Skyscrapers & corporate plazas — Checkerboard spaced so EVERY building sits on its own isolated block!)
        list.add(
            Building(
                id = "skyscraper_apex",
                name = "Apex Tower",
                arabicName = "برج القمة الشاهق",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = -17.5f, posZ = -80f,
                width = 13.5f, height = 75f, depth = 15.5f,
                colorPrimary = floatArrayOf(0.18f, 0.35f, 0.55f, 1f), // Glass blue
                colorSecondary = floatArrayOf(0.85f, 0.9f, 0.95f, 1f),
                roofType = Building.RoofType.TIERED_SPIRE,
                iconEmoji = "🏙️"
            )
        )
        list.add(
            Building(
                id = "skyscraper_lumina",
                name = "Lumina Tower",
                arabicName = "برج لومينا التجاري",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = 55f, posZ = -15f,
                width = 15.5f, height = 62f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.22f, 0.28f, 0.42f, 1f),
                colorSecondary = floatArrayOf(0.95f, 0.75f, 0.25f, 1f), // Gold accents
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🏢"
            )
        )
        list.add(
            Building(
                id = "bank_central",
                name = "Central Bank",
                arabicName = "بنك المدينة المركزي",
                type = BuildingType.BANK,
                district = District.DOWNTOWN,
                posX = 17.5f, posZ = -45f,
                width = 13.5f, height = 28f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.85f, 0.82f, 0.75f, 1f), // Marble
                colorSecondary = floatArrayOf(0.7f, 0.65f, 0.55f, 1f),
                roofType = Building.RoofType.FLAT,
                doorPosX = 17.5f, doorPosZ = -39.2f,
                iconEmoji = "🏦"
            )
        )
        // Skyscraper 3: برج هورايزون المالي العالمي (Horizon Global Mega Tower)
        list.add(
            Building(
                id = "skyscraper_horizon",
                name = "Horizon Global Tower",
                arabicName = "برج هورايزون العالمي المالي (ناطحة سحاب مع مهبط طائرات)",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = -95f, posZ = -80f,
                width = 16f, height = 96f, depth = 16f,
                colorPrimary = floatArrayOf(0.12f, 0.22f, 0.38f, 1f), // Deep Midnight Glass
                colorSecondary = floatArrayOf(0.0f, 0.85f, 1.0f, 1f), // Cyan Neon Glow
                roofType = Building.RoofType.TIERED_SPIRE,
                iconEmoji = "🏙️"
            )
        )
        // Skyscraper 4: برج الماسة الكريستالي الهندسي (Diamond Prism Tower)
        list.add(
            Building(
                id = "skyscraper_diamond",
                name = "Diamond Prism Tower",
                arabicName = "برج الماسة الكريستالي الشاهق",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = 55f, posZ = -80f,
                width = 16f, height = 82f, depth = 16f,
                colorPrimary = floatArrayOf(0.25f, 0.45f, 0.65f, 1f), // Reflective Crystal Azure
                colorSecondary = floatArrayOf(0.95f, 0.85f, 0.25f, 1f), // 24K Gold Crown
                roofType = Building.RoofType.PEAKED,
                iconEmoji = "💎"
            )
        )

        // 2. RESIDENTIAL DISTRICT (Spacious Royal Villa Sector — 38m-84m open green gardens between EVERY estate!)
        // Row 1 (Z = 216f): West (-158f) & East (52f, 136f, 220f, 302f)
        // Villa 1: فيلا النيوكلاسيك الفاخرة (Inside main city block X=35..75, Z=135..175 — clear grass [43.95..66.05]×[143.95..166.05])
        list.add(
            Building(
                id = "villa_player",
                name = "Neoclassical Luxury City Villa",
                arabicName = "فيلا النيوكلاسيك — كومباوند 90 أفينيو (بوابة المدينة)",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 53.8f, posZ = 151.4f,
                width = 11.4f, height = 14.0f, depth = 9.8f,
                colorPrimary = floatArrayOf(0.95f, 0.93f, 0.88f, 1f), // Travertine marble
                colorSecondary = floatArrayOf(0.20f, 0.20f, 0.22f, 1f), // Slate charcoal roof
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 53.8f, doorPosZ = 161.2f,
                iconEmoji = "🏛️",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 2: قصر النخيل العصري
        list.add(
            Building(
                id = "villa_palm",
                name = "Modern Palm Grand Mansion",
                arabicName = "قصر النخيل العصري — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 136f, posZ = 216f,
                width = 24f, height = 18f, depth = 24f,
                colorPrimary = floatArrayOf(0.92f, 0.94f, 0.98f, 1f),
                colorSecondary = floatArrayOf(0.12f, 0.45f, 0.85f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 136f, doorPosZ = 234.5f,
                iconEmoji = "🌴",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 3: قصر وحدائق الشاطئ الملكي
        list.add(
            Building(
                id = "villa_beach",
                name = "Garden Beach Grand Palace",
                arabicName = "قصر وحدائق الشاطئ — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 220f, posZ = 216f,
                width = 25f, height = 19f, depth = 24f,
                colorPrimary = floatArrayOf(0.98f, 0.96f, 0.90f, 1f),
                colorSecondary = floatArrayOf(0.92f, 0.70f, 0.15f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 220f, doorPosZ = 234.5f,
                iconEmoji = "🏖️",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 4: قصر الأفق البانورامي (Southwest Meadow Row 2 — -158f, 298f)
        list.add(
            Building(
                id = "villa_skyline",
                name = "Skyline Panorama Grand Estate",
                arabicName = "قصر الأفق البانورامي — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = -158f, posZ = 298f,
                width = 26f, height = 18f, depth = 24f,
                colorPrimary = floatArrayOf(0.90f, 0.94f, 0.97f, 1f),
                colorSecondary = floatArrayOf(0.15f, 0.75f, 0.65f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = -158f, doorPosZ = 316.5f,
                iconEmoji = "🌅",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 5: قصر مارينا الملكي الفاخر (Southwest Meadow Row 1 — -158f, 216f)
        list.add(
            Building(
                id = "villa_waterfront_1",
                name = "Marina Horizon Royal Palace",
                arabicName = "قصر مارينا الملكي — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = -158f, posZ = 216f,
                width = 26f, height = 20f, depth = 24f,
                colorPrimary = floatArrayOf(0.98f, 0.98f, 0.98f, 1f),
                colorSecondary = floatArrayOf(0.15f, 0.55f, 0.85f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = -158f, doorPosZ = 234.5f,
                iconEmoji = "🌊",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Row 2 East (Z = 298f): X = 52f, 136f, 220f, 302f
        // Villa 6: قصر الواحة الفاخر
        list.add(
            Building(
                id = "villa_oasis",
                name = "Oasis Luxury Grand Estate",
                arabicName = "قصر الواحة الفاخر — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 52f, posZ = 298f,
                width = 25f, height = 19f, depth = 24f,
                colorPrimary = floatArrayOf(0.97f, 0.95f, 0.90f, 1f),
                colorSecondary = floatArrayOf(0.15f, 0.65f, 0.35f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 52f, doorPosZ = 316.5f,
                iconEmoji = "🏰",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 7: قصر القمة الكلاسيكي
        list.add(
            Building(
                id = "villa_summit",
                name = "Summit Classic Grand Estate",
                arabicName = "قصر القمة الكلاسيكي — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 136f, posZ = 298f,
                width = 24f, height = 18f, depth = 24f,
                colorPrimary = floatArrayOf(0.94f, 0.92f, 0.88f, 1f),
                colorSecondary = floatArrayOf(0.85f, 0.20f, 0.25f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 136f, doorPosZ = 316.5f,
                iconEmoji = "💎",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 8: قصر الصنوبر الملكي
        list.add(
            Building(
                id = "villa_pine",
                name = "Royal Pine Grand Palace",
                arabicName = "قصر الصنوبر الملكي — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 220f, posZ = 298f,
                width = 25f, height = 20f, depth = 24f,
                colorPrimary = floatArrayOf(0.95f, 0.94f, 0.88f, 1f),
                colorSecondary = floatArrayOf(0.28f, 0.55f, 0.25f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 220f, doorPosZ = 316.5f,
                iconEmoji = "🌲",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 9: فيلا الزجاج العصرية الكبرى (East Row 1 — 302f, 216f)
        list.add(
            Building(
                id = "villa_glass_modern",
                name = "Ultra-Modern Glass Villa",
                arabicName = "فيلا الزجاج العصرية — كومباوند 90 أفينيو",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 302f, posZ = 216f,
                width = 24f, height = 18f, depth = 24f,
                colorPrimary = floatArrayOf(0.20f, 0.22f, 0.28f, 1f),
                colorSecondary = floatArrayOf(0.10f, 0.85f, 1.0f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 302f, doorPosZ = 234.5f,
                iconEmoji = "💠",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        // Villa 10: قصر الريف الإمبراطوري الأعظم (100M)
        list.add(
            Building(
                id = "villa_palace",
                name = "Imperial Grand Country Palace",
                arabicName = "قصر الريف الإمبراطوري — كومباوند 90 أفينيو (100M$)",
                type = BuildingType.RESIDENTIAL_VILLA,
                district = District.RESIDENTIAL,
                posX = 302f, posZ = 298f,
                width = 26f, height = 24f, depth = 26f,
                colorPrimary = floatArrayOf(0.98f, 0.98f, 0.96f, 1f),
                colorSecondary = floatArrayOf(0.68f, 0.35f, 0.90f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 302f, doorPosZ = 317.5f,
                iconEmoji = "👑",
                streetZone = StreetZone.AVENUE_90_COMPOUND
            )
        )
        list.add(
            Building(
                id = "apartments_oasis",
                name = "Oasis Residences",
                arabicName = "عمارة الواحة السكنية",
                type = BuildingType.APARTMENT_BLOCK,
                district = District.RESIDENTIAL,
                posX = -92f, posZ = 298f,
                width = 16f, height = 30f, depth = 14f,
                colorPrimary = floatArrayOf(0.82f, 0.78f, 0.72f, 1f),
                colorSecondary = floatArrayOf(0.4f, 0.35f, 0.3f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🏢"
            )
        )

        // 2.5. CENTRAL STANDARD RESIDENTIAL HOMES (متباعدة في مربعات متفرقة عبر المدينة لخصوصية كاملة)
        list.add(
            Building(
                id = "standard_house_1",
                name = "Modern Family Home 1",
                arabicName = "البيت السكني العصري 1 (مع كراج سيارة واحدة)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 93.0f, posZ = 17.5f,
                width = 11.5f, height = 9f, depth = 12f,
                colorPrimary = floatArrayOf(0.92f, 0.90f, 0.86f, 1f), // Clean beige
                colorSecondary = floatArrayOf(0.35f, 0.45f, 0.60f, 1f), // Slate blue roof
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 93.0f, doorPosZ = 24.4f,
                iconEmoji = "🏠",
                facadeDescription = "بيت سكني عصري 6 شبابيك مع كراج يكفي سيارة واحدة.",
                listingBadge = "للبيع: 35,000 عملة",
                salePrice = 35000,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "standard_house_2",
                name = "Modern Family Home 2",
                arabicName = "البيت السكني العصري 2 (مع كراج سيارة واحدة)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 173.0f, posZ = 17.5f,
                width = 11.5f, height = 9f, depth = 12f,
                colorPrimary = floatArrayOf(0.95f, 0.92f, 0.88f, 1f),
                colorSecondary = floatArrayOf(0.65f, 0.35f, 0.25f, 1f), // Terracotta roof
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 173.0f, doorPosZ = 24.4f,
                iconEmoji = "🏡",
                facadeDescription = "بيت سكني عائلي 6 شبابيك مع كراج يكفي سيارة واحدة.",
                listingBadge = "للبيع: 45,000 عملة",
                salePrice = 45000,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "standard_house_3",
                name = "Modern Family Duplex 3",
                arabicName = "البيت السكني العصري 3 (مع كراج سيارة واحدة)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 173.0f, posZ = 82.5f,
                width = 11.5f, height = 9.5f, depth = 12f,
                colorPrimary = floatArrayOf(0.90f, 0.92f, 0.95f, 1f),
                colorSecondary = floatArrayOf(0.20f, 0.50f, 0.35f, 1f), // Forest green roof
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 173.0f, doorPosZ = 89.4f,
                iconEmoji = "🏘️",
                facadeDescription = "دوبلكس سكني 6 شبابيك مع كراج يكفي سيارة واحدة.",
                listingBadge = "للبيع: 65,000 عملة",
                salePrice = 65000,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "standard_house_4",
                name = "Modern Family Townhouse 4",
                arabicName = "البيت السكني العصري 4 (مع كراج سيارة واحدة)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = -137.0f, posZ = -45f,
                width = 11.5f, height = 9.5f, depth = 9.5f,
                colorPrimary = floatArrayOf(0.94f, 0.90f, 0.85f, 1f),
                colorSecondary = floatArrayOf(0.75f, 0.40f, 0.20f, 1f), // Warm copper roof
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = -137.0f, doorPosZ = -39.2f,
                iconEmoji = "🏡",
                facadeDescription = "تاون هاوس حديث 6 شبابيك مع كراج يكفي سيارة واحدة.",
                listingBadge = "للبيع: 85,000 عملة",
                salePrice = 85000,
                hasSingleCarGarage = true
            )
        )

        // 3. COMMERCIAL & SERVICES AVENUE (ALL strictly on isolated Green Grass Blocks with wide open space around every mall!)
        list.add(
            Building(
                id = "dining_avenue_complex",
                name = "Southern Dining Avenue",
                arabicName = "مجمع المطاعم والخدمات (Dining Avenue) 🍲🍔🥐",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = -135f, posZ = -120f,
                width = 16f, height = 14f, depth = 15f,
                colorPrimary = floatArrayOf(0.88f, 0.32f, 0.18f, 1f), // Gourmet Orange & Terracotta
                colorSecondary = floatArrayOf(1.0f, 0.85f, 0.25f, 1f), // 24K Gold Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.DINING_AVENUE,
                doorPosX = -135f, doorPosZ = -111.5f,
                iconEmoji = "🍽️"
            )
        )
        list.add(
            Building(
                id = "supermarket_central",
                name = "City Supermarket",
                arabicName = "سوبرماركت المدينة المركزي",
                type = BuildingType.SUPERMARKET,
                district = District.COMMERCIAL,
                posX = -55f, posZ = 82.5f,
                width = 16f, height = 12f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.2f, 0.55f, 0.8f, 1f), // Vibrant blue
                colorSecondary = floatArrayOf(0.95f, 0.6f, 0.15f, 1f), // Orange signage
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.SUPERMARKET,
                doorPosX = -55f, doorPosZ = 75.2f,
                iconEmoji = "🛒"
            )
        )
        list.add(
            Building(
                id = "cafe_city",
                name = "City Restaurant & Lounge",
                arabicName = "مطعم واستراحة المدينة للأكل والشرب 🍖🥤",
                type = BuildingType.RESTAURANT_CAFE,
                district = District.COMMERCIAL,
                posX = 55f, posZ = 50f,
                width = 15.5f, height = 10f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.85f, 0.35f, 0.15f, 1f), // Warm appetizing terracotta / restaurant orange
                colorSecondary = floatArrayOf(1.0f, 0.85f, 0.3f, 1f), // Gold accents
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.CAFE,
                doorPosX = 55f, doorPosZ = 55.8f,
                iconEmoji = "🍖"
            )
        )
        list.add(
            Building(
                id = "car_parts_shop",
                name = "Auto Parts & Tuning Market",
                arabicName = "متجر قطع ولوازم وتعديل السيارات 🔧🏎️",
                type = BuildingType.SUPERMARKET,
                district = District.COMMERCIAL,
                posX = -95f, posZ = -15f,
                width = 16f, height = 10f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.14f, 0.16f, 0.22f, 1f), // Modern Dark Slate
                colorSecondary = floatArrayOf(0.1f, 0.8f, 1.0f, 1f), // Electric Cyan Neon
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.CAR_PARTS_SHOP,
                doorPosX = -95f, doorPosZ = -9.2f,
                iconEmoji = "🔧"
            )
        )
        list.add(
            Building(
                id = "mall_grand",
                name = "Grand City Mall",
                arabicName = "المول التجاري الكبير (أواعي • خضار • أكل • طاولات) 🏬",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = -95f, posZ = 117.5f,
                width = 16f, height = 24f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.7f, 0.35f, 0.65f, 1f), // Magenta/purple modern
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.GRAND_MALL,
                doorPosX = -95f, doorPosZ = 124.8f,
                iconEmoji = "🏬",
                facadeDescription = "مول متكامل: محلات أواعي وماركات + سوق خضار وفواكه + مطاعم وسوبرماركت + طاولات طعام كثيرة!"
            )
        )
        // FLAGSHIP NIKE & ADIDAS CLOTHING & PANTS STORE («متجر ومول الأواعي والماركات: نايكي وأديداس»)
        list.add(
            Building(
                id = "nike_adidas_fashion_mall",
                name = "Nike & Adidas Sportswear Mall",
                arabicName = "متجر الأواعي والماركات: نايكي • أديداس 👕👖👟",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = 17.5f, posZ = 82.5f,
                width = 13.5f, height = 13f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.11f, 0.13f, 0.19f, 1f), // Sleek Brand Obsidian
                colorSecondary = floatArrayOf(1.0f, 0.42f, 0.0f, 1f), // Nike Orange & Electric Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.CLOTHING_STORE,
                doorPosX = 17.5f, doorPosZ = 75.2f,
                iconEmoji = "👕",
                facadeDescription = "شركات نايكي (Nike) وأديداس (Adidas) وبناطيل وأحذية وإكسسوارات!"
            )
        )
        list.add(
            Building(
                id = "real_estate_agency",
                name = "Luxury Real Estate & Villa Center",
                arabicName = "شركة ومقر بيع الفلل الفاخرة 🏢🏡",
                type = BuildingType.BANK,
                district = District.COMMERCIAL,
                posX = -55f, posZ = 155f,
                width = 16f, height = 15f, depth = 15f,
                colorPrimary = floatArrayOf(0.10f, 0.20f, 0.35f, 1f), // Deep Luxury Navy Glass
                colorSecondary = floatArrayOf(1.0f, 0.85f, 0.25f, 1f), // 24K Polished Gold Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.REAL_ESTATE_AGENCY,
                doorPosX = -55f, doorPosZ = 146.5f,
                iconEmoji = "🏡"
            )
        )
        // PLACE FOR SELLING METRO CARDS («مركزي لبيع كروت المترو»)
        list.add(
            Building(
                id = "metro_card_sales_office",
                name = "Metro Card Sales Center",
                arabicName = "مركزي لبيع كروت المترو 💳🚇",
                type = BuildingType.BANK,
                district = District.COMMERCIAL,
                streetZone = StreetZone.MAIN_STREET,
                posX = -17.5f, posZ = 155.0f,
                width = 14.5f, height = 10.5f, depth = 13.0f,
                colorPrimary = floatArrayOf(0.06f, 0.30f, 0.48f, 1f), // Modern Transit Cyan-Navy
                colorSecondary = floatArrayOf(0.14f, 0.86f, 0.96f, 1f), // Electric Metro Cyan Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.METRO_CARD_SHOP,
                doorPosX = -17.5f, doorPosZ = 162.6f,
                iconEmoji = "💳",
                facadeDescription = "محلك الخاص لبيع وشحن كروت المترو للركاب وكسب المال!"
            )
        )
        // PLACE FOR PURCHASING VILLAS, FOOD, DRINKS & WATER («مكان شراء الفلل والطعام والشراب والماء»)
        list.add(
            Building(
                id = "mall_villas_food_water",
                name = "Villas, Food, Drink & Water Center",
                arabicName = "مكان شراء الفلل والطعام والشراب والماء 🏡🍖🥤💧",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = -28f, posZ = 216f,
                width = 16f, height = 16f, depth = 14f,
                colorPrimary = floatArrayOf(0.08f, 0.45f, 0.55f, 1f), // Elegant Cyan/Emerald Aqua Glass
                colorSecondary = floatArrayOf(1.0f, 0.75f, 0.20f, 1f), // Golden Sun Glow Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.VILLAS_FOOD_WATER_MALL,
                doorPosX = -28f, doorPosZ = 224.2f,
                iconEmoji = "🏡"
            )
        )
        // PLACE FOR BUYING STREET HOUSES («مكان ومكتب شراء بيوت الشارع»)
        list.add(
            Building(
                id = "street_houses_market",
                name = "Street Houses Real Estate Office",
                arabicName = "مكان شراء بيوت الشارع 🏘️🔑",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = 95f, posZ = 82.5f,
                width = 16f, height = 13f, depth = 11.5f,
                colorPrimary = floatArrayOf(0.07f, 0.38f, 0.30f, 1f), // Emerald Showroom Glass
                colorSecondary = floatArrayOf(0.98f, 0.78f, 0.16f, 1f), // Golden Real Estate Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.STREET_HOUSES_MARKET,
                doorPosX = 95f, doorPosZ = 89.2f,
                iconEmoji = "🔑",
                facadeDescription = "المكتب الرسمي لشراء جميع بيوت الشارع في كل أحياء وشوارع المدينة."
            )
        )
        // MOTOR CITY CAR MARKET («مدينة المحركات» — على المساحة الخضراء الشرقية بعيداً عن جميع المباني والشوارع!)
        list.add(
            Building(
                id = "motor_city_dealership",
                name = "Motor City Auto Market",
                arabicName = "سوق سيارات «مدينة المحركات»",
                type = BuildingType.CAR_DEALERSHIP,
                district = District.COMMERCIAL,
                posX = 306f, posZ = 144f,
                width = 34f, height = 15f, depth = 22f,
                colorPrimary = floatArrayOf(0.12f, 0.15f, 0.22f, 1f), // Modern Dark Slate
                colorSecondary = floatArrayOf(0.95f, 0.45f, 0.1f, 1f), // Glowing Orange Neon Trim
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.NONE, // Showroom is seamlessly integrated in open 3D world + dedicated UI
                doorPosX = 306f, doorPosZ = 131f,
                iconEmoji = "🏎️"
            )
        )
        // GRAND IMAX & CINEMA COMPLEX («مجمع سينما وبوليفارد آيماكس العالمي»)
        list.add(
            Building(
                id = "cinema_imax_complex",
                name = "Grand IMAX & Cinema Theater",
                arabicName = "مجمع سينما وبوليفارد آيماكس العالمي 🎬🍿",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = -135f, posZ = 155f,
                width = 16f, height = 19f, depth = 15f,
                colorPrimary = floatArrayOf(0.10f, 0.12f, 0.18f, 1f), // Sleek Cinema Obsidian
                colorSecondary = floatArrayOf(1.0f, 0.20f, 0.40f, 1f), // Neon Magenta Marquee
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.CAFE,
                doorPosX = -135f, doorPosZ = 163.5f,
                iconEmoji = "🎬"
            )
        )
        // WATERFRONT MARINA & YACHT CLUB («كورنيش وبحيرة مارينا ونادي اليخوت»)
        list.add(
            Building(
                id = "waterfront_marina",
                name = "Marina Promenade & Yacht Club",
                arabicName = "نادي اليخوت وكورنيش مارينا المدينة والبحيرة ⛵🚤",
                type = BuildingType.RESTAURANT_CAFE,
                district = District.COMMERCIAL,
                posX = 236f, posZ = -45f,
                width = 22f, height = 12f, depth = 16f,
                colorPrimary = floatArrayOf(0.96f, 0.96f, 0.98f, 1f), // Nautical White
                colorSecondary = floatArrayOf(0.08f, 0.50f, 0.85f, 1f), // Deep Ocean Blue
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.CAFE,
                doorPosX = 236f, doorPosZ = -36f,
                iconEmoji = "⛵"
            )
        )

        // 4. TRANSIT HUB (Central Bus Terminal, Train Station, East International Airport — all spaced wide apart!)
        list.add(
            Building(
                id = "travel_terminal",
                name = "Central Transit Station",
                arabicName = "محطة السفر المركزية الكبرى",
                type = BuildingType.CENTRAL_BUS_TERMINAL,
                district = District.TRAVEL_HUB,
                posX = -55f, posZ = 17.5f,
                width = 16f, height = 18f, depth = 10.5f,
                colorPrimary = floatArrayOf(0.15f, 0.45f, 0.65f, 1f),
                colorSecondary = floatArrayOf(0.95f, 0.85f, 0.25f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.TRAVEL_TERMINAL,
                doorPosX = -55f, doorPosZ = 23.8f,
                iconEmoji = "🚌"
            )
        )
        list.add(
            Building(
                id = "train_station",
                name = "Grand Rail Station",
                arabicName = "محطة قطار المدينة السريع",
                type = BuildingType.TRAIN_STATION,
                district = District.TRAVEL_HUB,
                posX = -135f, posZ = 17.5f,
                width = 16f, height = 15f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.4f, 0.42f, 0.45f, 1f),
                colorSecondary = floatArrayOf(0.85f, 0.2f, 0.2f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🚆"
            )
        )
        list.add(
            Building(
                id = "airport_hangar",
                name = "City Regional Airport",
                arabicName = "مطار المدينة الدولي",
                type = BuildingType.AIRPORT_HANGAR,
                district = District.TRAVEL_HUB,
                posX = 296f, posZ = -40f,
                width = 26f, height = 18f, depth = 26f,
                colorPrimary = floatArrayOf(0.85f, 0.85f, 0.9f, 1f),
                colorSecondary = floatArrayOf(0.2f, 0.4f, 0.7f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "✈️"
            )
        )
        // AIRPORT CONTROL TOWER («برج مراقبة المطار الدولي مع رادار ملاحي»)
        list.add(
            Building(
                id = "airport_control_tower",
                name = "Airport Air Traffic Control Tower",
                arabicName = "برج مراقبة المطار الدولي (مع رادار ملاحي وكاميرات رصد)",
                type = BuildingType.MODERN_TOWER,
                district = District.TRAVEL_HUB,
                posX = 296f, posZ = -85f,
                width = 13f, height = 52f, depth = 13f,
                colorPrimary = floatArrayOf(0.92f, 0.92f, 0.95f, 1f), // Airport Grey
                colorSecondary = floatArrayOf(0.15f, 0.65f, 0.95f, 1f), // Glass observation cab
                roofType = Building.RoofType.FLAT,
                iconEmoji = "📡"
            )
        )
        // AIRPORT INTERNATIONAL TERMINAL GATES («بوابات المطار الدولية وصالة الركاب»)
        list.add(
            Building(
                id = "airport_runway_terminal",
                name = "International Terminal Gates",
                arabicName = "بوابات المطار الدولية وصالة الركاب الفاخرة",
                type = BuildingType.CENTRAL_BUS_TERMINAL,
                district = District.TRAVEL_HUB,
                posX = 296f, posZ = -135f,
                width = 28f, height = 16f, depth = 22f,
                colorPrimary = floatArrayOf(0.88f, 0.90f, 0.94f, 1f),
                colorSecondary = floatArrayOf(0.95f, 0.75f, 0.15f, 1f), // Golden canopy
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🛫"
            )
        )

        // 5. CIVIC & PUBLIC SERVICES (Hospital, Police, Fire, Mosque, University — all separated by empty park blocks!)
        list.add(
            Building(
                id = "hospital_central",
                name = "Central Hospital",
                arabicName = "مستشفى الأمل المركزي",
                type = BuildingType.HOSPITAL,
                district = District.SERVICES,
                posX = 135f, posZ = 50f,
                width = 16f, height = 24f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f), // Pure white
                colorSecondary = floatArrayOf(0.9f, 0.15f, 0.15f, 1f), // Red cross
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.HOSPITAL,
                doorPosX = 135f, doorPosZ = 55.8f,
                iconEmoji = "🏥"
            )
        )
        list.add(
            Building(
                id = "police_headquarters",
                name = "Police Headquarters",
                arabicName = "مركز الشرطة والأمن العام",
                type = BuildingType.POLICE_STATION,
                district = District.SERVICES,
                posX = -93.5f, posZ = -161.5f,
                width = 11.5f, height = 16f, depth = 9.5f,
                colorPrimary = floatArrayOf(0.2f, 0.3f, 0.45f, 1f), // Navy
                colorSecondary = floatArrayOf(0.15f, 0.6f, 0.9f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.POLICE_STATION,
                doorPosX = -93.5f, doorPosZ = -155.7f,
                iconEmoji = "👮"
            )
        )
        list.add(
            Building(
                id = "grand_mosque",
                name = "Grand City Mosque",
                arabicName = "المسجد الكبير والمنارة",
                type = BuildingType.GRAND_MOSQUE,
                district = District.SERVICES,
                posX = 175f, posZ = -120f,
                width = 12.5f, height = 22f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.95f, 0.94f, 0.88f, 1f), // Off-white marble
                colorSecondary = floatArrayOf(0.15f, 0.65f, 0.35f, 1f), // Emerald & gold
                roofType = Building.RoofType.DOMED,
                iconEmoji = "🕌"
            )
        )
        list.add(
            Building(
                id = "fire_station",
                name = "Fire & Rescue Station",
                arabicName = "محطة الإطفاء والدفاع المدني",
                type = BuildingType.FIRE_STATION,
                district = District.SERVICES,
                posX = -55f, posZ = -45f,
                width = 16f, height = 15f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.85f, 0.22f, 0.18f, 1f), // Fire engine red
                colorSecondary = floatArrayOf(0.95f, 0.85f, 0.2f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🚒"
            )
        )
        list.add(
            Building(
                id = "university_campus",
                name = "City University",
                arabicName = "جامعة العلوم والتكنولوجيا",
                type = BuildingType.UNIVERSITY,
                district = District.SERVICES,
                posX = 135f, posZ = -160f,
                width = 16.5f, height = 20f, depth = 16.5f,
                colorPrimary = floatArrayOf(0.72f, 0.45f, 0.3f, 1f), // Red brick
                colorSecondary = floatArrayOf(0.9f, 0.9f, 0.9f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🎓"
            )
        )

        list.add(
            Building(
                id = "stadium_arena",
                name = "Sports Arena",
                arabicName = "ملعب المدينة الرياضي",
                type = BuildingType.STADIUM,
                district = District.SERVICES,
                posX = 236f, posZ = 22f,
                width = 38f, height = 16f, depth = 38f,
                colorPrimary = floatArrayOf(0.35f, 0.65f, 0.35f, 1f), // Pitch green
                colorSecondary = floatArrayOf(0.3f, 0.5f, 0.7f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "⚽"
            )
        )
        // GRAND CITY CULTURAL MUSEUM & OPERA («متحف المدينة ودار الأوبرا الثقافية الكبرى»)
        list.add(
            Building(
                id = "museum_culture_center",
                name = "Grand City Cultural Museum & Opera",
                arabicName = "متحف المدينة ودار الأوبرا الثقافية الكبرى 🏛️🎭",
                type = BuildingType.PARK_GAZEBO,
                district = District.SERVICES,
                posX = 135f, posZ = -80f,
                width = 15f, height = 22f, depth = 12f,
                colorPrimary = floatArrayOf(0.95f, 0.92f, 0.85f, 1f), // Classical Marble
                colorSecondary = floatArrayOf(0.85f, 0.65f, 0.15f, 1f), // Golden Pediment
                roofType = Building.RoofType.PEAKED,
                iconEmoji = "🏛️"
            )
        )
        // OLYMPIC AQUATIC & SPORTS CLUB («المجمع المائي والأولمبي الترفيهي»)
        list.add(
            Building(
                id = "olympic_aquatic_center",
                name = "Olympic Aquatic & Sports Club",
                arabicName = "المجمع المائي والأولمبي الترفيهي 🏊‍♂️🏆",
                type = BuildingType.STADIUM,
                district = District.SERVICES,
                posX = 236f, posZ = 102f,
                width = 34f, height = 16f, depth = 34f,
                colorPrimary = floatArrayOf(0.12f, 0.55f, 0.85f, 1f), // Aquatic Blue
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.98f, 1f), // Clean White
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🏊"
            )
        )
        list.add(
            Building(
                id = "gas_station_main",
                name = "Express Gas Station",
                arabicName = "محطة وقود إكسبريس",
                type = BuildingType.GAS_STATION,
                district = District.COMMERCIAL,
                posX = -95f, posZ = 50f,
                width = 13.5f, height = 8f, depth = 9.8f,
                colorPrimary = floatArrayOf(0.9f, 0.2f, 0.2f, 1f),
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.2f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "⛽"
            )
        )
        // 6.5. UNDERGROUND CITY PARKING GARAGE PORTAL («كراج المدينة تحت الأرض لتصفية السيارات»)
        list.add(
            Building(
                id = "underground_city_garage",
                name = "Underground City Parking Garage",
                arabicName = "كراج المدينة تحت الأرض (لتصفية السيارات) 🅿️⬇️",
                type = BuildingType.PARKING_LOT,
                district = District.COMMERCIAL,
                posX = 17.5f, posZ = 16.5f,
                width = 13.5f, height = 4.2f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.18f, 0.20f, 0.24f, 1f),
                colorSecondary = floatArrayOf(0.08f, 0.72f, 0.95f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.UNDERGROUND_GARAGE,
                doorPosX = 17.5f, doorPosZ = 24.5f,
                iconEmoji = "🅿️",
                facadeDescription = "منحدر دخول الكراج تحت الأرض — تُصف فيه جميع سيارات المدينة لتبقى الشوارع فارغة وسالكة!"
            )
        )

        // 7. INDUSTRIAL DISTRICT
        list.add(
            Building(
                id = "factory_zone",
                name = "Industrial Complex",
                arabicName = "مجمع المصانع والمستودعات",
                type = BuildingType.FACTORY,
                district = District.INDUSTRIAL,
                posX = -175f, posZ = -160f,
                width = 16f, height = 18f, depth = 16f,
                colorPrimary = floatArrayOf(0.45f, 0.45f, 0.48f, 1f),
                colorSecondary = floatArrayOf(0.9f, 0.5f, 0.1f, 1f),
                roofType = Building.RoofType.FLAT,
                iconEmoji = "🏭"
            )
        )

        // 8. SUBURBS & COUNTRYSIDE
        list.add(
            Building(
                id = "farmhouse_green",
                name = "Green Acres Farm",
                arabicName = "مزرعة الريف الهادئ (مع كراج سيارة واحدة)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.SUBURBS,
                posX = -137.0f, posZ = 82.5f,
                width = 11.5f, height = 9f, depth = 12f,
                colorPrimary = floatArrayOf(0.75f, 0.3f, 0.2f, 1f), // Barn red
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f),
                roofType = Building.RoofType.PEAKED,
                iconEmoji = "🌾",
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "windmill_country",
                name = "Windmill Ridge",
                arabicName = "طاحونة الهواء الريفية",
                type = BuildingType.WINDMILL,
                district = District.SUBURBS,
                posX = -175f, posZ = 50f,
                width = 10f, height = 22f, depth = 9.5f,
                colorPrimary = floatArrayOf(0.9f, 0.88f, 0.82f, 1f),
                colorSecondary = floatArrayOf(0.4f, 0.3f, 0.2f, 1f),
                roofType = Building.RoofType.TIERED_SPIRE,
                iconEmoji = "💨"
            )
        )

        // 9. FOUR CATEGORIZED CITY STREETS (كل بيت ومجمع في مربع منفصل بمساحة خضراء محيطة واسعة!)
        // 9A. الشارع الرئيسي (Main Street) - حيوي، تجاري، مزدحم بالحركة
        list.add(
            Building(
                id = "main_glass_complex",
                name = "Main Street Glass Residences",
                arabicName = "مجمع الشارع الرئيسي الزجاجي",
                type = BuildingType.MODERN_TOWER,
                district = District.COMMERCIAL,
                posX = -17.5f, posZ = 117.5f,
                width = 13.5f, height = 26f, depth = 12.5f,
                colorPrimary = floatArrayOf(0.16f, 0.38f, 0.56f, 1f),
                colorSecondary = floatArrayOf(0.75f, 0.92f, 0.98f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = -17.5f, doorPosZ = 124.8f,
                iconEmoji = "🏢",
                streetZone = StreetZone.MAIN_STREET,
                facadeDescription = "مجمع سكني حديث بواجهات زجاجية وإطلالة على الشارع الرئيسي.",
                listingBadge = "بيت للبيع مع إطلالة بانورامية",
                salePrice = 150000
            )
        )
        list.add(
            Building(
                id = "main_deluxe_upper_apt",
                name = "Deluxe Upper Floor Apartment",
                arabicName = "شقة ديلوكس بالطابق العلوي",
                type = BuildingType.APARTMENT_BLOCK,
                district = District.COMMERCIAL,
                posX = -175f, posZ = -80f,
                width = 15.5f, height = 22f, depth = 14.5f,
                colorPrimary = floatArrayOf(0.86f, 0.82f, 0.74f, 1f),
                colorSecondary = floatArrayOf(0.95f, 0.65f, 0.15f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = -175f, doorPosZ = -71.7f,
                iconEmoji = "🗝️",
                streetZone = StreetZone.MAIN_STREET,
                facadeDescription = "شقة ديلوكس في الطابق العلوي - متاح للإيجار.",
                listingBadge = "منزل للإيجار الفوري - اضغط للدخول",
                rentPrice = 150
            )
        )
        list.add(
            Building(
                id = "main_shop_apartments",
                name = "Main Street Retail & Lofts",
                arabicName = "محلات وشقق الشارع الرئيسي",
                type = BuildingType.SHOPPING_MALL,
                district = District.COMMERCIAL,
                posX = -175f, posZ = -15f,
                width = 15.5f, height = 18f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.24f, 0.28f, 0.36f, 1f),
                colorSecondary = floatArrayOf(0.98f, 0.48f, 0.12f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.CAFE,
                doorPosX = -175f, doorPosZ = -9.2f,
                iconEmoji = "🛍️",
                streetZone = StreetZone.MAIN_STREET,
                facadeDescription = "محل تجاري في الطابق الأرضي مع شقق سكنية بالأعلى.",
                listingBadge = "طرق الباب • دخول فوري"
            )
        )
        list.add(
            Building(
                id = "main_investment_billboard",
                name = "Main Street Investment Tower",
                arabicName = "برج ولوحة استثمار الشارع الرئيسي",
                type = BuildingType.MODERN_TOWER,
                district = District.COMMERCIAL,
                posX = 17.5f, posZ = 155f,
                width = 13.5f, height = 28f, depth = 15f,
                colorPrimary = floatArrayOf(0.12f, 0.20f, 0.32f, 1f),
                colorSecondary = floatArrayOf(1.0f, 0.82f, 0.15f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.REAL_ESTATE_AGENCY,
                doorPosX = 17.5f, doorPosZ = 163.5f,
                iconEmoji = "📢",
                streetZone = StreetZone.MAIN_STREET,
                facadeDescription = "لوحة إعلانية: \"استثمر في عقارات الشارع الرئيسي اليوم\".",
                listingBadge = "السعر المعروض: 150,000 عملة",
                salePrice = 150000
            )
        )

        // 9B. الحي السكني الهادئ (Residential Suburbs) - متباعد بمربعات شطرنجية خضراء واسعة
        list.add(
            Building(
                id = "suburb_corner_cottage",
                name = "Charming Corner Cottage",
                arabicName = "البيت الريفي الساحر على الناصية",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 53.0f, posZ = 117.5f,
                width = 11.5f, height = 9.5f, depth = 12f,
                colorPrimary = floatArrayOf(0.96f, 0.92f, 0.84f, 1f),
                colorSecondary = floatArrayOf(0.28f, 0.56f, 0.32f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 53.0f, doorPosZ = 124.5f,
                iconEmoji = "🏡",
                streetZone = StreetZone.RESIDENTIAL_SUBURBS,
                facadeDescription = "بيت ريفي ساحر على ناصية الشارع مع كراج يتسع لسيارة واحدة فقط.",
                listingBadge = "منزل للإيجار الفوري - اضغط للدخول",
                rentPrice = 120,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "suburb_family_yard",
                name = "Warm Family Backyard Home",
                arabicName = "منزل الجار العائلي بحديقة خلفية",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 93.0f, posZ = 155f,
                width = 11.5f, height = 10f, depth = 13.5f,
                colorPrimary = floatArrayOf(0.94f, 0.88f, 0.78f, 1f),
                colorSecondary = floatArrayOf(0.76f, 0.36f, 0.22f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 93.0f, doorPosZ = 162.8f,
                iconEmoji = "🌳",
                streetZone = StreetZone.RESIDENTIAL_SUBURBS,
                facadeDescription = "منزل عائلي دافئ بحديقة خلفية واسعة وكراج سيارة واحدة فقط.",
                listingBadge = "كراج سيارة واحدة جنب البيت",
                houseSideQuest = HouseSideQuest.REPAIR_NEIGHBOR_FENCE,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "suburb_villa_garage",
                name = "Suburban Villa with Private Garage",
                arabicName = "فيلا الضواحي مع مرآب خاص",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 133.0f, posZ = 117.5f,
                width = 11.5f, height = 12f, depth = 12f,
                colorPrimary = floatArrayOf(0.95f, 0.95f, 0.96f, 1f),
                colorSecondary = floatArrayOf(0.20f, 0.24f, 0.32f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 133.0f, doorPosZ = 124.5f,
                iconEmoji = "🏠",
                streetZone = StreetZone.RESIDENTIAL_SUBURBS,
                facadeDescription = "فيلا مستقلة مع كراج سيارات خاص يتسع لسيارة واحدة فقط.",
                listingBadge = "السعر المعروض: 150,000 عملة",
                salePrice = 150000,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "suburb_four_bed",
                name = "4-Bedroom Grand Family House",
                arabicName = "منزل العائلات الكبيرة (4 غرف نوم)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.RESIDENTIAL,
                posX = 173.0f, posZ = 155f,
                width = 11.5f, height = 11f, depth = 13.5f,
                colorPrimary = floatArrayOf(0.92f, 0.94f, 0.90f, 1f),
                colorSecondary = floatArrayOf(0.18f, 0.45f, 0.65f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 173.0f, doorPosZ = 162.8f,
                iconEmoji = "👨‍👩‍👧‍👦",
                streetZone = StreetZone.RESIDENTIAL_SUBURBS,
                facadeDescription = "منزل مخصص للعائلات الكبيرة - يتضمن 4 غرف نوم وكراج لسيارة واحدة فقط.",
                listingBadge = "بيت للبيع مع إطلالة بانورامية",
                salePrice = 150000,
                hasSingleCarGarage = true
            )
        )

        // 9C. الحي القديم / التاريخي (Old Town / Historic District) - متباعد بمربعات منفصلة
        list.add(
            Building(
                id = "oldtown_heritage_tile",
                name = "Antique Tiled Heritage House",
                arabicName = "المنزل التراثي العتيق ذو القرميد",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.SUBURBS,
                posX = 93.0f, posZ = -120f,
                width = 11.5f, height = 10f, depth = 13.5f,
                colorPrimary = floatArrayOf(0.84f, 0.74f, 0.58f, 1f),
                colorSecondary = floatArrayOf(0.68f, 0.24f, 0.16f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 93.0f, doorPosZ = -112.2f,
                iconEmoji = "🏺",
                streetZone = StreetZone.OLD_TOWN,
                facadeDescription = "منزل تراثي عتيق يتميز بأسقف قرميدية وكراج يتسع لسيارة واحدة فقط.",
                listingBadge = "تاريخ التجديد: 2024 • متاح للإيجار",
                rentPrice = 90,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "oldtown_abandoned_treasure",
                name = "Old Abandoned Mystery House",
                arabicName = "البيت المهجور ذو القبو الغامض",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.SUBURBS,
                posX = 173.0f, posZ = -45f,
                width = 11.5f, height = 9.5f, depth = 9.5f,
                colorPrimary = floatArrayOf(0.54f, 0.50f, 0.44f, 1f),
                colorSecondary = floatArrayOf(0.32f, 0.25f, 0.20f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 173.0f, doorPosZ = -39.2f,
                iconEmoji = "💎",
                streetZone = StreetZone.OLD_TOWN,
                facadeDescription = "بيت قديم مهجور مع كراج جانبي لسيارة واحدة فقط.",
                listingBadge = "كراج سيارة واحدة جنب البيت",
                houseSideQuest = HouseSideQuest.BASEMENT_TREASURE,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "oldtown_secrets_locked",
                name = "House of Ancient Secrets",
                arabicName = "منزل الأسرار القديمة (مغلق بالمفتاح)",
                type = BuildingType.RUSTIC_FARMHOUSE,
                district = District.SUBURBS,
                posX = 93.0f, posZ = -45f,
                width = 11.5f, height = 10.5f, depth = 9.5f,
                colorPrimary = floatArrayOf(0.76f, 0.68f, 0.56f, 1f),
                colorSecondary = floatArrayOf(0.45f, 0.28f, 0.14f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 93.0f, doorPosZ = -39.2f,
                iconEmoji = "🗝️",
                streetZone = StreetZone.OLD_TOWN,
                facadeDescription = "منزل الأسرار القديمة مع كراج لسيارة واحدة فقط.",
                listingBadge = "مغلق بالمفتاح 🔒",
                isLockedByDefault = true,
                houseSideQuest = HouseSideQuest.FIND_GARDEN_KEY,
                hasSingleCarGarage = true
            )
        )
        list.add(
            Building(
                id = "oldtown_craft_workshop",
                name = "Traditional Craft Workshop",
                arabicName = "الورشة الحرفية التقليدية للأدوات اليدوية",
                type = BuildingType.WAREHOUSE,
                district = District.SUBURBS,
                posX = 135f, posZ = -15f,
                width = 15f, height = 9f, depth = 9.6f,
                colorPrimary = floatArrayOf(0.68f, 0.48f, 0.30f, 1f),
                colorSecondary = floatArrayOf(0.85f, 0.65f, 0.25f, 1f),
                roofType = Building.RoofType.PEAKED,
                isEnterable = true,
                interiorType = InteriorType.CAR_PARTS_SHOP,
                doorPosX = 135f, doorPosZ = -9.2f,
                iconEmoji = "🔨",
                streetZone = StreetZone.OLD_TOWN,
                facadeDescription = "ورشة حرفية تقليدية لصناعة الأدوات اليدوية.",
                listingBadge = "ورشة مفتوحة للزوار"
            )
        )

        // 9D. حي الأبراج والمجمعات الحديثة (Modern Towers District) - متباعد بنظام شطرنجي دون تلاصق
        list.add(
            Building(
                id = "tower_smart_skyline",
                name = "Smart Skyline Tower",
                arabicName = "البرج الذكي بمواقف الأسطح",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = -55f, posZ = -120f,
                width = 15.5f, height = 56f, depth = 15.5f,
                colorPrimary = floatArrayOf(0.12f, 0.24f, 0.42f, 1f),
                colorSecondary = floatArrayOf(0.08f, 0.85f, 0.98f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = -55f, doorPosZ = -111.2f,
                iconEmoji = "🛰️",
                streetZone = StreetZone.MODERN_TOWERS,
                facadeDescription = "برج سكني شاهق مزود بمصعد ذكي وموقف أسطح.",
                listingBadge = "منزل للإيجار الفوري - اضغط للدخول",
                rentPrice = 200
            )
        )
        list.add(
            Building(
                id = "tower_panoramic_20",
                name = "20th Floor Panoramic Suite",
                arabicName = "برج الشقة البانورامية (الطابق 20)",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = -17.5f, posZ = -160f,
                width = 13.5f, height = 68f, depth = 15.5f,
                colorPrimary = floatArrayOf(0.18f, 0.30f, 0.50f, 1f),
                colorSecondary = floatArrayOf(0.98f, 0.84f, 0.22f, 1f),
                roofType = Building.RoofType.TIERED_SPIRE,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = -17.5f, doorPosZ = -151.2f,
                iconEmoji = "🌆",
                streetZone = StreetZone.MODERN_TOWERS,
                facadeDescription = "شقة بانورامية فاخرة في الطابق العشرين.",
                listingBadge = "بيت للبيع مع إطلالة بانورامية",
                salePrice = 150000
            )
        )
        list.add(
            Building(
                id = "tower_penthouse_pool",
                name = "Upscale Penthouse Pool Complex",
                arabicName = "مجمع بنتهاوس الراقي (مسبح خاص)",
                type = BuildingType.MODERN_TOWER,
                district = District.DOWNTOWN,
                posX = 17.5f, posZ = -120f,
                width = 13.5f, height = 50f, depth = 15.5f,
                colorPrimary = floatArrayOf(0.14f, 0.18f, 0.28f, 1f),
                colorSecondary = floatArrayOf(0.05f, 0.90f, 0.85f, 1f),
                roofType = Building.RoofType.FLAT,
                isEnterable = true,
                interiorType = InteriorType.PLAYER_VILLA,
                doorPosX = 17.5f, doorPosZ = -111.2f,
                iconEmoji = "🏊",
                streetZone = StreetZone.MODERN_TOWERS,
                facadeDescription = "مجمع بنتهاوس الراقي - مجهز بحمام سباحة خاص.",
                listingBadge = "السعر المعروض: 150,000 عملة",
                salePrice = 150000
            )
        )
        list.add(
            Building(
                id = "tower_night_plaza",
                name = "Night Lights Luxury Tower",
                arabicName = "برج الأضواء الليلية الفاخر",
                type = BuildingType.SKYSCRAPER,
                district = District.DOWNTOWN,
                posX = 55f, posZ = -160f,
                width = 15.5f, height = 58f, depth = 15.5f,
                colorPrimary = floatArrayOf(0.15f, 0.16f, 0.30f, 1f),
                colorSecondary = floatArrayOf(0.85f, 0.35f, 0.98f, 1f),
                roofType = Building.RoofType.TIERED_SPIRE,
                isEnterable = true,
                interiorType = InteriorType.STANDARD_HOUSE,
                doorPosX = 55f, doorPosZ = -151.2f,
                iconEmoji = "🌃",
                streetZone = StreetZone.MODERN_TOWERS,
                facadeDescription = "مجمع سكني حديث بواجهات زجاجية وإضاءات ليلية مذهلة.",
                listingBadge = "مغلق بالمفتاح 🔒",
                isLockedByDefault = true
            )
        )

        // 10. STREET HOUSES ON WIDE-SPACED LOTS (بيوت الشارع مع مساحة خضراء شاسعة حول كل بيت ودون أي تلاصق!)
        val houseWallPalettes = listOf(
            floatArrayOf(0.95f, 0.92f, 0.86f, 1f) to floatArrayOf(0.28f, 0.45f, 0.62f, 1f),
            floatArrayOf(0.94f, 0.89f, 0.80f, 1f) to floatArrayOf(0.72f, 0.34f, 0.22f, 1f),
            floatArrayOf(0.90f, 0.94f, 0.92f, 1f) to floatArrayOf(0.18f, 0.52f, 0.36f, 1f),
            floatArrayOf(0.96f, 0.94f, 0.90f, 1f) to floatArrayOf(0.26f, 0.30f, 0.40f, 1f),
            floatArrayOf(0.92f, 0.88f, 0.84f, 1f) to floatArrayOf(0.65f, 0.42f, 0.20f, 1f)
        )
        val houseDescriptions = listOf(
            "منزل شارع أنيق بستة شبابيك وإطلالة حيوية على الشارع.",
            "بيت عائلي دافئ بستة شبابيك متاح للدخول والاستكشاف.",
            "منزل سكني على الشارع بتصميم عصري وحديقة واسعة حوله.",
            "بيت مريح على ناصية الشارع مجهز بستة شبابيك مضيئة."
        )
        val spaciousStreetHouseLots = listOf(
            Pair(-175f, 117.5f), // West Street Boulevard (0, 8)
            Pair(-88f, 216f),    // Southwest Boulevard Row 1 (spacious lawn + rooftop airplane pad!)
            Pair(-28f, 298f),    // Southwest Boulevard Row 2
            Pair(232f, -180f)    // North-East Coastal Promenade Lawn
        )

        val nsRoadsRef = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
        val ewRoadsRef = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f, 256f, 340f)
        var streetHouseNum = 1

        for ((lotX, cz) in spaciousStreetHouseLots) {
            val houseW = 11.5f
            val cx = lotX - 2.0f
            val garW = 4.4f
            val houseD = 12.0f
            val houseH = 9.5f
            val halfW = houseW / 2f
            val halfD = houseD / 2f
            val totalMinX = cx - halfW
            val totalMaxX = cx + halfW + garW

            // Strictly verify ZERO overlap with any road/sidewalk corridor
            val hitsNsRoad = nsRoadsRef.any { rx ->
                val maxZ = if (rx == 0f || rx == -195f) 348f else 182f
                cz <= maxZ + halfD + 8.85f && totalMaxX > (rx - 8.85f) && totalMinX < (rx + 8.85f)
            }
            val hitsEwRoad = ewRoadsRef.any { rz -> kotlin.math.abs(cz - rz) < halfD + 8.85f }
            if (hitsNsRoad || hitsEwRoad) continue

            // Guarantee generous >= 20m clear lawn buffer around every Street House vs neighbors
            val combinedCenterX = (totalMinX + totalMaxX) / 2f
            val combinedHalfW = (totalMaxX - totalMinX) / 2f
            val overlapsExisting = list.any { existing ->
                existing.getCollisionBoxes().any { box ->
                    kotlin.math.abs(combinedCenterX - box.centerX) < (combinedHalfW + box.width / 2f + 20.0f) &&
                        kotlin.math.abs(cz - box.centerZ) < (halfD + box.depth / 2f + 20.0f)
                }
            }
            if (overlapsExisting) continue

            val (wallCol, roofCol) = houseWallPalettes[streetHouseNum % houseWallPalettes.size]
            val desc = houseDescriptions[streetHouseNum % houseDescriptions.size]
            val corridorName = when {
                cz >= 190f -> "شارع الفلل الجنوبي"
                cz <= -100f -> "الشارع الشمالي"
                cx <= -100f -> "الشارع الغربي"
                cx >= 100f -> "الشارع الشرقي"
                else -> "شارع وسط المدينة"
            }
            val streetPrices = listOf(15000, 25000, 35000, 50000)
            val housePrice = streetPrices[(streetHouseNum - 1) % streetPrices.size]
            val formattedPrice = java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(housePrice)
            val doorZ = cz + halfD + 1.1f

            list.add(
                Building(
                    id = "city_street_house_$streetHouseNum",
                    name = "Street House #$streetHouseNum",
                    arabicName = "منزل الشارع رقم $streetHouseNum (مهبط طيارة بالسقف وكراج ✈️🚗)",
                    type = BuildingType.RUSTIC_FARMHOUSE,
                    district = District.RESIDENTIAL,
                    posX = cx,
                    posZ = cz,
                    width = houseW,
                    height = houseH,
                    depth = houseD,
                    colorPrimary = wallCol,
                    colorSecondary = roofCol,
                    roofType = Building.RoofType.FLAT,
                    isEnterable = true,
                    interiorType = InteriorType.STANDARD_HOUSE,
                    doorPosX = cx,
                    doorPosZ = doorZ,
                    iconEmoji = "🏡",
                    facadeDescription = "$desc ($corridorName) • مجهز بموقف طيارة فوق السقف ✈️ وكراج يكفي سيارة واحدة",
                    listingBadge = "للبيع: $formattedPrice عملة • أو إيجار فوري 100$",
                    salePrice = housePrice,
                    rentPrice = 100,
                    hasSingleCarGarage = true
                )
            )
            streetHouseNum++
        }

        return list
    }

    /**
     * Returns true if a building is a purchasable Street House shown in «مكان شراء بيوت الشارع».
     */
    fun isPurchasableStreetHouse(b: Building): Boolean {
        return b.id.startsWith("city_street_house_") ||
            b.id.startsWith("standard_house_") ||
            (b.salePrice > 0 && b.type != BuildingType.RESIDENTIAL_VILLA && b.id != "main_investment_billboard")
    }

    /**
     * Initial fleet of parked, ambient vehicles, and flyable aircraft.
     */
    fun generateVehicles(buildings: List<Building> = emptyList()): List<Vehicle> {
        val list = mutableListOf(
            // FREE Cyber Robot Car parked right beside player spawn (8.0, 6.5) on the street — ready to drive from second 1!
            Vehicle(
                id = "car_robot_cyber_free",
                type = VehicleType.SPORTS_CAR,
                posX = 6.2f, posY = 0f, posZ = 4.4f,
                yaw = 270f,
                colorPrimary = floatArrayOf(0.08f, 0.70f, 0.98f, 1f),
                colorSecondary = floatArrayOf(0.10f, 0.12f, 0.16f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                dealershipCarId = "car_robot_cyber_free",
                displayName = "سيارة الروبوتات السايبر (مجانية 🤖)",
                meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                customTopSpeed = 295f,
                customAccelRate = 70f,
                customBrakeRate = 92f,
                soundPitch = 1.18f,
                underglowColor = floatArrayOf(0.05f, 0.95f, 1.0f, 1f)
            ),
            // Single free starter car (car_robot_cyber_free) parked beside spawn (8.0, 6.5) — no extra starter cars!
            // Yellow Taxi Cab safely parked inside Underground City Garage (Bay #6)
            Vehicle(
                id = "taxi_cab_1",
                type = VehicleType.TAXI,
                posX = -12.0f, posY = 0f, posZ = 8.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(0.98f, 0.82f, 0.08f, 1f), // Taxi Yellow
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.1f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة أجرة تاكسي (كراج تحت الأرض)"
            ),
            // Police Cruiser parked cleanly inside Police HQ front patrol bay (on grass apron off all roads!)
            Vehicle(
                id = "police_cruiser_1",
                type = VehicleType.POLICE_CRUISER,
                posX = -89.1f, posY = 0f, posZ = -153.15f,
                yaw = 0f,
                colorPrimary = floatArrayOf(0.12f, 0.2f, 0.35f, 1f), // Police Blue
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f),
                isPlayerDriving = false,
                sirenOn = true,
                isAiControlled = false
            ),
            // City / Travel Bus parked neatly inside Central Travel Station bay (on grass apron off Z=35 street!)
            Vehicle(
                id = "travel_bus_1",
                type = VehicleType.CITY_BUS,
                posX = -55f, posY = 0f, posZ = 24.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(0.2f, 0.55f, 0.85f, 1f), // Transit Blue
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f),
                isPlayerDriving = false,
                isAiControlled = false
            ),
            // Flyable Private Airplane parked ready on East Airport Runway (strictly off city roads!)
            Vehicle(
                id = "player_airplane_airport",
                type = VehicleType.AIRPLANE,
                posX = 340f, posY = 0f, posZ = -20f,
                yaw = 180f,
                colorPrimary = floatArrayOf(0.97f, 0.98f, 1.0f, 1f), // Glossy Aviation White
                colorSecondary = floatArrayOf(0.08f, 0.45f, 0.92f, 1f), // Cobalt Wing Trim
                isPlayerDriving = false,
                isAiControlled = false,
                displayName = "طائرة المطار الخاصة ✈️",
                soundPitch = 1.15f
            ),
            // Orange Sport Cars stored neatly inside Underground City Garage Bays #12 & #13 (never blocking streets!)
            Vehicle(
                id = "sedan_ambient_1",
                type = VehicleType.SPORTS_CAR,
                posX = 12.0f, posY = 0f, posZ = 8.5f,
                yaw = 270f,
                colorPrimary = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.12f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة رياضية برتقالية (تحت الأرض)",
                meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                customTopSpeed = 290f,
                underglowColor = floatArrayOf(0.0f, 0.85f, 1.0f, 1.0f)
            ),
            Vehicle(
                id = "sedan_ambient_2",
                type = VehicleType.SPORTS_CAR,
                posX = -12.0f, posY = 0f, posZ = 13.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(1.0f, 0.45f, 0.0f, 1f), // Vibrant Orange
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.12f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة سوبر برتقالية (تحت الأرض)",
                meshStyle = CarMeshStyle.SUPER_SPORT,
                customTopSpeed = 290f,
                underglowColor = floatArrayOf(0.0f, 0.85f, 1.0f, 1.0f)
            )
        )

        // Add all dealership cars from «مدينة المحركات» (Motor City) except the single spawn starter car
        for (car in MotorCityData.CATALOG) {
            if (car.id == "car_robot_cyber_free") continue
            val vType = when (car.meshStyle) {
                CarMeshStyle.VINTAGE_OLD, CarMeshStyle.SEDAN_MODERN, CarMeshStyle.SUV_OFFROAD -> VehicleType.SEDAN
                else -> VehicleType.SPORTS_CAR
            }
            list.add(
                Vehicle(
                    id = car.id,
                    type = vType,
                    posX = car.displayPosX,
                    posY = 0f,
                    posZ = car.displayPosZ,
                    yaw = car.displayYaw,
                    colorPrimary = car.primaryColor,
                    colorSecondary = car.secondaryColor,
                    isPlayerDriving = false,
                    isAiControlled = false,
                    dealershipCarId = car.id,
                    displayName = car.nameArabic,
                    meshStyle = car.meshStyle,
                    customTopSpeed = car.topSpeedKmH,
                    customAccelRate = car.accelRate,
                    customBrakeRate = car.brakeRate,
                    soundPitch = car.soundPitch,
                    isShowroomDisplay = true
                )
            )
        }

        // Cars parked neatly in TWO facing rows inside the Underground City Parking Garage («كراج المدينة تحت الأرض»)
        // Left Bay Row (posX = -12.0f, facing East yaw = 90f)
        list.add(
            Vehicle(
                id = "parked_car_lot_1",
                type = VehicleType.SPORTS_CAR,
                posX = -12.0f, posY = 0f, posZ = -16.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(1.0f, 0.45f, 0.0f, 1f),
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.12f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة روبوت برتقالية (موقف 1)",
                meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                customTopSpeed = 310f,
                underglowColor = floatArrayOf(0.0f, 0.85f, 1.0f, 1.0f)
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_2",
                type = VehicleType.SEDAN,
                posX = -12.0f, posY = 0f, posZ = -11.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(0.85f, 0.15f, 0.15f, 1f),
                colorSecondary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة سيدان حمراء (موقف 2)",
                meshStyle = CarMeshStyle.SEDAN_MODERN,
                customTopSpeed = 220f
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_3",
                type = VehicleType.SPORTS_CAR,
                posX = -12.0f, posY = 0f, posZ = -6.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(0.1f, 0.65f, 0.95f, 1f),
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.12f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة زرقاء تيربو (موقف 3)",
                meshStyle = CarMeshStyle.SUPER_SPORT,
                customTopSpeed = 310f,
                underglowColor = floatArrayOf(0.1f, 0.9f, 1.0f, 1.0f)
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_4",
                type = VehicleType.SPORTS_CAR,
                posX = -12.0f, posY = 0f, posZ = -1.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(0.15f, 0.85f, 0.35f, 1f),
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.1f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة زمردية رياضية (موقف 4)",
                meshStyle = CarMeshStyle.SUPER_SPORT,
                customTopSpeed = 295f,
                underglowColor = floatArrayOf(0.2f, 1.0f, 0.4f, 1.0f)
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_5",
                type = VehicleType.SEDAN,
                posX = -12.0f, posY = 0f, posZ = 3.5f,
                yaw = 90f,
                colorPrimary = floatArrayOf(0.95f, 0.95f, 0.98f, 1f),
                colorSecondary = floatArrayOf(0.15f, 0.15f, 0.2f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة لؤلؤية كلاسيكية (موقف 5)",
                meshStyle = CarMeshStyle.SEDAN_MODERN,
                customTopSpeed = 230f
            )
        )

        // Right Bay Row (posX = +12.0f, facing West yaw = 270f)
        list.add(
            Vehicle(
                id = "parked_car_lot_6",
                type = VehicleType.SEDAN,
                posX = 12.0f, posY = 0f, posZ = -16.5f,
                yaw = 270f,
                colorPrimary = floatArrayOf(0.95f, 0.82f, 0.12f, 1f),
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.1f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة تاكسي ذهبية (موقف 8)",
                meshStyle = CarMeshStyle.SEDAN_MODERN,
                customTopSpeed = 200f
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_7",
                type = VehicleType.SPORTS_CAR,
                posX = 12.0f, posY = 0f, posZ = -11.5f,
                yaw = 270f,
                colorPrimary = floatArrayOf(0.65f, 0.25f, 0.85f, 1f),
                colorSecondary = floatArrayOf(0.12f, 0.12f, 0.12f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة بنفسجية ملكية (موقف 9)",
                meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                customTopSpeed = 300f,
                underglowColor = floatArrayOf(0.85f, 0.1f, 0.95f, 1.0f)
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_8",
                type = VehicleType.SPORTS_CAR,
                posX = 12.0f, posY = 0f, posZ = -6.5f,
                yaw = 270f,
                colorPrimary = floatArrayOf(0.98f, 0.2f, 0.2f, 1f),
                colorSecondary = floatArrayOf(0.08f, 0.08f, 0.08f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة حمراء خارقة (موقف 10)",
                meshStyle = CarMeshStyle.SUPER_SPORT,
                customTopSpeed = 330f,
                underglowColor = floatArrayOf(1.0f, 0.2f, 0.2f, 1.0f)
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_9",
                type = VehicleType.SPORTS_CAR,
                posX = 12.0f, posY = 0f, posZ = -1.5f,
                yaw = 270f,
                colorPrimary = floatArrayOf(0.05f, 0.85f, 0.95f, 1f),
                colorSecondary = floatArrayOf(0.12f, 0.12f, 0.14f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة كهربائية ذكية (موقف 11)",
                meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                customTopSpeed = 315f,
                underglowColor = floatArrayOf(0.0f, 0.9f, 1.0f, 1.0f)
            )
        )
        list.add(
            Vehicle(
                id = "parked_car_lot_10",
                type = VehicleType.SEDAN,
                posX = 12.0f, posY = 0f, posZ = 3.5f,
                yaw = 270f,
                colorPrimary = floatArrayOf(0.95f, 0.6f, 0.1f, 1f),
                colorSecondary = floatArrayOf(0.1f, 0.1f, 0.1f, 1f),
                isPlayerDriving = false,
                isAiControlled = false,
                isInUndergroundGarage = true,
                displayName = "سيارة ذهبية فاخرة (موقف 12)",
                meshStyle = CarMeshStyle.SEDAN_MODERN,
                customTopSpeed = 240f
            )
        )

        // Park a sleek Street-House Rooftop Airplane cleanly on top of the nearest Street House roof pad!
        val streetHouseForPlane = buildings.firstOrNull { it.hasRooftopAirplanePad && it.posZ >= 190f }
            ?: buildings.firstOrNull { it.hasRooftopAirplanePad }
        if (streetHouseForPlane != null) {
            val padY = streetHouseForPlane.getRooftopAirplanePadY() ?: (streetHouseForPlane.height + 0.25f)
            list.add(
                Vehicle(
                    id = "rooftop_street_plane",
                    type = VehicleType.AIRPLANE,
                    posX = streetHouseForPlane.posX,
                    posY = padY,
                    posZ = streetHouseForPlane.posZ,
                    yaw = 0f,
                    colorPrimary = floatArrayOf(0.98f, 0.96f, 0.92f, 1f),
                    colorSecondary = floatArrayOf(0.95f, 0.35f, 0.12f, 1f),
                    isPlayerDriving = false,
                    isAiControlled = false,
                    displayName = "طائرة فوق سقف البيت ✈️",
                    soundPitch = 1.2f
                )
            )
        }

        return list
    }

    /**
     * Initial citizens relaxing and walking strictly inside safe parks and gardens (never crossing any street).
     */
    fun generateNPCs(): List<NPC> {
        return listOf(
            NPC(
                id = "ped_1",
                name = "أحمد (مواطن في الحديقة)",
                posX = 94f, posZ = 216f,
                shirtColor = floatArrayOf(0.2f, 0.6f, 0.9f, 1f),
                pantsColor = floatArrayOf(0.2f, 0.2f, 0.3f, 1f),
                greetingArabic = "صباح الخير! حدائق حي الفلل واسعة ومتباعدة وبعيدة تماماً عن الشوارع.",
                waypoints = listOf(
                    Vector3(92f, 0f, 202f),
                    Vector3(96f, 0f, 202f),
                    Vector3(96f, 0f, 226f),
                    Vector3(92f, 0f, 226f)
                )
            ),
            NPC(
                id = "ped_2",
                name = "سارة (متنزهة في الحديقة)",
                posX = -17.5f, posZ = 50f,
                shirtColor = floatArrayOf(0.9f, 0.3f, 0.5f, 1f),
                pantsColor = floatArrayOf(0.9f, 0.9f, 0.9f, 1f),
                greetingArabic = "المشي داخل الحدائق والمساحات الخضراء آمن وممتع دون عبور أي شارع!",
                waypoints = listOf(
                    Vector3(-22f, 0f, 46f),
                    Vector3(-13f, 0f, 46f),
                    Vector3(-13f, 0f, 54f),
                    Vector3(-22f, 0f, 54f)
                )
            ),
            NPC(
                id = "ped_3",
                name = "كابتن خالد (مراقب المحطة)",
                posX = -95f, posZ = 17.5f,
                shirtColor = floatArrayOf(0.15f, 0.35f, 0.6f, 1f),
                pantsColor = floatArrayOf(0.15f, 0.15f, 0.2f, 1f),
                greetingArabic = "أهلاً بك! الشوارع منظمة وآمنة، والمباني متباعدة بمساحات خضراء جميلة.",
                waypoints = listOf(
                    Vector3(-101f, 0f, 12f),
                    Vector3(-89f, 0f, 12f),
                    Vector3(-89f, 0f, 23f),
                    Vector3(-101f, 0f, 23f)
                )
            ),
            NPC(
                id = "ped_4",
                name = "الدكتور فهد (في حديقة المستشفى)",
                posX = 95f, posZ = 50f,
                shirtColor = floatArrayOf(0.95f, 0.95f, 0.98f, 1f),
                pantsColor = floatArrayOf(0.3f, 0.5f, 0.7f, 1f),
                greetingArabic = "حديقة الأمل الخضراء هادئة وآمنة ومفصولة عن حركة السيارات.",
                waypoints = listOf(
                    Vector3(90f, 0f, 46f),
                    Vector3(100f, 0f, 46f),
                    Vector3(100f, 0f, 54f),
                    Vector3(90f, 0f, 54f)
                )
            )
        )
    }

    /**
     * Traffic lights at major city avenue & boulevard intersections.
     */
    fun generateTrafficLights(): List<TrafficLight> {
        val xs = listOf(-115f, -75f, -35f, 0f, 35f, 75f, 115f)
        val zs = listOf(-100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f)
        val lights = mutableListOf<TrafficLight>()
        for (x in xs) {
            for (z in zs) {
                lights.add(TrafficLight("tl_${x.toInt()}_${z.toInt()}", x, z))
            }
        }
        return lights
    }

    /**
     * Coastal Car Swap & Rescue Stations ("محطات البدالة") located where the main city avenues meet the beaches.
     * Whenever a car drives into the surrounding sea, it automatically returns to the nearest Swap Station.
     */
    data class CoastalSwapStation(
        val id: String,
        val arabicName: String,
        val padX: Float,
        val padZ: Float,
        val returnYaw: Float,
        val boatX: Float,
        val boatZ: Float
    )

    val COASTAL_SWAP_STATIONS = listOf(
        CoastalSwapStation(
            id = "swap_north",
            arabicName = "محطة بدالة الشاطئ الشمالي",
            padX = 0f,
            padZ = -194f,
            returnYaw = 0f, // Facing South back into City Center
            boatX = -7f,
            boatZ = -302f
        ),
        CoastalSwapStation(
            id = "swap_south",
            arabicName = "محطة بدالة الشاطئ الجنوبي",
            padX = 0f,
            padZ = 353f,
            returnYaw = 180f, // Facing North back onto Villa Boulevard
            boatX = 168f,
            boatZ = 458f
        ),
        CoastalSwapStation(
            id = "swap_west",
            arabicName = "محطة بدالة الشاطئ الغربي",
            padX = -209f,
            padZ = 0f,
            returnYaw = 90f, // Facing East back into West Avenue
            boatX = -320f,
            boatZ = -27f
        ),
        CoastalSwapStation(
            id = "swap_east",
            arabicName = "محطة بدالة الشاطئ الشرقي",
            padX = 209f,
            padZ = 0f,
            returnYaw = 270f, // Facing West back into East Avenue
            boatX = 466f,
            boatZ = 33f
        )
    )

    fun findNearestCoastalSwapStation(x: Float, z: Float): CoastalSwapStation {
        return COASTAL_SWAP_STATIONS.minByOrNull { st ->
            val dx = x - st.padX
            val dz = z - st.padZ
            dx * dx + dz * dz
        } ?: COASTAL_SWAP_STATIONS.first()
    }
}
