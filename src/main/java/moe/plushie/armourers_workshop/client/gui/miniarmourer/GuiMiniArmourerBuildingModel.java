package moe.plushie.armourers_workshop.client.gui.miniarmourer;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IRectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.model.bake.FaceRenderer;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.RenderBridge;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.data.MiniCube;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMiniArmourer;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiniArmourerBuildingModel {

    private final Minecraft mc;
    private final GuiScreen parent;
    private TileEntityMiniArmourer tileEntity;
    
    private ArrayList<MiniCube> cubes;
    ArrayList<MiniCube> renderCubes = new ArrayList<MiniCube>();
    
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
    
    private int fakeCubeRenders = 0;
    private int hoverCubeId = 0;
    
    public ISkinType currentSkinType;
    public ISkinPartType currentSkinPartType;
    public ItemStack stack;
    public SkinDescriptor skinPointer;
    
    public GuiMiniArmourerBuildingModel(GuiScreen parent, Minecraft mc, TileEntityMiniArmourer tileEntity) {
        this.parent = parent;
        this.mc = mc;
        this.tileEntity = tileEntity;
        
        this.rotation = 45;
        this.pitch = 45;
        this.cubes = new ArrayList<MiniCube>();
    }
    
    public void drawScreen(int mouseX, int mouseY) {
        if (stack != null) {
            skinPointer = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        }
        renderFakeCubes(mouseX, mouseY);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GuiScreen.drawRect(0, 0, parent.width, parent.height, 0xFF000000);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        renderModels(mouseX, mouseY);
        
        lastMousePosX = mouseX;
        lastMousePosY = mouseY;
    }
    
    private void renderFakeCubes(int mouseX, int mouseY) {
        ArrayList<SkinPart> skinParts = tileEntity.getSkinParts();
        for (int i = 0; i < skinParts.size(); i++) {
            /*
            if (skinParts.get(i).getPartType() == currentSkinPartType) {
                cubes = skinParts.get(i).getArmourData();
            }
            */
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
        Color c = GuiMiniArmourerHelper.getColourAtPos(Mouse.getX(), Mouse.getY());
        hoverCubeId = GuiMiniArmourerHelper.getIdFromColour(c);
        
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
        GL11.glPopMatrix();
    }
    
    private void renderModels(int mouseX, int mouseY) {
        
        float scale = 0.0625F;
        GL11.glPushMatrix();
        
        GL11.glTranslatef(parent.width / 2, parent.height / 2, 500.0F);
        GL11.glScalef((float)(-zoom), (float)zoom, (float)zoom);
        
        GL11.glRotatef(180F, 0F, 1F, 0F);
        
        
        GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        
        
        GL11.glTranslated(0, 1 * scale, 0);
        
        GL11.glScalef(-1, -1, 1);
        
        //Are we hovering over a cube?

        if (hoverCubeId != 0 && renderCubes != null) {
            int cubeId = (int) Math.ceil((double)hoverCubeId / 6);
            int cubeFace = cubeId * 6 - hoverCubeId;
            
            if (cubeId - 1 < renderCubes.size() & cubeId - 1 >= 0) {
                MiniCube tarCube = renderCubes.get(cubeId - 1);
                
                MiniCube newCube = new MiniCube(CubeRegistry.INSTANCE.getCubeFormId((byte) 0));
                EnumFacing dir = GuiMiniArmourerHelper.getDirectionForCubeFace(cubeFace);
                newCube.setX((byte) (tarCube.getX() + dir.getXOffset()));
                newCube.setY((byte) (tarCube.getY() + dir.getYOffset()));
                newCube.setZ((byte) (tarCube.getZ() + dir.getZOffset()));
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                IRenderBuffer buff = RenderBridge.INSTANCE;
                //buff.startDrawingQuads();
                //renderArmourBlock((byte)newCube.getX(), (byte)newCube.getY(), (byte)newCube.getZ(), newCube.getColour(), scale, true);
                //buff.draw();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
        
        //Gui.drawRect(0, 0, parent.width, parent.height, 0xFF000000);
        
        drawBuildingCubes(false);
        GL11.glScalef(-1, -1, 1);
        
        RenderHelper.enableStandardItemLighting();
        mc.renderEngine.bindTexture(mc.player.getLocationSkin());
        
        if (skinPointer != null) {
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin != null) {
                for (int i = 0; i < skin.getParts().size(); i++) {
                    SkinPart part = skin.getParts().get(i);
                    if (part.getPartType() == currentSkinPartType) {
                        SkinPartRenderer.INSTANCE.renderPart(part, scale, skinPointer.getSkinDye(), null, false);
                    }
                }
            }
        }
        
        
        if (currentSkinPartType != null) {
            GL11.glTranslated(0, -currentSkinPartType.getBuildingSpace().getY() * scale, 0);
            currentSkinPartType.renderBuildingGuide(scale, true, false);
            //SkinRenderHelper.renderBuildingGrid(currentSkinPartType, scale);
        }
        /*
        if (currentSkinType != null) {
            SkinRenderHelper.renderBuildingGuide(currentSkinType, scale, true, false);
            SkinRenderHelper.renderBuildingGrid(currentSkinType, scale);
        }
        */
        GL11.glPopMatrix();
    }
    
    private void renderArmourBlock(byte x, byte y, byte z, ICubeColour colour, float scale, boolean b) {
        for (int i = 0; i < 6; i++) {
            FaceRenderer.renderFace(x, y, z, colour.getRed(i), colour.getGreen(i), colour.getBlue(i), (byte)255, (byte)i, false, (byte)1);
        }
    }

    private void cubeClicked(int cubeId, int cubeFace, int button) {
        if (renderCubes != null && cubeId - 1 < renderCubes.size() & cubeId - 1 >= 0) {
            MiniCube tarCube = renderCubes.get(cubeId - 1);
            
            if (button == 0) {
                MiniCube newCube = new MiniCube(CubeRegistry.INSTANCE.getCubeFormId((byte) 0));
                newCube.setColour(0xFFFFFFFF);
                EnumFacing dir = GuiMiniArmourerHelper.getDirectionForCubeFace(cubeFace);
                newCube.setX((byte) (tarCube.getX() + dir.getXOffset()));
                newCube.setY((byte) (tarCube.getY() + dir.getYOffset()));
                newCube.setZ((byte) (tarCube.getZ() + dir.getZOffset()));
                cubes.add(newCube);
                //newCube.setId((byte) 0);
                
                /*
                MessageClientGuiMiniArmourerCubeEdit message;
                message = new MessageClientGuiMiniArmourerCubeEdit(currentSkinPartType, newCube, false);
                PacketHandler.networkWrapper.sendToServer(message);
                */
                
            }
            
            if (button == 1) {
                for (int i = 0; i < cubes.size(); i++) {
                    MiniCube mc = cubes.get(i);
                    if (mc.getX() == tarCube.getX()) {
                        if (mc.getY() == tarCube.getY()) {
                            if (mc.getZ() == tarCube.getZ()) {
                                cubes.remove(i);
                                return;
                            }
                        }
                    }
                }
                
                
                /*
                if (tarCube.getId() == -1) {
                    return;
                }
                */
                
                /*
                MessageClientGuiMiniArmourerCubeEdit message;
                message = new MessageClientGuiMiniArmourerCubeEdit(currentSkinPartType, tarCube, true);
                PacketHandler.networkWrapper.sendToServer(message);
                */
            }
        }
    }
    
    private void drawBuildingCubes(boolean fake) {
        if (cubes == null) {
            //return;
        }
        
        renderCubes.clear();
        fakeCubeRenders = 0;
        //GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        if (fake) {
            GL11.glDisable(GL11.GL_LIGHTING);
            IRectangle3D guideSpace = currentSkinPartType.getGuideSpace();
            for (int ix = 0; ix < guideSpace.getWidth(); ix++ ) {
                for (int iy = 0; iy < guideSpace.getHeight(); iy++ ) {
                    for (int iz = 0; iz < guideSpace.getDepth(); iz++ ) {
                        byte x = (byte) (ix + guideSpace.getX());
                        byte y = (byte) (iy + guideSpace.getY());
                        byte z = (byte) (iz + guideSpace.getZ());
                        MiniCube cube = new MiniCube(CubeRegistry.INSTANCE.getCubeFormId((byte) 0));
                        cube.setX(x);
                        cube.setY(y);
                        cube.setZ(z);
                        renderCubes.add(cube);
                    }
                }
            }
            fakeCubeRenders = renderCubes.size();
        }

        float scale = 0.0625F;
        int colourId = 1;
        
        renderCubes.addAll(cubes);
        IRenderBuffer buff = RenderBridge.INSTANCE;
        /*
        buff.startDrawingQuads();
        for (int i = 0; i < renderCubes.size(); i++) {
            MiniCube cube = renderCubes.get(i);
            if (cube != null) {
                if (cube.isGlowing() & !fake) {
                    GL11.glDisable(GL11.GL_LIGHTING);
                    ModRenderHelper.disableLighting();
                }
                ICubeColour colour = new CubeColour();
                if (fake) {
                    colour.setColour(GuiMiniArmourerHelper.getColourFromId(colourId).getRGB(), 0);
                    colour.setColour(GuiMiniArmourerHelper.getColourFromId(colourId + 1).getRGB(), 1);
                    colour.setColour(GuiMiniArmourerHelper.getColourFromId(colourId + 2).getRGB(), 2);
                    colour.setColour(GuiMiniArmourerHelper.getColourFromId(colourId + 3).getRGB(), 3);
                    colour.setColour(GuiMiniArmourerHelper.getColourFromId(colourId + 4).getRGB(), 4);
                    colour.setColour(GuiMiniArmourerHelper.getColourFromId(colourId + 5).getRGB(), 5);
                } else {
                    colour = cube.getCubeColour();
                }
                
                renderArmourBlock((byte)cube.getX(), (byte)cube.getY(), (byte)cube.getZ(), colour, scale, false);
                
                if (cube.isGlowing() & !fake) {
                    ModRenderHelper.enableLighting();
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
            }
            colourId += 6;
        }
        buff.draw();
        */
        if (fake) {
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
