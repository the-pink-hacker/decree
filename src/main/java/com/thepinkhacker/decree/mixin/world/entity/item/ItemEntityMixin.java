package com.thepinkhacker.decree.mixin.world.entity.item;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(
            method = "tick()V",
            constant = @Constant(intValue = 6_000)
    )
    private int decree$despawnAgeTick(int age) {
        return decree$getItemDespawnAge();
    }

    @ModifyConstant(
            method = "isMergable()Z",
            constant = @Constant(intValue = 6_000)
    )
    private int decree$despawnAgeCanMerge(int age) {
        return decree$getItemDespawnAge();
    }

    @ModifyConstant(
            method = "setExtendedLifetime()V",
            constant = @Constant(intValue = -6_000)
    )
    private int decree$despawnAgeConvertedItem(int age) {
        return -decree$getItemDespawnAge();
    }

    @ModifyConstant(
            method = "makeFakeItem()V",
            constant = @Constant(intValue = 5_999)
    )
    private int decree$despawnAgeDespawnImmediately(int age) {
        return decree$getItemDespawnAge() - 1;
    }

    @Unique
    private int decree$getItemDespawnAge() {
        // Only called in server context
        ServerLevel serverWorld = (ServerLevel)this.level();
        return serverWorld.getGameRules().get(DecreeGameRules.ITEM_DESPAWN_AGE);
    }
}
