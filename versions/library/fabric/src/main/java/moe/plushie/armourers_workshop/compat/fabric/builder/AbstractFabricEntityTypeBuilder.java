package moe.plushie.armourers_workshop.compat.fabric.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityTypeBuilder;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

@Available("[16, 26)")
public class AbstractFabricEntityTypeBuilder<T extends Entity> extends AbstractEntityTypeBuilder<T> {

    public AbstractFabricEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory category) {
        super(serializer, category);
        this.apply(it -> {
            DataContainer.set(it, false);
            return it;
        });
    }
}
