package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinSkirt extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinSkirt() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinSkirtPartBase());
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:skirt";
    }
}
