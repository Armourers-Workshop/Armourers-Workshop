package moe.plushie.armourers_workshop.core.render.skin;

import moe.plushie.armourers_workshop.core.entity.EntityProfile;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChickenSkinRenderer<T extends ChickenEntity, M extends ChickenModel<T>> extends LivingSkinRenderer<T, M> {

    public ChickenSkinRenderer(EntityProfile profile) {
        super(profile);
    }

    @Override
    public void initTransformers() {
        transformer.registerArmor(SkinPartTypes.BIPED_HEAD, m -> m.head);
        transformer.registerArmor(SkinPartTypes.BIPED_CHEST, m -> m.body);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_ARM, m -> m.wing0);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_ARM, m -> m.wing1);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_FOOT, m -> m.leg0);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_FOOT, m -> m.leg1);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_LEG, m -> m.leg0);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_LEG, m -> m.leg1);
        transformer.registerArmor(SkinPartTypes.BIPED_LEFT_WING, m -> m.wing0);
        transformer.registerArmor(SkinPartTypes.BIPED_RIGHT_WING, m -> m.wing1);
    }

//    @Override
//    public void override(T entity, M model, SkinWardrobe wardrobe) {
//        model.wing0.visible = true;
//        model.wing1.visible = true;
//        model.head.visible = true;
//        model.beak.visible = true;
//        model.body.visible = true;
//        model.redThing.visible = true;
//        model.leg1.visible = true;
//        model.leg0.visible = true;
//
//        SkinWardrobeState snapshot = wardrobe.snapshot();
//        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_ARM)) {
//            model.wing0.visible = false;
//        }
//        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_ARM)) {
//            model.wing1.visible = false;
//        }
//        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_HEAD)) {
//            model.head.visible = false;
//            model.beak.visible = false; // when override the head, the hat needs to override too
//            model.redThing.visible = false;
//        }
//        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_CHEST)) {
//            model.body.visible = false;
//        }
//        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_LEFT_FOOT)) {
//            model.leg0.visible = false;
//        }
//        if (snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_LEG) || snapshot.hasOverriddenPart(SkinPartTypes.BIPED_RIGHT_FOOT)) {
//            model.leg1.visible = false;
//        }
//    }

//    private void setNoChange(MatrixStack matrixStack, M model) {
//        matrixStack.translate(0.0f, 24.0f, 0.0f);
//    }
}
