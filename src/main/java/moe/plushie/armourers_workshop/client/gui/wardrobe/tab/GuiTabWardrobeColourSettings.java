package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.common.SkinHelper;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCapability;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeColourSettings extends GuiTabPanel {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);
    
    private EntityPlayer entityPlayer;
    private IEntitySkinCapability skinCapability;
    private IWardrobeCapability wardrobeCapability;
    
    private boolean selectingSkinColour = false;
    private boolean selectingHairColour = false;
    
    private Color skinColour;
    private Color hairColour;
    
    private GuiButtonExt selectSkinButton;
    private GuiButtonExt autoSkinButton;
    private GuiButtonExt selectHairButton;
    private GuiButtonExt autoHairButton;
    
    String guiName = "equipment-wardrobe";
    
    public GuiTabWardrobeColourSettings(int tabId, GuiScreen parent, EntityPlayer entityPlayer, IEntitySkinCapability skinCapability, IWardrobeCapability wardrobeCapability) {
        super(tabId, parent, false);
        this.entityPlayer = entityPlayer;
        this.skinCapability = skinCapability;
        this.wardrobeCapability = wardrobeCapability;
        this.skinColour = new Color(wardrobeCapability.getSkinColour());
        this.hairColour = new Color(wardrobeCapability.getHairColour());
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
            wardrobeCapability.setSkinColour(skinColour.getRGB());
            wardrobeCapability.sendUpdateToServer();
            selectingSkinColour = false;
        }
        if (button == 0 & selectingHairColour) {
            wardrobeCapability.setHairColour(hairColour.getRGB());
            wardrobeCapability.sendUpdateToServer();
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
            int newSkinColour = autoColourSkin((AbstractClientPlayer) this.entityPlayer);
            wardrobeCapability.setSkinColour(newSkinColour);
            wardrobeCapability.sendUpdateToServer();
        }
        
        if (button == autoHairButton) {
            int newHairColour = autoColourHair((AbstractClientPlayer) this.entityPlayer);
            wardrobeCapability.setHairColour(newHairColour);
            wardrobeCapability.sendUpdateToServer();
        }
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        
        //Top half of GUI. (active tab)
        this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);
        
        //Bottom half of GUI. (player inventory)
        this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);
        
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
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        String labelSkinColour = GuiHelper.getLocalizedControlName(guiName, "label.skinColour");
        fontRenderer.drawString(labelSkinColour + ":", 70, 18, 4210752); 
        
        //String labelSkinOverride = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinOverride");
        //this.fontRendererObj.drawString(labelSkinOverride + ":", 165, 18, 4210752); 
        
        String labelHairColour = GuiHelper.getLocalizedControlName(guiName, "label.hairColour");
        fontRenderer.drawString(labelHairColour + ":", 70, 70, 4210752); 
        
        this.skinColour = new Color(wardrobeCapability.getSkinColour());
        this.hairColour = new Color(wardrobeCapability.getHairColour());
        
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
    
    public int autoColourHair(AbstractClientPlayer player) {
        BufferedImage playerTexture = SkinHelper.getBufferedImageSkin(player);
        if (playerTexture == null) {
            return WardrobeCapability.COLOUR_HAIR_DEFAULT.getRGB();
        }
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerTexture.getRGB(ix + 11, iy + 3));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
    
    public int autoColourSkin(AbstractClientPlayer player) {
        BufferedImage playerTexture = SkinHelper.getBufferedImageSkin(player);
        if (playerTexture == null) {
            return WardrobeCapability.COLOUR_SKIN_DEFAULT.getRGB();
        }
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerTexture.getRGB(ix + 11, iy + 13));
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        r = r / 2;
        g = g / 2;
        b = b / 2;
        
        return new Color(r, g, b).getRGB();
    }
}
