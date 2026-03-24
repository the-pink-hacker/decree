package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.gamerules.GameRules;

public class DayLockCommand implements CommandRegistrationCallback {
    private static final DynamicCommandExceptionType DIMENSION_CLOCK_EXCEPTION = new DynamicCommandExceptionType(dimension -> Component.translatable("commands.time.no_default_clock", dimension));


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

    private static int execute(CommandSourceStack source, boolean dayLock) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        GameRules rules = level.getGameRules();
        rules.set(GameRules.ADVANCE_TIME, !dayLock, source.getServer());

        if (dayLock) {
            // Bedrock sets the game to 5_000 ticks, but noon (6_000) makes more sense
            // Bedrock is weird
            level.clockManager().moveToTimeMarker(getClockHolder(level), ClockTimeMarkers.NOON);
        }

        String key = dayLock ? "commands.decree.daylock.enabled" : "commands.decree.daylock.disabled";
        source.sendSuccess(() -> Component.translatable(key), true);

        // Return 1 only if the value changes
        return rules.get(GameRules.ADVANCE_TIME) == dayLock ? 1 : 0;
    }

    private static Holder<WorldClock> getClockHolder(ServerLevel level) throws CommandSyntaxException {
        return level.dimensionType()
                .defaultClock()
                .orElseThrow(() -> DIMENSION_CLOCK_EXCEPTION.create(
                        level.dimensionTypeRegistration().getRegisteredName())
                );
    }
}
