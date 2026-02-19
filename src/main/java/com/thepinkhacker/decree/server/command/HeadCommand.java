package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import com.thepinkhacker.decree.world.level.block.entity.SkullBlockEntityMutator;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.Collection;
import java.util.List;

public class HeadCommand implements CommandRegistrationCallback {

    private static final SimpleCommandExceptionType GIVE_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.decree.head.give.failed")
    );
    private static final SimpleCommandExceptionType UPDATE_EXCEPTION = new SimpleCommandExceptionType(
            Component.translatable("commands.decree.head.update.failed")
    );

    @Override
    public void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        LiteralCommandNode<CommandSourceStack> node = DecreeUtils.register(dispatcher, CommandConfigs.HEAD, command -> command
                .then(Commands.literal("give")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(context -> give(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "targets"),
                                                GameProfileArgument
                                                        .getGameProfiles(context, "player")
                                                        .stream()
                                                        .map(NameAndId::name)
                                                        .map(ResolvableProfile::createUnresolved)
                                                        .toList()
                                        ))
                                )
                        )
                        .executes(context -> give(
                                context.getSource()
                        ))
                )
                .then(Commands.literal("query")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.literal("uuid")
                                        .executes(context -> queryUUID(
                                                context.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos")
                                        ))
                                )
                                .then(Commands.literal("name")
                                        .executes(context -> queryName(
                                                context.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos")
                                        ))
                                )
                        )
                )
                .then(Commands.literal("update")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .executes(context -> updateHead(
                                                context.getSource(),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                GameProfileArgument
                                                        .getGameProfiles(context, "player")
                                                        .stream()
                                                        .findFirst()
                                                        .orElseThrow()
                                        ))
                                )
                                .executes(context -> updateHead(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos")
                                ))
                        )
                )
        );
    }

    private static int give(
            CommandSourceStack source,
            Collection<ServerPlayer> targets,
            Collection<ResolvableProfile> profiles
    ) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer player : targets) {
            for (ResolvableProfile profile : profiles) {
                final ItemStack stack = Items.PLAYER_HEAD.getDefaultInstance();
                stack.set(DataComponents.PROFILE, profile);

                player.addItem(stack);
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.head.give.success"), false);
        } else {
            throw GIVE_EXCEPTION.create();
        }

        return i;
    }

    private static int give(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayer();

        if (player == null) {
            throw GIVE_EXCEPTION.create();
        }

        return give(source, List.of(player), List.of(ResolvableProfile.createResolved(player.getGameProfile())));
    }

    private static int queryUUID(CommandSourceStack source, BlockPos pos) {
        ServerLevel world = source.getLevel();

        if (world.getBlockEntity(pos) instanceof SkullBlockEntity head) {
            ResolvableProfile owner = head.getOwnerProfile();

            if (owner == null) {
                return -1;
            }

            source.sendSuccess(() -> copyText(
                    "commands.decree.head.query.uuid.success",
                    owner.partialProfile().id().toString()),
                    false
            );
        }

        return 1;
    }

    private static int queryName(CommandSourceStack source, BlockPos pos) {
        ServerLevel world = source.getLevel();

        if (world.getBlockEntity(pos) instanceof SkullBlockEntity head) {
            ResolvableProfile owner = head.getOwnerProfile();

            if (owner == null) {
                return -1;
            }

            source.sendSuccess(() -> copyText(
                    "commands.decree.head.query.name.success",
                    owner.partialProfile().name()),
                    false
            );
        }

        return 1;
    }

    private static Component copyText(String key, String copyText) {
        return Component.translatable(
                key,
                ComponentUtils.wrapInSquareBrackets(Component.literal(copyText)
                        .withStyle((style) -> style
                                .withColor(ChatFormatting.GREEN)
                                .withClickEvent(new ClickEvent.CopyToClipboard(
                                        copyText
                                ))
                                .withHoverEvent(new HoverEvent.ShowText(
                                        Component.translatable(copyText)
                                ))
                                .withInsertion(copyText)
                        )
                )
        );
    }

    private static int updateHead(
            CommandSourceStack source,
            BlockPos pos,
            ResolvableProfile profile
    ) throws CommandSyntaxException {
        if (source.getLevel().getBlockEntity(pos) instanceof SkullBlockEntityMutator entity) {
            entity.decree$setOwner(profile);
            entity.decree$updateSkin();

            source.sendSuccess(() -> Component.translatable("commands.decree.head.update.success"), false);

            return 1;
        } else {
            throw UPDATE_EXCEPTION.create();
        }
    }

    private static int updateHead(
            CommandSourceStack source,
            BlockPos pos,
            NameAndId playerConfigEntry
    ) throws CommandSyntaxException {
        return updateHead(source, pos, ResolvableProfile.createUnresolved(playerConfigEntry.id()));
    }

    private static int updateHead(CommandSourceStack source, BlockPos pos) throws CommandSyntaxException {
        if (source.getLevel().getBlockEntity(pos) instanceof SkullBlockEntityMutator entity) {
            entity.decree$updateSkin();
            source.sendSuccess(() -> Component.translatable("commands.decree.head.update.success"), false);

            return 1;
        } else {
            throw UPDATE_EXCEPTION.create();
        }
    }
}
