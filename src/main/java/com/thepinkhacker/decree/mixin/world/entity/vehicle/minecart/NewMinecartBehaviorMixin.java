package com.thepinkhacker.decree.mixin.world.entity.vehicle.minecart;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        GameRule<Integer> gamerule = switch (this.minecart) {
            case MinecartChest ignored -> DecreeGameRules.MINECART_MAX_SPEED_CHEST;
            case MinecartFurnace ignored -> DecreeGameRules.MINECART_MAX_SPEED_FURNACE;
            case MinecartHopper ignored -> DecreeGameRules.MINECART_MAX_SPEED_HOPPER;
            case MinecartTNT ignored -> DecreeGameRules.MINECART_MAX_SPEED_TNT;
            case MinecartCommandBlock ignored -> DecreeGameRules.MINECART_MAX_SPEED_COMMAND_BLOCK;
            case MinecartSpawner ignored -> DecreeGameRules.MINECART_MAX_SPEED_SPAWNER;
            default -> {
                boolean hasRider = this.minecart.isVehicle();
                yield hasRider ? DecreeGameRules.MINECART_MAX_SPEED_RIDER : DecreeGameRules.MINECART_MAX_SPEED_EMPTY;
            }
        };

        int speed = instance.get(gamerule);
        return speed == -1 ? instance.get(rule) : speed;
    }

    @Inject(
            method = "calculateHaltTrackSpeed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void decree$cooldownStop(
            Vec3 deltaMovement,
            BlockState state,
            CallbackInfoReturnable<Vec3> cir
    ) {
        if (this.minecart.decree$shouldHalt()) {
            Vec3 newDelta;

            if (deltaMovement.length() < 0.03) {
                newDelta = Vec3.ZERO;
            } else {
                ServerLevel level = (ServerLevel)this.level();
                newDelta = deltaMovement.scale(level.getGameRules().get(DecreeGameRules.MINECART_DISMOUNT_HALT_FACTOR));
            }

            cir.setReturnValue(newDelta);
        }
    }

    @ModifyConstant(
            method = "calculateHaltTrackSpeed",
            constant = @Constant(doubleValue = 0.5)
    )
    private double decree$changeHaltFactor(double constant) {
        ServerLevel level = (ServerLevel) this.level();
        return level.getGameRules().get(DecreeGameRules.MINECART_HALT_FACTOR);
    }

    @Redirect(
            method = "calculateBoostTrackSpeed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z")
    )
    @SuppressWarnings("unchecked")
    private boolean decree$overrideBoost(BlockState instance, Object o) {
        return !this.minecart.decree$shouldHalt() && instance.is((Block)o);
    }
}
