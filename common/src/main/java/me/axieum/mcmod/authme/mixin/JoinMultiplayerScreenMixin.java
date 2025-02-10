package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;

import me.axieum.mcmod.authme.api.Config;
import me.axieum.mcmod.authme.api.gui.screen.AuthMethodScreen;
import me.axieum.mcmod.authme.api.gui.widget.AuthButtonWidget;
import me.axieum.mcmod.authme.api.util.SessionUtils;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * Injects a button into the multiplayer screen to open the authentication screen.
 */
@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen
{
    private JoinMultiplayerScreenMixin(Component title)
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
        LOGGER.info("Adding auth button to the multiplayer screen");
        assert minecraft != null;

        // Create and add the button to the screen
        addRenderableWidget(
            new AuthButtonWidget(
                this,
                Config.AuthButton.x,
                Config.AuthButton.y,
                btn -> minecraft.setScreen(new AuthMethodScreen(this)),
                // Optionally, enable button dragging
                Config.AuthButton.draggable ? btn -> {
                    // Sync configuration with the updated button position
                    LOGGER.info("Moved the auth button to {}, {}", btn.getX(), btn.getY());
                    Config.AuthButton.x = btn.getX();
                    Config.AuthButton.y = btn.getY();
                } : null,
                // Add a tooltip to greet the player
                Tooltip.create(Component.translatable(
                    "gui.authme.button.auth.tooltip",
                    Component.literal(SessionUtils.getUser().getName()).withStyle(ChatFormatting.YELLOW)
                )),
                // Non-visible text, useful for screen narrator
                Component.translatable("gui.authme.button.auth")
            )
        );
    }
}
