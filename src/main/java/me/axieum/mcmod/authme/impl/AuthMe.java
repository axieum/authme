package me.axieum.mcmod.authme.impl;

import me.shedaniel.autoconfig.ConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

import me.axieum.mcmod.authme.impl.config.AuthMeConfig;

/**
 * Auth Me - authenticate yourself in Minecraft and re-validate your session.
 */
public final class AuthMe implements ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ConfigHolder<AuthMeConfig> CONFIG = AuthMeConfig.init();
    public static final String MOJANG_ACCOUNT_MIGRATION_FAQ_URL = "https://aka.ms/MinecraftPostMigrationFAQ";

    @Override
    public void onInitializeClient() {}

    /**
     * Returns the config instance.
     *
     * @return config instance
     * @see ConfigHolder#getConfig()
     */
    public static AuthMeConfig getConfig()
    {
        return CONFIG.getConfig();
    }
}
