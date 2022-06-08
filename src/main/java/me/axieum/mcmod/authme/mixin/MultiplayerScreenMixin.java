package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import me.axieum.mcmod.authme.api.gui.widget.AuthButtonWidget;
import me.axieum.mcmod.authme.api.util.SessionUtils;
import me.axieum.mcmod.authme.impl.gui.AuthMethodScreen;
import static me.axieum.mcmod.authme.impl.AuthMe.CONFIG;
import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;
import static me.axieum.mcmod.authme.impl.AuthMe.getConfig;

/**
 * Injects a button into the multiplayer screen to open the authentication screen.
 */
@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen
{
    private MultiplayerScreenMixin(Text title)
    {
        super(title);
    }

    /**
     * Injects into the creation of the screen and adds the authentication button.
     *
     * @param ci injection callback info
     */
    @Inject(method = "init", at = @At("HEAD"))
    private void init(CallbackInfo ci)
    {
        LOGGER.info("Adding auth button to the multiplayer screen");
        assert client != null;

        // Create and add the button to the screen
        addDrawableChild(
            new AuthButtonWidget(
                this,
                getConfig().authButton.x,
                getConfig().authButton.y,
                btn -> client.setScreen(new AuthMethodScreen(this)),
                // Optionally, enable button dragging
                getConfig().authButton.draggable ? btn -> {
                    // Sync configuration with the updated button position
                    LOGGER.info("Moved the auth button to {}, {}", btn.x, btn.y);
                    getConfig().authButton.x = btn.x;
                    getConfig().authButton.y = btn.y;
                    CONFIG.save();
                } : null,
                // Add a tooltip to greet the player
                (btn, mtx, x, y) -> renderTooltip(mtx, Text.translatable(
                    "gui.authme.button.auth.tooltip",
                    Text.literal(SessionUtils.getSession().getUsername()).formatted(Formatting.YELLOW)
                ), x, y),
                // Non-visible text, useful for screen narrator
                Text.translatable("gui.authme.button.auth")
            )
        );
    }
}
