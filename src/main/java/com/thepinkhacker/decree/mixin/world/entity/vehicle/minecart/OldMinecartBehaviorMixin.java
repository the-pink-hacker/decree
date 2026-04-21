package com.thepinkhacker.decree.mixin.world.entity.vehicle.minecart;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(OldMinecartBehavior.class)
public abstract class OldMinecartBehaviorMixin extends MinecartBehavior {
    protected OldMinecartBehaviorMixin(AbstractMinecart minecart) {
        super(minecart);
    }

    @ModifyArgs(
            method = "moveAlongTrack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0
            )
    )
    private void decree$changeHaltFactor(Args args) {
        ServerLevel level = (ServerLevel)this.level();
        double factor = level.getGameRules().get(DecreeGameRules.MINECART_HALT_FACTOR);
        args.set(0, factor);
        args.set(2, factor);
    }
}
