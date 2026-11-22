package com.example.opencity.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.opencity.model.CarMeshStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.R
import com.example.opencity.audio.SoundManager
import com.example.opencity.data.GameSaveManager
import com.example.opencity.engine.CameraMode
import com.example.opencity.engine.CityFilter
import com.example.opencity.engine.CityGLSurfaceView
import com.example.opencity.engine.Weather
import com.example.opencity.model.Building
import com.example.opencity.model.BuildingInterior
import com.example.opencity.model.BuildingType
import com.example.opencity.model.CityData
import com.example.opencity.model.ClothingBrand
import com.example.opencity.model.ClothingCatalog
import com.example.opencity.model.ClothingSlot
import com.example.opencity.model.CompoundAvenue90
import com.example.opencity.model.DealershipCar
import com.example.opencity.model.ElectricMetroTrain
import com.example.opencity.model.HouseSideQuest
import com.example.opencity.model.InteriorType
import com.example.opencity.model.MetroSeat
import com.example.opencity.model.MetroStation
import com.example.opencity.model.Mission
import com.example.opencity.model.MissionData
import com.example.opencity.model.MissionStatus
import com.example.opencity.model.MotorCityData
import com.example.opencity.model.SkyMetroSystem
import com.example.opencity.model.StreetZone
import com.example.opencity.model.Vector3
import com.example.opencity.model.Vehicle
import com.example.opencity.model.VehicleType
import androidx.compose.runtime.withFrameNanos
import com.example.opencity.engine.CityWorldInitData
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun GameScreen(
    saveManager: GameSaveManager,
    soundManager: SoundManager,
    onBackToMenu: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var glSurfaceView by remember { mutableStateOf<CityGLSurfaceView?>(null) }
    var prebuiltWorldInit by remember { mutableStateOf<Pair<CityWorldInitData, Pair<Float, Float>>?>(null) }
    val gameFocusRequester = remember { FocusRequester() }

    // Wait for Frame 0 window/insets sync to settle cleanly, then build world data off Main Thread
    LaunchedEffect(Unit) {
        withFrameNanos { }
        val prepared = withContext(Dispatchers.Default) {
            val world = CityWorldInitData.buildOffMainThread()
            var sx = saveManager.savedPlayerPosX
            var sz = saveManager.savedPlayerPosZ
            if (sx !in -252f..400f || sz !in -234f..390f) {
                sx = 8.0f
                sz = 6.5f
            }
            for (b in world.buildings) {
                b.resolveCollisionPush3D(sx, 0f, sz, margin = 0.85f)?.let { pushed ->
                    sx = pushed.first
                    sz = pushed.second
                }
            }
            Pair(world, Pair(sx, sz))
        }
        prebuiltWorldInit = prepared
        withFrameNanos { }
        runCatching { gameFocusRequester.requestFocus() }
    }

    // Player & Game State
    var playerMoney by remember { mutableIntStateOf(saveManager.playerMoney) }
    var playerName by remember { mutableStateOf(saveManager.playerName) }
    var characterMotto by remember { mutableStateOf(saveManager.characterMotto) }
    var isDriving by remember { mutableStateOf(false) }
    var currentCar by remember { mutableStateOf<Vehicle?>(null) }
    var isInsideBuilding by remember { mutableStateOf(false) }
    var currentInteriorType by remember { mutableStateOf(InteriorType.NONE) }
    var currentVillaFloor by remember { mutableIntStateOf(1) }

    var cameraMode by remember { mutableStateOf(CameraMode.THIRD_PERSON) }
    var isRunning by remember { mutableStateOf(false) }
    var graphicsQuality by remember { mutableStateOf(saveManager.graphicsQuality) }

    // Joystick & Circle-in-Circle analog input
    var joyX by remember { mutableFloatStateOf(0f) }
    var joyY by remember { mutableFloatStateOf(0f) }
    var circleControlX by remember { mutableFloatStateOf(0f) }
    var circleControlY by remember { mutableFloatStateOf(0f) }

    // Driving direct controls
    var driveThrottle by remember { mutableFloatStateOf(0f) }
    var driveSteer by remember { mutableFloatStateOf(0f) }

    // Proximity helpers
    var nearbyBuildingForDoor by remember { mutableStateOf<Building?>(null) }
    var nearbyVehicleForEntry by remember { mutableStateOf<Vehicle?>(null) }
    var nearbyDealershipCar by remember { mutableStateOf<DealershipCar?>(null) }
    var nearbyAutonomousRobot by remember { mutableStateOf<com.example.opencity.model.AutonomousRobot?>(null) }
    var suppressedAutoDoorId by remember { mutableStateOf<String?>(null) }
    var lastEnteredBuildingId by remember { mutableStateOf<String?>(null) }
    var lastProximityDoorSoundId by remember { mutableStateOf<String?>(null) }
    var lastProximityGarageSoundId by remember { mutableStateOf<String?>(null) }

    // Dialog toggles
    var showFullMap by remember { mutableStateOf(false) }
    var showMissions by remember { mutableStateOf(false) }
    var showTravelTerminal by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showTrafficControl by remember { mutableStateOf(false) }
    var showNameEditDialog by remember { mutableStateOf(false) }
    var showMotorCityDialog by remember { mutableStateOf(false) }
    var showVillasFoodWaterDialog by remember { mutableStateOf(false) }
    var showTalabatFoodDialog by remember { mutableStateOf(false) }
    var showClothingStoreDialog by remember { mutableStateOf(false) }
    var showGrandMallDialog by remember { mutableStateOf(false) }
    var grandMallInitialTab by remember { mutableStateOf(MallShopTab.VEGETABLES) }
    var isControllingRobot by remember { mutableStateOf(false) }
    var clothingInitialTab by remember { mutableIntStateOf(0) }
    var clothingInitialBrand by remember { mutableStateOf<ClothingBrand?>(null) }
    var clothingInitialSlot by remember { mutableStateOf<ClothingSlot?>(null) }
    var ownedClothingIds by remember { mutableStateOf(saveManager.ownedClothingIds) }
    var currentOutfit by remember { mutableStateOf(saveManager.getEquippedOutfit()) }
    var inspectedDealershipCar by remember { mutableStateOf<DealershipCar?>(null) }
    var isTestDriving by remember { mutableStateOf(false) }
    var ownedCarIds by remember { mutableStateOf(saveManager.ownedCarIds) }
    var isNitroEngaged by remember { mutableStateOf(false) }
    var isHandbrakeEngaged by remember { mutableStateOf(false) }
    var showGamerBloxColorSheet by remember { mutableStateOf(false) }
    var showGamerBloxShowcaseDialog by remember { mutableStateOf(false) }
    var showCarCustomizationDialog by remember { mutableStateOf(false) }
    var currentCarNitroFuel by remember { mutableFloatStateOf(100f) }
    var currentCarFuel by remember { mutableFloatStateOf(100f) }
    var totalDriveCashEarned by remember { mutableIntStateOf(0) }
    var currentCarSpeedKmH by remember { mutableIntStateOf(0) }
    var currentCarWheelRotations by remember { mutableLongStateOf(0L) }
    var currentCarHighSpeedRotations by remember { mutableLongStateOf(0L) }
    var currentCarDistanceMeters by remember { mutableIntStateOf(0) }
    var currentWheelTurnPrice by remember { mutableIntStateOf(10) }
    var drivingRewardToast by remember { mutableStateOf<String?>(null) }
    var driveMoneyTimer by remember { mutableFloatStateOf(0f) }
    var isNearGasStation by remember { mutableStateOf(false) }
    var isNearParkingLot by remember { mutableStateOf(false) }
    var playerVelocityY by remember { mutableFloatStateOf(0f) }
    var interiorVelocityY by remember { mutableFloatStateOf(0f) }
    var flightClimbInput by remember { mutableFloatStateOf(0f) }
    var isSwimmingNow by remember { mutableStateOf(false) }
    var nearbyRooftopHouse by remember { mutableStateOf<Building?>(null) }
    var hoveredRooftopHouse by remember { mutableStateOf<Building?>(null) }
    var currentTimeOfDayHours by remember { mutableFloatStateOf(22.5f) }
    var isNightLocked by remember { mutableStateOf(true) }
    var currentCityFilter by remember { mutableStateOf(CityFilter.VIVID) }
    var showCityFiltersStrip by remember { mutableStateOf(true) }
    val pressedKeys = remember { mutableStateMapOf<Long, Boolean>() }

    // Police vs Robbers Event («حدث شرطة وحرامية»)
    var isPoliceEventActive by remember { mutableStateOf(false) }
    var policeEventRole by remember { mutableStateOf<String?>(null) }
    var policeEventTimer by remember { mutableFloatStateOf(45f) }
    var eventPoliceVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var eventRobberVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var showPoliceEventDialog by remember { mutableStateOf(false) }

    // Real Estate, Houses, and Bed Sleeping («بيوت وعقارات ونوم بالسرير»)
    var showHousingDialog by remember { mutableStateOf(false) }
    var ownedVillaIds by remember { mutableStateOf(saveManager.ownedVillaIds) }
    var isVillaOwned by remember { mutableStateOf(saveManager.isVillaOwned("villa_player")) }
    var isApartmentRented by remember { mutableStateOf(false) }

    // Elevated Electric Sky Metro & 90 Avenue Compound Water («مترو المدينة المعلق وبحر ومسبح 90 أفينيو»)
    var metroTicketsCount by remember { mutableIntStateOf(saveManager.metroTicketsCount) }
    var showMetroTicketDialog by remember { mutableStateOf(false) }
    var showMetroCardShopDialog by remember { mutableStateOf(false) }
    var metroCardsSoldCount by remember { mutableIntStateOf(saveManager.metroCardsSoldCount) }
    var metroShopTotalRevenue by remember { mutableIntStateOf(saveManager.metroShopTotalRevenue) }
    var metroCardStockCount by remember { mutableIntStateOf(saveManager.metroCardStockCount) }
    var nearbyTurnstileStation by remember { mutableStateOf<MetroStation?>(null) }
    var standingMetroStation by remember { mutableStateOf<MetroStation?>(null) }
    var boardableMetroTrain by remember { mutableStateOf<ElectricMetroTrain?>(null) }
    var isRidingMetro by remember { mutableStateOf(false) }
    var activeMetroTrain by remember { mutableStateOf<ElectricMetroTrain?>(null) }
    var selectedMetroSeat by remember { mutableStateOf<MetroSeat?>(null) }
    var isInCompoundSeaNow by remember { mutableStateOf(false) }
    var isInCompoundPoolNow by remember { mutableStateOf(false) }

    // 4-Street Zones, Door Interactions, Keys & Side Quests
    var currentStreetZone by remember { mutableStateOf<StreetZone?>(null) }
    var nearbyGarageBuilding by remember { mutableStateOf<Building?>(null) }
    var isInsideGarageBay by remember { mutableStateOf(false) }
    var isNearbyGarageOccupiedByOther by remember { mutableStateOf(false) }
    var wasInsideGarageBay by remember { mutableStateOf(false) }

    var undergroundTransitionCooldown by remember { mutableFloatStateOf(0f) }

    fun isSingleCarGarageOccupiedByOther(
        building: Building,
        excludeCar: Vehicle?,
        renderer: com.example.opencity.engine.CityRenderer?
    ): Boolean {
        if (!building.hasSingleCarGarage || renderer == null) return false
        for (v in renderer.vehicles) {
            if (v !== excludeCar && !v.isInUndergroundGarage && building.isPointInSingleCarGarageBay(v.posX, v.posZ, padding = 0.7f)) {
                return true
            }
        }
        for (v in renderer.trafficSystem.allActiveTrafficVehicles()) {
            if (v !== excludeCar && !v.isInUndergroundGarage && building.isPointInSingleCarGarageBay(v.posX, v.posZ, padding = 0.7f)) {
                return true
            }
        }
        return false
    }

    fun isPointOnCityRoad(px: Float, pz: Float): Boolean {
        val nsRoads = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f, 345f)
        val ewRoads = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f, 256f, 340f)
        return nsRoads.any { rx -> kotlin.math.abs(px - rx) <= 7.2f } ||
            ewRoads.any { rz -> kotlin.math.abs(pz - rz) <= 7.2f }
    }

    fun parkCarInUndergroundGarage(
        car: Vehicle,
        renderer: com.example.opencity.engine.CityRenderer?
    ): Int {
        val bayZPositions = listOf(-16.5f, -11.5f, -6.5f, -1.5f, 3.5f, 8.5f, 13.5f)
        val bays = mutableListOf<Triple<Float, Float, Float>>()
        for (bz in bayZPositions.reversed()) bays.add(Triple(12.0f, bz, 270f))
        for (bz in bayZPositions.reversed()) bays.add(Triple(-12.0f, bz, 90f))

        var chosenIdx = bays.size - 1
        var chosenBay = bays.first()
        for ((idx, bay) in bays.withIndex()) {
            val occupied = renderer?.vehicles?.any { v ->
                v !== car && v.isInUndergroundGarage &&
                    kotlin.math.abs(v.posX - bay.first) < 3.2f &&
                    kotlin.math.abs(v.posZ - bay.second) < 3.2f
            } == true
            if (!occupied) {
                chosenIdx = idx
                chosenBay = bay
                break
            }
        }

        car.posX = chosenBay.first
        car.posY = 0f
        car.posZ = chosenBay.second
        car.yaw = chosenBay.third
        car.speed = 0f
        car.pitchAngle = 0f
        car.isPlayerDriving = false
        car.isInUndergroundGarage = true
        return chosenIdx + 1
    }

    fun resetControlInputs() {
        pressedKeys.clear()
        circleControlX = 0f
        circleControlY = 0f
        joyX = 0f
        joyY = 0f
        driveThrottle = 0f
        driveSteer = 0f
        isHandbrakeEngaged = false
        isNitroEngaged = false
        flightClimbInput = 0f
    }

    fun syncKeyboardControls() {
        val fwd = pressedKeys[Key.W.keyCode] == true || pressedKeys[Key.DirectionUp.keyCode] == true
        val bwd = pressedKeys[Key.S.keyCode] == true || pressedKeys[Key.DirectionDown.keyCode] == true
        val left = pressedKeys[Key.A.keyCode] == true || pressedKeys[Key.DirectionLeft.keyCode] == true
        val right = pressedKeys[Key.D.keyCode] == true || pressedKeys[Key.DirectionRight.keyCode] == true
        val shift = pressedKeys[Key.ShiftLeft.keyCode] == true || pressedKeys[Key.ShiftRight.keyCode] == true
        val space = pressedKeys[Key.Spacebar.keyCode] == true
        val climbUp = pressedKeys[Key.Q.keyCode] == true || pressedKeys[Key.PageUp.keyCode] == true
        val climbDown = pressedKeys[Key.Z.keyCode] == true || pressedKeys[Key.PageDown.keyCode] == true

        val targetY = (if (fwd) 1f else 0f) - (if (bwd) 1f else 0f)
        val targetX = (if (right) 1f else 0f) - (if (left) 1f else 0f)
        val hasCircleInput = circleControlX != 0f || circleControlY != 0f

        if (isDriving) {
            driveThrottle = if (targetY != 0f) targetY else if (hasCircleInput) circleControlY else 0f
            driveSteer = if (targetX != 0f) targetX else if (hasCircleInput) circleControlX else 0f
            isHandbrakeEngaged = space
            isNitroEngaged = shift && currentCarNitroFuel > 1f
            flightClimbInput = (if (climbUp) 1f else 0f) - (if (climbDown) 1f else 0f)
        } else {
            joyX = if (targetX != 0f) targetX else circleControlX
            joyY = if (targetY != 0f) targetY else circleControlY
            flightClimbInput = 0f
            if (shift) {
                isRunning = true
            }
        }
    }

    fun triggerPlayerJump() {
        glSurfaceView?.renderer?.let { r ->
            if (isInsideBuilding) {
                val floorY = if (r.currentInteriorType == InteriorType.PLAYER_VILLA) {
                    BuildingInterior.getVillaFloorHeight(
                        r.interiorPlayerPos.x,
                        r.interiorPlayerPos.y,
                        r.interiorPlayerPos.z
                    )
                } else 0f
                if (r.interiorPlayerPos.y <= floorY + 0.15f) {
                    interiorVelocityY = 5.4f
                    soundManager.playFootstep()
                }
            } else {
                if (playerVelocityY <= 0.1f) {
                    playerVelocityY = 6.5f
                    soundManager.playFootstep()
                }
            }
        }
    }

    fun snapToNearestRoadNear(px: Float, pz: Float): Pair<Float, Float> {
        val nsRoads = listOf(-195f, -155f, -115f, -75f, -35f, 0f, 35f, 75f, 115f, 155f, 195f)
        val ewRoads = listOf(-180f, -140f, -100f, -60f, -30f, 0f, 35f, 65f, 100f, 135f, 175f)
        val nearestNs = nsRoads.minByOrNull { kotlin.math.abs(it - px) } ?: 0f
        val nearestEw = ewRoads.minByOrNull { kotlin.math.abs(it - pz) } ?: 0f
        val distToNs = kotlin.math.abs(px - nearestNs)
        val distToEw = kotlin.math.abs(pz - nearestEw)
        return if (distToNs <= distToEw) {
            Pair(nearestNs + 3.0f, pz.coerceIn(-170f, 170f))
        } else {
            Pair(px.coerceIn(-185f, 185f), nearestEw - 3.0f)
        }
    }

    fun exitCurrentInterior() {
        val exitingGarage = isInsideBuilding && currentInteriorType == InteriorType.UNDERGROUND_GARAGE
        suppressedAutoDoorId = lastEnteredBuildingId
        undergroundTransitionCooldown = 1.2f
        isInsideBuilding = false
        currentInteriorType = InteriorType.NONE
        currentVillaFloor = 1
        resetControlInputs()
        glSurfaceView?.renderer?.let { r ->
            r.isInsideBuilding = false
            r.currentInteriorType = InteriorType.NONE
            r.cameraYawOffset = 0f
            val activeCar = currentCar
            if (exitingGarage) {
                if (isDriving && activeCar != null) {
                    activeCar.isInUndergroundGarage = false
                    activeCar.posX = 17.5f
                    activeCar.posY = 0f
                    activeCar.posZ = 29.2f
                    activeCar.yaw = 0f
                    r.playerPos.set(17.5f, 0f, 29.2f)
                } else {
                    r.playerPos.set(17.5f, 0f, 29.2f)
                    r.playerYaw = 0f
                }
            }
        }
    }
    var showStreetDirectoryDialog by remember { mutableStateOf(false) }
    var showStreetHousesMarketDialog by remember { mutableStateOf(false) }
    var purchasedHouseIds by remember { mutableStateOf(saveManager.purchasedHouseIds) }
    var rentedHouseIds by remember { mutableStateOf(saveManager.rentedHouseIds) }
    var unlockedHouseIds by remember { mutableStateOf(saveManager.unlockedHouseIds) }
    var completedHouseQuests by remember { mutableStateOf(saveManager.completedHouseQuests) }
    var hasMasterGardenKey by remember { mutableStateOf(saveManager.hasMasterGardenKey) }
    var doorKnockMessage by remember { mutableStateOf<String?>(null) }
    var nearbyMailboxBuilding by remember { mutableStateOf<Building?>(null) }
    var activeMailboxBuilding by remember { mutableStateOf<Building?>(null) }
    var activeMailboxEnvelopes by remember { mutableStateOf<List<GameSaveManager.HouseEnvelope>>(emptyList()) }

    // Missions
    var missions by remember { mutableStateOf(MissionData.getDefaultMissions()) }
    var activeMission by remember { mutableStateOf<Mission?>(null) }
    var celebrationToast by remember { mutableStateOf<String?>(null) }

    fun setRobotControlMode(enable: Boolean, customToast: String? = null) {
        isControllingRobot = enable
        glSurfaceView?.renderer?.isControllingRobot = enable
        soundManager.playChime()
        celebrationToast = customToast ?: if (enable) {
            "🤖 تم تفعيل التحكم المباشر بالروبوت! يمكنك المشي على الأرض الخضراء أو قيادة السيارة يميناً وشمالاً."
        } else {
            "🧑 عدت للتحكم بشخصيتك العادية!"
        }
    }

    fun launchRobotCarDrive() {
        val r = glSurfaceView?.renderer ?: return
        resetControlInputs()
        isControllingRobot = true
        r.isControllingRobot = true
        isInsideBuilding = false
        currentInteriorType = InteriorType.NONE
        r.isInsideBuilding = false
        r.currentInteriorType = InteriorType.NONE
        isTestDriving = false

        val roadSpot = if (isPointOnCityRoad(r.playerPos.x, r.playerPos.z)) {
            Pair(r.playerPos.x, r.playerPos.z)
        } else {
            snapToNearestRoadNear(r.playerPos.x, r.playerPos.z)
        }
        var spawnX = roadSpot.first
        var spawnZ = roadSpot.second
        for (b in r.getBuildings()) {
            b.resolveCollisionPush3D(spawnX, 0f, spawnZ, margin = 2.0f, bodyHeight = 1.5f)?.let { (px, pz) ->
                spawnX = px
                spawnZ = pz
            }
        }

        val robotCar = currentCar?.takeIf { it.type != VehicleType.AIRPLANE && !it.isShowroomDisplay }
            ?: r.vehicles.find { !it.isShowroomDisplay && (it.id == "car_robot_cyber_free" || it.dealershipCarId == saveManager.activeCarId) }
            ?: r.vehicles.find { !it.isShowroomDisplay && it.type != VehicleType.AIRPLANE }
            ?: return

        val distFromPlayer = kotlin.math.hypot((robotCar.posX - r.playerPos.x).toDouble(), (robotCar.posZ - r.playerPos.z).toDouble())
        val blockedByBuilding = r.getBuildings().any { it.checkCollision3D(robotCar.posX, 0f, robotCar.posZ, margin = 1.2f) }
        if (robotCar.isInUndergroundGarage || distFromPlayer > 35.0 || blockedByBuilding) {
            robotCar.posX = spawnX
            robotCar.posZ = spawnZ
            robotCar.yaw = r.playerYaw
        }
        robotCar.isInUndergroundGarage = false
        robotCar.posY = 0f
        robotCar.speed = 0f
        robotCar.steeringAngle = 0f
        robotCar.fuel = 100f
        robotCar.nitroFuel = 100f
        robotCar.isPlayerDriving = true

        // Release any previous car cleanly
        if (currentCar != null && currentCar !== robotCar) {
            currentCar?.isPlayerDriving = false
            currentCar?.speed = 0f
        }

        isDriving = true
        currentCar = robotCar
        currentCarFuel = robotCar.fuel
        currentCarNitroFuel = robotCar.nitroFuel
        currentCarSpeedKmH = 0
        r.isPlayerInVehicle = true
        r.currentVehicle = robotCar
        r.playerPos.set(robotCar.posX, 0f, robotCar.posZ)
        r.playerYaw = robotCar.yaw
        r.cameraYawOffset = 0f
        r.cameraPitch = 16f

        soundManager.playDoorOpen()
        soundManager.playEngineRev(robotCar.soundPitch)
        celebrationToast = "🤖🚗 تقود السيارة الآن بالروبوت! تحكّم: ⬆️ قدام | ⬇️ ورا | ⬅️ شمال | ➡️ يمين!"
    }

    fun stopMetroRideSilently() {
        isRidingMetro = false
        activeMetroTrain = null
        selectedMetroSeat = null
        glSurfaceView?.renderer?.let { r ->
            r.isRidingMetro = false
            r.activeMetroTrain = null
            r.selectedMetroSeat = null
        }
    }

    fun unlockStationTurnstile(station: MetroStation) {
        val r = glSurfaceView?.renderer ?: return
        if (r.skyMetroSystem.unlockedStationGates.contains(station.id)) {
            celebrationToast = "✅ البوابة مفتوحة بالفعل! اصعد الدرج إلى ${station.arabicName} 🪜"
            return
        }
        when {
            saveManager.consumeMetroTicket() -> {
                metroTicketsCount = saveManager.metroTicketsCount
                r.skyMetroSystem.unlockedStationGates.add(station.id)
                soundManager.playChime()
                celebrationToast = "🎫✅ تم استخدام تذكرة مترو! البوابة مفتوحة — اصعد الدرج الآن إلى ${station.arabicName} 🪜⚡"
            }
            saveManager.playerMoney >= SkyMetroSystem.TICKET_PRICE_EGP -> {
                saveManager.playerMoney -= SkyMetroSystem.TICKET_PRICE_EGP
                playerMoney = saveManager.playerMoney
                r.skyMetroSystem.unlockedStationGates.add(station.id)
                soundManager.playChime()
                celebrationToast = "💳✅ تم دفع ${SkyMetroSystem.TICKET_PRICE_EGP} جنيه وفتح بوابة ${station.arabicName}! اصعد الدرج للمحطة 🪜⚡"
            }
            else -> {
                soundManager.playHorn()
                celebrationToast = "⚠️ تحتاج ${SkyMetroSystem.TICKET_PRICE_EGP} جنيه أو تذكرة لدخول المترو! قد سيارتك ثوانٍ لكسب المال 🚗💵"
                showMetroTicketDialog = true
            }
        }
    }

    fun boardMetroTrain(train: ElectricMetroTrain) {
        val r = glSurfaceView?.renderer ?: return
        resetControlInputs()
        isInsideBuilding = false
        currentInteriorType = InteriorType.NONE
        isDriving = false
        isTestDriving = false
        currentCar?.isPlayerDriving = false
        currentCar?.speed = 0f
        currentCar = null
        train.requestedStopStationId = null
        isRidingMetro = true
        activeMetroTrain = train
        selectedMetroSeat = null
        r.isInsideBuilding = false
        r.currentInteriorType = InteriorType.NONE
        r.isPlayerInVehicle = false
        r.currentVehicle = null
        r.isRidingMetro = true
        r.activeMetroTrain = train
        r.selectedMetroSeat = null
        r.playerPos.set(train.currentPosX, SkyMetroSystem.DECK_SURFACE_Y + 0.85f, train.currentPosZ)
        r.playerYaw = train.currentYaw
        r.cameraYawOffset = 0f
        r.cameraPitch = 14f
        soundManager.playChime()
        celebrationToast = "⚡🚇 ركبت ${train.arabicName} في الهواء! اختر أي كرسي للجلوس أو اطلب محطة النزول ✨"
    }

    fun exitMetroRide(stationToLand: MetroStation?) {
        val r = glSurfaceView?.renderer ?: return
        val train = activeMetroTrain
        val targetStation = stationToLand
            ?: train?.stoppedAtStationId?.let { sid -> SkyMetroSystem.STATIONS.find { it.id == sid } }
            ?: train?.let { r.skyMetroSystem.findNextStationAhead(it.progressMeters) }
            ?: r.skyMetroSystem.findNearestStationTo(r.playerPos.x, r.playerPos.z)
        stopMetroRideSilently()
        resetControlInputs()
        r.skyMetroSystem.unlockedStationGates.add(targetStation.id)
        r.playerPos.set(targetStation.boardPosX, SkyMetroSystem.DECK_SURFACE_Y, targetStation.boardPosZ)
        r.playerYaw = targetStation.boardYaw
        playerVelocityY = 0f
        soundManager.playDoorOpen()
        celebrationToast = "🚇✅ نزلت على رصيف ${targetStation.arabicName}! انزل الدرج للشارع بالسلامة 🪜"
    }

    DisposableEffect(lifecycleOwner, glSurfaceView) {
        val view = glSurfaceView
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> view?.onResume()
                Lifecycle.Event.ON_PAUSE -> {
                    resetControlInputs()
                    val r = view?.renderer
                    if (r != null && !isInsideBuilding) {
                        saveManager.savePosition(r.playerPos.x, r.playerPos.z)
                    }
                    view?.onPause()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            view?.onPause()
        }
    }

    // Load completed missions
    LaunchedEffect(Unit) {
        val completedSet = saveManager.completedMissions
        missions = missions.map {
            if (completedSet.contains(it.id)) it.copy(status = MissionStatus.COMPLETED) else it
        }
    }

    // Android System Back Button Handler
    BackHandler(enabled = true) {
        when {
            showMetroCardShopDialog -> showMetroCardShopDialog = false
            showMetroTicketDialog -> showMetroTicketDialog = false
            showGrandMallDialog -> showGrandMallDialog = false
            showClothingStoreDialog -> showClothingStoreDialog = false
            showTalabatFoodDialog -> showTalabatFoodDialog = false
            showVillasFoodWaterDialog -> showVillasFoodWaterDialog = false
            activeMailboxBuilding != null -> activeMailboxBuilding = null
            showMotorCityDialog -> showMotorCityDialog = false
            showStreetHousesMarketDialog -> showStreetHousesMarketDialog = false
            showStreetDirectoryDialog -> showStreetDirectoryDialog = false
            showHousingDialog -> showHousingDialog = false
            showFullMap -> showFullMap = false
            showMissions -> showMissions = false
            showTravelTerminal -> showTravelTerminal = false
            showSettings -> showSettings = false
            showTrafficControl -> showTrafficControl = false
            showNameEditDialog -> showNameEditDialog = false
            showGamerBloxShowcaseDialog -> showGamerBloxShowcaseDialog = false
            showCarCustomizationDialog -> showCarCustomizationDialog = false
            showGamerBloxColorSheet -> showGamerBloxColorSheet = false
            showPoliceEventDialog -> showPoliceEventDialog = false
            isInsideBuilding -> {
                exitCurrentInterior()
                soundManager.playDoorOpen()
            }
            isRidingMetro -> {
                exitMetroRide(null)
            }
            isDriving -> {
                val activeCar = currentCar
                resetControlInputs()
                isDriving = false
                isTestDriving = false
                activeCar?.isPlayerDriving = false
                activeCar?.speed = 0f
                currentCar = null
                glSurfaceView?.renderer?.let { r ->
                    r.isPlayerInVehicle = false
                    r.currentVehicle = null
                    if (activeCar != null) {
                        var px = activeCar.posX + 2.4f
                        var pz = activeCar.posZ
                        for (b in r.getBuildings()) {
                            b.resolveCollisionPush3D(px, activeCar.posY, pz, margin = 0.85f)?.let {
                                px = it.first
                                pz = it.second
                            }
                        }
                        r.playerPos.set(px, activeCar.posY, pz)
                    }
                }
                soundManager.playDoorOpen()
            }
            else -> onBackToMenu()
        }
    }

    // Main 60FPS Game Physics Loop
    LaunchedEffect(Unit) {
        var lastTime = System.currentTimeMillis()
        var footstepTimer = 0f
        var collisionHornCooldown = 0f
        var activeMissionTimeAccum = 0f
        var evPoliceAvoidTimer = 0f
        var evPoliceAvoidTurnSign = 1f

        while (true) {
            delay(16) // ~60 fps
            val now = System.currentTimeMillis()
            val dt = ((now - lastTime) / 1000f).coerceIn(0.005f, 0.05f)
            lastTime = now
            if (collisionHornCooldown > 0f) collisionHornCooldown -= dt
            if (undergroundTransitionCooldown > 0f) undergroundTransitionCooldown -= dt

            val renderer = glSurfaceView?.renderer ?: continue
            val newTotalMin = (renderer.timeOfDayHours * 60f).toInt() % 1440
            val curTotalMin = (currentTimeOfDayHours * 60f).toInt() % 1440
            if (newTotalMin != curTotalMin) {
                currentTimeOfDayHours = renderer.timeOfDayHours
            }

            if (isInsideBuilding) {
                hoveredRooftopHouse = null
                nearbyRooftopHouse = null
                isInCompoundSeaNow = false
                isInCompoundPoolNow = false
                nearbyTurnstileStation = null
                standingMetroStation = null
                boardableMetroTrain = null
                val garageCar = currentCar
                if (renderer.currentInteriorType == InteriorType.UNDERGROUND_GARAGE && isDriving && garageCar != null) {
                    isSwimmingNow = false
                    val car = garageCar
                    val throttle = if (driveThrottle != 0f) driveThrottle else joyY
                    val steer = if (driveSteer != 0f) driveSteer else joyX
                    car.isNitroActive = isNitroEngaged
                    car.updatePhysics(throttle, steer, dt, handbrake = isHandbrakeEngaged)
                    car.posX = car.posX.coerceIn(-15.5f, 15.5f)
                    car.posZ = car.posZ.coerceIn(-19.0f, 20.6f)
                    BuildingInterior.resolvePropCollisionPush(
                        InteriorType.UNDERGROUND_GARAGE,
                        car.posX,
                        car.posZ,
                        margin = car.type.width * 0.45f,
                        py = 0f
                    )?.let { (rx, rz) ->
                        car.posX = rx.coerceIn(-15.5f, 15.5f)
                        car.posZ = rz.coerceIn(-19.0f, 20.6f)
                        car.speed = -car.speed * 0.25f
                        if (collisionHornCooldown <= 0f) {
                            soundManager.playHorn()
                            collisionHornCooldown = 0.6f
                        }
                    }

                    if (kotlin.math.abs(currentCarNitroFuel - car.nitroFuel) >= 0.25f || car.nitroFuel <= 0.1f) {
                        currentCarNitroFuel = car.nitroFuel
                    }
                    if (kotlin.math.abs(currentCarFuel - car.fuel) >= 0.25f || car.fuel <= 0.1f) {
                        currentCarFuel = car.fuel
                    }
                    if (!car.isNitroActive && isNitroEngaged && car.nitroFuel <= 1f) {
                        isNitroEngaged = false
                    }
                    val absKmH = kotlin.math.abs(car.speedKmH)
                    currentCarSpeedKmH = absKmH.toInt()
                    currentCarWheelRotations = car.totalWheelRotations
                    currentCarHighSpeedRotations = car.highSpeedWheelRotations
                    currentCarDistanceMeters = car.wheelDistanceMeters.toInt()
                    renderer.interiorPlayerPos.set(car.posX, 0f, car.posZ)

                    // Drive up exit ramp (+Z) to return straight onto city street!
                    if (car.posZ > 18.6f && kotlin.math.abs(car.posX) < 5.0f && undergroundTransitionCooldown <= 0f) {
                        undergroundTransitionCooldown = 1.2f
                        car.isInUndergroundGarage = false
                        car.posX = 17.5f
                        car.posY = 0f
                        car.posZ = 29.2f
                        car.yaw = 0f
                        isInsideBuilding = false
                        currentInteriorType = InteriorType.NONE
                        suppressedAutoDoorId = "underground_city_garage"
                        renderer.isInsideBuilding = false
                        renderer.currentInteriorType = InteriorType.NONE
                        renderer.playerPos.set(car.posX, 0f, car.posZ)
                        renderer.cameraYawOffset = 0f
                        soundManager.playDoorOpen()
                        celebrationToast = "🚗⬆️ خرجت بالسيارة من كراج المدينة تحت الأرض إلى الشارع!"
                    }
                } else {
                // Movement inside room relative to camera view with solid partition/ceiling/wall collision
                val interior = BuildingInterior.getInterior(renderer.currentInteriorType)
                val halfW = (interior.roomWidth / 2f - 0.80f).coerceAtLeast(3f)
                val halfL = (interior.roomLength / 2f - 0.80f).coerceAtLeast(3f)

                val floorY = if (renderer.currentInteriorType == InteriorType.PLAYER_VILLA) {
                    BuildingInterior.getVillaFloorHeight(
                        renderer.interiorPlayerPos.x,
                        renderer.interiorPlayerPos.y,
                        renderer.interiorPlayerPos.z
                    )
                } else 0f

                val ceilingLimitY = BuildingInterior.getIndoorCeilingLimitY(
                    renderer.currentInteriorType,
                    renderer.interiorPlayerPos.x,
                    renderer.interiorPlayerPos.y,
                    renderer.interiorPlayerPos.z
                )

                if (interiorVelocityY != 0f || renderer.interiorPlayerPos.y > floorY + 0.08f) {
                    renderer.interiorPlayerPos.y += interiorVelocityY * dt
                    interiorVelocityY -= 20f * dt
                    if (renderer.interiorPlayerPos.y >= ceilingLimitY) {
                        renderer.interiorPlayerPos.y = ceilingLimitY
                        if (interiorVelocityY > 0f) interiorVelocityY = 0f
                    }
                    if (renderer.interiorPlayerPos.y <= floorY) {
                        renderer.interiorPlayerPos.y = floorY
                        interiorVelocityY = 0f
                    }
                } else {
                    val diffY = floorY - renderer.interiorPlayerPos.y
                    renderer.interiorPlayerPos.y += diffY * kotlin.math.min(1f, dt * 10f)
                }

                val inVillaPoolNow = renderer.currentInteriorType == InteriorType.PLAYER_VILLA &&
                    BuildingInterior.isPointInVillaIndoorPool(
                        renderer.interiorPlayerPos.x,
                        renderer.interiorPlayerPos.y,
                        renderer.interiorPlayerPos.z
                    )
                isSwimmingNow = inVillaPoolNow

                val moveSpeed = if (inVillaPoolNow) 3.3f else 5.0f
                if (joyX != 0f || joyY != 0f) {
                    val camYawRad = Math.toRadians(renderer.cameraYawOffset.toDouble())
                    val forwardX = sin(camYawRad).toFloat()
                    val forwardZ = cos(camYawRad).toFloat()
                    // Screen right = forward × up = (-cos(yaw), 0, sin(yaw))
                    val rightX = -cos(camYawRad).toFloat()
                    val rightZ = sin(camYawRad).toFloat()

                    val moveX = (forwardX * joyY + rightX * joyX) * moveSpeed * dt
                    val moveZ = (forwardZ * joyY + rightZ * joyX) * moveSpeed * dt

                    val curY = renderer.interiorPlayerPos.y
                    // Axis-separated wall + furniture collision sliding
                    val candX = (renderer.interiorPlayerPos.x + moveX).coerceIn(-halfW, halfW)
                    if (!BuildingInterior.checkPropCollision(renderer.currentInteriorType, candX, renderer.interiorPlayerPos.z, margin = 0.56f, py = curY)) {
                        renderer.interiorPlayerPos.x = candX
                    }
                    val candZ = (renderer.interiorPlayerPos.z + moveZ).coerceIn(-halfL, halfL)
                    if (!BuildingInterior.checkPropCollision(renderer.currentInteriorType, renderer.interiorPlayerPos.x, candZ, margin = 0.56f, py = curY)) {
                        renderer.interiorPlayerPos.z = candZ
                    }

                    val angle = Math.toDegrees(kotlin.math.atan2(moveX.toDouble(), moveZ.toDouble())).toFloat()
                    renderer.interiorPlayerYaw = angle
                    renderer.characterWalkPhase += dt * (if (inVillaPoolNow) 6f else 10f)
                }

                // Always resolve indoor wall/furniture push and outer bounds so corners never clip
                renderer.interiorPlayerPos.x = renderer.interiorPlayerPos.x.coerceIn(-halfW, halfW)
                renderer.interiorPlayerPos.z = renderer.interiorPlayerPos.z.coerceIn(-halfL, halfL)
                BuildingInterior.resolvePropCollisionPush(
                    renderer.currentInteriorType,
                    renderer.interiorPlayerPos.x,
                    renderer.interiorPlayerPos.z,
                    margin = 0.56f,
                    py = renderer.interiorPlayerPos.y
                )?.let { (rx, rz) ->
                    renderer.interiorPlayerPos.x = rx.coerceIn(-halfW, halfW)
                    renderer.interiorPlayerPos.z = rz.coerceIn(-halfL, halfL)
                }

                if (renderer.currentInteriorType == InteriorType.PLAYER_VILLA) {
                    currentVillaFloor = if (renderer.interiorPlayerPos.y >= 2.5f) 2 else 1
                } else if (renderer.currentInteriorType == InteriorType.UNDERGROUND_GARAGE &&
                    renderer.interiorPlayerPos.z > 19.8f &&
                    kotlin.math.abs(renderer.interiorPlayerPos.x) < 4.5f &&
                    undergroundTransitionCooldown <= 0f
                ) {
                    exitCurrentInterior()
                    soundManager.playDoorOpen()
                    celebrationToast = "⬆️ خرجت من كراج المدينة تحت الأرض إلى الشارع!"
                }
                }
            } else {
                val ridingTrain = activeMetroTrain
                val activeDrivingCar = currentCar
                if (isRidingMetro && ridingTrain != null) {
                    isSwimmingNow = false
                    isInCompoundSeaNow = false
                    isInCompoundPoolNow = false
                    hoveredRooftopHouse = null
                    nearbyRooftopHouse = null
                    nearbyTurnstileStation = null
                    standingMetroStation = null
                    boardableMetroTrain = null
                    renderer.isRidingMetro = true
                    renderer.activeMetroTrain = ridingTrain
                    renderer.selectedMetroSeat = selectedMetroSeat
                    val curSeat = selectedMetroSeat
                    if (curSeat != null) {
                        val sp = SkyMetroSystem.computeMetroSeatWorldPose(ridingTrain, curSeat)
                        renderer.playerPos.set(sp.x, sp.y, sp.z)
                        renderer.playerYaw = sp.yaw
                    } else {
                        renderer.playerPos.set(
                            ridingTrain.currentPosX,
                            SkyMetroSystem.DECK_SURFACE_Y + 0.85f,
                            ridingTrain.currentPosZ
                        )
                        renderer.playerYaw = ridingTrain.currentYaw
                    }
                    val reqStopId = ridingTrain.requestedStopStationId
                    val dockedId = ridingTrain.stoppedAtStationId
                    if (reqStopId != null && dockedId != null && reqStopId == dockedId) {
                        val arrivedStation = SkyMetroSystem.STATIONS.find { it.id == dockedId }
                        ridingTrain.requestedStopStationId = null
                        exitMetroRide(arrivedStation)
                    }
                } else if (isDriving && activeDrivingCar != null) {
                isSwimmingNow = false
                isInCompoundSeaNow = false
                isInCompoundPoolNow = false
                nearbyRooftopHouse = null
                val car = activeDrivingCar
                val throttle = if (driveThrottle != 0f) driveThrottle else joyY
                val steer = if (driveSteer != 0f) driveSteer else joyX
                val buildingsList = renderer.getBuildings()

                if (car.type == VehicleType.AIRPLANE) {
                    // Flyable Airplane Physics + Street House Rooftop Landing Pads
                    val padBuilding = buildingsList.firstOrNull {
                        it.isPointOnRooftopPad(car.posX, car.posZ, margin = 0.25f)
                    }
                    hoveredRooftopHouse = padBuilding
                    val supportSurfaceY = padBuilding?.getRooftopAirplanePadY() ?: 0f

                    car.isNitroActive = isNitroEngaged
                    car.updateFlightPhysics(
                        throttle = throttle,
                        steeringInput = steer,
                        climbInput = flightClimbInput,
                        deltaSec = dt,
                        handbrake = isHandbrakeEngaged,
                        supportSurfaceY = supportSurfaceY
                    )
                    car.posX = car.posX.coerceIn(-480f, 620f)
                    car.posZ = car.posZ.coerceIn(-480f, 620f)

                    // Solid 3D building collision push so airplane cannot fly through walls or houses
                    for (b in buildingsList) {
                        val pushed = b.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = 2.6f, bodyHeight = 2.0f)
                        if (pushed != null) {
                            car.posX = pushed.first
                            car.posZ = pushed.second
                            car.speed = -car.speed * 0.2f
                            if (collisionHornCooldown <= 0f) {
                                soundManager.playHorn()
                                collisionHornCooldown = 0.6f
                            }
                        }
                    }
                    renderer.skyMetroSystem.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = 2.6f, bodyHeight = 2.0f)?.let { (px, pz) ->
                        car.posX = px
                        car.posZ = pz
                        car.speed = -car.speed * 0.2f
                    }

                    renderer.playerPos.set(car.posX, car.posY, car.posZ)
                    renderer.playerYaw = car.yaw
                    if (kotlin.math.abs(currentCarNitroFuel - car.nitroFuel) >= 0.25f || car.nitroFuel <= 0.1f) {
                        currentCarNitroFuel = car.nitroFuel
                    }
                    if (kotlin.math.abs(currentCarFuel - car.fuel) >= 0.25f || car.fuel <= 0.1f) {
                        currentCarFuel = car.fuel
                    }
                    currentCarSpeedKmH = kotlin.math.abs(car.speedKmH).toInt()
                    currentCarWheelRotations = car.totalWheelRotations
                    currentCarHighSpeedRotations = car.highSpeedWheelRotations
                    currentCarDistanceMeters = car.wheelDistanceMeters.toInt()
                    isNearGasStation = false
                    isNearParkingLot = false
                    if (padBuilding != null && car.posY <= supportSurfaceY + 0.35f) {
                        drivingRewardToast = "✈️ الطائرة مصفوفة بأمان فوق سطح ${padBuilding.arabicName}!"
                    }
                } else {
                    hoveredRooftopHouse = null
                    car.isNitroActive = isNitroEngaged
                    car.updatePhysics(throttle, steer, dt, handbrake = isHandbrakeEngaged)
                    if (kotlin.math.abs(currentCarNitroFuel - car.nitroFuel) >= 0.25f || car.nitroFuel <= 0.1f) {
                        currentCarNitroFuel = car.nitroFuel
                    }
                    if (kotlin.math.abs(currentCarFuel - car.fuel) >= 0.25f || car.fuel <= 0.1f) {
                        currentCarFuel = car.fuel
                    }
                    if (!car.isNitroActive && isNitroEngaged && car.nitroFuel <= 1f) {
                        isNitroEngaged = false
                    }
                    renderer.playerPos.set(car.posX, car.posY, car.posZ)
                    renderer.playerYaw = car.yaw

                    // Real-time wheel rotation & 110+ km/h step counting: every 1 wheel turn increments counter & earned money!
                    val absKmH = kotlin.math.abs(car.speedKmH)
                    currentCarSpeedKmH = absKmH.toInt()
                    currentCarWheelRotations = car.totalWheelRotations
                    currentCarHighSpeedRotations = car.highSpeedWheelRotations
                    currentCarDistanceMeters = car.wheelDistanceMeters.toInt()

                    val pricePerTurn = when {
                        car.isNitroActive -> 100
                        absKmH >= 110f -> 50
                        car.isDrifting || absKmH >= 55f -> 25
                        else -> 10
                    }
                    currentWheelTurnPrice = pricePerTurn

                    if (car.lastTickWheelRotations > 0) {
                        val earnedThisTick = car.lastTickWheelRotations * pricePerTurn
                        saveManager.playerMoney += earnedThisTick
                        playerMoney = saveManager.playerMoney
                        totalDriveCashEarned += earnedThisTick
                    }

                    // Check distance to Gas Station (-95f, 50f)
                    val distToGas = kotlin.math.sqrt((car.posX - (-95f)) * (car.posX - (-95f)) + (car.posZ - 50f) * (car.posZ - 50f))
                    isNearGasStation = distToGas < 22f

                    // Check distance to Underground City Garage Ramp (17.5f, 24.5f) & drive-in trigger!
                    val distToParking = kotlin.math.sqrt((car.posX - 17.5f) * (car.posX - 17.5f) + (car.posZ - 24.5f) * (car.posZ - 24.5f))
                    isNearParkingLot = distToParking < 18f

                    if (!isTestDriving && undergroundTransitionCooldown <= 0f &&
                        car.posX in 13.2f..21.8f && car.posZ in 16.5f..25.0f
                    ) {
                        undergroundTransitionCooldown = 1.2f
                        isInsideBuilding = true
                        currentInteriorType = InteriorType.UNDERGROUND_GARAGE
                        lastEnteredBuildingId = "underground_city_garage"
                        suppressedAutoDoorId = "underground_city_garage"
                        car.isInUndergroundGarage = true
                        car.posX = 0f
                        car.posY = 0f
                        car.posZ = 14.5f
                        car.yaw = 180f
                        car.speed = (car.speed * 0.6f).coerceIn(-8f, 8f)
                        renderer.isInsideBuilding = true
                        renderer.currentInteriorType = InteriorType.UNDERGROUND_GARAGE
                        renderer.interiorPlayerPos.set(0f, 0f, 14.5f)
                        renderer.cameraYawOffset = 0f
                        renderer.cameraPitch = 16f
                        soundManager.playDoorOpen()
                        celebrationToast = "🅿️⬇️ نزلت بالسيارة إلى كراج المدينة تحت الأرض! اختر موقفاً لصفها."
                    }

                    // Enforce strict 1-car-only occupancy and smooth 3D wall/roof collision for buildings & single-car garages
                    for (b in buildingsList) {
                        if (b.hasSingleCarGarage && isSingleCarGarageOccupiedByOther(b, car, renderer)) {
                            val bounds = b.getSingleCarGarageBounds()
                            val frontZ = b.getSingleCarGarageFrontZ()
                            if (bounds != null && frontZ != null) {
                                val halfCarW = car.type.width * 0.48f
                                val halfCarL = car.type.length * 0.5f
                                if (car.posX >= bounds[0] - halfCarW && car.posX <= bounds[1] + halfCarW &&
                                    car.posZ >= bounds[2] - 0.5f && car.posZ <= frontZ + halfCarL + 0.35f
                                ) {
                                    car.posZ = frontZ + halfCarL + 0.45f
                                    car.speed = 0f
                                    renderer.playerPos.set(car.posX, car.posY, car.posZ)
                                    if (collisionHornCooldown <= 0f) {
                                        soundManager.playHorn()
                                        collisionHornCooldown = 0.7f
                                    }
                                    drivingRewardToast = "🚫 هذا الكراج يتسع لسيارة واحدة فقط! توجد سيارة بداخله بالفعل"
                                    break
                                }
                            }
                        }
                        val collisionMargin = if (b.hasSingleCarGarage) car.type.width * 0.42f else car.type.width / 2f
                        val pushed = b.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = collisionMargin, bodyHeight = car.type.height)
                        if (pushed != null) {
                            car.posX = pushed.first
                            car.posZ = pushed.second
                            car.speed = -car.speed * 0.25f
                            renderer.playerPos.set(car.posX, car.posY, car.posZ)
                            if (collisionHornCooldown <= 0f) {
                                soundManager.playHorn()
                                collisionHornCooldown = 0.6f
                            }
                        }
                    }
                    // Collide cleanly with Compound 90 Avenue walls/gate columns and Elevated Metro piers/stations
                    renderer.skyMetroSystem.resolveCollisionPush3D(car.posX, car.posY, car.posZ, margin = car.type.width * 0.46f, bodyHeight = car.type.height)?.let { (px, pz) ->
                        car.posX = px
                        car.posZ = pz
                        car.speed = -car.speed * 0.25f
                        renderer.playerPos.set(car.posX, car.posY, car.posZ)
                        if (collisionHornCooldown <= 0f) {
                            soundManager.playHorn()
                            collisionHornCooldown = 0.6f
                        }
                    }
                    // Keep cars out of Compound 90 Avenue Private Sea Lagoon & Swimming Pool
                    if (CompoundAvenue90.isInsideCompoundSea(car.posX, car.posZ) ||
                        CompoundAvenue90.isInsideCompoundPool(car.posX, car.posZ)
                    ) {
                        car.posX = 53.0f
                        car.posY = 0f
                        car.posZ = 180.5f
                        car.yaw = 0f
                        car.speed = 0f
                        renderer.playerPos.set(car.posX, car.posY, car.posZ)
                        renderer.playerYaw = car.yaw
                        soundManager.playHorn()
                        drivingRewardToast = "🏖️ بحر ومسبح كومباوند 90 أفينيو للسباحة فقط! تم إرجاع سيارتك عند البوابة 🚗"
                    }
                    // If the car drives into the sea water ("داخل البحر"), automatically return it to the nearest Swap Station ("محطة البدالة")!
                    if (car.posX < -317f || car.posX > 467f || car.posZ < -299f || car.posZ > 455f) {
                        val swapStation = CityData.findNearestCoastalSwapStation(car.posX, car.posZ)
                        car.posX = swapStation.padX
                        car.posZ = swapStation.padZ
                        car.yaw = swapStation.returnYaw
                        car.speed = 0f
                        renderer.playerPos.set(car.posX, car.posY, car.posZ)
                        renderer.playerYaw = car.yaw
                        soundManager.playHorn()
                        drivingRewardToast = "🌊 دخلت السيارة داخل البحر! تم إرجاعها إلى ${swapStation.arabicName} 🚗🛟"
                    }
                }
                } else {
                hoveredRooftopHouse = null
                val buildingsList = renderer.getBuildings()
                val curX = renderer.playerPos.x
                val curZ = renderer.playerPos.z

                // Determine support elevation: Rooftop Airplane Pad > Sky Metro Stairs/Platform > Compound Pool/Sea > Villa Outdoor Pool > Ocean > Ground
                val inOutdoorPool = buildingsList.any { it.isPointInOutdoorPool(curX, curZ) }
                val inCompoundSea = CompoundAvenue90.isInsideCompoundSea(curX, curZ)
                val inCompoundPool = CompoundAvenue90.isInsideCompoundPool(curX, curZ)
                isInCompoundSeaNow = inCompoundSea
                isInCompoundPoolNow = inCompoundPool

                val onNorthPier = curX in -2.5f..2.5f && curZ in -336f..-286f
                val onSouthPier = curX in 172.5f..177.5f && curZ in 441f..491f
                val onWestPier = curX in -353f..-303f && curZ in -22.5f..-17.5f
                val onEastPier = curX in 451f..501f && curZ in 37.5f..42.5f
                val onIsland = curX in -307f..457f && curZ in -289f..445f
                val inSeaWater = !onIsland && !onNorthPier && !onSouthPier && !onWestPier && !onEastPier

                var rooftopFloorY: Float? = null
                for (b in buildingsList) {
                    val padY = b.getRooftopAirplanePadY()
                    if (padY != null && renderer.playerPos.y >= padY - 0.85f && b.isPointOnRooftopPad(curX, curZ, margin = 0.2f)) {
                        rooftopFloorY = maxOf(rooftopFloorY ?: 0f, padY)
                    } else if (b.hasSingleCarGarage) {
                        val gp = b.getGarageParkCenter()
                        if (gp != null && renderer.playerPos.y >= 4.0f &&
                            kotlin.math.abs(curX - gp.first) <= 2.3f && kotlin.math.abs(curZ - gp.second) <= 3.8f
                        ) {
                            rooftopFloorY = maxOf(rooftopFloorY ?: 0f, 5.4f)
                        }
                    }
                }

                val metroSurfaceY = renderer.skyMetroSystem.getWalkableElevationY(curX, renderer.playerPos.y, curZ)
                renderer.skyMetroSystem.refreshTurnstileAutoLock(curX, renderer.playerPos.y, curZ)

                val targetGroundY = when {
                    rooftopFloorY != null -> rooftopFloorY
                    metroSurfaceY != null -> metroSurfaceY
                    inOutdoorPool || inCompoundPool -> -0.75f
                    inCompoundSea -> -0.66f
                    inSeaWater -> -0.58f
                    else -> 0f
                }
                isSwimmingNow = (rooftopFloorY == null && metroSurfaceY == null) &&
                    (inOutdoorPool || inCompoundPool || inCompoundSea || inSeaWater)

                // Overhead ceiling limit under garages or overhangs
                var overheadCeilingY = 999f
                for (b in buildingsList) {
                    val ceil = b.getCeilingUnderRoofY(curX, renderer.playerPos.y, curZ)
                    if (ceil != null) overheadCeilingY = minOf(overheadCeilingY, ceil - 1.90f)
                }

                // On-foot ground/stairs/pool/rooftop gravity & contact physics
                if (renderer.playerPos.y > targetGroundY + 0.04f || playerVelocityY > 0f) {
                    renderer.playerPos.y += playerVelocityY * dt
                    playerVelocityY -= 22f * dt
                    if (renderer.playerPos.y >= overheadCeilingY) {
                        renderer.playerPos.y = overheadCeilingY
                        if (playerVelocityY > 0f) playerVelocityY = 0f
                    }
                    if (renderer.playerPos.y <= targetGroundY) {
                        renderer.playerPos.y = targetGroundY
                        playerVelocityY = 0f
                    }
                } else {
                    playerVelocityY = 0f
                    val diff = targetGroundY - renderer.playerPos.y
                    val climbSpeed = if (metroSurfaceY != null) 20f else 12f
                    renderer.playerPos.y += diff * kotlin.math.min(1f, dt * climbSpeed)
                }

                // Check distance to Underground City Garage entrance on foot
                val distToParkingOnFoot = kotlin.math.sqrt((renderer.playerPos.x - 17.5f) * (renderer.playerPos.x - 17.5f) + (renderer.playerPos.z - 24.5f) * (renderer.playerPos.z - 24.5f))
                isNearParkingLot = distToParkingOnFoot < 18f

                // On-foot / Swimming / Sky Metro Stairs movement
                val baseSpeed = when {
                    isSwimmingNow -> 3.4f
                    isRunning -> 8.5f
                    else -> 4.2f
                }
                if (joyX != 0f || joyY != 0f) {
                    val camYawRad = Math.toRadians(renderer.cameraYawOffset.toDouble())
                    val forwardX = sin(camYawRad).toFloat()
                    val forwardZ = cos(camYawRad).toFloat()
                    // Screen right = forward × up = (-cos(yaw), 0, sin(yaw))
                    val rightX = -cos(camYawRad).toFloat()
                    val rightZ = sin(camYawRad).toFloat()

                    val moveX = (forwardX * joyY + rightX * joyX) * baseSpeed * dt
                    val moveZ = (forwardZ * joyY + rightZ * joyX) * baseSpeed * dt

                    val targetX = (renderer.playerPos.x + moveX).coerceIn(-460f, 610f)
                    if (buildingsList.none { it.checkCollision3D(targetX, renderer.playerPos.y, renderer.playerPos.z, margin = 0.72f) } &&
                        !renderer.skyMetroSystem.checkCollision3D(targetX, renderer.playerPos.y, renderer.playerPos.z, margin = 0.68f)
                    ) {
                        renderer.playerPos.x = targetX
                    }
                    val targetZ = (renderer.playerPos.z + moveZ).coerceIn(-460f, 610f)
                    if (buildingsList.none { it.checkCollision3D(renderer.playerPos.x, renderer.playerPos.y, targetZ, margin = 0.72f) } &&
                        !renderer.skyMetroSystem.checkCollision3D(renderer.playerPos.x, renderer.playerPos.y, targetZ, margin = 0.68f)
                    ) {
                        renderer.playerPos.z = targetZ
                    }

                    // Face movement direction
                    val angle = Math.toDegrees(kotlin.math.atan2(moveX.toDouble(), moveZ.toDouble())).toFloat()
                    renderer.playerYaw = angle

                    renderer.characterWalkPhase += dt * (if (isSwimmingNow) 6f else if (isRunning) 14f else 8f)
                    renderer.isCharacterRunning = isRunning && !isSwimmingNow

                    if (!isSwimmingNow) {
                        footstepTimer += dt
                        if (footstepTimer > (if (isRunning) 0.28f else 0.45f)) {
                            soundManager.playFootstep()
                            footstepTimer = 0f
                        }
                    }
                }

                // Always push player cleanly clear of all 3D building/compound/metro boxes every frame
                renderer.playerPos.x = renderer.playerPos.x.coerceIn(-460f, 610f)
                renderer.playerPos.z = renderer.playerPos.z.coerceIn(-460f, 610f)
                for (b in buildingsList) {
                    val pushed = b.resolveCollisionPush3D(renderer.playerPos.x, renderer.playerPos.y, renderer.playerPos.z, margin = 0.72f)
                    if (pushed != null) {
                        renderer.playerPos.x = pushed.first
                        renderer.playerPos.z = pushed.second
                    }
                }
                renderer.skyMetroSystem.resolveCollisionPush3D(renderer.playerPos.x, renderer.playerPos.y, renderer.playerPos.z, margin = 0.68f)?.let { (px, pz) ->
                    renderer.playerPos.x = px
                    renderer.playerPos.z = pz
                }
                }
            }

            // Update active Street Zone banner based on player world position
            currentStreetZone = if (!isInsideBuilding) {
                StreetZone.detectAt(renderer.playerPos.x, renderer.playerPos.z)
            } else null

            // Proximity checks for enterable doors, metro gates, sky platforms, rooftop ladders, mailboxes & vehicles
            if (!isInsideBuilding && !isDriving && !isRidingMetro) {
                nearbyTurnstileStation = renderer.skyMetroSystem.findNearbyTurnstileStation(
                    renderer.playerPos.x, renderer.playerPos.y, renderer.playerPos.z
                )
                standingMetroStation = renderer.skyMetroSystem.findStationWherePlayerStanding(
                    renderer.playerPos.x, renderer.playerPos.y, renderer.playerPos.z
                )
                boardableMetroTrain = standingMetroStation?.let { st ->
                    renderer.skyMetroSystem.findBoardableTrainAtStation(st)
                }
                val activeSuppressedId = suppressedAutoDoorId
                if (activeSuppressedId != null) {
                    val suppressedB = renderer.getBuildings().find { it.id == activeSuppressedId }
                    if (suppressedB == null) {
                        suppressedAutoDoorId = null
                    } else {
                        val sdx = renderer.playerPos.x - suppressedB.doorPosX
                        val sdz = renderer.playerPos.z - suppressedB.doorPosZ
                        if (sdx * sdx + sdz * sdz > 3.8f * 3.8f) {
                            suppressedAutoDoorId = null
                        }
                    }
                }

                var foundDoor: Building? = null
                for (b in renderer.getBuildings()) {
                    if (renderer.playerPos.y < 2.2f && b.isNearDoor(renderer.playerPos.x, renderer.playerPos.z, interactDistance = 5.0f)) {
                        foundDoor = b
                        break
                    }
                }
                if (foundDoor?.id != nearbyBuildingForDoor?.id) {
                    doorKnockMessage = null
                }
                nearbyBuildingForDoor = foundDoor

                if (foundDoor != null) {
                    if (lastProximityDoorSoundId != foundDoor.id) {
                        lastProximityDoorSoundId = foundDoor.id
                        soundManager.playDoorOpen()
                    }
                    val dxDoor = renderer.playerPos.x - foundDoor.doorPosX
                    val dzDoor = renderer.playerPos.z - foundDoor.doorPosZ
                    if (dxDoor * dxDoor + dzDoor * dzDoor <= 2.20f * 2.20f &&
                        renderer.playerPos.y < 1.8f &&
                        foundDoor.id != suppressedAutoDoorId
                    ) {
                        if (foundDoor.type == BuildingType.CAR_DEALERSHIP) {
                            if (!showMotorCityDialog) {
                                inspectedDealershipCar = null
                                showMotorCityDialog = true
                                suppressedAutoDoorId = foundDoor.id
                                joyX = 0f
                                joyY = 0f
                                soundManager.playDoorOpen()
                            }
                        } else if (foundDoor.interiorType != InteriorType.NONE) {
                            val isOwnedHouse = purchasedHouseIds.contains(foundDoor.id)
                            val isRentedHouse = rentedHouseIds.contains(foundDoor.id)
                            val isUnlockedHouse = !foundDoor.isLockedByDefault || unlockedHouseIds.contains(foundDoor.id)
                            if (isUnlockedHouse && (foundDoor.rentPrice == 0 || isOwnedHouse || isRentedHouse)) {
                                isInsideBuilding = true
                                currentVillaFloor = 1
                                currentInteriorType = foundDoor.interiorType
                                lastEnteredBuildingId = foundDoor.id
                                suppressedAutoDoorId = foundDoor.id
                                joyX = 0f
                                joyY = 0f
                                renderer.isInsideBuilding = true
                                renderer.currentInteriorType = foundDoor.interiorType
                                renderer.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(foundDoor.interiorType))
                                renderer.interiorPlayerYaw = 180f
                                renderer.cameraYawOffset = 180f
                                renderer.cameraPitch = 16f
                                soundManager.playDoorOpen()
                                celebrationToast = "🚪 انفتح باب «${foundDoor.arabicName}» تلقائياً عند اقترابك!"
                            }
                        }
                    }
                } else {
                    lastProximityDoorSoundId = null
                }

                var foundRooftop: Building? = null
                for (b in renderer.getBuildings()) {
                    if (b.hasRooftopAirplanePad) {
                        val padY = b.getRooftopAirplanePadY() ?: continue
                        val onPad = renderer.playerPos.y >= padY - 0.85f &&
                            b.isPointOnRooftopPad(renderer.playerPos.x, renderer.playerPos.z, margin = 0.5f)
                        val nearLadder = renderer.playerPos.y < 1.5f &&
                            b.isNearRooftopLadder(renderer.playerPos.x, renderer.playerPos.z, interactDistance = 3.2f)
                        if (onPad || nearLadder) {
                            foundRooftop = b
                            break
                        }
                    }
                }
                nearbyRooftopHouse = foundRooftop

                var foundCar: Vehicle? = null
                var foundDCar: DealershipCar? = null
                for (v in renderer.vehicles) {
                    if (v.isInUndergroundGarage) continue
                    val dist = renderer.playerPos.distanceTo2D(Vector3(v.posX, v.posY, v.posZ))
                    if (dist < 6.8f && kotlin.math.abs(renderer.playerPos.y - v.posY) < 3.2f) {
                        foundCar = v
                        if (v.dealershipCarId != null) {
                            foundDCar = MotorCityData.getCarById(v.dealershipCarId)
                        }
                        break
                    }
                }
                nearbyVehicleForEntry = foundCar
                nearbyDealershipCar = foundDCar

                var foundMailbox: Building? = null
                for (b in renderer.getBuildings()) {
                    if (renderer.playerPos.y < 2.2f && b.isNearMailbox(renderer.playerPos.x, renderer.playerPos.z, interactDistance = 4.2f)) {
                        foundMailbox = b
                        break
                    }
                }
                nearbyMailboxBuilding = foundMailbox
            } else {
                nearbyTurnstileStation = null
                standingMetroStation = null
                boardableMetroTrain = null
                nearbyBuildingForDoor = null
                nearbyMailboxBuilding = null
                if (isDriving || isRidingMetro) nearbyRooftopHouse = null
                if (isInsideBuilding && !isDriving && renderer.currentInteriorType == InteriorType.UNDERGROUND_GARAGE) {
                    var foundUndergroundCar: Vehicle? = null
                    var foundUndergroundDCar: DealershipCar? = null
                    for (v in renderer.vehicles) {
                        if (!v.isInUndergroundGarage) continue
                        val dist = renderer.interiorPlayerPos.distanceTo2D(Vector3(v.posX, v.posY, v.posZ))
                        if (dist < 5.5f) {
                            foundUndergroundCar = v
                            if (v.dealershipCarId != null) {
                                foundUndergroundDCar = MotorCityData.getCarById(v.dealershipCarId)
                            }
                            break
                        }
                    }
                    nearbyVehicleForEntry = foundUndergroundCar
                    nearbyDealershipCar = foundUndergroundDCar
                } else {
                    nearbyVehicleForEntry = null
                    nearbyDealershipCar = null
                }
            }

            // Garage proximity & occupancy detection (active both while driving AND on foot)
            if (!isInsideBuilding) {
                var foundGarage: Building? = null
                var insideBay = false
                for (b in renderer.getBuildings()) {
                    val gp = b.getGarageParkCenter() ?: continue
                    val dx = renderer.playerPos.x - gp.first
                    val dz = renderer.playerPos.z - gp.second
                    val distSq = dx * dx + dz * dz
                    val insideSingle = b.hasSingleCarGarage &&
                        b.isPointInSingleCarGarageBay(renderer.playerPos.x, renderer.playerPos.z, padding = 0.1f)
                    if (insideSingle || distSq <= 12.0f * 12.0f) {
                        foundGarage = b
                        insideBay = if (b.hasSingleCarGarage) insideSingle else distSq <= 5.0f * 5.0f
                        break
                    }
                }
                nearbyGarageBuilding = foundGarage
                isInsideGarageBay = insideBay
                isNearbyGarageOccupiedByOther = if (foundGarage != null && foundGarage.hasSingleCarGarage) {
                    isSingleCarGarageOccupiedByOther(foundGarage, currentCar, renderer)
                } else false

                if (isDriving && insideBay && !wasInsideGarageBay && foundGarage?.hasSingleCarGarage == true) {
                    soundManager.playChime()
                    celebrationToast = "🅿️ دخلت كراج سيارة واحدة فقط (${foundGarage.arabicName}) ✅"
                }
                if (foundGarage != null && !isNearbyGarageOccupiedByOther) {
                    if (lastProximityGarageSoundId != foundGarage.id) {
                        lastProximityGarageSoundId = foundGarage.id
                        soundManager.playDoorOpen()
                    }
                } else if (foundGarage == null) {
                    lastProximityGarageSoundId = null
                }
                wasInsideGarageBay = insideBay
            } else {
                nearbyGarageBuilding = null
                isInsideGarageBay = false
                isNearbyGarageOccupiedByOther = false
                wasInsideGarageBay = false
                lastProximityGarageSoundId = null
            }

            // Detect closest Autonomous Human-Like Robot nearby for live status indicator
            nearbyAutonomousRobot = if (!isInsideBuilding) {
                var closest: com.example.opencity.model.AutonomousRobot? = null
                var bestDistSq = 15.0f * 15.0f
                for (robot in renderer.trafficSystem.autonomousRobots) {
                    val rx = if (robot.isInCar) robot.car.posX else robot.footX
                    val rz = if (robot.isInCar) robot.car.posZ else robot.footZ
                    val dxr = rx - renderer.playerPos.x
                    val dzr = rz - renderer.playerPos.z
                    val dSq = dxr * dxr + dzr * dzr
                    if (dSq < bestDistSq) {
                        bestDistSq = dSq
                        closest = robot
                    }
                }
                closest
            } else null

            // Active Mission check & live countdown timer
            val curMission = activeMission
            if (curMission == null) {
                activeMissionTimeAccum = 0f
            } else {
                val dist = renderer.playerPos.distanceTo2D(curMission.targetPosition)

                if (curMission.timeLimitSec > 0) {
                    activeMissionTimeAccum += dt
                    if (activeMissionTimeAccum >= 1.0f) {
                        val elapsedSec = activeMissionTimeAccum.toInt()
                        activeMissionTimeAccum -= elapsedSec.toFloat()
                        val nextRemaining = (curMission.remainingTimeSec - elapsedSec).coerceAtLeast(0)
                        if (nextRemaining <= 0) {
                            celebrationToast = "⏱️ انتهى وقت المهمة: ${curMission.titleArabic}! حاول مجدداً."
                            missions = missions.map {
                                if (it.id == curMission.id) it.copy(status = MissionStatus.AVAILABLE, remainingTimeSec = it.timeLimitSec) else it
                            }
                            activeMission = null
                            renderer.activeMissionTargetPos = null
                            activeMissionTimeAccum = 0f
                        } else if (nextRemaining != curMission.remainingTimeSec) {
                            activeMission = curMission.copy(remainingTimeSec = nextRemaining)
                        }
                    }
                }

                if (activeMission != null && dist < 10.0f) {
                    // Mission Goal Reached!
                    soundManager.playChime()
                    saveManager.markMissionCompleted(curMission.id, 0)

                    celebrationToast = "🎉 اكتملت المهمة: ${curMission.titleArabic}!"
                    missions = missions.map {
                        if (it.id == curMission.id) it.copy(status = MissionStatus.COMPLETED, remainingTimeSec = it.timeLimitSec) else it
                    }
                    activeMission = null
                    renderer.activeMissionTargetPos = null
                    activeMissionTimeAccum = 0f
                }
            }

            // Police vs Robbers Event Physics & Logic («حدث شرطة وحرامية»)
            val evPoliceCar = eventPoliceVehicle
            val evRobberCar = eventRobberVehicle
            if (isPoliceEventActive && evPoliceCar != null && evRobberCar != null) {
                policeEventTimer -= dt
                val buildingsList = renderer.getBuildings()

                if (policeEventRole == "POLICE") {
                    // Robber vehicle AI drives dynamically within city bounds and avoids buildings
                    evRobberCar.speed = 13.2f
                    // Turn away smoothly if approaching island edges
                    if (evRobberCar.posZ > 170f && kotlin.math.cos(Math.toRadians(evRobberCar.yaw.toDouble())) > 0) {
                        evRobberCar.yaw = (evRobberCar.yaw + 110f * dt) % 360f
                    } else if (evRobberCar.posZ < -170f && kotlin.math.cos(Math.toRadians(evRobberCar.yaw.toDouble())) < 0) {
                        evRobberCar.yaw = (evRobberCar.yaw + 110f * dt) % 360f
                    } else if (evRobberCar.posX > 180f && kotlin.math.sin(Math.toRadians(evRobberCar.yaw.toDouble())) > 0) {
                        evRobberCar.yaw = (evRobberCar.yaw + 110f * dt) % 360f
                    } else if (evRobberCar.posX < -180f && kotlin.math.sin(Math.toRadians(evRobberCar.yaw.toDouble())) < 0) {
                        evRobberCar.yaw = (evRobberCar.yaw + 110f * dt) % 360f
                    }
                    val rRad = Math.toRadians(evRobberCar.yaw.toDouble())
                    evRobberCar.posX = (evRobberCar.posX + sin(rRad).toFloat() * evRobberCar.speed * dt).coerceIn(-210f, 340f)
                    evRobberCar.posZ = (evRobberCar.posZ + cos(rRad).toFloat() * evRobberCar.speed * dt).coerceIn(-200f, 340f)
                    for (b in buildingsList) {
                        val pushed = b.resolveCollisionPush3D(evRobberCar.posX, evRobberCar.posY, evRobberCar.posZ, margin = 1.6f, bodyHeight = evRobberCar.type.height)
                        if (pushed != null) {
                            evRobberCar.posX = pushed.first
                            evRobberCar.posZ = pushed.second
                            evRobberCar.yaw = (evRobberCar.yaw + 65f) % 360f
                        }
                    }

                    val distBetween = kotlin.math.hypot(
                        (evPoliceCar.posX - evRobberCar.posX).toDouble(),
                        (evPoliceCar.posZ - evRobberCar.posZ).toDouble()
                    ).toFloat()

                    fun finishChaseInLoop(toastMsg: String) {
                        celebrationToast = toastMsg
                        resetControlInputs()
                        renderer.vehicles.remove(evPoliceCar)
                        renderer.vehicles.remove(evRobberCar)
                        isDriving = false
                        currentCar = null
                        renderer.isPlayerInVehicle = false
                        renderer.currentVehicle = null
                        for (b in buildingsList) {
                            b.resolveCollisionPush3D(renderer.playerPos.x, renderer.playerPos.y, renderer.playerPos.z, margin = 0.85f)?.let {
                                renderer.playerPos.x = it.first
                                renderer.playerPos.z = it.second
                            }
                        }
                        isPoliceEventActive = false
                        eventPoliceVehicle = null
                        eventRobberVehicle = null
                        policeEventRole = null
                    }

                    // If cop caught robber (distance < 7.5 meters)
                    if (distBetween < 7.5f) {
                        soundManager.playChime()
                        finishChaseInLoop("🚨 كفو يا بطل! تم القبض على الحرامي وكلبشته واقتياده لمركز الشرطة وحبسه في زنزانة السجن 🔒!")
                    } else if (policeEventTimer <= 0f) {
                        finishChaseInLoop("⚠️ هرب الحرامي بالمسروقات! انتهى الحدث.")
                    }
                } else if (policeEventRole == "ROBBER") {
                    // Police AI car pursues the player cleanly and steers around intervening buildings
                    val dx = evRobberCar.posX - evPoliceCar.posX
                    val dz = evRobberCar.posZ - evPoliceCar.posZ
                    val directAngle = Math.toDegrees(kotlin.math.atan2(dx.toDouble(), dz.toDouble())).toFloat()
                    if (evPoliceAvoidTimer > 0f) {
                        evPoliceAvoidTimer = (evPoliceAvoidTimer - dt).coerceAtLeast(0f)
                    }
                    val pursueYaw = if (evPoliceAvoidTimer > 0f) {
                        directAngle + evPoliceAvoidTurnSign * 72f
                    } else {
                        directAngle
                    }
                    evPoliceCar.yaw = pursueYaw
                    evPoliceCar.speed = 12.8f
                    val pFwdX = sin(Math.toRadians(pursueYaw.toDouble())).toFloat()
                    val pFwdZ = cos(Math.toRadians(pursueYaw.toDouble())).toFloat()
                    evPoliceCar.posX = (evPoliceCar.posX + pFwdX * evPoliceCar.speed * dt).coerceIn(-240f, 390f)
                    evPoliceCar.posZ = (evPoliceCar.posZ + pFwdZ * evPoliceCar.speed * dt).coerceIn(-230f, 380f)
                    for (b in buildingsList) {
                        val pushed = b.resolveCollisionPush3D(evPoliceCar.posX, evPoliceCar.posY, evPoliceCar.posZ, margin = 1.6f, bodyHeight = evPoliceCar.type.height)
                        if (pushed != null) {
                            evPoliceCar.posX = pushed.first
                            evPoliceCar.posZ = pushed.second
                            if (evPoliceAvoidTimer <= 0.05f) {
                                evPoliceAvoidTurnSign = if (dx * pFwdZ - dz * pFwdX >= 0f) 1f else -1f
                            }
                            evPoliceAvoidTimer = 0.85f
                        }
                    }

                    val distBetween = kotlin.math.hypot(
                        (evPoliceCar.posX - evRobberCar.posX).toDouble(),
                        (evPoliceCar.posZ - evRobberCar.posZ).toDouble()
                    ).toFloat()

                    fun finishRobberChaseInLoop(toastMsg: String) {
                        celebrationToast = toastMsg
                        resetControlInputs()
                        renderer.vehicles.remove(evPoliceCar)
                        renderer.vehicles.remove(evRobberCar)
                        isDriving = false
                        currentCar = null
                        renderer.isPlayerInVehicle = false
                        renderer.currentVehicle = null
                        for (b in buildingsList) {
                            b.resolveCollisionPush3D(renderer.playerPos.x, renderer.playerPos.y, renderer.playerPos.z, margin = 0.85f)?.let {
                                renderer.playerPos.x = it.first
                                renderer.playerPos.z = it.second
                            }
                        }
                        isPoliceEventActive = false
                        eventPoliceVehicle = null
                        eventRobberVehicle = null
                        policeEventRole = null
                    }

                    // If robber evaded until timer runs out
                    if (policeEventTimer <= 0f) {
                        soundManager.playChime()
                        finishRobberChaseInLoop("🏎️ مبروك! نجحت في الهروب بالسيارة بعيداً عن دورية الشرطة!")
                    } else if (distBetween < 4.5f) {
                        finishRobberChaseInLoop("🚨 أوقفتك دورية الشرطة، تم كلبشتك واقتيادك لمركز الشرطة وحبسك في الزنزانة 🔒! انتهى الحدث.")
                    }
                }
            }
        }
    }

    fun endPoliceEvent() {
        val r = glSurfaceView?.renderer
        if (r != null) {
            eventPoliceVehicle?.let { r.vehicles.remove(it) }
            eventRobberVehicle?.let { r.vehicles.remove(it) }
        }
        if (currentCar == eventPoliceVehicle || currentCar == eventRobberVehicle) {
            resetControlInputs()
            isDriving = false
            currentCar = null
            r?.isPlayerInVehicle = false
            r?.currentVehicle = null
            if (r != null) {
                for (b in r.getBuildings()) {
                    b.resolveCollisionPush3D(r.playerPos.x, r.playerPos.y, r.playerPos.z, margin = 0.85f)?.let {
                        r.playerPos.x = it.first
                        r.playerPos.z = it.second
                    }
                }
            }
        }
        isPoliceEventActive = false
        eventPoliceVehicle = null
        eventRobberVehicle = null
        policeEventRole = null
    }

    fun startPoliceEvent(role: String) {
        val r = glSurfaceView?.renderer ?: return
        endPoliceEvent()
        resetControlInputs()
        isInsideBuilding = false
        currentInteriorType = InteriorType.NONE
        r.isInsideBuilding = false
        r.currentInteriorType = InteriorType.NONE
        isTestDriving = false
        isPoliceEventActive = true
        policeEventRole = role
        policeEventTimer = 45f

        // Police HQ sits on grass at (-93.5f, -161.5f); spawn patrol chase cleanly on adjacent avenue X = -75f
        val pCar = Vehicle(
            id = "event_police_car",
            type = VehicleType.POLICE_CRUISER,
            posX = -72f,
            posY = 0f,
            posZ = -165f,
            yaw = 0f,
            colorPrimary = floatArrayOf(0.12f, 0.45f, 0.95f, 1f),
            colorSecondary = floatArrayOf(0.95f, 0.95f, 0.95f, 1f)
        )
        val rCar = Vehicle(
            id = "event_robber_car",
            type = VehicleType.SPORTS_CAR,
            posX = -78f,
            posY = 0f,
            posZ = -148f,
            yaw = 0f,
            colorPrimary = floatArrayOf(0.92f, 0.72f, 0.12f, 1f),
            colorSecondary = floatArrayOf(0.12f, 0.12f, 0.12f, 1f)
        )

        r.vehicles.add(pCar)
        r.vehicles.add(rCar)
        eventPoliceVehicle = pCar
        eventRobberVehicle = rCar

        if (role == "POLICE") {
            currentCar = pCar
            isDriving = true
            pCar.isPlayerDriving = true
            r.isPlayerInVehicle = true
            r.currentVehicle = pCar
            r.playerPos.set(pCar.posX, pCar.posY, pCar.posZ)
            r.playerYaw = pCar.yaw
            currentCarFuel = pCar.fuel
            currentCarNitroFuel = pCar.nitroFuel
            celebrationToast = "🚨 انطلق حدث الشرطة! طارد الحرامي وألقِ القبض عليه!"
        } else {
            currentCar = rCar
            isDriving = true
            rCar.isPlayerDriving = true
            r.isPlayerInVehicle = true
            r.currentVehicle = rCar
            r.playerPos.set(rCar.posX, rCar.posY, rCar.posZ)
            r.playerYaw = rCar.yaw
            currentCarFuel = rCar.fuel
            currentCarNitroFuel = rCar.nitroFuel
            celebrationToast = "🦹 انطلق حدث الحرامي! اسرق واهرب من الشرطة لمدة 45 ثانية!"
        }
        r.cameraYawOffset = 0f
        r.cameraPitch = 16f
        soundManager.playSiren()
    }

    fun enterVehicle(car: Vehicle) {
        resetControlInputs()
        isDriving = true
        currentCar = car
        car.isPlayerDriving = true
        car.steeringAngle = 0f
        glSurfaceView?.renderer?.let { r ->
            r.isPlayerInVehicle = true
            r.currentVehicle = car
            r.cameraYawOffset = 0f
            r.cameraPitch = 16f
        }
        currentCarFuel = car.fuel
        currentCarNitroFuel = car.nitroFuel
        currentCarSpeedKmH = kotlin.math.abs(car.speedKmH).toInt()
        currentCarWheelRotations = car.totalWheelRotations
        currentCarHighSpeedRotations = car.highSpeedWheelRotations
        currentCarDistanceMeters = car.wheelDistanceMeters.toInt()
        if (car.type == VehicleType.AIRPLANE) {
            celebrationToast = "✈️ ركبت الطائرة! استخدم Q للصعود و Z للهبوط و WASD للقيادة!"
        }
        soundManager.playDoorOpen()
        soundManager.playEngineRev(car.soundPitch)
    }

    fun parkCurrentCarInUndergroundGarage() {
        val car = currentCar ?: return
        if (car.type == VehicleType.AIRPLANE || isTestDriving) return
        isDriving = false
        isTestDriving = false
        resetControlInputs()
        currentCar?.isPlayerDriving = false
        currentCar = null
        val r = glSurfaceView?.renderer
        val bayNum = parkCarInUndergroundGarage(car, r)
        undergroundTransitionCooldown = 1.2f
        isInsideBuilding = true
        currentInteriorType = InteriorType.UNDERGROUND_GARAGE
        lastEnteredBuildingId = "underground_city_garage"
        suppressedAutoDoorId = "underground_city_garage"
        r?.let {
            it.isPlayerInVehicle = false
            it.currentVehicle = null
            it.isInsideBuilding = true
            it.currentInteriorType = InteriorType.UNDERGROUND_GARAGE
            it.interiorPlayerPos.set(0f, 0f, car.posZ)
            it.interiorPlayerYaw = if (car.posX < 0f) 270f else 90f
            it.cameraYawOffset = if (car.posX < 0f) 270f else 90f
            it.cameraPitch = 18f
        }
        celebrationToast = "🅿️⬇️ تم صف سيارتك في الموقف رقم #$bayNum داخل الكراج تحت الأرض! الشوارع خالية تماماً."
        soundManager.playChime()
    }

    fun exitVehicle() {
        val car = currentCar ?: return
        val exitedCarY = car.posY
        val exitedCarX = car.posX
        val exitedCarZ = car.posZ
        val exitingInsideUndergroundGarage = isInsideBuilding && currentInteriorType == InteriorType.UNDERGROUND_GARAGE
        isDriving = false
        isTestDriving = false
        resetControlInputs()
        currentCar?.isPlayerDriving = false
        currentCar?.speed = 0f
        currentCar = null
        glSurfaceView?.renderer?.let { r ->
            r.isPlayerInVehicle = false
            r.currentVehicle = null
            if (exitingInsideUndergroundGarage) {
                val stepSideX = if (exitedCarX <= 0f) (exitedCarX + 2.8f) else (exitedCarX - 2.8f)
                r.interiorPlayerPos.set(stepSideX.coerceIn(-14.5f, 14.5f), 0f, exitedCarZ.coerceIn(-18.5f, 18.5f))
                BuildingInterior.resolvePropCollisionPush(
                    InteriorType.UNDERGROUND_GARAGE,
                    r.interiorPlayerPos.x,
                    r.interiorPlayerPos.z,
                    margin = 0.56f,
                    py = 0f
                )?.let { (px, pz) ->
                    r.interiorPlayerPos.x = px
                    r.interiorPlayerPos.z = pz
                }
                celebrationToast = "🚗🅿️ نزلت من السيارة داخل كراج المدينة تحت الأرض!"
            } else {
                val buildingsList = r.getBuildings()
                val candidates = listOf(
                    Pair(exitedCarX + 2.5f, exitedCarZ),
                    Pair(exitedCarX - 2.5f, exitedCarZ),
                    Pair(exitedCarX, exitedCarZ + 3.2f),
                    Pair(exitedCarX, exitedCarZ - 3.2f)
                )
                val safeSpot = candidates.firstOrNull { (cx, cz) ->
                    buildingsList.none { it.checkCollision3D(cx, exitedCarY, cz, margin = 0.75f) }
                } ?: candidates.first()
                r.playerPos.set(safeSpot.first, exitedCarY, safeSpot.second)
                for (b in buildingsList) {
                    b.resolveCollisionPush3D(r.playerPos.x, r.playerPos.y, r.playerPos.z, margin = 0.75f)?.let { (px, pz) ->
                        r.playerPos.x = px
                        r.playerPos.z = pz
                    }
                }
                playerVelocityY = 0f

                val inPrivateGarage = buildingsList.any { b ->
                    b.isPointInSingleCarGarageBay(exitedCarX, exitedCarZ, padding = 1.5f)
                }
                if (car.type != VehicleType.AIRPLANE && !car.isShowroomDisplay &&
                    !inPrivateGarage && isPointOnCityRoad(exitedCarX, exitedCarZ)
                ) {
                    val bayNum = parkCarInUndergroundGarage(car, r)
                    celebrationToast = "🅿️⬇️ تم نقل سيارتك تلقائياً إلى الموقف #$bayNum تحت الأرض حتى لا تقف في الشارع!"
                    soundManager.playChime()
                }
            }
        }
        soundManager.playDoorOpen()
    }

    fun enterBuildingInterior(b: Building) {
        resetControlInputs()
        lastEnteredBuildingId = b.id
        suppressedAutoDoorId = b.id
        isInsideBuilding = true
        currentVillaFloor = 1
        currentInteriorType = b.interiorType
        glSurfaceView?.renderer?.let { r ->
            r.isInsideBuilding = true
            r.currentInteriorType = b.interiorType
            r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
            r.interiorPlayerYaw = 180f
            r.cameraYawOffset = 180f
            r.cameraPitch = 16f
        }
        soundManager.playDoorOpen()
    }

    val anyDialogOpen = showFullMap || showMissions || showTravelTerminal || showSettings ||
        showTrafficControl || showNameEditDialog || showMotorCityDialog ||
        showVillasFoodWaterDialog || showTalabatFoodDialog || showClothingStoreDialog ||
        showGrandMallDialog || showMetroTicketDialog || showMetroCardShopDialog ||
        showHousingDialog || showPoliceEventDialog || showStreetDirectoryDialog ||
        showStreetHousesMarketDialog || showGamerBloxShowcaseDialog || showGamerBloxColorSheet ||
        showCarCustomizationDialog || activeMailboxBuilding != null

    LaunchedEffect(anyDialogOpen) {
        if (anyDialogOpen) {
            resetControlInputs()
        } else {
            delay(150)
            runCatching { gameFocusRequester.requestFocus() }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktopViewport = maxWidth >= 600.dp || maxWidth > maxHeight
        val isCompactHeight = maxHeight < 520.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(gameFocusRequester)
                .focusable()
                .onPreviewKeyEvent { keyEvent ->
                    if (anyDialogOpen) return@onPreviewKeyEvent false
                    val code = keyEvent.key.keyCode
                    when (keyEvent.type) {
                        KeyEventType.KeyDown -> {
                            when (keyEvent.key) {
                                Key.W, Key.S, Key.A, Key.D,
                                Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight,
                                Key.ShiftLeft, Key.ShiftRight, Key.Spacebar,
                                Key.Q, Key.Z, Key.PageUp, Key.PageDown -> {
                                    pressedKeys[code] = true
                                    syncKeyboardControls()
                                    if (keyEvent.key == Key.Spacebar && !isDriving) {
                                        triggerPlayerJump()
                                    }
                                    true
                                }
                                Key.U -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraYawOffset += 12f
                                        if (r.cameraYawOffset > 180f) r.cameraYawOffset -= 360f
                                    }
                                    true
                                }
                                Key.O -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraYawOffset -= 12f
                                        if (r.cameraYawOffset < -180f) r.cameraYawOffset += 360f
                                    }
                                    true
                                }
                                Key.I -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        val minPitch = if (r.isInsideBuilding) -35f else -55f
                                        r.cameraPitch = (r.cameraPitch - 6f).coerceAtLeast(minPitch)
                                    }
                                    true
                                }
                                Key.K -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        val maxPitch = if (r.isInsideBuilding) 65f else 75f
                                        r.cameraPitch = (r.cameraPitch + 6f).coerceAtMost(maxPitch)
                                    }
                                    true
                                }
                                Key.Equals, Key.NumPadAdd -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraDistance = (r.cameraDistance - 1.5f).coerceIn(3.5f, 22.0f)
                                    }
                                    true
                                }
                                Key.Minus, Key.NumPadSubtract -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraDistance = (r.cameraDistance + 1.5f).coerceIn(3.5f, 22.0f)
                                    }
                                    true
                                }
                                Key.H -> {
                                    if (isDriving) soundManager.playHorn()
                                    true
                                }
                                Key.R -> {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraYawOffset = if (isInsideBuilding) 180f else 0f
                                        r.cameraPitch = 16f
                                    }
                                    true
                                }
                                Key.V, Key.C -> {
                                    cameraMode = if (cameraMode == CameraMode.THIRD_PERSON) CameraMode.FIRST_PERSON else CameraMode.THIRD_PERSON
                                    glSurfaceView?.renderer?.cameraMode = cameraMode
                                    celebrationToast = if (cameraMode == CameraMode.FIRST_PERSON) "🎥 منظور الشخص الأول" else "🎥 منظور الشخص الثالث"
                                    true
                                }
                                Key.M -> {
                                    showFullMap = !showFullMap
                                    true
                                }
                                Key.Tab, Key.J -> {
                                    showMissions = !showMissions
                                    true
                                }
                                Key.F -> {
                                    val activeCar = currentCar
                                    if (isDriving && activeCar != null) {
                                        activeCar.refillFuel(100f)
                                        currentCarFuel = 100f
                                        soundManager.playEngineRev(1.2f)
                                        celebrationToast = "⛽ تم ملء خزان البنزين بالكامل (100%)!"
                                    } else {
                                        showCityFiltersStrip = !showCityFiltersStrip
                                    }
                                    true
                                }
                                Key.B -> {
                                    if (isDriving && currentCar != null) {
                                        parkCurrentCarInUndergroundGarage()
                                    }
                                    true
                                }
                                Key.Escape -> {
                                    when {
                                        isInsideBuilding -> {
                                            exitCurrentInterior()
                                            soundManager.playDoorOpen()
                                        }
                                        isRidingMetro -> exitMetroRide(null)
                                        else -> showSettings = true
                                    }
                                    true
                                }
                                Key.E, Key.Enter -> {
                                    val nearCar = nearbyVehicleForEntry
                                    val nearDoor = nearbyBuildingForDoor
                                    val turnstileSt = nearbyTurnstileStation
                                    val skyPlatformSt = standingMetroStation
                                    val metroTrain = boardableMetroTrain
                                    val roofHouse = nearbyRooftopHouse
                                    val mailboxBuilding = nearbyMailboxBuilding
                                    when {
                                        isDriving -> exitVehicle()
                                        isRidingMetro -> exitMetroRide(null)
                                        isInsideBuilding -> {
                                            exitCurrentInterior()
                                            soundManager.playDoorOpen()
                                        }
                                        turnstileSt != null && glSurfaceView?.renderer?.skyMetroSystem?.unlockedStationGates?.contains(turnstileSt.id) != true -> {
                                            unlockStationTurnstile(turnstileSt)
                                        }
                                        skyPlatformSt != null && metroTrain != null -> {
                                            boardMetroTrain(metroTrain)
                                        }
                                        roofHouse != null -> {
                                            val padY = roofHouse.getRooftopAirplanePadY() ?: 10f
                                            val onRoofAlready = (glSurfaceView?.renderer?.playerPos?.y ?: 0f) >= padY - 0.85f
                                            glSurfaceView?.renderer?.let { r ->
                                                if (onRoofAlready) {
                                                    r.playerPos.set(roofHouse.posX - roofHouse.width / 2f - 1.6f, 0f, roofHouse.posZ)
                                                    playerVelocityY = 0f
                                                    celebrationToast = "⬇️ نزلت عبر السلم من مهبط سطح ${roofHouse.arabicName}"
                                                } else {
                                                    r.playerPos.set(roofHouse.posX - roofHouse.width / 2f + 1.4f, padY, roofHouse.posZ)
                                                    playerVelocityY = 0f
                                                    celebrationToast = "🪜 صعدت إلى مهبط الطيارة فوق سطح ${roofHouse.arabicName} ✈️"
                                                }
                                            }
                                            soundManager.playFootstep()
                                        }
                                        nearCar != null -> enterVehicle(nearCar)
                                        nearDoor != null && nearDoor.isEnterable -> {
                                            when {
                                                nearDoor.type == BuildingType.CAR_DEALERSHIP -> {
                                                    suppressedAutoDoorId = nearDoor.id
                                                    inspectedDealershipCar = null
                                                    showMotorCityDialog = true
                                                }
                                                nearDoor.id == "metro_card_sales_office" -> {
                                                    showMetroCardShopDialog = true
                                                }
                                                nearDoor.isLockedByDefault && !unlockedHouseIds.contains(nearDoor.id) -> {
                                                    if (hasMasterGardenKey) {
                                                        saveManager.unlockHouse(nearDoor.id)
                                                        unlockedHouseIds = saveManager.unlockedHouseIds
                                                        soundManager.playChime()
                                                        celebrationToast = "🔓 تم فتح باب «${nearDoor.arabicName}» بالمفتاح!"
                                                    } else {
                                                        soundManager.playDoorOpen()
                                                        celebrationToast = "🔒 هذا البيت مغلق بالمفتاح! ابحث عن المفتاح المفقود في حديقة الحي القديم."
                                                    }
                                                }
                                                else -> enterBuildingInterior(nearDoor)
                                            }
                                        }
                                        mailboxBuilding != null -> {
                                            activeMailboxBuilding = mailboxBuilding
                                            activeMailboxEnvelopes = saveManager.getHouseEnvelopes(mailboxBuilding.id)
                                        }
                                    }
                                    true
                                }
                                else -> false
                            }
                        }
                        KeyEventType.KeyUp -> {
                            when (keyEvent.key) {
                                Key.W, Key.S, Key.A, Key.D,
                                Key.DirectionUp, Key.DirectionDown, Key.DirectionLeft, Key.DirectionRight,
                                Key.ShiftLeft, Key.ShiftRight, Key.Spacebar,
                                Key.Q, Key.Z, Key.PageUp, Key.PageDown -> {
                                    pressedKeys.remove(code)
                                    if ((keyEvent.key == Key.ShiftLeft || keyEvent.key == Key.ShiftRight) && !isDriving) {
                                        isRunning = false
                                    }
                                    syncKeyboardControls()
                                    true
                                }
                                else -> false
                            }
                        }
                        else -> false
                    }
                }
        ) {
        // 1. 3D OpenGL Viewport (mounted after Frame #0 sync with pre-built off-thread world data)
        val readyWorld = prebuiltWorldInit
        if (readyWorld != null) {
            AndroidView(
                factory = { ctx ->
                    CityGLSurfaceView(ctx, prebuiltWorld = readyWorld.first).also {
                        glSurfaceView = it
                        it.renderer.playerPos.set(readyWorld.second.first, 0f, readyWorld.second.second)
                        it.renderer.playerYaw = 195f
                        it.renderer.cameraYawOffset = 195f
                        it.renderer.playerOutfit = currentOutfit
                        it.renderer.isNightLocked = isNightLocked
                        it.renderer.timeOfDayHours = 22.5f
                        it.setGraphicsQuality(graphicsQuality)
                        it.renderer.vehicles.forEach { v -> saveManager.applySavedCustomization(v) }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Dedicated Mouse Camera Look Drag, Click-to-Focus & Scroll-Wheel Zoom Layer:
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        val activePointers = mutableMapOf<androidx.compose.ui.input.pointer.PointerId, androidx.compose.ui.geometry.Offset>()
                        var prevPinchDist: Float? = null
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Press) {
                                runCatching { gameFocusRequester.requestFocus() }
                            }
                            if (event.type == PointerEventType.Scroll) {
                                val scrollY = event.changes.firstOrNull()?.scrollDelta?.y ?: 0f
                                if (scrollY != 0f) {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraDistance = (r.cameraDistance + scrollY * 1.1f).coerceIn(3.5f, 22.0f)
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                                continue
                            }
                            val unconsumedDownOrMove = event.changes.filter { it.pressed && !it.isConsumed }

                            val pressedIds = unconsumedDownOrMove.map { it.id }.toSet()
                            activePointers.keys.retainAll(pressedIds)

                            if (unconsumedDownOrMove.size >= 2) {
                                val p1 = unconsumedDownOrMove[0]
                                val p2 = unconsumedDownOrMove[1]
                                val dist = kotlin.math.hypot(
                                    (p1.position.x - p2.position.x).toDouble(),
                                    (p1.position.y - p2.position.y).toDouble()
                                ).toFloat()
                                val previousDist = prevPinchDist
                                if (previousDist != null && dist > 10f) {
                                    val deltaDist = dist - previousDist
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraDistance = (r.cameraDistance - deltaDist * 0.025f).coerceIn(3.5f, 22.0f)
                                    }
                                }
                                prevPinchDist = dist
                                activePointers[p1.id] = p1.position
                                activePointers[p2.id] = p2.position
                                p1.consume()
                                p2.consume()
                            } else {
                                prevPinchDist = null
                                val change = unconsumedDownOrMove.firstOrNull()
                                if (change != null) {
                                    val prev = activePointers[change.id]
                                    if (prev != null) {
                                        val dx = change.position.x - prev.x
                                        val dy = change.position.y - prev.y
                                        if (dx != 0f || dy != 0f) {
                                            glSurfaceView?.renderer?.let { r ->
                                                r.cameraYawOffset -= dx * 0.40f
                                                if (r.cameraYawOffset < -180f) r.cameraYawOffset += 360f
                                                if (r.cameraYawOffset > 180f) r.cameraYawOffset -= 360f

                                                val minPitch = if (r.isInsideBuilding) -35f else -55f
                                                val maxPitch = if (r.isInsideBuilding) 65f else 75f
                                                r.cameraPitch = (r.cameraPitch + dy * 0.30f).coerceIn(minPitch, maxPitch)
                                            }
                                            change.consume()
                                        }
                                    }
                                    activePointers[change.id] = change.position
                                }
                            }
                        }
                    }
                }
        )

        // Floating 3D Character Overhead Name Tag (centered above the yellow head in third-person)
        if (cameraMode == CameraMode.THIRD_PERSON && !isDriving && !isInsideBuilding) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-75).dp)
            ) {
                Surface(
                    color = Color(0xD90F172A),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD600)),
                    modifier = Modifier
                        .clickable { showNameEditDialog = true }
                        .testTag("floating_player_nametag")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFD600))
                        )
                        Text(
                            text = "$playerName • $characterMotto",
                            color = Color(0xFFFFD600),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                        Text(text = "✏️", fontSize = 11.sp)
                    }
                }
            }
        }

        // 2. Interior Exploration Overlay
        if (isInsideBuilding) {
            InteriorHudOverlay(
                interiorType = currentInteriorType,
                playerMoney = playerMoney,
                onInteractProp = { prop ->
                    when (prop.name) {
                        "StairsFloor2" -> {
                            val targetFloor = if (currentVillaFloor == 1) 2 else 1
                            glSurfaceView?.renderer?.let { r ->
                                if (targetFloor == 2) {
                                    r.interiorPlayerPos.set(8.2f, 4.6f, 2.0f)
                                    currentVillaFloor = 2
                                    celebrationToast = "🪜 صعدت إلى الطابق الثاني من الفيلا (غرف النوم والحمام الرئيسي)!"
                                } else {
                                    r.interiorPlayerPos.set(0f, 0f, 8.8f)
                                    currentVillaFloor = 1
                                    celebrationToast = "🪜 نزلت إلى الطابق الأول من الفيلا (الصالة والمطبخ والمسبح)!"
                                }
                            }
                            soundManager.playFootstep()
                        }
                        "Bed" -> {
                            val r = glSurfaceView?.renderer
                            if (r != null && !r.isNightLocked) {
                                r.timeOfDayHours = 8.0f
                            }
                            celebrationToast = if (r?.isNightLocked != false) {
                                "😴 نمت براحة واستيقظت بنشاط في ليل المدينة الساحر 🌙✨!"
                            } else {
                                "😴 خلدت للنوم في الجناح الرئيسي بالطابق الثاني...\n☀️ صباح الخير! استيقظت بكامل طاقتك!"
                            }
                            soundManager.playChime()
                        }
                        "SecondBed" -> {
                            val r = glSurfaceView?.renderer
                            if (r != null && !r.isNightLocked) {
                                r.timeOfDayHours = 8.0f
                            }
                            celebrationToast = "🛏️ استرحت في غرفة النوم الثانية بالطابق العلوي!"
                            soundManager.playChime()
                        }
                        "Shower" -> {
                            celebrationToast = "🚿 أخذت حماماً ساخناً ومنعشاً تحت الدش الزجاجي الفاخر!"
                            soundManager.playChime()
                        }
                        "BathroomSink" -> {
                            celebrationToast = "🚰 غسلت يديك بالماء العذب في حمام الضيوف بالطابق الأول!"
                            soundManager.playDrinkingSound()
                        }
                        "StudyDeskVilla" -> {
                            celebrationToast = "💻 جلست على مكتب الدراسة والكمبيوتر في غرفة النوم الثانية!"
                            soundManager.playChime()
                        }
                        "TV" -> {
                            celebrationToast = "📺 تم تشغيل شاشة التلفاز العملاقة 98 بوصة! تشاهد الآن بث سباقات Gamer Blox المباشرة 🏎️"
                            soundManager.playChime()
                        }
                        "Couch" -> {
                            celebrationToast = "🛋️ جلست واسترخيت على أريكة الصالة العائلية البيج الفاخرة!"
                            soundManager.playFootstep()
                        }
                        "KitchenIsland" -> {
                            celebrationToast = "🍽️ تناولت وجبة إفطار فاخرة مع الفواكه الطازجة على جزيرة المطبخ الرخامية!"
                            soundManager.playEatingSound()
                        }
                        "Majlis" -> {
                            celebrationToast = "☕ جلست في مجلس الضيوف الفاخر واحتسيت فنجان قهوة عربية أصيلة من الدلة!"
                            soundManager.playDrinkingSound()
                        }
                        "RobotBed" -> {
                            celebrationToast = "🤖⚡ تم تفعيل منصة الشحن الذكية واستراحة الروبوتات بنجاح!"
                            soundManager.playChime()
                        }
                        "GuestSofa" -> {
                            celebrationToast = "🤖🛋️ جلس الروبوتات والأصدقاء للراحة في صالة الاستقبال!"
                            soundManager.playFootstep()
                        }
                        "Fridge" -> {
                            celebrationToast = "🍕 فتحت الثلاجة وتناولت شريحة بيتزا ساخنة وعصير فواكه طازج منعش!"
                            soundManager.playEatingSound()
                        }
                        "Coffee" -> {
                            celebrationToast = "☕ قمت بتشغيل صانعة القهوة وشربت كوب كابتشينو برغوة كريمية ساخنة!"
                            soundManager.playDrinkingSound()
                        }
                        "Kitchen Counter" -> {
                            celebrationToast = "🚰 فتحت الصنبور وغسلت يديك وارتشفت ماءً نقياً بارداً!"
                            soundManager.playDrinkingSound()
                        }
                        "Table" -> {
                            celebrationToast = "🍽️ جلست على طاولة الطعام وتناولت وجبة غداء عائلية شهية!"
                            soundManager.playEatingSound()
                        }
                        "Jacuzzi" -> {
                            celebrationToast = "🛁 استمتعت بالاستحمام في الجاكوزي الرخامي الدافئ بالطابق الثاني!"
                            soundManager.playChime()
                        }
                        "Chandelier" -> {
                            celebrationToast = "💡 تم الضغط على المفتاح وتبديل إنارة الثريا الكريستالية الفاخرة!"
                            soundManager.playChime()
                        }
                        "Safe" -> {
                            celebrationToast = "🔐 خزنة الفيلا المصفحة: رصيدك المحفوظ حالياً ${String.format(java.util.Locale.US, "%,d$", playerMoney)} — قد سيارتك في المدينة لكسب المزيد! 🚗💵"
                            soundManager.playChime()
                        }
                        "Stereo" -> {
                            celebrationToast = "🎵 تم تشغيل نظام الصوت السينمائي وأنغام موسيقى هادئة!"
                            soundManager.playChime()
                        }
                        "Garage" -> {
                            celebrationToast = "🚗 تم الضغط على ريموت الكراج وفتح الباب الآلي والسيارة جاهزة!"
                            soundManager.playChime()
                        }
                        "Wardrobe" -> {
                            clothingInitialTab = 1
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "👔 فتحت خزانة الأواعي الشخصية! بدّل ملابسك كما تحب."
                            soundManager.playChime()
                        }
                        "Pool" -> {
                            celebrationToast = "🏊 سبحت في مسبح الفيلا الداخلي الفيروزي الدافئ واستعدت نشاطك!"
                            soundManager.playChime()
                        }
                        "Cashier" -> {
                            celebrationToast = "🧾 تم مسح المشتريات عند الكاشير وإصدار الفاتورة بنجاح! شكراً لتسوقك في السوبرماركت."
                            soundManager.playChime()
                        }
                        "Board" -> {
                            celebrationToast = "🚌✈️ جدول الرحلات المباشر: وسط المدينة (كل 10د) • الضواحي (كل 15د) • المطار الدولي (كل 20د)."
                            soundManager.playChime()
                        }
                        "Reception" -> {
                            celebrationToast = "🩺 تم إجراء الفحص الطبي الشامل مجاناً واستعادة كامل صحتك ونشاطك 100%!"
                            soundManager.playChime()
                        }
                        "HospitalBed" -> {
                            celebrationToast = "🛏️ أخذت قسطاً مريحاً من الراحة والعناية الطبية واستعدت عافيتك بالكامل!"
                            soundManager.playChime()
                        }
                        "LivingSofa" -> {
                            celebrationToast = "🛋️ جلست واسترخيت على كنب صالة المعيشة العائلي المريح!"
                            soundManager.playFootstep()
                        }
                        "LivingTV" -> {
                            celebrationToast = "📺 تم تشغيل التلفاز العائلي 55 بوصة لمشاهدة البرامج مع العائلة!"
                            soundManager.playChime()
                        }
                        "CoffeeTable" -> {
                            celebrationToast = "🪵 وضعت كوب الشاي والمجلات على الطاولة الوسطية الخشبية!"
                            soundManager.playDrinkingSound()
                        }
                        "KitchenOpen" -> {
                            celebrationToast = "🍳 أعددت وجبة منزلية ساخنة وشهية في المطبخ الشبه مفتوح!"
                            soundManager.playEatingSound()
                        }
                        "DiningTable" -> {
                            celebrationToast = "🍽️ جلست مع أفراد العائلة على طاولة الطعام وتناولتم وجبة لذيذة!"
                            soundManager.playEatingSound()
                        }
                        "BedWood" -> {
                            val r = glSurfaceView?.renderer
                            if (r != null && !r.isNightLocked) {
                                r.timeOfDayHours = 8.0f
                            }
                            celebrationToast = if (r?.isNightLocked != false) {
                                "😴 نمت براحة على السرير الخشبي واستيقظت بكامل نشاطك تحت أضواء الليل 🌙✨!"
                            } else {
                                "😴 نمت براحة على السرير الخشبي المتين...\n☀️ صباح الخير! استيقظت بكامل نشاطك!"
                            }
                            soundManager.playChime()
                        }
                        "WardrobeWood" -> {
                            clothingInitialTab = 1
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "🚪 فتحت خزانتك الخشبية لتبديل الأواعي والبنطلون والأحذية!"
                            soundManager.playChime()
                        }
                        "StudyDesk" -> {
                            celebrationToast = "📚 جلست على المكتب الدراسي وأنجزت المهام والمذاكرة بنجاح!"
                            soundManager.playChime()
                        }
                        "CafeTable1", "CafeTable2" -> {
                            celebrationToast = "🪑 جلست في جلسة المطعم المريحة للاستمتاع بوجبتك ومشروبك!"
                            soundManager.playFootstep()
                        }
                        "DiningLounge1", "DiningLounge2" -> {
                            celebrationToast = "🍽️🪑 استمتعت بالجلسات العائلية الفاخرة في مجمع المطاعم (Dining Avenue)!"
                            soundManager.playFootstep()
                        }
                        "OutdoorTerrace" -> {
                            celebrationToast = "☀️🌴 استرخيت في جلسات التراس الخارجية المفتوحة المطلة على حدائق المدينة!"
                            soundManager.playFootstep()
                        }
                        "JuiceFountain" -> {
                            celebrationToast = "🥤💧 ارتشفت ماءً نقياً وعصائر فريش منعشة مجاناً!"
                            soundManager.playDrinkingSound()
                        }
                        "QualityInspect" -> {
                            celebrationToast = "⭐🥇 شهادة الجودة والنظافة 100%: جميع المكونات طازجة ومفحوصة يومياً بأعلى المعايير!"
                            soundManager.playChime()
                        }
                        "JailCell" -> {
                            celebrationToast = "🔒 تم إغلاق زنزانة السجن وتأمين الحراسة! قد سيارة الشرطة في المدينة لكسب المال ومطاردة اللصوص."
                            soundManager.playChime()
                        }
                        "Handcuffs" -> {
                            celebrationToast = "🗝️ استلمت الكلبشات ومفاتيح السجن، أنت الآن جاهز لمطاردة الحرامية!"
                            soundManager.playChime()
                        }
                        "InterrogationDesk" -> {
                            celebrationToast = "📋 تم فحص ملفات التحقيق والمخالفات بنجاح!"
                            soundManager.playChime()
                        }
                        "PoliceRadar" -> {
                            celebrationToast = "📡 تم تشغيل رادار مراقبة شوارع المدينة لرصد اللصوص وسيارات الحرامية!"
                            soundManager.playChime()
                        }
                        "FrontDesk" -> {
                            showPoliceEventDialog = true
                            celebrationToast = "👮 مرحباً بك في مكتب العمليات، اختر دورك لبدء حدث الشرطة والحرامية!"
                            soundManager.playFootstep()
                        }
                        "OpenVillaCatalog" -> {
                            showHousingDialog = true
                            celebrationToast = "🏘️ أهلاً بك في كتالوج الفلل الفاخرة! قد سيارتك لجمع المال واختر الفيلا المناسبة."
                            soundManager.playChime()
                        }
                        "ScaleModel" -> {
                            showHousingDialog = true
                            celebrationToast = "🏡🔍 مجسم الفلل والقصور المعماري ثلاثي الأبعاد: تفقد تصاميم ومواقع أفخم 10 فلل وقصور بالمدينة!"
                            soundManager.playFootstep()
                        }
                        "TitleDeedsSafe" -> {
                            celebrationToast = "🔐📜 خزنة الصكوك: لديك ${saveManager.ownedVillaIds.size} فلل مسجلة رسمياً باسمك في السجل العقاري!"
                            soundManager.playChime()
                        }
                        "VipLounge" -> {
                            celebrationToast = "☕🛋️ جلست في صالة الانتظار وارتشفت فنجان قهوة فاخرة!"
                            soundManager.playDrinkingSound()
                        }
                        "ContractTable" -> {
                            showHousingDialog = true
                            celebrationToast = "✒️📋 طاولة توقيع العقود: اختر الفيلا أو القصر لتوقيع ونقل صك الملكية الفورية!"
                            soundManager.playChime()
                        }
                        "VillasDeskMall" -> {
                            showVillasFoodWaterDialog = true
                            celebrationToast = "🏡👑 مكتب ومقر شراء الفلل الفاخرة! قد سيارتك لكسب المال واختر فيلتك."
                            soundManager.playChime()
                        }
                        "FoodGrillCorner" -> {
                            if (playerMoney >= 50) {
                                playerMoney -= 50
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🍖🍔 تناولت وجبة مشاوي كباب ولحوم ساخنة ولذيذة (-50$)! نَم نَم نَم!"
                                soundManager.playEatingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 50$ لشراء وجبة المشاوي!"
                            }
                        }
                        "BeverageJuiceBar" -> {
                            if (playerMoney >= 15) {
                                playerMoney -= 15
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🥤🍹 ارتشفت عصير فواكه طبيعي طازج ومشروبات مثلجة منعشة (-15$)!"
                                soundManager.playDrinkingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 15$ لشراء العصير!"
                            }
                        }
                        "PureWaterCooler" -> {
                            celebrationToast = "💧🧊 ارتشفت ماءً نقياً عذباً ومثلجاً من برادة مياه المركز! رويت عطشك مجاناً!"
                            soundManager.playDrinkingSound()
                        }
                        "WaterBottlesCrate" -> {
                            if (playerMoney >= 5) {
                                playerMoney -= 5
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "💧📦 اشتريت قوارير مياه شرب معدنية نقية عذبة (-5$) للفيلا والسيارة!"
                                soundManager.playDrinkingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 5$ لشراء قوارير المياه!"
                            }
                        }
                        "DiningTableMall" -> {
                            celebrationToast = "🍽️🪑 جلست على طاولة الطعام واستمتعت بالمأكولات والمشروبات في أجواء راقية!"
                            soundManager.playFootstep()
                        }
                        "MallClothesShop" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "👕 أهلاً بك في محل الأواعي بالمول! اشترِ والبس الملابس فوراً على شخصيتك."
                            soundManager.playChime()
                        }
                        "MallVegFruitMarket" -> {
                            grandMallInitialTab = MallShopTab.VEGETABLES
                            showGrandMallDialog = true
                            soundManager.playChime()
                        }
                        "MallRestaurantsFood" -> {
                            grandMallInitialTab = MallShopTab.FOOD
                            showGrandMallDialog = true
                            soundManager.playChime()
                        }
                        "MallBakerySupermarket" -> {
                            grandMallInitialTab = MallShopTab.SUPERMARKET_ALL
                            showGrandMallDialog = true
                            soundManager.playChime()
                        }
                        "MallJuiceDessertBar" -> {
                            grandMallInitialTab = MallShopTab.DRINKS_DESSERTS
                            showGrandMallDialog = true
                            soundManager.playChime()
                        }
                        "MallOpenAllShops" -> {
                            grandMallInitialTab = MallShopTab.CLOTHES
                            showGrandMallDialog = true
                            soundManager.playChime()
                        }
                        "ExitDoorMall" -> {
                            exitCurrentInterior()
                            celebrationToast = "🚪 خرجت إلى شوارع المدينة."
                            soundManager.playFootstep()
                        }
                        "MetroCardSalesCounter" -> {
                            showMetroCardShopDialog = true
                            celebrationToast = "💳 أهلاً بك في شباك محلك لبيع كروت المترو للزبائن!"
                            soundManager.playChime()
                        }
                        "MetroCardPrinterTerminal" -> {
                            val updatedStock = saveManager.printNewMetroCards(10)
                            metroCardStockCount = updatedStock
                            celebrationToast = "🖨️✅ تم طباعة وشحن +10 كروت مترو جديدة! المخزون الآن: $updatedStock 💳"
                            soundManager.playChime()
                        }
                        "MetroCardDisplayWall" -> {
                            showMetroCardShopDialog = true
                            celebrationToast = "🚇📋 باقات كروت المترو: عادي (5 ج) • فضي (45 ج) • ذهبي VIP (150 ج) • بلاتيني (500 ج)"
                            soundManager.playFootstep()
                        }
                        "MetroWaitingBench" -> {
                            celebrationToast = "🪑👥 الركاب ينتظرون دورهم عند الكونتر لشراء كروت المترو منك!"
                            soundManager.playFootstep()
                        }
                        "ExitMetroShopDoor" -> {
                            exitCurrentInterior()
                            celebrationToast = "🚪 خرجت من مركزي لبيع كروت المترو إلى الشارع الرئيسي."
                            soundManager.playFootstep()
                        }
                        "OpenStreetHousesCatalog", "StreetMapTable" -> {
                            showStreetHousesMarketDialog = true
                            celebrationToast = "🏘️🔑 أهلاً بك في مكان شراء بيوت الشارع! اختر أي بيت في شوارع المدينة للشراء."
                            soundManager.playChime()
                        }
                        "KeysCabinet" -> {
                            celebrationToast = "🔐🗝️ خزنة الصكوك: لديك ${purchasedHouseIds.size} بيوت شارع مملوكة لك في المدينة!"
                            soundManager.playChime()
                        }
                        "HospitalityCoffee" -> {
                            celebrationToast = "☕🛋️ ارتشفت فنجان قهوة عربية فاخرة في مكتب شراء بيوت الشارع!"
                            soundManager.playDrinkingSound()
                        }
                        "SignatureMeal200" -> {
                            if (playerMoney >= 200) {
                                playerMoney -= 200
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🍖🥤 تم شراء وتناول وجبة طعام ومشروبات فاخرة في مطعم واستراحة المدينة (-200$)! شحنت طاقتك وصحتك بالكامل 100%!"
                                soundManager.playEatingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 200$ لشراء وجبة الطعام والمشروبات الفاخرة! رصيدك الحالي ($playerMoney$). قُد سيارتك في المدينة لكسب المال! 🚗💵"
                                soundManager.playFootstep()
                            }
                        }
                        "NitroRefill" -> {
                            if (playerMoney >= 50) {
                                playerMoney -= 50
                                saveManager.playerMoney = playerMoney
                                currentCar?.nitroFuel = 100f
                                currentCarNitroFuel = 100f
                                celebrationToast = "🚀 تم شراء وشحن أسطوانة نايترو توربو خارقة بالكامل 100% (-50$)!"
                                soundManager.playChime()
                            } else {
                                celebrationToast = "⚠️ تحتاج 50$ لشراء أسطوانة النايترو!"
                            }
                        }
                        "SportTires" -> {
                            if (playerMoney >= 100) {
                                playerMoney -= 100
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🛞 تم شراء وتركيب طقم إطارات رياضية فائقة التماسك (-100$)!"
                                soundManager.playChime()
                            } else {
                                celebrationToast = "⚠️ تحتاج 100$ لشراء طقم الإطارات الرياضية!"
                            }
                        }
                        "TurboEngine" -> {
                            if (playerMoney >= 150) {
                                playerMoney -= 150
                                saveManager.playerMoney = playerMoney
                                val targetCar = currentCar ?: glSurfaceView?.renderer?.vehicles?.firstOrNull {
                                    it.dealershipCarId == saveManager.activeCarId
                                } ?: glSurfaceView?.renderer?.vehicles?.firstOrNull { it.type != VehicleType.AIRPLANE && !it.isShowroomDisplay }
                                targetCar?.let { car ->
                                    car.customTopSpeed = (car.effectiveMaxSpeedKmH + 55f).coerceAtMost(450f)
                                    car.customAccelRate = (car.effectiveAccelRate + 18f).coerceAtMost(100f)
                                    saveManager.saveVehicleCustomization(car)
                                }
                                celebrationToast = "⚡ تم شراء وتزويد السيارة بمحرك تيربو سوبر تشارجر مضاعف القوة (-150$)!"
                                soundManager.playChime()
                            } else {
                                celebrationToast = "⚠️ تحتاج 150$ لشراء محرك التيربو!"
                            }
                        }
                        "NeonUnderglow" -> {
                            if (playerMoney >= 80) {
                                playerMoney -= 80
                                saveManager.playerMoney = playerMoney
                                val targetCar = currentCar ?: glSurfaceView?.renderer?.vehicles?.firstOrNull {
                                    it.dealershipCarId == saveManager.activeCarId
                                } ?: glSurfaceView?.renderer?.vehicles?.firstOrNull { it.type != VehicleType.AIRPLANE && !it.isShowroomDisplay }
                                targetCar?.let { car ->
                                    car.underglowColor = floatArrayOf(0.08f, 0.85f, 1.0f, 1.0f)
                                    saveManager.saveVehicleCustomization(car)
                                }
                                celebrationToast = "💡 تم تفعيل إضاءة نيون زرقاء سفلية مثل سيارة Gamer Blox R-1 (-80$)!"
                                soundManager.playChime()
                            } else {
                                celebrationToast = "⚠️ تحتاج 80$ لشراء إضاءة النيون!"
                            }
                        }
                        "CarWashKit" -> {
                            if (playerMoney >= 25) {
                                playerMoney -= 25
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🧽 تم غسيل وتلميع وحماية طلاء وهيكل السيارة بالكامل (-25$)!"
                                soundManager.playChime()
                            } else {
                                celebrationToast = "⚠️ تحتاج 25$ لشراء طقم الغسيل والتلميع!"
                            }
                        }
                        "TuningComputer" -> {
                            showCarCustomizationDialog = true
                            soundManager.playChime()
                            celebrationToast = "🔧 فتحنا لك ورشة تعديل السيارة! عدّل اللون والنيون والشكل والأداء كما تحب."
                        }
                        "BBQMeat" -> {
                            if (playerMoney >= 50) {
                                playerMoney -= 50
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🥩 تناولت طبق مشاوي كباب وريش لحم ساخن ولذيذ (-50$)!"
                                soundManager.playEatingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 50$ لطلب طبق المشاوي!"
                            }
                        }
                        "JuiceAndSoda" -> {
                            if (playerMoney >= 15) {
                                playerMoney -= 15
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🥤 ارتشفت عصير فواكه طازج مثلج ومنعش (-15$)!"
                                soundManager.playDrinkingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 15$ لشراء العصير!"
                            }
                        }
                        "DessertDisplay" -> {
                            if (playerMoney >= 25) {
                                playerMoney -= 25
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🍰🍦 تناولت طبق حلويات شرقية وآيس كريم مثلج (-25$)!"
                                soundManager.playEatingSound()
                            } else {
                                celebrationToast = "⚠️ لا تملك 25$ لشراء الحلويات!"
                            }
                        }
                        "NikeBoutiqueRack" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = ClothingBrand.NIKE
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "✔️ قسم Nike الرياضي: اشترِ وارتدِ قمصان وبنطلونات وأحذية نايكي فوراً!"
                            soundManager.playChime()
                        }
                        "AdidasBoutiqueRack" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = ClothingBrand.ADIDAS
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "☰ قسم Adidas الكلاسيكي: تشكيلة الخطوط الثلاثة جاهزة للارتداء!"
                            soundManager.playChime()
                        }
                        "PantsJeansRack" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = null
                            clothingInitialSlot = ClothingSlot.PANTS
                            showClothingStoreDialog = true
                            celebrationToast = "👖 قسم البنطلونات والجينز والشورتات الرياضية!"
                            soundManager.playChime()
                        }
                        "SneakersWall" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = null
                            clothingInitialSlot = ClothingSlot.SHOES
                            showClothingStoreDialog = true
                            celebrationToast = "👟 جدار أحذية نايكي وأديداس وجوردان!"
                            soundManager.playChime()
                        }
                        "CapsGlassesDisplay" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = null
                            clothingInitialSlot = ClothingSlot.ACCESSORY
                            showClothingStoreDialog = true
                            celebrationToast = "🧢 قسم القبعات والنظارات الشمسية والساعات الذهبية!"
                            soundManager.playChime()
                        }
                        "WardrobeFittingMirror" -> {
                            clothingInitialTab = 1
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "🪞 خزانتك الشخصية ومرآة القياس: جرّب كل أواعيك المشتراة!"
                            soundManager.playChime()
                        }
                        "GarageValetDesk" -> {
                            val r = glSurfaceView?.renderer
                            var sweptCount = 0
                            if (r != null) {
                                for (v in r.vehicles) {
                                    val inPrivateGarage = r.getBuildings().any { b ->
                                        b.isPointInSingleCarGarageBay(v.posX, v.posZ, padding = 1.5f)
                                    }
                                    if (!v.isInUndergroundGarage && !v.isShowroomDisplay && v.type != VehicleType.AIRPLANE &&
                                        !inPrivateGarage && isPointOnCityRoad(v.posX, v.posZ)
                                    ) {
                                        parkCarInUndergroundGarage(v, r)
                                        sweptCount++
                                    }
                                }
                            }
                            val undergroundTotal = r?.vehicles?.count { it.isInUndergroundGarage } ?: 13
                            celebrationToast = if (sweptCount > 0) {
                                "🅿️⬇️ تم سحب وصف $sweptCount سيارة من الشوارع إلى الكراج تحت الأرض! (الإجمالي: $undergroundTotal سيارة)"
                            } else {
                                "✅ شوارع المدينة نظيفة وخالية! يوجد الآن $undergroundTotal سيارة مصفوفة بأمان داخل الكراج تحت الأرض."
                            }
                            soundManager.playChime()
                        }
                        "UndergroundEVCharger" -> {
                            glSurfaceView?.renderer?.vehicles?.filter { it.isInUndergroundGarage }?.forEach { v ->
                                v.fuel = 100f
                                v.nitroFuel = 100f
                            }
                            currentCar?.let {
                                it.fuel = 100f
                                it.nitroFuel = 100f
                                currentCarFuel = 100f
                                currentCarNitroFuel = 100f
                            }
                            celebrationToast = "⚡🔋 تم شحن وتزويد جميع السيارات في الكراج تحت الأرض بالوقود والنايترو 100% مجاناً!"
                            soundManager.playChime()
                        }
                        "UndergroundCarWash" -> {
                            if (playerMoney >= 20) {
                                playerMoney -= 20
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🧽✨ تم غسيل وتلميع سيارتك في المغسلة الأرضية (-20$)!"
                                soundManager.playChime()
                            } else {
                                celebrationToast = "⚠️ تحتاج 20$ لاستخدام المغسلة الآلية! قُد سيارتك بالمدينة لكسب المال."
                            }
                        }
                        "GarageExitRamp" -> {
                            exitCurrentInterior()
                            soundManager.playDoorOpen()
                            celebrationToast = "⬆️ صعدت عبر المخرج إلى شوارع المدينة!"
                        }
                        "ClothingCashier" -> {
                            clothingInitialTab = 0
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                            celebrationToast = "🛍️ أهلاً بك في متجر Nike & Adidas للأواعي والماركات!"
                            soundManager.playChime()
                        }
                        else -> {
                            if (prop.name.startsWith("MallDiningTable_")) {
                                soundManager.playEatingSound()
                                celebrationToast = "🍽️🪑 جلست على ${prop.arabicName} وتناولت وجبتك الهنية في ساحة طعام المول!"
                            } else if (prop.price > 0 && playerMoney >= prop.price) {
                                playerMoney -= prop.price
                                saveManager.playerMoney = playerMoney
                                celebrationToast = "🛒 تم: ${prop.actionText} (-${prop.price}$)"
                                if (prop.name.startsWith("Shelf") || prop.name.contains("Cafe", ignoreCase = true)) {
                                    soundManager.playEatingSound()
                                } else {
                                    soundManager.playChime()
                                }
                            } else {
                                celebrationToast = "✅ تم استخدام: ${prop.arabicName}"
                                soundManager.playFootstep()
                            }
                        }
                    }
                },
                onExitBuilding = {
                    exitCurrentInterior()
                    soundManager.playDoorOpen()
                },
                currentFloor = currentVillaFloor,
                onSwitchFloor = { targetFloor ->
                    glSurfaceView?.renderer?.let { r ->
                        if (targetFloor == 2) {
                            r.interiorPlayerPos.set(8.2f, 4.6f, 2.0f)
                            currentVillaFloor = 2
                            celebrationToast = "🪜 صعدت إلى الطابق الثاني من الفيلا!"
                        } else {
                            r.interiorPlayerPos.set(0f, 0f, 8.8f)
                            currentVillaFloor = 1
                            celebrationToast = "🪜 نزلت إلى الطابق الأول من الفيلا!"
                        }
                    }
                    soundManager.playFootstep()
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
            )
        }

        // 3. Desktop Top Command & Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Left Status Pill (Desktop Mode, Money, Clock, Player, Shortcut Legend)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xDC0B1324)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0x3338BDF8),
                            shape = RoundedCornerShape(7.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                            modifier = Modifier.testTag("desktop_mode_badge")
                        ) {
                            Text(
                                text = "🖥️ PC 3D",
                                color = Color(0xFF7DD3FC),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "$$playerMoney",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        if (isDriving && currentCar?.type != VehicleType.AIRPLANE) {
                            val numFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0x3338BDF8),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                                modifier = Modifier.testTag("wheel_rotations_badge")
                            ) {
                                Text(
                                    text = "🔄 لفات العجلة: ${numFormatter.format(currentCarWheelRotations)}",
                                    color = Color(0xFF7DD3FC),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        val time = currentTimeOfDayHours
                        val totalMinutes = (time * 60f).toInt() % 1440
                        val hours = (totalMinutes / 60) % 24
                        val mins = totalMinutes % 60
                        val timePeriod = when (hours) {
                            in 5..11 -> "الصبح 🌅"
                            12 -> "الظهر ☀️"
                            in 13..17 -> "العصر 🌤️"
                            in 18..20 -> "المغرب 🌇"
                            0 -> "منتصف الليل 🌙"
                            else -> "الليل 🌃"
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (hours in 6..18) Color(0x33F59E0B) else Color(0x336366F1),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (hours in 6..18) Color(0xFFFBBF24) else Color(0xFF818CF8)
                            ),
                            modifier = Modifier
                                .focusProperties { canFocus = false }
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable {
                                // Time skip toggle: 22:30 الليل الدائم -> 08:00 الصبح -> 13:00 الظهر -> 18:30 المغرب -> 22:30 الليل الدائم
                                glSurfaceView?.renderer?.let { r ->
                                    val (nextHour, lockNight) = when {
                                        r.isNightLocked || r.timeOfDayHours > 19.5f || r.timeOfDayHours < 6f -> 8.0f to false
                                        r.timeOfDayHours < 11.5f -> 13.0f to false
                                        r.timeOfDayHours < 17.5f -> 18.5f to false
                                        else -> 22.5f to true
                                    }
                                    r.isNightLocked = lockNight
                                    isNightLocked = lockNight
                                    r.timeOfDayHours = nextHour
                                    currentTimeOfDayHours = nextHour
                                    soundManager.playChime()
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (hours in 6..18) Icons.Default.LightMode else Icons.Default.Nightlight,
                                    contentDescription = "وقت المدينة",
                                    tint = if (hours in 6..18) Color(0xFFFBBF24) else Color(0xFFA5B4FC),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = String.format("%02d:%02d", hours, mins),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = timePeriod,
                                    color = if (hours in 6..18) Color(0xFFFDE68A) else Color(0xFFC7D2FE),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Player Name Tag (Clickable to change/write name)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .focusProperties { canFocus = false }
                                .pointerHoverIcon(PointerIcon.Hand)
                                .clickable { showNameEditDialog = true }
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🟡", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = playerName,
                                color = Color(0xFFFFD600),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (isDesktopViewport) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "M خريطة • Tab مهام • V كاميرا • Esc إعدادات",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Desktop Quick Actions Toolbar
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Settings / Pause
                    FilledIconButton(
                        onClick = { showSettings = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xCC0F172A)),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_settings_button")
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Settings (Esc)", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Missions
                    FilledIconButton(
                        onClick = { showMissions = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (activeMission != null) Color(0xFF2563EB) else Color(0xCC0F172A)
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_missions_button")
                    ) {
                        Icon(Icons.Default.TrackChanges, contentDescription = "Missions (Tab)", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Character Profile & Name Button («شخصيتي واسمي»)
                    Surface(
                        onClick = { showNameEditDialog = true },
                        shape = CircleShape,
                        color = Color(0xFFD97706),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD600)),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_character_name_button")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_character_ambition),
                            contentDescription = "Character Specs & Name",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Motor City Car Dealership & Garage («مدينة المحركات»)
                    FilledIconButton(
                        onClick = {
                            inspectedDealershipCar = null
                            showMotorCityDialog = true
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFF97316)),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_motor_city_button")
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = "Motor City Dealership", tint = Color.White, modifier = Modifier.size(19.dp))
                    }

                    // Car Customization Workshop («ورشة تعديل السيارة»)
                    Surface(
                        onClick = { showCarCustomizationDialog = true },
                        color = Color(0xFF0F766E),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF2DD4BF)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_car_workshop_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🔧", fontSize = 14.sp)
                            Text(
                                text = "تعديل السيارة",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Exclusive New 8M$ Supercar Shortcut («السيارة الجديدة 8,000,000$»)
                    Surface(
                        onClick = {
                            inspectedDealershipCar = MotorCityData.getCarById("car_new_phantom_x8")
                            showMotorCityDialog = true
                        },
                        color = Color(0xFF0E7490),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF22D3EE)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_new_8m_car_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🚀", fontSize = 14.sp)
                            Text(
                                text = "الجديدة 8M",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Place for Purchasing Villas, Food, Drinks & Water («مكان شراء الفلل والطعام والشراب والماء»)
                    FilledIconButton(
                        onClick = { showVillasFoodWaterDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF0D9488)),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFF00E5FF), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_villas_food_water_mall_button")
                    ) {
                        Text("🏡", fontSize = 16.sp)
                    }

                    // Direct Luxury Villas Market Button («مكان شراء الفلل (3,950,000 - 100,000,000 ر.س / $)»)
                    FilledIconButton(
                        onClick = { showHousingDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFD97706)),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFFFFD700), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_villas_catalog_direct_button")
                    ) {
                        Text("👑", fontSize = 16.sp)
                    }

                    // Food Delivery Hub («طلبات الطعام»)
                    Surface(
                        onClick = { showTalabatFoodDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFF5A00),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFD600)),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_talabat_phone_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("🍔", fontSize = 15.sp)
                            Column {
                                Text(
                                    text = "طلبات الطعام",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "كل أكل العالم 🍕",
                                    color = Color(0xFFFFE082),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Gamer Blox R-1 Poster & Showcase Dialog («مواصفات وبطاقة Gamer Blox R-1»)
                    FilledIconButton(
                        onClick = { showGamerBloxShowcaseDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF0369A1)),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFF00E5FF), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_gamer_blox_showcase_button")
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = "Gamer Blox R-1 Specs & Drive", tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                    }

                    // Travel Hub Terminal
                    FilledIconButton(
                        onClick = { showTravelTerminal = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF0284C7)),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_travel_button")
                    ) {
                        Icon(Icons.Default.Map, contentDescription = "Travel", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Traffic & Signals Control Button
                    FilledIconButton(
                        onClick = { showTrafficControl = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF15803D)),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_traffic_button")
                    ) {
                        Icon(Icons.Default.Traffic, contentDescription = "Traffic", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Camera 1st/3rd person toggle
                    FilledIconButton(
                        onClick = {
                            cameraMode = if (cameraMode == CameraMode.THIRD_PERSON) CameraMode.FIRST_PERSON else CameraMode.THIRD_PERSON
                            glSurfaceView?.renderer?.cameraMode = cameraMode
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (cameraMode == CameraMode.FIRST_PERSON) Color(0xFF7C3AED) else Color(0xCC0F172A)
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("toggle_camera_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Camera (V)", tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    // Police vs Robbers Event Button («حدث شرطة وحرامية»)
                    FilledIconButton(
                        onClick = { showPoliceEventDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isPoliceEventActive) Color(0xFFDC2626) else Color(0xFF1E3A8A)
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFF3B82F6), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_police_event_button")
                    ) {
                        Text("🚨", fontSize = 16.sp)
                    }

                    // Real Estate & Houses Button («بيوت وشقق وعقارات»)
                    FilledIconButton(
                        onClick = { showHousingDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF059669)),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFF10B981), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_housing_button")
                    ) {
                        Text("🏡", fontSize = 16.sp)
                    }

                    // 4-Street Directory & Neighborhoods Button («دليل شوارع وبيوت المدينة»)
                    FilledIconButton(
                        onClick = { showStreetDirectoryDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF7C3AED)),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFFC4B5FD), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_street_directory_button")
                    ) {
                        Text("🏘️", fontSize = 16.sp)
                    }

                    // Street Houses Market Button («مكان شراء بيوت الشارع»)
                    FilledIconButton(
                        onClick = { showStreetHousesMarketDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF047857)),
                        modifier = Modifier
                            .size(38.dp)
                            .border(1.5.dp, Color(0xFFFBBF24), CircleShape)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_street_houses_market_button")
                    ) {
                        Text("🔑", fontSize = 16.sp)
                    }

                    // Quick Teleport to City Center («🏙️ المدينة»)
                    Surface(
                        onClick = {
                            resetControlInputs()
                            stopMetroRideSilently()
                            if (isDriving) {
                                isDriving = false
                                isTestDriving = false
                                currentCar?.isPlayerDriving = false
                                currentCar?.speed = 0f
                                currentCar = null
                            }
                            if (isInsideBuilding) {
                                suppressedAutoDoorId = lastEnteredBuildingId
                                isInsideBuilding = false
                            }
                            glSurfaceView?.renderer?.let { r ->
                                r.isPlayerInVehicle = false
                                r.currentVehicle = null
                                r.isInsideBuilding = false
                                r.currentInteriorType = InteriorType.NONE
                                r.playerPos.set(8.0f, 0f, 6.5f)
                                r.playerYaw = 195f
                                r.cameraYawOffset = 195f
                                r.cameraPitch = 18f
                            }
                            saveManager.savePosition(8.0f, 6.5f)
                            soundManager.playChime()
                            celebrationToast = "🏙️ أنت الآن في وسط المدينة!"
                        },
                        color = Color(0xFF9333EA),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF38BDF8)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("go_to_city_center_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🏙️", fontSize = 15.sp)
                            Text(
                                text = "المدينة",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Clothing & Brands Boutique Button («مكان الأواعي والماركات - نايكي وأديداس»)
                    Surface(
                        onClick = {
                            clothingInitialTab = 0
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                        },
                        color = Color(0xFFE11D48),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFFD600)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_clothing_store_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("👕", fontSize = 15.sp)
                            Text(
                                text = "الأواعي",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Personal Portable Wardrobe Button («خزانتي معي - مكان من معاي»)
                    Surface(
                        onClick = {
                            clothingInitialTab = 1
                            clothingInitialBrand = null
                            clothingInitialSlot = null
                            showClothingStoreDialog = true
                        },
                        color = Color(0xFF4F46E5),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF38BDF8)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_my_wardrobe_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("👔", fontSize = 15.sp)
                            Text(
                                text = "خزانتي",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // City Filters Toggle Pill («🎨 فلاتر المدينة»)
                    Surface(
                        onClick = { showCityFiltersStrip = !showCityFiltersStrip },
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(currentCityFilter.accentHex)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("toggle_city_filters_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🎨", fontSize = 15.sp)
                            Text(
                                text = "فلاتر",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Commercial Grand Mall Button («🏬 المول: أواعي وخضار وأكل وطاولات»)
                    Surface(
                        onClick = {
                            grandMallInitialTab = MallShopTab.VEGETABLES
                            showGrandMallDialog = true
                        },
                        color = Color(0xFF7E22CE),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE879F9)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_grand_mall_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🏬", fontSize = 15.sp)
                            Text(
                                text = "المول",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Direct Robot Control Mode Toggle («🤖 تحكم بالروبوت في الأرض والسيارة»)
                    Surface(
                        onClick = { setRobotControlMode(!isControllingRobot) },
                        color = if (isControllingRobot) Color(0xFF0891B2) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.2.dp,
                            if (isControllingRobot) Color(0xFF67E8F9) else Color(0xFF38BDF8)
                        ),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("toggle_robot_control_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🤖", fontSize = 15.sp)
                            Text(
                                text = if (isControllingRobot) "روبوت ✓" else "روبوت",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Elevated Sky Metro & 5 EGP Tickets Button («🚇 المترو الكهربائي المعلق والتذاكر»)
                    Surface(
                        onClick = { showMetroTicketDialog = true },
                        color = if (isRidingMetro) Color(0xFF0284C7) else Color(0xFF0E7490),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFFACC15)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_metro_tickets_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🚇", fontSize = 15.sp)
                            Text(
                                text = if (metroTicketsCount > 0) "المترو (${metroTicketsCount}🎫)" else "المترو 5ج",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // My Metro Card Selling Shop Shortcut («💳 بيع كروت المترو»)
                    Surface(
                        onClick = { showMetroCardShopDialog = true },
                        color = Color(0xFF0E7490),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF22D3EE)),
                        modifier = Modifier
                            .height(38.dp)
                            .focusProperties { canFocus = false }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .testTag("open_metro_card_shop_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("💳", fontSize = 14.sp)
                            Text(
                                text = "بيع كروت (${metroCardsSoldCount})",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Live 3D City Visual Filters Bar directly visible on the City HUD («فلاتر على المدينة نراها»)
                if (!isInsideBuilding && showCityFiltersStrip) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CityFilter.entries.forEach { filter ->
                            val isSelected = currentCityFilter == filter
                            val accent = Color(filter.accentHex)
                            Surface(
                                onClick = {
                                    currentCityFilter = filter
                                    glSurfaceView?.renderer?.cityFilter = filter
                                    soundManager.playChime()
                                    celebrationToast = "🎨 تم تفعيل فلتر المدينة: ${filter.emoji} ${filter.arabicName}"
                                },
                                color = if (isSelected) accent else Color(0xCC0F172A),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 1.8.dp else 1.dp,
                                    color = if (isSelected) Color.White else accent.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier
                                    .height(30.dp)
                                    .focusProperties { canFocus = false }
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("city_filter_chip_${filter.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = filter.emoji, fontSize = 12.sp)
                                    Text(
                                        text = filter.arabicName,
                                        color = if (isSelected) Color(0xFF0B1120) else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right Radar Minimap
            val r = glSurfaceView?.renderer
            val allMapVehicles = (r?.vehicles ?: emptyList()) + (r?.trafficSystem?.allActiveTrafficVehicles() ?: emptyList())
            MinimapView(
                playerPos = r?.playerPos ?: Vector3(53.8f, 0f, 163.5f),
                playerYaw = r?.playerYaw ?: 0f,
                buildings = r?.getBuildings() ?: emptyList(),
                vehicles = allMapVehicles,
                activeMissionPos = activeMission?.targetPosition,
                onOpenFullMap = { showFullMap = true },
                policeCarPos = eventPoliceVehicle?.let { Vector3(it.posX, it.posY, it.posZ) },
                robberCarPos = eventRobberVehicle?.let { Vector3(it.posX, it.posY, it.posZ) },
                autonomousRobots = r?.trafficSystem?.autonomousRobots ?: emptyList(),
                metroTrains = r?.skyMetroSystem?.trains ?: emptyList(),
                radarSize = if (isCompactHeight) 106.dp else 124.dp
            )
        }

        // 3.3.5. Nearby Autonomous Robot Status Pill OR Active Robot Control Banner
        if (isControllingRobot && !isInsideBuilding && !isPoliceEventActive) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (showCityFiltersStrip) 96.dp else 62.dp, start = 16.dp, end = 16.dp)
                    .testTag("active_robot_control_banner"),
                color = Color(0xEE083344),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22D3EE))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isDriving) "🤖🚗 الروبوت يقود السيارة (⬆️ قدام • ⬇️ ورا • ⬅️ شمال • ➡️ يمين)" else "🤖🌿 تحكم بالروبوت على الأرض الخضراء",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    if (!isDriving) {
                        Surface(
                            onClick = { launchRobotCarDrive() },
                            color = Color(0xFF7C3AED),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("launch_robot_car_button")
                        ) {
                            Text(
                                text = "🚗 اركب سيارة الروبوت",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        } else if (!isInsideBuilding && !isPoliceEventActive) {
            nearbyAutonomousRobot?.let { robot ->
            Surface(
                onClick = { setRobotControlMode(true, "🤖 انتقلت للتحكم بالروبوت ${robot.arabicName}!") },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (showCityFiltersStrip) 96.dp else 62.dp, start = 16.dp, end = 16.dp)
                    .testTag("nearby_robot_status_pill"),
                color = Color(0xEA0B1324),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (robot.isInCar) Color(0xFF38BDF8) else Color(0xFF4ADE80)
                )
            ) {
                Text(
                    text = "🤖 ${robot.arabicName}: ${robot.currentStatusArabic} • [اضغط للتحكم]",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
            }
        }

        // 3.4. Current Street Zone Classification Banner («تصنيف الشارع الحالي»)
        if (!isInsideBuilding && !isPoliceEventActive && activeMission == null) {
            currentStreetZone?.let { zone ->
            val zoneColor = Color(zone.accentColorHex)
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (showCityFiltersStrip) 130.dp else 98.dp, start = 16.dp, end = 16.dp)
                    .clickable { showStreetDirectoryDialog = true }
                    .testTag("street_zone_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xE60B1120)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, zoneColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = zone.iconEmoji, fontSize = 18.sp)
                    Column {
                        Text(
                            text = "${zone.arabicName} (${zone.englishName})",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "تصنيف الشارع: ${zone.classificationArabic}",
                            color = zoneColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            }
        }

        // 3.4.5. Stationary Water Swimming Indicator (أنت تسبح داخل مياه ثابتة وعميقة)
        if (isSwimmingNow && !isDriving && !isRidingMetro) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (isInsideBuilding) 136.dp else 156.dp)
                    .testTag("swimming_status_banner"),
                color = Color(0xEB0369A1),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8))
            ) {
                Text(
                    text = when {
                        isInsideBuilding -> "🏊 أنت تسبح الآن داخل مسبح الفيلا العميق (مياه ثابتة)"
                        isInCompoundSeaNow -> "🏖️ أنت تسبح الآن في بحر كومباوند 90 أفينيو الخلاب!"
                        isInCompoundPoolNow -> "🏊 أنت تسبح الآن في مسبح كومباوند 90 أفينيو الأولمبي!"
                        else -> "🏊 أنت تسبح الآن في المياه الثابتة المستقرة"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // 3.4.6. Active Elevated Sky Metro Train HUD Banner (Destination Stop Requests + Per-Chair 3D Seating Buttons)
        val ridingMetroNow = activeMetroTrain
        if (isRidingMetro && ridingMetroNow != null && !isInsideBuilding) {
            val nextStation = glSurfaceView?.renderer?.skyMetroSystem?.findNextStationAhead(ridingMetroNow.progressMeters)
            val stoppedStation = ridingMetroNow.stoppedAtStationId?.let { sid ->
                SkyMetroSystem.STATIONS.find { it.id == sid }
            }
            val requestedStop = ridingMetroNow.requestedStopStationId?.let { rsid ->
                SkyMetroSystem.STATIONS.find { it.id == rsid }
            }
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (showCityFiltersStrip) 130.dp else 98.dp, start = 12.dp, end = 12.dp)
                    .testTag("active_metro_ride_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xF2082F49)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF22D3EE))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🚇", fontSize = 20.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ridingMetroNow.arabicName,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = when {
                                    stoppedStation != null -> "🟢 متوقف الآن في: ${stoppedStation.arabicName}"
                                    requestedStop != null -> "🔔 طلب نزول مفعل عند: ${requestedStop.shortLabel} • السرعة ${(ridingMetroNow.speedMs * 3.6f).toInt()} km/h"
                                    else -> "⚡ في الهواء (${(ridingMetroNow.speedMs * 3.6f).toInt()} km/h) • التالي: ${nextStation?.shortLabel ?: ""}"
                                },
                                color = Color(0xFF67E8F9),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        }
                        Button(
                            onClick = { exitMetroRide(stoppedStation ?: nextStation) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("alight_metro_button")
                        ) {
                            Text("🚪 نزول", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Destination Stop Request Chips (any of the 4 outer city towers)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯 النزول:",
                            color = Color(0xFFFACC15),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                        for (st in SkyMetroSystem.STATIONS) {
                            val isReq = ridingMetroNow.requestedStopStationId == st.id
                            Surface(
                                onClick = {
                                    ridingMetroNow.requestedStopStationId = if (isReq) null else st.id
                                    soundManager.playChime()
                                    celebrationToast = if (!isReq) {
                                        "🔔 تم طلب التوقف والنزول في ${st.arabicName}!"
                                    } else {
                                        "✖️ تم إلغاء طلب النزول"
                                    }
                                },
                                color = if (isReq) Color(0xFF0EA5E9) else Color(0xFF132D46),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isReq) 1.5.dp else 1.dp,
                                    color = if (isReq) Color(0xFFFACC15) else Color(0xFF38BDF8).copy(alpha = 0.45f)
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("metro_stop_request_${st.id}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = if (isReq) "🔔 ${st.shortLabel}" else st.shortLabel,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Interactive 3D Chair Selection Row (1 Button for EVERY Chair inside Metro + Stand Up)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🪑 اختر كرسي:",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        )
                        for (seat in SkyMetroSystem.CAR_SEATS) {
                            val isSittingHere = selectedMetroSeat?.id == seat.id
                            Surface(
                                onClick = {
                                    if (isSittingHere) {
                                        selectedMetroSeat = null
                                        glSurfaceView?.renderer?.selectedMetroSeat = null
                                        celebrationToast = "🧍 وقفت داخل مقصورة المترو"
                                    } else {
                                        selectedMetroSeat = seat
                                        glSurfaceView?.renderer?.let { r ->
                                            r.selectedMetroSeat = seat
                                            r.cameraYawOffset = 0f
                                            r.cameraPitch = 12f
                                        }
                                        soundManager.playChime()
                                        celebrationToast = "🪑 جلست الآن على ${seat.arabicName} داخل المترو!"
                                    }
                                },
                                color = if (isSittingHere) Color(0xFF16A34A) else Color(0xFF1E3A5F),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSittingHere) 2.dp else 1.dp,
                                    color = if (isSittingHere) Color(0xFF86EFAC) else Color(0xFF60A5FA).copy(alpha = 0.6f)
                                ),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("metro_seat_${seat.id}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = if (isSittingHere) "✅ ${seat.shortLabel}" else seat.shortLabel,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        if (selectedMetroSeat != null) {
                            Surface(
                                onClick = {
                                    selectedMetroSeat = null
                                    glSurfaceView?.renderer?.selectedMetroSeat = null
                                    celebrationToast = "🧍 وقفت في ممر القطار"
                                },
                                color = Color(0xFF7C2D12),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDBA74)),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("metro_stand_up_button")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                ) {
                                    Text(
                                        text = "🧍 وقوف",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3.5. Police vs Robbers Live Event HUD Banner
        if (isPoliceEventActive) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (policeEventRole == "POLICE") Color(0xEE1E3A8A) else Color(0xEE78350F)
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (policeEventRole == "POLICE") Color(0xFF3B82F6) else Color(0xFFF59E0B)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = if (policeEventRole == "POLICE") "🚨" else "🦹", fontSize = 22.sp)
                    Column {
                        Text(
                            text = if (policeEventRole == "POLICE") "دورك: شرطي 👮 | طارد الحرامي واقبض عليه!"
                            else "دورك: حرامي 🦹 | اهرب من دوريات الشرطة!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "الوقت المتبقي: ${policeEventTimer.toInt()}s • مطاردة بالسيارة 🚗",
                            color = Color(0xFFFDE047),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }

                    if (policeEventRole == "POLICE") {
                        val pCar = eventPoliceVehicle
                        val rCar = eventRobberVehicle
                        val dist = if (pCar != null && rCar != null) {
                            kotlin.math.sqrt(
                                (pCar.posX - rCar.posX) * (pCar.posX - rCar.posX) +
                                (pCar.posZ - rCar.posZ) * (pCar.posZ - rCar.posZ)
                            )
                        } else 999f

                        if (dist < 14f) {
                            Button(
                                onClick = {
                                    soundManager.playChime()
                                    celebrationToast = "🎉 كفو يا بطل! الشرطة أمسكت الحرامي وأودعته السجن 🔒!"
                                    endPoliceEvent()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("arrest_robber_button")
                            ) {
                                Text("🚨 إلقاء القبض", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 4. Mission Goal Notification Banner
        activeMission?.let { missionBanner ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 96.dp, start = 16.dp, end = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xEE1E3A8A)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = missionBanner.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "المهمة: ${missionBanner.titleArabic}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (missionBanner.timeLimitSec > 0) {
                                "الوجهة: ${missionBanner.targetDistrict.arabicName}  •  ⏳ ${missionBanner.remainingTimeSec}ث"
                            } else {
                                "الوجهة: ${missionBanner.targetDistrict.arabicName}"
                            },
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 5. Celebration Toast
        AnimatedVisibility(
            visible = celebrationToast != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(20.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xF015803D)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF4ADE80))
            ) {
                Text(
                    text = celebrationToast ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(20.dp)
                )
            }
            LaunchedEffect(celebrationToast) {
                delay(3000)
                celebrationToast = null
            }
        }

        // 6. Action Interaction Prompt (Near Door, Metro Turnstile Gate, Sky Platform or Car)
        if (!isInsideBuilding && !isRidingMetro) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ground Metro Turnstile Gate (5 EGP Entry Lock to Climb Stairs)
                if (!isDriving && nearbyTurnstileStation != null) {
                    nearbyTurnstileStation?.let { st ->
                        val isGateOpen = glSurfaceView?.renderer?.skyMetroSystem?.unlockedStationGates?.contains(st.id) == true
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .widthIn(max = 580.dp)
                                .testTag("metro_turnstile_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isGateOpen) Color(0xEE064E3B) else Color(0xEE0B1120)
                            ),
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isGateOpen) Color(0xFF34D399) else Color(0xFFFACC15)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "🚇 ${st.arabicName}",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                                if (isGateOpen) {
                                    Text(
                                        text = "✅ البوابة مفتوحة! اصعد الدرج الآن إلى المحطة المعلقة فوق الهواء 🪜⚡",
                                        color = Color(0xFFA7F3D0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                } else {
                                    Text(
                                        text = "🎟️ بوابة التذاكر: ادفع ${SkyMetroSystem.TICKET_PRICE_EGP} جنيه (تذاكرك: ${metroTicketsCount}🎫) للدخول وصعود الدرج!",
                                        color = Color(0xFFFDE047),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { unlockStationTurnstile(st) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1.5f)
                                                .height(48.dp)
                                                .testTag("pay_5_egp_turnstile_button")
                                        ) {
                                            Text(
                                                text = if (metroTicketsCount > 0) {
                                                    "🎫 استخدم تذكرة وافتح البوابة"
                                                } else {
                                                    "💳 ادفع 5 جنيه لدخول المترو"
                                                },
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Button(
                                            onClick = { showMetroTicketDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(0.9f)
                                                .height(48.dp)
                                                .testTag("open_metro_booth_from_gate")
                                        ) {
                                            Text("🎟️ التذاكر", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Elevated Sky Platform Boarding Card (Y = 10.5m)
                if (!isDriving && standingMetroStation != null) {
                    standingMetroStation?.let { st ->
                        val trainReady = boardableMetroTrain
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .widthIn(max = 580.dp)
                                .testTag("sky_metro_boarding_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xF0082F49)),
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (trainReady != null) Color(0xFF4ADE80) else Color(0xFF38BDF8)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🚉 أنت على رصيف ${st.arabicName} (في الهواء ✨)",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                                if (trainReady != null) {
                                    Button(
                                        onClick = { boardMetroTrain(trainReady) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("board_sky_metro_button")
                                    ) {
                                        Text(
                                            text = "⚡🚇 ركوب ${trainReady.arabicName} الآن!",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 13.sp
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "⏳ انتظر ثوانٍ لوصول القطار الكهربائي على السكة المعلقة...",
                                        color = Color(0xFF7DD3FC),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
                // Enter Door or Dealership Button
                nearbyBuildingForDoor?.takeIf { it.isEnterable }?.let { b ->
                    if (b.type == BuildingType.CAR_DEALERSHIP) {
                        Button(
                            onClick = {
                                suppressedAutoDoorId = b.id
                                inspectedDealershipCar = null
                                showMotorCityDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("enter_dealership_building_button")
                        ) {
                            Text("🏎️ دخول سوق «مدينة المحركات» وتصفح المعرض", fontWeight = FontWeight.Bold)
                        }
                    } else if (b.id == "real_estate_agency") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    lastEnteredBuildingId = b.id
                                    suppressedAutoDoorId = b.id
                                    isInsideBuilding = true
                                    currentInteriorType = b.interiorType
                                    glSurfaceView?.renderer?.let { r ->
                                        r.isInsideBuilding = true
                                        r.currentInteriorType = b.interiorType
                                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
                                        r.interiorPlayerYaw = 180f
                                        r.cameraYawOffset = 180f
                                        r.cameraPitch = 18f
                                    }
                                    soundManager.playFootstep()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("enter_real_estate_agency_button")
                            ) {
                                Icon(Icons.Default.DoorFront, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🏢 دخول مقر بيع الفلل", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    showHousingDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("open_villas_catalog_button")
                            ) {
                                Text("🏡 شراء الفلل (3.95M-100M ر.س/$)", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (b.id == "metro_card_sales_office") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    lastEnteredBuildingId = b.id
                                    suppressedAutoDoorId = b.id
                                    isInsideBuilding = true
                                    currentInteriorType = b.interiorType
                                    glSurfaceView?.renderer?.let { r ->
                                        r.isInsideBuilding = true
                                        r.currentInteriorType = b.interiorType
                                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
                                        r.interiorPlayerYaw = 180f
                                        r.cameraYawOffset = 180f
                                        r.cameraPitch = 18f
                                    }
                                    soundManager.playDoorOpen()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E7490)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("enter_metro_card_shop_button")
                            ) {
                                Icon(Icons.Default.DoorFront, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🏢 دخول مركزي لبيع الكروت", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { showMetroCardShopDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("open_metro_card_counter_outside_button")
                            ) {
                                Text("💳 بيع كروت الآن", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (b.id == "mall_villas_food_water") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    lastEnteredBuildingId = b.id
                                    suppressedAutoDoorId = b.id
                                    isInsideBuilding = true
                                    currentInteriorType = b.interiorType
                                    glSurfaceView?.renderer?.let { r ->
                                        r.isInsideBuilding = true
                                        r.currentInteriorType = b.interiorType
                                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
                                        r.interiorPlayerYaw = 180f
                                        r.cameraYawOffset = 180f
                                        r.cameraPitch = 18f
                                    }
                                    soundManager.playFootstep()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("enter_mall_villas_food_water_button")
                            ) {
                                Icon(Icons.Default.DoorFront, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🏢 دخول المركز", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    showVillasFoodWaterDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("open_villas_food_water_dialog_button")
                            ) {
                                Text("🏡🍖 شراء الفلل والطعام والشراب والماء", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (b.id == "street_houses_market") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    lastEnteredBuildingId = b.id
                                    suppressedAutoDoorId = b.id
                                    isInsideBuilding = true
                                    currentInteriorType = b.interiorType
                                    glSurfaceView?.renderer?.let { r ->
                                        r.isInsideBuilding = true
                                        r.currentInteriorType = b.interiorType
                                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
                                        r.interiorPlayerYaw = 180f
                                        r.cameraYawOffset = 180f
                                        r.cameraPitch = 18f
                                    }
                                    soundManager.playFootstep()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("enter_street_houses_market_button")
                            ) {
                                Icon(Icons.Default.DoorFront, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🏢 دخول المكتب", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = {
                                    showStreetHousesMarketDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("open_street_houses_market_door_button")
                            ) {
                                Text("🏘️🔑 شراء بيوت الشارع", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else if (b.streetZone != null || b.facadeDescription.isNotBlank() || b.salePrice > 0 || b.rentPrice > 0 || b.isLockedByDefault || b.houseSideQuest != HouseSideQuest.NONE) {
                        val isOwned = purchasedHouseIds.contains(b.id)
                        val isRented = rentedHouseIds.contains(b.id)
                        val isUnlocked = !b.isLockedByDefault || unlockedHouseIds.contains(b.id)
                        val isQuestDone = completedHouseQuests.contains(b.id)
                        val zoneAccent = Color(b.streetZone?.accentColorHex ?: 0xFF38BDF8)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .widthIn(max = 580.dp)
                                .testTag("street_house_interaction_card"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xF00B1120)),
                            shape = RoundedCornerShape(18.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, zoneAccent)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${b.iconEmoji} ${b.arabicName}",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (b.streetZone != null) {
                                        Surface(
                                            color = zoneAccent.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, zoneAccent)
                                        ) {
                                            Text(
                                                text = b.streetZone.arabicName,
                                                color = zoneAccent,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                if (b.facadeDescription.isNotBlank()) {
                                    Text(
                                        text = b.facadeDescription,
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp
                                    )
                                }

                                if (b.listingBadge != null) {
                                    Text(
                                        text = if (isOwned) "✅ هذا العقار مملوك لك بالكامل!"
                                        else if (isRented) "✅ مستأجر باسمك — أهلاً بك!"
                                        else if (!isUnlocked) "🔒 هذا البيت مغلق بالمفتاح، أحتاج إلى واحد."
                                        else "🏷️ ${b.listingBadge}",
                                        color = if (isOwned || isRented) Color(0xFF4ADE80) else if (!isUnlocked) Color(0xFFFCA5A5) else Color(0xFFFDE68A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (doorKnockMessage != null) {
                                    Surface(
                                        color = Color(0x3338BDF8),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                                    ) {
                                        Text(
                                            text = doorKnockMessage ?: "",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }

                                // Primary Actions Row: Knock + Enter / Unlock
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            soundManager.playDoorOpen()
                                            doorKnockMessage = when {
                                                !isUnlocked && !hasMasterGardenKey ->
                                                    "🚪 طرق.. طرق! هل من أحد بالداخل؟\n🔒 هذا البيت مغلق بالمفتاح، أحتاج إلى واحد."
                                                !isUnlocked && hasMasterGardenKey ->
                                                    "🚪 طرق.. طرق! هل من أحد بالداخل؟\n🗝️ معك المفتاح المفقود — اضغط فتح الباب للدخول!"
                                                b.houseSideQuest == HouseSideQuest.REPAIR_NEIGHBOR_FENCE && !isQuestDone ->
                                                    "🚪 طرق.. طرق! هل من أحد بالداخل؟\n👴 الجار: أهلاً يا جاري! هل يمكنك مساعدتي في إصلاح سياج منزلي؟"
                                                b.houseSideQuest == HouseSideQuest.BASEMENT_TREASURE && !isQuestDone ->
                                                    "🚪 طرق.. طرق! هل من أحد بالداخل؟\n🕯️ لا أحد يجيب... لكن هناك شائعات عن وجود كنز في هذا القبو!"
                                                else ->
                                                    "🚪 طرق.. طرق! هل من أحد بالداخل؟\n✅ تم فتح الباب. يمكنك الدخول الآن."
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("knock_door_button")
                                    ) {
                                        Text("🚪 طرق الباب", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    if (!isUnlocked) {
                                        if (hasMasterGardenKey) {
                                            Button(
                                                onClick = {
                                                    saveManager.unlockHouse(b.id)
                                                    unlockedHouseIds = saveManager.unlockedHouseIds
                                                    soundManager.playChime()
                                                    doorKnockMessage = "🔓 تم فتح الباب. يمكنك الدخول الآن!"
                                                    celebrationToast = "🔓 تم فتح باب «${b.arabicName}» بالمفتاح!"
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1.3f)
                                                    .height(48.dp)
                                                    .testTag("unlock_door_with_key_button")
                                            ) {
                                                Text("🗝️ افتح بالمفتاح", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        } else if (b.houseSideQuest == HouseSideQuest.FIND_GARDEN_KEY) {
                                            Button(
                                                onClick = {
                                                    saveManager.hasMasterGardenKey = true
                                                    hasMasterGardenKey = true
                                                    saveManager.unlockHouse(b.id)
                                                    unlockedHouseIds = saveManager.unlockedHouseIds
                                                    if (saveManager.completeHouseQuest(b.id, 0)) {
                                                        completedHouseQuests = saveManager.completedHouseQuests
                                                    }
                                                    soundManager.playChime()
                                                    doorKnockMessage = "🗝️ وجدت المفتاح المفقود في الحديقة! تم فتح الباب. يمكنك الدخول الآن."
                                                    celebrationToast = "🗝️🎉 وجدت المفتاح المفقود في الحديقة! فُتح الباب بنجاح."
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1.4f)
                                                    .height(48.dp)
                                                    .testTag("find_garden_key_button")
                                            ) {
                                                Text("🔍 مفتاح الحديقة", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    soundManager.playDoorOpen()
                                                    celebrationToast = "🔒 هذا البيت مغلق بالمفتاح، أحتاج إلى واحد! ابحث عن المفتاح المفقود في حديقة الحي القديم."
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1.2f)
                                                    .height(48.dp)
                                                    .testTag("locked_door_button")
                                            ) {
                                                Text("🔒 مغلق بمفتاح", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                if (b.rentPrice > 0 && !isRented && !isOwned) {
                                                    if (saveManager.rentHouse(b.id, b.rentPrice)) {
                                                        rentedHouseIds = saveManager.rentedHouseIds
                                                        unlockedHouseIds = saveManager.unlockedHouseIds
                                                        playerMoney = saveManager.playerMoney
                                                        celebrationToast = "🗝️ تم استئجار ودخول «${b.arabicName}» (-${b.rentPrice}$)!"
                                                    } else {
                                                        celebrationToast = "⚠️ تحتاج ${b.rentPrice}$ للإيجار الفوري!"
                                                        return@Button
                                                    }
                                                }
                                                lastEnteredBuildingId = b.id
                                                suppressedAutoDoorId = b.id
                                                isInsideBuilding = true
                                                currentVillaFloor = 1
                                                currentInteriorType = b.interiorType
                                                glSurfaceView?.renderer?.let { r ->
                                                    r.isInsideBuilding = true
                                                    r.currentInteriorType = b.interiorType
                                                    r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
                                                    r.interiorPlayerYaw = 180f
                                                    r.cameraYawOffset = 180f
                                                    r.cameraPitch = 16f
                                                }
                                                soundManager.playDoorOpen()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier
                                                .weight(1.3f)
                                                .height(48.dp)
                                                .testTag("enter_street_house_button")
                                        ) {
                                            Icon(Icons.Default.DoorFront, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (b.rentPrice > 0 && !isRented && !isOwned) "إيجار ودخول (${b.rentPrice}$)" else "دخول البيت",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                // Secondary Row: Sale (150,000) + House Side Quests
                                if ((b.salePrice > 0 && !isOwned) || (b.houseSideQuest != HouseSideQuest.NONE && b.houseSideQuest != HouseSideQuest.FIND_GARDEN_KEY && !isQuestDone)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (b.salePrice > 0 && !isOwned) {
                                            val formatted = NumberFormat.getNumberInstance(Locale.US).format(b.salePrice)
                                            Button(
                                                onClick = {
                                                    if (saveManager.buyHouse(b.id, b.salePrice)) {
                                                        purchasedHouseIds = saveManager.purchasedHouseIds
                                                        unlockedHouseIds = saveManager.unlockedHouseIds
                                                        playerMoney = saveManager.playerMoney
                                                        soundManager.playCarPurchaseChime()
                                                        celebrationToast = "🎉 مبروك! اشتريت «${b.arabicName}» بسعر $formatted عملة!"
                                                    } else {
                                                        celebrationToast = "⚠️ السعر المعروض: $formatted عملة! رصيدك الحالي $playerMoney$"
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .testTag("buy_street_house_button")
                                            ) {
                                                Text("🏠 شراء ($formatted$)", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                            }
                                        }

                                         if (b.houseSideQuest == HouseSideQuest.REPAIR_NEIGHBOR_FENCE && !isQuestDone) {
                                            Button(
                                                onClick = {
                                                    if (saveManager.completeHouseQuest(b.id, 0)) {
                                                        completedHouseQuests = saveManager.completedHouseQuests
                                                        soundManager.playChime()
                                                        celebrationToast = "🛠️✅ ساعدت الجار في إصلاح سياج منزله بنجاح!"
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .testTag("repair_fence_quest_button")
                                            ) {
                                                Text("🛠️ إصلاح سياج الجار", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                         } else if (b.houseSideQuest == HouseSideQuest.BASEMENT_TREASURE && !isQuestDone) {
                                            Button(
                                                onClick = {
                                                    if (saveManager.completeHouseQuest(b.id, 0)) {
                                                        completedHouseQuests = saveManager.completedHouseQuests
                                                        soundManager.playCarPurchaseChime()
                                                        celebrationToast = "💎🎉 فتشت القبو الغامض وعثرت على التحفة الأثرية!"
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .testTag("basement_treasure_quest_button")
                                            ) {
                                                Text("💎 كنز القبو", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                // Mailbox Row: Place Envelope in front of the house («مكان وضع الظرف أمام البيت»)
                                if (b.hasEnvelopeMailbox()) {
                                    val envCount = saveManager.getHouseEnvelopes(b.id).size
                                    Button(
                                        onClick = {
                                            activeMailboxBuilding = b
                                            activeMailboxEnvelopes = saveManager.getHouseEnvelopes(b.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("open_house_mailbox_button")
                                    ) {
                                        Text(
                                            text = if (envCount > 0) "✉️ صندوق الظرف أمام البيت ($envCount)" else "✉️ ضع ظرف أمام البيت",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    lastEnteredBuildingId = b.id
                                    suppressedAutoDoorId = b.id
                                    isInsideBuilding = true
                                    currentVillaFloor = 1
                                    currentInteriorType = b.interiorType
                                    glSurfaceView?.renderer?.let { r ->
                                        r.isInsideBuilding = true
                                        r.currentInteriorType = b.interiorType
                                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(b.interiorType))
                                        r.interiorPlayerYaw = 180f
                                        r.cameraYawOffset = 180f
                                        r.cameraPitch = 16f
                                    }
                                    soundManager.playFootstep()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("enter_building_button")
                            ) {
                                Icon(Icons.Default.DoorFront, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("دخول ${b.arabicName}", fontWeight = FontWeight.Bold)
                            }
                            if (b.hasEnvelopeMailbox()) {
                                val envCount = saveManager.getHouseEnvelopes(b.id).size
                                Button(
                                    onClick = {
                                        activeMailboxBuilding = b
                                        activeMailboxEnvelopes = saveManager.getHouseEnvelopes(b.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("open_house_mailbox_button")
                                ) {
                                    Text(
                                        text = if (envCount > 0) "✉️ صندوق الظرف أمام البيت ($envCount)" else "✉️ ضع ظرف أمام البيت",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Standalone Front-of-House Envelope Mailbox Prompt when standing right by the mailbox
                if (!isDriving && nearbyMailboxBuilding != null && nearbyBuildingForDoor?.id != nearbyMailboxBuilding?.id) {
                    nearbyMailboxBuilding?.let { mbBuilding ->
                        val envCount = saveManager.getHouseEnvelopes(mbBuilding.id).size
                        Button(
                            onClick = {
                                activeMailboxBuilding = mbBuilding
                                activeMailboxEnvelopes = saveManager.getHouseEnvelopes(mbBuilding.id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("standalone_mailbox_button")
                        ) {
                            Text(
                                text = if (envCount > 0) {
                                    "✉️ صندوق الظرف — ${mbBuilding.arabicName} ($envCount)"
                                } else {
                                    "✉️ ضع ظرف أمام ${mbBuilding.arabicName}"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Rooftop Airplane Parking Pad Ladder / Descend Prompt
                if (!isDriving && nearbyRooftopHouse != null) {
                    nearbyRooftopHouse?.let { rHouse ->
                        val padY = rHouse.getRooftopAirplanePadY() ?: 10f
                        val onRoofAlready = (glSurfaceView?.renderer?.playerPos?.y ?: 0f) >= padY - 0.85f
                        Button(
                            onClick = {
                                glSurfaceView?.renderer?.let { r ->
                                    if (onRoofAlready) {
                                        r.playerPos.set(rHouse.posX - rHouse.width / 2f - 1.6f, 0f, rHouse.posZ)
                                        playerVelocityY = 0f
                                        celebrationToast = "⬇️ نزلت عبر السلم من مهبط سطح ${rHouse.arabicName}"
                                    } else {
                                        r.playerPos.set(rHouse.posX - rHouse.width / 2f + 1.4f, padY, rHouse.posZ)
                                        playerVelocityY = 0f
                                        celebrationToast = "🪜 صعدت إلى مهبط الطيارة فوق سطح ${rHouse.arabicName} ✈️"
                                    }
                                }
                                soundManager.playFootstep()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (onRoofAlready) Color(0xFF0F766E) else Color(0xFF0284C7)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("rooftop_ladder_action_button")
                        ) {
                            Text(
                                text = if (onRoofAlready) {
                                    "⬇️ النزول من السقف للأرض (${rHouse.arabicName})"
                                } else {
                                    "🪜 صعود إلى مهبط الطيارة فوق سطح ${rHouse.arabicName}"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Showroom Display Car Inspection Card
                // Tactical Light Vehicle / Airplane Entry Button
                if (!isDriving && nearbyVehicleForEntry != null) {
                    nearbyVehicleForEntry?.let { car ->
                        val isPlane = car.type == VehicleType.AIRPLANE
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier
                                    .focusProperties { canFocus = false }
                                    .clickable { enterVehicle(car) }
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(2.dp, Color(0xFF4ADE80), RoundedCornerShape(16.dp))
                                    .testTag("enter_car_tactical_button"),
                                color = Color(0xF2064E3B),
                                shadowElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = "Ride", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(
                                            text = if (isPlane) "✈️ ركوب الطائرة (E)" else "🚗 ركوب السيارة (E)",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = "${car.displayName} • اضغط E أو انقر للقيادة",
                                            color = Color(0xFF86EFAC),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            nearbyDealershipCar?.let { dCar ->
                                Button(
                                    onClick = {
                                        inspectedDealershipCar = dCar
                                        showMotorCityDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.testTag("inspect_car_hud_button")
                                ) {
                                    Text("🔍 مواصفات وشراء", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Desktop Bottom HUD & Action Dock (Keyboard Status + Mouse-Clickable Controls)
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            val activeHudCar = currentCar
            val isWPressed = pressedKeys[Key.W.keyCode] == true || pressedKeys[Key.DirectionUp.keyCode] == true
            val isSPressed = pressedKeys[Key.S.keyCode] == true || pressedKeys[Key.DirectionDown.keyCode] == true
            val isAPressed = pressedKeys[Key.A.keyCode] == true || pressedKeys[Key.DirectionLeft.keyCode] == true
            val isDPressed = pressedKeys[Key.D.keyCode] == true || pressedKeys[Key.DirectionRight.keyCode] == true
            val isShiftPressedNow = pressedKeys[Key.ShiftLeft.keyCode] == true || pressedKeys[Key.ShiftRight.keyCode] == true || isRunning || isNitroEngaged
            val isSpacePressedNow = pressedKeys[Key.Spacebar.keyCode] == true || isHandbrakeEngaged
            val isQPressed = pressedKeys[Key.Q.keyCode] == true || pressedKeys[Key.PageUp.keyCode] == true || flightClimbInput > 0.1f
            val isZPressed = pressedKeys[Key.Z.keyCode] == true || pressedKeys[Key.PageDown.keyCode] == true || flightClimbInput < -0.1f

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isDriving && activeHudCar != null && activeHudCar.type == VehicleType.AIRPLANE) {
                    hoveredRooftopHouse?.let { roofHouse ->
                        Surface(
                            color = Color(0xE60369A1),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                            modifier = Modifier.testTag("airplane_rooftop_target_badge")
                        ) {
                            Text(
                                text = "✈️ فوق مهبط سطح ${roofHouse.arabicName} — اضغط Z للهبوط والصف فوق البيت!",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left: Compact Mouse-Drag Circle-Inside-Circle Analog Wheel + Desktop Keyboard Status Bar
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isRidingMetro) {
                            VirtualJoystick(
                                size = if (isCompactHeight) 94.dp else 104.dp,
                                knobSize = if (isCompactHeight) 38.dp else 42.dp,
                                isDriving = isDriving,
                                onMove = { x, y ->
                                    circleControlX = x
                                    circleControlY = y
                                    syncKeyboardControls()
                                },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
                            )
                        }

                        if (isDesktopViewport || isDriving || isRidingMetro) {
                            Surface(
                                color = Color(0xE3091122),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF38BDF8)),
                                shadowElevation = 8.dp,
                                modifier = Modifier.testTag("desktop_keyboard_controls_bar")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                            // WASD Keycap Cluster
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    if (isDriving && activeHudCar?.type == VehicleType.AIRPLANE) {
                                        DesktopKeyCap(label = "Q", active = isQPressed, accent = Color(0xFF34D399))
                                    }
                                    DesktopKeyCap(label = "W", active = isWPressed, accent = Color(0xFF38BDF8))
                                    if (isDriving && activeHudCar?.type == VehicleType.AIRPLANE) {
                                        DesktopKeyCap(label = "Z", active = isZPressed, accent = Color(0xFF38BDF8))
                                    }
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    DesktopKeyCap(label = "A", active = isAPressed, accent = Color(0xFF38BDF8))
                                    DesktopKeyCap(label = "S", active = isSPressed, accent = Color(0xFF38BDF8))
                                    DesktopKeyCap(label = "D", active = isDPressed, accent = Color(0xFF38BDF8))
                                }
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                DesktopKeyCap(
                                    label = if (isDriving) "Shift نايترو" else "Shift جري",
                                    active = isShiftPressedNow,
                                    accent = Color(0xFFF59E0B),
                                    wide = true
                                )
                                DesktopKeyCap(
                                    label = if (isDriving) "Space فرامل" else "Space قفز",
                                    active = isSpacePressedNow,
                                    accent = Color(0xFFEF4444),
                                    wide = true
                                )
                            }

                            if (isRidingMetro) {
                                Text(
                                    text = "👀 اسحب بالماوس أو U/O للمشاهدة • عجلة الماوس للزوم • E نزول",
                                    color = Color(0xFF67E8F9),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (isDriving && activeHudCar != null) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "${activeHudCar.displayName} • $currentCarSpeedKmH km/h",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                    val fuelPct = currentCarFuel.toInt().coerceIn(0, 100)
                                    val nitroPct = currentCarNitroFuel.toInt().coerceIn(0, 100)
                                    val altText = if (activeHudCar.type == VehicleType.AIRPLANE) {
                                        " • ✈️ ارتفاع ${activeHudCar.posY.toInt()}م"
                                    } else ""
                                    Text(
                                        text = "⛽ ${fuelPct}% • ⚡ ${nitroPct}%$altText",
                                        color = if (fuelPct < 20) Color(0xFFFCA5A5) else Color(0xFF93C5FD),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                    } else if (isDesktopViewport) {
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = "🖥️ تحكم الكمبيوتر (كيبورد + ماوس)",
                                                color = Color(0xFF38BDF8),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "WASD / الأسهم: حركة • سحب الماوس: كاميرا 360° • عجلة الماوس: زوم • E: تفاعل",
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right: Mouse-Clickable Desktop Action Dock (with Shortcut Badges)
                    if (isDriving && activeHudCar != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            if (activeHudCar.type == VehicleType.AIRPLANE) {
                                Surface(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .focusProperties { canFocus = false }
                                        .holdTouchInput("airplane_descend") { held ->
                                            flightClimbInput = if (held) -1f else 0f
                                        }
                                        .testTag("airplane_descend_button"),
                                    color = if (flightClimbInput < -0.1f) Color(0xFF0369A1) else Color(0xDD1E293B),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8))
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                                        Text("🛬 هبوط (Z)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .focusProperties { canFocus = false }
                                        .holdTouchInput("airplane_climb") { held ->
                                            flightClimbInput = if (held) 1f else 0f
                                        }
                                        .testTag("airplane_climb_button"),
                                    color = if (flightClimbInput > 0.1f) Color(0xFF059669) else Color(0xDD047857),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF6EE7B7))
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                                        Text("🛫 صعود (Q)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    activeHudCar.refillFuel(100f)
                                    currentCarFuel = 100f
                                    soundManager.playEngineRev(1.2f)
                                    celebrationToast = "⛽ تم ملء خزان البنزين بالكامل (100%)!"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNearGasStation || currentCarFuel < 20f) Color(0xFF16A34A) else Color(0xCC0284C7)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .testTag("car_refuel_button")
                            ) {
                                Text("⛽ (F)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    if (activeHudCar.nitroFuel > 5f) {
                                        isNitroEngaged = !isNitroEngaged
                                        if (isNitroEngaged) soundManager.playEngineRev(activeHudCar.soundPitch * 1.35f)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isNitroEngaged) Color(0xFF00E5FF) else Color(0xCC0369A1),
                                    contentColor = if (isNitroEngaged) Color.Black else Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .testTag("car_nitro_boost_button")
                            ) {
                                Text("⚡ نايترو", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { soundManager.playHorn() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .testTag("car_horn_button")
                            ) {
                                Text("📢 (H)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            if (activeHudCar.type != VehicleType.AIRPLANE) {
                                Button(
                                    onClick = { showCarCustomizationDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .focusProperties { canFocus = false }
                                        .testTag("driving_customize_car_button")
                                ) {
                                    Text("🔧 تعديل", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            if (activeHudCar.type != VehicleType.AIRPLANE && !isTestDriving) {
                                Button(
                                    onClick = { parkCurrentCarInUndergroundGarage() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .focusProperties { canFocus = false }
                                        .testTag("park_car_button")
                                ) {
                                    Text("🅿️ كراج (B)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Button(
                                onClick = { exitVehicle() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .testTag("exit_car_button")
                            ) {
                                Text("نزول (E)", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            }
                        }
                    } else if (isRidingMetro && activeMetroTrain != null) {
                        val exitTargetStation = activeMetroTrain?.stoppedAtStationId?.let { sid ->
                            SkyMetroSystem.STATIONS.find { it.id == sid }
                        } ?: glSurfaceView?.renderer?.skyMetroSystem?.findNextStationAhead(activeMetroTrain?.progressMeters ?: 0f)
                        Button(
                            onClick = { exitMetroRide(exitTargetStation) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .focusProperties { canFocus = false }
                                .testTag("exit_metro_ride_hud_button")
                        ) {
                            Text(
                                text = "🚪 النزول في ${exitTargetStation?.arabicName ?: "المحطة"} (E)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    glSurfaceView?.renderer?.let { r ->
                                        r.cameraYawOffset = if (isInsideBuilding) 180f else 0f
                                        r.cameraPitch = 18f
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC0F172A)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("reset_camera_onfoot_button")
                            ) {
                                Text("🎯 توسيط [R]", color = Color(0xFF7DD3FC), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { isRunning = !isRunning },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRunning) Color(0xFFF97316) else Color(0xCC1E293B)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("sprint_button")
                            ) {
                                Text("🏃 جري [Shift]", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { triggerPlayerJump() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .focusProperties { canFocus = false }
                                    .pointerHoverIcon(PointerIcon.Hand)
                                    .testTag("jump_button")
                            ) {
                                Text("⬆️ قفز [Space]", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }

        // 8. Dialogs
        if (showFullMap) {
            FullMapDialog(
                playerPos = glSurfaceView?.renderer?.playerPos ?: Vector3(53.8f, 0f, 163.5f),
                buildings = glSurfaceView?.renderer?.getBuildings() ?: emptyList(),
                onFastTravel = { dest ->
                    stopMetroRideSilently()
                    glSurfaceView?.renderer?.let { r ->
                        r.playerPos.set(dest)
                        for (b in r.getBuildings()) {
                            b.resolveCollisionPush3D(r.playerPos.x, r.playerPos.y, r.playerPos.z, margin = 0.75f)?.let { (px, pz) ->
                                r.playerPos.x = px
                                r.playerPos.z = pz
                            }
                        }
                        r.skyMetroSystem.resolveCollisionPush3D(r.playerPos.x, r.playerPos.y, r.playerPos.z, margin = 0.75f)?.let { (px, pz) ->
                            r.playerPos.x = px
                            r.playerPos.z = pz
                        }
                        saveManager.savePosition(r.playerPos.x, r.playerPos.z)
                    }
                    soundManager.playChime()
                },
                onDismiss = { showFullMap = false }
            )
        }

        if (showMissions) {
            MissionDialog(
                missions = missions,
                activeMissionId = activeMission?.id,
                onStartMission = { mission ->
                    activeMission = mission.copy(
                        remainingTimeSec = mission.timeLimitSec,
                        status = MissionStatus.ACTIVE
                    )
                    glSurfaceView?.renderer?.activeMissionTargetPos = mission.targetPosition
                    soundManager.playChime()
                },
                onDismiss = { showMissions = false }
            )
        }

        if (showTravelTerminal) {
            TravelTerminalDialog(
                playerMoney = playerMoney,
                onBuyTicketAndTravel = { route ->
                    if (playerMoney >= route.ticketPrice) {
                        playerMoney -= route.ticketPrice
                        saveManager.playerMoney = playerMoney
                        stopMetroRideSilently()
                        soundManager.playChime()
                        // Teleport to destination safely outside building colliders
                        glSurfaceView?.renderer?.let { r ->
                            r.playerPos.set(route.destinationPos)
                            for (b in r.getBuildings()) {
                                b.resolveCollisionPush3D(r.playerPos.x, r.playerPos.y, r.playerPos.z, margin = 0.75f)?.let { (px, pz) ->
                                    r.playerPos.x = px
                                    r.playerPos.z = pz
                                }
                            }
                            r.skyMetroSystem.resolveCollisionPush3D(r.playerPos.x, r.playerPos.y, r.playerPos.z, margin = 0.75f)?.let { (px, pz) ->
                                r.playerPos.x = px
                                r.playerPos.z = pz
                            }
                            saveManager.savePosition(r.playerPos.x, r.playerPos.z)
                        }
                        showTravelTerminal = false
                        celebrationToast = "🚌 وصلت بحمد الله إلى: ${route.destinationArabic}"
                    }
                },
                onDismiss = { showTravelTerminal = false }
            )
        }

        if (showSettings) {
            SettingsDialog(
                isSoundEnabled = saveManager.isSoundEnabled,
                onToggleSound = {
                    saveManager.isSoundEnabled = it
                    soundManager.isMuted = !it
                },
                soundVolume = soundManager.volume,
                onChangeVolume = { soundManager.volume = it },
                currentTimeHours = glSurfaceView?.renderer?.timeOfDayHours ?: 22.5f,
                onSetTime = { hour ->
                    val lockNight = (hour > 19.5f || hour < 6f)
                    isNightLocked = lockNight
                    glSurfaceView?.renderer?.let { r ->
                        r.isNightLocked = lockNight
                        r.timeOfDayHours = hour
                    }
                    currentTimeOfDayHours = hour
                },
                isNightLocked = isNightLocked,
                onToggleNightLock = { locked ->
                    isNightLocked = locked
                    glSurfaceView?.renderer?.let { r ->
                        r.isNightLocked = locked
                        if (locked) {
                            r.timeOfDayHours = 22.5f
                            currentTimeOfDayHours = 22.5f
                        }
                    }
                },
                currentWeather = glSurfaceView?.renderer?.weather ?: Weather.CLEAR,
                onSetWeather = { glSurfaceView?.renderer?.weather = it },
                graphicsQuality = graphicsQuality,
                onSetGraphics = { q ->
                    graphicsQuality = q
                    saveManager.graphicsQuality = q
                    glSurfaceView?.setGraphicsQuality(q)
                },
                onResetProgress = {
                    saveManager.resetProgress()
                    playerMoney = saveManager.playerMoney
                    metroTicketsCount = saveManager.metroTicketsCount
                    stopMetroRideSilently()
                    totalDriveCashEarned = 0
                    purchasedHouseIds = saveManager.purchasedHouseIds
                    rentedHouseIds = saveManager.rentedHouseIds
                    unlockedHouseIds = saveManager.unlockedHouseIds
                    completedHouseQuests = saveManager.completedHouseQuests
                    hasMasterGardenKey = saveManager.hasMasterGardenKey
                    doorKnockMessage = null
                    missions = MissionData.getDefaultMissions()
                    activeMission = null
                    ownedCarIds = saveManager.ownedCarIds
                    glSurfaceView?.renderer?.let { r ->
                        r.activeMissionTargetPos = null
                        r.playerPos.set(8.0f, 0f, 6.5f)
                        r.playerYaw = 195f
                        r.cameraYawOffset = 195f
                        r.vehicles.find { it.id == "car_robot_cyber_free" }?.apply {
                            isInUndergroundGarage = false
                            posX = 6.2f; posY = 0f; posZ = 4.4f; yaw = 270f; speed = 0f
                        }
                    }
                },
                onDismiss = { showSettings = false }
            )
        }

        if (showTrafficControl) {
            val ts = glSurfaceView?.renderer?.trafficSystem
            if (ts != null) {
                TrafficControlDialog(
                    trafficSystem = ts,
                    isControllingRobot = isControllingRobot,
                    onToggleControlRobot = { setRobotControlMode(!isControllingRobot) },
                    onDriveRobotCar = { launchRobotCarDrive() },
                    onDismiss = { showTrafficControl = false },
                    onActionFeedback = { msg ->
                        celebrationToast = msg
                    }
                )
            }
        }

        if (showNameEditDialog) {
            NameEditDialog(
                currentName = playerName,
                currentMotto = characterMotto,
                onSaveName = { newName ->
                    playerName = newName
                    saveManager.playerName = newName
                    celebrationToast = "مرحباً بك يا $newName! تم اعتماد اسمك بنجاح ✨"
                },
                onSaveMotto = { newMotto ->
                    characterMotto = newMotto
                    saveManager.characterMotto = newMotto
                },
                onEquipAmbitionOutfit = {
                    val hoodieItem = ClothingCatalog.findById(ClothingCatalog.ROBLOX_AMBITION_TOP_ID)
                    if (hoodieItem != null) {
                        saveManager.equipClothingItem(hoodieItem)
                        val updatedOutfit = saveManager.getEquippedOutfit()
                        currentOutfit = updatedOutfit
                        glSurfaceView?.renderer?.playerOutfit = updatedOutfit
                        soundManager.playChime()
                        celebrationToast = "👕✨ تم ارتداء هودي «طموح بلا حدود» الأزرق (R) والسلسلة الذهبية!"
                    }
                },
                onDismiss = { showNameEditDialog = false }
            )
        }

        // 8. Active Test Drive Top HUD Banner
        if (isTestDriving && isDriving) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 56.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xEE0369A1)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🏎️ تجربة قيادة نشطة: ${currentCar?.displayName ?: "سيارة تجريبية"}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Button(
                        onClick = {
                            resetControlInputs()
                            isDriving = false
                            isTestDriving = false
                            currentCar?.isPlayerDriving = false
                            currentCar?.speed = 0f
                            currentCar = null
                            glSurfaceView?.renderer?.let { r ->
                                r.isPlayerInVehicle = false
                                r.currentVehicle = null
                                r.playerPos.set(306f, 0f, 128f) // Return to East Campus Motor City showroom
                            }
                            showMotorCityDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("إنهاء التجربة والشراء", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 9. «مدينة المحركات» (Motor City) Dealership & Garage Dialog
        if (showMotorCityDialog) {
            MotorCityDialog(
                initialCarId = inspectedDealershipCar?.id,
                playerMoney = playerMoney,
                ownedCarIds = ownedCarIds,
                onBuyCar = { car ->
                    val success = saveManager.buyCar(car.id, car.price)
                    if (success) {
                        playerMoney = saveManager.playerMoney
                        ownedCarIds = saveManager.ownedCarIds
                        soundManager.playCarPurchaseChime()
                        celebrationToast = "🎉 تهانينا! اشتريت ${car.nameArabic} بنجاح وحُفظت في مرآبك!"
                    }
                },
                onTestDrive = { car ->
                    resetControlInputs()
                    showMotorCityDialog = false
                    val trackStartPos = Vector3(342f, 0f, 60f)
                    val r = glSurfaceView?.renderer
                    var testVeh = r?.vehicles?.find { it.dealershipCarId == car.id }
                    if (testVeh == null) {
                        testVeh = Vehicle(
                            id = "test_${car.id}",
                            type = VehicleType.SPORTS_CAR,
                            posX = trackStartPos.x,
                            posY = 0f,
                            posZ = trackStartPos.z,
                            yaw = 0f,
                            colorPrimary = car.primaryColor,
                            colorSecondary = car.secondaryColor,
                            dealershipCarId = car.id,
                            displayName = car.nameArabic,
                            meshStyle = car.meshStyle,
                            customTopSpeed = car.topSpeedKmH,
                            customAccelRate = car.accelRate,
                            customBrakeRate = car.brakeRate,
                            soundPitch = car.soundPitch,
                            underglowColor = car.underglowColor
                        )
                        r?.vehicles?.add(testVeh)
                    }
                    testVeh.isInUndergroundGarage = false
                    testVeh.posX = trackStartPos.x
                    testVeh.posZ = trackStartPos.z
                    testVeh.yaw = 0f
                    testVeh.speed = 0f
                    testVeh.steeringAngle = 0f
                    testVeh.fuel = 100f
                    testVeh.nitroFuel = 100f
                    testVeh.isPlayerDriving = true

                    currentCar?.let { prev -> if (prev !== testVeh) prev.isPlayerDriving = false }
                    isDriving = true
                    currentCar = testVeh
                    currentCarFuel = testVeh.fuel
                    currentCarNitroFuel = testVeh.nitroFuel
                    currentCarSpeedKmH = 0
                    isTestDriving = true
                    r?.let {
                        it.playerPos.set(trackStartPos)
                        it.isPlayerInVehicle = true
                        it.currentVehicle = testVeh
                        it.cameraYawOffset = 0f
                        it.cameraPitch = 16f
                    }
                    soundManager.playDoorOpen()
                    soundManager.playEngineRev(car.soundPitch)
                    celebrationToast = "🏎️ انطلقت تجربة قيادة: ${car.nameArabic} في المسار المخصص!"
                },
                onDriveOwnedCar = { car ->
                    resetControlInputs()
                    showMotorCityDialog = false
                    saveManager.activeCarId = car.id
                    val pPos = glSurfaceView?.renderer?.playerPos ?: Vector3(53.8f, 0f, 163.5f)
                    val r = glSurfaceView?.renderer
                    var activeVeh = r?.vehicles?.find { it.dealershipCarId == car.id && !it.isShowroomDisplay }
                        ?: r?.vehicles?.find { it.dealershipCarId == car.id }
                    if (activeVeh == null) {
                        activeVeh = Vehicle(
                            id = "owned_${car.id}",
                            type = VehicleType.SPORTS_CAR,
                            posX = pPos.x + 2f,
                            posY = 0f,
                            posZ = pPos.z,
                            yaw = 0f,
                            colorPrimary = car.primaryColor,
                            colorSecondary = car.secondaryColor,
                            dealershipCarId = car.id,
                            displayName = car.nameArabic,
                            meshStyle = car.meshStyle,
                            customTopSpeed = car.topSpeedKmH,
                            customAccelRate = car.accelRate,
                            customBrakeRate = car.brakeRate,
                            soundPitch = car.soundPitch,
                            underglowColor = car.underglowColor
                        )
                        saveManager.applySavedCustomization(activeVeh)
                        r?.vehicles?.add(activeVeh)
                    }
                    activeVeh.isInUndergroundGarage = false
                    activeVeh.posX = pPos.x
                    activeVeh.posZ = pPos.z
                    activeVeh.speed = 0f
                    activeVeh.steeringAngle = 0f
                    activeVeh.fuel = activeVeh.fuel.coerceAtLeast(35f)
                    activeVeh.isPlayerDriving = true

                    currentCar?.let { prev -> if (prev !== activeVeh) prev.isPlayerDriving = false }
                    isDriving = true
                    currentCar = activeVeh
                    currentCarFuel = activeVeh.fuel
                    currentCarNitroFuel = activeVeh.nitroFuel
                    currentCarSpeedKmH = 0
                    isTestDriving = false
                    r?.let {
                        it.isPlayerInVehicle = true
                        it.currentVehicle = activeVeh
                        it.cameraYawOffset = 0f
                        it.cameraPitch = 16f
                    }
                    soundManager.playDoorOpen()
                    soundManager.playEngineRev(car.soundPitch)
                    celebrationToast = "🚗 تم تشغيل سيارتك الخاصة: ${car.nameArabic}"
                },
                onDismiss = { showMotorCityDialog = false }
            )
        }

        // 10. Gamer Blox R-1 Color Customizer Dialog
        if (showGamerBloxColorSheet) {
            GamerBloxColorDialog(
                currentColorIndex = saveManager.gamerBloxColorIndex,
                onSelectColor = { idx, color ->
                    saveManager.gamerBloxColorIndex = idx
                    currentCar?.let { c ->
                        c.colorPrimary = color.copyOf()
                        saveManager.saveVehicleCustomization(c)
                    }
                    celebrationToast = "🎨 تم تغيير لون السيارة بنجاح!"
                },
                onDismiss = { showGamerBloxColorSheet = false }
            )
        }

        // 11. Gamer Blox R-1 Poster & Showcase Dialog
        if (showGamerBloxShowcaseDialog) {
            GamerBloxShowcaseDialog(
                currentColorIndex = saveManager.gamerBloxColorIndex,
                onSelectColor = { idx, color ->
                    saveManager.gamerBloxColorIndex = idx
                    glSurfaceView?.renderer?.vehicles?.find { it.meshStyle == CarMeshStyle.ROBLOX_GAMER_BLOX }?.let { c ->
                        c.colorPrimary = color.copyOf()
                        saveManager.saveVehicleCustomization(c)
                    }
                    currentCar?.let { c ->
                        if (c.meshStyle == CarMeshStyle.ROBLOX_GAMER_BLOX) {
                            c.colorPrimary = color.copyOf()
                            saveManager.saveVehicleCustomization(c)
                        }
                    }
                    celebrationToast = "🎨 تم تغيير لون Gamer Blox R-1 بنجاح!"
                },
                onDriveNow = {
                    resetControlInputs()
                    val r = glSurfaceView?.renderer
                    var bloxCar = r?.vehicles?.find {
                        !it.isShowroomDisplay && (it.id == "car_robot_cyber_free" || it.meshStyle == CarMeshStyle.ROBLOX_GAMER_BLOX)
                    }
                    if (bloxCar == null) {
                        val pPos = r?.playerPos ?: Vector3(53.8f, 0f, 163.5f)
                        bloxCar = Vehicle(
                            id = "car_gamer_blox_r1",
                            type = VehicleType.SPORTS_CAR,
                            posX = pPos.x,
                            posY = 0f,
                            posZ = pPos.z,
                            yaw = 90f,
                            colorPrimary = floatArrayOf(0.96f, 0.96f, 0.98f, 1f),
                            colorSecondary = floatArrayOf(0.10f, 0.10f, 0.12f, 1f),
                            isPlayerDriving = true,
                            dealershipCarId = "car_gamer_blox_r1",
                            displayName = "Gamer Blox R-1 [Roblox]",
                            meshStyle = CarMeshStyle.ROBLOX_GAMER_BLOX,
                            customTopSpeed = 320f,
                            customAccelRate = 78f,
                            customBrakeRate = 96f,
                            soundPitch = 1.25f,
                            underglowColor = floatArrayOf(0.08f, 0.85f, 1.0f, 1.0f)
                        )
                        saveManager.applySavedCustomization(bloxCar)
                        r?.vehicles?.add(bloxCar)
                    }
                    bloxCar.isInUndergroundGarage = false
                    bloxCar.speed = 0f
                    bloxCar.steeringAngle = 0f
                    bloxCar.fuel = bloxCar.fuel.coerceAtLeast(35f)
                    currentCar?.let { prev -> if (prev !== bloxCar) prev.isPlayerDriving = false }
                    isDriving = true
                    currentCar = bloxCar
                    currentCarFuel = bloxCar.fuel
                    currentCarNitroFuel = bloxCar.nitroFuel
                    currentCarSpeedKmH = 0
                    bloxCar.isPlayerDriving = true
                    r?.let {
                        it.playerPos.set(bloxCar.posX, bloxCar.posY, bloxCar.posZ)
                        it.playerYaw = bloxCar.yaw
                        it.isPlayerInVehicle = true
                        it.currentVehicle = bloxCar
                        it.cameraYawOffset = 0f
                        it.cameraPitch = 16f
                    }
                    soundManager.playDoorOpen()
                    soundManager.playEngineRev(bloxCar.soundPitch)
                    celebrationToast = "🏎️ انطلقت في قيادة Gamer Blox R-1! استمتع بالسرعة الخارقة والنيترو!"
                },
                onDismiss = { showGamerBloxShowcaseDialog = false }
            )
        }

        // 12. Police vs Robbers Event Dialog («حدث شرطة وحرامية»)
        if (showPoliceEventDialog) {
            PoliceEventDialog(
                isEventActive = isPoliceEventActive,
                onStartEvent = { role ->
                    startPoliceEvent(role)
                },
                onEndEventEarly = {
                    endPoliceEvent()
                    celebrationToast = "🛑 تم إنهاء حدث الشرطة والحرامية وسحب السيارات."
                },
                onDismiss = { showPoliceEventDialog = false }
            )
        }

        // 13. Real Estate, Houses & Bed Sleeping Dialog («سوق البيوت وشقق وعقارات»)
        if (showHousingDialog) {
            HousingDialog(
                playerMoney = playerMoney,
                ownedVillaIds = ownedVillaIds,
                isApartmentRented = isApartmentRented,
                onBuyVilla = { villa ->
                    if (saveManager.buyVilla(villa.id, villa.price)) {
                        playerMoney = saveManager.playerMoney
                        ownedVillaIds = saveManager.ownedVillaIds
                        isVillaOwned = true
                        soundManager.playCarPurchaseChime()
                        celebrationToast = "🎉 مبروك! اشتريت «${villa.nameArabic}» للعيش فيها للأبد 🏡 بمبلغ (${String.format(java.util.Locale.US, "%,d ر.س / $", villa.price)})!"
                    } else {
                        val formattedPrice = String.format(java.util.Locale.US, "%,d ر.س / $", villa.price)
                        celebrationToast = "⚠️ رصيدك غير كافٍ لشراء «${villa.nameArabic}» ($formattedPrice)! قد سيارتك في أي مكان بالمدينة لتربح المال! 🚗💵"
                    }
                },
                onRentApartment = {
                    if (playerMoney >= 30) {
                        playerMoney -= 30
                        saveManager.playerMoney = playerMoney
                        isApartmentRented = true
                        soundManager.playChime()
                        celebrationToast = "🏢 تم استئجار الشقة السكنية في أبراج الواحة بنجاح!"
                    } else {
                        celebrationToast = "رصيدك غير كافٍ لاستئجار الشقة!"
                    }
                },
                onParkCarInGarage = { villa ->
                    val r = glSurfaceView?.renderer
                    val targetCar = currentCar?.takeIf { it.type != VehicleType.AIRPLANE }
                        ?: r?.vehicles?.firstOrNull { !it.isShowroomDisplay && (it.id == "car_robot_cyber_free" || it.dealershipCarId == saveManager.activeCarId) }
                    if (targetCar != null) {
                        targetCar.isInUndergroundGarage = false
                        targetCar.posX = villa.garageX
                        targetCar.posY = 0f
                        targetCar.posZ = villa.garageZ
                        targetCar.yaw = 0f
                        targetCar.speed = 0f
                        celebrationToast = "🚗 تم وضع سيارتك في كراج «${villa.nameArabic}» بأمان!"
                    }
                    soundManager.playChime()
                },
                onSleepInBed = {
                    val r = glSurfaceView?.renderer
                    if (r != null && !r.isNightLocked) {
                        r.timeOfDayHours = 8.0f
                        currentTimeOfDayHours = 8.0f
                    }
                    celebrationToast = if (r?.isNightLocked != false) {
                        "😴 نمت براحة في السرير واستيقظت بكامل طاقتك تحت القمر والنجوم 🌙✨!"
                    } else {
                        "😴 خلدت للنوم في السرير...\n☀️ صباح الخير! استيقظت بنشاط وحيوية واستعدت كامل طاقتك!"
                    }
                    soundManager.playChime()
                },
                onGoToVilla = { villa ->
                    lastEnteredBuildingId = villa.id
                    suppressedAutoDoorId = villa.id
                    isInsideBuilding = true
                    currentInteriorType = InteriorType.PLAYER_VILLA
                    glSurfaceView?.renderer?.let { r ->
                        r.playerPos.set(villa.doorX, 0f, villa.doorZ + 1.5f)
                        r.isInsideBuilding = true
                        r.currentInteriorType = InteriorType.PLAYER_VILLA
                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(InteriorType.PLAYER_VILLA))
                        r.interiorPlayerYaw = 180f
                        r.cameraYawOffset = 180f
                        r.cameraPitch = 18f
                    }
                    celebrationToast = "🏡 دخلت «${villa.nameArabic}»! كل شيء بالداخل جاهز للاستخدام بالكبسة."
                    soundManager.playFootstep()
                },
                onOpenStreetHousesMarket = { showStreetHousesMarketDialog = true },
                onDismiss = { showHousingDialog = false }
            )
        }

        // 14. Place for Purchasing Villas, Food, Drinks & Water («مكان شراء الفلل والطعام والشراب والماء»)
        if (showVillasFoodWaterDialog) {
            VillasFoodWaterDialog(
                playerMoney = playerMoney,
                ownedVillaIds = ownedVillaIds,
                onBuyVilla = { villa ->
                    if (saveManager.buyVilla(villa.id, villa.price)) {
                        playerMoney = saveManager.playerMoney
                        ownedVillaIds = saveManager.ownedVillaIds
                        isVillaOwned = true
                        soundManager.playCarPurchaseChime()
                        celebrationToast = "🎉 مبروك! اشتريت «${villa.nameArabic}» للعيش فيها للأبد 🏡 بمبلغ (${String.format(java.util.Locale.US, "%,d ر.س / $", villa.price)})!"
                    } else {
                        val formattedPrice = String.format(java.util.Locale.US, "%,d ر.س / $", villa.price)
                        celebrationToast = "⚠️ رصيدك غير كافٍ لشراء «${villa.nameArabic}» ($formattedPrice)! اركب وقد سيارتك في المدينة لكسب المال! 🚗💵"
                    }
                },
                onBuyFood = { item ->
                    if (playerMoney >= item.price) {
                        playerMoney -= item.price
                        saveManager.playerMoney = playerMoney
                        soundManager.playEatingSound()
                        celebrationToast = "🍖 تناولت «${item.nameArabic}» (-${item.price}$) نَم نَم نَم! شحنت طاقتك 100%!"
                    } else {
                        celebrationToast = "⚠️ لا تملك ${item.price}$ لشراء «${item.nameArabic}»!"
                    }
                },
                onBuyDrink = { item ->
                    if (playerMoney >= item.price) {
                        playerMoney -= item.price
                        saveManager.playerMoney = playerMoney
                        soundManager.playDrinkingSound()
                        celebrationToast = "🥤 ارتشفت «${item.nameArabic}» (-${item.price}$) منعش ولذيذ!"
                    } else {
                        celebrationToast = "⚠️ لا تملك ${item.price}$ لشراء «${item.nameArabic}»!"
                    }
                },
                onBuyWater = { item ->
                    if (item.price == 0) {
                        soundManager.playDrinkingSound()
                        celebrationToast = "💧 ارتشفت ماءً عذباً بارداً من برادة المركز! رويت عطشك بالكامل واستعدت نشاطك!"
                    } else if (playerMoney >= item.price) {
                        playerMoney -= item.price
                        saveManager.playerMoney = playerMoney
                        soundManager.playDrinkingSound()
                        celebrationToast = "💧 تم شراء «${item.nameArabic}» (-${item.price}$) مياه نقية عذبة!"
                    } else {
                        celebrationToast = "⚠️ لا تملك ${item.price}$ لشراء «${item.nameArabic}»!"
                    }
                },
                onGoToVilla = { villa ->
                    lastEnteredBuildingId = villa.id
                    suppressedAutoDoorId = villa.id
                    isInsideBuilding = true
                    currentInteriorType = InteriorType.PLAYER_VILLA
                    glSurfaceView?.renderer?.let { r ->
                        r.playerPos.set(villa.doorX, 0f, villa.doorZ + 1.5f)
                        r.isInsideBuilding = true
                        r.currentInteriorType = InteriorType.PLAYER_VILLA
                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(InteriorType.PLAYER_VILLA))
                        r.interiorPlayerYaw = 180f
                        r.cameraYawOffset = 180f
                        r.cameraPitch = 18f
                    }
                    celebrationToast = "🏡 دخلت «${villa.nameArabic}»! كل شيء بالداخل جاهز للاستخدام بالكبسة."
                    soundManager.playFootstep()
                },
                onDismiss = { showVillasFoodWaterDialog = false }
            )
        }

        // 15. Talabat World Food App Dialog («موقع وتطبيق طلبات — اطلب كل ما تحتاجه من طعام بالعالم»)
        if (showTalabatFoodDialog) {
            TalabatFoodDialog(
                playerMoney = playerMoney,
                onOrderDish = { dish ->
                    if (dish.price == 0) {
                        soundManager.playDrinkingSound()
                        celebrationToast = "💧 ارتشفت ماءً نقياً عذباً ومثلجاً من طلبات مجاناً! رويت عطشك بالكامل!"
                    } else if (playerMoney >= dish.price) {
                        playerMoney -= dish.price
                        saveManager.playerMoney = playerMoney
                        if (dish.isWater || dish.isBeverage) {
                            soundManager.playDrinkingSound()
                        } else {
                            soundManager.playEatingSound()
                        }
                        celebrationToast = "🛵 وصل طلبك من طلبات! تناولت «${dish.nameArabic}» (-${dish.price}$) واستعدت طاقتك 100%!"
                    } else {
                        celebrationToast = "⚠️ لا تملك ${dish.price}$ لطلب «${dish.nameArabic}» من طلبات! رصيدك: $playerMoney$"
                    }
                },
                onDismiss = { showTalabatFoodDialog = false }
            )
        }

        // 16. 4-Street City Directory Dialog («دليل شوارع وبيوت المدينة الأربعة»)
        if (showStreetDirectoryDialog) {
            StreetDirectoryDialog(
                buildings = glSurfaceView?.renderer?.getBuildings() ?: emptyList(),
                initialZone = currentStreetZone,
                playerMoney = playerMoney,
                purchasedHouseIds = purchasedHouseIds,
                rentedHouseIds = rentedHouseIds,
                unlockedHouseIds = unlockedHouseIds,
                completedHouseQuests = completedHouseQuests,
                hasMasterGardenKey = hasMasterGardenKey,
                onTeleportToBuilding = { target ->
                    resetControlInputs()
                    stopMetroRideSilently()
                    if (isDriving) {
                        isDriving = false
                        isTestDriving = false
                        currentCar?.isPlayerDriving = false
                        currentCar?.speed = 0f
                        currentCar = null
                    }
                    glSurfaceView?.renderer?.let { r ->
                        r.isPlayerInVehicle = false
                        r.currentVehicle = null
                        r.isInsideBuilding = false
                        r.currentInteriorType = InteriorType.NONE
                        r.playerPos.set(target.doorPosX, 0f, target.doorPosZ + 1.8f)
                        r.playerYaw = 180f
                        r.cameraYawOffset = 180f
                    }
                    saveManager.savePosition(target.doorPosX, target.doorPosZ + 1.8f)
                    isInsideBuilding = false
                    showStreetDirectoryDialog = false
                    soundManager.playChime()
                    celebrationToast = "📍 وصلت إلى: ${target.arabicName} (${target.streetZone?.arabicName ?: ""})"
                },
                onOpenStreetHousesMarket = { showStreetHousesMarketDialog = true },
                onDismiss = { showStreetDirectoryDialog = false }
            )
        }

        // 17. Street Houses Market Dialog («مكان شراء بيوت الشارع»)
        if (showStreetHousesMarketDialog) {
            StreetHousesMarketDialog(
                buildings = glSurfaceView?.renderer?.getBuildings() ?: emptyList(),
                playerPos = glSurfaceView?.renderer?.playerPos ?: Vector3(95f, 0f, 94.5f),
                playerMoney = playerMoney,
                purchasedHouseIds = purchasedHouseIds,
                onBuyHouse = { house ->
                    val price = if (house.salePrice > 0) house.salePrice else 25000
                    val formatted = NumberFormat.getNumberInstance(Locale.US).format(price)
                    if (saveManager.buyHouse(house.id, price)) {
                        purchasedHouseIds = saveManager.purchasedHouseIds
                        unlockedHouseIds = saveManager.unlockedHouseIds
                        playerMoney = saveManager.playerMoney
                        soundManager.playCarPurchaseChime()
                        celebrationToast = "🎉 مبروك! اشتريت «${house.arabicName}» بمبلغ $formatted$ وأصبح ملكك للأبد!"
                    } else {
                        celebrationToast = "⚠️ السعر المطلوب: $formatted$! رصيدك الحالي $playerMoney$ — قد سيارتك في أي مكان بالمدينة لتربح المال! 🚗💵"
                    }
                },
                onTeleportToHouse = { house ->
                    resetControlInputs()
                    if (isDriving) {
                        isDriving = false
                        isTestDriving = false
                        currentCar?.isPlayerDriving = false
                        currentCar?.speed = 0f
                        currentCar = null
                    }
                    glSurfaceView?.renderer?.let { r ->
                        r.isPlayerInVehicle = false
                        r.currentVehicle = null
                        r.isInsideBuilding = false
                        r.currentInteriorType = InteriorType.NONE
                        r.playerPos.set(house.doorPosX, 0f, house.doorPosZ + 2.0f)
                        r.playerYaw = 180f
                        r.cameraYawOffset = 180f
                    }
                    saveManager.savePosition(house.doorPosX, house.doorPosZ + 2.0f)
                    isInsideBuilding = false
                    soundManager.playChime()
                    celebrationToast = "📍 وصلت أمام باب «${house.arabicName}»!"
                },
                onEnterHouse = { house ->
                    resetControlInputs()
                    if (isDriving) {
                        isDriving = false
                        isTestDriving = false
                        currentCar?.isPlayerDriving = false
                        currentCar?.speed = 0f
                        currentCar = null
                    }
                    val targetInterior = if (house.interiorType == InteriorType.NONE) InteriorType.STANDARD_HOUSE else house.interiorType
                    lastEnteredBuildingId = house.id
                    suppressedAutoDoorId = house.id
                    isInsideBuilding = true
                    currentVillaFloor = 1
                    currentInteriorType = targetInterior
                    glSurfaceView?.renderer?.let { r ->
                        r.isPlayerInVehicle = false
                        r.currentVehicle = null
                        r.playerPos.set(house.doorPosX, 0f, house.doorPosZ + 2.0f)
                        r.isInsideBuilding = true
                        r.currentInteriorType = targetInterior
                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(targetInterior))
                        r.interiorPlayerYaw = 180f
                        r.cameraYawOffset = 180f
                        r.cameraPitch = 16f
                    }
                    saveManager.savePosition(house.doorPosX, house.doorPosZ + 2.0f)
                    soundManager.playDoorOpen()
                    celebrationToast = "🏡 أهلاً بك في بيتك «${house.arabicName}» (6 شبابيك مضيئة)!"
                },
                onDismiss = { showStreetHousesMarketDialog = false }
            )
        }

        // 18. Envelope Mailbox Dialog («مكان وضع الظرف أمام البيت في الشوارع»)
        activeMailboxBuilding?.let { mbBuilding ->
            EnvelopeMailboxDialog(
                building = mbBuilding,
                envelopes = activeMailboxEnvelopes,
                onPlaceEnvelope = { title, message ->
                    activeMailboxEnvelopes = saveManager.addHouseEnvelope(
                        houseId = mbBuilding.id,
                        title = title,
                        message = message,
                        sender = playerName
                    )
                    soundManager.playChime()
                    celebrationToast = "✉️✅ تم وضع الظرف أمام «${mbBuilding.arabicName}»!"
                },
                onRemoveEnvelope = { envId ->
                    activeMailboxEnvelopes = saveManager.removeHouseEnvelope(mbBuilding.id, envId)
                },
                onDismiss = { activeMailboxBuilding = null }
            )
        }

        // 19. Clothing & Brands Store + Personal Wardrobe («مكان الأواعي والماركات Nike/Adidas وخزانتي معي»)
        if (showClothingStoreDialog) {
            ClothingStoreDialog(
                playerMoney = playerMoney,
                ownedClothingIds = ownedClothingIds,
                currentOutfit = currentOutfit,
                initialTab = clothingInitialTab,
                initialBrandFilter = clothingInitialBrand,
                initialSlotFilter = clothingInitialSlot,
                onBuyAndWear = { item ->
                    if (saveManager.buyClothingItem(item)) {
                        playerMoney = saveManager.playerMoney
                        ownedClothingIds = saveManager.ownedClothingIds
                        val updatedOutfit = saveManager.getEquippedOutfit()
                        currentOutfit = updatedOutfit
                        glSurfaceView?.renderer?.playerOutfit = updatedOutfit
                        soundManager.playChime()
                        celebrationToast = "👕✅ اشتريت ولبست «${item.arabicName}» (${item.brand.arabicName}) فوراً!"
                    } else {
                        celebrationToast = "⚠️ رصيدك (${playerMoney}$) لا يكفي! تحتاج ${item.price}$ لشراء «${item.arabicName}» — قد سيارتك في المدينة لكسب المال 🚗💵"
                        soundManager.playFootstep()
                    }
                },
                onEquipOwned = { item ->
                    saveManager.equipClothingItem(item)
                    val updatedOutfit = saveManager.getEquippedOutfit()
                    currentOutfit = updatedOutfit
                    glSurfaceView?.renderer?.playerOutfit = updatedOutfit
                    soundManager.playChime()
                    celebrationToast = "✨ ارتديت «${item.arabicName}» على شخصيتك الآن!"
                },
                onDismiss = { showClothingStoreDialog = false }
            )
        }

        // 20. Grand Commercial Mall Dialog («المول التجاري الكبير: أواعي • خضار وفواكه • مطاعم • 12 طاولة»)
        if (showGrandMallDialog) {
            GrandMallDialog(
                initialTab = grandMallInitialTab,
                playerMoney = playerMoney,
                onBuyAndConsume = { item ->
                    if (item.price == 0) {
                        soundManager.playDrinkingSound()
                        celebrationToast = "💧 ارتشفت «${item.arabicName}» مجاناً في المول! منعش ونقي 100%!"
                    } else if (playerMoney >= item.price) {
                        playerMoney -= item.price
                        saveManager.playerMoney = playerMoney
                        when (item.kind) {
                            MallProductKind.VEGETABLE_FRUIT -> {
                                soundManager.playEatingSound()
                                celebrationToast = "${item.emoji}✅ اشتريت وأكلت «${item.arabicName}» (-${item.price}$) — صحة وطاقة 100%!"
                            }
                            MallProductKind.HOT_FOOD, MallProductKind.DESSERT -> {
                                soundManager.playEatingSound()
                                celebrationToast = "${item.emoji}🍽️ جلست على طاولة الطعام وأكلت «${item.arabicName}» (-${item.price}$)!"
                            }
                            MallProductKind.DRINK_WATER -> {
                                soundManager.playDrinkingSound()
                                celebrationToast = "${item.emoji}🥤 ارتشفت «${item.arabicName}» (-${item.price}$)!"
                            }
                            MallProductKind.SUPERMARKET_GOOD -> {
                                soundManager.playChime()
                                celebrationToast = "${item.emoji}🛍️ اشتريت «${item.arabicName}» من محلات المول (-${item.price}$)!"
                            }
                        }
                    } else {
                        celebrationToast = "⚠️ رصيدك ($playerMoney$) لا يكفي! تحتاج ${item.price}$ لشراء «${item.arabicName}»."
                    }
                },
                onOpenClothingStore = {
                    showGrandMallDialog = false
                    clothingInitialTab = 0
                    clothingInitialBrand = null
                    clothingInitialSlot = null
                    showClothingStoreDialog = true
                },
                onSitAtDiningTable = {
                    soundManager.playEatingSound()
                    celebrationToast = "🍽️🪑 جلست على إحدى الطاولات الـ 12 في ساحة طعام المول وتناولت وجبتك الهنية!"
                },
                onEnterMall3D = {
                    resetControlInputs()
                    showGrandMallDialog = false
                    if (isDriving) {
                        isDriving = false
                        isTestDriving = false
                        currentCar?.isPlayerDriving = false
                        currentCar?.speed = 0f
                        currentCar = null
                    }
                    lastEnteredBuildingId = "mall_grand"
                    suppressedAutoDoorId = "mall_grand"
                    isInsideBuilding = true
                    currentInteriorType = InteriorType.GRAND_MALL
                    glSurfaceView?.renderer?.let { r ->
                        r.isPlayerInVehicle = false
                        r.currentVehicle = null
                        r.playerPos.set(-95f, 0f, 124.8f)
                        r.isInsideBuilding = true
                        r.currentInteriorType = InteriorType.GRAND_MALL
                        r.interiorPlayerPos.set(BuildingInterior.getSpawnPosition(InteriorType.GRAND_MALL))
                        r.interiorPlayerYaw = 180f
                        r.cameraYawOffset = 180f
                        r.cameraPitch = 16f
                    }
                    soundManager.playDoorOpen()
                    celebrationToast = "🏬 أهلاً بك داخل المول التجاري الكبير! 12 طاولة طعام ومحلات أواعي وخضار وأكل أمامك!"
                },
                onDismiss = { showGrandMallDialog = false }
            )
        }

        // 21. Elevated Electric Sky Metro Ticket Booth & 5 EGP Gate Dialog («شباك تذاكر مترو المدينة المعلق 5 جنيه»)
        if (showMetroTicketDialog) {
            MetroTicketDialog(
                playerMoney = playerMoney,
                metroTicketsCount = metroTicketsCount,
                nearbyStation = nearbyTurnstileStation,
                onBuyTickets = { count, totalCost ->
                    if (saveManager.buyMetroTicket(count, SkyMetroSystem.TICKET_PRICE_EGP)) {
                        playerMoney = saveManager.playerMoney
                        metroTicketsCount = saveManager.metroTicketsCount
                        soundManager.playChime()
                        celebrationToast = "🎫✅ اشتريت $count تذكرة مترو بمبلغ $totalCost جنيه! تذاكرك الآن: $metroTicketsCount 🎫"
                    } else {
                        soundManager.playHorn()
                        celebrationToast = "⚠️ رصيدك الحالي ($playerMoney جنيه) لا يكفي لشراء $count تذكرة ($totalCost جنيه)!"
                    }
                },
                onUnlockTurnstileNow = { st ->
                    unlockStationTurnstile(st)
                    if (glSurfaceView?.renderer?.skyMetroSystem?.unlockedStationGates?.contains(st.id) == true) {
                        showMetroTicketDialog = false
                    }
                },
                onGoToStation = { st ->
                    resetControlInputs()
                    stopMetroRideSilently()
                    if (isDriving) {
                        isDriving = false
                        isTestDriving = false
                        currentCar?.isPlayerDriving = false
                        currentCar?.speed = 0f
                        currentCar = null
                    }
                    isInsideBuilding = false
                    currentInteriorType = InteriorType.NONE
                    glSurfaceView?.renderer?.let { r ->
                        r.isPlayerInVehicle = false
                        r.currentVehicle = null
                        r.isInsideBuilding = false
                        r.currentInteriorType = InteriorType.NONE
                        r.playerPos.set(st.groundEntryX, 0f, st.groundEntryZ)
                        r.playerYaw = 0f
                        r.cameraYawOffset = 0f
                        r.cameraPitch = 18f
                    }
                    saveManager.savePosition(st.groundEntryX, st.groundEntryZ)
                    soundManager.playChime()
                    celebrationToast = "🚇📍 وصلت أمام بوابة التذاكر والدرج في: ${st.arabicName} (الدخول 5 جنيه 🎫)"
                },
                onDismiss = { showMetroTicketDialog = false }
            )
        }

        // 22. Metro Card Selling Center Business Dialog («مركزي لبيع كروت المترو للزبائن»)
        if (showMetroCardShopDialog) {
            MetroCardShopDialog(
                playerMoney = playerMoney,
                cardsSoldCount = metroCardsSoldCount,
                totalRevenueEgp = metroShopTotalRevenue,
                cardStockCount = metroCardStockCount,
                onSellCard = { tier, customerName ->
                    val sold = saveManager.sellMetroCardToCustomer(tier.priceEgp, quantity = 1)
                    if (sold) {
                        playerMoney = saveManager.playerMoney
                        metroCardsSoldCount = saveManager.metroCardsSoldCount
                        metroShopTotalRevenue = saveManager.metroShopTotalRevenue
                        metroCardStockCount = saveManager.metroCardStockCount
                        soundManager.playChime()
                        celebrationToast = if (customerName != null) {
                            "💳✅ بعت ${tier.arabicTitle} إلى $customerName! ربحت +${tier.priceEgp} جنيه 💰"
                        } else {
                            "💳✅ تم بيع ${tier.arabicTitle} على الكونتر! ربحت +${tier.priceEgp} جنيه 💰"
                        }
                        true
                    } else {
                        soundManager.playHorn()
                        celebrationToast = "⚠️ نفد مخزون الكروت! اضغط «اطبع +10» لطباعة كروت مترو جديدة."
                        false
                    }
                },
                onPrintCards = { amount ->
                    val updated = saveManager.printNewMetroCards(amount)
                    metroCardStockCount = updated
                    soundManager.playChime()
                    celebrationToast = "🖨️✅ تم طباعة +$amount كروت مترو ذكية! المخزون المتاح: $updated 💳"
                },
                onTeleportToShop = {
                    resetControlInputs()
                    stopMetroRideSilently()
                    if (isDriving) {
                        isDriving = false
                        isTestDriving = false
                        currentCar?.isPlayerDriving = false
                        currentCar?.speed = 0f
                        currentCar = null
                    }
                    isInsideBuilding = false
                    currentInteriorType = InteriorType.NONE
                    glSurfaceView?.renderer?.let { r ->
                        r.isPlayerInVehicle = false
                        r.currentVehicle = null
                        r.isInsideBuilding = false
                        r.currentInteriorType = InteriorType.NONE
                        r.playerPos.set(-17.5f, 0f, 164.2f)
                        r.playerYaw = 180f
                        r.cameraYawOffset = 180f
                        r.cameraPitch = 18f
                    }
                    saveManager.savePosition(-17.5f, 164.2f)
                    soundManager.playChime()
                    celebrationToast = "💳📍 وصلت أمام محلك «مركزي لبيع كروت المترو»! ادخل من الباب وابدأ البيع."
                },
                onDismiss = { showMetroCardShopDialog = false }
            )
        }

        // 23. Car Customization Workshop Dialog («ورشة تعديل وتطوير السيارة»)
        if (showCarCustomizationDialog) {
            CarCustomizationDialog(
                vehicles = glSurfaceView?.renderer?.vehicles ?: emptyList(),
                currentCar = currentCar,
                ownedCarIds = ownedCarIds,
                onApplyCustomization = { updatedCar ->
                    saveManager.saveVehicleCustomization(updatedCar)
                    if (currentCar?.id == updatedCar.id) {
                        currentCarFuel = updatedCar.fuel
                        currentCarNitroFuel = updatedCar.nitroFuel
                    }
                    soundManager.playChime()
                },
                onResetToStock = { vehicleToReset ->
                    val key = vehicleToReset.dealershipCarId ?: vehicleToReset.id
                    saveManager.clearVehicleCustomization(key)
                    val stockCatalog = vehicleToReset.dealershipCarId?.let { MotorCityData.getCarById(it) }
                    val stockSpawn = CityData.generateVehicles().firstOrNull { it.id == vehicleToReset.id }
                    if (stockCatalog != null) {
                        vehicleToReset.colorPrimary = stockCatalog.primaryColor.copyOf()
                        vehicleToReset.colorSecondary = stockCatalog.secondaryColor.copyOf()
                        vehicleToReset.underglowColor = stockCatalog.underglowColor?.copyOf()
                        vehicleToReset.meshStyle = stockCatalog.meshStyle
                        vehicleToReset.customTopSpeed = stockCatalog.topSpeedKmH
                        vehicleToReset.customAccelRate = stockCatalog.accelRate
                        vehicleToReset.customBrakeRate = stockCatalog.brakeRate
                        vehicleToReset.soundPitch = stockCatalog.soundPitch
                    } else if (stockSpawn != null) {
                        vehicleToReset.colorPrimary = stockSpawn.colorPrimary.copyOf()
                        vehicleToReset.colorSecondary = stockSpawn.colorSecondary.copyOf()
                        vehicleToReset.underglowColor = stockSpawn.underglowColor?.copyOf()
                        vehicleToReset.meshStyle = stockSpawn.meshStyle
                        vehicleToReset.customTopSpeed = stockSpawn.customTopSpeed
                        vehicleToReset.customAccelRate = stockSpawn.customAccelRate
                        vehicleToReset.customBrakeRate = stockSpawn.customBrakeRate
                        vehicleToReset.soundPitch = stockSpawn.soundPitch
                    }
                    soundManager.playDoorOpen()
                    celebrationToast = "↺ تم استعادة إعدادات المصنع للسيارة بنجاح!"
                },
                onDriveVehicle = { targetCar ->
                    resetControlInputs()
                    stopMetroRideSilently()
                    if (isInsideBuilding) {
                        suppressedAutoDoorId = lastEnteredBuildingId
                        isInsideBuilding = false
                        currentInteriorType = InteriorType.NONE
                        glSurfaceView?.renderer?.let { r ->
                            r.isInsideBuilding = false
                            r.currentInteriorType = InteriorType.NONE
                        }
                    }
                    if (targetCar.isInUndergroundGarage) {
                        val r = glSurfaceView?.renderer
                        val pPos = r?.playerPos ?: Vector3(8.0f, 0f, 6.5f)
                        targetCar.isInUndergroundGarage = false
                        targetCar.posX = pPos.x + 2.2f
                        targetCar.posZ = pPos.z
                    }
                    currentCar?.let { prev -> if (prev !== targetCar) prev.isPlayerDriving = false }
                    enterVehicle(targetCar)
                    glSurfaceView?.renderer?.let { r ->
                        r.playerPos.set(targetCar.posX, targetCar.posY, targetCar.posZ)
                        r.playerYaw = targetCar.yaw
                    }
                    celebrationToast = "🚗 انطلقت بالقيادة بعد التعديل: ${targetCar.displayName ?: targetCar.type.arabicName}!"
                },
                onTeleportToWorkshop = {
                    resetControlInputs()
                    stopMetroRideSilently()
                    isInsideBuilding = false
                    currentInteriorType = InteriorType.NONE
                    val r = glSurfaceView?.renderer
                    val active = currentCar
                    if (isDriving && active != null && active.type != VehicleType.AIRPLANE) {
                        active.posX = -95f
                        active.posY = 0f
                        active.posZ = -4.5f
                        active.yaw = 180f
                        active.speed = 0f
                        r?.let {
                            it.isInsideBuilding = false
                            it.currentInteriorType = InteriorType.NONE
                            it.playerPos.set(-95f, 0f, -4.5f)
                            it.playerYaw = 180f
                            it.cameraYawOffset = 0f
                            it.cameraPitch = 16f
                        }
                    } else {
                        if (isDriving) {
                            isDriving = false
                            isTestDriving = false
                            active?.isPlayerDriving = false
                            currentCar = null
                        }
                        r?.let {
                            it.isPlayerInVehicle = false
                            it.currentVehicle = null
                            it.isInsideBuilding = false
                            it.currentInteriorType = InteriorType.NONE
                            it.playerPos.set(-95f, 0f, -6.5f)
                            it.playerYaw = 180f
                            it.cameraYawOffset = 180f
                            it.cameraPitch = 18f
                        }
                        saveManager.savePosition(-95f, -6.5f)
                    }
                    soundManager.playChime()
                    celebrationToast = "🔧📍 وصلت عند ورشة ومحلات تعديل السيارات بالمدينة!"
                },
                onDismiss = { showCarCustomizationDialog = false }
            )
        }
        }
    }
}

@Composable
private fun DesktopKeyCap(
    label: String,
    active: Boolean,
    accent: Color,
    wide: Boolean = false
) {
    Surface(
        color = if (active) accent else Color(0xFF1E293B),
        shape = RoundedCornerShape(7.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (active) 1.5.dp else 1.dp,
            color = if (active) Color.White else accent.copy(alpha = 0.65f)
        ),
        modifier = if (wide) {
            Modifier.widthIn(min = 78.dp).height(24.dp)
        } else {
            Modifier.size(26.dp)
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = if (wide) 6.dp else 2.dp)
        ) {
            Text(
                text = label,
                color = if (active) Color(0xFF091122) else Color.White,
                fontSize = if (wide) 10.sp else 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

private fun Modifier.holdTouchInput(
    key: Any? = Unit,
    onHoldChange: (Boolean) -> Unit
): Modifier = this.pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        down.consume()
        onHoldChange(true)
        try {
            while (true) {
                val event = awaitPointerEvent()
                event.changes.forEach { change ->
                    if (change.pressed) change.consume()
                }
                if (event.changes.none { it.pressed }) break
            }
        } finally {
            onHoldChange(false)
        }
    }
}

@Composable
fun GamerBloxColorDialog(
    currentColorIndex: Int,
    onSelectColor: (index: Int, color: FloatArray) -> Unit,
    onDismiss: () -> Unit
) {
    val colorOptions = listOf(
        Triple("أبيض كربوني (الأصلي)", Color(0xFFF1F5F9), floatArrayOf(0.96f, 0.96f, 0.98f, 1f)),
        Triple("أحمر ناري (Crimson)", Color(0xFFEF4444), floatArrayOf(0.92f, 0.15f, 0.15f, 1f)),
        Triple("أصفر بلوكس (Blox Gold)", Color(0xFFFACC15), floatArrayOf(0.98f, 0.82f, 0.08f, 1f)),
        Triple("بنفسجي سايبر (Cyber Violet)", Color(0xFFA855F7), floatArrayOf(0.68f, 0.20f, 0.92f, 1f))
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 520.dp)
                .padding(16.dp)
                .testTag("gamer_blox_color_dialog"),
            shape = RoundedCornerShape(22.dp),
            color = Color(0xF80B0F19),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF00E5FF))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎨", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "تخصيص ألوان Gamer Blox R-1",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "خيارات ألوان حصرية روبلوكس مع إضاءة نيون زرقاء",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorOptions.forEachIndexed { idx, (name, previewColor, floatArray) ->
                        val isSelected = currentColorIndex == idx
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectColor(idx, floatArray) }
                                .testTag("color_option_$idx"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0x3300E5FF) else Color(0x221E293B)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF00E5FF) else Color(0x4464748B)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(previewColor)
                                            .border(1.5.dp, Color.White, CircleShape)
                                    )
                                    Text(
                                        text = name,
                                        color = Color.White,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                                if (isSelected) {
                                    Surface(
                                        color = Color(0xFF00E5FF),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "محدد ✓",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("إغلاق وحفظ", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
