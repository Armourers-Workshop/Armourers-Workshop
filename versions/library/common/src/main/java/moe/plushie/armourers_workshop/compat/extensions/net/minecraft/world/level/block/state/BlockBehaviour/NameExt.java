package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;


@Available("[1.16, 1.22)")
@Extension
public class NameExt {

    @Extension
    public static class Properties {

        public static BlockBehaviour.Properties setId(@This BlockBehaviour.Properties properties, OpenResourceLocation id) {
            return properties;
        }
    }
}
