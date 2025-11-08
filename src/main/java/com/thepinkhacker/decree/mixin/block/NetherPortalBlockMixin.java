package com.thepinkhacker.decree.mixin.block;

import com.thepinkhacker.decree.world.DecreeGameRules;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Inject(
            method = "randomTick(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/random/Random;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;allowsSpawning(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z"
            ),
            cancellable = true
    )
    private void decreePiglinCheck(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.getGameRules().getBoolean(DecreeGameRules.DO_NETHER_PORTAL_MOB_SPAWN)) {
            ci.cancel();
        }
    }
}
