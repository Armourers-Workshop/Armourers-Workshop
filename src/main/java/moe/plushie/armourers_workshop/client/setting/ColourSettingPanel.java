package moe.plushie.armourers_workshop.client.setting;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.client.IconButton;
import moe.plushie.armourers_workshop.common.item.BottleItem;
import moe.plushie.armourers_workshop.common.item.SkinItems;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.Objects;

public class ColourSettingPanel extends BaseSettingPanel {

    private final ITextComponent paletteText;
    private final ArrayList<ColorPicker> pickers = new ArrayList<>();
    private final SkinWardrobe wardrobe;

    private ColorPicker activatedPicker;

    public ColourSettingPanel(SkinWardrobe wardrobe) {
        super(TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.colourSettings"));
        this.paletteText = TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.colour_settings.label.palette");
        this.wardrobe = wardrobe;
        this.setup();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        for (ColorPicker picker : pickers) {
            picker.reload();
        }
    }

    public void setup() {
        pickers.add(new ColorPicker(SkinPaintTypes.SKIN, 83, 26, true));
        pickers.add(new ColorPicker(SkinPaintTypes.HAIR, 83, 55, true));
        pickers.add(new ColorPicker(SkinPaintTypes.EYES, 83, 84, true));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_1, 178, 26, false));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_2, 178, 55, false));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_3, 178, 84, false));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_4, 178, 113, false));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderPalette(matrixStack, mouseX, mouseY, partialTicks);

        for (ColorPicker picker : pickers) {
            picker.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    public void renderPalette(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(SkinCore.TEX_WARDROBE_1);
        blit(matrixStack, leftPos, topPos + 152, 0, 152, 256, 98);

        Minecraft.getInstance().getTextureManager().bind(SkinCore.TEX_WARDROBE_2);
        blit(matrixStack, leftPos + 256, topPos + 152, 0, 152, 22, 98);

        font.draw(matrixStack, paletteText, leftPos + 6, topPos + 152 + 5, 0x404040);
    }

    public ColorPicker getActivatedPicker() {
        return activatedPicker;
    }

    public class ColorPicker {
        private final int x;
        private final int y;
        private final int slot;
        private final boolean enableAutoPick;

        private final ISkinPaintType paintType;

        private final ITextComponent title;

        private Button pickButton;
        private PaintColor color;

        public ColorPicker(ISkinPaintType paintType, int x, int y, boolean enableAutoPick) {
            String name = paintType.getRegistryName().getPath();
            this.x = x;
            this.y = y;
            this.enableAutoPick = enableAutoPick;
            this.paintType = paintType;
            this.slot = SkinSlotType.DYE.getIndex() + SkinSlotType.getSlotIndex(paintType);
            this.title = TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.colour_settings.label." + name);
        }

        public void reload() {
            int posX = leftPos + x + 16;
            int posY = topPos + y + 9;
            String name = paintType.getRegistryName().getPath();
            addIconButton(posX, posY, 144, 192, this::startPick, getText(name, "select"));
            addIconButton(posX + 17, posY, 208, 160, this::clear, getText(name, "clear"));
            if (enableAutoPick) {
                addIconButton(posX + 17 * 2, posY, 144, 208, this::autoPick, getText(name, "auto"));
            }
            color = getColor();
        }

        public void pick(int mouseX, int mouseY) {
            int rgb = RenderUtils.getPixelColour(mouseX, mouseY);
            color = new PaintColor(rgb, SkinPaintTypes.NORMAL);
        }

        public void startPick(Button button) {
            pickButton = button;
            activatedPicker = this;
            if (pickButton instanceof IconButton) {
                ((IconButton) pickButton).setSelected(true);
            }
        }

        public void endPick() {
            setColor(color);
            activatedPicker = null;
            if (pickButton instanceof IconButton) {
                ((IconButton) pickButton).setSelected(false);
            }
        }

        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            matrixStack.pushPose();
            matrixStack.translate(leftPos + x, topPos + y, 0);
            // picker name
            font.draw(matrixStack, title, 0, 0, 0x404040);
            // picker background
            Minecraft.getInstance().getTextureManager().bind(SkinCore.TEX_WARDROBE_2);
            blit(matrixStack, 0, 11, 242, 180 - 14, 14, 14);
            // picked color
            if (color != null) {
                fill(matrixStack, 1, 12, 13, 24, color.getRGB());
            }
            matrixStack.popPose();
        }

        private void addIconButton(int x, int y, int u, int v, Button.IPressable pressable, ITextComponent tooltip) {
            ResourceLocation texture = SkinCore.TEX_BUTTONS;
            addButton(new IconButton(x, y, 16, 16, u, v, texture, pressable, this::renderIconTooltip, tooltip));
        }

        private void renderIconTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
            renderTooltip(matrixStack, button.getMessage(), mouseX, mouseY);
        }


        private void clear(Button button) {
            setColor(null);
        }

        private void autoPick(Button button) {
//            BufferedImage playerTexture = TextureHelper.getBufferedImageSkin(player);
//            if (playerTexture == null) {
//                return ExtraColours.COLOUR_NONE;
//            }
//
//            int r = 0, g = 0, b = 0;
//
//            if (type == ExtraColourType.SKIN) {
//                for (int ix = 0; ix < 2; ix++) {
//                    for (int iy = 0; iy < 1; iy++) {
//                        Color c = new Color(playerTexture.getRGB(ix + 11, iy + 13));
//                        r += c.getRed();
//                        g += c.getGreen();
//                        b += c.getBlue();
//                    }
//                }
//                r = r / 2;
//                g = g / 2;
//                b = b / 2;
//            }
//            if (type == ExtraColourType.HAIR) {
//                for (int ix = 0; ix < 2; ix++) {
//                    for (int iy = 0; iy < 1; iy++) {
//                        Color c = new Color(playerTexture.getRGB(ix + 11, iy + 3));
//                        r += c.getRed();
//                        g += c.getGreen();
//                        b += c.getBlue();
//                    }
//                }
//                r = r / 2;
//                g = g / 2;
//                b = b / 2;
//            }
//            if (type == ExtraColourType.EYE) {
//                Color c1 = new Color(playerTexture.getRGB(10, 13));
//                Color c2 = new Color(playerTexture.getRGB(13, 13));
//
//                r += c1.getRed();
//                g += c1.getGreen();
//                b += c1.getBlue();
//
//                r += c2.getRed();
//                g += c2.getGreen();
//                b += c2.getBlue();
//
//                r = r / 2;
//                g = g / 2;
//                b = b / 2;
//            }
//            return new Color(r, g, b).getRGB();
        }


        private PaintColor getColor() {
            return BottleItem.getPaintColor(wardrobe.getInventory().getItem(slot));
        }

        private void setColor(PaintColor newValue) {
            color = newValue;
            if (Objects.equals(getColor(), newValue)) {
                return;
            }
            ItemStack itemStack = ItemStack.EMPTY;
            if (newValue != null) {
                itemStack = new ItemStack(SkinItems.BOTTLE.get());
                BottleItem.setColor(itemStack, newValue);
            }
            wardrobe.getInventory().setItem(slot, itemStack);
            wardrobe.sendToServer();
        }

        private ITextComponent getText(String name, String state) {
            return TranslateUtils.translate("inventory.armourers_workshop.wardrobe.tab.colour_settings.button." + name + "." + state);
        }
    }
}
