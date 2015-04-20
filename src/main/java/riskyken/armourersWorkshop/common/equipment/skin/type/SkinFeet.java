package riskyken.armourersWorkshop.common.equipment.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeBase;

public class SkinFeet extends SkinTypeBase {

    private ArrayList<ISkinPart> skinParts;
    
    public SkinFeet() {
        skinParts = new ArrayList<ISkinPart>();
        skinParts.add(new SkinFeetPartLeftFoot(this));
        skinParts.add(new SkinFeetPartRightFoot(this));
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
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
