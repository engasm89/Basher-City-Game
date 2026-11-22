package com.example.opencity.engine

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural 3D mesh builder that packs positions (3), normals (3), and RGBA colors (4) into a FloatBuffer.
 */
class MeshBuilder(initialCapacityVertices: Int = 4096) {

    // 10 floats per vertex: x, y, z, nx, ny, nz, r, g, b, a
    private var data = FloatArray(initialCapacityVertices * 10)
    private var vertexCount = 0

    private fun ensureCapacity(additionalVertices: Int) {
        val needed = (vertexCount + additionalVertices) * 10
        if (needed > data.size) {
            var newCap = data.size * 2
            if (newCap < needed) newCap = needed
            val newData = FloatArray(newCap)
            System.arraycopy(data, 0, newData, 0, vertexCount * 10)
            data = newData
        }
    }

    fun addVertex(
        x: Float, y: Float, z: Float,
        nx: Float, ny: Float, nz: Float,
        r: Float, g: Float, b: Float, a: Float = 1.0f
    ) {
        ensureCapacity(1)
        val offset = vertexCount * 10
        data[offset] = x
        data[offset + 1] = y
        data[offset + 2] = z
        data[offset + 3] = nx
        data[offset + 4] = ny
        data[offset + 5] = nz
        data[offset + 6] = r
        data[offset + 7] = g
        data[offset + 8] = b
        data[offset + 9] = a
        vertexCount++
    }

    fun addTriangle(
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        nx: Float, ny: Float, nz: Float,
        color: FloatArray
    ) {
        val r = color[0]
        val g = color[1]
        val b = color[2]
        val a = if (color.size > 3) color[3] else 1f

        addVertex(x1, y1, z1, nx, ny, nz, r, g, b, a)
        addVertex(x2, y2, z2, nx, ny, nz, r, g, b, a)
        addVertex(x3, y3, z3, nx, ny, nz, r, g, b, a)
    }

    fun addQuad(
        x1: Float, y1: Float, z1: Float,
        x2: Float, y2: Float, z2: Float,
        x3: Float, y3: Float, z3: Float,
        x4: Float, y4: Float, z4: Float,
        nx: Float, ny: Float, nz: Float,
        color: FloatArray
    ) {
        // Two triangles: (1, 2, 3) and (1, 3, 4)
        addTriangle(x1, y1, z1, x2, y2, z2, x3, y3, z3, nx, ny, nz, color)
        addTriangle(x1, y1, z1, x3, y3, z3, x4, y4, z4, nx, ny, nz, color)
    }

    /**
     * Adds an axis-aligned box centered at (cx, cy, cz) with dimensions (w, h, d).
     */
    fun addBox(
        cx: Float, cy: Float, cz: Float,
        w: Float, h: Float, d: Float,
        color: FloatArray,
        topColor: FloatArray? = null
    ) {
        val hw = w / 2f
        val hh = h / 2f
        val hd = d / 2f

        val actualTopColor = topColor ?: color

        // Front face (+Z)
        addQuad(
            cx - hw, cy - hh, cz + hd,
            cx + hw, cy - hh, cz + hd,
            cx + hw, cy + hh, cz + hd,
            cx - hw, cy + hh, cz + hd,
            0f, 0f, 1f, color
        )
        // Back face (-Z)
        addQuad(
            cx + hw, cy - hh, cz - hd,
            cx - hw, cy - hh, cz - hd,
            cx - hw, cy + hh, cz - hd,
            cx + hw, cy + hh, cz - hd,
            0f, 0f, -1f, color
        )
        // Left face (-X)
        addQuad(
            cx - hw, cy - hh, cz - hd,
            cx - hw, cy - hh, cz + hd,
            cx - hw, cy + hh, cz + hd,
            cx - hw, cy + hh, cz - hd,
            -1f, 0f, 0f, color
        )
        // Right face (+X)
        addQuad(
            cx + hw, cy - hh, cz + hd,
            cx + hw, cy - hh, cz - hd,
            cx + hw, cy + hh, cz - hd,
            cx + hw, cy + hh, cz + hd,
            1f, 0f, 0f, color
        )
        // Top face (+Y)
        addQuad(
            cx - hw, cy + hh, cz + hd,
            cx + hw, cy + hh, cz + hd,
            cx + hw, cy + hh, cz - hd,
            cx - hw, cy + hh, cz - hd,
            0f, 1f, 0f, actualTopColor
        )
        // Bottom face (-Y)
        addQuad(
            cx - hw, cy - hh, cz - hd,
            cx + hw, cy - hh, cz - hd,
            cx + hw, cy - hh, cz + hd,
            cx - hw, cy - hh, cz + hd,
            0f, -1f, 0f, color
        )
    }

