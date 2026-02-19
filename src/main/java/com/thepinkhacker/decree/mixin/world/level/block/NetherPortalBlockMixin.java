package com.thepinkhacker.decree.mixin.world.level.block;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.NetherPortalBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Redirect(
            method = "randomTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isSpawningMonsters()Z"
            )
    )
    public boolean decree$piglinCheck(ServerLevel instance) {
        return instance.isSpawningMonsters()
                && instance.getGameRules().get(DecreeGameRules.NETHER_PORTAL_MOB_SPAWN);
    }
}
