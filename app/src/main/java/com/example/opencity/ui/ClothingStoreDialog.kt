package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.opencity.model.ClothingBrand
import com.example.opencity.model.ClothingCatalog
import com.example.opencity.model.ClothingItem
import com.example.opencity.model.ClothingSlot
import com.example.opencity.model.PlayerOutfit
import java.text.NumberFormat
import java.util.Locale

/**
 * متجر الأواعي والماركات العالمية (Nike • Adidas • Jordan • Puma) وخزانة الملابس الشخصية («مكان من معاي»).
 * Buying any outfit piece immediately equips it onto the 3D player character; Wardrobe tab lets players swap owned clothes anytime.
 */
@Composable
fun ClothingStoreDialog(
    playerMoney: Int,
    ownedClothingIds: Set<String>,
    currentOutfit: PlayerOutfit,
    initialTab: Int = 0, // 0 = Brands Store (مكان الأواعي والماركات), 1 = My Wardrobe (خزانتي معي)
    initialBrandFilter: ClothingBrand? = null,
    initialSlotFilter: ClothingSlot? = null,
    onBuyAndWear: (ClothingItem) -> Unit,
    onEquipOwned: (ClothingItem) -> Unit,
    onDismiss: () -> Unit
) {
    var activeTab by remember { mutableIntStateOf(initialTab) }
    var selectedBrand by remember { mutableStateOf(initialBrandFilter) }
    var selectedSlot by remember { mutableStateOf(initialSlotFilter) }
    val numFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    val filteredItems = remember(activeTab, selectedBrand, selectedSlot, ownedClothingIds) {
        ClothingCatalog.allItems.filter { item ->
            val tabMatch = if (activeTab == 1) ownedClothingIds.contains(item.id) else true
            val brandMatch = selectedBrand == null || item.brand == selectedBrand
            val slotMatch = selectedSlot == null || item.slot == selectedSlot
            tabMatch && brandMatch && slotMatch
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 920.dp)
                .fillMaxHeight(0.90f)
                .testTag("clothing_store_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0F1E)),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Title, Wallet & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (activeTab == 0) "👕 متجر الأواعي والماركات (Nike • Adidas)" else "👔 خزانتي معي (ملابسي المملوكة)",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "عند شراء أي قطعة تلبسها شخصيتك فوراً في العالم ثلاثي الأبعاد!",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0xFF064E3B),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Text(
                                text = "💵 ${numFormatter.format(playerMoney)}$",
                                color = Color(0xFF6EE7B7),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .testTag("close_clothing_dialog_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                        }
                    }
                }

                // Current Live Equipped Outfit Preview Strip
                Surface(
                    color = Color(0xFF111827),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "✨ الطقم المرتدى حالياً على شخصيتك:",
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutfitPreviewChip(
                                label = "بلوزة",
                                item = currentOutfit.top,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedSlot = ClothingSlot.TOP }
                            )
                            OutfitPreviewChip(
                                label = "بنطلون",
                                item = currentOutfit.pants,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedSlot = ClothingSlot.PANTS }
                            )
                            OutfitPreviewChip(
                                label = "حذاء",
                                item = currentOutfit.shoes,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedSlot = ClothingSlot.SHOES }
                            )
                            OutfitPreviewChip(
                                label = "إكسسوار",
                                item = currentOutfit.accessory,
                                modifier = Modifier.weight(1f),
                                onClick = { selectedSlot = ClothingSlot.ACCESSORY }
                            )
                        }
                    }
                }

                // Primary Tabs: [متجر الشركات والماركات] vs [خزانة ملابسي معي]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { activeTab = 0 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeTab == 0) Color(0xFFFF6B00) else Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("tab_clothing_store")
                    ) {
                        Text(
                            text = "🛍️ مكان الأواعي والماركات",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Button(
                        onClick = { activeTab = 1 },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeTab == 1) Color(0xFF0284C7) else Color(0xFF1E293B)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("tab_my_wardrobe")
                    ) {
                        Text(
                            text = "👔 خزانتي معي (${ownedClothingIds.size})",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Brand Filter Chips (الشركات: Nike, Adidas, Jordan, Puma)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BrandFilterChip(
                        text = "كل الشركات",
                        isSelected = selectedBrand == null,
                        accentColor = Color(0xFF38BDF8),
                        onClick = { selectedBrand = null }
                    )
                    ClothingBrand.entries.forEach { brand ->
                        BrandFilterChip(
                            text = "${brand.arabicName} (${brand.badgeText})",
                            isSelected = selectedBrand == brand,
                            accentColor = Color(brand.accentColorHex),
                            onClick = { selectedBrand = if (selectedBrand == brand) null else brand }
                        )
                    }
                }

                // Slot Filter Chips (بلوزة / بنطلون / حذاء / قبعة)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BrandFilterChip(
                        text = "كل الأواعي",
                        isSelected = selectedSlot == null,
                        accentColor = Color(0xFF10B981),
                        onClick = { selectedSlot = null }
                    )
                    ClothingSlot.entries.forEach { slot ->
                        BrandFilterChip(
                            text = "${slot.emoji} ${slot.arabicName}",
                            isSelected = selectedSlot == slot,
                            accentColor = Color(0xFF10B981),
                            onClick = { selectedSlot = if (selectedSlot == slot) null else slot }
                        )
                    }
                }

                // Catalog / Wardrobe List
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("👕👖👟", fontSize = 36.sp)
                            Text(
                                text = "لا توجد قطع مطابقة في هذا القسم — تصفح متجر الماركات لشراء ملابس جديدة!",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(filteredItems, key = { it.id }) { item ->
                            val isOwned = ownedClothingIds.contains(item.id)
                            val isEquipped = when (item.slot) {
                                ClothingSlot.TOP -> currentOutfit.top.id == item.id
                                ClothingSlot.PANTS -> currentOutfit.pants.id == item.id
                                ClothingSlot.SHOES -> currentOutfit.shoes.id == item.id
                                ClothingSlot.ACCESSORY -> currentOutfit.accessory.id == item.id
                            }
                            ClothingItemCard(
                                item = item,
                                isOwned = isOwned,
                                isEquipped = isEquipped,
                                canAfford = playerMoney >= item.price,
                                onAction = {
                                    if (isOwned) {
                                        onEquipOwned(item)
                                    } else {
                                        onBuyAndWear(item)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutfitPreviewChip(
    label: String,
    item: ClothingItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(item.brand.accentColorHex).copy(alpha = 0.7f)),
        modifier = modifier.height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(item.swatchColorHex), Color(item.accentSwatchHex))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp,
                    maxLines = 1
                )
                Text(
                    text = item.brand.arabicName,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun BrandFilterChip(
    text: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) accentColor.copy(alpha = 0.25f) else Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accentColor else Color(0xFF334155)
        ),
        modifier = Modifier.height(48.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ClothingItemCard(
    item: ClothingItem,
    isOwned: Boolean,
    isEquipped: Boolean,
    canAfford: Boolean,
    onAction: () -> Unit
) {
    val brandColor = Color(item.brand.accentColorHex)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEquipped) Color(0xFF0F293A) else Color(0xFF141B2D)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isEquipped) 2.dp else 1.dp,
            color = if (isEquipped) Color(0xFF38BDF8) else brandColor.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clothing_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Color & Brand Preview Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(item.swatchColorHex), Color(item.accentSwatchHex))
                        )
                    )
                    .border(2.dp, brandColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.emoji, fontSize = 26.sp)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = brandColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, brandColor)
                    ) {
                        Text(
                            text = item.brand.badgeText,
                            color = brandColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = item.slot.arabicName,
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = item.arabicName,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.descriptionArabic,
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onAction,
                enabled = !isEquipped && (isOwned || canAfford),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isEquipped -> Color(0xFF0284C7)
                        isOwned -> Color(0xFF10B981)
                        canAfford -> brandColor
                        else -> Color(0xFF334155)
                    },
                    disabledContainerColor = if (isEquipped) Color(0xFF0369A1) else Color(0xFF334155)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("clothing_action_${item.id}")
            ) {
                when {
                    isEquipped -> {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("مرتدٍ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    isOwned -> {
                        Text("لبس الآن", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }
                    else -> {
                        Text("شراء ولبس\n${item.price}$", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
