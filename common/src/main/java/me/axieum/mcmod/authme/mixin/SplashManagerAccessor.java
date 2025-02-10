package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.User;
import net.minecraft.client.resources.SplashManager;

/**
 * Provides the means to access protected members of the Splash Text Resource supplier.
 */
@Mixin(SplashManager.class)
public interface SplashManagerAccessor
{
    /**
     * Sets the Minecraft session.
     *
     * @param session new Minecraft session
     */
    @Accessor
    @Mutable
    void setUser(User session);
}
