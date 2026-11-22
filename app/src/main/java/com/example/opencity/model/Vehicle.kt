package com.example.opencity.model

import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

enum class VehicleType(
    val arabicName: String,
    val maxSpeedKmH: Float,
    val accelRate: Float,
    val brakeRate: Float,
    val length: Float,
    val width: Float,
    val height: Float
) {
    SPORTS_CAR("سيارة رياضية", 140f, 45f, 75f, 4.4f, 2.0f, 1.2f),
    SEDAN("سيارة سيدان عائلية", 100f, 32f, 60f, 4.6f, 1.9f, 1.5f),
    TAXI("سيارة أجرة تاكسي", 105f, 34f, 65f, 4.6f, 1.9f, 1.55f),
    CITY_BUS("حافلة سفر ونقل", 75f, 18f, 45f, 9.5f, 2.6f, 3.2f),
    POLICE_CRUISER("سيارة دورية شرطة", 130f, 42f, 70f, 4.8f, 2.0f, 1.55f),
    AIRPLANE("طائرة خاصة", 220f, 36f, 52f, 5.6f, 6.2f, 2.0f)
}

/**
 * 3D drivable, traffic, and flyable vehicle entity.
 */
data class Vehicle(
    val id: String,
    val type: VehicleType,
    var posX: Float,
    var posY: Float = 0f,
    var posZ: Float,
    var yaw: Float = 0f, // degrees rotation around Y axis
    var pitchAngle: Float = 0f, // degrees pitch up/down for aircraft
    var speed: Float = 0f, // current forward speed in m/s
    var verticalSpeed: Float = 0f, // m/s vertical speed for aircraft
    var steeringAngle: Float = 0f, // degrees
    var colorPrimary: FloatArray, // RGBA
    var colorSecondary: FloatArray,
    var isPlayerDriving: Boolean = false,
    var headlightsOn: Boolean = true,
    var sirenOn: Boolean = false,
    var hornActive: Boolean = false,
    // AI navigation data for ambient traffic
    var aiTargetIndex: Int = 0,
    var isAiControlled: Boolean = true,
    // Dealership custom attributes
    val dealershipCarId: String? = null,
    val displayName: String? = null,
    var meshStyle: CarMeshStyle? = null,
    var customTopSpeed: Float? = null,
    var customAccelRate: Float? = null,
    var customBrakeRate: Float? = null,
    var soundPitch: Float = 1.0f,
    var isShowroomDisplay: Boolean = false,
    var isInUndergroundGarage: Boolean = false,
    var isNitroActive: Boolean = false,
    var nitroFuel: Float = 100f,
    var isDrifting: Boolean = false,
    var underglowColor: FloatArray? = null,
    var fuel: Float = 100f, // 0 to 100% (بنزين)
    var wheelDistanceMeters: Float = 0f,
    var wheelTurnAccumulatorMeters: Float = 0f,
    var totalWheelRotations: Long = 0L,
    var highSpeedWheelRotations: Long = 0L,
    var lastTickWheelRotations: Int = 0,
    var lastTickHighSpeedRotations: Int = 0
) {
    companion object {
        const val WHEEL_RADIUS_METERS = 0.32f
        const val WHEEL_CIRCUMFERENCE_METERS = (2.0 * Math.PI * WHEEL_RADIUS_METERS).toFloat()
    }

    val effectiveMaxSpeedKmH: Float
        get() = customTopSpeed ?: type.maxSpeedKmH

    val effectiveAccelRate: Float
        get() = customAccelRate ?: type.accelRate

    val effectiveBrakeRate: Float
        get() = customBrakeRate ?: type.brakeRate

    val speedKmH: Float
        get() = speed * 3.6f

    fun refillFuel(amount: Float = 100f) {
        fuel = (fuel + amount).coerceIn(0f, 100f)
    }

    fun updatePhysics(
        throttle: Float, // -1 (reverse) to 1 (accelerate)
        steeringInput: Float, // -1 (left) to 1 (right)
        deltaSec: Float,
        handbrake: Boolean = false
    ) {
        if (deltaSec <= 0f || deltaSec.isNaN()) return
        val dt = deltaSec.coerceAtMost(0.1f)
        val safeThrottle = if (throttle.isNaN()) 0f else throttle.coerceIn(-1f, 1f)
        val safeSteer = if (steeringInput.isNaN()) 0f else steeringInput.coerceIn(-1f, 1f)

        // Fuel consumption while engine is active
        val hasFuel = fuel > 0.05f
        if (hasFuel && kotlin.math.abs(speed) > 0.1f) {
            val fuelBurnRate = if (isNitroActive) 0.85f else 0.35f
            fuel = (fuel - dt * fuelBurnRate).coerceAtLeast(0f)
        }

        // Keep an emergency limp-home reserve (38% power) so 4-way driving never dies completely
        val effectiveThrottle = if (hasFuel) safeThrottle else safeThrottle * 0.38f

        // Nitro Boost calculation
        val nitroBoostActive = isNitroActive && nitroFuel > 1f && hasFuel
        if (nitroBoostActive) {
            nitroFuel = (nitroFuel - dt * 28f).coerceAtLeast(0f)
            if (nitroFuel <= 0.5f) isNitroActive = false
        } else {
            nitroFuel = (nitroFuel + dt * 9f).coerceAtMost(100f)
        }

        val nitroSpeedMultiplier = if (nitroBoostActive) 1.25f else 1.0f
        val nitroAccelMultiplier = if (nitroBoostActive) 2.2f else 1.0f

        val baseMaxSpeedMs = effectiveMaxSpeedKmH / 3.6f
        val maxSpeedMs = baseMaxSpeedMs * nitroSpeedMultiplier * (if (hasFuel) 1.0f else 0.42f)
        val maxReverseMs = -baseMaxSpeedMs * 0.40f * (if (hasFuel) 1.0f else 0.48f)
        val currentAccelRate = effectiveAccelRate * nitroAccelMultiplier

        isDrifting = handbrake && kotlin.math.abs(speed) > 4f

        // Strong Handbrake & Instant Braking
        if (handbrake) {
            val brakeForce = effectiveBrakeRate * 2.4f * dt
            if (speed > brakeForce) speed -= brakeForce
            else if (speed < -brakeForce) speed += brakeForce
            else speed = 0f
        } else if (effectiveThrottle > 0.02f) {
            // Acceleration forward (قدام)
            speed += currentAccelRate * effectiveThrottle * dt
            if (speed > maxSpeedMs) speed = maxSpeedMs
        } else if (effectiveThrottle < -0.02f) {
            if (speed > 0.5f) {
                // Foot braking while moving forward
                val brakePower = effectiveBrakeRate * 1.7f
                speed -= brakePower * dt
                if (speed < 0f) speed = 0f
            } else {
                // Reversing backward (ورا)
                speed += currentAccelRate * 0.7f * effectiveThrottle * dt
                if (speed < maxReverseMs) speed = maxReverseMs
            }
        } else {
            // Natural friction / rolling resistance (lower friction while drifting)
            val friction = (if (isDrifting) 5f else 10f) * dt
            if (speed > friction) speed -= friction
            else if (speed < -friction) speed += friction
            else speed = 0f
        }

        // Steering Left / Right (يمين / شمال)
        val maxSteerAngle = if (isDrifting) 45f else 36f
        val targetSteering = safeSteer * maxSteerAngle
        steeringAngle += (targetSteering - steeringAngle) * min(1f, dt * (if (isDrifting) 14f else 10f))

        // Turn rate depends on forward/reverse speed and drift state
        if (kotlin.math.abs(speed) > 0.04f) {
            val speedSign = if (speed >= 0f) 1f else -1f
            val speedAbs = kotlin.math.abs(speed)
            // Snappy and agile steering at all speeds (including low speed & reverse)
            val speedTurnScalar = (speedAbs / 5.5f).coerceIn(0.42f, 1.0f)
            val driftTurnMultiplier = if (isDrifting) 3.5f else 2.6f
            val turnRate = (steeringAngle * driftTurnMultiplier) * speedSign * speedTurnScalar
            // In right-handed +Y-up coordinates (forward = +Z, screen-right = -X), turning right decreases yaw
            yaw = ((yaw - turnRate * dt) % 360f + 360f) % 360f
        }

        // Move position based on yaw
        val rad = Math.toRadians(yaw.toDouble())
        val forwardX = sin(rad).toFloat()
        val forwardZ = cos(rad).toFloat()

        posX += forwardX * speed * dt
        posZ += forwardZ * speed * dt

        // Count real-time wheel revolutions / steps based on wheel circumference (~2.01m per turn)
        val stepDistance = kotlin.math.abs(speed) * dt
        if (stepDistance > 0.0001f) {
            wheelDistanceMeters += stepDistance
            wheelTurnAccumulatorMeters += stepDistance
            val completedTurns = (wheelTurnAccumulatorMeters / WHEEL_CIRCUMFERENCE_METERS).toInt()
            if (completedTurns > 0) {
                wheelTurnAccumulatorMeters -= completedTurns * WHEEL_CIRCUMFERENCE_METERS
                totalWheelRotations += completedTurns
                lastTickWheelRotations = completedTurns
                if (kotlin.math.abs(speedKmH) >= 110f) {
                    highSpeedWheelRotations += completedTurns
                    lastTickHighSpeedRotations = completedTurns
                } else {
                    lastTickHighSpeedRotations = 0
                }
            } else {
                lastTickWheelRotations = 0
                lastTickHighSpeedRotations = 0
            }
        } else {
            lastTickWheelRotations = 0
            lastTickHighSpeedRotations = 0
        }
    }

    /**
     * Smooth VTOL / light-aircraft flight physics supporting airport runways and street-house rooftop parking pads.
     */
    fun updateFlightPhysics(
        throttle: Float,
        steeringInput: Float,
        climbInput: Float, // +1 ascend/takeoff, -1 descend/land, 0 hover/level
        deltaSec: Float,
        handbrake: Boolean = false,
        supportSurfaceY: Float = 0f
    ) {
        if (deltaSec <= 0f || deltaSec.isNaN()) return
        val dt = deltaSec.coerceAtMost(0.1f)
        val safeThrottle = if (throttle.isNaN()) 0f else throttle.coerceIn(-1f, 1f)
        val safeSteer = if (steeringInput.isNaN()) 0f else steeringInput.coerceIn(-1f, 1f)
        val safeClimb = if (climbInput.isNaN()) 0f else climbInput.coerceIn(-1f, 1f)

        val maxSpeedMs = effectiveMaxSpeedKmH / 3.6f
        val maxReverseMs = -10f

        if (handbrake) {
            val brakeForce = effectiveBrakeRate * 2.2f * dt
            if (speed > brakeForce) speed -= brakeForce
            else if (speed < -brakeForce) speed += brakeForce
            else speed = 0f
        } else if (safeThrottle > 0.05f) {
            speed = (speed + effectiveAccelRate * safeThrottle * dt).coerceAtMost(maxSpeedMs)
        } else if (safeThrottle < -0.05f) {
            if (speed > 0.5f) {
                speed = (speed - effectiveBrakeRate * 1.4f * dt).coerceAtLeast(0f)
            } else {
                speed = (speed + effectiveAccelRate * 0.4f * safeThrottle * dt).coerceAtLeast(maxReverseMs)
            }
        } else {
            val drag = (if (posY > supportSurfaceY + 0.25f) 4.0f else 8.5f) * dt
            if (speed > drag) speed -= drag
            else if (speed < -drag) speed += drag
            else speed = 0f
        }

        // Steering works in flight and while taxiing on runway/rooftop
        val targetSteering = safeSteer * 36f
        steeringAngle += (targetSteering - steeringAngle) * min(1f, dt * 8f)
        if (kotlin.math.abs(steeringAngle) > 0.2f) {
            val rate = steeringAngle * 2.2f * (if (speed >= -0.2f) 1f else -1f)
            yaw = ((yaw - rate * dt) % 360f + 360f) % 360f
        }

        // Vertical climb / hover / precision rooftop landing
        val targetVy = when {
            safeClimb > 0.1f -> 14f * safeClimb
            safeClimb < -0.1f -> 12f * safeClimb
            else -> 0f
        }
        verticalSpeed += (targetVy - verticalSpeed) * min(1f, dt * 7f)
        posY += verticalSpeed * dt

        if (posY <= supportSurfaceY) {
            posY = supportSurfaceY
            if (verticalSpeed < 0f) verticalSpeed = 0f
            pitchAngle += (0f - pitchAngle) * min(1f, dt * 10f)
        } else {
            posY = posY.coerceAtMost(140f)
            val targetPitch = (verticalSpeed * 1.2f).coerceIn(-18f, 22f)
            pitchAngle += (targetPitch - pitchAngle) * min(1f, dt * 6f)
        }

        val rad = Math.toRadians(yaw.toDouble())
        posX += sin(rad).toFloat() * speed * dt
        posZ += cos(rad).toFloat() * speed * dt
        lastTickWheelRotations = 0
        lastTickHighSpeedRotations = 0
    }

    /**
     * Bounding box check for collision with another point.
     */
    fun checkCollision(px: Float, pz: Float, margin: Float = 0.5f): Boolean {
        val rad = Math.toRadians(-yaw.toDouble())
        val cosA = cos(rad).toFloat()
        val sinA = sin(rad).toFloat()
        val dx = px - posX
        val dz = pz - posZ
        val localX = dx * cosA - dz * sinA
        val localZ = dx * sinA + dz * cosA

        val halfW = type.width / 2f + margin
        val halfL = type.length / 2f + margin
        return localX in -halfW..halfW && localZ in -halfL..halfL
    }
}
