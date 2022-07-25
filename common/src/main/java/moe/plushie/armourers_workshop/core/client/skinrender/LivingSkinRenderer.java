package moe.plushie.armourers_workshop.core.client.skinrender;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

@Environment(value = EnvType.CLIENT)
public class LivingSkinRenderer<T extends LivingEntity, M extends EntityModel<T>> extends SkinRenderer<T, M> {

    protected final HashMap<Class<?>, BiFunction<RenderLayerParent<T, M>, RenderLayer<T, M>, RenderLayer<T, M>>> mappers = new HashMap<>();
    protected LivingEntityRenderer<T, M> renderer;

    public LivingSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void init(EntityRenderer<T> entityRenderer) {
        super.init(entityRenderer);
        if (entityRenderer instanceof LivingEntityRenderer<?, ?>) {
            init((LivingEntityRenderer<T, M>) entityRenderer);
        }
    }

    protected void init(LivingEntityRenderer<T, M> entityRenderer) {
        List<RenderLayer<T, M>> layers = entityRenderer.layers;
        this.renderer = entityRenderer;
        this.mappers.forEach((key, value) -> {
            for (int index = 0; index < layers.size(); ++index) {
                RenderLayer<T, M> oldValue = layers.get(index);
                if (key.isInstance(oldValue)) {
                    layers.set(index, value.apply(entityRenderer, oldValue));
                }
            }
        });
    }

    @Override
    public int render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, ItemTransforms.TransformType transformType, int light, float partialTicks, int slotIndex, PoseStack matrixStack, MultiBufferSource buffers) {
        // we don't know how to draw without a model, right?
        if (model == null) {
            model = getModel();
        }
        return super.render(entity, model, bakedSkin, scheme, itemStack, transformType, light, partialTicks, slotIndex, matrixStack, buffers);
    }

    public M getModel() {
        return renderer.getModel();
    }
}

