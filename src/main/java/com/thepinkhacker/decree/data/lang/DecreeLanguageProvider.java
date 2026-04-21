package com.thepinkhacker.decree.data.lang;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.thepinkhacker.decree.Decree;
import com.thepinkhacker.decree.server.command.*;
import com.thepinkhacker.decree.world.DecreeGameRules;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.concurrent.CompletableFuture;

public class DecreeLanguageProvider extends FabricLanguageProvider {
    public DecreeLanguageProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
        add(
                builder,
                DecreeGameRules.ENDERMAN_PICKUP,
                "Do Enderman Pickup",
                "Allow Endermen to pickup blocks."
        );
        add(
                builder,
                DecreeGameRules.ENDERMAN_PLACE,
                "Do Enderman Place",
                "Allow Endermen to place blocks."
        );
        add(
                builder,
                DecreeGameRules.ITEM_DESPAWN_AGE,
                "Item Despawn Age",
                "Controls how long it takes for an item to despawn."
        );
        add(
                builder,
                DecreeGameRules.NETHER_PORTAL_MOB_SPAWN,
                "Do Nether Portal Mob Spawn",
                "Enables zombified piglins randomly spawning from nether portals in the overworld."
        );

        add(
                builder,
                DecreeGameRules.DO_MOB_EXPLOSION_GRIEFING,
                "Do Mob Explosions Griefing",
                "Whether monster explosions damage the level."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_EMPTY,
                "Max Empty Minecart Speed",
                "Override the max speed for empty minecarts."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_RIDER,
                "Max Rider Minecart Speed",
                "Override the max speed for minecarts with riders."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_CHEST,
                "Max Minecart with Chest Speed",
                "Override the max speed for minecarts with chests."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_FURNACE,
                "Max Minecart with Furnace Speed",
                "Override the max speed for minecarts with furnaces."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_HOPPER,
                "Max Minecart with Hopper Speed",
                "Override the max speed for minecarts with hoppers."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_TNT,
                "Max Minecart with TNT Speed",
                "Override the max speed for minecarts with TNT."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_COMMAND_BLOCK,
                "Max Minecart with Command Block Speed",
                "Override the max speed for minecarts with command blocks."
        );

        add(
                builder,
                DecreeGameRules.MINECART_MAX_SPEED_SPAWNER,
                "Max Minecart with Monster Spawner Speed",
                "Override the max speed for minecarts with monster spawners."
        );

        add(
                builder,
                DecreeGameRules.MINECART_DISMOUNT_HALT_COOLDOWN,
                "Minecart Dismount Halt Cooldown",
                "On dismount, for how many ticks a minecart should halt."
        );

        add(
                builder,
                DecreeGameRules.MINECART_HALT_FACTOR,
                "Minecart Halt Factor",
                "The factor at which minecarts slow down."
        );

        add(
                builder,
                DecreeGameRules.MINECART_DISMOUNT_HALT_FACTOR,
                "Minecart Dismount Halt Factor",
                "The factor at which minecarts slow down after dismounting."
        );

        add(builder, NameCommand.ENTITY_EXCEPTION, "Failed to name entity");
        add(builder, NameCommand.ENTITY_REMOVE_EXCEPTION, "Failed to remove entity name");
        add(builder, NameCommand.ITEM_EXCEPTION, "Failed to name item");
        add(builder, NameCommand.ITEM_REMOVE_EXCEPTION, "Failed to remove item name");

        add(builder, HeadCommand.GIVE_EXCEPTION, "Failed to give head to player");
        add(builder, HeadCommand.UPDATE_EXCEPTION, "Failed to update head block");

        add(builder, RideCommand.EVICT_RIDERS_EXCEPTION, "Failed to evict rider(s)");
        add(builder, RideCommand.START_RIDING_EXCEPTION, "Failed to ride the entity");
        add(builder, RideCommand.STOP_RIDING_EXCEPTION, "Failed to dismount the entity");
        add(builder, RideCommand.SUMMON_RIDE_EXCEPTION, "Failed to summon a ride");
        add(builder, RideCommand.SUMMON_RIDER_EXCEPTION, "Failed to summon a rider");

        add(builder, HealthCommand.SET_HEALTH_EXCEPTION, "Failed to set health");
        add(builder, HealthCommand.ADD_HEALTH_EXCEPTION, "Failed to add health");
        add(builder, HealthCommand.QUERY_HEALTH_EXCEPTION, "Failed to query health");

        add(builder, SetOwnerCommand.EXCEPTION, "Failed to set pet's owner");

        add(builder, HungerCommand.ADD_EXHAUSTION_EXCEPTION, "Failed to add exhaustion");
        add(builder, HungerCommand.ADD_FOOD_EXCEPTION, "Failed to add food");
        add(builder, HungerCommand.ADD_SATURATION_EXCEPTION, "Failed to add saturation");
        add(builder, HungerCommand.SET_EXHAUSTION_EXCEPTION, "Failed to set exhaustion");
        add(builder, HungerCommand.SET_FOOD_EXCEPTION, "Failed to set food");
        add(builder, HungerCommand.SET_SATURATION_EXCEPTION, "Failed to set saturation");

