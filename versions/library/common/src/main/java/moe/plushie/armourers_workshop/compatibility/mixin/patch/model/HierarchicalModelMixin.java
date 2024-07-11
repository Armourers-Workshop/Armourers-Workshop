package moe.plushie.armourers_workshop.compatibility.mixin.patch.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.model.IModelPartCollector;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelHolder;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Available("[1.18, )")
@Mixin(HierarchicalModel.class)
public abstract class HierarchicalModelMixin implements IModelPartCollector {

    @Shadow
    public abstract ModelPart root();

    @Override
    public void aw2$collect(Map<String, ModelPart> collector) {
        AbstractModelHolder.collect("root", root(), collector);
    }
}
