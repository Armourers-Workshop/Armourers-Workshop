//package moe.plushie.armourers_workshop.core.skin.type.chest;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperty;
//import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkinChest extends AbstractSkinType {
//
//    private ArrayList<ISkinPartType> skinParts;
//
//    public SkinChest() {
//        skinParts = new ArrayList<ISkinPartType>();
//        skinParts.add(new SkinChestPartBase());
//        skinParts.add(new SkinChestPartLeftArm());
//        skinParts.add(new SkinChestPartRightArm());
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return this.skinParts;
//    }
//
//    @Override
//    public int getVanillaArmourSlotId() {
//        return 1;
//    }
//
//    @Override
//    public ArrayList<ISkinProperty<?>> getProperties() {
//        ArrayList<ISkinProperty<?>> properties = super.getProperties();
//        properties.add(SkinProperty.MODEL_OVERRIDE_CHEST);
//        properties.add(SkinProperty.MODEL_OVERRIDE_ARM_LEFT);
//        properties.add(SkinProperty.MODEL_OVERRIDE_ARM_RIGHT);
//        properties.add(SkinProperty.MODEL_HIDE_OVERLAY_CHEST);
//        properties.add(SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT);
//        properties.add(SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT);
//        return properties;
//    }
//
//    @Override
//    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
//        if (SkinProperty.MODEL_OVERRIDE_CHEST.getValue(skinPropsOld) != SkinProperty.MODEL_OVERRIDE_CHEST.getValue(skinPropsNew)) {
//            return true;
//        }
//        if (SkinProperty.MODEL_OVERRIDE_ARM_LEFT.getValue(skinPropsOld) != SkinProperty.MODEL_OVERRIDE_ARM_LEFT.getValue(skinPropsNew)) {
//            return true;
//        }
//        if (SkinProperty.MODEL_OVERRIDE_ARM_RIGHT.getValue(skinPropsOld) != SkinProperty.MODEL_OVERRIDE_ARM_RIGHT.getValue(skinPropsNew)) {
//            return true;
//        }
//        return false;
//    }
//}
