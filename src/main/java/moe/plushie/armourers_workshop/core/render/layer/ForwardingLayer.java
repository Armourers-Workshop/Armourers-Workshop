package moe.plushie.armourers_workshop.core.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class ForwardingLayer<T extends Entity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    private final LayerRenderer<T, M> target;
    private BiFunction<T, M, Boolean> test;

    public ForwardingLayer(IEntityRenderer<T, M> renderer, LayerRenderer<T, M> target) {
        this(renderer, target, (t, m) -> true);
    }

    public ForwardingLayer(IEntityRenderer<T, M> renderer, LayerRenderer<T, M> target, BiFunction<T, M, Boolean> test) {
        super(renderer);
        this.target = target;
        this.test = test;
    }

    public static <T extends Entity, M extends EntityModel<T>> BiFunction<IEntityRenderer<T, M>, LayerRenderer<T, M>, LayerRenderer<T, M>> when(BiFunction<T, M, Boolean> test) {
        return (entityRenderer, layer) -> new ForwardingLayer<>(entityRenderer, layer, test);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffers, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        if (test.apply(entity, getParentModel())) {
            target.render(matrixStack, buffers, p_225628_3_, entity, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_);
        }
    }

    public LayerRenderer<T, M> getTarget() {
        return target;
    }
}
