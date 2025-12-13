package me.axieum.mcmod.authme.mixin;

import java.io.File;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;

import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;

import me.axieum.mcmod.authme.mixinHelper.YggdrasilAuthenticationServiceGetter;

/**
 * Saves the YggdrasilAuthenticationService from initialization to be retrieved later.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements YggdrasilAuthenticationServiceGetter
{
    /** Constructs a new Minecraft mixin instance. */
    public MinecraftMixin() {}

    @SuppressWarnings({"checkstyle:illegalidentifiername", "checkstyle:membername"})
    @Unique
    private YggdrasilAuthenticationService authme$authService;

    @SuppressWarnings({"checkstyle:linelength"})
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Services;create(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Ljava/io/File;)Lnet/minecraft/server/Services;"))
    private Services wrapCreateServices(YggdrasilAuthenticationService yggdrasilAuthenticationService, File file, Operation<Services> original)
    {
        this.authme$authService = yggdrasilAuthenticationService;
        return original.call(yggdrasilAuthenticationService, file);
    }

    @SuppressWarnings({"checkstyle:illegalidentifiername"})
    @Override
    public YggdrasilAuthenticationService authme$getAuthService()
    {
        return this.authme$authService;
    }
}
