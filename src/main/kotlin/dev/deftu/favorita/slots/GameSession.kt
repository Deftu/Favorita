package dev.deftu.favorita.slots

import dev.deftu.favorita.mixin.CreativeSlotAccessor
import dev.deftu.favorita.mixin.SlotAccessor
import net.minecraft.client.Minecraft

//? if >=1.21 {
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.LevelResource
//?} else {
/*import net.minecraft.client.sound.instance.SimpleSoundInstance
import net.minecraft.entity.living.player.PlayerInventory
import net.minecraft.inventory.slot.InventorySlot
import net.minecraft.item.ItemStack
import net.minecraft.resource.Identifier
*///?}

//? if >=1.21 {
typealias MenuSlot = Slot
//?} else {
/*typealias MenuSlot = InventorySlot
*///?}

/** The version-specific view of the client: who is playing where, and which inventory slot a menu slot points at */
object GameSession {
    const val OFFHAND_SLOT = 40

    private val client: Minecraft
        get() = Minecraft.getInstance()

    data class Place(val key: String, val name: String, val isServer: Boolean)

    /** The world or server the player is in, or null on the title screen */
    val currentPlace: Place?
        get() {
            //? if >=1.21 {
            client.player ?: return null
            val server = client.currentServer
            if (server != null && !client.isLocalServer) return Place(server.ip.lowercase(), server.name, true)
            val integrated = client.singleplayerServer ?: return null
            val folder = integrated.getWorldPath(LevelResource.ROOT).normalize().fileName.toString()
            return Place(WORLD_PREFIX + folder, integrated.worldData.levelName, false)
            //?} else {
            /*client.player ?: return null
            val server = client.currentServerEntry
            if (server != null && !client.isIntegratedServerRunning) return Place(server.ip.lowercase(), server.name, true)
            val integrated = client.server ?: return null
            return Place(WORLD_PREFIX + integrated.worldSaveName, integrated.worldName, false)
            *///?}
        }

    val selectedHotbarSlot: Int
        get() {
            val player = client.player ?: return -1
            //? if >=1.21.5 {
            return player.inventory.selectedSlot
            //?} elif >=1.21 {
            /*return player.inventory.selected
            *///?} else {
            /*return player.inventory.selectedSlot
            *///?}
        }

    /** The player inventory index a menu slot shows, or -1 when it belongs to another container */
    @JvmStatic
    fun inventoryIndex(slot: MenuSlot): Int {
        val target = (slot as? CreativeSlotAccessor)?.`favorita$getTarget`() ?: slot
        //? if >=1.21 {
        val isPlayerInventory = target.container is Inventory
        //?} else {
        /*val isPlayerInventory = target.inventory is PlayerInventory
        *///?}
        return if (isPlayerInventory) (target as SlotAccessor).`favorita$getSlotIndex`() else -1
    }

    /** The player inventory index behind a slot id of the open menu */
    fun inventoryIndexInOpenMenu(slotId: Int): Int {
        val player = client.player ?: return -1
        //? if >=1.21 {
        val slots = player.containerMenu.slots
        //?} else {
        /*val slots = player.menu.slots
        *///?}
        return if (slotId in slots.indices) inventoryIndex(slots[slotId]) else -1
    }

    /** The player inventory index behind a slot id of the player's own inventory menu */
    fun inventoryIndexInPlayerMenu(slotId: Int): Int {
        val player = client.player ?: return -1
        //? if >=1.21 {
        val slots = player.inventoryMenu.slots
        //?} else {
        /*val slots = player.playerMenu.slots
        *///?}
        return if (slotId in slots.indices) inventoryIndex(slots[slotId]) else -1
    }

    /** The menu slot id a creative inventory tab wrapper stands for in the player's own menu, or null for any other slot */
    fun creativeTargetId(slot: MenuSlot): Int? = (slot as? CreativeSlotAccessor)?.`favorita$getTarget`()?.index

    /** Whether a locked inventory slot holds what double-clicking would gather onto the cursor */
    fun gatherWouldTakeFrom(isLocked: (Int) -> Boolean): Boolean {
        val player = client.player ?: return false
        val inventory = player.inventory
        //? if >=1.21 {
        val carried = player.containerMenu.carried
        if (carried.isEmpty) return false
        return (0 until inventory.containerSize).any { index ->
            isLocked(index) && ItemStack.isSameItemSameComponents(inventory.getItem(index), carried)
        }
        //?} else {
        /*val carried: ItemStack = inventory.cursorItem ?: return false
        return (0 until inventory.size).any { index ->
            val stack: ItemStack? = inventory.getItem(index)
            isLocked(index) && stack != null && stack.matchesItem(carried) && ItemStack.matchesNbt(stack, carried)
        }
        *///?}
    }

    fun playLockSound(locked: Boolean) {
        //? if >=1.21 {
        client.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, if (locked) 1.2f else 0.8f))
        //?} else {
        /*client.soundManager.play(SimpleSoundInstance.of(Identifier("random.orb"), if (locked) 1.2f else 0.8f))
        *///?}
    }

    fun playBlockedSound() {
        //? if >=1.21 {
        client.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS, 1f))
        //?} else {
        /*client.soundManager.play(SimpleSoundInstance.of(Identifier("note.bass"), 1f))
        *///?}
    }

    private const val WORLD_PREFIX = "world:"

    fun isWorldKey(key: String) = key.startsWith(WORLD_PREFIX)
}
