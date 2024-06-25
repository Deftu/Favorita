package dev.deftu.favorita.mixins.client;

//#if MC >= 1.12.2
import net.minecraft.screen.slot.SlotActionType;
//#endif

import dev.deftu.favorita.client.FavoritaClient;
import dev.deftu.favorita.client.FavoritaConfig;
import dev.deftu.favorita.client.utils.InventoryUtils;
import dev.deftu.favorita.client.utils.MiscUtils;
import dev.deftu.favorita.client.utils.SoundUtils;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.class)
public class Mixin_BlockFavoritedCreativeSlots {

    @Inject(
            //#if MC >= 1.16.5
            method = "onMouseClick",
            //#else
            //$$ method = "handleMouseClick",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    private void favorita$blockMouseClicks(
            Slot slot,
            int slotId,
            int otherSlotId,
            //#if MC >= 1.12.2
            SlotActionType type,
            //#else
            //$$ int mode,
            //#endif
            CallbackInfo ci
    ) {
        if (slot == null) {
            return;
        }

        //#if MC >= 1.16.5
        HandledScreen<?> $this = (HandledScreen<?>) (Object) this;
        //#else
        //$$ GuiContainer $this = (GuiContainer) (Object) this;
        //#endif
        int slotIndex = InventoryUtils.getSavedSlotIndex($this, slot);
        if (FavoritaClient.isDebug()) {
            FavoritaClient.getLogger().error("[SLOT CREATIVE BLOCK] Internal index: {}, game index: {}, inventory: {}", slotIndex, InventoryUtils.getInternalSlotIndex(slot), slot.inventory);
        }

        if (FavoritaConfig.isFavorited(slotIndex)) {
            ci.cancel();
            SoundUtils.playBlockedSound();
            MiscUtils.notifyBlocked();
        }
    }

}
