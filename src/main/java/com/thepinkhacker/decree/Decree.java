package com.thepinkhacker.decree;

import com.mojang.brigadier.CommandDispatcher;
import com.thepinkhacker.decree.server.command.*;
import com.thepinkhacker.decree.server.dedicated.command.CommandRegistrationCallbackDedicated;
import com.thepinkhacker.decree.server.dedicated.command.StopCommand;
import com.thepinkhacker.decree.world.DecreeGameRules;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Decree implements ModInitializer {
    public static final String MOD_ID = "decree";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandConfigs.initialize();
        DecreeGameRules.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(
                    dispatcher,
                    registryAccess,
                    environment,
                    new ClearSpawnPointCommand(),
                    new DayLockCommand(),
                    new HeadCommand(),
                    new HealthCommand(),
                    new HungerCommand(),
                    new NameCommand(),
                    new SetOwnerCommand(),
                    new ToggleDownfallCommand(),
                    new StopCommand(),
                    new GameRulePresetCommand(),
                    new RideCommand()
            );

            LOGGER.info("Registered Decree.");
        });
    }

    private static void registerCommands(
            CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment,
            CommandRegistrationCallback... commands
    ) {
        for (CommandRegistrationCallback command : commands) {
            if (command instanceof CommandRegistrationCallbackDedicated) {
                if (environment.includeDedicated) command.register(dispatcher, registryAccess, environment);
            } else {
                command.register(dispatcher, registryAccess, environment);
            }
        }
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }
}
