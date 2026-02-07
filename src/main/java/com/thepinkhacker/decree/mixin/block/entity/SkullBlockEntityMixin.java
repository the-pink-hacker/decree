package com.thepinkhacker.decree.mixin.block.entity;

import com.thepinkhacker.decree.block.entity.SkullBlockEntityMutator;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SkullBlockEntity.class)
public abstract class SkullBlockEntityMixin implements SkullBlockEntityMutator {
    @Shadow
    private @Nullable ProfileComponent owner;

    @Shadow
    public abstract BlockEntityUpdateS2CPacket toUpdatePacket();

    @Override
    public void decree$setOwner(ProfileComponent owner) {
        this.owner = owner;
    }

    @Override
    public void decree$updateSkin() {
        SkullBlockEntity entity = ((SkullBlockEntity)(Object)this);
        if (entity.getWorld() instanceof ServerWorld world) {
            for (ServerPlayerEntity player : PlayerLookup.tracking(world, entity.getPos())) {
                player.networkHandler.sendPacket(this.toUpdatePacket());
            }
        }
    }
}
