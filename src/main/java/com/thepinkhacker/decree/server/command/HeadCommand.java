package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.thepinkhacker.decree.block.entity.SkullBlockEntityMutator;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.List;

public class HeadCommand implements CommandRegistrationCallback {

    private static final SimpleCommandExceptionType GIVE_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("commands.decree.head.give.failed")
    );
    private static final SimpleCommandExceptionType UPDATE_EXCEPTION = new SimpleCommandExceptionType(
            Text.translatable("commands.decree.head.update.failed")
    );

    @Override
    public void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess registryAccess,
            CommandManager.RegistrationEnvironment environment
    ) {
        LiteralCommandNode<ServerCommandSource> node = DecreeUtils.register(dispatcher, CommandConfigs.HEAD, command -> command
                .then(CommandManager.literal("give")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(context -> give(
                                                context.getSource(),
                                                EntityArgumentType.getPlayers(context, "targets"),
                                                GameProfileArgumentType
                                                        .getProfileArgument(context, "player")
                                                        .stream()
                                                        .map(PlayerConfigEntry::name)
                                                        .map(ProfileComponent::ofDynamic)
                                                        .toList()
                                        ))
                                )
                        )
                        .executes(context -> give(
                                context.getSource()
                        ))
                )
                .then(CommandManager.literal("query")
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .then(CommandManager.literal("uuid")
                                        .executes(context -> queryUUID(
                                                context.getSource(),
                                                BlockPosArgumentType.getLoadedBlockPos(context, "pos")
                                        ))
                                )
                                .then(CommandManager.literal("name")
                                        .executes(context -> queryName(
                                                context.getSource(),
                                                BlockPosArgumentType.getLoadedBlockPos(context, "pos")
                                        ))
                                )
                        )
                )
                .then(CommandManager.literal("update")
                        .requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(context -> updateHead(
                                                context.getSource(),
                                                BlockPosArgumentType.getLoadedBlockPos(context, "pos"),
                                                GameProfileArgumentType
                                                        .getProfileArgument(context, "player")
                                                        .stream()
                                                        .findFirst()
                                                        .orElseThrow()
                                        ))
                                )
                                .executes(context -> updateHead(
                                        context.getSource(),
                                        BlockPosArgumentType.getLoadedBlockPos(context, "pos")
                                ))
                        )
                )
        );
    }

    private static int give(
            ServerCommandSource source,
            Collection<ServerPlayerEntity> targets,
            Collection<ProfileComponent> profiles
    ) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayerEntity player : targets) {
            for (ProfileComponent profile : profiles) {
                final ItemStack stack = Items.PLAYER_HEAD.getDefaultStack();
                stack.set(DataComponentTypes.PROFILE, profile);

                player.giveItemStack(stack);
                i++;
            }
        }

        if (i > 0) {
            source.sendFeedback(() -> Text.translatable("commands.decree.head.give.success"), false);
        } else {
            throw GIVE_EXCEPTION.create();
        }

        return i;
    }

    private static int give(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) {
            throw GIVE_EXCEPTION.create();
        }

        return give(source, List.of(player), List.of(ProfileComponent.ofStatic(player.getGameProfile())));
    }

    private static int queryUUID(ServerCommandSource source, BlockPos pos) {
        ServerWorld world = source.getWorld();

        if (world.getBlockEntity(pos) instanceof SkullBlockEntity head) {
            ProfileComponent owner = head.getOwner();

            if (owner == null) {
                return -1;
            }

            source.sendFeedback(() -> copyText(
                    "commands.decree.head.query.uuid.success",
                    owner.getGameProfile().id().toString()),
                    false
            );
        }

        return 1;
    }

    private static int queryName(ServerCommandSource source, BlockPos pos) {
        ServerWorld world = source.getWorld();

        if (world.getBlockEntity(pos) instanceof SkullBlockEntity head) {
            ProfileComponent owner = head.getOwner();

            if (owner == null) {
                return -1;
            }

            source.sendFeedback(() -> copyText(
                    "commands.decree.head.query.name.success",
                    owner.getGameProfile().name()),
                    false
            );
        }

        return 1;
    }

    private static Text copyText(String key, String copyText) {
        return Text.translatable(
                key,
                Texts.bracketed(Text.literal(copyText)
                        .styled((style) -> style
                                .withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent.CopyToClipboard(
                                        copyText
                                ))
                                .withHoverEvent(new HoverEvent.ShowText(
                                        Text.translatable(copyText)
                                ))
                                .withInsertion(copyText)
                        )
                )
        );
    }

    private static int updateHead(
            ServerCommandSource source,
            BlockPos pos,
            ProfileComponent profile
    ) throws CommandSyntaxException {
        if (source.getWorld().getBlockEntity(pos) instanceof SkullBlockEntityMutator entity) {
            entity.decree$setOwner(profile);
            entity.decree$updateSkin();

            source.sendFeedback(() -> Text.translatable("commands.decree.head.update.success"), false);

            return 1;
        } else {
            throw UPDATE_EXCEPTION.create();
        }
    }

    private static int updateHead(
            ServerCommandSource source,
            BlockPos pos,
            PlayerConfigEntry playerConfigEntry
    ) throws CommandSyntaxException {
        return updateHead(source, pos, ProfileComponent.ofDynamic(playerConfigEntry.id()));
    }

    private static int updateHead(ServerCommandSource source, BlockPos pos) throws CommandSyntaxException {
        if (source.getWorld().getBlockEntity(pos) instanceof SkullBlockEntityMutator entity) {
            entity.decree$updateSkin();
            source.sendFeedback(() -> Text.translatable("commands.decree.head.update.success"), false);

            return 1;
        } else {
            throw UPDATE_EXCEPTION.create();
        }
    }
}
