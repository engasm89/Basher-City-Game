package com.example.opencity.model

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Traffic light phase for an intersection.
 * NS = North-South avenue, EW = East-West boulevard.
 */
enum class IntersectionPhase {
    NS_GREEN,
    NS_YELLOW,
    EW_GREEN,
    EW_YELLOW,
    ALL_STOP
}

data class Intersection(
    val id: String,
    val x: Float,
    val z: Float,
    var phase: IntersectionPhase = IntersectionPhase.NS_GREEN,
    var timer: Float = 0f
) {
    companion object {
        const val GREEN_DURATION_SEC = 100.0f
        const val YELLOW_DURATION_SEC = 10.0f
    }

    fun isNsGreen(): Boolean = phase == IntersectionPhase.NS_GREEN
    fun isNsYellow(): Boolean = phase == IntersectionPhase.NS_YELLOW
    fun isNsRed(): Boolean = !isNsGreen() && !isNsYellow()
    fun isEwGreen(): Boolean = phase == IntersectionPhase.EW_GREEN
    fun isEwYellow(): Boolean = phase == IntersectionPhase.EW_YELLOW
    fun isEwRed(): Boolean = !isEwGreen() && !isEwYellow()

    fun getPhaseRemainingSeconds(): Int {
        val dur = when (phase) {
            IntersectionPhase.NS_GREEN, IntersectionPhase.EW_GREEN -> GREEN_DURATION_SEC
            IntersectionPhase.NS_YELLOW, IntersectionPhase.EW_YELLOW -> YELLOW_DURATION_SEC
            IntersectionPhase.ALL_STOP -> 0f
        }
        return max(0, (dur - timer).toInt())
    }
}

/**
 * Cyber Robot walking naturally across green grass parks and lawns around the city.
 */
data class GrassRoamRobot(
    val id: String,
    val arabicName: String,
    var posX: Float,
    var posY: Float = 0f,
    var posZ: Float,
    var yaw: Float = 0f,
    var walkPhase: Float = 0f,
    var wavePhase: Float = 0f,
    val visorColor: FloatArray,
    val waypoints: List<Vector3>,
    var currentWaypointIndex: Int = 0,
    val speed: Float = 1.85f
) {
    fun update(deltaSec: Float, buildings: List<Building>, playerPos: Vector3) {
        if (deltaSec <= 0f || deltaSec.isNaN() || deltaSec.isInfinite()) return
        if (waypoints.isEmpty()) return
        val dxP = playerPos.x - posX
        val dzP = playerPos.z - posZ
        val nearPlayer = (dxP * dxP + dzP * dzP) <= 10.0f * 10.0f
        if (nearPlayer) {
            wavePhase += deltaSec * 7.5f
        } else {
            wavePhase = 0f
        }

        val target = waypoints[currentWaypointIndex]
        val dx = target.x - posX
        val dz = target.z - posZ
        val dist = sqrt(dx * dx + dz * dz)

        if (dist < 0.8f) {
            currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size
            return
        }

        val targetYaw = Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
        var diff = (targetYaw - yaw) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        yaw = (yaw + diff.coerceIn(-240f * deltaSec, 240f * deltaSec) + 360f) % 360f

        val rad = Math.toRadians(yaw.toDouble())
        var nx = posX + sin(rad).toFloat() * speed * deltaSec
        var nz = posZ + cos(rad).toFloat() * speed * deltaSec
        for (b in buildings) {
            val pushed = b.resolveCollisionPush(nx, nz, margin = 0.85f)
            if (pushed != null) {
                nx = pushed.first
                nz = pushed.second
            }
        }
        posX = nx
        posZ = nz
        walkPhase += deltaSec * 8.5f
    }
}

/**
 * Direction a car is traveling on the grid.
 */
enum class TravelDirection {
    NORTH, // +Z (yaw 0)
    SOUTH, // -Z (yaw 180)
    EAST,  // +X (yaw 90)
    WEST   // -X (yaw 270)
}

/**
 * Comprehensive traffic management system for OpenCity 3D.
 * Manages intersection traffic lights (100s green, 10s yellow, red), realistic vehicle lane-following,
 * left/right intersection turns, red light stopping, distance spacing between vehicles,
 * autonomous cyber robots, and grass-roaming pedestrians & robots.
 */
class TrafficSystem {

