package dev.deftu.favorita.slots

import dev.deftu.favorita.config.BlockedFeedback
import dev.deftu.favorita.config.FavoritaConfig
import dev.deftu.favorita.config.HotbarLocks
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import org.polyfrost.oneconfig.api.platform.v1.Platform

/** Decides what a locked slot blocks, and gives the feedback for it */
object SlotLocks {
    private const val FLASH_MS = 3000L
    private const val FEEDBACK_COOLDOWN_MS = 600L

    // Ordinals of vanilla's click types, which 1.8.9 passes as plain ints in the same order
    private const val QUICK_MOVE = 1
    private const val SWAP = 2
    private const val PICKUP_ALL = 6

    private var cachedLevel: Any? = null
    private var cachedPlace: GameSession.Place? = null
    private val flashes = HashMap<Int, Long>()
    private var lastFeedback = 0L

    /** The world or server whose locks apply right now, refreshed whenever the client's level changes */
    val currentPlace: GameSession.Place?
        get() {
            //? if >=1.21 {
            val level = Minecraft.getInstance().level
            //?} else {
            /*val level = Minecraft.getInstance().world
            *///?}
            if (level !== cachedLevel) {
                // The level can exist a moment before the player does, so it only counts as seen once the place resolves
                val place = if (level == null) null else GameSession.currentPlace ?: return null
                cachedLevel = level
                if (place?.key != cachedPlace?.key) flashes.clear()
                cachedPlace = place?.also(FavoriteStore::visit)
            }
            return cachedPlace
        }

    @JvmStatic
    fun isLocked(index: Int): Boolean {
        if (index < 0 || !FavoritaConfig.enabled) return false
        val place = currentPlace ?: return false
        return FavoriteStore.isLocked(place.key, index)
    }

    @JvmStatic
    fun isLocked(slot: MenuSlot?): Boolean = slot != null && isLocked(GameSession.inventoryIndex(slot))

    @JvmStatic
    fun toggle(index: Int): Boolean {
        if (index < 0 || !FavoritaConfig.enabled) return false
        val place = currentPlace ?: return false
        val locked = !FavoriteStore.isLocked(place.key, index)
        FavoriteStore.setLocked(place.key, index, locked)
        if (FavoritaConfig.playSounds) GameSession.playLockSound(locked)
        flash(index)
        return true
    }

    @JvmStatic
    fun toggle(slot: MenuSlot?): Boolean = slot != null && toggle(GameSession.inventoryIndex(slot))

    fun toggleSelectedHotbarSlot() {
        val selected = GameSession.selectedHotbarSlot
        if (!FavoritaConfig.unlockFromHotbar && isLocked(selected)) {
            flash(selected)
            blocked("Unlock it from your inventory.")
            return
        }
        toggle(selected)
    }


    /** Whether a click on the open menu would take from or overwrite a locked slot */
    @JvmStatic
    fun blocksMenuClick(slotId: Int, button: Int, action: Int): Boolean {
        val blocked = when {
            isLocked(GameSession.inventoryIndexInOpenMenu(slotId)) -> true
            action == SWAP && isLocked(swapTarget(button)) -> true
            action == PICKUP_ALL && FavoritaConfig.protectFromGathering -> GameSession.gatherWouldTakeFrom(::isLocked)
            action == QUICK_MOVE -> return quickMoveAround(false, slotId, button)
            else -> false
        }
        if (blocked) blocked()
        return blocked
    }

    /** The creative screen handles its own clicks, so it is checked against the clicked slot directly */
    @JvmStatic
    fun blocksCreativeClick(slot: MenuSlot?, button: Int, action: Int): Boolean {
        val blocked = isLocked(slot) ||
            (action == SWAP && isLocked(swapTarget(button))) ||
            (action == PICKUP_ALL && FavoritaConfig.protectFromGathering && GameSession.gatherWouldTakeFrom(::isLocked))
        if (blocked) {
            blocked()
            return true
        }
        val target = slot?.let(GameSession::creativeTargetId) ?: return false
        return action == QUICK_MOVE && quickMoveAround(true, target, button)
    }

    /** Sends a shift-click's items past locked slots. True when the original click must not go through */
    private fun quickMoveAround(inPlayerMenu: Boolean, slotId: Int, button: Int): Boolean =
        when (QuickMoves.moveAround(inPlayerMenu, slotId, button, ::isLocked)) {
            QuickMoves.Result.UNTOUCHED -> false
            QuickMoves.Result.MOVED -> true
            QuickMoves.Result.BLOCKED -> true.also { blocked() }
        }

    /** The creative bin's clear-all and hotbar loading write into the player's menu by slot id, so locked ones are skipped */
    @JvmStatic
    fun keepsOnCreativeClear(playerMenuSlotId: Int): Boolean = isLocked(GameSession.inventoryIndexInPlayerMenu(playerMenuSlotId))

    @JvmStatic
    fun blocksDrop(): Boolean {
        val selected = GameSession.selectedHotbarSlot
        if (!isLocked(selected)) return false
        flash(selected)
        blocked()
        return true
    }

    /** Swapping hands outside a screen would move a locked held item or replace a locked offhand */
    @JvmStatic
    fun blocksOffhandSwap(): Boolean {
        val locked = listOf(GameSession.selectedHotbarSlot, GameSession.OFFHAND_SLOT).filter(::isLocked)
        if (locked.isEmpty()) return false
        locked.forEach(::flash)
        blocked()
        return true
    }

    /** How strongly to draw a hotbar slot's lock, from 0 for hidden to 1 for fully shown */
    @JvmStatic
    fun hotbarLockAlpha(index: Int): Float {
        if (!isLocked(index)) return flashAlpha(index, unlocking = true)
        return when (FavoritaConfig.hotbarLocks) {
            HotbarLocks.ALWAYS -> 1f
            HotbarLocks.WHEN_USED -> flashAlpha(index, unlocking = false)
            HotbarLocks.NEVER -> 0f
        }
    }

    private fun flashAlpha(index: Int, unlocking: Boolean): Float {
        val started = flashes[index] ?: return 0f
        val elapsed = System.currentTimeMillis() - started
        if (elapsed >= FLASH_MS) {
            flashes.remove(index)
            return 0f
        }
        val fade = ((FLASH_MS - elapsed) / 500f).coerceAtMost(1f)
        return if (unlocking) fade * 0.6f else fade
    }

    private fun flash(index: Int) {
        if (index in 0..8 || index == GameSession.OFFHAND_SLOT) flashes[index] = System.currentTimeMillis()
    }

    /** A swap's button is the hotbar slot it swaps with, or 40 for the offhand */
    private fun swapTarget(button: Int): Int = if (button in 0..8 || button == GameSession.OFFHAND_SLOT) button else -1

    private fun blocked(hint: String = "Unlock it with your favorite slot key first.") {
        val now = System.currentTimeMillis()
        if (now - lastFeedback < FEEDBACK_COOLDOWN_MS) return
        lastFeedback = now
        if (FavoritaConfig.playSounds) GameSession.playBlockedSound()
        when (FavoritaConfig.blockedFeedback) {
            BlockedFeedback.NOTIFICATION -> Notifications.info("Slot locked", hint)
            BlockedFeedback.CHAT -> Platform.compatibility().displayChatMessage(
                Component.translatable().key("message.favorita.blocked").fallback("This slot is locked!").color(NamedTextColor.RED).build(),
            )
            BlockedFeedback.NONE -> Unit
        }
    }
}
