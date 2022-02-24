package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nullable;
import java.util.List;

public class AWComboButton extends Button {

    private final List<ComboItem> items;
    private final FontRenderer font;
    private final int fontHeight;
    protected int selectedIndex;

    protected int handX;
    protected int handY;
    protected int handWidth;
    protected int handHeight;

    protected int listX;
    protected int listY;
    protected int listRowHeight;

    protected boolean popping;

    public AWComboButton(int x, int y, int width, int height, List<ComboItem> items, int selectedIndex, Button.IPressable changeHandler) {
        super(x, y, width, height, StringTextComponent.EMPTY, changeHandler, NO_TOOLTIP);
        this.items = items;
        this.selectedIndex = selectedIndex;
        this.font = Minecraft.getInstance().font;
        this.fontHeight = font.lineHeight;
        this.handWidth = 14;
        this.handHeight = height;
        this.handX = x + width - handWidth;
        this.handY = y;
        this.listY = y + height + 1;
        this.listX = x;
        this.listRowHeight = fontHeight + 2;
    }

    public int getHandState() {
        if (isHovered) {
            return 1;
        }
        return 0;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Nullable
    public ComboItem getSelectedItem() {
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            return items.get(selectedIndex);
        }
        return null;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        int k = getHandState();
        RenderUtils.bind(WIDGETS_LOCATION);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, 0);
        GuiUtils.drawContinuousTexturedBox(matrixStack, handX, handY, 0, 66 + 20 * k, handWidth, handHeight, 200, 20, 2, 3, 2, 2, 0);

        String text = "v";
        int textY = handY + (handHeight - fontHeight) / 2;
        if (popping) {
            text = "^";
            textY += 2;
        }
        int textWidth = font.width(text);
        font.draw(matrixStack, text, handX + (handWidth - textWidth) / 2.0f, textY, 0xffffff);

        ComboItem item = getSelectedItem();
        if (item != null) {
            item.render(matrixStack, x, y, width, height, mouseX, mouseY, partialTicks, true);
        }

        if (popping) {
            renderList(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    protected void renderList(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int rows = items.size();
        int width = this.width;
        int height = rows * listRowHeight + 6;

        RenderUtils.bind(WIDGETS_LOCATION);
        GuiUtils.drawContinuousTexturedBox(matrixStack, listX, listY, 0, 46, width, height, 200, 20, 2, 3, 2, 2, 0);

        for (int i = 0; i < rows; ++i) {
            ComboItem item = items.get(i);
            int offsetY = listY + 4 + i * listRowHeight;
            item.render(matrixStack, listX, offsetY, width, listRowHeight, mouseX, mouseY, partialTicks, false);
        }
    }

    @Override
    public void onPress() {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            popping = !popping;
            return true;
        }
        if (!popping) {
            return false;
        }
        for (int i = 0; i < items.size(); ++i) {
            ComboItem item = items.get(i);
            int offsetY = listY + 4 + i * listRowHeight;
            if (item.isHovered(listX, offsetY, width, listRowHeight, (int) mouseX, (int) mouseY)) {
                popping = false;
                selectedIndex = i;
                if (onPress != null) {
                    onPress.onPress(this);
                }
                return true;
            }
        }
        return false;
    }

    public static class ComboItem {

        final FontRenderer font;
        final ITextComponent title;

        boolean isEnabled = true;

        public ComboItem(ITextComponent title) {
            this.title = title;
            this.font = Minecraft.getInstance().font;
        }

        public void render(MatrixStack matrixStack, int x, int y, int width, int height, int mouseX, int mouseY, float partialTicks, boolean isTopRender) {
            int textColor = 0xffffffff;
            if (!isEnabled) {
                textColor = 0xffcc0000;
            } else if (!isTopRender && isHovered(x, y, width, height, mouseX, mouseY)) {
                textColor = 0xffffffa0;
                fill(matrixStack, x + 1, y - 1, x + width - 1, y + height - 1, 0x44cccccc);
            }
            if (isTopRender) {
                font.draw(matrixStack, title, x + 3, y + 3, textColor);
            } else {
                font.draw(matrixStack, title, x + 3, y + 1, textColor);
            }
        }

        public boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= (x + width) && mouseY >= (y + 1) && mouseY <= (y + height);
        }
    }
}
