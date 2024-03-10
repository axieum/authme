package me.axieum.mcmod.authme.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import me.axieum.mcmod.authme.impl.gui.AuthMethodScreen;
import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;

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
    private Text reason;

    private DisconnectedScreenMixin(Text title)
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
        if (isUserRelated(reason)) {
            LOGGER.info("Adding auth button to the disconnected screen");
            assert client != null;

            // Create and add the button to the screen where the back button is
            final ButtonWidget backButton = (ButtonWidget) children().get(2);
            addDrawableChild(
                ButtonWidget.builder(
                    Text.translatable("gui.authme.button.relogin"),
                    btn -> client.setScreen(new AuthMethodScreen(parent))
                ).dimensions(
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
    private static boolean isUserRelated(final @Nullable Text reason)
    {
        if (reason != null && reason.getContent() instanceof TranslatableTextContent content) {
            final String key = content.getKey();
            return key != null && switch (key) {
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