    /**
     * Adds a peaked roof prism along Z axis.
     */
    fun addPeakedRoof(
        cx: Float, cy: Float, cz: Float,
        w: Float, h: Float, d: Float,
        color: FloatArray
    ) {
        val hw = w / 2f
        val hd = d / 2f
        val topY = cy + h

        // Front triangle (+Z)
        addTriangle(
            cx - hw, cy, cz + hd,
            cx + hw, cy, cz + hd,
            cx, topY, cz + hd,
            0f, 0.4f, 0.9f, color
        )
        // Back triangle (-Z)
        addTriangle(
            cx + hw, cy, cz - hd,
            cx - hw, cy, cz - hd,
            cx, topY, cz - hd,
            0f, 0.4f, -0.9f, color
        )
        // Left slope
        addQuad(
            cx - hw, cy, cz - hd,
            cx - hw, cy, cz + hd,
            cx, topY, cz + hd,
            cx, topY, cz - hd,
            -0.7f, 0.7f, 0f, color
        )
        // Right slope
        addQuad(
            cx + hw, cy, cz + hd,
            cx + hw, cy, cz - hd,
            cx, topY, cz - hd,
            cx, topY, cz + hd,
            0.7f, 0.7f, 0f, color
        )
    }

    /**
     * Adds a cylinder/pillar/minaret.
     */
    fun addCylinder(
        cx: Float, cy: Float, cz: Float,
        radius: Float, height: Float,
        color: FloatArray,
        segments: Int = 10
    ) {
        val yBottom = cy
        val yTop = cy + height

        for (i in 0 until segments) {
            val a1 = (i.toFloat() / segments) * Math.PI.toFloat() * 2f
            val a2 = ((i + 1).toFloat() / segments) * Math.PI.toFloat() * 2f

            val x1 = cx + cos(a1) * radius
            val z1 = cz + sin(a1) * radius
            val x2 = cx + cos(a2) * radius
            val z2 = cz + sin(a2) * radius

            val nx = cos((a1 + a2) / 2f)
            val nz = sin((a1 + a2) / 2f)

            // Side wall quad
            addQuad(
                x1, yBottom, z1,
                x2, yBottom, z2,
                x2, yTop, z2,
                x1, yTop, z1,
                nx, 0f, nz, color
            )

            // Top cap
            addTriangle(
                cx, yTop, cz,
                x1, yTop, z1,
                x2, yTop, z2,
                0f, 1f, 0f, color
            )
        }
    }

    /**
     * Adds a hemispherical dome (e.g., Mosque dome).
     */
    fun addDome(
        cx: Float, cy: Float, cz: Float,
        radius: Float,
        color: FloatArray,
        slices: Int = 8,
        stacks: Int = 5
    ) {
        for (i in 0 until stacks) {
            val lat1 = (i.toFloat() / stacks) * (Math.PI.toFloat() / 2f)
            val lat2 = ((i + 1).toFloat() / stacks) * (Math.PI.toFloat() / 2f)

            val r1 = cos(lat1) * radius
            val y1 = cy + sin(lat1) * radius
            val r2 = cos(lat2) * radius
            val y2 = cy + sin(lat2) * radius

            for (j in 0 until slices) {
                val lon1 = (j.toFloat() / slices) * Math.PI.toFloat() * 2f
                val lon2 = ((j + 1).toFloat() / slices) * Math.PI.toFloat() * 2f

                val x11 = cx + cos(lon1) * r1
                val z11 = cz + sin(lon1) * r1
                val x12 = cx + cos(lon2) * r1
                val z12 = cz + sin(lon2) * r1

                val x21 = cx + cos(lon1) * r2
                val z21 = cz + sin(lon1) * r2
                val x22 = cx + cos(lon2) * r2
                val z22 = cz + sin(lon2) * r2

                addQuad(
                    x11, y1, z11,
                    x12, y1, z12,
                    x22, y2, z22,
                    x21, y2, z21,
                    0f, 1f, 0f, color
                )
            }
        }
    }

