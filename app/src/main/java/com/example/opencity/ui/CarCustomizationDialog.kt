package com.example.opencity.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.opencity.model.CarMeshStyle
import com.example.opencity.model.Vehicle
import com.example.opencity.model.VehicleType
import kotlin.math.roundToInt

private data class ColorSwatchOption(
    val label: String,
    val rgba: FloatArray
) {
    val composeColor: Color
        get() = Color(
            red = rgba[0].coerceIn(0f, 1f),
            green = rgba[1].coerceIn(0f, 1f),
            blue = rgba[2].coerceIn(0f, 1f),
            alpha = 1f
        )
}

private data class BodyKitOption(
    val style: CarMeshStyle,
    val arabicLabel: String,
    val badge: String
)

private val PRIMARY_PAINT_SWATCHES = listOf(
    ColorSwatchOption("أبيض لؤلؤي", floatArrayOf(0.96f, 0.97f, 0.99f, 1f)),
    ColorSwatchOption("أحمر ناري", floatArrayOf(0.92f, 0.14f, 0.16f, 1f)),
    ColorSwatchOption("أزرق سايبر", floatArrayOf(0.10f, 0.56f, 0.98f, 1f)),
    ColorSwatchOption("أصفر سباقي", floatArrayOf(0.98f, 0.80f, 0.08f, 1f)),
    ColorSwatchOption("أسود مطفي", floatArrayOf(0.11f, 0.12f, 0.15f, 1f)),
    ColorSwatchOption("بنفسجي نيون", floatArrayOf(0.66f, 0.24f, 0.95f, 1f)),
    ColorSwatchOption("أخضر زمردي", floatArrayOf(0.08f, 0.78f, 0.42f, 1f)),
    ColorSwatchOption("برتقالي ماكلارين", floatArrayOf(0.98f, 0.44f, 0.08f, 1f)),
    ColorSwatchOption("وردي فاقع", floatArrayOf(0.95f, 0.24f, 0.62f, 1f)),
    ColorSwatchOption("تركواز لامع", floatArrayOf(0.04f, 0.84f, 0.88f, 1f)),
    ColorSwatchOption("فضي تيتانيوم", floatArrayOf(0.68f, 0.72f, 0.78f, 1f)),
    ColorSwatchOption("ذهبي ملكي", floatArrayOf(0.90f, 0.68f, 0.18f, 1f))
)

private val SECONDARY_TRIM_SWATCHES = listOf(
    ColorSwatchOption("كربون أسود", floatArrayOf(0.10f, 0.11f, 0.14f, 1f)),
    ColorSwatchOption("كروم أبيض", floatArrayOf(0.94f, 0.95f, 0.98f, 1f)),
    ColorSwatchOption("ذهبي", floatArrayOf(0.96f, 0.76f, 0.18f, 1f)),
    ColorSwatchOption("أحمر رياضي", floatArrayOf(0.90f, 0.15f, 0.15f, 1f)),
    ColorSwatchOption("أزرق كهربائي", floatArrayOf(0.12f, 0.74f, 0.98f, 1f)),
    ColorSwatchOption("بنفسجي", floatArrayOf(0.68f, 0.28f, 0.96f, 1f)),
    ColorSwatchOption("أخضر نيون", floatArrayOf(0.18f, 0.92f, 0.44f, 1f)),
    ColorSwatchOption("برتقالي", floatArrayOf(0.98f, 0.50f, 0.12f, 1f))
)

private val UNDERGLOW_SWATCHES = listOf(
    ColorSwatchOption("أزرق نيون", floatArrayOf(0.08f, 0.85f, 1.0f, 1f)),
    ColorSwatchOption("بنفسجي", floatArrayOf(0.72f, 0.24f, 1.0f, 1f)),
    ColorSwatchOption("أخضر سام", floatArrayOf(0.14f, 0.96f, 0.45f, 1f)),
    ColorSwatchOption("أحمر ليزر", floatArrayOf(1.0f, 0.16f, 0.24f, 1f)),
    ColorSwatchOption("ذهبي", floatArrayOf(1.0f, 0.80f, 0.14f, 1f)),
    ColorSwatchOption("وردي نيون", floatArrayOf(1.0f, 0.24f, 0.72f, 1f)),
    ColorSwatchOption("أبيض ثلجي", floatArrayOf(0.92f, 0.97f, 1.0f, 1f))
)

