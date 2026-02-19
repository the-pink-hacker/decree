package com.thepinkhacker.decree.mixin.world.entity.vehicle;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
    protected NewMinecartBehaviorMixin(AbstractMinecart abstractMinecart) {
        super(abstractMinecart);
    }

    @Redirect(
            method = "getMaxSpeed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
            )
    )
    private Object decree$getMaxSpeed(GameRules instance, GameRule<Integer> rule) {
        boolean hasRider = this.minecart.hasExactlyOnePlayerPassenger();

        int speed = instance.get(
                hasRider ? DecreeGameRules.MINECART_MAX_SPEED_RIDER : DecreeGameRules.MINECART_MAX_SPEED_EMPTY
        );

        return speed == 0 ? instance.get(rule) : speed;
    }
}
