package com.thepinkhacker.decree.world.level.block.entity;

import net.minecraft.world.item.component.ResolvableProfile;

public interface SkullBlockEntityMutator {
    void decree$setOwner(ResolvableProfile owner);

    void decree$updateSkin();
}
