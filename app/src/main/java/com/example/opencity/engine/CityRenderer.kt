package com.example.opencity.engine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.example.opencity.model.Building
import com.example.opencity.model.BuildingType
import com.example.opencity.model.CityData
import com.example.opencity.model.ElectricMetroTrain
import com.example.opencity.model.InteriorType
import com.example.opencity.model.NPC
import com.example.opencity.model.SkyMetroSystem
import com.example.opencity.model.StairDirection
import com.example.opencity.model.TrafficLight
import com.example.opencity.model.TrafficSystem
import com.example.opencity.model.Vector3
import com.example.opencity.model.Vehicle
import java.nio.FloatBuffer
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

enum class Weather {
    CLEAR,
    SUNSET,
    RAINY,
    FOGGY
}

enum class CameraMode {
    THIRD_PERSON,
    FIRST_PERSON
}

enum class CityFilter(
    val modeValue: Float,
    val arabicName: String,
    val emoji: String,
    val accentHex: Long
) {
    VIVID(1.0f, "حيوي ساطع", "✨", 0xFF38BDF8),
    GOLDEN(2.0f, "ذهبي سينمائي", "🌅", 0xFFF59E0B),
    NEON(3.0f, "نيون متوهج", "🌃", 0xFFE879F9),
    COOL(4.0f, "بارد نقي", "❄️", 0xFF60A5FA),
    VINTAGE(5.0f, "كلاسيك", "🎞️", 0xFFD97706),
    NATURAL(0.0f, "طبيعي", "🏙️", 0xFF4ADE80)
}

data class CityWorldInitData(
    val buildings: List<Building>,
    val vehicles: List<Vehicle>,
    val npcs: List<NPC>,
    val trafficLights: List<TrafficLight>,
    val trafficSystem: TrafficSystem,
    val skyMetroSystem: SkyMetroSystem
) {
    companion object {
        fun buildOffMainThread(): CityWorldInitData {
            val buildings = CityData.generateBuildings()
            return CityWorldInitData(
                buildings = buildings,
                vehicles = CityData.generateVehicles(buildings),
                npcs = CityData.generateNPCs(),
                trafficLights = CityData.generateTrafficLights(),
                trafficSystem = TrafficSystem(),
                skyMetroSystem = SkyMetroSystem()
            )
        }
    }
}

/**
 * High-performance OpenGL ES 2.0 3D renderer for OpenCity 3D.
 */
