package me.axieum.mcmod.authme.api.util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;

import me.axieum.mcmod.authme.mixin.MinecraftClientAccessor;
import me.axieum.mcmod.authme.mixin.RealmsMainScreenAccessor;
import me.axieum.mcmod.authme.mixin.YggdrasilAuthenticationServiceAccessor;
import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;

/**
 * Utility methods for interacting with the Microsoft game session.
 */
public final class SessionUtils
{
    // The access token used for offline sessions
    public static final String OFFLINE_TOKEN = "invalidtoken";
    // The number of milliseconds that a session status is cached for
    public static final long STATUS_TTL = 60_000L; // 60s
    // The time of the last session status check (milliseconds since epoch)
    private static long lastStatusCheck;
    // The last session status value
    private static SessionStatus lastStatus = SessionStatus.UNKNOWN;

    private SessionUtils() {}

    /**
     * Returns the current Minecraft session.
     *
     * @return current Minecraft session instance
     */
    public static Session getSession()
    {
        return MinecraftClient.getInstance().getSession();
    }

    /**
     * Replaces the Minecraft session instance.
     *
     * @param session new Minecraft session
     */
    public static void setSession(Session session)
    {
        final MinecraftClient client = MinecraftClient.getInstance();

        // Use an accessor mixin to update the 'private final' Minecraft session
        ((MinecraftClientAccessor) client).setSession(session);

        // Refresh the session properties
        client.getSessionProperties().clear();
        client.getSessionProperties();

        // Re-create the user API service (ignore offline session)
        UserApiService userApiService = UserApiService.OFFLINE;
        if (!OFFLINE_TOKEN.equals(session.getAccessToken())) {
            try {
                userApiService = getAuthService().createUserApiService(session.getAccessToken());
            } catch (AuthenticationException e) {
                LOGGER.error("Failed to verify authentication for new user API service!", e);
            }
        }
        ((MinecraftClientAccessor) client).setUserApiService(userApiService);

        // Re-create the social interactions manager
        ((MinecraftClientAccessor) client).setSocialInteractionsManager(
            new SocialInteractionsManager(client, userApiService)
        );

        // Re-create the profile keys
        ((MinecraftClientAccessor) client).setProfileKeys(
            new ProfileKeys(userApiService, session.getProfile().getId(), client.runDirectory.toPath())
        );

        // Re-create the abuse report context
        ((MinecraftClientAccessor) client).setAbuseReportContext(
            AbuseReportContext.create(client.getAbuseReportContext().environment(), userApiService)
        );

        // Necessary for Realms to re-check for a valid session
        RealmsMainScreenAccessor.setCheckedClientCompatibility(false);
        RealmsMainScreenAccessor.setRealmsGenericErrorScreen(null);

        // The cached status is now stale
        lastStatus = SessionStatus.UNKNOWN;
        lastStatusCheck = 0;

        LOGGER.info("Minecraft session for {} (uuid={}) has been applied", session.getUsername(), session.getUuid());
    }

    /**
     * Builds and returns a new offline Minecraft session.
     *
     * @param username custom username
     * @return a new offline Minecraft session
     * @see #setSession(Session) to apply the new session
     */
    public static Session offline(String username)
    {
        return new Session(
            username,
            UUID.nameUUIDFromBytes(("offline:" + username).getBytes()).toString(),
            OFFLINE_TOKEN,
            Optional.empty(),
            Optional.empty(),
            Session.AccountType.LEGACY
        );
    }

    /**
     * Checks and returns the current Minecraft session status.
     *
     * <p>NB: This is an expensive task as it involves connecting to servers to
     * validate any access tokens, and hence is executed on a separate thread.
     *
     * <p>The session status is cached for about 1 minute for subsequent calls.
     *
     * @return a completable future for the Minecraft session status
     */
    public static CompletableFuture<SessionStatus> getStatus()
    {
        // Check if the status has already been checked recently
        if (System.currentTimeMillis() - lastStatusCheck < STATUS_TTL) {
            return CompletableFuture.completedFuture(lastStatus);
        }

        // Otherwise, return an asynchronous action to check the session status
        return CompletableFuture.supplyAsync(() -> {
            // Fetch the current session
            final Session session = getSession();
            final GameProfile profile = session.getProfile();
            final String token = session.getAccessToken();
            final String id = UUID.randomUUID().toString();

            // Attempt to join the Minecraft Session Service server
            final YggdrasilMinecraftSessionService sessionService = getSessionService();
            try {
                LOGGER.info("Verifying Minecraft session...");
                sessionService.joinServer(profile, token, id);
                if (sessionService.hasJoinedServer(profile, id, null).isComplete()) {
                    LOGGER.info("The Minecraft session is valid");
                    lastStatus = SessionStatus.VALID;
                } else {
                    LOGGER.warn("The Minecraft session is invalid!");
                    lastStatus = SessionStatus.INVALID;
                }
            } catch (AuthenticationException e) {
                LOGGER.error("Could not validate the Minecraft session!", e);
                lastStatus = SessionStatus.OFFLINE;
            }

            // Update the last status check and return
            lastStatusCheck = System.currentTimeMillis();
            return lastStatus;
        });
    }

    /**
     * Returns the Yggdrasil Minecraft Session Service.
     *
     * @return Yggdrasil Minecraft Session Service instance
     */
    public static YggdrasilMinecraftSessionService getSessionService()
    {
        return (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
    }

    /**
     * Returns the Yggdrasil Authentication Service.
     *
     * @return Yggdrasil Authentication Service instance
     */
    public static YggdrasilAuthenticationService getAuthService()
    {
        final YggdrasilAuthenticationService authService = getSessionService().getAuthenticationService();

        // Provide a random client token if not set
        if (((YggdrasilAuthenticationServiceAccessor) authService).getClientToken() == null) {
            ((YggdrasilAuthenticationServiceAccessor) authService).setClientToken(UUID.randomUUID().toString());
        }

        return authService;
    }

    /**
     * Returns the Yggdrasil User Authentication provider.
     *
     * @return Yggdrasil User Authentication instance
     */
    public static YggdrasilUserAuthentication getAuthProvider()
    {
        return (YggdrasilUserAuthentication) getAuthService().createUserAuthentication(Agent.MINECRAFT);
    }

    /**
     * The status of a Minecraft session.
     *
     * @see #getStatus() for the current session status
     */
    public enum SessionStatus
    {
        VALID, INVALID, OFFLINE, UNKNOWN
    }
}
