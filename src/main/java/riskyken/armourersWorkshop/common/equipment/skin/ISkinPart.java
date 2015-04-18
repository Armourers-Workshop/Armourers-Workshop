package riskyken.armourersWorkshop.common.equipment.skin;

import javax.vecmath.Point3i;

import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.Rectangle3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public interface ISkinPart {
    
    public Rectangle3D getBuildingSpace();
    
    public Rectangle3D getGuideSpace();
    
    public Point3i getOffset();
    
    @SideOnly(Side.CLIENT)
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper);
    
    @SideOnly(Side.CLIENT)
    public void renderBuildingGrid(float scale);
    
    public void createBoundingBoxesForPart(World world, int x, int y, int z);
    
    public void removeBoundingBoxesForPart(World world, int x, int y, int z);
}
