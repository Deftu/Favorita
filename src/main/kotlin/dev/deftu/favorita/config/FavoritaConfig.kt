package dev.deftu.favorita.config

import dev.deftu.favorita.Favorita
import dev.deftu.favorita.slots.SlotLocks
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.KtConfig
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper

//? if >=1.21 {
import com.mojang.blaze3d.platform.InputConstants
//?} else {
/*import org.polyfrost.oneconfig.internal.legacy.InputConstants
*///?}

enum class HotbarLocks(val label: String) {
    ALWAYS("Always"),
    WHEN_USED("When used"),
    NEVER("Never"),
}

enum class BlockedFeedback(val label: String) {
    NOTIFICATION("Notification"),
    CHAT("Chat message"),
    NONE("None"),
}

object FavoritaConfig : KtConfig("favorita.json", Favorita.NAME, Category.QOL, Favorita.ICON) {
    private const val GENERAL = "General"
    private const val APPEARANCE = "Appearance"

    var enabled by masterToggle()

    var favoriteKey by keybind(
        name = "Favorite slot",
        def = KeybindHelper.builder().key(InputConstants.KEY_B).action(Runnable(SlotLocks::toggleSelectedHotbarSlot)).build(),
        nameKey = "key.favorita.favorite",
        description = "Locks or unlocks the hovered slot in an inventory, or your selected hotbar slot while playing.",
        category = GENERAL,
        subcategory = "Locking",
    )

    var unlockFromHotbar by switch(
        name = "Unlock from the hotbar",
        def = true,
        description = "Lets the favorite slot key unlock your held hotbar slot while no inventory is open. Turn it off so slots can only be unlocked from an inventory.",
        category = GENERAL,
        subcategory = "Locking",
    )

    var protectFromGathering by switch(
        name = "Protect from double-clicks",
        def = true,
        description = "Stops a double-click from gathering matching items out of locked slots.",
        category = GENERAL,
        subcategory = "Protection",
    )

    var playSounds by switch(
        name = "Play sounds",
        def = true,
        description = "Plays a sound when you lock or unlock a slot, and when a locked slot stops an action.",
        category = GENERAL,
        subcategory = "Feedback",
    )

    var blockedFeedback by dropdown(
        name = "Blocked message",
        defaultOption = BlockedFeedback.NOTIFICATION,
        options = BlockedFeedback.entries.toTypedArray(),
        description = "How you're told that a locked slot stopped an action.",
        category = GENERAL,
        subcategory = "Feedback",
        stringTransformer = { it.label },
    )

    var lockColor by color(
        name = "Lock color",
        def = PolyColor.rgba(201, 42, 67, 89),
        description = "The tint drawn over locked slots.",
        category = APPEARANCE,
        subcategory = "Locks",
    )

    var showLockIcon by switch(
        name = "Show lock icon",
        def = true,
        description = "Draws a padlock on top of the tint.",
        category = APPEARANCE,
        subcategory = "Locks",
    )

    var hotbarLocks by dropdown(
        name = "Hotbar locks",
        defaultOption = HotbarLocks.WHEN_USED,
        options = HotbarLocks.entries.toTypedArray(),
        description = "When to draw locks on the hotbar. When used shows them for a few seconds after you lock a slot or one stops an action.",
        category = APPEARANCE,
        subcategory = "Locks",
        stringTransformer = { it.label },
    )
}
