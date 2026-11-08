package dev.deftu.favorita.api

import dev.deftu.favorita.exceptions.NoIdentifierException
import dev.deftu.favorita.slots.FavoriteStore
import dev.deftu.favorita.slots.SlotLocks

/**
 * Checks and changes locked slots.
 *
 * A place is a server's address in lowercase, or `world:` followed by a singleplayer world's save folder name.
 * Slots are player inventory indices: 0 to 8 for the hotbar, 9 to 35 for the main inventory, 36 to 39 for armor and 40 for the offhand.
 * The results don't depend on whether the player has turned Favorita off.
 */
object FavoritedSlots {
    /** The current world or server's place, or null on the title screen */
    @JvmStatic
    val currentPlace: String?
        get() = SlotLocks.currentPlace?.key

    /** @throws NoIdentifierException if [place] is blank */
    @JvmStatic
    fun isFavorited(place: String, slotIndex: Int): Boolean = FavoriteStore.isLocked(checked(place), slotIndex)

    /** @throws NoIdentifierException if no world or server is loaded */
    @JvmStatic
    fun isFavorited(slotIndex: Int): Boolean = isFavorited(current(), slotIndex)

    /** @throws NoIdentifierException if [place] is blank */
    @JvmStatic
    fun setFavorited(place: String, slotIndex: Int, isFavorited: Boolean) = FavoriteStore.setLocked(checked(place), slotIndex, isFavorited)

    /** @throws NoIdentifierException if no world or server is loaded */
    @JvmStatic
    fun setFavorited(slotIndex: Int, isFavorited: Boolean) = setFavorited(current(), slotIndex, isFavorited)

    private fun current(): String = currentPlace ?: throw NoIdentifierException("No server or world is loaded!")

    private fun checked(place: String): String = place.ifBlank { throw NoIdentifierException("Identifier cannot be blank!") }
}
