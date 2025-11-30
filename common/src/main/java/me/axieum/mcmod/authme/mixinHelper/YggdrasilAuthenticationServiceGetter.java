package me.axieum.mcmod.authme.mixinHelper;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

/**
 * Interface for getting the YggdrasilAuthenticationService.
 */
public interface YggdrasilAuthenticationServiceGetter
{
    /**
     * Gets the YggdrasilAuthenticationService.
     *
     * @return the YggdrasilAuthenticationService
     */
    @SuppressWarnings({"checkstyle:illegalidentifiername", "checkstyle:methodname"})
    default YggdrasilAuthenticationService authme$getAuthService()
    {
        throw new AssertionError();
    }
}
