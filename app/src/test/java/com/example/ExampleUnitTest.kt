package com.example

import com.example.opencity.model.CityData
import com.example.opencity.model.District
import com.example.opencity.model.MissionData
import com.example.opencity.model.Vector3
import com.example.opencity.model.VehicleType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun vector3_operations_areCorrect() {
        val v = Vector3(1f, 2f, 3f)
        v.add(2f, 1f, 0f)
        assertEquals(3f, v.x, 0.001f)
        assertEquals(3f, v.y, 0.001f)
        assertEquals(3f, v.z, 0.001f)

        val dist = v.distanceTo2D(Vector3(0f, 0f, 3f))
        assertEquals(3f, dist, 0.001f)
    }

    @Test
    fun cityData_generatesAllRequiredDistricts() {
        val buildings = CityData.generateBuildings()
        assertTrue("Buildings list should not be empty", buildings.isNotEmpty())

        val districts = buildings.map { it.district }.toSet()
        assertTrue(districts.contains(District.DOWNTOWN))
        assertTrue(districts.contains(District.RESIDENTIAL))
        assertTrue(districts.contains(District.COMMERCIAL))
        assertTrue(districts.contains(District.TRAVEL_HUB))
        assertTrue(districts.contains(District.SERVICES))
        assertTrue(districts.contains(District.SUBURBS))

        val enterable = buildings.filter { it.isEnterable }
        assertTrue("Should have multiple enterable buildings", enterable.size >= 4)
    }

    @Test
    fun vehicles_and_missions_areAvailable() {
        val vehicles = CityData.generateVehicles()
        assertTrue(vehicles.any { it.type == VehicleType.SPORTS_CAR })
        assertTrue(vehicles.any { it.type == VehicleType.TAXI })
        assertTrue(vehicles.any { it.type == VehicleType.CITY_BUS })
        assertTrue(vehicles.any { it.type == VehicleType.POLICE_CRUISER })

        val missions = MissionData.getDefaultMissions()
        assertTrue("Missions should be populated", missions.size >= 5)
        assertNotNull(missions.firstOrNull { it.id == "m1_food_delivery" })
    }

    @Test
    fun trafficSystem_managesIntersectionsAndVehicles() {
        val ts = com.example.opencity.model.TrafficSystem()
        assertTrue("Should have multiple intersections", ts.intersections.size >= 10)
        assertTrue("Should have traffic cars", ts.trafficCars.size >= 15)
        assertTrue("Should have sidewalk pedestrians", ts.pedestrians.size >= 10)

        // Test manual traffic control
        ts.setAllNsGreen()
        assertTrue(ts.intersections.all { it.isNsGreen() })

        ts.toggleEmergencyAllStop()
        assertTrue(ts.allStopActive)

        // NaN / Infinity safety check
        ts.update(Float.NaN, null, Vector3(0f, 0f, 0f))
        ts.update(Float.POSITIVE_INFINITY, null, Vector3(0f, 0f, 0f))
    }

    @Test
    fun skyMetro_outerLoopSeatsAndDestinationRequests_areValid() {
        val metro = com.example.opencity.model.SkyMetroSystem()
        assertEquals(4, com.example.opencity.model.SkyMetroSystem.STATIONS.size)
        assertEquals(6, com.example.opencity.model.SkyMetroSystem.CAR_SEATS.size)

        val train = metro.trains.first()
        for (seat in com.example.opencity.model.SkyMetroSystem.CAR_SEATS) {
            val pose = com.example.opencity.model.SkyMetroSystem.computeMetroSeatWorldPose(train, seat)
            assertTrue("Seat X must be finite", !pose.x.isNaN() && !pose.x.isInfinite())
            assertTrue("Seat Z must be finite", !pose.z.isNaN() && !pose.z.isInfinite())
            assertEquals(
                com.example.opencity.model.SkyMetroSystem.DECK_SURFACE_Y + 1.04f,
                pose.y,
                0.01f
            )
        }

        metro.update(Float.NaN)
        metro.update(Float.POSITIVE_INFINITY)
    }

    @Test
    fun freeSpawnRobotCar_starterOutfit_and_new8MillionSupercar_areConfigured() {
        val spawnedVehicles = CityData.generateVehicles()
        val robotFreeCars = spawnedVehicles.filter { it.dealershipCarId == "car_robot_cyber_free" }
        assertEquals("Only one car_robot_cyber_free should exist (no showroom duplicate)", 1, robotFreeCars.size)
        val robotFreeCar = robotFreeCars.first()
        val distToSpawn = Vector3(robotFreeCar.posX, 0f, robotFreeCar.posZ)
            .distanceTo2D(Vector3(8.0f, 0f, 6.5f))
        assertTrue("Free robot car should be parked right beside spawn (< 5m)", distToSpawn < 5.0f)

        val surfaceCarsNearSpawn = spawnedVehicles.filter {
            !it.isInUndergroundGarage &&
                !it.isShowroomDisplay &&
                it.type != VehicleType.AIRPLANE &&
                Vector3(it.posX, 0f, it.posZ).distanceTo2D(Vector3(8.0f, 0f, 6.5f)) < 25.0f
        }
        assertEquals("Strictly ONE car should be near spawn", 1, surfaceCarsNearSpawn.size)
        assertTrue("Extra Villa Garage starter cars should not spawn by default", spawnedVehicles.none { it.id == "player_sports_car" })

        val freeCatalogCar = com.example.opencity.model.MotorCityData.getCarById("car_robot_cyber_free")
        assertNotNull(freeCatalogCar)
        assertEquals(0, freeCatalogCar!!.price)

        val new8MCar = com.example.opencity.model.MotorCityData.getCarById("car_new_phantom_x8")
        assertNotNull(new8MCar)
        assertEquals(8_000_000, new8MCar!!.price)

        assertEquals("top_robot_cyber_jacket", com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID)
        assertTrue(com.example.opencity.model.ClothingCatalog.defaultOwnedIds.contains("top_robot_cyber_jacket"))
    }

    @Test
    fun metroCardSalesOffice_placementAndInteriorAreValid() {
        val buildings = CityData.generateBuildings()
        val shop = buildings.firstOrNull { it.id == "metro_card_sales_office" }
        assertNotNull("Metro card sales office should exist in city", shop)
        assertTrue("Shop must be enterable", shop!!.isEnterable)
        assertEquals(com.example.opencity.model.InteriorType.METRO_CARD_SHOP, shop.interiorType)

        // Ensure it sits inside the clear block (-26.25..-8.75, 143.75..166.25) without intersecting any building
        val shopMinX = shop.posX - shop.width / 2f
        val shopMaxX = shop.posX + shop.width / 2f
        val shopMinZ = shop.posZ - shop.depth / 2f
        val shopMaxZ = shop.posZ + shop.depth / 2f
        assertTrue("Shop must stay clear of X=-35 and X=0 roads", shopMinX > -27.5f && shopMaxX < -7.5f)
        assertTrue("Shop must stay clear of Z=135 and Z=175 roads", shopMinZ > 142.5f && shopMaxZ < 167.5f)

        for (other in buildings) {
            if (other.id == shop.id) continue
            val oMinX = other.posX - other.width / 2f
            val oMaxX = other.posX + other.width / 2f
            val oMinZ = other.posZ - other.depth / 2f
            val oMaxZ = other.posZ + other.depth / 2f
            val overlaps = shopMinX < oMaxX && shopMaxX > oMinX && shopMinZ < oMaxZ && shopMaxZ > oMinZ
            assertTrue("Shop must not overlap ${other.id}", !overlaps)
        }

        val interior = com.example.opencity.model.BuildingInterior.getInterior(com.example.opencity.model.InteriorType.METRO_CARD_SHOP)
        assertTrue(interior.props.any { it.name == "MetroCardSalesCounter" })
        assertTrue(interior.props.any { it.name == "MetroCardPrinterTerminal" })
        assertTrue(interior.props.any { it.name == "ExitMetroShopDoor" })
    }

    @Test
    fun vehicleCustomization_and_smoothEllipsoidGeometry_areValid() {
        val mb = com.example.opencity.engine.MeshBuilder(initialCapacityVertices = 2048)
        mb.addEllipsoid(
            cx = 0f, cy = 1.3f, cz = 0f,
            rx = 0.215f, ry = 0.205f, rz = 0.205f,
            color = floatArrayOf(1f, 0.85f, 0.1f, 1f),
            slices = 20, stacks = 12
        )
        assertEquals(20 * 12 * 6, mb.getVertexCount())
        val buf = mb.build()
        while (buf.hasRemaining()) {
            val v = buf.get()
            assertTrue("Vertex attribute must be finite", !v.isNaN() && !v.isInfinite())
        }

        val car = CityData.generateVehicles().first { it.type == VehicleType.SPORTS_CAR }
        car.colorPrimary = floatArrayOf(0.9f, 0.1f, 0.1f, 1f)
        car.colorSecondary = floatArrayOf(0.1f, 0.1f, 0.1f, 1f)
        car.underglowColor = floatArrayOf(0.08f, 0.85f, 1.0f, 1f)
        car.meshStyle = com.example.opencity.model.CarMeshStyle.HYPER_AERO
        car.customTopSpeed = 380f
        car.customAccelRate = 88f
        car.customBrakeRate = 108f
        car.soundPitch = 1.38f

        assertEquals(380f, car.effectiveMaxSpeedKmH, 0.01f)
        assertEquals(88f, car.effectiveAccelRate, 0.01f)
        assertEquals(108f, car.effectiveBrakeRate, 0.01f)
        assertEquals(com.example.opencity.model.CarMeshStyle.HYPER_AERO, car.meshStyle)
    }
}
