package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opencity.model.BuildingInterior
import com.example.opencity.model.InteriorProp
import com.example.opencity.model.InteriorType

/**
 * Interior HUD Overlay inside enterable buildings (Villas, Supermarket, Hospital, Police Station, Cafe, Terminal).
 * For the Player Villa, every single appliance and furniture item has its dedicated interactive button.
 */
@Composable
fun InteriorHudOverlay(
    interiorType: InteriorType,
    playerMoney: Int,
    onInteractProp: (prop: InteriorProp) -> Unit,
    onExitBuilding: () -> Unit,
    currentFloor: Int = 1,
    onSwitchFloor: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val interior = remember(interiorType) { BuildingInterior.getInterior(interiorType) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 980.dp)
            .padding(12.dp)
    ) {
        // Top Room Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xDD0F172A)),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF10B981))
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
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF059669)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = interior.npcAvatarEmoji, fontSize = 22.sp)
                    }

                    Column {
                        Text(
                            text = interior.titleArabic,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = interior.npcGreeting,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6EE7B7),
                            fontSize = 11.sp
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onExitBuilding,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .focusProperties { canFocus = false }
                            .testTag("exit_interior_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروج للمدينة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (interiorType == InteriorType.PLAYER_VILLA && onSwitchFloor != null) {
                        val targetFloor = if (currentFloor == 1) 2 else 1
                        Button(
                            onClick = { onSwitchFloor(targetFloor) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentFloor == 1) Color(0xFF2563EB) else Color(0xFF7C3AED)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                            modifier = Modifier
                                .focusProperties { canFocus = false }
                                .testTag("villa_switch_floor_button")
                        ) {
                            Text(
                                text = if (currentFloor == 1) "الطابق 1 • صعود للطابق 2 🪜" else "الطابق 2 • نزول للطابق 1 🪜",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Interactive Props Header
        val interactiveProps = interior.props.filter { it.isInteractive }
        if (interactiveProps.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(16.dp))
                    Text(
                        text = if (interiorType == InteriorType.PLAYER_VILLA) "مرافق وأثاث الفيلا (ط1 و ط2):" else "الأغراض والمرافق التفاعلية المتاحة:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${interactiveProps.size} مرافق تفاعلية",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Horizontally Scrollable Bar with Interactive Buttons
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(interactiveProps) { prop ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE1E293B)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF38BDF8)),
                        modifier = Modifier.widthIn(min = 155.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = prop.arabicName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )

                            Button(
                                onClick = {
                                    onInteractProp(prop)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (prop.name) {
                                        "Bed", "SecondBed" -> Color(0xFF6366F1)
                                        "Pool", "Jacuzzi", "Shower", "BathroomSink" -> Color(0xFF0284C7)
                                        "StairsFloor2" -> Color(0xFF7C3AED)
                                        "Safe" -> Color(0xFFD97706)
                                        else -> Color(0xFF059669)
                                    }
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusProperties { canFocus = false }
                                    .testTag("prop_button_${prop.name}")
                            ) {
                                Text(
                                    text = prop.actionText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
