package com.thepinkhacker.decree.mixin.world.entity.vehicle;

import com.thepinkhacker.decree.world.DecreeGameRules;
import com.thepinkhacker.decree.world.entity.vehicle.DismountStopCooldown;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements DismountStopCooldown {
    @Unique
    private int dismountCooldown;

    @Unique
    private void zeroVelocity() {
        AbstractMinecart self = (AbstractMinecart)(Object)this;
        self.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void decree$startStopCooldown(GameRules rules) {
        int ruleValue = rules.get(DecreeGameRules.MINECART_DISMOUNT_STOP_COOLDOWN);

        if (ruleValue >= 0) {
            zeroVelocity();
            dismountCooldown = ruleValue;
        }
    }

    @Override
    public void decree$cancelCooldown() {
        dismountCooldown = 0;
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void decree$tick(CallbackInfo ci) {
        if (dismountCooldown > 0) {
            zeroVelocity();
            dismountCooldown--;
        }
    }
}
