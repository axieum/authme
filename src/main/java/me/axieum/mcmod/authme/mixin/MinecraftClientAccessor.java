package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.authlib.minecraft.UserApiService;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.report.AbuseReportContext;
import net.minecraft.client.util.ProfileKeys;
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

    /**
     * Sets the Minecraft user API service.
     *
     * @param userApiService new Minecraft user API service
     */
    @Accessor
    @Mutable
    void setUserApiService(UserApiService userApiService);

    /**
     * Sets the Minecraft social interactions manager.
     *
     * @param socialInteractionsManager new Minecraft social interactions manager
     */
    @Accessor
    @Mutable
    void setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);

    /**
     * Sets the Minecraft profile keys.
     *
     * @param profileKeys new Minecraft profile keys
     */
    @Accessor
    @Mutable
    void setProfileKeys(ProfileKeys profileKeys);

    /**
     * Sets the Minecraft abuse report context.
     *
     * @param abuseReportContext new Minecraft abuse report context
     */
    @Accessor
    @Mutable
    void setAbuseReportContext(AbuseReportContext abuseReportContext);
}
