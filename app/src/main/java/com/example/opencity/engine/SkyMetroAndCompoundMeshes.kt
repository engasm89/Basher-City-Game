package com.example.opencity.engine

import com.example.opencity.model.CompoundAvenue90
import com.example.opencity.model.MetroStation
import com.example.opencity.model.SkyMetroSystem
import com.example.opencity.model.StairDirection
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

data class MetroCarVisualMeshes(
    val leadBodyBuffer: FloatBuffer,
    val leadBodyCount: Int,
    val leadEmissiveBuffer: FloatBuffer,
    val leadEmissiveCount: Int,
    val coachBodyBuffer: FloatBuffer,
    val coachBodyCount: Int,
    val coachEmissiveBuffer: FloatBuffer,
    val coachEmissiveCount: Int
)

/**
 * Procedural 3D architecture for:
 * 1) «كومباوند 90 أفينيو» (90 Avenue Luxury Compound walls, arched gates, private Sea Lagoon & Olympic Pool)
 * 2) «مترو المدينة الكهربائي المعلق» (Elevated Air Sky Metro viaduct, electric catenary wires,
 *    3 elevated stations with 28-step walkable stairs, 5 EGP turnstile kiosks & 3-car electric train meshes)
 */
object SkyMetroAndCompoundMeshes {

