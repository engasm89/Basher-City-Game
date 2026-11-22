package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

enum class MallShopTab(val arabicTitle: String, val emoji: String) {
    VEGETABLES("الخضار والفواكه", "🥬"),
    FOOD("الأكل والمطاعم", "🍔"),
    CLOTHES("الأواعي والملابس", "👕"),
    DRINKS_DESSERTS("العصائر والحلويات", "🥤"),
    SUPERMARKET_ALL("السوبرماركت والمحلات", "🛒")
}

enum class MallProductKind {
    VEGETABLE_FRUIT,
    HOT_FOOD,
    DRINK_WATER,
    DESSERT,
    SUPERMARKET_GOOD
}

data class MallProduct(
    val id: String,
    val arabicName: String,
    val categoryArabic: String,
    val descriptionArabic: String,
    val price: Int,
    val emoji: String,
    val kind: MallProductKind
)

object GrandMallCatalog {
    val vegetablesAndFruits = listOf(
        MallProduct("veg_tomato", "طماطم حمراء طازجة", "سوق الخضار", "طماطم بلدية طازجة من المزرعة", 3, "🍅", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_cucumber", "خيار أخضر مقرمش", "سوق الخضار", "خيار طازج ومقرمش للسلطة", 3, "🥒", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_lettuce", "خس أخضر طازج", "سوق الخضار", "أوراق خس خضراء طازجة ومغذية", 2, "🥬", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_carrot", "جزر حلو طازج", "سوق الخضار", "جزر برتقالي غني بالفيتامينات", 3, "🥕", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_potato", "بطاطا طازجة", "سوق الخضار", "بطاطا بلدية للشوي والقلي", 4, "🥔", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_pepper", "فلفل رومي ملون", "سوق الخضار", "فلفل أحمر وأخضر وأصفر طازج", 4, "🫑", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_eggplant", "باذنجان طازج", "سوق الخضار", "باذنجان أسود طازج للطبخ", 4, "🍆", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_onion", "بصل أحمر وأبيض", "سوق الخضار", "بصل بلدي عالي الجودة", 2, "🧅", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("fruit_apple", "تفاح أحمر مقرمش", "سوق الفواكه", "تفاح سكري طازج وبارد", 4, "🍎", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("fruit_banana", "موز طازج", "سوق الفواكه", "موز أصفر غني بالطاقة", 3, "🍌", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("fruit_orange", "برتقال عصير حلو", "سوق الفواكه", "برتقال طازج مليء بالعصير", 4, "🍊", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("fruit_watermelon", "بطيخ أحمر بارد", "سوق الفواكه", "بطيخ حلو ومنعش", 6, "🍉", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("fruit_strawberry", "فراولة حمراء طازجة", "سوق الفواكه", "فراولة لذيذة وصحية", 5, "🍓", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("fruit_grape", "عنب أخضر حلو", "سوق الفواكه", "عناقيد عنب حلو بدون بذور", 5, "🍇", MallProductKind.VEGETABLE_FRUIT),
        MallProduct("veg_basket", "سلة خضار وفواكه عائلية", "سوق الخضار", "تشكيلة كاملة من كل الخضار والفواكه", 16, "🧺", MallProductKind.VEGETABLE_FRUIT)
    )

    val hotFoods = listOf(
        MallProduct("food_burger", "كومبو برجر مشوي بالجبنة", "مطعم البرجر", "برجر لحم ساخن مع بطاطس مقرمشة", 15, "🍔", MallProductKind.HOT_FOOD),
        MallProduct("food_shawarma", "وجبة شاورما عربي دبل", "ركن الشاورما", "شاورما مشوية مع مثومة ومخلل", 12, "🌯", MallProductKind.HOT_FOOD),
        MallProduct("food_mansaf", "منسف لحم بالجميد الأصيل", "مطبخ الضيافة", "منسف لحم طازج مع أرز ومكسرات", 35, "🍛", MallProductKind.HOT_FOOD),
        MallProduct("food_kabsa", "كبسة دجاج بالبهارات", "المطبخ العربي", "كبسة متبلة ساخنة مع الدقوس", 26, "🍲", MallProductKind.HOT_FOOD),
        MallProduct("food_pizza", "بيتزا بيبروني وموزاريلا", "فرن البيتزا", "بيتزا مقرمشة مخبوزة على الحطب", 18, "🍕", MallProductKind.HOT_FOOD),
        MallProduct("food_kebab", "مشاوي كباب وأوصال مشكلة", "ركن المشاوي", "تشكيلة مشاوي على الفحم", 32, "🍖", MallProductKind.HOT_FOOD),
        MallProduct("food_crispy", "دجاج بروستد مقرمش", "مطعم البروستد", "قطع دجاج ذهبية مقرمشة ساخنة", 16, "🍗", MallProductKind.HOT_FOOD),
        MallProduct("food_pasta", "باستا ألفريدو بالكريمة", "المطعم الإيطالي", "مكرونة بالصوص الأبيض والدجاج", 20, "🍝", MallProductKind.HOT_FOOD),
        MallProduct("food_falafel", "ساندويتش فلافل وحمص", "ركن الشعبيات", "فلافل ساخنة مقرمشة مع خضار", 6, "🧆", MallProductKind.HOT_FOOD),
        MallProduct("food_fish", "سمك مشوي بالليمون", "ركن البحريات", "سمك طازج مشوي مع الأرز", 28, "🐟", MallProductKind.HOT_FOOD)
    )

    val drinksAndDesserts = listOf(
        MallProduct("drink_water", "ماء معدني بارد نقي", "برادة المول", "مياه شرب نقية عذبة مجاناً", 0, "💧", MallProductKind.DRINK_WATER),
        MallProduct("drink_orange", "عصير برتقال طبيعي طازج", "بار العصائر", "عصير معصور طازج بدون سكر مضاف", 6, "🍊", MallProductKind.DRINK_WATER),
        MallProduct("drink_mango", "سموذي مانجو وفراولة", "بار العصائر", "كوكتيل فواكه طبيعية بارد", 7, "🥭", MallProductKind.DRINK_WATER),
        MallProduct("drink_mojito", "موهيتو توت أزرق مثلج", "بار العصائر", "مشروب غازي بارد بالنعناع والتوت", 8, "🍹", MallProductKind.DRINK_WATER),
        MallProduct("drink_coffee", "دلة قهوة عربية بالهيل", "كافيه المول", "قهوة عربية أصيلة ساخنة", 5, "☕", MallProductKind.DRINK_WATER),
        MallProduct("dessert_knafeh", "كنافة نابلسية ساخنة", "ركن الحلويات", "كنافة بالجبنة الذائبة والفستق", 12, "🍮", MallProductKind.DESSERT),
        MallProduct("dessert_cake", "كيك لافا شوكولاتة", "ركن الحلويات", "كيك دافئ محشو بالشوكولاتة", 10, "🍰", MallProductKind.DESSERT),
        MallProduct("dessert_waffle", "وافل بالنوتيلا والفواكه", "ركن الحلويات", "وافل مقرمش مع موز وفراولة", 11, "🧇", MallProductKind.DESSERT),
        MallProduct("dessert_icecream", "بوظة آيس كريم مشكلة", "ركن الحلويات", "3 كرات آيس كريم بالفواكه", 6, "🍨", MallProductKind.DESSERT)
    )

    val supermarketAndStores = listOf(
        MallProduct("sup_bakery", "خبز فرنسي وكرواسون طازج", "المخبز الآلي", "مخبوزات ساخنة خارجة من الفرن الآن", 4, "🥐", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("sup_dairy", "حليب طازج وأجبان وبيض", "السوبرماركت", "منتجات ألبان يومية عالية الجودة", 8, "🥛", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("sup_olive", "زيت زيتون وزعتر وأغراض بيت", "السوبرماركت", "مونة المنزل كاملة من السوبرماركت", 14, "🫒", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("shop_perfume", "عطر العود والياسمين الفاخر", "محل العطور", "زجاجة عطر فاخرة برائحة تدوم طويلاً", 65, "✨", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("shop_watch", "ساعة ذكية وإلكترونيات المول", "متجر الإلكترونيات", "ساعة ذكية للمدينة والرياضة", 120, "⌚", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("shop_headphones", "سماعات رأس لاسلكية", "متجر الإلكترونيات", "سماعات صوت محيطي عالي النقاء", 55, "🎧", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("shop_toys", "كرة قدم وألعاب رياضية", "محل الألعاب", "كرة قدم احترافية للحدائق الخضراء", 25, "⚽", MallProductKind.SUPERMARKET_GOOD),
        MallProduct("shop_flowers", "باقة ورد وهدايا أنيقة", "محل الهدايا", "باقة ورود طبيعية منسقة", 20, "💐", MallProductKind.SUPERMARKET_GOOD)
    )
}

@Composable
fun GrandMallDialog(
    initialTab: MallShopTab = MallShopTab.VEGETABLES,
    playerMoney: Int,
    onBuyAndConsume: (MallProduct) -> Unit,
    onOpenClothingStore: () -> Unit,
    onSitAtDiningTable: () -> Unit,
    onEnterMall3D: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 980.dp)
                .fillMaxHeight(0.90f)
                .testTag("grand_mall_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.5.dp, Color(0xFFA855F7))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF7E22CE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "المول التجاري الكبير 🏬🛍️",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "أواعي • خضار وفواكه • مطاعم • سوبرماركت • 12 طاولة أكل",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFC4B5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFF14532D),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF4ADE80))
                        ) {
                            Text(
                                text = "💵 $playerMoney$",
                                color = Color(0xFF86EFAC),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("close_grand_mall_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "خروج من المول", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Actions Strip: 12 Food Court Tables + Enter 3D Mall Interior
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🍽️ ساحة الطعام: 12 طاولة كبيرة (48 مقعداً)",
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "اشترِ أكلاً وخضاراً واجلس على الطاولات لتناولها",
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onSitAtDiningTable,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("mall_sit_eat_table_button"),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🪑 اجلس وكل", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onEnterMall3D,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("enter_mall_3d_interior_button")
                ) {
                    Text(
                        text = "🚶 دخول المول 3D (التجول بين المحلات والـ 12 طاولة طعام)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MallShopTab.entries.forEach { tab ->
                        val selected = selectedTab == tab
                        Surface(
                            onClick = { selectedTab = tab },
                            color = if (selected) Color(0xFF2563EB) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (selected) Color(0xFF38BDF8) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("mall_tab_${tab.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(tab.emoji, fontSize = 16.sp)
                                Text(
                                    text = tab.arabicTitle,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Content List
                if (selectedTab == MallShopTab.CLOTHES) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFEC4899)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "👕👖👟 بوتيك الأواعي والماركات العالمية داخل المول",
                                    color = Color(0xFFF9A8D4),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "اشترِ تيشيرتات وهوديات نايكي (Nike) وأديداس (Adidas) وبناطيل الجينز والسبورت والأحذية والقبعات والنظارات — وتُلبس فوراً على شخصيتك في المدينة 3D!",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp
                                )
                                Button(
                                    onClick = onOpenClothingStore,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDB2777)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("mall_open_clothes_store_button")
                                ) {
                                    Icon(Icons.Default.Checkroom, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "👕 فتح محل الأواعي والقياس (لشراء ولبس الملابس)",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "الأقسام المتوفرة داخل محل الأواعي:",
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text("• 👕 تيشيرتات وهوديات نايكي (Nike ✓) وأديداس (Adidas ☰)", color = Color.White, fontSize = 12.sp)
                                Text("• 👖 بناطيل جينز وبناطيل رياضية مريحة", color = Color.White, fontSize = 12.sp)
                                Text("• 👟 أحذية سنيكرز رياضية ملونة", color = Color.White, fontSize = 12.sp)
                                Text("• 🧢 قبعات ونظارات شمسية وساعات تُرتدى مباشرة", color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    val itemsToShow = when (selectedTab) {
                        MallShopTab.VEGETABLES -> GrandMallCatalog.vegetablesAndFruits
                        MallShopTab.FOOD -> GrandMallCatalog.hotFoods
                        MallShopTab.DRINKS_DESSERTS -> GrandMallCatalog.drinksAndDesserts
                        MallShopTab.SUPERMARKET_ALL -> GrandMallCatalog.supermarketAndStores
                        MallShopTab.CLOTHES -> emptyList()
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(itemsToShow, key = { it.id }) { item ->
                            MallProductRow(
                                item = item,
                                playerMoney = playerMoney,
                                onBuyAndConsume = { onBuyAndConsume(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MallProductRow(
    item: MallProduct,
    playerMoney: Int,
    onBuyAndConsume: () -> Unit
) {
    val canAfford = playerMoney >= item.price
    val actionLabel = when (item.kind) {
        MallProductKind.VEGETABLE_FRUIT -> "شراء وأكل 🥬"
        MallProductKind.HOT_FOOD -> "أكل على الطاولة 🍽️"
        MallProductKind.DRINK_WATER -> if (item.price == 0) "اشرب مجاناً 💧" else "شراء وشرب 🥤"
        MallProductKind.DESSERT -> "شراء وأكل 🍰"
        MallProductKind.SUPERMARKET_GOOD -> "شراء 🛒"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            when (item.kind) {
                MallProductKind.VEGETABLE_FRUIT -> Color(0xFF22C55E)
                MallProductKind.HOT_FOOD -> Color(0xFFF97316)
                MallProductKind.DRINK_WATER -> Color(0xFF38BDF8)
                MallProductKind.DESSERT -> Color(0xFFEC4899)
                MallProductKind.SUPERMARKET_GOOD -> Color(0xFFEAB308)
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.emoji, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = item.arabicName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${item.categoryArabic} • ${item.descriptionArabic}",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (item.price == 0) "مجاني 🎉" else "${item.price}$",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onBuyAndConsume,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canAfford) Color(0xFF16A34A) else Color(0xFF475569)
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("mall_buy_${item.id}")
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(actionLabel, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
