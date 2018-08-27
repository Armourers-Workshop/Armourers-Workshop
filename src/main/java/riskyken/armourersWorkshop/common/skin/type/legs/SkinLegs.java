package riskyken.armourersWorkshop.common.skin.type.legs;

import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinLegs extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinLegs() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinLegsPartLeftLeg(this));
        skinParts.add(new SkinLegsPartRightLeg(this));
        skinParts.add(new SkinLegsPartSkirt(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
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
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_LEGS);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_LEGS);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_ARMOUR_OVERRIDE);
        return properties;
    }
}