    // =========================================================================
    // 1. COMPOUND 90 AVENUE: WALLS, GATES, PRIVATE SEA LAGOON & OLYMPIC POOL
    // =========================================================================
    fun buildCompoundAvenue90(
        mb: MeshBuilder,
        nightMb: MeshBuilder,
        waterMb: MeshBuilder
    ) {
        val limestone = floatArrayOf(0.93f, 0.90f, 0.84f, 1f)
        val copingCream = floatArrayOf(0.97f, 0.95f, 0.90f, 1f)
        val warmSand = floatArrayOf(0.94f, 0.85f, 0.62f, 1f)
        val wetSand = floatArrayOf(0.82f, 0.72f, 0.49f, 1f)
        val seaBottom = floatArrayOf(0.05f, 0.34f, 0.56f, 1f)
        val bronzeGold = floatArrayOf(0.86f, 0.68f, 0.24f, 1f)
        val ironDark = floatArrayOf(0.14f, 0.16f, 0.20f, 1f)
        val archNavy = floatArrayOf(0.10f, 0.16f, 0.28f, 1f)
        val glassTint = floatArrayOf(0.45f, 0.82f, 0.96f, 1f)
        val marbleWhite = floatArrayOf(0.95f, 0.95f, 0.93f, 1f)
        val palmTrunk = floatArrayOf(0.44f, 0.29f, 0.15f, 1f)
        val palmLeaf = floatArrayOf(0.20f, 0.66f, 0.26f, 1f)
        val pierWood = floatArrayOf(0.54f, 0.36f, 0.20f, 1f)
        val warmLampGlow = floatArrayOf(1.0f, 0.88f, 0.45f, 1f)
        val cyanNeon = floatArrayOf(0.12f, 0.94f, 1.0f, 1f)

        fun addGroundBand(minX: Float, maxX: Float, minZ: Float, maxZ: Float, y: Float, color: FloatArray) {
            if (maxX <= minX + 0.05f || maxZ <= minZ + 0.05f) return
            mb.addQuad(
                minX, y, maxZ,
                maxX, y, maxZ,
                maxX, y, minZ,
                minX, y, minZ,
                0f, 1f, 0f, color
            )
        }

        fun addCompoundWallSegmentX(x1: Float, x2: Float, z: Float) {
            val len = x2 - x1
            if (len < 0.8f) return
            val cx = (x1 + x2) / 2f
            // Masonry lower wall (H = 2.0m) + cream stone coping + decorative iron rail top
            mb.addBox(cx, 1.0f, z, len, 2.0f, 0.55f, limestone)
            mb.addBox(cx, 2.08f, z, len + 0.15f, 0.18f, 0.72f, copingCream)
            mb.addBox(cx, 2.55f, z, len, 0.12f, 0.14f, ironDark)
            // Architectural pilasters & subtle wall sconce glow every 12m
            val steps = (len / 12f).toInt().coerceAtLeast(1)
            val spanStep = len / steps
            for (i in 0..steps) {
                val px = x1 + i * spanStep
                mb.addBox(px, 1.30f, z, 0.75f, 2.60f, 0.78f, copingCream)
                mb.addBox(px, 2.72f, z, 0.90f, 0.24f, 0.92f, bronzeGold)
                nightMb.addBox(px, 2.86f, z, 0.42f, 0.22f, 0.42f, warmLampGlow)
            }
        }

        fun addCompoundWallSegmentZ(x: Float, z1: Float, z2: Float) {
            val len = z2 - z1
            if (len < 0.8f) return
            val cz = (z1 + z2) / 2f
            mb.addBox(x, 1.0f, cz, 0.55f, 2.0f, len, limestone)
            mb.addBox(x, 2.08f, cz, 0.72f, 0.18f, len + 0.15f, copingCream)
            mb.addBox(x, 2.55f, cz, 0.14f, 0.12f, len, ironDark)
            val steps = (len / 14f).toInt().coerceAtLeast(1)
            val spanStep = len / steps
            for (i in 0..steps) {
                val pz = z1 + i * spanStep
                mb.addBox(x, 1.30f, pz, 0.78f, 2.60f, 0.75f, copingCream)
                mb.addBox(x, 2.72f, pz, 0.92f, 0.24f, 0.90f, bronzeGold)
                nightMb.addBox(x, 2.86f, pz, 0.42f, 0.22f, 0.42f, warmLampGlow)
            }
        }

        fun addCompoundGateArch(gMinX: Float, gMaxX: Float, z: Float, isGrandMainGate: Boolean) {
            val gxCenter = (gMinX + gMaxX) / 2f
            val gateSpan = (gMaxX - gMinX) + 1.2f
            val pillarH = if (isGrandMainGate) 6.6f else 5.6f
            val beamY = if (isGrandMainGate) 6.8f else 5.8f

            // Flanking stone & bronze security gate columns strictly outside opening
            for (px in listOf(gMinX - 0.5f, gMaxX + 0.5f)) {
                mb.addBox(px, pillarH / 2f, z, 1.35f, pillarH, 1.35f, copingCream)
                mb.addBox(px, pillarH + 0.25f, z, 1.65f, 0.50f, 1.65f, bronzeGold)
                nightMb.addBox(px, pillarH * 0.5f, z + 0.72f, 0.25f, pillarH * 0.75f, 0.08f, warmLampGlow)
                nightMb.addBox(px, pillarH * 0.5f, z - 0.72f, 0.25f, pillarH * 0.75f, 0.08f, warmLampGlow)
            }

            // High clearance arch lintel leaving road/driveway completely open underneath!
            mb.addBox(gxCenter, beamY, z, gateSpan, 1.25f, 1.45f, archNavy)
            mb.addBox(gxCenter, beamY + 0.70f, z, gateSpan + 0.6f, 0.22f, 1.65f, bronzeGold)
            // Security Guard Canopy / Emblem above arch
            if (isGrandMainGate) {
                mb.addBox(gxCenter, beamY + 1.55f, z, 11.5f, 1.35f, 0.95f, copingCream)
                // Illuminated "90 AVENUE" signature night crown on both faces
                nightMb.addBox(gxCenter, beamY + 1.55f, z + 0.52f, 10.2f, 0.92f, 0.12f, warmLampGlow)
                nightMb.addBox(gxCenter, beamY + 1.55f, z - 0.52f, 10.2f, 0.92f, 0.12f, warmLampGlow)
                nightMb.addBox(gxCenter, beamY - 0.58f, z, gateSpan - 1.2f, 0.14f, 1.50f, cyanNeon)
            } else {
                nightMb.addBox(gxCenter, beamY, z + 0.78f, gateSpan * 0.55f, 0.45f, 0.10f, warmLampGlow)
                nightMb.addBox(gxCenter, beamY, z - 0.78f, gateSpan * 0.55f, 0.45f, 0.10f, warmLampGlow)
            }
        }

        // East Wing Compound Walls & Open Gate Arches
        for (z in listOf(185.5f, 246.0f, 266.0f, 330.5f)) {
            for ((s, e) in CompoundAvenue90.openWallSegments(11.5f, 334.0f, CompoundAvenue90.eastGateIntervals)) {
                addCompoundWallSegmentX(s, e, z)
            }
            for ((gMin, gMax) in CompoundAvenue90.eastGateIntervals) {
                val isGrand = (z == 185.5f && gMin == 42f)
                addCompoundGateArch(gMin, gMax, z, isGrandMainGate = isGrand)
            }
        }
        addCompoundWallSegmentZ(11.5f, 185.5f, 246.0f)
        addCompoundWallSegmentZ(11.5f, 266.0f, 330.5f)
        addCompoundWallSegmentZ(334.0f, 185.5f, 246.0f)
        addCompoundWallSegmentZ(334.0f, 266.0f, 330.5f)

        // West Wing Compound Walls & Open Gate Arches
        for (z in listOf(185.5f, 246.0f, 266.0f, 330.5f)) {
            for ((s, e) in CompoundAvenue90.openWallSegments(-184.0f, -108.0f, CompoundAvenue90.westGateIntervals)) {
                addCompoundWallSegmentX(s, e, z)
            }
            for ((gMin, gMax) in CompoundAvenue90.westGateIntervals) {
                addCompoundGateArch(gMin, gMax, z, isGrandMainGate = (z == 185.5f))
            }
        }
        addCompoundWallSegmentZ(-184.0f, 185.5f, 246.0f)
        addCompoundWallSegmentZ(-184.0f, 266.0f, 330.5f)
        addCompoundWallSegmentZ(-108.0f, 185.5f, 246.0f)
        addCompoundWallSegmentZ(-108.0f, 266.0f, 330.5f)

        // Grand Cobblestone Boulevard Entry Walkways connecting Road Z=175 -> Gate -> Beach -> Pool
        mb.addBox(53f, 0.03f, 182.5f, 18.0f, 0.05f, 7.0f, marbleWhite)
        mb.addBox(61.5f, 0.03f, 213.0f, 4.5f, 0.05f, 48.0f, marbleWhite)

        // ---------------------------------------------------------------------
        // A) PRIVATE TURQUOISE SEA LAGOON INSIDE 90 AVENUE COMPOUND (20..56, 192..238)
        // ---------------------------------------------------------------------
        val sMinX = CompoundAvenue90.SEA_MIN_X
        val sMaxX = CompoundAvenue90.SEA_MAX_X
        val sMinZ = CompoundAvenue90.SEA_MIN_Z
        val sMaxZ = CompoundAvenue90.SEA_MAX_Z
        val bMinX = CompoundAvenue90.BEACH_MIN_X
        val bMaxX = CompoundAvenue90.BEACH_MAX_X
        val bMinZ = CompoundAvenue90.BEACH_MIN_Z
        val bMaxZ = CompoundAvenue90.BEACH_MAX_Z

        // 4 Non-overlapping Golden Sand Beach Strips framing the sunken Lagoon hole
        addGroundBand(bMinX, sMinX, bMinZ, bMaxZ, 0.015f, warmSand) // West sand beach
        addGroundBand(sMaxX, bMaxX, bMinZ, bMaxZ, 0.015f, warmSand) // East sand beach
        addGroundBand(sMinX, sMaxX, bMinZ, sMinZ, 0.015f, warmSand) // North sand beach
        addGroundBand(sMinX, sMaxX, sMaxZ, bMaxZ, 0.015f, warmSand) // South sand beach

        // Wet Sand Shore Trim + Deep Seabed Floor (-0.98m) + Stone Retaining Liner
        val seaCx = (sMinX + sMaxX) / 2f
        val seaCz = (sMinZ + sMaxZ) / 2f
        val seaW = sMaxX - sMinX
        val seaD = sMaxZ - sMinZ
        mb.addBox(seaCx, -0.96f, seaCz, seaW, 0.10f, seaD, seaBottom)
        mb.addBox(seaCx, -0.45f, sMinZ - 0.15f, seaW + 0.5f, 1.0f, 0.30f, wetSand)
        mb.addBox(seaCx, -0.45f, sMaxZ + 0.15f, seaW + 0.5f, 1.0f, 0.30f, wetSand)
        mb.addBox(sMinX - 0.15f, -0.45f, seaCz, 0.30f, 1.0f, seaD + 0.5f, wetSand)
        mb.addBox(sMaxX + 0.15f, -0.45f, seaCz, 0.30f, 1.0f, seaD + 0.5f, wetSand)

        // Underwater Coastal Turquoise Rim LEDs glowing softly inside the compound sea at night
        nightMb.addBox(seaCx, -0.82f, sMinZ + 0.6f, seaW - 4f, 0.08f, 0.25f, cyanNeon)
        nightMb.addBox(seaCx, -0.82f, sMaxZ - 0.6f, seaW - 4f, 0.08f, 0.25f, cyanNeon)

        // Translucent Multi-Layer Sea Lagoon Water (drawn in waterMb pass)
        val compoundSeaTop = floatArrayOf(0.08f, 0.74f, 0.92f, 0.68f)
        val compoundSeaDeep = floatArrayOf(0.03f, 0.48f, 0.76f, 0.38f)
        waterMb.addQuad(
            sMinX, 0.03f, sMaxZ,
            sMaxX, 0.03f, sMaxZ,
            sMaxX, 0.03f, sMinZ,
            sMinX, 0.03f, sMinZ,
            0f, 1f, 0f, compoundSeaTop
        )
        waterMb.addQuad(
            sMinX, -0.40f, sMaxZ,
            sMaxX, -0.40f, sMaxZ,
            sMaxX, -0.40f, sMinZ,
            sMinX, -0.40f, sMinZ,
            0f, 1f, 0f, compoundSeaDeep
        )

        // Wooden Pier Jetty extending over Compound Sea (walkable above water)
        val pierCx = (CompoundAvenue90.PIER_MIN_X + CompoundAvenue90.PIER_MAX_X) / 2f
        val pierCz = (CompoundAvenue90.PIER_MIN_Z + CompoundAvenue90.PIER_MAX_Z) / 2f
        val pierW = CompoundAvenue90.PIER_MAX_X - CompoundAvenue90.PIER_MIN_X
        val pierD = CompoundAvenue90.PIER_MAX_Z - CompoundAvenue90.PIER_MIN_Z
        mb.addBox(pierCx, 0.16f, pierCz, pierW, 0.14f, pierD, pierWood)
        for (px in listOf(45.2f, 49.5f, 54.8f)) {
            mb.addCylinder(px, -0.95f, pierCz - 1.5f, 0.16f, 1.35f, pierWood, segments = 6)
            mb.addCylinder(px, -0.95f, pierCz + 1.5f, 0.16f, 1.35f, pierWood, segments = 6)
            nightMb.addBox(px, 0.45f, pierCz - 1.5f, 0.22f, 0.22f, 0.22f, warmLampGlow)
            nightMb.addBox(px, 0.45f, pierCz + 1.5f, 0.22f, 0.22f, 0.22f, warmLampGlow)
        }

        // Moored Sleek Sailboat / Compound Yacht inside Compound Lagoon
        val boatX = 31.5f
        val boatZ = 212.0f
        mb.addBox(boatX, 0.35f, boatZ, 3.2f, 0.72f, 8.2f, copingCream)
        mb.addBox(boatX, 0.92f, boatZ - 0.6f, 2.2f, 0.65f, 4.0f, floatArrayOf(0.08f, 0.42f, 0.80f, 1f))
        mb.addCylinder(boatX, 1.15f, boatZ + 1.2f, 0.08f, 6.2f, marbleWhite, segments = 6)
        mb.addBox(boatX + 0.05f, 4.4f, boatZ + 0.1f, 0.08f, 4.8f, 2.5f, floatArrayOf(0.98f, 0.97f, 0.94f, 1f))
        nightMb.addBox(boatX, 7.45f, boatZ + 1.2f, 0.28f, 0.28f, 0.28f, warmLampGlow)

        // Beach Palm Trees & Umbrellas framing Compound Sea
        val compoundPalms = listOf(
            Pair(16.5f, 193f), Pair(16.5f, 215f), Pair(16.5f, 237f),
            Pair(58.5f, 195f), Pair(58.5f, 234f)
        )
        for ((px, pz) in compoundPalms) {
            mb.addCylinder(px, 0.02f, pz, 0.22f, 5.2f, palmTrunk, segments = 8)
            mb.addDome(px, 5.2f, pz, 2.1f, palmLeaf)
            mb.addDome(px, 5.75f, pz, 1.25f, floatArrayOf(0.14f, 0.52f, 0.20f, 1f))
        }
        for (uz in listOf(202f, 224f)) {
            mb.addCylinder(58.6f, 0.02f, uz, 0.06f, 2.4f, copingCream, segments = 6)
            mb.addDome(58.6f, 2.4f, uz, 1.55f, floatArrayOf(0.96f, 0.34f, 0.22f, 1f))
            mb.addBox(58.6f, 0.26f, uz + 1.1f, 0.85f, 0.24f, 1.85f, copingCream)
        }

        // ---------------------------------------------------------------------
        // B) COMPOUND OLYMPIC SWIMMING POOL INSIDE 90 AVENUE COMPOUND (65..83, 197..229)
        // ---------------------------------------------------------------------
        val pMinX = CompoundAvenue90.POOL_MIN_X
        val pMaxX = CompoundAvenue90.POOL_MAX_X
        val pMinZ = CompoundAvenue90.POOL_MIN_Z
        val pMaxZ = CompoundAvenue90.POOL_MAX_Z
        val pCx = (pMinX + pMaxX) / 2f
        val pCz = (pMinZ + pMaxZ) / 2f
        val pW = pMaxX - pMinX
        val pD = pMaxZ - pMinZ

        // 4 Non-overlapping White Marble Apron Strips around Pool Opening
        addGroundBand(pMinX - 2.6f, pMinX, pMinZ - 2.6f, pMaxZ + 2.6f, 0.02f, marbleWhite)
        addGroundBand(pMaxX, pMaxX + 2.6f, pMinZ - 2.6f, pMaxZ + 2.6f, 0.02f, marbleWhite)
        addGroundBand(pMinX, pMaxX, pMinZ - 2.6f, pMinZ, 0.02f, marbleWhite)
        addGroundBand(pMinX, pMaxX, pMaxZ, pMaxZ + 2.6f, 0.02f, marbleWhite)

        // Pool Tiled Floor (-1.06m) & Vertical Azure Mosaic Side Walls
        val tileFloor = floatArrayOf(0.06f, 0.45f, 0.72f, 1f)
        val tileWall = floatArrayOf(0.10f, 0.60f, 0.84f, 1f)
        mb.addBox(pCx, -1.04f, pCz, pW, 0.14f, pD, tileFloor)
        mb.addBox(pCx, -0.45f, pMinZ - 0.15f, pW + 0.4f, 1.15f, 0.28f, tileWall)
        mb.addBox(pCx, -0.45f, pMaxZ + 0.15f, pW + 0.4f, 1.15f, 0.28f, tileWall)
        mb.addBox(pMinX - 0.15f, -0.45f, pCz, 0.28f, 1.15f, pD + 0.4f, tileWall)
        mb.addBox(pMaxX + 0.15f, -0.45f, pCz, 0.28f, 1.15f, pD + 0.4f, tileWall)

        // Olympic Lane Markings on Pool Floor + Submerged Marble Entry Steps on North End
        for (lx in listOf(pCx - 4.5f, pCx, pCx + 4.5f)) {
            mb.addBox(lx, -0.95f, pCz, 0.28f, 0.04f, pD - 3.0f, floatArrayOf(0.04f, 0.22f, 0.48f, 1f))
        }
        mb.addBox(pCx, -0.22f, pMinZ + 0.8f, 7.0f, 0.36f, 1.2f, marbleWhite)
        mb.addBox(pCx, -0.52f, pMinZ + 1.8f, 7.0f, 0.36f, 1.2f, marbleWhite)
        mb.addBox(pCx, -0.82f, pMinZ + 2.8f, 7.0f, 0.36f, 1.2f, marbleWhite)

        // Bright Underwater Pool Lights (Night LED strips along both sides)
        for (lz in listOf(202f, 209f, 217f, 224f)) {
            nightMb.addBox(pMinX + 0.2f, -0.55f, lz, 0.12f, 0.35f, 1.8f, cyanNeon)
            nightMb.addBox(pMaxX - 0.2f, -0.55f, lz, 0.12f, 0.35f, 1.8f, cyanNeon)
        }

        // Translucent Olympic Pool Water (top & deep layers)
        val poolWaterTop = floatArrayOf(0.10f, 0.84f, 0.98f, 0.58f)
        val poolWaterDeep = floatArrayOf(0.05f, 0.62f, 0.90f, 0.30f)
        waterMb.addQuad(
            pMinX, 0.04f, pMaxZ,
            pMaxX, 0.04f, pMaxZ,
            pMaxX, 0.04f, pMinZ,
            pMinX, 0.04f, pMinZ,
            0f, 1f, 0f, poolWaterTop
        )
        waterMb.addQuad(
            pMinX, -0.44f, pMaxZ,
            pMaxX, -0.44f, pMaxZ,
            pMaxX, -0.44f, pMinZ,
            pMinX, -0.44f, pMinZ,
            0f, 1f, 0f, poolWaterDeep
        )

        // Club Cabana Pergola & Loungers along East Pool Deck
        val cabX = 87.2f
        val cabZ = 213.0f
        mb.addBox(cabX, 0.12f, cabZ, 4.2f, 0.24f, 10.0f, marbleWhite)
        for (cz in listOf(cabZ - 4.4f, cabZ + 4.4f)) {
            mb.addCylinder(cabX - 1.6f, 0.24f, cz, 0.14f, 3.2f, limestone, segments = 8)
            mb.addCylinder(cabX + 1.6f, 0.24f, cz, 0.14f, 3.2f, limestone, segments = 8)
        }
        mb.addBox(cabX, 3.55f, cabZ, 4.6f, 0.26f, 10.4f, copingCream)
        mb.addBox(cabX, 3.72f, cabZ, 4.8f, 0.12f, 10.6f, bronzeGold)
        nightMb.addBox(cabX, 3.38f, cabZ, 3.8f, 0.10f, 9.2f, warmLampGlow)
        // Poolside Sun Loungers along West deck edge of Pool (63.4f)
        for (lz in listOf(203f, 209f, 217f, 223f)) {
            mb.addBox(63.6f, 0.24f, lz, 1.85f, 0.22f, 0.85f, glassTint)
        }
    }

