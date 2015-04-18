package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.LightingHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.client.model.armourer.ModelFeet;
import riskyken.armourersWorkshop.client.model.armourer.ModelHand;
import riskyken.armourersWorkshop.client.model.armourer.ModelHead;
import riskyken.armourersWorkshop.client.model.armourer.ModelLegs;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.equipment.cubes.Cube;
import riskyken.armourersWorkshop.common.equipment.cubes.ICube;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiniArmourerBuilding extends GuiScreen implements IDropDownListCallback {
    
    private static final ModelHead modelHead = new ModelHead();
    private static final ModelChest modelChest = new ModelChest();
    private static final ModelLegs modelLegs = new ModelLegs();
    private static final ModelFeet modelFeet = new ModelFeet();
    private static final ModelHand modelHand = new ModelHand();
    
    private TileEntityMiniArmourer tileEntity;
    private ArrayList<ICube> cubes;
    
    private boolean mouseLeftIsDown = false;
    private boolean mouseRightIsDown = false;
    private boolean mouseCenterIsDown = false;
    
    private int mouseRightDownPosX = 0;
    private int mouseRightDownPosY = 0;
    private int lastMousePosX = 0;
    private int lastMousePosY = 0;
    
    private int mouseLeftDownId = -1;
    private int mouseRightDownId = -1;
    
    private float zoom = 150.0F;
    private float rotation = 0F;
    private float pitch = 0F;
    
    public GuiMiniArmourerBuilding(TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
        this.cubes = new ArrayList<ICube>();
        ICube cube = new Cube();
        cube.setColour(UtilColour.getMinecraftColor(1));
        this.cubes.add(cube);
        
        cube = new Cube();
        cube.setColour(UtilColour.getMinecraftColor(2));
        cube.setX((byte) 10);
        this.cubes.add(cube);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonList.add(new GuiButtonExt(0, this.width - 60, this.height - 18, 60, 18, "Exit"));
        buttonList.add(new GuiButtonExt(1, 0, this.height - 18, 60, 18, "Cookies"));
        
        GuiDropDownList dropDownList = new GuiDropDownList(2, 2, 2, 80, "", this);
        
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkins();
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            dropDownList.addListItem(SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType));
            if (skinType == tileEntity.getSkinType()) {
                dropDownList.setListSelectedIndex(i);
            }
        }
        
        buttonList.add(dropDownList);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        if (Mouse.isCreated()) {
            int dWheel = Mouse.getDWheel();
            if (dWheel < 0) {
                zoom -= 10F;
            } else if (dWheel > 0) {
                zoom += 10F;
            }
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
                    LightingHelper.disableLighting();
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
                
                EquipmentPartRenderer.INSTANCE.renderArmourBlock(cube.getX(), cube.getY(), cube.getZ(), colour, scale, null, false);
                if (cube.isGlowing() & !fake) {
                    LightingHelper.enableLighting();
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
    
    private int getIdFromColour(Color colour) {
        Color c = new Color(colour.getRGB());
        //ModLogger.log(c);
        int id = c.getRed();
        id += c.getGreen() * 256;
        id += c.getBlue() * 256 * 256;
        return id;
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
        //ModLogger.log(id + " r" + r + " b" + b + " g" + g);
        return new Color(r, g, b);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        this.drawRect(0, 0, this.width, this.height, 0xFF000000);
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableStandardItemLighting();
        
        renderModels(mouseX, mouseY);
        
        LightingHelper.disableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        
        super.drawScreen(mouseX, mouseY, p_73863_3_);
        
        renderToolButtons();
        renderCubeButtons();
        
        String guiSizeLabel = "Gui Size: " + this.width  + " * " + this.height;
        String zoomLabel = "Zoom: " + this.zoom;
        
        String guiName = tileEntity.getInventoryName();
        String localizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        
        drawTextCentered(localizedName, this.width / 2, 2, UtilColour.getMinecraftColor(0));
        drawTextCentered(guiSizeLabel, this.width / 2, this.height - 10, UtilColour.getMinecraftColor(0));
        drawTextCentered(zoomLabel, this.width / 2, this.height - 20, UtilColour.getMinecraftColor(0));

        lastMousePosX = mouseX;
        lastMousePosY = mouseY;
    }
    
    private void renderModels(int mouseX, int mouseY) {
        GL11.glPushMatrix();
        
        GL11.glTranslatef(this.width / 2, this.height - 50, 500.0F);
        GL11.glScalef((float)(-zoom), (float)zoom, (float)zoom);
        
        GL11.glRotatef(180F, 0F, 1F, 0F);
        
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
        
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
                float scale = 0.0625F;
                int[] colour = {newCube.getColour(), newCube.getColour(), newCube.getColour(), newCube.getColour(), newCube.getColour(), newCube.getColour()};
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                EquipmentPartRenderer.INSTANCE.renderArmourBlock(newCube.getX(), newCube.getY(), newCube.getZ(), colour, scale, null, true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
        
        RenderHelper.enableStandardItemLighting();
        
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());
        
        ISkinType skinType = tileEntity.getSkinType();
        float scale = 0.0625F;
        if (skinType != null) {
            skinType.renderBuildingGuide(scale, true, false);
            
            skinType.renderBuildingGrid(scale);
        }
        
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
                cubes.add(newCube);
            }
            
            if (button == 1) {
                cubes.remove(cubeId - 1);
            }
            
            ModLogger.log("cubeId:" + cubeId + " cubeFace:" + cubeFace); 
        }
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
    
    private void renderToolButtons() {
        drawRect(this.width - 18, 2, this.width - 2, 18, -2130706433);
        ItemStack[] tools = {
                new ItemStack(ModItems.paintbrush, 1),
                new ItemStack(ModItems.paintRoller, 1),
                new ItemStack(ModItems.burnTool, 1),
                new ItemStack(ModItems.dodgeTool, 1),
                new ItemStack(ModItems.colourPicker, 1),
                new ItemStack(ModItems.colourNoiseTool, 1),
                new ItemStack(ModItems.shadeNoiseTool, 1)
        };
        
        for (int i = 0; i < tools.length; i++) {
            renderItemInGUI(tools[i], this.width - 18, 2 + 18 * i);
        }
    }
    
    private void renderCubeButtons() {
        ItemStack[] buildingBlocks = {
                new ItemStack(ModBlocks.colourable, 1),
                new ItemStack(ModBlocks.colourableGlass, 1),
                new ItemStack(ModBlocks.colourableGlowing, 1),
                new ItemStack(ModBlocks.colourableGlassGlowing, 1)
        };
        
        for (int i = 0; i < buildingBlocks.length; i++) {
            renderItemInGUI(buildingBlocks[i], this.width - 36, 2 + 18 * i);
        }
    }
    
    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r,g,b);
    }
    
    private void drawTextCentered(String text, int x, int y, int colour) {
        int stringWidth = fontRendererObj.getStringWidth(text);
        fontRendererObj.drawString(text, x - (stringWidth / 2), y, colour);
    }
    
    private void renderItemInGUI(ItemStack stack, int x, int y) {
        itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y);
    }
    
    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    @Override
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) dropDownList.getListSelectedIndex()));
    }
}
