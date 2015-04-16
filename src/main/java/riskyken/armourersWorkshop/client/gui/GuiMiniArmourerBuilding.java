package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.client.LightingHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.client.model.armourer.ModelFeet;
import riskyken.armourersWorkshop.client.model.armourer.ModelHand;
import riskyken.armourersWorkshop.client.model.armourer.ModelHead;
import riskyken.armourersWorkshop.client.model.armourer.ModelLegs;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMiniArmourer;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMiniArmourerBuilding extends GuiScreen implements IDropDownListCallback {
    
    private TileEntityMiniArmourer tileEntity;
    
    private static final ModelHead modelHead = new ModelHead();
    private static final ModelChest modelChest = new ModelChest();
    private static final ModelLegs modelLegs = new ModelLegs();
    private static final ModelFeet modelFeet = new ModelFeet();
    private static final ModelHand modelHand = new ModelHand();
    
    public GuiMiniArmourerBuilding(TileEntityMiniArmourer tileEntity) {
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        buttonList.add(new GuiButtonExt(0, this.width - 60, this.height - 18, 60, 18, "Exit"));
        buttonList.add(new GuiButtonExt(1, 0, this.height - 18, 60, 18, "Cookies"));
        
        GuiDropDownList dropDownList = new GuiDropDownList(2, 2, 2, 80, "", this);
        for (int i = 1; i < EnumEquipmentType.values().length - 1; i++) {
            dropDownList.addListItem(getLocalizedEquipmentName(EnumEquipmentType.getOrdinal(i)));
        }
        dropDownList.setListSelectedIndex(tileEntity.getEquipmentType().ordinal() - 1);
        
        buttonList.add(dropDownList);
    }
    
    private String getLocalizedEquipmentName(EnumEquipmentType equipmentType) {
        String localizedName = "armourTypes." + LibModInfo.ID.toLowerCase() + ":" + equipmentType.name().toLowerCase() + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        return localizedName;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        this.drawDefaultBackground();
        //this.drawBackground(0);
        super.drawScreen(mouseX, mouseY, p_73863_3_);
        
        String guiSize = "Gui Size: " + this.width  + " * " + this.height;
        
        String guiName = tileEntity.getInventoryName();
        String localizedName = "inventory." + LibModInfo.ID.toLowerCase() + ":" + guiName + ".name";
        localizedName = StatCollector.translateToLocal(localizedName);
        
        drawTextCentered(localizedName, this.width / 2, 2, UtilColour.getMinecraftColor(0));
        drawTextCentered(guiSize, this.width / 2, this.height - 10, UtilColour.getMinecraftColor(0));

        drawRect(this.width - 18, 2, this.width - 2, 18, -2130706433);
        
        LightingHelper.disableLighting();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        ItemStack[] tools = {
                new ItemStack(ModItems.paintbrush, 1),
                new ItemStack(ModItems.paintRoller, 1),
                new ItemStack(ModItems.burnTool, 1),
                new ItemStack(ModItems.dodgeTool, 1),
                new ItemStack(ModItems.colourPicker, 1),
                new ItemStack(ModItems.colourNoiseTool, 1),
                new ItemStack(ModItems.shadeNoiseTool, 1)
        };
        
        ItemStack[] buildingBlocks = {
                new ItemStack(ModBlocks.colourable, 1),
                new ItemStack(ModBlocks.colourableGlass, 1),
                new ItemStack(ModBlocks.colourableGlowing, 1),
                new ItemStack(ModBlocks.colourableGlassGlowing, 1)
        };
        
        for (int i = 0; i < tools.length; i++) {
            renderItemInGUI(tools[i], this.width - 18, 2 + 18 * i);
        }
        for (int i = 0; i < buildingBlocks.length; i++) {
            renderItemInGUI(buildingBlocks[i], this.width - 36, 2 + 18 * i);
        }
        
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2, this.height / 2, 100.0F);
        GL11.glScalef((float)(-100), (float)100, (float)100);
        float rotation = (float)((double)System.currentTimeMillis() / 40 % 360);
        GL11.glRotatef(rotation, 0F, 1F, 0F);
        //GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableStandardItemLighting();
        
        mc.renderEngine.bindTexture(mc.thePlayer.getLocationSkin());
        
        EnumEquipmentType type = tileEntity.getEquipmentType();
        float scale = 0.0625F;
        switch (type) {
        case NONE:
            break;
        case HEAD:
            GL11.glTranslated(0, -11 * scale, 0);
            modelHead.render(true);
            GL11.glTranslated(0, 11 * scale, 0);
            break;
        case CHEST:
            modelChest.renderChest();
            GL11.glTranslated(scale * 11, 0, 0);
            modelChest.renderLeftArm();
            GL11.glTranslated(scale * -22, 0, 0);
            modelChest.renderRightArm();
            break;
        case LEGS:
            GL11.glTranslated(scale * 6, 0, 0);
            modelLegs.renderLeftLeft();
            GL11.glTranslated(scale * -12, 0, 0);
            modelLegs.renderRightLeg();
            break;
        case SKIRT:
            GL11.glTranslated(scale * 2, 0, 0);
            modelLegs.renderLeftLeft();
            GL11.glTranslated(scale * -4, 0, 0);
            modelLegs.renderRightLeg();
            break;
        case FEET:
            GL11.glTranslated(scale * 6, 0, 0);
            modelFeet.renderLeftLeft();
            GL11.glTranslated(scale * -12, 0, 0);
            modelFeet.renderRightLeg();
            break;
        case SWORD:
            modelHand.render();
            break;
        case BOW:
            modelHand.render();
            break;
        }
        GL11.glPopMatrix();
    }
    
    private void drawTextCentered(String text, int x, int y, int colour) {
        int stringWidth = fontRendererObj.getStringWidth(text);
        fontRendererObj.drawString(text, x - (stringWidth / 2), y, colour);
    }
    
    private void renderItemInGUI(ItemStack stack, int x, int y) {
        itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, stack, x, y);
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
