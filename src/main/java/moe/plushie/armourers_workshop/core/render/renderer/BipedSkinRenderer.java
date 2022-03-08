package moe.plushie.armourers_workshop.core.render.renderer;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeState;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedSkinRenderer<T extends LivingEntity, M extends BipedModel<T>> extends ExtendedSkinRenderer<T, M> {

    public BipedSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    protected void applyOverriders(T entity, M model, SkinWardrobe wardrobe, SkinWardrobeState snapshot) {
        super.applyOverriders(entity, model, wardrobe, snapshot);
        if (model instanceof PlayerModel) {
            PlayerModel<?> playerModel = (PlayerModel<?>) model;
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
                addOverrider(playerModel.leftSleeve);
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
                addOverrider(playerModel.rightSleeve);
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
                addOverrider(playerModel.jacket);
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
                addOverrider(playerModel.leftPants);
            }
            if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
                addOverrider(playerModel.rightPants);
            }
        }
    }

    @Override
    public IPartAccessor<M> getAccessor() {
        return new IPartAccessor<M>() {

            public ModelRenderer getHat(M model) {
                return model.hat;
            }

            public ModelRenderer getHead(M model) {
                return model.head;
            }

            public ModelRenderer getBody(M model) {
                return model.body;
            }

            public ModelRenderer getLeftArm(M model) {
                return model.leftArm;
            }

            public ModelRenderer getRightArm(M model) {
                return model.rightArm;
            }

            public ModelRenderer getLeftLeg(M model) {
                return model.leftLeg;
            }

            public ModelRenderer getRightLeg(M model) {
                return model.rightLeg;
            }

            public float getHeadScale(M model) {
                if (model.scaleHead) {
                    return 1.5f;
                }
                return model.babyBodyScale;
            }
        };
    }
}

