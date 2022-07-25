package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.core.client.layer.ForwardingLayer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.world.entity.LivingEntity;

public class ZombieVillagerSkinRenderer<T extends LivingEntity, M extends HumanoidModel<T>> extends BipedSkinRenderer<T, M> {

    public ZombieVillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        super.initTransformers();
        // proxy => VillagerProfessionLayer
        // proxy => CrossedArmsItemLayer
        mappers.put(VillagerProfessionLayer.class, ForwardingLayer.when(this::visibleHat));
    }

    private boolean visibleHat(T entity, M model) {
        return model.hat.visible;
    }
}
