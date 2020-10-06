package me.axieum.mcmod.authme.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
     */
    public static AuthMeConfig init()
    {
        return AutoConfig.register(AuthMeConfig.class, JanksonConfigSerializer::new)
                         .getConfig();
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
