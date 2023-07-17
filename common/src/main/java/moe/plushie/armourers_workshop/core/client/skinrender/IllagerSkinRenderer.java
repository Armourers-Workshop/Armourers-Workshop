package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IHumanoidModel;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
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
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_LEFT_ARM)) {
            addModelOverride(model.getPart("arms"));
        }
        if (overriddenManager.overrideModel(SkinPartTypes.BIPPED_RIGHT_ARM)) {
            addModelOverride(model.getPart("arms"));
        }
    }
}

