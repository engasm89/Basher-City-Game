package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.NumberFormat
import java.util.Locale

/**
 * Data item representing Food, Drink, or Water products available in the Center.
 */
data class MallItem(
    val id: String,
    val nameArabic: String,
    val descriptionArabic: String,
    val price: Int,
    val emoji: String,
    val category: String // "FOOD", "DRINK", "WATER"
)

val MALL_FOOD_ITEMS = listOf(
    MallItem("bbq_plate", "طبق مشاوي كباب وريش لحم مشوي", "لحم ضأن طازج مشوي على الفحم مع خبز ساخن ومقبلات", 50, "🥩", "FOOD"),
    MallItem("royal_burger", "برجر لحم ملكي سوبر تشيز", "شريحة لحم بقري أنجوس مشوية مع جبن الشيدر والبطاطس", 30, "🍔", "FOOD"),
    MallItem("pizza_italian", "بيتزا إيطالية بالبيبروني والجبنة", "عجينة إيطالية هشة مع صلصة طماطم وجبنة موزاريلا", 25, "🍕", "FOOD"),
    MallItem("shawarma_wrap", "شاورما لحم عربي مع صلصة الثوم", "خبز صاج محمص مع شرائح لحم شاورما طرية ومخللات", 20, "🥙", "FOOD"),
    MallItem("royal_dinner_200", "وجبة عشاء ملكية متكاملة وسوبر فاخرة", "أشهى المأكولات والمشاوي مع مقبلات وحلويات تشحن طاقتك 100%", 200, "🍖", "FOOD")
)

val MALL_DRINK_ITEMS = listOf(
    MallItem("orange_juice", "عصير برتقال طبيعي طازج مثلج", "معصور يدوياً من برتقال يافا الطازج مع مكعبات ثلج", 15, "🍊", "DRINK"),
    MallItem("mojito_mint", "موهيتو ليمون ونعناع مثلج ومنعش", "شرائح ليمون ونعناع بري منعش مع صودا فوارة وثلج مجروش", 18, "🍋", "DRINK"),
    MallItem("cold_cola", "مشروب كولا غازي بارد مع ثلج", "مشروب غازي كلاسيكي مثلج يمنحك الانتعاش الفوري", 10, "🥤", "DRINK"),
    MallItem("strawberry_smoothie", "سموذي فراولة ومانجو طبيعي", "عصير فواكه مثلج كريمي غني بالفيتامينات والنكهة الرائعة", 20, "🧃", "DRINK")
)

val MALL_WATER_ITEMS = listOf(
    MallItem("free_water_cooler", "شرب ماء عذب مثلج من البرادة مباشرة", "مياه شرب نقية وعذبة مبردة تروي عطشك بالكامل", 0, "💧", "WATER"),
    MallItem("mineral_water_bottle", "قنينة مياه معدنية طبيعية باردة (500 مل)", "مياه ينابيع طبيعية نقية معقمة ومفلترة", 5, "🧊", "WATER"),
    MallItem("sparkling_water", "مياه فوارة نقية بقطع الثلج والليمون", "مياه معدنية فوارة غازية هاضمة ومنعشة جداً", 8, "🫧", "WATER"),
    MallItem("water_gallon_5l", "جالون مياه نقي كبير 5 لتر (للفيلا والسيارة)", "جالون مياه عذبة للشرب والطبخ في الفيلا والرحلات", 15, "🚰", "WATER"),
    MallItem("water_crate_24", "كرتونة مياه معدنية نقية (24 قارورة)", "كرتون كامل لقوارير المياه المعدنية تكفيك لأيام في الفيلا", 25, "📦", "WATER")
)

/**
 * Dedicated Center Dialog: «مكان شراء الفلل والطعام والشراب والماء»
 */
