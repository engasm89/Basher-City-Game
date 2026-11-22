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
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.opencity.model.Building
import com.example.opencity.model.CityData
import com.example.opencity.model.Vector3
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.hypot

/**
 * Dedicated Real Estate Marketplace for buying Street Houses («مكان شراء بيوت الشارع»).
 */
@Composable
fun StreetHousesMarketDialog(
    buildings: List<Building>,
    playerPos: Vector3,
    playerMoney: Int,
    purchasedHouseIds: Set<String>,
    onBuyHouse: (Building) -> Unit,
    onTeleportToHouse: (Building) -> Unit,
    onEnterHouse: (Building) -> Unit,
    onDismiss: () -> Unit
) {
    // 0 = الكل, 1 = متاح للشراء, 2 = بيوتي المملوكة
    var filterTab by remember { mutableIntStateOf(0) }
    var sortByPrice by remember { mutableStateOf(false) }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    val allStreetHouses = remember(buildings) {
        buildings.filter { CityData.isPurchasableStreetHouse(it) }
    }

    val ownedCount = remember(allStreetHouses, purchasedHouseIds) {
        allStreetHouses.count { purchasedHouseIds.contains(it.id) }
    }

    val displayedHouses = remember(allStreetHouses, purchasedHouseIds, filterTab, sortByPrice, playerPos.x, playerPos.z) {
        val base = when (filterTab) {
            1 -> allStreetHouses.filter { !purchasedHouseIds.contains(it.id) }
            2 -> allStreetHouses.filter { purchasedHouseIds.contains(it.id) }
            else -> allStreetHouses
        }
        if (sortByPrice) {
            base.sortedBy { it.salePrice }
        } else {
            base.sortedBy { hypot(it.doorPosX - playerPos.x, it.doorPosZ - playerPos.z) }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 940.dp)
                .fillMaxHeight(0.90f)
                .padding(8.dp)
                .testTag("street_houses_market_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF508111E),
            border = BorderStroke(1.8.dp, Color(0xFF10B981))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF059669), Color(0xFFD97706))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏘️", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                text = "مكان شراء بيوت الشارع",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "مملوك: $ownedCount من ${allStreetHouses.size} • رصيدك: ${numberFormat.format(playerMoney)}$",
                                color = Color(0xFF6EE7B7),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_street_houses_market_button")
                    ) {
                        Text(
                            text = "❌ خروج",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Filter & Sort Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf(
                        "🏠 الكل (${allStreetHouses.size})" to 0,
                        "🔑 متاح للشراء (${allStreetHouses.size - ownedCount})" to 1,
                        "✅ بيوتي المملوكة ($ownedCount)" to 2
                    )
                    for ((label, idx) in tabs) {
                        val selected = filterTab == idx
                        Surface(
                            onClick = { filterTab = idx },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selected) Color(0xFF059669) else Color(0xFF1E293B),
                            border = BorderStroke(
                                1.2.dp,
                                if (selected) Color(0xFF34D399) else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("street_market_filter_$idx")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (selected) Color.White else Color(0xFFCBD5E1),
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = { sortByPrice = !sortByPrice },
                        shape = RoundedCornerShape(12.dp),
                        color = if (sortByPrice) Color(0xFFD97706) else Color(0xFF1E293B),
                        border = BorderStroke(
                            1.2.dp,
                            if (sortByPrice) Color(0xFFFBBF24) else Color(0xFF334155)
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("street_market_sort_toggle")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = if (sortByPrice) "💰 مرتب: الأرخص أولاً" else "📍 مرتب: الأقرب لك",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Houses List
                if (displayedHouses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (filterTab == 2) {
                                "لم تشترِ أي بيت في الشارع بعد! اضغط على «متاح للشراء» لشراء أول بيت."
                            } else {
                                "مبروك! اشتريت جميع بيوت الشارع في المدينة!"
                            },
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(displayedHouses, key = { it.id }) { house ->
                            val isOwned = purchasedHouseIds.contains(house.id)
                            val distanceMeters = hypot(
                                house.doorPosX - playerPos.x,
                                house.doorPosZ - playerPos.z
                            ).toInt()
                            val price = if (house.salePrice > 0) house.salePrice else 25000
                            val formattedPrice = numberFormat.format(price)

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111C2E)),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.4.dp,
                                    if (isOwned) Color(0xFF10B981) else Color(0xFF283548)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
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
                                            Text(text = house.iconEmoji, fontSize = 24.sp)
                                            Column {
                                                Text(
                                                    text = house.arabicName,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "📍 يبعد ${distanceMeters}م • 6 شبابيك مضيئة 🪟",
                                                    color = Color(0xFF38BDF8),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }

                                        if (isOwned) {
                                            Surface(
                                                color = Color(0x3310B981),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFF10B981))
                                            ) {
                                                Text(
                                                    text = "مملوك لك ✅",
                                                    color = Color(0xFF34D399),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        } else {
                                            Surface(
                                                color = Color(0x33F59E0B),
                                                shape = RoundedCornerShape(8.dp),
                                                border = BorderStroke(1.dp, Color(0xFFF59E0B))
                                            ) {
                                                Text(
                                                    text = "$formattedPrice$",
                                                    color = Color(0xFFFCD34D),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 13.sp,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (house.facadeDescription.isNotBlank()) {
                                        Text(
                                            text = house.facadeDescription,
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (!isOwned) {
                                            Button(
                                                onClick = { onBuyHouse(house) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1.25f)
                                                    .height(48.dp)
                                                    .testTag("buy_street_market_${house.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Key,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "شراء ($formattedPrice$)",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    onEnterHouse(house)
                                                    onDismiss()
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1.2f)
                                                    .height(48.dp)
                                                    .testTag("enter_street_market_${house.id}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DoorFront,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "دخول بيتي 🏡",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                onTeleportToHouse(house)
                                                onDismiss()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(48.dp)
                                                .testTag("teleport_street_market_${house.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "انتقال للباب",
                                                fontWeight = FontWeight.Bold,
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
    }
}
