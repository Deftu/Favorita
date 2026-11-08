package dev.deftu.favorita.slots

import dev.deftu.favorita.config.FavoritaConfig
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.KeyInputEvent
import org.polyfrost.oneconfig.api.event.v1.events.MouseInputEvent
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeyModifiers
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 * Matches the favorite key inside inventory screens.
 *
 * OneConfig only fires the keybind's action outside screens, so screens check presses themselves to toggle the hovered slot
 * and swallow the input before vanilla acts on it. OneConfig posts its input events before the screen sees the press,
 * so the held keys tracked here already include it.
 */
object FavoriteKey {
    private val downKeys = HashSet<Int>()
    private val downMouse = HashSet<Int>()
    private var mods = KeyModifiers.NONE
    private var lastKeyRepeated = false

    fun register() {
        eventHandler { (key, _, state): KeyInputEvent ->
            if (key == 0) return@eventHandler
            lastKeyRepeated = state == KeyInputEvent.REPEAT
            if (lastKeyRepeated) return@eventHandler
            val down = state == KeyInputEvent.PRESSED
            val mod = KeyModifiers.of(key)
            if (mod != KeyModifiers.NONE) mods = if (down) mods or mod else mods and mod.inv()
            if (down) downKeys.add(key) else downKeys.remove(key)
        }
        eventHandler { (button, state): MouseInputEvent ->
            if (state == MouseInputEvent.PRESSED) downMouse += button else downMouse -= button
        }
    }

    @JvmStatic
    fun matchesKey(keyCode: Int): Boolean =
        !lastKeyRepeated && FavoritaConfig.favoriteKey.keyCodes?.contains(keyCode) == true && isHeld()

    @JvmStatic
    fun matchesMouse(button: Int): Boolean =
        FavoritaConfig.favoriteKey.mouseBtns?.contains(button) == true && isHeld()

    /** The bind's whole combination is down, so a bind like Ctrl+B doesn't fire on a plain B */
    private fun isHeld(): Boolean {
        val bind = FavoritaConfig.favoriteKey
        if (!FavoritaConfig.enabled || !bind.isBound) return false
        if ((mods and bind.mods) != bind.mods) return false
        return bind.keyCodes?.all(downKeys::contains) != false && bind.mouseBtns?.all(downMouse::contains) != false
    }
}
