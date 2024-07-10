package dev.deftu.favorita.client

import dev.deftu.favorita.FavoritaConstants
import dev.deftu.omnicore.client.OmniKeyBinding
import dev.deftu.omnicore.client.OmniKeyboard
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.annotations.ApiStatus.Internal

object FavoritaClient {

    @JvmStatic
    val logger: Logger = LogManager.getLogger()

    @JvmStatic
    val isDebug: Boolean
        get() = System.getProperty("favorita.debug")?.toBoolean() ?: false

    @JvmStatic
    val favoriteKeyBinding = OmniKeyBinding("key.favorita.favorite", FavoritaConstants.NAME, OmniKeyboard.KEY_B)

    @Internal
    @JvmStatic
    val renderedHotbarIndices = mutableSetOf<Int>()

    @Internal
    @JvmStatic
    val renderHotbarState = mutableMapOf<Int, HotbarState>()

    internal fun onInitializeClient() {
        println("Hello Favorita ${FavoritaConstants.VERSION}!")
        FavoritaConfig.load()

        //#if MC <= 1.18.2
        favoriteKeyBinding.attemptRegister()
        //#endif
    }

    enum class HotbarState {
        LOCKED,
        UNLOCKED
    }

}
