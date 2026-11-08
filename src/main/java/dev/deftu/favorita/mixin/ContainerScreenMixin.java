package dev.deftu.favorita.mixin;

import dev.deftu.favorita.render.LockOverlay;
import dev.deftu.favorita.slots.FavoriteKey;
import dev.deftu.favorita.slots.SlotLocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

//? if >=1.21 {
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?} else {
/*import net.minecraft.client.gui.screen.game.inventory.InventoryMenuScreen;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.slot.InventorySlot;
import org.polyfrost.oneconfig.internal.legacy.KeyCodes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}

//? if >=26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} elif >=1.21 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}

//? if >=1.21.10 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}

//? if >=1.21 {
@Mixin(AbstractContainerScreen.class)
//?} else {
/*@Mixin(InventoryMenuScreen.class)
*///?}
public abstract class ContainerScreenMixin {
    //? if >=1.21 {
    @Shadow protected Slot hoveredSlot;
    //?} else {
    /*@Shadow private InventorySlot hoveredSlot;
    *///?}

    //? if >=26.1 {
    @Inject(method = "extractSlot", at = @At("TAIL"))
    private void favorita$drawLock(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (SlotLocks.isLocked(slot)) LockOverlay.draw(graphics, slot.x, slot.y);
    }
    //?} elif >=1.21.11 {
    /*@Inject(method = "renderSlot", at = @At("TAIL"))
    private void favorita$drawLock(GuiGraphics graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (SlotLocks.isLocked(slot)) LockOverlay.draw(graphics, slot.x, slot.y);
    }
    *///?} elif >=1.21 {
    /*@Inject(method = "renderSlot", at = @At("TAIL"))
    private void favorita$drawLock(GuiGraphics graphics, Slot slot, CallbackInfo ci) {
        if (SlotLocks.isLocked(slot)) LockOverlay.draw(graphics, slot.x, slot.y);
    }
    *///?} else {
    /*@Inject(method = "renderSlot", at = @At("TAIL"))
    private void favorita$drawLock(InventorySlot slot, CallbackInfo ci) {
        if (SlotLocks.isLocked(slot)) LockOverlay.draw(null, slot.x, slot.y);
    }
    *///?}

    // OneConfig stores 1.8.9 binds in modern key codes, so LWJGL 2's codes are translated before comparing
    //? if >=1.21.10 {
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void favorita$toggleOnKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (FavoriteKey.matchesKey(event.key()) && favorita$toggleHovered()) cir.setReturnValue(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void favorita$toggleOnClick(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (FavoriteKey.matchesMouse(event.button()) && favorita$toggleHovered()) cir.setReturnValue(true);
    }
    //?} elif >=1.21 {
    /*@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void favorita$toggleOnKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (FavoriteKey.matchesKey(keyCode) && favorita$toggleHovered()) cir.setReturnValue(true);
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void favorita$toggleOnClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (FavoriteKey.matchesMouse(button) && favorita$toggleHovered()) cir.setReturnValue(true);
    }
    *///?} else {
    /*@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void favorita$toggleOnKey(char character, int keyCode, CallbackInfo ci) {
        if (FavoriteKey.matchesKey(KeyCodes.fromLegacy(keyCode).getValue()) && favorita$toggleHovered()) ci.cancel();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void favorita$toggleOnClick(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (FavoriteKey.matchesMouse(KeyCodes.mouseFromLegacy(button)) && favorita$toggleHovered()) ci.cancel();
    }
    *///?}

    // Dragging a stack across the inventory skips locked slots. Checked here since menus such as the creative one override the check
    //? if >=1.21 {
    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;canDragTo(Lnet/minecraft/world/inventory/Slot;)Z"))
    private boolean favorita$skipLockedSlots(AbstractContainerMenu menu, Slot slot, Operation<Boolean> original) {
        return !SlotLocks.isLocked(slot) && original.call(menu, slot);
    }
    //?} else {
    /*@WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/menu/InventoryMenu;canClickDragInto(Lnet/minecraft/inventory/slot/InventorySlot;)Z"))
    private boolean favorita$skipLockedSlots(InventoryMenu menu, InventorySlot slot, Operation<Boolean> original) {
        return !SlotLocks.isLocked(slot) && original.call(menu, slot);
    }
    *///?}

    @Unique
    private boolean favorita$toggleHovered() {
        return SlotLocks.toggle(this.hoveredSlot);
    }
}
