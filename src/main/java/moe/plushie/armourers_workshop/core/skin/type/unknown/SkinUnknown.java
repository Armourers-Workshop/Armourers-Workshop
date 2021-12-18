//package moe.plushie.armourers_workshop.core.skin.type.unknown;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkinUnknown extends AbstractSkinType {
//
//    public final ISkinPartType skinUnknownPart;
//
//    private ArrayList<ISkinPartType> skinParts;
//
//    public SkinUnknown() {
//        this.skinParts = new ArrayList<ISkinPartType>();
//        skinUnknownPart = new SkinUnknownPartUnknown();
//        skinParts.add(skinUnknownPart);
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return this.skinParts;
//    }
//
//    @Override
//    public boolean isHidden() {
//        return true;
//    }
//}
