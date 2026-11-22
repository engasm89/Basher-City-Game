package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Police vs Robbers Event Dialog («حدث شرطة وحرامية»):
 * - If Cop catches Robber -> Cop wins chase
 * - If Robber steals and escapes -> Robber wins escape
 * - Driving the event vehicle still earns standard driving money per second!
 * - Police car spawns at event building, Robber car spawns at event building
 * - As soon as the event finishes, both cars despawn immediately!
 */
@Composable
fun PoliceEventDialog(
    isEventActive: Boolean,
    onStartEvent: (role: String) -> Unit,
    onEndEventEarly: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("POLICE") } // "POLICE" or "ROBBER"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 720.dp)
                .heightIn(max = 640.dp)
                .testTag("police_event_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF2070D1A),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3B82F6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Bar
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
                                .background(Brush.linearGradient(listOf(Color(0xFF1D4ED8), Color(0xFFDC2626)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚨", fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = "حدث شرطة وحرامية (Cops vs Robbers)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "مواجهة بوليسية حماسية في شوارع المدينة",
                                color = Color(0xFF93C5FD),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Prominent X Close Button
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("close_police_event_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إغلاق (X)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Event Rules Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC1E293B)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "📜 قواعد التحدي:",
                            color = Color(0xFFFACC15),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• لو الشرطي أمسك الحرامي: يحقق الفوز ويودعه في السجن 🔒!",
                            color = Color(0xFF93C5FD),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• لو الحرامي نجح في الهروب: يفلت من المطاردة بنجاح 🏎️!",
                            color = Color(0xFFFDE047),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• تظهر سيارة الشرطة وسيارة الحرامي عند مبنى الحدث فور بدء التحدي.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                        Text(
                            text = "• أول ما ينتهي الحدث، تذهب وتختفي السيارتان فوراً من الموقع.",
                            color = Color(0xFF6EE7B7),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Role Selection Pills
                Text(
                    text = "اختر دورك في الحدث الآن:",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option 1: Police Role
                    Surface(
                        onClick = { selectedRole = "POLICE" },
                        color = if (selectedRole == "POLICE") Color(0xFF1E3A8A) else Color(0xFF0F172A),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (selectedRole == "POLICE") 2.dp else 1.dp,
                            if (selectedRole == "POLICE") Color(0xFF3B82F6) else Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("👮", fontSize = 32.sp)
                            Text(
                                text = "دور الشرطي",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "قُد سيارة الدورية، طارد الحرامي واقبض عليه بنجاح",
                                color = Color(0xFF93C5FD),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Option 2: Robber Role
                    Surface(
                        onClick = { selectedRole = "ROBBER" },
                        color = if (selectedRole == "ROBBER") Color(0xFF78350F) else Color(0xFF0F172A),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (selectedRole == "ROBBER") 2.dp else 1.dp,
                            if (selectedRole == "ROBBER") Color(0xFFF59E0B) else Color(0xFF334155)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🦹", fontSize = 32.sp)
                            Text(
                                text = "دور الحرامي",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "قُد سيارة الهروب، انطلق بأقصى سرعة واهرب من الشرطة",
                                color = Color(0xFFFDE68A),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Start / End Button
                if (!isEventActive) {
                    Button(
                        onClick = {
                            onStartEvent(selectedRole)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedRole == "POLICE") Color(0xFF2563EB) else Color(0xFFD97706)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_police_event_action_button")
                    ) {
                        Text(
                            text = if (selectedRole == "POLICE") "🚨 بدء الحدث كـ «شرطي» ومطاردة الحرامي"
                            else "🏎️ بدء الحدث كـ «حرامي» والهروب بالمسروقات",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            onEndEventEarly()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("end_police_event_button")
                    ) {
                        Text(
                            text = "🛑 إنهاء الحدث وسحب سيارات الشرطة والحرامي",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
