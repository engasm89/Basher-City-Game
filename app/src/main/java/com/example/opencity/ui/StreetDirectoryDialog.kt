package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import com.example.opencity.model.Building
import com.example.opencity.model.HouseSideQuest
import com.example.opencity.model.StreetZone
import java.text.NumberFormat
import java.util.Locale

@Composable
fun StreetDirectoryDialog(
    buildings: List<Building>,
    initialZone: StreetZone?,
    playerMoney: Int,
    purchasedHouseIds: Set<String>,
    rentedHouseIds: Set<String>,
    unlockedHouseIds: Set<String>,
    completedHouseQuests: Set<String>,
    hasMasterGardenKey: Boolean,
    onTeleportToBuilding: (Building) -> Unit,
    onOpenStreetHousesMarket: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedZone by remember { mutableStateOf(initialZone ?: StreetZone.MAIN_STREET) }
    val zoneBuildings = remember(buildings, selectedZone) {
        buildings.filter { it.streetZone == selectedZone && !it.id.startsWith("city_street_house_") }
    }
    val accent = Color(selectedZone.accentColorHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 940.dp)
                .fillMaxHeight(0.88f)
                .padding(8.dp)
                .testTag("street_directory_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFA0B1120),
            border = BorderStroke(1.5.dp, accent)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🏘️", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "دليل شوارع وبيوت المدينة",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "رصيدك: ${NumberFormat.getNumberInstance(Locale.US).format(playerMoney)}$ • ${if (hasMasterGardenKey) "🗝️ مفتاح الحديقة معك" else "🔒 ابحث عن المفتاح في الحي القديم"}",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_street_directory_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق دليل الشوارع",
                            tint = Color.White
                        )
                    }
                }

                // Direct button to Street Houses Market («مكان شراء بيوت الشارع»)
                Button(
                    onClick = {
                        onDismiss()
                        onOpenStreetHousesMarket()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("directory_open_street_houses_market_button")
                ) {
                    Text(
                        text = "🏘️🔑 فتح مكان شراء بيوت الشارع (شراء بيوت كل الشوارع)",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                // 4 Street Category Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StreetZone.entries.forEach { zone ->
                        val isSelected = selectedZone == zone
                        val zoneAccent = Color(zone.accentColorHex)
                        Surface(
                            onClick = { selectedZone = zone },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) zoneAccent.copy(alpha = 0.25f) else Color(0xFF1E293B),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) zoneAccent else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("street_tab_${zone.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = zone.iconEmoji, fontSize = 16.sp)
                                Text(
                                    text = zone.arabicName,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Selected Street Classification Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.14f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedZone.iconEmoji} ${selectedZone.arabicName} (${selectedZone.englishName})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "تصنيف الشارع: ${selectedZone.classificationArabic}",
                                color = accent,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Buildings List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(zoneBuildings, key = { it.id }) { b ->
                        val isOwned = purchasedHouseIds.contains(b.id)
                        val isRented = rentedHouseIds.contains(b.id)
                        val isUnlocked = !b.isLockedByDefault || unlockedHouseIds.contains(b.id)
                        val isQuestDone = completedHouseQuests.contains(b.id)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131C2E)),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF283548))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = b.iconEmoji, fontSize = 22.sp)
                                        Text(
                                            text = b.arabicName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }

                                    val statusText = when {
                                        isOwned -> "مملوك لك ✅"
                                        isRented -> "مستأجر ✅"
                                        !isUnlocked -> "مغلق بالمفتاح 🔒"
                                        b.salePrice > 0 -> "${NumberFormat.getNumberInstance(Locale.US).format(b.salePrice)}$"
                                        b.rentPrice > 0 -> "إيجار ${b.rentPrice}$"
                                        else -> "مفتوح 🔓"
                                    }
                                    val statusColor = when {
                                        isOwned || isRented -> Color(0xFF22C55E)
                                        !isUnlocked -> Color(0xFFEF4444)
                                        b.salePrice > 0 -> Color(0xFFF59E0B)
                                        else -> Color(0xFF38BDF8)
                                    }
                                    Surface(
                                        color = statusColor.copy(alpha = 0.18f),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, statusColor)
                                    ) {
                                        Text(
                                            text = statusText,
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                if (b.facadeDescription.isNotBlank()) {
                                    Text(
                                        text = b.facadeDescription,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 13.sp
                                    )
                                }

                                if (b.listingBadge != null) {
                                    Text(
                                        text = "🏷️ ${b.listingBadge}",
                                        color = Color(0xFFFDE68A),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                if (b.houseSideQuest != HouseSideQuest.NONE) {
                                    Text(
                                        text = if (isQuestDone) "✅ مكتملة: ${b.houseSideQuest.titleArabic}"
                                        else "⭐ مهمة جانبية: ${b.houseSideQuest.titleArabic}",
                                        color = if (isQuestDone) Color(0xFF4ADE80) else Color(0xFF38BDF8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = { onTeleportToBuilding(b) },
                                    colors = ButtonDefaults.buttonColors(containerColor = accent),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("teleport_street_building_${b.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "انتقال إلى أمام الباب",
                                        color = Color.Black,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
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
