package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinHead extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinHead() {
        this.skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinHeadPartBase(this));
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
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_HEAD);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 0;
    }
}
