package me.axieum.mcmod.authme.mixin;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.realmsclient.RealmsAvailability;

/**
 * Provides the means to access protected members of the Realms availability check.
 */
@Mixin(RealmsAvailability.class)
public interface RealmsAvailabilityAccessor
{
    /**
     * Sets the Realms availability info checker.
     *
     * @param future Realms availability info completable future
     */
    @Accessor
    @Mutable
    static void setFuture(CompletableFuture<RealmsAvailability.Result> future) {}
}
