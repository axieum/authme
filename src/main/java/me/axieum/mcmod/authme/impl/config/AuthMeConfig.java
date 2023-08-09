package me.axieum.mcmod.authme.impl.config;

import java.util.Objects;
import java.util.Optional;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.util.Session;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import me.axieum.mcmod.authme.api.util.MicrosoftUtils;
import me.axieum.mcmod.authme.api.util.MicrosoftUtils.MicrosoftPrompt;

@Config(name = "authme")
public class AuthMeConfig implements ConfigData
{
    @Comment("Auth Button")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public AuthButtonSchema authButton = new AuthButtonSchema();

    /**
     * Authentication button configuration schema.
     */
    public static class AuthButtonSchema
    {
        @Comment("Position of the button on the multiplayer screen")
        public int x = 6, y = 6;

        @Comment("True if the button can be dragged to a new position")
        public boolean draggable = true;
    }

    @Comment("Auto Login")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public AutoLoginSchema autoLogin = new AutoLoginSchema();

    /**
     * Auto login config.
     */
    public static class AutoLoginSchema
    {
        @Comment("Save session after login to be used for auto login")
        public boolean saveSession = false;

        @Comment("Automatically attempt to login using a saved session")
        public boolean doAutoLogin = false;

        @Comment("Saved session (valid for 24h if account type is msa - Microsoft)")
        @ConfigEntry.Gui.CollapsibleObject()
        public SavedSessionSchema savedSession = new SavedSessionSchema();

        public static class SavedSessionSchema
        {
            @Comment("Player username")
            public String username = "";
            @Comment("Player UUID")
            public String uuid = "";
            @ConfigEntry.Gui.Excluded
            @Comment("Access Token - DO NOT SHARE")
            public String accessToken = "";
            @ConfigEntry.Gui.Excluded
            public String xuid = "";
            @ConfigEntry.Gui.Excluded
            public String clientId = "";
            @Comment("Account type (legacy/mojang/msa)")
            public String accountType = "";

            public boolean hasSavedSession()
            {
                return !(username.isEmpty() || uuid.isEmpty() || accessToken.isEmpty()
                    || Session.AccountType.byName(accountType) == null);
            }

            public Session getSession()
            {
                return new Session(username,
                    uuid,
                    accessToken,
                    xuid.isEmpty() ? Optional.empty() : Optional.of(xuid),
                    clientId.isEmpty() ? Optional.empty() : Optional.of(clientId),
                    Session.AccountType.byName(accountType));
            }

            public void setSession(Session session)
            {
                username = session.getUsername();
                uuid = session.getUuid();
                accessToken = session.getAccessToken();
                xuid = session.getXuid().orElse("");
                clientId = session.getClientId().orElse("");
                accountType = session.getAccountType().getName();
            }
        }
    }

    @Comment("Login Methods")
    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    public LoginMethodsSchema methods = new LoginMethodsSchema();

    /**
     * Authentication methods configuration schema.
     */
    public static class LoginMethodsSchema
    {
        @Comment("Login via Microsoft")
        @ConfigEntry.Gui.CollapsibleObject
        public MicrosoftLoginSchema microsoft = new MicrosoftLoginSchema();

        /**
         * Login via Microsoft configuration schema.
         */
        public static class MicrosoftLoginSchema
        {
            @Comment("Indicates the type of user interaction that is required")
            public MicrosoftPrompt prompt = MicrosoftPrompt.DEFAULT;

            @Comment("The port from which to listen for OAuth2 callbacks")
            public int port = 25585;

            @Comment("OAuth2 client id")
            public String clientId = MicrosoftUtils.CLIENT_ID;

            @Comment("OAuth2 authorization url")
            public String authorizeUrl = MicrosoftUtils.AUTHORIZE_URL;

            @Comment("OAuth2 access token url")
            public String tokenUrl = MicrosoftUtils.TOKEN_URL;

            @Comment("Xbox authentication url")
            public String xboxAuthUrl = MicrosoftUtils.XBOX_AUTH_URL;

            @Comment("Xbox XSTS authorization url")
            public String xboxXstsUrl = MicrosoftUtils.XBOX_XSTS_URL;

            @Comment("Minecraft authentication url")
            public String mcAuthUrl = MicrosoftUtils.MC_AUTH_URL;

            @Comment("Minecraft profile url")
            public String mcProfileUrl = MicrosoftUtils.MC_PROFILE_URL;

            /**
             * Determines whether the configured URLs differ from the defaults.
             *
             * @return true if the configured URLs are unchanged
             */
            public boolean isDefaults()
            {
                return Objects.equals(authorizeUrl, MicrosoftUtils.AUTHORIZE_URL)
                    && Objects.equals(tokenUrl, MicrosoftUtils.TOKEN_URL)
                    && Objects.equals(xboxAuthUrl, MicrosoftUtils.XBOX_AUTH_URL)
                    && Objects.equals(xboxXstsUrl, MicrosoftUtils.XBOX_XSTS_URL)
                    && Objects.equals(mcAuthUrl, MicrosoftUtils.MC_AUTH_URL)
                    && Objects.equals(mcProfileUrl, MicrosoftUtils.MC_PROFILE_URL);
            }
        }

        @Comment("Login via Mojang (or legacy)")
        @ConfigEntry.Gui.CollapsibleObject
        public MojangLoginSchema mojang = new MojangLoginSchema();

        /**
         * Login via Mojang (or legacy) configuration schema.
         */
        public static class MojangLoginSchema
        {
            @Comment("Last used username")
            public @Nullable String lastUsername = "";
        }

        @Comment("Login Offline")
        @ConfigEntry.Gui.CollapsibleObject
        public OfflineLoginSchema offline = new OfflineLoginSchema();

        /**
         * Login offline configuration schema.
         */
        public static class OfflineLoginSchema
        {
            @Comment("Last used username")
            public @Nullable String lastUsername = "";
        }
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
}
