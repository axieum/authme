package me.axieum.mcmod.authme.api.util;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

import me.axieum.mcmod.authme.mixin.MinecraftClientAccessor;
import me.axieum.mcmod.authme.mixin.RealmsMainScreenAccessor;
import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;

/**
 * Utility methods for interacting with the Microsoft game session.
 */
public final class SessionUtils
{
    // The Mojang authentication service
    private static final YggdrasilAuthenticationService YAS = new YggdrasilAuthenticationService(
        MinecraftClient.getInstance().getNetworkProxy(), UUID.randomUUID().toString()
    );
    // The Mojang user authentication provider
    private static final YggdrasilUserAuthentication YUA = (YggdrasilUserAuthentication)
        YAS.createUserAuthentication(Agent.MINECRAFT);
    // The Mojang Minecraft session service
    private static final YggdrasilMinecraftSessionService YMSS = (YggdrasilMinecraftSessionService)
        YAS.createMinecraftSessionService();

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
        // Use an accessor mixin to update the 'private final' Minecraft session
        ((MinecraftClientAccessor) MinecraftClient.getInstance()).setSession(session);

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
            "invalidtoken",
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
            try {
                LOGGER.info("Verifying Minecraft session...");
                YMSS.joinServer(profile, token, id);
                if (YMSS.hasJoinedServer(profile, id, null).isComplete()) {
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
     * Returns the Yggdrasil Authentication Service.
     *
     * @return Yggdrasil Authentication Service instance
     */
    public static YggdrasilAuthenticationService getAuthService()
    {
        return YAS;
    }

    /**
     * Returns the Yggdrasil User Authentication provider.
     *
     * @return Yggdrasil User Authentication instance
     */
    public static YggdrasilUserAuthentication getAuthProvider()
    {
        return YUA;
    }

    /**
     * Returns the Yggdrasil Minecraft Session Service.
     *
     * @return Yggdrasil Minecraft Session Service instance
     */
    public static YggdrasilMinecraftSessionService getSessionService()
    {
        return YMSS;
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
