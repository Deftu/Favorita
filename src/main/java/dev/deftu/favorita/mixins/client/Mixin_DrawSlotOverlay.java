package dev.deftu.favorita.mixins.client;

//#if MC >= 1.20.1
import net.minecraft.client.gui.DrawContext;
//#elseif MC >= 1.16.5
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC >= 1.16.5
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
//#endif

//#if MC <= 1.12.2
//$$ import net.minecraft.inventory.Container;
//#endif

import dev.deftu.favorita.client.FavoritaConfig;
import dev.deftu.favorita.client.FavoritaRenderer;
import dev.deftu.favorita.client.utils.InventoryUtils;
import dev.deftu.omnicore.client.render.OmniMatrixStack;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class Mixin_DrawSlotOverlay
    //#if MC >= 1.16.5
        <T extends ScreenHandler>
    //#endif
{

    //#if MC >= 1.16.5
    @Shadow @Final protected T handler;
    //#else
    //$$ @Shadow public Container inventorySlots;
    //#endif

    @Inject(
            //#if MC >= 1.16.5
            method = "render",
            //#else
            //$$ method = "drawScreen",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 1.16.5
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    //#else
                    //$$ target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V",
                    //#endif
                    shift = At.Shift.BEFORE
            )
    )
    private void favorita$onPostSlotRender(
            //#if MC >= 1.20.1
            DrawContext ctx,
            //#elseif MC >= 1.16.5
            //$$ MatrixStack ctx,
            //#endif
            int mouseX,
            int mouseY,
            float tickDelta,
            CallbackInfo ci
    ) {
        //#if MC >= 1.16.5
        HandledScreen<?> $this = (HandledScreen<?>) (Object) this;
        //#else
        //$$ GuiContainer $this = (GuiContainer) (Object) this;
        //#endif
        OmniMatrixStack stack = new OmniMatrixStack(
                //#if MC >= 1.20.1
                ctx.getMatrices()
                //#elseif MC >= 1.16.5
                //$$ ctx
                //#endif
        );
        for (
                Slot slot :
                //#if MC >= 1.16.5
                this.handler.slots
                //#else
                //$$ this.inventorySlots.inventorySlots
                //#endif
        ) {
            int index = InventoryUtils.getSavedSlotIndex($this, slot);
            if (!FavoritaConfig.isFavorited(index)) {
                continue;
            }

            FavoritaRenderer.drawSlotOverlay(
                    stack,
                    slot.x,
                    slot.y,
                    16,
                    16
            );
        }
    }

}
