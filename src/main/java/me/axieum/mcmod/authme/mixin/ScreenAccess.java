package me.axieum.mcmod.authme.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public interface ScreenAccess
{
    @Accessor
    @Mutable
    List<Element> getChildren();
}
