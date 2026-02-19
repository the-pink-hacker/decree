package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;

public class ClearSpawnPointCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        DecreeUtils.register(dispatcher, CommandConfigs.CLEAR_SPAWN_POINT, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> clearSpawnPoint(
                                context.getSource(),
                                EntityArgument.getPlayers(context, "targets"))
                        )
                )
                .executes(context -> clearSpawnPoint(context.getSource()))
        );
    }

    private static int clearSpawnPoint(CommandSourceStack source, Collection<ServerPlayer> players) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            player.setRespawnPosition(null, false);
            i++;
        }

        if (i > 0) {
            int finalI = i;
            source.sendSuccess(() -> Component.translatable("commands.decree.clearspawnpoint.success", finalI), true);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.clearspawnpoint.failed")).create();
        }

        return i;
    }

    private static int clearSpawnPoint(CommandSourceStack source) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return clearSpawnPoint(source, players);
    }
}
