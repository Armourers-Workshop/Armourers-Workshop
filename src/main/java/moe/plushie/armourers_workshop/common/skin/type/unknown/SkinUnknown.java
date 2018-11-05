package moe.plushie.armourers_workshop.common.skin.type.unknown;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinUnknown extends AbstractSkinTypeBase {

    public final ISkinPartType skinUnknownPart;
    
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinUnknown() {
        this.skinParts = new ArrayList<ISkinPartType>();
        skinUnknownPart = new SkinUnknownPartUnknown(this);
        skinParts.add(skinUnknownPart);
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:unknown";
    }
    
    @Override
    public String getName() {
        return "unknown";
    }
    
    @Override
    public boolean isHidden() {
        return true;
    }
}
