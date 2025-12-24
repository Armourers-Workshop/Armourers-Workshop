package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.InvokerResult;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.compat.client.utils.AbstractColorPicker;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinBakery;
import moe.plushie.armourers_workshop.core.client.texture.PlayerSkinLoader;
import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.skin.texture.PlayerSkin;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class SkinWardrobeColorSetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobe wardrobe;

    public SkinWardrobeColorSetting(SkinWardrobe wardrobe) {
        super("wardrobe.colour_settings");
        this.wardrobe = wardrobe;
        this.setup();
    }

    private void setup() {
        setupPaletteView();
        setupPickerView(SkinPaintTypes.SKIN, 83, 26, true);
        setupPickerView(SkinPaintTypes.HAIR, 83, 55, true);
        setupPickerView(SkinPaintTypes.EYES, 83, 84, true);
        setupPickerView(SkinPaintTypes.MISC_1, 178, 26, false);
        setupPickerView(SkinPaintTypes.MISC_2, 178, 55, false);
        setupPickerView(SkinPaintTypes.MISC_3, 178, 84, false);
        setupPickerView(SkinPaintTypes.MISC_4, 178, 113, false);
    }

    private void setupPickerView(SkinPaintType paintType, int x, int y, boolean enableAutoPick) {
        var picker = new ColorPicker(paintType, new CGRect(x, y, 90, 24), enableAutoPick);
        addSubview(picker);
    }

    private void setupPaletteView() {
        var bg1 = new UIView(new CGRect(0, 152, 256, 98));
        var bg2 = new UIView(new CGRect(256, 152, 22, 98));
        bg1.setContents(UIImage.of(ModTextures.WARDROBE_1).uv(0, 152).build());
        bg2.setContents(UIImage.of(ModTextures.WARDROBE_2).uv(0, 152).build());
        insertViewAtIndex(bg2, 0);
        insertViewAtIndex(bg1, 0);
        var label = new UILabel(new CGRect(6, 5, 100, 9));
        label.setText(getDisplayText("label.palette"));
        bg1.addSubview(label);
    }

    private class ColorPicker extends UIView {

        private final UILabel titleView = new UILabel(CGRect.ZERO);
        private final UIView colorView = new UIView(new CGRect(1, 12, 12, 12));

        private final int slot;
        private final SkinPaintType paintType;

        private SkinPaintColor color;
        private UIButton pickerButton;

        public ColorPicker(SkinPaintType paintType, CGRect frame, boolean enableAutoPick) {
            super(frame);
            this.paintType = paintType;
            this.slot = SkinSlotType.getDyeSlotIndex(paintType);
            this.updateColor(getColor());
            this.setup(paintType, enableAutoPick);
        }

        private void setup(SkinPaintType paintType, boolean enableAutoPick) {
            var name = paintType.registryName().path();
            // title
            this.titleView.setText(getDisplayText("label." + name));
            this.titleView.setFrame(new CGRect(0, 0, bounds().width, 9));
            this.addSubview(titleView);
            // buttons
            setupIconButton(16, 9, 144, 192, ColorPicker::start, "button." + name + ".select");
            setupIconButton(33, 9, 208, 160, ColorPicker::clear, "button." + name + ".clear");
            if (enableAutoPick) {
                setupIconButton(50, 9, 144, 208, ColorPicker::autoPick, "button." + name + ".auto");
            }
            // picked color
            var view = new UIView(new CGRect(0, 11, 14, 14));
            view.setContents(UIImage.of(ModTextures.WARDROBE_2).uv(242, 166).build());
            addSubview(view);
            addSubview(colorView);
        }

        private void setupIconButton(int x, int y, int u, int v, BiConsumer<ColorPicker, UIControl> consumer, String tooltip) {
            var button = new UIButton(new CGRect(x, y, 16, 16));
            button.setBackgroundImage(ModTextures.defaultButtonImage(u, v), UIControl.State.ALL);
            button.setTooltip(getDisplayText(tooltip));
            button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, consumer);
            addSubview(button);
        }

        public void start(UIControl control) {
            if (!(control instanceof UIButton button)) {
                return;
            }
            button.setSelected(true);
            pickerButton = button;
            var window = window();
            if (window != null) {
                window.addGlobalTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ColorPicker::end);
                window.addGlobalTarget(this, UIControl.Event.MOUSE_MOVED, ColorPicker::update);
            }
        }

        public void update(UIEvent event) {
            var window = window();
            if (window == null) {
                return;
            }
            var point = event.locationInWindow();
            var frame = window.frame();
            AbstractColorPicker.pick(point.x + frame.x, point.y + frame.y, color -> {
                updateColor(SkinPaintColor.of(color, SkinPaintTypes.NORMAL));
            });
        }

        public void end(UIEvent event) {
            event.cancel(InvokerResult.FAIL);
            setColor(color);
            var window = window();
            if (window != null) {
                window.removeGlobalTarget(this, UIControl.Event.MOUSE_MOVED);
                window.removeGlobalTarget(this, UIControl.Event.MOUSE_LEFT_DOWN);
            }
        }

        private void clear(UIControl control) {
            setColor(SkinPaintColor.CLEAR);
        }

        private void autoPick(UIControl control) {
            var texture = PlayerSkinLoader.getInstance().loadSkin(wardrobe.entity());
            if (texture != null) {
                setColor(getColorFromTexture(texture));
            }
        }

        private SkinPaintColor getColorFromTexture(PlayerSkin texture) {
            var skin = PlayerSkinBakery.getInstance().loadSkin(texture).body();
            if (skin == null) {
                return SkinPaintColor.WHITE;
            }
            var colors = new ArrayList<SkinPaintColor>();
            if (paintType == SkinPaintTypes.SKIN) {
                colors.add(skin.getColor(11, 13));
                colors.add(skin.getColor(12, 13));
            }
            if (paintType == SkinPaintTypes.HAIR) {
                colors.add(skin.getColor(11, 3));
                colors.add(skin.getColor(12, 3));
            }
            if (paintType == SkinPaintTypes.EYES) {
                colors.add(skin.getColor(10, 12));
                colors.add(skin.getColor(13, 12));
            }
            int r = 0, g = 0, b = 0, c = 0;
            for (var paintColor : colors) {
                if (paintColor != null) {
                    r += paintColor.red();
                    g += paintColor.green();
                    b += paintColor.blue();
                    c += 1;
                }
            }
            if (c == 0) {
                return SkinPaintColor.CLEAR; // :p a wrong texture
            }
            return SkinPaintColor.of(r / c, g / c, b / c, SkinPaintTypes.NORMAL);
        }

        private SkinPaintColor getColor() {
            var itemStack = wardrobe.inventory().getItem(slot);
            return itemStack.getOrDefault(ModDataComponents.TOOL_COLOR.get(), SkinPaintColor.CLEAR);
        }

        private void setColor(SkinPaintColor newValue) {
            if (pickerButton != null) {
                pickerButton.setSelected(false);
                pickerButton = null;
            }
            updateColor(newValue);
            if (Objects.equals(getColor(), newValue)) {
                return;
            }
            NetworkManager.sendToServer(UpdateWardrobePacket.dying(wardrobe, slot, newValue));
        }

        private void updateColor(SkinPaintColor paintColor) {
            color = paintColor;
            if (!paintColor.isEmpty()) {
                colorView.setBackgroundColor(UIColor.of(paintColor.argb()));
            } else {
                colorView.setBackgroundColor(null);
            }
        }
    }
}