class CityRenderer(
    private val context: Context,
    prebuiltWorld: CityWorldInitData? = null
) : GLSurfaceView.Renderer {

    // Shader program & uniform locations
    private var programId: Int = 0
    private var uMVPMatrixLoc: Int = -1
    private var uModelMatrixLoc: Int = -1
    private var uLightDirLoc: Int = -1
    private var uSunColorLoc: Int = -1
    private var uAmbientColorLoc: Int = -1
    private var uHeadlightPosLoc: Int = -1
    private var uHeadlightDirLoc: Int = -1
    private var uHeadlightEnabledLoc: Int = -1
    private var uNightStreetLightLoc: Int = -1
    private var uUnlitEmissiveLoc: Int = -1
    private var uFogColorLoc: Int = -1
    private var uFogDensityLoc: Int = -1
    private var uFilterModeLoc: Int = -1
    private var uCameraPosLoc: Int = -1

    private var aPositionLoc: Int = -1
    private var aNormalLoc: Int = -1
    private var aColorLoc: Int = -1

    // Matrices
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)
    private val identityMatrix = FloatArray(16).also { Matrix.setIdentityM(it, 0) }
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val tempMatrix = FloatArray(16)

    // Meshes & World Entities (pre-built off Main Thread when provided)
    val worldMeshes = CityWorldMeshes()
    private val initData: CityWorldInitData = prebuiltWorld ?: CityWorldInitData.buildOffMainThread()
    private val buildings: List<Building> = initData.buildings
    val vehicles: MutableList<Vehicle> = CopyOnWriteArrayList(initData.vehicles)
    val npcs: MutableList<NPC> = CopyOnWriteArrayList(initData.npcs)
    val trafficLights: List<TrafficLight> = initData.trafficLights
    val trafficSystem = initData.trafficSystem
    val skyMetroSystem = initData.skyMetroSystem

    // Player state
    var playerPos = Vector3(8.0f, 0f, 6.5f)
    var playerYaw: Float = 195f
    var isPlayerInVehicle: Boolean = false
    var currentVehicle: Vehicle? = null
    var isRidingMetro: Boolean = false
    var activeMetroTrain: ElectricMetroTrain? = null
    var selectedMetroSeat: com.example.opencity.model.MetroSeat? = null
    var isInsideBuilding: Boolean = false
    var currentInteriorType: InteriorType = InteriorType.NONE
    var interiorPlayerPos = Vector3(0f, 0f, 5f)
    var interiorPlayerYaw: Float = 180f

    // Camera controls
    var cameraMode: CameraMode = CameraMode.THIRD_PERSON
    var cameraYawOffset: Float = 195f
    var cameraPitch: Float = 18f // degrees looking down
    var cameraDistance: Float = 7.5f

    // Time & Weather (Default: Permanent Clear Night Mode with bright city & realistic Moon/Stars)
    var timeOfDayHours: Float = 22.5f // 22:30 starry night
    var timeSpeed: Float = 0f // Keep night stable by default
    var isNightLocked: Boolean = true
    var weather: Weather = Weather.CLEAR
    var headlightsActive: Boolean = true
    var cityFilter: CityFilter = CityFilter.VIVID
    @Volatile var graphicsQuality: String = "MEDIUM"

    // Mission target marker position (null if none active)
    var activeMissionTargetPos: Vector3? = null

    // Character animation & equipped clothing outfit
    var characterWalkPhase: Float = 0f
    var isCharacterRunning: Boolean = false
    var playerOutfit: com.example.opencity.model.PlayerOutfit? = null

    // Direct Robot Control Mode ("تحكم بالروبوت على الأرض/العشب أو بالسيارة")
    var isControllingRobot: Boolean = false
    var controlledRobotVisorColor: FloatArray = floatArrayOf(0.05f, 0.95f, 1.0f, 1f)

    // Proximity-animated 3D doors & sectional garage doors (0f = closed, 1f = open)
    private val doorOpenProgress = mutableMapOf<String, Float>()
    private val garageDoorOpenProgress = mutableMapOf<String, Float>()

    // Timing & frame counter
    private var lastTimeNanos: Long = 0L
    private var frameCounter: Int = 0

    // Background static world builder & multi-frame warmup guard (ensures Frame #0 commits <5ms for SurfaceSyncGroup)
    private val worldBuildStarted = AtomicBoolean(false)
    @Volatile private var warmupStage: Int = 0

    // Persistent GPU VBO cache keyed by FloatBuffer identity (avoids re-uploading megabytes of static geometry every frame!)
    private val vboCache = java.util.IdentityHashMap<FloatBuffer, Int>(256)
    private val vboIdScratch = IntArray(1)
    private var boundVboId: Int = 0

    // Camera horizontal forward vector for fast frustum/distance culling
    private var camFwdX: Float = 0f
    private var camFwdZ: Float = 1f

    // Pre-allocated per-frame uniform scratch buffers (zero GC churn per frame)
    private val skyColorBuf = FloatArray(3)
    private val clearSkyBuf = FloatArray(3)
    private val sunColorBuf = FloatArray(3)
    private val ambientColorBuf = FloatArray(3)
    private val lightDirBuf = FloatArray(3)
    private val hlPosBuf = FloatArray(3)
    private val hlDirBuf = FloatArray(3)
    private val camPosBuf = FloatArray(3)

    companion object {
        private const val MAX_VBO_CACHE_SIZE = 512
        private val METRO_CAR_OFFSETS = floatArrayOf(0f, 10.2f, 20.4f)
        private val METRO_CAR_FLIP_REAR = booleanArrayOf(false, false, true)
        private val METRO_CAR_IS_LEAD = booleanArrayOf(true, false, true)
    }

    // Cached dynamic buffers (rebuilt only when visual state actually changes)
    private var interiorMeshCache: Pair<FloatBuffer, Int>? = null
    private var cachedInteriorType: InteriorType = InteriorType.NONE
    private var cachedTrafficLightsMesh: Pair<FloatBuffer, Int>? = null
    private var lastTrafficLightsSignature: Long = Long.MIN_VALUE
    private var cachedDayBulbsMesh: Pair<FloatBuffer, Int>? = null
    private var cachedNightBulbsMesh: Pair<FloatBuffer, Int>? = null
    private var cachedNightGlowMesh: Pair<FloatBuffer, Int>? = null
    private var cachedDoorsMesh: Pair<FloatBuffer, Int>? = null
    private var lastDoorsSignature: Long = Long.MIN_VALUE
    private var cachedNightSkyDomeMesh: Pair<FloatBuffer, Int>? = null
    private var cachedStarsSteadyMesh: Pair<FloatBuffer, Int>? = null
    private var cachedStarsTwinkleHighMesh: Pair<FloatBuffer, Int>? = null
    private var cachedStarsTwinkleSoftMesh: Pair<FloatBuffer, Int>? = null
    private var cachedMoonSphereMesh: Pair<FloatBuffer, Int>? = null
    private var cachedMoonHaloMesh: Pair<FloatBuffer, Int>? = null
    private var cachedSunMesh: Pair<FloatBuffer, Int>? = null
    private var lastSunBucket: Int = Int.MIN_VALUE
    private var cachedMissionMarkerMesh: Pair<FloatBuffer, Int>? = null
    private var cachedMetroCarMeshes: MetroCarVisualMeshes? = null
    private var cachedLockedBarMesh: Pair<FloatBuffer, Int>? = null
    private var cachedUnlockedBarMesh: Pair<FloatBuffer, Int>? = null
    private var activeFogDensity: Float = 0.0005f

    private fun startAsyncWorldBuild() {
        if (worldBuildStarted.compareAndSet(false, true)) {
            val builderThread = Thread({
                worldMeshes.buildWorldStaticMesh(buildings, trafficLights)
            }, "OpenCity-WorldMeshBuilder")
            builderThread.priority = Thread.MIN_PRIORITY
            builderThread.isDaemon = true
            builderThread.start()
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        warmupStage = 0
        // EGL context recreated: discard stale VBO handles from any previous context
        vboCache.clear()
        boundVboId = 0
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)

        val vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, ShaderHelper.VERTEX_SHADER_CODE)
        val fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, ShaderHelper.FRAGMENT_SHADER_CODE)
        programId = ShaderHelper.createProgram(vertexShader, fragmentShader)

        uMVPMatrixLoc = GLES20.glGetUniformLocation(programId, "uMVPMatrix")
        uModelMatrixLoc = GLES20.glGetUniformLocation(programId, "uModelMatrix")
        uLightDirLoc = GLES20.glGetUniformLocation(programId, "uLightDir")
        uSunColorLoc = GLES20.glGetUniformLocation(programId, "uSunColor")
        uAmbientColorLoc = GLES20.glGetUniformLocation(programId, "uAmbientColor")
        uHeadlightPosLoc = GLES20.glGetUniformLocation(programId, "uHeadlightPos")
        uHeadlightDirLoc = GLES20.glGetUniformLocation(programId, "uHeadlightDir")
        uHeadlightEnabledLoc = GLES20.glGetUniformLocation(programId, "uHeadlightEnabled")
        uNightStreetLightLoc = GLES20.glGetUniformLocation(programId, "uNightStreetLight")
        uUnlitEmissiveLoc = GLES20.glGetUniformLocation(programId, "uUnlitEmissive")
        uFogColorLoc = GLES20.glGetUniformLocation(programId, "uFogColor")
        uFogDensityLoc = GLES20.glGetUniformLocation(programId, "uFogDensity")
        uFilterModeLoc = GLES20.glGetUniformLocation(programId, "uFilterMode")
        uCameraPosLoc = GLES20.glGetUniformLocation(programId, "uCameraPos")

        aPositionLoc = GLES20.glGetAttribLocation(programId, "aPosition")
        aNormalLoc = GLES20.glGetAttribLocation(programId, "aNormal")
        aColorLoc = GLES20.glGetAttribLocation(programId, "aColor")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat().coerceAtLeast(1f)
        Matrix.perspectiveM(projectionMatrix, 0, 68f, aspect, 0.38f, 2400f)
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = System.nanoTime()
        val deltaSec = if (lastTimeNanos > 0) ((now - lastTimeNanos) / 1_000_000_000f).coerceIn(0.001f, 0.1f) else 0.016f
        lastTimeNanos = now
        frameCounter++

        // Enforce permanent Night lock when enabled so city stays illuminated at night
        if (isNightLocked) {
            timeSpeed = 0f
            if (timeOfDayHours in 6.2f..18.0f) {
                timeOfDayHours = 22.5f
            }
        } else if (timeSpeed > 0f) {
            timeOfDayHours += timeSpeed * deltaSec
            if (timeOfDayHours >= 24f) timeOfDayHours -= 24f
        }

        // Update NPCs & Traffic: always update nearby NPCs smoothly, throttle very distant NPCs
        val pRefX = currentVehicle?.posX ?: playerPos.x
        val pRefZ = currentVehicle?.posZ ?: playerPos.z
        val updateFarNpcs = (frameCounter and 3) == 0
        for (npc in npcs) {
            val dx = npc.posX - pRefX
            val dz = npc.posZ - pRefZ
            if (dx * dx + dz * dz < 160f * 160f) {
                npc.update(deltaSec, buildings, playerPos)
            } else if (updateFarNpcs) {
                npc.update(deltaSec * 4f, buildings, null)
            }
        }
        trafficSystem.update(deltaSec, currentVehicle, playerPos, buildings)
        skyMetroSystem.update(deltaSec)
        updateProximityDoors(deltaSec)

        // Calculate sky & lighting parameters based on time and weather
        val skyColor = skyColorBuf
        val sunColor = sunColorBuf
        val ambientColor = ambientColorBuf
        val lightDir = lightDirBuf
        var fogDensity = 0.0020f

        val sunAngle = ((timeOfDayHours - 6f) / 24f) * Math.PI.toFloat() * 2f
        lightDir[0] = cos(sunAngle)
        lightDir[1] = sin(sunAngle).coerceAtLeast(0.05f)
        lightDir[2] = 0.35f

        val isNight = isNightLocked || timeOfDayHours < 6.2f || timeOfDayHours > 18.0f
        val isSunset = !isNight && (timeOfDayHours in 17.2f..18.0f || weather == Weather.SUNSET)

        if (isSunset) {
            skyColor[0] = 0.90f; skyColor[1] = 0.45f; skyColor[2] = 0.22f
            sunColor[0] = 1.15f; sunColor[1] = 0.75f; sunColor[2] = 0.35f
            ambientColor[0] = 0.50f; ambientColor[1] = 0.38f; ambientColor[2] = 0.30f
            fogDensity = 0.00065f
        } else if (isNight) {
            // Clear moonlit night sky & balanced moonlight ambient so streets, buildings & sea stay luminous and crisp
            skyColor[0] = 0.03f; skyColor[1] = 0.06f; skyColor[2] = 0.14f
            sunColor[0] = 0.34f; sunColor[1] = 0.40f; sunColor[2] = 0.56f
            ambientColor[0] = 0.36f; ambientColor[1] = 0.40f; ambientColor[2] = 0.52f
            lightDir[0] = -0.32f; lightDir[1] = 0.72f; lightDir[2] = -0.62f
            fogDensity = 0.00045f
        } else {
            // Daytime: ضوء شمس حار وقوي جداً ترى به كل شيء بوضوح فائق (Ultra-bright blazing sun)
            if (weather == Weather.RAINY) {
                skyColor[0] = 0.50f; skyColor[1] = 0.53f; skyColor[2] = 0.58f
                sunColor[0] = 0.85f; sunColor[1] = 0.85f; sunColor[2] = 0.90f
                ambientColor[0] = 0.55f; ambientColor[1] = 0.57f; ambientColor[2] = 0.60f
                fogDensity = 0.006f
            } else if (weather == Weather.FOGGY) {
                skyColor[0] = 0.70f; skyColor[1] = 0.72f; skyColor[2] = 0.75f
                sunColor[0] = 0.75f; sunColor[1] = 0.75f; sunColor[2] = 0.78f
                ambientColor[0] = 0.60f; ambientColor[1] = 0.60f; ambientColor[2] = 0.65f
                fogDensity = 0.012f
            } else {
                skyColor[0] = 0.40f; skyColor[1] = 0.72f; skyColor[2] = 1.0f // Crystal clear bright azure sky
                sunColor[0] = 1.35f; sunColor[1] = 1.30f; sunColor[2] = 1.15f // Hot blazing golden sunlight
                ambientColor[0] = 0.58f; ambientColor[1] = 0.58f; ambientColor[2] = 0.62f // Clear bright ambient light
                fogDensity = 0.00045f // Ultra-clear horizon visibility across the beach and vast open ocean
            }
        }

        if (isInsideBuilding) {
            sunColor[0] = 0.85f; sunColor[1] = 0.82f; sunColor[2] = 0.78f
            ambientColor[0] = 0.75f; ambientColor[1] = 0.74f; ambientColor[2] = 0.72f
            fogDensity = 0.0005f
        }

        // Sync glClearColor sky with active GPU CityFilter so horizon fog never seams
        val clearSky = applyFilterToSkyRgb(skyColor, if (isInsideBuilding) CityFilter.NATURAL else cityFilter)
        GLES20.glClearColor(clearSky[0], clearSky[1], clearSky[2], 1.0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Commit Frame #0 immediately (< 2ms) so WindowManager/SurfaceSyncGroup never times out waiting for GLSurfaceView
        if (warmupStage == 0) {
            warmupStage = 1
            return
        }
        if (warmupStage == 1) {
            startAsyncWorldBuild()
        }

        GLES20.glUseProgram(programId)
        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glEnableVertexAttribArray(aNormalLoc)
        GLES20.glEnableVertexAttribArray(aColorLoc)
        boundVboId = 0
        activeFogDensity = fogDensity

        // Pre-normalize directional light vector once on CPU instead of per-vertex on GPU
        val lightLen = kotlin.math.sqrt(
            (lightDir[0] * lightDir[0] + lightDir[1] * lightDir[1] + lightDir[2] * lightDir[2]).toDouble()
        ).toFloat().coerceAtLeast(0.0001f)
        lightDir[0] /= lightLen
        lightDir[1] /= lightLen
        lightDir[2] /= lightLen

        // Setup common uniforms
        GLES20.glUniform3fv(uLightDirLoc, 1, lightDir, 0)
        GLES20.glUniform3fv(uSunColorLoc, 1, sunColor, 0)
        GLES20.glUniform3fv(uAmbientColorLoc, 1, ambientColor, 0)
        GLES20.glUniform3fv(uFogColorLoc, 1, skyColor, 0)
        GLES20.glUniform1f(uFogDensityLoc, fogDensity)
        GLES20.glUniform1f(
            uFilterModeLoc,
            if (isInsideBuilding) CityFilter.NATURAL.modeValue else cityFilter.modeValue
        )

        // Turn on street lighting smoothly at dusk/night when outside on the city streets
        val streetLightFactor = when {
            isInsideBuilding -> 0.0f
            isNight -> 1.0f
            isSunset -> 0.60f
            else -> 0.0f
        }
        GLES20.glUniform1f(uNightStreetLightLoc, streetLightFactor)
        GLES20.glUniform1f(uUnlitEmissiveLoc, 0.0f)

        // Headlights uniform (pre-normalized direction on CPU)
        val targetVeh = currentVehicle
        if (targetVeh != null && (isNight || headlightsActive)) {
            val rad = Math.toRadians(targetVeh.yaw.toDouble())
            val fwdX = sin(rad).toFloat()
            val fwdZ = cos(rad).toFloat()
            hlPosBuf[0] = targetVeh.posX + fwdX * 2f; hlPosBuf[1] = 0.6f; hlPosBuf[2] = targetVeh.posZ + fwdZ * 2f
            val hlLen = kotlin.math.sqrt((fwdX * fwdX + 0.08f * 0.08f + fwdZ * fwdZ).toDouble()).toFloat().coerceAtLeast(0.0001f)
            hlDirBuf[0] = fwdX / hlLen; hlDirBuf[1] = -0.08f / hlLen; hlDirBuf[2] = fwdZ / hlLen
            GLES20.glUniform3fv(uHeadlightPosLoc, 1, hlPosBuf, 0)
            GLES20.glUniform3fv(uHeadlightDirLoc, 1, hlDirBuf, 0)
            GLES20.glUniform1f(uHeadlightEnabledLoc, 1.0f)
        } else {
            GLES20.glUniform1f(uHeadlightEnabledLoc, 0.0f)
        }

        // Setup Camera View Matrix & precompute VP matrix once for the entire frame
        setupCamera()
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        if (isInsideBuilding) {
            renderInterior()
        } else {
            renderCityWorld(isNight, isSunset)
        }

        if (boundVboId != 0) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            boundVboId = 0
        }
        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aNormalLoc)
        GLES20.glDisableVertexAttribArray(aColorLoc)

        if (warmupStage < 4) {
            warmupStage++
        }
    }

    private fun applyFilterToSkyRgb(c: FloatArray, filter: CityFilter): FloatArray {
        val r = c[0]
        val g = c[1]
        val b = c[2]
        val luma = r * 0.299f + g * 0.587f + b * 0.114f
        fun lerp(a: Float, bVal: Float, t: Float) = a + (bVal - a) * t
        val out = clearSkyBuf
        when (filter) {
            CityFilter.NATURAL -> {
                out[0] = r; out[1] = g; out[2] = b
            }
            CityFilter.VIVID -> {
                out[0] = ((lerp(luma, r, 1.24f) - 0.5f) * 1.07f + 0.5f).coerceIn(0f, 1f)
                out[1] = ((lerp(luma, g, 1.24f) - 0.5f) * 1.07f + 0.5f).coerceIn(0f, 1f)
                out[2] = ((lerp(luma, b, 1.24f) - 0.5f) * 1.07f + 0.5f).coerceIn(0f, 1f)
            }
            CityFilter.GOLDEN -> {
                out[0] = (((lerp(luma, r, 1.20f) - 0.5f) * 1.08f + 0.5f) * 1.12f).coerceIn(0f, 1f)
                out[1] = (((lerp(luma, g, 1.20f) - 0.5f) * 1.08f + 0.5f) * 1.01f).coerceIn(0f, 1f)
                out[2] = (((lerp(luma, b, 1.20f) - 0.5f) * 1.08f + 0.5f) * 0.82f).coerceIn(0f, 1f)
            }
            CityFilter.NEON -> {
                val t = (luma * 1.2f).coerceIn(0f, 1f)
                out[0] = (lerp(luma, r, 1.38f) * lerp(0.85f, 1.16f, t)).coerceIn(0f, 1f)
                out[1] = (lerp(luma, g, 1.38f) * lerp(0.95f, 0.88f, t)).coerceIn(0f, 1f)
                out[2] = (lerp(luma, b, 1.38f) * lerp(1.18f, 1.14f, t)).coerceIn(0f, 1f)
            }
            CityFilter.COOL -> {
                out[0] = (lerp(luma, r, 1.12f) * 0.88f).coerceIn(0f, 1f)
                out[1] = (lerp(luma, g, 1.12f) * 0.99f).coerceIn(0f, 1f)
                out[2] = (lerp(luma, b, 1.12f) * 1.14f).coerceIn(0f, 1f)
            }
            CityFilter.VINTAGE -> {
                val sr = r * 0.393f + g * 0.769f + b * 0.189f
                val sg = r * 0.349f + g * 0.686f + b * 0.168f
                val sb = r * 0.272f + g * 0.534f + b * 0.131f
                out[0] = (lerp(r, sr, 0.68f) * 1.04f).coerceIn(0f, 1f)
                out[1] = (lerp(g, sg, 0.68f) * 1.00f).coerceIn(0f, 1f)
                out[2] = (lerp(b, sb, 0.68f) * 0.94f).coerceIn(0f, 1f)
            }
        }
        return out
    }

    private fun setupCamera() {
        val activeVeh = currentVehicle
        val ridingTrain = activeMetroTrain
        val activeSeat = selectedMetroSeat
        val seatPose = if (!isInsideBuilding && isRidingMetro && ridingTrain != null && activeSeat != null) {
            SkyMetroSystem.computeMetroSeatWorldPose(ridingTrain, activeSeat)
        } else null

        val targetPos = if (isInsideBuilding) {
            if (isPlayerInVehicle && activeVeh != null) {
                Vector3(activeVeh.posX, activeVeh.posY, activeVeh.posZ)
            } else {
                interiorPlayerPos
            }
        } else if (isRidingMetro && ridingTrain != null) {
            if (seatPose != null) {
                Vector3(seatPose.x, seatPose.y + 0.55f, seatPose.z)
            } else {
                Vector3(ridingTrain.currentPosX, SkyMetroSystem.DECK_SURFACE_Y + 2.0f, ridingTrain.currentPosZ)
            }
        } else if (isPlayerInVehicle && activeVeh != null) {
            Vector3(activeVeh.posX, activeVeh.posY, activeVeh.posZ)
        } else {
            playerPos
        }

        val totalYaw = when {
            isInsideBuilding && isPlayerInVehicle && activeVeh != null -> activeVeh.yaw + cameraYawOffset
            !isInsideBuilding && isRidingMetro && seatPose != null -> seatPose.yaw + cameraYawOffset
            !isInsideBuilding && isRidingMetro && ridingTrain != null -> ridingTrain.currentYaw + cameraYawOffset
            !isInsideBuilding && isPlayerInVehicle && activeVeh != null -> activeVeh.yaw + cameraYawOffset
            else -> cameraYawOffset
        }
        val yawRad = Math.toRadians(totalYaw.toDouble())
        val pitchRad = Math.toRadians(cameraPitch.toDouble())

        if (cameraMode == CameraMode.FIRST_PERSON) {
            // First-person eye position
            val eyeHeight = when {
                isRidingMetro && seatPose != null -> 0.50f
                isRidingMetro -> 0.55f
                isPlayerInVehicle -> 1.15f
                else -> 1.65f
            }
            val eyeX = targetPos.x
            val eyeY = targetPos.y + eyeHeight
            val eyeZ = targetPos.z

            val lookDist = 10f
            val centerX = eyeX + (sin(yawRad) * cos(pitchRad) * lookDist).toFloat()
            val centerY = eyeY - (sin(pitchRad) * lookDist).toFloat()
            val centerZ = eyeZ + (cos(yawRad) * cos(pitchRad) * lookDist).toFloat()

            camPosBuf[0] = eyeX; camPosBuf[1] = eyeY; camPosBuf[2] = eyeZ
            camFwdX = sin(yawRad).toFloat()
            camFwdZ = cos(yawRad).toFloat()
            if (uCameraPosLoc >= 0) {
                GLES20.glUniform3fv(uCameraPosLoc, 1, camPosBuf, 0)
            }
            Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, 0f, 1f, 0f)
        } else {
            // Third-person trailing camera with full up/down/left/right manual control
            val dist = when {
                isInsideBuilding && isPlayerInVehicle -> (cameraDistance + 0.5f).coerceIn(4.2f, 8.2f)
                isRidingMetro && seatPose != null -> (cameraDistance * 0.55f).coerceIn(2.8f, 10.0f)
                isRidingMetro -> (cameraDistance + 6.5f).coerceIn(10.5f, 22.0f)
                isPlayerInVehicle -> (cameraDistance + 1.0f).coerceIn(4.5f, 18.0f)
                isInsideBuilding -> cameraDistance.coerceIn(2.6f, 6.0f)
                else -> cameraDistance.coerceIn(3.5f, 16.0f)
            }
            val heightBase = when {
                isRidingMetro && seatPose != null -> 0.75f
                isPlayerInVehicle || isRidingMetro -> 2.2f
                else -> 1.75f
            }
            val rawCamY = (targetPos.y + heightBase + sin(pitchRad) * dist).toFloat()
            val minOutdoorCamY = if (isRidingMetro && seatPose != null) targetPos.y + 0.25f else if (isPlayerInVehicle || isRidingMetro) targetPos.y + 0.85f else targetPos.y + 0.55f
            var camY = rawCamY.coerceAtLeast(minOutdoorCamY)
            val horizDist = cos(pitchRad) * dist
            var camX = (targetPos.x - sin(yawRad) * horizDist).toFloat()
            var camZ = (targetPos.z - cos(yawRad) * horizDist).toFloat()

            // When looking UP (pitch < 0), tilt lookTargetY smoothly skyward instead of burying camera underground
            val upwardLookFactor = (-sin(pitchRad)).toFloat().coerceAtLeast(0f)
            var lookTargetY = targetPos.y + (if (isRidingMetro && seatPose != null) 0.40f else if (isPlayerInVehicle) 1.3f else 1.25f) + upwardLookFactor * (dist * 1.15f)

            // Clamp indoor camera strictly inside walls and floor/ceiling so it never clips outside into void
            if (isInsideBuilding) {
                val interior = com.example.opencity.model.BuildingInterior.getInterior(currentInteriorType)
                val maxX = (interior.roomWidth / 2f - 0.55f).coerceAtLeast(1f)
                val maxZ = (interior.roomLength / 2f - 0.55f).coerceAtLeast(1f)
                camX = camX.coerceIn(-maxX, maxX)
                camZ = camZ.coerceIn(-maxZ, maxZ)

                val minCamY: Float
                val maxCamY: Float
                if (currentInteriorType == InteriorType.PLAYER_VILLA) {
                    if (targetPos.y < 2.6f) {
                        minCamY = if (targetPos.y < -0.1f) -0.35f else 0.55f
                        maxCamY = 4.20f
                    } else {
                        minCamY = 4.95f
                        maxCamY = interior.roomHeight - 0.45f
                    }
                } else {
                    minCamY = 0.55f
                    maxCamY = interior.roomHeight - 0.45f
                }
                camY = camY.coerceIn(minCamY, maxCamY)
                lookTargetY = lookTargetY.coerceIn(-0.4f, interior.roomHeight - 0.3f)
            }

            camPosBuf[0] = camX; camPosBuf[1] = camY; camPosBuf[2] = camZ
            camFwdX = sin(yawRad).toFloat()
            camFwdZ = cos(yawRad).toFloat()
            if (uCameraPosLoc >= 0) {
                GLES20.glUniform3fv(uCameraPosLoc, 1, camPosBuf, 0)
            }
            Matrix.setLookAtM(viewMatrix, 0, camX, camY, camZ, targetPos.x, lookTargetY, targetPos.z, 0f, 1f, 0f)
        }
    }

    private fun isVisibleInCamera(wx: Float, wz: Float, maxDistSq: Float, padBack: Float = 16f): Boolean {
        val dx = wx - camPosBuf[0]
        val dz = wz - camPosBuf[2]
        val distSq = dx * dx + dz * dz
        if (distSq > maxDistSq) return false
        if (distSq <= padBack * padBack) return true
        return (dx * camFwdX + dz * camFwdZ) > -padBack
    }

    private fun renderCityWorld(isNight: Boolean, isSunset: Boolean) {
        // 0. Deep Night Sky Dome, Starfield, and Realistic Lunar-Maria Moon (rendered cleanly at infinity behind world)
        renderSkyAndCelestialBodies(isNight)

        // Dynamic view-distance thresholds scaled by Graphics Quality preset
        val vehMaxDistSq: Float
        val pedMaxDistSq: Float
        val metroMaxDistSq: Float
        when (graphicsQuality) {
            "LOW" -> {
                vehMaxDistSq = 155f * 155f
                pedMaxDistSq = 85f * 85f
                metroMaxDistSq = 220f * 220f
            }
            "HIGH" -> {
                vehMaxDistSq = 320f * 320f
                pedMaxDistSq = 175f * 175f
                metroMaxDistSq = 450f * 450f
            }
            else -> {
                vehMaxDistSq = 225f * 225f
                pedMaxDistSq = 125f * 125f
                metroMaxDistSq = 310f * 310f
            }
        }

        // 1. Static City Buffer (drawn directly from GPU VBO with precomputed VP matrix)
        val staticBuf = worldMeshes.worldStaticBuffer
        val staticCount = worldMeshes.worldStaticVertexCount
        if (staticBuf != null && staticCount > 0) {
            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, vpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, identityMatrix, 0)
            drawBuffer(staticBuf, staticCount)
        }

        // 1.5. Proximity-Animated 3D Doors & Garage Roll-Up Doors
        renderProximityDoors()

        // 2. Dynamic Vehicles (frustum + distance culled)
        fun drawWorldVehicle(veh: Vehicle, showRobotInSeat: Boolean) {
            if (veh.isInUndergroundGarage) return
            if (!veh.isPlayerDriving && !isVisibleInCamera(veh.posX, veh.posZ, vehMaxDistSq, padBack = 18f)) return
            val mesh = worldMeshes.getVehicleMesh(
                veh.type,
                veh.colorPrimary,
                veh.colorSecondary,
                veh.meshStyle,
                veh.isNitroActive,
                veh.underglowColor,
                hasRobotDriver = showRobotInSeat
            )

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, veh.posX, veh.posY, veh.posZ)
            Matrix.rotateM(modelMatrix, 0, veh.yaw, 0f, 1f, 0f)
            if (veh.pitchAngle != 0f) {
                Matrix.rotateM(modelMatrix, 0, veh.pitchAngle, 1f, 0f, 0f)
            }

            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(mesh.first, mesh.second)
        }

        for (veh in vehicles) {
            val showRobotInSeat = if (veh.isPlayerDriving) isControllingRobot else veh.isAiControlled
            drawWorldVehicle(veh, showRobotInSeat)
        }
        for (veh in trafficSystem.trafficCars) {
            val showRobotInSeat = if (veh.isPlayerDriving) isControllingRobot else veh.isAiControlled
            drawWorldVehicle(veh, showRobotInSeat)
        }
        for (robot in trafficSystem.autonomousRobots) {
            val veh = robot.car
            val showRobotInSeat = if (veh.isPlayerDriving) isControllingRobot else robot.isInCar
            drawWorldVehicle(veh, showRobotInSeat)
        }

        // 2.5. Dynamic Turnstile Barrier Gates (5 EGP Entry Lock) & Elevated 3-Car Electric Sky Metro Trains
        val lockedBar = cachedLockedBarMesh ?: SkyMetroAndCompoundMeshes.buildTurnstileBarrierBar(false).also { cachedLockedBarMesh = it }
        val unlockedBar = cachedUnlockedBarMesh ?: SkyMetroAndCompoundMeshes.buildTurnstileBarrierBar(true).also { cachedUnlockedBarMesh = it }
        for (st in SkyMetroSystem.STATIONS) {
            if (!isVisibleInCamera(st.turnstileX, st.turnstileZ, 110f * 110f, padBack = 14f)) continue
            val isUnlocked = skyMetroSystem.unlockedStationGates.contains(st.id)
            val barMesh = if (isUnlocked) unlockedBar else lockedBar
            val yawAngle = if (st.stairDirection == StairDirection.CLIMB_SOUTH || st.stairDirection == StairDirection.CLIMB_NORTH) 0f else 90f
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, st.turnstileX, 0f, st.turnstileZ)
            Matrix.rotateM(modelMatrix, 0, yawAngle, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)
            drawBuffer(barMesh.first, barMesh.second)
        }

        if (warmupStage >= 2) {
            val metroMeshes = cachedMetroCarMeshes ?: SkyMetroAndCompoundMeshes.buildMetroCarMeshes().also { cachedMetroCarMeshes = it }
            for (train in skyMetroSystem.trains) {
                if (train !== activeMetroTrain && !isVisibleInCamera(train.currentPosX, train.currentPosZ, metroMaxDistSq, padBack = 32f)) continue
                for (cIdx in 0..2) {
                    val pose = SkyMetroSystem.sampleTrackPose(train.progressMeters - METRO_CAR_OFFSETS[cIdx])
                    val renderYaw = if (METRO_CAR_FLIP_REAR[cIdx]) (pose.yaw + 180f) % 360f else pose.yaw
                    val isLeadStyle = METRO_CAR_IS_LEAD[cIdx]
                    val buf = if (isLeadStyle) metroMeshes.leadBodyBuffer else metroMeshes.coachBodyBuffer
                    val cnt = if (isLeadStyle) metroMeshes.leadBodyCount else metroMeshes.coachBodyCount
                    Matrix.setIdentityM(modelMatrix, 0)
                    Matrix.translateM(modelMatrix, 0, pose.x, pose.y, pose.z)
                    Matrix.rotateM(modelMatrix, 0, renderYaw, 0f, 1f, 0f)
                    Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
                    GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
                    GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)
                    drawBuffer(buf, cnt)
                }
            }
        }

        // 3. Animated Player Character OR Direct-Controlled Cyber Robot (Outdoors OR Seated/Standing inside Sky Metro Car)
        if (isRidingMetro && activeMetroTrain != null) {
            val ridingTrain = activeMetroTrain!!
            val seat = selectedMetroSeat
            val seated = seat != null
            val renderX: Float
            val renderY: Float
            val renderZ: Float
            val renderYaw: Float
            if (seat != null) {
                val pose = SkyMetroSystem.computeMetroSeatWorldPose(ridingTrain, seat)
                renderX = pose.x
                renderY = pose.y
                renderZ = pose.z
                renderYaw = pose.yaw
            } else {
                renderX = ridingTrain.currentPosX
                renderY = SkyMetroSystem.DECK_SURFACE_Y + 1.04f
                renderZ = ridingTrain.currentPosZ
                renderYaw = ridingTrain.currentYaw
            }
            val charMesh = if (isControllingRobot) {
                worldMeshes.buildRobotMesh(0f, 0f, controlledRobotVisorColor, isSeated = seated)
            } else {
                worldMeshes.buildCharacterMesh(0f, false, playerOutfit, isSeated = seated)
            }
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, renderX, renderY, renderZ)
            Matrix.rotateM(modelMatrix, 0, renderYaw, 0f, 1f, 0f)
            Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)
            drawBuffer(charMesh.first, charMesh.second)
        } else if (!isPlayerInVehicle) {
            val charMesh = if (isControllingRobot) {
                worldMeshes.buildRobotMesh(characterWalkPhase, 0f, controlledRobotVisorColor)
            } else {
                worldMeshes.buildCharacterMesh(characterWalkPhase, isCharacterRunning, playerOutfit)
            }

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, playerPos.x, playerPos.y, playerPos.z)
            Matrix.rotateM(modelMatrix, 0, playerYaw, 0f, 1f, 0f)

            Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(charMesh.first, charMesh.second)
        }

        // 4. Pedestrian Citizens on Sidewalks / Parks (frustum + distance culled)
        fun drawPedestrian(npc: NPC) {
            if (!isVisibleInCamera(npc.posX, npc.posZ, pedMaxDistSq, padBack = 12f)) return
            val npcMesh = worldMeshes.buildCharacterMesh(npc.walkAnimTime, false)

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, npc.posX, npc.posY, npc.posZ)
            Matrix.rotateM(modelMatrix, 0, npc.yaw, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(npcMesh.first, npcMesh.second)
        }
        for (npc in npcs) drawPedestrian(npc)
        for (npc in trafficSystem.pedestrians) drawPedestrian(npc)

        // 4.2. Autonomous Human-Like Cyber Robots outside their cars
        for (robot in trafficSystem.autonomousRobots) {
            if (robot.isInCar) continue
            if (!isVisibleInCamera(robot.footX, robot.footZ, pedMaxDistSq, padBack = 12f)) continue
            val robotMesh = worldMeshes.buildRobotMesh(robot.walkPhase, robot.wavePhase, robot.visorColor)

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, robot.footX, robot.footY, robot.footZ)
            Matrix.rotateM(modelMatrix, 0, robot.footYaw, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(robotMesh.first, robotMesh.second)
        }

        // 4.3. Dedicated Grass-Walking Cyber Robots roaming green parks across the city
        for (grobot in trafficSystem.grassRobots) {
            if (!isVisibleInCamera(grobot.posX, grobot.posZ, pedMaxDistSq, padBack = 12f)) continue
            val gMesh = worldMeshes.buildRobotMesh(grobot.walkPhase, grobot.wavePhase, grobot.visorColor)

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, grobot.posX, grobot.posY, grobot.posZ)
            Matrix.rotateM(modelMatrix, 0, grobot.yaw, 0f, 1f, 0f)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(gMesh.first, gMesh.second)
        }

        // 4.5. Stationary Crystal-Clear Translucent Water (Sea + Outdoor Villa Pools) drawn after characters so swimmers appear submerged
        val waterBuf = worldMeshes.worldWaterBuffer
        val waterCount = worldMeshes.worldWaterVertexCount
        if (waterBuf != null && waterCount > 0) {
            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, vpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, identityMatrix, 0)

            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            GLES20.glDepthMask(false)
            drawBuffer(waterBuf, waterCount)
            GLES20.glDepthMask(true)
            GLES20.glDisable(GLES20.GL_BLEND)
        }

        // 5. Dynamic Traffic Light Bulbs & Self-Illuminated Night Building Windows / Signs
        GLES20.glUniform1f(uUnlitEmissiveLoc, 1.0f)
        val isStreetLightsOn = isNight || isSunset
        if (isStreetLightsOn) {
            val nightBuf = worldMeshes.worldNightEmissiveBuffer
            val nightCount = worldMeshes.worldNightEmissiveVertexCount
            if (nightBuf != null && nightCount > 0) {
                GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, vpMatrix, 0)
                GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, identityMatrix, 0)
                drawBuffer(nightBuf, nightCount)
            }
        }
        // Emissive Electric Sky Metro Train Headlights, Cabin Windows & Neon Stripes
        val metroEmissiveMeshes = cachedMetroCarMeshes
        if (metroEmissiveMeshes != null) {
            for (train in skyMetroSystem.trains) {
                if (train !== activeMetroTrain && !isVisibleInCamera(train.currentPosX, train.currentPosZ, metroMaxDistSq, padBack = 32f)) continue
                for (cIdx in 0..2) {
                    val pose = SkyMetroSystem.sampleTrackPose(train.progressMeters - METRO_CAR_OFFSETS[cIdx])
                    val renderYaw = if (METRO_CAR_FLIP_REAR[cIdx]) (pose.yaw + 180f) % 360f else pose.yaw
                    val isLeadStyle = METRO_CAR_IS_LEAD[cIdx]
                    val buf = if (isLeadStyle) metroEmissiveMeshes.leadEmissiveBuffer else metroEmissiveMeshes.coachEmissiveBuffer
                    val cnt = if (isLeadStyle) metroEmissiveMeshes.leadEmissiveCount else metroEmissiveMeshes.coachEmissiveCount
                    Matrix.setIdentityM(modelMatrix, 0)
                    Matrix.translateM(modelMatrix, 0, pose.x, pose.y, pose.z)
                    Matrix.rotateM(modelMatrix, 0, renderYaw, 0f, 1f, 0f)
                    Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
                    GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
                    GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)
                    drawBuffer(buf, cnt)
                }
            }
        }
        renderTrafficLights()

        // 6. Dynamic Street Lights along all avenues (Glow brightly at dusk & night)
        if (warmupStage >= 3) {
            renderStreetLights(isStreetLightsOn)
        }
        GLES20.glUniform1f(uUnlitEmissiveLoc, 0.0f)

        // 8. Active Mission Marker (Rotating 3D beacon)
        val target = activeMissionTargetPos
        if (target != null) {
            val timeSec = System.currentTimeMillis() / 1000f
            val bounceY = sin(timeSec * 3f) * 0.5f + 3f
            val rotAngle = (timeSec * 90f) % 360f

            val markerMesh = cachedMissionMarkerMesh ?: run {
                val mb = MeshBuilder(initialCapacityVertices = 128)
                val markerColor = floatArrayOf(0.98f, 0.85f, 0.1f, 1f) // Gold
                mb.addDome(0f, 0f, 0f, 1.2f, markerColor)
                mb.addPeakedRoof(0f, -1.8f, 0f, 1.5f, 1.8f, 1.5f, markerColor)
                Pair(mb.build(), mb.getVertexCount()).also { cachedMissionMarkerMesh = it }
            }

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, target.x, target.y + bounceY, target.z)
            Matrix.rotateM(modelMatrix, 0, rotAngle, 0f, 1f, 0f)

            Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(markerMesh.first, markerMesh.second)
        }
    }

    private fun updateProximityDoors(deltaSec: Float) {
        if (isInsideBuilding) return
        val activeVeh = currentVehicle
        val ax = activeVeh?.posX ?: playerPos.x
        val ay = activeVeh?.posY ?: playerPos.y
        val az = activeVeh?.posZ ?: playerPos.z
        val step = (deltaSec * 4.2f).coerceIn(0.01f, 0.45f)

        for (b in buildings) {
            if (b.isEnterable) {
                val dx = ax - b.doorPosX
                val dz = az - b.doorPosZ
                val target = if (dx * dx + dz * dz <= 6.0f * 6.0f && ay < 3.5f) 1f else 0f
                val cur = doorOpenProgress[b.id] ?: 0f
                if (cur != target) {
                    val next = if (cur < target) (cur + step).coerceAtMost(1f) else (cur - step).coerceAtLeast(0f)
                    doorOpenProgress[b.id] = next
                }
            }

            if (b.hasSingleCarGarage || b.type == BuildingType.RESIDENTIAL_VILLA) {
                val target = if (b.hasSingleCarGarage) {
                    val garX = b.posX + b.width / 2f + 2.2f
                    val frontZ = b.posZ + b.depth / 2f
                    val dx = ax - garX
                    val dz = az - frontZ
                    if ((dx * dx + dz * dz <= 11.5f * 11.5f || b.isPointInSingleCarGarageBay(ax, az, padding = 1.5f)) && ay < 4.5f) 1f else 0f
                } else {
                    val spec = b.getVillaExteriorSpec()
                    if (spec != null) {
                        val frontZ = spec.garZ + spec.garD / 2f
                        val dx = ax - spec.garX
                        val dz = az - frontZ
                        if (dx * dx + dz * dz <= 12.5f * 12.5f && ay < 5.5f) 1f else 0f
                    } else 0f
                }
                val cur = garageDoorOpenProgress[b.id] ?: 0f
                if (cur != target) {
                    val next = if (cur < target) (cur + step).coerceAtMost(1f) else (cur - step).coerceAtLeast(0f)
                    garageDoorOpenProgress[b.id] = next
                }
            }
        }
    }

    private fun renderProximityDoors() {
        val px = currentVehicle?.posX ?: playerPos.x
        val pz = currentVehicle?.posZ ?: playerPos.z
        val cellX = (px / 14f).toInt()
        val cellZ = (pz / 14f).toInt()
        val maxRenderDistSq = 95f * 95f

        // Compute signature from grid cell + quantized progress of nearby doors/garages
        var sig = (cellX.toLong() * 73856093L) xor (cellZ.toLong() * 19349663L)
        for ((id, open) in doorOpenProgress) {
            if (open > 0.001f) {
                sig = sig * 31L + id.hashCode().toLong() * 131L + (open * 100f).toInt().toLong()
            }
        }
        for ((id, open) in garageDoorOpenProgress) {
            if (open > 0.001f) {
                sig = sig * 31L + id.hashCode().toLong() * 257L + (open * 100f).toInt().toLong()
            }
        }

        var mesh = cachedDoorsMesh
        if (mesh == null || sig != lastDoorsSignature) {
            lastDoorsSignature = sig
            val mb = MeshBuilder(initialCapacityVertices = 2048)

            val glassDoorTint = floatArrayOf(0.58f, 0.80f, 0.94f, 1f)
            val glassDoorTrim = floatArrayOf(0.18f, 0.24f, 0.34f, 1f)
            val walnutWood = floatArrayOf(0.36f, 0.20f, 0.11f, 1f)
            val brassHandle = floatArrayOf(0.88f, 0.72f, 0.22f, 1f)
            val doorPanelWhite = floatArrayOf(0.92f, 0.93f, 0.95f, 1f)
            val frameDark = floatArrayOf(0.14f, 0.15f, 0.18f, 1f)

            for (b in buildings) {
                val dx = b.posX - px
                val dz = b.posZ - pz
                if (dx * dx + dz * dz > maxRenderDistSq) continue

                if (b.type == BuildingType.PARKING_LOT) {
                    val open = (doorOpenProgress[b.id] ?: 0f).coerceIn(0f, 1f)
                    val gateZ = b.posZ + b.depth / 2f + 1.0f
                    val armY = 1.08f + open * 2.15f
                    val armX = b.posX - 1.2f - open * 3.1f
                    val armW = (7.8f - open * 6.2f).coerceAtLeast(1.2f)
                    mb.addBox(armX, armY, gateZ, armW, 0.16f, 0.16f, floatArrayOf(0.95f, 0.15f, 0.15f, 1f))
                    if (armW > 2.2f) {
                        mb.addBox(armX + armW * 0.2f, armY + 0.01f, gateZ, 1.0f, 0.18f, 0.18f, floatArrayOf(0.96f, 0.96f, 0.98f, 1f))
                    }
                } else if (b.isEnterable && b.type != BuildingType.RESIDENTIAL_VILLA) {
                    val open = (doorOpenProgress[b.id] ?: 0f).coerceIn(0f, 1f)
                    val doorZ = b.posZ + b.depth / 2f + 0.08f
                    val slideOffset = 0.74f + open * 1.25f
                    val leftX = b.doorPosX - slideOffset
                    val rightX = b.doorPosX + slideOffset

                    mb.addBox(leftX, 1.80f, doorZ, 1.46f, 3.44f, 0.12f, glassDoorTint)
                    mb.addBox(rightX, 1.80f, doorZ, 1.46f, 3.44f, 0.12f, glassDoorTint)
                    mb.addBox(leftX + 0.62f, 1.65f, doorZ + 0.05f, 0.10f, 0.85f, 0.08f, glassDoorTrim)
                    mb.addBox(rightX - 0.62f, 1.65f, doorZ + 0.05f, 0.10f, 0.85f, 0.08f, glassDoorTrim)
                } else if (b.type == BuildingType.RESIDENTIAL_VILLA) {
                    val spec = b.getVillaExteriorSpec()
                    if (spec != null) {
                        val open = (doorOpenProgress[b.id] ?: 0f).coerceIn(0f, 1f)
                        val doorZ = b.posZ + b.depth / 2f + 0.07f
                        val leafW = spec.doorWidth * 0.49f
                        val closedOffset = leafW * 0.50f
                        val slideOffset = closedOffset + open * (leafW * 0.85f)
                        val leftX = b.posX - slideOffset
                        val rightX = b.posX + slideOffset
                        val doorCenterY = 0.32f + spec.doorHeight / 2f

                        mb.addBox(leftX, doorCenterY, doorZ, leafW, spec.doorHeight - 0.04f, 0.14f, walnutWood)
                        mb.addBox(rightX, doorCenterY, doorZ, leafW, spec.doorHeight - 0.04f, 0.14f, walnutWood)
                        val handleOff = leafW * 0.40f
                        mb.addBox(leftX + handleOff, 2.0f, doorZ + 0.07f, 0.09f, 0.80f, 0.08f, brassHandle)
                        mb.addBox(rightX - handleOff, 2.0f, doorZ + 0.07f, 0.09f, 0.80f, 0.08f, brassHandle)
                    }
                }

                if (b.hasSingleCarGarage) {
                    val open = (garageDoorOpenProgress[b.id] ?: 0f).coerceIn(0f, 1f)
                    val garX = b.posX + b.width / 2f + 2.2f
                    val frontZ = b.posZ + b.depth / 2f - 0.02f
                    val topY = 3.45f
                    val bottomY = 0.16f + open * 3.00f
                    val doorH = (topY - bottomY).coerceAtLeast(0.28f)
                    val centerY = bottomY + doorH / 2f

                    mb.addBox(garX, centerY, frontZ, 3.80f, doorH, 0.14f, doorPanelWhite)
                    mb.addBox(garX, bottomY + 0.06f, frontZ + 0.02f, 3.80f, 0.10f, 0.16f, frameDark)
                    if (doorH > 1.2f) {
                        mb.addBox(garX, centerY, frontZ + 0.02f, 3.76f, 0.06f, 0.15f, frameDark)
                    }
                } else if (b.type == BuildingType.RESIDENTIAL_VILLA) {
                    val spec = b.getVillaExteriorSpec()
                    if (spec != null) {
                        val open = (garageDoorOpenProgress[b.id] ?: 0f).coerceIn(0f, 1f)
                        val garX = spec.garX
                        val frontZ = spec.garZ + spec.garD / 2f + 0.04f
                        val topY = spec.garH - 0.48f
                        val openTravel = (topY - 0.55f).coerceAtLeast(1.0f)
                        val bottomY = 0.19f + open * openTravel
                        val doorH = (topY - bottomY).coerceAtLeast(0.32f)
                        val centerY = bottomY + doorH / 2f
                        val dw = spec.garBayDoorW

                        for (bayOffset in listOf(-spec.garBayOffset, spec.garBayOffset)) {
                            mb.addBox(garX + bayOffset, centerY, frontZ, dw, doorH, 0.16f, doorPanelWhite)
                            mb.addBox(garX + bayOffset, bottomY + 0.08f, frontZ + 0.02f, dw, 0.12f, 0.18f, frameDark)
                            if (doorH > 1.8f) {
                                mb.addBox(garX + bayOffset, centerY, frontZ + 0.02f, dw - 0.04f, 0.07f, 0.17f, frameDark)
                            }
                        }
                    }
                }
            }

            cachedDoorsMesh?.first?.let { releaseVbo(it) }
            mesh = Pair(mb.build(), mb.getVertexCount())
            cachedDoorsMesh = mesh
        }

        val vCount = mesh.second
        if (vCount > 0) {
            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, vpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, identityMatrix, 0)

            drawBuffer(mesh.first, vCount)
        }
    }

    /**
     * Renders the seamless sky dome, twinkling stars, lunar halo, and photorealistic cratered 3D Moon sphere
     * (or daytime Sun when unlocked), then clears the depth buffer so sky elements always sit behind mountains and towers.
     */
    private fun renderSkyAndCelestialBodies(isNight: Boolean) {
        GLES20.glUniform1f(uUnlitEmissiveLoc, 1.0f)
        GLES20.glUniform1f(uFogDensityLoc, 0.0f)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, camPosBuf[0], 0f, camPosBuf[2])
        Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

        if (isNight) {
            // 1. Seamless Night Sky Dome (Zenith midnight blue -> gentle horizon blue)
            val skyDome = cachedNightSkyDomeMesh ?: run {
                val mb = MeshBuilder(initialCapacityVertices = 4096)
                mb.addSkyDomeGradient(
                    0f, 0f, 0f, 1080f,
                    zenithColor = floatArrayOf(0.014f, 0.028f, 0.082f, 1f),
                    horizonColor = floatArrayOf(0.055f, 0.105f, 0.225f, 1f),
                    slices = 28,
                    stacks = 12
                )
                Pair(mb.build(), mb.getVertexCount()).also { cachedNightSkyDomeMesh = it }
            }
            GLES20.glDepthMask(false)
            drawBuffer(skyDome.first, skyDome.second)

            // 2. Multi-Layer Starfield with gentle natural twinkle across the upper sky
            if (cachedStarsSteadyMesh == null || cachedStarsTwinkleHighMesh == null || cachedStarsTwinkleSoftMesh == null) {
                val steadyMb = MeshBuilder(initialCapacityVertices = 4096)
                val twHighMb = MeshBuilder(initialCapacityVertices = 2048)
                val twSoftMb = MeshBuilder(initialCapacityVertices = 2048)
                val rng = java.util.Random(202607L)
                val starRadius = 960f
                for (s in 0 until 420) {
                    val u = rng.nextFloat() * 0.92f + 0.05f // y elevation above horizon
                    val theta = rng.nextFloat() * Math.PI.toFloat() * 2f
                    val horiz = kotlin.math.sqrt((1f - u * u).coerceAtLeast(0f))
                    val sx = cos(theta) * horiz * starRadius
                    val sy = u * starRadius
                    val sz = sin(theta) * horiz * starRadius
                    // Avoid overlapping Moon core spot (~-210, 210, -560)
                    val tint = when (s % 5) {
                        0 -> floatArrayOf(0.84f, 0.92f, 1.0f, 1f)   // Blue-white star
                        1 -> floatArrayOf(1.0f, 0.96f, 0.84f, 1f)   // Warm cream star
                        2 -> floatArrayOf(0.75f, 0.86f, 1.0f, 1f)   // Pale azure star
                        else -> floatArrayOf(0.98f, 0.99f, 1.0f, 1f) // Pure white star
                    }
                    val size = if (s % 9 == 0) 2.6f else if (s % 3 == 0) 1.8f else 1.25f
                    if (s % 3 == 0) {
                        twHighMb.addBox(sx, sy, sz, size, size, size, tint)
                        val softTint = floatArrayOf(tint[0] * 0.45f, tint[1] * 0.45f, tint[2] * 0.52f, 1f)
                        twSoftMb.addBox(sx, sy, sz, size * 0.72f, size * 0.72f, size * 0.72f, softTint)
                    } else {
                        steadyMb.addBox(sx, sy, sz, size, size, size, tint)
                    }
                }
                cachedStarsSteadyMesh = Pair(steadyMb.build(), steadyMb.getVertexCount())
                cachedStarsTwinkleHighMesh = Pair(twHighMb.build(), twHighMb.getVertexCount())
                cachedStarsTwinkleSoftMesh = Pair(twSoftMb.build(), twSoftMb.getVertexCount())
            }

            GLES20.glDisable(GLES20.GL_CULL_FACE)
            cachedStarsSteadyMesh?.let { drawBuffer(it.first, it.second) }
            val twinklePulse = ((System.currentTimeMillis() / 900L) % 2L == 0L)
            val activeTwinkle = if (twinklePulse) cachedStarsTwinkleHighMesh else cachedStarsTwinkleSoftMesh
            activeTwinkle?.let { drawBuffer(it.first, it.second) }

            // 3. Soft Atmospheric Moon Halo + Complete Photorealistic Cratered 3D Moon Sphere
            val moonX = -215f
            val moonY = 205f
            val moonZ = -620f
            val moonRadius = 34f

            val moonHalo = cachedMoonHaloMesh ?: run {
                val hmb = MeshBuilder(initialCapacityVertices = 1024)
                hmb.addSphere(moonX, moonY, moonZ, moonRadius * 1.45f, floatArrayOf(0.76f, 0.84f, 0.98f, 0.16f), slices = 20, stacks = 12)
                hmb.addSphere(moonX, moonY, moonZ, moonRadius * 2.15f, floatArrayOf(0.55f, 0.68f, 0.92f, 0.07f), slices = 20, stacks = 12)
                Pair(hmb.build(), hmb.getVertexCount()).also { cachedMoonHaloMesh = it }
            }
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            drawBuffer(moonHalo.first, moonHalo.second)
            GLES20.glDisable(GLES20.GL_BLEND)

            // Draw full realistic 3D Moon sphere cleanly on stage >= 2
            if (warmupStage >= 2) {
                val moonSphere = cachedMoonSphereMesh ?: run {
                    val mmb = MeshBuilder(initialCapacityVertices = 4096)
                    mmb.addRealisticMoonSphere(moonX, moonY, moonZ, moonRadius, slices = 32, stacks = 22)
                    Pair(mmb.build(), mmb.getVertexCount()).also { cachedMoonSphereMesh = it }
                }
                GLES20.glEnable(GLES20.GL_CULL_FACE)
                GLES20.glDepthMask(true)
                drawBuffer(moonSphere.first, moonSphere.second)
            }
        } else {
            // Daytime / Sunset Sun (never rendered at night)
            val bucket = (timeOfDayHours * 25f).toInt()
            var sunMesh = cachedSunMesh
            if (sunMesh == null || bucket != lastSunBucket) {
                lastSunBucket = bucket
                val qHours = bucket / 25f
                val mb = MeshBuilder(initialCapacityVertices = 1024)
                val sunAngle = ((qHours - 6f) / 24f) * Math.PI.toFloat() * 2f
                val skyRadius = 720f
                val sunX = cos(sunAngle) * skyRadius
                val sunY = (sin(sunAngle) * skyRadius).coerceAtLeast(-20f)
                val sunZ = -180f
                if (sunY > -15f) {
                    mb.addSphere(sunX, sunY, sunZ, 38f, floatArrayOf(1.0f, 0.98f, 0.85f, 1.0f), slices = 18, stacks = 12)
                }
                cachedSunMesh?.first?.let { releaseVbo(it) }
                sunMesh = Pair(mb.build(), mb.getVertexCount())
                cachedSunMesh = sunMesh
            }
            if (sunMesh.second > 0) {
                GLES20.glDepthMask(false)
                drawBuffer(sunMesh.first, sunMesh.second)
                GLES20.glDepthMask(true)
            }
        }

        // Clear depth buffer so skybox, stars, and Moon sit infinitely behind all city mountains, towers, and sea
        GLES20.glDepthMask(true)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUniform1f(uFogDensityLoc, activeFogDensity)
        GLES20.glUniform1f(uUnlitEmissiveLoc, 0.0f)
    }

    private fun renderTrafficLights() {
        var sig = if (trafficSystem.allStopActive) 1L else 0L
        for (inter in trafficSystem.intersections) {
            sig = sig * 31L + (inter.phase.ordinal + 1).toLong()
        }

        var mesh = cachedTrafficLightsMesh
        if (mesh == null || sig != lastTrafficLightsSignature) {
            lastTrafficLightsSignature = sig
            val mb = MeshBuilder(initialCapacityVertices = 4096)

            for (inter in trafficSystem.intersections) {
                val isNsGreen = inter.isNsGreen()
                val isNsYellow = inter.isNsYellow()
                val isNsRed = !isNsGreen && !isNsYellow

                val isEwGreen = inter.isEwGreen()
                val isEwYellow = inter.isEwYellow()
                val isEwRed = !isEwGreen && !isEwYellow

                val nsRed = if (isNsRed) floatArrayOf(1.0f, 0.1f, 0.1f, 1f) else floatArrayOf(0.22f, 0.04f, 0.04f, 1f)
                val nsYellow = if (isNsYellow) floatArrayOf(1.0f, 0.85f, 0.05f, 1f) else floatArrayOf(0.22f, 0.18f, 0.04f, 1f)
                val nsGreen = if (isNsGreen) floatArrayOf(0.1f, 1.0f, 0.25f, 1f) else floatArrayOf(0.04f, 0.22f, 0.06f, 1f)

                val ewRed = if (isEwRed) floatArrayOf(1.0f, 0.1f, 0.1f, 1f) else floatArrayOf(0.22f, 0.04f, 0.04f, 1f)
                val ewYellow = if (isEwYellow) floatArrayOf(1.0f, 0.85f, 0.05f, 1f) else floatArrayOf(0.22f, 0.18f, 0.04f, 1f)
                val ewGreen = if (isEwGreen) floatArrayOf(0.1f, 1.0f, 0.25f, 1f) else floatArrayOf(0.04f, 0.22f, 0.06f, 1f)

                // North-East (+7.2, +7.2) and South-West (-7.2, -7.2) intersection corner signal heads
                val c1x = inter.x + 7.2f
                val c1z = inter.z + 7.2f
                mb.addBox(c1x, 6.7f, c1z + 0.39f, 0.42f, 0.40f, 0.12f, nsRed)
                mb.addBox(c1x, 6.2f, c1z + 0.39f, 0.42f, 0.40f, 0.12f, nsYellow)
                mb.addBox(c1x, 5.7f, c1z + 0.39f, 0.42f, 0.40f, 0.12f, nsGreen)
                mb.addBox(c1x, 6.7f, c1z - 0.39f, 0.42f, 0.40f, 0.12f, nsRed)
                mb.addBox(c1x, 6.2f, c1z - 0.39f, 0.42f, 0.40f, 0.12f, nsYellow)
                mb.addBox(c1x, 5.7f, c1z - 0.39f, 0.42f, 0.40f, 0.12f, nsGreen)
                mb.addBox(c1x + 0.39f, 6.7f, c1z, 0.12f, 0.40f, 0.42f, ewRed)
                mb.addBox(c1x + 0.39f, 6.2f, c1z, 0.12f, 0.40f, 0.42f, ewYellow)
                mb.addBox(c1x + 0.39f, 5.7f, c1z, 0.12f, 0.40f, 0.42f, ewGreen)
                mb.addBox(c1x - 0.39f, 6.7f, c1z, 0.12f, 0.40f, 0.42f, ewRed)
                mb.addBox(c1x - 0.39f, 6.2f, c1z, 0.12f, 0.40f, 0.42f, ewYellow)
                mb.addBox(c1x - 0.39f, 5.7f, c1z, 0.12f, 0.40f, 0.42f, ewGreen)

                val c2x = inter.x - 7.2f
                val c2z = inter.z - 7.2f
                mb.addBox(c2x, 6.7f, c2z + 0.39f, 0.42f, 0.40f, 0.12f, nsRed)
                mb.addBox(c2x, 6.2f, c2z + 0.39f, 0.42f, 0.40f, 0.12f, nsYellow)
                mb.addBox(c2x, 5.7f, c2z + 0.39f, 0.42f, 0.40f, 0.12f, nsGreen)
                mb.addBox(c2x, 6.7f, c2z - 0.39f, 0.42f, 0.40f, 0.12f, nsRed)
                mb.addBox(c2x, 6.2f, c2z - 0.39f, 0.42f, 0.40f, 0.12f, nsYellow)
                mb.addBox(c2x, 5.7f, c2z - 0.39f, 0.42f, 0.40f, 0.12f, nsGreen)
                mb.addBox(c2x + 0.39f, 6.7f, c2z, 0.12f, 0.40f, 0.42f, ewRed)
                mb.addBox(c2x + 0.39f, 6.2f, c2z, 0.12f, 0.40f, 0.42f, ewYellow)
                mb.addBox(c2x + 0.39f, 5.7f, c2z, 0.12f, 0.40f, 0.42f, ewGreen)
                mb.addBox(c2x - 0.39f, 6.7f, c2z, 0.12f, 0.40f, 0.42f, ewRed)
                mb.addBox(c2x - 0.39f, 6.2f, c2z, 0.12f, 0.40f, 0.42f, ewYellow)
                mb.addBox(c2x - 0.39f, 5.7f, c2z, 0.12f, 0.40f, 0.42f, ewGreen)
            }

            cachedTrafficLightsMesh?.first?.let { releaseVbo(it) }
            mesh = Pair(mb.build(), mb.getVertexCount())
            cachedTrafficLightsMesh = mesh
        }

        if (mesh.second > 0) {
            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, vpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, identityMatrix, 0)

            drawBuffer(mesh.first, mesh.second)
        }
    }

    private fun ensureStreetLightsCached(isLightsOn: Boolean) {
        if (!isLightsOn && cachedDayBulbsMesh != null) return
        if (isLightsOn && cachedNightBulbsMesh != null) return

        val bulbsMb = MeshBuilder(initialCapacityVertices = 4096)
        val glowMb = if (isLightsOn) MeshBuilder(initialCapacityVertices = 8192) else null

        val bulbColor = if (isLightsOn) {
            floatArrayOf(1.0f, 0.96f, 0.62f, 1.0f)
        } else {
            floatArrayOf(0.85f, 0.88f, 0.92f, 1.0f)
        }
        val streetGlowPool = floatArrayOf(1.0f, 0.90f, 0.45f, 0.44f)
        val lightConeColor = floatArrayOf(1.0f, 0.94f, 0.60f, 0.12f)

        fun addLampLight(lx: Float, lz: Float, poolX: Float, poolZ: Float, poolW: Float, poolD: Float) {
            bulbsMb.addBox(lx, 7.5f, lz, 0.80f, 0.36f, 0.80f, bulbColor)
            if (glowMb != null) {
                glowMb.addBox(poolX, 0.095f, poolZ, poolW, 0.015f, poolD, streetGlowPool)
                glowMb.addPeakedRoof(lx, 0.25f, lz, 6.2f, 7.2f, 6.2f, lightConeColor)
            }
        }

        val allNsAvenues = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
        for (x in allNsAvenues) {
            val maxZ = if (x == 0f || x == -195f) 336 else 160
            for (z in -176..maxZ step 32) {
                addLampLight(x + 7.6f - 1.3f, z.toFloat(), x + 3.0f, z.toFloat(), 5.8f, 18.0f)
                addLampLight(x - 7.6f + 1.3f, z.toFloat(), x - 3.0f, z.toFloat(), 5.8f, 18.0f)
            }
        }

        val allEwBoulevards = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)
        for (z in allEwBoulevards) {
            val maxLampX = if (z == 175f) 335 else 190
            for (x in -190..maxLampX step 40) {
                addLampLight(x.toFloat(), z + 7.6f - 1.3f, x.toFloat(), z + 1.5f, 22.0f, 9.0f)
                addLampLight(x.toFloat(), z - 7.6f + 1.3f, x.toFloat(), z - 1.5f, 22.0f, 9.0f)
            }
        }

        for (z in listOf(256f, 340f)) {
            for (x in -190..340 step 40) {
                addLampLight(x.toFloat(), z + 7.6f - 1.3f, x.toFloat(), z + 1.5f, 22.0f, 9.0f)
                addLampLight(x.toFloat(), z - 7.6f + 1.3f, x.toFloat(), z - 1.5f, 22.0f, 9.0f)
            }
        }

        for (z in 192..320 step 32) {
            addLampLight(345f + 7.6f - 1.3f, z.toFloat(), 345f + 3.0f, z.toFloat(), 5.8f, 18.0f)
            addLampLight(345f - 7.6f + 1.3f, z.toFloat(), 345f - 3.0f, z.toFloat(), 5.8f, 18.0f)
        }

        for (px in listOf(11f, 24f)) {
            for (pz in listOf(10f, 26f)) {
                bulbsMb.addBox(px, 6.2f, pz, 1.4f, 0.34f, 0.85f, bulbColor)
                if (glowMb != null) {
                    glowMb.addBox(px, 0.14f, pz, 11.0f, 0.02f, 11.0f, streetGlowPool)
                    glowMb.addPeakedRoof(px, 0.15f, pz, 8.0f, 6.0f, 8.0f, lightConeColor)
                }
            }
        }

        val bulbsPair = Pair(bulbsMb.build(), bulbsMb.getVertexCount())
        if (isLightsOn) {
            cachedNightBulbsMesh = bulbsPair
            cachedNightGlowMesh = if (glowMb != null && glowMb.getVertexCount() > 0) {
                Pair(glowMb.build(), glowMb.getVertexCount())
            } else null
        } else {
            cachedDayBulbsMesh = bulbsPair
        }
    }

    private fun renderStreetLights(isLightsOn: Boolean) {
        ensureStreetLightsCached(isLightsOn)
        val bulbsPair = if (isLightsOn) cachedNightBulbsMesh else cachedDayBulbsMesh
        if (bulbsPair == null || bulbsPair.second == 0) return

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, vpMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, identityMatrix, 0)

        // Pass 1: Solid opaque bright lamp bulbs
        drawBuffer(bulbsPair.first, bulbsPair.second)

        // Pass 2: Soft translucent golden street light pools & cones at night (skipped in LOW ultra-smooth mode to cut overdraw)
        val glowPair = if (isLightsOn && graphicsQuality != "LOW") cachedNightGlowMesh else null
        if (glowPair != null && glowPair.second > 0) {
            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            GLES20.glDepthMask(false)
            drawBuffer(glowPair.first, glowPair.second)
            GLES20.glDepthMask(true)
            GLES20.glDisable(GLES20.GL_BLEND)
        }
    }

    private fun renderInterior() {
        if (cachedInteriorType != currentInteriorType || interiorMeshCache == null) {
            interiorMeshCache?.first?.let { releaseVbo(it) }
            interiorMeshCache = worldMeshes.buildInteriorMesh(currentInteriorType)
            cachedInteriorType = currentInteriorType
        }

        val cache = interiorMeshCache ?: return

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(tempMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, tempMatrix, 0)
        GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

        drawBuffer(cache.first, cache.second)

        // Render all cars parked inside the Underground City Parking Garage!
        if (currentInteriorType == InteriorType.UNDERGROUND_GARAGE) {
            for (veh in vehicles) {
                if (!veh.isInUndergroundGarage) continue
                val mesh = worldMeshes.getVehicleMesh(
                    veh.type,
                    veh.colorPrimary,
                    veh.colorSecondary,
                    veh.meshStyle,
                    veh.isNitroActive,
                    veh.underglowColor
                )
                Matrix.setIdentityM(modelMatrix, 0)
                Matrix.translateM(modelMatrix, 0, veh.posX, veh.posY, veh.posZ)
                Matrix.rotateM(modelMatrix, 0, veh.yaw, 0f, 1f, 0f)
                Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
                Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
                GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
                GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)
                drawBuffer(mesh.first, mesh.second)
            }
        }

        // Draw player character OR controlled Cyber Robot inside room (when not seated inside a car)
        if (!isPlayerInVehicle) {
            val charMesh = if (isControllingRobot) {
                worldMeshes.buildRobotMesh(characterWalkPhase, 0f, controlledRobotVisorColor)
            } else {
                worldMeshes.buildCharacterMesh(characterWalkPhase, false, playerOutfit)
            }
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, interiorPlayerPos.x, interiorPlayerPos.y, interiorPlayerPos.z)
            Matrix.rotateM(modelMatrix, 0, interiorPlayerYaw, 0f, 1f, 0f)

            Matrix.multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, tempMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, mvpMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            drawBuffer(charMesh.first, charMesh.second)
        }

        // Draw still translucent indoor pool water after character so swimmer stands visibly inside water
        val waterBuf = worldMeshes.interiorWaterBuffer
        val waterCount = worldMeshes.interiorWaterVertexCount
        if (waterBuf != null && waterCount > 0) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(tempMatrix, 0, mvpMatrix, 0, modelMatrix, 0)

            GLES20.glUniformMatrix4fv(uMVPMatrixLoc, 1, false, tempMatrix, 0)
            GLES20.glUniformMatrix4fv(uModelMatrixLoc, 1, false, modelMatrix, 0)

            GLES20.glEnable(GLES20.GL_BLEND)
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
            GLES20.glDepthMask(false)
            drawBuffer(waterBuf, waterCount)
            GLES20.glDepthMask(true)
            GLES20.glDisable(GLES20.GL_BLEND)
        }
    }

    private fun releaseVbo(buffer: FloatBuffer) {
        val vboId = vboCache.remove(buffer) ?: return
        if (boundVboId == vboId) {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
            boundVboId = 0
        }
        vboIdScratch[0] = vboId
        GLES20.glDeleteBuffers(1, vboIdScratch, 0)
    }

    private fun getOrCreateVbo(buffer: FloatBuffer, vertexCount: Int): Int {
        vboCache[buffer]?.let { return it }

        // Prevent unbounded VRAM growth if many transient meshes are generated over a long session
        if (vboCache.size >= MAX_VBO_CACHE_SIZE) {
            val ids = IntArray(vboCache.size)
            var idx = 0
            for (id in vboCache.values) {
                ids[idx++] = id
            }
            if (boundVboId != 0) {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                boundVboId = 0
            }
            GLES20.glDeleteBuffers(idx, ids, 0)
            vboCache.clear()
        }

        GLES20.glGenBuffers(1, vboIdScratch, 0)
        val vboId = vboIdScratch[0]
        if (vboId == 0) return 0

        buffer.position(0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId)
        boundVboId = vboId
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            vertexCount * 10 * 4,
            buffer,
            GLES20.GL_STATIC_DRAW
        )
        vboCache[buffer] = vboId
        return vboId
    }

    private fun drawBuffer(buffer: FloatBuffer, vertexCount: Int) {
        if (vertexCount <= 0) return
        val stride = 10 * 4 // 10 floats = 40 bytes
        val vboId = getOrCreateVbo(buffer, vertexCount)
        if (vboId != 0) {
            if (boundVboId != vboId) {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId)
                boundVboId = vboId
            }
            GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, stride, 0)
            GLES20.glVertexAttribPointer(aNormalLoc, 3, GLES20.GL_FLOAT, false, stride, 3 * 4)
            GLES20.glVertexAttribPointer(aColorLoc, 4, GLES20.GL_FLOAT, false, stride, 6 * 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        } else {
            if (boundVboId != 0) {
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
                boundVboId = 0
            }
            buffer.position(0)
            GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, stride, buffer)
            buffer.position(3)
            GLES20.glVertexAttribPointer(aNormalLoc, 3, GLES20.GL_FLOAT, false, stride, buffer)
            buffer.position(6)
            GLES20.glVertexAttribPointer(aColorLoc, 4, GLES20.GL_FLOAT, false, stride, buffer)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        }
    }

    fun getBuildings(): List<Building> = buildings
}
