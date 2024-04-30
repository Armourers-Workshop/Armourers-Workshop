package moe.plushie.armourers_workshop.compatibility.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererLayerProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends AbstractLivingEntityRendererImpl<T, M> {

    public AbstractLivingEntityRenderer(Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    @Override
    public final void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        render(entity, f, g, AbstractPoseStack.wrap(poseStack), AbstractBufferSource.wrap(multiBufferSource), i);
    }

    public void render(T entity, float f, float g, IPoseStack poseStack, IBufferSource multiBufferSource, int i) {
        super.render(entity, f, g, AbstractPoseStack.unwrap(poseStack), AbstractBufferSource.unwrap(multiBufferSource), i);
    }

    public void setModel(M model) {
        this.model = model;
    }

    public AbstractEntityRendererLayerProvider getLayerProvider() {
        return EntityRenderDispatcher.createLayerProvider(this);
    }
}
