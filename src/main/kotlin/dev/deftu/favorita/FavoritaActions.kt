package dev.deftu.favorita

import dev.deftu.favorita.config.FavoritaConfig
import dev.deftu.favorita.slots.FavoriteStore
import dev.deftu.favorita.slots.SlotLocks
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI
import org.polyfrost.oneconfig.api.ui.v1.page.OneConfigPages

@OptIn(ExperimentalOneConfigUiApi::class)
object FavoritaActions {
    fun openConfig() {
        OneConfigUI.open(OneConfigPages.modConfig(FavoritaConfig.id))
    }

    fun clearCurrent(): Boolean {
        val place = SlotLocks.currentPlace
        if (place == null) {
            Notifications.error("Not in a world", "Join a world or server to clear its locked slots.")
            return false
        }
        FavoriteStore.setSlots(place.key, emptySet())
        Notifications.success("Slots unlocked", "Every slot in ${place.name} is unlocked.")
        return true
    }
}
