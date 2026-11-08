package dev.deftu.favorita.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if >=1.21 {
import net.minecraft.world.inventory.Slot;
//?} else {
/*import net.minecraft.inventory.slot.InventorySlot;
*///?}

/** The creative inventory tab shows wrappers whose own index is the menu's, so locks look through them to the real slot */
//? if >=1.21 {
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$SlotWrapper")
//?} else {
/*@Mixin(targets = "net.minecraft.client.gui.screen.game.inventory.CreativeInventoryScreen$CreativeInventorySlot")
*///?}
public interface CreativeSlotAccessor {
    //? if >=1.21 {
    @Accessor("target")
    Slot favorita$getTarget();
    //?} else {
    /*@Accessor("slot")
    InventorySlot favorita$getTarget();
    *///?}
}
