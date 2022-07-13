package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.render.other.SkinOverriddenManager;
import moe.plushie.armourers_workshop.core.render.other.SkinRenderData;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class PlayerSkinRenderer<T extends LivingEntity, M extends PlayerModel<T>> extends BipedSkinRenderer<T, M> {

    public PlayerSkinRenderer(EntityProfile profile) {
        super(profile);
    }


    @Override
    protected void apply(T entity, M model, SkinOverriddenManager overriddenManager, SkinRenderData renderData) {
        super.apply(entity, model, overriddenManager, renderData);
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_HEAD)) {
            addModelOverride(model.hat);
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_LEFT_ARM)) {
            addModelOverride(model.leftSleeve);
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addModelOverride(model.rightSleeve);
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_CHEST)) {
            addModelOverride(model.jacket);
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_LEFT_LEG) || overriddenManager.overrideOverlay(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addModelOverride(model.leftPants);
        }
        if (overriddenManager.overrideOverlay(SkinPartTypes.BIPED_RIGHT_LEG) || overriddenManager.overrideOverlay(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addModelOverride(model.rightPants);
        }
    }
}
