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
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class RideCommand implements CommandRegistrationCallback {
    private static final SimpleCommandExceptionType START_RIDING_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.ride.start_riding.failed"));
    private static final SimpleCommandExceptionType STOP_RIDING_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.ride.stop_riding.failed"));
    private static final SimpleCommandExceptionType EVICT_RIDERS_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.ride.evict_riders.failed"));
    private static final SimpleCommandExceptionType SUMMON_RIDER_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.ride.summon_rider.failed"));
    private static final SimpleCommandExceptionType SUMMON_RIDE_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.decree.ride.summon_ride.failed"));
    private static final SimpleCommandExceptionType FAILED_UUID_EXCEPTION = new SimpleCommandExceptionType(Component.translatable("commands.decree.summon.failed.uuid"));

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        /*
         * Todo: Add optional fields from bedrock edition
         *  - nameTag
         *  - rideRules
         */
        LiteralCommandNode<CommandSourceStack> node = DecreeUtils.register(dispatcher, CommandConfigs.RIDE, command -> command
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("riders", EntityArgument.entities())
                        .then(Commands.literal("start_riding")
                                .then(Commands.argument("ride", EntityArgument.entity())
                                        .then(Commands.literal("teleport_ride")
                                                .executes(context -> startRiding(
                                                        context.getSource(),
                                                        EntityArgument.getEntities(context, "riders"),
                                                        EntityArgument.getEntity(context, "ride"),
                                                        TeleportRule.TELEPORT_RIDE
                                                ))
                                        )
                                        .then(Commands.literal("teleport_rider")
                                                .executes(context -> startRiding(
                                                        context.getSource(),
                                                        EntityArgument.getEntities(context, "riders"),
                                                        EntityArgument.getEntity(context, "ride"),
                                                        TeleportRule.TELEPORT_RIDER
                                                ))
                                        )
                                        .executes(context -> startRiding(
                                                context.getSource(),
                                                EntityArgument.getEntities(context, "riders"),
                                                EntityArgument.getEntity(context, "ride")
                                        ))
                                )
                        )
                        .then(Commands.literal("stop_riding")
                                .executes(context -> stopRiding(
                                        context.getSource(),
                                        EntityArgument.getEntities(context, "riders")
                                ))
                        )
                        .then(Commands.literal("evict_riders")
                                .executes(context -> evictRiders(
                                        context.getSource(),
                                        EntityArgument.getEntities(context, "riders")
                                ))
                        )
                        .then(Commands.literal("summon_rider")
                                .then(Commands.argument("entity", ResourceArgument.resource(registryAccess, Registries.ENTITY_TYPE))
                                        .suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES))
                                        .executes(context -> summonRider(
                                                context.getSource(),
                                                EntityArgument.getEntity(context, "riders"),
                                                ResourceArgument.getSummonableEntityType(context, "entity")
                                        ))
                                )
                        )
                        .then(Commands.literal("summon_ride")
                                .then(Commands.argument("entity", ResourceArgument.resource(registryAccess, Registries.ENTITY_TYPE))
                                        .suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES))
                                        .executes(context -> summonRide(
                                                context.getSource(),
                                                EntityArgument.getEntity(context, "riders"),
                                                ResourceArgument.getSummonableEntityType(context, "entity")
                                        ))
                                )
                        )
                )
        );
    }

    /**
     * Makes "riders" ride on "ride"
     */
    private static int startRiding(
            CommandSourceStack source,
            Collection<? extends Entity> riders,
            Entity ride,
            TeleportRule teleportRule
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity rider : riders) {
            // Teleports ride to rider based on teleport rule
            if (teleportRule == TeleportRule.TELEPORT_RIDE) ride.setPos(rider.position());

            if (!rider.isPassenger()) {
                rider.startRiding(ride);
                i++;
            }

            ride = rider;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.ride.start_riding.success"), false);
        } else {
            throw START_RIDING_FAILED.create();
        }

        return i;
    }

    private static int startRiding(
            CommandSourceStack source,
            Collection<? extends Entity> riders,
            Entity ride
    ) throws CommandSyntaxException {
        return startRiding(source, riders, ride, TeleportRule.TELEPORT_RIDER);
    }

    /**
     * Makes "targets" dismount their vehicle
     */
    private static int stopRiding(
            CommandSourceStack source,
            Collection<? extends Entity> targets
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity rider : targets) {
            if (rider.isPassenger()) {
                rider.removeVehicle();
                i++;
            }
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.ride.stop_riding.success"), false);
        } else {
            throw STOP_RIDING_FAILED.create();
        }

        return i;
    }

    /**
     * Makes entities that are rinding on "targets" dismount
     */
    private static int evictRiders(
            CommandSourceStack source,
            Collection<? extends Entity> targets
    ) throws CommandSyntaxException {
        int i = 0;

        for (Entity rider : targets) {
            rider.ejectPassengers();
            i++;
        }

        if (i > 0) {
            source.sendSuccess(() -> Component.translatable("commands.decree.ride.evict_riders.success"), false);
        } else {
            throw EVICT_RIDERS_FAILED.create();
        }

        return i;
    }

    /**
     * Summon an entity that will ride on "ride"
     */
    private static int summonRider(
            CommandSourceStack source,
            Entity ride,
            Holder.Reference<EntityType<?>> entityType
    ) throws CommandSyntaxException {
        if (!ride.isVehicle()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("id", entityType.key().identifier().toString());

            ServerLevel world = source.getLevel();

            Vec3 ridePos = ride.position();

            Entity rider = EntityType.loadEntityRecursive(nbt, world, EntitySpawnReason.COMMAND, entity -> {
                entity.setPos(ridePos);
                return entity;
            });

            if (rider != null) {
                if (rider instanceof Mob mobEntity) {
                    mobEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(mobEntity.blockPosition()), EntitySpawnReason.COMMAND, null);
                }

                if (world.tryAddFreshEntityWithPassengers(rider)) {
                    rider.startRiding(ride);
                    source.sendSuccess(() -> Component.translatable("commands.decree.ride.summon_rider.success", rider.getDisplayName()), true);
                    return 1;
                } else {
                    throw FAILED_UUID_EXCEPTION.create();
                }
            }
        }

        throw SUMMON_RIDER_FAILED.create();
    }

    /**
     * Summon an entity that "rider" will ride
     */
    private static int summonRide(
            CommandSourceStack source,
            Entity rider,
            Holder.Reference<EntityType<?>> entityType
    ) throws CommandSyntaxException {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", entityType.key().identifier().toString());

        ServerLevel world = source.getLevel();

        Vec3 riderPos = rider.position();

        Entity ride = EntityType.loadEntityRecursive(nbt, world, EntitySpawnReason.COMMAND, entity -> {
            entity.setPos(riderPos);
            return entity;
        });

        if (ride != null) {
            if (ride instanceof Mob mobEntity) {
                mobEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(mobEntity.blockPosition()), EntitySpawnReason.COMMAND, null);
            }

            if (world.tryAddFreshEntityWithPassengers(ride)) {
                rider.startRiding(ride);
                source.sendSuccess(() -> Component.translatable("commands.decree.ride.summon_ride.success", ride.getDisplayName()), true);
                return 1;
            } else {
                throw FAILED_UUID_EXCEPTION.create();
            }
        }

        throw SUMMON_RIDE_FAILED.create();
    }

    public enum TeleportRule {
        TELEPORT_RIDE,
        TELEPORT_RIDER,
    }
}
