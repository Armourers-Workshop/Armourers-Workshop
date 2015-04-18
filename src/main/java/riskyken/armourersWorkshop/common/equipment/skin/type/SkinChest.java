package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinChest extends SkinTypeBase {
    
    private ArrayList<ISkinPart> skinParts;
    
    public SkinChest() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinChestPartBase());
        skinParts.add(new SkinChestPartLeftArm());
        skinParts.add(new SkinChestPartRightArm());
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:chest";
    }
    
    @Override
    public String getName() {
        return "Chest";
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 1;
    }
}
