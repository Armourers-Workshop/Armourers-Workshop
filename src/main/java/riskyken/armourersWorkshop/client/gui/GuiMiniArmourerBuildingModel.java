package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.client.render.SkinRenderHelper;
import riskyken.armourersWorkshop.common.skin.cubes.Cube;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;

public class GuiMiniArmourerBuildingModel {

    private final Minecraft mc;
    private final GuiScreen parent;
    
    private ArrayList<ICube> cubes;
    
    private int mouseLeftDownId = -1;
    private int mouseRightDownId = -1;
    
    public float zoom = 150.0F;
    private float rotation = 0F;
    private float pitch = 0F;
    
    private boolean mouseLeftIsDown = false;
    private boolean mouseRightIsDown = false;
    private boolean mouseCenterIsDown = false;
    
    private int mouseRightDownPosX = 0;
    private int mouseRightDownPosY = 0;
    private int lastMousePosX = 0;
    private int lastMousePosY = 0;
    
    public ISkinType currentSkinType;
    public ISkinPartType currentSkinPartType;
    public ItemStack stack;
    public SkinPointer skinPointer;
    
    public GuiMiniArmourerBuildingModel(GuiScreen parent, Minecraft mc) {
        this.parent = parent;
        this.mc = mc;
        
        this.cubes = new ArrayList<ICube>();
        ICube cube = new Cube();
        cube.setColour(UtilColour.getMinecraftColor(1));
        this.cubes.add(cube);
        
        cube = new Cube();
        cube.setColour(UtilColour.getMinecraftColor(2));
        cube.setY((byte) -8);
        this.cubes.add(cube);
    }
    
    public void drawScreen(int mouseX, int mouseY) {
        if (stack != null) {
            skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
        }
        renderModels(mouseX, mouseY);
        lastMousePosX = mouseX;
        lastMousePosY = mouseY;
    }
    