    // =========================================================================
    // 2. ELEVATED ELECTRIC SKY METRO SYSTEM («مترو المدينة الكهربائي المعلق»)
    // =========================================================================
    fun buildSkyMetroInfrastructure(mb: MeshBuilder, nightMb: MeshBuilder) {
        val concreteDeck = floatArrayOf(0.76f, 0.78f, 0.82f, 1f)
        val concreteDark = floatArrayOf(0.32f, 0.36f, 0.42f, 1f)
        val steelPillar = floatArrayOf(0.20f, 0.26f, 0.35f, 1f)
        val railSteel = floatArrayOf(0.78f, 0.82f, 0.88f, 1f)
        val railSleepers = floatArrayOf(0.24f, 0.22f, 0.20f, 1f)
        val electricWire = floatArrayOf(0.92f, 0.86f, 0.50f, 1f)
        val cyanStrip = floatArrayOf(0.06f, 0.90f, 1.0f, 1f)
        val goldStrip = floatArrayOf(1.0f, 0.82f, 0.18f, 1f)
        val warmLampGlow = floatArrayOf(1.0f, 0.90f, 0.60f, 1f)

        val deckY = SkyMetroSystem.DECK_SURFACE_Y
        val deckThick = SkyMetroSystem.DECK_THICKNESS
        val deckCenterY = deckY - deckThick / 2f // 10.0f center, top 10.50f
        val wireY = SkyMetroSystem.CATENARY_WIRE_Y

        val minX = SkyMetroSystem.LOOP_MIN_X // -222
        val maxX = SkyMetroSystem.LOOP_MAX_X // 374
        val minZ = SkyMetroSystem.LOOP_MIN_Z // -206
        val maxZ = SkyMetroSystem.LOOP_MAX_Z // 362
        val spanX = maxX - minX + 6.4f
        val spanZ = maxZ - minZ + 6.4f
        val midX = (minX + maxX) / 2f
        val midZ = (minZ + maxZ) / 2f

        // 1. Elevated Viaduct Concrete Deck Beams around ENTIRE outer city perimeter (Y = 9.5..10.5m in AIR!)
        // North (Z=-206) & South (Z=362) girders
        for (z in listOf(minZ, maxZ)) {
            mb.addBox(midX, deckCenterY, z, spanX, deckThick, 6.2f, concreteDeck)
            mb.addBox(midX, deckY + 0.35f, z - 2.95f, spanX, 0.70f, 0.26f, concreteDark)
            mb.addBox(midX, deckY + 0.35f, z + 2.95f, spanX, 0.70f, 0.26f, concreteDark)
            // Illuminated Night Viaduct Cyan Under-Glow & Side Accent
            nightMb.addBox(midX, deckCenterY - 0.1f, z - 3.15f, spanX - 2f, 0.18f, 0.10f, cyanStrip)
            nightMb.addBox(midX, deckCenterY - 0.1f, z + 3.15f, spanX - 2f, 0.18f, 0.10f, cyanStrip)
            // Twin Steel Rails + Sleepers
            mb.addBox(midX, deckY + 0.12f, z, spanX - 2f, 0.08f, 2.2f, railSleepers)
            mb.addBox(midX, SkyMetroSystem.TRACK_RAIL_Y, z - 0.75f, spanX - 2f, 0.18f, 0.14f, railSteel)
            mb.addBox(midX, SkyMetroSystem.TRACK_RAIL_Y, z + 0.75f, spanX - 2f, 0.18f, 0.14f, railSteel)
            // Overhead Electric Contact Wire (Y = 15.2m)
            mb.addBox(midX, wireY, z, spanX - 1f, 0.10f, 0.10f, electricWire)
        }

        // West (X=-222) & East (X=374) girders
        for (x in listOf(minX, maxX)) {
            mb.addBox(x, deckCenterY, midZ, 6.2f, deckThick, spanZ, concreteDeck)
            mb.addBox(x - 2.95f, deckY + 0.35f, midZ, 0.26f, 0.70f, spanZ, concreteDark)
            mb.addBox(x + 2.95f, deckY + 0.35f, midZ, 0.26f, 0.70f, spanZ, concreteDark)
            nightMb.addBox(x - 3.15f, deckCenterY - 0.1f, midZ, 0.10f, 0.18f, spanZ - 2f, cyanStrip)
            nightMb.addBox(x + 3.15f, deckCenterY - 0.1f, midZ, 0.10f, 0.18f, spanZ - 2f, cyanStrip)
            // Twin Steel Rails + Sleepers
            mb.addBox(x, deckY + 0.12f, midZ, 2.2f, 0.08f, spanZ - 2f, railSleepers)
            mb.addBox(x - 0.75f, SkyMetroSystem.TRACK_RAIL_Y, midZ, 0.14f, 0.18f, spanZ - 2f, railSteel)
            mb.addBox(x + 0.75f, SkyMetroSystem.TRACK_RAIL_Y, midZ, 0.14f, 0.18f, spanZ - 2f, railSteel)
            // Overhead Electric Contact Wire (Y = 15.2m)
            mb.addBox(x, wireY, midZ, 0.10f, 0.10f, spanZ - 1f, electricWire)
        }

        // 2. Straddle Portal Piers in open outer green meadow (+-4.8m from track, zero houses or streets touched!)
        for (x in -200..355 step 45) {
            if (kotlin.math.abs(x - 0) < 12 || kotlin.math.abs(x - 40) < 22 || kotlin.math.abs(x - 140) < 22) continue
            for (z in listOf(minZ, maxZ)) {
                mb.addBox(x.toFloat(), 4.65f, z - 4.8f, 0.95f, 9.3f, 0.95f, steelPillar)
                mb.addBox(x.toFloat(), 4.65f, z + 4.8f, 0.95f, 9.3f, 0.95f, steelPillar)
                mb.addBox(x.toFloat(), 9.20f, z, 1.15f, 0.80f, 10.6f, steelPillar)
                // Catenary mast arch rising above deck up to Y = 15.6m to hold live wire
                mb.addBox(x.toFloat(), 13.0f, z - 2.95f, 0.26f, 5.2f, 0.26f, steelPillar)
                mb.addBox(x.toFloat(), 15.50f, z - 1.35f, 0.26f, 0.22f, 3.4f, steelPillar)
                mb.addCylinder(x.toFloat(), wireY, z, 0.10f, 0.32f, cyanStrip, segments = 6)
            }
        }
        for (z in -185..345 step 45) {
            if (kotlin.math.abs(z - 0) < 12 || kotlin.math.abs(z - 40) < 22 || kotlin.math.abs(z - 65) < 22) continue
            for (x in listOf(minX, maxX)) {
                mb.addBox(x - 4.8f, 4.65f, z.toFloat(), 0.95f, 9.3f, 0.95f, steelPillar)
                mb.addBox(x + 4.8f, 4.65f, z.toFloat(), 0.95f, 9.3f, 0.95f, steelPillar)
                mb.addBox(x, 9.20f, z.toFloat(), 10.6f, 0.80f, 1.15f, steelPillar)
                mb.addBox(x - 2.95f, 13.0f, z.toFloat(), 0.26f, 5.2f, 0.26f, steelPillar)
                mb.addBox(x - 1.35f, 15.50f, z.toFloat(), 3.4f, 0.22f, 0.26f, steelPillar)
                mb.addCylinder(x, wireY, z.toFloat(), 0.10f, 0.32f, cyanStrip, segments = 6)
            }
        }
        // Corner anchor portal columns
        for (cx in listOf(minX - 4.8f, maxX + 4.8f)) {
            for (cz in listOf(minZ - 4.8f, maxZ + 4.8f)) {
                mb.addBox(cx, 4.65f, cz, 1.15f, 9.3f, 1.15f, steelPillar)
            }
        }

        // 3. Elevated Metro Stations, 26m Station Towers, 28-Step Walkable Stairs & 5-EGP Turnstiles
        for (st in SkyMetroSystem.STATIONS) {
            buildSingleMetroStation(mb, nightMb, st, concreteDeck, steelPillar, cyanStrip, goldStrip, warmLampGlow)
        }
    }

