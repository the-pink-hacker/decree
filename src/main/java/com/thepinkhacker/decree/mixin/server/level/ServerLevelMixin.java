package com.thepinkhacker.decree.mixin.server.level;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Shadow
    public abstract GameRules getGameRules();

    @Shadow
    protected abstract Explosion.BlockInteraction getDestroyType(GameRule<Boolean> decayRule);

    @Redirect(
            method = "explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/core/particles/ParticleOptions;Lnet/minecraft/util/random/WeightedList;Lnet/minecraft/core/Holder;)V",
            at = @At(
                    value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/server/level/ServerLevel;getDestroyType(Lnet/minecraft/world/level/gamerules/GameRule;)Lnet/minecraft/world/level/Explosion$BlockInteraction;"
            )
    )
    private Explosion.BlockInteraction decree$mobExplosion(ServerLevel instance, GameRule<Boolean> decayRule) {
        return getGameRules().get(DecreeGameRules.DO_MOB_EXPLOSION_GRIEFING) ? getDestroyType(decayRule) : Explosion.BlockInteraction.KEEP;
    }
}
