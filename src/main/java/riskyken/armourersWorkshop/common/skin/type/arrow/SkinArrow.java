package riskyken.armourersWorkshop.common.skin.type.arrow;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

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
