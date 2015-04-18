package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinHead extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinHead() {
        this.skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinHeadPartBase());
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:head";
    }
    
    @Override
    public String getName() {
        return "Head";
    }

    @Override
    public boolean showSkinOverlayCheckbox() {
        return true;
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 0;
    }
}
