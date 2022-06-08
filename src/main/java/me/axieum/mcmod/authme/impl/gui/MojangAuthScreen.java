package me.axieum.mcmod.authme.impl.gui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mojang.authlib.exceptions.InvalidCredentialsException;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import me.axieum.mcmod.authme.api.gui.AuthScreen;
import me.axieum.mcmod.authme.api.gui.widget.PasswordFieldWidget;
import me.axieum.mcmod.authme.api.util.MojangUtils;
import me.axieum.mcmod.authme.api.util.SessionUtils;
import static me.axieum.mcmod.authme.impl.AuthMe.CONFIG;
import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;
import static me.axieum.mcmod.authme.impl.AuthMe.getConfig;

/**
 * A screen for handling user authentication via Mojang (or legacy).
 */
public class MojangAuthScreen extends AuthScreen
{
    // The executor to run the login task on
    private ExecutorService executor = null;
    // The completable future for all Mojang login tasks
    private CompletableFuture<Void> task = null;
    // The current progress/status of the login task
    private Text status = null;
    // The username and password text field widgets
    private TextFieldWidget usernameField, passwordField;
    // The login button widget
    private ButtonWidget loginBtn;

    /**
     * Constructs a new authentication via Mojang (or legacy) screen.
     *
     * @param parentScreen  parent (or last) screen that opened this screen
     * @param successScreen screen to be returned to after a successful login
     */
    public MojangAuthScreen(Screen parentScreen, Screen successScreen)
    {
        super(Text.translatable("gui.authme.mojang.title"), parentScreen, successScreen);
        this.enableKeyboardRepeatEvents = true;
        this.closeOnSuccess = true;
    }

    @Override
    protected void init()
    {
        super.init();
        assert client != null;

        // Add a username text field
        addDrawableChild(
            usernameField = new TextFieldWidget(
                client.textRenderer,
                width / 2 - 100, height / 2 - 39, 200, 20,
                Text.translatable("gui.authme.mojang.field.username")
            )
        );
        usernameField.setMaxLength(128);
        if (getConfig().methods.mojang.lastUsername != null) {
            usernameField.setText(getConfig().methods.mojang.lastUsername);
        }
        usernameField.setChangedListener(value -> loginBtn.active = isFormValid());

        // Add a password text field
        addDrawableChild(
            passwordField = new PasswordFieldWidget(
                client.textRenderer,
                width / 2 - 100, height / 2 + 6, 200, 20,
                Text.translatable("gui.authme.mojang.field.password")
            )
        );
        passwordField.setChangedListener(value -> loginBtn.active = isFormValid());

        // Add a login button to submit the form
        addDrawableChild(
            loginBtn = new ButtonWidget(
                width / 2 - 100 - 2, height / 2 + 59, 100, 20,
                Text.translatable("gui.authme.mojang.button.login"),
                button -> login()
            )
        );
        loginBtn.active = isFormValid();

        // Add a cancel button to abort the task
        addDrawableChild(
            new ButtonWidget(
                width / 2 + 2, height / 2 + 59, 100, 20,
                Text.translatable("gui.cancel"),
                button -> close()
            )
        );
    }

    /**
     * Attempts to log into Mojang with the provided credentials.
     */
    public void login()
    {
        assert client != null;

        // Check whether the form is valid
        if (!isFormValid()) return;

        // Disable the form fields while logging in
        usernameField.active = false;
        passwordField.active = false;
        loginBtn.active = false;

        // Set the initial progress/status of the login task
        status = Text.translatable("gui.authme.mojang.status.loggingIn");

        // Prepare a new executor thread to run the login task on
        executor = Executors.newSingleThreadExecutor();

        // Start the login task
        task = MojangUtils
            // Log into Mojang with username and password and hence build a new session
            .login(usernameField.getText(), passwordField.getText(), executor)

            // Update the game session, sync the config and greet the player
            .thenAccept(session -> {
                // Apply the new session
                SessionUtils.setSession(session);
                // Sync configuration with the last used username
                getConfig().methods.mojang.lastUsername = usernameField.getText();
                CONFIG.save();
                // Add a toast that greets the player
                SystemToast.add(
                    client.getToastManager(), SystemToast.Type.TUTORIAL_HINT,
                    Text.translatable("gui.authme.toast.greeting", Text.literal(session.getUsername())), null
                );
                // Mark the task as successful, in turn closing the screen
                LOGGER.info("Successfully logged in via Mojang (or legacy)!");
                success = true;
            })

            // On any exception, update the status and re-enable form fields
            .exceptionally(error -> {
                status = Text.translatable(
                    error.getCause() instanceof InvalidCredentialsException ? "gui.authme.error.credentials"
                                                                            : "gui.authme.error.generic"
                ).formatted(Formatting.RED);
                usernameField.active = true;
                passwordField.active = true;
                return null; // return a default value
            });
    }

    /**
     * Checks whether the form can be submitted, and hence logged in.
     *
     * @return true if both username & password fields are valid
     */
    public boolean isFormValid()
    {
        return !usernameField.getText().isBlank() && !passwordField.getText().isBlank();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        assert client != null;

        // Render the background before any widgets
        renderBackground(matrices);

        // Render a title for the screen
        drawCenteredText(matrices, client.textRenderer, title, width / 2, usernameField.y - 16 - 23, 0xffffff);

        // Render the username & password field labels
        drawTextWithShadow(
            matrices, client.textRenderer, usernameField.getMessage(), usernameField.x, usernameField.y - 16, 0xa0a0a0
        );
        drawTextWithShadow(
            matrices, client.textRenderer, passwordField.getMessage(), passwordField.x, passwordField.y - 16, 0xa0a0a0
        );

        // Render the current progress/status of the login, if present
        if (status != null) {
            drawCenteredText(matrices, client.textRenderer, status, width / 2, loginBtn.y - 20, 0xdddddd);
        }

        // Cascade the rendering
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc()
    {
        return !usernameField.isFocused() && !passwordField.isFocused();
    }

    @Override
    public void close()
    {
        // Cancel the login task if still running
        if (task != null && !task.isDone()) {
            task.cancel(true);
            executor.shutdownNow();
        }

        // Cascade the closing
        super.close();
    }
}
