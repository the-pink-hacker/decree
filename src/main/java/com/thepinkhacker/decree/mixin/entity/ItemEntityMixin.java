package com.thepinkhacker.decree.mixin.entity;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @ModifyConstant(
            method = "tick()V",
            constant = @Constant(intValue = 6_000)
    )
    private int despawnAgeTick(int age) {
        return $decree$getItemDespawnAge();
    }

    @ModifyConstant(
            method = "canMerge()Z",
            constant = @Constant(intValue = 6_000)
    )
    private int despawnAgeCanMerge(int age) {
        return $decree$getItemDespawnAge();
    }

    @ModifyConstant(
            method = "setCovetedItem()V",
            constant = @Constant(intValue = -6_000)
    )
    private int despawnAgeConvertedItem(int age) {
        return -$decree$getItemDespawnAge();
    }

    @ModifyConstant(
            method = "setDespawnImmediately()V",
            constant = @Constant(intValue = 5_999)
    )
    private int despawnAgeDespawnImmediately(int age) {
        return $decree$getItemDespawnAge() - 1;
    }

    @Unique
    private int $decree$getItemDespawnAge() {
        World world = ((ItemEntity)(Object)this).getEntityWorld();
        // Only called in server context
        ServerWorld serverWorld = (ServerWorld)world;
        return serverWorld.getGameRules().getValue(DecreeGameRules.ITEM_DESPAWN_AGE);
    }
}
