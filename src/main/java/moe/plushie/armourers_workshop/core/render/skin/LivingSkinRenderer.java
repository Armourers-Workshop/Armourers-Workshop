package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.layer.SkinWardrobeArmorLayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class LivingSkinRenderer<T extends LivingEntity, M extends EntityModel<T>> extends SkinRenderer<T, M> {

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
        mappers.forEach((key, value) -> {
            for (int index = 0; index < layers.size(); ++index) {
                LayerRenderer<T, M> oldValue = layers.get(index);
                if (key.isInstance(oldValue)) {
                    layers.set(index, value.apply(entityRenderer, oldValue));
                }
            }
        });
        entityRenderer.addLayer(new SkinWardrobeArmorLayer<>(entityRenderer));
    }
}

