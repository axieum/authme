package me.axieum.mcmod.authme.util;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;
import me.axieum.mcmod.authme.api.Status;
import me.axieum.mcmod.authme.mixin.MinecraftClientAccess;
import me.axieum.mcmod.authme.mixin.RealmsMainScreenAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Session.AccountType;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static me.axieum.mcmod.authme.AuthMe.LOGGER;

public class SessionUtil
{
    // Session status cache
    public static final long STATUS_TTL = 60000L;
    private static Status lastStatus = Status.UNKNOWN;
    private static long lastStatusCheck;

    /**
     * Authentication Services.
     */
    private static final YggdrasilAuthenticationService yas;
    private static final YggdrasilUserAuthentication yua;
    private static final YggdrasilMinecraftSessionService ymss;

    static {
        yas = new YggdrasilAuthenticationService(MinecraftClient.getInstance().getNetworkProxy(),
                                                 UUID.randomUUID().toString());
        yua = (YggdrasilUserAuthentication) yas.createUserAuthentication(Agent.MINECRAFT);
        ymss = (YggdrasilMinecraftSessionService) yas.createMinecraftSessionService();
    }

    /**
     * Returns the current session.
     *
     * @return current session instance
     */
    public static Session getSession()
    {
        return MinecraftClient.getInstance().getSession();
    }

    /**
     * Checks and returns a completable future for the current session status.
     * NB: This is an expensive task as it involves connecting to servers to
     * validate the stored tokens, and hence is executed on a separate thread.
     * The response is cached for ~1 minutes.
     *
     * @return completable future for the status (async)
     */
    public static CompletableFuture<Status> getStatus()
    {
        if (System.currentTimeMillis() - lastStatusCheck < STATUS_TTL)
            return CompletableFuture.completedFuture(lastStatus);

        return CompletableFuture.supplyAsync(() -> {
            final Session session = getSession();

            GameProfile profile = session.getProfile();
            String token = session.getAccessToken();
            String id = UUID.randomUUID().toString();

            try {
                ymss.joinServer(profile, token, id);
                if (ymss.hasJoinedServer(profile, id, null).isComplete()) {
                    LOGGER.info("Session is valid");
                    lastStatus = Status.VALID;
                } else {
                    LOGGER.warn("Session is invalid!");
                    lastStatus = Status.INVALID;
                }
            } catch (AuthenticationException e) {
                LOGGER.warn("Unable to validate the session: {}", e.getMessage());
                lastStatus = Status.INVALID;
            }

            lastStatusCheck = System.currentTimeMillis();
            return lastStatus;
        });
    }

    /**
     * Attempts to login and set a new session for the current Minecraft instance.
     *
     * @param username Minecraft account username
     * @param password Minecraft account password
     * @return completable future for the new session
     */
    public static CompletableFuture<Session> login(String username, String password)
    {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LOGGER.info("Logging into a new session with username");

                // Set credentials and login
                yua.setUsername(username);
                yua.setPassword(password);
                yua.logIn();

                // Fetch useful session data
                final String name = yua.getSelectedProfile().getName();
                final String uuid = UUIDTypeAdapter.fromUUID(yua.getSelectedProfile().getId());
                final String token = yua.getAuthenticatedToken();
                final String type = yua.getUserType().getName();

                // Logout after fetching what is needed
                yua.logOut();

                // Persist the new session to the Minecraft instance
                final Session session = new Session(name, uuid, token, type);
                setSession(session);

                LOGGER.info("Session login successful");
                return session;
            } catch (Exception e) {
                LOGGER.error("Session login failed: {}", e.getMessage());
                throw new CompletionException(e);
            }
        });
    }

    /**
     * Mocks a login, setting the desired username on the session.
     * NB: Useful for offline play.
     *
     * @param username desired username
     * @return new session if success, else old session
     */
    public static Session login(String username)
    {
        try {
            UUID uuid = UUID.nameUUIDFromBytes(("offline:" + username).getBytes());

            final Session session = new Session(username, uuid.toString(), "invalidtoken", AccountType.LEGACY.name());
            setSession(session);

            LOGGER.info("Session login (offline) successful");
            return session;
        } catch (Exception e) {
            LOGGER.error("Session login (offline) failed: {}", e.getMessage());
            return SessionUtil.getSession();
        }
    }

    /**
     * Replaces the session on the Minecraft instance.
     *
     * @param session new session with updated properties
     * @see MinecraftClientAccess
     */
    private static void setSession(Session session)
    {
        // NB: Minecraft#session is a final property - use mixin accessor
        ((MinecraftClientAccess) MinecraftClient.getInstance()).setSession(session);

        // Necessary for Realms to re-check for a valid session
        RealmsMainScreenAccess.setCheckedClientCompatibility(false);
        RealmsMainScreenAccess.setRealmsGenericErrorScreen(null);

        // Cached status is now stale
        lastStatus = Status.UNKNOWN;
        lastStatusCheck = 0;
    }
}
