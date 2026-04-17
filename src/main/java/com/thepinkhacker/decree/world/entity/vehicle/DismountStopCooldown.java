package com.thepinkhacker.decree.world.entity.vehicle;

import net.minecraft.world.level.gamerules.GameRules;

public interface DismountStopCooldown {
    default void decree$startStopCooldown(GameRules rules) {
        throw new AssertionError("Implemented in Mixin");
    }

    default void decree$cancelCooldown() {
        throw new AssertionError("Implemented in Mixin");
    }
}
