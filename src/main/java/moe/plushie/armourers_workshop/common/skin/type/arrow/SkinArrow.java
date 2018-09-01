package moe.plushie.armourers_workshop.common.skin.type.arrow;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinArrow extends AbstractSkinTypeBase {
    
    public final ISkinPartType partBase;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinArrow() {
        this.skinParts = new ArrayList<ISkinPartType>();
        this.partBase = new SkinArrowPartBase(this);
        this.skinParts.add(this.partBase);
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:arrow";
    }

    @Override
    public String getName() {
        return "arrow";
    }
    
    @Override
    public boolean showHelperCheckbox() {
        return true;
    }
}
