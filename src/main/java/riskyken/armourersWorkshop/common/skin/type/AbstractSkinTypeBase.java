package riskyken.armourersWorkshop.common.skin.type;

import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractSkinTypeBase implements ISkinType {

    private int id = -1;
    
    @SideOnly(Side.CLIENT)
    protected IIcon icon;
    
    @SideOnly(Side.CLIENT)
    protected IIcon emptySlotIcon;
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon() {
        return this.icon;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getEmptySlotIcon() {
        return this.emptySlotIcon;
    }
    
    @Override
    public boolean showSkinOverlayCheckbox() {
        return false;
    }
    
    @Override
    public boolean showHelperCheckbox() {
        return false;
    }
    
    @Override
    public int getVanillaArmourSlotId() {
        return -1;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
}
