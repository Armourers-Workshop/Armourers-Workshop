package riskyken.armourersWorkshop.client.gui.mannequin;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.data.Rectangle_I_2D;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

public class GuiMannequinTabSkinHair extends GuiTabPanel {

    private GuiButtonExt selectSkinButton;
    private GuiButtonExt autoSkinButton;
    private GuiButtonExt selectHairButton;
    private GuiButtonExt autoHairButton;
    
    private final TileEntityMannequin tileEntity;
    public int skinColour;
    public int hairColour;
    public boolean selectingSkinColour = false;
    public boolean selectingHairColour = false;
    public Color hoverColour = null;
    
    public GuiMannequinTabSkinHair(int tabId, GuiScreen parent, TileEntityMannequin tileEntity) {
        super(tabId, parent);
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        skinColour = tileEntity.getSkinColour();
        hairColour = tileEntity.getHairColour();
        
        selectSkinButton = new GuiButtonExt(0, width / 2 - 90, 25, 80, 14, 
                GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "selectSkin"));
        selectHairButton = new GuiButtonExt(0, width / 2 - 90, 40, 80, 14, 
                GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "selectHair"));
        
        autoSkinButton = new GuiButtonExt(0, width / 2 + 10, 25, 80, 14,
                GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "autoSkin"));
        autoHairButton = new GuiButtonExt(0, width / 2 + 10, 40, 80, 14,
                GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "autoHair"));
        
        buttonList.add(selectSkinButton);
        buttonList.add(selectHairButton);
        
        buttonList.add(autoSkinButton);
        buttonList.add(autoHairButton);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 200, 62);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
        
        if (selectingSkinColour) {
            drawColourBox(width / 2 - 7, 25, hoverColour.getRGB());
        } else {
            drawColourBox(width / 2 - 7, 25, skinColour);
        }
        if (selectingHairColour) {
            drawColourBox(width / 2 - 7, 40, hoverColour.getRGB());
        } else {
            drawColourBox(width / 2 - 7, 40, hairColour);
        }
    }
    
    private void drawColourBox(int x, int y, int colour) {
        Color c = new Color(colour);
        drawTexturedModalRect(x, y, 38, 200, 14, 14);
        GL11.glColor3f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
        drawTexturedModalRect(x + 1, y + 1, 39, 201, 12, 12);
        GL11.glColor3f(1, 1, 1);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (selectingSkinColour) {
            selectingSkinColour = false;
            if (hoverColour != null) {
                skinColour = hoverColour.getRGB();
            }
            ((GuiMannequin)parent).tabOffset.sendData();
        }
        if (selectingHairColour) {
            selectingHairColour = false;
            if (hoverColour != null) {
                hairColour = hoverColour.getRGB();
            }
            ((GuiMannequin)parent).tabOffset.sendData();
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == selectSkinButton) {
            selectingSkinColour = true;
        }
        if (button == autoSkinButton) {
            ResourceLocation rl = AbstractClientPlayer.locationStevePng;
            if (tileEntity.getGameProfile() != null) {
                rl = AbstractClientPlayer.getLocationSkin(tileEntity.getGameProfile().getName());
                AbstractClientPlayer.getDownloadImageSkin(rl, tileEntity.getGameProfile().getName());
            }
            skinColour = autoColourSkin(rl);
            ((GuiMannequin)parent).tabOffset.sendData();
        }
        if (button == selectHairButton) {
            selectingHairColour = true;
        }
        if (button == autoHairButton) {
            ResourceLocation rl = AbstractClientPlayer.locationStevePng;
            if (tileEntity.getGameProfile() != null) {
                rl = AbstractClientPlayer.getLocationSkin(tileEntity.getGameProfile().getName());
                AbstractClientPlayer.getDownloadImageSkin(rl, tileEntity.getGameProfile().getName());
            }
            hairColour = autoColourHair(rl);
            ((GuiMannequin)parent).tabOffset.sendData();
        }
    }
    
    private BufferedImage getBufferedImage(ResourceLocation rl) {
        BufferedImage buff = SkinHelper.getBufferedImageSkin(rl);
        if (buff == null) {
            buff = SkinHelper.getBufferedImageSkin(AbstractClientPlayer.locationStevePng);
        }
        return buff;
    }
    
    private int autoColourHair(ResourceLocation rl) {
        BufferedImage playerSkin = getBufferedImage(rl);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 3));
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
    
    private int autoColourSkin(ResourceLocation rl) {
        BufferedImage playerSkin = getBufferedImage(rl);
        
        int r = 0, g = 0, b = 0;
        
        for (int ix = 0; ix < 2; ix++) {
            for (int iy = 0; iy < 1; iy++) {
                Color c = new Color(playerSkin.getRGB(ix + 11, iy + 13));
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
