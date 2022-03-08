package moe.plushie.armourers_workshop.core.render.renderer;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.layer.ForwardingLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;

public class ZombieVillagerSkinRenderer<T extends LivingEntity, M extends BipedModel<T>> extends BipedSkinRenderer<T, M> {

    public ZombieVillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        super.initTransformers();
        // proxy => VillagerLevelPendantLayer
        // proxy => CrossedArmsItemLayer
        mappers.put(VillagerLevelPendantLayer.class, ForwardingLayer.when(this::visibleHat));
    }

    private boolean visibleHat(T entity, M model) {
        return model.hat.visible;
    }
}