private val BODY_KIT_OPTIONS = listOf(
    BodyKitOption(CarMeshStyle.ROBLOX_GAMER_BLOX, "Gamer Blox LED", "⚡"),
    BodyKitOption(CarMeshStyle.SUPER_SPORT, "سوبر سبورت", "🏎️"),
    BodyKitOption(CarMeshStyle.HYPER_AERO, "هايبر أيرو", "🚀"),
    BodyKitOption(CarMeshStyle.ULTIMATE_STEALTH, "ستيلث شبح", "🛸"),
    BodyKitOption(CarMeshStyle.MUSCLE_GT, "موسل GT", "🔥"),
    BodyKitOption(CarMeshStyle.SUV_OFFROAD, "دفع رباعي 4x4", "🚙"),
    BodyKitOption(CarMeshStyle.SEDAN_MODERN, "سيدان عصرية", "🚘"),
    BodyKitOption(CarMeshStyle.VINTAGE_OLD, "كلاسيك قديمة", "🚗")
)

private fun floatColorClose(a: FloatArray?, b: FloatArray?): Boolean {
    if (a == null || b == null || a.size < 3 || b.size < 3) return false
    return kotlin.math.abs(a[0] - b[0]) < 0.06f &&
        kotlin.math.abs(a[1] - b[1]) < 0.06f &&
        kotlin.math.abs(a[2] - b[2]) < 0.06f
}

private fun FloatArray.toComposeColor(): Color {
    return Color(
        red = getOrElse(0) { 0.5f }.coerceIn(0f, 1f),
        green = getOrElse(1) { 0.5f }.coerceIn(0f, 1f),
        blue = getOrElse(2) { 0.5f }.coerceIn(0f, 1f),
        alpha = 1f
    )
}