        GenericTranslationBuilder.of(builder)
                .child(GenericTranslationBuilder.Node.of("commands")
                        .child(GenericTranslationBuilder.Node.of(Decree.MOD_ID)
                                .child(GenericTranslationBuilder.Node.of("clearspawnpoint")
                                        .child("failed", "Failed to clear spawnpoint")
                                        .child("success", "Cleared spawnpoint of %s player(s)")
                                )
                                .child(GenericTranslationBuilder.Node.of("ride")
                                        .child(GenericTranslationBuilder.Node.of("evict_riders")
                                                .child("success", "Evicted rider(s)")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("start_riding")
                                                .child("success", "Mounted entities")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("stop_riding")
                                                .child("success", "Dismounted entities")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("summon_ride")
                                                .child("success", "Summoned and mounted to a %s")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("summon_rider")
                                                .child("success", "Summoned and mounted a %s")
                                        )
                                )
                                .child(GenericTranslationBuilder.Node.of("stop")
                                        .child(GenericTranslationBuilder.Node.of("cancel")
                                                .child("failed", "Failed to cancel server stop")
                                                .child("success", "Canceled server stop")
                                        )
                                        .child("immediate", "Stopping server now")
                                        .child("stop", "Stopped server")
                                        .child("time", "Stopping server in %s second(s)...")
                                )
                                .child(GenericTranslationBuilder.Node.of("daylock")
                                        .child("enabled", "Enabled daylock")
                                        .child("disabled", "Disabled daylock")
                                )
                                .child(GenericTranslationBuilder.Node.of("gamerulepreset")
                                        .child(GenericTranslationBuilder.Node.of("load")
                                                .child("changed", "Changed gamerule %s: %s -> %s")
                                                .child("failed", "Failed to load gamerule preset: %s")
                                                .child("unchanged", "No gamerules were affected by: %s")
                                                .child("success", "Loaded gamerule preset: %s")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("save")
                                                .child("success", "Saved gamerule preset: %s")
                                        )
                                )
                                .child(GenericTranslationBuilder.Node.of("head")
                                        .child(GenericTranslationBuilder.Node.of("give")
                                                .child("success", "Gave head to player")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("query")
                                                .child(GenericTranslationBuilder.Node.of("name")
                                                        .child("success", "The head belongs to %s")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("uuid")
                                                        .child("success", "The head's owner's UUID is %s")
                                                )
                                        )
                                        .child(GenericTranslationBuilder.Node.of("update")
                                                .child("success", "Updated head block")
                                        )
                                )
                                .child(GenericTranslationBuilder.Node.of("health")
                                        .child(GenericTranslationBuilder.Node.of("add")
                                                .child("success", "Added %s health")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("query")
                                                .child("success", "Health is %s")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("set")
                                                .child("success", "Set health to %s")
                                        )
                                )
                                .child(GenericTranslationBuilder.Node.of("hunger")
                                        .child(GenericTranslationBuilder.Node.of("add")
                                                .child(GenericTranslationBuilder.Node.of("exhaustion")
                                                        .child("success", "Added %s exhaustion")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("food")
                                                        .child("success", "Added %s food")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("saturation")
                                                        .child("success", "Added %s saturation")
                                                )
                                        )
                                        .child(GenericTranslationBuilder.Node.of("query")
                                                .child(GenericTranslationBuilder.Node.of("exhaustion")
                                                        .child("success", "Player's exhaustion is %s")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("food")
                                                        .child("success", "Player's food is %s")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("saturation")
                                                        .child("success", "Player's saturation is %s")
                                                )
                                        )
                                        .child(GenericTranslationBuilder.Node.of("set")
                                                .child(GenericTranslationBuilder.Node.of("exhaustion")
                                                        .child("success", "Set exhaustion to %s")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("food")
                                                        .child("success", "Set food to %s")
                                                )
                                                .child(GenericTranslationBuilder.Node.of("saturation")
                                                        .child("success", "Set saturation to %s")
                                                )
                                        )
                                )
                                .child(GenericTranslationBuilder.Node.of("name")
                                        .child(GenericTranslationBuilder.Node.of("entity")
                                                // TODO: Color name
                                                .child("name.success", "Named entity to \"%s\"")
                                                .child("remove.success", "Removed entity name")
                                        )
                                        .child(GenericTranslationBuilder.Node.of("item")
                                                // TODO: Color name
                                                .child("name.success", "Named item to \"%s\"")
                                                .child("remove.success", "Removed item name")
                                        )
                                )
                                .child(GenericTranslationBuilder.Node.of("setowner")
                                        .child("success", "Set pet's owner to %s")
                                )
                        )
                )
                .build();
    }

    private static <T> void add(
            TranslationBuilder builder,
            GameRule<T> gamerule,
            String title,
            String description
    ) {
        add(builder, gamerule, title);
        builder.add(gamerule.getDescriptionId() + ".description", description);
    }

    private static <T> void add(TranslationBuilder builder, GameRule<T> gamerule, String title) {
        builder.add(gamerule.getDescriptionId(), title);
    }

    private static void add(TranslationBuilder builder, SimpleCommandExceptionType exception, String value) {
        builder.add(exception.toString(), value);
    }
}
