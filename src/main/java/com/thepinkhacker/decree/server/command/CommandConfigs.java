package com.thepinkhacker.decree.server.command;

import com.thepinkhacker.decree.Decree;
import com.thepinkhacker.decree.data.command.CommandConfig;
import com.thepinkhacker.decree.data.command.CommandPrefix;
import com.thepinkhacker.decree.registry.DecreeRegistries;
import com.thepinkhacker.decree.registry.DecreeRegistryKeys;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class CommandConfigs {
    public static final ResourceKey<CommandConfig> CLEAR_SPAWN_POINT = register("clearspawnpoint");
    public static final ResourceKey<CommandConfig> DAY_LOCK = register("daylock");
    public static final ResourceKey<CommandConfig> GAME_RULE_PRESET = register("gamerulepreset");
    public static final ResourceKey<CommandConfig> HEAD = register("head");
    public static final ResourceKey<CommandConfig> HEALTH = register("health");
    public static final ResourceKey<CommandConfig> HUNGER = register("hunger");
    public static final ResourceKey<CommandConfig> NAME = register("name");
    public static final ResourceKey<CommandConfig> RIDE = register("ride", false);
    public static final ResourceKey<CommandConfig> SET_OWNER = register("setowner");
    public static final ResourceKey<CommandConfig> TOGGLE_DOWNFALL = register("toggledownfall");
    public static final ResourceKey<CommandConfig> STOP = register("stop", false);

    private static ResourceKey<CommandConfig> register(String id) {
        return register(id, true);
    }

    private static ResourceKey<CommandConfig> register(String id, boolean prefixOptional) {
        Identifier decreeId = Decree.id(id);
        Registry.register(
                DecreeRegistries.COMMAND_CONFIG,
                decreeId,
                CommandConfig.of(CommandPrefix.of(decreeId.getNamespace(), prefixOptional))
        );
        return ResourceKey.create(DecreeRegistryKeys.COMMAND_CONFIG, decreeId);
    }

    public static ResourceKey<CommandConfig> registerAndGetDefault(Registry<CommandConfig> registry) {
        return NAME;
    }

    public static void initialize() {}
}
