package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;

/**
 * Provides the means to access protected members of the Abuse Report Context.
 */
@Mixin(ReportingContext.class)
public interface ReportingContextAccessor
{
    /**
     * Returns the reporter environment.
     *
     * @return environment
     */
    @Accessor
    ReportEnvironment getEnvironment();
}