    /**
     * Adds a 4-sided pyramid / hip roof.
     */
    fun addPyramid(
        cx: Float, cy: Float, cz: Float,
        w: Float, h: Float, d: Float,
        color: FloatArray
    ) {
        val hw = w / 2f
        val hd = d / 2f
        val topY = cy + h

        // Front face (+Z)
        addTriangle(
            cx - hw, cy, cz + hd,
            cx + hw, cy, cz + hd,
            cx, topY, cz,
            0f, 0.6f, 0.8f, color
        )
        // Back face (-Z)
        addTriangle(
            cx + hw, cy, cz - hd,
            cx - hw, cy, cz - hd,
            cx, topY, cz,
            0f, 0.6f, -0.8f, color
        )
        // Left face (-X)
        addTriangle(
            cx - hw, cy, cz - hd,
            cx - hw, cy, cz + hd,
            cx, topY, cz,
            -0.8f, 0.6f, 0f, color
        )
        // Right face (+X)
        addTriangle(
            cx + hw, cy, cz + hd,
            cx + hw, cy, cz - hd,
            cx, topY, cz,
            0.8f, 0.6f, 0f, color
        )
    }

    /**
     * Adds a distant low-poly mountain peak on the horizon for realism.
     */
    fun addMountain(
        cx: Float, cy: Float, cz: Float,
        baseRadius: Float, height: Float,
        rockColor: FloatArray,
        snowColor: FloatArray? = null,
        sides: Int = 7
    ) {
        val snowH = height * 0.35f
        val midY = cy + height - snowH
        val peakY = cy + height

        for (i in 0 until sides) {
            val a1 = (i.toFloat() / sides) * Math.PI.toFloat() * 2f
            val a2 = ((i + 1).toFloat() / sides) * Math.PI.toFloat() * 2f

            val x1 = cx + cos(a1) * baseRadius
            val z1 = cz + sin(a1) * baseRadius
            val x2 = cx + cos(a2) * baseRadius
            val z2 = cz + sin(a2) * baseRadius

            // Base mountain slope
            val m1x = cx + cos(a1) * (baseRadius * 0.35f)
            val m1z = cz + sin(a1) * (baseRadius * 0.35f)
            val m2x = cx + cos(a2) * (baseRadius * 0.35f)
            val m2z = cz + sin(a2) * (baseRadius * 0.35f)

            addQuad(
                x1, cy, z1,
                x2, cy, z2,
                m2x, midY, m2z,
                m1x, midY, m1z,
                0f, 0.5f, 0.5f, rockColor
            )

            // Snow-capped peak or rocky summit
            val summitColor = snowColor ?: rockColor
            addTriangle(
                m1x, midY, m1z,
                m2x, midY, m2z,
                cx, peakY, cz,
                0f, 0.7f, 0.5f, summitColor
            )
        }
    }

    /**
     * Adds a full 3D UV sphere centered at (cx, cy, cz) with smooth outward normals.
     */
    fun addSphere(
        cx: Float, cy: Float, cz: Float,
        radius: Float,
        color: FloatArray,
        slices: Int = 20,
        stacks: Int = 14
    ) {
        val rCol = color[0]
        val gCol = color[1]
        val bCol = color[2]
        val aCol = if (color.size > 3) color[3] else 1f
        val halfPi = (Math.PI / 2.0).toFloat()
        val twoPi = (Math.PI * 2.0).toFloat()

        for (i in 0 until stacks) {
            val lat1 = -halfPi + (i.toFloat() / stacks) * Math.PI.toFloat()
            val lat2 = -halfPi + ((i + 1).toFloat() / stacks) * Math.PI.toFloat()
            val y1 = sin(lat1)
            val y2 = sin(lat2)
            val c1 = cos(lat1)
            val c2 = cos(lat2)

            for (j in 0 until slices) {
                val lon1 = (j.toFloat() / slices) * twoPi
                val lon2 = ((j + 1).toFloat() / slices) * twoPi

                val nx11 = c1 * cos(lon1); val nz11 = c1 * sin(lon1)
                val nx12 = c1 * cos(lon2); val nz12 = c1 * sin(lon2)
                val nx21 = c2 * cos(lon1); val nz21 = c2 * sin(lon1)
                val nx22 = c2 * cos(lon2); val nz22 = c2 * sin(lon2)

                // CCW outward triangles: (p11, p21, p22) and (p11, p22, p12)
                addVertex(cx + nx11 * radius, cy + y1 * radius, cz + nz11 * radius, nx11, y1, nz11, rCol, gCol, bCol, aCol)
                addVertex(cx + nx21 * radius, cy + y2 * radius, cz + nz21 * radius, nx21, y2, nz21, rCol, gCol, bCol, aCol)
                addVertex(cx + nx22 * radius, cy + y2 * radius, cz + nz22 * radius, nx22, y2, nz22, rCol, gCol, bCol, aCol)

                addVertex(cx + nx11 * radius, cy + y1 * radius, cz + nz11 * radius, nx11, y1, nz11, rCol, gCol, bCol, aCol)
                addVertex(cx + nx22 * radius, cy + y2 * radius, cz + nz22 * radius, nx22, y2, nz22, rCol, gCol, bCol, aCol)
                addVertex(cx + nx12 * radius, cy + y1 * radius, cz + nz12 * radius, nx12, y1, nz12, rCol, gCol, bCol, aCol)
            }
        }
    }

