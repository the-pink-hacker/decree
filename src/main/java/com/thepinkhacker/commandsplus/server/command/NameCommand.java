package com.thepinkhacker.commandsplus.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;

public class NameCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("name")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("item")
                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                .then(CommandManager.argument("slot", ItemSlotArgumentType.itemSlot())
                                        .then(CommandManager.argument("name", MessageArgumentType.message())
                                                .executes(context -> nameItem(
                                                        context.getSource(),
                                                        EntityArgumentType.getEntities(context, "targets"),
                                                        ItemSlotArgumentType.getItemSlot(context, "slot"),
                                                        MessageArgumentType.getMessage(context, "name").getString()
                                                ))
                                        )
                                )
                                .then(CommandManager.argument("name", MessageArgumentType.message())
                                        .executes(context -> nameItem(
                                                context.getSource(),
                                                EntityArgumentType.getEntities(context, "targets"),
                                                0,
                                                MessageArgumentType.getMessage(context, "name").getString()
                                        ))
                                )
                        )
                )
                .then(CommandManager.literal("entity")
                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                .then(CommandManager.argument("name", MessageArgumentType.message())
                                        .executes(context -> nameEntity(
                                                context.getSource(),
                                                EntityArgumentType.getEntities(context, "targets"),
                                                MessageArgumentType.getMessage(context, "name").getString()
                                        ))
                                )
                        )
                )
        );
    }

    private static int nameItem(ServerCommandSource source, Collection<? extends Entity> targets, int slot, String name) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            StackReference stackReference = entity.getStackReference(slot);

            if (stackReference != StackReference.EMPTY) {
                ItemStack itemStack = stackReference.get();

                if (!itemStack.isEmpty()) {
                    itemStack.set(DataComponentTypes.ITEM_NAME, Text.of(name));
                    i++;
                }
            }
        }

        if (i > 0) {
            source.sendFeedback(() -> Text.translatable("commands.name.item.success", name), false);
        } else {
            throw new SimpleCommandExceptionType(Text.translatable("commands.name.item.fail")).create();
        }

        return i;
    }

    public static int nameEntity(ServerCommandSource source, Collection<? extends Entity> targets, String name) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setCustomName(Text.of(name));
                livingEntity.setCustomNameVisible(true);
                i++;
            }
        }

        if (i > 0) {
            source.sendFeedback(() -> Text.translatable("commands.name.entity.success", name), false);
        } else {
            throw new SimpleCommandExceptionType(Text.translatable("commands.name.entity.fail")).create();
        }

        return i;
    }
}
