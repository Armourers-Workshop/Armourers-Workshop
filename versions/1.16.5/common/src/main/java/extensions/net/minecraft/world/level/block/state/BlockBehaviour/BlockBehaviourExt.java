package extensions.net.minecraft.world.level.block.state.BlockBehaviour;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Extension
public class BlockBehaviourExt {

    public static class Properties {

        public static BlockBehaviour.Properties noLootTable(@This BlockBehaviour.Properties properties) {
            return properties.noDrops();
        }
    }
}
