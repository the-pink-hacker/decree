package com.thepinkhacker.decree.registry;

import com.thepinkhacker.decree.data.command.CommandConfig;
import com.thepinkhacker.decree.server.command.CommandConfigs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class DecreeRegistries {
    public static final Registry<CommandConfig> COMMAND_CONFIG = BuiltInRegistries.registerSimple(
            DecreeRegistryKeys.COMMAND_CONFIG,
            CommandConfigs::registerAndGetDefault
    );
}
