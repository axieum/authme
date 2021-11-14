package me.axieum.mcmod.authme.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsGenericErrorScreen;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import me.axieum.mcmod.authme.impl.gui.AuthMethodScreen;
import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;

/**
 * Injects a button into the Realms' error screen to open the authentication screen.
 */
@Mixin(RealmsGenericErrorScreen.class)
public abstract class RealmsGenericErrorScreenMixin extends RealmsScreen
{
    @Shadow
    @Final
    private Screen parent;

    @Shadow
    private Text line1;

    private RealmsGenericErrorScreenMixin(Text title)
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
        if (isUserRelated(line1)) {
            LOGGER.info("Adding auth button to the Realms error screen");
            assert client != null;

            // Create and add the button to the screen above the back button
            final ButtonWidget backButton = (ButtonWidget) children().get(0);
            addDrawableChild(
                new ButtonWidget(
                    backButton.x,
                    backButton.y - backButton.getHeight() - 4,
                    backButton.getWidth(),
                    backButton.getHeight(),
                    new TranslatableText("gui.authme.button.relogin"),
                    btn -> client.setScreen(new AuthMethodScreen(parent))
                )
            );
        }
    }

    /**
     * Determines if a Realms' disconnection reason is user or session related.
     *
     * @param reason disconnect reason text
     * @return true if the disconnection reason is user or session related
     */
    private static boolean isUserRelated(final @Nullable Text reason)
    {
        if (reason instanceof TranslatableText) {
            final String key = ((TranslatableText) reason).getKey();
            return key != null && key.startsWith("mco.error.invalid.session");
        }
        return false;
    }
}
