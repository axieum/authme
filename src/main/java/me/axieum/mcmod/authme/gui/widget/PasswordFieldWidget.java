package me.axieum.mcmod.authme.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PasswordFieldWidget extends TextFieldWidget
{
    public PasswordFieldWidget(TextRenderer font, int x, int y, int width, int height, Text msg)
    {
        super(font, x, y, width, height, null, msg);
        setMaxLength(256);
        setRenderTextProvider((value, limit) -> new LiteralText(value).styled(style -> style.withFormatting(Formatting.OBFUSCATED))
                                                                      .asOrderedText());

//        // NB: Overriding the rendered characters affects interaction, as the
//        // rendered characters have different widths to the actual underlying text.
//        setRenderTextProvider((value, limit) -> StringUtils.repeat('\u204E', value.length()));
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
