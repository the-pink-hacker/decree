package com.thepinkhacker.decree.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;

public class DecreeGameTestHelper {
    private final GameTestHelper context;

    DecreeGameTestHelper(GameTestHelper context) {
        this.context = context;
    }

    public void assertWeatherClear() {
        context.assertTrue(!context.getLevel().isRaining(), "Expected weather to be clear.");
    }

    public void assertWeatherRain() {
        context.assertTrue(context.getLevel().isRaining(), "Expected weather to be raining.");
    }

    public void assertWeatherThunder() {
        context.assertTrue(context.getLevel().isThundering(), "Expected weather to be thundering.");
    }

    public void executeCommand(String command) {
        MinecraftServer server = context.getLevel().getServer();

        try {
            server.getCommands()
                    .getDispatcher()
                    .execute(command, server.createCommandSourceStack().withLevel(context.getLevel()));
        } catch (CommandSyntaxException e) {
            context.fail(e.toString());
        }
    }

    public GameTestHelper getContext() {
        return context;
    }

    public static DecreeGameTestHelper of(GameTestHelper context) {
        return new DecreeGameTestHelper(context);
    }
}
