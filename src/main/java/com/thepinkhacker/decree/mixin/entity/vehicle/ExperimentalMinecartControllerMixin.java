package com.thepinkhacker.decree.mixin.entity.vehicle;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ExperimentalMinecartController.class)
@Debug(export = true)
public abstract class ExperimentalMinecartControllerMixin {
    @Redirect(
            method = "getMaxSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/rule/GameRules;getValue(Lnet/minecraft/world/rule/GameRule;)Ljava/lang/Object;"
            )
    )
    private Object decree$GetMaxSpeed(GameRules instance, GameRule<Integer> rule) {
        boolean hasRider = ((ExperimentalMinecartController)(Object)this).minecart.hasPlayerRider();

        int speed = instance.getValue(
                hasRider ? DecreeGameRules.MINECART_MAX_SPEED_RIDER : DecreeGameRules.MINECART_MAX_SPEED_EMPTY
        );

        return speed == 0 ? instance.getValue(rule) : speed;
    }
}
