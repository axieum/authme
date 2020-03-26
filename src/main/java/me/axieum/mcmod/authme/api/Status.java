package me.axieum.mcmod.authme.api;

import net.minecraft.client.resource.language.I18n;

public enum Status
{
    VALID("gui.authme.status.valid", 0xFF00FF00),
    INVALID("gui.authme.status.invalid", 0xFFFF0000),
    UNKNOWN("gui.authme.status.unknown", 0xFF999999);

    public final String langKey;
    public final int color;

    Status(String langKey, int color)
    {
        this.langKey = langKey;
        this.color = color;
    }

    @Override
    public String toString()
    {
        return I18n.translate(langKey);
    }
}
