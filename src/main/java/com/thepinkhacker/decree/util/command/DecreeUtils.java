package com.thepinkhacker.decree.util.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.thepinkhacker.decree.data.command.CommandConfig;
import com.thepinkhacker.decree.registry.DecreeRegistries;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;

public class DecreeUtils {
    public static LiteralCommandNode<CommandSourceStack> register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            ResourceKey<CommandConfig> key,
            Function<LiteralArgumentBuilder<CommandSourceStack>, LiteralArgumentBuilder<CommandSourceStack>> command
    ) {
        // TODO: Check for collisions
        LiteralArgumentBuilder<CommandSourceStack> builtCommand = command.apply(Commands.literal(key.identifier().getPath()));
        CommandConfig config = DecreeRegistries.COMMAND_CONFIG.getValueOrThrow(key);

        if (config.prefix.optional) {
            dispatcher.register(builtCommand);
        }

        return dispatcher.register(Commands.literal(config.prefix.prefix).then(builtCommand));
    }
}
