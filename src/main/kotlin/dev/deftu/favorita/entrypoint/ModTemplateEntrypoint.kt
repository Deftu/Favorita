package dev.deftu.favorita.entrypoint

//#if FABRIC
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
//#elseif FORGE
//#if MC >= 1.15.2
//$$ import net.minecraftforge.eventbus.api.IEventBus
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
//$$ import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
//$$ import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
//#else
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.common.Mod.EventHandler
//$$ import net.minecraftforge.fml.common.event.FMLInitializationEvent
//#endif
//#elseif NEOFORGE
//$$ import net.neoforged.bus.api.IEventBus
//$$ import net.neoforged.fml.common.Mod
//$$ import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
//$$ import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
//#endif

import dev.deftu.favorita.Favorita
import dev.deftu.favorita.client.FavoritaClient
import dev.deftu.favorita.server.FavoritaServer

//#if FORGE-LIKE
//$$ import dev.deftu.favorita.ModTemplateConstants
//#if MC >= 1.15.2
//$$ @Mod(ModTemplateConstants.ID)
//#else
//$$ @Mod(modid = ModTemplateConstants.ID)
//#endif
//#endif
object ModTemplateEntrypoint
    //#if FABRIC
    : ModInitializer, ClientModInitializer, DedicatedServerModInitializer
    //#endif
{

    //#if FORGE && MC >= 1.15.2
    //$$ init {
    //$$     setupForgeEvents(FMLJavaModLoadingContext.get().modEventBus)
    //$$ }
    //#elseif NEOFORGE
    //$$ constructor(modEventBus: IEventBus) {
    //$$     setupForgeEvents(modEventBus)
    //$$ }
    //#endif

    //#if FABRIC
    override
    //#endif
    fun onInitialize(
        //#if FORGE-LIKE
        //#if MC >= 1.15.2
        //$$ event: FMLCommonSetupEvent
        //#else
        //$$ event: FMLInitializationEvent
        //#endif
        //#endif
    ) {
        Favorita.onInitializeCommon()
    }

    //#if FABRIC
    override
    //#endif
    fun onInitializeClient(
        //#if FORGE-LIKE
        //#if MC >= 1.15.2
        //$$ event: FMLClientSetupEvent
        //#else
        //$$ event: FMLInitializationEvent
        //#endif
        //#endif
    ) {
        //#if MC <= 1.12.2
        //$$ if (!event.side.isClient) return
        //#endif

        FavoritaClient.onInitializeClient()
    }

    //#if FABRIC
    override
    //#endif
    fun onInitializeServer(
        //#if FORGE-LIKE
        //#if MC >= 1.15.2
        //$$ event: FMLDedicatedServerSetupEvent
        //#else
        //$$ event: FMLInitializationEvent
        //#endif
        //#endif
    ) {
        //#if MC <= 1.12.2
        //$$ if (!event.side.isServer) return
        //#endif

        FavoritaServer.onInitializeServer()
    }

    //#if FORGE-LIKE && MC >= 1.15.2
    //$$ fun setupForgeEvents(modEventBus: IEventBus) {
    //$$     modEventBus.addListener(this::onInitialize)
    //$$     modEventBus.addListener(this::onInitializeClient)
    //$$     modEventBus.addListener(this::onInitializeServer)
    //$$ }
    //#endif

}
