import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import dev.deftu.gradle.utils.GameSide
import dev.deftu.gradle.utils.MinecraftVersion
import dev.deftu.gradle.utils.ModLoader
import dev.deftu.gradle.utils.includeOrShade

plugins {
    java
    kotlin("jvm")
    id("dev.deftu.gradle.multiversion")
    id("dev.deftu.gradle.tools")
    id("dev.deftu.gradle.tools.resources")
    id("dev.deftu.gradle.tools.bloom")
    id("dev.deftu.gradle.tools.shadow")
    id("dev.deftu.gradle.tools.publishing.maven")
    id("dev.deftu.gradle.tools.minecraft.loom")
    id("dev.deftu.gradle.tools.minecraft.releases")
}

toolkitMultiversion {
    moveBuildsToRootProject.set(true)
}

toolkitMavenPublishing {
    forceLowercase.set(true)
}

toolkitLoomHelper {
    disableRunConfigs(GameSide.SERVER)

    if (!mcData.isNeoForge) {
        useMixinRefMap(modData.id)
    }

    if (mcData.isForge) {
        useTweaker("org.spongepowered.asm.launch.MixinTweaker")
        useForgeMixin(modData.id)
    }

    if (mcData.isForgeLike && mcData.version >= MinecraftVersion.VERSION_1_15_2) {
        useKotlinForForge()
    }
}

toolkitReleases {
    rootProject.file("changelogs/${modData.version}.md").let { file -> if (file.exists()) changelogFile.set(file) }
    detectVersionType.set(true)

    modrinth {
        projectId.set("XLpX6IPW")
        if (mcData.loader == ModLoader.FABRIC) {
            dependencies.addAll(listOf(
                ModDependency("P7dR8mSH", DependencyType.REQUIRED),                     // Fabric API
                ModDependency("Ha28R6CL", DependencyType.REQUIRED),                     // Fabric Language Kotlin
                ModDependency("mOgUt4GM", DependencyType.OPTIONAL)                      // Mod Menu
            ))
        } else if (mcData.version >= MinecraftVersion.VERSION_1_16_5) {
            dependencies.addAll(listOf(
                ModDependency("ordsPcFz", DependencyType.REQUIRED)                      // Kotlin for Forge
            ))
        }

        if (mcData.version >= MinecraftVersion.VERSION_1_16_5) {
            dependencies.add(ModDependency("T0Zb6DLv", DependencyType.REQUIRED))        // Textile
            dependencies.add(ModDependency("UhitUcEo", DependencyType.REQUIRED))        // Textualizer
            dependencies.add(ModDependency("MaDESStl", DependencyType.REQUIRED))        // Omnicore
        }
    }
}

dependencies {
    val textileVersion = "0.5.2"
    val omnicoreVersion = "0.9.0"
    val textualizerVersion = "0.1.1"
    implementation("dev.deftu:textile:$textileVersion")
    modImplementation("dev.deftu:textile-$mcData:$textileVersion")
    modImplementation("dev.deftu:omnicore-$mcData:$omnicoreVersion")
    modImplementation("dev.deftu:textualizer-$mcData:$textualizerVersion")

    if (mcData.isFabric) {
        modImplementation("net.fabricmc.fabric-api:fabric-api:${mcData.dependencies.fabric.fabricApiVersion}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${mcData.dependencies.fabric.fabricLanguageKotlinVersion}")

        modImplementation(mcData.dependencies.fabric.modMenuDependency)
    } else if (mcData.isLegacyForge) {
        implementation(includeOrShade(kotlin("stdlib-jdk8"))!!)
        implementation(includeOrShade("org.jetbrains.kotlin:kotlin-reflect:1.6.10")!!)

        modImplementation(includeOrShade("org.spongepowered:mixin:0.7.11-SNAPSHOT")!!)

        includeOrShade("dev.deftu:textile:$textileVersion")
        includeOrShade("dev.deftu:textile-$mcData:$textileVersion")
        includeOrShade("dev.deftu:omnicore-$mcData:$omnicoreVersion")
        includeOrShade("dev.deftu:textualizer-$mcData:$textualizerVersion")
    }
}

tasks {

    fatJar {
        if (mcData.isLegacyForge) {
            relocate("dev.deftu.textile", "dev.deftu.favorita.textile")
            relocate("dev.deftu.omnicore", "dev.deftu.favorita.omnicore")
            relocate("dev.deftu.textualizer", "dev.deftu.favorita.textualizer")
        }
    }

}
