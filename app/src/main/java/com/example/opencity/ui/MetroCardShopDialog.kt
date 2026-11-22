package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class MetroCardOfferTier(
    val id: String,
    val arabicTitle: String,
    val subtitleArabic: String,
    val priceEgp: Int,
    val emoji: String,
    val badgeColor: Color
)

data class MetroCardCustomerRequest(
    val customerName: String,
    val avatarEmoji: String,
    val greetingArabic: String,
    val requestedTierId: String
)

object MetroCardShopCatalog {
    val tiers = listOf(
        MetroCardOfferTier(
            id = "card_single",
            arabicTitle = "كارت مترو عادي",
            subtitleArabic = "رحلة واحدة عبر الخط المعلق",
            priceEgp = 5,
            emoji = "💳",
            badgeColor = Color(0xFF06B6D4)
        ),
        MetroCardOfferTier(
            id = "card_silver_10",
            arabicTitle = "كارت فضي (10 رحلات)",
            subtitleArabic = "اقتصادي للموظفين والطلاب",
            priceEgp = 45,
            emoji = "🪙",
            badgeColor = Color(0xFF94A3B8)
        ),
        MetroCardOfferTier(
            id = "card_gold_vip",
            arabicTitle = "كارت ذهبي أسبوعي VIP",
            subtitleArabic = "رحلات غير محدودة 7 أيام",
            priceEgp = 150,
            emoji = "👑",
            badgeColor = Color(0xFFF59E0B)
        ),
        MetroCardOfferTier(
            id = "card_platinum_month",
            arabicTitle = "كارت بلاتيني شهري",
            subtitleArabic = "بطاقة ذكية شاملة طوال الشهر",
            priceEgp = 500,
            emoji = "💎",
            badgeColor = Color(0xFF8B5CF6)
        )
    )

    val customerQueue = listOf(
        MetroCardCustomerRequest(
            customerName = "أحمد المسافر",
            avatarEmoji = "🧑",
            greetingArabic = "مرحباً! أريد كارت رحلة واحدة إلى محطة النخيل.",
            requestedTierId = "card_single"
        ),
        MetroCardCustomerRequest(
            customerName = "سارة طالبة الجامعة",
            avatarEmoji = "👩",
            greetingArabic = "لو سمحت، عايزة كارت فضي 10 رحلات للتنقل يومياً.",
            requestedTierId = "card_silver_10"
        ),
        MetroCardCustomerRequest(
            customerName = "كابتن الروبوت الآلي",
            avatarEmoji = "🤖",
            greetingArabic = "جاري طلب الكارت الذهبي الأسبوعي VIP لشحن التنقل السريع!",
            requestedTierId = "card_gold_vip"
        ),
        MetroCardCustomerRequest(
            customerName = "الأستاذ كريم رجل الأعمال",
            avatarEmoji = "🤵",
            greetingArabic = "من فضلك جهز لي الكارت البلاتيني الشهري المفتوح الآن.",
            requestedTierId = "card_platinum_month"
        ),
        MetroCardCustomerRequest(
            customerName = "ليلى موظفة المول",
            avatarEmoji = "👩‍💼",
            greetingArabic = "أهلاً! محتاجة كارت فضي 10 رحلات عشان شغلي في المول.",
            requestedTierId = "card_silver_10"
        )
    )
}

