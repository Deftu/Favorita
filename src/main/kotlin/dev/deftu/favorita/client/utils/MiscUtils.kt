package dev.deftu.favorita.client.utils

import dev.deftu.omnicore.client.OmniChat
import dev.deftu.textile.minecraft.MinecraftTextFormat
import dev.deftu.textualizer.text.TextualizerTextHolder

object MiscUtils {

    @JvmStatic
    fun notifyBlocked() {
        val text = TextualizerTextHolder("message.favorita.blocked").formatted(MinecraftTextFormat.RED)
        OmniChat.showChatMessage(text)
    }

}
