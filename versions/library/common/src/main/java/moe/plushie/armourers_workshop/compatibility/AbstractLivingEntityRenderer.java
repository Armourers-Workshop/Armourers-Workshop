package moe.plushie.armourers_workshop.compatibility;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.18, )")
@Environment(value = EnvType.CLIENT)
public abstract class AbstractLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public AbstractLivingEntityRenderer(AbstractEntityRendererContext context, M entityModel, float f) {
        super(context, entityModel, f);
    }
}
