package moe.plushie.armourers_workshop.core.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

import java.util.function.BiFunction;

@Environment(value = EnvType.CLIENT)
public class ForwardingLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private final RenderLayer<T, M> target;
    private final BiFunction<T, M, Boolean> test;

    public ForwardingLayer(RenderLayerParent<T, M> renderer, RenderLayer<T, M> target) {
        this(renderer, target, (t, m) -> true);
    }

    public ForwardingLayer(RenderLayerParent<T, M> renderer, RenderLayer<T, M> target, BiFunction<T, M, Boolean> test) {
        super(renderer);
        this.target = target;
        this.test = test;
    }

    public static <T extends Entity, M extends EntityModel<T>> BiFunction<RenderLayerParent<T, M>, RenderLayer<T, M>, RenderLayer<T, M>> when(BiFunction<T, M, Boolean> test) {
        return (entityRenderer, layer) -> new ForwardingLayer<>(entityRenderer, layer, test);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffers, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (test.apply(entity, getParentModel())) {
            target.render(matrixStack, buffers, p_225628_3_, entity, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
        }
    }

    public RenderLayer<T, M> getTarget() {
        return target;
    }
}
