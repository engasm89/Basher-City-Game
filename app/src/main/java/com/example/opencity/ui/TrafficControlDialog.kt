package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Traffic
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.opencity.model.IntersectionPhase
import com.example.opencity.model.TrafficSystem
import kotlinx.coroutines.delay

@Composable
fun TrafficControlDialog(
    trafficSystem: TrafficSystem,
    isControllingRobot: Boolean,
    onToggleControlRobot: () -> Unit,
    onDriveRobotCar: () -> Unit,
    onDismiss: () -> Unit,
    onActionFeedback: (String) -> Unit
) {
    var currentPhase by remember { mutableStateOf(trafficSystem.intersections.firstOrNull()?.phase ?: IntersectionPhase.NS_GREEN) }
    var remainingSec by remember { mutableIntStateOf(trafficSystem.intersections.firstOrNull()?.getPhaseRemainingSeconds() ?: 100) }

    LaunchedEffect(Unit) {
        while (true) {
            val inter = trafficSystem.intersections.firstOrNull()
            if (inter != null) {
                currentPhase = inter.phase
                remainingSec = inter.getPhaseRemainingSeconds()
            }
            delay(300L)
        }
    }

    val phaseBadgeText = when (currentPhase) {
        IntersectionPhase.NS_GREEN -> "🟢 شمال/جنوب أخضر ($remainingSec ث) • 🔴 شرق/غرب أحمر"
        IntersectionPhase.NS_YELLOW -> "🟡 شمال/جنوب أصفر ($remainingSec ث) • 🔴 شرق/غرب أحمر"
        IntersectionPhase.EW_GREEN -> "🟢 شرق/غرب أخضر ($remainingSec ث) • 🔴 شمال/جنوب أحمر"
        IntersectionPhase.EW_YELLOW -> "🟡 شرق/غرب أصفر ($remainingSec ث) • 🔴 شمال/جنوب أحمر"
        IntersectionPhase.ALL_STOP -> "🔴 إيقاف كامل لجميع الشوارع"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 780.dp)
                .heightIn(max = 700.dp)
                .padding(12.dp)
                .testTag("traffic_control_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.5.dp, Color(0xFF38BDF8))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
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
                                .background(Color(0xFF2563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Traffic, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "غرفة التحكم بالمرور والروبوتات 🚦🤖",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "🟢 أخضر 100 ثانية | 🟡 أصفر 10 ثواني | 🔴 أحمر للتوقف",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFACC15),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("close_traffic_control_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Live Timer Banner
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.5.dp,
                        when (currentPhase) {
                            IntersectionPhase.NS_GREEN, IntersectionPhase.EW_GREEN -> Color(0xFF22C55E)
                            IntersectionPhase.NS_YELLOW, IntersectionPhase.EW_YELLOW -> Color(0xFFFACC15)
                            IntersectionPhase.ALL_STOP -> Color(0xFFEF4444)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Text(
                            text = "حالة الإشارات الآن بالوقت الحقيقي:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = phaseBadgeText,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            modifier = Modifier.testTag("live_traffic_phase_text")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 1: Traffic Lights Override
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "التحكم بإشارات التقاطعات (${trafficSystem.intersections.size} تقاطع):",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    trafficSystem.setAllNsGreen()
                                    onActionFeedback("تم فتح الإشارة الخضراء (100 ثانية) شمال/جنوب 🟢")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("signal_ns_green_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🟢 شمال/جنوب", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    trafficSystem.setAllEwGreen()
                                    onActionFeedback("تم فتح الإشارة الخضراء (100 ثانية) شرق/غرب 🟢")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("signal_ew_green_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🟢 شرق/غرب", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    trafficSystem.setAllYellowWarning()
                                    onActionFeedback("🟡 إشارة صفراء (10 ثواني) للاستعداد قبل الأحمر!")
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("signal_yellow_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCA8A04)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🟡 أصفر (10 ثواني)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            Button(
                                onClick = {
                                    trafficSystem.toggleEmergencyAllStop()
                                    onActionFeedback(
                                        if (trafficSystem.allStopActive) "تم إيقاف كل حركة المرور (إشارات حمراء) 🔴"
                                        else "تم استئناف حركة المرور 🟢"
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("signal_all_stop_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (trafficSystem.allStopActive) Color(0xFFDC2626) else Color(0xFFB91C1C)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (trafficSystem.allStopActive) "إلغاء الأحمر" else "🔴 الكل أحمر",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                trafficSystem.setAutoCycle()
                                onActionFeedback("🔄 دورة الإشارات التلقائية مفعّلة: 100ث أخضر ◄ 10ث أصفر ◄ أحمر")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("signal_auto_cycle_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🔄 تشغيل الدورة التلقائية (100ث أخضر / 10ث أصفر / أحمر)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Direct Robot Control (Ground/Grass + Car Steering Left/Right)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF22D3EE)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🤖 التحكم المباشر بالروبوت (على العشب الأخضر والسيارة):",
                            color = Color(0xFF22D3EE),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تحكّم بالروبوت يمشي بحرية على الأرض الخضراء أو يقود السيارة ويلف يميناً وشمالاً!",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onToggleControlRobot()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("dialog_toggle_robot_control_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isControllingRobot) Color(0xFF0D9488) else Color(0xFF0284C7)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isControllingRobot) "🧑 العودة للاعب" else "🤖 تحكم بالروبوت (الأرض)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Button(
                                onClick = {
                                    onDriveRobotCar()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("dialog_drive_robot_car_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("🚗 روبوت بالسيارة (قدام/ورا/يمين/شمال)", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 3: Autonomous Robots, Grass Robots & Cars Status
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "حالة شوارع المدينة والروبوتات الآن:",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🌿🤖 روبوتات تمشي بحرية على الأرض الخضراء: ${trafficSystem.grassRobots.size} روبوت",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "🤖🚗 روبوتات تقود السيارات وتنعطف يميناً وشمالاً: ${trafficSystem.autonomousRobots.size + trafficSystem.trafficCars.size} سيارة",
                            color = Color(0xFF22D3EE),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "🌿🚶 المتنزهون في الحدائق الخضراء (لا يقطعون الشارع): ${trafficSystem.pedestrians.size} مواطن",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        trafficSystem.autonomousRobots.forEach { robot ->
                            val badgeColor = if (robot.isInCar) Color(0xFF38BDF8) else Color(0xFF4ADE80)
                            Text(
                                text = "• ${robot.arabicName}: ${robot.currentStatusArabic}",
                                color = badgeColor,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("تم وإغلاق التحكم", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
