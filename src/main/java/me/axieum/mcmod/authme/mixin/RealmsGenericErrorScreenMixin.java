package me.axieum.mcmod.authme.mixin;

import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import me.axieum.mcmod.authme.AuthMe;
import me.axieum.mcmod.authme.gui.AuthScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsGenericErrorScreen.class)
public abstract class RealmsGenericErrorScreenMixin extends RealmsScreen
{
    @Shadow
    @Final
    private Text line1;

    @Shadow
    @Final
    private Screen parent;

    protected RealmsGenericErrorScreenMixin(Text line1, Text line2, Screen parent) { super(); }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info)
    {
        // Determine if the disconnection reason is session related
        if (line1 == null || !getTranslationKey(line1).startsWith("mco.error.invalid.session")) return;

        final AbstractButtonWidget backButton = this.buttons.get(0);

        // Inject the authentication button where the back button was
        AuthMe.LOGGER.debug("Injecting authentication button into disconnection screen");
        this.addButton(new ButtonWidget(backButton.x,
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
        return component instanceof TranslatableText ? ((TranslatableText) component).getKey()
                : "";
    }
}
