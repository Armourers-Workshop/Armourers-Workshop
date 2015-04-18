package riskyken.armourersWorkshop.common.equipment.skin;

import javax.vecmath.Point3i;

import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class SkinTypeBase implements ISkinType {

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        for (int i = 0; i < this.getSkinParts().size(); i++) {
            ISkinPart skinPart = this.getSkinParts().get(i);
            Point3i partOffset = skinPart.getOffset();
            GL11.glTranslated(partOffset.x * scale, partOffset.y * scale, partOffset.z * scale);
            skinPart.renderBuildingGuide(scale, showSkinOverlay, showHelper);
            GL11.glTranslated(-partOffset.x * scale, -partOffset.y * scale, -partOffset.z * scale);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGrid(float scale) {
        for (int i = 0; i < this.getSkinParts().size(); i++) {
            ISkinPart skinPart = this.getSkinParts().get(i);
            Point3i partOffset = skinPart.getOffset();
            GL11.glTranslated(partOffset.x * scale, partOffset.y * scale, partOffset.z * scale);
            skinPart.renderBuildingGrid(scale);
            GL11.glTranslated(-partOffset.x * scale, -partOffset.y * scale, -partOffset.z * scale);
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
        // TODO Auto-generated method stub
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
        return false;
    }
}