    /**
     * Adds a smooth 3D ellipsoid centered at (cx, cy, cz) with radii (rx, ry, rz) and smooth outward normals.
     */
    fun addEllipsoid(
        cx: Float, cy: Float, cz: Float,
        rx: Float, ry: Float, rz: Float,
        color: FloatArray,
        slices: Int = 24,
        stacks: Int = 16
    ) {
        val rCol = color[0]
        val gCol = color[1]
        val bCol = color[2]
        val aCol = if (color.size > 3) color[3] else 1f
        val halfPi = (Math.PI / 2.0).toFloat()
        val twoPi = (Math.PI * 2.0).toFloat()
        val invRx = if (rx > 0.0001f) 1f / rx else 1f
        val invRy = if (ry > 0.0001f) 1f / ry else 1f
        val invRz = if (rz > 0.0001f) 1f / rz else 1f

        fun emit(sx: Float, sy: Float, sz: Float) {
            val nx = sx * invRx
            val ny = sy * invRy
            val nz = sz * invRz
            val len = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(0.0001f)
            addVertex(
                cx + sx * rx, cy + sy * ry, cz + sz * rz,
                nx / len, ny / len, nz / len,
                rCol, gCol, bCol, aCol
            )
        }

        for (i in 0 until stacks) {
            val lat1 = -halfPi + (i.toFloat() / stacks) * Math.PI.toFloat()
            val lat2 = -halfPi + ((i + 1).toFloat() / stacks) * Math.PI.toFloat()
            val y1 = sin(lat1)
            val y2 = sin(lat2)
            val c1 = cos(lat1)
            val c2 = cos(lat2)

            for (j in 0 until slices) {
                val lon1 = (j.toFloat() / slices) * twoPi
                val lon2 = ((j + 1).toFloat() / slices) * twoPi

                val x11 = c1 * cos(lon1); val z11 = c1 * sin(lon1)
                val x12 = c1 * cos(lon2); val z12 = c1 * sin(lon2)
                val x21 = c2 * cos(lon1); val z21 = c2 * sin(lon1)
                val x22 = c2 * cos(lon2); val z22 = c2 * sin(lon2)

                emit(x11, y1, z11)
                emit(x21, y2, z21)
                emit(x22, y2, z22)

                emit(x11, y1, z11)
                emit(x22, y2, z22)
                emit(x12, y1, z12)
            }
        }
    }

