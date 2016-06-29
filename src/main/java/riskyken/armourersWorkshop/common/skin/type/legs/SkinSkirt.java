package riskyken.armourersWorkshop.common.skin.type.legs;

import java.util.ArrayList;

import net.minecraft.inventory.EntityEquipmentSlot;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinSkirt extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinSkirt() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinSkirtPartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
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
    public EntityEquipmentSlot getEntityEquipmentSlot() {
        return EntityEquipmentSlot.LEGS;
    }
}