    private void renderModels(int mouseX, int mouseY) {
        float scale = 0.0625F;
        GL11.glPushMatrix();
        
        GL11.glTranslatef(parent.width / 2, parent.height / 2, 500.0F);
        GL11.glScalef((float)(-zoom), (float)zoom, (float)zoom);
        
        GL11.glRotatef(180F, 0F, 1F, 0F);
        
        
        
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        
        GL11.glTranslated(0, 1 * scale, 0);
        
        
        drawBuildingCubes(true);
        Color c = getColourAtPos(Mouse.getX(), Mouse.getY());
        int hoverCubeId = getIdFromColour(c);
        
        
        if (Mouse.isButtonDown(0)) {
            if (!mouseLeftIsDown) {
                mouseLeftIsDown = true;
                mouseLeftDownId = hoverCubeId;
            }
        } else {
            if (mouseLeftIsDown) {
                mouseLeftIsDown = false;
                if (mouseLeftDownId != 0 & hoverCubeId == mouseLeftDownId) {
                    int cubeId = (int) Math.ceil((double)mouseLeftDownId / 6);
                    int cubeFace = cubeId * 6 - mouseLeftDownId;
                    cubeClicked(cubeId, cubeFace, 0);
                    mouseLeftDownId = 0;
                }
            }
        }
        
        if (Mouse.isButtonDown(1)) {
            if (!mouseRightIsDown) {
                mouseRightIsDown = true;
                mouseRightDownPosX = mouseX;
                mouseRightDownPosY = mouseY;
                mouseRightDownId = hoverCubeId;
            } else {
                rotation += lastMousePosX - mouseX;
                pitch -= lastMousePosY - mouseY;
            }
        } else {
            if (mouseRightIsDown) {
                mouseRightIsDown = false;
                if (mouseRightDownId != 0 & hoverCubeId == mouseRightDownId) {
                    int cubeId = (int) Math.ceil((double)mouseRightDownId / 6);
                    int cubeFace = cubeId * 6 - mouseRightDownId;
                    cubeClicked(cubeId, cubeFace, 1);
                    mouseRightDownId = 0;
                }
            }
        }
        
        drawBuildingCubes(false);
        
        //Are we hovering over a cube?
        if (hoverCubeId != 0) {
            int cubeId = (int) Math.ceil((double)hoverCubeId / 6);
            int cubeFace = cubeId * 6 - hoverCubeId;
            
            if (cubeId - 1 < cubes.size() & cubeId - 1 >= 0) {
                ICube tarCube = cubes.get(cubeId - 1);
                
                ICube newCube = new Cube();
                ForgeDirection dir = getDirectionForCubeFace(cubeFace);
                newCube.setX((byte) (tarCube.getX() + dir.offsetX));
                newCube.setY((byte) (tarCube.getY() + dir.offsetY));
                newCube.setZ((byte) (tarCube.getZ() + dir.offsetZ));
                newCube.setColour(0xFFFFFFFF);
                int[] colour = {newCube.getColour(), newCube.getColour(), newCube.getColour(), newCube.getColour(), newCube.getColour(), newCube.getColour()};
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                EquipmentPartRenderer.INSTANCE.renderArmourBlock(newCube.getX(), newCube.getY(), newCube.getZ(), newCube.getCubeColour(), scale, null, true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
        
        RenderHelper.enableStandardItemLighting();
        
        
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());
        
        
        
        if (skinPointer != null) {
            Skin skin = ClientModelCache.INSTANCE.getEquipmentItemData(skinPointer.getSkinId());
            for (int i = 0; i < skin.getParts().size(); i++) {
                SkinPart part = skin.getParts().get(i);
                if (part.getPartType() == currentSkinPartType) {
                    EquipmentPartRenderer.INSTANCE.renderPart(part, scale);
                }
            }
            
        }
        GL11.glTranslated(0, -currentSkinPartType.getBuildingSpace().getY() * scale, 0);
        
        if (currentSkinPartType != null) {
            currentSkinPartType.renderBuildingGuide(scale, true, false);
            SkinRenderHelper.renderBuildingGrid(currentSkinPartType, scale);
        }
        /*
        if (currentSkinType != null) {
            SkinRenderHelper.renderBuildingGuide(currentSkinType, scale, true, false);
            SkinRenderHelper.renderBuildingGrid(currentSkinType, scale);
        }
        */
        GL11.glPopMatrix();
    }
    
    private void cubeClicked(int cubeId, int cubeFace, int button) {
        if (cubeId - 1 < cubes.size() & cubeId - 1 >= 0) {
            ICube tarCube = cubes.get(cubeId - 1);

            if (button == 0) {
                ICube newCube = new Cube();
                newCube.setColour(tarCube.getColour());
                ForgeDirection dir = getDirectionForCubeFace(cubeFace);
                newCube.setX((byte) (tarCube.getX() + dir.offsetX));
                newCube.setY((byte) (tarCube.getY() + dir.offsetY));
                newCube.setZ((byte) (tarCube.getZ() + dir.offsetZ));
                ModLogger.log(newCube);
                cubes.add(newCube);
            }
            
            if (button == 1) {
                cubes.remove(cubeId - 1);
            }
            
            ModLogger.log("cubeId:" + cubeId + " cubeFace:" + cubeFace); 
        }
    }
    
    private void drawBuildingCubes(boolean fake) {
        //GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if (fake) {
            GL11.glDisable(GL11.GL_LIGHTING);
        }

        float scale = 0.0625F;
        int colourId = 1;
        
        for (int i = 0; i < cubes.size(); i++) {
            ICube cube = cubes.get(i);
            if (cube != null) {
                if (cube.isGlowing() & !fake) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    ModRenderHelper.disableLighting();
                }
                int colour[];
                if (fake) {
                    colour = new int[] {
                            getColourFromId(colourId).getRGB(),
                            getColourFromId(colourId + 1).getRGB(),
                            getColourFromId(colourId + 2).getRGB(),
                            getColourFromId(colourId + 3).getRGB(),
                            getColourFromId(colourId + 4).getRGB(),
                            getColourFromId(colourId + 5).getRGB()};
                } else {
                    colour = new int[] {cube.getColour(), cube.getColour(), cube.getColour(), cube.getColour(), cube.getColour(), cube.getColour()};
                }
                
                EquipmentPartRenderer.INSTANCE.renderArmourBlock(cube.getX(), cube.getY(), cube.getZ(), cube.getCubeColour(), scale, null, false);
                if (cube.isGlowing() & !fake) {
                    ModRenderHelper.enableLighting();
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
            }
            colourId += 6;
        }
        if (fake) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    
    private Color getColourFromId(int id) {
        int r = 0;
        int g = 0;
        int b = 0;
        while (id > 255 * 256) {
            b += 1;
            id -= 256 * 256;
        }
        while (id > 255) {
            g += 1;
            id -= 256;
        }
        while (id > 0) {
            r += 1;
            id -= 1;
        }
        return new Color(r, g, b);
    }
    
    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r,g,b);
    }
    
    private int getIdFromColour(Color colour) {
        Color c = new Color(colour.getRGB());
        int id = c.getRed();
        id += c.getGreen() * 256;
        id += c.getBlue() * 256 * 256;
        return id;
    }
    
    private ForgeDirection getDirectionForCubeFace(int cubeFace) {
        ForgeDirection dir;
        switch (cubeFace) {
        case 1:
            dir = ForgeDirection.NORTH;
            break;
        case 0:
            dir = ForgeDirection.SOUTH;
            break;
        case 4:
            dir = ForgeDirection.WEST;
            break;
        case 5:
            dir = ForgeDirection.EAST;
            break;
        case 3:
            dir = ForgeDirection.DOWN;
            break;
        case 2:
            dir = ForgeDirection.UP;
            break;
        default:
            dir = ForgeDirection.UNKNOWN;
            break;
        }
        return dir;
    }
}
