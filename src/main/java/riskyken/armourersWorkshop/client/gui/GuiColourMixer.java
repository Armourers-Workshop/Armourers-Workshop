package riskyken.armourersWorkshop.client.gui;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiColourSelector;
import riskyken.armourersWorkshop.client.gui.controls.GuiHSBSlider;
import riskyken.armourersWorkshop.client.gui.controls.GuiHSBSlider.HSBSliderType;
import riskyken.armourersWorkshop.client.gui.controls.GuiHSBSlider.IHSBSliderCallback;
import riskyken.armourersWorkshop.common.inventory.ContainerColourMixer;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColourMixer extends GuiContainer implements IHSBSliderCallback {

    private TileEntityColourMixer tileEntityColourMixer;
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/colour-mixer.png");
    
    private Color colour;
    private GuiHSBSlider[] slidersHSB;
    private GuiTextField colourHex;
    private GuiColourSelector colourSelector;
    
    public GuiColourMixer(InventoryPlayer invPlayer, TileEntityColourMixer tileEntityColourMixer) {
        super(new ContainerColourMixer(invPlayer, tileEntityColourMixer));
        this.tileEntityColourMixer = tileEntityColourMixer;
        this.xSize = 176;
        this.ySize = 233;
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
        colourHex = new GuiTextField(fontRendererObj, this.guiLeft + 5, this.guiTop + 90, 50, 10);
        colourHex.setMaxStringLength(7);
        updateHexTextbox();
        colourSelector = new GuiColourSelector(3, this.guiLeft + 5, this.guiTop + 110, 82, 22, 10, 10, 8);
        buttonList.add(colourSelector);
    }
    
    private void checkForColourUpdates() {
        if (tileEntityColourMixer.getHasItemUpdateAndReset()) {
            this.colour = new Color(tileEntityColourMixer.getColour());
            updateSliders();
        }
    }
    
    private void updateSliders() {
        float[] hsbvals = Color.RGBtoHSB(this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue(), null);
        slidersHSB[0].setValue(hsbvals[0]);
        slidersHSB[1].setValue(hsbvals[1]);
        slidersHSB[2].setValue(hsbvals[2]);
    }
    
    private void updateHexTextbox() {
        if (!colourHex.isFocused()) {
            colourHex.setText(String.format(
                    "#%02x%02x%02x",
                    this.colour.getRed(),
                    this.colour.getGreen(),
                    this.colour.getBlue()));
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 3) {
            this.colour = ((GuiColourSelector)button).getSelectedColour();
            updateHexTextbox();
            updateSliders();
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        colourHex.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);
        if (which != 0) { return; }
        
        updateColour();
    }
    
    private void updateColour() {
        Color colourOld = new Color(tileEntityColourMixer.getColour());
        if (this.colour.equals(colourOld)) { return; }
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiColourUpdate(this.colour.getRGB(), false));
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        if (!colourHex.textboxKeyTyped(key, keyCode)) {
            super.keyTyped(key, keyCode);
        } else {
            String text = colourHex.getText();
            if (isValidHex(text)) {
                Color newColour = Color.decode(text);
                if (!newColour.equals(this.colour)) {
                    this.colour = newColour;
                    updateSliders();
                    updateColour();
                }
            }
        }
    }
    
    private boolean isValidHex (String colorStr) {
        String hexPatten = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern pattern = Pattern.compile(hexPatten);
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntityColourMixer.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        
        String labelHue = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.hue");
        String labelSaturation = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.saturation");
        String labelBrightness = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.brightness");
        String labelHex = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.hex");
        String labelPresets = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getInventoryName(), "label.presets");
        
        this.fontRendererObj.drawString(labelHue + ":", 5, 21, 4210752);
        this.fontRendererObj.drawString(labelSaturation + ":", 5, 41, 4210752);
        this.fontRendererObj.drawString(labelBrightness + ":", 5, 61, 4210752);
        this.fontRendererObj.drawString(labelHex + ":", 5, 81, 4210752);
        this.fontRendererObj.drawString(labelPresets + ":", 5, 101, 4210752);
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
        colourHex.drawTextBox();
    }
    
    @Override
    public void valueUpdated(GuiHSBSlider source, double sliderValue) {
        float[] hsbvals = { (float)slidersHSB[0].getValue(), (float)slidersHSB[1].getValue(), (float)slidersHSB[2].getValue() };
        hsbvals[source.getType().ordinal()] = (float)sliderValue;
        this.colour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
        updateHexTextbox();
        if (source.getType() == HSBSliderType.HUE) {
            slidersHSB[1].setHue((float) source.getValue());
        }
        if (source.getType() == HSBSliderType.BRIGHTNESS) {
            slidersHSB[1].setBrightness((float) source.getValue());
        }
    }
}
