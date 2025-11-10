package com.thepinkhacker.decree.world;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.GameRules;

public class DecreeGameRules {
    public static final GameRules.Key<GameRules.IntRule> ITEM_DESPAWN_AGE = GameRuleRegistry.register(
            "itemDespawnAge",
            GameRules.Category.DROPS,
            TimeRule.create(6_000)
    );

    public static final GameRules.Key<GameRules.BooleanRule> DO_ENDERMAN_PICKUP = GameRuleRegistry.register(
            "doEndermanPickup",
            GameRules.Category.MOBS,
            GameRules.BooleanRule.create(true)
    );

    public static final GameRules.Key<GameRules.BooleanRule> DO_ENDERMAN_PLACE = GameRuleRegistry.register(
            "doEndermanPlace",
            GameRules.Category.MOBS,
            GameRules.BooleanRule.create(true)
    );

    public static final GameRules.Key<GameRules.BooleanRule> DO_NETHER_PORTAL_MOB_SPAWN = GameRuleRegistry.register(
            "doNetherPortalMobSpawn",
            GameRules.Category.MOBS,
            GameRules.BooleanRule.create(true)
    );
    
    public static final GameRules.Key<GameRules.BooleanRule> DO_MOB_EXPLOSION_GRIEFING = GameRuleRegistry.register(
            "doMobExplosionGriefing",
            GameRules.Category.MOBS,
            GameRules.BooleanRule.create(true)
    );

    public static final GameRules.Key<GameRules.IntRule> MINECART_MAX_SPEED_EMPTY = GameRuleRegistry.register(
            "minecartMaxSpeedEmpty",
            GameRules.Category.MISC,
            GameRules.IntRule.create(0, 0, 1000, FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS), (server, value) -> {})
    );

    public static final GameRules.Key<GameRules.IntRule> MINECART_MAX_SPEED_RIDER = GameRuleRegistry.register(
            "minecartMaxSpeedRider",
            GameRules.Category.MISC,
            GameRules.IntRule.create(0, 0, 1000, FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS), (server, value) -> {})
    );

    // Java is weird and won't init the variables unless this runs
    // Otherwise it would try to init after the registry is frozen
    public static void register() {}
}
