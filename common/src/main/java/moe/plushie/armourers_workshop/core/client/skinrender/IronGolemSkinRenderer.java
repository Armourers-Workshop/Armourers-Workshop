package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModelHolder;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.world.entity.animal.IronGolem;

@Environment(value = EnvType.CLIENT)
public class IronGolemSkinRenderer<T extends IronGolem, V extends IronGolemModel<T>, M extends IHumanoidModelHolder<V>> extends ExtendedSkinRenderer<T, V, M> {

    public IronGolemSkinRenderer(EntityProfile profile) {
        super(profile);
    }
}
