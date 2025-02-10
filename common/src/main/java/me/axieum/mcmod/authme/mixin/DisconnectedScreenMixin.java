package me.axieum.mcmod.authme.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

import me.axieum.mcmod.authme.api.gui.screen.AuthMethodScreen;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * Injects a button into the disconnected screen to open the authentication screen.
 */
@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen
{
    @Shadow
    @Final
    private Screen parent;

    @Shadow
    @Final
    private DisconnectionDetails details;

    private DisconnectedScreenMixin(Component title)
    {
        super(title);
    }

    /**
     * Injects into the creation of the screen and adds the authentication button.
     *
     * @param ci injection callback info
     */
    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo ci)
    {
        // Determine if the disconnection reason is user or session related
        if (authme$isUserRelated(details.reason())) {
            LOGGER.info("Adding auth button to the disconnected screen");
            assert minecraft != null;

            // Create and add the button to the screen where the back button is
            final Button backButton = (Button) children().get(2);
            addRenderableWidget(
                Button.builder(
                    Component.translatable("gui.authme.button.relogin"),
                    btn -> minecraft.setScreen(new AuthMethodScreen(parent))
                ).bounds(
                    backButton.getX(),
                    backButton.getY(),
                    backButton.getWidth(),
                    backButton.getHeight()
                ).build()
            );

            // Shift the back button down below our new button
            backButton.setY(backButton.getY() + backButton.getHeight() + 4);
        }
    }

    /**
     * Determines if a server disconnection reason is user or session related.
     *
     * @param reason disconnect reason text
     * @return true if the disconnection reason is user or session related
     */
    @Unique
    @SuppressWarnings("checkstyle:methodname")
    private static boolean authme$isUserRelated(final @Nullable Component reason)
    {
        if (reason != null && reason.getContents() instanceof TranslatableContents content) {
            final String key = content.getKey();
            return switch (key) {
                case "disconnect.kicked",
                     "multiplayer.disconnect.banned",
                     "multiplayer.disconnect.banned.reason",
                     "multiplayer.disconnect.banned.expiration",
                     "multiplayer.disconnect.duplicate_login",
                     "multiplayer.disconnect.kicked",
                     "multiplayer.disconnect.unverified_username",
                     "multiplayer.disconnect.not_whitelisted",
                     "multiplayer.disconnect.name_taken",
                     "multiplayer.disconnect.missing_public_key",
                     "multiplayer.disconnect.expired_public_key",
                     "multiplayer.disconnect.invalid_public_key_signature",
                     "multiplayer.disconnect.unsigned_chat",
                     "multiplayer.disconnect.chat_validation_failed" -> true;
                default -> key.startsWith("disconnect.loginFailed");
            };
        }
        return false;
    }
}
