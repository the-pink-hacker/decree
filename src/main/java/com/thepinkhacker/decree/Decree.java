package com.thepinkhacker.decree;

import com.mojang.brigadier.CommandDispatcher;
import com.thepinkhacker.decree.command.argument.DecreeArgumentTypes;
import com.thepinkhacker.decree.server.command.*;
import com.thepinkhacker.decree.server.dedicated.command.CommandRegistrationCallbackDedicated;
import com.thepinkhacker.decree.server.dedicated.command.StopCommand;
import com.thepinkhacker.decree.world.DecreeGameRules;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Decree implements ModInitializer {
    public static final String MOD_ID = "decree";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final boolean CLIENT_REQUIRED = true;

    @Override
    public void onInitialize() {
        CommandConfigs.initialize();
        if (CLIENT_REQUIRED) {
            DecreeArgumentTypes.register();
        }
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
                    new StopCommand()
            );

            if (CLIENT_REQUIRED) {
                registerCommands(
                        dispatcher,
                        registryAccess,
                        environment,
                        new GameRulePresetCommand(),
                        new RideCommand()
                );
            }

            LOGGER.info("Registered Decree.");
        });
    }

    private static void registerCommands(
            CommandDispatcher<net.minecraft.server.command.ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment,
            CommandRegistrationCallback... commands
    ) {
        for (CommandRegistrationCallback command : commands) {
            if (command instanceof CommandRegistrationCallbackDedicated) {
                if (environment.dedicated) command.register(dispatcher, registryAccess, environment);
            } else {
                command.register(dispatcher, registryAccess, environment);
            }
        }
    }

    public static Identifier id(String id) {
        return Identifier.of(MOD_ID, id);
    }
}
