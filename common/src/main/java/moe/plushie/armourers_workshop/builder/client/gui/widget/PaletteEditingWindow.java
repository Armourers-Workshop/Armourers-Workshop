package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.InputManagerImpl;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UITextField;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.builder.data.palette.Palette;
import moe.plushie.armourers_workshop.builder.data.palette.PaletteManager;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.HSBSliderBox;
import moe.plushie.armourers_workshop.core.client.gui.widget.InputDialog;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.PaintColorView;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.utils.ColorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public abstract class PaletteEditingWindow<M extends AbstractContainerMenu> extends MenuWindow<M> implements UITextFieldDelegate {

    protected final HSBSliderBox[] sliders = {null, null, null};

    protected final PaintColorView paintColorView = new PaintColorView(new CGRect(108, 102, 13, 13));
    protected final UITextField hexInputView = new UITextField(new CGRect(5, 105, 55, 16));
    protected final UIComboBox paintComboBox = new UIComboBox(new CGRect(164, 32, 86, 14));

    protected final UIComboBox paletteComboBox = new UIComboBox(new CGRect(164, 62, 86, 14));
    protected final PaletteBox paletteBox = new PaletteBox(new CGRect(166, 80, 82, 42));

    protected final PaletteManager paletteManager = PaletteManager.getInstance();

    protected ArrayList<Palette> palettes = new ArrayList<>();
    protected ArrayList<SkinPaintType> paintTypes;

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
        int index = paletteBox.selectedIndex();
        Palette palette = selectedPalette();
        if (palette != null) {
            if (!palette.isLocked() && InputManagerImpl.hasShiftDown()) {
                palette.setColor(index, paintColorView.color().getRGB());
                paletteManager.markDirty();
                return;
            }
            var selectedColor = palette.getColor(index);
            if (selectedColor == 0) {
                return;
            }
            setSelectedColor(new UIColor(selectedColor));
            submitColorChange(button);
        }
    }

    protected void applyColorChange(UIControl button) {
        setColorComponents(new float[]{sliders[0].value(), sliders[1].value(), sliders[2].value()});
    }

    protected void showNewPaletteDialog(UIControl button) {
        var alert = new InputDialog();
        alert.setTitle(NSString.localizedString("colour-mixer.add_palette.title"));
        alert.setPlaceholder(NSString.localizedString("colour-mixer.add_palette.enter_name"));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                setSelectedPalette(paletteManager.addPalette(alert.value()));
                this.reloadPalettes();
            }
        });
    }

    protected void showRenamePaletteDialog(UIControl button) {
        var palette = selectedPalette();
        if (palette == null || palette.isLocked()) {
            return;
        }
        var alert = new InputDialog();
        alert.setTitle(NSString.localizedString("colour-mixer.rename_palette.title"));
        alert.setPlaceholder(NSString.localizedString("colour-mixer.rename_palette.enter_name"));
        alert.setValue(palette.name());
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                String name = palette.name();
                PaletteManager.getInstance().renamePalette(name, alert.value());
                this.reloadPalettes();
            }
        });
    }

    protected void showRemovePaletteDialog(UIControl button) {
        var palette = selectedPalette();
        if (palette == null || palette.isLocked()) {
            return;
        }
        var alert = new ConfirmDialog();
        alert.setTitle(NSString.localizedString("colour-mixer.remove_palette.title"));
        alert.setMessage(NSString.localizedString("colour-mixer.remove_palette.message", palette.name()));
        alert.showInView(this, () -> {
            if (!alert.isCancelled()) {
                String name = palette.name();
                paletteManager.deletePalette(name);
                setSelectedPalette(paletteManager.palettes().iterator().next());
                this.reloadPalettes();
            }
        });
    }

    protected void reloadPalettes() {
        var selectedIndex = 0;
        palettes = new ArrayList<>();
        var items = new ArrayList<UIComboItem>();
        for (var palette : PaletteManager.getInstance().palettes()) {
            var item = new UIComboItem(new NSString(palette.name()));
            if (palette == selectedPalette()) {
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
        var value = textField.text();
        if (value.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
            setSelectedColor(UIColor.decode(value));
            submitColorChange(textField);
        }
        return true;
    }

    protected void setColorComponents(float[] values) {
        var newValue = new UIColor(ColorUtils.HSBtoRGB(values[0], values[1], values[2]));
        paintColorView.setColor(newValue);
        for (var slider : sliders) {
            slider.setValueWithComponents(values);
        }
        hexInputView.setText(String.format("#%02x%02x%02x", newValue.red(), newValue.green(), newValue.blue()));
        if (hexInputView.isEditing()) {
            hexInputView.resignFirstResponder();
        }
    }

    public void setSelectedColor(UIColor selectedColor) {
        var values = ColorUtils.RGBtoHSB(selectedColor.red(), selectedColor.green(), selectedColor.blue(), null);
        setColorComponents(values);
    }

    public UIColor selectedColor() {
        return paintColorView.color();
    }

    public void setSelectedPalette(Palette selectedPalette) {
        paletteManager.setCurrentPalette(selectedPalette);
        if (paletteBox != null) {
            paletteBox.setPalette(selectedPalette);
        }
    }

    public Palette selectedPalette() {
        return paletteManager.currentPalette();
    }
}
