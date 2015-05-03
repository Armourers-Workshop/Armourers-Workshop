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
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiMiniArmourerAddOrEdit;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiMiniArmourerRemove;
import riskyken.armourersWorkshop.common.skin.cubes.Cube;
import riskyken.armourersWorkshop.common.skin.cubes.CubeColour;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.ModLogger;

public class GuiMiniArmourerBuildingModel {

    private final Minecraft mc;
    private final GuiScreen parent;
    private TileEntityMiniArmourer tileEntity;
    
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
    
    public GuiMiniArmourerBuildingModel(GuiScreen parent, Minecraft mc, TileEntityMiniArmourer tileEntity) {
        this.parent = parent;
        this.mc = mc;
        this.tileEntity = tileEntity;
        
        //this.cubes = new ArrayList<ICube>();
        //ICube cube = new Cube();
        //cube.setY((byte) -1);
        //cube.setColour(0xFF0000, 0);
        //cube.setColour(0x00FF00, 1);
        //cube.setColour(0x0000FF, 2);
        //cube.setColour(0xFF00FF, 3);
        
        //this.cubes.add(cube);
        
        //cube = new Cube();
        //cube.setColour(UtilColour.getMinecraftColor(2));
        //cube.setY((byte) -8);
        //this.cubes.add(cube);
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
        cubes = null;
        ArrayList<SkinPart> skinParts = tileEntity.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            if (skinParts.get(i).getPartType() == currentSkinPartType) {
                cubes = skinParts.get(i).getArmourData();
            }
        }
        
        float scale = 0.0625F;
        GL11.glPushMatrix();
        
        GL11.glTranslatef(parent.width / 2, parent.height / 2, 500.0F);
        GL11.glScalef((float)(-zoom), (float)zoom, (float)zoom);
        
        GL11.glRotatef(180F, 0F, 1F, 0F);
        
        
        GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        
        
        GL11.glTranslated(0, 1 * scale, 0);
        
        GL11.glScalef(-1, -1, 1);
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
        if (hoverCubeId != 0 && cubes != null) {
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
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                EquipmentPartRenderer.INSTANCE.renderArmourBlock(newCube.getX(), newCube.getY(), newCube.getZ(), newCube.getCubeColour(), scale, null, true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
        
        GL11.glScalef(-1, -1, 1);
        
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
        if (cubes != null && cubeId - 1 < cubes.size() & cubeId - 1 >= 0) {
            ICube tarCube = cubes.get(cubeId - 1);

            if (button == 0) {
                ICube newCube = new Cube();
                newCube.getCubeColour().setRed(tarCube.getCubeColour().getRed());
                newCube.getCubeColour().setGreen(tarCube.getCubeColour().getGreen());
                newCube.getCubeColour().setBlue(tarCube.getCubeColour().getBlue());
                ForgeDirection dir = getDirectionForCubeFace(cubeFace);
                newCube.setX((byte) (tarCube.getX() + dir.offsetX));
                newCube.setY((byte) (tarCube.getY() + dir.offsetY));
                newCube.setZ((byte) (tarCube.getZ() + dir.offsetZ));
                newCube.setId((byte) 0);
                ModLogger.log("sending cube: " + newCube);
                
                MessageClientGuiMiniArmourerAddOrEdit message;
                message = new MessageClientGuiMiniArmourerAddOrEdit(currentSkinPartType, newCube);
                PacketHandler.networkWrapper.sendToServer(message);
            }
            
            if (button == 1) {
                MessageClientGuiMiniArmourerRemove message;
                message = new MessageClientGuiMiniArmourerRemove(currentSkinPartType, tarCube.getX(), tarCube.getY(), tarCube.getZ());
                PacketHandler.networkWrapper.sendToServer(message);
            }
            
            ModLogger.log("cubeId:" + cubeId + " cubeFace:" + cubeFace); 
        }
    }
    
    private void drawBuildingCubes(boolean fake) {
        if (cubes == null) {
            return;
        }
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
                ICubeColour colour = new CubeColour();
                if (fake) {
                    colour.setColour(getColourFromId(colourId).getRGB(), 0);
                    colour.setColour(getColourFromId(colourId + 1).getRGB(), 1);
                    colour.setColour(getColourFromId(colourId + 2).getRGB(), 2);
                    colour.setColour(getColourFromId(colourId + 3).getRGB(), 3);
                    colour.setColour(getColourFromId(colourId + 4).getRGB(), 4);
                    colour.setColour(getColourFromId(colourId + 5).getRGB(), 5);
                } else {
                    colour = cube.getCubeColour();
                }
                
                EquipmentPartRenderer.INSTANCE.renderArmourBlock(cube.getX(), cube.getY(), cube.getZ(), colour, scale, null, false);
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
            dir = ForgeDirection.EAST;
            break;
        case 0:
            dir = ForgeDirection.WEST;
            break;
        case 4:
            dir = ForgeDirection.DOWN;
            break;
        case 5:
            dir = ForgeDirection.UP;
            break;
        case 3:
            dir = ForgeDirection.NORTH;
            break;
        case 2:
            dir = ForgeDirection.SOUTH;
            break;
        default:
            dir = ForgeDirection.UNKNOWN;
            break;
        }
        return dir;
    }
}
