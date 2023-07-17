package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IPlayerModel;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class PlayerSkinRenderer<T extends LivingEntity, M extends IPlayerModel> extends BipedSkinRenderer<T, M> {

    public PlayerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_HEAD)) {
            addModelOverride(model.getHatPart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_LEFT_ARM)) {
            addModelOverride(model.getLeftSleevePart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_RIGHT_ARM)) {
            addModelOverride(model.getRightSleevePart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_CHEST)) {
            addModelOverride(model.getJacketPart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_LEFT_LEG) || overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_LEFT_FOOT)) {
            addModelOverride(model.getLeftPantsPart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_RIGHT_LEG) || overriddenManager.overrideOverlay(SkinPartTypes.BIPPED_RIGHT_FOOT)) {
            addModelOverride(model.getRightPantsPart());
        }
    }
}
