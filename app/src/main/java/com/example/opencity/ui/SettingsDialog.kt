package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.opencity.engine.Weather

@Composable
fun SettingsDialog(
    isSoundEnabled: Boolean,
    onToggleSound: (Boolean) -> Unit,
    soundVolume: Float,
    onChangeVolume: (Float) -> Unit,
    currentTimeHours: Float,
    onSetTime: (Float) -> Unit,
    isNightLocked: Boolean = true,
    onToggleNightLock: (Boolean) -> Unit = {},
    currentWeather: Weather,
    onSetWeather: (Weather) -> Unit,
    graphicsQuality: String,
    onSetGraphics: (String) -> Unit,
    onResetProgress: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 760.dp)
                .heightIn(max = 700.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F172A)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الإعدادات والتحكم ⚙️",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Audio Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("المؤثرات الصوتية (المحرك، الأبواق، الخطى)", color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                            Switch(
                                checked = isSoundEnabled,
                                onCheckedChange = onToggleSound,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        if (isSoundEnabled) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("مستوى الصوت:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Slider(
                                value = soundVolume,
                                onValueChange = onChangeVolume,
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF38BDF8),
                                    activeTrackColor = Color(0xFF38BDF8)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Day & Night Cycle
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("وقت اليوم ودورة الليل والنهار:", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🌙 تثبيت الليل الدائم (مع النجوم والقمر وإنارة المدينة)",
                                color = Color(0xFFA5B4FC),
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = isNightLocked,
                                onCheckedChange = onToggleNightLock,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF818CF8))
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onSetTime(8.0f) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isNightLocked && currentTimeHours in 6f..10f) Color(0xFF0284C7) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🌅 صباح", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetTime(13.0f) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isNightLocked && currentTimeHours in 11f..16f) Color(0xFF0284C7) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("☀️ ظهيرة", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetTime(18.5f) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!isNightLocked && currentTimeHours in 17f..19.5f) Color(0xFFD97706) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🌇 غروب", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetTime(22.5f) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNightLocked || currentTimeHours > 19.5f || currentTimeHours < 6f) Color(0xFF7C3AED) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🌙 ليل", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Weather Conditions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("الطقس والمناخ:", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onSetWeather(Weather.CLEAR) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentWeather == Weather.CLEAR) Color(0xFF0284C7) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("☀️ صافٍ", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetWeather(Weather.RAINY) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentWeather == Weather.RAINY) Color(0xFF0284C7) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🌧️ ممطر", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetWeather(Weather.FOGGY) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentWeather == Weather.FOGGY) Color(0xFF0284C7) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🌫️ ضبابي", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3.5. Desktop Performance & Graphics Quality
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("أداء الرسوميات والسلاسة (مُحسّن للمتصفح والكمبيوتر):", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onSetGraphics("LOW") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("graphics_quality_low"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (graphicsQuality == "LOW") Color(0xFF16A34A) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🚀 فائق السلاسة", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetGraphics("MEDIUM") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("graphics_quality_medium"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (graphicsQuality == "MEDIUM") Color(0xFF0284C7) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("⚡ متوازن", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { onSetGraphics("HIGH") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("graphics_quality_high"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (graphicsQuality == "HIGH") Color(0xFF7C3AED) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("✨ دقة كاملة", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Instructions / How to play
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("دليل التحكم والاستكشاف (نسخة الكمبيوتر 🖥️):", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("⌨️ W / A / S / D أو الأسهم: المشي أو قيادة السيارة في كل الاتجاهات.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("🖱️ سحب الماوس (أو U/O/I/K): تدوير وتحريك زاوية الكاميرا 360 درجة.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("🔍 عجلة الماوس (أو + / -): تقريب وإبعاد زوم الكاميرا.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("␣ Space: القفز على الأقدام أو الفرامل اليدوية أثناء القيادة.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("⇧ Shift: الجري السريع على الأقدام أو تشغيل نايترو التيربو بالسيارة.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("🔑 E أو Enter: ركوب/نزول السيارة، دخول/خروج المباني، ركوب المترو وفتح البوابات.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("✈️ Q / Z (أو PageUp/PageDown): صعود وهبوط الطائرة في الجو.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        Text("🎛️ H: بوري السيارة • F: بنزين/فلاتر • B: كراج تحت الأرض • R: توسيط • V: منظور الكاميرا • M: الخريطة • Tab: المهام • Esc: الإعدادات.", color = Color(0xFFCBD5E1), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reset Progress
                Button(
                    onClick = { onResetProgress(); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
                ) {
                    Text("إعادة تعيين التقدم والبدء من جديد", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
