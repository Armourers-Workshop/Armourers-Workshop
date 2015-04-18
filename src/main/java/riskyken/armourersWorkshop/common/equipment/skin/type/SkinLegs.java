package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinLegs extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinLegs() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinLegsPartLeftLeg());
        skinParts.add(new SkinLegsPartRightLeg());
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:legs";
    }
}
