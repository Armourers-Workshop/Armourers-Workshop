package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;
import java.util.BitSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.custom.equipment.CustomModelRenderer;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourPartData;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomEquipmentBlockData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomItemBuilt extends ModelBiped implements Runnable {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    private final CustomModelRenderer main;
    private int timeFromRender = 0;
    public final String renderId;
    private boolean displayCompiled;
    private int displayList;
    private boolean facesCompiled;
    private boolean facesCompileStarted;
    ArrayList<CustomEquipmentBlockData> blocks = new ArrayList<CustomEquipmentBlockData>();
    BitSet faceFlags;
            
    private static float scale = 0.0625F;
    
    public static void bindArmourTexture() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }
    
    public ModelCustomItemBuilt(CustomArmourItemData itemData, ArmourType armourType, String renderId) {
        this.renderId = renderId;
        textureWidth = 4;
        textureHeight = 4;
        
        main = new CustomModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
        
        ArrayList<CustomArmourPartData> parts = itemData.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadPart(parts.get(i));
        }
    }
    
    private void loadPart(CustomArmourPartData part) {
        ArrayList<CustomEquipmentBlockData> partBlocks = part.getArmourData();
        for (int i = 0; i < partBlocks.size(); i++) {
            CustomEquipmentBlockData blockData = partBlocks.get(i);
            
            switch (part.getArmourPart()) {
            case LEFT_ARM:
                blockData.x += 7;
                blockData.y += 2;
                break;
            case RIGHT_ARM:
                blockData.x -= 7;
                blockData.y += 2;
                break;
            case LEFT_LEG:
                blockData.x -= 4;
                break;
            case RIGHT_LEG:
                blockData.x += 4;
                break;
            case LEFT_FOOT:
                blockData.x -= 4;
                break;
            case RIGHT_FOOT:
                blockData.x += 4;
                break;
            default:
                break;
            }
            
            blocks.add(partBlocks.get(i));
        }
    }
    
    private void setBlockFaceFlags(ArrayList<CustomEquipmentBlockData> partBlocks, CustomEquipmentBlockData block) {
        block.faceFlags = new BitSet(6);
        for (int j = 0; j < partBlocks.size(); j++) {
            CustomEquipmentBlockData checkBlock = partBlocks.get(j);
            checkFaces(block, checkBlock);
        }
    }
    
    private void checkFaces(CustomEquipmentBlockData block, CustomEquipmentBlockData checkBlock) {
        ForgeDirection[] dirs = { ForgeDirection.EAST, ForgeDirection.WEST,  ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH };
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (block.x + dir.offsetX == checkBlock.x) {
                if (block.y + dir.offsetY == checkBlock.y) {
                    if (block.z + dir.offsetZ == checkBlock.z) {
                        block.faceFlags.set(i, true); 
                    }
                }
            }
        }
    }

    public void render() {
        timeFromRender = 0;
        
        if (!facesCompiled) {
            if (!facesCompileStarted) {
                facesCompileStarted = true;
                new Thread(this, "Item model face cull").start();
            }
            return;
        }
        
        if (!this.displayCompiled) {
            this.compileDisplayList();
        }
        GL11.glCallList(this.displayList);
    }
    

    @Override
    public void run() {
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            setBlockFaceFlags(blocks, blockData);
        }
        facesCompiled = true;
    }
    
    public void tick() {
        timeFromRender++;
    }
    
    public boolean needsCleanup() {
        if (timeFromRender > 6) {
            return true;
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    private void compileDisplayList() {
        this.displayList = GLAllocation.generateDisplayLists(1);
        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        Tessellator tessellator = Tessellator.instance;

        oldRender();

        GL11.glEndList();
        this.displayCompiled = true;
    }
    
    public void cleanUp() {
        if (this.displayCompiled) {
            GLAllocation.deleteDisplayLists(this.displayList);
        }
    }
    
    private void oldRender() {
        bindArmourTexture();
        GL11.glPushMatrix();
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            if (!blockData.isGlowing()) {
                renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale, blockData.faceFlags);
            }
        }
        
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        for (int i = 0; i < blocks.size(); i++) {
            CustomEquipmentBlockData blockData = blocks.get(i);
            if (blockData.isGlowing()) {
                renderArmourBlock(blockData.x, blockData.y, blockData.z, blockData.colour, scale, blockData.faceFlags);
            }
        }
        
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
    
    public void renderArmourBlock(int x, int y, int z, int colour, float scale, BitSet faceFlags) {
        float colourRed = (colour >> 16 & 0xff) / 255F;
        float colourGreen = (colour >> 8 & 0xff) / 255F;
        float colourBlue = (colour & 0xff) / 255F;

        GL11.glPushMatrix();
        GL11.glColor3f(colourRed, colourGreen, colourBlue);
        GL11.glTranslated(x * scale, y * scale, z * scale);
        main.render(scale, faceFlags);
        GL11.glPopMatrix();
    }
}
