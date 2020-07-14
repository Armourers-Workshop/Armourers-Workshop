package riskyken.armourersWorkshop.common.skin.type;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperties;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;

public abstract class AbstractSkinTypeBase implements ISkinType {
    
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
    public boolean isHidden() {
        return false;
    }
    
    @Override
    public boolean enabled() {
        return true;
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = new ArrayList<ISkinProperty<?>>();
        properties.add(SkinProperties.PROP_ALL_FLAVOUR_TEXT);
        return properties;
    }
    
    @Override
    public boolean haveBoundsChanged(ISkinProperties skinPropsOld, ISkinProperties skinPropsNew) {
        for (ISkinPartType partType : getSkinParts()) {
            if (partType.isModelOverridden(skinPropsOld) != partType.isModelOverridden(skinPropsNew)) {
                return true;
            }
        }
        return false;
    }
}
