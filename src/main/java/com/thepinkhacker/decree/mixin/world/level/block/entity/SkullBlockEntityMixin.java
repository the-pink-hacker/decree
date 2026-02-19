package com.thepinkhacker.decree.mixin.world.level.block.entity;

import com.thepinkhacker.decree.world.level.block.entity.SkullBlockEntityMutator;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SkullBlockEntity.class)
public abstract class SkullBlockEntityMixin extends BlockEntity implements SkullBlockEntityMutator {
    @Shadow
    private @Nullable ResolvableProfile owner;

    public SkullBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void decree$setOwner(ResolvableProfile owner) {
        this.owner = owner;
    }

    @Override
    public void decree$updateSkin() {
        if (this.getLevel() instanceof ServerLevel level) {
            for (ServerPlayer player : PlayerLookup.tracking(level, this.getBlockPos())) {
                player.connection.send(this.getUpdatePacket());
            }
        }
    }
}
