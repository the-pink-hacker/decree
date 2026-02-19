package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import com.thepinkhacker.decree.world.GameRulePreset;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;

public class GameRulePresetCommand implements CommandRegistrationCallback {
    private static final DynamicCommandExceptionType FAILED_TO_LOAD_EXCEPTION = new DynamicCommandExceptionType(preset -> Component.translatable("commands.decree.gamerulepreset.load.failed", preset));

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        DecreeUtils.register(dispatcher, CommandConfigs.GAME_RULE_PRESET, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("save")
                        .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
//                        .then(CommandManager.argument("preset", GameRulePresetArgumentType.preset())
//                                .executes(context -> save(
//                                        context.getSource(),
//                                        GameRulePresetArgumentType.getPreset(context, "preset"))
//                                )
//                        )
                )
                .then(Commands.literal("load")
//                        .then(CommandManager.argument("preset", GameRulePresetArgumentType.preset())
//                                .executes(context -> load(
//                                        context.getSource(),
//                                        GameRulePresetArgumentType.getPreset(context, "preset"))
//                                )
//                        )
                )
        );
    }

    private static int save(CommandSourceStack source, Path path) {
        GameRulePreset.save(path, source.getLevel());
        source.sendSuccess(() -> Component.translatable("commands.decree.gamerulepreset.save.success", FilenameUtils.getBaseName(path.toString())), true);
        return 1;
    }

    private static int load(CommandSourceStack source, Path path) throws CommandSyntaxException {
        int i = GameRulePreset.load(path, source);

        if (i >= 1) {
            source.sendSuccess(() -> Component.translatable("commands.decree.gamerulepreset.load.success", FilenameUtils.getBaseName(path.toString())), true);
            return i;
        } else if (i == 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.gamerulepreset.load.unchanged", FilenameUtils.getBaseName(path.toString())), true);
            return i;
        }

        throw FAILED_TO_LOAD_EXCEPTION.create(FilenameUtils.getBaseName(path.toString()));
    }
}
