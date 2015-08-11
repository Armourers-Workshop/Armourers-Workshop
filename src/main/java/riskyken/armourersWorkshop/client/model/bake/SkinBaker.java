package riskyken.armourersWorkshop.client.model.bake;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.skin.cubes.CubeFactory;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public final class SkinBaker {
    
    public static boolean withinMaxRenderDistance(double x, double y, double z) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (player.getDistance(x, y, z) > ConfigHandler.maxSkinRenderDistance) {
            return false;
        }
        return true;
    }
    
    public static void cullFacesOnEquipmentPart(SkinPart partData) {
        ArrayList<ICube> blocks = partData.getArmourData();
        partData.totalCubesInPart = new int[CubeFactory.INSTANCE.getTotalCubes()];
        for (int i = 0; i < blocks.size(); i++) {
            ICube blockData = blocks.get(i);
            int cubeId = CubeFactory.INSTANCE.getIdForCubeClass(blockData.getClass());
            partData.totalCubesInPart[cubeId] += 1;
            setBlockFaceFlags(blocks, blockData);
        }
    }
    
    private static void setBlockFaceFlags(ArrayList<ICube> partBlocks, ICube block) {
        block.setFaceFlags(new BitSet(6));
        for (int j = 0; j < partBlocks.size(); j++) {
            ICube checkBlock = partBlocks.get(j);
            checkFaces(block, checkBlock);
        }
    }
    
    private static void checkFaces(ICube block, ICube checkBlock) {
        ForgeDirection[] dirs = {ForgeDirection.UP, ForgeDirection.DOWN, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };
        //dirs = ForgeDirection.VALID_DIRECTIONS;
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (block.getX() + dir.offsetX == checkBlock.getX()) {
                if (block.getY() + dir.offsetY == checkBlock.getY()) {
                    if (block.getZ() + dir.offsetZ == checkBlock.getZ()) {
                        if (block.needsPostRender() == checkBlock.needsPostRender()) {
                            block.getFaceFlags().set(i, true); 
                        }
                    }
                }
            }
        }

    }
    
    public static void buildPartDisplayListArray(SkinPart partData) {
        boolean multipassSkinRendering = ClientProxy.useMultipassSkinRendering();
        
        ArrayList<ColouredVertexWithUV>[] renderLists;
        int[] pie;
        
        pie = new int[3];
        
        if (multipassSkinRendering) {
            renderLists = (ArrayList<ColouredVertexWithUV>[]) new ArrayList[4];
        } else {
            renderLists = (ArrayList<ColouredVertexWithUV>[]) new ArrayList[2];
        }
        
        for (int i = 0; i < renderLists.length; i++) {
            renderLists[i] = new ArrayList<ColouredVertexWithUV>();
        }
        
        float scale = 0.0625F;
        
        for (int i = 0; i < partData.getArmourData().size(); i++) {
            ICube cube = partData.getArmourData().get(i);
            
            ICubeColour cc = cube.getCubeColour();
            byte a = (byte) 255;
            if (cube.needsPostRender()) {
                a = (byte) 127;
            }
            
            if (multipassSkinRendering) {
                int listIndex = 0;
                if (cube.isGlowing() && !cube.needsPostRender()) {
                    listIndex = 1;
                }
                if (cube.needsPostRender() && !cube.isGlowing()) {
                    listIndex = 2;
                }
                if (cube.isGlowing() && cube.needsPostRender()) {
                    listIndex = 3;
                }
                EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[listIndex],
                        scale, cube.getFaceFlags(), cube.getX(), cube.getY(), cube.getZ(),
                        cc.getRed(), cc.getGreen(), cc.getBlue(), a);
            } else {
                if (cube.isGlowing()) {
                    EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[1],
                            scale, cube.getFaceFlags(), cube.getX(), cube.getY(), cube.getZ(),
                            cc.getRed(), cc.getGreen(), cc.getBlue(), a);
                } else {
                    EquipmentPartRenderer.INSTANCE.main.buildDisplayListArray(renderLists[0],
                            scale, cube.getFaceFlags(), cube.getX(), cube.getY(), cube.getZ(),
                            cc.getRed(), cc.getGreen(), cc.getBlue(), a);
                }
            }
        }
        
        partData.getArmourData().clear();
        
        partData.setVertexLists(renderLists);
    }
    
    /*
    private static void checkBlockFaceIntersectsBodyPart(EnumBodyPart bodyPart, ICube block) {
        ForgeDirection[] dirs = { ForgeDirection.EAST, ForgeDirection.WEST,  ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH };
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (cordsIntersectsBodyPart(bodyPart, (block.getX() + dir.offsetX), -(block.getY() + dir.offsetY + 1), (block.getZ() + dir.offsetZ))) {
                block.getFaceFlags().set(i, true);
            }
        }
    }
    
    private static boolean cordsIntersectsBodyPart(EnumBodyPart bodyPart, int x, int y, int z) {
        int xCen = x + bodyPart.xOrigin;
        int yCen = y + 1 + bodyPart.yOrigin;
        int zCen = z + bodyPart.zOrigin;
        
        if (bodyPart.xSize > xCen & 0 <= xCen) {
            if (bodyPart.zSize > zCen & 0 <= zCen) {
                if (bodyPart.ySize > yCen & 0 <= yCen) {
                    return true;
                }
            }
        }
        return false;
    }
    */
}
