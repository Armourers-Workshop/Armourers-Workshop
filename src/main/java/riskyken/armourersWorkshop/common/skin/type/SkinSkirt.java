package riskyken.armourersWorkshop.common.skin.type;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;

public class SkinSkirt extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinSkirt() {
        skinParts = new ArrayList<ISkinPartType>();
        skinParts.add(new SkinSkirtPartBase(this));
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return this.skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:skirt";
    }
    
    @Override
    public String getName() {
        return "Skirt";
    }
    
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_SKIRT);
        this.emptySlotIcon = register.registerIcon(LibItemResources.SLOT_SKIN_SKIRT);
    }

    @Override
    public int getVanillaArmourSlotId() {
        return 2;
    }
}
