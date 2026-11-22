package com.example.opencity.model

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A closed road intersection waypoint on an autonomous robot's endless city loop,
 * optionally paired with a safe grass/plaza stopover where the robot pulls over,
 * steps OUT of its car, behaves like a human, then steps back IN and drives forever.
 */
data class RobotLoopNode(
    val roadX: Float,
    val roadZ: Float,
    val stopoverAtCoord: Float? = null, // Coordinate along the outgoing leg to pull over
    val grassSpotX: Float? = null,
    val grassSpotZ: Float? = null,
    val exploreOffsetX: Float = 2.8f,
    val exploreOffsetZ: Float = 3.2f,
    val activityArabic: String = ""
)

enum class RobotBehaviorState {
    DRIVING_LOOP,
    PULLING_OVER,
    EXITING_CAR,
    WALKING_TO_SPOT,
    HUMAN_ACTIVITY,
    RETURNING_TO_CAR,
    ENTERING_CAR
}

/**
 * Autonomous Human-Like Cyber Robot with its own vehicle:
 * - Loops around closed city avenues forever ("يظل يلف حول المدينة للأبد")
 * - Obeys intersection traffic lights & yields cleanly to other vehicles and the player
 * - Periodically pulls neatly onto the outer curb, exits its car, walks onto safe grass/plaza,
 *   strolls/inspects/rests/waves at the player like a human, returns to the car, climbs back
 *   into the driver seat, and resumes its infinite city loop.
 */
