package com.thepinkhacker.decree.mixin.world.entity.vehicle;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.world.entity.vehicle.minecart.*;
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
}
