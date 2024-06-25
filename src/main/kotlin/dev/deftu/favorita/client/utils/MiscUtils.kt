package dev.deftu.favorita.client.utils

import dev.deftu.omnicore.client.OmniChat
import dev.deftu.textile.Format
import dev.deftu.textile.impl.TranslatableText

object MiscUtils {

    @JvmStatic
    fun notifyBlocked() {
        val text = TranslatableText("message.favorita.blocked").format(Format.RED)
        OmniChat.showChatMessage(text)
    }

}
