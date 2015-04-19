package riskyken.armourersWorkshop.api.common.equipment.skin;

import java.util.ArrayList;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author RiskyKen
 *
 */
public interface ISkinType {

    public ArrayList<ISkinPart> getSkinParts();
    
    /**
     * Gets the name this skin will be registered with.
     * Armourer's Workshop uses the format armourers:skinName.
     * Example armourers:head is the registry name of
     * Armourer's Workshop head armour skin.
     * @return registryName
     */
    public String getRegistryName();
    
    /**
     * This only exists for backwards compatibility with old world saves.
     * Just return getRegistryName().
     * @return name
     */
    public String getName();
    
    /**
     * 
     * @param scale Normaly 0.0625F.
     * @param showSkinOverlay
     * @param showHelper
     */
    @SideOnly(Side.CLIENT)
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper);
    
    @SideOnly(Side.CLIENT)
    public void renderBuildingGrid(float scale);
    
    public void createBoundingBoxes(World world, int x, int y, int z);
    
    public void removeBoundingBoxes(World world, int x, int y, int z);

    public int clearArmourCubes(World world, int x, int y, int z);
    
    /**
     * Should the show skin overlay check box be shown in the armourer and mini armourer.
     * @return
     */
    public boolean showSkinOverlayCheckbox();
    
    /**
     * If this skin is for vanilla armour return the slot id here, otherwise return -1.
     * @return slotId
     */
    public int getVanillaArmourSlotId();
    
    /**
     * Should return id that was given in setId.
     * @return id
     */
    public int getId();
    
    /**
     * Id given to this skin when it is register.
     * @param id
     */
    public void setId(int id);
}
