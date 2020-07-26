package me.axieum.mcmod.authme.mixin;

import com.mojang.realmsclient.RealmsMainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsMainScreen.class)
public interface RealmsMainScreenMixin
{
    @Accessor(value="checkedClientCompatability")
    @Mutable
    static void setCheckedClientCompatability(boolean checkedClientCompatability){
        // Method body is ignored.
        throw new UnsupportedOperationException();
    };

    @Accessor(value="realmsGenericErrorScreen")
    @Mutable
    static void setRealmsGenericErrorScreen(Screen realmsGenericErrorScreen){
        // Method body is ignored.
        throw new UnsupportedOperationException();
    };

}