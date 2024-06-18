package dev.deftu.favorita.entrypoint

//#if FABRIC
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
//#elseif FORGE
//#if MC >= 1.15.2
//$$ import net.minecraftforge.eventbus.api.IEventBus
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
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
//#endif

import dev.deftu.favorita.Favorita
import dev.deftu.favorita.client.FavoritaClient

//#if FORGE-LIKE
//$$ import dev.deftu.favorita.FavoritaConstants
//$$
//#if MC >= 1.15.2
//$$ @Mod(FavoritaConstants.ID)
//#else
//$$ @Mod(modid = FavoritaConstants.ID)
//#endif
//#endif
class FavoritaEntrypoint
    //#if FABRIC
    : ModInitializer, ClientModInitializer
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

    //#if FORGE-LIKE && MC >= 1.15.2
    //$$ fun setupForgeEvents(modEventBus: IEventBus) {
    //$$     modEventBus.addListener(this::onInitialize)
    //$$     modEventBus.addListener(this::onInitializeClient)
    //$$ }
    //#endif

}
