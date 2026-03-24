package com.thepinkhacker.decree.gametest;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.WeatherData;

public class DecreeGameTestHelper {
    private final GameTestHelper context;

    DecreeGameTestHelper(GameTestHelper context) {
        this.context = context;
    }

    public void assertWeatherNoRain() {
        context.assertTrue(
                !context.getLevel().getWeatherData().isRaining(),
                "Expected weather to be clear."
        );
    }

    public void assertWeatherRain() {
        context.assertTrue(
                context.getLevel().getWeatherData().isRaining(),
                "Expected weather to be raining."
        );
    }

    public void assertWeatherRainThunder() {
        WeatherData weather = context.getLevel().getWeatherData();
        context.assertTrue(
                weather.isRaining() && weather.isThundering(),
                "Expected weather to be thundering."
        );
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
