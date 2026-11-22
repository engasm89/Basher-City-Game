package com.example.opencity.model

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Axis-aligned 3D collision box used for Compound 90 Avenue walls/gates
 * and Elevated Sky Metro viaduct piers, ticket booths, stair walls, and platform railings.
 */
data class WorldBoxCollider(
    val id: String,
    val centerX: Float,
    val centerZ: Float,
    val width: Float,
    val depth: Float,
    val minY: Float = 0f,
    val maxY: Float = 4f
) {
    fun overlaps3D(px: Float, py: Float, pz: Float, margin: Float, bodyHeight: Float = 1.75f): Boolean {
        if (py + bodyHeight <= minY + 0.05f || py >= maxY - 0.05f) return false
        return abs(px - centerX) < (width / 2f + margin) &&
            abs(pz - centerZ) < (depth / 2f + margin)
    }

    fun resolvePush3D(px: Float, py: Float, pz: Float, margin: Float, bodyHeight: Float = 1.75f): Pair<Float, Float>? {
        if (!overlaps3D(px, py, pz, margin, bodyHeight)) return null
        val dx = px - centerX
        val dz = pz - centerZ
        val overlapX = (width / 2f + margin) - abs(dx)
        val overlapZ = (depth / 2f + margin) - abs(dz)
        return if (overlapX < overlapZ) {
            val sign = if (dx >= 0f) 1f else -1f
            Pair(centerX + sign * (width / 2f + margin), pz)
        } else {
            val sign = if (dz >= 0f) 1f else -1f
            Pair(px, centerZ + sign * (depth / 2f + margin))
        }
    }
}

/**
 * «كومباوند 90 أفينيو» (90 Avenue Compound):
 * Gated residential luxury compound enclosing the city's villas, featuring:
 * - A Private Turquoise Sea Lagoon & Golden Sandy Beach INSIDE the compound (19..56, 192..238)
 * - An Olympic Club Swimming Pool INSIDE the compound (65..83, 197..229)
 * - Monumental Illuminated Arches & Security Gate Portals leaving every villa driveway & road 100% open
 */
object CompoundAvenue90 {

    const val COMPOUND_NAME_AR = "كومباوند 90 أفينيو"
    const val COMPOUND_GATE_X = 54.0f
    const val COMPOUND_GATE_Z = 185.5f

    // Private Compound Sea Lagoon INSIDE 90 Avenue Compound (East North Campus)
    const val SEA_MIN_X = 20.0f
    const val SEA_MAX_X = 56.0f
    const val SEA_MIN_Z = 192.0f
    const val SEA_MAX_Z = 238.0f

    // Sand beach surrounding the compound lagoon
    const val BEACH_MIN_X = 14.0f
    const val BEACH_MAX_X = 61.0f
    const val BEACH_MIN_Z = 187.5f
    const val BEACH_MAX_Z = 243.5f

    // Wooden jetty pier over compound lagoon (player stays above water while standing on pier)
    const val PIER_MIN_X = 44.5f
    const val PIER_MAX_X = 56.5f
    const val PIER_MIN_Z = 213.2f
    const val PIER_MAX_Z = 216.8f

    // Compound Club Olympic Swimming Pool INSIDE 90 Avenue Compound
    const val POOL_MIN_X = 65.0f
    const val POOL_MAX_X = 83.0f
    const val POOL_MIN_Z = 197.0f
    const val POOL_MAX_Z = 229.0f

    // Ground terrain holes fed into CityWorldMeshes.addGroundRectWithHoles so sea & pool drop genuinely below y=0
    val waterCutouts: List<FloatArray> = listOf(
        floatArrayOf(SEA_MIN_X, SEA_MAX_X, SEA_MIN_Z, SEA_MAX_Z),
        floatArrayOf(POOL_MIN_X, POOL_MAX_X, POOL_MIN_Z, POOL_MAX_Z)
    )

    val compoundVillaIds: Set<String> = setOf(
        "villa_player",
        "villa_palm",
        "villa_beach",
        "villa_waterfront_1",
        "villa_skyline",
        "villa_oasis",
        "villa_summit",
        "villa_pine",
        "villa_glass_modern",
        "villa_palace"
    )

    fun isInsideCompoundSea(px: Float, pz: Float): Boolean {
        val onPier = px in PIER_MIN_X..PIER_MAX_X && pz in PIER_MIN_Z..PIER_MAX_Z
        if (onPier) return false
        return px in (SEA_MIN_X + 0.35f)..(SEA_MAX_X - 0.35f) &&
            pz in (SEA_MIN_Z + 0.35f)..(SEA_MAX_Z - 0.35f)
    }

    fun isInsideCompoundPool(px: Float, pz: Float): Boolean {
        return px in (POOL_MIN_X + 0.35f)..(POOL_MAX_X - 0.35f) &&
            pz in (POOL_MIN_Z + 0.35f)..(POOL_MAX_Z - 0.35f)
    }

    fun isInsideCompoundZone(px: Float, pz: Float): Boolean {
        return (px in 11f..336f && pz in 184f..332f) ||
            (px in -188f..-106f && pz in 184f..332f)
    }

