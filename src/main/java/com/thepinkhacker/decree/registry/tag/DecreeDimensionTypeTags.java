package com.thepinkhacker.decree.registry.tag;

import com.thepinkhacker.decree.Decree;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.dimension.DimensionType;

public class DecreeDimensionTypeTags {
    public static final TagKey<DimensionType> GLIDE_BLACKLIST = of("glide_blacklist");

    private static TagKey<DimensionType> of(String id) {
        return TagKey.create(Registries.DIMENSION_TYPE, Decree.id(id));
    }
}
