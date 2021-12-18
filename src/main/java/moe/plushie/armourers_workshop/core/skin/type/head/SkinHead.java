//package moe.plushie.armourers_workshop.core.skin.type.head;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperty;
//import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//import moe.plushie.armourers_workshop.core.skin.type.SkinPartTypes;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class SkinHead extends AbstractSkinType {
//
//    private List<ISkinPartType> skinParts;
//
//    public SkinHead() {
//        this.skinParts = Arrays.asList(SkinPartTypes.BIPED_HEAD);
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return this.skinParts;
//    }
//
//
//    @Override
//    public boolean showSkinOverlayCheckbox() {
//        return true;
//    }
//
//    @Override
//    public int getVanillaArmourSlotId() {
//        return 0;
//    }
//
//    @Override
//    public ArrayList<ISkinProperty<?>> getProperties() {
//        ArrayList<ISkinProperty<?>> properties = super.getProperties();
//        properties.add(SkinProperty.MODEL_OVERRIDE_HEAD);
//        properties.add(SkinProperty.MODEL_HIDE_OVERLAY_HEAD);
//        return properties;
//    }
//
//    @Override
//    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
//        return SkinProperty.MODEL_OVERRIDE_HEAD.getValue(skinPropsOld) != SkinProperty.MODEL_OVERRIDE_HEAD.getValue(skinPropsNew);
//    }
//}