    val nsAvenues = listOf(-115f, -75f, -35f, 0f, 35f, 75f, 115f)
    val ewBoulevards = listOf(-100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f)

    // Intersections with traffic lights
    val intersections = mutableListOf<Intersection>()

    // Mode: Automatic timer or manual player override
    var manualControlActive = false
    var allStopActive = false

    // AI Vehicles (robot-driven cars turning right & left through city streets)
    val trafficCars = java.util.concurrent.CopyOnWriteArrayList<Vehicle>()
    private val carTurnCooldown = mutableMapOf<String, Float>()
    private val carTargetYaw = mutableMapOf<String, Float>()
    private val carTargetLaneAxis = mutableMapOf<String, Pair<Boolean, Float>>() // isVerticalRoad -> fixed coordinate
    private val carStuckTimer = mutableMapOf<String, Float>()
    private val carReverseTimer = mutableMapOf<String, Float>()
    private val internalObstacleList = ArrayList<Vehicle>(40)

    // Autonomous Human-Like Cyber Robots (drive closed city loops forever, pull over, exit, act human, return)
    val autonomousRobots: List<AutonomousRobot> = AutonomousRobot.createCityFleet()

    // Dedicated Cyber Robots walking naturally on green grass lawns across the city
    val grassRobots = java.util.concurrent.CopyOnWriteArrayList<GrassRoamRobot>()

    // Pedestrians on parks/lawns
    val pedestrians = java.util.concurrent.CopyOnWriteArrayList<NPC>()

    fun allActiveTrafficVehicles(): List<Vehicle> {
        val list = ArrayList<Vehicle>(trafficCars.size + autonomousRobots.size)
        list.addAll(trafficCars)
        for (robot in autonomousRobots) {
            list.add(robot.car)
        }
        return list
    }

    private fun refreshInternalObstacles() {
        internalObstacleList.clear()
        internalObstacleList.addAll(trafficCars)
        for (robot in autonomousRobots) {
            internalObstacleList.add(robot.car)
        }
    }

    init {
        // Create intersections with 100s green / 10s yellow staggered cycles
        for (rx in nsAvenues) {
            for (rz in ewBoulevards) {
                val startNs = ((rx.toInt() + rz.toInt()) % 70 == 0)
                val initialOffset = ((abs(rx.toInt()) * 3 + abs(rz.toInt()) * 5) % 95).toFloat()
                intersections.add(
                    Intersection(
                        id = "inter_${rx.toInt()}_${rz.toInt()}",
                        x = rx,
                        z = rz,
                        phase = if (startNs) IntersectionPhase.NS_GREEN else IntersectionPhase.EW_GREEN,
                        timer = initialOffset
                    )
                )
            }
        }

        spawnInitialTraffic()
        spawnGrassRobots()
        spawnSidewalkPedestrians()
    }

