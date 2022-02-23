package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("NullableProblems")
public class OptionButton extends Button {

    private final int iconWidth;
    private final int iconHeight;

    private final int textWidth;
    private final int textHeight;

    private int textColour = 0x404040;
    private FontRenderer font;
    private boolean isSelected;

    public OptionButton(int x, int y, int iconWidth, int iconHeight, ITextComponent title, boolean isSelected) {
        this(x, y, iconWidth, iconHeight, title, isSelected, null);
    }
    public OptionButton(int x, int y, int iconWidth, int iconHeight, ITextComponent title, boolean isSelected, Button.IPressable changeHandler) {
        super(x, y, iconWidth, iconHeight, title, changeHandler, NO_TOOLTIP);
        this.isSelected = isSelected;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
        this.font = Minecraft.getInstance().font;
        this.textHeight = font.lineHeight;
        this.textWidth = font.width(title.getVisualOrderText());
        this.width = iconWidth + 2 + textWidth;
        this.height = Math.max(iconHeight, textHeight);
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setTextColour(int textColour) {
        this.textColour = textColour;
    }

    @Override
    public void onPress() {
        isSelected = !isSelected;
        if (onPress != null) {
            onPress.onPress(this);
        }
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        RenderUtils.bind(WIDGETS_LOCATION);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, y, 0, 46, iconWidth, iconHeight, 200, 20, 2, 3, 2, 2, 0);
        if (this.isSelected()) {
            drawCenteredString(matrixStack, font, "x", x + iconWidth / 2 + 1, y, 0xffcccccc);
        }
        int color = this.textColour;
        font.draw(matrixStack, getMessage(), x + iconWidth + 2, y + 1, color);
    }
}