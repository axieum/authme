package me.axieum.mcmod.authme.impl.fabric;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import me.axieum.mcmod.authme.api.AuthMe;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * The Fabric platform mod.
 */
public class AuthMeFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        // Migrate the old `config/authme.json5` configuration file
        migrateConfig();

        // Cascade mod initialisation
        AuthMe.init();
    }

    /**
     * Migrates the old `authme.json5` config to new `authme.jsonc`.
     */
    private static void migrateConfig()
    {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path oldConfig = configDir.resolve("authme.json5");
        Path newConfig = configDir.resolve("authme.jsonc");

        if (Files.exists(oldConfig) && Files.notExists(newConfig)) {
            LOGGER.warn("Found old config file '{}', renaming to '{}'", oldConfig, newConfig);
            try {
                Files.move(oldConfig, newConfig);
            } catch (IOException e) {
                LOGGER.error("Could not migrate old config file to '{}'", newConfig, e);
            }
        }
    }
}
