package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinTypeBase;

public class SkinSword extends EquipmentSkinTypeBase {

    private ArrayList<IEquipmentSkinPart> skinParts;
    
    public SkinSword() {
        this.skinParts = new ArrayList<IEquipmentSkinPart>();
        skinParts.add(new SkinSwordPartBase(this));
    }
    
    @Override
    public ArrayList<IEquipmentSkinPart> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:sword";
    }
    
    @Override
    public String getName() {
        return "Sword";
    }
    
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_WEAPON);
    }
}
