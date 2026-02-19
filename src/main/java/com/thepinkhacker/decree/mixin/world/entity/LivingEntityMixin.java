package com.thepinkhacker.decree.mixin.world.entity;

import com.thepinkhacker.decree.registry.tag.DecreeDimensionTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final EnumSet<Relative> DECREE_GLIDE_FLAGS = EnumSet.allOf(Relative.class);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "canGlide",
            at = @At("HEAD"),
            cancellable = true
    )
    private void decree$canGlide(CallbackInfoReturnable<Boolean> cir) {
        if (this.level() instanceof ServerLevel world) {
            if (this.level().dimensionTypeRegistration().is(DecreeDimensionTypeTags.GLIDE_BLACKLIST)) {
                this.teleportTo(
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
