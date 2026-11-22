package com.example.opencity.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.opencity.model.Building
import com.example.opencity.model.District
import com.example.opencity.model.Vector3

@Composable
fun FullMapDialog(
    playerPos: Vector3,
    buildings: List<Building>,
    onFastTravel: (destination: Vector3) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBuilding by remember { mutableStateOf<Building?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 980.dp)
                .fillMaxHeight(0.90f)
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "خريطة المدينة الكبرى",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "اضغط على أي مَعلَم للانتقال السريع إليه أو استكشاف أحيائه",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("close_map_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Map Canvas Area with shared world-to-dp/px projection
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF043B64)) // Huge vast open ocean surrounding the city
                        .border(1.dp, Color(0xFF0EA5E9), RoundedCornerShape(16.dp))
                ) {
                    val worldCenterX = 75f
                    val worldCenterZ = 78f
                    val worldSpan = 920f
                    val scaleDp = ((minOf(maxWidth.value, maxHeight.value) - 24f).coerceAtLeast(180f)) / worldSpan

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val scale = scaleDp.dp.toPx()

                        fun mapX(wx: Float) = centerX + (wx - worldCenterX) * scale
                        fun mapY(wz: Float) = centerY + (wz - worldCenterZ) * scale

                        // Turquoise coastal lagoon water around shoreline
                        drawRect(
                            color = Color(0xFF0284C7),
                            topLeft = Offset(mapX(-380f), mapY(-365f)),
                            size = Size(910f * scale, 885f * scale)
                        )
                        // Shallow aqua foam rim
                        drawRect(
                            color = Color(0xFF38BDF8),
                            topLeft = Offset(mapX(-324f), mapY(-306f)),
                            size = Size(798f * scale, 768f * scale)
                        )
                        // Golden Sandy Beach Perimeter surrounding all 4 sides of the city
                        drawRect(
                            color = Color(0xFFE7C57D),
                            topLeft = Offset(mapX(-315f), mapY(-297f)),
                            size = Size(780f * scale, 750f * scale)
                        )
                        // City Green Land Island Interior
                        drawRect(
                            color = Color(0xFF1A2F26),
                            topLeft = Offset(mapX(-256f), mapY(-238f)),
                            size = Size(662f * scale, 632f * scale)
                        )

                        // District zone backgrounds
                        drawRect(
                            color = Color(0x2238BDF8),
                            topLeft = Offset(mapX(-90f), mapY(-100f)),
                            size = Size(180f * scale, 130f * scale)
                        ) // Downtown
                        drawRect(
                            color = Color(0x2222C55E),
                            topLeft = Offset(mapX(10f), mapY(180f)),
                            size = Size(330f * scale, 165f * scale)
                        ) // Dedicated Suburban Villa District

                        // Waterfront Marina Canal & Lake (East Grass Peninsula)
                        drawRect(
                            color = Color(0x660284C7),
                            topLeft = Offset(mapX(236f - 23f), mapY(-115f - 42f)),
                            size = Size(46f * scale, 84f * scale)
                        )

                        // 4 Coastal Wooden Piers
                        val pierColor = Color(0xFFA16B3B)
                        drawRect(color = pierColor, topLeft = Offset(mapX(-3.1f), mapY(-334f)), size = Size(6.2f * scale, 48f * scale))
                        drawRect(color = pierColor, topLeft = Offset(mapX(171.9f), mapY(441f)), size = Size(6.2f * scale, 48f * scale))
                        drawRect(color = pierColor, topLeft = Offset(mapX(-351f), mapY(-23.1f)), size = Size(48f * scale, 6.2f * scale))
                        drawRect(color = pierColor, topLeft = Offset(mapX(451f), mapY(36.9f)), size = Size(48f * scale, 6.2f * scale))

                        // 4 Coastal Swap Stations ("محطات البدالة")
                        for (st in com.example.opencity.model.CityData.COASTAL_SWAP_STATIONS) {
                            val cx = mapX(st.padX)
                            val cy = mapY(st.padZ)
                            drawCircle(color = Color(0xFFFACC15), radius = 6.dp.toPx(), center = Offset(cx, cy))
                            drawCircle(color = Color(0xFF06B6D4), radius = 3.5.dp.toPx(), center = Offset(cx, cy))
                        }

                        // Roads
                        val roadColor = Color(0xFF334155)
                        for (rx in listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)) {
                            val zMin = -188f
                            val zMax = if (rx == 0f || rx == -195f) 348f else 182f
                            drawRect(
                                color = roadColor,
                                topLeft = Offset(mapX(rx) - 3f, mapY(zMin)),
                                size = Size(6f, (zMax - zMin) * scale)
                            )
                        }
                        for (rz in listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)) {
                            val xMin = -203f
                            val xMax = if (rz == 175f) 353f else 203f
                            drawRect(
                                color = roadColor,
                                topLeft = Offset(mapX(xMin), mapY(rz) - 3f),
                                size = Size((xMax - xMin) * scale, 6f)
                            )
                        }
                        // Villa Boulevards & East Villa Road
                        for (rz in listOf(256f, 340f)) {
                            drawRect(
                                color = roadColor,
                                topLeft = Offset(mapX(-203f), mapY(rz) - 3f),
                                size = Size(556f * scale, 6f)
                            )
                        }
                        drawRect(
                            color = roadColor,
                            topLeft = Offset(mapX(345f) - 3f, mapY(175f)),
                            size = Size(6f, 173f * scale)
                        )

                        // Compound 90 Avenue Private Beach, Sea Lagoon & Olympic Pool
                        drawRect(
                            color = Color(0xFFE5C47A),
                            topLeft = Offset(mapX(14f), mapY(187.5f)),
                            size = Size(47f * scale, 56f * scale)
                        )
                        drawRect(
                            color = Color(0xFF06B6D4),
                            topLeft = Offset(mapX(20f), mapY(192f)),
                            size = Size(36f * scale, 46f * scale)
                        )
                        drawRect(
                            color = Color(0xFF38BDF8),
                            topLeft = Offset(mapX(65f), mapY(197f)),
                            size = Size(18f * scale, 32f * scale)
                        )

                        // Elevated Sky Metro Rail Outer Loop & 4 Iconic Station Towers
                        drawRect(
                            color = Color(0xFF00E5FF),
                            topLeft = Offset(mapX(-222f), mapY(-206f)),
                            size = Size(596f * scale, 568f * scale),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                        for (st in com.example.opencity.model.SkyMetroSystem.STATIONS) {
                            drawCircle(
                                color = Color(0xFFFACC15),
                                radius = 5.5.dp.toPx(),
                                center = Offset(mapX(st.groundEntryX), mapY(st.groundEntryZ))
                            )
                        }

                        // Buildings
                        for (b in buildings) {
                            val bx = mapX(b.posX)
                            val by = mapY(b.posZ)
                            val bw = (b.width * scale).coerceAtLeast(6f)
                            val bh = (b.depth * scale).coerceAtLeast(6f)

                            val color = when (b.district) {
                                District.DOWNTOWN -> Color(0xFF38BDF8)
                                District.RESIDENTIAL -> Color(0xFF4ADE80)
                                District.COMMERCIAL -> Color(0xFFFACC15)
                                District.TRAVEL_HUB -> Color(0xFF818CF8)
                                District.SERVICES -> Color(0xFFF43F5E)
                                District.INDUSTRIAL -> Color(0xFF94A3B8)
                                District.SUBURBS -> Color(0xFFA3E635)
                            }

                            drawRect(
                                color = color,
                                topLeft = Offset(bx - bw / 2f, by - bh / 2f),
                                size = Size(bw, bh)
                            )
                        }

                        // Player marker
                        val px = mapX(playerPos.x)
                        val py = mapY(playerPos.z)
                        drawCircle(color = Color(0xFF38BDF8), radius = 8.dp.toPx(), center = Offset(px, py))
                        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(px, py))
                    }

                    // Interactive touch overlays aligned 1:1 with the Canvas coordinate system (curated landmarks & district houses)
                    buildings.filter {
                        (!it.id.startsWith("city_street_house_") && it.isEnterable) ||
                            it.type == com.example.opencity.model.BuildingType.SKYSCRAPER ||
                            it.type == com.example.opencity.model.BuildingType.GRAND_MOSQUE
                    }.forEach { b ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(
                                    x = ((b.posX - worldCenterX) * scaleDp).dp,
                                    y = ((b.posZ - worldCenterZ) * scaleDp).dp
                                )
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (selectedBuilding?.id == b.id) Color(0xFFE11D48) else Color(0xCC0F172A))
                                .border(1.5.dp, Color.White, CircleShape)
                                .clickable { selectedBuilding = b },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = b.iconEmoji, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Landmark selection detail card
                val sel = selectedBuilding
                if (sel != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = sel.iconEmoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = sel.arabicName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = sel.district.arabicName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF38BDF8)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val dest = if (sel.isEnterable) Vector3(sel.doorPosX, 0f, sel.doorPosZ + 2f) else Vector3(sel.posX, 0f, sel.posZ + sel.depth / 2f + 4f)
                                    onFastTravel(dest)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("fast_travel_button")
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("انتقال سريع")
                            }
                        }
                    }
                } else {
                    // Quick district, street houses market & beach fast travel buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onFastTravel(Vector3(53f, 0f, 182f)); onDismiss() },
                            modifier = Modifier.weight(1.1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🏖️ 90 أفينيو", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onFastTravel(Vector3(140f, 0f, 391f)); onDismiss() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🚇 المترو", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onFastTravel(Vector3(53.8f, 0f, 163.5f)); onDismiss() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🏡 فيلتي", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onFastTravel(Vector3(8.0f, 0f, 6.5f)); onDismiss() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🏙️ الوسط", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
