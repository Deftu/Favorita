package dev.deftu.favorita.client

//#if MC <= 1.8.9
//$$ import com.google.gson.JsonPrimitive
//#endif

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.deftu.favorita.FavoritaConstants
import dev.deftu.omnicore.annotations.GameSide
import dev.deftu.omnicore.annotations.Side
import dev.deftu.omnicore.client.OmniClient
import org.jetbrains.annotations.ApiStatus.Internal
import java.io.File

private val deftuDir by lazy {
    val deftuDir = File("Deftu")
    if (!deftuDir.exists() && !deftuDir.mkdirs()) {
        throw IllegalStateException("Could not create Deftu directory!")
    }

    deftuDir
}

private val configFile by lazy {
    File(deftuDir, "${FavoritaConstants.ID}.json")
}

/**
 * Saves favorited items to a config file.
 *
 * The file's schema is as follows:
 * ```json
 * [
 *     {
 *         "identifier": "server_address",
 *         "slots": [0,1,2]
 *     },
 *     {
 *         "identifier": "world_name",
 *         "slots": [3,4,5]
 *     }
 * ]
 * ```
 */
@GameSide(Side.CLIENT)
object FavoritaConfig {

    @GameSide(Side.CLIENT)
    const val SINGLEPLAYER_IDENTIFIER = "Singleplayer"

    //#if MC <= 1.17.1
    //$$ private val jsonParser = JsonParser()
    //#endif

    private val data = mutableMapOf<String, Set<Int>>()
    private var isDirty = false

    private val currentIdentifier: String
        get() = OmniClient.Multiplayer.getCurrentServerAddress() ?: SINGLEPLAYER_IDENTIFIER

    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun save() {
        if (!isDirty) {
            return
        }

        val array = JsonArray()
        for ((identifier, slots) in data) {
            val obj = JsonObject()
            obj.addProperty("identifier", identifier)

            val slotsArray = JsonArray()
            for (slot in slots.sorted()) {
                //#if MC >= 1.12.2
                slotsArray.add(slot)
                //#else
                //$$ slotsArray.add(JsonPrimitive(slot))
                //#endif
            }

            obj.add("slots", slotsArray)
            array.add(obj)
        }

        configFile.writeText(array.toString())
    }

    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun load() {
        if (!configFile.exists()) {
            if (!configFile.createNewFile()) {
                throw IllegalStateException("Could not create config file!")
            }

            // Create and populate a new config file with the singleplayer identifier
            data[SINGLEPLAYER_IDENTIFIER] = emptySet()
            save()
            return
        }

        val array = configFile.readText().let { content ->
            try {
                //#if MC >= 1.18.2
                JsonParser.parseString(content).asJsonArray
                //#else
                //$$ jsonParser.parse(content).asJsonArray
                //#endif
            } catch (e: Exception) {
                return
            }
        }

        for (element in array) {
            if (!element.isJsonObject) {
                continue
            }

            val obj = element.asJsonObject
            if (!obj.has("identifier") || !obj.has("slots")) {
                continue
            }

            val identifier = obj.get("identifier").asString
            val slots = obj.getAsJsonArray("slots").mapNotNull { it.asInt }.toSet()
            data[identifier] = slots
        }
    }

    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun isFavorited(
        identifier: String,
        slotIndex: Int
    ): Boolean {
        return data[identifier]?.contains(slotIndex) == true
    }

    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun isFavorited(slotIndex: Int): Boolean {
        return isFavorited(currentIdentifier, slotIndex)
    }

    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun setFavorited(
        identifier: String,
        slotIndex: Int,
        isFavorited: Boolean
    ) {
        val slots = data.getOrPut(identifier) { mutableSetOf() }.toMutableSet()
        if (isFavorited) {
            slots.add(slotIndex)
        } else {
            slots.remove(slotIndex)
        }

        data[identifier] = slots
        isDirty = true
    }

    @Internal
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun setFavorited(slotIndex: Int, isFavorited: Boolean) {
        setFavorited(currentIdentifier, slotIndex, isFavorited)
    }

}
