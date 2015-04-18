package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinFeet extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinFeet() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinFeetPartLeftFoot());
        skinParts.add(new SkinFeetPartRightFoot());
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:feet";
    }
}
