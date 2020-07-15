package moe.plushie.armourers_workshop.common.skin.type.advanced;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinAdvancedPart extends AbstractSkinTypeBase {

    public final ISkinPartType partBase;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinAdvancedPart() {
        this.skinParts = new ArrayList<ISkinPartType>();
        this.partBase = new SkinAdvancedPartBase(this);
        this.skinParts.add(this.partBase);
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:" + getName();
    }

    @Override
    public String getName() {
        return "part";
    }
    
    @Override
    public boolean enabled() {
        return false;
    }
}
