package com.thepinkhacker.decree.registry.tag;

import com.thepinkhacker.decree.Decree;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.dimension.DimensionType;

public class DecreeDimensionTypeTags {
    public static final TagKey<DimensionType> GLIDE_BLACKLIST = of("glide_blacklist");

    private static TagKey<DimensionType> of(String id) {
        return TagKey.of(RegistryKeys.DIMENSION_TYPE, Decree.id(id));
    }
}
