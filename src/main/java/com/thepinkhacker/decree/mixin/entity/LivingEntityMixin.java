package com.thepinkhacker.decree.mixin.entity;

import com.thepinkhacker.decree.registry.tag.DecreeDimensionTypeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private static final EnumSet<PositionFlag> DECREE_GLIDE_FLAGS = EnumSet.allOf(PositionFlag.class);

    @Inject(
            method = "canGlide",
            at = @At("HEAD"),
            cancellable = true
    )
    private void decreeCanGlide(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity.getEntityWorld() instanceof ServerWorld world) {
            if (entity.getEntityWorld().getDimensionEntry().isIn(DecreeDimensionTypeTags.GLIDE_BLACKLIST)) {
                entity.teleport(
                        world,
                        0.0d,
                        0.0d,
                        0.0d,
                        DECREE_GLIDE_FLAGS,
                        0.0f,
                        0.0f,
                        false
                );
                cir.cancel();
            }
        }
    }
}
