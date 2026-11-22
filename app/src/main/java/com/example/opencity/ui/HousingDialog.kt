package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Villa Property Model for Housing Catalog
 */
data class VillaProperty(
    val id: String,
    val nameArabic: String,
    val descriptionArabic: String,
    val price: Int,
    val posX: Float,
    val posZ: Float,
    val doorX: Float,
    val doorZ: Float,
    val garageX: Float,
    val garageZ: Float,
    val emoji: String,
    val featuresArabic: String
)

val CITY_VILLAS_CATALOG = listOf(
    VillaProperty(
        id = "villa_player",
        nameArabic = "فيلا النيوكلاسيك — كومباوند 90 أفينيو",
        descriptionArabic = "فيلا عصرية على بوابة كومباوند 90 أفينيو أمام البحر الخاص والمسبح الأولمبي، طابقان، 5 غرف نوم، 6 حمامات، مجلس ضيوف فاخر، مسبح مضيء، وكراج متكامل.",
        price = 3_950_000,
        posX = 53.8f,
        posZ = 151.4f,
        doorX = 53.8f,
        doorZ = 161.2f,
        garageX = 61.25f,
        garageZ = 152.6f,
        emoji = "🏛️",
        featuresArabic = "كومباوند 90 أفينيو • قرب البحر والمسبح • طابقان • كراج"
    ),
    VillaProperty(
        id = "villa_palm",
        nameArabic = "قصر النخيل — كومباوند 90 أفينيو",
        descriptionArabic = "فيلا نيوكلاسيكية حديثة داخل كومباوند 90 أفينيو على بُعد خطوات من بحر الكومباوند الخاص والمسبح الأولمبي.",
        price = 20_000_000,
        posX = 136f,
        posZ = 216f,
        doorX = 136f,
        doorZ = 234.5f,
        garageX = 153.5f,
        garageZ = 238.0f,
        emoji = "🌴",
        featuresArabic = "كومباوند 90 أفينيو • بحر ومسبح مشترك + خاص • كراج 4 سيارات"
    ),
    VillaProperty(
        id = "villa_beach",
        nameArabic = "قصر الشاطئ — كومباوند 90 أفينيو",
        descriptionArabic = "فيلا ذهبية فاخرة داخل كومباوند 90 أفينيو مع حديقة غناء ومسبح رخامي والوصول لبحر الكومباوند الخاص.",
        price = 35_000_000,
        posX = 220f,
        posZ = 216f,
        doorX = 220f,
        doorZ = 234.5f,
        garageX = 238.0f,
        garageZ = 238.0f,
        emoji = "🏖️",
        featuresArabic = "كومباوند 90 أفينيو • بحر ومسبح الكومباوند • كراج 4 سيارات"
    ),
    VillaProperty(
        id = "villa_skyline",
        nameArabic = "قصر الأفق — كومباوند 90 أفينيو",
        descriptionArabic = "فيلا بتصميم معماري راقٍ في الجناح الغربي لكومباوند 90 أفينيو المسور مع جاكوزي دافئ.",
        price = 50_000_000,
        posX = -158f,
        posZ = 298f,
        doorX = -158f,
        doorZ = 316.5f,
        garageX = -139.5f,
        garageZ = 320.0f,
        emoji = "🌅",
        featuresArabic = "كومباوند 90 أفينيو • كراج 4 سيارات • جاكوزي دافئ"
    ),
    VillaProperty(
        id = "villa_waterfront_1",
        nameArabic = "قصر مارينا — كومباوند 90 أفينيو",
        descriptionArabic = "قصر ساحلي أنيق داخل الجناح الغربي لكومباوند 90 أفينيو مع مسبح خاص وكراج يتسع لأربع سيارات.",
        price = 60_000_000,
        posX = -158f,
        posZ = 216f,
        doorX = -158f,
        doorZ = 234.5f,
        garageX = -139.5f,
        garageZ = 238.0f,
        emoji = "🌊",
        featuresArabic = "كومباوند 90 أفينيو • مسبح خاص • كراج 4 سيارات • أثاث ملكي"
    ),
    VillaProperty(
        id = "villa_oasis",
        nameArabic = "قصر الواحة — كومباوند 90 أفينيو",
        descriptionArabic = "قصر رحب داخل كومباوند 90 أفينيو المسور مقابل بحيرة الكومباوند والمسبح الأولمبي.",
        price = 65_000_000,
        posX = 52f,
        posZ = 298f,
        doorX = 52f,
        doorZ = 316.5f,
        garageX = 70.0f,
        garageZ = 320.0f,
        emoji = "🏰",
        featuresArabic = "كومباوند 90 أفينيو • قرب البحر والمسبح • كراج 4 سيارات"
    ),
    VillaProperty(
        id = "villa_summit",
        nameArabic = "قصر القمة — كومباوند 90 أفينيو",
        descriptionArabic = "فيلا فارهة وأعمدة إنارة ذهبية داخل كومباوند 90 أفينيو مع مسبح دافئ وكراج يتسع لأسطول السيارات.",
        price = 75_000_000,
        posX = 136f,
        posZ = 298f,
        doorX = 136f,
        doorZ = 316.5f,
        garageX = 153.5f,
        garageZ = 320.0f,
        emoji = "💎",
        featuresArabic = "كومباوند 90 أفينيو • طراز كلاسيكي • مسبح دافئ"
    ),
    VillaProperty(
        id = "villa_pine",
        nameArabic = "قصر الصنوبر — كومباوند 90 أفينيو",
        descriptionArabic = "فيلا هادئة بطابع طبيعي داخل كومباوند 90 أفينيو مع مرافق ترفيهية متكاملة وخزنة مجوهرات.",
        price = 85_000_000,
        posX = 220f,
        posZ = 298f,
        doorX = 220f,
        doorZ = 316.5f,
        garageX = 238.0f,
        garageZ = 320.0f,
        emoji = "🌲",
        featuresArabic = "كومباوند 90 أفينيو • طبيعة خلابة • مسبح خاص • كراج"
    ),
    VillaProperty(
        id = "villa_glass_modern",
        nameArabic = "فيلا الزجاج — كومباوند 90 أفينيو",
        descriptionArabic = "تحفة معمارية عصرية بنوافذ بانورامية داخل كومباوند 90 أفينيو المسور وتجهيزات فاخرة.",
        price = 92_000_000,
        posX = 302f,
        posZ = 216f,
        doorX = 302f,
        doorZ = 234.5f,
        garageX = 319.5f,
        garageZ = 238.0f,
        emoji = "💠",
        featuresArabic = "كومباوند 90 أفينيو • نوافذ بانورامية • مسبح • كراج"
    ),
    VillaProperty(
        id = "villa_palace",
        nameArabic = "القصر الإمبراطوري — كومباوند 90 أفينيو",
        descriptionArabic = "أكبر وأفخم قصر إمبراطوري داخل كومباوند 90 أفينيو مبني بالرخام الملكي مع مسبح إمبراطوري وثريات ذهبية.",
        price = 100_000_000,
        posX = 302f,
        posZ = 298f,
        doorX = 302f,
        doorZ = 317.5f,
        garageX = 320.5f,
        garageZ = 320.0f,
        emoji = "👑",
        featuresArabic = "كومباوند 90 أفينيو • قصر رخامي ضخم • مسبح إمبراطوري"
    )
)

