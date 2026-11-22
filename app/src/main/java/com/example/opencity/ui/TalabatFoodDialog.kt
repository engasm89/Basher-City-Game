package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.NumberFormat
import java.util.Locale

/**
 * World Food item in Talabat online delivery platform.
 */
data class TalabatDish(
    val id: String,
    val nameArabic: String,
    val cuisineArabic: String,
    val descriptionArabic: String,
    val price: Int,
    val emoji: String,
    val isBeverage: Boolean = false,
    val isWater: Boolean = false
)

val WORLD_TALABAT_MENU = listOf(
    // 🥩 Arabic & Traditional World Grills
    TalabatDish("mansaf", "منسف أردني ملكي باللحم والجميد", "🥩 مأكولات عربية أصيلة", "أرز بالزعفران مع قطع لحم خروف طرية ولبن جميد كركي ساخن ولوز محمص", 45, "🥘"),
    TalabatDish("kabsa", "كبسة لحم خليجية ملكية بالمكسرات", "🥩 مأكولات خليجية", "أرز بسمتي طويل الحبة مع لحم غنم طازج وتوابل خليجية وزعفران وزبيب", 40, "🍚"),
    TalabatDish("iraqi_kebab", "كباب عراقي مشوي على الفحم", "🥩 مشاوي عربية", "أسياخ كباب لحم بلدي مفروم مع دهن لية مشوي على الجمر مع خبز تنور حار", 35, "🍢"),
    TalabatDish("musakhan", "مسخن دجاج فلسطيني بلدي بالسماق", "🥩 مأكولات شامية", "خبز طابون مغطى ببصل مكرمل بزيت الزيتون البكر وسماق بلدي ودجاج محمر", 30, "🫓"),
    TalabatDish("royal_bbq", "مشكل مشاوي المدينة الفاخر (لحم وريش وكفتة)", "🥩 مشاوي عائلية", "تشكيلة لحوم ومشاوي كباب وريش وشقف مع مقبلات وحمص وبطاطس", 55, "🥩"),

    // 🍔 American Fast Food & Burgers
    TalabatDish("wagyu_burger", "برجر لحم واغيو دبل تشيز فاخر", "🍔 فاست فود أمريكي", "قطعتان من لحم الواغيو الفاخر مع جبنة الشيدر الذائبة وصوص سري وبطاطس كريسبي", 32, "🍔"),
    TalabatDish("crispy_chicken", "وجبة كرسبي تشيكن ستريبس المقرمشة", "🍔 وجبات سريعة", "قطع دجاج مقرمشة ذهبية مع صوص الرانش وصوص الباربكيو والبطاطس", 22, "🍗"),
    TalabatDish("philly_steak", "ساندوتش فيلي تشيز ستيك باللحم", "🍔 ساندوتشات أمريكية", "شرائح لحم ستيك بقري متبل مع بصل وفلفل مشوي وجبنة سويسرية ذائبة", 26, "🥪"),
    TalabatDish("loaded_fries", "بطاطس محمرة لوديد باللحم المفروم والجبن", "🍔 مقبلات سريعة", "بطاطس مقلية مغطاة بصوص الجبن الذائب ولحم مفروم وهلابينو", 18, "🍟"),

    // 🍕 Italian Pizza & Pasta
    TalabatDish("pizza_margherita", "بيتزا نابولي إيطالية بالريحان والموزاريلا", "🍕 مأكولات إيطالية", "عجينة نابولية تقليدية هشة مع صلصة طماطم سان مارزانو وموزاريلا طازجة", 26, "🍕"),
    TalabatDish("pizza_pepperoni", "بيتزا بيبروني بالجبنة الإيطالية المزدوجة", "🍕 بيتزا عالمية", "شرائح بيبروني بقري متبل مع صلصة إيطالية وجبن موزاريلا غني", 28, "🍕"),
    TalabatDish("pasta_alfredo", "باستا فيتوتشيني ألفريدو بالدجاج والفطر", "🍕 باستا إيطالية", "باستا إيطالية بصلصة الكريمة الغنية مع جبنة البارميزان وقطع الدجاج والفطر", 24, "🍝"),
    TalabatDish("lasagna_bolognese", "لازانيا بولونيز باللحم والباشميل", "🍕 مأكولات إيطالية", "طبقات معكرونة محشوة بصلصة اللحم الإيطالية المطهوة وباشميل غني", 25, "🍲"),

    // 🍣 Asian & Japanese Cuisine
    TalabatDish("sushi_platter", "طبق سوشي سالمون وتونة ياباني بلاتينيوم", "🍣 مأكولات يابانية", "تشكيلة قطع سوشي وماكي نيجيري بالسالمون الطازج والتونة والأفوكادو", 38, "🍣"),
    TalabatDish("ramen_beef", "نودلز رامين ياباني حار باللحم والبيض", "🍣 مأكولات آسيوية", "حساء الرامين الغني مع شرائح اللحم المتبل والبيض المسلوق والخضار", 25, "🍜"),
    TalabatDish("dim_sum", "فطائر ديم سام صينية مطهوة على البخار", "🍣 مأكولات صينية", "فطائر طرية محشوة باللحم والروبيان والخضار مع صلصة الصويا والسمسم", 20, "🥟"),

    // 🌮 Mexican Tacos & Burritos
    TalabatDish("taco_crispy", "تاكو لحم مكسيكي كريسبي بالجبن والصلصة", "🌮 مأكولات مكسيكية", "رقائق التاكو المقرمشة محشوة بلحم متبل مع صلصة جواكامولي وسالسا", 18, "🌮"),
    TalabatDish("burrito_giant", "بوريتو لحم فاهيتا عملاق بالأرز والفاصولياء", "🌮 مأكولات مكسيكية", "خبز تورتيلا كبير ملفوف بحشوة اللحم والأرز المكسيكي والصلصات الحارة", 22, "🌯"),

    // 🥙 Shawarma & Street Food
    TalabatDish("shawarma_double", "شاورما لحم عربي دوبل بالثومية والبطاطس", "🥙 ساندوتشات عربية", "خبز صاج مقمر مع لحم شاورما طري وثومية ومخلل خيار وبطاطس مقلية", 16, "🥙"),
    TalabatDish("falafel_plate", "صحن فلافل بلدي مقرمش مع طحينة وحمص", "🥙 فطور ومأكولات خفيفة", "حبات فلافل مقرمشة طازجة مع حمص بلدي وطحينة وسلطة خضراء وخبز ساخن", 12, "🧆"),

    // 🍰 Desserts & Sweets
    TalabatDish("knafeh", "كنافة نابلسية خشنة بالجبن والقطر الساخن", "🍰 حلويات شرقية", "كنافة ذهبية محشوة بالجبن البلدي الذائب ومسقية بالقطر ورشة فستق حلبي", 15, "🥮"),
    TalabatDish("lava_cake", "كيكة الشوكولاتة الذائبة (لافا كيك) مع آيس كريم", "🍰 حلويات غربية", "كيكة شوكولاتة داكنة دافئة محشوة شوكولاتة ذائبة مع بولة آيس كريم فانيليا", 18, "🍫"),
    TalabatDish("waffle_fruits", "وافل بلجيكي بالنوتيلا والفواكه الطازجة", "🍰 حلويات عالمية", "وافل مقرمش مغطى بصوص النوتيلا وشرائح الفراولة والموز", 16, "🧇"),

    // 🥤 Cold Beverages & Juices
    TalabatDish("fresh_orange", "عصير برتقال طبيعي طازج مثلج معصور فوراً", "🥤 عصائر طبيعية", "معصور من أجود أنواع البرتقال الطازج غني بفيتامين C بدون سكر مضاف", 12, "🍊", isBeverage = true),
    TalabatDish("mojito_berry", "موهيتو توت بري وليمون ونعناع مثلج", "🥤 مشروبات منعشة", "توت بري منعش مع أوراق النعناع وقطع الليمون ومياه صودا فوارة وثلج", 15, "🫐", isBeverage = true),
    TalabatDish("iced_cola", "مشروب كولا غازي مثلج بحجم عائلي", "🥤 مشروبات غازية", "مشروب غازي بارد مع فقاعات منعشة تروي العطش", 8, "🥤", isBeverage = true),

    // 💧 Pure Water Options
    TalabatDish("free_water", "كوب ماء نقي عذب بارد (شرب فوري مجاني)", "💧 مياه نقية", "مياه شرب نقية مبردة تروي عطشك وتجدد نشاطك بالكامل مجاناً", 0, "💧", isWater = true),
    TalabatDish("mineral_water", "قنينة مياه معدنية طبيعية باردة 500 مل", "💧 مياه معدنية", "مياه ينابيع طبيعية معقمة وصحية لترطيب الجسم والانتعاش", 4, "🧊", isWater = true),
    TalabatDish("sparkling_water", "مياه فوارة نقية مثلجة بقطع الليمون والنعناع", "💧 مياه فوارة", "مياه معدنية غازية فوارة ومنعشة جداً بعد الوجبات الدسمة", 7, "🫧", isWater = true)
)

