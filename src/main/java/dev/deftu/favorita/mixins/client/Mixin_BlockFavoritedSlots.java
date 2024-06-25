package dev.deftu.favorita.mixins.client;

//#if MC >= 1.16.5
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//#else
//$$ import net.minecraft.item.ItemStack;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

//#if MC >= 1.12.2
import net.minecraft.screen.slot.SlotActionType;
//#endif

import dev.deftu.favorita.client.FavoritaClient;
import dev.deftu.favorita.client.FavoritaConfig;
import dev.deftu.favorita.client.utils.InventoryUtils;
import dev.deftu.favorita.client.utils.MiscUtils;
import dev.deftu.favorita.client.utils.SoundUtils;
import dev.deftu.omnicore.client.OmniScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(ClientPlayerInteractionManager.class)
public class Mixin_BlockFavoritedSlots {

    @Inject(
            //#if MC >= 1.16.5
            method = "clickSlot",
            //#else
            //$$ method = "windowClick",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    private void favorita$cancelSlotClicks(
            int screenId,
            int slotId,
            int otherSlotId,
            //#if MC >= 1.12.2
            SlotActionType type,
            //#else
            //$$ int mode,
            //#endif
            PlayerEntity player,
            //#if MC >= 1.16.5
            CallbackInfo ci
            //#else
            //$$ CallbackInfoReturnable<ItemStack> cir
            //#endif
    ) {
        if (0 > slotId) {
            return;
        }

        ScreenHandler handler = player.currentScreenHandler;
        if (handler == null || screenId != handler.syncId) {
            return;
        }

        Screen currentScreen = OmniScreen.getCurrentScreen();
        if (!(
                currentScreen instanceof
                        //#if MC >= 1.16.5
                        HandledScreen<?>
                        //#else
                        //$$ GuiContainer
                        //#endif
        )) {
            return;
        }

        //#if MC >= 1.16.5
        HandledScreen<?> screen = (HandledScreen<?>) currentScreen;
        //#else
        //$$ GuiContainer screen = (GuiContainer) currentScreen;
        //#endif
        Slot slot = handler.getSlot(slotId);
        if (slot == null) {
            return;
        }

        int slotIndex = InventoryUtils.getSavedSlotIndex(screen, slot);
        if (FavoritaClient.isDebug()) {
            FavoritaClient.getLogger().error("[SLOT CLICK BLOCK] Internal index: {}, game index: {}, inventory: {}", slotIndex, InventoryUtils.getInternalSlotIndex(slot), slot.inventory);
        }

        if (FavoritaConfig.isFavorited(slotIndex)) {
            //#if MC >= 1.16.5
            ci.cancel();
            //#elseif MC >= 1.12.2
            //$$ cir.setReturnValue(ItemStack.EMPTY);
            //#else
            //$$ cir.setReturnValue(null);
            //#endif
            SoundUtils.playBlockedSound();
            MiscUtils.notifyBlocked();
        }
    }

}
