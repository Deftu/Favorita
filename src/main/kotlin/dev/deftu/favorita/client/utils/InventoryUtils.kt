package dev.deftu.favorita.client.utils

//#if MC <= 1.19.2
//$$ import net.minecraft.item.ItemGroup
//#endif

//#if MC == 1.16.5
//$$ import dev.deftu.favorita.mixins.Mixin_SlotIndex
//#endif

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.slot.Slot

typealias ContainerScreen =
    //#if MC >= 1.16.5
    HandledScreen<*>
    //#else
    //$$ GuiContainer
    //#endif

object InventoryUtils {

    private val CreativeInventoryScreen.isInInventory: Boolean
        get() {
            //#if MC >= 1.19.4
            return this.isInventoryTabSelected
            //#else
            //$$ return this.selectedTab == ItemGroup.INVENTORY.index
            //#endif
        }

    @JvmStatic
    val Slot.internalSlotIndex: Int
        get() {
            //#if MC >= 1.17.1
            return this.index
            //#elseif MC == 1.16.5
            //$$ return (this as Mixin_SlotIndex).index
            //#else
            //$$ return this.slotIndex
            //#endif
        }

    /**
     * @return The config-serialized index of the given slot, relative to the inventory contents of the given screen.
     */
    @JvmStatic
    fun getSavedSlotIndex(
        screen: ContainerScreen,
        slot: Slot
    ): Int {
        if (slot.inventory !is PlayerInventory) {
            return -1
        }

        val slotIndex = slot.internalSlotIndex
        return when {
            // Ensure that,
            // inventory itself starts at index 9 (9-35 in vanilla)
            // the armour slots start at 36 (5-8 in vanilla)
            // hotbar is within it's own range (36-44 in vanilla)
            screen is CreativeInventoryScreen && screen.isInInventory -> {
                when (slotIndex) {
                    // Armour
                    in 5..8 -> {
                        slotIndex + 31
                    }

                    // Hotbar
                    in 36..44 -> {
                        slotIndex - 36
                    }

                    else -> {
                        slotIndex
                    }
                }
            }

            else -> {
                slotIndex
            }
        }
    }

}
