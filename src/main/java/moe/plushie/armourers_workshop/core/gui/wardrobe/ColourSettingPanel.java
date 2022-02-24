package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.color.PaintColor;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.item.ColoredItem;
import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.network.packet.UpdateWardrobePacket;
import moe.plushie.armourers_workshop.core.texture.BakedEntityTexture;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.*;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class ColourSettingPanel extends BaseSettingPanel {

    private final ITextComponent paletteText;
    private final ArrayList<ColorPicker> pickers = new ArrayList<>();
    private final SkinWardrobe wardrobe;

    private ColorPicker activatedPicker;

    public ColourSettingPanel(SkinWardrobeContainer container) {
        super("inventory.armourers_workshop.wardrobe.colour_settings");
        this.paletteText = getDisplayText("label.palette");
        this.wardrobe = container.getWardrobe();
        this.setup();
    }

    protected void setup() {
        pickers.add(new ColorPicker(SkinPaintTypes.SKIN, 83, 26, true));
        pickers.add(new ColorPicker(SkinPaintTypes.HAIR, 83, 55, true));
        pickers.add(new ColorPicker(SkinPaintTypes.EYES, 83, 84, true));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_1, 178, 26, false));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_2, 178, 55, false));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_3, 178, 84, false));
        pickers.add(new ColorPicker(SkinPaintTypes.MISC_4, 178, 113, false));
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        for (ColorPicker picker : pickers) {
            picker.reload();
        }
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
        RenderUtils.blit(matrixStack, leftPos, topPos + 152, 0, 152, 256, 98, RenderUtils.TEX_WARDROBE_1);
        RenderUtils.blit(matrixStack, leftPos + 256, topPos + 152, 0, 152, 22, 98, RenderUtils.TEX_WARDROBE_2);

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
            this.title = getDisplayText("label", name);
        }

        public void reload() {
            int posX = leftPos + x + 16;
            int posY = topPos + y + 9;
            String name = paintType.getRegistryName().getPath();
            addIconButton(posX, posY, 144, 192, this::start, getDisplayText("button", name, "select"));
            addIconButton(posX + 17, posY, 208, 160, this::clear, getDisplayText("button", name, "clear"));
            if (enableAutoPick) {
                addIconButton(posX + 17 * 2, posY, 144, 208, this::autoPick, getDisplayText("button", name, "auto"));
            }
            color = getColor();
        }

        public void start(Button button) {
            pickButton = button;
            activatedPicker = this;
            if (pickButton instanceof AWImageButton) {
                ((AWImageButton) pickButton).setSelected(true);
            }
        }

        public void update(int mouseX, int mouseY) {
            int rgb = RenderUtils.getPixelColour(mouseX, mouseY);
            color = PaintColor.of(rgb, SkinPaintTypes.NORMAL);
        }

        public void end() {
            setColor(color);
        }

        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            matrixStack.pushPose();
            matrixStack.translate(leftPos + x, topPos + y, 0);
            // picker name
            font.draw(matrixStack, title, 0, 0, 0x404040);
            // picker background
            RenderUtils.blit(matrixStack, 0, 11, 242, 180 - 14, 14, 14, RenderUtils.TEX_WARDROBE_2);
            // picked color
            if (color != null) {
                fill(matrixStack, 1, 12, 13, 24, color.getRGB());
            }
            matrixStack.popPose();
        }

        private void addIconButton(int x, int y, int u, int v, Button.IPressable pressable, ITextComponent tooltip) {
            ResourceLocation texture = RenderUtils.TEX_BUTTONS;
            addButton(new AWImageButton(x, y, 16, 16, u, v, texture, pressable, this::renderIconTooltip, tooltip));
        }

        private void renderIconTooltip(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
            renderTooltip(matrixStack, button.getMessage(), mouseX, mouseY);
        }

        private void clear(Button button) {
            setColor(null);
        }

        private void autoPick(Button button) {
            ResourceLocation texture = TextureUtils.getTexture(wardrobe.getEntity());
            if (texture == null) {
                return;
            }
            pickButton = button;
            SkinBakery.getInstance().loadEntityTexture(texture, bakedTexture -> {
                if (this.pickButton == button) {
                    PaintColor paintColor = getColorFromTexture(bakedTexture.orElse(null));
                    RenderSystem.recordRenderCall(() -> setColor(paintColor));
                }
            });
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

        private PaintColor getColor() {
            return ColoredItem.getColor(wardrobe.getInventory().getItem(slot));
        }

        private void setColor(PaintColor newValue) {
            activatedPicker = null;
            if (pickButton instanceof AWImageButton) {
                ((AWImageButton) pickButton).setSelected(false);
                pickButton = null;
            }
            color = newValue;
            if (Objects.equals(getColor(), newValue)) {
                return;
            }
            ItemStack itemStack = ItemStack.EMPTY;
            if (newValue != null) {
                itemStack = new ItemStack(AWItems.BOTTLE);
                ColoredItem.setColor(itemStack, newValue);
            }
            NetworkHandler.getInstance().sendToServer(UpdateWardrobePacket.item(wardrobe, slot, itemStack));
        }
    }
}
