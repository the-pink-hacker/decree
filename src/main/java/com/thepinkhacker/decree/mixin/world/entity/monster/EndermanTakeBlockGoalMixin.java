package com.thepinkhacker.decree.mixin.world.entity.monster;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.EnderMan;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderMan.EndermanTakeBlockGoal.class)
public abstract class EndermanTakeBlockGoalMixin {
    @Shadow @Final private EnderMan enderman;

    @Inject(
            method = "canUse()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/gamerules/GameRules;get(Lnet/minecraft/world/level/gamerules/GameRule;)Ljava/lang/Object;"
            ),
            cancellable = true
    )
    private void decree$gameruleCheck(CallbackInfoReturnable<Boolean> cir) {
        if (this.enderman.level() instanceof ServerLevel world) {
            if (!world.getGameRules().get(DecreeGameRules.ENDERMAN_PICKUP)) {
                cir.setReturnValue(false);
            }
        }
    }
}
