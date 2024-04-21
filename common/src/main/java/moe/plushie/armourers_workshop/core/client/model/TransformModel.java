package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractPlayerModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class TransformModel<T extends LivingEntity> extends AbstractPlayerModel<T> {

    public TransformModel(float scale) {
        this(AbstractEntityRendererProvider.Context.sharedContext(), scale);
    }

    protected TransformModel(AbstractEntityRendererProvider.Context context, float scale) {
        super(context, scale, Type.NORMAL);
    }

    public void setup(T entity, int light, float partialRenderTick) {
        this.transformFrom(entity, partialRenderTick);
    }
}
