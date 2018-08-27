package riskyken.armourersWorkshop.client.gui.wardrobe.tab;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.wardrobe.GuiWardrobe;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.proxies.ClientProxy;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeColourSettings extends GuiTabPanel {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);
    
    private boolean selectingSkinColour = false;
    private boolean selectingHairColour = false;
    
    private Color skinColour;
    private Color hairColour;
    
    private GuiButtonExt selectSkinButton;
    private GuiButtonExt autoSkinButton;
    private GuiButtonExt selectHairButton;
    private GuiButtonExt autoHairButton;
    
    EntityPlayer entityPlayer;
    ExPropsPlayerSkinData propsPlayerSkinData;
    EquipmentWardrobeData equipmentWardrobeData;
    
    String guiName = "equipmentWardrobe";
    
    public GuiTabWardrobeColourSettings(int tabId, GuiScreen parent, EntityPlayer entityPlayer, ExPropsPlayerSkinData propsPlayerSkinData, EquipmentWardrobeData equipmentWardrobeData) {
        super(tabId, parent, false);
        this.skinColour = new Color(equipmentWardrobeData.skinColour);
        this.hairColour = new Color(equipmentWardrobeData.hairColour);
        this.entityPlayer = entityPlayer;
        this.propsPlayerSkinData = propsPlayerSkinData;
        this.equipmentWardrobeData = equipmentWardrobeData;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        selectSkinButton = new GuiButtonExt(0, 68, 46, 100, 18, GuiHelper.getLocalizedControlName(guiName, "selectSkin"));
        autoSkinButton = new GuiButtonExt(0, 68 + 105, 46, 50, 18, GuiHelper.getLocalizedControlName(guiName, "autoSkin"));
        selectHairButton = new GuiButtonExt(0, 68, 98, 100, 18, GuiHelper.getLocalizedControlName(guiName, "selectHair"));
        autoHairButton = new GuiButtonExt(0, 68 + 105, 98, 50, 18, GuiHelper.getLocalizedControlName(guiName, "autoHair"));
        
        buttonList.add(selectSkinButton);
        buttonList.add(autoSkinButton);
        buttonList.add(selectHairButton);
        buttonList.add(autoHairButton);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
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
    protected void actionPerformed(GuiButton button) {
        
        if (button == selectSkinButton) {
            selectingSkinColour = true;
        }
        
        if (button == selectHairButton) {
            selectingHairColour = true;
        }
        if (button == autoSkinButton) {
            int newSkinColour = equipmentWardrobeData.autoColourSkin((AbstractClientPlayer) this.entityPlayer);
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            ewd.skinColour = newSkinColour;
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        }
        
        if (button == autoHairButton) {
            int newHairColour = equipmentWardrobeData.autoColourHair((AbstractClientPlayer) this.entityPlayer);
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            ewd.hairColour = newHairColour;
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        }
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        
        //Top half of GUI. (active tab)
        this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);
        
        //Bottom half of GUI. (player inventory)
        this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);
        
        PlayerPointer playerPointer = new PlayerPointer(entityPlayer);
        EquipmentWardrobeData newEwd = equipmentWardrobeData = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        if (newEwd != null) {
            equipmentWardrobeData = newEwd;
        }
        
        float skinR = (float) skinColour.getRed() / 255;
        float skinG = (float) skinColour.getGreen() / 255;
        float skinB = (float) skinColour.getBlue() / 255;
        
        // Skin colour display
        this.drawTexturedModalRect(this.x + 68, this.y + 30, 242, 180, 14, 14);
        GL11.glColor4f(skinR, skinG, skinB, 1F);
        this.drawTexturedModalRect(this.x + 69, this.y + 31, 243, 181, 12, 12);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        float hairR = (float) hairColour.getRed() / 255;
        float hairG = (float) hairColour.getGreen() / 255;
        float hairB = (float) hairColour.getBlue() / 255;
        
        // Hair colour display
        this.drawTexturedModalRect(this.x + 68, this.y + 82, 242, 180, 14, 14);
        GL11.glColor4f(hairR, hairG, hairB, 1F);
        this.drawTexturedModalRect(this.x + 69, this.y + 83, 243, 181, 12, 12);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        String labelSkinColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinColour");
        fontRenderer.drawString(labelSkinColour + ":", 70, 18, 4210752); 
        
        //String labelSkinOverride = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinOverride");
        //this.fontRendererObj.drawString(labelSkinOverride + ":", 165, 18, 4210752); 
        
        String labelHairColour = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.hairColour");
        fontRenderer.drawString(labelHairColour + ":", 70, 70, 4210752); 
        
        this.skinColour = new Color(equipmentWardrobeData.skinColour);
        this.hairColour = new Color(equipmentWardrobeData.hairColour);
        
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ModRenderHelper.enableAlphaBlend();
        // Draw player preview.
        if (selectingSkinColour) {
            skinColour = ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY, true);
        } else if (selectingHairColour) {
            hairColour = ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY, true);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ModRenderHelper.enableAlphaBlend();
            ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY, false);
        }
        GL11.glPopMatrix();
    }
}