/**
 * Real Estate & Residential Housing Dialog (سوق البيوت والعقارات):
 * - Buy luxury villas with private garage to live forever (8 distinct villas!)
 * - Rent furnished apartments in the towers
 * - Park/retrieve car in villa private garage
 * - Bed sleeping and waking up morning system
 */
@Composable
fun HousingDialog(
    playerMoney: Int,
    ownedVillaIds: Set<String>,
    isApartmentRented: Boolean,
    onBuyVilla: (villa: VillaProperty) -> Unit,
    onRentApartment: () -> Unit,
    onParkCarInGarage: (villa: VillaProperty) -> Unit,
    onSleepInBed: () -> Unit,
    onGoToVilla: (villa: VillaProperty) -> Unit,
    onOpenStreetHousesMarket: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Villas, 1: Apartments, 2: Sleep

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 960.dp)
                .fillMaxHeight(0.92f)
                .testTag("housing_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF00B132B),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏢", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "شركة وسوق الفلل الفاخرة",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "شراء الفلل من 3,950,000 ر.س إلى 100,000,000 ر.س • رصيدك: ${String.format(java.util.Locale.US, "%,d ر.س / $", playerMoney)}",
                                color = Color(0xFF6EE7B7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Close Button (إكس للخروج)
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("close_housing_button")
                    ) {
                        Text("❌", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروج (X)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "🏘️ الفلل الفاخرة للبيع (10)" to 0,
                        "🏢 شقق للإيجار" to 1,
                        "🛏️ النوم والاستراحة" to 2
                    )
                    for ((title, index) in tabs) {
                        val isSelected = selectedTab == index
                        Button(
                            onClick = { selectedTab = index },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF059669) else Color(0x661E293B)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 6.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Bridge to Street Houses Market («مكان شراء بيوت الشارع»)
                Button(
                    onClick = {
                        onDismiss()
                        onOpenStreetHousesMarket()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("housing_open_street_houses_market_button")
                ) {
                    Text(
                        text = "🏘️🔑 الانتقال إلى مكان شراء بيوت الشارع (أسعار تبدأ من 15,000$)",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content View according to selected Tab
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> {
                            // 8 Luxury Villas List
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(CITY_VILLAS_CATALOG) { villa ->
                                    val isOwned = ownedVillaIds.contains(villa.id)
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xCC1E293B)),
                                        shape = RoundedCornerShape(14.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.2.dp,
                                            if (isOwned) Color(0xFF10B981) else Color(0xFF334155)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(villa.emoji, fontSize = 24.sp)
                                                    Column {
                                                        Text(
                                                            text = villa.nameArabic,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp
                                                        )
                                                        Text(
                                                            text = villa.featuresArabic,
                                                            color = Color(0xFF6EE7B7),
                                                            fontSize = 10.sp
                                                        )
                                                    }
                                                }

                                                if (isOwned) {
                                                    Surface(
                                                        color = Color(0x3310B981),
                                                        shape = RoundedCornerShape(6.dp),
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                                                    ) {
                                                        Text(
                                                            text = "مملوكة لك للأبد ✅",
                                                            color = Color(0xFF34D399),
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 10.sp,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                } else {
                                                    Text(
                                                        text = "${String.format(java.util.Locale.US, "%,d", villa.price)} ر.س / $",
                                                        color = Color(0xFFFFD600),
                                                        fontWeight = FontWeight.ExtraBold,
                                                        fontSize = 14.sp
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = villa.descriptionArabic,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            if (!isOwned) {
                                                Button(
                                                    onClick = { onBuyVilla(villa) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.fillMaxWidth().testTag("buy_villa_${villa.id}")
                                                ) {
                                                    Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("شراء الفيلا والعيش فيها للأبد (${String.format(java.util.Locale.US, "%,d", villa.price)} ر.س)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            onGoToVilla(villa)
                                                            onDismiss()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.weight(1f).testTag("enter_villa_${villa.id}")
                                                    ) {
                                                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(15.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("انتقال ودخول الفيلا 🏡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }

                                                    Button(
                                                        onClick = { onParkCarInGarage(villa) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier.weight(1f).testTag("park_in_garage_${villa.id}")
                                                    ) {
                                                        Icon(Icons.Default.Garage, contentDescription = null, modifier = Modifier.size(15.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("🚗 وضع السيارة بالكراج", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // Furnished Apartment for Rent
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xCC1E293B)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isApartmentRented) Color(0xFF38BDF8) else Color(0xFF334155)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text("🏢", fontSize = 26.sp)
                                                Column {
                                                    Text(
                                                        text = "شقة سكنية للإيجار في أبراج الواحة",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        text = "إطلالة بانورامية رائعة على شوارع المدينة ومفروشة بالكامل",
                                                        color = Color(0xFF94A3B8),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }

                                            if (isApartmentRented) {
                                                Surface(
                                                    color = Color(0x3338BDF8),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                                                ) {
                                                    Text(
                                                        text = "مستأجرة حالياً ✅",
                                                        color = Color(0xFF38BDF8),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Button(
                                            onClick = onRentApartment,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("rent_apartment_button")
                                        ) {
                                            Text(
                                                text = if (isApartmentRented) "تجديد عقد إيجار الشقة (30$)" else "استئجار الشقة السكنية الآن (30$)",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            // Sleep in Bed & Morning Wake-up
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xCC0F172A)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF818CF8)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text("🛏️", fontSize = 26.sp)
                                            Column {
                                                Text(
                                                    text = "النوم في السرير والاستيقاظ صباحاً",
                                                    color = Color(0xFFC7D2FE),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                                Text(
                                                    text = "اضغط على السرير للنوم ثم تصحى في الصباح الباكر وتستعيد كامل طاقتك ونشاطك",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        Button(
                                            onClick = {
                                                onSleepInBed()
                                                onDismiss()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("sleep_in_bed_button")
                                        ) {
                                            Icon(Icons.Default.Bed, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("😴 النوم في السرير والاستيقاظ في الصباح ☀️", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
