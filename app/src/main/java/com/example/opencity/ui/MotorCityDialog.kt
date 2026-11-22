package com.example.opencity.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
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
import com.example.opencity.model.CarTier
import com.example.opencity.model.DealershipCar
import com.example.opencity.model.MotorCityData
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MotorCityDialog(
    initialCarId: String? = null,
    playerMoney: Int,
    ownedCarIds: Set<String>,
    onBuyCar: (DealershipCar) -> Unit,
    onTestDrive: (DealershipCar) -> Unit,
    onDriveOwnedCar: (DealershipCar) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTier by remember { mutableStateOf<CarTier?>(null) }
    var selectedCar by remember {
        mutableStateOf(
            if (initialCarId != null) MotorCityData.getCarById(initialCarId) ?: MotorCityData.CATALOG.first()
            else MotorCityData.CATALOG.first()
        )
    }
    var activeTab by remember { mutableStateOf(0) } // 0: Showroom Market, 1: My Garage

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 980.dp)
                .fillMaxHeight(0.90f)
                .padding(12.dp)
                .testTag("motor_city_dealership_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFA0B0F19),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF97316))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
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
                                .background(Brush.linearGradient(listOf(Color(0xFFF97316), Color(0xFFEA580C)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏎️", fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = "سوق سيارات «مدينة المحركات»",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Motor City Auto Dealership & Test Track",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Player Money Badge & Close Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("💵", fontSize = 14.sp)
                                Text(
                                    text = "${numberFormat.format(playerMoney)}$",
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        // Prominent Red "X" Exit Button (إكس للخروج من مكان شراء السيارات)
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFCA5A5)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier
                                .testTag("close_motor_city_button")
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "خروج (X)",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: المعرض (Showroom) / مرآبي (My Garage)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabPill(
                        title = "🚗 معرض السيارات والأسعار",
                        isSelected = activeTab == 0,
                        modifier = Modifier.weight(1f),
                        onClick = { activeTab = 0 }
                    )
                    TabPill(
                        title = "🏁 مرآب سياراتي (${ownedCarIds.size})",
                        isSelected = activeTab == 1,
                        modifier = Modifier.weight(1f),
                        onClick = { activeTab = 1 }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (activeTab == 0) {
                    // Tier Filter Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChipPill(
                                label = "جميع الفئات (${MotorCityData.CATALOG.size})",
                                isSelected = selectedTier == null,
                                onClick = { selectedTier = null }
                            )
                        }
                        items(CarTier.values()) { tier ->
                            FilterChipPill(
                                label = "${tier.badgeEmoji} ${tier.arabicName}",
                                sublabel = tier.priceDescription,
                                isSelected = selectedTier == tier,
                                onClick = { selectedTier = tier }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Main Display: Left side list of cars, Right side inspection & test drive panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Left: Car Catalog List
                        val filteredCars = remember(selectedTier) {
                            if (selectedTier == null) MotorCityData.CATALOG
                            else MotorCityData.CATALOG.filter { it.tier == selectedTier }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(0.48f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCars) { car ->
                                val isOwned = ownedCarIds.contains(car.id)
                                val isSelected = selectedCar.id == car.id

                                DealershipCarCard(
                                    car = car,
                                    isOwned = isOwned,
                                    isSelected = isSelected,
                                    formattedPrice = numberFormat.format(car.price),
                                    onClick = { selectedCar = car }
                                )
                            }
                        }

                        // Right: Detailed Car Inspection & Action Panel
                        CarInspectionDetailPane(
                            car = selectedCar,
                            isOwned = ownedCarIds.contains(selectedCar.id),
                            canAfford = playerMoney >= selectedCar.price,
                            formattedPrice = numberFormat.format(selectedCar.price),
                            onBuy = { onBuyCar(selectedCar) },
                            onTestDrive = { onTestDrive(selectedCar) },
                            onDrive = { onDriveOwnedCar(selectedCar) },
                            onDismiss = onDismiss,
                            modifier = Modifier
                                .weight(0.52f)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    // Tab 1: My Garage ("مرآبي")
                    val ownedCars = remember(ownedCarIds) {
                        MotorCityData.CATALOG.filter { ownedCarIds.contains(it.id) }
                    }

                    if (ownedCars.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لا تملك أي سيارات بعد! تصفح المعرض واشترِ سيارتك الأولى بدءاً من 200$",
                                color = Color(0xFF94A3B8),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.48f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(ownedCars) { car ->
                                    DealershipCarCard(
                                        car = car,
                                        isOwned = true,
                                        isSelected = selectedCar.id == car.id,
                                        formattedPrice = numberFormat.format(car.price),
                                        onClick = { selectedCar = car }
                                    )
                                }
                            }

                            CarInspectionDetailPane(
                                car = selectedCar,
                                isOwned = true,
                                canAfford = true,
                                formattedPrice = numberFormat.format(selectedCar.price),
                                onBuy = {},
                                onTestDrive = { onTestDrive(selectedCar) },
                                onDrive = { onDriveOwnedCar(selectedCar) },
                                onDismiss = onDismiss,
                                modifier = Modifier
                                    .weight(0.52f)
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabPill(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFF97316) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFF94A3B8),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    sublabel: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color(0xFFEA580C) else Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFF97316) else Color(0xFF334155)
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (sublabel != null) {
                Text(
                    text = sublabel,
                    color = if (isSelected) Color(0xFFFED7AA) else Color(0xFF94A3B8),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun DealershipCarCard(
    car: DealershipCar,
    isOwned: Boolean,
    isSelected: Boolean,
    formattedPrice: String,
    onClick: () -> Unit
) {
    val tierColor = when (car.tier) {
        CarTier.WEAK_OLD -> Color(0xFF94A3B8)
        CarTier.MID_RANGE -> Color(0xFF38BDF8)
        CarTier.RARE -> Color(0xFFF87171)
        CarTier.EXCLUSIVE -> Color(0xFF2DD4BF)
        CarTier.SUPER -> Color(0xFFFACC15)
        CarTier.ULTIMATE_RARE -> Color(0xFFC084FC)
    }

    Surface(
        onClick = onClick,
        color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) Color(0xFFF97316) else Color(0xFF334155)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tier Badge Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tierColor.copy(alpha = 0.2f))
                        .border(1.dp, tierColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(car.tier.badgeEmoji, fontSize = 20.sp)
                }

                Column {
                    Text(
                        text = car.nameArabic,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = car.nameEnglish,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "${car.topSpeedKmH.toInt()} كم/س • ${car.horsePower} حصان",
                        color = tierColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (car.id == "car_new_phantom_x8") {
                    Surface(
                        color = Color(0x330EA5E9),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                        modifier = Modifier.padding(bottom = 3.dp)
                    ) {
                        Text(
                            text = "⭐ جديدة 8M",
                            color = Color(0xFF7DD3FC),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }
                }
                if (car.price == 0) {
                    Surface(
                        color = Color(0x3306B6D4),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22D3EE))
                    ) {
                        Text(
                            text = "مجانية 0$ 🤖",
                            color = Color(0xFF67E8F9),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (isOwned) {
                    Surface(
                        color = Color(0x3322C55E),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22C55E))
                    ) {
                        Text(
                            text = "مملوكة ✅",
                            color = Color(0xFF4ADE80),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = Color(0x33F97316),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF97316))
                    ) {
                        Text(
                            text = "$formattedPrice$",
                            color = Color(0xFFFDBA74),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "للشراء والقيادة",
                        color = Color(0xFF94A3B8),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CarInspectionDetailPane(
    car: DealershipCar,
    isOwned: Boolean,
    canAfford: Boolean,
    formattedPrice: String,
    onBuy: () -> Unit,
    onTestDrive: () -> Unit,
    onDrive: () -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tierColor = when (car.tier) {
        CarTier.WEAK_OLD -> Color(0xFF94A3B8)
        CarTier.MID_RANGE -> Color(0xFF38BDF8)
        CarTier.RARE -> Color(0xFFF87171)
        CarTier.EXCLUSIVE -> Color(0xFF2DD4BF)
        CarTier.SUPER -> Color(0xFFFACC15)
        CarTier.ULTIMATE_RARE -> Color(0xFFC084FC)
    }

    Surface(
        color = Color(0xFF0F172A),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Header with Tier and Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = tierColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tierColor)
                    ) {
                        Text(
                            text = "${car.tier.badgeEmoji} ${car.tier.arabicName}",
                            color = tierColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = when {
                                car.price == 0 -> "مجانية 0$ 🤖"
                                isOwned -> "مملوكة ✅ ($formattedPrice$)"
                                else -> "$formattedPrice$"
                            },
                            color = if (isOwned || car.price == 0) Color(0xFF4ADE80) else Color(0xFFFACC15),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (car.price == 0) "هدية البداية المجانية" else "السعر الرسمي",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = car.nameArabic,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${car.nameEnglish} • ${car.engineType}",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Divider(color = Color(0xFF334155), thickness = 1.dp)

                // Technical Performance Gauges (Speed, Acceleration, Braking)
                Text(
                    text = "📊 مقاييس الأداء الفني والتحكم:",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // 1. Top Speed
                StatBar(
                    label = "⚡ السرعة القصوى",
                    valueText = "${car.topSpeedKmH.toInt()} كم/س",
                    percentage = car.speedScore / 100f,
                    barColor = Color(0xFFF97316)
                )

                // 2. Acceleration
                StatBar(
                    label = "🚀 قوة التسارع",
                    valueText = "${car.accelScore}/100",
                    percentage = car.accelScore / 100f,
                    barColor = Color(0xFF38BDF8)
                )

                // 3. Braking Power
                StatBar(
                    label = "🛑 قوة الفرامل والثبات",
                    valueText = "${car.brakeScore}/100",
                    percentage = car.brakeScore / 100f,
                    barColor = Color(0xFF4ADE80)
                )

                // Description
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = car.descriptionArabic,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Action Buttons at bottom
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Test Drive Button (Available for all cars so player can drive right away!)
                Button(
                    onClick = onTestDrive,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("test_drive_car_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Test Drive")
                        Text(
                            text = "🏎️ تجربة قيادة الآن (سوقها في المسار)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                if (isOwned || car.price == 0) {
                    // Drive Owned / Free Car Directly in City
                    Button(
                        onClick = onDrive,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("drive_car_direct_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Drive", tint = Color.White)
                            Text(
                                text = "🚗 قيادة السيارة الآن في المدينة (سوقها)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Purchase Car with In-Game Money (e.g. 8,000,000$)
                    Button(
                        onClick = onBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF97316),
                            disabledContainerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("buy_car_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Buy", tint = Color.White)
                            Text(
                                text = if (canAfford) {
                                    "💰 شراء السيارة بـ $formattedPrice$"
                                } else {
                                    "🔒 تحتاج $formattedPrice$ للشراء (قُد سيارة لكسب المال)"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Dedicated Bottom Exit / Leave Button (خروج من مكان شراء السيارات)
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("exit_motor_city_dialog_bottom_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("❌", fontSize = 16.sp)
                        Text(
                            text = "خروج من مكان شراء السيارات والعودة لشوارع المدينة (X)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBar(
    label: String,
    valueText: String,
    percentage: Float,
    barColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
            Text(text = valueText, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF1E293B))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage.coerceIn(0.05f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(barColor)
            )
        }
    }
}
