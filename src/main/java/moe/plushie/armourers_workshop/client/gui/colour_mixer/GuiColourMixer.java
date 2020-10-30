package moe.plushie.armourers_workshop.client.gui.colour_mixer;

import java.awt.Color;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.AbstractGuiDialog;
import moe.plushie.armourers_workshop.client.gui.controls.GuiColourSelector;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHSBSlider;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHSBSlider.HSBSliderType;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHSBSlider.IHSBSliderCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHelp;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.IDialogCallback;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiContainer;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiControl.IScreenSize;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.palette.Palette;
import moe.plushie.armourers_workshop.common.data.type.Rectangle_I_2D;
import moe.plushie.armourers_workshop.common.inventory.ContainerColourMixer;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiColourUpdate;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourMixer;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiColourMixer extends ModGuiContainer<ContainerColourMixer> implements IHSBSliderCallback, IDropDownListCallback, IDialogCallback, IScreenSize {

    private TileEntityColourMixer tileEntityColourMixer;
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(LibGuiResources.GUI_COLOUR_MIXER);
    private static final ResourceLocation CUBE_TEXTURE = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/armour/cube.png");
    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);

    private static String activePalette = ClientProxy.getPaletteManager().getFirstPaletteName();

    private Color colour;
    private GuiHSBSlider[] slidersHSB;
    private GuiTextField colourHex;
    private GuiColourSelector colourSelector;
    private GuiDropDownList colourFamilyList;
    private GuiDropDownList paintTypeDropDown;
    private GuiIconButton buttonPaletteAdd;
    private GuiIconButton buttonPaletteRemove;
    private GuiIconButton buttonPaletteRename;

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

        colourSelector = new GuiColourSelector(3, this.guiLeft + 166, this.guiTop + 80, 82, 42, 10, 10, 8, 4, GUI_TEXTURE);
        buttonList.add(colourSelector);

        colourFamilyList = new GuiDropDownList(4, this.guiLeft + 164, this.guiTop + 60, 86, "", this);
        colourFamilyList.setMaxDisplayCount(16);
        colourFamilyList.setScissor(true);

        updateDropDownPalettes();

        colourSelector.setPalette(ClientProxy.getPaletteManager().getPalette(activePalette));
        buttonList.add(colourFamilyList);

        paintTypeDropDown = new GuiDropDownList(5, this.guiLeft + 164, this.guiTop + 30, 86, "", this);
        paintTypeDropDown.setMaxDisplayCount(10);
        updatePaintTypeDropDown();
        buttonList.add(paintTypeDropDown);

        buttonPaletteAdd = new GuiIconButton(this, -1, this.guiLeft + 230, this.guiTop + 124, 20, 20, GuiHelper.getLocalizedControlName(getName(), "button.add_palette"), TEXTURE_BUTTONS);
        buttonPaletteAdd.setDrawButtonBackground(false).setIconLocation(208, 176, 16, 16);
        buttonList.add(buttonPaletteAdd);

        buttonPaletteRemove = new GuiIconButton(this, -1, this.guiLeft + 230 - 18, this.guiTop + 124, 20, 20, GuiHelper.getLocalizedControlName(getName(), "button.remove_palette"), TEXTURE_BUTTONS);
        buttonPaletteRemove.setDrawButtonBackground(false).setIconLocation(208, 160, 16, 16);
        buttonList.add(buttonPaletteRemove);

        buttonPaletteRename = new GuiIconButton(this, -1, this.guiLeft + 230 - 18 * 2, this.guiTop + 124, 20, 20, GuiHelper.getLocalizedControlName(getName(), "button.rename_palette"), TEXTURE_BUTTONS);
        buttonPaletteRename.setDrawButtonBackground(false).setIconLocation(208, 192, 16, 16);
        buttonList.add(buttonPaletteRename);

        GuiHelp guiHelp = new GuiHelp(this, 0, guiLeft + 240 - 18 * 3, guiTop + 129, GuiHelper.getLocalizedControlName(getName(), "help.palette"));
        buttonList.add(guiHelp);
    }

    private void updateDropDownPalettes() {
        Palette[] palettes = ClientProxy.getPaletteManager().getPalettes();
        colourFamilyList.clearList();
        for (int i = 0; i < palettes.length; i++) {
            colourFamilyList.addListItem(palettes[i].getName());
            if (palettes[i].getName().equals(activePalette)) {
                colourFamilyList.setListSelectedIndex(i);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ClientProxy.getPaletteManager().save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        colourFamilyList.drawForeground(mc, mouseX, mouseY, partialTicks);
        paintTypeDropDown.drawForeground(mc, mouseX, mouseY, partialTicks);
        // this.renderHoveredToolTip(mouseX, mouseY);
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
        for (IPaintType paintType : PaintTypeRegistry.getInstance().getRegisteredTypes()) {
            if (paintType == PaintTypeRegistry.PAINT_TYPE_TEXTURE) {
                paintTypeDropDown.addListItem(paintType.getLocalizedName(), "", false);
            } else {
                paintTypeDropDown.addListItem(paintType.getLocalizedName());
            }
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
            colourHex.setText(String.format("#%02x%02x%02x", this.colour.getRed(), this.colour.getGreen(), this.colour.getBlue()));
        }
    }

    public void updatePaletteColour(Palette palette, int index) {
        palette.setColour(index, colour.getRGB());
        ClientProxy.getPaletteManager().markDirty();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 3) {
            if (!isShiftKeyDown()) {
                this.colour = colourSelector.getSelectedColour();
                updateHexTextbox();
                updateSliders();
            } else {
                if (!colourSelector.getPalette().isLocked()) {
                    updatePaletteColour(colourSelector.getPalette(), colourSelector.getColourIndex());
                }
            }
        }
        if (button == buttonPaletteAdd) {
            openDialog(new GuiDialogAddPalette(this, getName() + ".dialog.add_palette", this));
        }
        if (button == buttonPaletteRemove) {
            Palette palette = ClientProxy.getPaletteManager().getPalette(activePalette);
            if (palette != null) {
                if (!palette.isLocked()) {
                    openDialog(new GuiDialogConfirm(this, getName() + ".dialog.remove_palette", this, I18n.format("inventory." + LibModInfo.ID + ":" + getName() + ".dialog.remove_palette.message", activePalette)));
                }
            }
        }
        if (button == buttonPaletteRename) {
            Palette palette = ClientProxy.getPaletteManager().getPalette(activePalette);
            if (palette != null) {
                if (!palette.isLocked()) {
                    openDialog(new GuiDialogRename(this, getName() + ".dialog.rename_palette", this, activePalette));
                }
            }
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
        IPaintType paintType = getDropDownPaintType();
        if (this.colour.equals(colourOld)) {
            if (paintType == tileEntityColourMixer.getPaintType(0))
                return;
        }
        MessageClientGuiColourUpdate message = new MessageClientGuiColourUpdate(this.colour.getRGB(), false, paintType);
        PacketHandler.networkWrapper.sendToServer(message);
    }

    private IPaintType getDropDownPaintType() {
        return PaintTypeRegistry.getInstance().getRegisteredTypes().get((paintTypeDropDown.getListSelectedIndex()));
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

    private boolean isValidHex(String colorStr) {
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

        if (!colourFamilyList.getIsDroppedDown()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-guiLeft, -guiTop, 0);
            for (int i = 0; i < buttonList.size(); i++) {
                GuiButton button = buttonList.get(i);
                if (button instanceof GuiHelp) {
                    ((GuiHelp) button).drawRollover(mc, mouseX, mouseY);
                }
                if (button instanceof GuiIconButton) {
                    ((GuiIconButton) button).drawRollover(mc, mouseX, mouseY);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        checkForColourUpdates();
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        GlStateManager.pushAttrib();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GlStateManager.matrixMode(GL11.GL_TEXTURE);
        GlStateManager.pushMatrix();
        double f = PaintingHelper.getPaintTextureOffset();
        double paintScale = 0.00390625D;
        GlStateManager.translate(getDropDownPaintType().getU() * paintScale, f / 256D, 0);

        mc.renderEngine.bindTexture(CUBE_TEXTURE);

        float red = (float) colour.getRed() / 255;
        float green = (float) colour.getGreen() / 255;
        float blue = (float) colour.getBlue() / 255;

        if (getDropDownPaintType() != PaintTypeRegistry.PAINT_TYPE_RAINBOW) {
            GlStateManager.color(red, green, blue, 1F);
        }

        drawScaledCustomSizeModalRect(this.guiLeft + 108, this.guiTop + 102, 0, 0, 1, 1, 13, 13, 256, 256);

        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        GlStateManager.resetColor();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popAttrib();

        GlStateManager.color(1F, 1F, 1F, 1F);
        colourHex.drawTextBox();
    }

    @Override
    public void valueUpdated(GuiHSBSlider source, double sliderValue) {
        float[] hsbvals = { (float) slidersHSB[0].getValue(), (float) slidersHSB[1].getValue(), (float) slidersHSB[2].getValue() };
        hsbvals[source.getType().ordinal()] = (float) sliderValue;
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
            DropDownListItem listItem = dropDownList.getListSelectedItem();
            colourSelector.setPalette(null);
            if (listItem != null) {
                Palette palette = ClientProxy.getPaletteManager().getPalette(listItem.displayText);
                if (palette != null) {
                    activePalette = palette.getName();
                    colourSelector.setPalette(palette);
                }
            } else {
                activePalette = null;
            }
        }
        if (dropDownList == paintTypeDropDown) {
            updateColour();
        }
    }

    @Override
    public String getName() {
        return tileEntityColourMixer.getName();
    }

    @Override
    public void dialogResult(AbstractGuiDialog dialog, DialogResult result) {
        if (result == DialogResult.OK) {
            if (dialog instanceof GuiDialogAddPalette) {
                String newName = ((GuiDialogAddPalette) dialog).getNewName();
                if (!StringUtils.isNullOrEmpty(newName)) {
                    if (ClientProxy.getPaletteManager().getPalette(newName) == null) {
                        ClientProxy.getPaletteManager().addPalette(newName);
                        ClientProxy.getPaletteManager().markDirty();
                        activePalette = newName;
                        updateDropDownPalettes();
                        onDropDownListChanged(colourFamilyList);
                    }
                }
            }
            if (dialog instanceof GuiDialogConfirm) {
                Palette palette = ClientProxy.getPaletteManager().getPalette(activePalette);
                if (palette != null) {
                    if (!palette.isLocked()) {
                        ClientProxy.getPaletteManager().deletePalette(activePalette);
                        ClientProxy.getPaletteManager().markDirty();
                        activePalette = ClientProxy.getPaletteManager().getFirstPaletteName();
                        updateDropDownPalettes();
                        onDropDownListChanged(colourFamilyList);
                    }
                }
            }
            if (dialog instanceof GuiDialogRename) {
                Palette palette = ClientProxy.getPaletteManager().getPalette(activePalette);
                if (palette != null) {
                    if (!palette.isLocked()) {
                        String newName = ((GuiDialogRename) dialog).getNewName();
                        if (!StringUtils.isNullOrEmpty(newName)) {
                            if (ClientProxy.getPaletteManager().getPalette(newName) == null) {
                                ClientProxy.getPaletteManager().renamePalette(activePalette, newName);
                                ClientProxy.getPaletteManager().markDirty();
                                activePalette = newName;
                                updateDropDownPalettes();
                                onDropDownListChanged(colourFamilyList);
                            }
                        }
                    }
                }
            }
        }
        closeDialog();
    }

    @Override
    public Rectangle_I_2D getSize() {
        return new Rectangle_I_2D(0, 0, width, height);
    }
}
