package com.example.opencity.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opencity.model.Building
import com.example.opencity.model.BuildingType
import com.example.opencity.model.Vector3
import com.example.opencity.model.Vehicle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-tech Police & Tactical 3D Radar (رادار الشرطة والمدينة النشط):
 * - Rotating animated radar sweep hand (360° sweeping beam)
 * - Concentric distance range rings (25m, 50m, 75m)
 * - Live Compass directions (N / S / E / W)
 * - Targets: Police Station (flashing blue/red), Robber (pulsing red skull), Vehicles, Buildings
 */
@Composable
fun MinimapView(
    playerPos: Vector3,
    playerYaw: Float,
    buildings: List<Building>,
    vehicles: List<Vehicle>,
    activeMissionPos: Vector3?,
    onOpenFullMap: () -> Unit,
    policeCarPos: Vector3? = null,
    robberCarPos: Vector3? = null,
    autonomousRobots: List<com.example.opencity.model.AutonomousRobot> = emptyList(),
    metroTrains: List<com.example.opencity.model.ElectricMetroTrain> = emptyList(),
    radarSize: androidx.compose.ui.unit.Dp = 124.dp,
    modifier: Modifier = Modifier
) {
    // Continuous rotating radar sweep
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    // Pulsing blips
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val normalizedYaw = ((playerYaw % 360f) + 360f) % 360f
    val compassDirection = when (normalizedYaw.toInt()) {
        in 338..360, in 0..22 -> "شمال N"
        in 23..67 -> "شمال شرق NE"
        in 68..112 -> "شرق E"
        in 113..157 -> "جنوب شرق SE"
        in 158..202 -> "جنوب S"
        in 203..247 -> "جنوب غرب SW"
        in 248..292 -> "غرب W"
        else -> "شمال غرب NW"
    }

    // Distance to robber if active
    val distToRobber = if (robberCarPos != null) {
        val dx = robberCarPos.x - playerPos.x
        val dz = robberCarPos.z - playerPos.z
        sqrt(dx * dx + dz * dz)
    } else null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Radar Top Header Label with status
        Surface(
            color = Color(0xD9060B14),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (distToRobber != null) Color(0xFFEF4444) else Color(0xFF00E5FF)
            ),
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(if (distToRobber != null) "🚨" else "📡", fontSize = 10.sp)
                Text(
                    text = if (distToRobber != null) {
                        "رادار الحرامي: ${distToRobber.toInt()}م"
                    } else {
                        "رادار: $compassDirection"
                    },
                    color = if (distToRobber != null) Color(0xFFFFD600) else Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }

        // Radar Circular Screen
        Box(
            modifier = Modifier
                .size(radarSize)
                .clip(CircleShape)
                .background(Color(0xFF042B49)) // Deep vast ocean surrounding the city island
                .border(
                    2.dp,
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFF00E5FF),
                            Color(0xFF3B82F6),
                            Color(0xFF10B981),
                            Color(0xFF00E5FF)
                        )
                    ),
                    CircleShape
                )
                .focusProperties { canFocus = false }
                .clickable { onOpenFullMap() }
                .testTag("minimap_radar")
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f
                val scale = size.width / 160f // 160m diameter

                // 1. Radar Concentric Distance Grid Rings
                val gridColor = Color(0x3300E5FF)
                drawCircle(color = gridColor, radius = radius * 0.33f, center = center, style = Stroke(width = 1f))
                drawCircle(color = gridColor, radius = radius * 0.66f, center = center, style = Stroke(width = 1f))
                drawCircle(color = Color(0x5500E5FF), radius = radius - 1.5f, center = center, style = Stroke(width = 1.5f))

                // Radar Crosshairs
                drawLine(color = gridColor, start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1f)
                drawLine(color = gridColor, start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1f)

                // 2. City Island, Beach, Ocean & Features rotated with player orientation
                rotate(degrees = -playerYaw, pivot = center) {
                    // Shallow turquoise coastal sea band around beach
                    drawRect(
                        color = Color(0xFF0284C7),
                        topLeft = Offset(center.x + (-360f - playerPos.x) * scale, center.y + (-345f - playerPos.z) * scale),
                        size = Size(870f * scale, 845f * scale)
                    )
                    // Wide Golden Sandy Beach Perimeter surrounding the entire city
                    drawRect(
                        color = Color(0xFFE5C47A),
                        topLeft = Offset(center.x + (-315f - playerPos.x) * scale, center.y + (-297f - playerPos.z) * scale),
                        size = Size(780f * scale, 750f * scale)
                    )
                    // Lush Green City Island Platform
                    drawRect(
                        color = Color(0xFF1A3A2B),
                        topLeft = Offset(center.x + (-256f - playerPos.x) * scale, center.y + (-238f - playerPos.z) * scale),
                        size = Size(662f * scale, 632f * scale)
                    )

                    // Four Coastal Wooden Piers extending into the sea
                    val pierColor = Color(0xFF926239)
                    drawRect(color = pierColor, topLeft = Offset(center.x + (-3.1f - playerPos.x) * scale, center.y + (-334f - playerPos.z) * scale), size = Size(6.2f * scale, 48f * scale))
                    drawRect(color = pierColor, topLeft = Offset(center.x + (171.9f - playerPos.x) * scale, center.y + (441f - playerPos.z) * scale), size = Size(6.2f * scale, 48f * scale))
                    drawRect(color = pierColor, topLeft = Offset(center.x + (-351f - playerPos.x) * scale, center.y + (-23.1f - playerPos.z) * scale), size = Size(48f * scale, 6.2f * scale))
                    drawRect(color = pierColor, topLeft = Offset(center.x + (451f - playerPos.x) * scale, center.y + (36.9f - playerPos.z) * scale), size = Size(48f * scale, 6.2f * scale))

                    // Four Coastal Car Swap Stations ("محطات البدالة")
                    for (st in com.example.opencity.model.CityData.COASTAL_SWAP_STATIONS) {
                        val sx = center.x + (st.padX - playerPos.x) * scale
                        val sz = center.y + (st.padZ - playerPos.z) * scale
                        drawCircle(color = Color(0xFFFACC15), radius = 4.2.dp.toPx(), center = Offset(sx, sz))
                        drawCircle(color = Color(0xFF06B6D4), radius = 2.4.dp.toPx(), center = Offset(sx, sz))
                    }

                    // Roads
                    val roadColor = Color(0xFF1C273B)
                    val waterColor = Color(0xFF0369A1)
                    // Waterfront Marina & Lake on Radar (East Grass Peninsula)
                    val wdx = (236f - 23f - playerPos.x) * scale
                    val wdz = (-115f - 42f - playerPos.z) * scale
                    drawRect(
                        color = waterColor,
                        topLeft = Offset(center.x + wdx, center.y + wdz),
                        size = Size(46f * scale, 84f * scale)
                    )

                    val nsRoads = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
                    for (rx in nsRoads) {
                        val zMin = -188f
                        val zMax = if (rx == 0f || rx == -195f) 348f else 182f
                        val dx = (rx - playerPos.x) * scale
                        val topY = center.y + (zMin - playerPos.z) * scale
                        val roadH = (zMax - zMin) * scale
                        drawRect(
                            color = roadColor,
                            topLeft = Offset(center.x + dx - 6f * scale, topY),
                            size = Size(12f * scale, roadH)
                        )
                    }
                    val ewRoads = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)
                    for (rz in ewRoads) {
                        val xMin = -203f
                        val xMax = if (rz == 175f) 353f else 203f
                        val dz = (rz - playerPos.z) * scale
                        drawRect(
                            color = roadColor,
                            topLeft = Offset(center.x + (xMin - playerPos.x) * scale, center.y + dz - 6f * scale),
                            size = Size((xMax - xMin) * scale, 12f * scale)
                        )
                    }
                    // Villa Boulevards (Z=256f, Z=340f) spanning X=-203..353 & East Villa Road (X=345f)
                    for (rz in listOf(256f, 340f)) {
                        val dz = (rz - playerPos.z) * scale
                        drawRect(
                            color = roadColor,
                            topLeft = Offset(center.x + (-203f - playerPos.x) * scale, center.y + dz - 6f * scale),
                            size = Size(556f * scale, 12f * scale)
                        )
                    }
                    drawRect(
                        color = roadColor,
                        topLeft = Offset(center.x + (345f - playerPos.x) * scale - 6f * scale, center.y + (175f - playerPos.z) * scale),
                        size = Size(12f * scale, 173f * scale)
                    )

                    // Compound 90 Avenue Private Beach, Sea Lagoon & Olympic Pool
                    drawRect(
                        color = Color(0xFFE5C47A),
                        topLeft = Offset(center.x + (14f - playerPos.x) * scale, center.y + (187.5f - playerPos.z) * scale),
                        size = Size(47f * scale, 56f * scale)
                    )
                    drawRect(
                        color = Color(0xFF06B6D4),
                        topLeft = Offset(center.x + (20f - playerPos.x) * scale, center.y + (192f - playerPos.z) * scale),
                        size = Size(36f * scale, 46f * scale)
                    )
                    drawRect(
                        color = Color(0xFF38BDF8),
                        topLeft = Offset(center.x + (65f - playerPos.x) * scale, center.y + (197f - playerPos.z) * scale),
                        size = Size(18f * scale, 32f * scale)
                    )

                    // Elevated Sky Metro Electric Outer Perimeter Loop
                    drawRect(
                        color = Color(0xAA00E5FF),
                        topLeft = Offset(center.x + (-222f - playerPos.x) * scale, center.y + (-206f - playerPos.z) * scale),
                        size = Size(596f * scale, 568f * scale),
                        style = Stroke(width = 2.2f)
                    )
                    for (st in com.example.opencity.model.SkyMetroSystem.STATIONS) {
                        val msx = center.x + (st.groundEntryX - playerPos.x) * scale
                        val msz = center.y + (st.groundEntryZ - playerPos.z) * scale
                        drawCircle(color = Color(0xFFFACC15), radius = 3.6.dp.toPx(), center = Offset(msx, msz))
                    }

                    // Buildings
                    for (b in buildings) {
                        val bx = center.x + (b.posX - playerPos.x) * scale
                        val bz = center.y + (b.posZ - playerPos.z) * scale
                        val bw = b.width * scale
                        val bd = b.depth * scale

                        val bColor = when (b.type) {
                            BuildingType.POLICE_STATION -> Color(0xFF2563EB) // Police blue
                            BuildingType.RESIDENTIAL_VILLA -> Color(0xFF10B981) // Emerald Home
                            BuildingType.GAS_STATION -> Color(0xFFEF4444) // Red Gas
                            BuildingType.CAR_DEALERSHIP -> Color(0xFFF97316) // Orange Showroom
                            BuildingType.HOSPITAL -> Color(0xFFF43F5E) // Hospital
                            else -> if (b.isEnterable) Color(0xFF0284C7) else Color(0xFF334155)
                        }

                        drawRect(
                            color = bColor,
                            topLeft = Offset(bx - bw / 2f, bz - bd / 2f),
                            size = Size(bw, bd)
                        )

                        // Highlight Police Station on Radar
                        if (b.type == BuildingType.POLICE_STATION) {
                            drawCircle(
                                color = Color(0x883B82F6).copy(alpha = pulseAlpha),
                                radius = 9.dp.toPx(),
                                center = Offset(bx, bz)
                            )
                        }
                    }

                    // Ambient & Parked Vehicles
                    for (v in vehicles) {
                        val vx = center.x + (v.posX - playerPos.x) * scale
                        val vz = center.y + (v.posZ - playerPos.z) * scale
                        drawCircle(
                            color = if (v.isPlayerDriving) Color(0xFF00E5FF) else Color(0xFFFF7300),
                            radius = 3.2.dp.toPx(),
                            center = Offset(vx, vz)
                        )
                    }

                    // Autonomous Human-Like Robots (glowing cyan dot on foot outside car)
                    for (robot in autonomousRobots) {
                        if (!robot.isInCar) {
                            val rbx = center.x + (robot.footX - playerPos.x) * scale
                            val rbz = center.y + (robot.footZ - playerPos.z) * scale
                            drawCircle(
                                color = Color(0x6600E5FF),
                                radius = 5.5.dp.toPx(),
                                center = Offset(rbx, rbz)
                            )
                            drawCircle(
                                color = Color(0xFF22D3EE),
                                radius = 3.0.dp.toPx(),
                                center = Offset(rbx, rbz)
                            )
                        }
                    }

                    // Live Elevated Electric Sky Metro Train Blips
                    for (tr in metroTrains) {
                        val tx = center.x + (tr.currentPosX - playerPos.x) * scale
                        val tz = center.y + (tr.currentPosZ - playerPos.z) * scale
                        drawCircle(color = Color(0xFFFACC15), radius = 4.2.dp.toPx(), center = Offset(tx, tz))
                        drawCircle(color = Color(0xFF00E5FF), radius = 2.3.dp.toPx(), center = Offset(tx, tz))
                    }

                    // Police Car Blip (if event is active)
                    if (policeCarPos != null) {
                        val px = center.x + (policeCarPos.x - playerPos.x) * scale
                        val pz = center.y + (policeCarPos.z - playerPos.z) * scale
                        // Pulsing police siren halo
                        drawCircle(
                            color = Color(0x663B82F6).copy(alpha = pulseAlpha * 0.8f),
                            radius = 10.dp.toPx(),
                            center = Offset(px, pz)
                        )
                        drawCircle(
                            color = Color(0xFF3B82F6),
                            radius = 4.5.dp.toPx(),
                            center = Offset(px, pz)
                        )
                    }

                    // Robber Car Blip (الحرامي على الرادار)
                    if (robberCarPos != null) {
                        val rx = center.x + (robberCarPos.x - playerPos.x) * scale
                        val rz = center.y + (robberCarPos.z - playerPos.z) * scale
                        // Pulsing red robber alarm beacon
                        drawCircle(
                            color = Color(0x88EF4444).copy(alpha = pulseAlpha),
                            radius = 12.dp.toPx(),
                            center = Offset(rx, rz)
                        )
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 5.dp.toPx(),
                            center = Offset(rx, rz)
                        )
                    }

                    // Mission / Objective Beacon
                    if (activeMissionPos != null) {
                        val mx = center.x + (activeMissionPos.x - playerPos.x) * scale
                        val mz = center.y + (activeMissionPos.z - playerPos.z) * scale
                        drawCircle(
                            color = Color(0xFFE11D48).copy(alpha = pulseAlpha),
                            radius = 6.dp.toPx(),
                            center = Offset(mx, mz)
                        )
                    }
                }

                // 3. Radar Sweep Line & Glowing Gradient Fan
                val sweepRad = Math.toRadians(sweepAngle.toDouble())
                val sweepEnd = Offset(
                    center.x + (cos(sweepRad) * radius).toFloat(),
                    center.y + (sin(sweepRad) * radius).toFloat()
                )

                // Bright sweeping radar beam line
                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = 0.85f),
                    start = center,
                    end = sweepEnd,
                    strokeWidth = 2f
                )

                // 4. Center Player Arrow (Always facing UP)
                val arrowPath = Path().apply {
                    moveTo(center.x, center.y - 9.dp.toPx())
                    lineTo(center.x - 6.dp.toPx(), center.y + 7.dp.toPx())
                    lineTo(center.x, center.y + 3.5.dp.toPx())
                    lineTo(center.x + 6.dp.toPx(), center.y + 7.dp.toPx())
                    close()
                }
                drawPath(arrowPath, Color(0xFF00E5FF))
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = center)
            }
        }
    }
}
