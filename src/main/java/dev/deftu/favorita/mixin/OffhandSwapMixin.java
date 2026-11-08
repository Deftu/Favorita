package dev.deftu.favorita.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.21 {
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.deftu.favorita.slots.SlotLocks;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.spongepowered.asm.mixin.injection.At;
//?}

/** Stops the swap hands key from moving a locked held item or replacing a locked offhand. 1.8.9 has no offhand */
@Mixin(Minecraft.class)
public class OffhandSwapMixin {
    //? if >=1.21 {
    @WrapWithCondition(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"))
    private boolean favorita$blockLockedSwap(ClientPacketListener connection, Packet<?> packet) {
        return !(packet instanceof ServerboundPlayerActionPacket action
            && action.getAction() == ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND
            && SlotLocks.blocksOffhandSwap());
    }
    //?}
}
