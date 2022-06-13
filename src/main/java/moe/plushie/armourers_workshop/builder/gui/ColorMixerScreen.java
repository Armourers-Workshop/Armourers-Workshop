package moe.plushie.armourers_workshop.builder.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.container.ColorMixerContainer;
import moe.plushie.armourers_workshop.builder.tileentity.ColorMixerTileEntity;
import moe.plushie.armourers_workshop.core.gui.widget.*;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateColorMixerPacket;
import moe.plushie.armourers_workshop.core.render.bufferbuilder.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.color.PaintColor;
import moe.plushie.armourers_workshop.utils.color.Palette;
import moe.plushie.armourers_workshop.utils.color.PaletteManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class ColorMixerScreen extends AWAbstractContainerScreen<ColorMixerContainer> {

    private final ImmutableList<Label> labels = new ImmutableList.Builder<Label>()
            .add(new Label(5, 21, getDisplayText("label.hue")))
            .add(new Label(5, 46, getDisplayText("label.saturation")))
            .add(new Label(5, 71, getDisplayText("label.brightness")))
            .add(new Label(5, 94, getDisplayText("label.hex")))
            .add(new Label(165, 51, getDisplayText("label.presets")))
            .add(new Label(165, 21, getDisplayText("label.paintType")))
            .build();

    private final AWHSBSliderBox[] sliders = {null, null, null};

    private AWTextField textField;
    private AWPaletteBox paletteBox;

    private AWComboBox paintComboBox;
    private ArrayList<ISkinPaintType> paintTypes;


    private Color selectedColor = Color.white;
    private ISkinPaintType selectedPaintType = SkinPaintTypes.NORMAL;
    private Palette selectedPalette;

    public ColorMixerScreen(ColorMixerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 240;
        this.reloadStatus();
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelX = 48;
        this.inventoryLabelY = imageHeight - 96;

        this.addIconButton(leftPos + 232, topPos + 126, 208, 176, "button.add_palette", this::showNewPaletteDialog);
        this.addIconButton(leftPos + 214, topPos + 126, 208, 160, "button.remove_palette", this::showRemovePaletteDialog);
        this.addIconButton(leftPos + 196, topPos + 126, 208, 192, "button.rename_palette", this::showRenamePaletteDialog);
        this.addHelpButton(leftPos + 186, topPos + 130);

        this.textField = this.addTextField(leftPos + 5, topPos + 105);
        this.paletteBox = this.addPalettePanel(leftPos + 166, topPos + 80);

        this.sliders[0] = addHSBSlider(leftPos + 5, topPos + 30, AWHSBSliderBox.Type.HUE);
        this.sliders[1] = addHSBSlider(leftPos + 5, topPos + 55, AWHSBSliderBox.Type.SATURATION);
        this.sliders[2] = addHSBSlider(leftPos + 5, topPos + 80, AWHSBSliderBox.Type.BRIGHTNESS);

        this.addPaletteList(leftPos + 164, topPos + 62);
        this.addPaintList(leftPos + 164, topPos + 32);

        this.setSelectedColor(selectedColor);
    }

    @Override
    public void removed() {
        super.removed();
        this.sliders[0] = null;
        this.sliders[1] = null;
        this.sliders[2] = null;
        this.textField = null;
        PaletteManager.getInstance().save();
    }

    @Override
    protected void slotClicked(Slot slot, int p_184098_2_, int p_184098_3_, ClickType clickType) {
        super.slotClicked(slot, p_184098_2_, p_184098_3_, clickType);
        this.reloadStatus();
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, RenderUtils.TEX_COLOUR_MIXER);
        for (Label label : labels) {
            this.font.draw(matrixStack, label.title, leftPos + label.x, topPos + label.y, 0x404040);
        }
        this.renderPreviewColor(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderPreviewColor(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int cx = leftPos + 108;
        int cy = topPos + 102;
        int cu = (int) selectedPaintType.getU();
        int cv = (int) selectedPaintType.getV();

        if (selectedPaintType != SkinPaintTypes.RAINBOW) {
            GL11.glColor4f(selectedColor.getRed() / 255f, selectedColor.getGreen() / 255f, selectedColor.getBlue() / 255f, 1f);
        }

        RenderState renderState = SkinRenderType.colorOffset();
        renderState.setupRenderState();
        RenderUtils.resize(matrixStack, cx, cy, cu, cv, 13, 13, 1, 1, RenderUtils.TEX_CUBE);
        renderState.clearRenderState();

        if (selectedPaintType != SkinPaintTypes.RAINBOW) {
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }

    private void onSliderChange(Button button) {
        setColorComponents(new float[]{sliders[0].getValue(), sliders[1].getValue(), sliders[2].getValue()});
    }

    private void onPaletteChange(Button button) {
        int index = paletteBox.getSelectedIndex();
        if (selectedPalette != null) {
            if (!selectedPalette.isLocked() && hasShiftDown()) {
                selectedPalette.setColor(index, selectedColor.getRGB());
                PaletteManager.getInstance().markDirty();
                return;
            }
            setSelectedColor(new Color(selectedPalette.getColor(index)));
            onSubmit(null);
        }
    }

    private void onSubmit(Button button) {
        ColorMixerTileEntity tileEntity = menu.getTileEntity(ColorMixerTileEntity.class);
        if (tileEntity == null) {
            return;
        }
        PaintColor paintColor = PaintColor.of(selectedColor.getRGB(), selectedPaintType);
        UpdateColorMixerPacket.Field field = UpdateColorMixerPacket.Field.COLOUR;
        if (paintColor.equals(field.get(tileEntity))) {
            return;
        }
        UpdateColorMixerPacket packet = new UpdateColorMixerPacket(tileEntity, field, paintColor);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private void showNewPaletteDialog(Button button) {
        AWInputDialog dialog = new AWInputDialog(getDisplayText("add_palette.title"));
        dialog.setPlaceholderText(getDisplayText("add_palette.enter_name"));
        present(dialog, dialog1 -> {
            if (!dialog1.isCancelled()) {
                selectedPalette = PaletteManager.getInstance().addPalette(dialog1.getText());
                this.reloadPalettes();
            }
        });
    }

    private void showRenamePaletteDialog(Button button) {
        if (selectedPalette.isLocked()) {
            return;
        }
        AWInputDialog dialog = new AWInputDialog(getDisplayText("rename_palette.title"));
        dialog.setPlaceholderText(getDisplayText("rename_palette.enter_name"));
        dialog.setText(selectedPalette.getName());
        present(dialog, dialog1 -> {
            if (!dialog1.isCancelled()) {
                PaletteManager.getInstance().renamePalette(selectedPalette.getName(), dialog.getText());
                this.reloadPalettes();
            }
        });
    }

    private void showRemovePaletteDialog(Button button) {
        if (selectedPalette.isLocked()) {
            return;
        }
        AWConfirmDialog dialog = new AWConfirmDialog(getDisplayText("remove_palette.title"));
        dialog.setMessage(getDisplayText("remove_palette.message"));
        present(dialog, dialog1 -> {
            if (!dialog1.isCancelled()) {
                PaletteManager.getInstance().deletePalette(selectedPalette.getName());
                selectedPalette = PaletteManager.getInstance().getPalettes().iterator().next();
                this.reloadPalettes();

            }
        });
    }

    @Override
    protected void addHoveredButton(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
        if (getFocused() != button && getFocused() != null && getFocused().isMouseOver(mouseX, mouseY)) {
            return;
        }
        super.addHoveredButton(button, matrixStack, mouseX, mouseY);
    }

    private AWHSBSliderBox addHSBSlider(int x, int y, AWHSBSliderBox.Type type) {
        AWHSBSliderBox sliderBox = new AWHSBSliderBox(x, y, 150, 10, type, this::onSliderChange);
        sliderBox.setEndListener(this::onSubmit);
        addButton(sliderBox);
        return sliderBox;
    }

    private void addPaintList(int x, int y) {
        int selectedIndex = 0;
        paintTypes = new ArrayList<>();
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinPaintType paintType : SkinPaintTypes.values()) {
            AWComboBox.ComboItem item = new AWComboBox.ComboItem(TranslateUtils.Name.of(paintType));
            if (paintType == SkinPaintTypes.TEXTURE) {
                item.setEnabled(false);
            }
            if (paintType == selectedPaintType) {
                selectedIndex = items.size();
            }
            items.add(item);
            paintTypes.add(paintType);
        }
        AWComboBox comboBox = new AWComboBox(x, y, 86, 14, items, selectedIndex, button -> {
            if (button instanceof AWComboBox) {
                int newValue = ((AWComboBox) button).getSelectedIndex();
                selectedPaintType = paintTypes.get(newValue);
                onSubmit(null);
            }
        });
        comboBox.setMaxRowCount(5);
        paintComboBox = comboBox;
        addButton(comboBox);
    }

    private void addPaletteList(int x, int y) {
        int selectedIndex = 0;
        ArrayList<Palette> palettes = new ArrayList<>();
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (Palette palette : PaletteManager.getInstance().getPalettes()) {
            AWComboBox.ComboItem item = new AWComboBox.ComboItem(new StringTextComponent(palette.getName()));
            if (palette == selectedPalette) {
                selectedIndex = items.size();
            }
            items.add(item);
            palettes.add(palette);
        }
        AWComboBox comboBox = new AWComboBox(x, y, 86, 14, items, selectedIndex, button -> {
            if (button instanceof AWComboBox) {
                int newValue = ((AWComboBox) button).getSelectedIndex();
                setSelectedPalette(palettes.get(newValue));
                setFocused(null);
            }
        });
        setSelectedPalette(palettes.get(selectedIndex));
        comboBox.setMaxRowCount(5);
        addButton(comboBox);
    }

    private AWPaletteBox addPalettePanel(int x, int y) {
        AWPaletteBox paletteBox = new AWPaletteBox(x, y, 82, 42, 8, 4, this::onPaletteChange);
        addButton(paletteBox);
        return paletteBox;
    }

    private AWTextField addTextField(int x, int y) {
        AWTextField textBox = new AWTextField(font, x, y, 50, 16, StringTextComponent.EMPTY);
        textBox.setMaxLength(7);
        textBox.setReturnHandler(value -> {
            if (value.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                setSelectedColor(Color.decode(value));
                onSubmit(null);
            }
        });
        addButton(textBox);
        return textBox;
    }

    private void addIconButton(int x, int y, int u, int v, String key, Button.IPressable handler) {
        ITextComponent tooltip = getDisplayText(key);
        AWImageButton button = new AWImageButton(x, y, 16, 16, u, v, RenderUtils.TEX_BUTTONS, handler, this::addHoveredButton, tooltip);
        addButton(button);
    }

    private void addHelpButton(int x, int y) {
        ITextComponent tooltip = getDisplayText("help.palette");
        AWImageButton button = new AWHelpButton(x, y, 7, 8, Objects::hash, this::addHoveredButton, tooltip);
        addButton(button);
    }

    private void reloadPalettes() {
        init(Minecraft.getInstance(), width, height);
    }

    private void setColorComponents(float[] values) {
        this.selectedColor = Color.getHSBColor(values[0], values[1], values[2]);
        for (AWHSBSliderBox slider : sliders) {
            slider.setValueWithComponents(values);
        }
        this.textField.setValue(String.format("#%02x%02x%02x", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()));
        if (this.textField.isFocused()) {
            this.textField.setFocus(false);
        }
    }

    private Color getSelectedColor() {
        return selectedColor;
    }

    private void setSelectedColor(Color selectedColor) {
        float[] values = Color.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
        setColorComponents(values);
    }

    private Palette getSelectedPalette() {
        return selectedPalette;
    }

    private void setSelectedPalette(Palette selectedPalette) {
        this.selectedPalette = selectedPalette;
        if (paletteBox != null) {
            paletteBox.setPalette(selectedPalette);
        }
    }

    protected ITextComponent getDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.colour-mixer" + "." + key);
    }

    protected void reloadStatus() {
        ColorMixerTileEntity tileEntity = menu.getTileEntity(ColorMixerTileEntity.class);
        if (tileEntity == null) {
            return;
        }
        IPaintColor paintColor = tileEntity.getColor();
        selectedColor = new Color(paintColor.getRGB());
        selectedPaintType = paintColor.getPaintType();
        if (paintComboBox == null || paintTypes == null) {
            return;
        }
        this.setSelectedColor(selectedColor);
        int selectedIndex = paintTypes.indexOf(selectedPaintType);
        if (selectedIndex >= 0) {
            paintComboBox.setSelectedIndex(selectedIndex);
        }
    }

    public static class Label {
        int x;
        int y;
        ITextComponent title;

        public Label(int x, int y, ITextComponent title) {
            this.title = title;
            this.x = x;
            this.y = y;
        }
    }
}
