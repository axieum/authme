package me.axieum.mcmod.authme.api.gui.widget;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import me.axieum.mcmod.authme.api.util.SessionUtils;
import me.axieum.mcmod.authme.api.util.SessionUtils.SessionStatus;

/**
 * The textured button widget for opening the authentication screens.
 */
public class AuthButtonWidget extends ImageButton
{
    /** The screen used to constrain the button movements to. */
    private final @Nullable Screen screen;
    /** Callback for after the button has been dragged. */
    private final @Nullable MoveAction moveAction;
    /** True if the button was just dragged. */
    private boolean didDrag = false;
    /** The last known status of the Minecraft session. */
    private SessionStatus sessionStatus = SessionStatus.UNKNOWN;
    /** The authentication button textures. */
    public static final WidgetSprites BUTTON_TEXTURES = new WidgetSprites(
        ResourceLocation.parse("widget/locked_button"),
        ResourceLocation.parse("widget/locked_button_disabled"),
        ResourceLocation.parse("widget/locked_button_highlighted")
    );
    /** The session status icon texture. */
    public static final ResourceLocation SESSION_STATUS_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "authme", "textures/gui/session_status.png"
    );

    /**
     * Constructs a fixed (no drag) authentication button.
     *
     * @param x           x coordinate
     * @param y           y coordinate
     * @param pressAction on click action
     * @see #AuthButtonWidget(Screen, int, int, OnPress, MoveAction)
     */
    public AuthButtonWidget(int x, int y, OnPress pressAction)
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
     * @see #AuthButtonWidget(Screen, int, int, OnPress, MoveAction, Tooltip, Component)
     */
    public AuthButtonWidget(int x, int y, OnPress pressAction, @Nullable Component message)
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
     * @see #AuthButtonWidget(Screen, int, int, OnPress, MoveAction, Tooltip, Component)
     */
    public AuthButtonWidget(
        int x, int y, OnPress pressAction, @Nullable Tooltip tooltip, @Nullable Component message
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
     * @see #AuthButtonWidget(Screen, int, int, OnPress, MoveAction, Component)
     */
    public AuthButtonWidget(
        @Nullable Screen screen, int x, int y, OnPress pressAction, @Nullable MoveAction moveAction
    )
    {
        this(screen, x, y, pressAction, moveAction, Component.translatable("gui.authme.button.auth"));
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
     * @see #AuthButtonWidget(Screen, int, int, OnPress, MoveAction, Tooltip, Component)
     */
    public AuthButtonWidget(
        @Nullable Screen screen,
        int x,
        int y,
        OnPress pressAction,
        @Nullable MoveAction moveAction,
        @Nullable Component message
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
        OnPress pressAction,
        @Nullable MoveAction moveAction,
        @Nullable Tooltip tooltip,
        Component message
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
            return this.isValidClickButton(button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * Adjusts the default mouse up behaviour to trigger a click.
     *
     * @see ImageButton#mouseClicked(double, double, int)
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
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta)
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

        context.blit(
            SESSION_STATUS_TEXTURE,
            getX() + width - 6,
            getY() - 1,
            u,
            0,
            8,
            8,
            24,
            8
        );
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
        void onMove(Button button);
    }
}
