package moe.plushie.armourers_workshop.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.controls.GuiColourSelector;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHSBSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHSBSlider.HSBSliderType;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHSBSlider.IHSBSliderCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.common.inventory.ContainerColourMixer;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiColourUpdate;
import moe.plushie.armourers_workshop.common.painting.PaintRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintType;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourMixer;
import moe.plushie.armourers_workshop.utils.UtilColour.ColourFamily;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColourMixer extends GuiContainer implements IHSBSliderCallback, IDropDownListCallback {

    private TileEntityColourMixer tileEntityColourMixer;
    private static final ResourceLocation guiTexture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/colour-mixer.png");
    
    private Color colour;
    private GuiHSBSlider[] slidersHSB;
    private GuiTextField colourHex;
    private GuiColourSelector colourSelector;
    private GuiDropDownList colourFamilyList;
    private GuiDropDownList paintTypeDropDown;
    private GuiIconButton buttonPaletteAdd;
    private GuiIconButton buttonPaletteRemove;
    
    public GuiColourMixer(InventoryPlayer invPlayer, TileEntityColourMixer tileEntityColourMixer) {
        super(new ContainerColourMixer(invPlayer, tileEntityColourMixer));
        this.tileEntityColourMixer = tileEntityColourMixer;
        this.xSize = 256;
        this.ySize = 240;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        colour = new Color(tileEntityColourMixer.getColour(0));
        float[] hsbvals = Color.RGBtoHSB(colour.getRed(), colour.getGreen(), colour.getBlue(), null);
        slidersHSB = new GuiHSBSlider[3];
        slidersHSB[0] = new GuiHSBSlider(0, this.guiLeft + 5, this.guiTop + 30, 150, 10, this, HSBSliderType.HUE, hsbvals[0], hsbvals[0], hsbvals[2]);
        slidersHSB[1] = new GuiHSBSlider(1, this.guiLeft + 5, this.guiTop + 55, 150, 10, this, HSBSliderType.SATURATION, hsbvals[1], hsbvals[0], hsbvals[2]);
        slidersHSB[2] = new GuiHSBSlider(2, this.guiLeft + 5, this.guiTop + 80, 150, 10, this, HSBSliderType.BRIGHTNESS, hsbvals[2], hsbvals[0], hsbvals[2]);
        buttonList.add(slidersHSB[0]);
        buttonList.add(slidersHSB[1]);
        buttonList.add(slidersHSB[2]);
        
        colourHex = new GuiTextField(-1, fontRenderer, this.guiLeft + 5, this.guiTop + 105, 50, 10);
        colourHex.setMaxStringLength(7);
        updateHexTextbox();
        
        colourSelector = new GuiColourSelector(3, this.guiLeft + 166, this.guiTop + 80, 82, 42, 10, 10, 8, 4, guiTexture);
        buttonList.add(colourSelector);
        
        colourFamilyList = new GuiDropDownList(4, this.guiLeft + 164, this.guiTop + 60, 86, "", this);
        for (int i = 0; i < ColourFamily.values().length; i++) {
            ColourFamily cf = ColourFamily.values()[i];
            colourFamilyList.addListItem(cf.getLocalizedName());
        }
        ColourFamily cf = tileEntityColourMixer.getColourFamily();
        colourFamilyList.setListSelectedIndex(cf.ordinal());
        colourSelector.setColourFamily(cf);
        buttonList.add(colourFamilyList);
        
        paintTypeDropDown = new GuiDropDownList(5, this.guiLeft + 164, this.guiTop + 30, 86, "", this);
        updatePaintTypeDropDown();
        buttonList.add(paintTypeDropDown);
        
        buttonPaletteAdd = new GuiIconButton(this, -1, this.guiLeft + 166, this.guiTop + 124, 20, 20, "Add Palette", guiTexture).setIconLocation(223, 240, 16, 16);
        buttonList.add(buttonPaletteAdd);
        
        buttonPaletteRemove = new GuiIconButton(this, -1, this.guiLeft + 228, this.guiTop + 124, 20, 20, "Remove Palette", guiTexture).setIconLocation(223, 224, 16, 16);
        buttonList.add(buttonPaletteRemove);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        colourFamilyList.drawForeground(mc, mouseX, mouseY, partialTicks);
        paintTypeDropDown.drawForeground(mc, mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
    
    private void checkForColourUpdates() {
        if (tileEntityColourMixer.getHasItemUpdateAndReset()) {
            this.colour = new Color(tileEntityColourMixer.getColour(0));
            updateSliders();
            updatePaintTypeDropDown();
        }
    }
    
    private void updatePaintTypeDropDown() {
        int paintCount = 0;
        paintTypeDropDown.clearList();
        for (PaintType paintType : PaintRegistry.getRegisteredTypes()) {
            paintTypeDropDown.addListItem(paintType.getLocalizedName());
            if (paintType == tileEntityColourMixer.getPaintType(0)) {
                paintTypeDropDown.setListSelectedIndex(paintCount);
            }
            paintCount++;
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
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (colourFamilyList.getIsDroppedDown()) {
            colourFamilyList.mousePressed(mc, mouseX, mouseY);
            return;
        }
        if (paintTypeDropDown.getIsDroppedDown()) {
            paintTypeDropDown.mousePressed(mc, mouseX, mouseY);
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        colourHex.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (colourFamilyList.getIsDroppedDown()) {
            colourFamilyList.mouseReleased(mouseX, mouseY);
            return;
        }
        if (paintTypeDropDown.getIsDroppedDown()) {
            paintTypeDropDown.mouseReleased(mouseX, mouseY);
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
        if (state != 0) {
            return;
        }
        updateColour();
    }
    
    private void updateColour() {
        Color colourOld = new Color(tileEntityColourMixer.getColour(0));
        PaintType paintType = PaintRegistry.getRegisteredTypes().get((paintTypeDropDown.getListSelectedIndex()));
        if (this.colour.equals(colourOld)) {
            if (paintType == tileEntityColourMixer.getPaintType(0))
            return;
        }
        
        MessageClientGuiColourUpdate message = new MessageClientGuiColourUpdate(this.colour.getRGB(), false, paintType);
        PacketHandler.networkWrapper.sendToServer(message);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) throws IOException {
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
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, tileEntityColourMixer.getName());
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 48, this.ySize - 96 + 2, 4210752);
        
        String labelHue = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getName(), "label.hue");
        String labelSaturation = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getName(), "label.saturation");
        String labelBrightness = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getName(), "label.brightness");
        String labelHex = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getName(), "label.hex");
        String labelPresets = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getName(), "label.presets");
        String labelPaintType = GuiHelper.getLocalizedControlName(tileEntityColourMixer.getName(), "label.paintType");
        
        this.fontRenderer.drawString(labelHue + ":", 5, 21, 4210752);
        this.fontRenderer.drawString(labelSaturation + ":", 5, 46, 4210752);
        this.fontRenderer.drawString(labelBrightness + ":", 5, 71, 4210752);
        this.fontRenderer.drawString(labelHex + ":", 5, 94, 4210752);
        this.fontRenderer.drawString(labelPresets + ":", 165, 51, 4210752);
        this.fontRenderer.drawString(labelPaintType + ":", 165, 21, 4210752);
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        buttonPaletteAdd.drawRollover(mc, mouseX, mouseY);
        buttonPaletteRemove.drawRollover(mc, mouseX, mouseY);
        GlStateManager.popMatrix();
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        checkForColourUpdates();
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(guiTexture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        float red = (float) colour.getRed() / 255;
        float green = (float) colour.getGreen() / 255;
        float blue = (float) colour.getBlue() / 255;
        GL11.glColor4f(red, green, blue, 1F);
        
        drawTexturedModalRect(this.guiLeft + 108, this.guiTop + 102, 108, 102, 13, 13);
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

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        if (dropDownList == colourFamilyList) {
            ColourFamily cf = ColourFamily.values()[dropDownList.getListSelectedIndex()];
            colourSelector.setColourFamily(cf);
            MessageClientGuiButton message = new MessageClientGuiButton((byte) cf.ordinal());
            PacketHandler.networkWrapper.sendToServer(message);
        }
        if (dropDownList == paintTypeDropDown) {
            updateColour();
        }
    }
}
