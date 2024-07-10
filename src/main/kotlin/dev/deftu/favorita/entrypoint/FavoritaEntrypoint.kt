package dev.deftu.favorita.entrypoint

//#if FABRIC
import net.fabricmc.api.ClientModInitializer
//#elseif FORGE
//#if MC >= 1.16.5
//$$ import net.minecraftforge.eventbus.api.IEventBus
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
//#if MC >= 1.19.2
//$$ import net.minecraftforge.client.event.RegisterKeyMappingsEvent
//#endif
//#else
//$$ import net.minecraftforge.fml.common.Mod
//$$ import net.minecraftforge.fml.common.Mod.EventHandler
//$$ import net.minecraftforge.fml.common.event.FMLInitializationEvent
//#endif
//#elseif NEOFORGE
//$$ import net.neoforged.bus.api.IEventBus
//$$ import net.neoforged.fml.common.Mod
//$$ import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
//$$ import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
//#endif

import dev.deftu.favorita.client.FavoritaClient

//#if FORGE-LIKE
//$$ import dev.deftu.favorita.FavoritaConstants
//$$
//#if MC >= 1.15.2
//$$ @Mod(FavoritaConstants.ID)
//#else
//$$ @Mod(modid = FavoritaConstants.ID, version = FavoritaConstants.VERSION)
//#endif
//#endif
class FavoritaEntrypoint
    //#if FABRIC
    : ClientModInitializer
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
    //#elseif MC <= 1.12.2
    //$$ @Mod.EventHandler
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

    //#if FORGE-LIKE && MC >= 1.16.5
    //$$ fun setupForgeEvents(modEventBus: IEventBus) {
    //$$     modEventBus.addListener(this::onInitializeClient)
    //#if MC >= 1.19.2
    //$$     modEventBus.addListener<RegisterKeyMappingsEvent> { event ->
    //$$         FavoritaClient.favoriteKeyBinding.register(event)
    //$$     }
    //#endif
    //$$ }
    //#endif

}
