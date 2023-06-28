package moe.plushie.armourers_workshop.core.client.gui.wardrobe;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.InvokerResult;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.network.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.NetworkManager;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.TextureUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiConsumer;

@Environment(EnvType.CLIENT)
public class SkinWardrobeColorSetting extends SkinWardrobeBaseSetting {

    private final SkinWardrobe wardrobe;

    public SkinWardrobeColorSetting(SkinWardrobe wardrobe) {
        super("inventory.armourers_workshop.wardrobe.colour_settings");
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

    private void setupPickerView(ISkinPaintType paintType, int x, int y, boolean enableAutoPick) {
        ColorPicker picker = new ColorPicker(paintType, new CGRect(x, y, 90, 24), enableAutoPick);
        addSubview(picker);
    }

    private void setupPaletteView() {
        UIView bg1 = new UIView(new CGRect(0, 152, 256, 98));
        UIView bg2 = new UIView(new CGRect(256, 152, 22, 98));
        bg1.setContents(UIImage.of(ModTextures.WARDROBE_1).uv(0, 152).build());
        bg2.setContents(UIImage.of(ModTextures.WARDROBE_2).uv(0, 152).build());
        insertViewAtIndex(bg2, 0);
        insertViewAtIndex(bg1, 0);
        UILabel label = new UILabel(new CGRect(6, 5, 100, 9));
        label.setText(getDisplayText("label.palette"));
        bg1.addSubview(label);
    }

    public class ColorPicker extends UIView {

        private final UILabel titleView = new UILabel(CGRect.ZERO);
        private final UIView colorView = new UIView(new CGRect(1, 12, 12, 12));

        private final int slot;
        private final ISkinPaintType paintType;

        private IPaintColor color;
        private UIButton pickerButton;

        public ColorPicker(ISkinPaintType paintType, CGRect frame, boolean enableAutoPick) {
            super(frame);
            this.paintType = paintType;
            this.slot = SkinSlotType.getDyeSlotIndex(paintType);
            this.updateColor(getColor());
            this.setup(paintType, enableAutoPick);
        }

        private void setup(ISkinPaintType paintType, boolean enableAutoPick) {
            String name = paintType.getRegistryName().getPath();
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
            UIView view = new UIView(new CGRect(0, 11, 14, 14));
            view.setContents(UIImage.of(ModTextures.WARDROBE_2).uv(242, 166).build());
            addSubview(view);
            addSubview(colorView);
        }

        private void setupIconButton(int x, int y, int u, int v, BiConsumer<ColorPicker, UIControl> consumer, String tooltip) {
            UIButton button = new UIButton(new CGRect(x, y, 16, 16));
            button.setBackgroundImage(ModTextures.defaultButtonImage(u, v), UIControl.State.ALL);
            button.setTooltip(getDisplayText(tooltip));
            button.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, consumer);
            addSubview(button);
        }

        public void start(UIControl control) {
            UIButton button = ObjectUtils.unsafeCast(control);
            button.setSelected(true);
            pickerButton = button;
            UIWindow window = window();
            if (window != null) {
                window.addGlobalTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, ColorPicker::end);
                window.addGlobalTarget(this, UIControl.Event.MOUSE_MOVED, ColorPicker::update);
            }
        }

        public void update(UIEvent event) {
            UIWindow window = window();
            if (window == null) {
                return;
            }
            CGPoint point = event.locationInWindow();
            CGRect frame = window.frame();
            int rgb = RenderSystem.getPixelColor(point.x + frame.x, point.y + frame.y);
            updateColor(PaintColor.of(rgb, SkinPaintTypes.NORMAL));
        }

        public void end(UIEvent event) {
            event.cancel(InvokerResult.FAIL);
            setColor(color);
            UIWindow window = window();
            if (window != null) {
                window.removeGlobalTarget(this, UIControl.Event.MOUSE_MOVED);
                window.removeGlobalTarget(this, UIControl.Event.MOUSE_LEFT_DOWN);
            }
        }

        private void clear(UIControl control) {
            setColor(null);
        }

        private void autoPick(UIControl control) {
            ResourceLocation location = TextureUtils.getTexture(wardrobe.getEntity());
            if (location == null) {
                return;
            }
            BakedEntityTexture texture = PlayerTextureLoader.getInstance().getTextureModel(location);
            if (texture != null) {
                setColor(getColorFromTexture(texture));
            }
        }

        private PaintColor getColorFromTexture(BakedEntityTexture texture) {
            if (texture == null) {
                return null;
            }
            ArrayList<PaintColor> colors = new ArrayList<>();
            if (paintType == SkinPaintTypes.SKIN) {
                colors.add(texture.getColor(11, 13));
                colors.add(texture.getColor(12, 13));
            }
            if (paintType == SkinPaintTypes.HAIR) {
                colors.add(texture.getColor(11, 3));
                colors.add(texture.getColor(12, 3));
            }
            if (paintType == SkinPaintTypes.EYES) {
                colors.add(texture.getColor(10, 12));
                colors.add(texture.getColor(13, 12));
            }
            int r = 0, g = 0, b = 0, c = 0;
            for (PaintColor paintColor : colors) {
                if (paintColor != null) {
                    r += paintColor.getRed();
                    g += paintColor.getGreen();
                    b += paintColor.getBlue();
                    c += 1;
                }
            }
            if (c == 0) {
                return null; // :p a wrong texture
            }
            int argb = 0xff000000 | (r / c) << 16 | (g / c) << 8 | (b / c);
            return PaintColor.of(argb, SkinPaintTypes.NORMAL);
        }

        private IPaintColor getColor() {
            return ColorUtils.getColor(wardrobe.getInventory().getItem(slot));
        }

        private void setColor(IPaintColor newValue) {
            if (pickerButton != null) {
                pickerButton.setSelected(false);
                pickerButton = null;
            }
            updateColor(newValue);
            if (Objects.equals(getColor(), newValue)) {
                return;
            }
            ItemStack itemStack = ItemStack.EMPTY;
            if (newValue != null) {
                itemStack = new ItemStack(ModItems.BOTTLE.get());
                ColorUtils.setColor(itemStack, newValue);
            }
            NetworkManager.sendToServer(UpdateWardrobePacket.pick(wardrobe, slot, itemStack));
        }

        private void updateColor(IPaintColor paintColor) {
            color = paintColor;
            if (paintColor != null) {
                colorView.setBackgroundColor(new UIColor(paintColor.getRGB()));
            } else {
                colorView.setBackgroundColor(null);
            }
        }
    }
}
