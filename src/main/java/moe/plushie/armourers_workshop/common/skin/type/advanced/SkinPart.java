package moe.plushie.armourers_workshop.common.skin.type.advanced;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinPart extends AbstractSkinTypeBase {

    public final ISkinPartType partBase;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinPart() {
        this.skinParts = new ArrayList<ISkinPartType>();
        this.partBase = new SkinPartBase(this);
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
        return true;
    }
}
