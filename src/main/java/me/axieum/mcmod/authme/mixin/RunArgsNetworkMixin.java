package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.RunArgs;
import net.minecraft.client.util.Session;

import me.axieum.mcmod.authme.impl.AuthMe;
import me.axieum.mcmod.authme.impl.config.AuthMeConfig;

@Mixin(RunArgs.Network.class)
public abstract class RunArgsNetworkMixin
{

    /**
     * Injects into the creation of RunArgs.Network and replaces current session with the saved one.
     *
     * @param oldSession original session the game wanted to use
     * @return Saved session, unless it is invalid or this feature is disabled in the config
     */
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Session modifyLaunchSession(Session oldSession)
    {
        AuthMeConfig.AutoLogin autoLogin = AuthMe.getConfig().autoLogin;
        return autoLogin.doAutoLogin && autoLogin.savedSession.hasSavedSession()
            ? autoLogin.savedSession.getSession() : oldSession;
    }
}
