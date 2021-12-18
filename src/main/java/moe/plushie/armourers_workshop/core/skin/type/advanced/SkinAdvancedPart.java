//package moe.plushie.armourers_workshop.core.skin.type.advanced;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkinAdvancedPart extends AbstractSkinType {
//
//    public final ISkinPartType partBase;
//    private ArrayList<ISkinPartType> skinParts;
//
//    public SkinAdvancedPart() {
//        this.skinParts = new ArrayList<ISkinPartType>();
//        this.partBase = new SkinAdvancedPartBase();
//        this.skinParts.add(this.partBase);
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return skinParts;
//    }
//
//
//    @Override
//    public boolean enabled() {
//        return false;
//    }
//}