    private fun spawnInitialTraffic() {
        // Fleet of realistic robot-driven traffic vehicles traveling in distinct road lanes
        val carConfigs = listOf(
            // North-bound cars (+Z, lane x = rx + 3f)
            Triple(0f + 3f, -120f, TravelDirection.NORTH),
            Triple(75f + 3f, -85f, TravelDirection.NORTH),
            Triple(0f + 3f, 65f, TravelDirection.NORTH),
            Triple(35f + 3f, -90f, TravelDirection.NORTH),
            Triple(35f + 3f, 10f, TravelDirection.NORTH),
            Triple(-35f + 3f, -50f, TravelDirection.NORTH),
            Triple(-75f + 3f, -80f, TravelDirection.NORTH),

            // South-bound cars (-Z, lane x = rx - 3f)
            Triple(0f - 3f, 130f, TravelDirection.SOUTH),
            Triple(0f - 3f, 55f, TravelDirection.SOUTH),
            Triple(-75f - 3f, -30f, TravelDirection.SOUTH),
            Triple(35f - 3f, 80f, TravelDirection.SOUTH),
            Triple(-35f - 3f, 90f, TravelDirection.SOUTH),
            Triple(-35f - 3f, -10f, TravelDirection.SOUTH),
            Triple(75f - 3f, 20f, TravelDirection.SOUTH),

            // East-bound cars (+X, lane z = rz - 3f)
            Triple(-130f, 0f - 3f, TravelDirection.EAST),
            Triple(-95f, -60f - 3f, TravelDirection.EAST),
            Triple(45f, 0f - 3f, TravelDirection.EAST),
            Triple(-100f, 35f - 3f, TravelDirection.EAST),
            Triple(20f, 65f - 3f, TravelDirection.EAST),

            // West-bound cars (-X, lane z = rz + 3f)
            Triple(120f, 0f + 3f, TravelDirection.WEST),
            Triple(95f, 35f + 3f, TravelDirection.WEST),
            Triple(-50f, 0f + 3f, TravelDirection.WEST),
            Triple(90f, 35f + 3f, TravelDirection.WEST),
            Triple(-30f, 65f + 3f, TravelDirection.WEST)
        )

        val colors = listOf(
            floatArrayOf(1.0f, 0.45f, 0.0f, 1f),   // Cyber Orange
            floatArrayOf(0.08f, 0.58f, 0.96f, 1f), // Electric Blue
            floatArrayOf(0.92f, 0.16f, 0.24f, 1f), // Crimson Red
            floatArrayOf(0.96f, 0.78f, 0.10f, 1f), // Solar Gold
            floatArrayOf(0.14f, 0.76f, 0.48f, 1f), // Emerald Cyber
            floatArrayOf(0.92f, 0.94f, 0.98f, 1f)  // Pearl Titanium
        )

        val styles = listOf(
            CarMeshStyle.ROBLOX_GAMER_BLOX,
            CarMeshStyle.SUPER_SPORT,
            CarMeshStyle.MUSCLE_GT,
            CarMeshStyle.SEDAN_MODERN
        )

        for (i in carConfigs.indices) {
            val cfg = carConfigs[i]
            val yaw = when (cfg.third) {
                TravelDirection.NORTH -> 0f
                TravelDirection.SOUTH -> 180f
                TravelDirection.EAST -> 90f
                TravelDirection.WEST -> 270f
            }

            val primaryColor = if (i % 3 == 0) floatArrayOf(1.0f, 0.45f, 0.0f, 1f) else colors[i % colors.size]
            val secondaryColor = floatArrayOf(0.10f, 0.12f, 0.16f, 1f)

            val v = Vehicle(
                id = "ai_robot_car_$i",
                type = VehicleType.SPORTS_CAR,
                posX = cfg.first,
                posY = 0f,
                posZ = cfg.second,
                yaw = yaw,
                speed = 10.5f,
                colorPrimary = primaryColor,
                colorSecondary = secondaryColor,
                isPlayerDriving = false,
                isAiControlled = true,
                displayName = "سيارة يقودها روبوت 🤖",
                meshStyle = styles[i % styles.size],
                customTopSpeed = 280f,
                customAccelRate = 65f,
                customBrakeRate = 90f,
                underglowColor = floatArrayOf(0.0f, 0.85f, 1.0f, 1.0f)
            )
            carTargetYaw[v.id] = yaw
            carTurnCooldown[v.id] = (i % 4) * 1.2f
            trafficCars.add(v)
        }
    }

    private fun spawnGrassRobots() {
        val visors = listOf(
            floatArrayOf(0.05f, 0.95f, 1.0f, 1f),
            floatArrayOf(0.20f, 0.98f, 0.45f, 1f),
            floatArrayOf(1.0f, 0.78f, 0.12f, 1f),
            floatArrayOf(0.88f, 0.35f, 1.0f, 1f)
        )
        val grassPatrols = listOf(
            Triple("روبوت العشب - وسط المدينة 🌿", Pair(-17.5f, 50f), 4.8f),
            Triple("روبوت العشب - الحديقة الشرقية 🌿", Pair(17.5f, 50f), 4.8f),
            Triple("روبوت العشب - ساحة المول 🌿", Pair(-55f, 50f), 5.0f),
            Triple("روبوت العشب - حديقة المستشفى 🌿", Pair(95f, 50f), 5.0f),
            Triple("روبوت العشب - الحي السكني 🌿", Pair(95f, 118f), 4.5f),
            Triple("روبوت العشب - مروج المارينا 🌿", Pair(175f, -80f), 6.0f),
            Triple("روبوت العشب - حي الفلل 1 🌿", Pair(94f, 212f), 7.5f),
            Triple("روبوت العشب - حي الفلل 2 🌿", Pair(178f, 212f), 7.5f)
        )
        for (i in grassPatrols.indices) {
            val (name, center, r) = grassPatrols[i]
            val cx = center.first
            val cz = center.second
            val wps = listOf(
                Vector3(cx - r * 0.6f, 0f, cz - r),
                Vector3(cx + r * 0.6f, 0f, cz - r * 0.4f),
                Vector3(cx + r * 0.6f, 0f, cz + r),
                Vector3(cx - r * 0.6f, 0f, cz + r * 0.4f)
            )
            grassRobots.add(
                GrassRoamRobot(
                    id = "grass_robot_$i",
                    arabicName = name,
                    posX = cx,
                    posY = 0f,
                    posZ = cz,
                    visorColor = visors[i % visors.size],
                    waypoints = wps
                )
            )
        }
    }