    private fun buildSingleMetroStation(
        mb: MeshBuilder,
        nightMb: MeshBuilder,
        st: MetroStation,
        concreteDeck: FloatArray,
        steelPillar: FloatArray,
        cyanStrip: FloatArray,
        goldStrip: FloatArray,
        warmLampGlow: FloatArray
    ) {
        val deckY = SkyMetroSystem.DECK_SURFACE_Y
        val glassRail = floatArrayOf(0.45f, 0.82f, 0.96f, 1f)
        val marbleStep = floatArrayOf(0.88f, 0.90f, 0.94f, 1f)
        val handrailChrome = floatArrayOf(0.84f, 0.88f, 0.92f, 1f)
        val boothNavy = floatArrayOf(0.08f, 0.15f, 0.30f, 1f)
        val towerStone = floatArrayOf(0.84f, 0.87f, 0.92f, 1f)
        val benchWood = floatArrayOf(0.58f, 0.36f, 0.18f, 1f)

        val pcx = (st.platMinX + st.platMaxX) / 2f
        val pcz = (st.platMinZ + st.platMaxZ) / 2f
        val pw = st.platMaxX - st.platMinX
        val pd = st.platMaxZ - st.platMinZ

        // 0. Open Spacious Ground Meadow Plaza around Station Tower & Stairs
        mb.addBox(pcx, 0.03f, pcz, pw + 12f, 0.06f, pd + 12f, floatArrayOf(0.74f, 0.77f, 0.80f, 1f))
        mb.addBox(st.groundEntryX, 0.04f, st.groundEntryZ, 9.5f, 0.08f, 9.5f, marbleStep)

        // 0.5 Iconic 26m Metro Station Tower Landmark rising boldly above every station!
        mb.addBox(st.towerX, 11.0f, st.towerZ, 3.6f, 22.0f, 3.6f, boothNavy)
        mb.addBox(st.towerX, 11.0f, st.towerZ, 3.75f, 20.5f, 2.2f, towerStone)
        mb.addBox(st.towerX, 11.0f, st.towerZ, 2.2f, 20.5f, 3.75f, towerStone)
        // Illuminated Clock / Sky Metro Ring & Spire Crown
        mb.addBox(st.towerX, 22.4f, st.towerZ, 4.4f, 0.9f, 4.4f, steelPillar)
        mb.addCylinder(st.towerX, 22.8f, st.towerZ, 0.24f, 4.8f, handrailChrome, segments = 8)
        mb.addDome(st.towerX, 27.6f, st.towerZ, 0.75f, goldStrip, slices = 10, stacks = 4)
        nightMb.addBox(st.towerX, 20.2f, st.towerZ, 4.0f, 1.8f, 4.0f, cyanStrip)
        nightMb.addBox(st.towerX, 12.5f, st.towerZ, 3.9f, 12.0f, 0.45f, goldStrip)
        nightMb.addBox(st.towerX, 12.5f, st.towerZ, 0.45f, 12.0f, 3.9f, goldStrip)
        nightMb.addDome(st.towerX, 27.6f, st.towerZ, 0.82f, cyanStrip, slices = 8, stacks = 3)

        // Elevated Station Platform Deck Slab (Top strictly at DECK_SURFACE_Y = 10.50f)
        mb.addBox(pcx, deckY - 0.35f, pcz, pw, 0.70f, pd, concreteDeck)
        // Tactile Golden Safety Edge strip along track boarding face
        when (st.id) {
            "station_north" -> nightMb.addBox(pcx, deckY + 0.02f, st.platMaxZ - 0.45f, pw - 0.6f, 0.04f, 0.45f, goldStrip)
            "station_east" -> nightMb.addBox(st.platMinX + 0.45f, deckY + 0.02f, pcz, 0.45f, 0.04f, pd - 0.6f, goldStrip)
            "station_south" -> nightMb.addBox(pcx, deckY + 0.02f, st.platMinZ + 0.45f, pw - 0.6f, 0.04f, 0.45f, goldStrip)
            "station_west" -> nightMb.addBox(st.platMaxX - 0.45f, deckY + 0.02f, pcz, 0.45f, 0.04f, pd - 0.6f, goldStrip)
        }

        // Support Pillars under Platform
        for (px in listOf(st.platMinX + 1.2f, st.platMaxX - 1.2f)) {
            for (pz in listOf(st.platMinZ + 1.2f, st.platMaxZ - 1.2f)) {
                mb.addCylinder(px, 0f, pz, 0.38f, deckY - 0.6f, steelPillar, segments = 8)
            }
        }

        // Glass Canopy Roof & Structural Posts (Y = 10.5f -> 14.1f)
        for (px in listOf(st.platMinX + 1.0f, st.platMaxX - 1.0f)) {
            for (pz in listOf(st.platMinZ + 1.0f, st.platMaxZ - 1.0f)) {
                mb.addCylinder(px, deckY, pz, 0.14f, 3.6f, steelPillar, segments = 6)
            }
        }
        mb.addBox(pcx, deckY + 3.72f, pcz, pw + 1.2f, 0.32f, pd + 1.2f, boothNavy)
        mb.addBox(pcx, deckY + 3.92f, pcz, pw + 0.8f, 0.16f, pd + 0.8f, cyanStrip)
        nightMb.addBox(pcx, deckY + 3.50f, pcz, pw * 0.75f, 0.12f, pd * 0.45f, warmLampGlow)

        // Waiting Benches on Elevated Platform
        mb.addBox(pcx - (if (pw > pd) 2.5f else 0f), deckY + 0.28f, pcz - (if (pd > pw) 2.5f else 0f), 2.2f, 0.12f, 0.8f, benchWood)

        // Upper Platform Safety Balustrades (matching staticColliders gaps!)
        when (st.id) {
            "station_north" -> {
                mb.addBox(pcx, deckY + 0.65f, st.platMinZ + 0.20f, pw, 1.3f, 0.16f, glassRail)
                mb.addBox(st.platMinX + 0.20f, deckY + 0.65f, pcz, 0.16f, 1.3f, pd, glassRail)
                mb.addBox(st.platMaxX - 0.20f, deckY + 0.65f, (st.platMinZ + st.stairMinZ) / 2f, 0.16f, 1.3f, (st.stairMinZ - st.platMinZ).coerceAtLeast(0.2f), glassRail)
                mb.addBox(st.platMaxX - 0.20f, deckY + 0.65f, (st.stairMaxZ + st.platMaxZ) / 2f, 0.16f, 1.3f, (st.platMaxZ - st.stairMaxZ).coerceAtLeast(0.2f), glassRail)
                nightMb.addBox(pcx, deckY + 3.45f, st.platMaxZ + 0.35f, 8.5f, 0.72f, 0.14f, goldStrip)
            }
            "station_east" -> {
                mb.addBox(pcx, deckY + 0.65f, st.platMinZ + 0.20f, pw, 1.3f, 0.16f, glassRail)
                mb.addBox(pcx, deckY + 0.65f, st.platMaxZ - 0.20f, pw, 1.3f, 0.16f, glassRail)
                mb.addBox(st.platMaxX - 0.20f, deckY + 0.65f, (st.platMinZ + st.stairMinZ) / 2f, 0.16f, 1.3f, (st.stairMinZ - st.platMinZ).coerceAtLeast(0.2f), glassRail)
                mb.addBox(st.platMaxX - 0.20f, deckY + 0.65f, (st.stairMaxZ + st.platMaxZ) / 2f, 0.16f, 1.3f, (st.platMaxZ - st.stairMaxZ).coerceAtLeast(0.2f), glassRail)
                nightMb.addBox(st.platMinX - 0.35f, deckY + 3.45f, pcz, 0.14f, 0.72f, 8.5f, goldStrip)
            }
            "station_south" -> {
                mb.addBox(st.platMinX + 0.20f, deckY + 0.65f, pcz, 0.16f, 1.3f, pd, glassRail)
                mb.addBox(st.platMaxX - 0.20f, deckY + 0.65f, pcz, 0.16f, 1.3f, pd, glassRail)
                mb.addBox((st.platMinX + st.stairMinX) / 2f, deckY + 0.65f, st.platMaxZ - 0.20f, (st.stairMinX - st.platMinX).coerceAtLeast(0.2f), 1.3f, 0.16f, glassRail)
                mb.addBox((st.stairMaxX + st.platMaxX) / 2f, deckY + 0.65f, st.platMaxZ - 0.20f, (st.platMaxX - st.stairMaxX).coerceAtLeast(0.2f), 1.3f, 0.16f, glassRail)
                nightMb.addBox(pcx, deckY + 3.45f, st.platMinZ - 0.35f, 8.5f, 0.72f, 0.14f, goldStrip)
            }
            "station_west" -> {
                mb.addBox(pcx, deckY + 0.65f, st.platMinZ + 0.20f, pw, 1.3f, 0.16f, glassRail)
                mb.addBox(pcx, deckY + 0.65f, st.platMaxZ - 0.20f, pw, 1.3f, 0.16f, glassRail)
                mb.addBox(st.platMinX + 0.20f, deckY + 0.65f, (st.platMinZ + st.stairMinZ) / 2f, 0.16f, 1.3f, (st.stairMinZ - st.platMinZ).coerceAtLeast(0.2f), glassRail)
                mb.addBox(st.platMinX + 0.20f, deckY + 0.65f, (st.stairMaxZ + st.platMaxZ) / 2f, 0.16f, 1.3f, (st.platMaxZ - st.stairMaxZ).coerceAtLeast(0.2f), glassRail)
                nightMb.addBox(st.platMaxX + 0.35f, deckY + 3.45f, pcz, 0.14f, 0.72f, 8.5f, goldStrip)
            }
        }

        // -----------------------------------------------------------------
        // 28-Step Walkable 3D Staircase (Y: 0.0f -> 10.50f) with Handrails & Step LEDs
        // -----------------------------------------------------------------
        val stepCount = 28
        val stepRise = deckY / stepCount
        val scx = (st.stairMinX + st.stairMaxX) / 2f
        val scz = (st.stairMinZ + st.stairMaxZ) / 2f
        val sw = st.stairMaxX - st.stairMinX
        val sd = st.stairMaxZ - st.stairMinZ

        when (st.stairDirection) {
            StairDirection.CLIMB_WEST -> {
                val runLen = sw / stepCount
                for (i in 0 until stepCount) {
                    val sx = st.stairMaxX - (i + 0.5f) * runLen
                    val topY = (i + 1) * stepRise
                    mb.addBox(sx, topY / 2f, scz, runLen * 1.15f, topY, sd, marbleStep)
                    if (i % 2 == 0) {
                        nightMb.addBox(sx, topY + 0.02f, scz, 0.10f, 0.04f, sd * 0.85f, goldStrip)
                    }
                    mb.addCylinder(sx, topY, st.stairMinZ - 0.18f, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addCylinder(sx, topY, st.stairMaxZ + 0.18f, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addBox(sx, topY + 1.10f, st.stairMinZ - 0.18f, runLen * 1.35f, 0.12f, 0.12f, steelPillar)
                    mb.addBox(sx, topY + 1.10f, st.stairMaxZ + 0.18f, runLen * 1.35f, 0.12f, 0.12f, steelPillar)
                }
            }
            StairDirection.CLIMB_EAST -> {
                val runLen = sw / stepCount
                for (i in 0 until stepCount) {
                    val sx = st.stairMinX + (i + 0.5f) * runLen
                    val topY = (i + 1) * stepRise
                    mb.addBox(sx, topY / 2f, scz, runLen * 1.15f, topY, sd, marbleStep)
                    if (i % 2 == 0) {
                        nightMb.addBox(sx, topY + 0.02f, scz, 0.10f, 0.04f, sd * 0.85f, goldStrip)
                    }
                    mb.addCylinder(sx, topY, st.stairMinZ - 0.18f, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addCylinder(sx, topY, st.stairMaxZ + 0.18f, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addBox(sx, topY + 1.10f, st.stairMinZ - 0.18f, runLen * 1.35f, 0.12f, 0.12f, steelPillar)
                    mb.addBox(sx, topY + 1.10f, st.stairMaxZ + 0.18f, runLen * 1.35f, 0.12f, 0.12f, steelPillar)
                }
            }
            StairDirection.CLIMB_SOUTH -> {
                val runLen = sd / stepCount
                for (i in 0 until stepCount) {
                    val sz = st.stairMinZ + (i + 0.5f) * runLen
                    val topY = (i + 1) * stepRise
                    mb.addBox(scx, topY / 2f, sz, sw, topY, runLen * 1.15f, marbleStep)
                    if (i % 2 == 0) {
                        nightMb.addBox(scx, topY + 0.02f, sz, sw * 0.85f, 0.04f, 0.10f, goldStrip)
                    }
                    mb.addCylinder(st.stairMinX - 0.18f, topY, sz, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addCylinder(st.stairMaxX + 0.18f, topY, sz, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addBox(st.stairMinX - 0.18f, topY + 1.10f, sz, 0.12f, 0.12f, runLen * 1.35f, steelPillar)
                    mb.addBox(st.stairMaxX + 0.18f, topY + 1.10f, sz, 0.12f, 0.12f, runLen * 1.35f, steelPillar)
                }
            }
            StairDirection.CLIMB_NORTH -> {
                val runLen = sd / stepCount
                for (i in 0 until stepCount) {
                    val sz = st.stairMaxZ - (i + 0.5f) * runLen
                    val topY = (i + 1) * stepRise
                    mb.addBox(scx, topY / 2f, sz, sw, topY, runLen * 1.15f, marbleStep)
                    if (i % 2 == 0) {
                        nightMb.addBox(scx, topY + 0.02f, sz, sw * 0.85f, 0.04f, 0.10f, goldStrip)
                    }
                    mb.addCylinder(st.stairMinX - 0.18f, topY, sz, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addCylinder(st.stairMaxX + 0.18f, topY, sz, 0.05f, 1.1f, handrailChrome, segments = 6)
                    mb.addBox(st.stairMinX - 0.18f, topY + 1.10f, sz, 0.12f, 0.12f, runLen * 1.35f, steelPillar)
                    mb.addBox(st.stairMaxX + 0.18f, topY + 1.10f, sz, 0.12f, 0.12f, runLen * 1.35f, steelPillar)
                }
            }
        }

        // -----------------------------------------------------------------
        // Ground Ticket Kiosk Booth & Turnstile Frame («تذاكر 5 جنيه»)
        // -----------------------------------------------------------------
        mb.addBox(st.boothX, 1.65f, st.boothZ, 2.5f, 3.3f, 2.3f, boothNavy)
        mb.addBox(st.boothX, 3.45f, st.boothZ, 3.1f, 0.35f, 2.8f, cyanStrip)
        mb.addBox(st.boothX, 1.75f, st.boothZ, 2.60f, 1.25f, 2.40f, glassRail)
        nightMb.addBox(st.boothX, 3.75f, st.boothZ, 2.2f, 0.55f, 1.8f, goldStrip)
        nightMb.addBox(st.boothX, 1.75f, st.boothZ, 2.1f, 0.85f, 2.1f, warmLampGlow)

        val pillarOff = 2.0f
        if (st.stairDirection == StairDirection.CLIMB_SOUTH || st.stairDirection == StairDirection.CLIMB_NORTH) {
            mb.addBox(st.turnstileX - pillarOff, 0.70f, st.turnstileZ, 0.45f, 1.40f, 0.90f, steelPillar)
            mb.addBox(st.turnstileX + pillarOff, 0.70f, st.turnstileZ, 0.45f, 1.40f, 0.90f, steelPillar)
            mb.addBox(st.turnstileX, 3.40f, st.turnstileZ, 4.7f, 0.65f, 0.70f, boothNavy)
            nightMb.addBox(st.turnstileX, 3.40f, st.turnstileZ, 3.8f, 0.36f, 0.82f, cyanStrip)
        } else {
            mb.addBox(st.turnstileX, 0.70f, st.turnstileZ - pillarOff, 0.90f, 1.40f, 0.45f, steelPillar)
            mb.addBox(st.turnstileX, 0.70f, st.turnstileZ + pillarOff, 0.90f, 1.40f, 0.45f, steelPillar)
            mb.addBox(st.turnstileX, 3.40f, st.turnstileZ, 0.70f, 0.65f, 4.7f, boothNavy)
            nightMb.addBox(st.turnstileX, 3.40f, st.turnstileZ, 0.82f, 0.36f, 3.8f, cyanStrip)
        }
    }

    // =========================================================================
    // 3. DYNAMIC REUSABLE 3-CAR ELECTRIC METRO MESHES & PANTOGRAPHS
    // =========================================================================
    fun buildMetroCarMeshes(): MetroCarVisualMeshes {
        val pearlBody = floatArrayOf(0.95f, 0.96f, 0.98f, 1f)
        val navyTrim = floatArrayOf(0.08f, 0.16f, 0.32f, 1f)
        val steelDark = floatArrayOf(0.16f, 0.18f, 0.22f, 1f)
        val chromeWheel = floatArrayOf(0.75f, 0.80f, 0.86f, 1f)
        val cabinSeat = floatArrayOf(0.14f, 0.46f, 0.82f, 1f)
        val glassFront = floatArrayOf(0.12f, 0.22f, 0.34f, 1f)
        val windowGlass = floatArrayOf(0.28f, 0.66f, 0.88f, 1f)
        val cyanGlow = floatArrayOf(0.08f, 0.94f, 1.0f, 1f)
        val warmCabinGlow = floatArrayOf(1.0f, 0.94f, 0.68f, 1f)
        val headlightWhite = floatArrayOf(1.0f, 0.98f, 0.86f, 1f)

        fun buildSharedCarStructure(mb: MeshBuilder, em: MeshBuilder, isLeadNose: Boolean) {
            // Bogies + Steel Wheels resting cleanly on rails (rails at y = 0.25 relative to DECK_SURFACE_Y)
            for (bz in listOf(-2.9f, 2.9f)) {
                mb.addBox(0f, 0.45f, bz, 1.9f, 0.36f, 1.7f, steelDark)
                for (wx in listOf(-0.82f, 0.82f)) {
                    for (wz in listOf(bz - 0.55f, bz + 0.55f)) {
                        mb.addBox(wx, 0.42f, wz, 0.16f, 0.52f, 0.52f, chromeWheel)
                    }
                }
            }

            // Interior Cabin Floor
            mb.addBox(0f, 0.84f, 0f, 2.90f, 0.38f, 9.2f, navyTrim)
            mb.addBox(0f, 1.04f, -0.1f, 2.66f, 0.04f, 8.8f, floatArrayOf(0.24f, 0.28f, 0.36f, 1f))

            // 6 Detailed Sculpted 3D Passenger Seats inside the metro cabin (matching SkyMetroSystem.CAR_SEATS!)
            val seatChairBlue = floatArrayOf(0.12f, 0.48f, 0.90f, 1f)
            val seatArmGrey = floatArrayOf(0.32f, 0.36f, 0.44f, 1f)
            for (seat in SkyMetroSystem.CAR_SEATS) {
                val sx = seat.localX
                val sz = seat.localZ
                val inward = if (sx < 0f) 1f else -1f
                // Seat pedestal leg + seat cushion (top surface ~y=1.34f)
                mb.addBox(sx, 1.15f, sz, 0.34f, 0.22f, 0.42f, steelDark)
                mb.addBox(sx + inward * 0.04f, 1.29f, sz, 0.54f, 0.12f, 0.68f, seatChairBlue)
                // Tall sculpted backrest against window wall + cyan LED headrest accent
                mb.addBox(sx - inward * 0.20f, 1.72f, sz, 0.14f, 0.82f, 0.64f, seatChairBlue)
                mb.addBox(sx - inward * 0.19f, 2.15f, sz, 0.16f, 0.12f, 0.48f, cyanGlow)
                em.addBox(sx - inward * 0.18f, 2.16f, sz, 0.12f, 0.08f, 0.40f, cyanGlow)
                // Left & right armrests
                mb.addBox(sx + inward * 0.02f, 1.46f, sz - 0.34f, 0.44f, 0.08f, 0.08f, seatArmGrey)
                mb.addBox(sx + inward * 0.02f, 1.46f, sz + 0.34f, 0.44f, 0.08f, 0.08f, seatArmGrey)
            }

            // Side Wall Pillars & Upper Header Rails (panoramic clear bays so interior chairs & seated player are 100% visible!)
            for (pz in listOf(-4.2f, -2.0f, 0f, 2.0f, 4.1f)) {
                mb.addBox(-1.40f, 2.15f, pz, 0.18f, 2.25f, 0.34f, pearlBody)
                mb.addBox(1.40f, 2.15f, pz, 0.18f, 2.25f, 0.34f, pearlBody)
            }
            // Lower waist skirts & upper roof headers leaving large panoramic window openings
            mb.addBox(-1.42f, 1.32f, 0f, 0.12f, 0.62f, 9.1f, navyTrim)
            mb.addBox(1.42f, 1.32f, 0f, 0.12f, 0.62f, 9.1f, navyTrim)
            mb.addBox(-1.40f, 2.98f, -0.1f, 0.16f, 0.28f, 9.0f, pearlBody)
            mb.addBox(1.40f, 2.98f, -0.1f, 0.16f, 0.28f, 9.0f, pearlBody)
            // Exterior glowing cyan waist stripe + warm interior ceiling strip light!
            em.addBox(-1.49f, 1.56f, -0.1f, 0.06f, 0.14f, 8.8f, cyanGlow)
            em.addBox(1.49f, 1.56f, -0.1f, 0.06f, 0.14f, 8.8f, cyanGlow)
            em.addBox(0f, 3.09f, -0.1f, 0.48f, 0.06f, 8.0f, warmCabinGlow)

            // Aerodynamic Roof Shell + Rooftop AC Modules (y = 3.25..3.65)
            mb.addBox(0f, 3.28f, -0.1f, 2.86f, 0.34f, 9.1f, pearlBody)
            mb.addBox(0f, 3.55f, -1.2f, 1.75f, 0.28f, 2.8f, chromeWheel)

            // Electric Pantograph on Lead/Rear Cars touching overhead 15.2m wire (DECK=10.5 -> +4.7m high!)
            if (isLeadNose) {
                mb.addBox(0f, 3.55f, 1.4f, 1.4f, 0.18f, 1.6f, steelDark)
                // Lower & Upper Pantograph V-arms + overhead Collector Shoe
                mb.addBox(-0.32f, 4.02f, 1.1f, 0.08f, 0.90f, 0.08f, chromeWheel)
                mb.addBox(0.32f, 4.02f, 1.1f, 0.08f, 0.90f, 0.08f, chromeWheel)
                mb.addBox(-0.32f, 4.42f, 1.4f, 0.08f, 0.72f, 0.08f, chromeWheel)
                mb.addBox(0.32f, 4.42f, 1.4f, 0.08f, 0.72f, 0.08f, chromeWheel)
                mb.addBox(0f, 4.68f, 1.65f, 1.35f, 0.09f, 0.24f, cyanGlow)
                em.addBox(0f, 4.70f, 1.65f, 0.55f, 0.10f, 0.12f, cyanGlow) // Electric arc glow contact!

                // Sloped Aerodynamic Nose Front (+Z) & Windshield + Dual Headlights
                mb.addBox(0f, 1.50f, 4.75f, 2.74f, 1.45f, 0.75f, navyTrim)
                mb.addBox(0f, 2.55f, 4.52f, 2.58f, 1.15f, 0.55f, glassFront)
                mb.addBox(0f, 3.18f, 4.40f, 2.50f, 0.36f, 0.70f, pearlBody)
                em.addBox(-0.88f, 1.32f, 5.15f, 0.52f, 0.28f, 0.10f, headlightWhite)
                em.addBox(0.88f, 1.32f, 5.15f, 0.52f, 0.28f, 0.10f, headlightWhite)
                em.addBox(0f, 3.16f, 4.78f, 1.65f, 0.24f, 0.08f, cyanGlow) // Express Line destination LED
                // Rear articulation diaphragm gangway (-Z)
                mb.addBox(0f, 2.05f, -4.65f, 2.25f, 2.25f, 0.45f, steelDark)
            } else {
                // Articulation Gangway Bellows on both ends of Middle Coach
                mb.addBox(0f, 2.05f, -4.65f, 2.25f, 2.25f, 0.45f, steelDark)
                mb.addBox(0f, 2.05f, 4.65f, 2.25f, 2.25f, 0.45f, steelDark)
            }
        }

        val leadMb = MeshBuilder(1024)
        val leadEm = MeshBuilder(512)
        buildSharedCarStructure(leadMb, leadEm, isLeadNose = true)

        val coachMb = MeshBuilder(1024)
        val coachEm = MeshBuilder(512)
        buildSharedCarStructure(coachMb, coachEm, isLeadNose = false)

        return MetroCarVisualMeshes(
            leadBodyBuffer = leadMb.build(),
            leadBodyCount = leadMb.getVertexCount(),
            leadEmissiveBuffer = leadEm.build(),
            leadEmissiveCount = leadEm.getVertexCount(),
            coachBodyBuffer = coachMb.build(),
            coachBodyCount = coachMb.getVertexCount(),
            coachEmissiveBuffer = coachEm.build(),
            coachEmissiveCount = coachEm.getVertexCount()
        )
    }

    /**
     * Reusable horizontal turnstile barrier gate bar centered around local (0,0,0) pointing along +X (width 3.6m).
     */
    fun buildTurnstileBarrierBar(isUnlocked: Boolean): Pair<FloatBuffer, Int> {
        val mb = MeshBuilder(64)
        val color = if (isUnlocked) {
            floatArrayOf(0.10f, 0.92f, 0.45f, 1f) // Bright Green Opened
        } else {
            floatArrayOf(0.98f, 0.18f, 0.22f, 1f) // Red Locked (5 EGP Required!)
        }
        val silver = floatArrayOf(0.88f, 0.90f, 0.94f, 1f)
        if (isUnlocked) {
            // Swung vertical open beside pillar
            mb.addBox(-1.75f, 1.95f, 0f, 0.16f, 2.2f, 0.16f, color)
        } else {
            // Horizontal closed across the stairs entrance
            mb.addBox(0f, 1.05f, 0f, 3.6f, 0.18f, 0.18f, color)
            mb.addBox(0f, 1.05f, 0f, 0.65f, 0.22f, 0.22f, silver)
        }
        return Pair(mb.build(), mb.getVertexCount())
    }
}