    /**
     * Generates wall spans [start..end] skipping open gate portals so driveways & avenues remain 100% open.
     */
    fun openWallSegments(minVal: Float, maxVal: Float, gates: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        val result = mutableListOf<Pair<Float, Float>>()
        var cursor = minVal
        for ((gMin, gMax) in gates.sortedBy { it.first }) {
            if (gMin > cursor + 0.8f) {
                result.add(Pair(cursor, min(gMin, maxVal)))
            }
            cursor = max(cursor, gMax)
            if (cursor >= maxVal) break
        }
        if (cursor < maxVal - 0.8f) {
            result.add(Pair(cursor, maxVal))
        }
        return result
    }

    val eastGateIntervals: List<Pair<Float, Float>> = listOf(
        Pair(42f, 64f),     // Main Sea, Pool & Villa_Oasis Gate
        Pair(126f, 146f),   // Villa Palm / Summit Portal
        Pair(210f, 230f),   // Villa Beach / Pine Portal
        Pair(292f, 312f)    // Villa Glass / Imperial Palace Portal
    )

    val westGateIntervals: List<Pair<Float, Float>> = listOf(
        Pair(-169f, -147f)  // Marina & Skyline Royal Estates Portal
    )

    val wallColliders: List<WorldBoxCollider> by lazy {
        val list = mutableListOf<WorldBoxCollider>()
        fun addWallSegX(idPrefix: String, x1: Float, x2: Float, z: Float) {
            val len = x2 - x1
            if (len < 0.8f) return
            list.add(WorldBoxCollider("${idPrefix}_${x1.toInt()}_${z.toInt()}", (x1 + x2) / 2f, z, len, 0.9f, 0f, 3.2f))
        }
        // East Compound Perimeter Walls (North Z=185.5, Mid-N Z=246.0, Mid-S Z=266.0, South Z=330.5)
        for (z in listOf(185.5f, 246.0f, 266.0f, 330.5f)) {
            for ((s, e) in openWallSegments(11.5f, 334.0f, eastGateIntervals)) {
                addWallSegX("ewall", s, e, z)
            }
        }
        // East Compound Side Walls (X=11.5 and X=334.0)
        list.add(WorldBoxCollider("ewall_left_n", 11.5f, 215.75f, 0.9f, 60.5f, 0f, 3.2f))
        list.add(WorldBoxCollider("ewall_left_s", 11.5f, 298.25f, 0.9f, 64.5f, 0f, 3.2f))
        list.add(WorldBoxCollider("ewall_right_n", 334.0f, 215.75f, 0.9f, 60.5f, 0f, 3.2f))
        list.add(WorldBoxCollider("ewall_right_s", 334.0f, 298.25f, 0.9f, 64.5f, 0f, 3.2f))

        // West Compound Wing Walls (X=-184..-108)
        for (z in listOf(185.5f, 246.0f, 266.0f, 330.5f)) {
            for ((s, e) in openWallSegments(-184.0f, -108.0f, westGateIntervals)) {
                addWallSegX("wwall", s, e, z)
            }
        }
        list.add(WorldBoxCollider("wwall_left_n", -184.0f, 215.75f, 0.9f, 60.5f, 0f, 3.2f))
        list.add(WorldBoxCollider("wwall_left_s", -184.0f, 298.25f, 0.9f, 64.5f, 0f, 3.2f))
        list.add(WorldBoxCollider("wwall_right_n", -108.0f, 215.75f, 0.9f, 60.5f, 0f, 3.2f))
        list.add(WorldBoxCollider("wwall_right_s", -108.0f, 298.25f, 0.9f, 64.5f, 0f, 3.2f))

        // Gate Pillars (left & right columns of main arches)
        for (gx in listOf(41.4f, 64.6f, 125.4f, 146.6f, 209.4f, 230.6f, 291.4f, 312.6f, -169.6f, -146.4f)) {
            for (gz in listOf(185.5f, 246.0f, 266.0f, 330.5f)) {
                list.add(WorldBoxCollider("gp_${gx.toInt()}_${gz.toInt()}", gx, gz, 1.4f, 1.4f, 0f, 6.5f))
            }
        }
        list
    }
}

enum class StairDirection {
    CLIMB_WEST,  // Foot at +X (Y=0) -> Top at -X (Y=10.5)
    CLIMB_SOUTH, // Foot at -Z (Y=0) -> Top at +Z (Y=10.5)
    CLIMB_EAST,  // Foot at -X (Y=0) -> Top at +X (Y=10.5)
    CLIMB_NORTH  // Foot at +Z (Y=0) -> Top at -Z (Y=10.5)
}