    /**
     * Adds an inward-facing sky hemisphere dome with smooth vertical gradient from horizon to zenith.
     */
    fun addSkyDomeGradient(
        cx: Float, cy: Float, cz: Float,
        radius: Float,
        zenithColor: FloatArray,
        horizonColor: FloatArray,
        slices: Int = 28,
        stacks: Int = 12
    ) {
        val halfPi = (Math.PI / 2.0).toFloat()
        val twoPi = (Math.PI * 2.0).toFloat()

        fun mixColor(t: Float): FloatArray {
            val k = t.coerceIn(0f, 1f)
            return floatArrayOf(
                horizonColor[0] + (zenithColor[0] - horizonColor[0]) * k,
                horizonColor[1] + (zenithColor[1] - horizonColor[1]) * k,
                horizonColor[2] + (zenithColor[2] - horizonColor[2]) * k,
                1f
            )
        }

        for (i in -1 until stacks) {
            // Extend slightly below horizon (-0.12 rad) so horizon never shows a seam
            val lat1 = if (i < 0) -0.14f else (i.toFloat() / stacks) * halfPi
            val lat2 = ((i + 1).toFloat() / stacks) * halfPi
            val r1 = cos(lat1) * radius
            val y1 = cy + sin(lat1) * radius
            val r2 = cos(lat2) * radius
            val y2 = cy + sin(lat2) * radius

            val t1 = sin(lat1.coerceAtLeast(0f))
            val t2 = sin(lat2.coerceAtLeast(0f))
            val c1 = mixColor(t1 * 0.85f + (t1 * t1) * 0.15f)
            val c2 = mixColor(t2 * 0.85f + (t2 * t2) * 0.15f)

            for (j in 0 until slices) {
                val lon1 = (j.toFloat() / slices) * twoPi
                val lon2 = ((j + 1).toFloat() / slices) * twoPi

                val x11 = cx + cos(lon1) * r1; val z11 = cz + sin(lon1) * r1
                val x12 = cx + cos(lon2) * r1; val z12 = cz + sin(lon2) * r1
                val x21 = cx + cos(lon1) * r2; val z21 = cz + sin(lon1) * r2
                val x22 = cx + cos(lon2) * r2; val z22 = cz + sin(lon2) * r2

                // Inward CCW winding (visible cleanly from inside the dome)
                addVertex(x11, y1, z11, 0f, -1f, 0f, c1[0], c1[1], c1[2], 1f)
                addVertex(x22, y2, z22, 0f, -1f, 0f, c2[0], c2[1], c2[2], 1f)
                addVertex(x21, y2, z21, 0f, -1f, 0f, c2[0], c2[1], c2[2], 1f)

                addVertex(x11, y1, z11, 0f, -1f, 0f, c1[0], c1[1], c1[2], 1f)
                addVertex(x12, y1, z12, 0f, -1f, 0f, c1[0], c1[1], c1[2], 1f)
                addVertex(x22, y2, z22, 0f, -1f, 0f, c2[0], c2[1], c2[2], 1f)
            }
        }
    }

