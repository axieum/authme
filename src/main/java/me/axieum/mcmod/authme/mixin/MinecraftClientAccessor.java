package me.axieum.mcmod.authme.mixin;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;

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
     * Sets the game profile.
     *
     * @param future the future of the new game profile
     */
    @Accessor
    @Mutable
    void setGameProfileFuture(CompletableFuture<ProfileResult> future);

    /**
     * Returns the Minecraft authentication service.
     *
     * @return the Minecraft authentication service
     */
    @Accessor
    YggdrasilAuthenticationService getAuthenticationService();

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

    /**
     * Sets the Minecraft Realms periodic checkers.
     *
     * @param realmsPeriodicCheckers new Minecraft Realms periodic checkers
     */
    @Accessor
    @Mutable
    void setRealmsPeriodicCheckers(RealmsPeriodicCheckers realmsPeriodicCheckers);
}
