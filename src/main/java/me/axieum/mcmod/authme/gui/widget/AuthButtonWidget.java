package me.axieum.mcmod.authme.gui.widget;

import java.util.function.BiConsumer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AuthButtonWidget extends TexturedButtonWidget
{
    private final BiConsumer<Integer, Integer> onSetPos;
    private final Screen screen;
    private boolean didDrag = false;

    /**
     * Constructs a new Auth Button widget.
     *
     * @param x        initial x position
     * @param y        initial y position
     * @param action   on press consumer
     * @param onSetPos on update position consumer
     * @param text     button text
     * @param screen   screen to constrain the button's position to
     */
    public AuthButtonWidget(int x,
                            int y,
                            PressAction action,
                            BiConsumer<Integer, Integer> onSetPos,
                            Text text,
                            Screen screen)
    {
        super(x, y, 20, 20, 0, 146, 20, new Identifier("minecraft:textures/gui/widgets.png"), 256, 256, action, text);
        this.onSetPos = onSetPos;
        this.screen = screen;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        // Don't handle a button press action here, rather do that on mouse up/release
        // NB: This allows us to initially click the button to start dragging
        return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        // We'll actually handle a mouse clicked here, i.e. press the button
        // But first, we need to check if we just finished dragging
        if (didDrag) this.onSetPos.accept(x, y);
        return didDrag ? didDrag = false
                       : super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        // Move the button with the drag
        this.setPos(Math.min(Math.max(0, (int) mouseX - this.width / 2), this.screen.width - this.width),
                    Math.min(Math.max(0, (int) mouseY - this.width / 2), this.screen.height - this.height));
        this.didDrag = true;

        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }
}
