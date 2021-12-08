package me.axieum.mcmod.authme.api.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

/**
 * A screen for handling user authentication.
 */
public abstract class AuthScreen extends Screen
{
    // The parent (or last) screen that opened this screen
    protected final Screen parentScreen;
    // The screen to be returned to after a successful login
    protected final Screen successScreen;
    // True if repeat keyboard events should be enabled for this screen
    protected boolean enableKeyboardRepeatEvents = false;
    // True if the login task completed successfully
    protected boolean success = false;
    // True if the screen should be closed on success stat (via render thread)
    protected boolean closeOnSuccess = false;

    /**
     * Constructs a new authentication screen.
     *
     * @param title         screen title
     * @param parentScreen  parent (or last) screen that opened this screen
     * @param successScreen screen to be returned to after a successful login
     */
    public AuthScreen(TranslatableText title, Screen parentScreen, Screen successScreen)
    {
        super(title);
        this.parentScreen = parentScreen;
        this.successScreen = successScreen;
    }

    @Override
    protected void init()
    {
        super.init();
        assert client != null;

        // Optionally enable keyboard repeat events, e.g. allow holding down backspace
        client.keyboard.setRepeatEvents(enableKeyboardRepeatEvents);
    }

    @Override
    public void tick()
    {
        super.tick();

        // Optionally close the screen if the login task completed successfully
        if (success && closeOnSuccess) this.onClose();
    }

    @Override
    public void onClose()
    {
        if (client != null) client.openScreen(success ? successScreen : parentScreen);
    }

    @Override
    public void removed()
    {
        if (enableKeyboardRepeatEvents && client != null) client.keyboard.setRepeatEvents(false);
    }
}
