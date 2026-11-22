package com.example.opencity.model

enum class MissionStatus {
    AVAILABLE,
    ACTIVE,
    COMPLETED
}

data class Mission(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val rewardMoney: Int,
    val iconEmoji: String,
    val targetBuildingId: String,
    val targetPosition: Vector3,
    val targetDistrict: District,
    val timeLimitSec: Int = 0, // 0 = unlimited
    var remainingTimeSec: Int = timeLimitSec,
    var status: MissionStatus = MissionStatus.AVAILABLE
)

object MissionData {
    fun getDefaultMissions(): List<Mission> {
        return listOf(
            Mission(
                id = "m1_food_delivery",
                titleArabic = "توصيل وجبة سريعة من المطعم",
                descriptionArabic = "استلم الوجبة الساخنة من مطعم وسط المدينة وقُد السيارة لتوصيلها إلى فيلا الحي السكني سريعاً!",
                rewardMoney = 0,
                iconEmoji = "🍔",
                targetBuildingId = "villa_player",
                targetPosition = Vector3(53.8f, 0f, 161.2f),
                targetDistrict = District.RESIDENTIAL,
                timeLimitSec = 90
            ),
            Mission(
                id = "m2_travel_ticket",
                titleArabic = "رحلة السفر بالحافلة المركزية",
                descriptionArabic = "توجّه إلى محطة السفر المركزية الكبرى، وادخل صالة الركاب لحجز تذكرة السفر إلى ريف المدينة والضواحي.",
                rewardMoney = 0,
                iconEmoji = "🚌",
                targetBuildingId = "travel_terminal",
                targetPosition = Vector3(-55f, 0f, 24.8f),
                targetDistrict = District.TRAVEL_HUB,
                timeLimitSec = 0
            ),
            Mission(
                id = "m3_supermarket_supplies",
                titleArabic = "شراء مستلزمات من السوبرماركت",
                descriptionArabic = "ادخل سوبرماركت المدينة في المنطقة التجارية، واشترِ مشروب طاقة وعصائر لتجديد طاقتك.",
                rewardMoney = 0,
                iconEmoji = "🛒",
                targetBuildingId = "supermarket_central",
                targetPosition = Vector3(-55f, 0f, 75.2f),
                targetDistrict = District.COMMERCIAL,
                timeLimitSec = 0
            ),
            Mission(
                id = "m4_police_patrol",
                titleArabic = "استجابة أمنية عاجلة لبنك المدينة",
                descriptionArabic = "اركب سيارة الشرطة، شغّل الأضواء التحذيرية والصفارة، وتوجه فوراً إلى بنك المدينة الرئيسي لحمايته!",
                rewardMoney = 0,
                iconEmoji = "🚨",
                targetBuildingId = "bank_central",
                targetPosition = Vector3(17.5f, 0f, -38.5f),
                targetDistrict = District.DOWNTOWN,
                timeLimitSec = 60
            ),
            Mission(
                id = "m5_landmark_tour",
                titleArabic = "جولة استكشاف معالم المدينة",
                descriptionArabic = "قم بزيارة المسجد الكبير وبرج ناطحة السحاب واستمتع بجمال الهندسة المعمارية.",
                rewardMoney = 0,
                iconEmoji = "🕌",
                targetBuildingId = "grand_mosque",
                targetPosition = Vector3(175f, 0f, -108f),
                targetDistrict = District.SERVICES,
                timeLimitSec = 0
            )
        )
    }
}
