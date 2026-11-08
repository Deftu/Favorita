package dev.deftu.favorita.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if >=1.21 {
import net.minecraft.world.inventory.Slot;
//?} else {
/*import net.minecraft.inventory.slot.InventorySlot;
*///?}

/** A slot's index in its own container, read from the field since subclasses may override the getter */
//? if >=1.21 {
@Mixin(Slot.class)
//?} else {
/*@Mixin(InventorySlot.class)
*///?}
public interface SlotAccessor {
    //? if >=1.21 {
    @Accessor("slot")
    int favorita$getSlotIndex();
    //?} else {
    /*@Accessor("id")
    int favorita$getSlotIndex();
    *///?}
}
