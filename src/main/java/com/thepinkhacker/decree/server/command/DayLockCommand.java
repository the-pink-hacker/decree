package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.gamerules.GameRules;

public class DayLockCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        DecreeUtils.register(dispatcher, CommandConfigs.DAY_LOCK, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("lock", BoolArgumentType.bool())
                        .executes(context -> execute(
                                context.getSource(),
                                BoolArgumentType.getBool(context, "lock")
                        ))
                )
                .executes(context -> execute(
                        context.getSource(),
                        true
                ))
        );
    }

    private static int execute(CommandSourceStack source, boolean dayLock) {
        GameRules rules = source.getLevel().getGameRules();
        rules.set(GameRules.ADVANCE_TIME, !dayLock, source.getServer());

        // Set noon
        // Bedrock sets the game to 5,000 ticks, but this makes more sense
        // Bedrock is weird
        if (dayLock) source.getLevel().setDayTime(6_000);

        String key = dayLock ? "commands.decree.daylock.enabled" : "commands.decree.daylock.disabled";
        source.sendSuccess(() -> Component.translatable(key), true);

        // Return 1 only if the value changes
        return rules.get(GameRules.ADVANCE_TIME) == dayLock ? 1 : 0;
    }
}
