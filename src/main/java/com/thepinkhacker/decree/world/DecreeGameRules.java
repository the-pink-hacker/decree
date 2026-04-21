package com.thepinkhacker.decree.world;

import com.thepinkhacker.decree.Decree;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class DecreeGameRules {
    private static final FeatureFlagSet MINECART_IMPROVEMENTS = FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS);

    public static final GameRule<Integer> ITEM_DESPAWN_AGE = GameRuleBuilder
            .forInteger(6_000)
            .minValue(0)
            .category(GameRuleCategory.DROPS)
            .buildAndRegister(Decree.id("item_despawn_age"));

    public static final GameRule<Boolean> ENDERMAN_PICKUP = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Decree.id("enderman_pickup"));

    public static final GameRule<Boolean> ENDERMAN_PLACE = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Decree.id("enderman_place"));

    public static final GameRule<Boolean> NETHER_PORTAL_MOB_SPAWN = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Decree.id("nether_portal_mob_spawn"));
    
    public static final GameRule<Boolean> DO_MOB_EXPLOSION_GRIEFING = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Decree.id("mob_explosion_griefing"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_EMPTY = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_empty"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_RIDER = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_rider"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_CHEST = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_chest"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_FURNACE = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_furnace"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_HOPPER = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_hopper"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_TNT = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_tnt"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_COMMAND_BLOCK = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_command_block"));

    public static final GameRule<Integer> MINECART_MAX_SPEED_SPAWNER = GameRuleBuilder
            .forInteger(-1)
            .range(-1, 1_000)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_max_speed_command_spawner"));

    public static final GameRule<Integer> MINECART_DISMOUNT_HALT_COOLDOWN = GameRuleBuilder
            .forInteger(0)
            .minValue(0)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_dismount_cooldown"));

    public static final GameRule<Double> MINECART_HALT_FACTOR = GameRuleBuilder
            .forDouble(0.5)
            .range(0.0, 1.0)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(Decree.id("minecart_halt_factor"));

    public static final GameRule<Double> MINECART_DISMOUNT_HALT_FACTOR = GameRuleBuilder
            .forDouble(0.95)
            .range(0.0, 1.0)
            .category(GameRuleCategory.MISC)
            .requiredFeatures(MINECART_IMPROVEMENTS)
            .buildAndRegister(Decree.id("minecart_dismount_halt_factor"));

    // Java is weird and won't init the variables unless this runs
    // Otherwise it would try to init after the registry is frozen
    public static void register() {}
}
