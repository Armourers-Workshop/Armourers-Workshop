package riskyken.armourersWorkshop.common.equipment.skin;

import java.util.ArrayList;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinHead implements ISkinType {

    private ISkinPart partHead;
    private ArrayList<ISkinPart> skinParts;
    
    public SkinHead() {
        this.skinParts = new ArrayList<ISkinPart>();
        partHead = new SkinHeadPartBase();
        skinParts.add(partHead);
    }
    
    @Override
    public ArrayList<ISkinPart> getSkinParts() {
        return this.skinParts;
    }
    
    @Override
    public String getRegistryName() {
        return "armourers:head";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        for (int i = 0; i < this.skinParts.size(); i++) {
            ISkinPart skinPart = this.skinParts.get(i);
            skinPart.renderBuildingGuide(scale, showSkinOverlay, showHelper);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGrid(float scale) {
        for (int i = 0; i < this.skinParts.size(); i++) {
            ISkinPart skinPart = this.skinParts.get(i);
            skinPart.renderBuildingGrid(scale);
        }
    }

    @Override
    public void createBoundingBoxes(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeBoundingBoxed(World world, int x, int y, int z) {
        // TODO Auto-generated method stub
    }

    @Override
    public int clearArmourCubes() {
        /*
        for (int i = 0; i < type.getParts().length; i++) {
            EnumEquipmentPart part = type.getParts()[i];
            ModLogger.log("Clearing " + part);
            for (int ix = 0; ix <  part.getTotalXSize(); ix++) {
                for (int iy = 0; iy < part.getTotalYSize(); iy++) {
                    for (int iz = 0; iz <  part.getTotalZSize(); iz++) {
                        int tarX = xCoord + part.getStartX() - part.xLocation + ix;
                        int tarY = yCoord + part.getStartY() + getHeightOffset() + part.yLocation + iy;
                        int tarZ = zCoord + part.getStartZ() - part.zLocation + iz;
                        Block tarBlock = worldObj.getBlock(tarX, tarY, tarZ);
                        if (
                                tarBlock == ModBlocks.colourable |
                                tarBlock == ModBlocks.colourableGlowing |
                                tarBlock == ModBlocks.colourableGlass |
                                tarBlock == ModBlocks.colourableGlassGlowing
                            ) {
                            worldObj.setBlockToAir(tarX, tarY, tarZ);
                        }
                    }
                }
            }
        }
        */
        return 0;
    }

    @Override
    public boolean showSkinOverlayCheckbox() {
        // TODO Auto-generated method stub
        return false;
    }
}
