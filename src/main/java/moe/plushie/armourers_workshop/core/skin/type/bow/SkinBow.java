//package moe.plushie.armourers_workshop.core.skin.type.bow;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkinBow extends AbstractSkinType {
//
//    private ArrayList<ISkinPartType> skinParts;
//
//    public SkinBow() {
//        this.skinParts = new ArrayList<ISkinPartType>();
//        skinParts.add(new SkinBowPartBase());
//        skinParts.add(new SkinBowPartFrame1());
//        skinParts.add(new SkinBowPartFrame2());
//        skinParts.add(new SkinBowPartArrow());
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return this.skinParts;
//    }
//
//
//    @Override
//    public boolean showHelperCheckbox() {
//        return true;
//    }
//}
