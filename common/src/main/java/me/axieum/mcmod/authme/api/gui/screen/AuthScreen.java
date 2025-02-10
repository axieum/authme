package me.axieum.mcmod.authme.api.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A screen for handling user authentication.
 */
public abstract class AuthScreen extends Screen
{
    /** The parent (or last) screen that opened this screen. */
    protected final Screen parentScreen;
    /** The screen to be returned to after a successful login. */
    protected final Screen successScreen;
    /** True if the login task completed successfully. */
    protected boolean success = false;
    /** True if the screen should be closed on success stat (via render thread). */
    protected boolean closeOnSuccess = false;

    /**
     * Constructs a new authentication screen.
     *
     * @param title         screen title
     * @param parentScreen  parent (or last) screen that opened this screen
     * @param successScreen screen to be returned to after a successful login
     */
    public AuthScreen(Component title, Screen parentScreen, Screen successScreen)
    {
        super(title);
        this.parentScreen = parentScreen;
        this.successScreen = successScreen;
    }

    @Override
    protected void init()
    {
        super.init();
        assert minecraft != null;
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
        if (minecraft != null) minecraft.setScreen(success ? successScreen : parentScreen);
    }
}
