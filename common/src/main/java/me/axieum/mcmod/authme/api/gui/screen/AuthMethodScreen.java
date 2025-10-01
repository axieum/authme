package me.axieum.mcmod.authme.api.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import me.axieum.mcmod.authme.api.AuthMe;
import me.axieum.mcmod.authme.api.Config;
import me.axieum.mcmod.authme.api.util.SessionUtils;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * The authentication method selection screen.
 */
public class AuthMethodScreen extends Screen
{
    /** The parent (or last) screen that opened this screen. */
    private final Screen parentScreen;
    /** The 'Microsoft' authentication method button textures. */
    public static final WidgetSprites MICROSOFT_BUTTON_TEXTURES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath("authme", "widget/microsoft_button"),
        ResourceLocation.fromNamespaceAndPath("authme", "widget/microsoft_button_disabled"),
        ResourceLocation.fromNamespaceAndPath("authme", "widget/microsoft_button_focused")
    );
    /** The 'Mojang (or legacy)' authentication method button textures. */
    public static final WidgetSprites MOJANG_BUTTON_TEXTURES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath("authme", "widget/mojang_button"),
        ResourceLocation.fromNamespaceAndPath("authme", "widget/mojang_button_disabled"),
        ResourceLocation.fromNamespaceAndPath("authme", "widget/mojang_button_focused")
    );
    /** The 'Offline' authentication method button textures. */
    public static final WidgetSprites OFFLINE_BUTTON_TEXTURES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath("authme", "widget/offline_button"),
        ResourceLocation.fromNamespaceAndPath("authme", "widget/offline_button_disabled"),
        ResourceLocation.fromNamespaceAndPath("authme", "widget/offline_button_focused")
    );

    /**
     * Constructs a new authentication method choice screen.
     *
     * @param parentScreen parent (or last) screen that opened this screen
     */
    public AuthMethodScreen(Screen parentScreen)
    {
        super(Component.translatable("gui.authme.method.title"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init()
    {
        super.init();
        assert minecraft != null;

        // Add a title
        StringWidget titleWidget = addRenderableWidget(new StringWidget(title, font));
        titleWidget.setColor(0xffffff);
        titleWidget.setPosition(width / 2 - titleWidget.getWidth() / 2, height / 2 - titleWidget.getHeight() / 2 - 22);

        // Add a greeting message
        StringWidget greetingWidget = addRenderableWidget(new StringWidget(
            Component.translatable(
                "gui.authme.method.greeting",
                Component.literal(SessionUtils.getUser().getName()).withStyle(ChatFormatting.YELLOW)
            ),
            font
        ));
        greetingWidget.setColor(0xa0a0a0);
        greetingWidget.setPosition(
            width / 2 - greetingWidget.getWidth() / 2, height / 2 - greetingWidget.getHeight() / 2 - 42
        );

        // Add a button for the 'Microsoft' authentication method
        ImageButton msButton = new ImageButton(
            width / 2 - 20 - 10 - 4, height / 2 - 5, 20, 20,
            MICROSOFT_BUTTON_TEXTURES,
            button -> {
                // If 'Left Control' is being held, enforce user interaction
                final boolean selectAccount = InputConstants.isKeyDown(
                    minecraft.getWindow(), InputConstants.KEY_LCONTROL
                );
                if (Config.LoginMethods.Microsoft.isDefaults()) {
                    minecraft.setScreen(new MicrosoftAuthScreen(this, parentScreen, selectAccount));
                } else {
                    LOGGER.warn("Non-default Microsoft authentication URLs are in use!");
                    ConfirmScreen confirmScreen = new ConfirmScreen(
                        a -> minecraft.setScreen(a ? new MicrosoftAuthScreen(this, parentScreen, selectAccount) : this),
                        Component.translatable("gui.authme.microsoft.warning.title"),
                        Component.translatable("gui.authme.microsoft.warning.body"),
                        Component.translatable("gui.authme.microsoft.warning.accept"),
                        Component.translatable("gui.authme.microsoft.warning.cancel")
                    );
                    minecraft.setScreen(confirmScreen);
                    confirmScreen.setDelay(40);
                }
            },
            Component.translatable("gui.authme.method.button.microsoft")
        );
        msButton.setTooltip(Tooltip.create(
            Component.translatable("gui.authme.method.button.microsoft")
                .append("\n")
                .append(
                    Component.translatable("gui.authme.method.button.microsoft.selectAccount")
                        .withStyle(ChatFormatting.GRAY)
                )
        ));
        addRenderableWidget(msButton);

        // Add a button for the 'Mojang (or legacy)' authentication method
        ImageButton mojangButton = new ImageButton(
            width / 2 - 10, height / 2 - 5, 20, 20,
            MOJANG_BUTTON_TEXTURES,
            ConfirmLinkScreen.confirmLink(this, AuthMe.MOJANG_ACCOUNT_MIGRATION_FAQ_URL),
            Component.translatable("gui.authme.method.button.mojang")
        );
        mojangButton.setTooltip(Tooltip.create(
            Component.translatable("gui.authme.method.button.mojang")
                .append("\n")
                .append(Component.translatable("gui.authme.method.button.mojang.unavailable")
                    .withStyle(ChatFormatting.GRAY))
        ));
        addRenderableWidget(mojangButton);

        // Add a button for the 'Offline' authentication method
        ImageButton offlineButton = new ImageButton(
            width / 2 + 10 + 4, height / 2 - 5, 20, 20,
            OFFLINE_BUTTON_TEXTURES,
            button -> minecraft.setScreen(new OfflineAuthScreen(this, parentScreen)),
            Component.translatable("gui.authme.method.button.offline")
        );
        offlineButton.setTooltip(Tooltip.create(Component.translatable("gui.authme.method.button.offline")));
        addRenderableWidget(offlineButton);

        // Add a button to go back
        addRenderableWidget(
            Button.builder(Component.translatable("gui.back"), button -> onClose())
                .bounds(width / 2 - 50, height / 2 + 27, 100, 20)
                .build()
        );
    }

    @Override
    public void onClose()
    {
        if (minecraft != null) minecraft.setScreen(parentScreen);
    }
}
