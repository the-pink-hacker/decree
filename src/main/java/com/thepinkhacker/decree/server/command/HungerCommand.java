package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.world.food.FoodData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class HungerCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        LiteralCommandNode<CommandSourceStack> node = DecreeUtils.register(dispatcher, CommandConfigs.HUNGER, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("set")
                        .then(Commands.literal("food")
                                .then(Commands.argument("food", IntegerArgumentType.integer(0))
                                        .executes(context -> setFood(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "food"))
                                        )
                                )
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("food", IntegerArgumentType.integer(0))
                                                .executes(context -> setFood(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        IntegerArgumentType.getInteger(context, "food"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("exhaustion")
                                .then(Commands.argument("exhaustion", FloatArgumentType.floatArg(0.0f))
                                        .executes(context -> setExhaustion(
                                                context.getSource(),
                                                FloatArgumentType.getFloat(context, "exhaustion"))
                                        )
                                )
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("exhaustion", FloatArgumentType.floatArg(0.0f, 40.0f))
                                                .executes(context -> setExhaustion(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "exhaustion"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("saturation")
                                .then(Commands.argument("saturation", FloatArgumentType.floatArg(0.0f))
                                        .executes(context -> setSaturation(
                                                context.getSource(),
                                                FloatArgumentType.getFloat(context, "saturation"))
                                        )
                                )
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("saturation", FloatArgumentType.floatArg(0.0f))
                                                .executes(context -> setSaturation(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "saturation"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.argument("food", IntegerArgumentType.integer(0))
                                .executes(context -> setFood(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "food"))
                                )
                        )
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("food", IntegerArgumentType.integer(0))
                                        .executes(context -> setFood(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "food"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("add")
                        .then(Commands.literal("food")
                                .then(Commands.argument("food", IntegerArgumentType.integer())
                                        .executes(context -> addFood(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "food"))
                                        )
                                )
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("food", IntegerArgumentType.integer())
                                                .executes(context -> addFood(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        IntegerArgumentType.getInteger(context, "food"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("exhaustion")
                                .then(Commands.argument("exhaustion", FloatArgumentType.floatArg())
                                        .executes(context -> setExhaustion(
                                                context.getSource(),
                                                FloatArgumentType.getFloat(context, "exhaustion"))
                                        )
                                )
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("exhaustion", FloatArgumentType.floatArg())
                                                .executes(context -> setExhaustion(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "exhaustion"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("saturation")
                                .then(Commands.argument("saturation", FloatArgumentType.floatArg())
                                        .executes(context -> setSaturation(
                                                context.getSource(),
                                                FloatArgumentType.getFloat(context, "saturation"))
                                        )
                                )
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("saturation", FloatArgumentType.floatArg())
                                                .executes(context -> setSaturation(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        FloatArgumentType.getFloat(context, "saturation"))
                                                )
                                        )
                                )
                        )
                        .then(Commands.argument("food", IntegerArgumentType.integer())
                                .executes(context -> addFood(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "food"))
                                )
                        )
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("food", IntegerArgumentType.integer())
                                        .executes(context -> addFood(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "food"))
                                        )
                                )
                        )
                )
                .then(Commands.literal("query")
                        .then(Commands.literal("food")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> queryFood(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "target"))
                                        )
                                )
                                .executes(context -> queryFood(context.getSource()))
                        )
                        .then(Commands.literal("exhaustion")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> queryExhaustion(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "target"))
                                        )
                                )
                                .executes(context -> queryExhaustion(context.getSource()))
                        )
                        .then(Commands.literal("saturation")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(context -> querySaturation(
                                                context.getSource(),
                                                EntityArgument.getPlayer(context, "target"))
                                        )
                                )
                                .executes(context -> querySaturation(context.getSource()))
                        )
                        .executes(context -> queryFood(context.getSource()))
                )
        );
    }

    private static int setFood(CommandSourceStack source, Collection<ServerPlayer> players, int food) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            player.getFoodData().setFoodLevel(food);
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.hunger.set.food.success", food), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.hunger.set.food.failed")).create();
        }

        return i;
    }

    private static int setFood(CommandSourceStack source, int food) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return setFood(source, players, food);
    }

    private static int setExhaustion(CommandSourceStack source, Collection<ServerPlayer> players, float exhaustion) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            player.getFoodData().exhaustionLevel = exhaustion;
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.hunger.set.exhaustion.success", exhaustion), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.hunger.set.exhaustion.failed")).create();
        }

        return i;
    }

    private static int setExhaustion(CommandSourceStack source, float exhaustion) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return setExhaustion(source, players, exhaustion);
    }

    private static int setSaturation(CommandSourceStack source, Collection<ServerPlayer> players, float saturation) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            player.getFoodData().setSaturation(saturation);
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.hunger.set.saturation.success", saturation), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.hunger.set.saturation.failed")).create();
        }

        return i;
    }

    private static int setSaturation(CommandSourceStack source, float saturation) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return setSaturation(source, players, saturation);
    }

    private static int addFood(CommandSourceStack source, Collection<ServerPlayer> players, int food) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            FoodData hungerManager = player.getFoodData();
            hungerManager.setFoodLevel(food + hungerManager.getFoodLevel());
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.hunger.add.food.success", food), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.hunger.add.food.failed")).create();
        }

        return i;
    }

    private static int addFood(CommandSourceStack source, int food) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return addFood(source, players, food);
    }

    private static int addExhaustion(CommandSourceStack source, Collection<ServerPlayer> players, float exhaustion) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            FoodData hungerManager = player.getFoodData();
            hungerManager.addExhaustion(exhaustion);
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.hunger.add.exhaustion.success", exhaustion), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.hunger.add.exhaustion.failed")).create();
        }

        return i;
    }

    private static int addExhaustion(CommandSourceStack source, float exhaustion) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return addExhaustion(source, players, exhaustion);
    }

    private static int addSaturation(CommandSourceStack source, Collection<ServerPlayer> players, float saturation) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : players) {
            FoodData hungerManager = player.getFoodData();
            hungerManager.setSaturation(saturation + hungerManager.getSaturationLevel());
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.hunger.add.saturation.success", saturation), false);
        } else {
            throw new SimpleCommandExceptionType(Component.translatable("commands.decree.hunger.add.saturation.failed")).create();
        }

        return i;
    }

    private static int addSaturation(CommandSourceStack source, float saturation) throws CommandSyntaxException {
        Collection<ServerPlayer> players = new ArrayList<>();
        players.add(source.getPlayer());

        return addSaturation(source, players, saturation);
    }

    private static int queryFood(CommandSourceStack source, ServerPlayer player) {
        int hunger = player.getFoodData().getFoodLevel();
        source.sendSuccess(() -> Component.translatable("commands.decree.hunger.query.food.success", hunger), false);
        return hunger > 0 ? 1 : 0;
    }

    private static int queryFood(CommandSourceStack source) {
        return queryFood(source, Objects.requireNonNull(source.getPlayer()));
    }

    private static int queryExhaustion(CommandSourceStack source, ServerPlayer player) {
        float exhaustion = player.getFoodData().exhaustionLevel;
        source.sendSuccess(() -> Component.translatable("commands.decree.hunger.query.exhaustion.success", exhaustion), false);
        return exhaustion > 0 ? 1 : 0;
    }

    private static int queryExhaustion(CommandSourceStack source) {
        return queryExhaustion(source, Objects.requireNonNull(source.getPlayer()));
    }

    private static int querySaturation(CommandSourceStack source, ServerPlayer player) {
        float saturation = player.getFoodData().getSaturationLevel();
        source.sendSuccess(() -> Component.translatable("commands.decree.hunger.query.saturation.success", saturation), false);
        return saturation > 0 ? 1 : 0;
    }

    private static int querySaturation(CommandSourceStack source) {
        return querySaturation(source, Objects.requireNonNull(source.getPlayer()));
    }
}
