package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinSword extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinSword() {
        this.skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinSwordPartBase());
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:sword";
    }
}
