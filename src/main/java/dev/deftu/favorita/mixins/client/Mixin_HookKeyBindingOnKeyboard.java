package dev.deftu.favorita.mixins.client;

//#if MC >= 1.16.5
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
//#else
//$$ import net.minecraft.client.Minecraft;
//$$ import org.lwjgl.input.Keyboard;
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
@Mixin(Keyboard.class)
//#else
//$$ @Mixin(Minecraft.class)
//#endif
public class Mixin_HookKeyBindingOnKeyboard {

    @Inject(
            //#if MC >= 1.16.5
            method = "onKey",
            //#else
            //$$ method = "dispatchKeypresses",
            //#endif
            at = @At("HEAD")
    )
    private void favorita$onKey(
            //#if MC >= 1.16.5
            long handle,
            int key,
            int scancode,
            int action,
            int mods,
            //#endif
            CallbackInfo ci
    ) {
        boolean isRelease =
                //#if MC >= 1.16.5
                action == GLFW.GLFW_RELEASE;
                //#else
                //$$ Keyboard.getEventKeyState();
                //#endif
        boolean isKeyBindingPressed =
                //#if MC >= 1.16.5
                FavoritaClient.getFavoriteKeyBinding().matchesKey(key, scancode);
                //#else
                //$$ Keyboard.getEventKey() == FavoritaClient.getFavoriteKeyBinding().getKeyCode();
                //#endif
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
