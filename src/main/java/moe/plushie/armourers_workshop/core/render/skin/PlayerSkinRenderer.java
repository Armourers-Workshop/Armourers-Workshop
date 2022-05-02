package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
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
    protected void applyOverriders(T entity, M model, SkinRenderData renderData) {
        super.applyOverriders(entity, model, renderData);
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            addOverrider(model.leftSleeve);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addOverrider(model.rightSleeve);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            addOverrider(model.jacket);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || renderData.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addOverrider(model.leftPants);
        }
        if (renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || renderData.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addOverrider(model.rightPants);
        }
    }
}