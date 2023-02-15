package moe.plushie.armourers_workshop.builder.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.KeyboardManagerImpl;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.blockentity.ColorMixerBlockEntity;
import moe.plushie.armourers_workshop.builder.data.palette.Palette;
import moe.plushie.armourers_workshop.builder.data.palette.PaletteManager;
import moe.plushie.armourers_workshop.builder.menu.ColorMixerMenu;
import moe.plushie.armourers_workshop.builder.network.UpdateColorMixerPacket;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.HSBSliderBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PaintColorView;
import moe.plushie.armourers_workshop.core.client.gui.widget.PaletteBox;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@Environment(value = EnvType.CLIENT)
public class ColorMixerWindow extends MenuWindow<ColorMixerMenu> implements UITextFieldDelegate {

    private static Palette selectedPalette;

    private final HSBSliderBox[] sliders = {null, null, null};

    private final PaintColorView paintColorView = new PaintColorView(new CGRect(108, 102, 13, 13));
    private final UITextField hexInputView = new UITextField(new CGRect(5, 105, 55, 16));
    private final UIComboBox paintComboBox = new UIComboBox(new CGRect(164, 32, 86, 14));

    private final UIComboBox paletteComboBox = new UIComboBox(new CGRect(164, 62, 86, 14));
    private final PaletteBox paletteBox = new PaletteBox(new CGRect(166, 80, 82, 42));

    private ArrayList<Palette> palettes = new ArrayList<>();
    private ArrayList<ISkinPaintType> paintTypes;

    public ColorMixerWindow(ColorMixerMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.setFrame(new CGRect(0, 0, 256, 240));
        this.reloadStatus();
    }

    @Override
    public void init() {
        super.init();
        setupBackgroundView();
        setupPaletteViews();
        setupPickerViews();
        setSelectedColor(paintColorView.color());
        // adjust the title and inventory to top.
        bringSubviewToFront(titleView);
        bringSubviewToFront(inventoryView);
    }

    @Override
    public void deinit() {
        super.deinit();
        PaletteManager.getInstance().save();
    }

    @Override
    public void menuDidChange() {
        super.menuDidChange();
        reloadStatus();
    }

    @Override
    public void textFieldDidEndEditing(UITextField textField) {
        onSliderChange(textField);
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        String value = textField.value();
        if (value.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            setSelectedColor(UIColor.decode(value));
            onSubmit(null);
        }
        return true;
    }

    private void reloadStatus() {
        ColorMixerBlockEntity tileEntity = menu.getTileEntity(ColorMixerBlockEntity.class);
        if (tileEntity == null) {
            return;
        }
        IPaintColor paintColor = tileEntity.getColor();
        UIColor selectedColor = new UIColor(paintColor.getRGB());
        ISkinPaintType selectedPaintType = paintColor.getPaintType();
        paintColorView.setColor(selectedColor);
        paintColorView.setPaintType(selectedPaintType);
        if (paintComboBox == null || paintTypes == null) {
            return;
        }
        setSelectedColor(selectedColor);
        int selectedIndex = paintTypes.indexOf(selectedPaintType);
        if (selectedIndex >= 0) {
            paintComboBox.setSelectedIndex(selectedIndex);
        }
    }

    private void onPaletteChange(UIControl button) {
        int index = paletteBox.getSelectedIndex();
        if (selectedPalette != null) {
            if (!selectedPalette.isLocked() && KeyboardManagerImpl.hasShiftDown()) {
                selectedPalette.setColor(index, paintColorView.color());
                PaletteManager.getInstance().markDirty();
                return;
            }
            UIColor selectedColor = selectedPalette.getColor(index);
            if (selectedColor == null) {
                return;
            }
            setSelectedColor(selectedColor);
            onSubmit(null);
        }
    }

    private void onSliderChange(UIControl button) {
        setColorComponents(new float[]{sliders[0].getValue(), sliders[1].getValue(), sliders[2].getValue()});
    }

