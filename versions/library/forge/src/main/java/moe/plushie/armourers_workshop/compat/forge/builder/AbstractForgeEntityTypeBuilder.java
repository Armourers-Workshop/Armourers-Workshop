package moe.plushie.armourers_workshop.compat.forge.builder;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.compat.builder.AbstractEntityTypeBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;

@Available("[16, )")
public class AbstractForgeEntityTypeBuilder<T extends Entity> extends AbstractEntityTypeBuilder<T> {

    public AbstractForgeEntityTypeBuilder(IEntityType.Serializer<T> serializer, MobCategory category) {
        super(serializer, category);
    }
}
