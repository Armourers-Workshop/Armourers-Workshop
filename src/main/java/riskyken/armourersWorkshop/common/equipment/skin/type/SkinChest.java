package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinChest extends SkinTypeBase {
    
    private ArrayList<ISkinPart> skinParts;
    
    public SkinChest() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinChestPartBase(this));
        skinParts.add(new SkinChestPartLeftArm(this));
        skinParts.add(new SkinChestPartRightArm(this));
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
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_CHEST);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 1;
    }
}