    private fun spawnSidewalkPedestrians() {
        // Citizens walk ONLY inside safe off-road parks, gardens, and inner plazas — NEVER crossing or touching any street
        val parkLoops = listOf(
            Pair(94f, 210f) to 8f,
            Pair(178f, 212f) to 8f,
            Pair(261f, 210f) to 8f,
            Pair(94f, 294f) to 8f,
            Pair(178f, 294f) to 8f,
            Pair(-17.5f, 49f) to 5f,
            Pair(17.5f, 49f) to 5f,
            Pair(-55f, 49f) to 5f,
            Pair(-95f, 17.5f) to 4.5f,
            Pair(95f, 49f) to 5f,
            Pair(95f, 118f) to 5f,
            Pair(135f, 82.5f) to 5f,
            Pair(175f, -80f) to 6f,
            Pair(175f, -15f) to 4.5f,
            Pair(211f, -20f) to 6f
        )

        val names = listOf(
            "عمر (في الحديقة)", "فاطمة (متنزهة)", "خالد (مهندس)", "مريم (طالبة)",
            "سعيد (رياضي)", "نورة (طبيبة)", "يوسف (زائر)", "ليلى (معلمة)",
            "طارق (سائح)", "زينب (رياضية)", "حمزة (طالب جامعي)", "هدى (متنزهة)"
        )

        val shirts = listOf(
            floatArrayOf(0.2f, 0.5f, 0.85f, 1f),
            floatArrayOf(0.9f, 0.25f, 0.35f, 1f),
            floatArrayOf(0.25f, 0.75f, 0.35f, 1f),
            floatArrayOf(0.95f, 0.75f, 0.2f, 1f),
            floatArrayOf(0.6f, 0.35f, 0.75f, 1f),
            floatArrayOf(0.95f, 0.95f, 0.95f, 1f)
        )

        for (i in parkLoops.indices) {
            val (center, span) = parkLoops[i]
            val sx = center.first
            val sz = center.second

            val waypoints = listOf(
                Vector3(sx, 0f, sz - span),
                Vector3(sx + 2.5f, 0f, sz),
                Vector3(sx, 0f, sz + span),
                Vector3(sx - 2.5f, 0f, sz)
            )

            val npc = NPC(
                id = "ped_park_$i",
                name = names[i % names.size],
                posX = sx,
                posY = 0f,
                posZ = sz,
                speed = 1.25f + (i % 3) * 0.2f,
                shirtColor = shirts[i % shirts.size],
                pantsColor = floatArrayOf(0.18f, 0.2f, 0.28f, 1f),
                waypoints = waypoints,
                greetingArabic = "مرحباً بك! الحدائق الخضراء آمنة ولا أحد يقطع الشوارع."
            )
            pedestrians.add(npc)
        }
    }

    private fun normalizeYawDelta(target: Float, current: Float): Float {
        var diff = (target - current) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return diff
    }