@Composable
fun MetroCardShopDialog(
    playerMoney: Int,
    cardsSoldCount: Int,
    totalRevenueEgp: Int,
    cardStockCount: Int,
    onSellCard: (tier: MetroCardOfferTier, customerName: String?) -> Boolean,
    onPrintCards: (amount: Int) -> Unit,
    onTeleportToShop: () -> Unit,
    onDismiss: () -> Unit
) {
    var customerIndex by remember { mutableIntStateOf(0) }
    val currentCustomer = MetroCardShopCatalog.customerQueue[customerIndex % MetroCardShopCatalog.customerQueue.size]
    val requestedTier = MetroCardShopCatalog.tiers.first { it.id == currentCustomer.requestedTierId }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
                .fillMaxHeight(0.88f)
                .testTag("metro_card_shop_dialog"),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF071A2F),
            border = BorderStroke(2.dp, Color(0xFF22D3EE)),
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0C2D4D), Color(0xFF071A2F), Color(0xFF040D1A))
                        )
                    )
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0E7490)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("💳", fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = "مركزي لبيع كروت المترو",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "بع كروت المترو للركاب واكسب المال مباشرة 🚇💰",
                                color = Color(0xFF67E8F9),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                onTeleportToShop()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("teleport_to_metro_card_shop_button")
                        ) {
                            Text("📍 انتقل للمحل", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                                .testTag("close_metro_card_shop_dialog")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Owner Wallet, Revenue & Stock Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShopStatBadge(
                        label = "رصيدك",
                        value = "$playerMoney ج",
                        accent = Color(0xFF4ADE80),
                        modifier = Modifier.weight(1f)
                    )
                    ShopStatBadge(
                        label = "كروت مباعة",
                        value = "$cardsSoldCount 💳",
                        accent = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f)
                    )
                    ShopStatBadge(
                        label = "أرباح المحل",
                        value = "$totalRevenueEgp ج",
                        accent = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1f)
                    )
                    ShopStatBadge(
                        label = "المخزون",
                        value = "$cardStockCount",
                        accent = if (cardStockCount > 3) Color(0xFFA78BFA) else Color(0xFFF87171),
                        modifier = Modifier.weight(0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Card Printer / Restock Bar
                Surface(
                    color = Color(0xFF0D2847),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E4E79)),
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
                                text = "🖨️ طابعة شحن كروت المترو الذكية",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (cardStockCount == 0) "نفد المخزون! اطبع كروت جديدة للبيع فوراً" else "اطبع كروت NFC جديدة لتبقى جاهزاً للركاب",
                                color = if (cardStockCount == 0) Color(0xFFFCA5A5) else Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Button(
                            onClick = { onPrintCards(10) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("print_metro_cards_stock_button")
                        ) {
                            Text("🖨️ اطبع +10", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Active Customer Queue Card
                    Surface(
                        color = Color(0xFF0A2E45),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF22D3EE)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_metro_customer_card")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF164E63)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(currentCustomer.avatarEmoji, fontSize = 24.sp)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "الزبون عند الشباك: ${currentCustomer.customerName}",
                                        color = Color(0xFF67E8F9),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "«${currentCustomer.greetingArabic}»",
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Surface(
                                color = Color(0x33000000),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${requestedTier.emoji} المطلوب: ${requestedTier.arabicTitle}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "+${requestedTier.priceEgp} جنيه",
                                        color = Color(0xFF4ADE80),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (onSellCard(requestedTier, currentCustomer.customerName)) {
                                            customerIndex++
                                        }
                                    },
                                    enabled = cardStockCount > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF16A34A),
                                        disabledContainerColor = Color(0xFF334155)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("sell_to_current_customer_button")
                                ) {
                                    Text(
                                        text = if (cardStockCount > 0) "✅ بيع للزبون (+${requestedTier.priceEgp} ج)" else "اطبع كروت أولاً 🖨️",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = { customerIndex++ },
                                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("next_metro_customer_button")
                                ) {
                                    Text("🔄 زبون تالي", color = Color(0xFF7DD3FC), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Text(
                        text = "💳 بيع مباشر على الكونتر (اختر نوع الكارت لبيعه الآن):",
                        color = Color(0xFFBAE6FD),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )

                    // Direct Sell Card Tiers
                    MetroCardShopCatalog.tiers.forEach { tier ->
                        Surface(
                            color = Color(0xFF0E233C),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, tier.badgeColor.copy(alpha = 0.65f)),
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(tier.badgeColor.copy(alpha = 0.22f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(tier.emoji, fontSize = 22.sp)
                                    }
                                    Column {
                                        Text(
                                            text = tier.arabicTitle,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = tier.subtitleArabic,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onSellCard(tier, null) },
                                    enabled = cardStockCount > 0,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0284C7),
                                        disabledContainerColor = Color(0xFF334155)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("sell_direct_${tier.id}_button")
                                ) {
                                    Text(
                                        text = "بيع +${tier.priceEgp} ج",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopStatBadge(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFF0B223B),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
            Text(
                text = value,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
