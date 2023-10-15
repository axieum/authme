package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.client.session.Session;

/**
 * Provides the means to access protected members of the Splash Text Resource supplier.
 */
@Mixin(SplashTextResourceSupplier.class)
public interface SplashTextResourceSupplierAccessor
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
