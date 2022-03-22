package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobeState;
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
    protected void applyOverriders(T entity, M model, SkinWardrobe wardrobe, SkinWardrobeState snapshot) {
        super.applyOverriders(entity, model, wardrobe, snapshot);
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
            addOverrider(model.leftSleeve);
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
            addOverrider(model.rightSleeve);
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
            addOverrider(model.jacket);
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
            addOverrider(model.leftPants);
        }
        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
            addOverrider(model.rightPants);
        }
    }
}
