package me.axieum.mcmod.authme.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import me.axieum.mcmod.authme.mixinHelper.YggdrasilAuthenticationServiceGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.server.Services;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;

@Mixin(Minecraft.class)
public class MinecraftMixin implements YggdrasilAuthenticationServiceGetter {
    @Unique
    private YggdrasilAuthenticationService authme$authService;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Services;create(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Ljava/io/File;)Lnet/minecraft/server/Services;"))
    private Services wrapCreateServices(YggdrasilAuthenticationService yggdrasilAuthenticationService, File file, Operation<Services> original) {
        this.authme$authService = yggdrasilAuthenticationService;
        return original.call(yggdrasilAuthenticationService, file);
    }

    @Override
    public YggdrasilAuthenticationService authme$getAuthService() {
        return this.authme$authService;
    }
}
