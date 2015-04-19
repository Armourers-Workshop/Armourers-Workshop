package riskyken.armourersWorkshop.api.common.equipment.skin;

import java.util.ArrayList;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ISkinType {

    public ArrayList<ISkinPart> getSkinParts();
    
    public String getRegistryName();
    
    public String getName();
    
    @SideOnly(Side.CLIENT)
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper);
    
    @SideOnly(Side.CLIENT)
    public void renderBuildingGrid(float scale);
    
    public void createBoundingBoxes(World world, int x, int y, int z);
    
    public void removeBoundingBoxes(World world, int x, int y, int z);

    public int clearArmourCubes(World world, int x, int y, int z);
    
    public boolean showSkinOverlayCheckbox();
    
    public int getVanillaArmourSlotId();
    
    public int getId();
    
    public void setId(int id);
}