@Composable
fun VillasFoodWaterDialog(
    playerMoney: Int,
    ownedVillaIds: Set<String>,
    onBuyVilla: (villa: VillaProperty) -> Unit,
    onBuyFood: (item: MallItem) -> Unit,
    onBuyDrink: (item: MallItem) -> Unit,
    onBuyWater: (item: MallItem) -> Unit,
    onGoToVilla: (villa: VillaProperty) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: الفلل, 1: الطعام, 2: الشراب, 3: الماء
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 940.dp)
                .fillMaxHeight(0.90f)
                .testTag("villas_food_water_mall_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF2071324),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E5FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header with Branding, Wallet, and Prominent Red Close "X" Button
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF0D9488), Color(0xFF0284C7)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏡", fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = "مكان شراء الفلل والطعام والشراب والماء",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "رصيدك الحالي: ${numberFormat.format(playerMoney)}$",
                                color = Color(0xFF4ADE80),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Prominent Red "X" Close Button (إكس للخروج من المكان)
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFCA5A5)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("close_villas_food_water_button")
                    ) {
                        Text("❌", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "خروج (X)",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Navigation Tabs: الفلل • الطعام • الشراب • الماء
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color(0xFF00E5FF),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF00E5FF),
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("🏡 الفلل (3.95M-100M)", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("🍖 الطعام", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("🥤 الشراب", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("💧 الماء", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> VillasTab(
                            playerMoney = playerMoney,
                            ownedVillaIds = ownedVillaIds,
                            onBuyVilla = onBuyVilla,
                            onGoToVilla = onGoToVilla,
                            onDismiss = onDismiss
                        )
                        1 -> ItemsListTab(
                            items = MALL_FOOD_ITEMS,
                            playerMoney = playerMoney,
                            actionLabel = "شراء وتناول الطعام 🍖",
                            actionColor = Color(0xFFEA580C),
                            onBuyItem = onBuyFood
                        )
                        2 -> ItemsListTab(
                            items = MALL_DRINK_ITEMS,
                            playerMoney = playerMoney,
                            actionLabel = "شراء وارتشاف المشروب 🥤",
                            actionColor = Color(0xFF0284C7),
                            onBuyItem = onBuyDrink
                        )
                        3 -> ItemsListTab(
                            items = MALL_WATER_ITEMS,
                            playerMoney = playerMoney,
                            actionLabel = "شرب وتعبئة الماء 💧",
                            actionColor = Color(0xFF0D9488),
                            onBuyItem = onBuyWater
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dedicated Bottom Exit / Leave Button (خروج من مكان الشراء إلى شوارع المدينة)
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("exit_mall_dialog_bottom_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("❌", fontSize = 16.sp)
                        Text(
                            text = "خروج من مكان شراء الفلل والطعام والشراب والماء (X)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VillasTab(
    playerMoney: Int,
    ownedVillaIds: Set<String>,
    onBuyVilla: (villa: VillaProperty) -> Unit,
    onGoToVilla: (villa: VillaProperty) -> Unit,
    onDismiss: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(CITY_VILLAS_CATALOG) { villa ->
            val isOwned = ownedVillaIds.contains(villa.id)
            val formattedPrice = String.format(Locale.US, "%,d ر.س / $", villa.price)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isOwned) Color(0xFF064E3B) else Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isOwned) Color(0xFF10B981) else Color(0xFF38BDF8)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                    text = "مملوكة لك ✅",
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Text(
                                text = formattedPrice,
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
                            modifier = Modifier.fillMaxWidth().testTag("mall_buy_villa_${villa.id}")
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("شراء الفيلا ($formattedPrice)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                onGoToVilla(villa)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("mall_enter_villa_${villa.id}")
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("انتقال ودخول الفيلا 🏡", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemsListTab(
    items: List<MallItem>,
    playerMoney: Int,
    actionLabel: String,
    actionColor: Color,
    onBuyItem: (item: MallItem) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            val canAfford = item.price == 0 || playerMoney >= item.price

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.emoji, fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = item.nameArabic,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = item.descriptionArabic,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (item.price == 0) "مجاناً 0$" else "${item.price}$",
                                color = if (item.price == 0) Color(0xFF34D399) else Color(0xFFFFD600),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = { onBuyItem(item) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) actionColor else Color(0xFF475569)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("buy_mall_item_${item.id}")
                    ) {
                        Text(
                            text = if (item.price == 0) "شرب 💧" else "شراء (${item.price}$)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
