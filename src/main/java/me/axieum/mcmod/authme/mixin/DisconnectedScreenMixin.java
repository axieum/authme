package me.axieum.mcmod.authme.mixin;

import me.axieum.mcmod.authme.AuthMe;
import me.axieum.mcmod.authme.gui.AuthScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen
{
    @Shadow
    @Final
    private Text reason;

    @Shadow
    @Final
    private Screen parent;

    protected DisconnectedScreenMixin(Text title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info)
    {
        // Determine if the disconnection reason is session related
        if (reason == null || !getTranslationKey(reason).startsWith("disconnect.loginFailed")) return;

        final ButtonWidget backButton = (ButtonWidget) ((ScreenAccess) this).getChildren().get(0);

        // Inject the authentication button where the back button was
        AuthMe.LOGGER.debug("Injecting authentication button into disconnection screen");
        this.addDrawableChild(new ButtonWidget(backButton.x,
                                               backButton.y,
                                               backButton.getWidth(),
                                               20,
                                               new TranslatableText("gui.authme.disconnect.button.auth"),
                                               button -> this.client.openScreen(new AuthScreen(parent))));

        // Move back button below
        backButton.y += 26;
    }

    /**
     * Returns the translation key for a text component.
     *
     * @param component text component
     * @return translation key of translation text component else empty string
     */
    private static String getTranslationKey(Text component)
    {
        return component instanceof TranslatableText ? ((TranslatableText) component).getKey() : "";
    }
}
