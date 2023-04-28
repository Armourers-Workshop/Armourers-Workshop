package moe.plushie.armourers_workshop.compatibility.client.renderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProviderImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractLivingEntityRendererImpl<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> implements AbstractEntityRendererProviderImpl {

    public AbstractLivingEntityRendererImpl(Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }
}
