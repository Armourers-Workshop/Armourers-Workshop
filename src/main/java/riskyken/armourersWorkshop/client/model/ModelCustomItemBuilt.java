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

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.client.model.custom.equipment.CustomModelRenderer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.equipment.cubes.ICube;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomItemBuilt extends ModelBiped implements Runnable {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    private final CustomModelRenderer main;
    private int timeFromRender = 0;
    public final int renderId;
    private boolean displayCompiled;
    private int displayList;
    private boolean facesCompiled;
    private boolean facesCompileStarted;
    ArrayList<ICube> blocks = new ArrayList<ICube>();
    BitSet faceFlags;
            
    private static float scale = 0.0625F;
    
    public static void bindArmourTexture() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
    }
    
    public ModelCustomItemBuilt(CustomEquipmentItemData itemData, EnumEquipmentType armourType, int renderId) {
        this.renderId = renderId;
        textureWidth = 4;
        textureHeight = 4;
        
        main = new CustomModelRenderer(this, 0, 0);
        main.addBox(0F, 0F, 0F, 1, 1, 1);
        main.setRotationPoint(0, 0, 0);
        
        ArrayList<CustomEquipmentPartData> parts = itemData.getParts();
        
        for (int i = 0; i < parts.size(); i++) {
            loadPart(parts.get(i));
        }
    }
    
    private void loadPart(CustomEquipmentPartData part) {
        ArrayList<ICube> partBlocks = part.getArmourData();
        for (int i = 0; i < partBlocks.size(); i++) {
            ICube blockData = partBlocks.get(i);
            
            switch (part.getArmourPart()) {
            case LEFT_ARM:
                blockData.setX((byte) (blockData.getX() + 7));
                blockData.setY((byte) (blockData.getY() + 2));
                break;
            case RIGHT_ARM:
                blockData.setX((byte) (blockData.getX() - 7));
                blockData.setY((byte) (blockData.getY() + 2));
                break;
            case LEFT_LEG:
                blockData.setX((byte) (blockData.getX() - 4));
                break;
            case RIGHT_LEG:
                blockData.setX((byte) (blockData.getX() + 4));
                break;
            case LEFT_FOOT:
                blockData.setX((byte) (blockData.getX() - 4));
                break;
            case RIGHT_FOOT:
                blockData.setX((byte) (blockData.getX() + 4));
                break;
            default:
                break;
            }
            
            blocks.add(partBlocks.get(i));
        }
    }
    
    private void setBlockFaceFlags(ArrayList<ICube> partBlocks, ICube block) {
        block.setFaceFlags(new BitSet(6));
        for (int j = 0; j < partBlocks.size(); j++) {
            ICube checkBlock = partBlocks.get(j);
            checkFaces(block, checkBlock);
        }
    }
    
    private void checkFaces(ICube block, ICube checkBlock) {
        ForgeDirection[] dirs = { ForgeDirection.EAST, ForgeDirection.WEST,  ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH };
        for (int i = 0; i < dirs.length; i++) {
            ForgeDirection dir = dirs[i];
            if (block.getX() + dir.offsetX == checkBlock.getX()) {
                if (block.getY() + dir.offsetY == checkBlock.getY()) {
                    if (block.getZ() + dir.offsetZ == checkBlock.getZ()) {
                        block.getFaceFlags().set(i, true); 
                    }
                }
            }
        }
    }

    public void render() {
        timeFromRender = 0;
        GL11.glColor3f(1F, 1F, 1F);
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
        //GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
        GL11.glCallList(this.displayList);
        //GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
        GL11.glColor3f(1F, 1F, 1F);
    }
    

    @Override
    public void run() {
        for (int i = 0; i < blocks.size(); i++) {
            ICube blockData = blocks.get(i);
            setBlockFaceFlags(blocks, blockData);
        }
        facesCompiled = true;
    }
    
    public void tick() {
        timeFromRender++;
    }
    
    public boolean needsCleanup() {
        if (timeFromRender > ConfigHandler.modelCacheTime) {
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
            ICube blockData = blocks.get(i);
            if (!blockData.isGlowing()) {
                renderArmourBlock(blockData.getX(), blockData.getY(), blockData.getZ(), blockData.getColour(), scale, blockData.getFaceFlags());
            }
        }
        
        float lastBrightnessX = OpenGlHelper.lastBrightnessX;
        float lastBrightnessY = OpenGlHelper.lastBrightnessY;
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        
        for (int i = 0; i < blocks.size(); i++) {
            ICube blockData = blocks.get(i);
            if (blockData.isGlowing()) {
                renderArmourBlock(blockData.getX(), blockData.getY(), blockData.getZ(), blockData.getColour(), scale, blockData.getFaceFlags());
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
