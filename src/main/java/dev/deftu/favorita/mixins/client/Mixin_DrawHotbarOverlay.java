package dev.deftu.favorita.mixins.client;

//#if MC >= 1.20.1
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
//#elseif MC >= 1.16.5
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

import dev.deftu.favorita.client.FavoritaClient;
import dev.deftu.favorita.client.FavoritaRenderer;
import dev.deftu.omnicore.client.render.OmniMatrixStack;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(InGameHud.class)
public class Mixin_DrawHotbarOverlay {

    @Unique private final Map<Integer, Integer> lastRenderedHotbarTimes = new HashMap<>();

    @Inject(
            //#if MC >= 1.16.5
            method = "renderHotbarItem",
            //#else
            //$$ method = "renderHotbarItem",
            //#endif
            at = @At(
                    value = "INVOKE",
                    //#if MC >= 1.20.1
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"
                    //#elseif MC >= 1.16.5
                    //#if FABRIC
                    //$$ target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V"
                    //#else
                    //#if MC >= 1.19.4
                    //$$ target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"
                    //#else
                    //$$ target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"
                    //#endif
                    //#endif
                    //#elseif MC >= 1.12.2
                    //$$ target = "Lnet/minecraft/client/renderer/RenderItem;renderItemOverlays(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;II)V"
                    //#else
                    //$$ target = "Lnet/minecraft/client/renderer/entity/RenderItem;renderItemOverlays(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;II)V"
                    //#endif
            )
    )
    private void favorita$renderInHotbar(
            //#if MC >= 1.20.1
            DrawContext ctx,
            //#elseif MC >= 1.16.5
            //$$ MatrixStack ctx,
            //#elseif MC <= 1.8.9
            //$$ int index,
            //#endif
            int x,
            int y,
            //#if MC >= 1.20.1
            RenderTickCounter tickDelta,
            //#else
            //$$ float tickDelta,
            //#endif
            PlayerEntity player,
            //#if MC >= 1.12.2
            ItemStack itemStack,
            //#endif
            //#if MC >= 1.16.5
            int oneBasedIndex,
            //#endif
            CallbackInfo ci
    ) {
        // Render the slot overlay for 3 seconds when the player tries to drop a favorited item

        int slotIndex =
                //#if MC >= 1.16.5
                oneBasedIndex - 1;
                //#elseif MC >= 1.12.2
                //$$ player.inventory.mainInventory.indexOf(itemStack);
                //#else
                //$$ index;
                //#endif
        if (slotIndex > 8) {
            return;
        }

        if (FavoritaClient.getRenderedHotbarIndices().contains(slotIndex)) {
            OmniMatrixStack stack = new OmniMatrixStack(
                    //#if MC >= 1.20.1
                    ctx.getMatrices()
                    //#elseif MC >= 1.16.5
                    //$$ ctx
                    //#endif
            );

            FavoritaClient.HotbarState state = FavoritaClient.getRenderHotbarState().get(slotIndex);
            if (state == null) {
                state = FavoritaClient.HotbarState.LOCKED;
            }

            boolean isLocked = state == FavoritaClient.HotbarState.LOCKED;
            FavoritaRenderer.drawSlotOverlay(
                    stack,
                    x,
                    y,
                    16,
                    16,
                    isLocked,
                    isLocked ? FavoritaRenderer.RED_BACKGROUND : FavoritaRenderer.ORANGE_BACKGROUND
            );

            int currentTime = (int) (System.currentTimeMillis() / 1000);
            Integer lastRenderedTime = lastRenderedHotbarTimes.get(slotIndex);
            if (lastRenderedTime == null) {
                lastRenderedHotbarTimes.put(slotIndex, currentTime);
                lastRenderedTime = currentTime;
            }

            int timeDiff = (currentTime - lastRenderedTime) + 1;
            if (timeDiff > 3) {
                FavoritaClient.getRenderedHotbarIndices().remove(slotIndex);
                lastRenderedHotbarTimes.remove(slotIndex);
            }
        }
    }

}
