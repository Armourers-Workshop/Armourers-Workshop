package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiniArmourerBuilding extends GuiScreen implements IDropDownListCallback {
    
    public TileEntityMiniArmourer tileEntity;
    
    private GuiMiniArmourerBuildingModel model;
    private GuiDropDownList dropDownSkins;
    private GuiDropDownList dropDownParts;
    
    public GuiMiniArmourerBuilding(TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
        model = new GuiMiniArmourerBuildingModel(this, Minecraft.getMinecraft(), tileEntity);
        model.currentSkinType = tileEntity.getSkinType();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonList.add(new GuiButtonExt(0, this.width - 60, this.height - 18, 60, 18, "Exit"));
        buttonList.add(new GuiButtonExt(1, 0, this.height - 18, 60, 18, "Cookies"));
        
        dropDownParts = new GuiDropDownList(3, 84, 2, 80, "", this);
        
        dropDownSkins = new GuiDropDownList(2, 2, 2, 80, "", this);
        ArrayList<ISkinType> skinTypes = SkinTypeRegistry.INSTANCE.getRegisteredSkinTypes();
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            String skinLocalizedName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType);
            String skinRegistryName = skinType.getRegistryName();
            dropDownSkins.addListItem(skinLocalizedName, skinRegistryName, true);
            if (skinType == tileEntity.getSkinType()) {
                dropDownSkins.setListSelectedIndex(i);
                updatePartsDropDown(skinType);
            }
        }
        
        buttonList.add(dropDownSkins);
        buttonList.add(dropDownParts);
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
                model.zoom -= 10F;
            } else if (dWheel > 0) {
                model.zoom += 10F;
            }
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        model.currentSkinType = tileEntity.getSkinType();
        model.stack = tileEntity.getStackInSlot(0);
        this.drawRect(0, 0, this.width, this.height, 0xFF000000);
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableStandardItemLighting();
        
        model.drawScreen(mouseX, mouseY);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        ModRenderHelper.disableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        
        super.drawScreen(mouseX, mouseY, p_73863_3_);
        
        renderToolButtons();
        renderCubeButtons();
        
        String guiSizeLabel = "Gui Size: " + this.width  + " * " + this.height;
        String zoomLabel = "Zoom: " + model.zoom;
        
        String guiName = tileEntity.getInventoryName();
        String localizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        
        drawTextCentered(localizedName, this.width / 2, 2, UtilColour.getMinecraftColor(0));
        drawTextCentered(guiSizeLabel, this.width / 2, this.height - 10, UtilColour.getMinecraftColor(0));
        drawTextCentered(zoomLabel, this.width / 2, this.height - 20, UtilColour.getMinecraftColor(0));
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
        if (dropDownList == dropDownSkins) {
            DropDownListItem listItem = dropDownList.getListSelectedItem();
            ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(listItem.tag);
            updatePartsDropDown(skinType);
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinType(skinType));
        }
        if (dropDownList == dropDownParts) {
            String partName = dropDownParts.getListSelectedItem().tag;
            ISkinPartType skinPartType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
            model.currentSkinPartType = skinPartType;
        }
    }
    
    private void updatePartsDropDown(ISkinType skinType) {
        ArrayList<ISkinPartType> partsList = skinType.getSkinParts();
        dropDownParts.clearList();
        for (int i = 0; i < partsList.size(); i++) {
            ISkinPartType skinPartType = partsList.get(i);
            String skinLocalizedName = SkinTypeRegistry.INSTANCE.getLocalizedSkinPartTypeName(skinPartType);
            String skinRegistryName = skinPartType.getRegistryName();
            dropDownParts.addListItem(skinLocalizedName, skinRegistryName, true);
        }
        dropDownParts.setListSelectedIndex(0);
        String partName = dropDownParts.getListSelectedItem().tag;
        ISkinPartType skinPartType = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(partName);
        model.currentSkinPartType = skinPartType;
    }
}