    private void onSubmit(UIControl button) {
        ColorMixerBlockEntity tileEntity = menu.getTileEntity(ColorMixerBlockEntity.class);
        if (tileEntity == null) {
            return;
        }
        PaintColor paintColor = paintColorView.paintColor();
        UpdateColorMixerPacket.Field field = UpdateColorMixerPacket.Field.COLOR;
        if (paintColor.equals(field.get(tileEntity))) {
            return;
        }
        NetworkManager.sendToServer(new UpdateColorMixerPacket(tileEntity, field, paintColor));
    }

    private void showNewPaletteDialog(UIControl button) {
        InputDialog alert = new InputDialog();
        alert.setTitle(getDisplayText("add_palette.title"));
        alert.setPlaceholder(getDisplayText("add_palette.enter_name"));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                selectedPalette = PaletteManager.getInstance().addPalette(alert.value());
                this.reloadPalettes();
            }
        });
    }

    private void showRenamePaletteDialog(UIControl button) {
        if (selectedPalette == null || selectedPalette.isLocked()) {
            return;
        }
        InputDialog alert = new InputDialog();
        alert.setTitle(getDisplayText("rename_palette.title"));
        alert.setPlaceholder(getDisplayText("rename_palette.enter_name"));
        alert.setValue(selectedPalette.getName());
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                String name = selectedPalette.getName();
                PaletteManager.getInstance().renamePalette(name, alert.value());
                this.reloadPalettes();
            }
        });
    }

    private void showRemovePaletteDialog(UIControl button) {
        if (selectedPalette == null || selectedPalette.isLocked()) {
            return;
        }
        ConfirmDialog alert = new ConfirmDialog();
        alert.setTitle(getDisplayText("remove_palette.title"));
        alert.setMessage(getDisplayText("remove_palette.message"));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                String name = selectedPalette.getName();
                PaletteManager.getInstance().deletePalette(name);
                selectedPalette = PaletteManager.getInstance().getPalettes().iterator().next();
                this.reloadPalettes();
            }
        });
    }

    private HSBSliderBox setupHSBSlider(int x, int y, HSBSliderBox.Type type) {
        HSBSliderBox slider = new HSBSliderBox(type, new CGRect(x, y, 150, 10));
        slider.addTarget(this, UIControl.Event.VALUE_CHANGED, ColorMixerWindow::onSliderChange);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, ColorMixerWindow::onSubmit);
        addSubview(slider);
        return slider;
    }

    private void setupBackgroundView() {
        setBackgroundView(UIImage.of(ModTextures.COLOR_MIXER).build());

        setupLabel(5, 21, "label.hue");
        setupLabel(5, 46, "label.saturation");
        setupLabel(5, 71, "label.brightness");
        setupLabel(5, 94, "label.hex");
        setupLabel(165, 51, "label.presets");
        setupLabel(165, 21, "label.paintType");

        addSubview(paintColorView);
    }

    private void setupPickerViews() {
        sliders[0] = setupHSBSlider(5, 30, HSBSliderBox.Type.HUE);
        sliders[1] = setupHSBSlider(5, 55, HSBSliderBox.Type.SATURATION);
        sliders[2] = setupHSBSlider(5, 80, HSBSliderBox.Type.BRIGHTNESS);

        hexInputView.setMaxLength(7);
        hexInputView.setContentInsets(new UIEdgeInsets(2, 5, 2, 0));
        hexInputView.setDelegate(this);
        addSubview(hexInputView);

        setupPaintList();
    }

    private void setupPaletteViews() {
        paletteBox.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ColorMixerWindow::onPaletteChange);
        addSubview(paletteBox);
        setupHelpButton(186, 130);
        setupButton(232, 126, 208, 176, "button.add_palette", ColorMixerWindow::showNewPaletteDialog);
        setupButton(214, 126, 208, 160, "button.remove_palette", ColorMixerWindow::showRemovePaletteDialog);
        setupButton(196, 126, 208, 192, "button.rename_palette", ColorMixerWindow::showRenamePaletteDialog);
        setupPaletteList();
    }

    private void setupLabel(int x, int y, String key) {
        UILabel label = new UILabel(new CGRect(x, y, 80, 9));
        label.setText(getDisplayText(key));
        addSubview(label);
    }

    private void setupButton(int x, int y, int u, int v, String key, BiConsumer<ColorMixerWindow, UIControl> consumer) {
        UIButton button = new UIButton(new CGRect(x, y, 16, 16));
        button.setTooltip(getDisplayText(key));
        button.setBackgroundImage(ModTextures.defaultButtonImage(u, v), UIControl.State.ALL);
        button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, consumer);
        addSubview(button);
    }

    private void setupHelpButton(int x, int y) {
        UIButton button = new UIButton(new CGRect(x, y, 7, 8));
        button.setBackgroundImage(ModTextures.helpButtonImage(), UIControl.State.ALL);
        button.setTooltip(getDisplayText("help.palette"));
        button.setCanBecomeFocused(false);
        addSubview(button);
    }

    private void setupPaintList() {
        int selectedIndex = 0;
        paintTypes = new ArrayList<>();
        ArrayList<UIComboItem> items = new ArrayList<>();
        for (ISkinPaintType paintType : SkinPaintTypes.values()) {
            UIComboItem item = new UIComboItem(new NSString(TranslateUtils.Name.of(paintType)));
            if (paintType == SkinPaintTypes.TEXTURE) {
                item.setEnabled(false);
            }
            if (paintType == paintColorView.paintType()) {
                selectedIndex = items.size();
            }
            items.add(item);
            paintTypes.add(paintType);
        }
        paintComboBox.setMaxRows(5);
        paintComboBox.setSelectedIndex(selectedIndex);
        paintComboBox.reloadData(items);
        paintComboBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, c) -> {
            int newValue = self.paintComboBox.selectedIndex();
            self.paintColorView.setPaintType(self.paintTypes.get(newValue));
            self.onSubmit(null);
        });
        addSubview(paintComboBox);
    }

    private void setupPaletteList() {
        paletteComboBox.setMaxRows(5);
        paletteComboBox.addTarget(this, UIControl.Event.VALUE_CHANGED, (self, view) -> {
            int newValue = ((UIComboBox) view).selectedIndex();
            self.setSelectedPalette(self.palettes.get(newValue));
        });
        addSubview(paletteComboBox);
        reloadPalettes();
    }

    private void reloadPalettes() {
        int selectedIndex = 0;
        palettes = new ArrayList<>();
        ArrayList<UIComboItem> items = new ArrayList<>();
        for (Palette palette : PaletteManager.getInstance().getPalettes()) {
            UIComboItem item = new UIComboItem(new NSString(palette.getName()));
            if (palette == selectedPalette) {
                selectedIndex = items.size();
            }
            items.add(item);
            palettes.add(palette);
        }
        paletteComboBox.setSelectedIndex(selectedIndex);
        paletteComboBox.reloadData(items);
        setSelectedPalette(palettes.get(selectedIndex));
    }

    private void setColorComponents(float[] values) {
        UIColor newValue = UIColor.getHSBColor(values[0], values[1], values[2]);
        paintColorView.setColor(newValue);
        for (HSBSliderBox slider : sliders) {
            slider.setValueWithComponents(values);
        }
        hexInputView.setValue(String.format("#%02x%02x%02x", newValue.getRed(), newValue.getGreen(), newValue.getBlue()));
        if (hexInputView.isEditing()) {
            hexInputView.resignFirstResponder();
        }
    }

    private void setSelectedColor(UIColor selectedColor) {
        float[] values = UIColor.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
        setColorComponents(values);
    }

    private void setSelectedPalette(Palette selectedPalette) {
        ColorMixerWindow.selectedPalette = selectedPalette;
        if (paletteBox != null) {
            paletteBox.setPalette(selectedPalette);
        }
    }

    private NSString getDisplayText(String key) {
        return new NSString(TranslateUtils.title("inventory.armourers_workshop.colour-mixer" + "." + key));
    }
}