    /**
     * Updates traffic lights (100s green / 10s yellow / red), robot vehicles (4-way driving & turning), and grass robots.
     */
    fun update(deltaSec: Float, playerCar: Vehicle?, playerPos: Vector3, buildings: List<Building> = emptyList()) {
        if (deltaSec <= 0f || deltaSec.isNaN() || deltaSec.isInfinite()) return

        // 1. Update Traffic Lights cycle: 100s Green + 10s Yellow per direction (Red for cross direction)
        if (!manualControlActive && !allStopActive) {
            for (inter in intersections) {
                inter.timer += deltaSec
                when (inter.phase) {
                    IntersectionPhase.NS_GREEN -> {
                        if (inter.timer >= Intersection.GREEN_DURATION_SEC) {
                            inter.phase = IntersectionPhase.NS_YELLOW
                            inter.timer = 0f
                        }
                    }
                    IntersectionPhase.NS_YELLOW -> {
                        if (inter.timer >= Intersection.YELLOW_DURATION_SEC) {
                            inter.phase = IntersectionPhase.EW_GREEN
                            inter.timer = 0f
                        }
                    }
                    IntersectionPhase.EW_GREEN -> {
                        if (inter.timer >= Intersection.GREEN_DURATION_SEC) {
                            inter.phase = IntersectionPhase.EW_YELLOW
                            inter.timer = 0f
                        }
                    }
                    IntersectionPhase.EW_YELLOW -> {
                        if (inter.timer >= Intersection.YELLOW_DURATION_SEC) {
                            inter.phase = IntersectionPhase.NS_GREEN
                            inter.timer = 0f
                        }
                    }
                    IntersectionPhase.ALL_STOP -> {}
                }
            }
        }

        // 2. Update AI Robot-Driven Vehicles: lane-following, left/right turning, & reverse obstacle avoidance
        refreshInternalObstacles()
        for (i in trafficCars.indices) {
            val car = trafficCars[i]
            if (car.isPlayerDriving || car.isInUndergroundGarage) continue

            // Reverse recovery maneuver (ورا + يمين/شمال) when blocked or deadlocked
            val revLeft = carReverseTimer[car.id] ?: 0f
            if (revLeft > 0f) {
                carReverseTimer[car.id] = max(0f, revLeft - deltaSec)
                val steerSign = if (i % 2 == 0) 1f else -1f
                car.steeringAngle = steerSign * 26f
                car.speed = -4.2f
                car.yaw = ((car.yaw - steerSign * 55f * deltaSec) % 360f + 360f) % 360f
                val radRev = Math.toRadians(car.yaw.toDouble())
                car.posX += sin(radRev).toFloat() * car.speed * deltaSec
                car.posZ += cos(radRev).toFloat() * car.speed * deltaSec
                for (b in buildings) {
                    val pushed = b.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = 1.4f, bodyHeight = car.type.height)
                    if (pushed != null) {
                        car.posX = pushed.first
                        car.posZ = pushed.second
                    }
                }
                continue
            }

            val cd = (carTurnCooldown[car.id] ?: 0f) - deltaSec
            carTurnCooldown[car.id] = max(0f, cd)

            val desiredYaw = carTargetYaw[car.id] ?: car.yaw
            val yawDiff = normalizeYawDelta(desiredYaw, car.yaw)
            val isCurrentlyTurning = abs(yawDiff) > 2.0f
            if (isCurrentlyTurning) {
                car.yaw = ((car.yaw + yawDiff.coerceIn(-165f * deltaSec, 165f * deltaSec)) % 360f + 360f) % 360f
                car.steeringAngle = yawDiff.coerceIn(-30f, 30f)
            } else {
                car.yaw = desiredYaw
                car.steeringAngle *= max(0f, 1f - deltaSec * 8f)
                // Gently snap toward aligned lane coordinate after finishing a turn
                carTargetLaneAxis[car.id]?.let { (isVerticalRoad, laneCoord) ->
                    if (isVerticalRoad) {
                        car.posX += (laneCoord - car.posX) * min(1f, deltaSec * 4.5f)
                    } else {
                        car.posZ += (laneCoord - car.posZ) * min(1f, deltaSec * 4.5f)
                    }
                }
            }

            val cruiseSpeedMs = if (isCurrentlyTurning) 7.4f else car.type.maxSpeedKmH * 0.32f / 3.6f
            val normalizedYaw = ((car.yaw % 360f) + 360f) % 360f
            val isNorth = normalizedYaw < 45f || normalizedYaw >= 315f
            val isSouth = normalizedYaw in 135f..225f
            val isEast = normalizedYaw in 45f..135f
            val isWest = normalizedYaw in 225f..315f

            var stoppedByLight = allStopActive

            // Check traffic light & initiate realistic Right / Left turns inside intersections
            if (!stoppedByLight && !isCurrentlyTurning) {
                for (inter in intersections) {
                    val distToInterX = inter.x - car.posX
                    val distToInterZ = inter.z - car.posZ

                    if (isNorth && abs(distToInterX) < 5f && distToInterZ in 3f..20f && !inter.isNsGreen()) {
                        stoppedByLight = true; break
                    } else if (isSouth && abs(distToInterX) < 5f && distToInterZ in -20f..-3f && !inter.isNsGreen()) {
                        stoppedByLight = true; break
                    } else if (isEast && abs(distToInterZ) < 5f && distToInterX in 3f..20f && !inter.isEwGreen()) {
                        stoppedByLight = true; break
                    } else if (isWest && abs(distToInterZ) < 5f && distToInterX in -20f..-3f && !inter.isEwGreen()) {
                        stoppedByLight = true; break
                    }

                    // Smooth Left or Right turn when inside an intersection on Green!
                    if (cd <= 0f && abs(distToInterX) < 3.6f && abs(distToInterZ) < 3.6f) {
                        val atBoundary = (isNorth && inter.z >= 130f) ||
                            (isSouth && inter.z <= -95f) ||
                            (isEast && inter.x >= 110f) ||
                            (isWest && inter.x <= -110f)
                        val shouldTurn = atBoundary || ((i + inter.x.toInt() + inter.z.toInt()) % 2 == 0)
                        if (shouldTurn) {
                            val currentDir = when {
                                isNorth -> TravelDirection.NORTH
                                isSouth -> TravelDirection.SOUTH
                                isEast -> TravelDirection.EAST
                                else -> TravelDirection.WEST
                            }
                            val leftDir = when (currentDir) {
                                TravelDirection.NORTH -> TravelDirection.WEST
                                TravelDirection.WEST -> TravelDirection.SOUTH
                                TravelDirection.SOUTH -> TravelDirection.EAST
                                TravelDirection.EAST -> TravelDirection.NORTH
                            }
                            val rightDir = when (currentDir) {
                                TravelDirection.NORTH -> TravelDirection.EAST
                                TravelDirection.EAST -> TravelDirection.SOUTH
                                TravelDirection.SOUTH -> TravelDirection.WEST
                                TravelDirection.WEST -> TravelDirection.NORTH
                            }
                            // Pick valid turn staying inside city road grid (alternate left/right turns)
                            val preferRight = ((i + abs(inter.x.toInt())) % 2 == 0)
                            val candidates = if (preferRight) listOf(rightDir, leftDir) else listOf(leftDir, rightDir)
                            val chosenDir = candidates.firstOrNull { d ->
                                when (d) {
                                    TravelDirection.NORTH -> inter.z < 130f
                                    TravelDirection.SOUTH -> inter.z > -95f
                                    TravelDirection.EAST -> inter.x < 110f
                                    TravelDirection.WEST -> inter.x > -110f
                                }
                            }
                            if (chosenDir != null) {
                                val nextYaw = when (chosenDir) {
                                    TravelDirection.NORTH -> 0f
                                    TravelDirection.SOUTH -> 180f
                                    TravelDirection.EAST -> 90f
                                    TravelDirection.WEST -> 270f
                                }
                                carTargetYaw[car.id] = nextYaw
                                carTargetLaneAxis[car.id] = when (chosenDir) {
                                    TravelDirection.NORTH -> Pair(true, inter.x + 3.0f)
                                    TravelDirection.SOUTH -> Pair(true, inter.x - 3.0f)
                                    TravelDirection.EAST -> Pair(false, inter.z - 3.0f)
                                    TravelDirection.WEST -> Pair(false, inter.z + 3.0f)
                                }
                                carTurnCooldown[car.id] = 4.5f
                            }
                        } else {
                            carTurnCooldown[car.id] = 3.2f
                        }
                        break
                    }
                }
            }

            // Distance spacing to car ahead
            var blockedByOtherCar = false
            if (!stoppedByLight) {
                val fwdX = sin(Math.toRadians(car.yaw.toDouble())).toFloat()
                val fwdZ = cos(Math.toRadians(car.yaw.toDouble())).toFloat()
                for (other in internalObstacleList) {
                    if (other === car || other.isInUndergroundGarage) continue
                    val ox = other.posX - car.posX
                    val oz = other.posZ - car.posZ
                    val ahead = ox * fwdX + oz * fwdZ
                    val lateral = abs(ox * (-fwdZ) + oz * fwdX)
                    if (ahead in 1.0f..10.0f && lateral < 1.85f) {
                        blockedByOtherCar = true
                        break
                    }
                }
            }

            // Yield safely to player & player car
            val distToPlayer = car.checkCollision(playerPos.x, playerPos.z, margin = 4.2f) ||
                (playerCar != null && car.checkCollision(playerCar.posX, playerCar.posZ, margin = 4.8f))

            if (blockedByOtherCar && !stoppedByLight && !distToPlayer) {
                val st = (carStuckTimer[car.id] ?: 0f) + deltaSec
                carStuckTimer[car.id] = st
                if (st >= 2.2f) {
                    carStuckTimer[car.id] = 0f
                    carReverseTimer[car.id] = 1.0f
                    continue
                }
            } else {
                carStuckTimer[car.id] = 0f
            }

            val shouldStop = stoppedByLight || blockedByOtherCar || distToPlayer

            car.speed = if (shouldStop) {
                max(0f, car.speed - 24f * deltaSec)
            } else if (car.speed < cruiseSpeedMs) {
                min(cruiseSpeedMs, car.speed + 12f * deltaSec)
            } else {
                max(cruiseSpeedMs, car.speed - 10f * deltaSec)
            }

            val rad = Math.toRadians(car.yaw.toDouble())
            car.posX += sin(rad).toFloat() * car.speed * deltaSec
            car.posZ += cos(rad).toFloat() * car.speed * deltaSec

            for (b in buildings) {
                val pushed = b.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = 1.4f, bodyHeight = car.type.height)
                if (pushed != null) {
                    car.posX = pushed.first
                    car.posZ = pushed.second
                    carReverseTimer[car.id] = 0.9f
                }
            }

