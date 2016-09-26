package riskyken.armourersWorkshop.common.skin.type.wings;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinWings extends AbstractSkinTypeBase {
    
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinWings() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinWingsPartLeftWing(this));
        skinParts.add(new SkinWingsPartRightWing(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:wings";
    }
    
    @Override
    public String getName() {
        return "wings";
    }
}
