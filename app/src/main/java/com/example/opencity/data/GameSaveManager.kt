package com.example.opencity.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages local persistent storage of player progress, wallet, missions, and settings.
 */
class GameSaveManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("opencity_save_prefs", Context.MODE_PRIVATE)

    init {
        if (!prefs.getBoolean("zero_start_migrated_v1", false)) {
            prefs.edit()
                .putInt("player_money", 0)
                .putBoolean("zero_start_migrated_v1", true)
                .apply()
        }
        if (!prefs.getBoolean("city_center_spawn_migrated_v1", false)) {
            prefs.edit()
                .putFloat("pos_x", 8.0f)
                .putFloat("pos_z", 6.5f)
                .putBoolean("garage_spawn_migrated_v1", true)
                .putBoolean("city_center_spawn_migrated_v1", true)
                .apply()
        }
        if (!prefs.getBoolean("robot_cyber_pack_v1", false)) {
            val savedClothing = prefs.getStringSet("owned_clothing_ids", null)?.toMutableSet()
                ?: com.example.opencity.model.ClothingCatalog.defaultOwnedIds.toMutableSet()
            savedClothing.add(com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID)
            val savedCars = prefs.getStringSet("owned_cars", null)?.toMutableSet()
                ?: mutableSetOf("car_robot_cyber_free")
            savedCars.add("car_robot_cyber_free")
            prefs.edit()
                .putStringSet("owned_clothing_ids", savedClothing)
                .putString("equipped_top_id", com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID)
                .putStringSet("owned_cars", savedCars)
                .putBoolean("robot_cyber_pack_v1", true)
                .apply()
        }
        if (!prefs.getBoolean("single_starter_car_v1", false)) {
            val oldAutoStarterIds = setOf("player_sports_car", "car_beetle_1974", "car_gamer_blox_r1")
            val existing = prefs.getStringSet("owned_cars", null)?.toMutableSet() ?: mutableSetOf()
            val cleaned = existing.filterNot { it in oldAutoStarterIds }.toMutableSet()
            cleaned.add("car_robot_cyber_free")
            prefs.edit()
                .putStringSet("owned_cars", cleaned)
                .putString("active_car_id", "car_robot_cyber_free")
                .putBoolean("single_starter_car_v1", true)
                .apply()
        }
    }

    var playerMoney: Int
        get() = prefs.getInt("player_money", 0).coerceAtLeast(0) // Start at $0; earn money by driving cars!
        set(value) = prefs.edit().putInt("player_money", value.coerceAtLeast(0)).apply()

    var playerName: String
        get() = prefs.getString("player_name", "أشرف") ?: "أشرف"
        set(value) = prefs.edit().putString("player_name", value).apply()

    var characterMotto: String
        get() = prefs.getString("character_motto", "طموح بلا حدود") ?: "طموح بلا حدود"
        set(value) = prefs.edit().putString("character_motto", value.ifBlank { "طموح بلا حدود" }).apply()

    var ownedCarIds: Set<String>
        get() {
            val raw = prefs.getStringSet("owned_cars", null)
            return if (raw == null) setOf("car_robot_cyber_free") else raw + "car_robot_cyber_free"
        }
        set(value) = prefs.edit().putStringSet("owned_cars", HashSet(value)).apply()

    var activeCarId: String
        get() = prefs.getString("active_car_id", "car_robot_cyber_free") ?: "car_robot_cyber_free"
        set(value) = prefs.edit().putString("active_car_id", value).apply()

    var gamerBloxColorIndex: Int
        get() = prefs.getInt("gamer_blox_color_idx", 0) // 0: White, 1: Red, 2: Yellow, 3: Purple
        set(value) = prefs.edit().putInt("gamer_blox_color_idx", value).apply()

    fun isCarOwned(carId: String): Boolean {
        return ownedCarIds.contains(carId)
    }

    fun buyCar(carId: String, price: Int): Boolean {
        if (playerMoney >= price && !isCarOwned(carId)) {
            playerMoney -= price
            val updated = ownedCarIds.toMutableSet()
            updated.add(carId)
            ownedCarIds = updated
            activeCarId = carId
            return true
        }
        return false
    }

    var ownedVillaIds: Set<String>
        get() = prefs.getStringSet("owned_villas", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("owned_villas", value).apply()

    fun isVillaOwned(villaId: String): Boolean {
        return ownedVillaIds.contains(villaId)
    }

    fun buyVilla(villaId: String, price: Int): Boolean {
        if (playerMoney >= price && !isVillaOwned(villaId)) {
            playerMoney -= price
            val updated = ownedVillaIds.toMutableSet()
            updated.add(villaId)
            ownedVillaIds = updated
            return true
        }
        return false
    }

    var completedMissions: Set<String>
        get() = prefs.getStringSet("completed_missions", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("completed_missions", value).apply()

    fun markMissionCompleted(missionId: String, reward: Int = 0) {
        val set = completedMissions.toMutableSet()
        set.add(missionId)
        completedMissions = set
    }

    // Street Houses, Door Keys, Sale/Rent, & Side Quests
    var purchasedHouseIds: Set<String>
        get() = prefs.getStringSet("purchased_houses", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("purchased_houses", HashSet(value)).apply()

    var rentedHouseIds: Set<String>
        get() = prefs.getStringSet("rented_houses", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("rented_houses", HashSet(value)).apply()

    var unlockedHouseIds: Set<String>
        get() = prefs.getStringSet("unlocked_houses", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("unlocked_houses", HashSet(value)).apply()

    var completedHouseQuests: Set<String>
        get() = prefs.getStringSet("completed_house_quests", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("completed_house_quests", HashSet(value)).apply()

    var hasMasterGardenKey: Boolean
        get() = prefs.getBoolean("has_master_garden_key", false)
        set(value) = prefs.edit().putBoolean("has_master_garden_key", value).apply()

    fun buyHouse(houseId: String, price: Int): Boolean {
        if (purchasedHouseIds.contains(houseId)) return true
        if (playerMoney >= price) {
            playerMoney -= price
            purchasedHouseIds = purchasedHouseIds + houseId
            unlockedHouseIds = unlockedHouseIds + houseId
            return true
        }
        return false
    }

    fun rentHouse(houseId: String, rentCost: Int): Boolean {
        if (rentedHouseIds.contains(houseId)) return true
        if (playerMoney >= rentCost) {
            playerMoney -= rentCost
            rentedHouseIds = rentedHouseIds + houseId
            unlockedHouseIds = unlockedHouseIds + houseId
            return true
        }
        return false
    }

    fun unlockHouse(houseId: String) {
        unlockedHouseIds = unlockedHouseIds + houseId
    }

    fun completeHouseQuest(houseId: String, reward: Int = 0): Boolean {
        if (completedHouseQuests.contains(houseId)) return false
        completedHouseQuests = completedHouseQuests + houseId
        return true
    }

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var graphicsQuality: String
        get() = prefs.getString("graphics_quality", "MEDIUM") ?: "MEDIUM"
        set(value) = prefs.edit().putString("graphics_quality", value).apply()

    var savedPlayerPosX: Float
        get() = prefs.getFloat("pos_x", 8.0f)
        set(value) = prefs.edit().putFloat("pos_x", value).apply()

    var savedPlayerPosZ: Float
        get() = prefs.getFloat("pos_z", 6.5f)
        set(value) = prefs.edit().putFloat("pos_z", value).apply()

    fun savePosition(x: Float, z: Float) {
        prefs.edit().putFloat("pos_x", x).putFloat("pos_z", z).apply()
    }

    // House Envelope Mailboxes («ظروف أمام البيت في الشوارع» - بدون أي مكافآت مالية)
    data class HouseEnvelope(
        val id: String,
        val title: String,
        val message: String,
        val sender: String
    )

    fun getHouseEnvelopes(houseId: String): List<HouseEnvelope> {
        val raw = prefs.getString("house_envelopes_$houseId", null) ?: return emptyList()
        return try {
            val arr = org.json.JSONArray(raw)
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        HouseEnvelope(
                            id = obj.optString("id", "env_$i"),
                            title = obj.optString("title", "ظرف بريد"),
                            message = obj.optString("message", ""),
                            sender = obj.optString("sender", playerName)
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addHouseEnvelope(houseId: String, title: String, message: String, sender: String = playerName): List<HouseEnvelope> {
        val current = getHouseEnvelopes(houseId).toMutableList()
        current.add(
            0,
            HouseEnvelope(
                id = "env_${System.currentTimeMillis()}_${current.size}",
                title = title.ifBlank { "ظرف بريد خاص" },
                message = message.ifBlank { "رسالة مرسلة أمام البيت" },
                sender = sender.ifBlank { playerName }
            )
        )
        saveHouseEnvelopes(houseId, current)
        return current
    }

    fun removeHouseEnvelope(houseId: String, envelopeId: String): List<HouseEnvelope> {
        val updated = getHouseEnvelopes(houseId).filterNot { it.id == envelopeId }
        saveHouseEnvelopes(houseId, updated)
        return updated
    }

    private fun saveHouseEnvelopes(houseId: String, list: List<HouseEnvelope>) {
        val arr = org.json.JSONArray()
        for (env in list) {
            val obj = org.json.JSONObject()
            obj.put("id", env.id)
            obj.put("title", env.title)
            obj.put("message", env.message)
            obj.put("sender", env.sender)
            arr.put(obj)
        }
        prefs.edit().putString("house_envelopes_$houseId", arr.toString()).apply()
    }

    // Clothing Store, Brands (Nike/Adidas) & Personal Wardrobe Persistence («الأواعي والخزانة»)
    var ownedClothingIds: Set<String>
        get() {
            val saved = prefs.getStringSet("owned_clothing_ids", null)
            return if (saved == null) com.example.opencity.model.ClothingCatalog.defaultOwnedIds
            else saved + com.example.opencity.model.ClothingCatalog.defaultOwnedIds
        }
        set(value) = prefs.edit().putStringSet("owned_clothing_ids", HashSet(value)).apply()

    var equippedTopId: String
        get() = prefs.getString("equipped_top_id", com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID)
            ?: com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID
        set(value) = prefs.edit().putString("equipped_top_id", value).apply()

    var equippedPantsId: String
        get() = prefs.getString("equipped_pants_id", com.example.opencity.model.ClothingCatalog.DEFAULT_PANTS_ID)
            ?: com.example.opencity.model.ClothingCatalog.DEFAULT_PANTS_ID
        set(value) = prefs.edit().putString("equipped_pants_id", value).apply()

    var equippedShoesId: String
        get() = prefs.getString("equipped_shoes_id", com.example.opencity.model.ClothingCatalog.DEFAULT_SHOES_ID)
            ?: com.example.opencity.model.ClothingCatalog.DEFAULT_SHOES_ID
        set(value) = prefs.edit().putString("equipped_shoes_id", value).apply()

    var equippedAccessoryId: String
        get() = prefs.getString("equipped_accessory_id", com.example.opencity.model.ClothingCatalog.DEFAULT_ACCESSORY_ID)
            ?: com.example.opencity.model.ClothingCatalog.DEFAULT_ACCESSORY_ID
        set(value) = prefs.edit().putString("equipped_accessory_id", value).apply()

    fun getEquippedOutfit(): com.example.opencity.model.PlayerOutfit {
        return com.example.opencity.model.ClothingCatalog.resolveOutfit(
            topId = equippedTopId,
            pantsId = equippedPantsId,
            shoesId = equippedShoesId,
            accessoryId = equippedAccessoryId
        )
    }

    fun equipClothingItem(item: com.example.opencity.model.ClothingItem) {
        when (item.slot) {
            com.example.opencity.model.ClothingSlot.TOP -> equippedTopId = item.id
            com.example.opencity.model.ClothingSlot.PANTS -> equippedPantsId = item.id
            com.example.opencity.model.ClothingSlot.SHOES -> equippedShoesId = item.id
            com.example.opencity.model.ClothingSlot.ACCESSORY -> equippedAccessoryId = item.id
        }
    }

    fun buyClothingItem(item: com.example.opencity.model.ClothingItem): Boolean {
        if (ownedClothingIds.contains(item.id)) {
            equipClothingItem(item)
            return true
        }
        if (playerMoney >= item.price) {
            playerMoney -= item.price
            ownedClothingIds = ownedClothingIds + item.id
            equipClothingItem(item)
            return true
        }
        return false
    }

    var metroTicketsCount: Int
        get() = prefs.getInt("metro_tickets_count", 0)
        set(value) = prefs.edit().putInt("metro_tickets_count", value.coerceAtLeast(0)).apply()

    fun buyMetroTicket(count: Int = 1, priceEgp: Int = 5): Boolean {
        val totalCost = count * priceEgp
        if (playerMoney >= totalCost) {
            playerMoney -= totalCost
            metroTicketsCount += count
            return true
        }
        return false
    }

    fun consumeMetroTicket(): Boolean {
        val current = metroTicketsCount
        if (current > 0) {
            metroTicketsCount = current - 1
            return true
        }
        return false
    }

    // Metro Card Sales Business Persistence («مركزي لبيع كروت المترو»)
    var metroCardsSoldCount: Int
        get() = prefs.getInt("metro_cards_sold_count", 0)
        set(value) = prefs.edit().putInt("metro_cards_sold_count", value.coerceAtLeast(0)).apply()

    var metroShopTotalRevenue: Int
        get() = prefs.getInt("metro_shop_total_revenue", 0)
        set(value) = prefs.edit().putInt("metro_shop_total_revenue", value.coerceAtLeast(0)).apply()

    var metroCardStockCount: Int
        get() = prefs.getInt("metro_card_stock_count", 25)
        set(value) = prefs.edit().putInt("metro_card_stock_count", value.coerceAtLeast(0)).apply()

    fun sellMetroCardToCustomer(priceEgp: Int, quantity: Int = 1): Boolean {
        val stock = metroCardStockCount
        if (stock < quantity || priceEgp <= 0) return false
        metroCardStockCount = stock - quantity
        metroCardsSoldCount += quantity
        metroShopTotalRevenue += priceEgp
        playerMoney += priceEgp
        return true
    }

    fun printNewMetroCards(amount: Int = 10): Int {
        val updated = metroCardStockCount + amount.coerceAtLeast(1)
        metroCardStockCount = updated
        return updated
    }

    // Car Customization Workshop Persistence («ورشة تعديل السيارة»)
    data class VehicleCustomization(
        val primaryRgba: FloatArray? = null,
        val secondaryRgba: FloatArray? = null,
        val underglowRgba: FloatArray? = null,
        val underglowDisabled: Boolean = false,
        val meshStyleName: String? = null,
        val topSpeedKmH: Float? = null,
        val accelRate: Float? = null,
        val brakeRate: Float? = null,
        val soundPitch: Float? = null
    )

    private fun vehicleKeyFor(vehicle: com.example.opencity.model.Vehicle): String {
        return vehicle.dealershipCarId ?: vehicle.id
    }

    fun saveVehicleCustomization(vehicle: com.example.opencity.model.Vehicle) {
        val key = vehicleKeyFor(vehicle)
        val obj = org.json.JSONObject()
        obj.put("primary", org.json.JSONArray(vehicle.colorPrimary.map { it.toDouble() }))
        obj.put("secondary", org.json.JSONArray(vehicle.colorSecondary.map { it.toDouble() }))
        val ug = vehicle.underglowColor
        if (ug != null) {
            obj.put("underglow", org.json.JSONArray(ug.map { it.toDouble() }))
            obj.put("underglowDisabled", false)
        } else {
            obj.put("underglowDisabled", true)
        }
        vehicle.meshStyle?.let { obj.put("meshStyle", it.name) }
        vehicle.customTopSpeed?.let { obj.put("topSpeed", it.toDouble()) }
        vehicle.customAccelRate?.let { obj.put("accelRate", it.toDouble()) }
        vehicle.customBrakeRate?.let { obj.put("brakeRate", it.toDouble()) }
        obj.put("soundPitch", vehicle.soundPitch.toDouble())
        prefs.edit().putString("vehicle_custom_$key", obj.toString()).apply()
    }

    fun clearVehicleCustomization(vehicleKey: String) {
        prefs.edit().remove("vehicle_custom_$vehicleKey").apply()
    }

    fun applySavedCustomization(vehicle: com.example.opencity.model.Vehicle) {
        val key = vehicleKeyFor(vehicle)
        val raw = prefs.getString("vehicle_custom_$key", null) ?: return
        try {
            val obj = org.json.JSONObject(raw)
            obj.optJSONArray("primary")?.let { arr ->
                if (arr.length() >= 4) {
                    vehicle.colorPrimary = floatArrayOf(
                        arr.getDouble(0).toFloat(),
                        arr.getDouble(1).toFloat(),
                        arr.getDouble(2).toFloat(),
                        arr.getDouble(3).toFloat()
                    )
                }
            }
            obj.optJSONArray("secondary")?.let { arr ->
                if (arr.length() >= 4) {
                    vehicle.colorSecondary = floatArrayOf(
                        arr.getDouble(0).toFloat(),
                        arr.getDouble(1).toFloat(),
                        arr.getDouble(2).toFloat(),
                        arr.getDouble(3).toFloat()
                    )
                }
            }
            if (obj.optBoolean("underglowDisabled", false)) {
                vehicle.underglowColor = null
            } else {
                obj.optJSONArray("underglow")?.let { arr ->
                    if (arr.length() >= 4) {
                        vehicle.underglowColor = floatArrayOf(
                            arr.getDouble(0).toFloat(),
                            arr.getDouble(1).toFloat(),
                            arr.getDouble(2).toFloat(),
                            arr.getDouble(3).toFloat()
                        )
                    }
                }
            }
            val styleStr = obj.optString("meshStyle", "")
            if (styleStr.isNotEmpty()) {
                runCatching { com.example.opencity.model.CarMeshStyle.valueOf(styleStr) }.getOrNull()?.let {
                    vehicle.meshStyle = it
                }
            }
            if (obj.has("topSpeed")) vehicle.customTopSpeed = obj.getDouble("topSpeed").toFloat()
            if (obj.has("accelRate")) vehicle.customAccelRate = obj.getDouble("accelRate").toFloat()
            if (obj.has("brakeRate")) vehicle.customBrakeRate = obj.getDouble("brakeRate").toFloat()
            if (obj.has("soundPitch")) vehicle.soundPitch = obj.getDouble("soundPitch").toFloat()
        } catch (_: Exception) {
        }
    }

    fun resetProgress() {
        prefs.edit()
            .clear()
            .putBoolean("zero_start_migrated_v1", true)
            .putBoolean("garage_spawn_migrated_v1", true)
            .putBoolean("city_center_spawn_migrated_v1", true)
            .putBoolean("robot_cyber_pack_v1", true)
            .putBoolean("single_starter_car_v1", true)
            .putString("equipped_top_id", com.example.opencity.model.ClothingCatalog.DEFAULT_TOP_ID)
            .putStringSet("owned_clothing_ids", HashSet(com.example.opencity.model.ClothingCatalog.defaultOwnedIds))
            .putStringSet("owned_cars", hashSetOf("car_robot_cyber_free"))
            .putString("active_car_id", "car_robot_cyber_free")
            .apply()
    }
}
