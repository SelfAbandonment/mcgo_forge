package org.mcgo_forge.mcgo_forge.gameplay;

import net.minecraft.ChatFormatting;

public enum Team {
    TERRORIST("Terrorist", "T", ChatFormatting.GOLD),
    COUNTER_TERRORIST("Counter-Terrorist", "CT", ChatFormatting.BLUE),
    SPECTATOR("Spectator", "SPEC", ChatFormatting.GRAY);

    private final String displayName;
    private final String shortName;
    private final ChatFormatting color;

    Team(String displayName, String shortName, ChatFormatting color) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public Team getOpposite() {
        return switch (this) {
            case TERRORIST -> COUNTER_TERRORIST;
            case COUNTER_TERRORIST -> TERRORIST;
            default -> SPECTATOR;
        };
    }
}