data class MetroStation(
    val id: String,
    val arabicName: String,
    val shortLabel: String,
    val subtitleArabic: String,
    val trackProgressMeters: Float,
    // Elevated Platform (Y = 10.5f)
    val platMinX: Float,
    val platMaxX: Float,
    val platMinZ: Float,
    val platMaxZ: Float,
    // Iconic 26m Station Tower Landmark beside platform
    val towerX: Float,
    val towerZ: Float,
    // Optional secondary top landing connecting stair to platform
    val landingMinX: Float = 0f,
    val landingMaxX: Float = 0f,
    val landingMinZ: Float = 0f,
    val landingMaxZ: Float = 0f,
    val hasLanding: Boolean = false,
    // Stand / Boarding position on Elevated Platform (Y = 10.5f)
    val boardPosX: Float,
    val boardPosZ: Float,
    val boardYaw: Float,
    // Straight 3D Staircase flight (Y: 0f -> 10.5f)
    val stairDirection: StairDirection,
    val stairMinX: Float,
    val stairMaxX: Float,
    val stairMinZ: Float,
    val stairMaxZ: Float,
    // Ground Turnstile Gate & Ticket Booth (Y = 0f)
    val turnstileX: Float,
    val turnstileZ: Float,
    val boothX: Float,
    val boothZ: Float,
    val groundEntryX: Float,
    val groundEntryZ: Float
) {
    fun isOnPlatform(px: Float, pz: Float, margin: Float = 0f): Boolean {
        val inMain = px in (platMinX - margin)..(platMaxX + margin) && pz in (platMinZ - margin)..(platMaxZ + margin)
        val inLanding = hasLanding && px in (landingMinX - margin)..(landingMaxX + margin) && pz in (landingMinZ - margin)..(landingMaxZ + margin)
        return inMain || inLanding
    }

    fun isOnStairFootprint(px: Float, pz: Float, margin: Float = 0.25f): Boolean {
        return px in (stairMinX - margin)..(stairMaxX + margin) &&
            pz in (stairMinZ - margin)..(stairMaxZ + margin)
    }

    fun computeStairElevation(px: Float, pz: Float): Float {
        val spanX = (stairMaxX - stairMinX).coerceAtLeast(0.1f)
        val spanZ = (stairMaxZ - stairMinZ).coerceAtLeast(0.1f)
        val t = when (stairDirection) {
            StairDirection.CLIMB_WEST -> (stairMaxX - px) / spanX
            StairDirection.CLIMB_EAST -> (px - stairMinX) / spanX
            StairDirection.CLIMB_SOUTH -> (pz - stairMinZ) / spanZ
            StairDirection.CLIMB_NORTH -> (stairMaxZ - pz) / spanZ
        }.coerceIn(0f, 1f)
        return t * SkyMetroSystem.DECK_SURFACE_Y
    }

    fun isNearTurnstile(px: Float, py: Float, pz: Float, radius: Float = 4.8f): Boolean {
        if (py > 2.8f) return false
        val dx = px - turnstileX
        val dz = pz - turnstileZ
        return dx * dx + dz * dz <= radius * radius
    }
}

data class TrackPose(
    val x: Float,
    val y: Float,
    val z: Float,
    val yaw: Float
)

data class MetroSeat(
    val id: String,
    val shortLabel: String,
    val arabicName: String,
    val localX: Float,
    val localZ: Float,
    val facingYawOffset: Float
)

data class ElectricMetroTrain(
    val id: String,
    val arabicName: String,
    var progressMeters: Float,
    var speedMs: Float = 0f,
    var stationStopTimer: Float = 0f,
    var stoppedAtStationId: String? = null,
    var requestedStopStationId: String? = null,
    var doorsOpenFactor: Float = 0f,
    var currentPosX: Float = 0f,
    var currentPosZ: Float = 0f,
    var currentYaw: Float = 90f,
    val accentColor: FloatArray
) {
    override fun equals(other: Any?): Boolean = other is ElectricMetroTrain && other.id == id
    override fun hashCode(): Int = id.hashCode()
}

/**
 * Full Outer-City Elevated Electric Sky Metro System («مترو المدينة الكهربائي المعلق»):
 * - Wraps around the ENTIRE city perimeter through the air at Y = 10.5m along a 2,328m closed ring:
 *   North: Z=-206 (X: -222..374), East: X=374 (Z: -206..362), South: Z=362 (X: 374..-222), West: X=-222 (Z: 362..-206)
 * - Strictly routed through open green meadow outer perimeter so ZERO streets, houses, villas, pools, or malls are touched!
 * - 4 Far-Apart Iconic Station Towers (~580m apart) with wide open plazas, 28-step walkable stairs, and 5 EGP turnstile gates
 * - 4 Articulated 3-Car Electric Trains with destination stop request support
 */
class SkyMetroSystem {

