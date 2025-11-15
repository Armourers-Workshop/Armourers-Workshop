package moe.plushie.armourers_workshop.compat.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Available("[1.21, 1.22)")
@OnlyIn(Dist.CLIENT)
public abstract class AbstractLivingEntityRendererImpl<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> implements IEntityRenderer<T, S> {

    public AbstractLivingEntityRendererImpl(Context context, M entityModel, float f) {
        super(AbstractEntityRenderer.unwrap(context), entityModel, f);
    }

    @Override
    protected void setupRotations(T livingEntity, PoseStack poseStack, float f, float g, float h, float i) {
        poseStack.scale(1 / i, 1 / i, 1 / i);
        super.setupRotations(livingEntity, poseStack, f, g, h, 1);
    }
}
