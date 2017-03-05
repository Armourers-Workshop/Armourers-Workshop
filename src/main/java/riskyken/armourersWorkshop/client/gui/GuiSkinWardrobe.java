package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.BitSet;

import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinWardrobe;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerEquipmentData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class GuiSkinWardrobe extends GuiContainer {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/customArmourInventory.png");

    private static int activeTab = 0;
    private static final int TAB_MAIN = 0;
    private static final int TAB_OVERRIDE = 1;
    private static final int TAB_SKIN = 2;
    
    Color skinColour;
    Color hairColour;
    BitSet armourOverride;
    boolean headOverlay;
    boolean limitLimbs;

    ExPropsPlayerEquipmentData customEquipmentData;
    EquipmentWardrobeData equipmentWardrobeData;
    EntityPlayer player;
    
    private GuiButtonExt selectSkinButton;
    private GuiButtonExt autoSkinButton;
    private GuiButtonExt selectHairButton;
    private GuiButtonExt autoHairButton;
    
    private boolean selectingSkinColour = false;
    private boolean selectingHairColour = false;
    private boolean rotatingPlayer = false;
    private float playerRotation = 45F;
    
    private GuiCheckBox[] armourOverrideCheck;
    private GuiCheckBox[] overlayOverrideCheck;
    private GuiCheckBox limitLimbsCheck;

    private int lastMouseX;
    private int lastMouseY;

    public GuiSkinWardrobe(InventoryPlayer inventory, ExPropsPlayerEquipmentData customEquipmentData) {
        super(new ContainerSkinWardrobe(inventory, customEquipmentData));
        
        this.customEquipmentData = customEquipmentData;
        this.player = inventory.player;
        
        PlayerPointer playerPointer = new PlayerPointer(player);
        equipmentWardrobeData = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        
        if (equipmentWardrobeData == null) {
            equipmentWardrobeData = new EquipmentWardrobeData();
            ModLogger.log(Level.ERROR,"Unable to get skin info for player: " + this.player.getDisplayName());
        }
        
        if (equipmentWardrobeData != null) {
            this.skinColour = new Color(equipmentWardrobeData.skinColour);
            this.hairColour = new Color(equipmentWardrobeData.hairColour);
            this.armourOverride = equipmentWardrobeData.armourOverride;
            this.headOverlay = equipmentWardrobeData.headOverlay;
            this.limitLimbs = equipmentWardrobeData.limitLimbs;
        }
        
        this.xSize = 256;
        this.ySize = 256;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        String guiName = "equipmentWardrobe";
        
        armourOverrideCheck = new GuiCheckBox[4];
        armourOverrideCheck[0] = new GuiCheckBox(2, this.guiLeft + 88, this.guiTop + 17, GuiHelper.getLocalizedControlName(guiName, "renderHeadArmour"), !armourOverride.get(0));
        armourOverrideCheck[1] = new GuiCheckBox(3, this.guiLeft + 88, this.guiTop + 37, GuiHelper.getLocalizedControlName(guiName, "renderChestArmour"), !armourOverride.get(1));
        armourOverrideCheck[2] = new GuiCheckBox(4, this.guiLeft + 88, this.guiTop + 75, GuiHelper.getLocalizedControlName(guiName, "renderLegArmour"), !armourOverride.get(2));
        armourOverrideCheck[3] = new GuiCheckBox(5, this.guiLeft + 88, this.guiTop + 94, GuiHelper.getLocalizedControlName(guiName, "renderFootArmour"), !armourOverride.get(3));
        
        overlayOverrideCheck = new GuiCheckBox[1];
        overlayOverrideCheck[0] = new GuiCheckBox(6, this.guiLeft + 88, this.guiTop + 26, GuiHelper.getLocalizedControlName(guiName, "renderHeadOverlay"), !headOverlay);
        
        limitLimbsCheck = new GuiCheckBox(7, this.guiLeft + 88, this.guiTop + 56, GuiHelper.getLocalizedControlName(guiName, "limitLimbMovement"), limitLimbs);
        
        buttonList.add(overlayOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[0]);
        buttonList.add(armourOverrideCheck[1]);
        buttonList.add(armourOverrideCheck[2]);
        buttonList.add(armourOverrideCheck[3]);
        buttonList.add(limitLimbsCheck);
        
        selectSkinButton = new GuiButtonExt(0, this.guiLeft + 90, this.guiTop + 46, 100, 18, GuiHelper.getLocalizedControlName(guiName, "selectSkin"));
        autoSkinButton = new GuiButtonExt(0, this.guiLeft + 90 + 105, this.guiTop + 46, 50, 18, GuiHelper.getLocalizedControlName(guiName, "autoSkin"));
        selectHairButton = new GuiButtonExt(0, this.guiLeft + 90, this.guiTop + 98, 100, 18, GuiHelper.getLocalizedControlName(guiName, "selectHair"));
        autoHairButton = new GuiButtonExt(0, this.guiLeft + 90 + 105, this.guiTop + 98, 50, 18, GuiHelper.getLocalizedControlName(guiName, "autoHair"));
        
        buttonList.add(selectSkinButton);
        buttonList.add(autoSkinButton);
        buttonList.add(selectHairButton);
        buttonList.add(autoHairButton);
        
        setActiveTab(activeTab);
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 1) {
            rotatingPlayer = true;
        }
        
        if (button == 0 & selectingSkinColour) {
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            ewd.skinColour = skinColour.getRGB();
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
            selectingSkinColour = false;
        }
        if (button == 0 & selectingHairColour) {
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            ewd.hairColour = hairColour.getRGB();
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
            selectingHairColour = false;
        }
        
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        
        if (button == 1) {
            rotatingPlayer = false;
        }
        
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
                    setActiveTab(TAB_OVERRIDE);
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
            button.visible = tabNumber == TAB_OVERRIDE;
        }
        for (int i = 6; i < 10; i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            button.visible = tabNumber == TAB_SKIN;
        }

        for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(tabNumber == TAB_MAIN);
            }
            //SlotHidable slot = (SlotHidable) inventorySlots.inventorySlots.get(i);
            //slot.setVisible(tabNumber == TAB_MAIN);
        }
        for (int i = 7; i < 9; i++) {
            SlotHidable slot = (SlotHidable) inventorySlots.inventorySlots.get(i);
            //slot.setVisible(tabNumber == TAB_SKIN);
        }
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
            equipmentWardrobeData.headOverlay = headOverlay;
            equipmentWardrobeData.armourOverride = armourOverride;
            equipmentWardrobeData.limitLimbs = limitLimbsCheck.isChecked();
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(equipmentWardrobeData));
        }
        
        if (button == selectSkinButton) {
            selectingSkinColour = true;
        }
        
        if (button == selectHairButton) {
            selectingHairColour = true;
        }
        
        if (button == autoSkinButton) {
            int newSkinColour = equipmentWardrobeData.autoColourSkin((AbstractClientPlayer) this.player);
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            ewd.skinColour = newSkinColour;
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        }
        
        if (button == autoHairButton) {
            int newHairColour = equipmentWardrobeData.autoColourHair((AbstractClientPlayer) this.player);
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            ewd.hairColour = newHairColour;
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        //Title label.
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, "equipmentWardrobe");
        
        if (activeTab == TAB_SKIN) {
            String labelSkinColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinColour");
            this.fontRendererObj.drawString(labelSkinColour + ":", 90, 18, 4210752); 
            
            //String labelSkinOverride = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinOverride");
            //this.fontRendererObj.drawString(labelSkinOverride + ":", 165, 18, 4210752); 
            
            String labelHairColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.hairColour");
            this.fontRendererObj.drawString(labelHairColour + ":", 90, 70, 4210752); 
        }
        
        //Player inventory label.
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 54, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int mouseX, int mouseY) {
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
        
        if (rotatingPlayer) {
            playerRotation += mouseX - lastMouseX;
            if (playerRotation < 0F) {
                playerRotation += 360F;
            }
            if (playerRotation > 360F) {
                playerRotation -= 360F;
            }
        }
        
        if (this.activeTab == TAB_MAIN) {
            for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
                Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
                this.drawTexturedModalRect(this.guiLeft + slot.xDisplayPosition - 1,
                        this.guiTop + slot.yDisplayPosition - 1,
                        238, 194, sloImageSize, sloImageSize);
            }
            //this.drawTexturedModalRect(this.guiLeft + 87, this.guiTop + 17, 18, 173, sloImageSize, 56);
            //this.drawTexturedModalRect(this.guiLeft + 87, this.guiTop + 74, 0, 192, sloImageSize, 37);
            //this.drawTexturedModalRect(this.guiLeft + 68, this.guiTop + 112, 0, 173, sloImageSize, sloImageSize);
            //this.drawTexturedModalRect(this.guiLeft + 27, this.guiTop + 112, 238, 238, sloImageSize, sloImageSize);
        }
        
        if (this.activeTab == TAB_OVERRIDE) {
            
        }
        
        if (this.activeTab == TAB_SKIN) {
            PlayerPointer playerPointer = new PlayerPointer(player);
            EquipmentWardrobeData newEwd = equipmentWardrobeData = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
            if (newEwd != null) {
                equipmentWardrobeData = newEwd;
            }
            //Skin colour slots
            //this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 34, 238, 194, sloImageSize, sloImageSize);
            //this.drawTexturedModalRect(this.guiLeft + 126, this.guiTop + 30, 230, 212, 26, 26);
            
            //Hair colour slots
            //this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 86, 238, 194, sloImageSize, sloImageSize);
            //this.drawTexturedModalRect(this.guiLeft + 126, this.guiTop + 82, 230, 212, 26, 26);
            
            this.skinColour = new Color(equipmentWardrobeData.skinColour);
            this.hairColour = new Color(equipmentWardrobeData.hairColour);
            
            //3D player preview
            int boxX = this.guiLeft + 57;
            int boxY = this.guiTop + 95;
            float lookX = boxX - mouseX;
            float lookY = boxY - 50 - mouseY;
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glTranslatef(boxX, boxY, 50);
            GL11.glRotatef(-20, 1, 0, 0);
            GL11.glRotatef(playerRotation, 0, 1, 0);
            GL11.glTranslatef(0, 0, -50);
            
            renderEntityWithoutLighting(0, 0, 35, 0, 0, this.mc.thePlayer);
            if (selectingSkinColour) {
                skinColour = getColourAtPos(Mouse.getX(), Mouse.getY());
            }
            if (selectingHairColour) {
                hairColour = getColourAtPos(Mouse.getX(), Mouse.getY());
            }
            GuiInventory.func_147046_a(0, 0, 35, 0, 0, this.mc.thePlayer);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            
            float skinR = (float) skinColour.getRed() / 255;
            float skinG = (float) skinColour.getGreen() / 255;
            float skinB = (float) skinColour.getBlue() / 255;
            
            //Skin colour display
            this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 30, 242, 180, 14, 14);
            GL11.glColor4f(skinR, skinG, skinB, 1F);
            this.drawTexturedModalRect(this.guiLeft + 91, this.guiTop + 31, 243, 181, 12, 12);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            float hairR = (float) hairColour.getRed() / 255;
            float hairG = (float) hairColour.getGreen() / 255;
            float hairB = (float) hairColour.getBlue() / 255;
            
            //Hair colour display
            this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 82, 242, 180, 14, 14);
            GL11.glColor4f(hairR, hairG, hairB, 1F);
            this.drawTexturedModalRect(this.guiLeft + 91, this.guiTop + 83, 243, 181, 12, 12);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            //3D player preview
            int boxX = this.guiLeft + 57;
            int boxY = this.guiTop + 95;
            float lookX = boxX - mouseX;
            float lookY = boxY - 50 - mouseY;
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glTranslatef(boxX, boxY, 50);
            GL11.glRotatef(-20, 1, 0, 0);
            GL11.glRotatef(playerRotation, 0, 1, 0);
            GL11.glTranslatef(0, 0, -50);
            GuiInventory.func_147046_a(0, 0, 35, 0, 0, this.mc.thePlayer);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }
    
    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r,g,b);
    }
    
    private void renderEntityWithoutLighting(int xPos, int yPos, int scale, float p_147046_3_, float p_147046_4_, EntityLivingBase entity) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)xPos, (float)yPos, 50.0F);
        GL11.glScalef((float)(-scale), (float)scale, (float)scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.disableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 20.0F;
        entity.rotationYaw = (float)Math.atan((double)(p_147046_3_ / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float)Math.atan((double)(p_147046_4_ / 40.0F))) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
