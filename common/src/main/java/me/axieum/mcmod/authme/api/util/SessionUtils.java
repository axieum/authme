package me.axieum.mcmod.authme.api.util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.gui.RealmsDataFetcher;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;

import me.axieum.mcmod.authme.mixin.MinecraftAccessor;
import me.axieum.mcmod.authme.mixin.RealmsAvailabilityAccessor;
import me.axieum.mcmod.authme.mixin.ReportingContextAccessor;
import me.axieum.mcmod.authme.mixin.SplashManagerAccessor;
import static me.axieum.mcmod.authme.api.AuthMe.LOGGER;

/**
 * Utility methods for interacting with the Microsoft game session.
 */
public final class SessionUtils
{
    /** The access token used for offline sessions. */
    public static final String OFFLINE_TOKEN = "invalidtoken";
    /** The number of milliseconds that a session status is cached for. */
    public static final long STATUS_TTL = 60_000L; // 60s
    /** The time of the last session status check (milliseconds since epoch). */
    private static long lastStatusCheck;
    /** The last session status value. */
    private static SessionStatus lastStatus = SessionStatus.UNKNOWN;

    private SessionUtils() {}

    /**
     * Returns the current Minecraft user.
     *
     * @return current Minecraft user instance
     */
    public static User getUser()
    {
        return Minecraft.getInstance().getUser();
    }

    /**
     * Replaces the Minecraft user instance.
     *
     * @param user new Minecraft user
     */
    public static void setUser(User user)
    {
        final Minecraft client = Minecraft.getInstance();

        // Use an accessor mixin to update the 'private final' Minecraft session
        ((MinecraftAccessor) client).setUser(user);
        ((SplashManagerAccessor) client.getSplashManager()).setUser(user);

        // Re-create the game profile future
        ((MinecraftAccessor) client).setProfileFuture(
            CompletableFuture.supplyAsync(
                () -> client.services().sessionService().fetchProfile(user.getProfileId(), true),
                Util.nonCriticalIoPool()
            )
        );

        // Re-create the user API service (ignore offline session)
        UserApiService userApiService = UserApiService.OFFLINE;
        if (!OFFLINE_TOKEN.equals(user.getAccessToken())) {
            userApiService = getAuthService().createUserApiService(user.getAccessToken());
        }
        ((MinecraftAccessor) client).setUserApiService(userApiService);

        // Re-create the social interactions manager
        ((MinecraftAccessor) client).setPlayerSocialManager(
            new PlayerSocialManager(client, userApiService)
        );

        // Re-create the profile keys
        ((MinecraftAccessor) client).setProfileKeyPairManager(
            ProfileKeyPairManager.create(userApiService, user, client.gameDirectory.toPath())
        );

        // Re-create the abuse report context
        ((MinecraftAccessor) client).setReportingContext(
            ReportingContext.create(
                ((ReportingContextAccessor) (Object) client.getReportingContext()).getEnvironment(),
                userApiService
            )
        );

        // Necessary for Realms to re-check for a valid session
        synchronized (RealmsClient.class) {
            RealmsClient realmsClient = new RealmsClient(user.getSessionId(), user.getName(), client);
            RealmsClient.realmsClientInstance = realmsClient;
            ((MinecraftAccessor) client).setRealmsDataFetcher(new RealmsDataFetcher(realmsClient));
            RealmsAvailabilityAccessor.setFuture(null);
        }

        // The cached status is now stale
        lastStatus = SessionStatus.UNKNOWN;
        lastStatusCheck = 0;

        LOGGER.info(
            "Minecraft session for {} (uuid={}) has been applied", user.getName(), user.getProfileId()
        );
    }

    /**
     * Builds and returns a new offline Minecraft session.
     *
     * @param username custom username
     * @return a new offline Minecraft session
     * @see #setUser(User)  to apply the new session
     */
    public static User offline(String username)
    {
        return new User(
            username,
            UUID.nameUUIDFromBytes(("offline:" + username).getBytes()),
            OFFLINE_TOKEN,
            Optional.empty(),
            Optional.empty()
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
            final User session = getUser();
            final String serverId = UUID.randomUUID().toString();

            // Attempt to join the Minecraft Session Service server
            final MinecraftSessionService sessionService = getSessionService();
            try {
                LOGGER.info("Verifying Minecraft session...");
                sessionService.joinServer(session.getProfileId(), session.getAccessToken(), serverId);
                if (sessionService.hasJoinedServer(session.getName(), serverId, null) != null) {
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
    public static MinecraftSessionService getSessionService()
    {
        return Minecraft.getInstance().services().sessionService();
    }

    /**
     * Returns the Yggdrasil Authentication Service.
     *
     * @return Yggdrasil Authentication Service instance
     */
    public static YggdrasilAuthenticationService getAuthService()
    {
        return Minecraft.getInstance().authme$getAuthService();
    }

    /**
     * The status of a Minecraft session.
     *
     * @see #getStatus() for the current session status
     */
    public enum SessionStatus
    {
        /** The session is valid. */
        VALID,
        /** The session is invalid. */
        INVALID,
        /** The session is offline. */
        OFFLINE,
        /** The session is undetermined. */
        UNKNOWN,
    }
}
