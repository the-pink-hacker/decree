package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class ToggleDownfallCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        DecreeUtils.register(dispatcher, CommandConfigs.TOGGLE_DOWNFALL, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(context -> execute(context.getSource()))
        );
    }

    private static int execute(CommandSourceStack source) {
        ServerLevel world = source.getLevel();

        world.setWeatherParameters(0, 0, !world.isRaining() && !world.isThundering(), false);

        source.sendSuccess(() -> Component.translatable("commands.decree.toggledownfall.success"), true);

        return 1;
    }
}
