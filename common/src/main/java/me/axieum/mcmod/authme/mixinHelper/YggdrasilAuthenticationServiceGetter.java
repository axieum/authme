package me.axieum.mcmod.authme.mixinHelper;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

public interface YggdrasilAuthenticationServiceGetter {
    default YggdrasilAuthenticationService authme$getAuthService() { throw new AssertionError(); }
}