/**
 * Talabat Food Delivery App Dialog (موقع وتطبيق طلبات لطلب طعام العالم بالمصاري)
 */
@Composable
fun TalabatFoodDialog(
    playerMoney: Int,
    onOrderDish: (dish: TalabatDish) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCuisineFilter by remember { mutableStateOf("الكل") }
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    val cuisineCategories = listOf(
        "الكل",
        "مشاوي وعربي",
        "برجر وسريع",
        "بيتزا وإيطالي",
        "ياباني وآسيوي",
        "مكسيكي وتاكو",
        "شاورما وساندوتش",
        "حلويات",
        "مشروبات",
        "مياه نقية"
    )

    val filteredDishes = WORLD_TALABAT_MENU.filter { dish ->
        val matchesSearch = searchQuery.isBlank() ||
                dish.nameArabic.contains(searchQuery, ignoreCase = true) ||
                dish.cuisineArabic.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCuisineFilter) {
            "الكل" -> true
            "مشاوي وعربي" -> dish.cuisineArabic.contains("عربية") || dish.cuisineArabic.contains("خليجية") || dish.cuisineArabic.contains("مشاوي") || dish.cuisineArabic.contains("شامية")
            "برجر وسريع" -> dish.cuisineArabic.contains("أمريكي") || dish.cuisineArabic.contains("سريعة")
            "بيتزا وإيطالي" -> dish.cuisineArabic.contains("إيطالي") || dish.cuisineArabic.contains("بيتزا")
            "ياباني وآسيوي" -> dish.cuisineArabic.contains("ياباني") || dish.cuisineArabic.contains("آسيوي") || dish.cuisineArabic.contains("صيني")
            "مكسيكي وتاكو" -> dish.cuisineArabic.contains("مكسيك")
            "شاورما وساندوتش" -> dish.cuisineArabic.contains("شاورما") || dish.cuisineArabic.contains("ساندوتش") || dish.cuisineArabic.contains("فطور")
            "حلويات" -> dish.cuisineArabic.contains("حلويات")
            "مشروبات" -> dish.isBeverage
            "مياه نقية" -> dish.isWater
            else -> true
        }

        matchesSearch && matchesCategory
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 960.dp)
                .fillMaxHeight(0.92f)
                .testTag("talabat_food_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF50D1117),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF5A00))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: Talabat Branding + Player Money + Red "X" Exit Button
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFFF5A00), Color(0xFFEA580C)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛵", fontSize = 24.sp)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "طلبات",
                                    color = Color(0xFFFF5A00),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "talabat World Food",
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "اطلب كل ما تحتاجه من طعام ومشروبات بالعالم • رصيدك: ${numberFormat.format(playerMoney)}$",
                                color = Color(0xFF4ADE80),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Prominent Red "X" Exit Button (إكس للخروج من موقع طلبات)
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFCA5A5)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("close_talabat_button")
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

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("ابحث عن أي أكلة أو وجبة أو مشروب في العالم...", color = Color(0xFF64748B), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFFF5A00)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.clickable { searchQuery = "" }
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF5A00),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFFFF5A00)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cuisine Filter Horizontal Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    cuisineCategories.forEach { category ->
                        val isSelected = selectedCuisineFilter == category
                        Surface(
                            color = if (isSelected) Color(0xFFFF5A00) else Color(0xFF1E293B),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFFF8A4C) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedCuisineFilter = category }
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dishes List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(filteredDishes) { dish ->
                        val canAfford = dish.price == 0 || playerMoney >= dish.price

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2E)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26334D)),
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
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color(0xFF0F172A)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(dish.emoji, fontSize = 26.sp)
                                    }
                                    Column {
                                        Text(
                                            text = dish.nameArabic,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = dish.cuisineArabic,
                                            color = Color(0xFFFF8A4C),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = dish.descriptionArabic,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp,
                                            maxLines = 2
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = if (dish.price == 0) "مجاناً 0$" else "${dish.price}$",
                                            color = if (dish.price == 0) Color(0xFF34D399) else Color(0xFFFFD600),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onOrderDish(dish) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (canAfford) Color(0xFFFF5A00) else Color(0xFF475569)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    modifier = Modifier.testTag("order_talabat_dish_${dish.id}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = if (dish.price == 0) "شرب 💧" else "اطلب 🛵",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Full-width Close Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("exit_talabat_dialog_bottom_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("❌", fontSize = 16.sp)
                        Text(
                            text = "خروج من موقع وتطبيق طلبات والعودة للمدينة (X)",
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
