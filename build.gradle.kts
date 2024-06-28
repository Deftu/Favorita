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
    id("dev.deftu.gradle.tools.minecraft.loom")
    id("dev.deftu.gradle.tools.minecraft.releases")
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

dependencies {
    val textileVersion = "0.3.1"
    val omnicoreVersion = "0.6.0"
    modImplementation("dev.deftu:textile-$mcData:$textileVersion")
    modImplementation("dev.deftu:omnicore-$mcData:$omnicoreVersion")

    if (mcData.isFabric) {
        modImplementation("net.fabricmc.fabric-api:fabric-api:${mcData.dependencies.fabric.fabricApiVersion}")
        modImplementation("net.fabricmc:fabric-language-kotlin:${mcData.dependencies.fabric.fabricLanguageKotlinVersion}")

        modImplementation(mcData.dependencies.fabric.modMenuDependency)
    } else if (mcData.version <= MinecraftVersion.VERSION_1_12_2) {
        implementation(includeOrShade(kotlin("stdlib-jdk8"))!!)
        implementation(includeOrShade("org.jetbrains.kotlin:kotlin-reflect:1.6.10")!!)

        modImplementation(includeOrShade("org.spongepowered:mixin:0.7.11-SNAPSHOT")!!)

        includeOrShade("dev.deftu:textile-$mcData:$textileVersion")
        includeOrShade("dev.deftu:omnicore-$mcData:$omnicoreVersion")
    }
}

toolkitReleases {
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

        dependencies.add(ModDependency("T0Zb6DLv", DependencyType.REQUIRED))            // Textile
        dependencies.add(ModDependency("MaDESStl", DependencyType.REQUIRED))            // Omnicore
    }
}

tasks {

    fatJar {
        if (mcData.isLegacyForge) {
            relocate("dev.deftu.textile", "dev.deftu.favorita.textile")
            relocate("dev.deftu.omnicore", "dev.deftu.favorita.omnicore")
        }
        
        exclude("META-INF/versions/**")
        exclude("META-INF/proguard/**")
        exclude("META-INF/maven/**")
        exclude("META-INF/com.android.tools/**")
    }

    remapJar {
        destinationDirectory.set(rootProject.layout.buildDirectory.asFile.get().resolve("jars"))
    }

}
