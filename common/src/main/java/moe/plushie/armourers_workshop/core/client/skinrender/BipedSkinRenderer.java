package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class BipedSkinRenderer<T extends LivingEntity, M extends IHumanoidModel> extends ExtendedSkinRenderer<T, M> {

    public BipedSkinRenderer(EntityProfile profile) {
        super(profile);
    }
}

