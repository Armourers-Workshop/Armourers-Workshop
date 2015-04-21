package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinTypeBase;

public class SkinHead extends EquipmentSkinTypeBase {

    private ArrayList<IEquipmentSkinPart> skinParts;
    
    public SkinHead() {
        this.skinParts = new ArrayList<IEquipmentSkinPart>();
        skinParts.add(new SkinHeadPartBase(this));
    }
    
    @Override
    public ArrayList<IEquipmentSkinPart> getSkinParts() {
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
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_HEAD);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 0;
    }
}
