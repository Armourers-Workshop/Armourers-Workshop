package moe.plushie.armourers_workshop.core.data;

import com.google.common.collect.Lists;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Collection;
import java.util.function.Predicate;

public class OptionalDirectionProperty extends EnumProperty<OptionalDirection> {

    protected OptionalDirectionProperty(String name, Collection<OptionalDirection> values) {
        super(name, OptionalDirection.class, values);
    }

    public static OptionalDirectionProperty create(String name, Predicate<OptionalDirection> values) {
        return create(name, ObjectUtils.filter(OptionalDirection.values(), values));
    }

    public static OptionalDirectionProperty create(String name, OptionalDirection... values) {
        return create(name, Lists.newArrayList(values));
    }

    public static OptionalDirectionProperty create(String name, Collection<OptionalDirection> values) {
        return new OptionalDirectionProperty(name, values);
    }
}
