package me.axieum.mcmod.authme.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;

/**
 * Provides the means to access protected members of the Realms Main Screen.
 */
@Mixin(RealmsMainScreen.class)
public interface RealmsMainScreenAccessor
{
    /**
     * Sets the 'checked client compatibility' flag.
     *
     * @param checked true if checked
     */
    @Accessor
    @Mutable
    static void setCheckedClientCompatibility(boolean checked) {}

    /**
     * Sets the 'Realms Generic Error' screen.
     *
     * @param screen error screen
     */
    @Accessor
    @Mutable
    static void setRealmsGenericErrorScreen(Screen screen) {}
}
