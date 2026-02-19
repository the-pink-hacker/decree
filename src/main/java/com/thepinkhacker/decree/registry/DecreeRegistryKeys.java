package com.thepinkhacker.decree.registry;

import com.thepinkhacker.decree.Decree;
import com.thepinkhacker.decree.data.command.CommandConfig;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class DecreeRegistryKeys {
    public static final ResourceKey<Registry<CommandConfig>> COMMAND_CONFIG = of("command_config");

    private static <T> ResourceKey<Registry<T>> of(String id) {
        return ResourceKey.createRegistryKey(Decree.id(id));
    }
}
