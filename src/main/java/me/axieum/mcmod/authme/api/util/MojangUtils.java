package me.axieum.mcmod.authme.api.util;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.util.UUIDTypeAdapter;

import net.minecraft.client.util.Session;

import static me.axieum.mcmod.authme.impl.AuthMe.LOGGER;

/**
 * Utility methods for authenticating via Mojang (or legacy).
 */
public final class MojangUtils
{
    private MojangUtils() {}

    /**
     * Logs into Mojang and returns a new Minecraft session.
     *
     * <p>NB: You must manually interrupt the executor thread if the
     * completable future is cancelled!
     *
     * @param username Mojang username/email
     * @param password Mojang password
     * @param executor executor to run the login task on
     * @return completable future for the new Minecraft session
     * @see SessionUtils#setSession(Session) to apply the new session
     */
    public static CompletableFuture<Session> login(
        final String username, final String password, final Executor executor
    )
    {
        return CompletableFuture.supplyAsync(() -> {
            LOGGER.info("Logging into Minecraft with Mojang (or legacy) credentials...");
            try {
                // Fetch the Yggdrasil User Authentication provider
                final YggdrasilUserAuthentication yua = SessionUtils.getAuthProvider();

                // Update the credentials and login
                yua.setUsername(username);
                yua.setPassword(password);
                yua.logIn();

                // Pluck all useful session data
                final String name = yua.getSelectedProfile().getName();
                final String uuid = UUIDTypeAdapter.fromUUID(yua.getSelectedProfile().getId());
                final String token = yua.getAuthenticatedToken();
                final Session.AccountType type = Session.AccountType.byName(yua.getUserType().getName());

                // Logout after fetching what is needed
                yua.logOut();

                // Finally, log success and return
                LOGGER.info("Successfully logged into Minecraft via Mojang (or legacy)! ({})", name);
                return new Session(name, uuid, token, Optional.empty(), Optional.empty(), type);
            } catch (Exception e) {
                LOGGER.error("Unable to login to Minecraft via Mojang (or legacy)!", e);
                throw new CompletionException(e);
            }
        }, executor);
    }
}
