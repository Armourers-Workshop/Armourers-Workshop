package riskyken.armourersWorkshop.common.skin.type.feet;

import java.util.ArrayList;

import net.minecraft.inventory.EntityEquipmentSlot;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinFeet extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinFeet() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinFeetPartLeftFoot(this));
        skinParts.add(new SkinFeetPartRightFoot(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:feet";
    }
    
    @Override
    public String getName() {
        return "Feet";
    }

    @Override
    public EntityEquipmentSlot getEntityEquipmentSlot() {
        return EntityEquipmentSlot.FEET;
    }
}
