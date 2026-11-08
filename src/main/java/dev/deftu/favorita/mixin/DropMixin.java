package dev.deftu.favorita.mixin;

import dev.deftu.favorita.slots.SlotLocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

//? if >=26.3 {
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} elif >=1.21 {
/*import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?} else {
/*import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
*///?}

//? if >=26.3 {
@Mixin(MultiPlayerGameMode.class)
//?} elif >=1.21 {
/*@Mixin(LocalPlayer.class)
*///?} else {
/*@Mixin(LocalClientPlayerEntity.class)
*///?}
public class DropMixin {
    //? if >=26.3 {
    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedDrop(LocalPlayer player, boolean fullStack, CallbackInfo ci) {
        if (SlotLocks.blocksDrop()) ci.cancel();
    }
    //?} elif >=1.21 {
    /*@Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedDrop(boolean fullStack, CallbackInfoReturnable<Boolean> cir) {
        if (SlotLocks.blocksDrop()) cir.setReturnValue(false);
    }
    *///?} else {
    /*@Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void favorita$blockLockedDrop(boolean fullStack, CallbackInfoReturnable<ItemEntity> cir) {
        if (SlotLocks.blocksDrop()) cir.setReturnValue(null);
    }
    *///?}
}
