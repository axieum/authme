package me.axieum.mcmod.authme.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Config(name = "authme")
public class AuthMeConfig implements ConfigData
{
    @Comment("Auth Button")
    @ConfigEntry.Gui.CollapsibleObject
    public AuthButton authButton = new AuthButton();

    /**
     * Authentication button configuration.
     */
    public static class AuthButton
    {
        @Comment("Position of the button on the multiplayer screen")
        public int x = 6, y = 6;
    }

    /**
     * Registers and prepares a new configuration instance.
     *
     * @return registered config holder
     * @see AutoConfig#register
     */
    public static ConfigHolder<AuthMeConfig> init()
    {
        // Register the config
        ConfigHolder<AuthMeConfig> holder = AutoConfig.register(AuthMeConfig.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
                AutoConfig.getConfigHolder(AuthMeConfig.class).load());

        return holder;
    }

    /**
     * Mod Menu integration.
     */
    @Environment(EnvType.CLIENT)
    public static class ModMenuIntegration implements ModMenuApi
    {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory()
        {
            return screen -> AutoConfig.getConfigScreen(AuthMeConfig.class, screen).get();
        }
    }
}
