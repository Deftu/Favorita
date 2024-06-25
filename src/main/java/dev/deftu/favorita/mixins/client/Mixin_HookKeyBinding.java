package dev.deftu.favorita.mixins.client;

//#if MC >= 1.16.5
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#else
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#endif

import dev.deftu.favorita.client.FavoritaClient;
import dev.deftu.favorita.client.FavoritaConfig;
import dev.deftu.favorita.client.utils.InventoryUtils;
import dev.deftu.favorita.client.utils.SoundUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.function.BooleanSupplier;

@Mixin(value = HandledScreen.class, priority = 1002)
public class Mixin_HookKeyBinding {

    @Shadow @Nullable protected Slot focusedSlot;

    @Inject(
            method = "mouseClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void favorita$onMouseClicked(
            //#if MC >= 1.16.5
            double mouseX,
            double mouseY,
            //#else
            //$$ int mouseX,
            //$$ int mouseY,
            //#endif
            int button,
            //#if MC >= 1.16.5
            CallbackInfoReturnable<Boolean> cir
            //#else
            //$$ CallbackInfo ci
            //#endif
    ) {
        //#if MC >= 1.16.5
        HandledScreen<?> $this = (HandledScreen<?>) (Object) this;
        //#else
        //$$ GuiContainer $this = (GuiContainer) (Object) this;
        //#endif
        favorita$inverseFavoriteForFocusedSlot(
                $this,
                //#if MC >= 1.16.5
                cir,
                //#else
                //$$ ci,
                //#endif
                () -> {
                    //#if MC >= 1.16.5
                    return FavoritaClient.getFavoriteKeyBinding().matchesMouse(button);
                    //#else
                    //$$ return FavoritaClient.getFavoriteKeyBinding().getKeyCode() == button;
                    //#endif
                }
        );
    }

    @Inject(
            //#if MC >= 1.16.5
            method = "keyPressed",
            //#else
            //$$ method = "keyTyped",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    private void favorita$onKeyPressed(
            //#if MC >= 1.16.5
            int keyCode,
            int scanCode,
            int modifiers,
            CallbackInfoReturnable<Boolean> cir
            //#else
            //$$ char typedChar,
            //$$ int keyCode,
            //$$ CallbackInfo ci
            //#endif
    ) {
        //#if MC >= 1.16.5
        HandledScreen<?> $this = (HandledScreen<?>) (Object) this;
        //#else
        //$$ GuiContainer $this = (GuiContainer) (Object) this;
        //#endif
        favorita$inverseFavoriteForFocusedSlot(
                $this,
                //#if MC >= 1.16.5
                cir,
                //#else
                //$$ ci,
                //#endif
                () -> {
                    //#if MC >= 1.16.5
                    return FavoritaClient.getFavoriteKeyBinding().matchesKey(keyCode, scanCode);
                    //#else
                    //$$ return FavoritaClient.getFavoriteKeyBinding().getKeyCode() == keyCode;
                    //#endif
                }
        );
    }

    @Unique
    private void favorita$inverseFavoriteForFocusedSlot(
            //#if MC >= 1.16.5
            HandledScreen<?> $this,
            CallbackInfoReturnable<Boolean> cir,
            //#else
            //$$ GuiContainer $this,
            //$$ CallbackInfo ci,
            //#endif
            BooleanSupplier keyBindingCheck
    ) {
        if (this.focusedSlot == null || !keyBindingCheck.getAsBoolean()) {
            return;
        }

        //#if MC >= 1.16.5
        cir.setReturnValue(true);
        //#else
        //$$ ci.cancel();
        //#endif

        int slotIndex = InventoryUtils.getSavedSlotIndex($this, this.focusedSlot);
        if (slotIndex == -1) {
            return;
        }

        boolean wasFavorited = FavoritaConfig.isFavorited(slotIndex);
        FavoritaConfig.setFavorited(slotIndex, !wasFavorited);
        FavoritaConfig.save();

        SoundUtils.playDingSound();
    }

}
