package me.axieum.mcmod.authme.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.realms.RealmsScreen;

import me.axieum.mcmod.authme.api.gui.screen.AuthMethodScreen;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * Injects a button into the Realms' error screen to open the authentication screen.
 */
@Mixin(RealmsGenericErrorScreen.class)
public abstract class RealmsGenericErrorScreenMixin extends RealmsScreen
{
    @Shadow
    @Final
    private Screen nextScreen;

    @Shadow
    @Final
    private RealmsGenericErrorScreen.ErrorMessage lines;

    private RealmsGenericErrorScreenMixin(Component title)
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
        if (authme$isUserRelated(lines.detail())) {
            LOGGER.info("Adding auth button to the Realms error screen");
            assert minecraft != null;

            // Create and add the button to the screen above the back button
            final Button backButton = (Button) children().getFirst();
            addRenderableWidget(
                Button.builder(
                    Component.translatable("gui.authme.button.relogin"),
                    btn -> minecraft.setScreen(new AuthMethodScreen(nextScreen))
                ).bounds(
                    backButton.getX(),
                    backButton.getY() - backButton.getHeight() - 4,
                    backButton.getWidth(),
                    backButton.getHeight()
                ).build()
            );
        }
    }

    /**
     * Determines if a Realms' disconnection reason is user or session related.
     *
     * @param reason disconnect reason text
     * @return true if the disconnection reason is user or session related
     */
    @Unique
    @SuppressWarnings({"checkstyle:illegalidentifiername", "checkstyle:methodname"})
    private static boolean authme$isUserRelated(final @Nullable Component reason)
    {
        if (reason != null && reason.getContents() instanceof TranslatableContents content) {
            return content.getKey().startsWith("mco.error.invalid.session");
        }
        return false;
    }
}
