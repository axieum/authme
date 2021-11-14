package me.axieum.mcmod.authme.impl;

import me.shedaniel.autoconfig.ConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;

import me.axieum.mcmod.authme.impl.config.AuthMeConfig;

/**
 * Auth Me - authenticate yourself in Minecraft and re-validate your session.
 */
public final class AuthMe implements ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ConfigHolder<AuthMeConfig> CONFIG = AuthMeConfig.init();
    public static final Identifier WIDGETS_TEXTURE = new Identifier("authme", "textures/gui/widgets.png");

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
