package dev.deftu.favorita.utils

import net.minecraft.util.Identifier

object IdentifierUtils {

    fun create(
        namespace: String,
        path: String
    ): Identifier {
        //#if MC >= 1.21
        return Identifier.of(namespace, path)
        //#else
        //$$ return Identifier(namespace, path)
        //#endif
    }

}
