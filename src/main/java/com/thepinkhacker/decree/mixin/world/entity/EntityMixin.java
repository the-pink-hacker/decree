package com.thepinkhacker.decree.mixin.world.entity;

import com.thepinkhacker.decree.world.entity.vehicle.DismountStopCooldown;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(
            method = "addPassenger",
            at = @At("HEAD")
    )
    private void addPassengers(Entity passenger, CallbackInfo ci) {
        if (this instanceof DismountStopCooldown cooldown) {
            cooldown.decree$cancelCooldown();
        }
    }
}
