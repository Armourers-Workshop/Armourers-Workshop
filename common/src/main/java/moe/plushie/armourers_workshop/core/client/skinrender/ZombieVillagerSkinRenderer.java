package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

public class ZombieVillagerSkinRenderer<T extends LivingEntity, V extends HumanoidModel<T>, M extends IHumanoidModelHolder<V>> extends BipedSkinRenderer<T, V, M> {

    public ZombieVillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }
}
