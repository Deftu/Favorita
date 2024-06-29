package dev.deftu.favorita.mixins.client;

//#if MC == 1.19.2
//$$ import net.minecraft.network.encryption.PlayerPublicKey;
//#endif

//#if MC <= 1.12.2
//$$ import net.minecraft.entity.item.EntityItem;
//#endif

import com.mojang.authlib.GameProfile;
import dev.deftu.favorita.client.FavoritaClient;
import dev.deftu.favorita.client.FavoritaConfig;
import dev.deftu.favorita.client.utils.MiscUtils;
import dev.deftu.favorita.client.utils.SoundUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class Mixin_BlockFavoritedHotbarSlots extends AbstractClientPlayerEntity {

    public Mixin_BlockFavoritedHotbarSlots(
            ClientWorld clientWorld,
            GameProfile gameProfile
            //#if MC == 1.19.2
            //$$ , PlayerPublicKey playerPublicKey
            //#endif
    ) {
        super(
                clientWorld,
                gameProfile
                //#if MC == 1.19.2
                //$$ , playerPublicKey
                //#endif
        );
    }

    @Inject(
            //#if MC >= 1.16.5
            method = "dropSelectedItem",
            //#else
            //$$ method = "dropItem",
            //#endif
            at = @At("HEAD"),
            cancellable = true
    )
    private void mantle$onSelectedItemDropped(
            boolean dropEntireStack,
            //#if MC >= 1.16.5
            CallbackInfoReturnable<Boolean> cir
            //#else
            //$$ CallbackInfoReturnable<EntityItem> cir
            //#endif
    ) {
        int slotIndex =
                //#if MC >= 1.17.1
                getInventory()
                //#else
                //$$ inventory
                //#endif
                .selectedSlot;
        if (FavoritaConfig.isFavorited(slotIndex)) {
            cir.setReturnValue(
                    //#if MC >= 1.16.5
                    false
                    //#else
                    //$$ null
                    //#endif
            );
            SoundUtils.playBlockedSound();
            MiscUtils.notifyBlocked();
            FavoritaClient.getRenderedHotbarIndices().add(slotIndex);
        }
    }

}
