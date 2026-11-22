package com.example.opencity.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Train
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
import com.example.opencity.model.Vector3

data class TransitRoute(
    val id: String,
    val titleArabic: String,
    val vehicleType: String, // BUS, TRAIN, FLIGHT
    val platform: String,
    val destinationArabic: String,
    val departureTime: String,
    val ticketPrice: Int,
    val destinationPos: Vector3
)

@Composable
fun TravelTerminalDialog(
    playerMoney: Int,
    onBuyTicketAndTravel: (route: TransitRoute) -> Unit,
    onDismiss: () -> Unit
) {
    val routes = listOf(
        TransitRoute(
            id = "bus_suburbs",
            titleArabic = "حافلة إكسبريس الضواحي والريف",
            vehicleType = "BUS",
            platform = "رصيف 1",
            destinationArabic = "مزارع وطواحين الريف",
            departureTime = "14:45",
            ticketPrice = 15,
            destinationPos = Vector3(95f, 0f, 128f)
        ),
        TransitRoute(
            id = "bus_downtown",
            titleArabic = "حافلة مترو وسط المدينة السريعة",
            vehicleType = "BUS",
            platform = "رصيف 2",
            destinationArabic = "ساحة ناطحات السحاب وبنك المدينة",
            departureTime = "15:00",
            ticketPrice = 20,
            destinationPos = Vector3(0f, 0f, 15f)
        ),
        TransitRoute(
            id = "train_express",
            titleArabic = "قطار المدينة فائق السرعة",
            vehicleType = "TRAIN",
            platform = "مسار السكة 3",
            destinationArabic = "محطة القطارات الكبرى والميناء",
            departureTime = "15:15",
            ticketPrice = 30,
            destinationPos = Vector3(-135f, 0f, 30f)
        ),
        TransitRoute(
            id = "airport_shuttle",
            titleArabic = "مكوك المطار الدولي السريع",
            vehicleType = "FLIGHT",
            platform = "بوابة المغادرة 4",
            destinationArabic = "مطار المدينة وصالة الطيران الإقليمي",
            departureTime = "15:30",
            ticketPrice = 40,
            destinationPos = Vector3(296f, 0f, -110f)
        ),
        TransitRoute(
            id = "bus_residential",
            titleArabic = "حافلة الحي السكني والواحة",
            vehicleType = "BUS",
            platform = "رصيف 5",
            destinationArabic = "الحي السكني وفلل اللاعبين",
            departureTime = "15:45",
            ticketPrice = 10,
            destinationPos = Vector3(55f, 0f, 35f)
        )
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 820.dp)
                .fillMaxHeight(0.86f)
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
                            text = "محطة السفر والمواصلات المركزية 🚌",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "صالة التذاكر وجدول رحلات الحافلات والقطارات والمطار",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wallet Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "رصيدك الحالي:",
                            color = Color(0xFFCBD5E1),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$$playerMoney",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timetable Header
                Text(
                    text = "لوحة المغادرة ومواعيد الرحلات:",
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Departures List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(routes) { route ->
                        val canAfford = playerMoney >= route.ticketPrice
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
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
                                            .background(
                                                when (route.vehicleType) {
                                                    "TRAIN" -> Color(0xFF7C3AED)
                                                    "FLIGHT" -> Color(0xFF0284C7)
                                                    else -> Color(0xFF2563EB)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (route.vehicleType) {
                                                "TRAIN" -> Icons.Default.Train
                                                "FLIGHT" -> Icons.Default.Flight
                                                else -> Icons.Default.DirectionsBus
                                            },
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = route.titleArabic,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "الوجهة: ${route.destinationArabic} • ${route.platform}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = "موعد الإقلاع: ${route.departureTime}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFBBF24)
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onBuyTicketAndTravel(route) },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF16A34A),
                                        disabledContainerColor = Color(0xFF334155)
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("buy_ticket_${route.id}")
                                ) {
                                    Text(
                                        text = "حجز ($${route.ticketPrice})",
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
