package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinTypeBase;

public class SkinLegs extends EquipmentSkinTypeBase {

    private ArrayList<IEquipmentSkinPart> skinParts;
    
    public SkinLegs() {
        skinParts = new ArrayList<IEquipmentSkinPart>();
        skinParts.add(new SkinLegsPartLeftLeg(this));
        skinParts.add(new SkinLegsPartRightLeg(this));
    }
    
    @Override
    public ArrayList<IEquipmentSkinPart> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:legs";
    }
    
    @Override
    public String getName() {
        return "Legs";
    }
    
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_LEGS);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
}
