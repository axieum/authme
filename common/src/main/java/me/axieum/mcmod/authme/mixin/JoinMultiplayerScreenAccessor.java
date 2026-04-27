package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;

/**
 * Provides the means to access protected members of the multiplayer screen.
 */
@Mixin(JoinMultiplayerScreen.class)
public interface JoinMultiplayerScreenAccessor
{
    @Accessor
    Screen getLastScreen();
}
