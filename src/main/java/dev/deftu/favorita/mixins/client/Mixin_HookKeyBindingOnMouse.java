package dev.deftu.favorita.mixins.client;

//#if MC >= 1.16.5
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
//#else
//$$ import net.minecraft.client.Minecraft;
//$$ import org.lwjgl.input.Mouse;
//#endif

import dev.deftu.favorita.client.FavoritaClient;
import dev.deftu.favorita.client.FavoritaConfig;
import dev.deftu.favorita.client.utils.SoundUtils;
import dev.deftu.omnicore.client.OmniClient;
import dev.deftu.omnicore.client.OmniScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 1.16.5
@Mixin(Mouse.class)
//#else
//$$ @Mixin(Minecraft.class)
//#endif
public class Mixin_HookKeyBindingOnMouse {

    @Inject(
            //#if MC >= 1.16.5
            method = "onMouseButton",
            at = @At("HEAD")
            //#elseif MC >= 1.12.2
            //$$ method = "runTickMouse",
            //$$ at = @At(
            //$$         value = "INVOKE",
            //$$         target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;fireMouseInput()V",
            //$$         remap = false
            //$$ )
            //#else
            //$$ method = "runTick",
            //$$ at = @At(
            //$$         value = "INVOKE",
            //$$         target = "Lnet/minecraftforge/fml/common/FMLCommonHandler;fireMouseInput()V",
            //$$         remap = false
            //$$ )
            //#endif
    )
    private void favorita$onMouseButton(
            //#if MC >= 1.16.5
            long handle,
            int button,
            int action,
            int mods,
            //#endif
            CallbackInfo ci
    ) {
        //#if MC <= 1.12.2
        //$$ int button = Mouse.getEventButton();
        //#endif
        boolean isRelease =
                //#if MC >= 1.16.5
                action == GLFW.GLFW_RELEASE;
                //#else
                //$$ Mouse.getEventButtonState();
                //#endif
        boolean isKeyBindingPressed = FavoritaClient.getFavoriteKeyBinding().matchesMouse(button);
        if (
                isRelease ||
                OmniScreen.isInScreen() ||
                !isKeyBindingPressed
        ) {
            return;
        }

        ClientPlayerEntity player = OmniClient.getPlayer();
        if (player == null) {
            return;
        }

        int slotIndex = player.
                        //#if MC >= 1.17.1
                        getInventory()
                        //#else
                        //$$ inventory
                        //#endif
                        .selectedSlot;
        boolean wasFavorited = FavoritaConfig.isFavorited(slotIndex);
        FavoritaConfig.setFavorited(slotIndex, !wasFavorited);
        FavoritaConfig.save();
        SoundUtils.playDingSound();
        FavoritaClient.getRenderedHotbarIndices().add(slotIndex);
        FavoritaClient.getRenderHotbarState().put(slotIndex, wasFavorited ? FavoritaClient.HotbarState.UNLOCKED : FavoritaClient.HotbarState.LOCKED);
    }

}
