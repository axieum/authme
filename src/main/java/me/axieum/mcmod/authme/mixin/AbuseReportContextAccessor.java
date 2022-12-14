package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ReporterEnvironment;

/**
 * Provides the means to access protected members of the Abuse Report Context.
 */
@Mixin(AbuseReportContext.class)
public interface AbuseReportContextAccessor
{
    /**
     * Returns the reporter environment.
     *
     * @return environment
     */
    @Accessor
    ReporterEnvironment getEnvironment();
}
