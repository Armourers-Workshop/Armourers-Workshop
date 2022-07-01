package moe.plushie.armourers_workshop.core.render.skin;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class LivingSkinRenderer<T extends LivingEntity, M extends EntityModel<T>> extends SkinRenderer<T, M> {

    protected LivingRenderer<T, M> renderer;

    protected final HashMap<Class<?>, BiFunction<IEntityRenderer<T, M>, LayerRenderer<T, M>, LayerRenderer<T, M>>> mappers = new HashMap<>();

    public LivingSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void init(EntityRenderer<T> entityRenderer) {
        super.init(entityRenderer);
        if (entityRenderer instanceof LivingRenderer<?, ?>) {
            init((LivingRenderer<T, M>) entityRenderer);
        }
    }

    protected void init(LivingRenderer<T, M> entityRenderer) {
        List<LayerRenderer<T, M>> layers = entityRenderer.layers;
        this.renderer = entityRenderer;
        this.mappers.forEach((key, value) -> {
            for (int index = 0; index < layers.size(); ++index) {
                LayerRenderer<T, M> oldValue = layers.get(index);
                if (key.isInstance(oldValue)) {
                    layers.set(index, value.apply(entityRenderer, oldValue));
                }
            }
        });
    }

    @Override
    public void render(T entity, M model, BakedSkin bakedSkin, ColorScheme scheme, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, int light, float partialTicks, int slotIndex, MatrixStack matrixStack, IRenderTypeBuffer buffers) {
        //
        if (model == null) {
            model = getModel();
        }
        super.render(entity, model, bakedSkin, scheme, itemStack, transformType, light, partialTicks, slotIndex, matrixStack, buffers);
    }

    public M getModel() {
        return renderer.getModel();
    }
}

