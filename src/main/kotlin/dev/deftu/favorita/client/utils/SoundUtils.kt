package dev.deftu.favorita.client.utils

//#if MC <= 1.16.5
//$$ import dev.deftu.favorita.mixins.Mixin_SoundPitch
//#endif

//#if MC >= 1.12.2
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
//#endif

import dev.deftu.omnicore.client.OmniClient
import net.minecraft.client.network.ClientPlayerEntity

object SoundUtils {

    private val ClientPlayerEntity.internalSoundPitch: Float
        get() {
            //#if MC >= 1.17.1
            return this.soundPitch
            //#else
            //$$ return (this as Mixin_SoundPitch).invokeGetSoundPitch()
            //#endif
        }

    /**
     * Plays a noise indicating that the attempted action has been blocked.
     */
    @JvmStatic
    fun playBlockedSound() {
        val event =
            //#if MC >= 1.12.2
            SoundEvents.BLOCK_NOTE_BLOCK_BASS
            //#if MC >= 1.19.4
            //#if FABRIC
            .comp_349()
            //#else
            //$$ .value()
            //#endif
            //#endif
            //#else
            //$$ "note.bass"
            //#endif
        play(event)
    }

    /**
     * Plays the small ding sound made when the player picks up an experience orb.
     */
    @JvmStatic
    fun playDingSound() {
        val event =
            //#if MC >= 1.12.2
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
            //#else
            //$$ "random.orb"
            //#endif
        play(event)
    }

    private fun play(
        //#if MC >= 1.12.2
        event: SoundEvent
        //#else
        //$$ event: String
        //#endif
    ) {
        val player = OmniClient.getPlayer() ?: return
        //#if MC >= 1.20.6
        player.playSound(event)
        //#elseif MC >= 1.19.2
        //$$ player.playSoundIfNotSilent(event)
        //#else
        //$$ player.playSound(event, 1f, player.internalSoundPitch)
        //#endif
    }

}
