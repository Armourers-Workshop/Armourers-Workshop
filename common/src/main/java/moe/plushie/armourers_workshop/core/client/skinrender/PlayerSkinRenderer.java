package moe.plushie.armourers_workshop.core.client.skinrender;

import moe.plushie.armourers_workshop.api.client.model.IPlayerModelHolder;
import moe.plushie.armourers_workshop.core.client.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;

@Environment(value = EnvType.CLIENT)
public class PlayerSkinRenderer<T extends LivingEntity, V extends PlayerModel<T>, M extends IPlayerModelHolder<V>> extends BipedSkinRenderer<T, V, M> {

    public PlayerSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(model.getHatPart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.getLeftSleevePart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.getRightSleevePart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(model.getJacketPart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_LEFT_LEG) || overriddenManager.overrideOverlay(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(model.getLeftPantsPart());
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_RIGHT_LEG) || overriddenManager.overrideOverlay(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(model.getRightPantsPart());
        }
    }
}
