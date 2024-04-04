package com.ryangar46.commandsplus.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public class RideCommand {
    private static final SimpleCommandExceptionType START_RIDING_FAILED = new SimpleCommandExceptionType(Text.translatable("command.ride.start_riding.fail"));
    private static final SimpleCommandExceptionType STOP_RIDING_FAILED = new SimpleCommandExceptionType(Text.translatable("command.ride.stop_riding.fail"));
    private static final SimpleCommandExceptionType EVICT_RIDERS_FAILED = new SimpleCommandExceptionType(Text.translatable("command.ride.evict_riders.fail"));
    private static final SimpleCommandExceptionType SUMMON_RIDER_FAILED = new SimpleCommandExceptionType(Text.translatable("command.ride.summon_rider.fail"));
    private static final SimpleCommandExceptionType SUMMON_RIDE_FAILED = new SimpleCommandExceptionType(Text.translatable("command.ride.summon_ride.fail"));
    private static final SimpleCommandExceptionType FAILED_UUID_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.summon.failed.uuid"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Todo: Add optional fields from bedrock edition
        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(CommandManager.literal("ride")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("riders", EntityArgumentType.entities())
                        .then(CommandManager.literal("start_riding")
                                .then(CommandManager.argument("ride", EntityArgumentType.entity())
                                        .executes(context -> startRiding(
                                                context.getSource(),
                                                EntityArgumentType.getEntities(context, "riders"),
                                                EntityArgumentType.getEntity(context, "ride"))
                                        )
                                )
                        )
                        .then(CommandManager.literal("stop_riding")
                                .executes(context -> stopRiding(
                                        context.getSource(),
                                        EntityArgumentType.getEntities(context, "riders"))
                                )
                        )
                        .then(CommandManager.literal("evict_riders")
                                .executes(context -> evictRiders(
                                        context.getSource(),
                                        EntityArgumentType.getEntities(context, "riders"))
                                )
                        )
                        .then(CommandManager.literal("summon_rider")
                                .then(CommandManager.argument("entity", EntitySummonArgumentType.entitySummon())
                                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .executes(context -> summonRider(
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "riders"),
                                                EntitySummonArgumentType.getEntitySummon(context, "entity"))
                                        )
                                )
                        )
                        .then(CommandManager.literal("summon_ride")
                                .then(CommandManager.argument("entity", EntitySummonArgumentType.entitySummon())
                                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                        .executes(context -> summonRide(
                                                context.getSource(),
                                                EntityArgumentType.getEntity(context, "riders"),
                                                EntitySummonArgumentType.getEntitySummon(context, "entity"))
                                        )
                                )
                        )
                )
        );

        dispatcher.register(CommandManager.literal("mount").redirect(node));
    }

    /**
     * Makes "riders" ride on "ride"
     */
    private static int startRiding(ServerCommandSource source, Collection<? extends Entity> riders, Entity ride) throws CommandSyntaxException {
        int i = 0;

        for (Entity rider : riders) {
            if (!rider.hasVehicle()) {
                rider.startRiding(ride);
                i++;
            }

            ride = rider;
        }

        if (i > 1) {
            source.sendFeedback(Text.translatable("command.ride.start_riding.success.multiple", i), false);
        } else if (i == 1) {
            source.sendFeedback(Text.translatable("command.ride.start_riding.success.single", i), false);
        } else {
            throw START_RIDING_FAILED.create();
        }

        return i;
    }

    /**
     * Makes "targets" dismount their vehicle
     */
    private static int stopRiding(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        int i = 0;

        for (Entity rider : targets) {
            if (rider.hasVehicle()) {
                rider.dismountVehicle();
                i++;
            }
        }

        if (i > 1) {
            source.sendFeedback(Text.translatable("command.ride.stop_riding.success.multiple", i), false);
        } else if (i == 1) {
            source.sendFeedback(Text.translatable("command.ride.stop_riding.success.single", i), false);
        } else {
            throw STOP_RIDING_FAILED.create();
        }

        return i;
    }

    /**
     * Makes entities that are rinding on "targets" dismount
     */
    private static int evictRiders(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
        int i = 0;

        for (Entity rider : targets) {
            rider.removeAllPassengers();
            i++;
        }

        if (i > 1) {
            source.sendFeedback(Text.translatable("command.ride.evict_riders.success.multiple", i), false);
        } else if (i == 1) {
            source.sendFeedback(Text.translatable("command.ride.evict_riders.success.single", i), false);
        } else {
            throw EVICT_RIDERS_FAILED.create();
        }

        return i;
    }

    /**
     * Summon an entity that will ride on "ride"
     */
    private static int summonRider(ServerCommandSource source, Entity ride, Identifier id) throws CommandSyntaxException {
        if (!ride.hasPassengers()) {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("id", id.toString());

            ServerWorld world = source.getWorld();

            Vec3d ridePos = ride.getPos();

            Entity rider = EntityType.loadEntityWithPassengers(nbt, world, entity -> {
                entity.setPosition(ridePos);
                return entity;
            });

            if (rider != null) {
                if (rider instanceof MobEntity mobEntity) {
                    mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.COMMAND, null, null);
                }

                if (world.spawnNewEntityAndPassengers(rider)) {
                    rider.startRiding(ride);
                    source.sendFeedback(Text.translatable("command.ride.summon_rider.success", rider.getDisplayName()), true);
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
    private static int summonRide(ServerCommandSource source, Entity rider, Identifier id) throws CommandSyntaxException {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id.toString());

        ServerWorld world = source.getWorld();

        Vec3d riderPos = rider.getPos();

        Entity ride = EntityType.loadEntityWithPassengers(nbt, world, entity -> {
            entity.setPosition(riderPos);
            return entity;
        });

        if (ride != null) {
            if (ride instanceof MobEntity mobEntity) {
                mobEntity.initialize(world, world.getLocalDifficulty(mobEntity.getBlockPos()), SpawnReason.COMMAND, null, null);
            }

            if (world.spawnNewEntityAndPassengers(ride)) {
                rider.startRiding(ride);
                source.sendFeedback(Text.translatable("command.ride.summon_ride.success", ride.getDisplayName()), true);
                return 1;
            } else {
                throw FAILED_UUID_EXCEPTION.create();
            }
        }

        throw SUMMON_RIDE_FAILED.create();
    }
}
