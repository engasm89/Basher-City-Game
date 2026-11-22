package com.example.opencity.model

enum class District(val arabicName: String, val englishName: String) {
    DOWNTOWN("وسط المدينة", "Downtown"),
    RESIDENTIAL("الحي السكني", "Residential"),
    COMMERCIAL("المنطقة التجارية", "Commercial"),
    SERVICES("حي الخدمات والمرافق", "Civic Services"),
    TRAVEL_HUB("محطة السفر والمواصلات", "Transit Hub"),
    INDUSTRIAL("المنطقة الصناعية", "Industrial"),
    SUBURBS("الضواحي والريف", "Suburbs & Countryside")
}

enum class BuildingType {
    SKYSCRAPER,
    MODERN_TOWER,
    RESIDENTIAL_VILLA,
    APARTMENT_BLOCK,
    SUPERMARKET,
    SHOPPING_MALL,
    CAR_DEALERSHIP,
    RESTAURANT_CAFE,
    HOSPITAL,
    POLICE_STATION,
    FIRE_STATION,
    GRAND_MOSQUE,
    UNIVERSITY,
    BANK,
    CENTRAL_BUS_TERMINAL,
    TRAIN_STATION,
    AIRPORT_HANGAR,
    GAS_STATION,
    STADIUM,
    PARK_GAZEBO,
    WAREHOUSE,
    FACTORY,
    RUSTIC_FARMHOUSE,
    WINDMILL,
    PARKING_LOT
}

enum class InteriorType {
    NONE,
    PLAYER_VILLA,
    STANDARD_HOUSE,
    SUPERMARKET,
    TRAVEL_TERMINAL,
    POLICE_STATION,
    HOSPITAL,
    CAFE,
    DINING_AVENUE,
    CAR_PARTS_SHOP,
    REAL_ESTATE_AGENCY,
    VILLAS_FOOD_WATER_MALL,
    STREET_HOUSES_MARKET,
    CLOTHING_STORE,
    UNDERGROUND_GARAGE,
    GRAND_MALL,
    METRO_CARD_SHOP
}

enum class StreetZone(
    val arabicName: String,
    val englishName: String,
    val classificationArabic: String,
    val iconEmoji: String,
    val accentColorHex: Long
) {
    MAIN_STREET(
        arabicName = "الشارع الرئيسي",
        englishName = "Main Street",
        classificationArabic = "حيوي • تجاري • مزدحم بالحركة",
        iconEmoji = "🛍️",
        accentColorHex = 0xFFF59E0B
    ),
    RESIDENTIAL_SUBURBS(
        arabicName = "الحي السكني الهادئ",
        englishName = "Residential Suburbs",
        classificationArabic = "هادئ • عائلي • مساحات خضراء واسعة",
        iconEmoji = "🏡",
        accentColorHex = 0xFF10B981
    ),
    OLD_TOWN(
        arabicName = "الحي القديم / التاريخي",
        englishName = "Old Town / Historic District",
        classificationArabic = "تراثي • مرصوف بالحصى • غامض",
        iconEmoji = "🏛️",
        accentColorHex = 0xFFD97706
    ),
    MODERN_TOWERS(
        arabicName = "حي الأبراج والمجمعات الحديثة",
        englishName = "Modern Towers District",
        classificationArabic = "عصري • أبراج عالية • إضاءات ليلية",
        iconEmoji = "🏙️",
        accentColorHex = 0xFF38BDF8
    ),
    AVENUE_90_COMPOUND(
        arabicName = "كومباوند 90 أفينيو",
        englishName = "90 Avenue Compound",
        classificationArabic = "كومباوند فلل مسور • بحر خاص • مسبح أولمبي",
        iconEmoji = "🏖️",
        accentColorHex = 0xFF06B6D4
    );

    companion object {
        fun detectAt(px: Float, pz: Float): StreetZone? {
            if (CompoundAvenue90.isInsideCompoundZone(px, pz) ||
                (px in 40f..70f && pz in 138f..170f)
            ) {
                return AVENUE_90_COMPOUND
            }
            return when {
                px in -75f..75f && pz in -180f..-100f -> MODERN_TOWERS
                px in 75f..195f && pz in -140f..0f -> OLD_TOWN
                px in 35f..195f && pz in 100f..175f -> RESIDENTIAL_SUBURBS
                (px in -35f..35f && pz in 100f..175f) ||
                    (px in -195f..-155f && pz in -100f..0f) -> MAIN_STREET
                else -> null
            }
        }
    }
}

