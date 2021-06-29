package me.axieum.mcmod.authme;

import me.shedaniel.autoconfig.ConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

import me.axieum.mcmod.authme.config.AuthMeConfig;

public class AuthMe implements ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ConfigHolder<AuthMeConfig> CONFIG = AuthMeConfig.init();

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
