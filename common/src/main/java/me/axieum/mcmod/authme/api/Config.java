package me.axieum.mcmod.authme.api;

import java.util.Objects;

import com.teamresourceful.resourcefulconfig.api.annotations.Category;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo;
import com.teamresourceful.resourcefulconfig.api.annotations.ConfigInfo.Link;

import me.axieum.mcmod.authme.api.util.MicrosoftUtils;
import me.axieum.mcmod.authme.api.util.MicrosoftUtils.MicrosoftPrompt;

/**
 * The mod configuration.
 */
@com.teamresourceful.resourcefulconfig.api.annotations.Config(
    value = AuthMe.MOD_ID,
    categories = {
        Config.AuthButton.class,
        Config.LoginMethods.class,
    }
)
@ConfigInfo(
    titleTranslation = "text.rconfig.authme.title",
    descriptionTranslation = "text.rconfig.authme.description",
    icon = "unlock",
    links = {
        @Link(text = "CurseForge", icon = "curseforge", value = "https://curseforge.com/minecraft/mc-mods/auth-me"),
        @Link(text = "Modrinth", icon = "modrinth", value = "https://modrinth.com/mod/auth-me"),
        @Link(text = "GitHub", icon = "github", value = "https://github.com/axieum/authme"),
    }
)
public class Config
{
    /** Authentication button configuration schema. */
    @Category(value = "authButton")
    @ConfigInfo(
        titleTranslation = "text.rconfig.authme.option.authButton",
        descriptionTranslation = "text.rconfig.authme.option.authButton.description"
    )
    public static class AuthButton
    {
        /** X coordinate of the button on the multiplayer screen. */
        @ConfigEntry(id = "x", translation = "text.rconfig.authme.option.authButton.x")
        public static int x = 6;

        /** Y coordinate of the button on the multiplayer screen. */
        @ConfigEntry(id = "y", translation = "text.rconfig.authme.option.authButton.y")
        public static int y = 6;

        /** True if the button can be dragged to a new position. */
        @ConfigEntry(id = "draggable", translation = "text.rconfig.authme.option.authButton.draggable")
        public static boolean draggable = true;
    }

    /** Authentication methods configuration schema. */
    @Category(
        value = "methods",
        categories = {
            LoginMethods.Microsoft.class,
            LoginMethods.Offline.class,
        }
    )
    @ConfigInfo(
        titleTranslation = "text.rconfig.authme.option.methods",
        descriptionTranslation = "text.rconfig.authme.option.methods.description"
    )
    public static class LoginMethods
    {
        /** Login via Microsoft configuration schema. */
        @Category(value = "microsoft")
        @ConfigInfo(
            titleTranslation = "text.rconfig.authme.option.methods.microsoft",
            descriptionTranslation = "text.rconfig.authme.option.methods.microsoft.description"
        )
        public static class Microsoft
        {
            /** Indicates the type of user interaction that is required. */
            @ConfigEntry(id = "prompt", translation = "text.rconfig.authme.option.methods.microsoft.prompt")
            public static MicrosoftPrompt prompt = MicrosoftPrompt.DEFAULT;

            /** The port from which to listen for OAuth2 callbacks. */
            @ConfigEntry(id = "port", translation = "text.rconfig.authme.option.methods.microsoft.port")
            public static int port = 25585;

            /** OAuth2 client id. */
            @ConfigEntry(id = "clientId", translation = "text.rconfig.authme.option.methods.microsoft.clientId")
            public static String clientId = MicrosoftUtils.CLIENT_ID;

            /** OAuth2 authorization url. */
            @ConfigEntry(id = "authorizeUrl", translation = "text.rconfig.authme.option.methods.microsoft.authorizeUrl")
            public static String authorizeUrl = MicrosoftUtils.AUTHORIZE_URL;

            /** OAuth2 access token url. */
            @ConfigEntry(id = "tokenUrl", translation = "text.rconfig.authme.option.methods.microsoft.tokenUrl")
            public static String tokenUrl = MicrosoftUtils.TOKEN_URL;

            /** Xbox authentication url. */
            @ConfigEntry(id = "xboxAuthUrl", translation = "text.rconfig.authme.option.methods.microsoft.xboxAuthUrl")
            public static String xboxAuthUrl = MicrosoftUtils.XBOX_AUTH_URL;

            /** Xbox XSTS authorization url. */
            @ConfigEntry(id = "xboxXstsUrl", translation = "text.rconfig.authme.option.methods.microsoft.xboxXstsUrl")
            public static String xboxXstsUrl = MicrosoftUtils.XBOX_XSTS_URL;

            /** Minecraft authentication url. */
            @ConfigEntry(id = "mcAuthUrl", translation = "text.rconfig.authme.option.methods.microsoft.mcAuthUrl")
            public static String mcAuthUrl = MicrosoftUtils.MC_AUTH_URL;

            /** Minecraft profile url. */
            @ConfigEntry(id = "mcProfileUrl", translation = "text.rconfig.authme.option.methods.microsoft.mcProfileUrl")
            public static String mcProfileUrl = MicrosoftUtils.MC_PROFILE_URL;

            /**
             * Determines whether the configured URLs differ from the defaults.
             *
             * @return true if the configured URLs are unchanged
             */
            public static boolean isDefaults()
            {
                return Objects.equals(authorizeUrl, MicrosoftUtils.AUTHORIZE_URL)
                    && Objects.equals(tokenUrl, MicrosoftUtils.TOKEN_URL)
                    && Objects.equals(xboxAuthUrl, MicrosoftUtils.XBOX_AUTH_URL)
                    && Objects.equals(xboxXstsUrl, MicrosoftUtils.XBOX_XSTS_URL)
                    && Objects.equals(mcAuthUrl, MicrosoftUtils.MC_AUTH_URL)
                    && Objects.equals(mcProfileUrl, MicrosoftUtils.MC_PROFILE_URL);
            }
        }

        /** Login offline configuration schema. */
        @Category(value = "offline")
        @ConfigInfo(
            titleTranslation = "text.rconfig.authme.option.methods.offline",
            descriptionTranslation = "text.rconfig.authme.option.methods.offline.description"
        )
        public static class Offline
        {
            /** Last used username. */
            @ConfigEntry(id = "lastUsername", translation = "text.rconfig.authme.option.methods.offline.lastUsername")
            public static String lastUsername = "";
        }
    }
}