enum class HouseSideQuest(
    val titleArabic: String,
    val rumorArabic: String,
    val rewardMoney: Int
) {
    NONE("", "", 0),
    REPAIR_NEIGHBOR_FENCE(
        titleArabic = "ساعد الجار في إصلاح سياج منزله",
        rumorArabic = "الجار يطلب المساعدة في ترميم وتثبيت السياج الخشبي للحديقة.",
        rewardMoney = 0
    ),
    FIND_GARDEN_KEY(
        titleArabic = "ابحث عن المفتاح المفقود في الحديقة",
        rumorArabic = "هذا البيت مغلق بالمفتاح، أحتاج إلى واحد! قد يكون مخبأً بين شجيرات الحديقة.",
        rewardMoney = 0
    ),
    BASEMENT_TREASURE(
        titleArabic = "هناك شائعات عن وجود كنز في هذا القبو",
        rumorArabic = "همسات قديمة تشير إلى صندوق تحف أثرية مدفون أسفل القبو.",
        rewardMoney = 0
    )
}

/**
 * 3D Building definition with coordinates, dimensions, aesthetic styling, and enterable door.
 */
data class Building(
    val id: String,
    val name: String,
    val arabicName: String,
    val type: BuildingType,
    val district: District,
    val posX: Float,
    val posY: Float = 0f,
    val posZ: Float,
    val width: Float,
    val height: Float,
    val depth: Float,
    val colorPrimary: FloatArray, // RGBA
    val colorSecondary: FloatArray, // RGBA
    val roofType: RoofType = RoofType.FLAT,
    val isEnterable: Boolean = false,
    val interiorType: InteriorType = InteriorType.NONE,
    val doorPosX: Float = posX,
    val doorPosZ: Float = posZ + depth / 2f + 1f,
    val iconEmoji: String = "🏢",
    val streetZone: StreetZone? = null,
    val facadeDescription: String = "",
    val listingBadge: String? = null,
    val salePrice: Int = 0,
    val rentPrice: Int = 0,
    val isLockedByDefault: Boolean = false,
    val houseSideQuest: HouseSideQuest = HouseSideQuest.NONE,
    val hasSingleCarGarage: Boolean = false
) {
    enum class RoofType {
        FLAT,
        PEAKED,
        DOMED,
        TIERED_SPIRE
    }

    data class CollisionBox(
        val centerX: Float,
        val centerZ: Float,
        val width: Float,
        val depth: Float,
        val minY: Float = 0f,
        val maxY: Float = 120f
    )

    val hasRooftopAirplanePad: Boolean
        get() = id.startsWith("city_street_house_") ||
            id.startsWith("standard_house_") ||
            streetZone == StreetZone.RESIDENTIAL_SUBURBS ||
            streetZone == StreetZone.OLD_TOWN ||
            type == BuildingType.RUSTIC_FARMHOUSE

    data class VillaExteriorSpec(
        val isCompact: Boolean,
        val lawnMinX: Float,
        val lawnMaxX: Float,
        val lawnMinZ: Float,
        val lawnMaxZ: Float,
        val gFloorH: Float,
        val fFloorH: Float,
        val porticoW: Float,
        val porticoOffsetZ: Float,
        val columnOffsetX: Float,
        val doorWidth: Float,
        val doorHeight: Float,
        val wingWinOffsetX: Float,
        val wingWinW: Float,
        val garX: Float,
        val garZ: Float,
        val garW: Float,
        val garH: Float,
        val garD: Float,
        val garWallT: Float,
        val garBayOffset: Float,
        val garBayDoorW: Float,
        val drivewayW: Float,
        val drivewayLen: Float,
        val pergX: Float,
        val pergZ: Float,
        val pergW: Float,
        val pergD: Float,
        val poolX: Float,
        val poolZ: Float,
        val poolW: Float,
        val poolD: Float,
        val mailboxX: Float,
        val mailboxZ: Float
    )

    fun getVillaExteriorSpec(): VillaExteriorSpec? {
        if (type != BuildingType.RESIDENTIAL_VILLA) return null
        val isCompact = width < 20f
        return if (isCompact) {
            val garW = 6.2f
            val garD = 8.4f
            val garH = 4.6f
            VillaExteriorSpec(
                isCompact = true,
                lawnMinX = posX - 9.85f,
                lawnMaxX = posX + 12.25f,
                lawnMinZ = posZ - 7.45f,
                lawnMaxZ = posZ + 14.65f,
                gFloorH = 6.8f,
                fFloorH = 6.2f,
                porticoW = 4.6f,
                porticoOffsetZ = 1.1f,
                columnOffsetX = 1.95f,
                doorWidth = 2.4f,
                doorHeight = 4.4f,
                wingWinOffsetX = 3.85f,
                wingWinW = 2.4f,
                garX = posX + width / 2f + garW / 2f,
                garZ = posZ + depth / 2f - garD / 2f,
                garW = garW,
                garH = garH,
                garD = garD,
                garWallT = 0.38f,
                garBayOffset = 1.35f,
                garBayDoorW = 2.45f,
                drivewayW = 6.2f,
                drivewayLen = 10.2f,
                pergX = posX - width / 2f - 2.1f,
                pergZ = posZ + 0.2f,
                pergW = 3.0f,
                pergD = 5.4f,
                poolX = posX - 6.2f,
                poolZ = posZ + depth / 2f + 5.8f,
                poolW = 5.2f,
                poolD = 4.6f,
                mailboxX = doorPosX + 2.75f,
                mailboxZ = doorPosZ + 0.8f
            )
        } else {
            VillaExteriorSpec(
                isCompact = false,
                lawnMinX = posX - (width + 18f) / 2f,
                lawnMaxX = posX + (width + 18f) / 2f,
                lawnMinZ = posZ - (depth + 18f) / 2f,
                lawnMaxZ = posZ + (depth + 18f) / 2f,
                gFloorH = 8.0f,
                fFloorH = 7.5f,
                porticoW = 9.0f,
                porticoOffsetZ = 1.5f,
                columnOffsetX = 3.8f,
                doorWidth = 3.2f,
                doorHeight = 5.0f,
                wingWinOffsetX = width / 4f + 1.2f,
                wingWinW = 5.0f,
                garX = posX + width / 2f + 5.5f,
                garZ = posZ,
                garW = 10.5f,
                garH = 6.8f,
                garD = 14.0f,
                garWallT = 0.6f,
                garBayOffset = 2.4f,
                garBayDoorW = 4.10f,
                drivewayW = 11.5f,
                drivewayLen = 26.5f,
                pergX = posX - width / 2f - 4.5f,
                pergZ = posZ + 3.0f,
                pergW = 8.0f,
                pergD = 8.5f,
                poolX = posX - width / 2f + 3.0f,
                poolZ = posZ + depth / 2f + 9.5f,
                poolW = 10.5f,
                poolD = 7.5f,
                mailboxX = doorPosX - 4.2f,
                mailboxZ = doorPosZ - 1.2f
            )
        }
    }

    /**
     * Returns all solid AABB collision boxes for this building (with exact vertical minY/maxY bounds so
     * characters/aircraft cannot clip walls, garages, or ceilings/roofs, while swimming pools are enterable).
     */
    fun getCollisionBoxes(): List<CollisionBox> {
        val mainMaxY = when {
            hasRooftopAirplanePad -> height + 0.25f
            id == "skyscraper_horizon" -> height + 0.30f
            roofType == RoofType.PEAKED -> height + height * 0.45f
            roofType == RoofType.DOMED -> height + width * 0.35f
            roofType == RoofType.TIERED_SPIRE -> height + 22f
            else -> height + 1.2f
        }
        if (id == "underground_city_garage") {
            // Hollow portal hood with wide open front ramp so vehicles & players can drive straight underground!
            val wallW = 2.6f
            val boxes = mutableListOf(
                CollisionBox(posX - width / 2f + wallW / 2f, posZ, wallW, depth, 0f, height),
                CollisionBox(posX + width / 2f - wallW / 2f, posZ, wallW, depth, 0f, height),
                CollisionBox(posX, posZ - depth / 2f + 1.4f, width, 2.8f, 0f, height),
                CollisionBox(posX, posZ - 1.5f, width, depth - 3.0f, 3.4f, height + 0.6f)
            )
            return boxes
        }

        val boxes = mutableListOf(CollisionBox(posX, posZ, width, depth, minY = 0f, maxY = mainMaxY))

        getVillaExteriorSpec()?.let { spec ->
            // Hollow 2-bay drive-in luxury garage on right wing (open front +Z so cars park inside!)
            val wt = spec.garWallT
            boxes.add(CollisionBox(spec.garX - spec.garW / 2f + wt / 2f, spec.garZ, wt, spec.garD, 0f, spec.garH)) // Left wall
            boxes.add(CollisionBox(spec.garX + spec.garW / 2f - wt / 2f, spec.garZ, wt, spec.garD, 0f, spec.garH)) // Right wall
            boxes.add(CollisionBox(spec.garX, spec.garZ - spec.garD / 2f + wt / 2f, spec.garW, wt, 0f, spec.garH)) // Back wall
            // Solid garage roof slab & slate cap so nothing falls or clips through garage ceiling/roof
            boxes.add(
                CollisionBox(
                    spec.garX,
                    spec.garZ,
                    spec.garW + 0.6f,
                    spec.garD + 0.6f,
                    spec.garH - 0.35f,
                    spec.garH + (if (spec.isCompact) 2.2f else 3.05f)
                )
            )

            // Pergola outdoor L-sofa seating & corner posts on left patio
            if (spec.isCompact) {
                boxes.add(CollisionBox(spec.pergX - 0.1f, spec.pergZ - 0.8f, 2.2f, 2.8f, 0f, 1.5f))
            } else {
                boxes.add(CollisionBox(spec.pergX - 0.8f, spec.pergZ - 1.5f, 6.2f, 4.2f, 0f, 1.8f))
            }
            // Notice: The Villa Outdoor Swimming Pool is recessed and OPEN for walking & swimming inside!
        }

        if (hasSingleCarGarage) {
            // Hollow 1-car garage attached next to the house (open front +Z, fits 1 car only)
            val garW = 4.4f
            val garH = 3.8f
            val garD = 7.4f
            val wallT = 0.28f
            val garX = posX + width / 2f + garW / 2f
            val garZ = posZ + depth / 2f - garD / 2f
            boxes.add(CollisionBox(garX - garW / 2f + wallT / 2f, garZ, wallT, garD, 0f, garH)) // Shared left wall
            boxes.add(CollisionBox(garX + garW / 2f - wallT / 2f, garZ, wallT, garD, 0f, garH)) // Right outer wall
            boxes.add(CollisionBox(garX, garZ - garD / 2f + wallT / 2f, garW, wallT, 0f, garH)) // Back wall
            // Solid 1-car garage roof slab & pitched cap
            boxes.add(CollisionBox(garX, garZ, garW + 0.35f, garD + 0.35f, garH, garH + 1.6f))
        }
        return boxes
    }

    /**
     * Returns the outdoor pool inner basin bounds [minX, maxX, minZ, maxZ] for RESIDENTIAL_VILLA.
     */
    fun getOutdoorPoolBounds(): FloatArray? {
        val spec = getVillaExteriorSpec() ?: return null
        val halfW = spec.poolW / 2f
        val halfD = spec.poolD / 2f
        return floatArrayOf(spec.poolX - halfW, spec.poolX + halfW, spec.poolZ - halfD, spec.poolZ + halfD)
    }

    /**
     * True when (px, pz) lies inside the Villa outdoor swimming pool water basin.
     */
    fun isPointInOutdoorPool(px: Float, pz: Float, inset: Float = 0.1f): Boolean {
        val b = getOutdoorPoolBounds() ?: return false
        return px >= (b[0] + inset) && px <= (b[1] - inset) &&
            pz >= (b[2] + inset) && pz <= (b[3] - inset)
    }

    /**
     * Returns the elevation Y of the rooftop airplane landing/parking pad on this building, if equipped.
     */
    fun getRooftopAirplanePadY(): Float? {
        return when {
            hasRooftopAirplanePad -> height + 0.25f
            id == "skyscraper_horizon" -> height + 0.30f
            else -> null
        }
    }

    /**
     * Checks if world (px, pz) sits over this building's rooftop aircraft landing & parking pad.
     */
    fun isPointOnRooftopPad(px: Float, pz: Float, margin: Float = 0.4f): Boolean {
        if (getRooftopAirplanePadY() == null) return false
        val halfW = if (id == "skyscraper_horizon") 9.0f else (width / 2f + 0.6f)
        val halfD = if (id == "skyscraper_horizon") 9.0f else (depth / 2f + 0.6f)
        return px >= (posX - halfW - margin) && px <= (posX + halfW + margin) &&
            pz >= (posZ - halfD - margin) && pz <= (posZ + halfD + margin)
    }

    /**
     * Checks if player stands near the exterior side access ladder leading to this street house's rooftop pad.
     */
    fun isNearRooftopLadder(px: Float, pz: Float, interactDistance: Float = 2.6f): Boolean {
        if (!hasRooftopAirplanePad) return false
        val ladderX = posX - width / 2f - 0.85f
        val ladderZ = posZ
        val dx = px - ladderX
        val dz = pz - ladderZ
        return (dx * dx + dz * dz) <= (interactDistance * interactDistance)
    }

    /**
     * Returns the lowest solid overhang/garage ceiling bottom Y above (px, py, pz), or null if open sky.
     */
    fun getCeilingUnderRoofY(px: Float, py: Float, pz: Float): Float? {
        for (box in getCollisionBoxes()) {
            if (box.minY > py + 0.4f) {
                val halfW = box.width / 2f + 0.25f
                val halfD = box.depth / 2f + 0.25f
                if (px in (box.centerX - halfW)..(box.centerX + halfW) &&
                    pz in (box.centerZ - halfD)..(box.centerZ + halfD)
                ) {
                    return box.minY
                }
            }
        }
        return null
    }

    /**
     * Returns the interior parking bay center (x, z) if this building has a garage.
     */
    fun getGarageParkCenter(): Pair<Float, Float>? {
        val villaSpec = getVillaExteriorSpec()
        return when {
            villaSpec != null -> Pair(villaSpec.garX, villaSpec.garZ + 0.5f)
            hasSingleCarGarage -> {
                val garW = 4.4f
                val garD = 7.4f
                Pair(posX + width / 2f + garW / 2f, posZ + depth / 2f - garD / 2f + 0.35f)
            }
            else -> null
        }
    }

    /**
     * Returns the front entrance Z coordinate (+Z opening facing the street) of the single-car garage.
     */
    fun getSingleCarGarageFrontZ(): Float? {
        if (!hasSingleCarGarage) return null
        return posZ + depth / 2f
    }

    /**
     * Returns inner bay bounds [minX, maxX, minZ, maxZ] for the 1-car garage.
     */
    fun getSingleCarGarageBounds(): FloatArray? {
        if (!hasSingleCarGarage) return null
        val garW = 4.4f
        val garD = 7.4f
        val wallT = 0.28f
        val garX = posX + width / 2f + garW / 2f
        val garZ = posZ + depth / 2f - garD / 2f
        return floatArrayOf(
            garX - garW / 2f + wallT,
            garX + garW / 2f - wallT,
            garZ - garD / 2f + wallT,
            garZ + garD / 2f
        )
    }

    /**
     * Checks if world coordinate (px, pz) lies inside (or within [padding] of) this building's 1-car garage bay.
     */
    fun isPointInSingleCarGarageBay(px: Float, pz: Float, padding: Float = 0f): Boolean {
        val b = getSingleCarGarageBounds() ?: return false
        return px >= (b[0] - padding) && px <= (b[1] + padding) &&
            pz >= (b[2] - padding) && pz <= (b[3] + padding)
    }

    /**
     * Checks if a 2D footprint at ground height (py = 0f) collides with any solid volume of this building.
     */
    fun checkCollision(px: Float, pz: Float, margin: Float = 0.8f): Boolean {
        return checkCollision3D(px, 0f, pz, margin = margin)
    }

    /**
     * Height-aware 3D collision check: prevents clipping through walls, garage roofs, and pitched roofs
     * while allowing walking/parking cleanly on top of rooftop airplane decks and driving inside open garages.
     */
    fun checkCollision3D(
        px: Float,
        py: Float,
        pz: Float,
        margin: Float = 0.68f,
        bodyHeight: Float = 1.80f
    ): Boolean {
        for (box in getCollisionBoxes()) {
            if (py + bodyHeight <= box.minY + 0.08f || py >= box.maxY - 0.14f) continue
            val halfW = box.width / 2f + margin
            val halfD = box.depth / 2f + margin
            if (px >= (box.centerX - halfW) && px <= (box.centerX + halfW) &&
                pz >= (box.centerZ - halfD) && pz <= (box.centerZ + halfD)
            ) {
                return true
            }
        }
        return false
    }

    /**
     * Resolves a 2D collision at ground level (py = 0f) by computing the pushed-out (x, z) position.
     */
    fun resolveCollisionPush(px: Float, pz: Float, margin: Float = 0.8f): Pair<Float, Float>? {
        return resolveCollisionPush3D(px, 0f, pz, margin = margin)
    }

    /**
     * Resolves 3D wall/roof collision along minimum penetration axis while respecting rooftop/garage elevation.
     */
    fun resolveCollisionPush3D(
        px: Float,
        py: Float,
        pz: Float,
        margin: Float = 0.68f,
        bodyHeight: Float = 1.80f
    ): Pair<Float, Float>? {
        var resolvedX = px
        var resolvedZ = pz
        var collided = false
        for (box in getCollisionBoxes()) {
            if (py + bodyHeight <= box.minY + 0.08f || py >= box.maxY - 0.14f) continue
            val halfW = box.width / 2f + margin
            val halfD = box.depth / 2f + margin
            val dx = resolvedX - box.centerX
            val dz = resolvedZ - box.centerZ
            val overlapX = halfW - kotlin.math.abs(dx)
            val overlapZ = halfD - kotlin.math.abs(dz)
            if (overlapX > 0f && overlapZ > 0f) {
                collided = true
                if (overlapX < overlapZ) {
                    resolvedX = if (dx >= 0f) box.centerX + halfW + 0.04f else box.centerX - halfW - 0.04f
                } else {
                    resolvedZ = if (dz >= 0f) box.centerZ + halfD + 0.04f else box.centerZ - halfD - 0.04f
                }
            }
        }
        return if (collided) Pair(resolvedX, resolvedZ) else null
    }

    /**
     * Checks if the player is close enough to interact with the door.
     */
    fun isNearDoor(px: Float, pz: Float, interactDistance: Float = 4.5f): Boolean {
        if (!isEnterable) return false
        val dx = px - doorPosX
        val dz = pz - doorPosZ
        return (dx * dx + dz * dz) <= (interactDistance * interactDistance)
    }

    /**
     * Returns true if this house/residence has a front-of-house envelope mailbox («مكان لوضع الظرف أمام البيت»).
     */
    fun hasEnvelopeMailbox(): Boolean {
        return hasSingleCarGarage ||
            type == BuildingType.RUSTIC_FARMHOUSE ||
            type == BuildingType.RESIDENTIAL_VILLA ||
            interiorType == InteriorType.STANDARD_HOUSE ||
            interiorType == InteriorType.PLAYER_VILLA
    }

    /**
     * Returns the world (x, z) coordinates of the front-yard envelope mailbox post standing in front of this house.
     */
    fun getMailboxPosition(): Pair<Float, Float>? {
        if (!hasEnvelopeMailbox()) return null
        val villaSpec = getVillaExteriorSpec()
        return if (villaSpec != null) {
            Pair(villaSpec.mailboxX, villaSpec.mailboxZ)
        } else {
            val offsetX = (width * 0.28f).coerceIn(2.0f, 3.6f)
            Pair(posX - offsetX, posZ + depth / 2f + 1.65f)
        }
    }

    /**
     * Checks if the player is standing near this house's envelope mailbox.
     */
    fun isNearMailbox(px: Float, pz: Float, interactDistance: Float = 4.2f): Boolean {
        val pos = getMailboxPosition() ?: return false
        val dx = px - pos.first
        val dz = pz - pos.second
        return (dx * dx + dz * dz) <= (interactDistance * interactDistance)
    }
}
