package dev.deftu.favorita.api

import dev.deftu.favorita.client.FavoritaConfig
import dev.deftu.omnicore.annotations.GameSide
import dev.deftu.omnicore.annotations.Side

object FavoritedSlots {

    /**
     * Checks whether the given slot index is favorited for the given identifier.
     *
     * @param identifier The name of the world or address of the server.
     * @param slotIndex The slot index to check.
     */
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun isFavorited(
        identifier: String,
        slotIndex: Int
    ): Boolean = FavoritaConfig.isFavorited(identifier, slotIndex)

    /**
     * Checks whether the given slot index is favorited for the currently loaded world or server.
     *
     * @param slotIndex The slot index to check.
     *
     * @throws IllegalStateException If no server or world is loaded.
     */
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun isFavorited(
        slotIndex: Int
    ): Boolean = FavoritaConfig.isFavorited(slotIndex)

    /**
     * Sets whether the given slot index is favorited for the given identifier.
     *
     * @param identifier The name of the world or address of the server.
     * @param slotIndex The slot index to set.
     * @param isFavorited Whether the slot should be favorited.
     */
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun setFavorited(
        identifier: String,
        slotIndex: Int,
        isFavorited: Boolean
    ) = FavoritaConfig.setFavorited(identifier, slotIndex, isFavorited)

    /**
     * Sets whether the given slot index is favorited for the currently loaded world or server.
     *
     * @param slotIndex The slot index to set.
     * @param isFavorited Whether the slot should be favorited.
     *
     * @throws IllegalStateException If no server or world is loaded.
     */
    @JvmStatic
    @GameSide(Side.CLIENT)
    fun setFavorited(
        slotIndex: Int,
        isFavorited: Boolean
    ) = FavoritaConfig.setFavorited(slotIndex, isFavorited)


}
