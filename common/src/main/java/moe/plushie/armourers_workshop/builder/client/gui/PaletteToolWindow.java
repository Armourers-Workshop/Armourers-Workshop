package moe.plushie.armourers_workshop.builder.client.gui;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UITextFieldDelegate;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.builder.client.gui.widget.PaletteEditingWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.ClientMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.HSBSliderBox;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.menu.AbstractContainerMenu;
import moe.plushie.armourers_workshop.core.network.UpdateConfigurableToolPacket;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class PaletteToolWindow extends PaletteEditingWindow<AbstractContainerMenu> implements UITextFieldDelegate {

    private final InteractionHand hand;
    private final ItemStack itemStack;

    public PaletteToolWindow(Component title, ItemStack itemStack, InteractionHand hand) {
        super(ClientMenuScreen.getEmptyMenu(), ClientMenuScreen.getEmptyInventory(), new NSString(title));
        this.hand = hand;
        this.itemStack = itemStack;
        this.paintColorView.setPaintColor(getItemColor(itemStack));
        this.inventoryView.removeFromSuperview();
        this.setFrame(new CGRect(0, 0, 256, 151));
    }

    @Override
    public void init() {
        super.init();
        setupLayout();;
        setupBackgroundView();
        setupPaletteViews();
        setupPickerViews();
        setSelectedColor(paintColorView.color());
        // adjust the title and inventory to top.
        bringSubviewToFront(titleView);
        bringSubviewToFront(inventoryView);
    }

    @Override
    protected void submitColorChange(UIControl control) {
        PaintColor paintColor = paintColorView.paintColor();
        ColorUtils.setColor(itemStack, paintColor);
        NetworkManager.sendToServer(new UpdateConfigurableToolPacket(hand, itemStack));
    }

    private void setupLayout() {
        paintColorView.setFrame(new CGRect(108, 102, 13, 13));
        hexInputView.setFrame(new CGRect(5, 105, 55, 16));
        paintComboBox.setFrame(new CGRect(164, 32, 86, 14));
        paletteComboBox.setFrame(new CGRect(164, 62, 86, 14));
        paletteBox.setFrame(new CGRect(166, 80, 82, 42));
    }

    private void setupBackgroundView() {
        setBackgroundView(ModTextures.defaultWindowImage());

        UIImageView bg = new UIImageView(new CGRect(107, 101, 15, 15));
        bg.setImage(UIImage.of(ModTextures.COLOR_MIXER).uv(107, 101).build());
        bg.setAutoresizingMask(AutoresizingMask.flexibleBottomMargin | AutoresizingMask.flexibleRightMargin);
        addSubview(bg);

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
        paletteBox.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, PaletteToolWindow::applyPaletteChange);
        addSubview(paletteBox);
        setupHelpButton(186, 130);
        setupButton(232, 126, 208, 176, "button.add_palette", PaletteToolWindow::showNewPaletteDialog);
        setupButton(214, 126, 208, 160, "button.remove_palette", PaletteToolWindow::showRemovePaletteDialog);
        setupButton(196, 126, 208, 192, "button.rename_palette", PaletteToolWindow::showRenamePaletteDialog);
        setupPaletteList();
    }

    private void setupLabel(int x, int y, String key) {
        UILabel label = new UILabel(new CGRect(x, y, 80, 9));
        label.setText(getDisplayText(key));
        addSubview(label);
    }

    private void setupButton(int x, int y, int u, int v, String key, BiConsumer<PaletteToolWindow, UIControl> consumer) {
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

    private HSBSliderBox setupHSBSlider(int x, int y, HSBSliderBox.Type type) {
        HSBSliderBox slider = new HSBSliderBox(type, new CGRect(x, y, 150, 10));
        slider.addTarget(this, UIControl.Event.VALUE_CHANGED, PaletteToolWindow::applyColorChange);
        slider.addTarget(this, UIControl.Event.EDITING_DID_END, PaletteToolWindow::submitColorChange);
        addSubview(slider);
        return slider;
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
            self.submitColorChange(null);
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

    @Override
    protected NSString getDisplayText(String key) {
        return new NSString(TranslateUtils.title("inventory.armourers_workshop.colour-mixer" + "." + key));
    }

    protected IPaintColor getItemColor(ItemStack itemStack) {
        IPaintColor paintColor = ColorUtils.getColor(itemStack);
        if (paintColor != null) {
            return paintColor;
        }
        return PaintColor.WHITE;
    }

    public Screen asScreen() {
        return new ClientMenuScreen(this, title.component());
    }
}
