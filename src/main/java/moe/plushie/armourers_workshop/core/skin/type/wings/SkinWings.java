//package moe.plushie.armourers_workshop.core.skin.type.wings;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperty;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
//
//public class SkinWings extends AbstractSkinType {
//
//    private ArrayList<ISkinPartType> skinParts;
//
//    public SkinWings() {
//        skinParts = new ArrayList<ISkinPartType>();
//        skinParts.add(new SkinWingsPartLeftWing());
//        skinParts.add(new SkinWingsPartRightWing());
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return this.skinParts;
//    }
//
//    @Override
//    public ArrayList<ISkinProperty<?>> getProperties() {
//        ArrayList<ISkinProperty<?>> properties = super.getProperties();
//        properties.add(SkinProperty.WINGS_FLYING_SPEED);
//        properties.add(SkinProperty.WINGS_IDLE_SPEED);
//        properties.add(SkinProperty.WINGS_MAX_ANGLE);
//        properties.add(SkinProperty.WINGS_MIN_ANGLE);
//        properties.add(SkinProperty.WINGS_MOVMENT_TYPE);
//        return properties;
//    }
//
//    public enum MovementType {
//        EASE,
//        LINEAR
//    }
//}
