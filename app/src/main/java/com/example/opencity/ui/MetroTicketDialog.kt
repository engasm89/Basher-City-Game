package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsTransit
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
import com.example.opencity.model.MetroStation
import com.example.opencity.model.SkyMetroSystem
import java.text.NumberFormat
import java.util.Locale

/**
 * شباك تذاكر مترو المدينة الكهربائي المعلق («تذكرة 5 جنيه») ودليل المحطات المعلقة.
 */
@Composable
fun MetroTicketDialog(
    playerMoney: Int,
    metroTicketsCount: Int,
    nearbyStation: MetroStation?,
    onBuyTickets: (count: Int, totalCost: Int) -> Unit,
    onUnlockTurnstileNow: (MetroStation) -> Unit,
    onGoToStation: (MetroStation) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 820.dp)
                .fillMaxHeight(0.86f)
                .padding(8.dp)
                .testTag("metro_ticket_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFA091224),
            border = BorderStroke(1.5.dp, Color(0xFF00E5FF))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF06B6D4))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsTransit,
                                contentDescription = "المترو الكهربائي المعلق",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "مترو المدينة الكهربائي المعلق ⚡🚇",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "قطار كهربائي في الهواء على ارتفاع 10.5م + درج صعود وبوابات تذاكر 5 جنيه",
                                color = Color(0xFF67E8F9),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_metro_dialog_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = Color.White)
                    }
                }

                // Wallet & Owned Metro Tickets Summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10203A)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("رصيدك الحالي", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(
                                text = "${NumberFormat.getNumberInstance(Locale.US).format(playerMoney)} جنيه / $",
                                color = Color(0xFF4ADE80),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                        Surface(
                            color = Color(0x33FACC15),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFFACC15))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = "تذاكر المترو",
                                    tint = Color(0xFFFACC15),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "تذاكرك: $metroTicketsCount 🎫",
                                    color = Color(0xFFFDE047),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // If player is right next to a station turnstile gate, provide immediate unlock CTA
                if (nearbyStation != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE064E3B)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF34D399))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "📍 أنت الآن أمام بوابة: ${nearbyStation.arabicName}",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "ادفع 5 جنيه (أو استخدم تذكرة معك) لفتح البوابة وصعود الدرج إلى القطار!",
                                color = Color(0xFFA7F3D0),
                                fontSize = 11.sp
                            )
                            Button(
                                onClick = { onUnlockTurnstileNow(nearbyStation) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("dialog_unlock_turnstile_button")
                            ) {
                                Text(
                                    text = if (metroTicketsCount > 0) {
                                        "🎫 استخدم تذكرة وافتح البوابة الآن!"
                                    } else {
                                        "💳 ادفع 5 جنيه وافتح بوابة الدرج الآن!"
                                    },
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Ticket Purchase Cards (5 EGP Single & 25 EGP 5-Ticket Pack)
                Text(
                    text = "🎟️ شراء تذاكر المترو (سعر الدخول 5 جنيه)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF06B6D4))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎫 تذكرة رحلة واحدة", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            Text("تفتح بوابة الدرج للصعود للمحطة المعلقة وركوب القطار الكهربائي.", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Button(
                                onClick = { onBuyTickets(1, SkyMetroSystem.TICKET_PRICE_EGP) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("buy_single_metro_ticket_button")
                            ) {
                                Text("شراء بـ 5 جنيه", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFACC15))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎟️ باقة 5 تذاكر", color = Color(0xFFFDE047), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            Text("وفر وقتك واحتفظ بـ 5 تذاكر جاهزة لجميع محطات المترو بالمدينة.", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Button(
                                onClick = { onBuyTickets(5, SkyMetroSystem.TICKET_PRICE_EGP * 5) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("buy_pack_metro_ticket_button")
                            ) {
                                Text("شراء بـ 25 جنيه", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                if (playerMoney < SkyMetroSystem.TICKET_PRICE_EGP && metroTicketsCount == 0) {
                    Text(
                        text = "💡 تلميح: رصيدك أقل من 5 جنيه — اركب أي سيارة في المدينة وقدها ثوانٍ لتكسب أرباحاً وتشتري تذكرة المترو!",
                        color = Color(0xFFFBBF24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Elevated Station Directory & Instant Teleport to Turnstile Steps
                Text(
                    text = "🪜 محطات المترو المعلقة (4 أبراج بعيدة في المساحات المفتوحة)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                for (st in SkyMetroSystem.STATIONS) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🚉 ${st.arabicName}",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = st.subtitleArabic,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    onGoToStation(st)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("goto_station_${st.id}")
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("الذهاب للمحطة", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
