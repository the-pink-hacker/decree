package com.thepinkhacker.decree.mixin.server.world;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Shadow
    public abstract GameRules getGameRules();

    @Shadow
    protected abstract Explosion.DestructionType getDestructionType(GameRule<Boolean> decayRule);

    @Redirect(
            method = "createExplosion(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/damage/DamageSource;Lnet/minecraft/world/explosion/ExplosionBehavior;DDDFZLnet/minecraft/world/World$ExplosionSourceType;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/particle/ParticleEffect;Lnet/minecraft/util/collection/Pool;Lnet/minecraft/registry/entry/RegistryEntry;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;getDestructionType(Lnet/minecraft/world/rule/GameRule;)Lnet/minecraft/world/explosion/Explosion$DestructionType;"
            )
    )
    private Explosion.DestructionType decree_mob_explosion(ServerWorld instance, GameRule<Boolean> decayRule) {
        return getGameRules().getValue(DecreeGameRules.DO_MOB_EXPLOSION_GRIEFING) ? getDestructionType(decayRule) : Explosion.DestructionType.KEEP;
    }
}
