package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class BipedSkinRenderer<T extends LivingEntity, V extends HumanoidModel<T>, M extends IHumanoidModelHolder<V>> extends ExtendedSkinRenderer<T, V, M> {

    public BipedSkinRenderer(EntityProfile profile) {
        super(profile);
    }
}

