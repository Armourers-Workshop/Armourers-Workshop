package moe.plushie.armourers_workshop.compat.mixin.patch.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractModelCollector;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.16, 1.22)")
@Mixin(ListModel.class)
public abstract class ListModelMixin implements AbstractModelCollector {

    @Shadow
    public abstract Iterable<ModelPart> parts();

    @Override
    public void aw2$collect(Builder builder) {
        builder.put("parts", parts());
    }
}
