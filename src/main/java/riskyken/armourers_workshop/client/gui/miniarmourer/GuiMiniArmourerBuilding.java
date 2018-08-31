package riskyken.armourers_workshop.client.gui.miniarmourer;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.skin.type.ISkinPartType;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.client.gui.controls.GuiDropDownList;
import riskyken.armourers_workshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import riskyken.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourers_workshop.client.render.ModRenderHelper;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.items.ModItems;
import riskyken.armourers_workshop.common.lib.LibModInfo;
import riskyken.armourers_workshop.common.network.PacketHandler;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import riskyken.armourers_workshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourers_workshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourers_workshop.utils.UtilColour;
import riskyken.armourers_workshop.utils.UtilColour.ColourFamily;

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
        int skinCount = 0;
        for (int i = 0; i < skinTypes.size(); i++) {
            ISkinType skinType = skinTypes.get(i);
            if (skinType != SkinTypeRegistry.skinSkirt) {
                String skinLocalizedName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                dropDownSkins.addListItem(skinLocalizedName, skinRegistryName, skinType.enabled());
                if (skinType == tileEntity.getSkinType()) {
                    dropDownSkins.setListSelectedIndex(skinCount);
                    updatePartsDropDown(skinType);
                }
                skinCount++;
            }
        }
        
        buttonList.add(dropDownSkins);
        buttonList.add(dropDownParts);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.player.closeScreen();
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
        
        String guiName = tileEntity.getName();
        String localizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + ".name";
        localizedName = I18n.format(localizedName);
        
        drawTextCentered(localizedName, this.width / 2, 2, UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT));
        drawTextCentered("WARNING - This block is unfinished.", this.width / 2, 12, 0xFF0000);
        drawTextCentered("!!! Do not use !!!", this.width / 2, 22, 0xFF0000);
        drawTextCentered(guiSizeLabel, this.width / 2, this.height - 10, UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT));
        drawTextCentered(zoomLabel, this.width / 2, this.height - 20, UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT));
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
        int stringWidth = fontRenderer.getStringWidth(text);
        fontRenderer.drawString(text, x - (stringWidth / 2), y, colour);
    }
    
    private void renderItemInGUI(ItemStack stack, int x, int y) {
        //itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, stack, x, y);
    }
    
    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) throws IOException {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    @Override
    protected void keyTyped(char key, int keyCode) throws IOException {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.player.closeScreen();
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
