package dev.deftu.favorita.slots

import net.minecraft.client.Minecraft

//? if >=1.21 {
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
//?} else {
/*import net.minecraft.block.Blocks
import net.minecraft.inventory.menu.InventoryMenu
import net.minecraft.inventory.slot.InventorySlot
import net.minecraft.item.ItemStack
*///?}

//? if >=26.1 {
import net.minecraft.world.inventory.ContainerInput
//?} elif >=1.21 {
/*import net.minecraft.world.inventory.ClickType
*///?}

/**
 * Shift-clicks that skip locked slots.
 *
 * A shift-click picks its own destination, and the server makes the same choice without knowing about locks. So the click
 * is first tried on the client's menu and put back. If it would touch a locked slot, it is tried again with every locked
 * slot holding a full stack of barriers, and that outcome is replayed as plain pickup and place clicks the server accepts.
 */
object QuickMoves {
    enum class Result { UNTOUCHED, MOVED, BLOCKED }

    private val client: Minecraft
        get() = Minecraft.getInstance()

    //? if >=1.21 {
    private class Outcome(val before: List<ItemStack>, val after: List<ItemStack>)
    //?} else {
    /*private class Outcome(val before: List<ItemStack?>, val after: List<ItemStack?>)
    *///?}

    /** @param inPlayerMenu true for the creative inventory tab, which edits the player's own menu and sets slots directly */
    fun moveAround(inPlayerMenu: Boolean, slotId: Int, button: Int, isLocked: (Int) -> Boolean): Result {
        val player = client.player ?: return Result.UNTOUCHED
        //? if >=1.21 {
        val menu: AbstractContainerMenu = if (inPlayerMenu) player.inventoryMenu else player.containerMenu
        //?} else {
        /*val menu: InventoryMenu = if (inPlayerMenu) player.playerMenu else player.menu
        *///?}
        if (slotId !in menu.slots.indices) return Result.UNTOUCHED
        val lockedIds = menu.slots.indices.filter { isLocked(GameSession.inventoryIndex(menu.slots[it])) }
        if (lockedIds.isEmpty()) return Result.UNTOUCHED

        val plain = simulate(menu, slotId, button, emptyList())
        if (lockedIds.all { sameStack(plain.before[it], plain.after[it]) }) return Result.UNTOUCHED

        // Result slots craft again on every shift-click, which plain clicks can't reproduce
        val source = menu.slots[slotId]
        if (count(source.item) == 0 || !mayPlace(source, source.item)) return Result.BLOCKED

        val around = simulate(menu, slotId, button, lockedIds)
        val targets = menu.slots.indices.filter { it != slotId && it !in lockedIds && count(around.after[it]) > count(around.before[it]) }
        if (targets.isEmpty()) return Result.BLOCKED

        if (inPlayerMenu) {
            for (id in targets + slotId) {
                setStack(menu.slots[id], around.after[id])
                sendCreativeSlot(around.after[id], id)
            }
            return Result.MOVED
        }

        // Filled targets go first, so the one left part-filled takes whatever is still on the cursor
        val ordered = targets.sortedBy { if (count(around.after[it]) >= maxCount(menu.slots[it], around.after[it])) 0 else 1 }
        click(menu, slotId)
        ordered.forEach { click(menu, it) }
        if (count(around.after[slotId]) > 0) click(menu, slotId)
        return Result.MOVED
    }

    //? if >=1.21 {
    private fun simulate(menu: AbstractContainerMenu, slotId: Int, button: Int, barriers: List<Int>): Outcome {
        val player = client.player!!
        val before = menu.slots.map { it.item.copy() }
        val carried = menu.carried.copy()
        try {
            barriers.forEach { menu.slots[it].set(ItemStack(Items.BARRIER, Items.BARRIER.defaultMaxStackSize)) }
            //? if >=26.1 {
            menu.clicked(slotId, button, ContainerInput.QUICK_MOVE, player)
            //?} else {
            /*menu.clicked(slotId, button, ClickType.QUICK_MOVE, player)
            *///?}
            return Outcome(before, menu.slots.map { it.item.copy() })
        } finally {
            menu.slots.forEachIndexed { index, slot -> slot.set(before[index]) }
            menu.carried = carried
        }
    }

    private fun click(menu: AbstractContainerMenu, slotId: Int) {
        //? if >=26.1 {
        client.gameMode?.handleContainerInput(menu.containerId, slotId, 0, ContainerInput.PICKUP, client.player!!)
        //?} else {
        /*client.gameMode?.handleInventoryMouseClick(menu.containerId, slotId, 0, ClickType.PICKUP, client.player!!)
        *///?}
    }

    private fun sendCreativeSlot(stack: ItemStack, slotId: Int) {
        client.gameMode?.handleCreativeModeItemAdd(stack, slotId)
    }

    private fun sameStack(a: ItemStack, b: ItemStack) = ItemStack.matches(a, b)
    private fun count(stack: ItemStack) = stack.count
    private fun maxCount(slot: Slot, stack: ItemStack) = slot.getMaxStackSize(stack)
    private fun mayPlace(slot: Slot, stack: ItemStack) = slot.mayPlace(stack)
    private fun setStack(slot: Slot, stack: ItemStack) = slot.set(stack)
    //?} else {
    /*private fun simulate(menu: InventoryMenu, slotId: Int, button: Int, barriers: List<Int>): Outcome {
        val player = client.player!!
        val before = menu.slots.map { it.item?.copy() }
        val carried = player.inventory.cursorItem?.copy()
        try {
            barriers.forEach { menu.slots[it].setItem(ItemStack(Blocks.BARRIER, 64)) }
            menu.onClickSlot(slotId, button, 1, player)
            return Outcome(before, menu.slots.map { it.item?.copy() })
        } finally {
            menu.slots.forEachIndexed { index, slot -> slot.setItem(before[index]) }
            player.inventory.cursorItem = carried
        }
    }

    private fun click(menu: InventoryMenu, slotId: Int) {
        client.interactionManager.clickSlot(menu.networkId, slotId, 0, 0, client.player)
    }

    private fun sendCreativeSlot(stack: ItemStack?, slotId: Int) {
        client.interactionManager.addItemToCreativeMenu(stack, slotId)
    }

    private fun sameStack(a: ItemStack?, b: ItemStack?) = ItemStack.matches(a, b)
    private fun count(stack: ItemStack?) = stack?.size ?: 0
    private fun maxCount(slot: InventorySlot, stack: ItemStack?) = minOf(slot.maxStackSize, stack?.maxSize ?: 64)
    private fun mayPlace(slot: InventorySlot, stack: ItemStack?) = slot.isItemAllowed(stack)
    private fun setStack(slot: InventorySlot, stack: ItemStack?) = slot.setItem(stack)
    *///?}
}
