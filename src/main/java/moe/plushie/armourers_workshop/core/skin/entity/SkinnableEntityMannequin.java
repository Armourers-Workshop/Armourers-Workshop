package moe.plushie.armourers_workshop.core.skin.entity;//package moe.plushie.armourers_workshop.core.skin.entity;
//
//import java.util.ArrayList;
//
//import moe.plushie.armourers_workshop.core.skin.type.ISkinType;
//import moe.plushie.armourers_workshop.core.init.entities.EntityMannequin;
//import moe.plushie.armourers_workshop.core.skin.type.SkinTypeRegistry;
//import net.minecraft.client.renderer.entity.RenderManager;
//import net.minecraft.entity.Entity;
//
//public class SkinnableEntityMannequin extends SkinnableEntity {
//
//    @Override
//    public Class<? extends Entity> getEntityClass() {
//        return EntityMannequin.class;
//    }
//
//    @Override
//    public void addRenderLayer(RenderManager renderManager) {
//        // NO-OP
//    }
//
//    @Override
//    public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
//        skinTypes.add(SkinTypes.BIPED_OUTFIT);
//
//        skinTypes.add(SkinTypes.BIPED_HEAD);
//        skinTypes.add(SkinTypes.BIPED_CHEST);
//        skinTypes.add(SkinTypes.BIPED_LEGS);
//        skinTypes.add(SkinTypes.BIPED_FEET);
//        skinTypes.add(SkinTypes.BIPED_WINGS);
//    }
//
//    @Override
//    public int getSlotsForSkinType(ISkinType skinType) {
//        if (skinType.getVanillaArmourSlotId() != -1) {
//            return 10;
//        }
//        if (skinType == SkinTypes.BIPED_WINGS) {
//            return 10;
//        }
//        if (skinType == SkinTypes.BIPED_OUTFIT) {
//            return 10;
//        }
//        return 1;
//    }
//}
