package moe.plushie.armourers_workshop.library.gui.widget;


import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("NullableProblems")
@OnlyIn(Dist.CLIENT)
public class SkinRatingButton extends Button {

    private int value;
    private int maxValue;

    public SkinRatingButton(int x, int y, int width, int height, Button.IPressable pressable) {
        super(x, y, width, height, StringTextComponent.EMPTY, pressable);
        this.setMaxValue(10);
        this.setValue(7);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        RenderUtils.bind(RenderUtils.TEX_RATING);

        this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
        for (int i = 0; i < (getMaxValue() / 2); i++) {
            RenderUtils.blit(matrixStack, x + i * 16, y, 32, 0, 16, 16);
        }

        int rating = getValue();
        int state = getYImage(isHovered);
        if (state == 2) {
            rating = getRatingAtPos(mouseX, mouseY);
        }

        int stars = MathHelper.floor(rating / 2F);
        int halfStar = rating % 2;
        for (int i = 0; i < stars; i++) {
            RenderUtils.blit(matrixStack, x + i * 16, y, 0, 0, 16, 16);
        }
        if (halfStar == 1) {
            RenderUtils.blit(matrixStack, x + stars * 16, y, 0, 0, 8, 16);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        int value = MathHelper.floor((mouseX + 8 - x) / 8F);
        if (value >= 0 && value <= getMaxValue()) {
            this.setValue(value);
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = MathHelper.clamp(value, 0, getMaxValue());
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        this.width = maxValue * 8;
    }

    private int getRatingAtPos(int mouseX, int mouseY) {
        return MathHelper.clamp(MathHelper.floor((mouseX + 8 - x) / 8F), 0, maxValue);
    }
}
