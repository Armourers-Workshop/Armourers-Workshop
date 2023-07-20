package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.core.client.other.SkinVisibilityTransformer;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.monster.AbstractIllager;

@Environment(EnvType.CLIENT)
public class IllagerSkinRenderer<T extends AbstractIllager, M extends IHumanoidModel> extends ExtendedSkinRenderer<T, M> {

    public IllagerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void init(SkinVisibilityTransformer<M> transformer) {
        super.init(transformer);
        transformer.modelToPart(SkinPartTypes.BIPPED_LEFT_ARM, "arms");
        transformer.modelToPart(SkinPartTypes.BIPPED_RIGHT_ARM, "arms");
    }
}

