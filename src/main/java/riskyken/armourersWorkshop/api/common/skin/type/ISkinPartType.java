package riskyken.armourersWorkshop.api.common.skin.type;

import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public interface ISkinPartType {
    
    public String getRegistryName();
    
    public String getPartName();
    
    public IRectangle3D getBuildingSpace();
    
    public IRectangle3D getGuideSpace();
    
    public IPoint3D getOffset();
    
    /**
     * 
     * @param scale Normally 0.0625F.
     * @param showSkinOverlay
     * @param showHelper
     */
    @SideOnly(Side.CLIENT)
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper);
}
