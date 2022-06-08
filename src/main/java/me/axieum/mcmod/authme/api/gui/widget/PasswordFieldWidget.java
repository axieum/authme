package me.axieum.mcmod.authme.api.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * A text field widget for masking typed passwords.
 */
public class PasswordFieldWidget extends TextFieldWidget
{
    /**
     * Constructs a new password field widget.
     *
     * @param textRenderer text renderer
     * @param x            x position
     * @param y            y position
     * @param width        widget width
     * @param height       widget height
     * @param text         label
     */
    public PasswordFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
    {
        super(textRenderer, x, y, width, height, text);
        setRenderTextProvider(
            (val, limit) -> Text.literal(val).styled(style -> style.withObfuscated(true)).asOrderedText()
        );
        // NB: Overriding the rendered characters affects interaction, as the
        // rendered characters have different widths to the actual underlying text.
        // i.e. setRenderTextProvider((value, limit) -> StringUtils.repeat('\u204E', value.length()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Prevent copying the password to clipboard
        if (!this.isActive() || Screen.isCopy(keyCode) || Screen.isCut(keyCode)) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
