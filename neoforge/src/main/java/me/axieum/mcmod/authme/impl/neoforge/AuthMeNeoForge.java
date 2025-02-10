package me.axieum.mcmod.authme.impl.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import me.axieum.mcmod.authme.api.AuthMe;

/**
 * The NeoForge platform mod.
 */
@Mod(AuthMe.MOD_ID)
public class AuthMeNeoForge
{
    /**
     * Constructs a new NeoForge platform mod.
     *
     * @param eventBus the NeoForge event bus
     */
    public AuthMeNeoForge(IEventBus eventBus)
    {
        // Cascade mod initialisation
        AuthMe.init();
    }
}
