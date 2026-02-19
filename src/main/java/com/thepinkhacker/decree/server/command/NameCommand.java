package com.thepinkhacker.decree.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.thepinkhacker.decree.util.command.DecreeUtils;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class NameCommand implements CommandRegistrationCallback {
    public static final SimpleCommandExceptionType ITEM_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.name.item.name.failed"));
    public static final SimpleCommandExceptionType ITEM_REMOVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.name.item.remove.failed"));
    public static final SimpleCommandExceptionType ENTITY_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.name.entity.name.failed"));
    public static final SimpleCommandExceptionType ENTITY_REMOVE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.name.entity.remove.failed"));

    @Override
    public void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        DecreeUtils.register(dispatcher, CommandConfigs.NAME, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("item")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(context -> removeNameItem(
                                        context.getSource(),
                                        EntityArgument.getEntities(context, "targets")
                                ))
                                .then(Commands.argument("slot", SlotArgument.slot())
                                        .executes(context -> removeNameItem(
                                                context.getSource(),
                                                EntityArgument.getEntities(context, "targets"),
                                                SlotArgument.getSlot(context, "slot")
                                        ))
                                        .then(Commands.argument("name", MessageArgument.message())
                                                .executes(context -> nameItem(
                                                        context.getSource(),
                                                        EntityArgument.getEntities(context, "targets"),
                                                        SlotArgument.getSlot(context, "slot"),
                                                        MessageArgument.getMessage(context, "name")
                                                ))
                                        )
                                )
                                .then(Commands.argument("name", MessageArgument.message())
                                        .executes(context -> nameItem(
                                                context.getSource(),
                                                EntityArgument.getEntities(context, "targets"),
                                                MessageArgument.getMessage(context, "name")
                                        ))
                                )
                        )
                )
                .then(Commands.literal("entity")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(context -> removeNameEntity(
                                        context.getSource(),
                                        EntityArgument.getEntities(context, "targets")
                                ))
                                .then(Commands.argument("name", MessageArgument.message())
                                        .executes(context -> nameEntity(
                                                context.getSource(),
                                                EntityArgument.getEntities(context, "targets"),
                                                MessageArgument.getMessage(context, "name")
                                        ))
                                )
                        )
                )
        );
    }

    private static int nameItem(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            Component name
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (nameItemTarget(entity, name)) {
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(
                    () -> Component.translatable("commands.decree.name.item.name.success", name),
                    false
            );
            return i;
        } else {
            throw ITEM_FAILED.create();
        }
    }

    private static int nameItem(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            int slot,
            Component name
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (nameItemTarget(entity, slot, name)) {
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(
                    () -> Component.translatable("commands.decree.name.item.name.success", name),
                    false
            );
            return i;
        } else {
            throw ITEM_FAILED.create();
        }
    }

    private static boolean nameItemTarget(Entity entity, int slot, Component name) {
        SlotAccess stack = entity.getSlot(slot);

        if (stack == null || stack.get() == ItemStack.EMPTY) return false;

        return nameItemTarget(stack.get(), name);
    }

    private static boolean nameItemTarget(Entity entity, Component name) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getMainHandItem();
            return nameItemTarget(stack, name);
        } else {
            return nameItemTarget(entity, 0, name);
        }
    }

    private static boolean nameItemTarget(ItemStack stack, Component name) {
        if (stack.isEmpty()) return false;

        stack.set(DataComponents.CUSTOM_NAME, name);
        return true;
    }

    private static int removeNameItem(
            CommandSourceStack source,
            Collection<? extends Entity> targets
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (removeNameItemTarget(entity)) {
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.name.item.remove.success"), false);
            return i;
        } else {
            throw ITEM_REMOVE_FAILED.create();
        }
    }

    private static int removeNameItem(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            int slot
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (removeNameItemTarget(entity, slot)) {
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.name.item.remove.success"), false);
            return i;
        } else {
            throw ITEM_REMOVE_FAILED.create();
        }
    }

    private static boolean removeNameItemTarget(Entity entity, int slot) {
        SlotAccess stack = entity.getSlot(slot);

        if (stack == null || stack.get() == ItemStack.EMPTY) return false;

        return removeNameItemTarget(stack.get());
    }

    private static boolean removeNameItemTarget(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            ItemStack stack = livingEntity.getMainHandItem();
            return removeNameItemTarget(stack);
        } else {
            return false;
        }
    }

    private static boolean removeNameItemTarget(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return stack.remove(DataComponents.CUSTOM_NAME) != null;
    }

    public static int nameEntity(
            CommandSourceStack source,
            Collection<? extends Entity> targets,
            Component name
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.setCustomName(name);
                livingEntity.setCustomNameVisible(true);
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(
                    () -> Component.translatable("commands.decree.name.entity.name.success", name),
                    false
            );
            return i;
        } else {
            throw ENTITY_FAILED.create();
        }
    }

    public static int removeNameEntity(
            CommandSourceStack source,
            Collection<? extends Entity> targets
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (entity.getCustomName() == null) continue;

            entity.setCustomNameVisible(false);
            entity.setCustomName(null);
            i++;
        }

        if (i > 0) {
            source.sendSuccess(
                    () -> Component.translatable("commands.decree.name.entity.remove.success"),
                    false
            );
            return i;
        } else {
            throw ENTITY_REMOVE_FAILED.create();
        }
    }
}
