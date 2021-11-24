package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;

/**
 * Provides the means to access protected members of the Minecraft client.
 */
@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor
{
    /**
     * Sets the Minecraft session.
     *
     * @param session new Minecraft session
     */
    @Accessor
    @Mutable
    void setSession(Session session);
}
