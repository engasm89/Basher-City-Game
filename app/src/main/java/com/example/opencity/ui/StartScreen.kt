package com.example.opencity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StartScreen(
    playerName: String = "أشرف",
    onEditName: () -> Unit = {},
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0A0E1A)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background visual banner
            Image(
                painter = painterResource(id = R.drawable.city_game_icon_1793874242096),
                contentDescription = "OpenCity 3D Desktop Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Vignette gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xD60A0E1A),
                                Color(0xAA0A0E1A),
                                Color(0xEB0A0E1A),
                                Color(0xFF0A0E1A)
                            )
                        )
                    )
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                val isWideDesktop = maxWidth >= 600.dp || maxWidth > maxHeight

                Column(
                    modifier = Modifier
                        .widthIn(max = 1120.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Desktop Edition Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x991E293B))
                            .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(20.dp))
                            .padding(horizontal = 18.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🖥️ إصدار أجهزة سطح المكتب • تحكم بالماوس ولوحة المفاتيح 3D",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (isWideDesktop) {
                        // Two-Pane Widescreen Desktop Layout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(28.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Pane: Title, Description, Feature Chips & Controls Summary
                            Column(
                                modifier = Modifier.weight(1.15f),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "OpenCity 3D",
                                    fontSize = 46.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "مدينة الأحلام ثلاثية الأبعاد — نسخة الكمبيوتر",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "استكشف أحياء المدينة، وسط ناطحات السحاب، والحي السكني، ومحطة السفر والمترو المعلق، وقُد سياراتك وطائراتك بالماوس ولوحة المفاتيح!",
                                    fontSize = 14.sp,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 22.sp
                                )

                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FeatureChip("⌨️ WASD / الأسهم")
                                    FeatureChip("🖱️ سحب وعجلة الماوس")
                                    FeatureChip("🚗 قيادة وطيران")
                                    FeatureChip("🚪 استكشاف مباني")
                                    FeatureChip("🚇 مترو معلق")
                                }

                                DesktopQuickGuideCard()
                            }

                            // Right Pane: Player Card & Launch Actions
                            Column(
                                modifier = Modifier.weight(0.85f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StartActionControls(
                                    playerName = playerName,
                                    onEditName = onEditName,
                                    onStartGame = onStartGame,
                                    onOpenSettings = onOpenSettings,
                                    onOpenHelp = onOpenHelp
                                )
                            }
                        }
                    } else {
                        // Compact Window Fallback (still desktop-focused, centered)
                        Column(
                            modifier = Modifier.widthIn(max = 560.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "OpenCity 3D",
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "مدينة الأحلام ثلاثية الأبعاد — نسخة الكمبيوتر",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "استكشف أحياء المدينة، وقُد سياراتك وادخل المباني عبر لوحة المفاتيح والماوس!",
                                fontSize = 13.sp,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FeatureChip("⌨️ WASD / الأسهم")
                                FeatureChip("🖱️ الماوس: كاميرا وزوم")
                                FeatureChip("🚗 قيادة سيارات")
                                FeatureChip("🚪 استكشاف بيوت")
                            }

                            DesktopQuickGuideCard()

                            StartActionControls(
                                playerName = playerName,
                                onEditName = onEditName,
                                onStartGame = onStartGame,
                                onOpenSettings = onOpenSettings,
                                onOpenHelp = onOpenHelp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopQuickGuideCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xCC0F172A)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "🎮 اختصارات سطح المكتب:",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• W / A / S / D أو الأسهم: المشي والقيادة  •  Space: قفز / فرامل  •  Shift: جري / نايترو",
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp
            )
            Text(
                text = "• سحب الماوس: تحريك الكاميرا  •  عجلة الماوس (+ / -): زوم  •  E / Enter: ركوب ودخول",
                color = Color(0xFFE2E8F0),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun StartActionControls(
    playerName: String,
    onEditName: () -> Unit,
    onStartGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHelp: () -> Unit
) {
    // Character Customization & Name Card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onEditName() },
        colors = CardDefaults.cardColors(containerColor = Color(0xCC1E293B)),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.img_character_ambition),
                    contentDescription = "Character Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFFFFD600), CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "اسم شخصيتك: $playerName",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD600),
                            fontSize = 15.sp
                        )
                        Surface(
                            color = Color(0x3310B981),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "المواصفات الأصلية",
                                color = Color(0xFF34D399),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "انقر لعرض ملصق المواصفات وكتابة اسمك ✏️",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }
            Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
        }
    }

    Button(
        onClick = onStartGame,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("start_game_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "بدء اللعب والاستكشاف الآن",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("start_settings_button"),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("الإعدادات", fontSize = 14.sp)
        }

        OutlinedButton(
            onClick = onOpenHelp,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("start_help_button"),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("دليل التحكم", fontSize = 14.sp)
        }
    }
}

@Composable
private fun FeatureChip(text: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xAA1E293B)),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Text(
            text = text,
            color = Color(0xFFF1F5F9),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
