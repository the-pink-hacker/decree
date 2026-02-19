package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.Collection;

public class SetOwnerCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        LiteralCommandNode<CommandSourceStack> node = DecreeUtils.register(dispatcher, CommandConfigs.SET_OWNER, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("pets", EntityArgument.entities())
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> setOwner(
                                        context.getSource(),
                                        EntityArgument.getEntities(context, "pets"),
                                        EntityArgument.getPlayer(context, "player"))
                                )
                        )
                        .executes(context -> setOwner(
                                context.getSource(),
                                EntityArgument.getEntities(context, "pets"))
                        )
                )
        );
    }

    private static int setOwner(CommandSourceStack source, Collection<? extends Entity> entities, ServerPlayer player) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : entities) {
            if (entity instanceof TamableAnimal pet) {
                if (!pet.isOwnedBy(player)) {
                    pet.setOwner(player);
                    i++;
                }
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.setowner.success", player.getDisplayName()), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.setowner.failed")).create();
        }

        return i;
    }

    private static int setOwner(CommandSourceStack source, Collection<? extends Entity> entities) throws CommandSyntaxException {
        return setOwner(source, entities, source.getPlayer());
    }
}
