package me.axieum.mcmod.authme;

import me.axieum.mcmod.authme.config.AuthMeConfig;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthMe implements ClientModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final AuthMeConfig CONFIG = AuthMeConfig.init();

    @Override
    public void onInitializeClient() {}
}
