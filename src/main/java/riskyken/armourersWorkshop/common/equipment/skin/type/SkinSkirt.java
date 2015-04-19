package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinSkirt extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinSkirt() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinSkirtPartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:skirt";
    }
    
    @Override
    public String getName() {
        return "Skirt";
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
}
