package com.thepinkhacker.decree.block.entity;

import net.minecraft.component.type.ProfileComponent;

public interface SkullBlockEntityMutator {
    void decree$setOwner(ProfileComponent owner);

    void decree$updateSkin();
}
