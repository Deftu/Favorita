package dev.deftu.favorita.mixins

//#if MC >= 1.16.5
import org.objectweb.asm.tree.ClassNode
//#endif

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class MixinPlugin : IMixinConfigPlugin {

    override fun getMixins(): MutableList<String> {
        val result = mutableListOf<String>()

        //#if MC == 1.16.5
        //$$ result.add("Mixin_SlotIndex")
        //#endif

        //#if MC <= 1.16.5
        //$$ result.add("Mixin_SoundPitch")
        //#endif

        return result
    }

    override fun getRefMapperConfig(): String? = null
    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean = true

    override fun onLoad(mixinPackage: String) {
        // no-op
    }

    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: MutableSet<String>) {
        // no-op
    }

    override fun preApply(
        targetClassName: String,
        //#if MC >= 1.16.5
        targetClass: ClassNode,
        //#else
        //$$ targetClass: org.spongepowered.asm.lib.tree.ClassNode,
        //#endif
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        // no-op
    }

    override fun postApply(
        targetClassName: String,
        //#if MC >= 1.16.5
        targetClass: ClassNode,
        //#else
        //$$ targetClass: org.spongepowered.asm.lib.tree.ClassNode,
        //#endif
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
        // no-op
    }

}
