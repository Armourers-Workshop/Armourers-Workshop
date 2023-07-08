package extensions.net.minecraft.world.level.block.state.BlockState;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.level.block.state.BlockState;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.20, )")
public class PropertyProvider {

    public static boolean isReplaceable(@This BlockState state) {
        return state.canBeReplaced();
    }
}
