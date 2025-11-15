package moe.plushie.armourers_workshop.compat.mixin.patch.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.renderer.model.AbstractModelCollector;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Available("[1.18, )")
@Mixin(ModelPart.class)
public class ModelPartMixin implements AbstractModelCollector {

    @Shadow
    @Final
    private Map<String, ModelPart> children;

    @Override
    public void aw2$collect(Builder builder) {
        builder.putAll(children);
    }
}
