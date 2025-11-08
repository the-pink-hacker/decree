package com.thepinkhacker.decree.mixin.server.world;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Redirect(
            method = "Lnet/minecraft/server/world/ServerWorld;getDestructionType(Lnet/minecraft/world/GameRules$Key;)Lnet/minecraft/world/explosion/Explosion$DestructionType;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"
            )
    )
    private boolean decree_mob_explosion(GameRules instance, GameRules.Key<GameRules.BooleanRule> rule) {
        return instance.getBoolean(rule) & instance.getBoolean(DecreeGameRules.DO_MOB_EXPLOSION_GRIEFING);
    }
}
