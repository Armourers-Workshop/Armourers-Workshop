package riskyken.armourersWorkshop.common.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;

public class SkinChest extends AbstractSkinTypeBase {
    
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinChest() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinChestPartBase(this));
        skinParts.add(new SkinChestPartLeftArm(this));
        skinParts.add(new SkinChestPartRightArm(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
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
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_CHEST);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 1;
    }
}
