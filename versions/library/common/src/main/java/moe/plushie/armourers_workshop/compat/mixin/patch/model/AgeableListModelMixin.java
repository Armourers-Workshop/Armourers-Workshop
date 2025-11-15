package moe.plushie.armourers_workshop.compat.mixin.patch.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractModelCollector;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.16, 1.22)")
@Mixin(AgeableListModel.class)
public abstract class AgeableListModelMixin implements AbstractModelCollector {

    @Shadow
    protected abstract Iterable<ModelPart> headParts();

    @Shadow
    protected abstract Iterable<ModelPart> bodyParts();

    @Override
    public void aw2$collect(Builder builder) {
        builder.put("headParts", headParts());
        builder.put("bodyParts", bodyParts());
    }
}
