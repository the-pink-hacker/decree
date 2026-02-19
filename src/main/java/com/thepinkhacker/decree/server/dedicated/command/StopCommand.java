package com.thepinkhacker.decree.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.thepinkhacker.decree.server.command.CommandConfigs;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;

public class StopCommand implements CommandRegistrationCallbackDedicated {
    private static volatile int timeLeft;
    private static final SimpleCommandExceptionType FAILED_CANCEL = new SimpleCommandExceptionType(Component.translatable("commands.decree.stop.cancel.failed"));

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        DecreeUtils.register(dispatcher, CommandConfigs.STOP, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("cancel")
                        .executes(context -> cancel())
                )
                .then(Commands.argument("time", IntegerArgumentType.integer(0))
                        .executes(context -> stopServer(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "time")
                        ))
                )
                .executes(context -> stopServer(
                        context.getSource(),
                        0
                ))
        );
    }

    private static int stopServer(CommandSourceStack source, int seconds) {
        final MinecraftServer server = source.getServer();

        if (seconds > 0) {
            Thread thread = new Thread(() -> {
                final PlayerList playerManager = server.getPlayerList();
                timeLeft = seconds;

                for (int i = 0; i < seconds; i++) {
                    // Cancel
                    if (timeLeft == -1) {
                        sendServerMessage(playerManager, source, Component.translatable("commands.decree.stop.cancel.success"));
                        return;
                    }

                    timeLeft = seconds - i;

                    if (timeLeft <= 10 || timeLeft % 10 == 0) sendServerMessage(playerManager, source, Component.translatable("commands.decree.stop.time", timeLeft));

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                sendServerMessage(playerManager, source, Component.translatable("commands.decree.stop.stop", seconds));
                server.halt(false);
            }, "Server stop thread");

            thread.start();
        } else {
            source.sendSuccess(() -> Component.translatable("commands.decree.stop.immediate"), true);
            server.halt(false);
        }

        return 1;
    }

    private static int cancel() throws CommandSyntaxException {
        if (timeLeft > 0) {
            timeLeft = -1;
            return 1;
        }

        throw FAILED_CANCEL.create();
    }

    private static void sendServerMessage(PlayerList playerManager, CommandSourceStack source, Component text) {
        // Todo: Make secure
        playerManager.broadcastChatMessage(PlayerChatMessage.system(text.toString()), source, ChatType.bind(ChatType.CHAT, source));
    }
}
