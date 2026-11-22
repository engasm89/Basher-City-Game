package com.example.opencity.engine

import com.example.opencity.model.Building
import com.example.opencity.model.BuildingType
import com.example.opencity.model.CityData
import com.example.opencity.model.CompoundAvenue90
import com.example.opencity.model.InteriorType
import com.example.opencity.model.CarMeshStyle
import com.example.opencity.model.CarTier
import com.example.opencity.model.MotorCityData
import com.example.opencity.model.StreetZone
import com.example.opencity.model.TrafficLight
import com.example.opencity.model.TrafficLightColor
import com.example.opencity.model.Vehicle
import com.example.opencity.model.VehicleType
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * Generates and manages 3D geometry buffers for all world elements.
 */
class CityWorldMeshes {

    @Volatile var worldStaticBuffer: FloatBuffer? = null
    @Volatile var worldStaticVertexCount: Int = 0
    @Volatile var worldNightEmissiveBuffer: FloatBuffer? = null
    @Volatile var worldNightEmissiveVertexCount: Int = 0
    @Volatile var worldWaterBuffer: FloatBuffer? = null
    @Volatile var worldWaterVertexCount: Int = 0
    var interiorWaterBuffer: FloatBuffer? = null
    var interiorWaterVertexCount: Int = 0

    // Reusable buffers for dynamic vehicles & quantized character/robot caches
    private val vehicleBuffers = mutableMapOf<VehicleType, Pair<FloatBuffer, Int>>()
    private val dynamicVehicleMeshCache = HashMap<String, Pair<FloatBuffer, Int>>()
    private val robotMeshCache = HashMap<String, Pair<FloatBuffer, Int>>()
    private val characterMeshCache = HashMap<String, Pair<FloatBuffer, Int>>()

    @Synchronized
    fun buildWorldStaticMesh(buildings: List<Building>, trafficLights: List<TrafficLight>) {
        if (worldStaticBuffer != null) return
        val mb = MeshBuilder(initialCapacityVertices = 36864)
        val nightMb = MeshBuilder(initialCapacityVertices = 8192)
        val waterMb = MeshBuilder(initialCapacityVertices = 2048)

        // 1. Huge Vast Stationary Ocean, Turquoise Lagoon, Golden Sandy Beach & City Green Island
        // Built as non-overlapping hollow rings so water never flickers or oscillates back and forth!
        val deepOceanColor = floatArrayOf(0.03f, 0.28f, 0.58f, 1f)     // Deep expansive open sea
        val midSeaColor = floatArrayOf(0.05f, 0.45f, 0.74f, 1f)        // Rich coastal sapphire water
        val turquoiseLagoon = floatArrayOf(0.09f, 0.66f, 0.88f, 1f)    // Bright tropical turquoise shallows
        val shallowWater = floatArrayOf(0.24f, 0.85f, 0.94f, 1f)       // Crystal shoreline shallows
        val foamColor = floatArrayOf(0.93f, 0.97f, 1.0f, 1f)           // White surf seafoam line
        val wetSandColor = floatArrayOf(0.84f, 0.73f, 0.50f, 1f)       // Wet shoreline sand
        val drySandColor = floatArrayOf(0.95f, 0.87f, 0.64f, 1f)       // Warm golden sandy beach
        val cornicheStone = floatArrayOf(0.85f, 0.83f, 0.79f, 1f)      // Coastal promenade curb
        val grassColor = floatArrayOf(0.30f, 0.58f, 0.30f, 1f)         // Lush city island park green

        // Helper quad for axis-aligned horizontal planes (CCW upward normal +Y)
        fun addGroundRect(minX: Float, maxX: Float, minZ: Float, maxZ: Float, y: Float, color: FloatArray) {
            if (maxX <= minX || maxZ <= minZ) return
            mb.addQuad(
                minX, y, maxZ,
                maxX, y, maxZ,
                maxX, y, minZ,
                minX, y, minZ,
                0f, 1f, 0f, color
            )
        }

        // Non-overlapping hollow ring around an inner rectangle (eliminates coplanar Z-fighting/flicker)
        fun addHollowGroundRing(
            outMinX: Float, outMaxX: Float, outMinZ: Float, outMaxZ: Float,
            inMinX: Float, inMaxX: Float, inMinZ: Float, inMaxZ: Float,
            y: Float, color: FloatArray
        ) {
            addGroundRect(outMinX, inMinX, outMinZ, outMaxZ, y, color) // West strip
            addGroundRect(inMaxX, outMaxX, outMinZ, outMaxZ, y, color) // East strip
            addGroundRect(inMinX, inMaxX, outMinZ, inMinZ, y, color)  // North strip
            addGroundRect(inMinX, inMaxX, inMaxZ, outMaxZ, y, color)  // South strip
        }

        // Recursively cuts open rectangular holes (e.g., recessed Villa Pools) out of the terrain slab
        fun addGroundRectWithHoles(
            minX: Float, maxX: Float, minZ: Float, maxZ: Float,
            y: Float, color: FloatArray,
            holes: List<FloatArray>,
            holeIndex: Int = 0
        ) {
            if (maxX <= minX + 0.01f || maxZ <= minZ + 0.01f) return
            var idx = holeIndex
            while (idx < holes.size) {
                val h = holes[idx]
                if (h[1] > minX + 0.01f && h[0] < maxX - 0.01f && h[3] > minZ + 0.01f && h[2] < maxZ - 0.01f) {
                    val hMinX = h[0].coerceIn(minX, maxX)
                    val hMaxX = h[1].coerceIn(minX, maxX)
                    val hMinZ = h[2].coerceIn(minZ, maxZ)
                    val hMaxZ = h[3].coerceIn(minZ, maxZ)
                    addGroundRectWithHoles(minX, hMinX, minZ, maxZ, y, color, holes, idx + 1)
                    addGroundRectWithHoles(hMaxX, maxX, minZ, maxZ, y, color, holes, idx + 1)
                    addGroundRectWithHoles(hMinX, hMaxX, minZ, hMinZ, y, color, holes, idx + 1)
                    addGroundRectWithHoles(hMinX, hMaxX, hMaxZ, maxZ, y, color, holes, idx + 1)
                    return
                }
                idx++
            }
            addGroundRect(minX, maxX, minZ, maxZ, y, color)
        }

        // Collect all outdoor Villa swimming pool cutouts + Compound 90 Avenue Sea & Pool cutouts so water drops genuinely below y=0
        val outdoorPoolHoles = buildings.mapNotNull { it.getOutdoorPoolBounds() } + CompoundAvenue90.waterCutouts

        // 1a. Huge Vast Open Ocean (-2800..2800, completely still outside coastal rings)
        addHollowGroundRing(-2800f, 2800f, -2800f, 2800f, -790f, 940f, -760f, 920f, -0.35f, deepOceanColor)
        // 1b. Outer Coastal Sapphire Sea Band
        addHollowGroundRing(-790f, 940f, -760f, 920f, -435f, 585f, -418f, 572f, -0.28f, midSeaColor)
        // 1c. Mid Turquoise Coastal Lagoon
        addHollowGroundRing(-435f, 585f, -418f, 572f, -330f, 480f, -312f, 468f, -0.22f, turquoiseLagoon)
        // 1d. Shallow Crystal Surf Zone
        addHollowGroundRing(-330f, 480f, -312f, 468f, -319f, 469f, -301f, 457f, -0.16f, shallowWater)
        // 1e. White Seafoam Surf Collar hugging the outer shoreline
        addHollowGroundRing(-319f, 469f, -301f, 457f, -315f, 465f, -297f, 453f, -0.12f, foamColor)
        // 1f. Wet Golden Sand Shoreline Strip
        addHollowGroundRing(-315f, 465f, -297f, 453f, -306f, 456f, -288f, 444f, -0.09f, wetSandColor)
        // 1g. Wide Golden Sandy Beach Perimeter (~46m wide beach wrapping North, South, East, West of the expanded island)
        addHollowGroundRing(-306f, 456f, -288f, 444f, -260f, 410f, -242f, 398f, -0.055f, drySandColor)
        // 1h. Coastal Corniche Limestone Rim around the entire City Island
        addHollowGroundRing(-260f, 410f, -242f, 398f, -256f, 406f, -238f, 394f, -0.03f, cornicheStone)
        // 1i. Lush Green City Island Platform with wide open perimeter green meadows for Sky Metro Towers & recessed cutouts for Villa Pools
        addGroundRectWithHoles(-256f, 406f, -238f, 394f, -0.01f, grassColor, outdoorPoolHoles)

        // 1.5. Distant Scenic Ocean Islands & Coastal Beach Props (palms, umbrellas, piers, lighthouse, yachts)
        buildCoastalBeachAndSeaDetails(mb, drySandColor, wetSandColor, grassColor, cornicheStone)

        // 2. Road Network (Continuous asphalt streets, intersection-trimmed sidewalks, zero crosswalk white tiles)
        val asphaltColor = floatArrayOf(0.18f, 0.20f, 0.22f, 1f) // Dark matte asphalt
        val sidewalkColor = floatArrayOf(0.78f, 0.77f, 0.75f, 1f) // Realistic concrete paving
        val stripeYellow = floatArrayOf(0.98f, 0.82f, 0.15f, 1f) // Highway yellow divider
        val whiteStripe = floatArrayOf(0.95f, 0.95f, 0.95f, 1f)

        val roadHalfWidth = 6.25f

        // Computes clean non-overlapping [start, end] intervals inside [rangeMin, rangeMax] outside cross-road corridors
        fun openSegmentsBetween(rangeMin: Float, rangeMax: Float, crossCenters: List<Float>, gapHalfWidth: Float): List<Pair<Float, Float>> {
            val result = mutableListOf<Pair<Float, Float>>()
            var cursor = rangeMin
            for (c in crossCenters.sorted()) {
                val gapStart = c - gapHalfWidth
                val gapEnd = c + gapHalfWidth
                if (gapStart > cursor + 0.05f && gapStart <= rangeMax) {
                    result.add(Pair(cursor, minOf(gapStart, rangeMax)))
                }
                if (gapEnd > cursor) {
                    cursor = maxOf(cursor, gapEnd)
                }
                if (cursor >= rangeMax) break
            }
            if (cursor < rangeMax - 0.05f) {
                result.add(Pair(cursor, rangeMax))
            }
            return result
        }

        val nsRoads = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
        val ewRoads = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)

        // Main North-South Avenues (Z axis) - continuous asphalt, sidewalks & lane stripes stop cleanly at cross-roads
        for (rx in nsRoads) {
            val zMin = -188f
            val zMax = if (rx == 0f || rx == -195f) 348f else 182f
            val centerZ = (zMin + zMax) / 2f
            val roadDepth = zMax - zMin
            val crossZs = if (rx == 0f || rx == -195f) ewRoads + listOf(256f, 340f) else ewRoads

            // Continuous unobstructed asphalt road surface
            mb.addBox(rx, 0.02f, centerZ, 12.5f, 0.04f, roadDepth, asphaltColor)

            // Sidewalks & outer lane edge lines strictly between cross-roads (never cutting across perpendicular streets!)
            for ((segStart, segEnd) in openSegmentsBetween(zMin, zMax, crossZs, roadHalfWidth)) {
                val segLen = segEnd - segStart
                val segCenter = (segStart + segEnd) / 2f
                mb.addBox(rx - 7.5f, 0.14f, segCenter, 2.5f, 0.20f, segLen, sidewalkColor)
                mb.addBox(rx + 7.5f, 0.14f, segCenter, 2.5f, 0.20f, segLen, sidewalkColor)
                mb.addBox(rx - 5.5f, 0.05f, segCenter, 0.18f, 0.02f, segLen, whiteStripe)
                mb.addBox(rx + 5.5f, 0.05f, segCenter, 0.18f, 0.02f, segLen, whiteStripe)
            }

            // Double yellow center dashes cleanly skipping intersections
            val zMaxLine = if (rx == 0f || rx == -195f) 344 else 176
            for (z in -182..zMaxLine step 7) {
                val zf = z.toFloat()
                if (crossZs.any { kotlin.math.abs(zf - it) < roadHalfWidth + 2.4f }) continue
                mb.addBox(rx - 0.22f, 0.06f, zf, 0.14f, 0.02f, 4.2f, stripeYellow)
                mb.addBox(rx + 0.22f, 0.06f, zf, 0.14f, 0.02f, 4.2f, stripeYellow)
            }
        }

        // Main East-West Boulevards (X axis) - asphalt fills strictly between NS avenues so intersections never Z-fight or break
        for (rz in ewRoads) {
            val xMin = -203f
            val xMax = if (rz == 175f) 353f else 203f
            val crossXs = if (rz == 175f) nsRoads + listOf(345f) else nsRoads

            for ((segStart, segEnd) in openSegmentsBetween(xMin, xMax, crossXs, roadHalfWidth)) {
                val segLen = segEnd - segStart
                val segCenter = (segStart + segEnd) / 2f
                mb.addBox(segCenter, 0.02f, rz, segLen, 0.04f, 12.5f, asphaltColor)
                mb.addBox(segCenter, 0.14f, rz - 7.5f, segLen, 0.20f, 2.5f, sidewalkColor)
                mb.addBox(segCenter, 0.14f, rz + 7.5f, segLen, 0.20f, 2.5f, sidewalkColor)
                mb.addBox(segCenter, 0.05f, rz - 5.5f, segLen, 0.02f, 0.18f, whiteStripe)
                mb.addBox(segCenter, 0.05f, rz + 5.5f, segLen, 0.02f, 0.18f, whiteStripe)
            }

            // Double yellow center dashes cleanly skipping intersections
            val xMaxLine = if (rz == 175f) 344 else 196
            for (x in -196..xMaxLine step 7) {
                val xf = x.toFloat()
                if (crossXs.any { kotlin.math.abs(xf - it) < roadHalfWidth + 2.4f }) continue
                mb.addBox(xf, 0.06f, rz - 0.22f, 4.2f, 0.02f, 0.14f, stripeYellow)
                mb.addBox(xf, 0.06f, rz + 0.22f, 4.2f, 0.02f, 0.14f, stripeYellow)
            }
        }

        // Dedicated Villa Boulevards (Z = 256f & Z = 340f) spanning X = -203..353
        for (rz in listOf(256f, 340f)) {
            val crossXs = listOf(-195f, 0f, 345f)
            for ((segStart, segEnd) in openSegmentsBetween(-203f, 353f, crossXs, roadHalfWidth)) {
                val segLen = segEnd - segStart
                val segCenter = (segStart + segEnd) / 2f
                mb.addBox(segCenter, 0.02f, rz, segLen, 0.04f, 12.5f, asphaltColor)
                mb.addBox(segCenter, 0.14f, rz - 7.5f, segLen, 0.20f, 2.5f, sidewalkColor)
                mb.addBox(segCenter, 0.14f, rz + 7.5f, segLen, 0.20f, 2.5f, sidewalkColor)
                mb.addBox(segCenter, 0.05f, rz - 5.5f, segLen, 0.02f, 0.18f, whiteStripe)
                mb.addBox(segCenter, 0.05f, rz + 5.5f, segLen, 0.02f, 0.18f, whiteStripe)
            }
            for (x in -196..344 step 7) {
                val xf = x.toFloat()
                if (crossXs.any { kotlin.math.abs(xf - it) < roadHalfWidth + 2.4f }) continue
                mb.addBox(xf, 0.06f, rz - 0.22f, 4.2f, 0.02f, 0.14f, stripeYellow)
                mb.addBox(xf, 0.06f, rz + 0.22f, 4.2f, 0.02f, 0.14f, stripeYellow)
            }
        }

        // East Villa Perimeter Connector Road (X = 345f, Z = 168.75..348)
        val eastCrossZs = listOf(175f, 256f, 340f)
        mb.addBox(345f, 0.02f, 258.375f, 12.5f, 0.04f, 179.25f, asphaltColor)
        for ((segStart, segEnd) in openSegmentsBetween(168.75f, 348f, eastCrossZs, roadHalfWidth)) {
            val segLen = segEnd - segStart
            val segCenter = (segStart + segEnd) / 2f
            mb.addBox(345f - 7.5f, 0.14f, segCenter, 2.5f, 0.20f, segLen, sidewalkColor)
            mb.addBox(345f + 7.5f, 0.14f, segCenter, 2.5f, 0.20f, segLen, sidewalkColor)
            mb.addBox(345f - 5.5f, 0.05f, segCenter, 0.18f, 0.02f, segLen, whiteStripe)
            mb.addBox(345f + 5.5f, 0.05f, segCenter, 0.18f, 0.02f, segLen, whiteStripe)
        }
        for (z in 180..342 step 7) {
            val zf = z.toFloat()
            if (eastCrossZs.any { kotlin.math.abs(zf - it) < roadHalfWidth + 2.4f }) continue
            mb.addBox(345f - 0.22f, 0.06f, zf, 0.14f, 0.02f, 4.2f, stripeYellow)
            mb.addBox(345f + 0.22f, 0.06f, zf, 0.14f, 0.02f, 4.2f, stripeYellow)
        }

        val marbleWhite = floatArrayOf(0.96f, 0.95f, 0.92f, 1f)

        // 2.8. Waterfront Marina & Canal Waterway strictly on East Grass Peninsula (X = 236f, Z = -157f to -73f)
        val canalWater = floatArrayOf(0.08f, 0.65f, 0.88f, 1f)
        val pierWood = floatArrayOf(0.48f, 0.35f, 0.22f, 1f)
        val yachtWhite = floatArrayOf(0.98f, 0.98f, 0.98f, 1f)
        val yachtBlue = floatArrayOf(0.12f, 0.45f, 0.85f, 1f)
        // Water surface (X = 213..259, Z = -157..-73)
        mb.addBox(236f, 0.04f, -115f, 46f, 0.03f, 84f, canalWater)
        // Stone perimeter promenade wall
        mb.addBox(213f, 0.25f, -115f, 1.4f, 0.45f, 84f, marbleWhite)
        mb.addBox(259f, 0.25f, -115f, 1.4f, 0.45f, 84f, marbleWhite)
        // Wooden boat jetty piers extending into water
        for (pierZ in listOf(-90f, -115f, -140f)) {
            mb.addBox(225f, 0.18f, pierZ, 22f, 0.15f, 3.2f, pierWood)
            // Moored Luxury Yachts at piers
            mb.addBox(234f, 0.55f, pierZ + 4.2f, 4.0f, 0.85f, 10.5f, yachtWhite)
            mb.addBox(234f, 1.25f, pierZ + 3.2f, 2.8f, 0.70f, 5.0f, yachtBlue)
        }

        // 2.9. International Airport Runway strictly on East Aviation Peninsula (X = 340f, Z = -185f to 15f)
        val runwayAsphalt = floatArrayOf(0.12f, 0.13f, 0.15f, 1f)
        val runwayWhite = floatArrayOf(0.98f, 0.98f, 0.98f, 1f)
        // Main runway strip (length 200m, width 22m -> X = 329..351)
        mb.addBox(340f, 0.03f, -85f, 22f, 0.03f, 200f, runwayAsphalt)
        // Runway Piano Key Threshold stripes at North & South ends
        for (i in -3..3) {
            mb.addBox(340f + i * 2.4f, 0.06f, -177f, 1.2f, 0.02f, 10f, runwayWhite)
            mb.addBox(340f + i * 2.4f, 0.06f, 7f, 1.2f, 0.02f, 10f, runwayWhite)
        }
        // Runway Centerline Dashes
        for (rz in -165..-5 step 15) {
            mb.addBox(340f, 0.06f, rz.toFloat(), 1.4f, 0.02f, 9f, runwayWhite)
        }
        // Touchdown Aiming Markers
        mb.addBox(340f - 5.0f, 0.06f, -140f, 2.4f, 0.02f, 18f, runwayWhite)
        mb.addBox(340f + 5.0f, 0.06f, -140f, 2.4f, 0.02f, 18f, runwayWhite)
        // Runway Edge Lights (Green & Amber)
        for (rz in -180..10 step 20) {
            mb.addBox(328.6f, 0.15f, rz.toFloat(), 0.35f, 0.30f, 0.35f, floatArrayOf(0.1f, 0.95f, 0.2f, 1f))
            mb.addBox(351.4f, 0.15f, rz.toFloat(), 0.35f, 0.30f, 0.35f, floatArrayOf(0.1f, 0.95f, 0.2f, 1f))
        }

