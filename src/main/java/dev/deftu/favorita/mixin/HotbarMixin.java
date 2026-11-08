package dev.deftu.favorita.mixin;

import dev.deftu.favorita.render.LockOverlay;
import dev.deftu.favorita.slots.GameSession;
import dev.deftu.favorita.slots.SlotLocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=1.21 {
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//?} else {
/*import net.minecraft.client.gui.GameGui;
import net.minecraft.entity.living.player.PlayerEntity;
*///?}

//? if >=26.2 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
//?} elif >=26.1 {
/*import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
*///?} elif >=1.21 {
/*import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
*///?}

//? if >=26.2 {
@Mixin(Hud.class)
//?} elif >=1.21 {
/*@Mixin(Gui.class)
*///?} else {
/*@Mixin(GameGui.class)
*///?}
public class HotbarMixin {
    // Hotbar slots are drawn with seeds 1 to 9 in order, and the offhand comes after them
    //? if >=26.1 {
    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void favorita$drawLock(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker delta, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        int index = seed <= 9 ? seed - 1 : GameSession.OFFHAND_SLOT;
        LockOverlay.draw(graphics, x, y, SlotLocks.hotbarLockAlpha(index));
    }
    //?} elif >=1.21 {
    /*@Inject(method = "renderSlot", at = @At("TAIL"))
    private void favorita$drawLock(GuiGraphics graphics, int x, int y, DeltaTracker delta, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        int index = seed <= 9 ? seed - 1 : GameSession.OFFHAND_SLOT;
        LockOverlay.draw(graphics, x, y, SlotLocks.hotbarLockAlpha(index));
    }
    *///?} else {
    /*@Inject(method = "renderItemSlot", at = @At("TAIL"))
    private void favorita$drawLock(int index, int x, int y, float tickDelta, PlayerEntity player, CallbackInfo ci) {
        LockOverlay.draw(null, x, y, SlotLocks.hotbarLockAlpha(index));
    }
    *///?}
}
