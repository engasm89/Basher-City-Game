package com.example.opencity.model

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Pedestrian NPC moving strictly inside parks, gardens, and inner plazas — NEVER crossing any street.
 */
data class NPC(
    val id: String,
    val name: String,
    var posX: Float,
    var posY: Float = 0f,
    var posZ: Float,
    var yaw: Float = 0f,
    var speed: Float = 1.4f, // m/s
    val shirtColor: FloatArray,
    val pantsColor: FloatArray,
    val waypoints: List<Vector3>,
    var currentWaypointIndex: Int = 0,
    var walkAnimTime: Float = 0f,
    val greetingArabic: String = "أهلاً بك في مدينتنا الجميلة!"
) {
    companion object {
        private val NS_ROADS = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
        private val EW_ROADS = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)

        /**
         * Returns true if (x, z) lies inside any city road or crossing zone.
         * Road asphalt half-width is 6.25m; marginFromCenter >= 9.0m keeps NPCs completely off asphalt and curbs.
         */
        fun isOnOrNearRoad(x: Float, z: Float, marginFromCenter: Float = 9.0f): Boolean {
            val hitsNs = NS_ROADS.any { rx ->
                val maxZ = if (rx == 0f || rx == -195f) 348f else 182f
                z <= maxZ + marginFromCenter && abs(x - rx) < marginFromCenter
            } || (z > 165f && abs(x - 345f) < marginFromCenter)
            if (hitsNs) return true
            val hitsEw = EW_ROADS.any { rz -> abs(z - rz) < marginFromCenter } ||
                abs(z - 256f) < marginFromCenter ||
                abs(z - 340f) < marginFromCenter
            return hitsEw
        }

        /**
         * Pushes a point (x, z) strictly off any road corridor to at least safeMargin from road center.
         */
        fun pushClearOfRoads(x: Float, z: Float, safeMargin: Float = 9.5f): Pair<Float, Float> {
            var cx = x
            var cz = z
            for (rx in NS_ROADS) {
                val maxZ = if (rx == 0f || rx == -195f) 348f else 182f
                if (cz <= maxZ + safeMargin) {
                    val dx = cx - rx
                    if (abs(dx) < safeMargin) {
                        cx = if (dx >= 0f) rx + safeMargin else rx - safeMargin
                    }
                }
            }
            if (cz > 165f && abs(cx - 345f) < safeMargin) {
                cx = if (cx >= 345f) 345f + safeMargin else 345f - safeMargin
            }
            for (rz in EW_ROADS) {
                val dz = cz - rz
                if (abs(dz) < safeMargin) {
                    cz = if (dz >= 0f) rz + safeMargin else rz - safeMargin
                }
            }
            for (vz in listOf(256f, 340f)) {
                val dz = cz - vz
                if (abs(dz) < safeMargin) {
                    cz = if (dz >= 0f) vz + safeMargin else vz - safeMargin
                }
            }
            return Pair(cx, cz)
        }
    }

    fun update(deltaSec: Float, buildings: List<Building> = emptyList(), playerPos: Vector3? = null) {
        // Hard safety: ensure this NPC never stands inside any street or road crossing anywhere in the city
        if (isOnOrNearRoad(posX, posZ, marginFromCenter = 9.0f)) {
            val safe = pushClearOfRoads(posX, posZ, safeMargin = 10.2f)
            posX = safe.first
            posZ = safe.second
        }

        // Keep away from the player in or near the middle of streets
        if (playerPos != null) {
            val dxP = posX - playerPos.x
            val dzP = posZ - playerPos.z
            val distSq = dxP * dxP + dzP * dzP
            val minClearance = if (isOnOrNearRoad(playerPos.x, playerPos.z, marginFromCenter = 11.5f)) 11.0f else 4.0f
            if (distSq < minClearance * minClearance) {
                val d = sqrt(distSq).coerceAtLeast(0.01f)
                val candX = posX + (dxP / d) * (speed * 2.5f * deltaSec)
                val candZ = posZ + (dzP / d) * (speed * 2.5f * deltaSec)
                if (!isOnOrNearRoad(candX, candZ, marginFromCenter = 9.0f) &&
                    buildings.none { it.resolveCollisionPush(candX, candZ, margin = 0.72f) != null }
                ) {
                    posX = candX
                    posZ = candZ
                    yaw = Math.toDegrees(atan2((candX - playerPos.x).toDouble(), (candZ - playerPos.z).toDouble())).toFloat()
                    walkAnimTime += deltaSec * 8f
                    return
                }
            }
        }

        if (waypoints.isEmpty()) return

        val target = waypoints[currentWaypointIndex]
        val dx = target.x - posX
        val dz = target.z - posZ
        val dist = sqrt(dx * dx + dz * dz)

        if (dist < 0.8f) {
            currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size
        } else {
            val angle = Math.toDegrees(atan2(dx.toDouble(), dz.toDouble())).toFloat()
            yaw = angle

            val rad = Math.toRadians(yaw.toDouble())
            var nextX = posX + (sin(rad) * speed * deltaSec).toFloat()
            var nextZ = posZ + (cos(rad) * speed * deltaSec).toFloat()

            // Solid street/crossing exclusion: NEVER step onto asphalt or cross any street
            if (isOnOrNearRoad(nextX, nextZ, marginFromCenter = 9.0f)) {
                val pushed = pushClearOfRoads(nextX, nextZ, safeMargin = 9.2f)
                nextX = pushed.first
                nextZ = pushed.second
                currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size
            }

            // Solid building wall protection: no NPC may ever clip inside any wall or garage
            var blockedByWall = false
            for (b in buildings) {
                val pushed = b.resolveCollisionPush(nextX, nextZ, margin = 0.72f)
                if (pushed != null) {
                    nextX = pushed.first
                    nextZ = pushed.second
                    blockedByWall = true
                }
            }

            // Re-verify after wall push that NPC wasn't pushed onto a street
            if (isOnOrNearRoad(nextX, nextZ, marginFromCenter = 8.8f)) {
                nextX = posX
                nextZ = posZ
                currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size
            }

            val movedDistSq = (nextX - posX) * (nextX - posX) + (nextZ - posZ) * (nextZ - posZ)
            posX = nextX
            posZ = nextZ
            if (blockedByWall && movedDistSq < 0.0001f) {
                currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size
            }

            walkAnimTime += deltaSec * 8f
        }
    }
}