        // 3. Buildings & Architectural Details
        for (b in buildings) {
            val cx = b.posX
            val cz = b.posZ
            val cy = b.height / 2f
            val baseColor = b.colorPrimary
            val accentColor = b.colorSecondary

            if (b.type == BuildingType.PARKING_LOT) {
                // 🅿️⬇️ Underground City Garage Portal Entrance + Descending Ramp
                val concreteWall = floatArrayOf(0.76f, 0.78f, 0.82f, 1f)
                val darkTunnel = floatArrayOf(0.05f, 0.06f, 0.08f, 1f)
                val rampAsphalt = floatArrayOf(0.14f, 0.16f, 0.18f, 1f)
                val neonCyan = b.colorSecondary
                val paintYellow = floatArrayOf(0.98f, 0.84f, 0.12f, 1f)

                // Left & Right Portal Retaining Walls
                mb.addBox(cx - b.width / 2f + 1.3f, b.height / 2f, cz, 2.6f, b.height, b.depth, concreteWall)
                mb.addBox(cx + b.width / 2f - 1.3f, b.height / 2f, cz, 2.6f, b.height, b.depth, concreteWall)
                // Back Portal Wall & Deep Dark Tunnel Backdrop
                mb.addBox(cx, b.height / 2f, cz - b.depth / 2f + 1.2f, b.width, b.height, 2.4f, concreteWall)
                mb.addBox(cx, 1.55f, cz - b.depth / 2f + 2.6f, b.width - 5.2f, 3.1f, 0.8f, darkTunnel)
                // Portal Canopy Roof Slab (overhead gantry above ramp entrance)
                mb.addBox(cx, b.height - 0.35f, cz - 1.2f, b.width, 0.7f, b.depth - 2.4f, baseColor)
                // Illuminated Cyan Gantry Fascia + Blue 'P' Emblems on Portal Arch
                mb.addBox(cx, b.height - 0.35f, cz + b.depth / 2f - 2.25f, b.width + 0.2f, 0.55f, 0.22f, neonCyan)
                mb.addBox(cx, b.height - 0.35f, cz + b.depth / 2f - 2.12f, 3.2f, 0.9f, 0.16f, floatArrayOf(0.08f, 0.42f, 0.95f, 1f))

                // Wide Entrance Apron connecting cleanly up to Z = 35 sidewalk without spilling into asphalt
                mb.addBox(cx, 0.04f, cz + b.depth / 2f + 1.4f, 9.8f, 0.08f, 2.8f, rampAsphalt)
                // Stepped Descending Ramp Diving Underground into the Tunnel
                for (step in 0..9) {
                    val t = step / 9f
                    val stepZ = (cz + b.depth / 2f - 0.4f) - step * 1.15f
                    val stepY = 0.02f - t * 2.15f
                    mb.addBox(cx, stepY - 0.15f, stepZ, 9.4f, 0.30f, 1.22f, rampAsphalt)
                    if (step % 2 == 0) {
                        mb.addBox(cx, stepY + 0.02f, stepZ, 2.2f, 0.03f, 0.35f, paintYellow)
                    }
                }
            } else if (b.type == BuildingType.RESIDENTIAL_VILLA) {
                // 🏛️ Neoclassical Luxury Villa Architecture (مطابقة لصورة الإعلان العقاري "فيلا فاخرة للبيع")
                buildNeoclassicalVillaExterior(mb, waterMb, b, cx, cz)
            } else {
                // Main Building Mass
                mb.addBox(cx, cy, cz, b.width, b.height, b.depth, baseColor)

                // Building Plazas / Foundations trimmed tightly to never jut into city streets
                mb.addBox(cx, 0.1f, cz, b.width + 1.4f, 0.2f, b.depth + 1.4f, sidewalkColor)

                // Roof Styling (Street Houses get flat reinforced Rooftop Airplane Parking Pads + Ladder!)
                if (b.hasRooftopAirplanePad) {
                    addStreetHouseRooftopAirplanePad(mb, b, cx, cz, accentColor)
                } else {
                    when (b.roofType) {
                        Building.RoofType.PEAKED -> {
                            mb.addPeakedRoof(cx, b.height, cz, b.width, b.height * 0.45f, b.depth, accentColor)
                        }
                        Building.RoofType.DOMED -> {
                            // Grand Mosque Dome + Minaret (kept strictly inside grass block!)
                            mb.addDome(cx, b.height, cz, b.width * 0.35f, accentColor)
                            val minaretRadius = 1.4f
                            val minaretHeight = b.height * 1.6f
                            val mx = cx + b.width / 2f + 1.8f
                            val mz = cz + b.depth / 2f + 1.8f
                            mb.addCylinder(mx, 0f, mz, minaretRadius, minaretHeight, baseColor)
                            mb.addCylinder(mx, minaretHeight, mz, minaretRadius * 1.25f, 1.8f, accentColor) // Balcony
                            mb.addDome(mx, minaretHeight + 1.8f, mz, minaretRadius * 0.9f, accentColor) // Cap
                        }
                        Building.RoofType.TIERED_SPIRE -> {
                            // High-rise spire on Apex Tower or Windmill
                            if (b.type == BuildingType.WINDMILL) {
                                mb.addDome(cx, b.height, cz, b.width * 0.4f, accentColor)
                                // Windmill blades hub
                                mb.addBox(cx, b.height - 2f, cz + b.depth / 2f + 1f, 1f, 1f, 1.5f, accentColor)
                                // Blades
                                mb.addBox(cx, b.height - 2f, cz + b.depth / 2f + 2f, 0.6f, 16f, 0.2f, floatArrayOf(0.9f, 0.9f, 0.9f, 1f))
                                mb.addBox(cx, b.height - 2f, cz + b.depth / 2f + 2f, 16f, 0.6f, 0.2f, floatArrayOf(0.9f, 0.9f, 0.9f, 1f))
                            } else {
                                // Skyscraper tier
                                mb.addBox(cx, b.height + 4f, cz, b.width * 0.65f, 8f, b.depth * 0.65f, accentColor)
                                // Needle Spire
                                mb.addCylinder(cx, b.height + 8f, cz, 0.6f, 14f, floatArrayOf(0.9f, 0.9f, 0.95f, 1f))
                            }
                        }
                        Building.RoofType.FLAT -> {
                            // Parapet border
                            mb.addBox(cx, b.height + 0.6f, cz + b.depth / 2f - 0.3f, b.width, 1.2f, 0.6f, accentColor)
                            mb.addBox(cx, b.height + 0.6f, cz - b.depth / 2f + 0.3f, b.width, 1.2f, 0.6f, accentColor)
                        }
                    }
                }
            }

            // Window Bands / Glass Facade strips for Towers, and 6 Framed Windows for all Houses!
            if (b.type == BuildingType.SKYSCRAPER || b.type == BuildingType.MODERN_TOWER) {
                val windowColor = floatArrayOf(0.7f, 0.88f, 0.98f, 1f)
                val warmWinEmissive = floatArrayOf(1.0f, 0.90f, 0.56f, 1f)
                val cyanWinEmissive = floatArrayOf(0.42f, 0.90f, 1.0f, 1f)
                val floors = (b.height / 4f).toInt()
                for (fl in 1 until floors) {
                    val wy = fl * 4f
                    mb.addBox(cx, wy, cz, b.width + 0.2f, 1.8f, b.depth + 0.2f, windowColor)
                    if (fl % 2 == 1) {
                        val floorGlow = if ((fl / 2 + cx.toInt()) % 3 == 0) cyanWinEmissive else warmWinEmissive
                        nightMb.addBox(cx, wy, cz, b.width + 0.28f, 1.35f, b.depth + 0.28f, floorGlow)
                    }
                }
                // Aviation rooftop warning beacons on tall towers
                if (b.height >= 40f) {
                    nightMb.addBox(cx, b.height + 0.9f, cz, 1.0f, 0.6f, 1.0f, floatArrayOf(1.0f, 0.22f, 0.22f, 1f))
                }
            } else if (b.type == BuildingType.RUSTIC_FARMHOUSE ||
                b.type == BuildingType.APARTMENT_BLOCK ||
                b.interiorType == InteriorType.STANDARD_HOUSE ||
                (b.interiorType == InteriorType.PLAYER_VILLA && b.type != BuildingType.RESIDENTIAL_VILLA) ||
                b.streetZone == StreetZone.RESIDENTIAL_SUBURBS ||
                b.streetZone == StreetZone.OLD_TOWN
            ) {
                addHouseSixExteriorWindows(mb, b, cx, cz)
                // Illuminated warm house windows & porch lamp at night
                val winGlow = floatArrayOf(1.0f, 0.90f, 0.55f, 1f)
                val xOff = (b.width * 0.29f).coerceAtLeast(3.2f)
                val winW = (b.width * 0.20f).coerceIn(2.0f, 3.4f)
                val sideWinW = (b.depth * 0.24f).coerceIn(2.2f, 3.6f)
                val winH = (b.height * 0.26f).coerceIn(1.8f, 3.2f)
                val baseWinY = (b.height * 0.42f).coerceIn(2.4f, 4.5f)
                val upperWinY = if (b.height >= 15f) baseWinY + 4.4f else baseWinY
                val frontZ = cz + b.depth / 2f + 0.16f
                val backZ = cz - b.depth / 2f - 0.16f
                nightMb.addBox(cx - xOff, baseWinY, frontZ, winW * 0.85f, winH * 0.82f, 0.04f, winGlow)
                nightMb.addBox(cx + xOff, baseWinY, frontZ, winW * 0.85f, winH * 0.82f, 0.04f, winGlow)
                nightMb.addBox(cx - xOff, upperWinY, backZ, winW * 0.85f, winH * 0.82f, 0.04f, winGlow)
                nightMb.addBox(cx + xOff, upperWinY, backZ, winW * 0.85f, winH * 0.82f, 0.04f, winGlow)
                nightMb.addBox(cx - b.width / 2f - 0.16f, baseWinY, cz, 0.04f, winH * 0.82f, sideWinW * 0.85f, winGlow)
                nightMb.addBox(cx + b.width / 2f + 0.16f, upperWinY, cz, 0.04f, winH * 0.82f, sideWinW * 0.85f, winGlow)
            }

            // 1-Car Attached Drive-In Garage next to every city house («كراج يكفي سيارة واحدة جنب كل بيت»)
            if (b.hasSingleCarGarage) {
                addSingleCarAttachedGarage(mb, b, cx, cz, baseColor, accentColor)
            }

            // Front-of-House Envelope Mailbox («مكان لوضع الظرف أمام البيت في الشوارع»)
            if (b.hasEnvelopeMailbox()) {
                val mbPos = b.getMailboxPosition()
                if (mbPos != null) {
                    addHouseEnvelopeMailbox(mb, mbPos.first, mbPos.second, accentColor)
                }
            }

            // Entrance Door Frame & Threshold Backdrop for enterable buildings (doors themselves animate dynamically on approach)
            if (b.isEnterable && b.type != BuildingType.RESIDENTIAL_VILLA) {
                val frameColor = floatArrayOf(0.14f, 0.20f, 0.30f, 1f)
                val hallBackdrop = floatArrayOf(0.05f, 0.08f, 0.14f, 1f)
                val doorZ = cz + b.depth / 2f
                // Dark interior vestibule backdrop inside doorway
                mb.addBox(b.doorPosX, 1.8f, doorZ + 0.02f, 3.0f, 3.5f, 0.14f, hallBackdrop)
                // Side & top doorway frame trim
                mb.addBox(b.doorPosX - 1.60f, 1.8f, doorZ + 0.10f, 0.22f, 3.6f, 0.36f, frameColor)
                mb.addBox(b.doorPosX + 1.60f, 1.8f, doorZ + 0.10f, 0.22f, 3.6f, 0.36f, frameColor)
                mb.addBox(b.doorPosX, 3.62f, doorZ + 0.10f, 3.42f, 0.22f, 0.36f, frameColor)
                // Entrance Awning canopy
                mb.addBox(b.doorPosX, 3.9f, doorZ + 1.2f, 4.4f, 0.3f, 2.2f, accentColor)
                // Glowing entrance marker on sidewalk
                val beaconColor = floatArrayOf(0.15f, 0.95f, 0.55f, 1f)
                mb.addBox(b.doorPosX, 0.18f, b.doorPosZ, 2.2f, 0.08f, 2.2f, beaconColor)
            }

            // Specific Landmark Features
            if (b.type == BuildingType.HOSPITAL) {
                // Red Cross Emblem
                val red = floatArrayOf(0.95f, 0.1f, 0.1f, 1f)
                val emblemY = b.height - 4f
                mb.addBox(cx, emblemY, cz + b.depth / 2f + 0.3f, 4f, 1.2f, 0.4f, red)
                mb.addBox(cx, emblemY, cz + b.depth / 2f + 0.3f, 1.2f, 4f, 0.4f, red)
            } else if (b.type == BuildingType.CENTRAL_BUS_TERMINAL) {
                // Compact glass canopy over passenger platform inside block bounds (never overhangs street)
                val canopyColor = floatArrayOf(0.2f, 0.6f, 0.9f, 0.8f)
                mb.addBox(cx, 5.5f, cz + b.depth / 2f + 1.8f, b.width * 0.92f, 0.5f, 3.6f, canopyColor)
                for (px in listOf(cx - b.width * 0.40f, cx, cx + b.width * 0.40f)) {
                    mb.addCylinder(px, 0f, cz + b.depth / 2f + 3.2f, 0.35f, 5.5f, floatArrayOf(0.85f, 0.85f, 0.85f, 1f))
                }
            } else if (b.id == "nike_adidas_fashion_mall") {
                // Iconic Nike Orange & Adidas Blue Roof Brand Marquee & Showcase Pillars
                val nikeOrange = floatArrayOf(1.0f, 0.42f, 0.0f, 1f)
                val adidasBlue = floatArrayOf(0.12f, 0.45f, 0.96f, 1f)
                val neonWhite = floatArrayOf(0.98f, 0.98f, 1.0f, 1f)
                val frontZ = cz + b.depth / 2f + 0.16f
                // Dual Brand Signboard on Rooftop (+Z front), sized neatly inside block width
                mb.addBox(cx - 3.6f, b.height + 1.5f, frontZ - 0.5f, 6.4f, 2.2f, 0.5f, nikeOrange)
                mb.addBox(cx + 3.6f, b.height + 1.5f, frontZ - 0.5f, 6.4f, 2.2f, 0.5f, adidasBlue)
                // Adidas 3-stripe accent bar & Nike swoosh bar on storefront facade
                for (i in 0..2) {
                    mb.addBox(cx + 2.2f + i * 0.95f, b.height * 0.62f, frontZ, 0.40f, 3.6f, 0.18f, neonWhite)
                }
                mb.addBox(cx - 3.8f, b.height * 0.62f, frontZ, 4.4f, 0.60f, 0.18f, nikeOrange)
            } else if (b.type == BuildingType.STADIUM) {
                // Inner green soccer pitch
                mb.addBox(cx, 0.2f, cz, b.width * 0.7f, 0.05f, b.depth * 0.7f, floatArrayOf(0.2f, 0.7f, 0.25f, 1f))
            } else if (b.type == BuildingType.GAS_STATION) {
                // Gas pump island canopy
                mb.addBox(cx, 4.5f, cz, b.width, 0.5f, b.depth, floatArrayOf(0.9f, 0.2f, 0.2f, 1f))
                mb.addBox(cx, 1f, cz, 2f, 2f, 6f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f)) // Fuel pumps
            } else if (b.type == BuildingType.CAR_DEALERSHIP) {
                // «مدينة المحركات» (Motor City) Showroom Pavilion & Test Track strictly on East Grass Campus
                val glassBlue = floatArrayOf(0.2f, 0.55f, 0.85f, 0.8f)
                val neonOrange = floatArrayOf(0.98f, 0.5f, 0.08f, 1f)
                val chrome = floatArrayOf(0.92f, 0.92f, 0.95f, 1f)

                // Front panoramic glass showroom wall facing North (-Z) toward display cars
                mb.addBox(cx, 5f, cz - b.depth / 2f + 0.5f, b.width - 4f, 8f, 0.5f, glassBlue)
                // Modern showroom entrance portal
                mb.addBox(cx, 6.5f, cz - b.depth / 2f - 2f, 16f, 1.2f, 5f, floatArrayOf(0.12f, 0.14f, 0.18f, 1f))
                mb.addCylinder(cx - 7f, 0.1f, cz - b.depth / 2f - 4f, 0.45f, 6.5f, chrome)
                mb.addCylinder(cx + 7f, 0.1f, cz - b.depth / 2f - 4f, 0.45f, 6.5f, chrome)

                // Large illuminated roof marquee sign: «مدينة المحركات - MOTOR CITY»
                mb.addBox(cx, b.height + 1.8f, cz - b.depth / 2f + 2f, 28f, 3.2f, 1.2f, floatArrayOf(0.08f, 0.1f, 0.15f, 1f))
                mb.addBox(cx, b.height + 1.8f, cz - b.depth / 2f + 1.3f, 26f, 2.2f, 0.2f, neonOrange)

                // Car lot asphalt ground extension for open showroom displays (East Grass X=283..325, Z=62..126)
                mb.addBox(304f, 0.05f, 94f, 42f, 0.04f, 64f, floatArrayOf(0.16f, 0.17f, 0.2f, 1f))

                // Display podiums for each car tier with glowing illuminated borders
                for (car in MotorCityData.CATALOG) {
                    val tierGlow = when (car.tier) {
                        CarTier.WEAK_OLD -> floatArrayOf(0.7f, 0.7f, 0.7f, 1f)
                        CarTier.MID_RANGE -> floatArrayOf(0.2f, 0.6f, 0.95f, 1f)
                        CarTier.RARE -> floatArrayOf(0.95f, 0.2f, 0.2f, 1f)
                        CarTier.EXCLUSIVE -> floatArrayOf(0.1f, 0.85f, 0.85f, 1f)
                        CarTier.SUPER -> floatArrayOf(0.98f, 0.8f, 0.05f, 1f)
                        CarTier.ULTIMATE_RARE -> floatArrayOf(0.85f, 0.35f, 0.98f, 1f)
                    }
                    mb.addBox(car.displayPosX, 0.12f, car.displayPosZ, 3.2f, 0.18f, 5.8f, floatArrayOf(0.22f, 0.23f, 0.26f, 1f))
                    mb.addBox(car.displayPosX, 0.22f, car.displayPosZ, 3.4f, 0.06f, 6.0f, tierGlow)
                }

                // Dedicated Test Drive Circuit ("مسار تجربة القيادة") at East Grass X=342f, Z=48..144
                mb.addBox(342f, 0.04f, 96f, 20f, 0.03f, 96f, floatArrayOf(0.13f, 0.13f, 0.15f, 1f))
                for (step in 0..6) {
                    mb.addBox(342f, 0.06f, 56f + step * 5f, 9f, 0.02f, 0.5f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f))
                }
                val archX = 342f
                val archZ = 52f
                mb.addCylinder(archX - 6f, 0.1f, archZ, 0.4f, 7.5f, chrome)
                mb.addCylinder(archX + 6f, 0.1f, archZ, 0.4f, 7.5f, chrome)
                mb.addBox(archX, 7.2f, archZ, 13.5f, 1.8f, 1.2f, floatArrayOf(0.1f, 0.1f, 0.12f, 1f))
                mb.addBox(archX, 7.2f, archZ - 0.65f, 12f, 1.2f, 0.1f, neonOrange)
                for (czCurb in 52..140 step 4) {
                    val curbColor = if ((czCurb / 4) % 2 == 0) floatArrayOf(0.95f, 0.12f, 0.12f, 1f) else floatArrayOf(0.95f, 0.95f, 0.95f, 1f)
                    mb.addBox(352.5f, 0.12f, czCurb.toFloat(), 0.8f, 0.16f, 3.8f, curbColor)
                }
            } else if (b.type == BuildingType.POLICE_STATION) {
                // 👮 Police Station Landmark: Emergency Beacons, Patrol Bays & Jail Cell (all strictly inside grass block!)
                val policeBlue = floatArrayOf(0.12f, 0.45f, 0.95f, 1f)
                val policeRed = floatArrayOf(0.95f, 0.15f, 0.15f, 1f)
                val chrome = floatArrayOf(0.85f, 0.88f, 0.92f, 1f)

                mb.addBox(cx - 2.2f, b.height + 0.8f, cz, 1.8f, 0.6f, 0.8f, policeBlue)
                mb.addBox(cx + 2.2f, b.height + 0.8f, cz, 1.8f, 0.6f, 0.8f, policeRed)
                mb.addBox(cx, b.height + 0.7f, cz, 2.2f, 0.4f, 0.6f, chrome)

                mb.addBox(cx, 4.8f, cz + b.depth / 2f + 0.6f, 9.6f, 1.4f, 0.3f, floatArrayOf(0.08f, 0.15f, 0.28f, 1f))
                mb.addBox(cx, 4.8f, cz + b.depth / 2f + 0.8f, 8.8f, 1.0f, 0.1f, policeBlue)

                // Two Dedicated Marked Police Patrol Parking Bays cleanly on the grass plaza inside block
                for (slotX in listOf(cx - 4.4f, cx + 4.4f)) {
                    val bayZ = cz + b.depth / 2f + 3.6f
                    mb.addBox(slotX, 0.05f, bayZ, 3.6f, 0.03f, 6.4f, floatArrayOf(0.15f, 0.16f, 0.18f, 1f))
                    mb.addBox(slotX - 1.75f, 0.06f, bayZ, 0.14f, 0.02f, 6.4f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f))
                    mb.addBox(slotX + 1.75f, 0.06f, bayZ, 0.14f, 0.02f, 6.4f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f))
                    mb.addBox(slotX, 0.06f, bayZ - 3.0f, 3.6f, 0.02f, 0.2f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f))
                }

                // 3D Secure Prison Holding Cell neatly tucked beside Police HQ inside grass block
                val jailWall = floatArrayOf(0.20f, 0.22f, 0.26f, 1f)
                val steelBar = floatArrayOf(0.70f, 0.72f, 0.76f, 1f)
                val jailCellX = cx - b.width / 2f - 3.3f
                val jailCellZ = cz
                mb.addBox(jailCellX, 0.2f, jailCellZ, 5.4f, 0.4f, 7.2f, jailWall)
                mb.addBox(jailCellX, 4.2f, jailCellZ, 5.6f, 0.5f, 7.4f, jailWall)
                mb.addBox(jailCellX - 2.5f, 2.2f, jailCellZ, 0.4f, 3.8f, 7.2f, jailWall)
                mb.addBox(jailCellX, 2.2f, jailCellZ - 3.4f, 5.2f, 3.8f, 0.4f, jailWall)
                for (barStep in -3..3) {
                    val bz = jailCellZ + barStep * 0.95f
                    mb.addCylinder(jailCellX + 2.5f, 0.4f, bz, 0.09f, 3.8f, steelBar)
                }
                for (barStep in -2..2) {
                    val bx = jailCellX + barStep * 0.95f
                    mb.addCylinder(bx, 0.4f, jailCellZ + 3.4f, 0.09f, 3.8f, steelBar)
                }
                mb.addBox(jailCellX, 4.0f, jailCellZ + 3.65f, 4.4f, 0.7f, 0.2f, floatArrayOf(0.95f, 0.75f, 0.1f, 1f))
            } else if (b.type == BuildingType.RESIDENTIAL_VILLA) {
                // Fully modeled with 1:1 realism in buildNeoclassicalVillaExterior
            } else if (b.type == BuildingType.PARKING_LOT) {
                // 🅿️⬇️ Underground Garage Roadside Signage, Ticket Terminal & Clearance Arch
                val paintWhite = floatArrayOf(0.96f, 0.96f, 0.98f, 1f)
                val paintYellow = floatArrayOf(0.98f, 0.85f, 0.12f, 1f)
                val canopySteel = floatArrayOf(0.35f, 0.38f, 0.42f, 1f)
                val pylonBlue = floatArrayOf(0.08f, 0.42f, 0.95f, 1f)
                val neonGreen = floatArrayOf(0.1f, 0.95f, 0.45f, 1f)

                val gateZ = cz + b.depth / 2f + 0.8f
                // Left ticket dispenser kiosk
                mb.addBox(cx - 5.4f, 0.90f, gateZ, 0.8f, 1.8f, 0.8f, paintYellow)
                // Right guard control cabin
                mb.addBox(cx + 5.8f, 1.45f, gateZ - 0.6f, 2.2f, 2.8f, 2.2f, floatArrayOf(0.15f, 0.25f, 0.45f, 1f))
                mb.addBox(cx + 5.8f, 1.75f, gateZ + 0.55f, 1.6f, 1.0f, 0.1f, floatArrayOf(0.35f, 0.75f, 0.95f, 1f))
                // Tall Illuminated 'P' Entrance Pylon Sign (kept inside grass apron)
                val signX = cx - 6.6f
                val signZ = gateZ + 1.2f
                mb.addBox(signX, 3.2f, signZ, 0.7f, 6.4f, 0.7f, canopySteel)
                mb.addBox(signX, 5.9f, signZ, 3.0f, 2.2f, 0.45f, pylonBlue)
                mb.addBox(signX, 6.1f, signZ + 0.25f, 1.3f, 1.3f, 0.08f, paintWhite)
                mb.addBox(signX, 5.15f, signZ + 0.25f, 2.4f, 0.35f, 0.08f, neonGreen)
            } else if (b.id == "gate_jabhati") {
                // 🏛️ «جبهتي» — بوابة المدينة الشامخة والواجهة الرئيسية (Sized strictly inside grass plaza!)
                val marbleWhite = floatArrayOf(0.96f, 0.95f, 0.92f, 1f)
                val goldAccent = floatArrayOf(1.0f, 0.85f, 0.25f, 1f)
                val navySlate = floatArrayOf(0.12f, 0.16f, 0.25f, 1f)

                for (px in listOf(cx - 6.0f, cx - 2.6f, cx + 2.6f, cx + 6.0f)) {
                    mb.addCylinder(px, 0.1f, cz, 0.85f, 18.0f, marbleWhite)
                    mb.addBox(px, 18.2f, cz, 2.0f, 1.2f, 2.0f, goldAccent)
                    mb.addBox(px, 0.4f, cz, 2.2f, 0.8f, 2.2f, marbleWhite)
                }

                mb.addBox(cx, 19.5f, cz, 15.0f, 3.0f, 5.0f, marbleWhite)
                mb.addBox(cx, 21.4f, cz, 15.4f, 1.2f, 5.4f, goldAccent)

                mb.addBox(cx, 20.0f, cz + 2.6f, 11.0f, 2.2f, 0.3f, navySlate)
                mb.addBox(cx, 20.0f, cz + 2.78f, 9.8f, 1.6f, 0.1f, goldAccent)
                mb.addDome(cx, 22.8f, cz, 1.6f, goldAccent)
                mb.addBox(cx, 24.2f, cz, 0.4f, 1.8f, 0.4f, goldAccent)
            } else if (b.id == "skyscraper_horizon") {
                // 🚁 Horizon Global Tower Rooftop Helipad & Aviation Beacon
                val padY = b.height + 0.15f
                val yellowPaint = floatArrayOf(1.0f, 0.85f, 0.1f, 1f)
                val whitePaint = floatArrayOf(0.95f, 0.95f, 0.98f, 1f)
                val darkPad = floatArrayOf(0.18f, 0.20f, 0.24f, 1f)
                mb.addBox(cx, padY, cz, 15.5f, 0.3f, 15.5f, darkPad)
                mb.addCylinder(cx, padY + 0.16f, cz, 7.0f, 0.04f, yellowPaint, segments = 16)
                mb.addCylinder(cx, padY + 0.17f, cz, 6.3f, 0.04f, darkPad, segments = 16)
                mb.addBox(cx - 2.2f, padY + 0.18f, cz, 0.85f, 0.04f, 5.8f, whitePaint)
                mb.addBox(cx + 2.2f, padY + 0.18f, cz, 0.85f, 0.04f, 5.8f, whitePaint)
                mb.addBox(cx, padY + 0.18f, cz, 3.8f, 0.04f, 0.85f, whitePaint)
                mb.addCylinder(cx, padY + 0.3f, cz + 6f, 0.3f, 6.5f, floatArrayOf(0.85f, 0.85f, 0.9f, 1f))
                mb.addDome(cx, padY + 6.8f, cz + 6f, 0.65f, floatArrayOf(1.0f, 0.1f, 0.1f, 1f))
            } else if (b.id == "skyscraper_diamond") {
                mb.addPyramid(cx, b.height, cz, b.width * 0.85f, 16f, b.depth * 0.85f, floatArrayOf(1.0f, 0.85f, 0.25f, 1f))
            } else if (b.id == "cinema_imax_complex") {
                // 🎬 Grand IMAX Theater Facade & Glowing Magenta Neon Marquee (trimmed inside block!)
                val neonPink = floatArrayOf(1.0f, 0.15f, 0.55f, 1f)
                val neonCyan = floatArrayOf(0.1f, 0.9f, 1.0f, 1f)
                mb.addBox(cx, 8.5f, cz + b.depth / 2f + 0.9f, 17.5f, 2.0f, 2.2f, floatArrayOf(0.08f, 0.08f, 0.12f, 1f))
                mb.addBox(cx, 8.5f, cz + b.depth / 2f + 2.05f, 16.0f, 1.2f, 0.15f, neonPink)
                mb.addBox(cx - 5.2f, 14f, cz + b.depth / 2f + 0.2f, 4.6f, 7.5f, 0.2f, neonCyan)
                mb.addBox(cx + 5.2f, 14f, cz + b.depth / 2f + 0.2f, 4.6f, 7.5f, 0.2f, neonPink)
            } else if (b.id == "museum_culture_center") {
                // 🏛️ Classical Peristyle Portico & Columns for Museum & Opera (strictly on grass lot!)
                val marbleWhite = floatArrayOf(0.96f, 0.95f, 0.92f, 1f)
                val porticoZ = cz + b.depth / 2f + 1.8f
                for (step in 1..4) {
                    mb.addBox(cx, step * 0.25f, porticoZ - step * 0.4f, b.width * 0.78f, 0.25f, 1.0f, marbleWhite)
                }
                for (colStep in -3..3) {
                    val colX = cx + colStep * 2.5f
                    mb.addCylinder(colX, 1.0f, porticoZ, 0.45f, b.height - 3f, marbleWhite)
                }
                mb.addBox(cx, b.height - 1.5f, porticoZ, b.width * 0.85f, 1.8f, 2.8f, marbleWhite)
                mb.addPeakedRoof(cx, b.height + 0.3f, porticoZ, b.width * 0.85f, 5.5f, 2.8f, floatArrayOf(1.0f, 0.85f, 0.25f, 1f))
            } else if (b.id == "airport_control_tower") {
                // 📡 International Airport Traffic Control Tower Observation Cab & Radar
                val cabY = b.height - 4f
                val glassCab = floatArrayOf(0.2f, 0.65f, 0.92f, 0.85f)
                val darkMetal = floatArrayOf(0.18f, 0.20f, 0.24f, 1f)
                // Flared observation deck
                mb.addBox(cx, cabY, cz, b.width * 1.35f, 4.5f, b.depth * 1.35f, glassCab)
                mb.addBox(cx, cabY + 2.5f, cz, b.width * 1.40f, 0.8f, b.depth * 1.40f, darkMetal)
                // Rooftop spinning radar dish mast
                mb.addCylinder(cx, b.height + 0.4f, cz, 0.3f, 4.5f, floatArrayOf(0.85f, 0.85f, 0.9f, 1f))
                mb.addBox(cx, b.height + 4.2f, cz, 3.5f, 0.8f, 0.4f, floatArrayOf(0.95f, 0.95f, 0.98f, 1f))
                mb.addDome(cx, b.height + 5.2f, cz, 0.5f, floatArrayOf(1.0f, 0.1f, 0.1f, 1f))
            } else if (b.id == "waterfront_marina") {
                // ⛵ Waterfront Marina Observation Terrace & Deck
                val glassTeal = floatArrayOf(0.15f, 0.75f, 0.88f, 0.85f)
                val nauticalNavy = floatArrayOf(0.10f, 0.25f, 0.55f, 1f)
                // Upper promenade deck
                mb.addBox(cx, b.height + 0.4f, cz, b.width * 0.95f, 0.6f, b.depth * 0.95f, nauticalNavy)
                mb.addBox(cx, b.height + 1.2f, cz, b.width * 0.92f, 0.8f, b.depth * 0.92f, glassTeal)
            } else if (b.id == "street_houses_market") {
                // 🏘️🔑 «مكان ومكتب شراء بيوت الشارع» Showroom Facade & Miniature 3D House Plinth
                val emeraldGlow = floatArrayOf(0.08f, 0.78f, 0.48f, 1f)
                val goldTrim = floatArrayOf(1.0f, 0.82f, 0.16f, 1f)
                val glassPane = floatArrayOf(0.65f, 0.92f, 0.98f, 1f)
                val marblePlinth = floatArrayOf(0.95f, 0.94f, 0.90f, 1f)
                // Wide panoramic showroom glass windows
                mb.addBox(cx, 2.4f, cz + b.depth / 2f + 0.12f, b.width * 0.86f, 3.4f, 0.16f, glassPane)
                mb.addBox(cx, 7.6f, cz + b.depth / 2f + 0.12f, b.width * 0.86f, 2.8f, 0.16f, glassPane)
                // Glowing Emerald & Gold Rooftop Signboard
                mb.addCylinder(cx - 4.2f, b.height, cz + 2.5f, 0.25f, 3.8f, goldTrim)
                mb.addCylinder(cx + 4.2f, b.height, cz + 2.5f, 0.25f, 3.8f, goldTrim)
                mb.addBox(cx, b.height + 3.2f, cz + 2.5f, 11.0f, 3.2f, 0.55f, floatArrayOf(0.06f, 0.22f, 0.18f, 1f))
                mb.addBox(cx, b.height + 3.2f, cz + 2.82f, 10.2f, 2.4f, 0.12f, emeraldGlow)
                // Miniature 3D Display House on Pedestal in front of Showroom (kept strictly inside grass edge!)
                val miniX = cx - 5.2f
                val miniZ = cz + b.depth / 2f + 1.8f
                mb.addBox(miniX, 0.65f, miniZ, 2.4f, 1.3f, 2.2f, marblePlinth)
                mb.addBox(miniX, 2.05f, miniZ, 1.8f, 1.5f, 1.6f, floatArrayOf(0.96f, 0.92f, 0.84f, 1f))
                mb.addPeakedRoof(miniX, 2.80f, miniZ, 2.1f, 1.0f, 1.9f, goldTrim)
                mb.addBox(miniX - 0.45f, 2.1f, miniZ + 0.83f, 0.45f, 0.5f, 0.08f, glassPane)
                mb.addBox(miniX + 0.45f, 2.1f, miniZ + 0.83f, 0.45f, 0.5f, 0.08f, glassPane)
                // Golden Key Arch Pillar on right side of entrance
                val keyX = cx + 5.2f
                mb.addBox(keyX, 0.65f, miniZ, 2.0f, 1.3f, 2.0f, marblePlinth)
                mb.addCylinder(keyX, 1.3f, miniZ, 0.22f, 2.2f, goldTrim)
                mb.addDome(keyX, 3.6f, miniZ, 0.7f, goldTrim)
            } else if (b.id == "metro_card_sales_office") {
                // 💳🚇 «مركزي لبيع كروت المترو» Cyber Transit Storefront, Giant 3D Metro Card Sign & Canopy
                val cyanNeon = floatArrayOf(0.10f, 0.88f, 0.98f, 1f)
                val darkNavyFrame = floatArrayOf(0.05f, 0.14f, 0.24f, 1f)
                val goldCardStripe = floatArrayOf(1.0f, 0.82f, 0.16f, 1f)
                val glassFront = floatArrayOf(0.62f, 0.94f, 1.0f, 1f)
                val frontZ = cz + b.depth / 2f + 0.14f
                // Panoramic storefront windows & glowing cyan entry awning
                mb.addBox(cx - 4.0f, 2.3f, frontZ, 4.4f, 3.2f, 0.16f, glassFront)
                mb.addBox(cx + 4.0f, 2.3f, frontZ, 4.4f, 3.2f, 0.16f, glassFront)
                mb.addBox(cx, 3.85f, frontZ + 0.85f, 6.2f, 0.42f, 2.1f, darkNavyFrame)
                mb.addBox(cx, 4.08f, frontZ + 1.85f, 6.4f, 0.16f, 0.18f, cyanNeon)
                // Giant 3D Rooftop Smart Metro Card Display Monument
                mb.addCylinder(cx - 3.8f, b.height, cz + 2.2f, 0.22f, 2.8f, darkNavyFrame)
                mb.addCylinder(cx + 3.8f, b.height, cz + 2.2f, 0.22f, 2.8f, darkNavyFrame)
                mb.addBox(cx, b.height + 3.2f, cz + 2.2f, 9.8f, 4.0f, 0.55f, darkNavyFrame)
                mb.addBox(cx, b.height + 3.2f, cz + 2.52f, 9.0f, 3.2f, 0.12f, cyanNeon)
                // Gold NFC Chip emblem on giant rooftop Metro card
                mb.addBox(cx - 2.6f, b.height + 3.4f, cz + 2.62f, 1.5f, 1.1f, 0.10f, goldCardStripe)
                mb.addBox(cx + 1.4f, b.height + 2.5f, cz + 2.62f, 4.2f, 0.45f, 0.10f, goldCardStripe)
            }

            // Street-Zone Distinct Architectural Polish (تفاصيل الأحياء الأربعة)
            when (b.streetZone) {
                StreetZone.MAIN_STREET -> {
                    val glassStore = floatArrayOf(0.55f, 0.85f, 0.98f, 1f)
                    val neonGold = floatArrayOf(1.0f, 0.82f, 0.15f, 1f)
                    mb.addBox(cx, 2.2f, cz + b.depth / 2f + 0.12f, b.width * 0.85f, 3.2f, 0.15f, glassStore)
                    if (b.id == "main_investment_billboard") {
                        mb.addCylinder(cx - 4.5f, b.height, cz + 2f, 0.3f, 5.5f, floatArrayOf(0.3f, 0.32f, 0.38f, 1f))
                        mb.addCylinder(cx + 4.5f, b.height, cz + 2f, 0.3f, 5.5f, floatArrayOf(0.3f, 0.32f, 0.38f, 1f))
                        mb.addBox(cx, b.height + 4.8f, cz + 2f, 13.5f, 4.2f, 0.6f, floatArrayOf(0.08f, 0.12f, 0.22f, 1f))
                        mb.addBox(cx, b.height + 4.8f, cz + 2.35f, 12.6f, 3.4f, 0.12f, neonGold)
                    }
                }
                StreetZone.RESIDENTIAL_SUBURBS -> {
                    val hedgeGreen = floatArrayOf(0.18f, 0.56f, 0.22f, 1f)
                    val fenceWood = floatArrayOf(0.76f, 0.60f, 0.40f, 1f)
                    mb.addBox(cx - b.width / 2f + 2.2f, 0.65f, cz + b.depth / 2f + 1.2f, 3.8f, 1.1f, 1.2f, hedgeGreen)
                    mb.addBox(cx + b.width / 2f - 2.2f, 0.65f, cz + b.depth / 2f + 1.2f, 3.8f, 1.1f, 1.2f, hedgeGreen)
                    for (fz in -3..3 step 2) {
                        mb.addBox(cx + b.width / 2f + 1.1f, 0.7f, cz + fz * 1.8f, 0.25f, 1.3f, 1.5f, fenceWood)
                    }
                    if (b.id == "suburb_villa_garage") {
                        mb.addBox(cx - b.width / 2f - 1.4f, 2.2f, cz, 2.4f, 4.4f, 7.6f, floatArrayOf(0.88f, 0.88f, 0.90f, 1f))
                        mb.addBox(cx - b.width / 2f - 1.4f, 1.8f, cz + 3.87f, 1.8f, 3.0f, 0.15f, floatArrayOf(0.28f, 0.32f, 0.38f, 1f))
                    }
                }
                StreetZone.OLD_TOWN -> {
                    val cobbleStone = floatArrayOf(0.46f, 0.42f, 0.38f, 1f)
                    val cobbleDark = floatArrayOf(0.34f, 0.31f, 0.28f, 1f)
                    val warmLantern = floatArrayOf(1.0f, 0.78f, 0.28f, 1f)
                    // Cobblestone heritage lot base kept strictly inside grass block boundaries
                    mb.addBox(cx, 0.12f, cz, b.width + 2.2f, 0.18f, b.depth + 2.2f, cobbleStone)
                    mb.addBox(cx, 0.22f, cz + b.depth / 2f + 1.4f, 4.2f, 0.06f, 2.6f, cobbleDark)
                    mb.addBox(b.doorPosX + 2.2f, 2.8f, cz + b.depth / 2f + 0.3f, 0.45f, 0.65f, 0.45f, warmLantern)
                    if (b.id == "oldtown_secrets_locked") {
                        mb.addBox(cx - b.width / 2f - 1.5f, 0.75f, cz + 3.5f, 2.2f, 1.4f, 2.2f, floatArrayOf(0.16f, 0.50f, 0.22f, 1f))
                        mb.addBox(cx - b.width / 2f - 1.5f, 1.65f, cz + 3.5f, 0.65f, 0.65f, 0.65f, floatArrayOf(1.0f, 0.86f, 0.15f, 1f))
                    } else if (b.id == "oldtown_abandoned_treasure") {
                        mb.addBox(cx + b.width / 2f + 1.4f, 0.35f, cz + 2.5f, 2.4f, 0.5f, 3.0f, floatArrayOf(0.35f, 0.22f, 0.14f, 1f))
                    }
                }
                StreetZone.MODERN_TOWERS -> {
                    val neonCyan = floatArrayOf(0.05f, 0.90f, 1.0f, 1f)
                    val poolWater = floatArrayOf(0.10f, 0.78f, 0.96f, 1f)
                    // Vertical night-lighting architectural neon trim strips
                    mb.addBox(cx - b.width / 2f - 0.08f, b.height / 2f, cz + b.depth / 2f + 0.08f, 0.35f, b.height, 0.35f, neonCyan)
                    mb.addBox(cx + b.width / 2f + 0.08f, b.height / 2f, cz + b.depth / 2f + 0.08f, 0.35f, b.height, 0.35f, neonCyan)
                    if (b.id == "tower_penthouse_pool") {
                        // Rooftop private swimming pool deck!
                        mb.addBox(cx, b.height + 0.35f, cz, b.width * 0.78f, 0.6f, b.depth * 0.72f, floatArrayOf(0.92f, 0.94f, 0.96f, 1f))
                        mb.addBox(cx, b.height + 0.68f, cz, b.width * 0.62f, 0.12f, b.depth * 0.55f, poolWater)
                    } else if (b.id == "tower_smart_skyline") {
                        // Rooftop parking deck mark ('P')
                        mb.addBox(cx, b.height + 0.25f, cz, b.width * 0.75f, 0.3f, b.depth * 0.75f, floatArrayOf(0.22f, 0.25f, 0.30f, 1f))
                        mb.addBox(cx, b.height + 0.42f, cz, 4.5f, 0.06f, 4.5f, neonCyan)
                    }
                }
                StreetZone.AVENUE_90_COMPOUND -> Unit
                null -> Unit
            }
            if (b.isEnterable) {
                // Warm doorway porch glow at night
                val lampY = if (b.type == BuildingType.RESIDENTIAL_VILLA) 4.65f else 3.66f
                nightMb.addBox(b.doorPosX, lampY, cz + b.depth / 2f + 0.34f, 2.0f, 0.18f, 0.24f, floatArrayOf(1.0f, 0.92f, 0.58f, 1f))
            }
            b.getVillaExteriorSpec()?.let { spec ->
                // Villa night emissive golden roofline contour & chandeliers
                val gFloorH = spec.gFloorH
                val fFloorH = spec.fFloorH
                val totalRoofBaseY = gFloorH + 0.35f + fFloorH
                val roofExtra = if (spec.isCompact) 1.1f else 1.4f
                val roofPerimHalfW = (b.width + roofExtra) / 2f
                val roofPerimHalfD = (b.depth + roofExtra) / 2f
                val ledY = totalRoofBaseY + 0.55f
                val ledGlow = floatArrayOf(1.0f, 0.90f, 0.38f, 1f)
                nightMb.addBox(cx, ledY, cz + roofPerimHalfD + 0.10f, b.width + roofExtra + 0.15f, 0.24f, 0.14f, ledGlow)
                nightMb.addBox(cx, ledY, cz - roofPerimHalfD - 0.10f, b.width + roofExtra + 0.15f, 0.24f, 0.14f, ledGlow)
                nightMb.addBox(cx - roofPerimHalfW - 0.10f, ledY, cz, 0.14f, 0.24f, b.depth + roofExtra + 0.15f, ledGlow)
                nightMb.addBox(cx + roofPerimHalfW + 0.10f, ledY, cz, 0.14f, 0.24f, b.depth + roofExtra + 0.15f, ledGlow)
                val winGlowW = (spec.wingWinW - 0.35f).coerceAtLeast(1.8f)
                val gWinY = if (spec.isCompact) 3.9f else 4.5f
                val gWinH = if (spec.isCompact) 4.3f else 5.2f
                val upWinH = if (spec.isCompact) 3.8f else 4.6f
                for (wingSide in listOf(-1, 1)) {
                    val winX = cx + wingSide * spec.wingWinOffsetX
                    val frontZ = cz + b.depth / 2f + 0.16f
                    nightMb.addBox(winX, gWinY, frontZ, winGlowW, gWinH, 0.04f, floatArrayOf(1.0f, 0.92f, 0.62f, 1f))
                    val upY = gFloorH + 0.35f + fFloorH / 2f - 0.2f
                    nightMb.addBox(winX, upY, frontZ - 0.18f, winGlowW, upWinH, 0.04f, floatArrayOf(1.0f, 0.92f, 0.62f, 1f))
                }
            }
            // Landmark night neon accents & beacons
            when {
                b.type == BuildingType.HOSPITAL -> {
                    val red = floatArrayOf(1.0f, 0.16f, 0.16f, 1f)
                    val emblemY = b.height - 4f
                    nightMb.addBox(cx, emblemY, cz + b.depth / 2f + 0.54f, 4f, 1.2f, 0.1f, red)
                    nightMb.addBox(cx, emblemY, cz + b.depth / 2f + 0.54f, 1.2f, 4f, 0.1f, red)
                }
                b.type == BuildingType.POLICE_STATION -> {
                    nightMb.addBox(cx - 2.2f, b.height + 0.85f, cz, 1.85f, 0.64f, 0.85f, floatArrayOf(0.18f, 0.55f, 1.0f, 1f))
                    nightMb.addBox(cx + 2.2f, b.height + 0.85f, cz, 1.85f, 0.64f, 0.85f, floatArrayOf(1.0f, 0.20f, 0.20f, 1f))
                    nightMb.addBox(cx, 4.8f, cz + b.depth / 2f + 0.88f, 8.8f, 1.0f, 0.1f, floatArrayOf(0.20f, 0.60f, 1.0f, 1f))
                }
                b.type == BuildingType.GAS_STATION -> {
                    nightMb.addBox(cx, 4.22f, cz, b.width * 0.90f, 0.14f, b.depth * 0.90f, floatArrayOf(1.0f, 0.95f, 0.72f, 1f))
                }
                b.type == BuildingType.CAR_DEALERSHIP -> {
                    nightMb.addBox(cx, b.height + 1.8f, cz - b.depth / 2f + 1.18f, 26f, 2.2f, 0.16f, floatArrayOf(1.0f, 0.58f, 0.12f, 1f))
                }
                b.id == "nike_adidas_fashion_mall" -> {
                    val frontZ = cz + b.depth / 2f + 0.28f
                    nightMb.addBox(cx - 3.6f, b.height + 1.5f, frontZ - 0.36f, 6.4f, 2.2f, 0.16f, floatArrayOf(1.0f, 0.48f, 0.05f, 1f))
                    nightMb.addBox(cx + 3.6f, b.height + 1.5f, frontZ - 0.36f, 6.4f, 2.2f, 0.16f, floatArrayOf(0.18f, 0.56f, 1.0f, 1f))
                }
                b.id == "cinema_imax_complex" -> {
                    nightMb.addBox(cx, 8.5f, cz + b.depth / 2f + 2.15f, 16.0f, 1.2f, 0.12f, floatArrayOf(1.0f, 0.20f, 0.65f, 1f))
                    nightMb.addBox(cx - 5.2f, 14f, cz + b.depth / 2f + 0.32f, 4.6f, 7.5f, 0.12f, floatArrayOf(0.15f, 0.94f, 1.0f, 1f))
                    nightMb.addBox(cx + 5.2f, 14f, cz + b.depth / 2f + 0.32f, 4.6f, 7.5f, 0.12f, floatArrayOf(1.0f, 0.20f, 0.65f, 1f))
                }
                b.id == "street_houses_market" -> {
                    nightMb.addBox(cx, b.height + 3.2f, cz + 2.92f, 10.2f, 2.4f, 0.12f, floatArrayOf(0.12f, 0.92f, 0.58f, 1f))
                }
                b.id == "metro_card_sales_office" -> {
                    nightMb.addBox(cx, b.height + 3.2f, cz + 2.58f, 9.0f, 3.2f, 0.12f, floatArrayOf(0.12f, 0.94f, 1.0f, 1f))
                    nightMb.addBox(cx, 4.08f, cz + b.depth / 2f + 2.02f, 6.4f, 0.18f, 0.18f, floatArrayOf(0.14f, 0.96f, 1.0f, 1f))
                }
                b.id == "airport_control_tower" -> {
                    nightMb.addDome(cx, b.height + 5.2f, cz, 0.58f, floatArrayOf(1.0f, 0.15f, 0.15f, 1f))
                    nightMb.addBox(cx, b.height - 4f, cz, b.width * 1.38f, 3.6f, b.depth * 1.38f, floatArrayOf(0.28f, 0.85f, 1.0f, 1f))
                }
                b.streetZone == StreetZone.MODERN_TOWERS -> {
                    val neonCyan = floatArrayOf(0.10f, 0.94f, 1.0f, 1f)
                    nightMb.addBox(cx - b.width / 2f - 0.12f, b.height / 2f, cz + b.depth / 2f + 0.12f, 0.38f, b.height, 0.38f, neonCyan)
                    nightMb.addBox(cx + b.width / 2f + 0.12f, b.height / 2f, cz + b.depth / 2f + 0.12f, 0.38f, b.height, 0.38f, neonCyan)
                }
                b.streetZone == StreetZone.MAIN_STREET -> {
                    nightMb.addBox(cx, 2.2f, cz + b.depth / 2f + 0.22f, b.width * 0.82f, 2.8f, 0.08f, floatArrayOf(0.88f, 0.95f, 1.0f, 1f))
                }
            }
        }

        // 4. Street Lamps along all avenues and cross-streets throughout the entire metropolis
        val poleColor = floatArrayOf(0.28f, 0.30f, 0.34f, 1f)
        val fixtureHoodColor = floatArrayOf(0.18f, 0.20f, 0.22f, 1f)

        // North-South Avenues (both sides of all 11 avenues, trimmed inside city island)
        val allNsAvenues = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
        for (x in allNsAvenues) {
            val maxZ = if (x == 0f || x == -195f) 336 else 160
            for (z in -176..maxZ step 32) {
                // East side lamp
                val lx1 = x + 7.6f
                val lz1 = z.toFloat()
                mb.addCylinder(lx1, 0.1f, lz1, 0.18f, 7.5f, poleColor)
                mb.addBox(lx1 - 0.9f, 7.7f, lz1, 1.9f, 0.25f, 0.5f, poleColor)
                mb.addBox(lx1 - 1.3f, 7.68f, lz1, 0.75f, 0.12f, 0.75f, fixtureHoodColor)

                // West side lamp
                val lx2 = x - 7.6f
                val lz2 = z.toFloat()
                mb.addCylinder(lx2, 0.1f, lz2, 0.18f, 7.5f, poleColor)
                mb.addBox(lx2 + 0.9f, 7.7f, lz2, 1.9f, 0.25f, 0.5f, poleColor)
                mb.addBox(lx2 + 1.3f, 7.68f, lz2, 0.75f, 0.12f, 0.75f, fixtureHoodColor)
            }
        }

        // East-West Boulevards lamps (both curbs along every avenue)
        val allEwBoulevards = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)
        for (z in allEwBoulevards) {
            val maxLampX = if (z == 175f) 335 else 190
            for (x in -190..maxLampX step 40) {
                val lx = x.toFloat()
                val lz1 = z + 7.6f
                mb.addCylinder(lx, 0.1f, lz1, 0.18f, 7.5f, poleColor)
                mb.addBox(lx, 7.7f, lz1 - 0.9f, 0.5f, 0.25f, 1.9f, poleColor)
                mb.addBox(lx, 7.68f, lz1 - 1.3f, 0.75f, 0.12f, 0.75f, fixtureHoodColor)

                val lz2 = z - 7.6f
                mb.addCylinder(lx, 0.1f, lz2, 0.18f, 7.5f, poleColor)
                mb.addBox(lx, 7.7f, lz2 + 0.9f, 0.5f, 0.25f, 1.9f, poleColor)
                mb.addBox(lx, 7.68f, lz2 + 1.3f, 0.75f, 0.12f, 0.75f, fixtureHoodColor)
            }
        }

        // Villa Boulevards lamps (both curbs of Z = 256f & Z = 340f)
        for (z in listOf(256f, 340f)) {
            for (x in -190..340 step 40) {
                val lx = x.toFloat()
                val lz1 = z + 7.6f
                mb.addCylinder(lx, 0.1f, lz1, 0.18f, 7.5f, poleColor)
                mb.addBox(lx, 7.7f, lz1 - 0.9f, 0.5f, 0.25f, 1.9f, poleColor)
                mb.addBox(lx, 7.68f, lz1 - 1.3f, 0.75f, 0.12f, 0.75f, fixtureHoodColor)

                val lz2 = z - 7.6f
                mb.addCylinder(lx, 0.1f, lz2, 0.18f, 7.5f, poleColor)
                mb.addBox(lx, 7.7f, lz2 + 0.9f, 0.5f, 0.25f, 1.9f, poleColor)
                mb.addBox(lx, 7.68f, lz2 + 1.3f, 0.75f, 0.12f, 0.75f, fixtureHoodColor)
            }
        }

        // East Villa Road lamps (X = 345f)
        for (z in 192..320 step 32) {
            val lx1 = 345f + 7.6f
            val lz1 = z.toFloat()
            mb.addCylinder(lx1, 0.1f, lz1, 0.18f, 7.5f, poleColor)
            mb.addBox(lx1 - 0.9f, 7.7f, lz1, 1.9f, 0.25f, 0.5f, poleColor)
            mb.addBox(lx1 - 1.3f, 7.68f, lz1, 0.75f, 0.12f, 0.75f, fixtureHoodColor)

            val lx2 = 345f - 7.6f
            mb.addCylinder(lx2, 0.1f, lz1, 0.18f, 7.5f, poleColor)
            mb.addBox(lx2 + 0.9f, 7.7f, lz1, 1.9f, 0.25f, 0.5f, poleColor)
            mb.addBox(lx2 + 1.3f, 7.68f, lz1, 0.75f, 0.12f, 0.75f, fixtureHoodColor)
        }

        // Underground Garage Entrance Floodlights (أضواء مدخل كراج المدينة تحت الأرض)
        for (px in listOf(11f, 24f)) {
            for (pz in listOf(10f, 26f)) {
                mb.addCylinder(px, 0.1f, pz, 0.22f, 6.2f, poleColor)
                mb.addBox(px, 6.4f, pz, 1.5f, 0.36f, 0.9f, fixtureHoodColor)
            }
        }

        // 4.5. Realistic Street Furniture (Fire Hydrants, Benches, Bus Stops)
        val hydrantRed = floatArrayOf(0.92f, 0.15f, 0.15f, 1f)
        val benchWood = floatArrayOf(0.55f, 0.35f, 0.20f, 1f)
        val ironBlack = floatArrayOf(0.12f, 0.14f, 0.16f, 1f)
        val glassShelter = floatArrayOf(0.25f, 0.70f, 0.90f, 0.75f)

        // Fire Hydrants safely tucked along outer sidewalk grass edges (NEVER inside asphalt roads)
        val hydrantPositions = listOf(
            Pair(-44.5f, 12f), Pair(44.5f, 12f), Pair(-44.5f, 75f), Pair(44.5f, 75f),
            Pair(-84.5f, 12f), Pair(84.5f, 12f), Pair(-10.5f, 46f), Pair(10.5f, -40f)
        )
        for (hp in hydrantPositions) {
            val hx = hp.first
            val hz = hp.second
            mb.addCylinder(hx, 0.15f, hz, 0.22f, 0.85f, hydrantRed)
            mb.addDome(hx, 1.0f, hz, 0.22f, hydrantRed)
            mb.addBox(hx, 0.65f, hz, 0.65f, 0.15f, 0.15f, hydrantRed)
        }

        // Sidewalk Park Benches (strictly on open parks & outer lawns)
        val benchPositions = listOf(
            Pair(15f, 45f), Pair(22f, 45f), Pair(-15f, 45f), Pair(-22f, 45f),
            Pair(-55f, 45f), Pair(-62f, 45f), Pair(95f, 45f), Pair(105f, 45f),
            Pair(175f, -15f), Pair(185f, -15f), Pair(95f, 118f)
        )
        for (bp in benchPositions) {
            val bx = bp.first
            val bz = bp.second
            // Wooden seat slab
            mb.addBox(bx, 0.45f, bz, 2.2f, 0.08f, 0.7f, benchWood)
            // Wooden backrest
            mb.addBox(bx, 0.75f, bz - 0.30f, 2.2f, 0.45f, 0.08f, benchWood)
            // Cast iron legs
            mb.addBox(bx - 0.9f, 0.22f, bz, 0.08f, 0.44f, 0.65f, ironBlack)
            mb.addBox(bx + 0.9f, 0.22f, bz, 0.08f, 0.44f, 0.65f, ironBlack)
        }

        // Modern Glass-Sheltered Bus Stops cleanly setback off the roadway & sidewalks
        val busStopPositions = listOf(
            Pair(-46.8f, 18f), Pair(46.8f, 48f), Pair(-86.8f, -15f), Pair(86.8f, -15f)
        )
        for (bs in busStopPositions) {
            val sx = bs.first
            val sz = bs.second
            // Concrete base pad
            mb.addBox(sx, 0.16f, sz, 4.5f, 0.04f, 2.2f, sidewalkColor)
            // Glass back panel
            mb.addBox(sx, 1.6f, sz - 0.9f, 4.2f, 2.8f, 0.08f, glassShelter)
            // Modern steel canopy roof
            mb.addBox(sx, 3.0f, sz, 4.6f, 0.15f, 2.4f, ironBlack)
            // Steel support columns
            mb.addCylinder(sx - 2.1f, 0.16f, sz + 0.9f, 0.08f, 2.8f, ironBlack)
            mb.addCylinder(sx + 2.1f, 0.16f, sz + 0.9f, 0.08f, 2.8f, ironBlack)
        }

        // 5. Trees strictly on Green Grass Lawns, Parks & Villa Gardens (NEVER in any street or sidewalk!)
        val trunkColor = floatArrayOf(0.42f, 0.28f, 0.14f, 1f)
        val foliageColor = floatArrayOf(0.18f, 0.58f, 0.22f, 1f)
        val foliageDark = floatArrayOf(0.12f, 0.44f, 0.16f, 1f)
        val lawnPatchColor = floatArrayOf(0.16f, 0.52f, 0.20f, 1f)
        val pathCobble = floatArrayOf(0.72f, 0.70f, 0.65f, 1f)
        val palmFrondColor = floatArrayOf(0.22f, 0.65f, 0.25f, 1f)

        // Strict guard: never allow any tree or palm inside a road, sidewalk, or building collider
        fun isClearGrassTreeSpot(x: Float, z: Float): Boolean {
            val hitsNsRoad = nsRoads.any { rx ->
                val maxZ = if (rx == 0f || rx == -195f) 348f else 182f
                z <= maxZ + 9.8f && kotlin.math.abs(x - rx) < 9.8f
            } || (z > 180f && kotlin.math.abs(x - 345f) < 9.8f)
            val hitsEwRoad = ewRoads.any { rz -> kotlin.math.abs(z - rz) < 9.8f } ||
                (kotlin.math.abs(z - 256f) < 9.8f || kotlin.math.abs(z - 340f) < 9.8f)
            if (hitsNsRoad || hitsEwRoad) return false
            return buildings.none { b ->
                b.getCollisionBoxes().any { box ->
                    kotlin.math.abs(x - box.centerX) < (box.width / 2f + 2.0f) &&
                        kotlin.math.abs(z - box.centerZ) < (box.depth / 2f + 2.0f)
                }
            }
        }

        // Shaded Pedestrian Walkway Pavers on wide green lawns between villas in the Villa Sector
        for (vx in listOf(-122f, -58f, 94f, 178f, 261f)) {
            mb.addBox(vx, 0.05f, 216f, 4.5f, 0.02f, 52f, pathCobble)
            mb.addBox(vx, 0.05f, 298f, 4.5f, 0.02f, 52f, pathCobble)
        }

        // Ornamental Palm Trees on Grass Lawns framing Downtown & Parks (all clear of roads!)
        val grassPalmPositions = listOf(
            Pair(12.5f, -12.5f), Pair(23.5f, -12.5f), Pair(-55.0f, -12.5f), Pair(95.0f, -12.5f),
            Pair(-23.5f, 44.0f), Pair(-12.5f, 44.0f), Pair(-55.0f, 44.0f), Pair(17.5f, 44.0f)
        )
        for ((px, pz) in grassPalmPositions) {
            if (!isClearGrassTreeSpot(px, pz)) continue
            mb.addCylinder(px, 0.01f, pz, 1.8f, 0.04f, lawnPatchColor)
            mb.addCylinder(px, 0.04f, pz, 0.25f, 5.5f, trunkColor)
            mb.addDome(px, 5.5f, pz, 2.2f, palmFrondColor)
            mb.addDome(px, 6.1f, pz, 1.4f, foliageColor)
        }

        // Trees on Open Checkerboard Grass Parks, Lawns & Spacious Villa Gardens
        val treePositions = listOf(
            Pair(15f, 48f), Pair(22f, 52f), Pair(-55f, 48f), Pair(-62f, 52f),
            Pair(-15f, 48f), Pair(-22f, 52f), Pair(-95f, 18f), Pair(-55f, -15f),
            Pair(95f, 48f), Pair(105f, 52f), Pair(95f, 118f), Pair(135f, 82.5f),
            // Shaded trees on wide grassy alleys separating all villas & Southwest houses (Row 1 & Row 2)
            Pair(-122f, 202f), Pair(-122f, 224f), Pair(-58f, 202f), Pair(-58f, 224f),
            Pair(94f, 202f), Pair(94f, 224f), Pair(178f, 202f), Pair(178f, 224f),
            Pair(261f, 202f), Pair(261f, 224f),
            Pair(-122f, 284f), Pair(-122f, 306f), Pair(-58f, 284f), Pair(-58f, 306f),
            Pair(94f, 284f), Pair(94f, 306f), Pair(178f, 284f), Pair(178f, 306f),
            Pair(261f, 284f), Pair(261f, 306f),
            // East waterfront marina & campus grass lawns (completely off city streets!)
            Pair(208f, -90f), Pair(208f, -135f), Pair(265f, -90f), Pair(265f, -135f), Pair(268f, -20f)
        )
        for (pos in treePositions) {
            val tx = pos.first
            val tz = pos.second
            if (!isClearGrassTreeSpot(tx, tz)) continue
            mb.addCylinder(tx, 0.01f, tz, 2.0f, 0.04f, lawnPatchColor)
            mb.addCylinder(tx, 0.04f, tz, 0.4f, 4.5f, trunkColor)
            mb.addDome(tx, 4.0f, tz, 2.6f, foliageColor)
            mb.addDome(tx, 5.8f, tz, 1.9f, foliageDark)
        }

        // 6. Traffic Light Poles & Housings (cleanly at corner curbs ±7.2m outside 6.25m asphalt)
        for (tl in trafficLights) {
            mb.addCylinder(tl.posX + 7.2f, 0.1f, tl.posZ + 7.2f, 0.2f, 6.5f, poleColor)
            mb.addBox(tl.posX + 7.2f, 6.2f, tl.posZ + 7.2f, 0.7f, 1.8f, 0.7f, floatArrayOf(0.15f, 0.15f, 0.15f, 1f))
            mb.addCylinder(tl.posX - 7.2f, 0.1f, tl.posZ - 7.2f, 0.2f, 6.5f, poleColor)
            mb.addBox(tl.posX - 7.2f, 6.2f, tl.posZ - 7.2f, 0.7f, 1.8f, 0.7f, floatArrayOf(0.15f, 0.15f, 0.15f, 1f))
        }

        // Lighthouse & pier warm navigation beacons at night
        val navGlow = floatArrayOf(1.0f, 0.92f, 0.45f, 1f)
        nightMb.addCylinder(396f, 24.75f, 392f, 1.88f, 2.8f, navGlow, segments = 10)
        for (pz in -254 downTo -296 step 8) {
            nightMb.addBox(-2.6f, 1.05f, pz.toFloat(), 0.42f, 0.42f, 0.42f, navGlow)
            nightMb.addBox(2.6f, 1.05f, pz.toFloat(), 0.42f, 0.42f, 0.42f, navGlow)
        }
        for (pz in 410..452 step 8) {
            nightMb.addBox(172.4f, 1.05f, pz.toFloat(), 0.42f, 0.42f, 0.42f, navGlow)
            nightMb.addBox(177.6f, 1.05f, pz.toFloat(), 0.42f, 0.42f, 0.42f, navGlow)
        }

        // 7. Compound 90 Avenue («كومباوند 90 أفينيو»: walls, arches, private sea lagoon & Olympic pool)
        //    and Elevated Electric Sky Metro («مترو المدينة الكهربائي المعلق» at Y=10.5m, stairs & 5 EGP booths)
        SkyMetroAndCompoundMeshes.buildCompoundAvenue90(mb, nightMb, waterMb)
        SkyMetroAndCompoundMeshes.buildSkyMetroInfrastructure(mb, nightMb)

        val builtNight = nightMb.build()
        val nightCount = nightMb.getVertexCount()
        worldNightEmissiveVertexCount = nightCount
        worldNightEmissiveBuffer = builtNight

        val builtWater = waterMb.build()
        val waterCount = waterMb.getVertexCount()
        worldWaterVertexCount = waterCount
        worldWaterBuffer = builtWater

        val builtStatic = mb.build()
        val staticCount = mb.getVertexCount()
        worldStaticVertexCount = staticCount
        worldStaticBuffer = builtStatic
    }

    /**
     * Builds an unmistakable 3D Cyber Robot seated at the steering wheel inside AI vehicles.
     */
    private fun addSeatedRobotDriver(
        mb: MeshBuilder,
        driverX: Float = -0.36f,
        seatBaseY: Float = 0.50f,
        seatZ: Float = 0.10f,
        wheelZ: Float = 0.42f
    ) {
        val robotSilver = floatArrayOf(0.84f, 0.88f, 0.94f, 1f)
        val robotJointDark = floatArrayOf(0.18f, 0.21f, 0.27f, 1f)
        val visorCyan = floatArrayOf(0.05f, 0.95f, 1.0f, 1f)
        val coreGlow = floatArrayOf(0.0f, 0.82f, 1.0f, 1f)

        // Seated robot chassis / legs inside footwell
        mb.addBox(driverX - 0.09f, seatBaseY - 0.04f, seatZ + 0.18f, 0.13f, 0.14f, 0.34f, robotJointDark)
        mb.addBox(driverX + 0.09f, seatBaseY - 0.04f, seatZ + 0.18f, 0.13f, 0.14f, 0.34f, robotJointDark)

        // Metallic armor chest + glowing cyber reactor core on chest & upper back
        val torsoY = seatBaseY + 0.22f
        val jacketNavy = floatArrayOf(0.09f, 0.14f, 0.24f, 1f)
        // Cyber Robot Tactical Jacket Vest worn by robot driver
        mb.addBox(driverX, torsoY, seatZ, 0.40f, 0.36f, 0.26f, jacketNavy)
        mb.addBox(driverX - 0.13f, torsoY + 0.15f, seatZ + 0.08f, 0.10f, 0.10f, 0.14f, coreGlow) // glowing lapel
        mb.addBox(driverX + 0.13f, torsoY + 0.15f, seatZ + 0.08f, 0.10f, 0.10f, 0.14f, coreGlow)
        mb.addBox(driverX, torsoY + 0.02f, seatZ + 0.135f, 0.04f, 0.28f, 0.02f, visorCyan) // front cyber zip
        mb.addBox(driverX, torsoY + 0.04f, seatZ - 0.135f, 0.18f, 0.14f, 0.02f, coreGlow)
        // Jacket shoulder pauldrons & cyber sleeves
        mb.addBox(driverX - 0.22f, torsoY + 0.12f, seatZ, 0.11f, 0.14f, 0.23f, robotJointDark)
        mb.addBox(driverX + 0.22f, torsoY + 0.12f, seatZ, 0.11f, 0.14f, 0.23f, robotJointDark)

        // Articulated robot head rising cleanly above door line so player clearly sees the robot driving!
        val headY = torsoY + 0.34f
        mb.addBox(driverX, headY, seatZ + 0.01f, 0.28f, 0.26f, 0.26f, robotSilver)
        mb.addBox(driverX, headY - 0.09f, seatZ + 0.02f, 0.24f, 0.07f, 0.24f, robotJointDark) // chin plate
        // Full wrap-around glowing cyan LED robot visor + twin eye accents
        mb.addBox(driverX, headY + 0.03f, seatZ + 0.145f, 0.24f, 0.09f, 0.025f, visorCyan)
        mb.addBox(driverX - 0.145f, headY + 0.03f, seatZ + 0.04f, 0.02f, 0.07f, 0.14f, visorCyan)
        mb.addBox(driverX + 0.145f, headY + 0.03f, seatZ + 0.04f, 0.02f, 0.07f, 0.14f, visorCyan)
        // Top glowing cyber antenna + beacon sphere
        mb.addCylinder(driverX, headY + 0.13f, seatZ, 0.025f, 0.16f, robotJointDark, segments = 6)
        mb.addDome(driverX, headY + 0.29f, seatZ, 0.055f, visorCyan, slices = 6, stacks = 2)

        // Robot jacket arms reaching forward to grip steering wheel
        mb.addBox(driverX - 0.17f, torsoY + 0.04f, (seatZ + wheelZ) / 2f, 0.10f, 0.11f, 0.30f, jacketNavy)
        mb.addBox(driverX + 0.17f, torsoY + 0.04f, (seatZ + wheelZ) / 2f, 0.10f, 0.11f, 0.30f, jacketNavy)
        mb.addBox(driverX - 0.14f, torsoY + 0.04f, wheelZ - 0.02f, 0.08f, 0.09f, 0.09f, visorCyan)
        mb.addBox(driverX + 0.14f, torsoY + 0.04f, wheelZ - 0.02f, 0.08f, 0.09f, 0.09f, visorCyan)

        // Steering wheel gripped by the robot
        mb.addBox(driverX, torsoY + 0.06f, wheelZ, 0.28f, 0.28f, 0.04f, robotJointDark)
        mb.addBox(driverX, torsoY + 0.06f, wheelZ + 0.01f, 0.09f, 0.09f, 0.04f, visorCyan)
    }

    /**
     * Builds an animated full-body 3D Cyber Robot character dressed in a matching Cyber Robot Jacket
     * for autonomous robots when they step OUT of their car and walk around the city.
     */
    fun buildRobotMesh(
        walkPhase: Float,
        wavePhase: Float = 0f,
        accentColor: FloatArray = floatArrayOf(0.05f, 0.95f, 1.0f, 1f),
        isSeated: Boolean = false
    ): Pair<FloatBuffer, Int> {
        val twoPi = 6.2831855f
        val normWalk = ((walkPhase % twoPi) + twoPi) % twoPi
        val walkBin = ((normWalk * 16f / twoPi).toInt()) and 15
        val waveBin = if (wavePhase <= 0.01f) {
            0
        } else {
            val normWave = ((wavePhase % twoPi) + twoPi) % twoPi
            1 + (((normWave * 12f / twoPi).toInt()) % 12)
        }
        val ar = (accentColor[0] * 255f).toInt().coerceIn(0, 255)
        val ag = (accentColor[1] * 255f).toInt().coerceIn(0, 255)
        val ab = (accentColor[2] * 255f).toInt().coerceIn(0, 255)
        val key = "$walkBin|$waveBin|$ar|$ag|$ab|$isSeated"
        robotMeshCache[key]?.let { return it }

        val qWalk = walkBin * (twoPi / 16f)
        val qWave = if (waveBin == 0) 0f else (waveBin - 1) * (twoPi / 12f) + 0.05f

        val mb = MeshBuilder(initialCapacityVertices = 360)
        val silver = floatArrayOf(0.84f, 0.88f, 0.94f, 1f)
        val titaniumDark = floatArrayOf(0.18f, 0.21f, 0.27f, 1f)
        val jacketNavy = floatArrayOf(0.09f, 0.14f, 0.24f, 1f)
        val slateJoint = floatArrayOf(0.30f, 0.35f, 0.44f, 1f)
        val legSwing = if (isSeated) 0f else sin(qWalk.toDouble()).toFloat() * 0.26f
        val armSwing = -legSwing * 1.15f
        val sitDrop = if (isSeated) 0.26f else 0f

        // 1. Cyber Robot Head with glowing 360° LED visor + antenna beacon
        val headY = 1.62f - sitDrop
        mb.addBox(0f, headY, 0f, 0.40f, 0.36f, 0.38f, silver)
        mb.addBox(0f, headY - 0.12f, 0.02f, 0.34f, 0.09f, 0.34f, titaniumDark)
        mb.addBox(0f, headY + 0.04f, 0.20f, 0.32f, 0.11f, 0.025f, accentColor)
        mb.addBox(-0.205f, headY + 0.04f, 0.04f, 0.02f, 0.09f, 0.18f, accentColor)
        mb.addBox(0.205f, headY + 0.04f, 0.04f, 0.02f, 0.09f, 0.18f, accentColor)
        mb.addCylinder(0f, headY + 0.18f, 0f, 0.03f, 0.22f, titaniumDark, segments = 6)
        mb.addDome(0f, headY + 0.40f, 0f, 0.07f, accentColor, slices = 6, stacks = 2)

        // 2. Neck servo & Cyber Robot Jacket / Tactical Vest worn over metallic torso
        mb.addCylinder(0f, 1.38f - sitDrop, 0f, 0.09f, 0.12f, slateJoint, segments = 6)
        val torsoY = 1.06f - sitDrop
        mb.addBox(0f, torsoY, 0f, 0.60f, 0.66f, 0.38f, jacketNavy) // Outer Cyber Jacket Shell
        // High standing jacket collar & bright cyber accent lapels
        mb.addBox(-0.18f, torsoY + 0.30f, 0.10f, 0.16f, 0.12f, 0.22f, accentColor)
        mb.addBox(0.18f, torsoY + 0.30f, 0.10f, 0.16f, 0.12f, 0.22f, accentColor)
        // Center cyber zip + chest reactor core emblem + jacket hem
        mb.addBox(0f, torsoY, 0.198f, 0.045f, 0.60f, 0.02f, accentColor)
        mb.addBox(0.16f, torsoY + 0.12f, 0.198f, 0.14f, 0.06f, 0.02f, silver)
        mb.addBox(-0.16f, torsoY + 0.12f, 0.198f, 0.14f, 0.06f, 0.02f, accentColor)
        mb.addBox(0f, torsoY - 0.30f, 0f, 0.61f, 0.08f, 0.39f, titaniumDark)
        // Back tactical jacket power cell & glowing stripe
        mb.addBox(0f, torsoY + 0.04f, -0.21f, 0.34f, 0.38f, 0.08f, titaniumDark)
        mb.addBox(0f, torsoY + 0.08f, -0.255f, 0.22f, 0.12f, 0.02f, accentColor)
        mb.addBox(0f, 0.72f, 0f, 0.46f, 0.12f, 0.28f, titaniumDark)

        // 3. Jacket Sleeves with Neon Cyber Stripes & Robot Hands
        mb.addBox(-0.39f, torsoY + 0.24f, 0f, 0.18f, 0.18f, 0.30f, titaniumDark)
        mb.addBox(0.39f, torsoY + 0.24f, 0f, 0.18f, 0.18f, 0.30f, titaniumDark)
        // Left arm (Jacket sleeve + cyber cuff + silver hand)
        mb.addBox(-0.41f, torsoY + 0.01f, -armSwing, 0.20f, 0.44f, 0.22f, jacketNavy)
        mb.addBox(-0.515f, torsoY + 0.01f, -armSwing, 0.02f, 0.38f, 0.08f, accentColor)
        mb.addBox(-0.41f, torsoY - 0.28f, -armSwing, 0.16f, 0.16f, 0.18f, silver)
        // Right arm (raises smoothly up to wave if waveBin > 0)
        if (waveBin > 0) {
            val waveOsc = sin(qWave.toDouble()).toFloat() * 0.14f
            mb.addBox(0.44f + waveOsc * 0.5f, torsoY + 0.44f, 0.12f, 0.20f, 0.42f, 0.22f, jacketNavy)
            mb.addBox(0.44f + waveOsc, torsoY + 0.70f, 0.14f, 0.16f, 0.14f, 0.18f, accentColor)
        } else {
            mb.addBox(0.41f, torsoY + 0.01f, armSwing, 0.20f, 0.44f, 0.22f, jacketNavy)
            mb.addBox(0.515f, torsoY + 0.01f, armSwing, 0.02f, 0.38f, 0.08f, accentColor)
            mb.addBox(0.41f, torsoY - 0.28f, armSwing, 0.16f, 0.16f, 0.18f, silver)
        }

        // 4. Articulated Titanium Legs & Cyber Boots (Standing or Bent Seated)
        if (isSeated) {
            for (side in listOf(-1f, 1f)) {
                val lx = side * 0.15f
                mb.addBox(lx, 0.35f, 0.18f, 0.22f, 0.22f, 0.38f, silver)
                mb.addBox(lx, 0.16f, 0.34f, 0.20f, 0.26f, 0.22f, silver)
                mb.addBox(lx, 0.05f, 0.38f, 0.23f, 0.10f, 0.28f, titaniumDark)
            }
        } else {
            for (side in listOf(-1f, 1f)) {
                val lx = side * 0.15f
                val lz = if (side < 0f) legSwing else -legSwing
                mb.addBox(lx, 0.52f, lz * 0.7f, 0.21f, 0.32f, 0.23f, silver)
                mb.addBox(lx, 0.35f, lz, 0.18f, 0.10f, 0.21f, titaniumDark)
                mb.addBox(lx, 0.20f, lz, 0.20f, 0.26f, 0.22f, silver)
                mb.addBox(lx, 0.05f, lz + 0.03f, 0.23f, 0.10f, 0.31f, titaniumDark)
                mb.addBox(lx, 0.08f, lz + 0.18f, 0.16f, 0.04f, 0.02f, accentColor)
            }
        }

        val built = Pair(mb.build(), mb.getVertexCount())
        if (robotMeshCache.size >= 256) robotMeshCache.clear()
        robotMeshCache[key] = built
        return built
    }

    /**
     * Builds and caches geometry for each vehicle type and specialized dealership mesh styles.
     */
    fun getVehicleMesh(
        type: VehicleType,
        primaryColor: FloatArray,
        secondaryColor: FloatArray,
        meshStyle: CarMeshStyle? = null,
        isNitroActive: Boolean = false,
        underglowColor: FloatArray? = null,
        hasRobotDriver: Boolean = false
    ): Pair<FloatBuffer, Int> {
        val cacheKey = buildString {
            append(type.ordinal).append('|')
            append(meshStyle?.ordinal ?: -1).append('|')
            append(isNitroActive).append('|')
            append(hasRobotDriver).append('|')
            append((primaryColor[0] * 100).toInt()).append(',').append((primaryColor[1] * 100).toInt()).append(',').append((primaryColor[2] * 100).toInt()).append('|')
            append((secondaryColor[0] * 100).toInt()).append(',').append((secondaryColor[1] * 100).toInt()).append(',').append((secondaryColor[2] * 100).toInt()).append('|')
            if (underglowColor != null) {
                append((underglowColor[0] * 100).toInt()).append(',').append((underglowColor[1] * 100).toInt()).append(',').append((underglowColor[2] * 100).toInt())
            }
        }
        dynamicVehicleMeshCache[cacheKey]?.let { return it }

        val mb = MeshBuilder(initialCapacityVertices = 640)
        val glassColor = floatArrayOf(0.2f, 0.35f, 0.5f, 0.9f)
        val wheelColor = floatArrayOf(0.1f, 0.1f, 0.12f, 1f)
        val rimColor = floatArrayOf(0.85f, 0.85f, 0.88f, 1f)
        val headlightColor = floatArrayOf(1f, 0.98f, 0.8f, 1f)
        val tailLightColor = floatArrayOf(0.95f, 0.1f, 0.1f, 1f)

        if (meshStyle != null) {
            when (meshStyle) {
                CarMeshStyle.VINTAGE_OLD -> {
                    // Classic rounded vintage body (e.g. 1974 Beetle / classic retro)
                    mb.addBox(0f, 0.48f, 0f, 1.78f, 0.52f, 3.9f, primaryColor)
                    // Curved rounded roof bubble
                    mb.addBox(0f, 0.92f, -0.2f, 1.45f, 0.42f, 1.9f, primaryColor)
                    mb.addDome(0f, 1.15f, -0.2f, 0.75f, primaryColor, slices = 8, stacks = 3)
                    // Windows
                    mb.addBox(0f, 0.92f, 0.76f, 1.35f, 0.32f, 0.05f, glassColor) // front
                    mb.addBox(0f, 0.92f, -1.16f, 1.35f, 0.32f, 0.05f, glassColor) // rear
                    // Chrome bumpers
                    mb.addBox(0f, 0.32f, 2.02f, 1.82f, 0.12f, 0.15f, rimColor)
                    mb.addBox(0f, 0.32f, -2.02f, 1.82f, 0.12f, 0.15f, rimColor)
                }
                CarMeshStyle.SUV_OFFROAD -> {
                    // Rugged elevated 4x4 offroad body (e.g. Land Cruiser GR)
                    mb.addBox(0f, 0.75f, 0f, 2.1f, 0.75f, 4.8f, primaryColor)
                    // Cabin & large tinted windows
                    mb.addBox(0f, 1.48f, -0.2f, 1.95f, 0.72f, 3.1f, glassColor)
                    mb.addBox(0f, 1.86f, -0.2f, 1.9f, 0.08f, 3.0f, secondaryColor)
                    // Rugged front bull bar & dark grille
                    mb.addBox(0f, 0.65f, 2.45f, 1.6f, 0.45f, 0.15f, secondaryColor)
                    // Roof expedition rails
                    mb.addBox(-0.88f, 1.95f, -0.2f, 0.08f, 0.12f, 2.6f, secondaryColor)
                    mb.addBox(0.88f, 1.95f, -0.2f, 0.08f, 0.12f, 2.6f, secondaryColor)
                    // Spare wheel on rear tailgate
                    mb.addBox(0f, 1.1f, -2.52f, 0.28f, 0.72f, 0.72f, wheelColor)
                }
                CarMeshStyle.MUSCLE_GT -> {
                    // Long aggressive muscular hood & fastback silhouette (e.g. Mustang Dark Horse)
                    mb.addBox(0f, 0.52f, 0f, 1.95f, 0.52f, 4.6f, primaryColor)
                    // Hood air induction scoop
                    mb.addBox(0f, 0.82f, 1.25f, 0.65f, 0.15f, 0.75f, secondaryColor)
                    // Dual bold racing stripes
                    mb.addBox(-0.25f, 0.54f, 0f, 0.22f, 0.53f, 4.62f, secondaryColor)
                    mb.addBox(0.25f, 0.54f, 0f, 0.22f, 0.53f, 4.62f, secondaryColor)
                    if (hasRobotDriver) {
                        // Open panoramic cockpit + A/C pillars + roof arch so Robot Driver is clearly visible
                        mb.addBox(0f, 1.24f, -0.55f, 1.48f, 0.07f, 1.15f, secondaryColor)
                        mb.addBox(-0.72f, 0.96f, 0.58f, 0.08f, 0.50f, 0.10f, secondaryColor)
                        mb.addBox(0.72f, 0.96f, 0.58f, 0.08f, 0.50f, 0.10f, secondaryColor)
                        mb.addBox(-0.72f, 0.96f, -1.05f, 0.08f, 0.50f, 0.14f, secondaryColor)
                        mb.addBox(0.72f, 0.96f, -1.05f, 0.08f, 0.50f, 0.14f, secondaryColor)
                        addSeatedRobotDriver(mb, -0.36f, 0.62f, -0.05f, 0.30f)
                    } else {
                        mb.addBox(0f, 0.98f, -0.3f, 1.58f, 0.46f, 2.0f, glassColor)
                    }
                    // Rear fastback ducktail spoiler
                    mb.addBox(0f, 0.85f, -2.25f, 1.75f, 0.14f, 0.35f, secondaryColor)
                }
                CarMeshStyle.SUPER_SPORT -> {
                    // Low mid-engine aerodynamic supercar (e.g. GT-R Nismo / Porsche GT3 RS / F40)
                    mb.addBox(0f, 0.42f, 0f, 2.02f, 0.44f, 4.5f, primaryColor)
                    // Front air splitter
                    mb.addBox(0f, 0.22f, 2.26f, 2.08f, 0.06f, 0.35f, secondaryColor)
                    if (hasRobotDriver) {
                        // Open panoramic cockpit with slim carbon pillars so Robot Driver is 100% visible!
                        mb.addBox(0f, 1.16f, -0.45f, 1.42f, 0.06f, 1.15f, secondaryColor)
                        mb.addBox(-0.70f, 0.86f, 0.72f, 0.08f, 0.46f, 0.10f, secondaryColor)
                        mb.addBox(0.70f, 0.86f, 0.72f, 0.08f, 0.46f, 0.10f, secondaryColor)
                        mb.addBox(-0.70f, 0.86f, -0.98f, 0.08f, 0.46f, 0.14f, secondaryColor)
                        mb.addBox(0.70f, 0.86f, -0.98f, 0.08f, 0.46f, 0.14f, secondaryColor)
                        addSeatedRobotDriver(mb, -0.36f, 0.56f, 0.02f, 0.38f)
                    } else {
                        // Low-profile glass canopy
                        mb.addBox(0f, 0.82f, -0.1f, 1.48f, 0.42f, 2.0f, glassColor)
                        mb.addBox(0f, 1.05f, -0.1f, 1.42f, 0.06f, 1.9f, secondaryColor)
                    }
                    // Side carbon cooling vents
                    mb.addBox(-1.03f, 0.45f, -0.6f, 0.08f, 0.28f, 1.1f, secondaryColor)
                    mb.addBox(1.03f, 0.45f, -0.6f, 0.08f, 0.28f, 1.1f, secondaryColor)
                    // High-downforce carbon rear wing on dual struts
                    mb.addBox(0f, 1.18f, -2.15f, 2.05f, 0.06f, 0.48f, secondaryColor)
                    mb.addBox(-0.65f, 0.85f, -2.15f, 0.08f, 0.6f, 0.08f, secondaryColor)
                    mb.addBox(0.65f, 0.85f, -2.15f, 0.08f, 0.6f, 0.08f, secondaryColor)
                }
                CarMeshStyle.HYPER_AERO -> {
                    // Extreme carbon aerodynamic track hypercar (e.g. Centodieci / Chiron / Valkyrie)
                    mb.addBox(0f, 0.38f, 0f, 2.12f, 0.38f, 4.65f, primaryColor)
                    // Front carbon canards and ground splitter
                    mb.addBox(0f, 0.2f, 2.35f, 2.2f, 0.05f, 0.45f, secondaryColor)
                    mb.addBox(-1.08f, 0.38f, 2.0f, 0.06f, 0.25f, 0.4f, secondaryColor)
                    mb.addBox(1.08f, 0.38f, 2.0f, 0.06f, 0.25f, 0.4f, secondaryColor)
                    // Roof-mounted ram air intake scoop
                    mb.addBox(0f, 1.18f, -0.15f, 0.45f, 0.25f, 1.1f, secondaryColor)
                    // Low cockpit bubble
                    mb.addBox(0f, 0.78f, 0.1f, 1.42f, 0.42f, 1.9f, glassColor)
                    // Massive swan-neck active GT wing
                    mb.addBox(0f, 1.28f, -2.25f, 2.28f, 0.06f, 0.58f, secondaryColor)
                    mb.addBox(-1.15f, 1.28f, -2.25f, 0.04f, 0.38f, 0.62f, secondaryColor) // vertical endplates
                    mb.addBox(1.15f, 1.28f, -2.25f, 0.04f, 0.38f, 0.62f, secondaryColor)
                    // Rear diffuser strakes
                    mb.addBox(0f, 0.22f, -2.3f, 1.85f, 0.18f, 0.35f, secondaryColor)
                }
                CarMeshStyle.ULTIMATE_STEALTH -> {
                    // Cutting-edge stealth fighter hypercar (e.g. Jesko Absolut 500 / La Voiture Noire)
                    mb.addBox(0f, 0.35f, 0f, 2.18f, 0.36f, 4.75f, primaryColor)
                    // Long aerodynamic nose cone
                    mb.addBox(0f, 0.32f, 2.25f, 1.6f, 0.24f, 0.9f, primaryColor)
                    // Low wraparound cockpit canopy
                    mb.addBox(0f, 0.72f, 0.15f, 1.38f, 0.38f, 1.85f, glassColor)
                    // Center dorsal stabilization spine / shark fin
                    mb.addBox(0f, 0.92f, -0.95f, 0.04f, 0.36f, 2.1f, secondaryColor)
                    // Sleek low-drag longtail aero extension
                    mb.addBox(0f, 0.45f, -2.35f, 1.95f, 0.16f, 0.65f, secondaryColor)
                    // 6 exhaust pipes cluster
                    mb.addBox(0f, 0.46f, -2.42f, 0.8f, 0.14f, 0.1f, rimColor)
                }
                CarMeshStyle.ROBLOX_GAMER_BLOX -> {
                    // === Authentic Roblox Gamer Blox R-1 Low-Poly Sports Car ===
                    val cyanNeon = underglowColor ?: floatArrayOf(0.08f, 0.85f, 1.0f, 1.0f)
                    val darkCarbon = secondaryColor // Matte black racing carbon
                    val cyanFlame = floatArrayOf(0.1f, 0.9f, 1.0f, 0.95f)
                    val deepBlueFlame = floatArrayOf(0.1f, 0.4f, 1.0f, 0.95f)

                    // 1. Lower chassis & sculpted side rocker panels
                    mb.addBox(0f, 0.34f, 0f, 2.08f, 0.32f, 4.5f, primaryColor)
                    mb.addBox(-1.06f, 0.20f, 0f, 0.10f, 0.10f, 3.4f, darkCarbon)
                    mb.addBox(1.06f, 0.20f, 0f, 0.10f, 0.10f, 3.4f, darkCarbon)

                    // 2. Electric Cyan Neon Underglow (Side strips and lower underbody)
                    mb.addBox(-1.08f, 0.16f, 0f, 0.06f, 0.04f, 3.0f, cyanNeon)
                    mb.addBox(1.08f, 0.16f, 0f, 0.06f, 0.04f, 3.0f, cyanNeon)
                    mb.addBox(0f, 0.14f, 1.3f, 1.7f, 0.03f, 0.08f, cyanNeon)
                    mb.addBox(0f, 0.14f, -1.3f, 1.7f, 0.03f, 0.08f, cyanNeon)

                    // 3. Front Wedge Hood & Aggressive Front Splitter («بوز السيارة الأمامي»)
                    // Sloping front hood body
                    mb.addBox(0f, 0.62f, 0.90f, 1.86f, 0.14f, 0.70f, primaryColor)
                    mb.addBox(0f, 0.54f, 1.50f, 1.92f, 0.14f, 0.65f, primaryColor)
                    mb.addBox(0f, 0.46f, 2.05f, 1.96f, 0.14f, 0.55f, primaryColor)

                    // Raised muscular front fender ridges on left and right
                    mb.addBox(-0.84f, 0.58f, 1.45f, 0.30f, 0.08f, 1.65f, primaryColor)
                    mb.addBox(0.84f, 0.58f, 1.45f, 0.30f, 0.08f, 1.65f, primaryColor)

                    // Black carbon center hood racing panel running down the nose
                    mb.addBox(0f, 0.55f, 1.45f, 0.98f, 0.05f, 1.70f, darkCarbon)
                    // Dual aerodynamic carbon hood scoops / vents with recessed openings
                    mb.addBox(-0.30f, 0.60f, 1.55f, 0.22f, 0.06f, 0.55f, darkCarbon)
                    mb.addBox(-0.30f, 0.60f, 1.82f, 0.16f, 0.04f, 0.04f, floatArrayOf(0.02f, 0.02f, 0.03f, 1f))
                    mb.addBox(0.30f, 0.60f, 1.55f, 0.22f, 0.06f, 0.55f, darkCarbon)
                    mb.addBox(0.30f, 0.60f, 1.82f, 0.16f, 0.04f, 0.04f, floatArrayOf(0.02f, 0.02f, 0.03f, 1f))

                    // Front lower chin splitter (Black carbon aero lip)
                    mb.addBox(0f, 0.14f, 2.45f, 2.16f, 0.05f, 0.26f, darkCarbon)
                    // Splitter side winglets angled upward
                    mb.addBox(-1.06f, 0.20f, 2.38f, 0.08f, 0.12f, 0.22f, darkCarbon)
                    mb.addBox(1.06f, 0.20f, 2.38f, 0.08f, 0.12f, 0.22f, darkCarbon)
                    // Continuous glowing electric cyan-blue neon strip across front splitter lip
                    mb.addBox(0f, 0.14f, 2.57f, 2.06f, 0.035f, 0.04f, cyanNeon)

                    // Aerodynamic Front Bumper Corner Chamfers (matching the low-poly wedge profile in poster)
                    mb.addBox(-1.02f, 0.36f, 2.18f, 0.16f, 0.26f, 0.38f, primaryColor)
                    mb.addBox(1.02f, 0.36f, 2.18f, 0.16f, 0.26f, 0.38f, primaryColor)

                    // Front Bumper Core structure
                    mb.addBox(0f, 0.26f, 2.32f, 2.04f, 0.14f, 0.16f, primaryColor)

                    // Center black radiator grille with 5 vertical aerodynamic slats
                    mb.addBox(0f, 0.33f, 2.34f, 0.88f, 0.24f, 0.06f, floatArrayOf(0.02f, 0.02f, 0.03f, 1f))
                    for (gi in -2..2) {
                        mb.addBox(gi * 0.16f, 0.33f, 2.36f, 0.035f, 0.22f, 0.03f, darkCarbon)
                    }

                    // Dual Large Front Air Intake Scoops (Canards) to left and right
                    // Left scoop frame & recessed intake
                    mb.addBox(-0.72f, 0.33f, 2.33f, 0.44f, 0.20f, 0.05f, floatArrayOf(0.02f, 0.02f, 0.03f, 1f))
                    mb.addBox(-0.72f, 0.44f, 2.35f, 0.48f, 0.04f, 0.06f, primaryColor)
                    mb.addBox(-0.95f, 0.33f, 2.35f, 0.06f, 0.22f, 0.06f, primaryColor)
                    mb.addBox(-0.49f, 0.33f, 2.35f, 0.06f, 0.22f, 0.06f, primaryColor)
                    // Horizontal cyan-blue neon light strip inside left scoop
                    mb.addBox(-0.72f, 0.30f, 2.36f, 0.42f, 0.035f, 0.03f, cyanNeon)

                    // Right scoop frame & recessed intake
                    mb.addBox(0.72f, 0.33f, 2.33f, 0.44f, 0.20f, 0.05f, floatArrayOf(0.02f, 0.02f, 0.03f, 1f))
                    mb.addBox(0.72f, 0.44f, 2.35f, 0.48f, 0.04f, 0.06f, primaryColor)
                    mb.addBox(0.95f, 0.33f, 2.35f, 0.06f, 0.22f, 0.06f, primaryColor)
                    mb.addBox(0.49f, 0.33f, 2.35f, 0.06f, 0.22f, 0.06f, primaryColor)
                    // Horizontal cyan-blue neon light strip inside right scoop
                    mb.addBox(0.72f, 0.30f, 2.36f, 0.42f, 0.035f, 0.03f, cyanNeon)

                    // 4. Iconic Headlights («عيون البوز» - Segmented 3-Block Cyan LEDs)
                    // Left Headlight: Black casing + bottom cyan bar + 3 vertical cyan LED blocks
                    mb.addBox(-0.70f, 0.52f, 2.34f, 0.48f, 0.13f, 0.05f, darkCarbon)
                    mb.addBox(-0.70f, 0.47f, 2.365f, 0.45f, 0.03f, 0.025f, cyanNeon) // Bottom bar
                    mb.addBox(-0.86f, 0.53f, 2.365f, 0.09f, 0.08f, 0.025f, cyanNeon) // Outer block
                    mb.addBox(-0.70f, 0.53f, 2.365f, 0.09f, 0.08f, 0.025f, cyanNeon) // Middle block
                    mb.addBox(-0.54f, 0.53f, 2.365f, 0.09f, 0.08f, 0.025f, cyanNeon) // Inner block

                    // Right Headlight: Black casing + bottom cyan bar + 3 vertical cyan LED blocks
                    mb.addBox(0.70f, 0.52f, 2.34f, 0.48f, 0.13f, 0.05f, darkCarbon)
                    mb.addBox(0.70f, 0.47f, 2.365f, 0.45f, 0.03f, 0.025f, cyanNeon) // Bottom bar
                    mb.addBox(0.54f, 0.53f, 2.365f, 0.09f, 0.08f, 0.025f, cyanNeon) // Inner block
                    mb.addBox(0.70f, 0.53f, 2.365f, 0.09f, 0.08f, 0.025f, cyanNeon) // Middle block
                    mb.addBox(0.86f, 0.53f, 2.365f, 0.09f, 0.08f, 0.025f, cyanNeon) // Outer block

                    // 5. Angular Side Intakes (Behind doors for mid-engine cooling)
                    mb.addBox(-1.03f, 0.48f, -0.55f, 0.12f, 0.30f, 0.85f, darkCarbon)
                    mb.addBox(1.03f, 0.48f, -0.55f, 0.12f, 0.30f, 0.85f, darkCarbon)
                    mb.addBox(-1.05f, 0.48f, -0.55f, 0.04f, 0.24f, 0.70f, floatArrayOf(0.04f, 0.04f, 0.05f, 1f))
                    mb.addBox(1.05f, 0.48f, -0.55f, 0.04f, 0.24f, 0.70f, floatArrayOf(0.04f, 0.04f, 0.05f, 1f))

                    // 6. Cockpit Pillars & Open-View Panoramic Carbon Roof when Robot Driver is active
                    mb.addBox(0f, 1.08f, -0.35f, 1.50f, 0.07f, 1.35f, darkCarbon) // Sleek rear-half roof
                    mb.addBox(-0.73f, 0.78f, 0.65f, 0.08f, 0.54f, 0.10f, darkCarbon) // A-pillar left
                    mb.addBox(0.73f, 0.78f, 0.65f, 0.08f, 0.54f, 0.10f, darkCarbon) // A-pillar right
                    mb.addBox(-0.74f, 0.78f, -0.92f, 0.08f, 0.54f, 0.14f, darkCarbon) // C-pillar left
                    mb.addBox(0.74f, 0.78f, -0.92f, 0.08f, 0.54f, 0.14f, darkCarbon) // C-pillar right

                    if (!hasRobotDriver) {
                        // Windshield & side glass windows (only for parked/player mode)
                        mb.addBox(0f, 0.76f, 0.70f, 1.40f, 0.46f, 0.05f, glassColor)
                        mb.addBox(-0.74f, 0.76f, -0.10f, 0.04f, 0.44f, 1.35f, glassColor)
                        mb.addBox(0.74f, 0.76f, -0.10f, 0.04f, 0.44f, 1.35f, glassColor)
                        mb.addBox(0f, 0.78f, -0.92f, 1.36f, 0.44f, 0.05f, glassColor)
                    } else {
                        // Subtle lower windshield edge trim so robot head & visor shine unobstructed from front & sides!
                        mb.addBox(0f, 0.56f, 0.70f, 1.40f, 0.10f, 0.04f, glassColor)
                        mb.addBox(0f, 0.78f, -0.92f, 1.36f, 0.30f, 0.04f, glassColor)
                    }

                    // 7. Interior (4 seats + Dashboard + Seated Driver Character / Cyber Robot Driver)
                    // Front Left Driver Seat
                    mb.addBox(-0.38f, 0.42f, 0.18f, 0.44f, 0.14f, 0.46f, darkCarbon)
                    mb.addBox(-0.38f, 0.68f, -0.04f, 0.44f, 0.40f, 0.12f, darkCarbon)
                    // Front Right Passenger Seat
                    mb.addBox(0.38f, 0.42f, 0.18f, 0.44f, 0.14f, 0.46f, darkCarbon)
                    mb.addBox(0.38f, 0.68f, -0.04f, 0.44f, 0.40f, 0.12f, darkCarbon)
                    // Rear Left Seat
                    mb.addBox(-0.38f, 0.44f, -0.54f, 0.44f, 0.14f, 0.44f, darkCarbon)
                    mb.addBox(-0.38f, 0.70f, -0.74f, 0.44f, 0.40f, 0.12f, darkCarbon)
                    // Rear Right Seat
                    mb.addBox(0.38f, 0.44f, -0.54f, 0.44f, 0.14f, 0.44f, darkCarbon)
                    mb.addBox(0.38f, 0.70f, -0.74f, 0.44f, 0.40f, 0.12f, darkCarbon)

                    if (hasRobotDriver) {
                        addSeatedRobotDriver(mb, -0.38f, 0.52f, 0.16f, 0.44f)
                    } else {
                        // Seated Roblox Driver (Classic Yellow Face, Smiling, Blue Shirt, Smooth Plastic, No Hair/Accessories)
                        val driverYellow = floatArrayOf(1.0f, 0.84f, 0.02f, 1f)
                        val driverBlue = floatArrayOf(0.02f, 0.38f, 0.88f, 1f)
                        val driverDark = floatArrayOf(0.05f, 0.05f, 0.05f, 1f)
                        val driverGreen = floatArrayOf(0.08f, 0.68f, 0.16f, 1f)

                        // Driver Seated Legs (Green)
                        mb.addBox(-0.46f, 0.38f, 0.36f, 0.14f, 0.14f, 0.34f, driverGreen)
                        mb.addBox(-0.30f, 0.38f, 0.36f, 0.14f, 0.14f, 0.34f, driverGreen)

                        // Driver Torso (Blue rectangle - smooth plastic)
                        mb.addBox(-0.38f, 0.62f, 0.16f, 0.35f, 0.32f, 0.22f, driverBlue)
                        // Driver Head (Smooth rounded sphere - zero sharp face corners)
                        val drvHeadY = 0.91f
                        val drvHeadRx = 0.135f
                        val drvHeadRy = 0.132f
                        val drvHeadRz = 0.132f
                        mb.addEllipsoid(-0.38f, drvHeadY, 0.16f, drvHeadRx, drvHeadRy, drvHeadRz, driverYellow, slices = 22, stacks = 14)

                        // Smooth rounded face features hugging curved front (+Z)
                        mb.addEllipsoid(-0.432f, drvHeadY + 0.025f, 0.278f, 0.017f, 0.022f, 0.014f, driverDark, slices = 10, stacks = 8) // Eye Left
                        mb.addEllipsoid(-0.328f, drvHeadY + 0.025f, 0.278f, 0.017f, 0.022f, 0.014f, driverDark, slices = 10, stacks = 8) // Eye Right
                        for (sIdx in -2..2) {
                            val t = sIdx / 2f
                            val sx = -0.38f + t * 0.048f
                            val sy = drvHeadY - 0.036f + (t * t) * 0.014f
                            val sz = 0.16f + kotlin.math.sqrt((drvHeadRz * drvHeadRz - (sx + 0.38f) * (sx + 0.38f) - (sy - drvHeadY) * (sy - drvHeadY)).coerceAtLeast(0.002f)) + 0.004f
                            mb.addSphere(sx, sy, sz, 0.011f, driverDark, slices = 8, stacks = 6)
                        }

                        // Driver Arms holding steering wheel (Yellow)
                        mb.addBox(-0.51f, 0.60f, 0.30f, 0.09f, 0.11f, 0.26f, driverYellow)
                        mb.addBox(-0.25f, 0.60f, 0.30f, 0.09f, 0.11f, 0.26f, driverYellow)
                    }

                    // Dashboard & Console with Inset-2 Glowing Cyan Neon Piping
                    mb.addBox(0f, 0.62f, 0.60f, 1.36f, 0.20f, 0.26f, darkCarbon)
                    // Neon edge line across top of dash
                    mb.addBox(0f, 0.72f, 0.58f, 1.34f, 0.025f, 0.03f, cyanNeon)
                    // Dual illuminated cyan dials (Speed & RPM)
                    mb.addBox(-0.45f, 0.70f, 0.60f, 0.11f, 0.11f, 0.015f, cyanNeon)
                    mb.addBox(-0.31f, 0.70f, 0.60f, 0.11f, 0.11f, 0.015f, cyanNeon)
                    // Center screen with cyan glow
                    mb.addBox(0f, 0.65f, 0.60f, 0.28f, 0.16f, 0.02f, cyanNeon)
                    // Center console neon line
                    mb.addBox(0f, 0.50f, 0.05f, 0.04f, 0.02f, 0.90f, cyanNeon)
                    // Roblox steering wheel with center square logo
                    mb.addBox(-0.38f, 0.68f, 0.44f, 0.26f, 0.26f, 0.035f, floatArrayOf(0.18f, 0.18f, 0.2f, 1f))
                    mb.addBox(-0.38f, 0.68f, 0.44f, 0.08f, 0.08f, 0.05f, floatArrayOf(0.95f, 0.95f, 0.95f, 1f))
                    // Rearview mirror
                    mb.addBox(0f, 0.95f, 0.55f, 0.22f, 0.07f, 0.04f, darkCarbon)

                    // 8. Rear Deck, Engine Louvers, Diffuser & Exhaust Outlets
                    mb.addBox(0f, 0.55f, -1.55f, 1.85f, 0.24f, 1.30f, primaryColor)
                    mb.addBox(0f, 0.58f, -1.50f, 1.10f, 0.14f, 1.00f, darkCarbon)
                    mb.addBox(0f, 0.22f, -2.25f, 1.95f, 0.15f, 0.35f, darkCarbon)

                    // Dual exhaust tips
                    mb.addBox(-0.35f, 0.30f, -2.32f, 0.18f, 0.14f, 0.16f, floatArrayOf(0.15f, 0.15f, 0.18f, 1f))
                    mb.addBox(0.35f, 0.30f, -2.32f, 0.18f, 0.14f, 0.16f, floatArrayOf(0.15f, 0.15f, 0.18f, 1f))

                    // Glowing Red Taillights
                    mb.addBox(-0.65f, 0.55f, -2.28f, 0.55f, 0.12f, 0.04f, tailLightColor)
                    mb.addBox(0.65f, 0.55f, -2.28f, 0.55f, 0.12f, 0.04f, tailLightColor)

                    // 9. Rear Racing GT Spoiler on dual struts
                    mb.addBox(0f, 1.15f, -2.10f, 2.15f, 0.06f, 0.45f, darkCarbon)
                    mb.addBox(-1.08f, 1.15f, -2.10f, 0.04f, 0.24f, 0.48f, darkCarbon)
                    mb.addBox(1.08f, 1.15f, -2.10f, 0.04f, 0.24f, 0.48f, darkCarbon)
                    mb.addBox(-0.65f, 0.85f, -2.05f, 0.08f, 0.55f, 0.10f, darkCarbon)
                    mb.addBox(0.65f, 0.85f, -2.05f, 0.08f, 0.55f, 0.10f, darkCarbon)

                    // 10. 4 Sporty Black Wheels with Glowing Electric Cyan Rim Rings!
                    val halfL = type.length / 2f
                    val wheelY = 0.32f
                    val wheelRadius = 0.33f
                    val wheelW = 0.24f
                    val wheelX = type.width / 2f
                    val frontZ = halfL * 0.65f
                    val rearZ = -halfL * 0.65f

                    for (wx in listOf(-wheelX, wheelX)) {
                        for (wz in listOf(frontZ, rearZ)) {
                            // Tire rubber
                            mb.addBox(wx, wheelY, wz, wheelW, wheelRadius * 2f, wheelRadius * 2f, wheelColor)
                            // Glowing Cyan-Blue Rim Outer Ring
                            val rimOffset = if (wx > 0) 0.025f else -0.025f
                            mb.addBox(wx + rimOffset, wheelY, wz, 0.04f, wheelRadius * 1.75f, wheelRadius * 1.75f, cyanNeon)
                            // Dark Carbon Hub
                            val hubOffset = if (wx > 0) 0.035f else -0.035f
                            mb.addBox(wx + hubOffset, wheelY, wz, 0.05f, wheelRadius * 0.95f, wheelRadius * 0.95f, floatArrayOf(0.12f, 0.12f, 0.14f, 1f))
                        }
                    }

                    // 11. Nitro Boost Blue Flames (Active when nitro is fired!)
                    if (isNitroActive) {
                        mb.addBox(-0.35f, 0.30f, -2.60f, 0.26f, 0.20f, 0.55f, cyanFlame)
                        mb.addBox(0.35f, 0.30f, -2.60f, 0.26f, 0.20f, 0.55f, cyanFlame)
                        mb.addBox(-0.35f, 0.30f, -2.95f, 0.16f, 0.14f, 0.40f, deepBlueFlame)
                        mb.addBox(0.35f, 0.30f, -2.95f, 0.16f, 0.14f, 0.40f, deepBlueFlame)
                    }

                    // Gamer Blox R-1 has all custom components built
                    val built = Pair(mb.build(), mb.getVertexCount())
                    dynamicVehicleMeshCache[cacheKey] = built
                    return built
                }
                CarMeshStyle.SEDAN_MODERN -> {
                    // Modern executive sedan (e.g. Camry Sport / Maybach)
                    mb.addBox(0f, 0.54f, 0f, 1.92f, 0.56f, 4.6f, primaryColor)
                    if (hasRobotDriver) {
                        mb.addBox(0f, 1.32f, -0.55f, 1.56f, 0.07f, 1.30f, secondaryColor)
                        mb.addBox(-0.75f, 1.02f, 0.72f, 0.08f, 0.52f, 0.10f, secondaryColor)
                        mb.addBox(0.75f, 1.02f, 0.72f, 0.08f, 0.52f, 0.10f, secondaryColor)
                        mb.addBox(-0.75f, 1.02f, -1.20f, 0.08f, 0.52f, 0.14f, secondaryColor)
                        mb.addBox(0.75f, 1.02f, -1.20f, 0.08f, 0.52f, 0.14f, secondaryColor)
                        addSeatedRobotDriver(mb, -0.38f, 0.64f, 0.05f, 0.40f)
                    } else {
                        mb.addBox(0f, 1.02f, -0.1f, 1.62f, 0.52f, 2.5f, glassColor)
                        mb.addBox(0f, 1.30f, -0.1f, 1.58f, 0.08f, 2.4f, secondaryColor)
                    }
                    // Luxury chrome window trim & grille
                    mb.addBox(0f, 0.52f, 2.32f, 1.4f, 0.35f, 0.05f, rimColor)
                }
            }
        } else {
            // Default styles by VehicleType
            when (type) {
                VehicleType.SPORTS_CAR -> {
                    mb.addBox(0f, 0.45f, 0f, 1.9f, 0.5f, 4.2f, primaryColor)
                    mb.addBox(0f, 0.48f, 1.4f, 1.75f, 0.35f, 1.4f, primaryColor)
                    if (hasRobotDriver) {
                        mb.addBox(0f, 1.12f, -0.45f, 1.42f, 0.06f, 1.15f, secondaryColor)
                        mb.addBox(-0.68f, 0.85f, 0.58f, 0.08f, 0.46f, 0.10f, secondaryColor)
                        mb.addBox(0.68f, 0.85f, 0.58f, 0.08f, 0.46f, 0.10f, secondaryColor)
                        addSeatedRobotDriver(mb, -0.35f, 0.56f, 0.05f, 0.38f)
                    } else {
                        mb.addBox(0f, 0.85f, -0.2f, 1.5f, 0.48f, 2.0f, glassColor)
                        mb.addBox(0f, 1.1f, -0.3f, 1.4f, 0.08f, 1.6f, secondaryColor)
                    }
                    mb.addBox(0f, 1.05f, -1.9f, 1.8f, 0.08f, 0.4f, secondaryColor)
                    mb.addBox(-0.7f, 0.85f, -1.9f, 0.1f, 0.4f, 0.1f, secondaryColor)
                    mb.addBox(0.7f, 0.85f, -1.9f, 0.1f, 0.4f, 0.1f, secondaryColor)
                }
                VehicleType.TAXI -> {
                    mb.addBox(0f, 0.55f, 0f, 1.85f, 0.6f, 4.4f, primaryColor)
                    mb.addBox(0f, 1.05f, -0.1f, 1.6f, 0.55f, 2.4f, glassColor)
                    mb.addBox(0f, 1.34f, -0.1f, 1.55f, 0.08f, 2.3f, primaryColor)
                    mb.addBox(0f, 1.5f, -0.1f, 0.8f, 0.25f, 0.35f, floatArrayOf(0.98f, 0.95f, 0.2f, 1f))
                    mb.addBox(-0.93f, 0.65f, 0f, 0.04f, 0.15f, 3.6f, secondaryColor)
                    mb.addBox(0.93f, 0.65f, 0f, 0.04f, 0.15f, 3.6f, secondaryColor)
                }
                VehicleType.POLICE_CRUISER -> {
                    mb.addBox(0f, 0.55f, 0f, 1.9f, 0.62f, 4.6f, primaryColor)
                    mb.addBox(-0.94f, 0.55f, 0f, 0.05f, 0.58f, 2.2f, secondaryColor)
                    mb.addBox(0.94f, 0.55f, 0f, 0.05f, 0.58f, 2.2f, secondaryColor)
                    mb.addBox(0f, 1.05f, -0.1f, 1.6f, 0.55f, 2.4f, glassColor)
                    mb.addBox(0f, 1.34f, -0.1f, 1.55f, 0.08f, 2.3f, secondaryColor)
                    mb.addBox(-0.4f, 1.48f, -0.1f, 0.5f, 0.2f, 0.3f, floatArrayOf(0.98f, 0.1f, 0.1f, 1f))
                    mb.addBox(0.4f, 1.48f, -0.1f, 0.5f, 0.2f, 0.3f, floatArrayOf(0.1f, 0.4f, 0.98f, 1f))
                    mb.addBox(0f, 1.45f, -0.1f, 0.3f, 0.14f, 0.25f, floatArrayOf(0.85f, 0.85f, 0.85f, 1f))
                }
                VehicleType.CITY_BUS -> {
                    mb.addBox(0f, 1.4f, 0f, 2.5f, 2.2f, 9.2f, primaryColor)
                    mb.addBox(-1.26f, 1.6f, 0f, 0.06f, 0.85f, 8.4f, glassColor)
                    mb.addBox(1.26f, 1.6f, 0f, 0.06f, 0.85f, 8.4f, glassColor)
                    mb.addBox(0f, 1.6f, 4.61f, 2.2f, 1.0f, 0.06f, glassColor)
                    mb.addBox(0f, 2.35f, 4.62f, 1.8f, 0.3f, 0.05f, floatArrayOf(0.98f, 0.85f, 0.1f, 1f))
                }
                VehicleType.SEDAN -> {
                    mb.addBox(0f, 0.52f, 0f, 1.85f, 0.55f, 4.4f, primaryColor)
                    if (hasRobotDriver) {
                        mb.addBox(0f, 1.25f, -0.50f, 1.50f, 0.08f, 1.25f, secondaryColor)
                        mb.addBox(-0.72f, 0.98f, 0.65f, 0.08f, 0.50f, 0.10f, secondaryColor)
                        mb.addBox(0.72f, 0.98f, 0.65f, 0.08f, 0.50f, 0.10f, secondaryColor)
                        addSeatedRobotDriver(mb, -0.36f, 0.62f, 0.05f, 0.38f)
                    } else {
                        mb.addBox(0f, 0.98f, -0.1f, 1.55f, 0.5f, 2.4f, glassColor)
                        mb.addBox(0f, 1.25f, -0.1f, 1.5f, 0.08f, 2.3f, secondaryColor)
                    }
                }
                VehicleType.AIRPLANE -> {
                    // Sleek 3D Private Airplane / Light Jet for Airport Runway & Street-House Rooftop Pads
                    val chromeMetal = floatArrayOf(0.86f, 0.88f, 0.92f, 1f)
                    val darkProp = floatArrayOf(0.14f, 0.16f, 0.20f, 1f)
                    val redWingNav = floatArrayOf(0.98f, 0.14f, 0.14f, 1f)
                    val greenWingNav = floatArrayOf(0.14f, 0.96f, 0.32f, 1f)
                    val cockpitGlass = floatArrayOf(0.28f, 0.72f, 0.95f, 0.92f)

                    // Main aerodynamic fuselage & tapered nose
                    mb.addBox(0f, 0.95f, 0f, 1.05f, 0.92f, 4.6f, primaryColor)
                    mb.addBox(0f, 0.88f, 2.50f, 0.78f, 0.68f, 0.95f, primaryColor)
                    mb.addBox(0f, 0.84f, 3.02f, 0.42f, 0.42f, 0.35f, secondaryColor) // Spinner nose cone

                    // Fuselage accent trim stripe along both flanks
                    mb.addBox(-0.54f, 0.96f, 0f, 0.04f, 0.20f, 4.4f, secondaryColor)
                    mb.addBox(0.54f, 0.96f, 0f, 0.04f, 0.20f, 4.4f, secondaryColor)

                    // Cockpit glass canopy
                    mb.addBox(0f, 1.48f, 0.75f, 0.86f, 0.52f, 1.75f, cockpitGlass)
                    mb.addBox(0f, 1.76f, 0.70f, 0.74f, 0.10f, 1.35f, primaryColor)

                    // Swept Main Wings (6.0m wingspan — fits cleanly on every Street House rooftop pad!)
                    mb.addBox(0f, 0.90f, 0.35f, 6.0f, 0.14f, 1.35f, primaryColor)
                    mb.addBox(0f, 0.98f, 0.35f, 5.7f, 0.04f, 0.42f, secondaryColor)
                    // Twin wingtip aviation navigation beacons
                    mb.addBox(-3.04f, 0.92f, 0.35f, 0.16f, 0.18f, 0.45f, redWingNav)
                    mb.addBox(3.04f, 0.92f, 0.35f, 0.16f, 0.18f, 0.45f, greenWingNav)

                    // Tail fin & horizontal stabilizers
                    mb.addBox(0f, 1.02f, -2.25f, 2.4f, 0.12f, 0.68f, primaryColor)
                    mb.addBox(0f, 1.78f, -2.18f, 0.14f, 1.25f, 0.88f, secondaryColor)
                    mb.addBox(0f, 2.42f, -2.25f, 0.18f, 0.14f, 0.28f, redWingNav)

                    // Front propeller cross blades
                    mb.addBox(0f, 0.84f, 3.22f, 1.65f, 0.14f, 0.06f, darkProp)
                    mb.addBox(0f, 0.84f, 3.22f, 0.14f, 1.65f, 0.06f, darkProp)

                    // Tricycle landing gear struts & wheels resting cleanly at y = 0.0
                    for (gx in listOf(-0.85f, 0.85f)) {
                        mb.addCylinder(gx, 0.25f, 0.30f, 0.06f, 0.55f, chromeMetal, segments = 6)
                        mb.addBox(gx, 0.24f, 0.30f, 0.18f, 0.48f, 0.48f, wheelColor)
                        mb.addBox(gx + if (gx > 0) 0.03f else -0.03f, 0.24f, 0.30f, 0.04f, 0.28f, 0.28f, rimColor)
                    }
                    mb.addCylinder(0f, 0.22f, 2.05f, 0.05f, 0.55f, chromeMetal, segments = 6)
                    mb.addBox(0f, 0.20f, 2.05f, 0.15f, 0.40f, 0.40f, wheelColor)

                    return Pair(mb.build(), mb.getVertexCount())
                }
            }
        }

        // Headlights (glowing yellow-white in front)
        val halfL = type.length / 2f
        mb.addBox(-0.7f, 0.5f, halfL + 0.02f, 0.35f, 0.2f, 0.06f, headlightColor)
        mb.addBox(0.7f, 0.5f, halfL + 0.02f, 0.35f, 0.2f, 0.06f, headlightColor)

        // Tail Lights (glowing red in rear)
        mb.addBox(-0.7f, 0.55f, -halfL - 0.02f, 0.35f, 0.18f, 0.06f, tailLightColor)
        mb.addBox(0.7f, 0.55f, -halfL - 0.02f, 0.35f, 0.18f, 0.06f, tailLightColor)

        // 4 Wheels with rims
        val wheelY = 0.32f
        val wheelRadius = 0.32f
        val wheelW = 0.22f
        val wheelX = type.width / 2f
        val frontZ = halfL * 0.65f
        val rearZ = -halfL * 0.65f

        for (wx in listOf(-wheelX, wheelX)) {
            for (wz in listOf(frontZ, rearZ)) {
                mb.addBox(wx, wheelY, wz, wheelW, wheelRadius * 2f, wheelRadius * 2f, wheelColor)
                mb.addBox(wx + if (wx > 0) 0.02f else -0.02f, wheelY, wz, 0.04f, wheelRadius * 1.2f, wheelRadius * 1.2f, rimColor)
            }
        }

        val built = Pair(mb.build(), mb.getVertexCount())
        dynamicVehicleMeshCache[cacheKey] = built
        return built
    }

    /**
     * Builds animated player character mesh matching the exact specifications:
     * - Head: Yellow rounded cube with black eyes and simple smile
     * - Torso: Blue rectangle
     * - Arms: Two yellow blocks
     * - Legs: Two green blocks
     * - Material: Smooth plastic, no accessories
     * - Proportions: 5 units total height, 4 units total width
     */
    fun buildCharacterMesh(
        walkPhase: Float,
        isRunning: Boolean,
        outfit: com.example.opencity.model.PlayerOutfit? = null,
        isSeated: Boolean = false
    ): Pair<FloatBuffer, Int> {
        val twoPi = 6.2831855f
        val normWalk = ((walkPhase % twoPi) + twoPi) % twoPi
        val walkBin = ((normWalk * 16f / twoPi).toInt()) and 15
        val key = "$walkBin|$isRunning|${outfit?.top?.id ?: ""}|${outfit?.pants?.id ?: ""}|${outfit?.shoes?.id ?: ""}|${outfit?.accessory?.id ?: ""}|$isSeated"
        characterMeshCache[key]?.let { return it }

        val qWalk = walkBin * (twoPi / 16f)
        val mb = MeshBuilder(initialCapacityVertices = 6144)

        // Skin & Face Colors
        val yellowColor = floatArrayOf(1.0f, 0.84f, 0.02f, 1f) // Smooth bright yellow head & skin
        val hairBrown = floatArrayOf(0.32f, 0.19f, 0.10f, 1f)
        val hairBrownHighlight = floatArrayOf(0.41f, 0.25f, 0.14f, 1f)
        val torsoColor = outfit?.top?.primaryColor ?: floatArrayOf(0.02f, 0.38f, 0.88f, 1f)
        val topAccent = outfit?.top?.accentColor ?: floatArrayOf(0.85f, 0.92f, 1f, 1f)
        val pantsColor = outfit?.pants?.primaryColor ?: floatArrayOf(0.10f, 0.72f, 0.18f, 1f)
        val pantsAccent = outfit?.pants?.accentColor ?: floatArrayOf(0.95f, 0.95f, 0.95f, 1f)
        val shoeColor = outfit?.shoes?.primaryColor
        val shoeAccent = outfit?.shoes?.accentColor
        val faceDetailColor = floatArrayOf(0.05f, 0.05f, 0.06f, 1f) // Dark eyes, eyebrows, smirk
        val eyeWhiteColor = floatArrayOf(0.98f, 0.99f, 1.0f, 1f)
        val goldColor = floatArrayOf(0.98f, 0.79f, 0.18f, 1f)
        val whiteColor = floatArrayOf(0.97f, 0.98f, 0.99f, 1f)
        val redColor = floatArrayOf(0.92f, 0.14f, 0.18f, 1f)

        val swingAmp = if (isRunning) 0.42f else 0.24f
        val legSwing = if (isSeated) 0f else sin(qWalk.toDouble()).toFloat() * swingAmp
        val armSwing = -legSwing * 1.15f
        val sitDrop = if (isSeated) 0.26f else 0f

        // 1. Head: Smooth rounded spherical/ellipsoid head (zero sharp corners on face!)
        val headY = 1.62f - sitDrop
        val headRx = 0.215f
        val headRy = 0.205f
        val headRz = 0.205f
        mb.addEllipsoid(0f, headY, 0f, headRx, headRy, headRz, yellowColor, slices = 28, stacks = 18)

        // Face Details smoothly contoured onto front (+Z) curve of the sphere
        val faceZ = headRz + 0.004f
        // Bold expressive eyebrows
        mb.addEllipsoid(-0.078f, headY + 0.080f, 0.184f, 0.042f, 0.012f, 0.014f, faceDetailColor, slices = 12, stacks = 8)
        mb.addEllipsoid(0.078f, headY + 0.084f, 0.184f, 0.042f, 0.012f, 0.014f, faceDetailColor, slices = 12, stacks = 8)
        // Left & Right Eyes (smooth rounded ovals + crisp white highlight glints)
        mb.addEllipsoid(-0.076f, headY + 0.036f, 0.190f, 0.025f, 0.033f, 0.018f, faceDetailColor, slices = 14, stacks = 10)
        mb.addEllipsoid(0.076f, headY + 0.036f, 0.190f, 0.025f, 0.033f, 0.018f, faceDetailColor, slices = 14, stacks = 10)
        mb.addSphere(-0.068f, headY + 0.047f, 0.204f, 0.009f, eyeWhiteColor, slices = 8, stacks = 6)
        mb.addSphere(0.084f, headY + 0.047f, 0.204f, 0.009f, eyeWhiteColor, slices = 8, stacks = 6)
        // Confident contoured smirk along the spherical chin surface
        for (sIdx in -3..3) {
            val t = sIdx / 3f
            val sx = t * 0.078f
            val smirkLift = if (t > 0f) t * 0.020f else 0f
            val sy = headY - 0.056f + (t * t) * 0.022f + smirkLift
            val sz = kotlin.math.sqrt((headRz * headRz - sx * sx - (sy - headY) * (sy - headY)).coerceAtLeast(0.01f)) + 0.005f
            mb.addSphere(sx, sy, sz, 0.013f, faceDetailColor, slices = 10, stacks = 8)
        }

        // Smooth sculpted messy brown hair (soft ellipsoids, zero sharp corners)
        val acc = outfit?.accessory
        val wearingHat = acc?.accessoryKind == com.example.opencity.model.AccessoryKind.CAP ||
            acc?.accessoryKind == com.example.opencity.model.AccessoryKind.BEANIE
        if (!wearingHat) {
            mb.addEllipsoid(0f, headY + 0.125f, -0.020f, 0.218f, 0.115f, 0.212f, hairBrown, slices = 22, stacks = 12)
            mb.addEllipsoid(0f, headY + 0.055f, -0.155f, 0.205f, 0.140f, 0.095f, hairBrown, slices = 18, stacks = 10)
            // Front stylized messy swept hair locks
            mb.addEllipsoid(-0.085f, headY + 0.155f, 0.095f, 0.095f, 0.065f, 0.105f, hairBrownHighlight, slices = 14, stacks = 10)
            mb.addEllipsoid(0.065f, headY + 0.165f, 0.085f, 0.105f, 0.070f, 0.095f, hairBrown, slices = 14, stacks = 10)
            mb.addEllipsoid(0.010f, headY + 0.195f, 0.030f, 0.110f, 0.060f, 0.090f, hairBrownHighlight, slices = 14, stacks = 10)
            // Side hair tufts
            mb.addEllipsoid(-0.185f, headY + 0.065f, -0.025f, 0.055f, 0.095f, 0.135f, hairBrown, slices = 12, stacks = 8)
            mb.addEllipsoid(0.185f, headY + 0.065f, -0.025f, 0.055f, 0.095f, 0.135f, hairBrown, slices = 12, stacks = 8)
        } else {
            // Subtle side hair locks peeking cleanly under the hat
            mb.addEllipsoid(-0.185f, headY + 0.035f, -0.035f, 0.048f, 0.070f, 0.115f, hairBrown, slices = 10, stacks = 8)
            mb.addEllipsoid(0.185f, headY + 0.035f, -0.035f, 0.048f, 0.070f, 0.115f, hairBrown, slices = 10, stacks = 8)
        }

        // Optional Head / Face Accessory (Smooth curved Cap, Beanie, Sunglasses)
        if (acc != null) {
            when (acc.accessoryKind) {
                com.example.opencity.model.AccessoryKind.CAP -> {
                    mb.addDome(0f, headY + 0.055f, 0f, headRx * 1.03f, acc.primaryColor, slices = 22, stacks = 10)
                    // Curved forward visor peak (+Z)
                    mb.addEllipsoid(0f, headY + 0.065f, headRz * 0.92f, headRx * 0.95f, 0.024f, 0.16f, acc.primaryColor, slices = 18, stacks = 8)
                    // Curved front emblem
                    mb.addEllipsoid(0f, headY + 0.135f, headRz * 0.90f, 0.055f, 0.032f, 0.022f, acc.accentColor, slices = 12, stacks = 8)
                }
                com.example.opencity.model.AccessoryKind.BEANIE -> {
                    mb.addCylinder(0f, headY + 0.05f, 0f, headRx * 1.04f, 0.085f, acc.accentColor, segments = 24)
                    mb.addDome(0f, headY + 0.11f, 0f, headRx * 1.02f, acc.primaryColor, slices = 22, stacks = 10)
                }
                com.example.opencity.model.AccessoryKind.SUNGLASSES -> {
                    mb.addEllipsoid(-0.078f, headY + 0.038f, faceZ + 0.006f, 0.068f, 0.042f, 0.018f, acc.primaryColor, slices = 16, stacks = 10)
                    mb.addEllipsoid(0.078f, headY + 0.038f, faceZ + 0.006f, 0.068f, 0.042f, 0.018f, acc.primaryColor, slices = 16, stacks = 10)
                    mb.addEllipsoid(0f, headY + 0.045f, faceZ + 0.010f, 0.165f, 0.012f, 0.014f, acc.accentColor, slices = 12, stacks = 8)
                }
                else -> {}
            }
        }

        // 2. Torso: Brand Shirt / Hoodie / Jacket
        val torsoY = 1.06f - sitDrop
        val torsoW = 0.58f
        val torsoH = 0.70f
        val torsoD = 0.32f
        mb.addBox(0f, torsoY, 0f, torsoW, torsoH, torsoD, torsoColor)

        // Brand emblem / 3-stripes / cyber jacket zip & collar lapels / Ambition Blue Hoodie + Gold Chain + R Logo
        if (outfit != null && outfit.top.id == com.example.opencity.model.ClothingCatalog.ROBLOX_AMBITION_TOP_ID) {
            val chestZ = torsoD / 2f + 0.010f
            // White inner shirt V-panel + bottom hem
            mb.addBox(0f, torsoY + 0.25f, chestZ, 0.20f, 0.18f, 0.016f, whiteColor)
            mb.addBox(0f, torsoY - torsoH / 2f + 0.035f, 0f, torsoW + 0.015f, 0.07f, torsoD + 0.015f, whiteColor)
            // Soft rounded hoodie collar resting behind neck
            mb.addEllipsoid(0f, torsoY + torsoH / 2f + 0.02f, -0.09f, 0.23f, 0.085f, 0.14f, torsoColor, slices = 16, stacks = 10)
            // Shiny gold chain necklace around neck/upper chest
            for (cIdx in -4..4) {
                val t = cIdx / 4f
                val gx = t * 0.115f
                val gy = torsoY + 0.22f + (t * t) * 0.09f
                mb.addSphere(gx, gy, chestZ + 0.012f, 0.017f, goldColor, slices = 10, stacks = 8)
            }
            // Red & White "R" Emblem badge on upper left chest
            mb.addEllipsoid(0.14f, torsoY + 0.12f, chestZ + 0.008f, 0.068f, 0.068f, 0.018f, redColor, slices = 16, stacks = 10)
            mb.addEllipsoid(0.14f, torsoY + 0.12f, chestZ + 0.016f, 0.042f, 0.042f, 0.014f, whiteColor, slices = 14, stacks = 8)
        } else if (outfit != null && outfit.top.id == "top_robot_cyber_jacket") {
            val chestZ = torsoD / 2f + 0.010f
            val backZ = -torsoD / 2f - 0.010f
            // Matching Cyber Robot Jacket Collar Lapels, Glowing Center Zip & Chest Core Emblem
            mb.addBox(-0.18f, torsoY + torsoH / 2f + 0.03f, 0.06f, 0.16f, 0.11f, 0.20f, topAccent)
            mb.addBox(0.18f, torsoY + torsoH / 2f + 0.03f, 0.06f, 0.16f, 0.11f, 0.20f, topAccent)
            mb.addBox(0f, torsoY, chestZ, 0.045f, torsoH * 0.92f, 0.018f, topAccent)
            mb.addBox(0.15f, torsoY + 0.12f, chestZ, 0.14f, 0.06f, 0.018f, topAccent)
            mb.addBox(-0.15f, torsoY + 0.12f, chestZ, 0.14f, 0.06f, 0.018f, floatArrayOf(0.84f, 0.88f, 0.94f, 1f))
            mb.addBox(0f, torsoY + 0.04f, backZ, 0.24f, 0.14f, 0.018f, topAccent)
            mb.addBox(0f, torsoY - torsoH / 2f + 0.04f, 0f, torsoW + 0.02f, 0.08f, torsoD + 0.02f, floatArrayOf(0.18f, 0.21f, 0.27f, 1f))
        } else if (outfit != null && outfit.top.id != com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID) {
            val chestZ = torsoD / 2f + 0.008f
            val backZ = -torsoD / 2f - 0.008f
            when (outfit.top.brand) {
                com.example.opencity.model.ClothingBrand.ADIDAS -> {
                    for (sx in listOf(-0.10f, 0f, 0.10f)) {
                        mb.addBox(sx, torsoY + 0.06f, chestZ, 0.04f, 0.38f, 0.014f, topAccent)
                        mb.addBox(sx, torsoY + 0.06f, backZ, 0.04f, 0.38f, 0.014f, topAccent)
                    }
                }
                com.example.opencity.model.ClothingBrand.NIKE,
                com.example.opencity.model.ClothingBrand.JORDAN,
                com.example.opencity.model.ClothingBrand.PUMA -> {
                    // Iconic chest emblem bar + hem rib
                    mb.addBox(0.06f, torsoY + 0.14f, chestZ, 0.24f, 0.07f, 0.014f, topAccent)
                    mb.addBox(-0.06f, torsoY + 0.11f, chestZ, 0.10f, 0.05f, 0.014f, topAccent)
                    mb.addBox(0f, torsoY - torsoH / 2f + 0.04f, 0f, torsoW + 0.01f, 0.07f, torsoD + 0.01f, topAccent)
                }
                else -> {}
            }
        }

        // 3. Arms: Short sleeves / Long sleeves + optional Gold Watch
        val armW = 0.26f
        val armH = 0.70f
        val armD = 0.30f
        val armXOffset = torsoW / 2f + armW / 2f + 0.01f // ~0.43m
        val hasEquippedTop = outfit != null && (outfit.top.id != com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID || outfit.top.id == "top_robot_cyber_jacket")
        val sleeveFrac = when {
            !hasEquippedTop -> 0f
            outfit != null && outfit.top.hasLongSleeves -> 0.76f
            else -> 0.36f
        }

        for (side in listOf(-1f, 1f)) {
            val zOff = if (side < 0f) armSwing else -armSwing
            val ax = side * armXOffset
            if (sleeveFrac > 0f) {
                val sleeveH = armH * sleeveFrac
                val handH = armH - sleeveH
                val topY = torsoY + armH / 2f
                mb.addBox(ax, topY - sleeveH / 2f, zOff, armW + 0.01f, sleeveH, armD + 0.01f, torsoColor)
                mb.addBox(ax, topY - sleeveH - handH / 2f, zOff, armW, handH, armD, yellowColor)
                if (outfit?.top?.hasSideStripes == true) {
                    mb.addBox(ax + side * (armW / 2f + 0.008f), topY - sleeveH / 2f, zOff, 0.016f, sleeveH * 0.9f, 0.06f, topAccent)
                }
            } else {
                mb.addBox(ax, torsoY, zOff, armW, armH, armD, yellowColor)
            }
        }

        if (acc?.accessoryKind == com.example.opencity.model.AccessoryKind.GOLD_WATCH) {
            val watchX = armXOffset
            mb.addBox(watchX, torsoY - armH / 2f + 0.14f, -armSwing, armW + 0.03f, 0.08f, armD + 0.03f, acc.primaryColor)
        }

        // 4. Legs: Pants / Track Pants / Shorts + 3D Sneakers (Standing or Seated with Bent Knees!)
        val legW = 0.28f
        val legH = 0.72f
        val legD = 0.30f
        val legXOffset = legW / 2f + 0.01f // ~0.15m
        val hasCustomShoes = outfit != null && outfit.shoes.id != com.example.opencity.model.ClothingCatalog.DEFAULT_SHOES_ID
        val shoeH = if (hasCustomShoes || outfit != null) 0.16f else 0f
        val isShorts = outfit?.pants?.isShorts == true

        if (isSeated) {
            for (side in listOf(-1f, 1f)) {
                val lx = side * legXOffset
                // Bent thighs forward on seat cushion
                mb.addBox(lx, 0.36f, 0.18f, legW, 0.24f, 0.40f, pantsColor)
                // Shins + sneakers dropping down in front of seat edge
                mb.addBox(lx, 0.20f, 0.36f, legW * 0.96f, 0.24f, 0.24f, pantsColor)
                if (shoeColor != null && shoeAccent != null) {
                    mb.addBox(lx, 0.045f, 0.38f, legW + 0.02f, 0.07f, 0.30f, shoeAccent)
                    mb.addBox(lx, 0.11f, 0.38f, legW + 0.01f, 0.08f, 0.28f, shoeColor)
                }
            }
        } else {
            for (side in listOf(-1f, 1f)) {
                val lx = side * legXOffset
                val lz = if (side < 0f) legSwing else -legSwing
                if (shoeH > 0f && shoeColor != null && shoeAccent != null) {
                    val pantsAreaH = legH - shoeH
                    if (isShorts) {
                        val shortsH = pantsAreaH * 0.56f
                        val bareLegH = pantsAreaH - shortsH
                        mb.addBox(lx, legH - shortsH / 2f, lz, legW + 0.01f, shortsH, legD + 0.01f, pantsColor)
                        mb.addBox(lx, shoeH + bareLegH / 2f, lz, legW * 0.92f, bareLegH, legD * 0.92f, yellowColor)
                    } else {
                        mb.addBox(lx, shoeH + pantsAreaH / 2f, lz, legW, pantsAreaH, legD, pantsColor)
                        if (outfit?.pants?.hasSideStripes == true) {
                            val stripeX = lx + side * (legW / 2f + 0.008f)
                            if (outfit.pants.brand == com.example.opencity.model.ClothingBrand.ADIDAS) {
                                mb.addBox(stripeX, shoeH + pantsAreaH / 2f, lz - 0.05f, 0.014f, pantsAreaH * 0.94f, 0.03f, pantsAccent)
                                mb.addBox(stripeX, shoeH + pantsAreaH / 2f, lz, 0.014f, pantsAreaH * 0.94f, 0.03f, pantsAccent)
                                mb.addBox(stripeX, shoeH + pantsAreaH / 2f, lz + 0.05f, 0.014f, pantsAreaH * 0.94f, 0.03f, pantsAccent)
                            } else {
                                mb.addBox(stripeX, shoeH + pantsAreaH / 2f, lz, 0.016f, pantsAreaH * 0.90f, 0.06f, pantsAccent)
                            }
                        }
                    }
                    // 3D Sneakers at bottom of legs (midsole + upper + forward toe box)
                    mb.addBox(lx, 0.035f, lz + 0.03f, legW + 0.02f, 0.07f, legD + 0.08f, shoeAccent)
                    mb.addBox(lx, 0.115f, lz + 0.02f, legW + 0.01f, 0.095f, legD + 0.06f, shoeColor)
                } else {
                    val legY = legH / 2f
                    mb.addBox(lx, legY, lz, legW, legH, legD, pantsColor)
                }
            }
        }

        val built = Pair(mb.build(), mb.getVertexCount())
        if (characterMeshCache.size >= 96) characterMeshCache.clear()
        characterMeshCache[key] = built
        return built
    }

    /**
     * Builds full 3D interior room mesh with thick solid closed walls (zero void gaps),
     * closed double entrance/exit door, floor, ceiling, 6 villa windows, and all props.
     */
    fun buildInteriorMesh(interiorType: InteriorType): Pair<FloatBuffer, Int> {
        val mb = MeshBuilder(initialCapacityVertices = 6144)
        val interiorWaterMb = MeshBuilder(initialCapacityVertices = 256)
        val interior = com.example.opencity.model.BuildingInterior.getInterior(interiorType)

        val rw = interior.roomWidth
        val rl = interior.roomLength
        val rh = interior.roomHeight
        val wallThick = 0.5f
        val trimColor = floatArrayOf(0.84f, 0.80f, 0.72f, 1f)
        val darkOak = floatArrayOf(0.20f, 0.16f, 0.13f, 1f)
        val brassGold = floatArrayOf(0.88f, 0.72f, 0.28f, 1f)

        // Solid Floor Slab (split around the recessed Indoor Swimming Pool basin in PLAYER_VILLA so center drops DOWN!)
        if (interiorType == InteriorType.PLAYER_VILLA) {
            val totalMinX = -(rw + 1.0f) / 2f
            val totalMaxX = (rw + 1.0f) / 2f
            val totalMinZ = -(rl + 1.0f) / 2f
            val totalMaxZ = (rl + 1.0f) / 2f
            val pMinX = -11.75f
            val pMaxX = -7.85f
            val pMinZ = -9.15f
            val pMaxZ = -1.25f

            fun addSlab(x1: Float, x2: Float, z1: Float, z2: Float) {
                mb.addBox((x1 + x2) / 2f, -0.2f, (z1 + z2) / 2f, x2 - x1, 0.4f, z2 - z1, interior.floorColor)
            }
            addSlab(totalMinX, pMinX, totalMinZ, totalMaxZ)
            addSlab(pMaxX, totalMaxX, totalMinZ, totalMaxZ)
            addSlab(pMinX, pMaxX, totalMinZ, pMinZ)
            addSlab(pMinX, pMaxX, pMaxZ, totalMaxZ)
        } else {
            mb.addBox(0f, -0.2f, 0f, rw + 1.0f, 0.4f, rl + 1.0f, interior.floorColor)
        }
        // Solid Ceiling Slab
        mb.addBox(0f, rh + 0.2f, 0f, rw + 1.0f, 0.4f, rl + 1.0f, floatArrayOf(0.96f, 0.96f, 0.98f, 1f))

        // 4 Thick Solid Perimeter Walls (Completely sealed — no holes or back-face culling voids!)
        // Back Wall (-Z)
        mb.addBox(0f, rh / 2f, -rl / 2f - wallThick / 2f, rw + wallThick * 2f, rh, wallThick, interior.wallColor)
        // Front Wall (+Z) — completely solid behind the luxury closed double door
        mb.addBox(0f, rh / 2f, rl / 2f + wallThick / 2f, rw + wallThick * 2f, rh, wallThick, interior.wallColor)
        // Left Wall (-X)
        mb.addBox(-rw / 2f - wallThick / 2f, rh / 2f, 0f, wallThick, rh, rl, interior.wallColor)
        // Right Wall (+X)
        mb.addBox(rw / 2f + wallThick / 2f, rh / 2f, 0f, wallThick, rh, rl, interior.wallColor)

        // Baseboard skirting trim around inner perimeter
        mb.addBox(0f, 0.09f, -rl / 2f + 0.03f, rw, 0.18f, 0.06f, trimColor)
        mb.addBox(0f, 0.09f, rl / 2f - 0.03f, rw, 0.18f, 0.06f, trimColor)
        mb.addBox(-rw / 2f + 0.03f, 0.09f, 0f, 0.06f, 0.18f, rl, trimColor)
        mb.addBox(rw / 2f - 0.03f, 0.09f, 0f, 0.06f, 0.18f, rl, trimColor)

        if (interiorType == InteriorType.UNDERGROUND_GARAGE) {
            // Grand 9.2m Exit Tunnel Portal + Ascent Ramp mounted on Front Wall (+Z)
            val portalZ = rl / 2f - 0.12f
            val portalDark = floatArrayOf(0.06f, 0.07f, 0.09f, 1f)
            val clearanceYellow = floatArrayOf(0.98f, 0.84f, 0.12f, 1f)
            val exitGreen = floatArrayOf(0.10f, 0.85f, 0.40f, 1f)
            val pillarConcrete = floatArrayOf(0.38f, 0.40f, 0.44f, 1f)
            val hazardBlack = floatArrayOf(0.12f, 0.13f, 0.15f, 1f)
            val lineWhite = floatArrayOf(0.95f, 0.95f, 0.97f, 1f)

            // Recessed dark tunnel exit frame + glowing overhead EXIT sign
            mb.addBox(0f, 1.85f, portalZ, 9.2f, 3.7f, 0.24f, portalDark)
            mb.addBox(-4.7f, 1.85f, portalZ, 0.35f, 3.9f, 0.45f, clearanceYellow)
            mb.addBox(4.7f, 1.85f, portalZ, 0.35f, 3.9f, 0.45f, clearanceYellow)
            mb.addBox(0f, 3.85f, portalZ - 0.12f, 9.8f, 0.42f, 0.45f, exitGreen)
            // Ascending concrete exit ramp mat (z = 17.5..21.8)
            for (step in 0..5) {
                val t = step / 5f
                mb.addBox(0f, t * 0.42f + 0.02f, 17.6f + step * 0.8f, 8.6f, 0.06f, 0.84f, floatArrayOf(0.24f, 0.26f, 0.30f, 1f))
            }

            // Yellow double center driving lane dashes down the underground driveway (x = 0)
            for (dz in -17..14 step 4) {
                mb.addBox(-0.18f, 0.02f, dz.toFloat(), 0.14f, 0.02f, 2.2f, clearanceYellow)
                mb.addBox(0.18f, 0.02f, dz.toFloat(), 0.14f, 0.02f, 2.2f, clearanceYellow)
            }

            // 14 Marked Underground Parking Stalls (7 West Bays & 7 East Bays)
            val bayZList = listOf(-16.5f, -11.5f, -6.5f, -1.5f, 3.5f, 8.5f, 13.5f)
            for (bz in bayZList) {
                // West Bay markings (x = -16.0..-8.5)
                mb.addBox(-12.2f, 0.02f, bz - 2.35f, 7.2f, 0.02f, 0.14f, lineWhite)
                mb.addBox(-12.2f, 0.02f, bz + 2.35f, 7.2f, 0.02f, 0.14f, lineWhite)
                mb.addBox(-15.6f, 0.10f, bz, 0.25f, 0.18f, 3.0f, clearanceYellow) // Wheel stop

                // East Bay markings (x = +8.5..+16.0)
                mb.addBox(12.2f, 0.02f, bz - 2.35f, 7.2f, 0.02f, 0.14f, lineWhite)
                mb.addBox(12.2f, 0.02f, bz + 2.35f, 7.2f, 0.02f, 0.14f, lineWhite)
                mb.addBox(15.6f, 0.10f, bz, 0.25f, 0.18f, 3.0f, clearanceYellow) // Wheel stop
            }

            // 6 Reinforced Concrete Structural Columns with Yellow & Black Hazard Bands
            for (colX in listOf(-6.5f, 6.5f)) {
                for (colZ in listOf(-14.0f, -4.0f, 6.0f)) {
                    mb.addBox(colX, rh / 2f, colZ, 1.3f, rh, 1.3f, pillarConcrete)
                    mb.addBox(colX, 0.90f, colZ, 1.34f, 0.45f, 1.34f, clearanceYellow)
                    mb.addBox(colX, 0.45f, colZ, 1.34f, 0.25f, 1.34f, hazardBlack)
                }
            }

            // Overhead galvanized ventilation ducts running down the garage ceiling
            mb.addBox(-4.0f, rh - 0.35f, -2.0f, 0.85f, 0.45f, rl - 6f, floatArrayOf(0.55f, 0.58f, 0.62f, 1f))
            mb.addBox(4.0f, rh - 0.35f, -2.0f, 0.85f, 0.45f, rl - 6f, floatArrayOf(0.55f, 0.58f, 0.62f, 1f))
        } else {
            // Closed Luxury Double Entrance/Exit Door mounted cleanly on Front Wall (+Z)
            val doorZ = rl / 2f - 0.06f
            mb.addBox(0f, 1.5f, doorZ, 2.8f, 3.0f, 0.12f, darkOak) // Solid double door leaves
            mb.addBox(0f, 1.5f, doorZ - 0.06f, 0.05f, 2.9f, 0.04f, brassGold) // Center brass seam
            mb.addBox(-1.46f, 1.5f, doorZ, 0.14f, 3.1f, 0.18f, brassGold) // Left frame
            mb.addBox(1.46f, 1.5f, doorZ, 0.14f, 3.1f, 0.18f, brassGold) // Right frame
            mb.addBox(0f, 3.08f, doorZ, 3.06f, 0.16f, 0.18f, brassGold) // Top architrave
            mb.addCylinder(-0.18f, 1.15f, doorZ - 0.08f, 0.035f, 0.55f, brassGold) // Left gold handle
            mb.addCylinder(0.18f, 1.15f, doorZ - 0.08f, 0.035f, 0.55f, brassGold) // Right gold handle
            // Exit Threshold Mat inside door
            mb.addBox(0f, 0.03f, rl / 2f - 0.7f, 3.2f, 0.04f, 1.1f, floatArrayOf(0.16f, 0.75f, 0.40f, 1f))
        }

        // 3D Interior Furniture & Props
        if (interiorType == InteriorType.PLAYER_VILLA) {
            // 6 Framed Panoramic Windows (6 شبابيك) + Complete 2-Story Luxury Villa Interior
            buildVillaSixWindows(mb, rw, rl)
            buildVillaLuxuryFurniture(mb, interiorWaterMb, rw, rl, rh)
        } else {
            if (interiorType == InteriorType.STANDARD_HOUSE) {
                // 6 Framed Glass Windows (6 شبابيك) on the walls of every standard house
                buildStandardHouseSixWindows(mb, rw, rl)
            }
            // Standard Props (Tables, Couches, Shelves, Desks, Displays)
            for (prop in interior.props) {
                mb.addBox(prop.posX, prop.posY, prop.posZ, prop.width, prop.height, prop.depth, prop.color)
                if (prop.isInteractive) {
                    mb.addBox(prop.posX, prop.posY + prop.height / 2f + 0.1f, prop.posZ, 0.5f, 0.1f, 0.5f, floatArrayOf(0.95f, 0.85f, 0.2f, 1f))
                }
            }
            if (interiorType == InteriorType.CLOTHING_STORE) {
                // Visual hanging clothes bars & sneaker boxes inside the Nike/Adidas store
                val shirtColors = listOf(
                    floatArrayOf(0.98f, 0.42f, 0.0f, 1f),
                    floatArrayOf(0.10f, 0.45f, 0.95f, 1f),
                    floatArrayOf(0.96f, 0.96f, 0.98f, 1f),
                    floatArrayOf(0.86f, 0.12f, 0.18f, 1f)
                )
                for (i in 0..3) {
                    mb.addBox(-7.4f + i * 0.9f, 2.35f, -5.5f, 0.55f, 0.65f, 0.35f, shirtColors[i])
                    mb.addBox(-1.4f + i * 0.9f, 2.35f, -6.5f, 0.55f, 0.65f, 0.35f, shirtColors[3 - i])
                }
                // Pants hangers on the Jeans/Pants rack
                for (i in 0..3) {
                    mb.addBox(4.6f + i * 0.9f, 2.35f, -5.5f, 0.45f, 0.72f, 0.30f, floatArrayOf(0.14f, 0.30f, 0.58f, 1f))
                }
                // Fitting mirror reflective glass frame
                mb.addBox(0f, 1.55f, 0.5f, 1.6f, 2.5f, 0.25f, floatArrayOf(0.75f, 0.92f, 1.0f, 1f))
            } else if (interiorType == InteriorType.GRAND_MALL) {
                // 1. Storefront Canopy Headers along North Wing (Z = -10.4)
                mb.addBox(-10.5f, 3.6f, -9.8f, 5.4f, 0.85f, 2.2f, floatArrayOf(0.12f, 0.36f, 0.88f, 1f)) // Clothes Header
                mb.addBox(-3.5f, 3.6f, -10.4f, 5.4f, 0.85f, 2.4f, floatArrayOf(0.10f, 0.65f, 0.26f, 1f)) // Fresh Veg Header
                mb.addBox(3.5f, 3.6f, -10.4f, 5.4f, 0.85f, 2.4f, floatArrayOf(0.92f, 0.32f, 0.08f, 1f)) // Food & Grills Header
                mb.addBox(10.5f, 3.6f, -9.8f, 5.4f, 0.85f, 2.2f, floatArrayOf(0.85f, 0.55f, 0.12f, 1f)) // Bakery & Market Header

                // Hanging colorful shirts & jeans inside Clothes Shop (-10.5f, -9.8f)
                val mallShirts = listOf(
                    floatArrayOf(0.98f, 0.40f, 0.06f, 1f),
                    floatArrayOf(0.12f, 0.52f, 0.96f, 1f),
                    floatArrayOf(0.92f, 0.16f, 0.28f, 1f),
                    floatArrayOf(0.18f, 0.80f, 0.42f, 1f)
                )
                for (si in 0..3) {
                    mb.addBox(-12.2f + si * 1.1f, 2.55f, -9.8f, 0.58f, 0.72f, 0.32f, mallShirts[si])
                }

                // Fresh Vegetables & Fruits produce piles on top of Market Stall (-3.5f, -10.4f)
                val produceColors = listOf(
                    floatArrayOf(0.92f, 0.14f, 0.14f, 1f), // Tomatoes & Apples
                    floatArrayOf(0.14f, 0.78f, 0.22f, 1f), // Lettuce & Cucumbers
                    floatArrayOf(0.98f, 0.84f, 0.10f, 1f), // Bananas & Lemons
                    floatArrayOf(0.96f, 0.48f, 0.08f, 1f), // Carrots & Oranges
                    floatArrayOf(0.55f, 0.16f, 0.65f, 1f)  // Eggplants & Grapes
                )
                for (pi in produceColors.indices) {
                    val px = -5.3f + pi * 0.90f
                    mb.addBox(px, 1.85f, -10.3f, 0.72f, 0.35f, 1.35f, produceColors[pi])
                }

                // Hot Meals, Burgers & Pizza boxes on Food Counter (3.5f, -10.4f)
                mb.addBox(2.0f, 2.0f, -10.3f, 0.85f, 0.25f, 0.85f, floatArrayOf(0.95f, 0.65f, 0.22f, 1f))
                mb.addBox(3.5f, 2.0f, -10.3f, 0.95f, 0.22f, 0.95f, floatArrayOf(0.92f, 0.22f, 0.18f, 1f))
                mb.addBox(5.0f, 2.0f, -10.3f, 0.85f, 0.30f, 0.85f, floatArrayOf(0.96f, 0.84f, 0.30f, 1f))

                // 2. Central Food Court: 12 Dining Tables with 4 Chairs & Food Plates/Cups on every table!
                val chairSeat = floatArrayOf(0.16f, 0.45f, 0.78f, 1f)
                val chairLeg = floatArrayOf(0.22f, 0.24f, 0.28f, 1f)
                val plateWhite = floatArrayOf(0.98f, 0.98f, 0.99f, 1f)
                val mealWarm = floatArrayOf(0.92f, 0.52f, 0.18f, 1f)
                val saladGreen = floatArrayOf(0.18f, 0.78f, 0.28f, 1f)
                val juiceOrange = floatArrayOf(0.98f, 0.68f, 0.08f, 1f)

                val tableCols = listOf(-8.8f, -3.0f, 3.0f, 8.8f)
                val tableRows = listOf(-2.6f, 2.2f, 7.0f)
                for (tx in tableCols) {
                    for (tz in tableRows) {
                        // 4 Chairs tucked neatly around each table (North, South, East, West)
                        for ((cxOff, czOff, backX, backZ) in listOf(
                            listOf(-1.28f, 0f, -1.48f, 0f),
                            listOf(1.28f, 0f, 1.48f, 0f),
                            listOf(0f, -0.96f, 0f, -1.16f),
                            listOf(0f, 0.96f, 0f, 1.16f)
                        )) {
                            val chX = tx + cxOff
                            val chZ = tz + czOff
                            mb.addBox(chX, 0.24f, chZ, 0.42f, 0.44f, 0.42f, chairLeg)
                            mb.addBox(chX, 0.48f, chZ, 0.48f, 0.08f, 0.48f, chairSeat)
                            val isSide = kotlin.math.abs(cxOff) > 0.1f
                            mb.addBox(
                                tx + backX, 0.74f, tz + backZ,
                                if (isSide) 0.08f else 0.48f, 0.46f, if (isSide) 0.48f else 0.08f, chairSeat
                            )
                        }
                        // Dinner Plates, Vegetables & Drinks resting on top of every table (Y = 0.88..1.05)
                        mb.addBox(tx - 0.55f, 0.91f, tz - 0.28f, 0.42f, 0.05f, 0.42f, plateWhite)
                        mb.addBox(tx - 0.55f, 0.96f, tz - 0.28f, 0.26f, 0.06f, 0.26f, mealWarm)
                        mb.addBox(tx + 0.55f, 0.91f, tz + 0.28f, 0.42f, 0.05f, 0.42f, plateWhite)
                        mb.addBox(tx + 0.55f, 0.96f, tz + 0.28f, 0.26f, 0.06f, 0.26f, saladGreen)
                        mb.addCylinder(tx + 0.15f, 0.89f, tz - 0.35f, 0.09f, 0.24f, juiceOrange, segments = 6)
                        mb.addCylinder(tx - 0.15f, 0.89f, tz + 0.35f, 0.09f, 0.24f, floatArrayOf(0.2f, 0.8f, 1f, 1f), segments = 6)
                    }
                }
            } else if (interiorType == InteriorType.METRO_CARD_SHOP) {
                // 💳🚇 Interior of «مركزي لبيع كروت المترو»
                val counterCyan = floatArrayOf(0.12f, 0.88f, 0.98f, 1f)
                val glassShield = floatArrayOf(0.72f, 0.94f, 1.0f, 1f)
                val terminalBody = floatArrayOf(0.10f, 0.14f, 0.20f, 1f)
                val cardColors = listOf(
                    floatArrayOf(0.06f, 0.78f, 0.95f, 1f), // Cyan Single Ride
                    floatArrayOf(0.76f, 0.80f, 0.86f, 1f), // Silver 10-Trips
                    floatArrayOf(0.98f, 0.76f, 0.12f, 1f), // Gold VIP
                    floatArrayOf(0.58f, 0.28f, 0.95f, 1f)  // Platinum Monthly
                )
                // Glass teller partition + NFC card readers & cash trays on the sales counter (Z = -4.5)
                mb.addBox(0f, 1.95f, -4.1f, 7.6f, 1.15f, 0.08f, glassShield)
                mb.addBox(0f, 2.58f, -4.1f, 7.8f, 0.14f, 0.18f, counterCyan)
                for (rx in listOf(-2.4f, 0f, 2.4f)) {
                    mb.addBox(rx, 1.42f, -3.9f, 0.55f, 0.18f, 0.42f, terminalBody)
                    mb.addBox(rx, 1.54f, -3.9f, 0.40f, 0.05f, 0.26f, counterCyan)
                    // Stacks of ready-to-sell Metro cards on the counter
                    mb.addBox(rx + 0.65f, 1.38f, -4.1f, 0.34f, 0.14f, 0.22f, cardColors[0])
                }
                // Giant Glowing Card Showcase Panels on Back Wall Display (Z = -8.1)
                for (ci in cardColors.indices) {
                    val cxCard = -3.15f + ci * 2.1f
                    mb.addBox(cxCard, 2.05f, -7.65f, 1.65f, 1.05f, 0.14f, cardColors[ci])
                    mb.addBox(cxCard - 0.42f, 2.12f, -7.56f, 0.32f, 0.24f, 0.04f, floatArrayOf(1f, 0.84f, 0.15f, 1f))
                }
                // Smart NFC Printer LED Screen & freshly printed card tray (-6.8, -4.5)
                mb.addBox(-6.8f, 2.02f, -4.2f, 1.4f, 0.65f, 0.9f, terminalBody)
                mb.addBox(-6.8f, 2.06f, -3.72f, 1.05f, 0.42f, 0.06f, counterCyan)
                mb.addBox(-6.8f, 1.72f, -3.65f, 0.50f, 0.08f, 0.38f, cardColors[2])

                // 2 Waiting Passenger Figures standing cleanly in front of the sales counter (non-blocking)
                for ((px, shirtColor) in listOf(
                    Pair(-1.8f, floatArrayOf(0.14f, 0.58f, 0.92f, 1f)),
                    Pair(1.8f, floatArrayOf(0.92f, 0.34f, 0.18f, 1f))
                )) {
                    val pz = -2.2f
                    mb.addBox(px, 0.45f, pz, 0.42f, 0.90f, 0.28f, floatArrayOf(0.14f, 0.18f, 0.26f, 1f))
                    mb.addBox(px, 1.15f, pz, 0.54f, 0.56f, 0.32f, shirtColor)
                    mb.addBox(px, 1.62f, pz, 0.36f, 0.34f, 0.34f, floatArrayOf(1.0f, 0.84f, 0.02f, 1f))
                }
            }
        }

        // Ceiling lights
        if (interiorType == InteriorType.UNDERGROUND_GARAGE) {
            for (lz in listOf(-14f, -6f, 2f, 10f, 17f)) {
                mb.addBox(0f, rh - 0.05f, lz, 7.5f, 0.08f, 0.55f, floatArrayOf(0.92f, 0.97f, 1f, 1f))
            }
        } else {
            for (lx in listOf(-rw / 3f, 0f, rw / 3f)) {
                for (lz in listOf(-rl / 3f, 0f, rl / 3f)) {
                    mb.addBox(lx, rh - 0.05f, lz, 1.5f, 0.08f, 1.5f, floatArrayOf(1f, 1f, 0.9f, 1f))
                }
            }
        }

        interiorWaterBuffer = interiorWaterMb.build()
        interiorWaterVertexCount = interiorWaterMb.getVertexCount()
        return Pair(mb.build(), mb.getVertexCount())
    }

    /**
     * Adds a single framed glass window flush on an interior wall (backed by the solid wall, zero void gaps).
     */
    private fun addInteriorWindow(
        mb: MeshBuilder,
        x: Float,
        y: Float,
        z: Float,
        span: Float,
        height: Float,
        alongZ: Boolean
    ) {
        val frameColor = floatArrayOf(0.14f, 0.15f, 0.18f, 1f) // Matte black aluminum frame
        val sillColor = floatArrayOf(0.94f, 0.92f, 0.88f, 1f) // Travertine marble sill
        val glassSkyColor = floatArrayOf(0.55f, 0.84f, 0.98f, 1f) // Bright daylight sky-blue glass
        val glassGlowStrip = floatArrayOf(0.85f, 0.95f, 1.0f, 1f) // Sunlight highlight

        if (alongZ) {
            // Mounted on Left (-X) or Right (+X) wall
            mb.addBox(x, y, z, 0.06f, height, span, glassSkyColor)
            mb.addBox(x, y + height * 0.22f, z, 0.07f, 0.22f, span * 0.85f, glassGlowStrip)
            // Perimeter Frame & Mullions
            mb.addBox(x, y + height / 2f, z, 0.12f, 0.12f, span + 0.16f, frameColor)
            mb.addBox(x, y - height / 2f - 0.04f, z, 0.20f, 0.12f, span + 0.26f, sillColor)
            mb.addBox(x, y, z - span / 2f, 0.12f, height, 0.12f, frameColor)
            mb.addBox(x, y, z + span / 2f, 0.12f, height, 0.12f, frameColor)
            mb.addBox(x, y, z, 0.10f, height, 0.08f, frameColor) // Center vertical mullion
            mb.addBox(x, y + 0.15f, z, 0.10f, 0.08f, span, frameColor) // Horizontal transom
        } else {
            // Mounted on Back (-Z) or Front (+Z) wall
            mb.addBox(x, y, z, span, height, 0.06f, glassSkyColor)
            mb.addBox(x, y + height * 0.22f, z, span * 0.85f, 0.22f, 0.07f, glassGlowStrip)
            // Perimeter Frame & Mullions
            mb.addBox(x, y + height / 2f, z, span + 0.16f, 0.12f, 0.12f, frameColor)
            mb.addBox(x, y - height / 2f - 0.04f, z, span + 0.26f, 0.12f, 0.20f, sillColor)
            mb.addBox(x - span / 2f, y, z, 0.12f, height, 0.12f, frameColor)
            mb.addBox(x + span / 2f, y, z, 0.12f, height, 0.12f, frameColor)
            mb.addBox(x, y, z, 0.08f, height, 0.10f, frameColor) // Center vertical mullion
            mb.addBox(x, y + 0.15f, z, span, 0.08f, 0.10f, frameColor) // Horizontal transom
        }
    }

    /**
     * Places exactly 6 detailed panoramic glass windows (6 شبابيك) across both stories of the Villa.
     */
    private fun buildVillaSixWindows(mb: MeshBuilder, rw: Float, rl: Float) {
        // 3 Windows on Ground Floor (Y = 2.3f):
        // 1. Left Wall (-X) overlooking the Indoor Swimming Pool
        addInteriorWindow(mb, -rw / 2f + 0.06f, 2.3f, -5.2f, 4.2f, 2.5f, alongZ = true)
        // 2. Back Wall (-Z) behind the Formal Majlis
        addInteriorWindow(mb, -0.5f, 2.3f, -rl / 2f + 0.06f, 4.0f, 2.4f, alongZ = false)
        // 3. Right Wall (+X) in the Modern Kitchen
        addInteriorWindow(mb, rw / 2f - 0.06f, 2.3f, -6.0f, 3.8f, 2.4f, alongZ = true)

        // 3 Windows on Second Floor (Y = 7.0f):
        // 4. Left Wall (-X) in the Master Bedroom Suite
        addInteriorWindow(mb, -rw / 2f + 0.06f, 7.0f, -6.5f, 4.2f, 2.5f, alongZ = true)
        // 5. Back Wall (-Z) in the Second Bedroom
        addInteriorWindow(mb, 7.5f, 7.0f, -rl / 2f + 0.06f, 3.8f, 2.4f, alongZ = false)
        // 6. Front Wall (+Z) in the Upper Family Hallway
        addInteriorWindow(mb, 2.5f, 7.0f, rl / 2f - 0.06f, 4.2f, 2.4f, alongZ = false)
    }

    /**
     * Places exactly 6 framed glass windows (6 شبابيك) around all 4 interior walls of every standard house.
     */
    private fun buildStandardHouseSixWindows(mb: MeshBuilder, rw: Float, rl: Float) {
        val wy = 2.25f
        val winSpan = 2.6f
        val winHeight = 1.75f
        // 1 & 2. Front Wall (+Z) flanking the closed double door
        addInteriorWindow(mb, -4.8f, wy, rl / 2f - 0.06f, winSpan, winHeight, alongZ = false)
        addInteriorWindow(mb, 4.8f, wy, rl / 2f - 0.06f, winSpan, winHeight, alongZ = false)
        // 3 & 4. Back Wall (-Z) behind Living TV & Kitchen
        addInteriorWindow(mb, -4.2f, wy, -rl / 2f + 0.06f, winSpan, winHeight, alongZ = false)
        addInteriorWindow(mb, 4.2f, wy, -rl / 2f + 0.06f, winSpan, winHeight, alongZ = false)
        // 5. Left Wall (-X) beside Bedroom & Study area
        addInteriorWindow(mb, -rw / 2f + 0.06f, wy, 0.2f, 3.0f, winHeight, alongZ = true)
        // 6. Right Wall (+X) beside Dining & Kitchen area
        addInteriorWindow(mb, rw / 2f - 0.06f, wy, 0.2f, 3.0f, winHeight, alongZ = true)
    }

    /**
     * Adds 6 detailed framed exterior glass windows (6 شبابيك) to every house exterior:
     * 2 Front flanking the door, 2 Back, 1 Left side, and 1 Right side.
     */
    private fun addHouseSixExteriorWindows(mb: MeshBuilder, b: Building, cx: Float, cz: Float) {
        val frameColor = floatArrayOf(0.16f, 0.18f, 0.22f, 1f)
        val sillColor = floatArrayOf(0.92f, 0.90f, 0.86f, 1f)
        val glassBlue = floatArrayOf(0.45f, 0.78f, 0.95f, 1f)
        val glassWarmGlow = floatArrayOf(0.98f, 0.90f, 0.62f, 1f)

        val frontZ = cz + b.depth / 2f + 0.08f
        val backZ = cz - b.depth / 2f - 0.08f
        val leftX = cx - b.width / 2f - 0.08f
        val rightX = cx + b.width / 2f + 0.08f

        val winW = (b.width * 0.20f).coerceIn(2.0f, 3.4f)
        val sideWinW = (b.depth * 0.24f).coerceIn(2.2f, 3.6f)
        val winH = (b.height * 0.26f).coerceIn(1.8f, 3.2f)
        val baseWinY = (b.height * 0.42f).coerceIn(2.4f, 4.5f)
        val upperWinY = if (b.height >= 15f) baseWinY + 4.4f else baseWinY

        fun drawWindowXY(wx: Float, wy: Float, wz: Float, w: Float, h: Float) {
            mb.addBox(wx, wy, wz, w + 0.28f, h + 0.28f, 0.10f, frameColor)
            mb.addBox(wx, wy - h / 2f - 0.14f, wz, w + 0.46f, 0.16f, 0.24f, sillColor)
            mb.addBox(wx, wy, wz + (if (wz > cz) 0.04f else -0.04f), w, h, 0.06f, glassBlue)
            mb.addBox(wx, wy + h * 0.15f, wz + (if (wz > cz) 0.06f else -0.06f), w * 0.65f, h * 0.35f, 0.04f, glassWarmGlow)
            mb.addBox(wx, wy, wz + (if (wz > cz) 0.07f else -0.07f), 0.10f, h, 0.06f, frameColor)
            mb.addBox(wx, wy + 0.10f, wz + (if (wz > cz) 0.07f else -0.07f), w, 0.10f, 0.06f, frameColor)
        }

        fun drawWindowZY(wx: Float, wy: Float, wz: Float, span: Float, h: Float) {
            mb.addBox(wx, wy, wz, 0.10f, h + 0.28f, span + 0.28f, frameColor)
            mb.addBox(wx, wy - h / 2f - 0.14f, wz, 0.24f, 0.16f, span + 0.46f, sillColor)
            mb.addBox(wx + (if (wx > cx) 0.04f else -0.04f), wy, wz, 0.06f, h, span, glassBlue)
            mb.addBox(wx + (if (wx > cx) 0.06f else -0.06f), wy + h * 0.15f, wz, 0.04f, h * 0.35f, span * 0.65f, glassWarmGlow)
            mb.addBox(wx + (if (wx > cx) 0.07f else -0.07f), wy, wz, 0.06f, h, 0.10f, frameColor)
            mb.addBox(wx + (if (wx > cx) 0.07f else -0.07f), wy + 0.10f, wz, 0.06f, 0.10f, span, frameColor)
        }

        val xOff = (b.width * 0.29f).coerceAtLeast(3.2f)
        // 1 & 2: Two Front Windows flanking the front door
        drawWindowXY(cx - xOff, baseWinY, frontZ, winW, winH)
        drawWindowXY(cx + xOff, baseWinY, frontZ, winW, winH)
        // 3 & 4: Two Back Windows
        drawWindowXY(cx - xOff, upperWinY, backZ, winW, winH)
        drawWindowXY(cx + xOff, upperWinY, backZ, winW, winH)
        // 5: Left Side Window
        drawWindowZY(leftX, baseWinY, cz, sideWinW, winH)
        // 6: Right Side Window
        drawWindowZY(rightX, upperWinY, cz, sideWinW, winH)
    }

    /**
     * Builds an open-bay 1-car garage attached next to a house («كراج يكفي سيارة واحدة جنب البيت»).
     * Hollow inside with concrete bay floor, parking guide lines, rolled-up sectional header, lights, and driveway apron.
     */
    private fun addSingleCarAttachedGarage(
        mb: MeshBuilder,
        b: Building,
        cx: Float,
        cz: Float,
        wallColor: FloatArray,
        roofColor: FloatArray
    ) {
        val garW = 4.4f
        val garH = 3.8f
        val garD = 7.4f
        val wallT = 0.28f
        val garX = cx + b.width / 2f + garW / 2f
        val garZ = cz + b.depth / 2f - garD / 2f
        val frontZ = garZ + garD / 2f

        val concreteFloor = floatArrayOf(0.28f, 0.30f, 0.34f, 1f)
        val asphaltApron = floatArrayOf(0.18f, 0.19f, 0.22f, 1f)
        val doorPanelWhite = floatArrayOf(0.92f, 0.93f, 0.95f, 1f)
        val frameDark = floatArrayOf(0.14f, 0.15f, 0.18f, 1f)
        val guideLineWhite = floatArrayOf(0.95f, 0.96f, 0.98f, 1f)
        val entryEmerald = floatArrayOf(0.18f, 0.88f, 0.56f, 1f)
        val bumperYellow = floatArrayOf(0.96f, 0.80f, 0.14f, 1f)
        val warmLight = floatArrayOf(1.0f, 0.92f, 0.55f, 1f)

        // 1. Concrete bay floor + smooth 5.2m asphalt driveway apron extending to the street edge
        mb.addBox(garX, 0.10f, garZ, garW, 0.14f, garD, concreteFloor)
        mb.addBox(garX, 0.05f, frontZ + 2.6f, garW - 0.1f, 0.08f, 5.2f, asphaltApron)

        // Single-car bay lane markings (1 car width = ~2.0m centered inside 3.84m bay)
        mb.addBox(garX - 1.32f, 0.18f, garZ + 0.2f, 0.12f, 0.02f, garD - 1.0f, guideLineWhite)
        mb.addBox(garX + 1.32f, 0.18f, garZ + 0.2f, 0.12f, 0.02f, garD - 1.0f, guideLineWhite)
        // Emerald entry rails on driveway apron so players immediately spot the 1-car driveway from the street
        mb.addBox(garX - 1.32f, 0.10f, frontZ + 2.3f, 0.12f, 0.02f, 4.2f, entryEmerald)
        mb.addBox(garX + 1.32f, 0.10f, frontZ + 2.3f, 0.12f, 0.02f, 4.2f, entryEmerald)
        // Yellow tire bumper inside the bay
        mb.addBox(garX, 0.20f, garZ - garD / 2f + 1.1f, 2.2f, 0.10f, 0.24f, bumperYellow)

        // 2. Hollow walls (Left shared, Right outer, Back wall — front +Z completely open for driving straight in)
        mb.addBox(garX - garW / 2f + wallT / 2f, garH / 2f + 0.10f, garZ, wallT, garH, garD, wallColor)
        mb.addBox(garX + garW / 2f - wallT / 2f, garH / 2f + 0.10f, garZ, wallT, garH, garD, wallColor)
        mb.addBox(garX, garH / 2f + 0.10f, garZ - garD / 2f + wallT / 2f, garW, garH, wallT, wallColor)

        // 3. Roof slab + matching pitched accent roof cap
        mb.addBox(garX, garH + 0.22f, garZ, garW + 0.3f, 0.26f, garD + 0.3f, roofColor)
        mb.addPeakedRoof(garX, garH + 0.35f, garZ, garW + 0.35f, 1.15f, garD + 0.35f, roofColor)

        // 4. High clearance garage header + side jamb trim (sectional door animates dynamically when approaching!)
        mb.addBox(garX, garH - 0.18f, frontZ - 0.08f, garW, 0.44f, 0.22f, wallColor)
        // Side jamb trim framing the 3.84m doorway
        mb.addBox(garX - garW / 2f + 0.20f, (garH - 0.35f) / 2f + 0.10f, frontZ, 0.12f, garH - 0.35f, 0.16f, frameDark)
        mb.addBox(garX + garW / 2f - 0.20f, (garH - 0.35f) / 2f + 0.10f, frontZ, 0.12f, garH - 0.35f, 0.16f, frameDark)

        // 5. Interior ceiling light + glowing "1-CAR" emerald beacon bar & warm porch lanterns
        mb.addBox(garX, garH - 0.04f, garZ, 1.1f, 0.10f, 2.2f, warmLight)
        mb.addBox(garX, garH + 0.06f, frontZ + 0.12f, garW - 1.3f, 0.14f, 0.16f, entryEmerald)
        mb.addBox(garX - garW / 2f + 0.32f, garH - 0.65f, frontZ + 0.12f, 0.22f, 0.24f, 0.18f, warmLight)
        mb.addBox(garX + garW / 2f - 0.32f, garH - 0.65f, frontZ + 0.12f, 0.22f, 0.24f, 0.18f, warmLight)
    }

    /**
     * Builds a realistic front-of-house envelope drop box / mailbox («مكان وضع الظرف أمام البيت في الشوارع»).
     * Features a stone pad, metal post, steel mailbox with brass envelope slot, visible white envelope, and red flag.
     */
    private fun addHouseEnvelopeMailbox(
        mb: MeshBuilder,
        mx: Float,
        mz: Float,
        accentColor: FloatArray
    ) {
        val stoneBase = floatArrayOf(0.55f, 0.56f, 0.58f, 1f)
        val postDark = floatArrayOf(0.16f, 0.18f, 0.22f, 1f)
        val boxNavy = floatArrayOf(0.14f, 0.26f, 0.46f, 1f)
        val brassGold = floatArrayOf(0.95f, 0.78f, 0.22f, 1f)
        val slotInterior = floatArrayOf(0.05f, 0.06f, 0.08f, 1f)
        val envelopeWhite = floatArrayOf(0.98f, 0.97f, 0.94f, 1f)
        val stampRed = floatArrayOf(0.88f, 0.18f, 0.18f, 1f)
        val mailGlow = floatArrayOf(0.20f, 0.85f, 0.98f, 1f)

        // 1. Stone pad + subtle sidewalk interaction border
        mb.addBox(mx, 0.08f, mz, 0.78f, 0.04f, 0.78f, mailGlow)
        mb.addBox(mx, 0.12f, mz, 0.64f, 0.08f, 0.64f, stoneBase)

        // 2. Vertical dark steel support post
        mb.addBox(mx, 0.72f, mz, 0.15f, 1.15f, 0.15f, postDark)
        mb.addBox(mx, 1.28f, mz, 0.44f, 0.08f, 0.58f, postDark)

        // 3. Mailbox housing & roof cap
        mb.addBox(mx, 1.50f, mz, 0.50f, 0.38f, 0.72f, boxNavy)
        mb.addPeakedRoof(mx, 1.69f, mz, 0.54f, 0.16f, 0.76f, accentColor)

        // 4. Brass Envelope Slot Bezel on front (+Z facing the street) + recessed dark opening
        val frontZ = mz + 0.37f
        mb.addBox(mx, 1.54f, frontZ, 0.38f, 0.14f, 0.04f, brassGold)
        mb.addBox(mx, 1.54f, frontZ + 0.015f, 0.32f, 0.07f, 0.03f, slotInterior)

        // 5. Crisp White Envelope («ظرف») visibly tucked halfway in the front slot with a red wax seal!
        mb.addBox(mx, 1.53f, frontZ + 0.10f, 0.28f, 0.025f, 0.22f, envelopeWhite)
        mb.addBox(mx, 1.548f, frontZ + 0.14f, 0.06f, 0.015f, 0.06f, stampRed)

        // 6. Red Mail Indicator Flag on right side
        mb.addBox(mx + 0.27f, 1.60f, mz - 0.10f, 0.04f, 0.34f, 0.06f, stampRed)
        mb.addBox(mx + 0.27f, 1.72f, mz - 0.02f, 0.04f, 0.12f, 0.14f, stampRed)
    }

    /**
     * Builds a reinforced Flat Rooftop Airplane Landing & Parking Pad on every Street House
     * ("مكان الطيارة للصف فوق البيت") plus a steel exterior access ladder on the West flank.
     */
    private fun addStreetHouseRooftopAirplanePad(
        mb: MeshBuilder,
        b: Building,
        cx: Float,
        cz: Float,
        accentColor: FloatArray
    ) {
        val tarmac = floatArrayOf(0.18f, 0.20f, 0.24f, 1f)
        val tarmacBorder = floatArrayOf(0.32f, 0.35f, 0.40f, 1f)
        val yellowLine = floatArrayOf(1.0f, 0.84f, 0.12f, 1f)
        val whitePaint = floatArrayOf(0.96f, 0.96f, 0.98f, 1f)
        val emeraldBeacon = floatArrayOf(0.10f, 0.95f, 0.52f, 1f)
        val steelLadder = floatArrayOf(0.55f, 0.58f, 0.64f, 1f)

        val padW = b.width + 1.2f
        val padD = b.depth + 1.2f
        val deckTopY = b.height + 0.25f

        // 1. Structural Concrete Roof Rim & Tarmac Landing Deck
        mb.addBox(cx, b.height + 0.05f, cz, padW + 0.25f, 0.14f, padD + 0.25f, accentColor)
        mb.addBox(cx, b.height + 0.15f, cz, padW, 0.20f, padD, tarmac)

        // 2. Yellow Airplane Parking Box Borders & Corner Beacons
        val halfW = padW / 2f - 0.6f
        val halfD = padD / 2f - 0.6f
        mb.addBox(cx, deckTopY + 0.015f, cz + halfD, padW - 1.6f, 0.03f, 0.22f, yellowLine)
        mb.addBox(cx, deckTopY + 0.015f, cz - halfD, padW - 1.6f, 0.03f, 0.22f, yellowLine)
        mb.addBox(cx - halfW, deckTopY + 0.015f, cz, 0.22f, 0.03f, padD - 1.6f, yellowLine)
        mb.addBox(cx + halfW, deckTopY + 0.015f, cz, 0.22f, 0.03f, padD - 1.6f, yellowLine)

        // Center Airplane Nosewheel Guide T-stripe & Wing Alignment Stripes
        mb.addBox(cx, deckTopY + 0.02f, cz, 0.36f, 0.03f, padD * 0.58f, whitePaint)
        mb.addBox(cx, deckTopY + 0.02f, cz + 0.4f, 4.4f, 0.03f, 0.36f, whitePaint)
        mb.addBox(cx, deckTopY + 0.02f, cz - 1.8f, 1.8f, 0.03f, 0.28f, whitePaint)

        // 4 Glowing Corner Aviation Lights + Safety Corner Curbs
        for (sx in listOf(-1f, 1f)) {
            for (sz in listOf(-1f, 1f)) {
                val bx = cx + sx * (padW / 2f - 0.25f)
                val bz = cz + sz * (padD / 2f - 0.25f)
                mb.addBox(bx, deckTopY + 0.14f, bz, 0.55f, 0.28f, 0.55f, tarmacBorder)
                mb.addBox(bx, deckTopY + 0.32f, bz, 0.30f, 0.16f, 0.30f, emeraldBeacon)
            }
        }

        // 3. Exterior Steel Rooftop Access Ladder mounted on West (-X) Wall so player can also climb on foot
        val ladX = cx - b.width / 2f - 0.28f
        val ladZ = cz
        mb.addCylinder(ladX, 0.05f, ladZ - 0.32f, 0.05f, b.height + 0.8f, steelLadder, segments = 6)
        mb.addCylinder(ladX, 0.05f, ladZ + 0.32f, 0.05f, b.height + 0.8f, steelLadder, segments = 6)
        val rungs = (b.height / 0.55f).toInt()
        for (r in 1..rungs) {
            mb.addBox(ladX, r * 0.55f, ladZ, 0.07f, 0.06f, 0.64f, steelLadder)
        }
        // Glowing Climb Pad at base & top of ladder
        mb.addBox(ladX - 0.55f, 0.08f, ladZ, 1.1f, 0.06f, 1.4f, emeraldBeacon)
    }

    private fun buildVillaLuxuryFurniture(
        mb: MeshBuilder,
        waterMb: MeshBuilder,
        rw: Float,
        rl: Float,
        rh: Float
    ) {
        val darkOak = floatArrayOf(0.18f, 0.16f, 0.14f, 1f)
        val beigeFabric = floatArrayOf(0.91f, 0.89f, 0.84f, 1f)
        val creamFabric = floatArrayOf(0.94f, 0.92f, 0.88f, 1f)
        val crispWhite = floatArrayOf(0.96f, 0.96f, 0.98f, 1f)
        val brassGold = floatArrayOf(0.88f, 0.72f, 0.28f, 1f)
        val warmLamp = floatArrayOf(1.0f, 0.92f, 0.65f, 1f)
        val marbleWhite = floatArrayOf(0.94f, 0.94f, 0.92f, 1f)
        val wallCream = floatArrayOf(0.96f, 0.95f, 0.92f, 1f)
        val charcoalPanel = floatArrayOf(0.18f, 0.18f, 0.20f, 1f)
        val luxuryRug = floatArrayOf(0.88f, 0.86f, 0.82f, 1f)
        val chromeSilver = floatArrayOf(0.85f, 0.88f, 0.92f, 1f)
        val aquaGlass = floatArrayOf(0.55f, 0.85f, 0.95f, 1f)
        val waterTurquoise = floatArrayOf(0.06f, 0.78f, 0.92f, 1f)

        // =========================================================================
        // GROUND FLOOR (Y = 0.0f) — الطابق الأول
        // =========================================================================

        // 1. 🛋️ الصالة العائلية الفاخرة (Family Living Salon)
        mb.addBox(0f, 0.015f, 0.6f, 6.0f, 0.02f, 4.8f, luxuryRug)
        // Main L-shaped Sofa at Z = +2.1f
        mb.addBox(0f, 0.28f, 2.1f, 4.4f, 0.48f, 1.3f, beigeFabric)
        mb.addBox(0f, 0.95f, 2.65f, 4.4f, 0.90f, 0.30f, beigeFabric)
        mb.addBox(-2.1f, 0.28f, 1.0f, 1.3f, 0.48f, 2.2f, beigeFabric)
        mb.addBox(-2.65f, 0.95f, 1.0f, 0.30f, 0.90f, 2.2f, beigeFabric)
        mb.addBox(2.25f, 0.65f, 2.1f, 0.30f, 0.65f, 1.3f, beigeFabric)
        for (cx in listOf(-1.2f, 0f, 1.2f)) {
            mb.addBox(cx, 0.48f, 1.9f, 1.15f, 0.18f, 1.0f, creamFabric)
        }
        mb.addBox(-1.3f, 0.75f, 2.35f, 0.45f, 0.45f, 0.18f, brassGold)
        mb.addBox(1.2f, 0.75f, 2.35f, 0.45f, 0.45f, 0.18f, floatArrayOf(0.75f, 0.65f, 0.55f, 1f))
        // Coffee Table at Z = -0.4f
        mb.addBox(0f, 0.32f, -0.4f, 2.4f, 0.10f, 1.4f, marbleWhite)
        mb.addBox(0f, 0.16f, -0.4f, 2.2f, 0.22f, 1.2f, darkOak)
        mb.addDome(0.35f, 0.55f, -0.4f, 0.18f, floatArrayOf(0.95f, 0.45f, 0.65f, 1f))
        // 98" Marble Feature TV Wall at Z = -5.8f
        val tvZ = -5.8f
        mb.addBox(0f, 2.2f, tvZ, 5.2f, 4.3f, 0.28f, marbleWhite)
        mb.addBox(0f, 2.2f, tvZ + 0.02f, 5.4f, 4.35f, 0.06f, warmLamp)
        mb.addBox(0f, 2.2f, tvZ + 0.18f, 4.0f, 2.2f, 0.06f, floatArrayOf(0.06f, 0.06f, 0.08f, 1f))
        mb.addBox(0f, 2.2f, tvZ + 0.22f, 3.8f, 2.0f, 0.02f, floatArrayOf(0.10f, 0.35f, 0.65f, 1f))
        mb.addBox(0f, 0.42f, tvZ + 0.35f, 4.6f, 0.56f, 0.65f, darkOak)
        // Grand Chandelier
        mb.addCylinder(0f, 3.65f, 0f, 0.04f, 0.75f, brassGold)
        mb.addBox(0f, 3.60f, 0f, 2.0f, 0.12f, 2.0f, brassGold)
        mb.addBox(0f, 3.35f, 0f, 1.4f, 0.18f, 1.4f, warmLamp)

        // 2. ☕ مجلس الضيوف الفاخر (Formal Majlis at Z = -11.0f)
        mb.addBox(0f, 0.35f, -11.0f, 3.8f, 0.50f, 1.1f, floatArrayOf(0.24f, 0.34f, 0.45f, 1f))
        mb.addBox(0f, 0.90f, -11.45f, 3.8f, 0.75f, 0.25f, floatArrayOf(0.24f, 0.34f, 0.45f, 1f))
        mb.addBox(0f, 0.38f, -9.4f, 1.4f, 0.08f, 1.4f, brassGold)
        mb.addCylinder(0f, 0.02f, -9.4f, 0.15f, 0.36f, brassGold)
        mb.addCylinder(0f, 0.42f, -9.4f, 0.08f, 0.28f, brassGold)
        mb.addDome(0f, 0.70f, -9.4f, 0.09f, brassGold)

        // 3. 🍽️ المطبخ العصري الأمريكي (Modern Kitchen, X = +6.8..+13.4, Z = -11.5..-1.0)
        val islandX = 6.8f
        val islandZ = -6.0f
        mb.addBox(islandX, 0.94f, islandZ, 1.8f, 0.10f, 4.4f, marbleWhite)
        mb.addBox(islandX, 0.47f, islandZ - 2.15f, 1.8f, 0.94f, 0.10f, marbleWhite)
        mb.addBox(islandX, 0.47f, islandZ + 2.15f, 1.8f, 0.94f, 0.10f, marbleWhite)
        mb.addBox(islandX, 0.46f, islandZ, 1.6f, 0.86f, 4.0f, darkOak)
        mb.addDome(islandX, 1.15f, islandZ, 0.22f, floatArrayOf(0.95f, 0.85f, 0.45f, 1f))
        for (stoolStep in listOf(-1.4f, -0.5f, 0.5f, 1.4f)) {
            val sz = islandZ + stoolStep
            val sx = islandX - 1.2f
            mb.addCylinder(sx, 0.02f, sz, 0.06f, 0.65f, darkOak)
            mb.addBox(sx, 0.68f, sz, 0.52f, 0.08f, 0.52f, charcoalPanel)
            mb.addBox(sx - 0.22f, 0.90f, sz, 0.08f, 0.38f, 0.52f, charcoalPanel)
        }
        for (pZ in listOf(islandZ - 1.2f, islandZ, islandZ + 1.2f)) {
            mb.addCylinder(islandX, 3.2f, pZ, 0.02f, 1.2f, brassGold)
            mb.addDome(islandX, 3.15f, pZ, 0.22f, brassGold)
            mb.addDome(islandX, 3.05f, pZ, 0.15f, warmLamp)
        }
        val rearX = 12.6f
        mb.addBox(rearX, 0.45f, islandZ, 1.4f, 0.90f, 4.9f, darkOak)
        mb.addBox(rearX - 0.05f, 0.92f, islandZ, 1.5f, 0.08f, 5.0f, marbleWhite)
        mb.addBox(rearX + 0.62f, 1.55f, islandZ, 0.10f, 1.2f, 4.8f, marbleWhite)
        mb.addBox(rearX + 0.52f, 2.10f, islandZ, 0.08f, 0.06f, 4.8f, warmLamp)
        mb.addBox(rearX + 0.30f, 2.80f, islandZ, 0.8f, 1.2f, 4.8f, darkOak)
        mb.addBox(rearX - 0.15f, 1.20f, -4.0f, 0.55f, 0.45f, 0.55f, floatArrayOf(0.85f, 0.18f, 0.18f, 1f)) // Espresso
        // Double Refrigerator
        mb.addBox(rearX, 1.55f, -1.8f, 1.45f, 2.9f, 1.5f, floatArrayOf(0.88f, 0.90f, 0.94f, 1f))
        mb.addBox(rearX - 0.74f, 1.55f, -1.8f, 0.02f, 2.8f, 0.02f, darkOak)
        // Robot Charging Dock in North-East corner
        mb.addBox(12.2f, 0.10f, -10.8f, 2.1f, 0.20f, 2.1f, charcoalPanel)
        mb.addBox(12.2f, 0.95f, -11.6f, 1.4f, 1.5f, 0.25f, floatArrayOf(0.10f, 0.75f, 0.95f, 1f))

        // 4. 🏊 مسبح الفيلا الداخلي الخاص (Deep Recessed Indoor Pool, drops DOWN to -1.05m and brim-full with still turquoise water!)
        val poolX = -9.8f
        val poolZ = -5.2f
        val poolTileFloor = floatArrayOf(0.06f, 0.44f, 0.68f, 1f)
        val poolTileWall = floatArrayOf(0.10f, 0.56f, 0.80f, 1f)
        val stillIndoorWaterTop = floatArrayOf(0.08f, 0.82f, 0.96f, 0.60f)
        val stillIndoorWaterDeep = floatArrayOf(0.05f, 0.64f, 0.88f, 0.30f)

        // Deep tiled pool floor (-1.08 .. -0.96)
        mb.addBox(poolX, -1.04f, poolZ, 3.9f, 0.14f, 7.9f, poolTileFloor)
        // 4 Inner vertical azure mosaic pool walls (from floor -1.05 up to +0.24)
        mb.addBox(poolX, -0.40f, poolZ - 3.98f, 4.1f, 1.32f, 0.12f, poolTileWall)
        mb.addBox(poolX, -0.40f, poolZ + 3.98f, 4.1f, 1.32f, 0.12f, poolTileWall)
        mb.addBox(poolX - 1.98f, -0.40f, poolZ, 0.12f, 1.32f, 8.0f, poolTileWall)
        mb.addBox(poolX + 1.98f, -0.40f, poolZ, 0.12f, 1.32f, 8.0f, poolTileWall)

        // Low flush marble coping rim (open center so character steps right in)
        mb.addBox(poolX, 0.14f, poolZ - 4.15f, 4.6f, 0.28f, 0.45f, marbleWhite)
        mb.addBox(poolX, 0.14f, poolZ + 4.15f, 4.6f, 0.28f, 0.45f, marbleWhite)
        mb.addBox(poolX - 2.15f, 0.14f, poolZ, 0.45f, 0.28f, 8.6f, marbleWhite)
        mb.addBox(poolX + 2.15f, 0.14f, poolZ, 0.45f, 0.28f, 8.6f, marbleWhite)

        // Wide submerged marble entry steps along the East rim (-8.05 .. -8.85) leading down into deep center
        mb.addBox(poolX + 1.65f, -0.18f, poolZ, 0.55f, 0.24f, 2.4f, marbleWhite)
        mb.addBox(poolX + 1.25f, -0.46f, poolZ, 0.55f, 0.26f, 2.4f, marbleWhite)
        mb.addBox(poolX + 0.85f, -0.74f, poolZ, 0.55f, 0.26f, 2.4f, marbleWhite)

        // Underwater LED glow emitters resting on deep floor
        mb.addBox(poolX - 0.3f, -0.95f, poolZ - 2.2f, 2.4f, 0.04f, 0.38f, floatArrayOf(0.55f, 0.98f, 1.0f, 1f))
        mb.addBox(poolX - 0.3f, -0.95f, poolZ + 2.2f, 2.4f, 0.04f, 0.38f, floatArrayOf(0.55f, 0.98f, 1.0f, 1f))

        // Translucent still crystal turquoise water filling the recessed pool right up near the rim (y = +0.08f)
        waterMb.addQuad(
            poolX - 1.92f, 0.08f, poolZ + 3.92f,
            poolX + 1.92f, 0.08f, poolZ + 3.92f,
            poolX + 1.92f, 0.08f, poolZ - 3.92f,
            poolX - 1.92f, 0.08f, poolZ - 3.92f,
            0f, 1f, 0f, stillIndoorWaterTop
        )
        waterMb.addQuad(
            poolX - 1.92f, -0.42f, poolZ + 3.92f,
            poolX + 1.92f, -0.42f, poolZ + 3.92f,
            poolX + 1.92f, -0.42f, poolZ - 3.92f,
            poolX - 1.92f, -0.42f, poolZ - 3.92f,
            0f, 1f, 0f, stillIndoorWaterDeep
        )

        // Chrome handrails at South-East entry edge
        mb.addCylinder(poolX + 1.85f, 0.28f, poolZ + 3.2f, 0.04f, 0.78f, chromeSilver)
        mb.addCylinder(poolX + 1.85f, 0.28f, poolZ + 3.7f, 0.04f, 0.78f, chromeSilver)
        // 2 Poolside Loungers on Marble Deck
        for (lz in listOf(-3.5f, -6.8f)) {
            mb.addBox(-6.6f, 0.26f, lz, 0.95f, 0.22f, 1.95f, creamFabric)
            mb.addBox(-6.2f, 0.48f, lz, 0.30f, 0.35f, 0.95f, creamFabric)
        }

        // 5. 🚰 حمام الضيوف في الطابق الأول (Ground Floor Guest Bathroom, Front-Left Corner)
        // Tiled Divider Walls (Doorway on X = -7.2 at Z = 6.0..8.4)
        mb.addBox(-10.35f, 2.2f, 4.0f, 6.3f, 4.4f, 0.25f, wallCream)
        mb.addBox(-7.2f, 2.2f, 4.9f, 0.25f, 4.4f, 1.8f, wallCream)
        mb.addBox(-7.2f, 2.2f, 10.6f, 0.25f, 4.4f, 4.2f, wallCream)
        mb.addBox(-7.2f, 3.65f, 7.2f, 0.25f, 1.5f, 2.4f, wallCream) // Door header
        // Marble Vanity Sink & Illuminated Mirror
        mb.addBox(-12.5f, 0.45f, 10.6f, 1.0f, 0.90f, 2.0f, marbleWhite)
        mb.addBox(-12.95f, 1.75f, 10.6f, 0.08f, 1.3f, 1.8f, floatArrayOf(0.80f, 0.92f, 0.98f, 1f))
        mb.addCylinder(-12.55f, 0.92f, 10.6f, 0.04f, 0.25f, chromeSilver)
        // Ceramic Toilet
        mb.addBox(-12.6f, 0.24f, 5.4f, 0.75f, 0.48f, 0.55f, crispWhite)
        mb.addBox(-12.88f, 0.62f, 5.4f, 0.25f, 0.55f, 0.55f, crispWhite)
        // Walk-in Tiled Shower Corner
        mb.addBox(-9.2f, 0.08f, 11.3f, 2.0f, 0.14f, 2.0f, marbleWhite)
        mb.addBox(-8.2f, 1.25f, 11.3f, 0.06f, 2.3f, 2.0f, aquaGlass)
        mb.addCylinder(-9.2f, 2.15f, 12.1f, 0.04f, 0.35f, chromeSilver)

        // 6. 🪜 الدرج الفاخر المؤدي للطابق الثاني (Grand 3D Walkable Staircase, X = 9.7..13.3, Z = 11.4 -> 3.0)
        val stepCount = 16
        val startZ = 11.4f
        val endZ = 3.0f
        val topY = 4.6f
        val stepDepth = (startZ - endZ) / stepCount
        val stepRise = topY / stepCount
        for (i in 0 until stepCount) {
            val sz = startZ - (i + 0.5f) * stepDepth
            val sy = (i + 0.5f) * stepRise
            mb.addBox(11.5f, sy / 2f + sy / 2f, sz, 3.4f, stepRise + 0.08f, stepDepth + 0.04f, marbleWhite)
            // Warm LED step strip
            mb.addBox(11.5f, sy + stepRise * 0.45f, sz + stepDepth * 0.48f, 3.3f, 0.03f, 0.04f, warmLamp)
        }
        // Under-stair enclosure wall on Floor 1 (X = 9.5, Z = 3.0..10.2)
        mb.addBox(9.5f, 2.2f, 6.6f, 0.25f, 4.4f, 7.2f, wallCream)
        // Sloped Handrail Posts along staircase
        for (i in 0..4) {
            val t = i / 4f
            val pz = startZ - t * (startZ - endZ)
            val py = t * topY
            mb.addCylinder(9.75f, py, pz, 0.04f, 0.95f, brassGold)
            mb.addCylinder(13.25f, py, pz, 0.04f, 0.95f, brassGold)
        }

        // =========================================================================
        // SECOND FLOOR SLAB & GUARDRAILS (Y = 4.6f) — الطابق الثاني
        // =========================================================================
        val floor2Y = 4.52f
        // West Main Slab (-14 .. +9.5, full Z -13..+13)
        mb.addBox(-2.25f, floor2Y, 0f, 23.5f, 0.16f, rl, marbleWhite)
        // North-East Slab (+9.5 .. +14, Z -13 .. +3.0) leaving Stairwell Opening at X 9.5..14, Z 3.0..12.2
        mb.addBox(11.75f, floor2Y, -5.0f, 4.5f, 0.16f, 16.0f, marbleWhite)
        // South-East Edge Slab (+9.5 .. +14, Z +12.0 .. +13.0)
        mb.addBox(11.75f, floor2Y, 12.5f, 4.5f, 0.16f, 1.0f, marbleWhite)

        // Floor 1 Sub-ceiling warm soffit lights (avoiding stairwell cutout)
        for (lx in listOf(-8.5f, 0f, 6.8f)) {
            for (lz in listOf(-8.0f, 0f, 8.0f)) {
                mb.addBox(lx, 4.41f, lz, 1.4f, 0.05f, 1.4f, warmLamp)
            }
        }

        // Glass & Brass Stairwell Balustrades on Floor 2 (X = 9.5 & Z = 11.8)
        mb.addBox(9.5f, 5.10f, 7.5f, 0.08f, 1.0f, 8.8f, aquaGlass)
        mb.addBox(9.5f, 5.62f, 7.5f, 0.14f, 0.08f, 8.8f, brassGold)
        mb.addBox(11.6f, 5.10f, 11.8f, 4.2f, 1.0f, 0.08f, aquaGlass)
        mb.addBox(11.6f, 5.62f, 11.8f, 4.2f, 0.08f, 0.14f, brassGold)

        // =========================================================================
        // FLOOR 2 ROOMS (Y = 4.6f)
        // =========================================================================
        val f2 = 4.60f

        // 1. 🛏️ غرفة النوم الرئيسية (Master Bedroom Suite, North-West Quadrant)
        // Partition walls (Doorway on X = -2.2 at Z = -4.2..-1.4; Z = -1.0 at X = -6.5..-3.5)
        mb.addBox(-2.2f, f2 + 2.4f, -8.4f, 0.25f, 4.8f, 8.4f, wallCream)
        mb.addBox(-2.2f, f2 + 3.95f, -2.8f, 0.25f, 1.7f, 2.8f, wallCream) // Door header
        mb.addBox(-10.1f, f2 + 2.4f, -1.0f, 7.2f, 4.8f, 0.25f, wallCream)
        mb.addBox(-2.85f, f2 + 2.4f, -1.0f, 1.3f, 4.8f, 0.25f, wallCream)
        mb.addBox(-5.0f, f2 + 3.95f, -1.0f, 3.0f, 1.7f, 0.25f, wallCream) // Door header

        // King Bed with Tall Gold Slatted Headboard at (-8.5, f2, -9.5)
        val bedX = -8.5f
        val bedZ = -9.5f
        mb.addBox(bedX, f2 + 0.02f, bedZ, 4.4f, 0.03f, 4.8f, luxuryRug)
        mb.addBox(bedX, f2 + 0.26f, bedZ, 2.8f, 0.42f, 3.4f, beigeFabric)
        mb.addBox(bedX, f2 + 0.58f, bedZ, 2.6f, 0.35f, 3.2f, crispWhite)
        mb.addBox(bedX, f2 + 0.70f, bedZ + 0.35f, 2.65f, 0.14f, 2.2f, creamFabric)
        mb.addBox(bedX - 0.65f, f2 + 0.82f, bedZ - 1.0f, 0.95f, 0.18f, 0.65f, crispWhite)
        mb.addBox(bedX + 0.65f, f2 + 0.82f, bedZ - 1.0f, 0.95f, 0.18f, 0.65f, crispWhite)
        val hbZ = bedZ - 1.65f
        mb.addBox(bedX, f2 + 1.65f, hbZ, 3.2f, 2.1f, 0.14f, floatArrayOf(0.85f, 0.82f, 0.78f, 1f))
        for (slat in -6..6) {
            mb.addBox(bedX + slat * 0.24f, f2 + 1.65f, hbZ + 0.08f, 0.04f, 2.05f, 0.04f, brassGold)
        }
        mb.addBox(bedX, f2 + 2.70f, hbZ + 0.08f, 3.2f, 0.06f, 0.06f, brassGold)
        for (side in listOf(-1, 1)) {
            val nx = bedX + side * 2.1f
            val nz = bedZ - 1.1f
            mb.addBox(nx, f2 + 0.35f, nz, 0.85f, 0.68f, 0.75f, darkOak)
            mb.addBox(nx, f2 + 0.69f, nz, 0.87f, 0.04f, 0.77f, marbleWhite)
            mb.addCylinder(nx, f2 + 0.72f, nz, 0.04f, 0.32f, brassGold)
            mb.addCylinder(nx, f2 + 1.04f, nz, 0.18f, 0.28f, warmLamp)
        }
        // Dressing Vanity & Mirror + Walk-in Wardrobe + Safe
        mb.addBox(-3.8f, f2 + 0.45f, -9.5f, 0.9f, 0.85f, 2.0f, darkOak)
        mb.addBox(-3.4f, f2 + 1.65f, -9.5f, 0.06f, 1.4f, 1.6f, floatArrayOf(0.92f, 0.95f, 1.0f, 1f))
        mb.addBox(-12.6f, f2 + 1.4f, -4.5f, 1.2f, 2.8f, 3.4f, darkOak)
        mb.addBox(-12.0f, f2 + 1.4f, -4.5f, 0.06f, 2.6f, 3.2f, brassGold)
        mb.addBox(-13.2f, f2 + 1.2f, -9.0f, 0.65f, 1.1f, 1.0f, charcoalPanel)
        mb.addCylinder(-12.85f, f2 + 1.2f, -9.0f, 0.14f, 0.06f, brassGold)

        // 2. 🛁 الحمام الرئيسي الفاخر (Master Bathroom, South-West Quadrant)
        mb.addBox(-8.3f, f2 + 2.4f, 1.8f, 10.6f, 4.8f, 0.25f, wallCream)
        mb.addBox(-3.0f, f2 + 2.4f, 3.0f, 0.25f, 4.8f, 2.4f, wallCream)
        mb.addBox(-3.0f, f2 + 2.4f, 9.8f, 0.25f, 4.8f, 5.6f, wallCream)
        mb.addBox(-3.0f, f2 + 3.95f, 5.6f, 0.25f, 1.7f, 2.8f, wallCream) // Door header

        // Marble Jacuzzi Spa
        mb.addBox(-9.8f, f2 + 0.45f, 9.6f, 3.1f, 0.85f, 3.1f, marbleWhite)
        mb.addBox(-9.8f, f2 + 0.78f, 9.6f, 2.5f, 0.18f, 2.5f, waterTurquoise)
        mb.addBox(-11.1f, f2 + 0.98f, 9.6f, 0.18f, 0.25f, 0.22f, chromeSilver)
        mb.addBox(-8.5f, f2 + 0.95f, 9.6f, 0.25f, 0.18f, 0.65f, crispWhite)
        // Glass Walk-in Shower Cabin
        mb.addBox(-11.8f, f2 + 0.08f, 3.6f, 2.2f, 0.14f, 2.2f, marbleWhite)
        mb.addBox(-10.7f, f2 + 1.25f, 3.6f, 0.06f, 2.4f, 2.2f, aquaGlass)
        mb.addBox(-11.8f, f2 + 1.25f, 4.7f, 2.2f, 2.4f, 0.06f, aquaGlass)
        mb.addCylinder(-12.6f, f2 + 2.15f, 3.6f, 0.05f, 0.45f, chromeSilver)
        // Double Vanity Sink & Mirror
        mb.addBox(-5.6f, f2 + 0.45f, 11.8f, 2.6f, 0.90f, 0.9f, marbleWhite)
        mb.addBox(-5.6f, f2 + 1.75f, 12.2f, 2.4f, 1.3f, 0.08f, floatArrayOf(0.84f, 0.94f, 0.98f, 1f))
        mb.addCylinder(-6.2f, f2 + 0.92f, 11.8f, 0.04f, 0.25f, chromeSilver)
        mb.addCylinder(-5.0f, f2 + 0.92f, 11.8f, 0.04f, 0.25f, chromeSilver)
        // Ceramic Toilet
        mb.addBox(-5.2f, f2 + 0.24f, 3.4f, 0.65f, 0.48f, 0.85f, crispWhite)
        mb.addBox(-5.2f, f2 + 0.62f, 2.95f, 0.65f, 0.55f, 0.25f, crispWhite)

        // 3. 🛏️ غرفة النوم الثانية (Kids / Guests Bedroom, North-East Quadrant)
        mb.addBox(1.5f, f2 + 2.4f, -7.5f, 0.25f, 4.8f, 10.2f, wallCream)
        mb.addBox(3.35f, f2 + 2.4f, -2.4f, 3.7f, 4.8f, 0.25f, wallCream)
        mb.addBox(10.9f, f2 + 2.4f, -2.4f, 5.4f, 4.8f, 0.25f, wallCream)
        mb.addBox(6.7f, f2 + 3.95f, -2.4f, 3.0f, 1.7f, 0.25f, wallCream) // Door header

        // 2 Modern Beds
        val blueQuilt = floatArrayOf(0.28f, 0.52f, 0.82f, 1f)
        val mintQuilt = floatArrayOf(0.24f, 0.70f, 0.58f, 1f)
        for ((bx, quilt) in listOf(Pair(10.8f, blueQuilt), Pair(4.2f, mintQuilt))) {
            mb.addBox(bx, f2 + 0.24f, -9.6f, 2.0f, 0.40f, 3.0f, darkOak)
            mb.addBox(bx, f2 + 0.52f, -9.6f, 1.85f, 0.30f, 2.8f, crispWhite)
            mb.addBox(bx, f2 + 0.64f, -9.1f, 1.88f, 0.12f, 1.8f, quilt)
            mb.addBox(bx, f2 + 0.74f, -10.5f, 1.1f, 0.16f, 0.55f, crispWhite)
            mb.addBox(bx, f2 + 1.05f, -11.1f, 2.1f, 1.2f, 0.12f, darkOak)
        }
        // Study Desk with Monitor & Built-in Wardrobe
        mb.addBox(3.2f, f2 + 0.42f, -4.6f, 1.9f, 0.80f, 1.0f, darkOak)
        mb.addBox(3.2f, f2 + 1.12f, -4.75f, 0.9f, 0.55f, 0.08f, floatArrayOf(0.10f, 0.15f, 0.22f, 1f))
        mb.addBox(12.6f, f2 + 1.4f, -5.2f, 1.1f, 2.8f, 2.8f, darkOak)

        // 4. 🛋️ جلسة الطابق العلوي أمام الشباك البانورامي (Upper Lounge)
        mb.addBox(2.5f, f2 + 0.35f, 8.8f, 2.2f, 0.55f, 1.2f, beigeFabric)
        mb.addBox(2.5f, f2 + 0.78f, 9.3f, 2.2f, 0.65f, 0.25f, beigeFabric)
        mb.addBox(2.5f, f2 + 0.30f, 7.3f, 1.1f, 0.45f, 0.8f, marbleWhite)
    }

    /**
     * 🏛️ Luxury Neoclassical Villa Architecture (مطابقة لصورة الإعلان العقاري "فيلا فاخرة للبيع"):
     * - 2-Story grand villa structure with Travertine marble facade & classical floor moldings
     * - Grand central arched neoclassical entrance portico with classical fluted columns
     * - 2nd floor center arched balcony & side balconies with black wrought-iron railings
     * - Panoramic floor-to-ceiling glass windows with black mullions & glowing interior crystal chandeliers
     * - Dark charcoal hipped slate roof
     * - Continuous warm golden LED roofline perimeter contour strip tracing the roof eaves
     * - Classical carriage lanterns on columns & balconies emitting warm golden glow
     * - Left side modern outdoor Pergola Lounge with dark horizontal beams, beige L-sofa & coffee table
     * - Private sparkling turquoise swimming pool with white marble coping, underwater lights & sun loungers
     * - Travertine garden stepping stones through the lawn
     * - Modern glowing glass cube floor lanterns along the pool coping and garden paths
     * - Front cascading marble entrance staircase leading to the grand door
     * - Potted spherical topiary shrubs in black cubic planters and lush pink flowering garden bushes
     * - 4-car luxury garage with 2 sectional ribbed roll-up doors, lights, and private driveway
     */
    private fun buildNeoclassicalVillaExterior(
        mb: MeshBuilder,
        waterMb: MeshBuilder,
        b: Building,
        cx: Float,
        cz: Float
    ) {
        val spec = b.getVillaExteriorSpec() ?: return

        val travertine = floatArrayOf(0.95f, 0.93f, 0.88f, 1f) // Warm travertine marble
        val travertineDarker = floatArrayOf(0.90f, 0.88f, 0.82f, 1f)
        val corniceWhite = floatArrayOf(0.98f, 0.97f, 0.94f, 1f) // Neoclassical moldings
        val slateRoof = floatArrayOf(0.20f, 0.20f, 0.22f, 1f) // Dark charcoal hipped roof
        val ledRoofGlow = floatArrayOf(1.0f, 0.88f, 0.35f, 1f) // Signature warm golden LED roof contour
        val blackIron = floatArrayOf(0.10f, 0.10f, 0.12f, 1f) // Black mullions & wrought iron railings
        val windowChandelierGlow = floatArrayOf(1.0f, 0.95f, 0.70f, 1f) // Glowing chandeliers inside windows
        val windowGlassTint = floatArrayOf(0.25f, 0.35f, 0.45f, 0.85f)
        val carriageLanternGlow = floatArrayOf(1.0f, 0.90f, 0.45f, 1f)
        val poolCoping = floatArrayOf(0.92f, 0.92f, 0.90f, 1f) // White marble coping
        val outdoorBeigeFabric = floatArrayOf(0.92f, 0.90f, 0.85f, 1f) // Pergola sofa cushions
        val pergolaDarkWood = floatArrayOf(0.18f, 0.16f, 0.14f, 1f) // Pergola beams
        val grassLawn = floatArrayOf(0.25f, 0.58f, 0.22f, 1f) // Lush estate lawn
        val pinkFlowers = floatArrayOf(0.95f, 0.45f, 0.65f, 1f) // Flowering garden bushes
        val drivewayColor = floatArrayOf(0.24f, 0.24f, 0.26f, 1f)

        val poolX = spec.poolX
        val poolZ = spec.poolZ
        val poolW = spec.poolW
        val poolD = spec.poolD

        // 1. Estate Grounds: Lush green lawn split cleanly around the deep recessed pool hole + travertine podium flush at y=0
        val lawnMinX = spec.lawnMinX
        val lawnMaxX = spec.lawnMaxX
        val lawnMinZ = spec.lawnMinZ
        val lawnMaxZ = spec.lawnMaxZ
        val pMinX = poolX - poolW / 2f
        val pMaxX = poolX + poolW / 2f
        val pMinZ = poolZ - poolD / 2f
        val pMaxZ = poolZ + poolD / 2f
        fun addLawnPart(x1: Float, x2: Float, z1: Float, z2: Float) {
            if (x2 > x1 + 0.05f && z2 > z1 + 0.05f) {
                mb.addBox((x1 + x2) / 2f, 0.08f, (z1 + z2) / 2f, x2 - x1, 0.16f, z2 - z1, grassLawn)
            }
        }
        addLawnPart(lawnMinX, pMinX, lawnMinZ, lawnMaxZ)
        addLawnPart(pMaxX, lawnMaxX, lawnMinZ, lawnMaxZ)
        addLawnPart(pMinX, pMaxX, lawnMinZ, pMinZ)
        addLawnPart(pMinX, pMaxX, pMaxZ, lawnMaxZ)

        if (spec.isCompact) {
            // Travertine podium flush on ground (0.00..0.36) capped before garage edge
            mb.addBox(cx - 0.4f, 0.18f, cz, b.width + 1.6f, 0.36f, b.depth + 2.4f, travertineDarker)
        } else {
            mb.addBox(cx, 0.18f, cz, b.width + 4f, 0.36f, b.depth + 4f, travertineDarker)
        }

        // 2. Main 2-Story Villa Structure
        val gFloorH = spec.gFloorH
        val fFloorH = spec.fFloorH
        val corniceExtra = if (spec.isCompact) 0.7f else 0.9f
        val roofExtra = if (spec.isCompact) 1.1f else 1.4f
        // Ground Floor
        mb.addBox(cx, gFloorH / 2f + 0.35f, cz, b.width, gFloorH, b.depth, travertine)
        // Mid-floor neoclassical cornice molding
        mb.addBox(cx, gFloorH + 0.35f, cz, b.width + corniceExtra, 0.45f, b.depth + corniceExtra, corniceWhite)
        // First Floor (Upper Level)
        mb.addBox(cx, gFloorH + 0.35f + fFloorH / 2f, cz, b.width - 0.4f, fFloorH, b.depth - 0.4f, travertine)
        // Upper grand roof cornice
        val totalRoofBaseY = gFloorH + 0.35f + fFloorH
        mb.addBox(cx, totalRoofBaseY + 0.35f, cz, b.width + roofExtra, 0.7f, b.depth + roofExtra, corniceWhite)

        // 3. Dark Charcoal Hipped Slate Roof
        val slateExtra = if (spec.isCompact) 1.6f else 2.0f
        val slateH = if (spec.isCompact) 3.4f else 4.2f
        mb.addPyramid(cx, totalRoofBaseY + 0.7f, cz, b.width + slateExtra, slateH, b.depth + slateExtra, slateRoof)

        // 4. ✨ Signature Golden LED Roofline Perimeter Strip
        val roofPerimHalfW = (b.width + roofExtra) / 2f
        val roofPerimHalfD = (b.depth + roofExtra) / 2f
        val ledY = totalRoofBaseY + 0.55f
        mb.addBox(cx, ledY, cz + roofPerimHalfD + 0.06f, b.width + roofExtra + 0.1f, 0.22f, 0.12f, ledRoofGlow)
        mb.addBox(cx, ledY, cz - roofPerimHalfD - 0.06f, b.width + roofExtra + 0.1f, 0.22f, 0.12f, ledRoofGlow)
        mb.addBox(cx - roofPerimHalfW - 0.06f, ledY, cz, 0.12f, 0.22f, b.depth + roofExtra + 0.1f, ledRoofGlow)
        mb.addBox(cx + roofPerimHalfW + 0.06f, ledY, cz, 0.12f, 0.22f, b.depth + roofExtra + 0.1f, ledRoofGlow)

        // 5. 🏛️ Grand Neoclassical Arched Entrance Portico
        val frontWallZ = cz + b.depth / 2f
        val porticoZ = frontWallZ + spec.porticoOffsetZ
        val porticoW = spec.porticoW
        val colOffX = spec.columnOffsetX

        if (spec.isCompact) {
            // Compact Grand Entry Stairs descending from podium to walkway (0.00..0.40)
            mb.addBox(cx, 0.28f, frontWallZ + 1.7f, 4.0f, 0.24f, 1.1f, travertine)
            mb.addBox(cx, 0.18f, frontWallZ + 2.65f, 4.2f, 0.24f, 1.0f, travertine)
            mb.addBox(cx, 0.11f, frontWallZ + 3.55f, 4.4f, 0.22f, 1.0f, travertine)
            // Continuous paved walkway connecting stairs to south city sidewalk (top y = 0.20f > 0.16f lawn!)
            mb.addBox(cx, 0.10f, frontWallZ + 6.95f, 2.6f, 0.20f, 5.8f, travertine)
        } else {
            // Grand Estate Entry Stairs
            mb.addBox(cx, 0.35f, frontWallZ + 3.2f, 10.5f, 0.22f, 1.8f, travertine)
            mb.addBox(cx, 0.22f, frontWallZ + 4.6f, 11.5f, 0.20f, 1.6f, travertine)
            mb.addBox(cx, 0.11f, frontWallZ + 5.8f, 12.5f, 0.22f, 1.4f, travertine)
            // Grand paved walkway out to doorPosZ
            mb.addBox(cx, 0.10f, b.doorPosZ, 5.2f, 0.20f, 3.6f, travertine)
        }

        // Classical Fluted Entrance Columns flanking the grand door
        val colRad = if (spec.isCompact) 0.28f else 0.42f
        val plinthSize = if (spec.isCompact) 0.72f else 1.1f
        val colShaftH = gFloorH - 0.70f
        for (colX in listOf(cx - colOffX, cx + colOffX)) {
            mb.addBox(colX, 0.36f, porticoZ, plinthSize, 0.72f, plinthSize, corniceWhite)
            mb.addCylinder(colX, 0.72f, porticoZ, colRad, colShaftH, corniceWhite, 12)
            mb.addBox(colX, gFloorH + 0.10f, porticoZ, plinthSize, 0.42f, plinthSize, corniceWhite)
            val lY = if (spec.isCompact) 3.8f else 4.5f
            mb.addBox(colX, lY, porticoZ + plinthSize * 0.5f, 0.20f, 0.50f, 0.20f, blackIron)
            mb.addBox(colX, lY, porticoZ + plinthSize * 0.5f, 0.14f, 0.36f, 0.14f, carriageLanternGlow)
        }

        // Portico arched canopy header dividing ground and upper balcony
        val canopyDepth = if (spec.isCompact) 2.5f else 3.4f
        mb.addBox(cx, gFloorH + 0.45f, porticoZ, porticoW + 0.5f, 0.65f, canopyDepth, corniceWhite)

        // Grand Entrance Vestibule Backdrop & Frame
        val doorH = spec.doorHeight
        val doorW = spec.doorWidth
        mb.addBox(cx, doorH / 2f + 0.32f, frontWallZ + 0.01f, doorW, doorH, 0.12f, floatArrayOf(0.08f, 0.06f, 0.05f, 1f))
        mb.addBox(cx, doorH + 0.45f, frontWallZ + 0.08f, doorW + 0.6f, 0.24f, 0.24f, corniceWhite)
        mb.addBox(cx - doorW / 2f - 0.14f, doorH / 2f + 0.32f, frontWallZ + 0.08f, 0.22f, doorH + 0.18f, 0.24f, corniceWhite)
        mb.addBox(cx + doorW / 2f + 0.14f, doorH / 2f + 0.32f, frontWallZ + 0.08f, 0.22f, doorH + 0.18f, 0.24f, corniceWhite)

        // Enterable Green Marker placed cleanly above the front walkway (y = 0.22f > 0.20f walkway > 0.16f lawn)
        mb.addBox(b.doorPosX, 0.22f, b.doorPosZ, if (spec.isCompact) 2.0f else 2.6f, 0.05f, if (spec.isCompact) 2.0f else 2.6f, floatArrayOf(0.15f, 0.95f, 0.55f, 1f))

        // 6. 2nd Floor Arched Balcony above Portico
        val archPierH = fFloorH - 0.7f
        val archPierCenterY = gFloorH + 0.7f + archPierH / 2f
        mb.addBox(cx - colOffX, archPierCenterY, porticoZ - 0.15f, plinthSize, archPierH, plinthSize, travertine)
        mb.addBox(cx + colOffX, archPierCenterY, porticoZ - 0.15f, plinthSize, archPierH, plinthSize, travertine)
        mb.addBox(cx, totalRoofBaseY - 0.15f, porticoZ - 0.15f, porticoW, 1.0f, plinthSize, corniceWhite)
        // Black Wrought-Iron Balcony Railing on upper portico terrace
        val railY = gFloorH + 1.35f
        val railDepth = if (spec.isCompact) 1.8f else 2.4f
        mb.addBox(cx, railY, porticoZ + railDepth * 0.52f, porticoW - 0.4f, 1.0f, 0.08f, blackIron)
        mb.addBox(cx - (porticoW - 0.4f) / 2f, railY, porticoZ + 0.1f, 0.08f, 1.0f, railDepth, blackIron)
        mb.addBox(cx + (porticoW - 0.4f) / 2f, railY, porticoZ + 0.1f, 0.08f, 1.0f, railDepth, blackIron)
        // Upper Carriage Lanterns flanking arch
        val upLanternY = gFloorH + fFloorH * 0.65f
        mb.addBox(cx - colOffX, upLanternY, porticoZ + plinthSize * 0.42f, 0.18f, 0.42f, 0.18f, carriageLanternGlow)
        mb.addBox(cx + colOffX, upLanternY, porticoZ + plinthSize * 0.42f, 0.18f, 0.42f, 0.18f, carriageLanternGlow)

        // 7. 🪟 Floor-to-Ceiling Panoramic Windows with Black Mullions & Glowing Chandeliers
        val winW = spec.wingWinW
        val gWinY = if (spec.isCompact) 3.9f else 4.5f
        val gWinH = if (spec.isCompact) 4.6f else 5.6f
        val upWinH = if (spec.isCompact) 4.0f else 5.0f
        val balcW = winW + 0.5f
        for (wingSide in listOf(-1, 1)) {
            val winX = cx + wingSide * spec.wingWinOffsetX
            val frontZ = frontWallZ + 0.08f

            // Ground Floor Panoramic Window
            mb.addBox(winX, gWinY, frontZ, winW + 0.35f, gWinH + 0.35f, 0.12f, blackIron)
            mb.addBox(winX, gWinY, frontZ + 0.02f, winW, gWinH, 0.06f, windowGlassTint)
            mb.addBox(winX, gWinY + 0.5f, frontZ - 0.15f, winW * 0.38f, 1.1f, 0.2f, windowChandelierGlow)
            mb.addBox(winX, gWinY, frontZ + 0.04f, 0.08f, gWinH, 0.08f, blackIron)
            mb.addBox(winX, gWinY, frontZ + 0.04f, winW, 0.08f, 0.08f, blackIron)

            // Upper Floor Panoramic Window with Balcony & Black Railing
            val upY = gFloorH + 0.35f + fFloorH / 2f - 0.2f
            mb.addBox(winX, upY, frontZ - 0.2f, winW + 0.35f, upWinH + 0.35f, 0.12f, blackIron)
            mb.addBox(winX, upY, frontZ - 0.18f, winW, upWinH, 0.06f, windowGlassTint)
            mb.addBox(winX, upY + 0.45f, frontZ - 0.35f, winW * 0.34f, 0.95f, 0.2f, windowChandelierGlow)
            // Upper Floor Balcony Platform & Wrought Iron Railing
            mb.addBox(winX, gFloorH + 0.45f, frontZ + 0.42f, balcW, 0.24f, 1.0f, travertine)
            mb.addBox(winX, gFloorH + 1.10f, frontZ + 0.90f, balcW - 0.15f, 1.0f, 0.06f, blackIron)
            mb.addBox(winX - (balcW - 0.15f) / 2f, gFloorH + 1.10f, frontZ + 0.42f, 0.06f, 1.0f, 0.96f, blackIron)
            mb.addBox(winX + (balcW - 0.15f) / 2f, gFloorH + 1.10f, frontZ + 0.42f, 0.06f, 1.0f, 0.96f, blackIron)
        }

        // Side Panoramic Windows
        val leftSideX = cx - b.width / 2f + 0.16f
        val rightSideX = cx + b.width / 2f - 0.16f
        val upperWinY = gFloorH + 0.35f + fFloorH / 2f - 0.2f
        val sideWinD = if (spec.isCompact) 3.4f else 5.0f
        val sideWinH = if (spec.isCompact) 3.8f else 4.4f
        val sideSides = if (spec.isCompact) listOf(leftSideX to -1f) else listOf(leftSideX to -1f, rightSideX to 1f)
        for ((sx, dir) in sideSides) {
            mb.addBox(sx, upperWinY, cz - (if (spec.isCompact) 0.5f else 2.0f), 0.12f, sideWinH + 0.35f, sideWinD + 0.35f, blackIron)
            mb.addBox(sx + dir * 0.03f, upperWinY, cz - (if (spec.isCompact) 0.5f else 2.0f), 0.06f, sideWinH, sideWinD, windowGlassTint)
            mb.addBox(sx - dir * 0.15f, upperWinY + 0.4f, cz - (if (spec.isCompact) 0.5f else 2.0f), 0.2f, 1.0f, sideWinD * 0.34f, windowChandelierGlow)
            mb.addBox(sx + dir * 0.05f, upperWinY, cz - (if (spec.isCompact) 0.5f else 2.0f), 0.08f, sideWinH, 0.08f, blackIron)
            mb.addBox(sx + dir * 0.05f, upperWinY, cz - (if (spec.isCompact) 0.5f else 2.0f), 0.08f, 0.08f, sideWinD, blackIron)
        }

        // 8. ☕ Left Side Modern Outdoor Pergola Lounge (flush at y=0, patio top above 0.16f grass)
        val pergX = spec.pergX
        val pergZ = spec.pergZ
        val pergW = spec.pergW
        val pergD = spec.pergD
        mb.addBox(pergX, 0.11f, pergZ, pergW + 0.6f, 0.22f, pergD + 0.6f, travertine)
        val postH = if (spec.isCompact) 3.2f else 4.2f
        val postThick = if (spec.isCompact) 0.22f else 0.35f
        for (px in listOf(pergX - pergW / 2f, pergX + pergW / 2f)) {
            for (pz in listOf(pergZ - pergD / 2f, pergZ + pergD / 2f)) {
                mb.addBox(px, postH / 2f + 0.22f, pz, postThick, postH, postThick, pergolaDarkWood)
            }
        }
        val beamCountHalf = if (spec.isCompact) 3 else 4
        val beamStep = pergD / (beamCountHalf * 2 + 1)
        for (stepZ in -beamCountHalf..beamCountHalf) {
            val lz = pergZ + stepZ * beamStep
            mb.addBox(pergX, postH + 0.30f, lz, pergW + 0.4f, 0.20f, 0.10f, pergolaDarkWood)
        }
        // Modern L-Shaped Outdoor Sectional Sofa & Coffee Table inside Pergola
        if (spec.isCompact) {
            mb.addBox(pergX + 0.1f, 0.44f, pergZ - 1.3f, 2.2f, 0.40f, 0.95f, outdoorBeigeFabric)
            mb.addBox(pergX + 0.1f, 0.90f, pergZ - 1.72f, 2.2f, 0.65f, 0.24f, outdoorBeigeFabric)
            mb.addBox(pergX - 0.85f, 0.44f, pergZ - 0.1f, 0.85f, 0.40f, 1.5f, outdoorBeigeFabric)
            mb.addBox(pergX - 1.22f, 0.90f, pergZ - 0.1f, 0.24f, 0.65f, 1.5f, outdoorBeigeFabric)
            mb.addBox(pergX + 0.25f, 0.36f, pergZ + 0.3f, 1.1f, 0.28f, 0.75f, pergolaDarkWood)
            mb.addBox(pergX + 0.25f, 0.60f, pergZ + 0.3f, 0.20f, 0.20f, 0.20f, carriageLanternGlow)
        } else {
            mb.addBox(pergX, 0.45f, pergZ - 2.4f, 5.4f, 0.45f, 1.6f, outdoorBeigeFabric)
            mb.addBox(pergX, 1.05f, pergZ - 3.1f, 5.4f, 0.85f, 0.35f, outdoorBeigeFabric)
            mb.addBox(pergX - 2.4f, 0.45f, pergZ - 0.8f, 1.6f, 0.45f, 2.2f, outdoorBeigeFabric)
            mb.addBox(pergX - 3.1f, 1.05f, pergZ - 0.8f, 0.35f, 0.85f, 2.2f, outdoorBeigeFabric)
            mb.addBox(pergX + 0.5f, 0.35f, pergZ - 0.6f, 2.2f, 0.35f, 1.4f, pergolaDarkWood)
            mb.addBox(pergX + 0.5f, 0.65f, pergZ - 0.6f, 0.25f, 0.25f, 0.25f, carriageLanternGlow)
        }

        // 9. 🏊 Deep Recessed Swimming Pool (drops DOWN to -1.05m in center, brim-full with turquoise water!)
        val outerPoolFloor = floatArrayOf(0.06f, 0.45f, 0.70f, 1f)
        val outerPoolWall = floatArrayOf(0.10f, 0.58f, 0.84f, 1f)
        val stillWaterTop = floatArrayOf(0.08f, 0.82f, 0.96f, 0.62f)
        val stillWaterMid = floatArrayOf(0.06f, 0.66f, 0.90f, 0.28f)

        mb.addBox(poolX, -1.05f, poolZ, poolW, 0.14f, poolD, outerPoolFloor)
        mb.addBox(poolX, -0.42f, poolZ - poolD / 2f - 0.12f, poolW + 0.48f, 1.28f, 0.24f, outerPoolWall)
        mb.addBox(poolX, -0.42f, poolZ + poolD / 2f + 0.12f, poolW + 0.48f, 1.28f, 0.24f, outerPoolWall)
        mb.addBox(poolX - poolW / 2f - 0.12f, -0.42f, poolZ, 0.24f, 1.28f, poolD + 0.48f, outerPoolWall)
        mb.addBox(poolX + poolW / 2f + 0.12f, -0.42f, poolZ, 0.24f, 1.28f, poolD + 0.48f, outerPoolWall)

        // Hollow 4-sided white marble coping border flush above lawn (0.06..0.26)
        val copeT = if (spec.isCompact) 0.48f else 0.64f
        mb.addBox(poolX, 0.16f, poolZ - poolD / 2f - copeT / 2f, poolW + copeT * 2f, 0.20f, copeT, poolCoping)
        mb.addBox(poolX, 0.16f, poolZ + poolD / 2f + copeT / 2f, poolW + copeT * 2f, 0.20f, copeT, poolCoping)
        mb.addBox(poolX - poolW / 2f - copeT / 2f, 0.16f, poolZ, copeT, 0.20f, poolD + 0.04f, poolCoping)
        mb.addBox(poolX + poolW / 2f + copeT / 2f, 0.16f, poolZ, copeT, 0.20f, poolD + 0.04f, poolCoping)

        // Recessed marble entry stairs descending on East edge
        val stepDepthZ = if (spec.isCompact) 1.9f else 3.0f
        mb.addBox(poolX + poolW / 2f - 0.40f, -0.18f, poolZ, 0.80f, 0.24f, stepDepthZ, poolCoping)
        mb.addBox(poolX + poolW / 2f - 1.10f, -0.46f, poolZ, 0.72f, 0.24f, stepDepthZ, poolCoping)
        mb.addBox(poolX + poolW / 2f - 1.80f, -0.74f, poolZ, 0.72f, 0.24f, stepDepthZ, poolCoping)

        // Underwater LED pool floor emitters
        mb.addBox(poolX - poolW * 0.22f, -0.96f, poolZ, 0.50f, 0.04f, 0.50f, floatArrayOf(0.45f, 0.96f, 1.0f, 1f))
        mb.addBox(poolX + poolW * 0.16f, -0.96f, poolZ, 0.50f, 0.04f, 0.50f, floatArrayOf(0.45f, 0.96f, 1.0f, 1f))

        // Translucent brim-full still water layers
        waterMb.addQuad(
            poolX - poolW / 2f, 0.08f, poolZ + poolD / 2f,
            poolX + poolW / 2f, 0.08f, poolZ + poolD / 2f,
            poolX + poolW / 2f, 0.08f, poolZ - poolD / 2f,
            poolX - poolW / 2f, 0.08f, poolZ - poolD / 2f,
            0f, 1f, 0f, stillWaterTop
        )
        waterMb.addQuad(
            poolX - poolW / 2f, -0.42f, poolZ + poolD / 2f,
            poolX + poolW / 2f, -0.42f, poolZ + poolD / 2f,
            poolX + poolW / 2f, -0.42f, poolZ - poolD / 2f,
            poolX - poolW / 2f, -0.42f, poolZ - poolD / 2f,
            0f, 1f, 0f, stillWaterMid
        )

        // Sun Lounger Chaises beside the pool (clearly outside coping, sitting on top of 0.16f lawn)
        val chPositions = if (spec.isCompact) listOf(poolX - 1.1f, poolX + 1.0f) else listOf(poolX - 2.2f, poolX + 0.8f)
        val chZ = if (spec.isCompact) poolZ - poolD / 2f - 1.38f else poolZ - poolD / 2f - 1.6f
        val chW = if (spec.isCompact) 1.05f else 1.4f
        val chD = if (spec.isCompact) 1.65f else 2.6f
        for (chX in chPositions) {
            mb.addBox(chX, 0.27f, chZ, chW, 0.22f, chD, outdoorBeigeFabric)
            mb.addBox(chX, 0.48f, chZ - chD * 0.34f, chW, 0.28f, chD * 0.32f, outdoorBeigeFabric)
        }

        // 10. 🚶 Rectangular Travertine Stepping Stones visibly above the 0.16f lawn (top y = 0.20f)
        if (spec.isCompact) {
            for (stepI in 0..2) {
                val sz = pergZ + pergD / 2f + 0.7f + stepI * 1.15f
                mb.addBox(pergX + 0.6f, 0.18f, sz, 1.1f, 0.06f, 0.65f, travertine)
            }
        } else {
            for (stepI in 0..5) {
                val sz = frontWallZ + 2.5f + stepI * 1.5f
                mb.addBox(poolX + poolW / 2f + 1.8f, 0.18f, sz, 1.4f, 0.06f, 0.85f, travertine)
            }
        }

        // 11. 💡 Modern Glowing Glass Cube Floor Lanterns along pool & walkway (resting cleanly on grass)
        val lanternPositions = if (spec.isCompact) {
            listOf(
                Pair(poolX - poolW / 2f - 0.65f, poolZ + poolD / 2f),
                Pair(poolX + poolW / 2f + 0.65f, poolZ + poolD / 2f),
                Pair(cx + 1.95f, b.doorPosZ + 2.2f)
            )
        } else {
            listOf(
                Pair(poolX - poolW / 2f - 0.8f, poolZ - poolD / 2f),
                Pair(poolX - poolW / 2f - 0.8f, poolZ + poolD / 2f),
                Pair(poolX + poolW / 2f + 0.8f, poolZ + poolD / 2f),
                Pair(pergX + pergW / 2f + 0.6f, pergZ + pergD / 2f - 1f),
                Pair(cx + 6.0f, frontWallZ + 3.8f)
            )
        }
        for (lp in lanternPositions) {
            mb.addBox(lp.first, 0.45f, lp.second, 0.48f, 0.60f, 0.48f, blackIron)
            mb.addBox(lp.first, 0.45f, lp.second, 0.36f, 0.48f, 0.36f, carriageLanternGlow)
        }

        // 12. 🌸 Lush Landscaping: Topiaries, Flowering Bushes & Palm strictly inside estate lawn
        val potXs = if (spec.isCompact) listOf(cx - 2.65f, cx + 2.65f) else listOf(cx - 5.5f, cx + 5.5f)
        val potZ = if (spec.isCompact) frontWallZ + 2.5f else frontWallZ + 4.2f
        for (potX in potXs) {
            mb.addBox(potX, 0.38f, potZ, 0.65f, 0.56f, 0.65f, blackIron)
            mb.addDome(potX, 0.68f, potZ, 0.52f, floatArrayOf(0.18f, 0.55f, 0.18f, 1f))
        }
        val bushXs = if (spec.isCompact) {
            listOf(cx - 4.7f, cx + 4.4f)
        } else {
            listOf(cx - 9.5f, cx - 11.5f, cx + 9.5f, cx + 11.5f)
        }
        val bushZ = if (spec.isCompact) frontWallZ + 0.85f else frontWallZ + 2.0f
        for (bushX in bushXs) {
            mb.addDome(bushX, 0.35f, bushZ, if (spec.isCompact) 0.72f else 1.1f, floatArrayOf(0.20f, 0.50f, 0.20f, 1f))
            mb.addDome(bushX, 0.72f, bushZ, if (spec.isCompact) 0.38f else 0.55f, pinkFlowers)
        }
        val palmX = if (spec.isCompact) lawnMinX + 1.45f else cx - b.width / 2f - 7.5f
        val palmZ = if (spec.isCompact) lawnMinZ + 1.55f else frontWallZ + 6.0f
        mb.addCylinder(palmX, 0.14f, palmZ, if (spec.isCompact) 1.0f else 1.8f, 0.06f, floatArrayOf(0.16f, 0.52f, 0.20f, 1f))
        mb.addCylinder(palmX, 0.16f, palmZ, if (spec.isCompact) 0.24f else 0.35f, if (spec.isCompact) 7.2f else 11.0f, floatArrayOf(0.45f, 0.32f, 0.18f, 1f))
        mb.addDome(palmX, if (spec.isCompact) 7.3f else 11.0f, palmZ, if (spec.isCompact) 2.1f else 3.2f, floatArrayOf(0.15f, 0.62f, 0.15f, 1f))

        // 13. 🚗 Open-Bay Drive-In Luxury Garage & Driveway on right side
        val garX = spec.garX
        val garZ = spec.garZ
        val garW = spec.garW
        val garH = spec.garH
        val garD = spec.garD
        val wt = spec.garWallT
        val garFloorColor = floatArrayOf(0.26f, 0.28f, 0.31f, 1f)
        val bayStripeColor = floatArrayOf(0.96f, 0.82f, 0.15f, 1f)

        // Garage concrete floor pad flush on ground (0.00..0.20f, above 0.16f lawn!)
        mb.addBox(garX, 0.10f, garZ, garW - 0.15f, 0.20f, garD - 0.15f, garFloorColor)
        // Hollow Left, Right & Back Garage Walls (starting from y = 0.00f)
        mb.addBox(garX - garW / 2f + wt / 2f, garH / 2f, garZ, wt, garH, garD, travertine)
        mb.addBox(garX + garW / 2f - wt / 2f, garH / 2f, garZ, wt, garH, garD, travertine)
        mb.addBox(garX, garH / 2f, garZ - garD / 2f + wt / 2f, garW, garH, wt, travertine)
        // Center parking bay divider stripe & rear tire stops
        mb.addBox(garX, 0.21f, garZ + 0.3f, 0.14f, 0.02f, garD - 1.6f, bayStripeColor)
        val stopW = if (spec.isCompact) 1.7f else 2.8f
        val stopZ = garZ - garD / 2f + (if (spec.isCompact) 1.3f else 2.4f)
        mb.addBox(garX - spec.garBayOffset, 0.24f, stopZ, stopW, 0.10f, 0.22f, bayStripeColor)
        mb.addBox(garX + spec.garBayOffset, 0.24f, stopZ, stopW, 0.10f, 0.22f, bayStripeColor)

        // Garage ceiling slab, cornice & dark slate roof
        mb.addBox(garX, garH - 0.15f, garZ, garW, 0.32f, garD, corniceWhite)
        mb.addBox(garX, garH + 0.22f, garZ, garW + 0.55f, 0.42f, garD + 0.55f, corniceWhite)
        mb.addPeakedRoof(garX, garH + 0.43f, garZ, garW + 0.85f, if (spec.isCompact) 1.8f else 2.5f, garD + 0.85f, slateRoof)

        // Warm glowing interior garage light panels
        for (bayOffset in listOf(-spec.garBayOffset, spec.garBayOffset)) {
            mb.addBox(garX + bayOffset, garH - 0.36f, garZ, 1.0f, 0.10f, garD * 0.35f, carriageLanternGlow)
        }

        // 2 Garage Door Headers & Lanterns
        for (doorOffset in listOf(-spec.garBayOffset, spec.garBayOffset)) {
            val dX = garX + doorOffset
            val dZ = garZ + garD / 2f + 0.04f
            mb.addBox(dX, garH - 0.30f, dZ, spec.garBayDoorW + 0.2f, 0.38f, 0.22f, corniceWhite)
            mb.addBox(dX, garH - 0.04f, dZ + 0.18f, 0.30f, 0.26f, 0.20f, carriageLanternGlow)
        }

        // Paved Private Driveway flush on ground (0.00..0.20f) connecting garage to Villa Boulevard curb
        mb.addBox(garX, 0.10f, garZ + garD / 2f + spec.drivewayLen / 2f, spec.drivewayW, 0.20f, spec.drivewayLen, drivewayColor)
    }

    /**
     * Populates the surrounding golden beaches (North, South, East, West) and the huge open ocean
     * with coastal palms, colorful beach umbrellas & sun loungers, wooden sea piers, a lighthouse,
     * offshore sailboats/yachts, and distant horizon sea islands.
     */
    private fun buildCoastalBeachAndSeaDetails(
        mb: MeshBuilder,
        drySandColor: FloatArray,
        wetSandColor: FloatArray,
        grassColor: FloatArray,
        cornicheStone: FloatArray
    ) {
        val woodDeck = floatArrayOf(0.55f, 0.36f, 0.20f, 1f)
        val woodDark = floatArrayOf(0.36f, 0.22f, 0.12f, 1f)
        val palmTrunk = floatArrayOf(0.46f, 0.31f, 0.17f, 1f)
        val palmLeaves = floatArrayOf(0.18f, 0.64f, 0.22f, 1f)
        val palmTop = floatArrayOf(0.24f, 0.72f, 0.28f, 1f)
        val white = floatArrayOf(0.97f, 0.97f, 0.98f, 1f)
        val red = floatArrayOf(0.92f, 0.20f, 0.22f, 1f)
        val cyan = floatArrayOf(0.10f, 0.78f, 0.96f, 1f)
        val gold = floatArrayOf(1.0f, 0.82f, 0.18f, 1f)
        val coral = floatArrayOf(0.98f, 0.46f, 0.25f, 1f)
        val umbrellaColors = listOf(red, cyan, gold, coral)
        val islandRock = floatArrayOf(0.42f, 0.44f, 0.46f, 1f)
        val islandGreen = floatArrayOf(0.20f, 0.56f, 0.24f, 1f)
        val boatNavy = floatArrayOf(0.10f, 0.34f, 0.72f, 1f)
        val lanternGlow = floatArrayOf(1.0f, 0.90f, 0.40f, 1f)

        // Helper: Beach Palm Tree
        fun addBeachPalm(px: Float, pz: Float, h: Float = 7.2f) {
            mb.addCylinder(px, -0.05f, pz, 0.28f, h, palmTrunk, segments = 8)
            mb.addDome(px, h - 0.2f, pz, 2.3f, palmLeaves, slices = 8, stacks = 4)
            mb.addDome(px, h + 0.6f, pz, 1.4f, palmTop, slices = 6, stacks = 3)
        }

        // Helper: Beach Umbrella + Two Sun Lounger Chairs
        fun addBeachUmbrellaSet(ux: Float, uz: Float, canopyColor: FloatArray, loungeAlongZ: Boolean) {
            // Pole & Canopy
            mb.addCylinder(ux, -0.05f, uz, 0.08f, 2.7f, white, segments = 6)
            mb.addDome(ux, 2.35f, uz, 1.85f, canopyColor, slices = 8, stacks = 3)
            mb.addDome(ux, 2.55f, uz, 0.85f, white, slices = 8, stacks = 2)
            // Two Wooden Beach Loungers with Cream Cushions
            if (loungeAlongZ) {
                for (dx in listOf(-1.25f, 1.25f)) {
                    mb.addBox(ux + dx, 0.18f, uz, 0.95f, 0.22f, 2.2f, woodDeck)
                    mb.addBox(ux + dx, 0.33f, uz, 0.85f, 0.08f, 2.05f, white)
                }
            } else {
                for (dz in listOf(-1.25f, 1.25f)) {
                    mb.addBox(ux, 0.18f, uz + dz, 2.2f, 0.22f, 0.95f, woodDeck)
                    mb.addBox(ux, 0.33f, uz + dz, 2.05f, 0.08f, 0.85f, white)
                }
            }
        }

        // Helper: Coastal Yacht / Sailboat floating on open sea
        fun addSeaSailboat(bx: Float, bz: Float, alongX: Boolean, scale: Float = 1.0f) {
            val len = 10.5f * scale
            val wid = 3.6f * scale
            if (alongX) {
                mb.addBox(bx, 0.25f, bz, len, 0.95f, wid, white)
                mb.addBox(bx, 0.85f, bz, len * 0.45f, 0.65f, wid * 0.72f, boatNavy)
                mb.addCylinder(bx + 0.5f, 0.7f, bz, 0.14f * scale, 9.0f * scale, woodDark, segments = 6)
                mb.addBox(bx - 1.2f * scale, 5.2f * scale, bz, 4.2f * scale, 6.2f * scale, 0.12f, white)
            } else {
                mb.addBox(bx, 0.25f, bz, wid, 0.95f, len, white)
                mb.addBox(bx, 0.85f, bz, wid * 0.72f, 0.65f, len * 0.45f, boatNavy)
                mb.addCylinder(bx, 0.7f, bz + 0.5f, 0.14f * scale, 9.0f * scale, woodDark, segments = 6)
                mb.addBox(bx, 5.2f * scale, bz - 1.2f * scale, 0.12f, 6.2f * scale, 4.2f * scale, white)
            }
        }

        // 1. North Beach (-250..+400, Z = -242..-288)
        var idx = 0
        for (x in -235..385 step 36) {
            addBeachPalm(x.toFloat(), -254f, h = 7.5f + (idx % 3) * 0.8f)
            addBeachUmbrellaSet(x.toFloat() + 14f, -270f, umbrellaColors[idx % umbrellaColors.size], loungeAlongZ = true)
            idx++
        }

        // 2. South Beach (-250..+400, Z = +398..+444)
        for (x in -235..385 step 36) {
            addBeachPalm(x.toFloat(), 408f, h = 7.6f + (idx % 3) * 0.7f)
            addBeachUmbrellaSet(x.toFloat() + 14f, 424f, umbrellaColors[idx % umbrellaColors.size], loungeAlongZ = true)
            idx++
        }

        // 3. West Beach (X = -260..-306, Z = -230..+385)
        for (z in -215..370 step 38) {
            addBeachPalm(-269f, z.toFloat(), h = 7.4f + (idx % 3) * 0.8f)
            addBeachUmbrellaSet(-287f, z.toFloat() + 14f, umbrellaColors[idx % umbrellaColors.size], loungeAlongZ = false)
            idx++
        }

        // 4. East Beach (X = +410..+456, Z = -230..+385)
        for (z in -215..370 step 38) {
            addBeachPalm(419f, z.toFloat(), h = 7.8f + (idx % 3) * 0.7f)
            addBeachUmbrellaSet(437f, z.toFloat() + 14f, umbrellaColors[idx % umbrellaColors.size], loungeAlongZ = false)
            idx++
        }

        // 5. Four Wooden Beach Piers extending out over the crystal turquoise sea + Dock Lanterns & Moored Yachts
        // North Pier (X = 0f, Z = -286 .. -334)
        mb.addBox(0f, 0.35f, -310f, 6.2f, 0.28f, 48f, woodDeck)
        for (pz in -290 downTo -332 step 8) {
            mb.addCylinder(-2.6f, -0.4f, pz.toFloat(), 0.25f, 1.3f, woodDark, segments = 6)
            mb.addCylinder(2.6f, -0.4f, pz.toFloat(), 0.25f, 1.3f, woodDark, segments = 6)
            mb.addBox(-2.6f, 1.05f, pz.toFloat(), 0.35f, 0.35f, 0.35f, lanternGlow)
            mb.addBox(2.6f, 1.05f, pz.toFloat(), 0.35f, 0.35f, 0.35f, lanternGlow)
        }
        addSeaSailboat(7.5f, -326f, alongX = false, scale = 1.1f)

        // South Pier (X = 175f, Z = 441 .. 489)
        mb.addBox(175f, 0.35f, 465f, 6.2f, 0.28f, 48f, woodDeck)
        for (pz in 444..486 step 8) {
            mb.addCylinder(172.4f, -0.4f, pz.toFloat(), 0.25f, 1.3f, woodDark, segments = 6)
            mb.addCylinder(177.6f, -0.4f, pz.toFloat(), 0.25f, 1.3f, woodDark, segments = 6)
            mb.addBox(172.4f, 1.05f, pz.toFloat(), 0.35f, 0.35f, 0.35f, lanternGlow)
            mb.addBox(177.6f, 1.05f, pz.toFloat(), 0.35f, 0.35f, 0.35f, lanternGlow)
        }
        addSeaSailboat(183f, 480f, alongX = false, scale = 1.15f)

        // West Pier (X = -303 .. -351, Z = -20f)
        mb.addBox(-327f, 0.35f, -20f, 48f, 0.28f, 6.2f, woodDeck)
        for (px in -306 downTo -348 step 8) {
            mb.addCylinder(px.toFloat(), -0.4f, -22.6f, 0.25f, 1.3f, woodDark, segments = 6)
            mb.addCylinder(px.toFloat(), -0.4f, -17.4f, 0.25f, 1.3f, woodDark, segments = 6)
            mb.addBox(px.toFloat(), 1.05f, -22.6f, 0.35f, 0.35f, 0.35f, lanternGlow)
            mb.addBox(px.toFloat(), 1.05f, -17.4f, 0.35f, 0.35f, 0.35f, lanternGlow)
        }
        addSeaSailboat(-343f, -12f, alongX = true, scale = 1.1f)

        // East Pier (X = 451 .. 499, Z = 40f)
        mb.addBox(475f, 0.35f, 40f, 48f, 0.28f, 6.2f, woodDeck)
        for (px in 454..496 step 8) {
            mb.addCylinder(px.toFloat(), -0.4f, 37.4f, 0.25f, 1.3f, woodDark, segments = 6)
            mb.addCylinder(px.toFloat(), -0.4f, 42.6f, 0.25f, 1.3f, woodDark, segments = 6)
            mb.addBox(px.toFloat(), 1.05f, 37.4f, 0.35f, 0.35f, 0.35f, lanternGlow)
            mb.addBox(px.toFloat(), 1.05f, 42.6f, 0.35f, 0.35f, 0.35f, lanternGlow)
        }
        addSeaSailboat(491f, 48f, alongX = true, scale = 1.2f)

        // 6. Iconic Red-and-White Coastal Lighthouse on South-East Headland (434f, 430f)
        val lhX = 434f
        val lhZ = 430f
        mb.addCylinder(lhX, -0.05f, lhZ, 5.5f, 1.0f, cornicheStone, segments = 12)
        mb.addCylinder(lhX, 0.95f, lhZ, 3.2f, 6.0f, white, segments = 12)
        mb.addCylinder(lhX, 6.95f, lhZ, 2.9f, 6.0f, red, segments = 12)
        mb.addCylinder(lhX, 12.95f, lhZ, 2.6f, 6.0f, white, segments = 12)
        mb.addCylinder(lhX, 18.95f, lhZ, 2.3f, 5.0f, red, segments = 12)
        mb.addCylinder(lhX, 23.95f, lhZ, 3.0f, 0.8f, woodDark, segments = 12)
        mb.addCylinder(lhX, 24.75f, lhZ, 1.8f, 2.8f, lanternGlow, segments = 10)
        mb.addDome(lhX, 27.55f, lhZ, 2.1f, red, slices = 10, stacks = 4)

        // 7. Offshore Cruisers & Sailboats across the vast surrounding Ocean
        val offshoreBoats = listOf(
            Triple(-140f, -340f, true),
            Triple(220f, -380f, true),
            Triple(520f, -150f, false),
            Triple(580f, 210f, false),
            Triple(280f, 510f, true),
            Triple(-90f, 490f, true),
            Triple(-370f, 220f, false),
            Triple(-420f, -190f, false),
            Triple(60f, -520f, true),
            Triple(650f, -40f, false)
        )
        for ((bi, boat) in offshoreBoats.withIndex()) {
            addSeaSailboat(boat.first, boat.second, alongX = boat.third, scale = 1.25f + (bi % 3) * 0.25f)
        }

        // 8. Distant Scenic Horizon Sea Islands far out in the Vast Ocean (~850m - 1100m away)
        val distantSeaIslands = listOf(
            Triple(-820f, -860f, 150f),
            Triple(920f, -820f, 165f),
            Triple(1080f, 110f, 145f),
            Triple(900f, 920f, 160f),
            Triple(-800f, 890f, 155f),
            Triple(-1020f, -70f, 140f)
        )
        for (isl in distantSeaIslands) {
            val ix = isl.first
            val iz = isl.second
            val r = isl.third
            // Sandy reef beach island base
            mb.addCylinder(ix, -0.35f, iz, r * 1.18f, 0.35f, wetSandColor, segments = 14)
            mb.addCylinder(ix, -0.02f, iz, r * 1.05f, 0.45f, drySandColor, segments = 14)
            mb.addCylinder(ix, 0.42f, iz, r * 0.85f, 0.65f, grassColor, segments = 14)
            // Scenic tropical green/rocky island mountain peaks
            mb.addMountain(
                cx = ix,
                cy = 0.8f,
                cz = iz,
                baseRadius = r * 0.72f,
                height = r * 0.68f,
                rockColor = islandGreen,
                snowColor = islandRock,
                sides = 10
            )
        }

        // 9. Four Coastal Car Swap & Rescue Stations ("محطات البدالة الساحلية") + Beach Pedal Boats ("بدالة بحرية")
        val darkPad = floatArrayOf(0.16f, 0.18f, 0.22f, 1f)
        val chrome = floatArrayOf(0.88f, 0.90f, 0.94f, 1f)
        for ((si, st) in CityData.COASTAL_SWAP_STATIONS.withIndex()) {
            val sx = st.padX
            val sz = st.padZ
            val alongX = kotlin.math.abs(st.returnYaw - 90f) < 1f || kotlin.math.abs(st.returnYaw - 270f) < 1f

            // Checkered Rescue / Swap Pad Apron
            mb.addBox(sx, 0.04f, sz, 9.5f, 0.08f, 9.5f, darkPad)
            mb.addBox(sx, 0.09f, sz, 8.6f, 0.03f, 8.6f, cyan)
            mb.addBox(sx, 0.11f, sz, 6.4f, 0.03f, 6.4f, gold)
            mb.addBox(sx, 0.13f, sz, 4.6f, 0.03f, 4.6f, darkPad)

            // Arched Overhead Swap Station Gantry & Glowing Rescue Beacons
            if (alongX) {
                mb.addCylinder(sx, 0.08f, sz - 4.2f, 0.28f, 4.8f, chrome, segments = 8)
                mb.addCylinder(sx, 0.08f, sz + 4.2f, 0.28f, 4.8f, chrome, segments = 8)
                mb.addBox(sx, 4.9f, sz, 1.1f, 0.9f, 9.0f, cyan)
                mb.addBox(sx, 5.5f, sz - 4.2f, 0.55f, 0.55f, 0.55f, gold)
                mb.addBox(sx, 5.5f, sz + 4.2f, 0.55f, 0.55f, 0.55f, gold)
            } else {
                mb.addCylinder(sx - 4.2f, 0.08f, sz, 0.28f, 4.8f, chrome, segments = 8)
                mb.addCylinder(sx + 4.2f, 0.08f, sz, 0.28f, 4.8f, chrome, segments = 8)
                mb.addBox(sx, 4.9f, sz, 9.0f, 0.9f, 1.1f, cyan)
                mb.addBox(sx - 4.2f, 5.5f, sz, 0.55f, 0.55f, 0.55f, gold)
                mb.addBox(sx + 4.2f, 5.5f, sz, 0.55f, 0.55f, 0.55f, gold)
            }

            // Colorful 3D Beach Pedal Boat ("بدالة بحرية") anchored right next to each coastal pier
            val bx = st.boatX
            val bz = st.boatZ
            val hullCol = umbrellaColors[si % umbrellaColors.size]
            // Twin buoyant pontoons
            mb.addBox(bx - 0.85f, 0.12f, bz, 0.55f, 0.45f, 3.6f, hullCol)
            mb.addBox(bx + 0.85f, 0.12f, bz, 0.55f, 0.45f, 3.6f, hullCol)
            // Center pedal deck & cushioned seats + sun canopy
            mb.addBox(bx, 0.35f, bz, 2.0f, 0.16f, 2.8f, white)
            mb.addBox(bx, 0.70f, bz - 0.35f, 1.4f, 0.45f, 0.8f, cyan)
            mb.addCylinder(bx - 0.7f, 0.35f, bz - 0.8f, 0.06f, 1.65f, chrome, segments = 6)
            mb.addCylinder(bx + 0.7f, 0.35f, bz - 0.8f, 0.06f, 1.65f, chrome, segments = 6)
            mb.addBox(bx, 2.05f, bz - 0.2f, 2.1f, 0.12f, 2.2f, hullCol)
        }
    }
}
