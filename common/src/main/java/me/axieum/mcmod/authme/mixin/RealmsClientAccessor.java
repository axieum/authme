package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.realmsclient.client.RealmsClient;

import net.minecraft.client.Minecraft;

/**
 * Provides a means of accessing RealmsClient's internals.
 */
@Mixin(RealmsClient.class)
public interface RealmsClientAccessor
{
    /**
     * The constructor for the RealmsClient class.
     *
     * @param sessionId The session id.
     * @param username The username of the player.
     * @param minecraft The client Minecraft instance;
     * @return The constructed RealmsClient
     */
    @Invoker(value = "<init>")
    static RealmsClient init(String sessionId, String username, Minecraft minecraft)
    {
        throw new AssertionError();
    }

    /**
     * Sets the realmsClientInstance field.
     *
     * @param client The new value of the field.
     */
    @Accessor(value = "realmsClientInstance")
    static void setRealmsClientInstance(RealmsClient client)
    {
        throw new AssertionError();
    }
}
