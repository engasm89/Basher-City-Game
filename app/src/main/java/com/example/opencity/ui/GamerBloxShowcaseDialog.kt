package com.example.opencity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R

/**
 * High-fidelity interactive showcase dialog exactly mirroring the Gamer Blox R-1 poster:
 * - 3D Render Banner of Gamer Blox R-1 in Blox City
 * - 7 Core specifications and features
 * - Google/Roblox recommended specifications box
 * - Three visual insets: Rear nitro view, Cockpit interior view, and 4 Color swatches
 * - Direct "Drive Now" and "Test Nitro" actions
 */
@Composable
fun GamerBloxShowcaseDialog(
    currentColorIndex: Int,
    onSelectColor: (index: Int, color: FloatArray) -> Unit,
    onDriveNow: () -> Unit,
    onDismiss: () -> Unit
) {
    val colorOptions = listOf(
        Triple("أبيض كربوني (الأصلي)", Color(0xFFF1F5F9), floatArrayOf(0.96f, 0.96f, 0.98f, 1f)),
        Triple("أحمر ناري (Crimson)", Color(0xFFEF4444), floatArrayOf(0.92f, 0.15f, 0.15f, 1f)),
        Triple("أصفر بلوكس (Blox Gold)", Color(0xFFFACC15), floatArrayOf(0.98f, 0.82f, 0.08f, 1f)),
        Triple("بنفسجي سايبر (Cyber Violet)", Color(0xFFA855F7), floatArrayOf(0.68f, 0.20f, 0.92f, 1f))
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 980.dp)
                .fillMaxHeight(0.92f)
                .padding(horizontal = 14.dp, vertical = 14.dp)
                .testTag("gamer_blox_showcase_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFA070B14),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Top Header Bar (ROBLOX badge & Gamer Blox R-1 title)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0B132B), Color(0xFF1C2541), Color(0xFF0284C7))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color.Black,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                        ) {
                            Text(
                                text = "ROBLOX",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                letterSpacing = 2.sp
                            )
                        }
                        Column {
                            Text(
                                text = "السيارة: Gamer Blox R-1",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Roblox Edition • Super Sports Car",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    FilledIconButton(
                        onClick = onDismiss,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0x44FFFFFF)),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                // 2. High-Resolution Hero Banner of the Car in Blox City
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .background(Color(0xFF0F172A))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_gamer_blox_poster),
                        contentDescription = "Gamer Blox R-1 Roblox Car Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay glowing badges
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp),
                        color = Color(0xCC000000),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("⚡", fontSize = 13.sp)
                            Text(
                                text = "إضاءة نيون زرقاء + عجلات متوهجة",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        color = Color(0xCC000000),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Text(
                            text = "320 km/h • 0-100 في 3ث",
                            color = Color(0xFFFBBF24),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // 3. Main Content: Features & Specs
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Feature List Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE0B1329)),
                        shape = RoundedCornerShape(18.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "المميزات:",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )

                            val features = listOf(
                                Pair("⏱️", "سرعة عالية (حتى 320 كم/س داخل اللعبة)"),
                                Pair("⏩", "تسارع قوي (0 - 100 كم/س خلال 3 ثوانٍ تقريباً)"),
                                Pair("🕹️", "تحكم سلس (مناسبة للسباقات والاستكشاف)"),
                                Pair("🎨", "تخصيص كامل (تغيير اللون، العجلات، الإضاءة، الجناح)"),
                                Pair("💡", "إضاءة نيون (تحت السيارة وحول العجلات)"),
                                Pair("👥", "تتسع لـ 4 لاعبين (السائق + 3 ركاب)"),
                                Pair("🔊", "صوت محرك رياضي (مؤثرات صوتية واقعية داخل Roblox)")
                            )

                            features.forEach { (emoji, text) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0x3300E5FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 14.sp)
                                    }
                                    Text(
                                        text = text,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Three Visual Inset Details
                    Text(
                        text = "تفاصيل السيارة التفاعلية:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Inset 1: Rear View & Nitro
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xEE1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🔥 لهب نيترو أزرق", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("عادم مزدوج + جناح GT", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Text("أضواء فرامل LED حمراء", color = Color(0xFFEF4444), fontSize = 10.sp)
                            }
                        }

                        // Inset 2: Interior Cockpit
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xEE1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🕹️ مقصورة نيون", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("عدادات + شاشة لمس", color = Color(0xFF94A3B8), fontSize = 10.sp)
                                Text("مقود روبلوكس مربع", color = Color(0xFF38BDF8), fontSize = 10.sp)
                            }
                        }
                    }

                    // Inset 3: Multi-Color Selection Card (Interactive)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE0F172A)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🎨 خيارات ألوان متعددة (اضغط للتغيير الفوري):",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                colorOptions.forEachIndexed { idx, (name, previewColor, floatArray) ->
                                    val isSelected = currentColorIndex == idx
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { onSelectColor(idx, floatArray) }
                                            .testTag("color_swatch_$idx"),
                                        color = if (isSelected) Color(0x4400E5FF) else Color(0x221E293B),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF00E5FF) else Color(0x4464748B)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(previewColor)
                                                    .border(1.dp, Color.White, CircleShape)
                                            )
                                            Text(
                                                text = name.split(" ").first(),
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (isSelected) {
                                                Text("✓", color = Color(0xFF00E5FF), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Specifications Panel from Bottom Right of Image
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE0B1329)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "مواصفات مقترحة للإنشاء في (Google) Roblox:",
                                color = Color(0xFF38BDF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text("• النوع: سيارة رياضية (Sports Car)", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            Text("• الأسلوب: بلوكات (Low-Poly) Roblox", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            Text("• عدد المقاعد: 4 (السائق + 3 ركاب)", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            Text("• قابلة للتخصيص: نعم (ألوان، إضاءة، إكسسوارات)", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                            Text("• مناسبة للألعاب: السباقات، المدن المفتوحة، تقمص الأدوار (RP)", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        }
                    }

                    // Action Drive Button
                    Button(
                        onClick = {
                            onDriveNow()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("drive_gamer_blox_now_button")
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "🚀 ركوب وقيادة Gamer Blox R-1 الآن",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
