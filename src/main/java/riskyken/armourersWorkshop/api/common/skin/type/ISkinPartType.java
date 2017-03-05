package riskyken.armourersWorkshop.api.common.skin.type;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;


public interface ISkinPartType {
    
    /**
     * Gets the name this skin will be registered with.
     * Armourer's Workshop uses the format baseType.getRegistryName() + "." + getPartName().
     * Example armourers:chest.leftArm is the registry name of
     * Armourer's Workshop chest left arm skin part.
     * @return Registry name
     */
    public String getRegistryName();
    
    /**
     * Get the name of this part.
     * @return Part name
     */
    public String getPartName();
    
    /**
     * The last 3 values are used to define the size of this part, the first 3 values will change the origin.
     * Example -5, -5, -5, 10, 10, 10, Will create a 10x10x10 cube with it's origin in the centre.
     * @return
     */
    public IRectangle3D getBuildingSpace();
    
    /**
     * The last 3 values set the size of the invisible blocks that cubes can be placed on, the first 3 set the offset.
     * Use 0, 0, 0, 0, 0, 0, if you don't want to use this.
     * Setting showArmourerDebugRender to true in the config will show this box.
     * @return
     */
    public IRectangle3D getGuideSpace();
    
    /**
     * This is used by the armourer to position this part 
     * @return
     */
    public IPoint3D getOffset();
    
    /**
     * 
     * @param scale Normally 0.0625F.
     * @param showSkinOverlay
     * @param showHelper
     */
    @SideOnly(Side.CLIENT)
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper);
    
    /**
     * Get the minimum number of markers needed for this skin part.
     * @return
     */
    public int getMinimumMarkersNeeded();
    
    /**
     * Gets the maximum number of markers allowed for this skin part.
     * @return
     */
    public int getMaximumMarkersNeeded();
    
    /**
     * If true this part must be present for the skin to be saved.
     * @return
     */
    public boolean isPartRequired();
}
