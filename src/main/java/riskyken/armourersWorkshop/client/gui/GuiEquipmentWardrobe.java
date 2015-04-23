package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;
import java.util.BitSet;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiFileListItem;
import riskyken.armourersWorkshop.client.gui.controls.GuiList;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.inventory.ContainerEquipmentWardrobe;
import riskyken.armourersWorkshop.common.inventory.SlotHidable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiUpdateNakedInfo;
import riskyken.armourersWorkshop.common.skin.EntityNakedInfo;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEquipmentWardrobe extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/customArmourInventory.png");

    private static int activeTab = 0;
    private static final int TAB_MAIN = 0;
    private static final int TAB_SKIN = 1;
    
    Color skinColour;
    Color hairColour;
    Color pantsColour;
    Color pantStripeColour;
    BitSet armourOverride;
    boolean headOverlay;
    boolean limitLimbs;

    ExPropsPlayerEquipmentData customEquipmentData;
    PlayerSkinInfo skinInfo;
    EntityNakedInfo nakedInfo;
    EntityPlayer player;
    
    private GuiList skinList;
    private GuiButtonExt autoButton;

    private GuiCheckBox[] armourOverrideCheck;
    private GuiCheckBox[] overlayOverrideCheck;
    private GuiCheckBox limitLimbsCheck;

    private float mouseX;
    private float mouseY;

    public GuiEquipmentWardrobe(InventoryPlayer inventory, ExPropsPlayerEquipmentData customEquipmentData) {
        super(new ContainerEquipmentWardrobe(inventory, customEquipmentData));
        
        this.customEquipmentData = customEquipmentData;
        this.player = inventory.player;
        
        skinInfo = ArmourersWorkshop.proxy.getPlayersNakedData(this.player.getUniqueID());
        
        if (skinInfo == null) {
            skinInfo = new PlayerSkinInfo(new EntityNakedInfo());
            ModLogger.log(Level.ERROR,"Unable to get skin info for player: " + this.player.getDisplayName());
        }
        
        if (skinInfo != null) {
            nakedInfo = skinInfo.getNakedInfo();
            this.skinColour = new Color(nakedInfo.skinColour);
            this.hairColour = new Color(nakedInfo.hairColour);
            this.pantsColour = new Color(nakedInfo.pantsColour);
            this.pantStripeColour = new Color(nakedInfo.pantStripeColour);
            this.armourOverride = nakedInfo.armourOverride;
            this.headOverlay = nakedInfo.headOverlay;
            this.limitLimbs = nakedInfo.limitLimbs;
        }
        
        this.xSize = 256;
        this.ySize = 256;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        String guiName = "equipmentWardrobe";
        
        autoButton = new GuiButtonExt(0, this.guiLeft + 27, this.guiTop + 116, 80, 18, GuiHelper.getLocalizedControlName(guiName, "autoColour"));
        skinList = new GuiList(this.guiLeft + 165, this.guiTop + 30, 80, 96, 12);
        skinList.clearList();
        skinList.addListItem(new GuiFileListItem("Default", "", true));
        skinList.addListItem(new GuiFileListItem("None", "", true));
        skinList.setSelectedIndex(0);
        if (skinInfo.getNakedInfo().isNaked) {
            skinList.setSelectedIndex(1);
        }
        
        armourOverrideCheck = new GuiCheckBox[4];
        armourOverrideCheck[0] = new GuiCheckBox(2, this.guiLeft + 110, this.guiTop + 17, "Head armour render?", !armourOverride.get(0));
        armourOverrideCheck[1] = new GuiCheckBox(3, this.guiLeft + 110, this.guiTop + 37, "Chest armour render?", !armourOverride.get(1));
        armourOverrideCheck[2] = new GuiCheckBox(4, this.guiLeft + 110, this.guiTop + 75, "Leg armour render?", !armourOverride.get(2));
        armourOverrideCheck[3] = new GuiCheckBox(5, this.guiLeft + 110, this.guiTop + 94, "Foot armour render?", !armourOverride.get(3));
        
        overlayOverrideCheck = new GuiCheckBox[1];
        overlayOverrideCheck[0] = new GuiCheckBox(6, this.guiLeft + 110, this.guiTop + 26, "Head overlay render?", !headOverlay);
        
        limitLimbsCheck = new GuiCheckBox(7, this.guiLeft + 110, this.guiTop + 56, "Limit limb movement?", limitLimbs);
        
        buttonList.add(overlayOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[1]);
        buttonList.add(armourOverrideCheck[2]);
        buttonList.add(armourOverrideCheck[3]);
        buttonList.add(limitLimbsCheck);
        
        buttonList.add(autoButton);
        
        setActiveTab(activeTab);
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (skinList.mouseClicked(mouseX, mouseY, button)) {
            String skinName = skinList.getSelectedListEntry().getDisplayName();
            nakedInfo.isNaked = skinList.getSelectedIndex() == 1;
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(nakedInfo));
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        
        skinList.mouseMovedOrUp(mouseX, mouseY, button);
        
        if (button == 0) {
            int tabXPos = this.guiLeft;
            int tabYPos = this.guiTop + 12;
            int tabImageWidth = 23;
            int tabImageHeight = 20;
            
            if (mouseX >= tabXPos & mouseX <= tabXPos + tabImageWidth) {
                if (mouseY >= tabYPos & mouseY <= tabYPos + tabImageHeight) {
                    setActiveTab(TAB_MAIN);
                }
            }
            
            tabYPos += 21;
            
            if (mouseX >= tabXPos & mouseX <= tabXPos + tabImageWidth) {
                if (mouseY >= tabYPos & mouseY <= tabYPos + tabImageHeight) {
                    setActiveTab(TAB_SKIN);
                }
            }
        }
    }
    
    private void setActiveTab(int tabNumber) {
        this.activeTab = tabNumber;
        for (int i = 0; i < 6; i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            button.visible = tabNumber == TAB_MAIN;
        }
        for (int i = 6; i < 7; i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            button.visible = tabNumber == TAB_SKIN;
        }

        for (int i = 0; i < 7; i++) {
            SlotHidable slot = (SlotHidable) inventorySlots.inventorySlots.get(i);
            slot.setVisible(tabNumber == TAB_MAIN);
        }
        for (int i = 7; i < 9; i++) {
            SlotHidable slot = (SlotHidable) inventorySlots.inventorySlots.get(i);
            slot.setVisible(tabNumber == TAB_SKIN);
        }
        
        skinList.visible = tabNumber == TAB_SKIN;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
    	if (button instanceof GuiCheckBox) {
    		headOverlay = !overlayOverrideCheck[0].isChecked();
    		for (int i = 0; i < 4; i++) {
    			armourOverride.set(i, !armourOverrideCheck[i].isChecked());
    		}
    	}
    	
        if (button.id >= 1) {
            nakedInfo.headOverlay = headOverlay;
            nakedInfo.armourOverride = armourOverride;
            nakedInfo.limitLimbs = limitLimbsCheck.isChecked();
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiUpdateNakedInfo(nakedInfo));
        }
        if (button.id == 0) {
            skinInfo.autoColourSkin((AbstractClientPlayer) this.player);
            skinInfo.autoColourHair((AbstractClientPlayer) this.player);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //Title label.
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, "equipmentWardrobe");
        
        if (activeTab == TAB_SKIN) {
            String labelSkinColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinColour");
            this.fontRendererObj.drawString(labelSkinColour + ":", 90, 18, 4210752); 
            
            String labelSkinOverride = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinOverride");
            this.fontRendererObj.drawString(labelSkinOverride + ":", 165, 18, 4210752); 
            
            String labelHairColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.hairColour");
            this.fontRendererObj.drawString(labelHairColour + ":", 90, 70, 4210752); 
        }
        
        //Player inventory label.
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 54, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        
        //Top half of GUI. (active tab)
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, 167);
        
        //Bottom half of GUI. (player inventory)
        this.drawTexturedModalRect(this.guiLeft + 45, this.guiTop + 167, 45, 167, 178, 89);
        
        //Active tab image
        int tabImageX = 0;
        int tabImageY = 229;
        int tabImageWidth = 23;
        int tabImageHeight = 26;
        int tabXPos = this.guiLeft;
        int tabYPos = this.guiTop + 9;
        tabYPos += activeTab * 21;
        this.drawTexturedModalRect(tabXPos, tabYPos, tabImageX, tabImageY, tabImageWidth, tabImageHeight);
        
        int sloImageSize = 18;
        if (this.activeTab == TAB_MAIN) {
            this.drawTexturedModalRect(this.guiLeft + 87, this.guiTop + 17, 18, 173, sloImageSize, 56);
            this.drawTexturedModalRect(this.guiLeft + 87, this.guiTop + 74, 0, 192, sloImageSize, 37);
            this.drawTexturedModalRect(this.guiLeft + 68, this.guiTop + 112, 0, 173, sloImageSize, sloImageSize);
            this.drawTexturedModalRect(this.guiLeft + 27, this.guiTop + 112, 238, 238, sloImageSize, sloImageSize);
        }
        
        if (this.activeTab == TAB_SKIN) {
            //Skin colour slots
            this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 34, 238, 194, sloImageSize, sloImageSize);
            this.drawTexturedModalRect(this.guiLeft + 126, this.guiTop + 30, 230, 212, 26, 26);
            
            //Hair colour slots
            this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 86, 238, 194, sloImageSize, sloImageSize);
            this.drawTexturedModalRect(this.guiLeft + 126, this.guiTop + 82, 230, 212, 26, 26);
            
            this.skinColour = new Color(nakedInfo.skinColour);
            this.hairColour = new Color(nakedInfo.hairColour);
            this.pantsColour = new Color(nakedInfo.pantsColour);
            this.pantStripeColour = new Color(nakedInfo.pantStripeColour);
            
            float skinR = (float) skinColour.getRed() / 255;
            float skinG = (float) skinColour.getGreen() / 255;
            float skinB = (float) skinColour.getBlue() / 255;
            
            //Skin colour display
            this.drawTexturedModalRect(this.guiLeft + 110, this.guiTop + 36, 242, 180, 14, 14);
            GL11.glColor4f(skinR, skinG, skinB, 1F);
            this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 37, 243, 181, 12, 12);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            float hairR = (float) hairColour.getRed() / 255;
            float hairG = (float) hairColour.getGreen() / 255;
            float hairB = (float) hairColour.getBlue() / 255;
            
            //Hair colour display
            this.drawTexturedModalRect(this.guiLeft + 110, this.guiTop + 88, 242, 180, 14, 14);
            GL11.glColor4f(hairR, hairG, hairB, 1F);
            this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 89, 243, 181, 12, 12);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        
        //Skin list
        skinList.drawList(p_146976_2_, p_146976_3_, 0);

        //3D player preview
        int boxX = this.guiLeft + 57;
        int boxY = this.guiTop + 95;
        float lookX = boxX - this.mouseX;
        float lookY = boxY - 50 - this.mouseY;
        GuiInventory.func_147046_a(boxX, boxY, 35, lookX, lookY, this.mc.thePlayer);
    }
}
