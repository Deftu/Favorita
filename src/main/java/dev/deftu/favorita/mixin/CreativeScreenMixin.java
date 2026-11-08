package dev.deftu.favorita.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.deftu.favorita.slots.SlotLocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21 {
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
//?} else {
/*import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.client.gui.screen.game.inventory.CreativeInventoryScreen;
import net.minecraft.inventory.slot.InventorySlot;
import net.minecraft.item.ItemStack;
*///?}

//? if >=26.1 {
import net.minecraft.world.inventory.ContainerInput;
//?} elif >=1.21 {
/*import net.minecraft.world.inventory.ClickType;
*///?}

/** The creative screen edits the inventory itself instead of asking the game mode, so its clicks are checked here */
//? if >=1.21 {
@Mixin(CreativeModeInventoryScreen.class)
//?} else {
/*@Mixin(CreativeInventoryScreen.class)
*///?}
public class CreativeScreenMixin {
    //? if >=26.1 {
    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedSlots(Slot slot, int slotId, int button, ContainerInput input, CallbackInfo ci) {
        if (SlotLocks.blocksCreativeClick(slot, button, input.ordinal())) ci.cancel();
    }
    //?} elif >=1.21 {
    /*@Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedSlots(Slot slot, int slotId, int button, ClickType type, CallbackInfo ci) {
        if (SlotLocks.blocksCreativeClick(slot, button, type.ordinal())) ci.cancel();
    }
    *///?} else {
    /*@Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedSlots(InventorySlot slot, int slotId, int button, int action, CallbackInfo ci) {
        if (SlotLocks.blocksCreativeClick(slot, button, action)) ci.cancel();
    }
    *///?}

    // Shift-clicking the bin writes an empty stack into every slot, so locked ones keep their items
    //? if >=1.21 {
    @WrapWithCondition(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"))
    private boolean favorita$keepLockedSlots(MultiPlayerGameMode gameMode, ItemStack stack, int slotId) {
        return !SlotLocks.keepsOnCreativeClear(slotId);
    }
    //?} else {
    /*@WrapWithCondition(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ClientPlayerInteractionManager;addItemToCreativeMenu(Lnet/minecraft/item/ItemStack;I)V"))
    private boolean favorita$keepLockedSlots(ClientPlayerInteractionManager interactionManager, ItemStack stack, int slotId) {
        return !SlotLocks.keepsOnCreativeClear(slotId);
    }
    *///?}

    //? if >=1.21.4 {
    // From 1.21.4 the bin also empties each slot on the client before telling the server
    @WrapWithCondition(method = "slotClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;set(Lnet/minecraft/world/item/ItemStack;)V"))
    private boolean favorita$keepLockedSlotsLocally(Slot slot, ItemStack stack) {
        return !SlotLocks.isLocked(slot);
    }
    //?}

    //? if >=1.21 {
    // Loading a saved creative hotbar leaves locked hotbar slots as they are
    @WrapWithCondition(method = "handleHotbarLoadOrSave", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private static boolean favorita$keepLockedHotbarSlots(Inventory inventory, int index, ItemStack stack) {
        return !SlotLocks.isLocked(index);
    }

    @WrapWithCondition(method = "handleHotbarLoadOrSave", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V"))
    private static boolean favorita$keepLockedHotbarSlotsOnServer(MultiPlayerGameMode gameMode, ItemStack stack, int slotId) {
        return !SlotLocks.keepsOnCreativeClear(slotId);
    }
    //?}
}
