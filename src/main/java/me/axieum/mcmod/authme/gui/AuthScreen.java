package me.axieum.mcmod.authme.gui;

import com.mojang.authlib.exceptions.InvalidCredentialsException;
import me.axieum.mcmod.authme.gui.widget.PasswordFieldWidget;
import me.axieum.mcmod.authme.util.SessionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Session;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class AuthScreen extends Screen
{
    private final Screen parentScreen;

    private TextFieldWidget usernameField, passwordField;
    private ButtonWidget loginButton, cancelButton;
    private Text greeting, message;
    private String lastUsername;

    public AuthScreen(Screen parentScreen)
    {
        super(new TranslatableText("gui.authme.auth.title"));
        this.parentScreen = parentScreen;
        minecraft = MinecraftClient.getInstance();
        lastUsername = SessionUtil.getSession().getUsername();
        greeting = getGreeting(lastUsername);
    }

    @Override
    protected void init()
    {
        super.init();
        minecraft.keyboard.enableRepeatEvents(true);

        // Username Text Field
        usernameField = new TextFieldWidget(font,
                                            width / 2 - 100,
                                            76,
                                            200,
                                            20,
                                            I18n.translate("gui.authme.auth.field.username"));
        usernameField.setMaxLength(128);
        usernameField.setSuggestion(lastUsername); // Suggest their current username
        usernameField.setChangedListener(value -> {
            // Clear username suggestion if they're typing something else
            usernameField.setSuggestion(value.isEmpty() ? lastUsername : "");
            // Update the login button submission state
            loginButton.active = canSubmit();
        });
        children.add(usernameField);

        // Password Text Field
        passwordField = new PasswordFieldWidget(font,
                                                width / 2 - 100,
                                                116,
                                                200,
                                                20,
                                                I18n.translate("gui.authme.auth.field.password"));
        passwordField.changeFocus(true); // Focus password initially (as we've already suggested a username)
        passwordField.setChangedListener(value -> {
            // Tweak the login button depending on if password is given or not
            loginButton.setMessage(I18n.translate("gui.authme.auth.button.login."
                                                  + (value.isEmpty() ? "offline" : "online")));
            loginButton.active = canSubmit();
            // Reset the cancel button accordingly (after a successful login)
            cancelButton.setMessage(I18n.translate("gui.authme.auth.button.cancel"));
        });
        children.add(passwordField);

        // Login Button
        loginButton = new ButtonWidget(width / 2 - 100,
                                       height / 4 + 96 + 18,
                                       200,
                                       20,
                                       I18n.translate("gui.authme.auth.button.login.offline"),
                                       button -> submit());
        loginButton.active = false;
        addButton(loginButton);

        // Cancel Button
        cancelButton = new ButtonWidget(width / 2 - 100,
                                        height / 4 + 120 + 18,
                                        200,
                                        20,
                                        I18n.translate("gui.authme.auth.button.cancel"),
                                        button -> onClose());
        addButton(cancelButton);
    }

    @Override
    public boolean shouldCloseOnEsc()
    {
        return !usernameField.isFocused() && !passwordField.isFocused();
    }

    @Override
    public void onClose()
    {
        passwordField.setText("");
        minecraft.openScreen(parentScreen);
    }

    @Override
    public void removed()
    {
        minecraft.keyboard.enableRepeatEvents(false);
    }

    /**
     * Sets the flash status message.
     *
     * @param message text component to show
     */
    public void setMessage(Text message)
    {
        this.message = message;
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        renderBackground();

        drawCenteredString(font, title.asFormattedString(), width / 2, 17, 16777215);
        drawCenteredString(font, greeting.asFormattedString(), width / 2, 34, 16777215);

        if (message != null)
            drawCenteredString(font, message.asFormattedString(), width / 2, height / 4 + 86, 16777215);

        drawString(font, I18n.translate("gui.authme.auth.field.username"), width / 2 - 100, 64, 10526880);
        drawString(font, I18n.translate("gui.authme.auth.field.password"), width / 2 - 100, 104, 10526880);

        usernameField.render(mouseX, mouseY, delta);
        passwordField.render(mouseX, mouseY, delta);

        super.render(mouseX, mouseY, delta);
    }

    /**
     * Determines if the current form can be submitted.
     *
     * @return true if the form is ready for submission
     */
    protected boolean canSubmit()
    {
        return !usernameField.getText().isEmpty() || !passwordField.getText().isEmpty();
    }

    /**
     * Submits the current form, logging the credentials in.
     */
    public void submit()
    {
        // Prevent pre-mature submissions
        if (!loginButton.active) return;
        loginButton.active = false; // disable login button while logging in

        final String username = usernameField.getText().isEmpty() ? lastUsername : usernameField.getText();
        final String password = passwordField.getText();

        if (password.isEmpty()) {
            // Play offline
            Session offlineSession = SessionUtil.login(username);

            lastUsername = offlineSession.getUsername();
            greeting = getGreeting(lastUsername);
            message = new TranslatableText("gui.authme.auth.message.success.offline")
                    .setStyle(new Style().setBold(true).setColor(Formatting.AQUA));

            // Reset form
            usernameField.setText("");
            passwordField.setText("");
            cancelButton.setMessage(I18n.translate("gui.authme.auth.button.return"));
        } else {
            // Login
            SessionUtil.login(username, password)
                       .thenAccept(session -> {
                           // Successful login attempt
                           lastUsername = session.getUsername();
                           greeting = getGreeting(lastUsername);

                           // Set the message contents and style it as successful
                           message = new TranslatableText("gui.authme.auth.message.success")
                                   .setStyle(new Style().setBold(true).setColor(Formatting.GREEN));

                           // Reset form
                           usernameField.setText("");
                           passwordField.setText("");
                           cancelButton.setMessage(I18n.translate("gui.authme.auth.button.return"));
                       })
                       .exceptionally(e -> {
                           // Failed login attempt
                           loginButton.active = true; // re-enable login button to try again with same credentials

                           // Set the message contents and style it as an error
                           if (e.getCause() instanceof InvalidCredentialsException)
                               message = new TranslatableText("gui.authme.auth.message.failed.credentials");
                           else
                               message = new TranslatableText("gui.authme.auth.message.failed.generic",
                                                              e.getCause().getMessage());

                           message.setStyle(new Style().setBold(true).setColor(Formatting.RED));

                           return null;
                       });
        }
    }

    /**
     * Formats and returns a greeting text component.
     *
     * @param username username in greeting
     * @return formatted translatable text component greeting
     */
    protected static Text getGreeting(String username)
    {
        return new TranslatableText("gui.authme.auth.greeting",
                                    new LiteralText(username).setStyle(new Style().setColor(Formatting.YELLOW)))
                .setStyle(new Style().setColor(Formatting.GRAY));
    }
}
