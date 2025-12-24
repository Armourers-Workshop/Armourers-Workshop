package moe.plushie.armourers_workshop.compat.mixin.patch.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.entity.model.AbstractModelCollector;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Available("[1.18, 1.26)")
@Mixin(HierarchicalModel.class)
public abstract class HierarchicalModelMixin implements AbstractModelCollector {

    @Shadow
    public abstract ModelPart root();

    @Override
    public void aw2$collect(Builder builder) {
        builder.put("root", root());
    }
}
