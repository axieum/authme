package me.axieum.mcmod.authme.mixin;

import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.report.ReporterEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Provides the means to access protected members of the Splash Text Resource supplier.
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
