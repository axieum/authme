package me.axieum.mcmod.authme.api.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import me.axieum.mcmod.authme.api.Config;
import me.axieum.mcmod.authme.api.util.SessionUtils;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * A screen for handling offline user authentication.
 */
public class OfflineAuthScreen extends AuthScreen
{
    // The username and password text field widgets
    private EditBox usernameField;
    // The login button widget
    private Button loginBtn;

    /**
     * Constructs a new offline authentication screen.
     *
     * @param parentScreen  parent (or last) screen that opened this screen
     * @param successScreen screen to be returned to after a successful login
     */
    public OfflineAuthScreen(Screen parentScreen, Screen successScreen)
    {
        super(Component.translatable("gui.authme.offline.title"), parentScreen, successScreen);
        this.closeOnSuccess = true;
    }

    @Override
    protected void init()
    {
        super.init();
        assert minecraft != null;

        // Add a title
        StringWidget titleWidget = addRenderableWidget(new StringWidget(title, font));
        titleWidget.setColor(0xffffff);
        AuthScreen.centerPosition(titleWidget, this, 0, -40);

        // Add a username text field
        addRenderableWidget(
            usernameField = new EditBox(
                font,
                width / 2 - 100, height / 2 - 6, 200, 20,
                Component.translatable("gui.authme.offline.field.username")
            )
        );
        usernameField.setMaxLength(128);
        if (Config.LoginMethods.Offline.lastUsername != null) {
            usernameField.setValue(Config.LoginMethods.Offline.lastUsername);
        }
        usernameField.setResponder(value -> loginBtn.active = isFormValid());

        // Add a label for the username field
        StringWidget labelWidget = addRenderableWidget(new StringWidget(usernameField.getMessage(), font));
        labelWidget.setColor(0xdddddd);
        AuthScreen.centerPosition(labelWidget, this, -51, -17);

        // Add a login button to submit the form
        addRenderableWidget(
            loginBtn = Button.builder(
                Component.translatable("gui.authme.offline.button.login"),
                button -> login()
            ).bounds(
                width / 2 - 100 - 2, height / 2 + 26, 100, 20
            ).build()
        );
        loginBtn.active = isFormValid();

        // Add a cancel button to abort the task
        addRenderableWidget(
            Button.builder(
                Component.translatable("gui.cancel"),
                button -> onClose()
            ).bounds(
                width / 2 + 2, height / 2 + 26, 100, 20
            ).build()
        );
    }

    /**
     * Creates a new offline-mode session the provided username.
     */
    public void login()
    {
        assert minecraft != null;

        // Check whether the form is valid
        if (!isFormValid()) return;

        // Disable the form fields while logging in
        usernameField.active = false;
        loginBtn.active = false;

        // Create and apply a new offline Minecraft session
        SessionUtils.setUser(SessionUtils.offline(usernameField.getValue()));

        // Sync configuration with the last used username
        Config.LoginMethods.Offline.lastUsername = usernameField.getValue();

        // Add a toast that greets the player
        SystemToast.add(
            minecraft.getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
            Component.translatable("gui.authme.toast.greeting", Component.literal(usernameField.getValue())), null
        );

        // Mark the task as successful, in turn closing the screen
        LOGGER.info("Successfully logged in offline-mode!");
        success = true;
    }

    /**
     * Checks whether the form can be submitted, and hence logged in.
     *
     * @return true if the username field is valid
     */
    public boolean isFormValid()
    {
        return !usernameField.getValue().isBlank();
    }
}
