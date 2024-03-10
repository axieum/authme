package me.axieum.mcmod.authme.api.gui.widget;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import me.axieum.mcmod.authme.api.util.SessionUtils;
import me.axieum.mcmod.authme.api.util.SessionUtils.SessionStatus;

/**
 * The textured button widget for opening the authentication screens.
 */
public class AuthButtonWidget extends TexturedButtonWidget
{
    // The screen used to constrain the button movements to
    private final @Nullable Screen screen;
    // Callback for after the button has been dragged
    private final @Nullable MoveAction moveAction;
    // True if the button was just dragged
    private boolean didDrag = false;
    // The last known status of the Minecraft session
    private SessionStatus sessionStatus = SessionStatus.UNKNOWN;

    // The authentication button textures
    public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
        new Identifier("widget/locked_button"),
        new Identifier("widget/locked_button_disabled"),
        new Identifier("widget/locked_button_highlighted")
    );
    // The session status icon texture
    public static final Identifier SESSION_STATUS_TEXTURE = new Identifier("authme", "textures/gui/session_status.png");

    /**
     * Constructs a fixed (no drag) authentication button.
     *
     * @param x           x coordinate
     * @param y           y coordinate
     * @param pressAction on click action
     * @see #AuthButtonWidget(Screen, int, int, PressAction, MoveAction)
     */
    public AuthButtonWidget(int x, int y, PressAction pressAction)
    {
        this(null, x, y, pressAction, null);
    }

    /**
     * Constructs a fixed (no drag) authentication button with non-visible text.
     *
     * @param x           x coordinate
     * @param y           y coordinate
     * @param pressAction on click action
     * @param message     non-visible button text
     * @see #AuthButtonWidget(Screen, int, int, PressAction, MoveAction, Tooltip, Text)
     */
    public AuthButtonWidget(int x, int y, PressAction pressAction, @Nullable Text message)
    {
        this(null, x, y, pressAction, null, null, message);
    }

    /**
     * Constructs a fixed (no drag) authentication button with non-visible text and a tooltip.
     *
     * @param x               x coordinate
     * @param y               y coordinate
     * @param pressAction     on click action
     * @param tooltip         tooltip
     * @param message         non-visible button text
     * @see #AuthButtonWidget(Screen, int, int, PressAction, MoveAction, Tooltip, Text)
     */
    public AuthButtonWidget(
        int x, int y, PressAction pressAction, @Nullable Tooltip tooltip, @Nullable Text message
    )
    {
        this(null, x, y, pressAction, null, tooltip, message);
    }

    /**
     * Constructs a movable (with drag) authentication button.
     *
     * @param screen      screen to constrain movement to
     * @param x           initial x coordinate
     * @param y           initial y coordinate
     * @param pressAction on click action
     * @param moveAction  on move action
     * @see #AuthButtonWidget(Screen, int, int, PressAction, MoveAction, Text)
     */
    public AuthButtonWidget(
        @Nullable Screen screen, int x, int y, PressAction pressAction, @Nullable MoveAction moveAction
    )
    {
        this(screen, x, y, pressAction, moveAction, Text.translatable("gui.authme.button.auth"));
    }

    /**
     * Constructs a movable (with drag) authentication button with non-visible text.
     *
     * @param screen      screen to constrain movement to
     * @param x           initial x coordinate
     * @param y           initial y coordinate
     * @param pressAction on click action
     * @param moveAction  on move action
     * @param message     non-visible button text
     * @see #AuthButtonWidget(Screen, int, int, PressAction, MoveAction, Tooltip, Text)
     */
    public AuthButtonWidget(
        @Nullable Screen screen,
        int x,
        int y,
        PressAction pressAction,
        @Nullable MoveAction moveAction,
        @Nullable Text message
    )
    {
        this(screen, x, y, pressAction, moveAction, null, message);
    }

    /**
     * Constructs a movable (with drag) authentication button with non-visible text and a tooltip.
     *
     * @param screen          screen to constrain movement to
     * @param x               initial x coordinate
     * @param y               initial y coordinate
     * @param pressAction     on click action
     * @param moveAction      on move action
     * @param tooltip         tooltip
     * @param message         non-visible button text
     */
    public AuthButtonWidget(
        @Nullable Screen screen,
        int x,
        int y,
        PressAction pressAction,
        @Nullable MoveAction moveAction,
        @Nullable Tooltip tooltip,
        Text message
    )
    {
        super(x, y, 20, 20, BUTTON_TEXTURES, pressAction, message);
        this.screen = screen;
        this.moveAction = moveAction;
        this.setTooltip(tooltip);
        refreshSessionStatus();
    }

    /**
     * Returns the last known Minecraft session status.
     *
     * @return cached Minecraft session status
     */
    public SessionStatus getSessionStatus()
    {
        return sessionStatus;
    }

    /**
     * Refreshes the Minecraft session status.
     */
    public void refreshSessionStatus()
    {
        sessionStatus = SessionStatus.UNKNOWN;
        SessionUtils.getStatus().thenAccept(status -> sessionStatus = status);
    }

    /**
     * Sets the Minecraft session status.
     *
     * @param sessionStatus Minecraft session status
     */
    public void setSessionStatus(SessionStatus sessionStatus)
    {
        this.sessionStatus = sessionStatus;
    }

    /**
     * Adjusts the default mouse down behaviour to not trigger a click - rather
     * handle clicks on mouse up/release. This allows the user to initially
     * click to begin dragging the button to a new position.
     *
     * @see #mouseReleased for super invocation
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.moveAction != null) {
            return this.isValidClickButton(button) && this.clicked(mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Adjusts the default mouse up behaviour to trigger a click.
     *
     * @see super#mouseClicked for mouse click implementation
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.moveAction == null) return super.mouseReleased(mouseX, mouseY, button);
        if (!this.isValidClickButton(button)) return false;
        // Check if the user just finished dragging
        if (this.didDrag) {
            // Invoke any callbacks and reset the dragging status
            this.moveAction.onMove(this);
            return this.didDrag = false;
        }
        // Else, continue as a button press
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Allows the button to be moved while dragging.
     *
     * @see #mouseReleased for where dragging is finalised
     */
    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        if (this.moveAction != null) {
            // Flag the widget as in a dragging state
            this.didDrag = true;

            // Move the button with the drag, constraining within the screen's bounds
            if (this.screen != null) {
                this.setPosition(
                    Math.min(Math.max(0, (int) mouseX - this.width / 2), this.screen.width - this.width),
                    Math.min(Math.max(0, (int) mouseY - this.height / 2), this.screen.height - this.height)
                );
            } else {
                this.setPosition((int) mouseX - this.width / 2, (int) mouseY - this.height / 2);
            }
        }

        super.onDrag(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
    {
        // Cascade the rendering
        super.renderWidget(context, mouseX, mouseY, delta);

        // Render the current session status
        final int u;
        switch (sessionStatus) {
            case VALID -> u = 0;
            case OFFLINE -> u = 8;
            default -> u = 16;
        }
        context.drawTexture(SESSION_STATUS_TEXTURE, getX() + width - 6, getY() - 1, u, 0, 8, 8, 24, 8);
    }

    /**
     * A functional interface for widget relocation callback.
     */
    @FunctionalInterface
    public interface MoveAction
    {
        /**
         * Callback for when the widget has been dragged elsewhere.
         *
         * @param button button that was moved
         */
        void onMove(ButtonWidget button);
    }
}
