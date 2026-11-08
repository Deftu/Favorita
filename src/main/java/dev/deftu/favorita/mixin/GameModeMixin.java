package dev.deftu.favorita.mixin;

import dev.deftu.favorita.slots.SlotLocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if >=1.21 {
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
/*import net.minecraft.client.ClientPlayerInteractionManager;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

//? if >=26.1 {
import net.minecraft.world.inventory.ContainerInput;
//?} elif >=1.21 {
/*import net.minecraft.world.inventory.ClickType;
*///?}

//? if >=1.21 {
@Mixin(MultiPlayerGameMode.class)
//?} else {
/*@Mixin(ClientPlayerInteractionManager.class)
*///?}
public class GameModeMixin {
    //? if >=26.1 {
    @Inject(method = "handleContainerInput", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedSlots(int containerId, int slotId, int button, ContainerInput input, Player player, CallbackInfo ci) {
        if (SlotLocks.blocksMenuClick(slotId, button, input.ordinal())) ci.cancel();
    }
    //?} elif >=1.21 {
    /*@Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedSlots(int containerId, int slotId, int button, ClickType type, Player player, CallbackInfo ci) {
        if (SlotLocks.blocksMenuClick(slotId, button, type.ordinal())) ci.cancel();
    }
    *///?} else {
    /*@Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedSlots(int networkId, int slotId, int button, int action, PlayerEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (SlotLocks.blocksMenuClick(slotId, button, action)) cir.setReturnValue(null);
    }
    *///?}
}
