package com.example.opencity.model

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 3D vector for world positions, velocity, and orientations.
 */
data class Vector3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    fun set(nx: Float, ny: Float, nz: Float): Vector3 {
        x = nx
        y = ny
        z = nz
        return this
    }

    fun set(other: Vector3): Vector3 {
        x = other.x
        y = other.y
        z = other.z
        return this
    }

    fun add(dx: Float, dy: Float, dz: Float): Vector3 {
        x += dx
        y += dy
        z += dz
        return this
    }

    fun add(other: Vector3): Vector3 {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    fun distanceTo(other: Vector3): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun distanceTo2D(other: Vector3): Float {
        val dx = x - other.x
        val dz = z - other.z
        return sqrt(dx * dx + dz * dz)
    }

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalize(): Vector3 {
        val len = length()
        if (len > 0.0001f) {
            x /= len
            y /= len
            z /= len
        }
        return this
    }

    fun copy(): Vector3 = Vector3(x, y, z)

    companion object {
        fun fromYaw(yawDegrees: Float, length: Float = 1f): Vector3 {
            val rad = Math.toRadians(yawDegrees.toDouble())
            return Vector3(
                (sin(rad) * length).toFloat(),
                0f,
                (cos(rad) * length).toFloat()
            )
        }
    }
}