class AutonomousRobot(
    val id: String,
    val arabicName: String,
    val car: Vehicle,
    val visorColor: FloatArray,
    private val loopNodes: List<RobotLoopNode>,
    startNodeIndex: Int = 0,
    private val driveIntervalSeconds: Float = 26f
) {
    var state: RobotBehaviorState = RobotBehaviorState.DRIVING_LOOP
        private set

    var isInCar: Boolean = true
        private set

    var footX: Float = car.posX
    var footY: Float = 0f
    var footZ: Float = car.posZ
    var footYaw: Float = car.yaw
    var walkPhase: Float = 0f
    var wavePhase: Float = 0f
    var currentStatusArabic: String = "يقود السيارة حول المدينة 🔄"
        private set

    private var currentNodeIndex: Int = startNodeIndex % loopNodes.size
    private var stateTimer: Float = 0f
    private var driveElapsedSinceStop: Float = (startNodeIndex * 5.5f) % driveIntervalSeconds
    private var activeStopoverNode: RobotLoopNode? = null
    private var resolvedGrassSpotX: Float = 0f
    private var resolvedGrassSpotZ: Float = 0f
    private var subPhaseTimer: Float = 0f
    private var subStep: Int = 0
    private var pullOverTargetX: Float = 0f
    private var pullOverTargetZ: Float = 0f
    private var doorExitX: Float = 0f
    private var doorExitZ: Float = 0f
    private var resumeLaneX: Float = 0f
    private var resumeLaneZ: Float = 0f
    private var blockedStuckTimer: Float = 0f
    private var reverseRecoverTimer: Float = 0f
    private var reverseSteerSign: Float = 1f

    init {
        val dir = currentLegDirection()
        val node = loopNodes[currentNodeIndex]
        val lane = getLanePointForLeg(node.roadX, node.roadZ, dir, curbOffset = 0f)
        car.posX = lane.first
        car.posZ = lane.second
        car.yaw = directionToYaw(dir)
        car.speed = 9.5f
        footX = car.posX
        footZ = car.posZ
        footYaw = car.yaw
    }

    private fun nextNodeIndex(idx: Int = currentNodeIndex): Int = (idx + 1) % loopNodes.size

    private fun currentLegDirection(idx: Int = currentNodeIndex): TravelDirection {
        val a = loopNodes[idx]
        val b = loopNodes[nextNodeIndex(idx)]
        val dx = b.roadX - a.roadX
        val dz = b.roadZ - a.roadZ
        return if (abs(dx) >= abs(dz)) {
            if (dx >= 0f) TravelDirection.EAST else TravelDirection.WEST
        } else {
            if (dz >= 0f) TravelDirection.NORTH else TravelDirection.SOUTH
        }
    }

    private fun directionToYaw(dir: TravelDirection): Float = when (dir) {
        TravelDirection.NORTH -> 0f
        TravelDirection.SOUTH -> 180f
        TravelDirection.EAST -> 90f
        TravelDirection.WEST -> 270f
    }

    private fun getLanePointForLeg(
        roadX: Float,
        roadZ: Float,
        dir: TravelDirection,
        curbOffset: Float
    ): Pair<Float, Float> {
        val laneDist = 3.0f + curbOffset
        return when (dir) {
            TravelDirection.NORTH -> Pair(roadX + laneDist, roadZ)
            TravelDirection.SOUTH -> Pair(roadX - laneDist, roadZ)
            TravelDirection.EAST -> Pair(roadX, roadZ - laneDist)
            TravelDirection.WEST -> Pair(roadX, roadZ + laneDist)
        }
    }

    private fun getOuterDoorOffset(dir: TravelDirection): Pair<Float, Float> = when (dir) {
        TravelDirection.NORTH -> Pair(1.45f, 0f)
        TravelDirection.SOUTH -> Pair(-1.45f, 0f)
        TravelDirection.EAST -> Pair(0f, -1.45f)
        TravelDirection.WEST -> Pair(0f, 1.45f)
    }

    private fun normalizeAngleDelta(target: Float, current: Float): Float {
        var diff = (target - current) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return diff
    }

    private fun sanitizeGrassSpot(rawX: Float, rawZ: Float, buildings: List<Building>): Pair<Float, Float> {
        var sx = rawX
        var sz = rawZ
        if (NPC.isOnOrNearRoad(sx, sz, marginFromCenter = 9.2f)) {
            val safe = NPC.pushClearOfRoads(sx, sz, safeMargin = 9.6f)
            sx = safe.first
            sz = safe.second
        }
        for (b in buildings) {
            val pushed = b.resolveCollisionPush(sx, sz, margin = 1.0f)
            if (pushed != null && !NPC.isOnOrNearRoad(pushed.first, pushed.second, marginFromCenter = 9.0f)) {
                sx = pushed.first
                sz = pushed.second
            }
        }
        return Pair(sx, sz)
    }

    fun update(
        deltaSec: Float,
        intersections: List<Intersection>,
        allObstacles: List<Vehicle>,
        allStopActive: Boolean,
        playerCar: Vehicle?,
        playerPos: Vector3,
        buildings: List<Building>
    ) {
        if (deltaSec <= 0f || deltaSec.isNaN() || deltaSec.isInfinite()) return
        if (car.isPlayerDriving || car.isInUndergroundGarage) {
            // If the player takes a ride in this car or parks it underground, stay politely idle until free
            isInCar = false
            blockedStuckTimer = 0f
            reverseRecoverTimer = 0f
            wavePhase += deltaSec * 4f
            currentStatusArabic = if (car.isInUndergroundGarage) {
                "ينتظر سيارته بجانب الكراج 🅿️🤖"
            } else {
                "يراقبك وأنت تقود سيارته 👋"
            }
            return
        }

        when (state) {
            RobotBehaviorState.DRIVING_LOOP -> updateDrivingLoop(
                deltaSec, intersections, allObstacles, allStopActive, playerCar, playerPos, buildings
            )
            RobotBehaviorState.PULLING_OVER -> updatePullingOver(deltaSec, buildings)
            RobotBehaviorState.EXITING_CAR -> updateExitingCar(deltaSec, playerPos)
            RobotBehaviorState.WALKING_TO_SPOT -> updateWalkingToSpot(deltaSec, buildings, playerPos)
            RobotBehaviorState.HUMAN_ACTIVITY -> updateHumanActivity(deltaSec, buildings, playerPos)
            RobotBehaviorState.RETURNING_TO_CAR -> updateReturningToCar(deltaSec, buildings, playerPos)
            RobotBehaviorState.ENTERING_CAR -> updateEnteringCar(deltaSec)
        }
    }

    private fun updateDrivingLoop(
        deltaSec: Float,
        intersections: List<Intersection>,
        allObstacles: List<Vehicle>,
        allStopActive: Boolean,
        playerCar: Vehicle?,
        playerPos: Vector3,
        buildings: List<Building>
    ) {
        isInCar = true
        wavePhase = 0f
        driveElapsedSinceStop += deltaSec

        val legDir = currentLegDirection()
        val fromNode = loopNodes[currentNodeIndex]
        val toNode = loopNodes[nextNodeIndex()]
        val nextLegDir = currentLegDirection(nextNodeIndex())

        // Handle active Reverse & Steer maneuver (ورا + يمين/شمال) when clearing an obstacle
        if (reverseRecoverTimer > 0f) {
            reverseRecoverTimer = (reverseRecoverTimer - deltaSec).coerceAtLeast(0f)
            currentStatusArabic = "يرجع للوراء ويلف لتجاوز العائق 🤖🔄"
            car.steeringAngle = reverseSteerSign * 28f
            car.speed = -4.5f
            car.yaw = ((car.yaw - reverseSteerSign * 75f * deltaSec) % 360f + 360f) % 360f
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
            footX = car.posX
            footZ = car.posZ
            footYaw = car.yaw
            return
        }

        currentStatusArabic = "يقود السيارة قدام ويمين وشمال للأبد 🤖🚗"

        // Target coordinate along current leg, including corner lane offset of the NEXT leg so turns stay cleanly in lane
        val laneA = getLanePointForLeg(fromNode.roadX, fromNode.roadZ, legDir, 0f)
        val laneB = getLanePointForLeg(toNode.roadX, toNode.roadZ, nextLegDir, 0f)
        val targetX = when (legDir) {
            TravelDirection.NORTH, TravelDirection.SOUTH -> laneA.first
            TravelDirection.EAST, TravelDirection.WEST -> laneB.first
        }
        val targetZ = when (legDir) {
            TravelDirection.EAST, TravelDirection.WEST -> laneA.second
            TravelDirection.NORTH, TravelDirection.SOUTH -> laneB.second
        }

        // Check if it's time to pull over at this leg's grass stopover
        val stopCoord = fromNode.stopoverAtCoord
        if (stopCoord != null &&
            fromNode.grassSpotX != null &&
            fromNode.grassSpotZ != null &&
            driveElapsedSinceStop >= driveIntervalSeconds
        ) {
            val remainingToStop = when (legDir) {
                TravelDirection.NORTH -> stopCoord - car.posZ
                TravelDirection.SOUTH -> car.posZ - stopCoord
                TravelDirection.EAST -> stopCoord - car.posX
                TravelDirection.WEST -> car.posX - stopCoord
            }
            if (remainingToStop in 2.5f..18.0f) {
                activeStopoverNode = fromNode
                val curbLane = getLanePointForLeg(fromNode.roadX, fromNode.roadZ, legDir, curbOffset = 2.0f)
                pullOverTargetX = when (legDir) {
                    TravelDirection.NORTH, TravelDirection.SOUTH -> curbLane.first
                    TravelDirection.EAST, TravelDirection.WEST -> stopCoord
                }
                pullOverTargetZ = when (legDir) {
                    TravelDirection.EAST, TravelDirection.WEST -> curbLane.second
                    TravelDirection.NORTH, TravelDirection.SOUTH -> stopCoord
                }
                val stdLane = getLanePointForLeg(fromNode.roadX, fromNode.roadZ, legDir, curbOffset = 0f)
                resumeLaneX = stdLane.first
                resumeLaneZ = stdLane.second
                val cleanSpot = sanitizeGrassSpot(fromNode.grassSpotX, fromNode.grassSpotZ, buildings)
                resolvedGrassSpotX = cleanSpot.first
                resolvedGrassSpotZ = cleanSpot.second
                state = RobotBehaviorState.PULLING_OVER
                stateTimer = 0f
                blockedStuckTimer = 0f
                return
            }
        }

        val dxCorner = targetX - car.posX
        val dzCorner = targetZ - car.posZ
        val distToCorner = sqrt(dxCorner * dxCorner + dzCorner * dzCorner)

        if (distToCorner < 1.35f) {
            currentNodeIndex = nextNodeIndex()
            blockedStuckTimer = 0f
            return
        }

        val desiredYaw = Math.toDegrees(atan2(dxCorner.toDouble(), dzCorner.toDouble())).toFloat()
        val yawDelta = normalizeAngleDelta(desiredYaw, car.yaw)
        car.steeringAngle = yawDelta.coerceIn(-30f, 30f)
        car.yaw = ((car.yaw + yawDelta.coerceIn(-165f * deltaSec, 165f * deltaSec)) % 360f + 360f) % 360f

        var stoppedByLight = allStopActive

        // Traffic lights observance
        if (!stoppedByLight) {
            val isNorth = legDir == TravelDirection.NORTH
            val isSouth = legDir == TravelDirection.SOUTH
            val isEast = legDir == TravelDirection.EAST
            val isWest = legDir == TravelDirection.WEST
            for (inter in intersections) {
                val dix = inter.x - car.posX
                val diz = inter.z - car.posZ
                if (isNorth && abs(dix) < 5f && diz in 3f..18f && !inter.isNsGreen()) {
                    stoppedByLight = true; break
                } else if (isSouth && abs(dix) < 5f && diz in -18f..-3f && !inter.isNsGreen()) {
                    stoppedByLight = true; break
                } else if (isEast && abs(diz) < 5f && dix in 3f..18f && !inter.isEwGreen()) {
                    stoppedByLight = true; break
                } else if (isWest && abs(diz) < 5f && dix in -18f..-3f && !inter.isEwGreen()) {
                    stoppedByLight = true; break
                }
            }
        }

        var blockedByVehicle = false
        // Spacing to other cars ahead in same lane
        if (!stoppedByLight) {
            val fwdX = sin(Math.toRadians(car.yaw.toDouble())).toFloat()
            val fwdZ = cos(Math.toRadians(car.yaw.toDouble())).toFloat()
            for (other in allObstacles) {
                if (other === car || other.isInUndergroundGarage) continue
                val ox = other.posX - car.posX
                val oz = other.posZ - car.posZ
                val ahead = ox * fwdX + oz * fwdZ
                val lateral = abs(ox * (-fwdZ) + oz * fwdX)
                if (ahead in 1.0f..9.5f && lateral < 1.85f) {
                    blockedByVehicle = true
                    break
                }
            }
        }

        // Yield safely to player & player car
        val nearPlayerOrPlayerCar = car.checkCollision(playerPos.x, playerPos.z, margin = 4.2f) ||
            (playerCar != null && car.checkCollision(playerCar.posX, playerCar.posZ, margin = 5.0f))

        val shouldStop = stoppedByLight || blockedByVehicle || nearPlayerOrPlayerCar

        // If blocked by an empty obstacle/car (not a red light or nearby player on foot), trigger Reverse & Steer recovery
        if (blockedByVehicle && !stoppedByLight && !nearPlayerOrPlayerCar) {
            blockedStuckTimer += deltaSec
            if (blockedStuckTimer >= 1.8f) {
                blockedStuckTimer = 0f
                reverseRecoverTimer = 1.25f
                reverseSteerSign = if (yawDelta >= 0f) -1f else 1f
                return
            }
        } else {
            blockedStuckTimer = 0f
        }

        val targetSpeed = if (abs(yawDelta) > 22f) 6.8f else 10.6f
        car.speed = if (shouldStop) {
            max(0f, car.speed - 24f * deltaSec)
        } else if (car.speed < targetSpeed) {
            min(targetSpeed, car.speed + 12f * deltaSec)
        } else {
            max(targetSpeed, car.speed - 10f * deltaSec)
        }

        val rad = Math.toRadians(car.yaw.toDouble())
        car.posX += sin(rad).toFloat() * car.speed * deltaSec
        car.posZ += cos(rad).toFloat() * car.speed * deltaSec

        var hitBuilding = false
        for (b in buildings) {
            val pushed = b.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = 1.4f, bodyHeight = car.type.height)
            if (pushed != null) {
                car.posX = pushed.first
                car.posZ = pushed.second
                hitBuilding = true
            }
        }
        if (hitBuilding) {
            reverseRecoverTimer = 1.1f
            reverseSteerSign = if (yawDelta >= 0f) 1f else -1f
        }

        footX = car.posX
        footZ = car.posZ
        footYaw = car.yaw
    }

    private fun updatePullingOver(deltaSec: Float, buildings: List<Building>) {
        stateTimer += deltaSec
        currentStatusArabic = "يركن على جانب الطريق لينزل 🅿️"
        val dx = pullOverTargetX - car.posX
        val dz = pullOverTargetZ - car.posZ
        val dist = sqrt(dx * dx + dz * dz)
        val legDir = currentLegDirection()
        val parallelYaw = directionToYaw(legDir)

        if (dist < 0.85f || stateTimer > 5.0f) {
            car.posX = pullOverTargetX
            car.posZ = pullOverTargetZ
            car.yaw = parallelYaw
            car.speed = 0f
            val doorOffset = getOuterDoorOffset(legDir)
            var exitX = car.posX + doorOffset.first
            var exitZ = car.posZ + doorOffset.second
            for (b in buildings) {
                val pushed = b.resolveCollisionPush3D(exitX, 0f, exitZ, margin = 0.85f, bodyHeight = 1.8f)
                if (pushed != null) {
                    exitX = pushed.first
                    exitZ = pushed.second
                }
            }
            doorExitX = exitX
            doorExitZ = exitZ
            // Double-check grass destination stays building-clear
            activeStopoverNode?.let { n ->
                val clean = sanitizeGrassSpot(
                    n.grassSpotX ?: resolvedGrassSpotX,
                    n.grassSpotZ ?: resolvedGrassSpotZ,
                    buildings
                )
                resolvedGrassSpotX = clean.first
                resolvedGrassSpotZ = clean.second
            }
            state = RobotBehaviorState.EXITING_CAR
            stateTimer = 0f
            return
        }

        val targetYaw = Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
        val yawDelta = normalizeAngleDelta(targetYaw, car.yaw)
        car.yaw = ((car.yaw + yawDelta.coerceIn(-120f * deltaSec, 120f * deltaSec)) % 360f + 360f) % 360f
        val desiredSpeed = (dist * 1.4f).coerceIn(2.6f, 6.2f)
        car.speed += (desiredSpeed - car.speed) * min(1f, deltaSec * 4f)
        val rad = Math.toRadians(car.yaw.toDouble())
        car.posX += sin(rad).toFloat() * car.speed * deltaSec
        car.posZ += cos(rad).toFloat() * car.speed * deltaSec
        for (b in buildings) {
            val pushed = b.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = 1.4f, bodyHeight = car.type.height)
            if (pushed != null) {
                car.posX = pushed.first
                car.posZ = pushed.second
            }
        }
    }

    private fun updateExitingCar(deltaSec: Float, playerPos: Vector3) {
        stateTimer += deltaSec
        currentStatusArabic = "يفتح الباب وينزل من السيارة 🚪🤖"
        if (stateTimer >= 0.65f) {
            isInCar = false
            footX = doorExitX
            footZ = doorExitZ
            val dx = resolvedGrassSpotX - footX
            val dz = resolvedGrassSpotZ - footZ
            footYaw = Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
            walkPhase = 0f
            state = RobotBehaviorState.WALKING_TO_SPOT
            stateTimer = 0f
        } else {
            val dxp = playerPos.x - doorExitX
            val dzp = playerPos.z - doorExitZ
            if (dxp * dxp + dzp * dzp < 81f) {
                wavePhase += deltaSec * 5f
            }
        }
    }

    private fun updateWalkingToSpot(deltaSec: Float, buildings: List<Building>, playerPos: Vector3) {
        isInCar = false
        currentStatusArabic = activeStopoverNode?.activityArabic ?: "يمشي إلى الحديقة الخضراء 🌿"
        val dx = resolvedGrassSpotX - footX
        val dz = resolvedGrassSpotZ - footZ
        val dist = sqrt(dx * dx + dz * dz)

        checkPlayerWave(deltaSec, playerPos)

        if (dist < 0.7f || stateTimer > 8.5f) {
            state = RobotBehaviorState.HUMAN_ACTIVITY
            stateTimer = 0f
            subPhaseTimer = 0f
            subStep = 0
            return
        }

        stateTimer += deltaSec
        val targetYaw = Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
        val deltaYaw = normalizeAngleDelta(targetYaw, footYaw)
        footYaw = (footYaw + deltaYaw.coerceIn(-240f * deltaSec, 240f * deltaSec) + 360f) % 360f

        val walkSpeed = 2.0f
        val rad = Math.toRadians(footYaw.toDouble())
        var nx = footX + sin(rad).toFloat() * walkSpeed * deltaSec
        var nz = footZ + cos(rad).toFloat() * walkSpeed * deltaSec
        for (b in buildings) {
            val pushed = b.resolveCollisionPush(nx, nz, margin = 0.85f)
            if (pushed != null) {
                nx = pushed.first
                nz = pushed.second
            }
        }
        footX = nx
        footZ = nz
        walkPhase += deltaSec * 8.5f
    }

    private fun updateHumanActivity(deltaSec: Float, buildings: List<Building>, playerPos: Vector3) {
        isInCar = false
        stateTimer += deltaSec
        subPhaseTimer += deltaSec

        val nearPlayer = checkPlayerWave(deltaSec, playerPos)
        val node = activeStopoverNode
        val baseLabel = node?.activityArabic ?: "يستمتع بالهواء الطلق على العشب 🌿"

        if (nearPlayer) {
            currentStatusArabic = "يرحب بك ويلوّح بيده 👋🤖"
            val pYaw = Math.toDegrees(atan2((playerPos.x - footX).toDouble(), (playerPos.z - footZ).toDouble())).toFloat()
            val dYaw = normalizeAngleDelta(pYaw, footYaw)
            footYaw = (footYaw + dYaw.coerceIn(-210f * deltaSec, 210f * deltaSec) + 360f) % 360f
        } else {
            currentStatusArabic = baseLabel
            // Natural multi-step human exploration on the grass spot
            when (subStep) {
                0 -> {
                    // Stroll a few meters around the grass lawn
                    val ox = resolvedGrassSpotX + (node?.exploreOffsetX ?: 2.5f)
                    val oz = resolvedGrassSpotZ + (node?.exploreOffsetZ ?: 2.5f)
                    val safeDest = sanitizeGrassSpot(ox, oz, buildings)
                    if (stepFootToward(safeDest.first, safeDest.second, 1.45f, deltaSec, buildings) || subPhaseTimer > 3.2f) {
                        subStep = 1
                        subPhaseTimer = 0f
                    }
                }
                1 -> {
                    // Pause and look around admiring surroundings (buildings/trees/sky)
                    footYaw = (footYaw + 38f * deltaSec) % 360f
                    if (subPhaseTimer > 2.4f) {
                        subStep = 2
                        subPhaseTimer = 0f
                    }
                }
                2 -> {
                    // Walk across to opposite corner of the grass lawn
                    val ox = resolvedGrassSpotX - (node?.exploreOffsetX ?: 2.0f) * 0.6f
                    val oz = resolvedGrassSpotZ + (node?.exploreOffsetZ ?: 2.5f) * 0.5f
                    val safeDest = sanitizeGrassSpot(ox, oz, buildings)
                    if (stepFootToward(safeDest.first, safeDest.second, 1.35f, deltaSec, buildings) || subPhaseTimer > 3.0f) {
                        subStep = 3
                        subPhaseTimer = 0f
                    }
                }
                else -> {
                    // Stand calmly resting before heading back to car
                    val look = sin(subPhaseTimer.toDouble()).toFloat() * 24f * deltaSec
                    footYaw = (footYaw + look + 360f) % 360f
                }
            }
        }

        if (stateTimer >= 9.5f) {
            state = RobotBehaviorState.RETURNING_TO_CAR
            stateTimer = 0f
            wavePhase = 0f
        }
    }

    private fun updateReturningToCar(deltaSec: Float, buildings: List<Building>, playerPos: Vector3) {
        isInCar = false
        stateTimer += deltaSec
        currentStatusArabic = "يعود إلى سيارته ليركبها 🚶‍♂️🚗"
        checkPlayerWave(deltaSec, playerPos)

        if (stepFootToward(doorExitX, doorExitZ, 2.05f, deltaSec, buildings) || stateTimer > 8.5f) {
            footX = doorExitX
            footZ = doorExitZ
            val lookAtCar = Math.toDegrees(atan2((car.posX - footX).toDouble(), (car.posZ - footZ).toDouble())).toFloat()
            footYaw = lookAtCar
            state = RobotBehaviorState.ENTERING_CAR
            stateTimer = 0f
            wavePhase = 0f
        }
    }

    private fun updateEnteringCar(deltaSec: Float) {
        stateTimer += deltaSec
        currentStatusArabic = "يركب السيارة ويتحرك لإكمال جولته 🚗💨"
        if (stateTimer >= 0.65f) {
            isInCar = true
            driveElapsedSinceStop = 0f
            val legDir = currentLegDirection()
            // Re-center car gently onto its main traffic lane axis
            when (legDir) {
                TravelDirection.NORTH, TravelDirection.SOUTH -> car.posX = resumeLaneX
                TravelDirection.EAST, TravelDirection.WEST -> car.posZ = resumeLaneZ
            }
            car.yaw = directionToYaw(legDir)
            car.speed = 3.5f
            footX = car.posX
            footZ = car.posZ
            state = RobotBehaviorState.DRIVING_LOOP
            stateTimer = 0f
        }
    }

    private fun stepFootToward(
        targetX: Float,
        targetZ: Float,
        speed: Float,
        deltaSec: Float,
        buildings: List<Building>
    ): Boolean {
        val dx = targetX - footX
        val dz = targetZ - footZ
        val dist = sqrt(dx * dx + dz * dz)
        if (dist < 0.55f) return true

        val targetYaw = Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
        val deltaYaw = normalizeAngleDelta(targetYaw, footYaw)
        footYaw = (footYaw + deltaYaw.coerceIn(-250f * deltaSec, 250f * deltaSec) + 360f) % 360f

        val rad = Math.toRadians(footYaw.toDouble())
        var nx = footX + sin(rad).toFloat() * speed * deltaSec
        var nz = footZ + cos(rad).toFloat() * speed * deltaSec
        for (b in buildings) {
            val pushed = b.resolveCollisionPush(nx, nz, margin = 0.85f)
            if (pushed != null) {
                nx = pushed.first
                nz = pushed.second
            }
        }
        footX = nx
        footZ = nz
        walkPhase += deltaSec * 8.2f
        return false
    }

    private fun checkPlayerWave(deltaSec: Float, playerPos: Vector3): Boolean {
        val dxp = playerPos.x - footX
        val dzp = playerPos.z - footZ
        val near = (dxp * dxp + dzp * dzp) <= 11.5f * 11.5f
        if (near) {
            wavePhase += deltaSec * 7.5f
        } else {
            wavePhase = 0f
        }
        return near
    }

    companion object {
        /**
         * Builds the fleet of 6 autonomous Cyber Robots that loop around OpenCity 3D forever,
         * pulling over safely beside green parks and plazas to exit, act like humans, and return to drive.
         */
        fun createCityFleet(): List<AutonomousRobot> {
            return listOf(
                AutonomousRobot(
                    id = "robot_atlas",
                    arabicName = "أطلس (مستكشف الوسط)",
                    visorColor = floatArrayOf(0.05f, 0.95f, 1.0f, 1f),
                    startNodeIndex = 0,
                    driveIntervalSeconds = 18f,
                    car = Vehicle(
                        id = "robot_car_atlas",
                        type = VehicleType.SPORTS_CAR,
                        posX = -32f, posY = 0f, posZ = -20f,
                        yaw = 0f,
                        speed = 10f,
                        colorPrimary = floatArrayOf(0.10f, 0.72f, 0.96f, 1f),
                        colorSecondary = floatArrayOf(0.12f, 0.14f, 0.18f, 1f),
                        isAiControlled = true,
                        displayName = "سيارة الروبوت أطلس 🤖",
                        meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                        customTopSpeed = 290f,
                        underglowColor = floatArrayOf(0.05f, 0.95f, 1.0f, 1f)
                    ),
                    loopNodes = listOf(
                        RobotLoopNode(
                            roadX = -35f, roadZ = -30f,
                            stopoverAtCoord = 49f,
                            grassSpotX = -17.5f, grassSpotZ = 50f,
                            exploreOffsetX = 3.2f, exploreOffsetZ = 3.0f,
                            activityArabic = "يتمشى في الحديقة المركزية الخضراء 🌳🤖"
                        ),
                        RobotLoopNode(roadX = -35f, roadZ = 100f),
                        RobotLoopNode(
                            roadX = 35f, roadZ = 100f,
                            stopoverAtCoord = 50f,
                            grassSpotX = 17.5f, grassSpotZ = 50f,
                            exploreOffsetX = -2.8f, exploreOffsetZ = 2.6f,
                            activityArabic = "يتجول في حديقة وسط المدينة الهادئة 🌿🤖"
                        ),
                        RobotLoopNode(roadX = 35f, roadZ = -30f)
                    )
                ),
                AutonomousRobot(
                    id = "robot_nova",
                    arabicName = "نوفا (متسوقة البوليفارد)",
                    visorColor = floatArrayOf(0.88f, 0.35f, 1.0f, 1f),
                    startNodeIndex = 1,
                    driveIntervalSeconds = 21f,
                    car = Vehicle(
                        id = "robot_car_nova",
                        type = VehicleType.SPORTS_CAR,
                        posX = -72f, posY = 0f, posZ = 20f,
                        yaw = 0f,
                        speed = 10f,
                        colorPrimary = floatArrayOf(0.66f, 0.25f, 0.92f, 1f),
                        colorSecondary = floatArrayOf(0.96f, 0.96f, 0.98f, 1f),
                        isAiControlled = true,
                        displayName = "سيارة الروبوت نوفا 🤖",
                        meshStyle = CarMeshStyle.SUPER_SPORT,
                        customTopSpeed = 305f,
                        underglowColor = floatArrayOf(0.88f, 0.35f, 1.0f, 1f)
                    ),
                    loopNodes = listOf(
                        RobotLoopNode(
                            roadX = -75f, roadZ = -100f,
                            stopoverAtCoord = 50f,
                            grassSpotX = -55f, grassSpotZ = 50f,
                            exploreOffsetX = 3.0f, exploreOffsetZ = -3.0f,
                            activityArabic = "تستريح في الساحة التجارية الخضراء 🌿🤖"
                        ),
                        RobotLoopNode(roadX = -75f, roadZ = 135f),
                        RobotLoopNode(
                            roadX = -35f, roadZ = 135f,
                            stopoverAtCoord = -15f,
                            grassSpotX = -55f, grassSpotZ = -15f,
                            exploreOffsetX = -2.5f, exploreOffsetZ = 2.8f,
                            activityArabic = "تتجول في الحديقة الغربية الهادئة 🌿🚶‍♀️🤖"
                        ),
                        RobotLoopNode(roadX = -35f, roadZ = -100f)
                    )
                ),
                AutonomousRobot(
                    id = "robot_titan",
                    arabicName = "تايتان (حارس المدينة)",
                    visorColor = floatArrayOf(1.0f, 0.80f, 0.15f, 1f),
                    startNodeIndex = 2,
                    driveIntervalSeconds = 24f,
                    car = Vehicle(
                        id = "robot_car_titan",
                        type = VehicleType.SPORTS_CAR,
                        posX = 38f, posY = 0f, posZ = 40f,
                        yaw = 0f,
                        speed = 10f,
                        colorPrimary = floatArrayOf(0.96f, 0.72f, 0.12f, 1f),
                        colorSecondary = floatArrayOf(0.10f, 0.12f, 0.16f, 1f),
                        isAiControlled = true,
                        displayName = "سيارة الروبوت تايتان 🤖",
                        meshStyle = CarMeshStyle.MUSCLE_GT,
                        customTopSpeed = 295f,
                        underglowColor = floatArrayOf(1.0f, 0.80f, 0.15f, 1f)
                    ),
                    loopNodes = listOf(
                        RobotLoopNode(
                            roadX = 35f, roadZ = -60f,
                            stopoverAtCoord = 17.5f,
                            grassSpotX = 55f, grassSpotZ = 17.5f,
                            exploreOffsetX = 3.0f, exploreOffsetZ = 2.6f,
                            activityArabic = "يتأمل المدينة والأشجار على العشب 🌲🤖"
                        ),
                        RobotLoopNode(roadX = 35f, roadZ = 175f),
                        RobotLoopNode(
                            roadX = 115f, roadZ = 175f,
                            stopoverAtCoord = 50f,
                            grassSpotX = 95f, grassSpotZ = 50f,
                            exploreOffsetX = -2.8f, exploreOffsetZ = 2.8f,
                            activityArabic = "يأخذ استراحة قرب حديقة المستشفى 🏥🌿"
                        ),
                        RobotLoopNode(roadX = 115f, roadZ = -60f)
                    )
                ),
                AutonomousRobot(
                    id = "robot_spark",
                    arabicName = "سبارك (جوال المواصلات)",
                    visorColor = floatArrayOf(1.0f, 0.42f, 0.08f, 1f),
                    startNodeIndex = 0,
                    driveIntervalSeconds = 22f,
                    car = Vehicle(
                        id = "robot_car_spark",
                        type = VehicleType.SPORTS_CAR,
                        posX = -112f, posY = 0f, posZ = -40f,
                        yaw = 0f,
                        speed = 10f,
                        colorPrimary = floatArrayOf(1.0f, 0.42f, 0.06f, 1f),
                        colorSecondary = floatArrayOf(0.12f, 0.12f, 0.14f, 1f),
                        isAiControlled = true,
                        displayName = "سيارة الروبوت سبارك 🤖",
                        meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                        customTopSpeed = 300f,
                        underglowColor = floatArrayOf(1.0f, 0.45f, 0.08f, 1f)
                    ),
                    loopNodes = listOf(
                        RobotLoopNode(
                            roadX = -115f, roadZ = -140f,
                            stopoverAtCoord = 17.5f,
                            grassSpotX = -95f, grassSpotZ = 17.5f,
                            exploreOffsetX = 3.0f, exploreOffsetZ = 2.5f,
                            activityArabic = "يتنزه في الحديقة الغربية 🌳🤖"
                        ),
                        RobotLoopNode(roadX = -115f, roadZ = 100f),
                        RobotLoopNode(
                            roadX = 0f, roadZ = 100f,
                            stopoverAtCoord = 48f,
                            grassSpotX = -17.5f, grassSpotZ = 50f,
                            exploreOffsetX = -3.0f, exploreOffsetZ = 2.5f,
                            activityArabic = "يستمتع بالحديقة المركزية الخضراء 🌿🤖"
                        ),
                        RobotLoopNode(roadX = 0f, roadZ = -140f)
                    )
                ),
                AutonomousRobot(
                    id = "robot_zero",
                    arabicName = "زيرو (سائق الضواحي)",
                    visorColor = floatArrayOf(0.15f, 0.96f, 0.45f, 1f),
                    startNodeIndex = 2,
                    driveIntervalSeconds = 25f,
                    car = Vehicle(
                        id = "robot_car_zero",
                        type = VehicleType.SPORTS_CAR,
                        posX = 78f, posY = 0f, posZ = 10f,
                        yaw = 0f,
                        speed = 10f,
                        colorPrimary = floatArrayOf(0.14f, 0.80f, 0.45f, 1f),
                        colorSecondary = floatArrayOf(0.08f, 0.10f, 0.14f, 1f),
                        isAiControlled = true,
                        displayName = "سيارة الروبوت زيرو 🤖",
                        meshStyle = CarMeshStyle.SUPER_SPORT,
                        customTopSpeed = 290f,
                        underglowColor = floatArrayOf(0.15f, 0.96f, 0.45f, 1f)
                    ),
                    loopNodes = listOf(
                        RobotLoopNode(
                            roadX = 75f, roadZ = -140f,
                            stopoverAtCoord = -15f,
                            grassSpotX = 95f, grassSpotZ = -15f,
                            exploreOffsetX = 2.8f, exploreOffsetZ = 2.0f,
                            activityArabic = "يتجول بسلام في المروج الشمالية 🍃🤖"
                        ),
                        RobotLoopNode(roadX = 75f, roadZ = 135f),
                        RobotLoopNode(
                            roadX = 155f, roadZ = 135f,
                            stopoverAtCoord = 17.5f,
                            grassSpotX = 135f, grassSpotZ = 17.5f,
                            exploreOffsetX = -2.8f, exploreOffsetZ = 2.6f,
                            activityArabic = "يمارس المشي الصباحي على العشب 🏃‍♂️🤖"
                        ),
                        RobotLoopNode(roadX = 155f, roadZ = -140f)
                    )
                ),
                AutonomousRobot(
                    id = "robot_orion",
                    arabicName = "أوريون (زائر المارينا)",
                    visorColor = floatArrayOf(0.22f, 0.72f, 1.0f, 1f),
                    startNodeIndex = 0,
                    driveIntervalSeconds = 23f,
                    car = Vehicle(
                        id = "robot_car_orion",
                        type = VehicleType.SEDAN,
                        posX = 198f, posY = 0f, posZ = -80f,
                        yaw = 0f,
                        speed = 10f,
                        colorPrimary = floatArrayOf(0.92f, 0.95f, 0.99f, 1f),
                        colorSecondary = floatArrayOf(0.14f, 0.46f, 0.92f, 1f),
                        isAiControlled = true,
                        displayName = "سيارة الروبوت أوريون 🤖",
                        meshStyle = CarMeshStyle.SEDAN_MODERN,
                        customTopSpeed = 270f,
                        underglowColor = floatArrayOf(0.22f, 0.72f, 1.0f, 1f)
                    ),
                    loopNodes = listOf(
                        RobotLoopNode(
                            roadX = 195f, roadZ = -140f,
                            stopoverAtCoord = -20f,
                            grassSpotX = 210f, grassSpotZ = -20f,
                            exploreOffsetX = 2.8f, exploreOffsetZ = 3.5f,
                            activityArabic = "يتأمل كورنيش المارينا والعشب الشرقي ⛵🌊🤖"
                        ),
                        RobotLoopNode(roadX = 195f, roadZ = 175f),
                        RobotLoopNode(
                            roadX = 115f, roadZ = 175f,
                            stopoverAtCoord = 117.5f,
                            grassSpotX = 95f, grassSpotZ = 117.5f,
                            exploreOffsetX = -2.4f, exploreOffsetZ = 2.6f,
                            activityArabic = "يتفقد مروج الضواحي الخضراء 🌾🤖"
                        ),
                        RobotLoopNode(roadX = 115f, roadZ = -140f)
                    )
                )
            )
        }
    }
}
