package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.KeyboardManagerImpl;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.data.palette.Palette;
import moe.plushie.armourers_workshop.builder.data.palette.PaletteManager;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.HSBSliderBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PaintColorView;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;

public abstract class PaletteEditingWindow<M extends AbstractContainerMenu> extends MenuWindow<M> implements UITextFieldDelegate {

    protected final HSBSliderBox[] sliders = {null, null, null};

    protected final PaintColorView paintColorView = new PaintColorView(new CGRect(108, 102, 13, 13));
    protected final UITextField hexInputView = new UITextField(new CGRect(5, 105, 55, 16));
    protected final UIComboBox paintComboBox = new UIComboBox(new CGRect(164, 32, 86, 14));

    protected final UIComboBox paletteComboBox = new UIComboBox(new CGRect(164, 62, 86, 14));
    protected final PaletteBox paletteBox = new PaletteBox(new CGRect(166, 80, 82, 42));

    protected final PaletteManager paletteManager = PaletteManager.getInstance();

    protected ArrayList<Palette> palettes = new ArrayList<>();
    protected ArrayList<ISkinPaintType> paintTypes;

    public PaletteEditingWindow(M menu, Inventory inventory, NSString title) {
        super(menu, inventory, title);
    }

    @Override
    public void deinit() {
        super.deinit();
        paletteManager.save();
    }

    protected abstract void submitColorChange(UIControl control);

    protected void applyPaletteChange(UIControl button) {
        int index = paletteBox.getSelectedIndex();
        Palette palette = getSelectedPalette();
        if (palette != null) {
            if (!palette.isLocked() && KeyboardManagerImpl.hasShiftDown()) {
                palette.setColor(index, paintColorView.color());
                paletteManager.markDirty();
                return;
            }
            UIColor selectedColor = palette.getColor(index);
            if (selectedColor == null) {
                return;
            }
            setSelectedColor(selectedColor);
            submitColorChange(button);
        }
    }

    protected void applyColorChange(UIControl button) {
        setColorComponents(new float[]{sliders[0].getValue(), sliders[1].getValue(), sliders[2].getValue()});
    }

    protected abstract NSString getDisplayText(String key);

    protected void showNewPaletteDialog(UIControl button) {
        InputDialog alert = new InputDialog();
        alert.setTitle(getDisplayText("add_palette.title"));
        alert.setPlaceholder(getDisplayText("add_palette.enter_name"));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                setSelectedPalette(paletteManager.addPalette(alert.value()));
                this.reloadPalettes();
            }
        });
    }

    protected void showRenamePaletteDialog(UIControl button) {
        Palette palette = getSelectedPalette();
        if (palette == null || palette.isLocked()) {
            return;
        }
        InputDialog alert = new InputDialog();
        alert.setTitle(getDisplayText("rename_palette.title"));
        alert.setPlaceholder(getDisplayText("rename_palette.enter_name"));
        alert.setValue(palette.getName());
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                String name = palette.getName();
                PaletteManager.getInstance().renamePalette(name, alert.value());
                this.reloadPalettes();
            }
        });
    }

    protected void showRemovePaletteDialog(UIControl button) {
        Palette palette = getSelectedPalette();
        if (palette == null || palette.isLocked()) {
            return;
        }
        ConfirmDialog alert = new ConfirmDialog();
        alert.setTitle(getDisplayText("remove_palette.title"));
        alert.setMessage(getDisplayText("remove_palette.message"));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                String name = palette.getName();
                paletteManager.deletePalette(name);
                setSelectedPalette(paletteManager.getPalettes().iterator().next());
                this.reloadPalettes();
            }
        });
    }

    protected void reloadPalettes() {
        int selectedIndex = 0;
        palettes = new ArrayList<>();
        ArrayList<UIComboItem> items = new ArrayList<>();
        for (Palette palette : PaletteManager.getInstance().getPalettes()) {
            UIComboItem item = new UIComboItem(new NSString(palette.getName()));
            if (palette == getSelectedPalette()) {
                selectedIndex = items.size();
            }
            items.add(item);
            palettes.add(palette);
        }
        paletteComboBox.setSelectedIndex(selectedIndex);
        paletteComboBox.reloadData(items);
        setSelectedPalette(palettes.get(selectedIndex));
    }

    @Override
    public void textFieldDidEndEditing(UITextField textField) {
        applyColorChange(textField);
    }

    @Override
    public boolean textFieldShouldReturn(UITextField textField) {
        String value = textField.value();
        if (value.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            setSelectedColor(UIColor.decode(value));
            submitColorChange(textField);
        }
        return true;
    }

    protected void setColorComponents(float[] values) {
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

    public void setSelectedColor(UIColor selectedColor) {
        float[] values = UIColor.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
        setColorComponents(values);
    }

    public UIColor getSelectedColor() {
        return paintColorView.color();
    }

    public void setSelectedPalette(Palette selectedPalette) {
        paletteManager.setCurrentPalette(selectedPalette);
        if (paletteBox != null) {
            paletteBox.setPalette(selectedPalette);
        }
    }

    public Palette getSelectedPalette() {
        return paletteManager.getCurrentPalette();
    }
}
