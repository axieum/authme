package me.axieum.mcmod.authme.mixin;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.realms.RealmsAvailability;

/**
 * Provides the means to access protected members of the Realms availability check.
 */
@Mixin(RealmsAvailability.class)
public interface RealmsAvailabilityAccessor
{
    /**
     * Sets the Realms availability info checker.
     *
     * @param availabilityInfo Realms availability info completable future
     */
    @Accessor
    @Mutable
    static void setCurrentFuture(CompletableFuture<RealmsAvailability.Info> availabilityInfo) {}
}
