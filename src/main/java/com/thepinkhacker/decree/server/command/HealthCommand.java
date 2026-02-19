package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;

public class HealthCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        LiteralCommandNode<CommandSourceStack> node = DecreeUtils.register(dispatcher, CommandConfigs.HEALTH, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("set")
                        .then(Commands.argument("health", FloatArgumentType.floatArg(0.0f))
                                .executes(context -> setHealth(
                                        context.getSource(),
                                        FloatArgumentType.getFloat(context, "health"))
                                )
                        )
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("health", FloatArgumentType.floatArg(0.0f))
                                        .executes(context -> setHealth(
                                                context.getSource(),
                                                EntityArgument.getEntities(context, "targets"),
                                                FloatArgumentType.getFloat(context, "health"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("add")
                        .then(Commands.argument("health", FloatArgumentType.floatArg())
                                .executes(context -> addHealth(
                                        context.getSource(),
                                        FloatArgumentType.getFloat(context, "health"))
                                )
                        )
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("health", FloatArgumentType.floatArg())
                                        .executes(context -> addHealth(
                                                context.getSource(),
                                                EntityArgument.getEntities(context, "targets"),
                                                FloatArgumentType.getFloat(context, "health"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("query")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> queryHealth(
                                        context.getSource(),
                                        EntityArgument.getEntity(context, "target"))
                                )
                        )
                        .executes(context -> queryHealth(context.getSource()))
                )
        );
    }

    private static int setHealth(CommandSourceStack source, Collection<? extends Entity> entities, float health) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setHealth(health);
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.health.set.success", health), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.health.set.failed")).create();
        }

        return i;
    }

    private static int setHealth(CommandSourceStack source, float health) throws CommandSyntaxException {
        Collection<Entity> entities = new ArrayList<>();
        entities.add(source.getEntity());

        return setHealth(source, entities, health);
    }

    private static int addHealth(CommandSourceStack source, Collection<? extends Entity> entities, float health) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setHealth(health + livingEntity.getHealth());
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.health.add.success", health), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.health.add.failed")).create();
        }

        return i;
    }

    private static int addHealth(CommandSourceStack source, float health) throws CommandSyntaxException {
        Collection<Entity> entities = new ArrayList<>();
        entities.add(source.getEntity());

        return addHealth(source, entities, health);
    }

    private static int queryHealth(CommandSourceStack source, Entity entity) throws CommandSyntaxException {
        if (entity instanceof LivingEntity livingEntity) {
            source.sendSuccess(() -> Component.translatable("commands.decree.health.query.success", livingEntity.getHealth()), false);
            return 1;
        }

        throw new SimpleCommandExceptionType(Component.translatable("commands.decree.health.query.failed")).create();
    }

    private static int queryHealth(CommandSourceStack source) throws CommandSyntaxException {
        return queryHealth(source, source.getEntity());
    }
}
