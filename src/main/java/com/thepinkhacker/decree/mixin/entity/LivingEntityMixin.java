package com.thepinkhacker.decree.mixin.entity;

import com.thepinkhacker.decree.Decree;
import com.thepinkhacker.decree.registry.tag.DecreeDimensionTypeTags;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(
            method = "canGlide",
            at = @At("HEAD"),
            cancellable = true
    )
    private void decree_can_glide(CallbackInfoReturnable<Boolean> cir) {
        Decree.LOGGER.info(((LivingEntity)(Object)this).getWorld().getDimensionEntry().isIn(DecreeDimensionTypeTags.GLIDE_BLACKLIST));
        if (((LivingEntity)(Object)this).getWorld().getDimensionEntry().isIn(DecreeDimensionTypeTags.GLIDE_BLACKLIST)) {
            cir.cancel();
        }
    }
}