    companion object {
        const val DECK_SURFACE_Y = 10.5f
        const val DECK_THICKNESS = 1.0f
        const val TRACK_RAIL_Y = 10.75f
        const val CATENARY_WIRE_Y = 15.2f
        const val TICKET_PRICE_EGP = 5

        // Full Outer-City Sky Loop Bounds (strictly outside all city streets -203.75..353.75 x -188.75..348.75!)
        const val LOOP_MIN_X = -222.0f
        const val LOOP_MAX_X = 374.0f
        const val LOOP_MIN_Z = -206.0f
        const val LOOP_MAX_Z = 362.0f

        const val SEG_NORTH_LEN = LOOP_MAX_X - LOOP_MIN_X // 596m
        const val SEG_EAST_LEN = LOOP_MAX_Z - LOOP_MIN_Z  // 568m
        const val SEG_SOUTH_LEN = LOOP_MAX_X - LOOP_MIN_X // 596m
        const val SEG_WEST_LEN = LOOP_MAX_Z - LOOP_MIN_Z  // 568m
        const val LOOP_PERIMETER = SEG_NORTH_LEN + SEG_EAST_LEN + SEG_SOUTH_LEN + SEG_WEST_LEN // 2328m

        const val CRUISE_SPEED_MS = 32.0f
        const val STATION_DWELL_SECONDS = 5.0f

        val CAR_SEATS: List<MetroSeat> = listOf(
            MetroSeat("seat_1", "كرسي 1 🪑", "كرسي 1 (يسار أمام)", localX = -0.88f, localZ = 2.2f, facingYawOffset = 90f),
            MetroSeat("seat_2", "كرسي 2 🪑", "كرسي 2 (يسار وسط)", localX = -0.88f, localZ = 0.2f, facingYawOffset = 90f),
            MetroSeat("seat_3", "كرسي 3 🪑", "كرسي 3 (يسار خلف)", localX = -0.88f, localZ = -1.8f, facingYawOffset = 90f),
            MetroSeat("seat_4", "كرسي 4 🪑", "كرسي 4 (يمين أمام)", localX = 0.88f, localZ = 2.2f, facingYawOffset = 270f),
            MetroSeat("seat_5", "كرسي 5 🪑", "كرسي 5 (يمين وسط)", localX = 0.88f, localZ = 0.2f, facingYawOffset = 270f),
            MetroSeat("seat_6", "كرسي 6 🪑", "كرسي 6 (يمين خلف)", localX = 0.88f, localZ = -1.8f, facingYawOffset = 270f)
        )

        fun computeMetroSeatWorldPose(train: ElectricMetroTrain, seat: MetroSeat): TrackPose {
            val rad = Math.toRadians(train.currentYaw.toDouble())
            val cosY = kotlin.math.cos(rad).toFloat()
            val sinY = kotlin.math.sin(rad).toFloat()
            val wx = train.currentPosX + seat.localX * cosY + seat.localZ * sinY
            val wz = train.currentPosZ - seat.localX * sinY + seat.localZ * cosY
            val wy = DECK_SURFACE_Y + 1.04f
            val seatYaw = ((train.currentYaw + seat.facingYawOffset) % 360f + 360f) % 360f
            return TrackPose(wx, wy, wz, seatYaw)
        }

        val STATIONS: List<MetroStation> = listOf(
            // 1. North Iconic Station Tower (~596m wide open northern green meadow, completely clear of all streets/houses)
            MetroStation(
                id = "station_north",
                arabicName = "برج محطة الشمال (المساحات المفتوحة)",
                shortLabel = "الشمال 🧭",
                subtitleArabic = "ساحة مفتوحة شمال المدينة بعيداً عن الشوارع والبيوت",
                trackProgressMeters = 262.0f, // North leg Z=-206, X=40
                platMinX = 30.0f, platMaxX = 50.0f,
                platMinZ = -215.5f, platMaxZ = -209.5f,
                towerX = 26.5f, towerZ = -212.5f,
                hasLanding = false,
                boardPosX = 40.0f, boardPosZ = -212.5f, boardYaw = 0f,
                stairDirection = StairDirection.CLIMB_WEST,
                stairMinX = 50.0f, stairMaxX = 66.0f,
                stairMinZ = -214.5f, stairMaxZ = -210.5f,
                turnstileX = 67.3f, turnstileZ = -212.5f,
                boothX = 67.3f, boothZ = -216.2f,
                groundEntryX = 69.8f, groundEntryZ = -212.5f
            ),
            // 2. East Iconic Station Tower (Open east meadow beyond Motor City & Airport, zero streets/buildings!)
            MetroStation(
                id = "station_east",
                arabicName = "برج محطة الشرق (الساحة الشرقية)",
                shortLabel = "الشرق 🌅",
                subtitleArabic = "أرض خضراء واسعة شرق مدينة المحركات والمطار",
                trackProgressMeters = SEG_NORTH_LEN + 271.0f, // 867m -> East leg X=374, Z=65
                platMinX = 377.5f, platMaxX = 383.5f,
                platMinZ = 55.0f, platMaxZ = 75.0f,
                towerX = 380.5f, towerZ = 51.5f,
                hasLanding = false,
                boardPosX = 380.5f, boardPosZ = 65.0f, boardYaw = 270f,
                stairDirection = StairDirection.CLIMB_WEST,
                stairMinX = 383.5f, stairMaxX = 399.5f,
                stairMinZ = 63.0f, stairMaxZ = 67.0f,
                turnstileX = 400.8f, turnstileZ = 65.0f,
                boothX = 400.8f, boothZ = 61.2f,
                groundEntryX = 403.0f, groundEntryZ = 65.0f
            ),
            // 3. South Iconic Station Tower (Open southern meadow behind 90 Avenue Compound, completely off all roads!)
            MetroStation(
                id = "station_south",
                arabicName = "برج محطة الجنوب (خلف 90 أفينيو)",
                shortLabel = "الجنوب 🌴",
                subtitleArabic = "ساحة مفتوحة جنوب كومباوند 90 أفينيو بلا زحام",
                trackProgressMeters = SEG_NORTH_LEN + SEG_EAST_LEN + 234.0f, // 1398m -> South leg Z=362, X=140
                platMinX = 130.0f, platMaxX = 150.0f,
                platMinZ = 365.5f, platMaxZ = 371.5f,
                towerX = 126.5f, towerZ = 368.5f,
                hasLanding = false,
                boardPosX = 140.0f, boardPosZ = 368.5f, boardYaw = 180f,
                stairDirection = StairDirection.CLIMB_NORTH,
                stairMinX = 138.0f, stairMaxX = 142.0f,
                stairMinZ = 371.5f, stairMaxZ = 387.5f,
                turnstileX = 140.0f, turnstileZ = 388.8f,
                boothX = 136.2f, boothZ = 388.8f,
                groundEntryX = 140.0f, groundEntryZ = 391.0f
            ),
            // 4. West Iconic Station Tower (Wide open western green meadow, far from all buildings and streets!)
            MetroStation(
                id = "station_west",
                arabicName = "برج محطة الغرب (الحدائق الواسعة)",
                shortLabel = "الغرب 🌊",
                subtitleArabic = "مروج خضراء مفتوحة غرب المدينة والشاطئ",
                trackProgressMeters = SEG_NORTH_LEN + SEG_EAST_LEN + SEG_SOUTH_LEN + 322.0f, // 2082m -> West leg X=-222, Z=40
                platMinX = -231.5f, platMaxX = -225.5f,
                platMinZ = 30.0f, platMaxZ = 50.0f,
                towerX = -228.5f, towerZ = 26.5f,
                hasLanding = false,
                boardPosX = -228.5f, boardPosZ = 40.0f, boardYaw = 90f,
                stairDirection = StairDirection.CLIMB_EAST,
                stairMinX = -247.5f, stairMaxX = -231.5f,
                stairMinZ = 38.0f, stairMaxZ = 42.0f,
                turnstileX = -248.8f, turnstileZ = 40.0f,
                boothX = -248.8f, boothZ = 36.2f,
                groundEntryX = -251.0f, groundEntryZ = 40.0f
            )
        )

        fun sampleTrackPose(distanceMeters: Float): TrackPose {
            if (distanceMeters.isNaN() || distanceMeters.isInfinite()) {
                return TrackPose(LOOP_MIN_X, DECK_SURFACE_Y, LOOP_MIN_Z, 90f)
            }
            var d = distanceMeters % LOOP_PERIMETER
            if (d < 0f) d += LOOP_PERIMETER

            return when {
                d < SEG_NORTH_LEN -> {
                    TrackPose(
                        x = LOOP_MIN_X + d,
                        y = DECK_SURFACE_Y,
                        z = LOOP_MIN_Z,
                        yaw = 90f
                    )
                }
                d < SEG_NORTH_LEN + SEG_EAST_LEN -> {
                    val local = d - SEG_NORTH_LEN
                    TrackPose(
                        x = LOOP_MAX_X,
                        y = DECK_SURFACE_Y,
                        z = LOOP_MIN_Z + local,
                        yaw = 0f
                    )
                }
                d < SEG_NORTH_LEN + SEG_EAST_LEN + SEG_SOUTH_LEN -> {
                    val local = d - SEG_NORTH_LEN - SEG_EAST_LEN
                    TrackPose(
                        x = LOOP_MAX_X - local,
                        y = DECK_SURFACE_Y,
                        z = LOOP_MAX_Z,
                        yaw = 270f
                    )
                }
                else -> {
                    val local = d - SEG_NORTH_LEN - SEG_EAST_LEN - SEG_SOUTH_LEN
                    TrackPose(
                        x = LOOP_MIN_X,
                        y = DECK_SURFACE_Y,
                        z = LOOP_MAX_Z - local,
                        yaw = 180f
                    )
                }
            }
        }

        /**
         * Static 3D collision geometry for outer open-field viaduct piers, station tower cores,
         * ground ticket booths, stair side balustrades, and upper platform safety glass walls.
         */
        val staticColliders: List<WorldBoxCollider> by lazy {
            val cols = mutableListOf<WorldBoxCollider>()

            // 1. Straddle portal support columns in open green grass outer perimeter (+-4.8m from track)
            for (x in -200..355 step 45) {
                if (abs(x - 0) < 12 || abs(x - 40) < 22 || abs(x - 140) < 22) continue
                cols.add(WorldBoxCollider("mp_n_${x}_a", x.toFloat(), LOOP_MIN_Z - 4.8f, 1.0f, 1.0f, 0f, 10.6f))
                cols.add(WorldBoxCollider("mp_n_${x}_b", x.toFloat(), LOOP_MIN_Z + 4.8f, 1.0f, 1.0f, 0f, 10.6f))
                cols.add(WorldBoxCollider("mp_s_${x}_a", x.toFloat(), LOOP_MAX_Z - 4.8f, 1.0f, 1.0f, 0f, 10.6f))
                cols.add(WorldBoxCollider("mp_s_${x}_b", x.toFloat(), LOOP_MAX_Z + 4.8f, 1.0f, 1.0f, 0f, 10.6f))
            }
            for (z in -185..345 step 45) {
                if (abs(z - 0) < 12 || abs(z - 40) < 22 || abs(z - 65) < 22) continue
                cols.add(WorldBoxCollider("mp_w_${z}_a", LOOP_MIN_X - 4.8f, z.toFloat(), 1.0f, 1.0f, 0f, 10.6f))
                cols.add(WorldBoxCollider("mp_w_${z}_b", LOOP_MIN_X + 4.8f, z.toFloat(), 1.0f, 1.0f, 0f, 10.6f))
                cols.add(WorldBoxCollider("mp_e_${z}_a", LOOP_MAX_X - 4.8f, z.toFloat(), 1.0f, 1.0f, 0f, 10.6f))
                cols.add(WorldBoxCollider("mp_e_${z}_b", LOOP_MAX_X + 4.8f, z.toFloat(), 1.0f, 1.0f, 0f, 10.6f))
            }

            // 2. Station ground Ticket Booths, Station Tower base, Stair guardrails & Platform safety glass
            for (st in STATIONS) {
                cols.add(WorldBoxCollider("${st.id}_booth", st.boothX, st.boothZ, 2.8f, 2.6f, 0f, 3.6f))
                cols.add(WorldBoxCollider("${st.id}_tower", st.towerX, st.towerZ, 3.8f, 3.8f, 0f, 26.0f))

                // Stair side guard walls so player cannot walk sideways off elevated stairs mid-air
                when (st.stairDirection) {
                    StairDirection.CLIMB_WEST, StairDirection.CLIMB_EAST -> {
                        val len = st.stairMaxX - st.stairMinX
                        val cx = (st.stairMinX + st.stairMaxX) / 2f
                        cols.add(WorldBoxCollider("${st.id}_sw_n", cx, st.stairMinZ - 0.35f, len, 0.45f, 0.2f, 12.2f))
                        cols.add(WorldBoxCollider("${st.id}_sw_s", cx, st.stairMaxZ + 0.35f, len, 0.45f, 0.2f, 12.2f))
                    }
                    StairDirection.CLIMB_SOUTH, StairDirection.CLIMB_NORTH -> {
                        val len = st.stairMaxZ - st.stairMinZ
                        val cz = (st.stairMinZ + st.stairMaxZ) / 2f
                        cols.add(WorldBoxCollider("${st.id}_sw_w", st.stairMinX - 0.35f, cz, 0.45f, len, 0.2f, 12.2f))
                        cols.add(WorldBoxCollider("${st.id}_sw_e", st.stairMaxX + 0.35f, cz, 0.45f, len, 0.2f, 12.2f))
                    }
                }

                // Upper Platform safety glass walls (Y = 10.5..12.6) keeping player safely on sky platform
                val pw = st.platMaxX - st.platMinX
                val pd = st.platMaxZ - st.platMinZ
                val pcx = (st.platMinX + st.platMaxX) / 2f
                val pcz = (st.platMinZ + st.platMaxZ) / 2f
                when (st.id) {
                    "station_north" -> {
                        // Platform sits North of Z=-206; stair enters at East face X=50 (Z=-214.5..-210.5)
                        cols.add(WorldBoxCollider("${st.id}_rail_s", pcx, st.platMaxZ - 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_n", pcx, st.platMinZ + 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_w", st.platMinX + 0.2f, pcz, 0.4f, pd, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_en", st.platMaxX - 0.2f, (st.platMinZ + st.stairMinZ) / 2f, 0.4f, (st.stairMinZ - st.platMinZ).coerceAtLeast(0.2f), 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_es", st.platMaxX - 0.2f, (st.stairMaxZ + st.platMaxZ) / 2f, 0.4f, (st.platMaxZ - st.stairMaxZ).coerceAtLeast(0.2f), 10.5f, 12.6f))
                    }
                    "station_east" -> {
                        // Platform sits East of X=374; stair enters at East face X=383.5 (Z=63..67)
                        cols.add(WorldBoxCollider("${st.id}_rail_w", st.platMinX + 0.2f, pcz, 0.4f, pd, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_n", pcx, st.platMinZ + 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_s", pcx, st.platMaxZ - 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_en", st.platMaxX - 0.2f, (st.platMinZ + st.stairMinZ) / 2f, 0.4f, (st.stairMinZ - st.platMinZ).coerceAtLeast(0.2f), 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_es", st.platMaxX - 0.2f, (st.stairMaxZ + st.platMaxZ) / 2f, 0.4f, (st.platMaxZ - st.stairMaxZ).coerceAtLeast(0.2f), 10.5f, 12.6f))
                    }
                    "station_south" -> {
                        // Platform sits South of Z=362; stair enters at South face Z=371.5 (X=138..142)
                        cols.add(WorldBoxCollider("${st.id}_rail_n", pcx, st.platMinZ + 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_w", st.platMinX + 0.2f, pcz, 0.4f, pd, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_e", st.platMaxX - 0.2f, pcz, 0.4f, pd, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_sw", (st.platMinX + st.stairMinX) / 2f, st.platMaxZ - 0.2f, (st.stairMinX - st.platMinX).coerceAtLeast(0.2f), 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_se", (st.stairMaxX + st.platMaxX) / 2f, st.platMaxZ - 0.2f, (st.platMaxX - st.stairMaxX).coerceAtLeast(0.2f), 0.4f, 10.5f, 12.6f))
                    }
                    "station_west" -> {
                        // Platform sits West of X=-222; stair enters at West face X=-231.5 (Z=38..42)
                        cols.add(WorldBoxCollider("${st.id}_rail_e", st.platMaxX - 0.2f, pcz, 0.4f, pd, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_n", pcx, st.platMinZ + 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_s", pcx, st.platMaxZ - 0.2f, pw, 0.4f, 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_wn", st.platMinX + 0.2f, (st.platMinZ + st.stairMinZ) / 2f, 0.4f, (st.stairMinZ - st.platMinZ).coerceAtLeast(0.2f), 10.5f, 12.6f))
                        cols.add(WorldBoxCollider("${st.id}_rail_ws", st.platMinX + 0.2f, (st.stairMaxZ + st.platMaxZ) / 2f, 0.4f, (st.platMaxZ - st.stairMaxZ).coerceAtLeast(0.2f), 10.5f, 12.6f))
                    }
                }
            }
            cols
        }
    }

    val trains: List<ElectricMetroTrain> = listOf(
        ElectricMetroTrain(
            id = "metro_express_1",
            arabicName = "قطار المترو الكهربائي 1 (الخط الأزرق)",
            progressMeters = 262.0f, // Starts docked at North Tower Station
            stationStopTimer = STATION_DWELL_SECONDS,
            stoppedAtStationId = "station_north",
            doorsOpenFactor = 1f,
            accentColor = floatArrayOf(0.04f, 0.78f, 0.98f, 1f) // Electric Cyan
        ),
        ElectricMetroTrain(
            id = "metro_express_2",
            arabicName = "قطار المترو الكهربائي 2 (الخط الذهبي)",
            progressMeters = 867.0f, // Starts docked at East Tower Station
            stationStopTimer = STATION_DWELL_SECONDS,
            stoppedAtStationId = "station_east",
            doorsOpenFactor = 1f,
            accentColor = floatArrayOf(0.98f, 0.78f, 0.14f, 1f) // Royal Gold
        ),
        ElectricMetroTrain(
            id = "metro_express_3",
            arabicName = "قطار المترو الكهربائي 3 (الخط الزمردي)",
            progressMeters = 1398.0f, // Starts docked at South Tower Station
            stationStopTimer = STATION_DWELL_SECONDS,
            stoppedAtStationId = "station_south",
            doorsOpenFactor = 1f,
            accentColor = floatArrayOf(0.16f, 0.92f, 0.56f, 1f) // Emerald Glow
        ),
        ElectricMetroTrain(
            id = "metro_express_4",
            arabicName = "قطار المترو الكهربائي 4 (الخط المرجاني)",
            progressMeters = 2082.0f, // Starts docked at West Tower Station
            stationStopTimer = STATION_DWELL_SECONDS,
            stoppedAtStationId = "station_west",
            doorsOpenFactor = 1f,
            accentColor = floatArrayOf(0.96f, 0.42f, 0.24f, 1f) // Vibrant Orange
        )
    )

    // Thread-safe set accessed from both UI thread and GL render thread
    val unlockedStationGates = java.util.concurrent.CopyOnWriteArraySet<String>()

    init {
        for (train in trains) {
            val pose = sampleTrackPose(train.progressMeters)
            train.currentPosX = pose.x
            train.currentPosZ = pose.z
            train.currentYaw = pose.yaw
        }
    }

    fun update(deltaSec: Float) {
        if (deltaSec.isNaN() || deltaSec.isInfinite()) return
        val dt = deltaSec.coerceIn(0.001f, 0.08f)
        for (train in trains) {
            if (train.stationStopTimer > 0f) {
                train.stationStopTimer -= dt
                train.speedMs = 0f
                train.doorsOpenFactor = min(1f, train.doorsOpenFactor + dt * 3.5f)
                if (train.stationStopTimer <= 0f) {
                    train.stationStopTimer = 0f
                    train.stoppedAtStationId = null
                    train.progressMeters = (train.progressMeters + 1.8f) % LOOP_PERIMETER
                }
            } else {
                train.doorsOpenFactor = max(0f, train.doorsOpenFactor - dt * 4.0f)
                val nextStation = findNextStationAhead(train.progressMeters)
                val distToNext = nextStation?.let {
                    val raw = it.trackProgressMeters - train.progressMeters
                    if (raw >= 0f) raw else raw + LOOP_PERIMETER
                } ?: 999f

                val targetSpeed = when {
                    distToNext < 26f -> max(6.0f, CRUISE_SPEED_MS * (distToNext / 26f))
                    else -> CRUISE_SPEED_MS
                }
                train.speedMs += (targetSpeed - train.speedMs) * min(1f, dt * 3.2f)

                val prevProgress = train.progressMeters
                val nextProgress = (prevProgress + train.speedMs * dt) % LOOP_PERIMETER
                train.progressMeters = nextProgress

                for (st in STATIONS) {
                    val crossed = if (nextProgress >= prevProgress) {
                        st.trackProgressMeters in prevProgress..nextProgress
                    } else {
                        st.trackProgressMeters >= prevProgress || st.trackProgressMeters <= nextProgress
                    }
                    if (crossed && distToNext < 36f) {
                        train.progressMeters = st.trackProgressMeters
                        train.stationStopTimer = STATION_DWELL_SECONDS
                        train.stoppedAtStationId = st.id
                        train.speedMs = 0f
                        break
                    }
                }
            }

            val pose = sampleTrackPose(train.progressMeters)
            train.currentPosX = pose.x
            train.currentPosZ = pose.z
            train.currentYaw = pose.yaw
        }
    }

    fun findNextStationAhead(progressMeters: Float): MetroStation? {
        return STATIONS.minByOrNull { st ->
            val diff = st.trackProgressMeters - progressMeters
            if (diff > 1.8f) diff else diff + LOOP_PERIMETER
        }
    }

    fun findNearestStationTo(px: Float, pz: Float): MetroStation {
        return STATIONS.minByOrNull { st ->
            val dx = px - st.boardPosX
            val dz = pz - st.boardPosZ
            dx * dx + dz * dz
        } ?: STATIONS.first()
    }

    fun refreshTurnstileAutoLock(px: Float, py: Float, pz: Float) {
        if (py > 2.2f) return
        val toLock = unlockedStationGates.filter { id ->
            val st = STATIONS.find { it.id == id } ?: return@filter false
            val dx = px - st.turnstileX
            val dz = pz - st.turnstileZ
            val distSq = dx * dx + dz * dz
            !st.isOnStairFootprint(px, pz, margin = 1.6f) && distSq > 9.5f * 9.5f
        }
        if (toLock.isNotEmpty()) {
            unlockedStationGates.removeAll(toLock.toSet())
        }
    }

    fun getWalkableElevationY(px: Float, curY: Float, pz: Float): Float? {
        for (st in STATIONS) {
            if (st.isOnPlatform(px, pz, margin = 0.25f) && curY >= DECK_SURFACE_Y - 1.8f) {
                return DECK_SURFACE_Y
            }
            if (st.isOnStairFootprint(px, pz, margin = 0.25f)) {
                val stairY = st.computeStairElevation(px, pz)
                if (curY >= stairY - 1.5f && curY <= stairY + 2.5f) {
                    return stairY
                }
            }
        }
        return null
    }

    fun checkCollision3D(px: Float, py: Float, pz: Float, margin: Float, bodyHeight: Float = 1.75f): Boolean {
        if (CompoundAvenue90.wallColliders.any { it.overlaps3D(px, py, pz, margin, bodyHeight) }) return true
        if (staticColliders.any { it.overlaps3D(px, py, pz, margin, bodyHeight) }) return true
        for (st in STATIONS) {
            if (!unlockedStationGates.contains(st.id)) {
                val gateCollider = getTurnstileBarrierCollider(st)
                if (gateCollider.overlaps3D(px, py, pz, margin, bodyHeight)) {
                    if (!st.isOnStairFootprint(px, pz, margin = 0.2f)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun resolveCollisionPush3D(px: Float, py: Float, pz: Float, margin: Float, bodyHeight: Float = 1.75f): Pair<Float, Float>? {
        var curX = px
        var curZ = pz
        var pushed = false

        for (box in CompoundAvenue90.wallColliders) {
            box.resolvePush3D(curX, py, curZ, margin, bodyHeight)?.let { (nx, nz) ->
                curX = nx; curZ = nz; pushed = true
            }
        }
        for (box in staticColliders) {
            box.resolvePush3D(curX, py, curZ, margin, bodyHeight)?.let { (nx, nz) ->
                curX = nx; curZ = nz; pushed = true
            }
        }
        for (st in STATIONS) {
            if (!unlockedStationGates.contains(st.id) && !st.isOnStairFootprint(curX, curZ, margin = 0.1f)) {
                getTurnstileBarrierCollider(st).resolvePush3D(curX, py, curZ, margin, bodyHeight)?.let { (nx, nz) ->
                    curX = nx; curZ = nz; pushed = true
                }
            }
        }
        return if (pushed) Pair(curX, curZ) else null
    }

    private fun getTurnstileBarrierCollider(st: MetroStation): WorldBoxCollider {
        return when (st.stairDirection) {
            StairDirection.CLIMB_WEST, StairDirection.CLIMB_EAST ->
                WorldBoxCollider("${st.id}_gate", st.turnstileX, st.turnstileZ, 0.9f, 4.4f, 0f, 2.4f)
            StairDirection.CLIMB_SOUTH, StairDirection.CLIMB_NORTH ->
                WorldBoxCollider("${st.id}_gate", st.turnstileX, st.turnstileZ, 4.4f, 0.9f, 0f, 2.4f)
        }
    }

    fun findNearbyTurnstileStation(px: Float, py: Float, pz: Float): MetroStation? {
        return STATIONS.firstOrNull { it.isNearTurnstile(px, py, pz, radius = 5.2f) }
    }

    fun findStationWherePlayerStanding(px: Float, py: Float, pz: Float): MetroStation? {
        if (py < DECK_SURFACE_Y - 1.5f) return null
        return STATIONS.firstOrNull { it.isOnPlatform(px, pz, margin = 1.4f) }
    }

    fun findBoardableTrainAtStation(station: MetroStation): ElectricMetroTrain? {
        return trains.minByOrNull { t ->
            val d = abs(t.progressMeters - station.trackProgressMeters)
            min(d, LOOP_PERIMETER - d)
        }?.takeIf { t ->
            t.stoppedAtStationId == station.id || run {
                val d = abs(t.progressMeters - station.trackProgressMeters)
                min(d, LOOP_PERIMETER - d) <= 22.0f
            }
        }
    }
}