    /**
     * Adds a photorealistic 3D Moon sphere with lunar maria basins, highland plains, and craters.
     * Built with complete seamless geometry (zero holes or black overlap patches).
     */
    fun addRealisticMoonSphere(
        cx: Float, cy: Float, cz: Float,
        radius: Float,
        slices: Int = 32,
        stacks: Int = 22
    ) {
        val halfPi = (Math.PI / 2.0).toFloat()
        val twoPi = (Math.PI * 2.0).toFloat()

        // Notable crater seeds in unit sphere direction: (nx, ny, nz, angularRadius, rimBrightness)
        val craters = arrayOf(
            floatArrayOf(-0.22f, -0.58f, 0.78f, 0.16f, 0.12f),  // Tycho bright southern ray crater
            floatArrayOf(-0.42f, 0.18f, 0.89f, 0.15f, 0.08f),   // Copernicus
            floatArrayOf(-0.62f, 0.12f, 0.77f, 0.11f, 0.07f),   // Kepler
            floatArrayOf(0.28f, 0.42f, 0.86f, 0.18f, -0.06f),   // Serenitatis dark mare pocket
            floatArrayOf(0.55f, 0.14f, 0.82f, 0.20f, -0.08f),   // Tranquillitatis dark basin
            floatArrayOf(-0.18f, 0.52f, 0.83f, 0.22f, -0.09f),  // Mare Imbrium dark basin
            floatArrayOf(0.10f, -0.15f, 0.98f, 0.10f, -0.04f),  // Central lunar detail
            floatArrayOf(0.68f, -0.25f, 0.68f, 0.14f, -0.06f),  // Mare Fecunditatis
            floatArrayOf(-0.55f, -0.35f, 0.76f, 0.13f, 0.05f),  // Highland crater
            floatArrayOf(0.18f, 0.65f, -0.74f, 0.20f, -0.07f),  // Far side variation
            floatArrayOf(-0.48f, 0.25f, -0.84f, 0.16f, 0.06f)
        )

        fun moonShade(nx: Float, ny: Float, nz: Float): FloatArray {
            // Multi-octave organic lunar maria pattern
            val m1 = sin(nx * 3.4f + nz * 2.1f) * cos(ny * 2.9f - nx * 1.6f)
            val m2 = sin(nx * 7.2f - ny * 5.8f + nz * 4.3f) * 0.5f
            val m3 = cos(ny * 12.5f + nx * 9.8f - nz * 6.4f) * 0.25f
            val mariaFactor = ((m1 * 0.55f + m2 * 0.30f + m3 * 0.15f) * 0.5f + 0.5f).coerceIn(0f, 1f)

            // Base blend between silvery lunar highlands and cool basalt lunar seas
            var lum = 0.72f + mariaFactor * 0.25f
            for (c in craters) {
                val dot = (nx * c[0] + ny * c[1] + nz * c[2]).coerceIn(-1f, 1f)
                val distSq = (2f - 2f * dot).coerceAtLeast(0f)
                val crR = c[3]
                if (distSq < crR * crR * 2.2f) {
                    val d = kotlin.math.sqrt(distSq) / crR
                    if (d < 0.78f) {
                        // Crater floor
                        lum += c[4] * (1f - d * 0.5f) - 0.07f
                    } else if (d < 1.28f) {
                        // Crisp crater rim catchlight
                        lum += 0.09f * (1f - kotlin.math.abs(d - 1.0f) / 0.28f)
                    }
                }
            }

            // Gentle spherical shading along visible curvature (subtle sunlit pearl gradient)
            val phaseLight = (nx * -0.25f + ny * 0.32f + nz * 0.91f) * 0.10f
            lum = (lum + phaseLight).coerceIn(0.56f, 1.0f)

            val r = (lum * 0.98f).coerceIn(0f, 1f)
            val g = (lum * 0.97f).coerceIn(0f, 1f)
            val b = (lum * 0.94f + 0.04f).coerceIn(0f, 1f)
            return floatArrayOf(r, g, b)
        }

        for (i in 0 until stacks) {
            val lat1 = -halfPi + (i.toFloat() / stacks) * Math.PI.toFloat()
            val lat2 = -halfPi + ((i + 1).toFloat() / stacks) * Math.PI.toFloat()
            val y1 = sin(lat1); val y2 = sin(lat2)
            val c1 = cos(lat1); val c2 = cos(lat2)

            for (j in 0 until slices) {
                val lon1 = (j.toFloat() / slices) * twoPi
                val lon2 = ((j + 1).toFloat() / slices) * twoPi

                val nx11 = c1 * cos(lon1); val nz11 = c1 * sin(lon1)
                val nx12 = c1 * cos(lon2); val nz12 = c1 * sin(lon2)
                val nx21 = c2 * cos(lon1); val nz21 = c2 * sin(lon1)
                val nx22 = c2 * cos(lon2); val nz22 = c2 * sin(lon2)

                val col11 = moonShade(nx11, y1, nz11)
                val col12 = moonShade(nx12, y1, nz12)
                val col21 = moonShade(nx21, y2, nz21)
                val col22 = moonShade(nx22, y2, nz22)

                addVertex(cx + nx11 * radius, cy + y1 * radius, cz + nz11 * radius, nx11, y1, nz11, col11[0], col11[1], col11[2], 1f)
                addVertex(cx + nx21 * radius, cy + y2 * radius, cz + nz21 * radius, nx21, y2, nz21, col21[0], col21[1], col21[2], 1f)
                addVertex(cx + nx22 * radius, cy + y2 * radius, cz + nz22 * radius, nx22, y2, nz22, col22[0], col22[1], col22[2], 1f)

                addVertex(cx + nx11 * radius, cy + y1 * radius, cz + nz11 * radius, nx11, y1, nz11, col11[0], col11[1], col11[2], 1f)
                addVertex(cx + nx22 * radius, cy + y2 * radius, cz + nz22 * radius, nx22, y2, nz22, col22[0], col22[1], col22[2], 1f)
                addVertex(cx + nx12 * radius, cy + y1 * radius, cz + nz12 * radius, nx12, y1, nz12, col12[0], col12[1], col12[2], 1f)
            }
        }
    }

    fun getVertexCount(): Int = vertexCount

    fun build(): FloatBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(vertexCount * 10 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(data, 0, vertexCount * 10)
        floatBuffer.position(0)
        return floatBuffer
    }
}
