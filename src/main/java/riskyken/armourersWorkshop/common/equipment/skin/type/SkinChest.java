package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.EquipmentSkinTypeBase;

public class SkinChest extends EquipmentSkinTypeBase {
    
    private ArrayList<IEquipmentSkinPart> skinParts;
    
    public SkinChest() {
        skinParts = new ArrayList<IEquipmentSkinPart>();
        skinParts.add(new SkinChestPartBase(this));
        skinParts.add(new SkinChestPartLeftArm(this));
        skinParts.add(new SkinChestPartRightArm(this));
    }
    
    @Override
    public ArrayList<IEquipmentSkinPart> getSkinParts() {
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
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_CHEST);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 1;
    }
}
