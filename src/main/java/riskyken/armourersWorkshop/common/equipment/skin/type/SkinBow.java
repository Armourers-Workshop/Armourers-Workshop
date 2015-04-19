package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinBow extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinBow() {
        this.skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinBowPartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:bow";
    }

    @Override
    public String getName() {
        return "bow";
    }
}
