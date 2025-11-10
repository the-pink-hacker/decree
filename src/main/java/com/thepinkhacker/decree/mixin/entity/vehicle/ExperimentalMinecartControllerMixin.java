package com.thepinkhacker.decree.mixin.entity.vehicle;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperimentalMinecartController.class)
public abstract class ExperimentalMinecartControllerMixin {
    @Inject(
            method = "getMaxSpeed(Lnet/minecraft/server/world/ServerWorld;)D",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameRules;getInt(Lnet/minecraft/world/GameRules$Key;)I"
            ),
            cancellable = true
    )
    public void decreeGetMaxSpeed(ServerWorld world, CallbackInfoReturnable<Double> cir) {
        boolean hasRider = ((ExperimentalMinecartController)(Object)this).minecart.hasPlayerRider();

        if (hasRider) {
            int speed = world.getGameRules().getInt(DecreeGameRules.MINECART_MAX_SPEED_RIDER);

            if (speed != 0) {
                cir.setReturnValue((double) speed);
            }
        } else {
            int speed = world.getGameRules().getInt(DecreeGameRules.MINECART_MAX_SPEED_EMPTY);

            if (speed != 0) {
                cir.setReturnValue((double) speed);
            }
        }
    }
}
