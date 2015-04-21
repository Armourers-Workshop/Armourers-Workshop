package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinTypeBase;

public class SkinFeet extends EquipmentSkinTypeBase {

    private ArrayList<IEquipmentSkinPart> skinParts;
    
    public SkinFeet() {
        skinParts = new ArrayList<IEquipmentSkinPart>();
        skinParts.add(new SkinFeetPartLeftFoot(this));
        skinParts.add(new SkinFeetPartRightFoot(this));
    }
    
    @Override
    public ArrayList<IEquipmentSkinPart> getSkinParts() {
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
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_FEET);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 3;
    }
}
