package dev.deftu.favorita

import dev.deftu.favorita.config.FavoritaConfig
import dev.deftu.favorita.slots.FavoriteKey
import dev.deftu.favorita.slots.FavoriteStore
import net.fabricmc.api.ClientModInitializer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.polyfrost.oneconfig.api.commands.v1.CommandManager

object Favorita : ClientModInitializer {
    const val NAME = "@MOD_NAME@"
    const val ID = "@MOD_ID@"
    const val ICON = "/assets/favorita/favorita.svg"

    @JvmField
    val LOGGER: Logger = LogManager.getLogger(NAME)

    override fun onInitializeClient() {
        FavoritaConfig.preload()
        FavoriteStore.load()
        FavoriteKey.register()
        CommandManager.register(
            CommandManager.literal(ID)
                .executes { FavoritaActions.openConfig(); 1 }
                .then(CommandManager.literal("clear").executes { if (FavoritaActions.clearCurrent()) 1 else 0 }),
        )
    }
}
