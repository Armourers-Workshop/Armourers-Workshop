package extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.level.block.state.BlockBehaviour;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.19)")
public class BuilderProvider {

    @Extension
    public static class Properties {

        public static BlockBehaviour.Properties noLootTable(@This BlockBehaviour.Properties properties) {
            return properties.noDrops();
        }
    }
}