            // Wrap around paved city island boundary so AI cars stay off the beach and ocean
            if (car.posX > 202f) car.posX = -202f
            if (car.posX < -202f) car.posX = 202f
            if (car.posX > 15f) {
                if (car.posZ > 174f) car.posZ = -178f
                if (car.posZ < -180f) car.posZ = 174f
            } else {
                if (car.posZ > 180f) car.posZ = -182f
                if (car.posZ < -182f) car.posZ = 180f
            }
        }

        // 2.5. Update Forever-Looping Autonomous Human-Like Cyber Robots (drive cars 4-way, exit onto grass, return)
        for (robot in autonomousRobots) {
            robot.update(
                deltaSec = deltaSec,
                intersections = intersections,
                allObstacles = internalObstacleList,
                allStopActive = allStopActive,
                playerCar = playerCar,
                playerPos = playerPos,
                buildings = buildings
            )
        }

        // 2.8. Update Dedicated Grass-Walking Cyber Robots on Green Lawns
        for (grobot in grassRobots) {
            grobot.update(deltaSec, buildings, playerPos)
        }

        // 3. Update Park & Plaza Pedestrians (strictly off streets & away from player on road)
        for (ped in pedestrians) {
            ped.update(deltaSec, buildings, playerPos)
        }
    }

    /**
     * Manual control: switches all intersection phases to North-South Green.
     */
    fun setAllNsGreen() {
        manualControlActive = true
        allStopActive = false
        for (inter in intersections) {
            inter.phase = IntersectionPhase.NS_GREEN
            inter.timer = 0f
        }
    }

    /**
     * Manual control: switches all intersection phases to East-West Green.
     */
    fun setAllEwGreen() {
        manualControlActive = true
        allStopActive = false
        for (inter in intersections) {
            inter.phase = IntersectionPhase.EW_GREEN
            inter.timer = 0f
        }
    }

    /**
     * Manual control: switches all intersections to Yellow (10s warning).
     */
    fun setAllYellowWarning() {
        manualControlActive = false
        allStopActive = false
        for (inter in intersections) {
            inter.phase = if (inter.isEwGreen() || inter.isEwYellow()) IntersectionPhase.EW_YELLOW else IntersectionPhase.NS_YELLOW
            inter.timer = 0f
        }
    }

    /**
     * Manual control: emergency stop (all lights RED).
     */
    fun toggleEmergencyAllStop() {
        allStopActive = !allStopActive
        for (inter in intersections) {
            inter.phase = if (allStopActive) IntersectionPhase.ALL_STOP else IntersectionPhase.NS_GREEN
            inter.timer = 0f
        }
    }

    /**
     * Restores automatic green (100s) - yellow (10s) - red timer cycles.
     */
    fun setAutoCycle() {
        manualControlActive = false
        allStopActive = false
    }
}
