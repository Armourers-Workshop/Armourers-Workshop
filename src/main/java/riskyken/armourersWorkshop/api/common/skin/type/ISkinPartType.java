package riskyken.armourersWorkshop.api.common.skin.type;

import javax.vecmath.Point3i;

import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public interface ISkinPartType {
    
    public String getRegistryName();
    
    public String getPartName();
    
    public Rectangle3D getBuildingSpace();
    
    public Rectangle3D getGuideSpace();
    
    public Point3i getOffset();
    
    /**
     * 
     * @param scale Normally 0.0625F.
     * @param showSkinOverlay
     * @param showHelper
     */
    @SideOnly(Side.CLIENT)
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper);
}