@Composable
fun CarCustomizationDialog(
    vehicles: List<Vehicle>,
    currentCar: Vehicle?,
    ownedCarIds: Set<String> = emptySet(),
    onApplyCustomization: (Vehicle) -> Unit,
    onResetToStock: (Vehicle) -> Unit,
    onDriveVehicle: (Vehicle) -> Unit,
    onTeleportToWorkshop: () -> Unit,
    onDismiss: () -> Unit
) {
    val customizableCars = remember(vehicles, currentCar, ownedCarIds) {
        vehicles.filter { veh ->
            !veh.isShowroomDisplay &&
                veh.type != VehicleType.AIRPLANE &&
                (ownedCarIds.isEmpty() ||
                    ownedCarIds.contains(veh.dealershipCarId ?: veh.id) ||
                    currentCar?.id == veh.id)
        }.distinctBy { it.id }
    }

    val initialTarget = remember(currentCar, customizableCars) {
        if (currentCar != null && currentCar.type != VehicleType.AIRPLANE) {
            currentCar
        } else {
            customizableCars.firstOrNull { it.dealershipCarId == "car_robot_cyber_free" }
                ?: customizableCars.firstOrNull()
        }
    }

    var selectedCarId by remember(initialTarget) { mutableStateOf(initialTarget?.id ?: "") }
    val selectedVehicle = customizableCars.firstOrNull { it.id == selectedCarId } ?: initialTarget
    var mutationTick by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 980.dp)
                .fillMaxHeight(0.90f)
                .testTag("car_customization_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xF708101E),
            border = BorderStroke(1.8.dp, Color(0xFF00E5FF)),
            shadowElevation = 18.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0284C7), Color(0xFF7C3AED))
                                    )
                                )
                                .border(1.5.dp, Color(0xFF67E8F9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = "Car Workshop",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "🔧 ورشة تعديل وتطوير السيارة",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "عدّل لون الهيكل، النيون السفلي، الشكل الرياضي، والسرعة والتسارع فوراً على 3D",
                                color = Color(0xFF7DD3FC),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = {
                                onTeleportToWorkshop()
                                onDismiss()
                            },
                            color = Color(0xFF0F766E),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFF5EEAD4)),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("teleport_car_workshop_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Go to workshop in city",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "ورشة المدينة",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .testTag("close_car_workshop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Vehicle Selector Strip
                if (customizableCars.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        customizableCars.forEach { veh ->
                            val isSelected = selectedVehicle?.id == veh.id
                            val carLabel = veh.displayName ?: veh.type.arabicName
                            val isCurrent = currentCar?.id == veh.id
                            Surface(
                                onClick = { selectedCarId = veh.id },
                                color = if (isSelected) Color(0xFF0369A1) else Color(0xFF111C33),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF334155)
                                ),
                                modifier = Modifier
                                    .height(40.dp)
                                    .testTag("select_custom_car_${veh.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Read mutationTick so swatch dot updates live
                                    @Suppress("UNUSED_EXPRESSION")
                                    mutationTick
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(veh.colorPrimary.toComposeColor())
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                    Text(
                                        text = if (isCurrent) "$carLabel (الحالية)" else carLabel,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                if (selectedVehicle == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لا توجد سيارة متاحة للتعديل حالياً",
                            color = Color(0xFF94A3B8),
                            fontSize = 15.sp
                        )
                    }
                } else {
                    // Read mutationTick at top of content so live edits redraw preview & sliders
                    val tick = mutationTick

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        val twoColumn = maxWidth >= 660.dp
                        if (twoColumn) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CarWorkshopPreviewPane(
                                    vehicle = selectedVehicle,
                                    mutationTick = tick,
                                    isCurrentCar = currentCar?.id == selectedVehicle.id,
                                    onRefillFuelAndNitro = {
                                        selectedVehicle.fuel = 100f
                                        selectedVehicle.nitroFuel = 100f
                                        mutationTick++
                                        onApplyCustomization(selectedVehicle)
                                    },
                                    onResetToStock = {
                                        onResetToStock(selectedVehicle)
                                        mutationTick++
                                    },
                                    onDriveVehicle = {
                                        onDriveVehicle(selectedVehicle)
                                        onDismiss()
                                    },
                                    modifier = Modifier
                                        .weight(0.42f)
                                        .fillMaxHeight()
                                )

                                CarWorkshopControlsPane(
                                    vehicle = selectedVehicle,
                                    mutationTick = tick,
                                    onChanged = {
                                        mutationTick++
                                        onApplyCustomization(selectedVehicle)
                                    },
                                    modifier = Modifier
                                        .weight(0.58f)
                                        .fillMaxHeight()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CarWorkshopPreviewPane(
                                    vehicle = selectedVehicle,
                                    mutationTick = tick,
                                    isCurrentCar = currentCar?.id == selectedVehicle.id,
                                    onRefillFuelAndNitro = {
                                        selectedVehicle.fuel = 100f
                                        selectedVehicle.nitroFuel = 100f
                                        mutationTick++
                                        onApplyCustomization(selectedVehicle)
                                    },
                                    onResetToStock = {
                                        onResetToStock(selectedVehicle)
                                        mutationTick++
                                    },
                                    onDriveVehicle = {
                                        onDriveVehicle(selectedVehicle)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                CarWorkshopControlsPane(
                                    vehicle = selectedVehicle,
                                    mutationTick = tick,
                                    onChanged = {
                                        mutationTick++
                                        onApplyCustomization(selectedVehicle)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    scrollable = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarWorkshopPreviewPane(
    vehicle: Vehicle,
    mutationTick: Int,
    isCurrentCar: Boolean,
    onRefillFuelAndNitro: () -> Unit,
    onResetToStock: () -> Unit,
    onDriveVehicle: () -> Unit,
    modifier: Modifier = Modifier
) {
    @Suppress("UNUSED_EXPRESSION")
    mutationTick

    val primaryColor = vehicle.colorPrimary.toComposeColor()
    val secondaryColor = vehicle.colorSecondary.toComposeColor()
    val underglowColor = vehicle.underglowColor?.toComposeColor()
    val styleLabel = BODY_KIT_OPTIONS.firstOrNull { it.style == vehicle.meshStyle }?.arabicLabel
        ?: vehicle.type.arabicName

    Surface(
        modifier = modifier,
        color = Color(0xFF0D172B),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFF24344D))
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
                    text = vehicle.displayName ?: vehicle.type.arabicName,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Text(
                        text = styleLabel,
                        color = Color(0xFF7DD3FC),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Live Top-Down Stylized Preview Canvas with Underglow Halo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(168.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF172542), Color(0xFF060B16))
                        )
                    )
                    .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val carW = size.width * 0.56f
                    val carH = size.height * 0.48f

                    // Subtle workshop turntable circle
                    drawCircle(
                        color = Color(0x2238BDF8),
                        radius = size.minDimension * 0.44f,
                        center = Offset(cx, cy)
                    )

                    // Neon underglow aura if enabled
                    if (underglowColor != null) {
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    underglowColor.copy(alpha = 0.78f),
                                    underglowColor.copy(alpha = 0.25f),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = carW * 0.72f
                            ),
                            topLeft = Offset(cx - carW * 0.65f, cy - carH * 0.72f),
                            size = Size(carW * 1.30f, carH * 1.44f),
                            cornerRadius = CornerRadius(44f, 44f)
                        )
                    }

                    // 4 wheels
                    val wheelColor = Color(0xFF0F172A)
                    val wheelW = carW * 0.14f
                    val wheelH = carH * 0.22f
                    drawRoundRect(wheelColor, Offset(cx - carW * 0.34f, cy - carH * 0.56f), Size(wheelW, wheelH), CornerRadius(8f, 8f))
                    drawRoundRect(wheelColor, Offset(cx + carW * 0.20f, cy - carH * 0.56f), Size(wheelW, wheelH), CornerRadius(8f, 8f))
                    drawRoundRect(wheelColor, Offset(cx - carW * 0.34f, cy + carH * 0.34f), Size(wheelW, wheelH), CornerRadius(8f, 8f))
                    drawRoundRect(wheelColor, Offset(cx + carW * 0.20f, cy + carH * 0.34f), Size(wheelW, wheelH), CornerRadius(8f, 8f))

                    // Main chassis
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(cx - carW / 2f, cy - carH / 2f),
                        size = Size(carW, carH),
                        cornerRadius = CornerRadius(26f, 26f)
                    )

                    // Racing stripe / secondary trim
                    drawRoundRect(
                        color = secondaryColor,
                        topLeft = Offset(cx - carW * 0.34f, cy - carH * 0.14f),
                        size = Size(carW * 0.78f, carH * 0.28f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )

                    // Tinted aerodynamic cabin glass
                    drawRoundRect(
                        color = Color(0xDD1E293B),
                        topLeft = Offset(cx - carW * 0.22f, cy - carH * 0.36f),
                        size = Size(carW * 0.42f, carH * 0.72f),
                        cornerRadius = CornerRadius(14f, 14f)
                    )

                    // Rear Aero Spoiler for sporty styles
                    if (vehicle.meshStyle in listOf(
                            CarMeshStyle.SUPER_SPORT,
                            CarMeshStyle.HYPER_AERO,
                            CarMeshStyle.ULTIMATE_STEALTH,
                            CarMeshStyle.ROBLOX_GAMER_BLOX
                        )
                    ) {
                        drawRoundRect(
                            color = secondaryColor,
                            topLeft = Offset(cx - carW * 0.48f, cy - carH * 0.46f),
                            size = Size(carW * 0.09f, carH * 0.92f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }

                    // Headlights (Right side = front)
                    drawCircle(
                        color = Color(0xFFFFF9C4),
                        radius = 6.5f,
                        center = Offset(cx + carW * 0.46f, cy - carH * 0.30f)
                    )
                    drawCircle(
                        color = Color(0xFFFFF9C4),
                        radius = 6.5f,
                        center = Offset(cx + carW * 0.46f, cy + carH * 0.30f)
                    )

                    // Tail lights (Left side = rear)
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 5.5f,
                        center = Offset(cx - carW * 0.46f, cy - carH * 0.30f)
                    )
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 5.5f,
                        center = Offset(cx - carW * 0.46f, cy + carH * 0.30f)
                    )
                }
            }

            // Live Performance Telemetry Bars
            StatProgressRow(
                label = "السرعة القصوى",
                valueText = "${vehicle.effectiveMaxSpeedKmH.roundToInt()} km/h",
                progress = (vehicle.effectiveMaxSpeedKmH / 450f).coerceIn(0.05f, 1f),
                barColor = Color(0xFF38BDF8)
            )
            StatProgressRow(
                label = "التسارع",
                valueText = "${vehicle.effectiveAccelRate.roundToInt()}",
                progress = (vehicle.effectiveAccelRate / 100f).coerceIn(0.05f, 1f),
                barColor = Color(0xFFF59E0B)
            )
            StatProgressRow(
                label = "الفرامل والثبات",
                valueText = "${vehicle.effectiveBrakeRate.roundToInt()}",
                progress = (vehicle.effectiveBrakeRate / 120f).coerceIn(0.05f, 1f),
                barColor = Color(0xFF34D399)
            )
            StatProgressRow(
                label = "البنزين والنايترو",
                valueText = "⛽ ${vehicle.fuel.roundToInt()}%  ⚡ ${vehicle.nitroFuel.roundToInt()}%",
                progress = (vehicle.fuel / 100f).coerceIn(0f, 1f),
                barColor = Color(0xFFA855F7)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRefillFuelAndNitro,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("workshop_refill_button")
                ) {
                    Text("⛽⚡ تعبئة 100%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onResetToStock,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("workshop_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset car to stock",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("استعادة الأصل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = onDriveVehicle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentCar) Color(0xFF0284C7) else Color(0xFFF97316)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("workshop_drive_car_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Drive customized car",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCurrentCar) "متابعة القيادة بهذه التعديلات" else "اركب وقد هذه السيارة الآن",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun CarWorkshopControlsPane(
    vehicle: Vehicle,
    mutationTick: Int,
    onChanged: () -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true
) {
    @Suppress("UNUSED_EXPRESSION")
    mutationTick

    val columnModifier = if (scrollable) {
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0D172B))
            .border(1.dp, Color(0xFF24344D), RoundedCornerShape(18.dp))
            .verticalScroll(rememberScrollState())
            .padding(14.dp)
    } else {
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0D172B))
            .border(1.dp, Color(0xFF24344D), RoundedCornerShape(18.dp))
            .padding(14.dp)
    }

    Column(
        modifier = columnModifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Primary Paint
        SectionTitle(title = "🎨 لون طلاء السيارة الأساسي")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PRIMARY_PAINT_SWATCHES.forEachIndexed { idx, swatch ->
                val selected = floatColorClose(vehicle.colorPrimary, swatch.rgba)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            vehicle.colorPrimary = swatch.rgba.copyOf()
                            onChanged()
                        }
                        .padding(4.dp)
                        .testTag("workshop_primary_color_$idx")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(swatch.composeColor)
                            .border(
                                width = if (selected) 3.dp else 1.5.dp,
                                color = if (selected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.55f),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = swatch.label,
                        color = if (selected) Color(0xFF00E5FF) else Color(0xFFCBD5E1),
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // 2. Secondary Trim Color
        SectionTitle(title = "🖌️ لون الخطوط والجناح واللمسات الثانوية")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SECONDARY_TRIM_SWATCHES.forEachIndexed { idx, swatch ->
                val selected = floatColorClose(vehicle.colorSecondary, swatch.rgba)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            vehicle.colorSecondary = swatch.rgba.copyOf()
                            onChanged()
                        }
                        .padding(4.dp)
                        .testTag("workshop_secondary_color_$idx")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatch.composeColor)
                            .border(
                                width = if (selected) 3.dp else 1.5.dp,
                                color = if (selected) Color(0xFFFACC15) else Color.White.copy(alpha = 0.45f),
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = swatch.label,
                        color = if (selected) Color(0xFFFACC15) else Color(0xFF94A3B8),
                        fontSize = 9.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // 3. Neon Underglow
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(title = "💡 إضاءة النيون السفلية (Underglow)")
            Switch(
                checked = vehicle.underglowColor != null,
                onCheckedChange = { enabled ->
                    vehicle.underglowColor = if (enabled) {
                        UNDERGLOW_SWATCHES.first().rgba.copyOf()
                    } else {
                        null
                    }
                    onChanged()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF00E5FF)
                ),
                modifier = Modifier.testTag("workshop_underglow_switch")
            )
        }

        if (vehicle.underglowColor != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UNDERGLOW_SWATCHES.forEachIndexed { idx, swatch ->
                    val selected = floatColorClose(vehicle.underglowColor, swatch.rgba)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                vehicle.underglowColor = swatch.rgba.copyOf()
                                onChanged()
                            }
                            .padding(4.dp)
                            .testTag("workshop_underglow_color_$idx")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(swatch.composeColor)
                                .border(
                                    width = if (selected) 3.dp else 1.dp,
                                    color = if (selected) Color.White else Color.White.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = swatch.label,
                            color = if (selected) Color.White else Color(0xFF94A3B8),
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // 4. Body Kit Selector
        SectionTitle(title = "🚘 تصميم الهيكل الخارجي (Body Kit)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BODY_KIT_OPTIONS.forEach { kit ->
                val selected = vehicle.meshStyle == kit.style
                Surface(
                    onClick = {
                        vehicle.meshStyle = kit.style
                        onChanged()
                    },
                    color = if (selected) Color(0xFF1D4ED8) else Color(0xFF162238),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = if (selected) 1.6.dp else 1.dp,
                        color = if (selected) Color(0xFF60A5FA) else Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("workshop_bodykit_${kit.style.name}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(kit.badge, fontSize = 14.sp)
                        Text(
                            text = kit.arabicLabel,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 5. Performance & Tuning Presets + Sliders
        SectionTitle(title = "⚡ ترقية وتعديل المحرك والأداء")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    vehicle.customTopSpeed = (vehicle.effectiveMaxSpeedKmH + 55f).coerceAtMost(450f)
                    vehicle.customAccelRate = (vehicle.effectiveAccelRate + 18f).coerceAtMost(100f)
                    vehicle.customBrakeRate = (vehicle.effectiveBrakeRate + 15f).coerceAtMost(120f)
                    vehicle.soundPitch = (vehicle.soundPitch + 0.12f).coerceIn(0.65f, 1.65f)
                    onChanged()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("workshop_preset_turbo")
            ) {
                Text("⚡ تيربو +55", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    vehicle.customTopSpeed = 380f
                    vehicle.customAccelRate = 88f
                    vehicle.customBrakeRate = 108f
                    vehicle.soundPitch = 1.38f
                    if (vehicle.underglowColor == null) {
                        vehicle.underglowColor = floatArrayOf(0.08f, 0.85f, 1.0f, 1f)
                    }
                    onChanged()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("workshop_preset_hyper")
            ) {
                Text("🚀 باقة هايبر 380", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        TuningSliderRow(
            label = "السرعة القصوى",
            valueDisplay = "${vehicle.effectiveMaxSpeedKmH.roundToInt()} km/h",
            value = vehicle.effectiveMaxSpeedKmH.coerceIn(80f, 450f),
            range = 80f..450f,
            accent = Color(0xFF38BDF8),
            onValueChange = {
                vehicle.customTopSpeed = it
                onChanged()
            }
        )

        TuningSliderRow(
            label = "قوة التسارع (Turbo)",
            valueDisplay = "${vehicle.effectiveAccelRate.roundToInt()}",
            value = vehicle.effectiveAccelRate.coerceIn(20f, 100f),
            range = 20f..100f,
            accent = Color(0xFFF59E0B),
            onValueChange = {
                vehicle.customAccelRate = it
                onChanged()
            }
        )

        TuningSliderRow(
            label = "قوة الفرامل والتماسك",
            valueDisplay = "${vehicle.effectiveBrakeRate.roundToInt()}",
            value = vehicle.effectiveBrakeRate.coerceIn(35f, 120f),
            range = 35f..120f,
            accent = Color(0xFF34D399),
            onValueChange = {
                vehicle.customBrakeRate = it
                onChanged()
            }
        )

        TuningSliderRow(
            label = "حدة صوت المحرك",
            valueDisplay = "${((vehicle.soundPitch) * 100).roundToInt()}%",
            value = vehicle.soundPitch.coerceIn(0.65f, 1.65f),
            range = 0.65f..1.65f,
            accent = Color(0xFFA855F7),
            onValueChange = {
                vehicle.soundPitch = it
                onChanged()
            }
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFE2E8F0),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 12.sp
    )
}

@Composable
private fun StatProgressRow(
    label: String,
    valueText: String,
    progress: Float,
    barColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color(0xFF94A3B8), fontSize = 10.sp)
            Text(text = valueText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progress },
            color = barColor,
            trackColor = Color(0xFF1E293B),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun TuningSliderRow(
    label: String,
    valueDisplay: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    accent: Color,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text = valueDisplay, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accent,
                activeTrackColor = accent,
                inactiveTrackColor = Color(0xFF1E293B)
            )
        )
    }
}
