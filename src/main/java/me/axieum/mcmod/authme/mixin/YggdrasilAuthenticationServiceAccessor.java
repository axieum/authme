package me.axieum.mcmod.authme.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

/**
 * Provides the means to access protected members of the Yggdrasil Authentication service.
 */
@Mixin(YggdrasilAuthenticationService.class)
public interface YggdrasilAuthenticationServiceAccessor
{
    /**
     * Returns the client token.
     *
     * @return client token
     */
    @Accessor(remap = false)
    @Nullable String getClientToken();

    /**
     * Sets the client token.
     *
     * @param clientToken new client token
     */
    @Accessor(remap = false)
    @Mutable
    void setClientToken(String clientToken);
}
