package moe.plushie.armourers_workshop.compatibility.mixin.patch.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModelPartCollector;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelHolder;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Available("[1.16, )")
@Mixin(ListModel.class)
public abstract class ListModelMixin implements IModelPartCollector {

    @Shadow
    public abstract Iterable<ModelPart> parts();

    @Override
    public void aw2$collect(Map<String, ModelPart> collector) {
        AbstractModelHolder.collect("parts", parts(), collector);
    }
}
