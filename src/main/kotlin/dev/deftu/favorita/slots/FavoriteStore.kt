package dev.deftu.favorita.slots

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import dev.deftu.favorita.Favorita
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/** A world or server and the inventory slots locked in it */
data class FavoritePlace(
    val key: String,
    val name: String,
    val isServer: Boolean,
    val slots: Set<Int>,
    val lastPlayed: Long,
)

/** Every place's locked slots, saved to config/Favorita/favorites.json */
object FavoriteStore {
    /** Holds the slots that 0.x shared between all singleplayer worlds, until each world adopts a copy */
    const val LEGACY_SINGLEPLAYER_KEY = "legacy:singleplayer"

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val places = HashMap<String, FavoritePlace>()

    private val file: File by lazy {
        FabricLoader.getInstance().configDir.resolve(Favorita.NAME).toFile().apply { mkdirs() }.resolve("favorites.json")
    }

    fun load() {
        if (file.isFile) {
            runCatching { read(JsonParser.parseString(file.readText()).asJsonObject) }.onFailure {
                // Kept aside so the next save doesn't overwrite the only copy of the user's locks
                val backup = File(file.parentFile, "${file.name}.bad")
                Favorita.LOGGER.error("Failed to read {}, moving it to {}", file, backup, it)
                places.clear()
                Files.move(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }
        if (importLegacy()) save()
    }

    fun isLocked(key: String, slot: Int): Boolean = places[key]?.slots?.contains(slot) == true

    fun setLocked(key: String, slot: Int, locked: Boolean) = update(key) { place ->
        place.copy(slots = if (locked) place.slots + slot else place.slots - slot)
    }

    fun setSlots(key: String, slots: Set<Int>) = update(key) { it.copy(slots = slots) }

    /** Records a visit, and gives a world it hasn't seen before the slots 0.x shared between all worlds */
    fun visit(place: GameSession.Place) {
        val existing = places[place.key]
        val seeded = existing?.slots ?: if (!place.isServer) places[LEGACY_SINGLEPLAYER_KEY]?.slots.orEmpty() else emptySet()
        places[place.key] = FavoritePlace(place.key, place.name, place.isServer, seeded, System.currentTimeMillis())
        save()
    }

    private fun update(key: String, transform: (FavoritePlace) -> FavoritePlace) {
        val place = places[key] ?: FavoritePlace(key, key, !GameSession.isWorldKey(key), emptySet(), 0L)
        val updated = transform(place)
        if (updated == place) return
        places[key] = updated
        save()
    }

    private fun save() {
        val root = JsonObject()
        root.addProperty("version", 1)
        root.add("places", JsonArray().apply { places.values.sortedByDescending { it.lastPlayed }.forEach { add(write(it)) } })
        runCatching {
            val temp = File(file.parentFile, "${file.name}.tmp")
            temp.writeText(gson.toJson(root))
            try {
                Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        }.onFailure { Favorita.LOGGER.error("Failed to save {}", file, it) }
    }

    private fun read(root: JsonObject) {
        for (element in root.getAsJsonArray("places") ?: return) {
            val obj = element.takeIf { it.isJsonObject }?.asJsonObject ?: continue
            val key = obj.get("key")?.asString?.takeIf { it.isNotBlank() } ?: continue
            places[key] = FavoritePlace(
                key = key,
                name = obj.get("name")?.asString ?: key,
                isServer = obj.get("server")?.asBoolean ?: !GameSession.isWorldKey(key),
                slots = readSlots(obj.getAsJsonArray("slots")),
                lastPlayed = obj.get("lastPlayed")?.asLong ?: 0L,
            )
        }
    }

    private fun write(place: FavoritePlace) = JsonObject().apply {
        addProperty("key", place.key)
        addProperty("name", place.name)
        addProperty("server", place.isServer)
        add("slots", JsonArray().apply { place.slots.sorted().forEach { add(JsonPrimitive(it)) } })
        addProperty("lastPlayed", place.lastPlayed)
    }

    private fun readSlots(array: JsonArray?): Set<Int> =
        array?.mapNotNull { element -> element.takeIf { it.isJsonPrimitive }?.asInt?.takeIf { it >= 0 } }?.toSet().orEmpty()

    /** Moves 0.x's Deftu/favorita.json in, which was keyed by server address with one entry for all singleplayer worlds */
    private fun importLegacy(): Boolean {
        val legacy = File(FabricLoader.getInstance().gameDir.toFile(), "Deftu/favorita.json")
        if (!legacy.isFile) return false
        val imported = runCatching {
            for (element in JsonParser.parseString(legacy.readText()).asJsonArray) {
                val obj = element.takeIf { it.isJsonObject }?.asJsonObject ?: continue
                val identifier = obj.get("identifier")?.asString?.takeIf { it.isNotBlank() } ?: continue
                val slots = readSlots(obj.getAsJsonArray("slots"))
                if (slots.isEmpty()) continue
                val isSingleplayer = identifier == "Singleplayer"
                val key = if (isSingleplayer) LEGACY_SINGLEPLAYER_KEY else identifier.lowercase()
                places.putIfAbsent(key, FavoritePlace(key, if (isSingleplayer) "Singleplayer" else identifier, !isSingleplayer, slots, 0L))
            }
        }.onFailure { Favorita.LOGGER.error("Failed to import {}", legacy, it) }.isSuccess
        if (imported && !legacy.renameTo(File(legacy.parentFile, "favorita.json.migrated"))) legacy.delete()
        return imported
    }
}
