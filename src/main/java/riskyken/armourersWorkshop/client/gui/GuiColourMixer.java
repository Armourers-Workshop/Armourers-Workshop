package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiHSBSlider;
import riskyken.armourersWorkshop.client.gui.controls.GuiHSBSlider.HSBSliderType;
import riskyken.armourersWorkshop.client.gui.controls.GuiHSBSlider.IHSBSliderCallback;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.utils.ModLogger;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColourMixer extends GuiContainer implements IHSBSliderCallback {

    private TileEntityColourMixer tileEntityColourMixer;
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/colour-mixer.png");
    
    private Color colour;
    private GuiHSBSlider[] slidersHSB;
    
    public GuiColourMixer(InventoryPlayer invPlayer, TileEntityColourMixer tileEntityColourMixer) {
        super(new ContainerColourMixer(invPlayer, tileEntityColourMixer));
        this.tileEntityColourMixer = tileEntityColourMixer;
        this.xSize = 176;
        this.ySize = 213;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        colour = new Color(tileEntityColourMixer.getColour());
        float[] hsbvals = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
        slidersHSB = new GuiHSBSlider[3];
        slidersHSB[0] = new GuiHSBSlider(0, this.guiLeft + 5, this.guiTop + 30, 128, 10, this, HSBSliderType.HUE, hsbvals[0], hsbvals[0], hsbvals[2]);
        slidersHSB[1] = new GuiHSBSlider(1, this.guiLeft + 5, this.guiTop + 50, 128, 10, this, HSBSliderType.SATURATION, hsbvals[1], hsbvals[0], hsbvals[2]);
        slidersHSB[2] = new GuiHSBSlider(2, this.guiLeft + 5, this.guiTop + 70, 128, 10, this, HSBSliderType.BRIGHTNESS, hsbvals[2], hsbvals[0], hsbvals[2]);
        buttonList.add(slidersHSB[0]);
        buttonList.add(slidersHSB[1]);
        buttonList.add(slidersHSB[2]);
    }
    
    private void checkForColourUpdates() {
        if (tileEntityColourMixer.getHasItemUpdateAndReset()) {
            ModLogger.log("colour update");
            Color c = new Color(tileEntityColourMixer.getColour());
            float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
            slidersHSB[0].setValue(hsbvals[0]);
            slidersHSB[1].setValue(hsbvals[1]);
            slidersHSB[2].setValue(hsbvals[2]);
        }
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if (which != 0) { return; }
        float[] hsbvals = { (float)slidersHSB[0].getValue(), (float)slidersHSB[1].getValue(), (float)slidersHSB[2].getValue() };
        Color colourNew = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
        Color colourOld = new Color(tileEntityColourMixer.getColour());
        if (colourNew.equals(colourOld)) { return; }
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiColourUpdate(colourNew.getRGB(), false));
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntityColourMixer.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        String labelHue = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.hue");
        String labelSaturation = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.saturation");
        String labelBrightness = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.brightness");
        
        this.fontRendererObj.drawString(labelHue, 5, 22, 4210752);
        this.fontRendererObj.drawString(labelSaturation, 5, 42, 4210752);
        this.fontRendererObj.drawString(labelBrightness, 5, 62, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        checkForColourUpdates();
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        float red = (float) colour.getRed() / 255;
        float green = (float) colour.getGreen() / 255;
        float blue = (float) colour.getBlue() / 255;
        GL11.glColor4f(red, green, blue, 1F);
        
        drawTexturedModalRect(this.guiLeft + 146, this.guiTop + 59, 146, 59, 12, 13);
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    @Override
    public void valueUpdated(GuiHSBSlider source, double sliderValue) {
        float[] hsbvals = { (float)slidersHSB[0].getValue(), (float)slidersHSB[1].getValue(), (float)slidersHSB[2].getValue() };
        hsbvals[source.getType().ordinal()] = (float)sliderValue;
        this.colour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
        if (source.getType() == HSBSliderType.HUE) {
            slidersHSB[1].setHue((float) source.getValue());
        }
        if (source.getType() == HSBSliderType.BRIGHTNESS) {
            slidersHSB[1].setBrightness((float) source.getValue());
        }
    }
}
