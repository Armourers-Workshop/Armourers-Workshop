package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class ZombieVillagerSkinRenderer<T extends LivingEntity, M extends IHumanoidModel> extends BipedSkinRenderer<T, M> {

    public ZombieVillagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }
}
