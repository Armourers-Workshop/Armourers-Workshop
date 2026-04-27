package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.level.block.state.properties.BlockStateProperties;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Extension
@Available("[16, 26)")
public class Constructor {

    public static DirectionProperty createDirectionProperty(@ThisClass Class<?> clazz, String name) {
        return DirectionProperty.create(name, Direction.values());
    }

    public static BooleanProperty createBooleanProperty(@ThisClass Class<?> clazz, String name) {
        return BooleanProperty.create(name);
    }
}
