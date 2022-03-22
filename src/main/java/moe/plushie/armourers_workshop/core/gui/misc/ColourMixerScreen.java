package moe.plushie.armourers_workshop.core.gui.misc;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.container.ColourMixerContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWHSBSliderBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWPaletteBox;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateColourMixerPacket;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.tileentity.ColourMixerTileEntity;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.utils.color.PaintColor;
import moe.plushie.armourers_workshop.core.utils.color.Palette;
import moe.plushie.armourers_workshop.core.utils.color.PaletteManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public class ColourMixerScreen extends ContainerScreen<ColourMixerContainer> {

    private final ImmutableList<Label> labels = new ImmutableList.Builder<Label>()
            .add(new Label(5, 21, getDisplayText("label.hue")))
            .add(new Label(5, 46, getDisplayText("label.saturation")))
            .add(new Label(5, 71, getDisplayText("label.brightness")))
            .add(new Label(5, 94, getDisplayText("label.hex")))
            .add(new Label(165, 51, getDisplayText("label.presets")))
            .add(new Label(165, 21, getDisplayText("label.paintType")))
            .build();

    private final AWHSBSliderBox[] sliders = {null, null, null};

    private final ColourMixerContainer container;

    private TextFieldWidget textField;
    private AWPaletteBox paletteBox;

    private Color selectedColor = Color.white;
    private ISkinPaintType selectedPaintType = SkinPaintTypes.NORMAL;
    private Palette selectedPalette;

    public ColourMixerScreen(ColourMixerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.container = container;
        this.imageWidth = 256;
        this.imageHeight = 240;

        ColourMixerTileEntity tileEntity = container.getEntity();
        if (tileEntity != null) {
            PaintColor paintColor = tileEntity.getColor();
            selectedColor = new Color(paintColor.getRGB());
            selectedPaintType = paintColor.getPaintType();
        }
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        this.titleLabelY = 8;
        this.inventoryLabelX = 48;
        this.inventoryLabelY = imageHeight - 96;

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
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight, RenderUtils.TEX_COLOUR_MIXER);
        for (Label label : labels) {
            this.font.draw(matrixStack, label.title, leftPos + label.x, topPos + label.y, 0x404040);
        }
        int cx = leftPos + 108;
        int cy = topPos + 102;
        fill(matrixStack, cx, cy, cx + 13, cy + 13, selectedColor.getRGB() | 0xff000000);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0x404040);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), (float) this.inventoryLabelX, (float) this.inventoryLabelY, 0x404040);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if (this.textField != null) {
            this.textField.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.getFocused() != null && this.getFocused().mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double p_231043_5_) {
        if (this.getFocused() != null && this.getFocused().mouseScrolled(mouseX, mouseY, p_231043_5_)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, p_231043_5_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        if (this.getFocused() != null && this.getFocused().mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean keyPressed(int key, int p_231046_2_, int p_231046_3_) {
        boolean typed = super.keyPressed(key, p_231046_2_, p_231046_3_);
        if (!typed && textField != null && textField.isFocused() && key == GLFW.GLFW_KEY_ENTER) {
            String value = textField.getValue();
            if (value.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                setSelectedColor(Color.decode(value));
                onSubmit(null);
                return true;
            }
        }
        return typed;
    }


    private void onSliderChange(Button button) {
        setColorComponents(new float[]{sliders[0].getValue(), sliders[1].getValue(), sliders[2].getValue()});
    }

    private void onPaletteChange(Button button) {
        int index = paletteBox.getSelectedIndex();
        if (selectedPalette != null) {
            if (!selectedPalette.isLocked() && hasShiftDown()) {
                selectedPalette.setColor(index, selectedColor.getRGB());
                return;
            }
            setSelectedColor(new Color(selectedPalette.getColor(index)));
            onSubmit(null);
        }
    }

    private void onSubmit(Button button) {
        ColourMixerTileEntity tileEntity = menu.getEntity();
        if (tileEntity == null) {
            return;
        }
        PaintColor paintColor = PaintColor.of(selectedColor.getRGB(), selectedPaintType);
        UpdateColourMixerPacket.Field field = UpdateColourMixerPacket.Field.COLOUR;
        if (paintColor.equals(field.get(tileEntity))) {
            return;
        }
        UpdateColourMixerPacket packet = new UpdateColourMixerPacket(tileEntity, field, paintColor);
        NetworkHandler.getInstance().sendToServer(packet);
    }

    private AWHSBSliderBox addHSBSlider(int x, int y, AWHSBSliderBox.Type type) {
        AWHSBSliderBox sliderBox = new AWHSBSliderBox(x, y, 150, 10, type, this::onSliderChange);
        sliderBox.setEndListener(this::onSubmit);
        addButton(sliderBox);
        return sliderBox;
    }

    private AWComboBox addPaintList(int x, int y) {
        int selectedIndex = 0;
        ArrayList<ISkinPaintType> paintTypes = new ArrayList<>();
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (ISkinPaintType paintType : SkinPaintTypes.values()) {
            AWComboBox.ComboItem item = new AWComboBox.ComboItem(TranslateUtils.title("paintType." + paintType.getRegistryName()));
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
        addButton(comboBox);
        return comboBox;
    }


    private AWComboBox addPaletteList(int x, int y) {
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
            }
        });
        setSelectedPalette(palettes.get(selectedIndex));
        comboBox.setMaxRowCount(5);
        addButton(comboBox);
        return comboBox;
    }


    private AWPaletteBox addPalettePanel(int x, int y) {
        AWPaletteBox paletteBox = new AWPaletteBox(x, y, 82, 42, 8, 4, this::onPaletteChange);
        addButton(paletteBox);
        return paletteBox;
    }

    private TextFieldWidget addTextField(int x, int y) {
        TextFieldWidget textBox = new TextFieldWidget(font, x, y, 50, 16, StringTextComponent.EMPTY);
        textBox.setMaxLength(7);
        addWidget(textBox);
        return textBox;
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
