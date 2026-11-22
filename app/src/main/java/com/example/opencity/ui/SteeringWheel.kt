package com.example.opencity.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * High-fidelity Interactive Steering Wheel (ستيرنج / مقود السيارة التفاعلي):
 * - Authentic 3D sports racing wheel with leather rim, 3 metallic spokes, and top center stripe.
 * - Circular rotation following player's touch:
 *     * Turn Left -> Steers vehicle left (شمال)
 *     * Turn Right -> Steers vehicle right (يمين)
 *     * Center (0°) -> Drives perfectly straight ahead (مستقيم للأمام عند الضغط على البنزين)
 * - Automatic smooth spring-back to center on release!
 * - Real-time degree and directional angle indicator.
 */
@Composable
fun SteeringWheel(
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    maxAngle: Float = 110f,
    currentSteer: Float = 0f,
    onSteerChange: (steer: Float) -> Unit,
    onHornTap: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }
    val centerPx = sizePx / 2f
    val minTouchRadiusPx = with(density) { 18.dp.toPx() }

    var wheelAngle by remember { mutableFloatStateOf(currentSteer * maxAngle) }
    val animAngle = remember { Animatable(wheelAngle) }
    var previousTouchAngle by remember { mutableFloatStateOf(0f) }
    var isTouching by remember { androidx.compose.runtime.mutableStateOf(false) }
    var springJob by remember { androidx.compose.runtime.mutableStateOf<kotlinx.coroutines.Job?>(null) }

    // Sync external Left/Right button inputs cleanly with the visual Steering Wheel rotation
    androidx.compose.runtime.LaunchedEffect(currentSteer, isTouching) {
        if (!isTouching) {
            val targetAngle = (currentSteer.coerceIn(-1f, 1f)) * maxAngle
            if (abs(targetAngle - wheelAngle) > 1.0f) {
                springJob?.cancel()
                wheelAngle = targetAngle
                animAngle.snapTo(targetAngle)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.testTag("interactive_steering_wheel_container")
    ) {
        // Direction & Degree Status Badge
        Surface(
            color = Color(0xDD0B132B),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (abs(wheelAngle) < 4f) Color(0xFF10B981) else Color(0xFF00E5FF)
            ),
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("🛞", fontSize = 12.sp)
                val statusText = when {
                    abs(wheelAngle) < 4f -> "مستقيم (0°)"
                    wheelAngle < -4f -> "◀ شمال (${abs(wheelAngle).toInt()}°)"
                    else -> "يمين ▶ (${wheelAngle.toInt()}°)"
                }
                val statusColor = when {
                    abs(wheelAngle) < 4f -> Color(0xFF34D399) // Green (Straight ahead)
                    wheelAngle < -4f -> Color(0xFF38BDF8) // Cyan Left
                    else -> Color(0xFFFBBF24) // Gold Right
                }
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // The Rotatable Steering Wheel Component
        Box(
            modifier = Modifier
                .size(size)
            .pointerInput(sizePx) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()
                    isTouching = true
                    springJob?.cancel()
                    coroutineScope.launch { animAngle.stop() }

                    val startDx = down.position.x - centerPx
                    val startDy = down.position.y - centerPx
                    val startDist = sqrt(startDx * startDx + startDy * startDy)
                    if (startDist < minTouchRadiusPx && onHornTap != null) {
                        onHornTap()
                    }
                    previousTouchAngle = Math.toDegrees(atan2(startDy.toDouble(), startDx.toDouble())).toFloat()

                    try {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id }
                                ?: event.changes.firstOrNull()
                            if (change == null || !change.pressed) {
                                change?.consume()
                                break
                            }
                            change.consume()
                            val dx = change.position.x - centerPx
                            val dy = change.position.y - centerPx
                            val dist = sqrt(dx * dx + dy * dy)
                            if (dist > minTouchRadiusPx) {
                                val currentTouchAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                var delta = currentTouchAngle - previousTouchAngle
                                if (delta > 180f) delta -= 360f
                                if (delta < -180f) delta += 360f

                                wheelAngle = (wheelAngle + delta).coerceIn(-maxAngle, maxAngle)
                                previousTouchAngle = currentTouchAngle
                                onSteerChange((wheelAngle / maxAngle).coerceIn(-1f, 1f))
                            }
                        }
                    } finally {
                        isTouching = false
                        val releaseAngle = wheelAngle
                        springJob = coroutineScope.launch {
                            animAngle.snapTo(releaseAngle)
                            animAngle.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing)
                            ) {
                                if (!isTouching) {
                                    wheelAngle = value
                                    onSteerChange((value / maxAngle).coerceIn(-1f, 1f))
                                }
                            }
                        }
                    }
                }
            }
        ) {
            // Steering Wheel Body rendered on Canvas and rotated by wheelAngle
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = wheelAngle
                    }
                    .testTag("steering_wheel_canvas")
            ) {
                val w = this.size.width
                val h = this.size.height
                val center = Offset(w / 2f, h / 2f)
                val radius = (w / 2f) - 6.dp.toPx()
                val rimThickness = 16.dp.toPx()

                // 1. Outer Grip Ring Shadow & Base Rim
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = radius,
                    center = center,
                    style = Stroke(width = rimThickness)
                )

                // 2. Leather Texture Gradient Ring
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF334155),
                            Color(0xFF1E293B),
                            Color(0xFF475569),
                            Color(0xFF1E293B),
                            Color(0xFF334155)
                        ),
                        center = center
                    ),
                    radius = radius,
                    center = center,
                    style = Stroke(width = rimThickness - 3.dp.toPx())
                )

                // 3. Ergonomic Thumb Rest Grips at 10 & 2 o'clock
                val thumbAngle1 = -55.0 * Math.PI / 180.0
                val thumbAngle2 = -125.0 * Math.PI / 180.0
                val thumbR = radius
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = 5.dp.toPx(),
                    center = Offset(
                        (center.x + thumbR * cos(thumbAngle1)).toFloat(),
                        (center.y + thumbR * sin(thumbAngle1)).toFloat()
                    )
                )
                drawCircle(
                    color = Color(0xFF0284C7),
                    radius = 5.dp.toPx(),
                    center = Offset(
                        (center.x + thumbR * cos(thumbAngle2)).toFloat(),
                        (center.y + thumbR * sin(thumbAngle2)).toFloat()
                    )
                )

                // 4. Center Top Alignment Racing Stripe (at 12 o'clock / -90°)
                // Clearly shows player when wheel is straight or tilted!
                val topStripeAngle = -Math.PI / 2.0
                val stripeP1 = Offset(
                    (center.x + (radius - rimThickness / 2f) * cos(topStripeAngle)).toFloat(),
                    (center.y + (radius - rimThickness / 2f) * sin(topStripeAngle)).toFloat()
                )
                val stripeP2 = Offset(
                    (center.x + (radius + rimThickness / 2f) * cos(topStripeAngle)).toFloat(),
                    (center.y + (radius + rimThickness / 2f) * sin(topStripeAngle)).toFloat()
                )
                drawLine(
                    color = Color(0xFF00E5FF), // Neon Racing Cyan
                    start = stripeP1,
                    end = stripeP2,
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // 5. Metallic Racing Spokes (3 Spokes: Left, Right, Bottom)
                val spokeColor = Color(0xFF475569)
                val spokeHighlight = Color(0xFF94A3B8)
                val hubRadius = 26.dp.toPx()

                // Left Spoke
                drawLine(
                    brush = Brush.linearGradient(listOf(spokeHighlight, spokeColor)),
                    start = Offset(center.x - hubRadius + 4.dp.toPx(), center.y),
                    end = Offset(center.x - radius + rimThickness / 2f, center.y),
                    strokeWidth = 10.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Right Spoke
                drawLine(
                    brush = Brush.linearGradient(listOf(spokeColor, spokeHighlight)),
                    start = Offset(center.x + hubRadius - 4.dp.toPx(), center.y),
                    end = Offset(center.x + radius - rimThickness / 2f, center.y),
                    strokeWidth = 10.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Bottom Spoke
                drawLine(
                    brush = Brush.linearGradient(listOf(spokeHighlight, spokeColor)),
                    start = Offset(center.x, center.y + hubRadius - 4.dp.toPx()),
                    end = Offset(center.x, center.y + radius - rimThickness / 2f),
                    strokeWidth = 12.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // 6. Center Hub Outer Bezel
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF64748B), Color(0xFF1E293B)),
                        center = center,
                        radius = hubRadius + 4.dp.toPx()
                    ),
                    radius = hubRadius + 2.dp.toPx(),
                    center = center
                )

                // 7. Center Horn Button / Sports Emblem
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isTouching) Color(0xFF0284C7) else Color(0xFF0F172A),
                            Color(0xFF020617)
                        ),
                        center = center,
                        radius = hubRadius
                    ),
                    radius = hubRadius - 2.dp.toPx(),
                    center = center
                )

                // Center Ring Accent
                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = hubRadius - 4.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Center Hex Rivet Screws
                val rivetCount = 6
                for (i in 0 until rivetCount) {
                    val angle = (i * 2.0 * Math.PI) / rivetCount
                    val rx = (center.x + (hubRadius - 7.dp.toPx()) * cos(angle)).toFloat()
                    val ry = (center.y + (hubRadius - 7.dp.toPx()) * sin(angle)).toFloat()
                    drawCircle(
                        color = Color(0xFF94A3B8),
                        radius = 1.8.dp.toPx(),
                        center = Offset(rx, ry)
                    )
                }
            }

            // Horn Icon & Label in the Middle of the Hub
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📯",
                    fontSize = 16.sp
                )
            }
        }
    }
}
