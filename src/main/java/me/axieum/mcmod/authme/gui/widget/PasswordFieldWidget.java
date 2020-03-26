package me.axieum.mcmod.authme.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Formatting;

public class PasswordFieldWidget extends TextFieldWidget
{
    public PasswordFieldWidget(TextRenderer font, int x, int y, int width, int height, String msg)
    {
        super(font, x, y, width, height, null, msg);
        setMaxLength(256);
        setRenderTextProvider((value, limit) -> Formatting.OBFUSCATED + value);

//        // NB: Overriding the rendered characters affects interaction, as the
//        // actual rendered characters have different widths to the actual text.
//        setTextFormatter((value, limit) -> StringUtils.repeat('\u204E', value.length()));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        // Prevent copy/cut
        if (!this.isActive() || Screen.isCopy(keyCode) || Screen.isCut(keyCode))
            return false;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
