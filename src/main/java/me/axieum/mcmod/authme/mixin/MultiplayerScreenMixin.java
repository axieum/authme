package me.axieum.mcmod.authme.mixin;

import me.axieum.mcmod.authme.AuthMe;
import me.axieum.mcmod.authme.api.Status;
import me.axieum.mcmod.authme.gui.AuthScreen;
import me.axieum.mcmod.authme.util.SessionUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen
{
    private static Status status = Status.UNKNOWN;
    private static TexturedButtonWidget authButton;

    protected MultiplayerScreenMixin(Text title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info)
    {
        // Inject the authenticate button at top left, using lock texture or fallback text
        AuthMe.LOGGER.debug("Injecting authentication button into multiplayer screen");
        authButton = new TexturedButtonWidget(6,
                                              6,
                                              20,
                                              20,
                                              0,
                                              146,
                                              20,
                                              new Identifier("minecraft:textures/gui/widgets.png"),
                                              256,
                                              256,
                                              button -> this.client.openScreen(new AuthScreen(this)),
                                              new TranslatableText("gui.authme.multiplayer.button.auth"));
        this.addButton(authButton);

        // Fetch current session status
        MultiplayerScreenMixin.status = Status.UNKNOWN;
        SessionUtil.getStatus().thenAccept(status -> MultiplayerScreenMixin.status = status);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info)
    {
        // Draw status text/icon on button
        this.drawCenteredString(matrices,
                                this.client.textRenderer,
                                Formatting.BOLD + status.toString(),
                                authButton.x + authButton.getWidth(),
                                authButton.y - 1,
                                status.color);
    }
}
