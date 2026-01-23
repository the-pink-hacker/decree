package com.thepinkhacker.decree.mixin.entity.mob;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.PlaceBlockGoal.class)
public abstract class EndermanEntityPlaceBlockGoalMixin {
        @Shadow
        @Final
        private EndermanEntity enderman;

    @Inject(
            method = "canStart()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/rule/GameRules;getValue(Lnet/minecraft/world/rule/GameRule;)Ljava/lang/Object;"
            ),
            cancellable = true
    )
    private void decreeGameruleCheck(CallbackInfoReturnable<Boolean> cir) {
        if (this.enderman.getEntityWorld() instanceof ServerWorld world) {
            if (!world.getGameRules().getValue(DecreeGameRules.ENDERMAN_PLACE)) {
                cir.setReturnValue(false);
            }
        }
    }
}
