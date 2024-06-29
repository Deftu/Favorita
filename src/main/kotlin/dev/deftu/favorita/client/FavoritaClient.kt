package dev.deftu.favorita.client

//#if FABRIC
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
//#elseif FORGE
//#if MC == 1.18.2
//$$ import net.minecraftforge.client.ClientRegistry
//#elseif MC == 1.17.1
//$$ import net.minecraftforge.fmlclient.registry.ClientRegistry
//#elseif MC <= 1.16.5
//$$ import net.minecraftforge.fml.client.registry.ClientRegistry
//#endif
//#endif

import dev.deftu.favorita.FavoritaConstants
import dev.deftu.omnicore.client.OmniKeyboard
import net.minecraft.client.option.KeyBinding
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
    val favoriteKeyBinding: KeyBinding by lazy {
        KeyBinding("key.favorita.favorite", OmniKeyboard.KEY_F, FavoritaConstants.NAME)
    }

    @Internal
    @JvmStatic
    val renderedHotbarIndices = mutableSetOf<Int>()

    @Internal
    @JvmStatic
    val renderHotbarState = mutableMapOf<Int, HotbarState>()

    internal fun onInitializeClient() {
        FavoritaConfig.load()

        //#if FABRIC
        for (keyBinding in getKeyBindings()) {
            KeyBindingHelper.registerKeyBinding(keyBinding)
        }
        //#elseif FORGE && MC <= 1.18.2
        //$$ for (keyBinding in getKeyBindings()) {
        //$$     ClientRegistry.registerKeyBinding(keyBinding)
        //$$ }
        //#endif
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal fun getKeyBindings(): List<KeyBinding> {
        return listOf(favoriteKeyBinding)
    }

    enum class HotbarState {
        LOCKED,
        UNLOCKED
    }

}
