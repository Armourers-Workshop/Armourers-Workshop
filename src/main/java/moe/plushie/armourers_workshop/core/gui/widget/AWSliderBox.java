package moe.plushie.armourers_workshop.core.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.function.Function;


@OnlyIn(Dist.CLIENT)
public class AWSliderBox extends Button {

    private final int iconWidth;
    private final int lefIconX;
    private final int rightIconX;
    private final int contentLeft;
    private final int contentWidth;
    private final int valueWidth;
    private final double minValue;
    private final double maxValue;
    private final Function<Double, ITextComponent> titleProvider;
    private final FontRenderer fontRenderer;
    private int valueX;
    private boolean dragging;
    private double stepValue = 1;
    private double currentValue;
    private Button.IPressable endHandler;

    public AWSliderBox(int x, int y, int width, int height, Function<Double, ITextComponent> titleProvider, double minValue, double maxValue, Button.IPressable changeHandler) {
        super(x, y, width, height, StringTextComponent.EMPTY, changeHandler);
        this.titleProvider = titleProvider;
        this.iconWidth = 9;
        this.lefIconX = x;
        this.rightIconX = x + width - iconWidth;
        this.contentLeft = lefIconX + iconWidth + 1;
        this.contentWidth = (rightIconX - 1) - contentLeft;
        this.valueWidth = 8;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.fontRenderer = Minecraft.getInstance().font;
    }


    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        int leftState = getState(lefIconX, iconWidth, mouseX, mouseY);
        int rightState = getState(rightIconX, iconWidth, mouseX, mouseY);
        int contentState = getDragState(contentLeft, contentWidth, mouseX, mouseY);
        int leftV = 66 + leftState * 20;
        int rightV = 66 + rightState * 20;
        int contentV = 66 + (leftState | rightState | contentState) * 20;

        RenderUtils.bind(WIDGETS_LOCATION);
        GuiUtils.drawContinuousTexturedBox(matrixStack, contentLeft, y, 0, 46, contentWidth, height, 200, 20, 2, 3, 2, 2, 0);

        GuiUtils.drawContinuousTexturedBox(matrixStack, lefIconX, y, 0, leftV, iconWidth, height, 200, 20, 2, 3, 2, 2, 0);
        GuiUtils.drawContinuousTexturedBox(matrixStack, rightIconX, y, 0, rightV, iconWidth, height, 200, 20, 2, 3, 2, 2, 0);
        GuiUtils.drawContinuousTexturedBox(matrixStack, valueX, y, 0, contentV, valueWidth, height, 200, 20, 2, 3, 2, 2, 0);

        int color = 14737632;
        if (packedFGColor != 0) {
            color = packedFGColor;
        } else if ((leftState | rightState | contentState) != 0) {
            color = 16777120;
        }
        drawCenteredString(matrixStack, fontRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // click dec button
        if (getState(lefIconX, iconWidth, mouseX, mouseY) == 1) {
            updateValue(getResolvedValue(-stepValue));
            playDownSound();
            onPress();
            return true;
        }
        // click add button
        if (getState(rightIconX, iconWidth, mouseX, mouseY) == 1) {
            updateValue(getResolvedValue(stepValue));
            playDownSound();
            onPress();
            return true;
        }
        // click content
        if (getState(contentLeft, contentWidth, mouseX, mouseY) == 1) {
            dragging = true;
            updateValueWithPos(mouseX, mouseY);
            playDownSound();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        if (dragging) {
            updateValueWithPos(mouseX, mouseY);
        }
        return dragging;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            updateValueWithPos(mouseX, mouseY);
            onPress();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return dragging; // reduce conflict with other button.
    }

    public void setEndListener(Button.IPressable endHandler) {
        this.endHandler = endHandler;
    }

    @Override
    public void onPress() {
        if (endHandler != null) {
            endHandler.onPress(this);
        }
    }

    public double getStep() {
        return stepValue;
    }

    public void setStep(double stepValue) {
        this.stepValue = stepValue;
    }

    public double getValue() {
        return currentValue;
    }

    public void setValue(double value) {
        currentValue = MathHelper.clamp(value, minValue, maxValue);
        double progress = (currentValue - minValue) / (maxValue - minValue);
        valueX = contentLeft + (int) ((contentWidth - valueWidth) * progress);
        setMessage(titleProvider.apply(currentValue));
    }

    private void updateValueWithPos(double mouseX, double mouseY) {
        double value = (mouseX - contentLeft) / (double) contentWidth;
        if (Math.abs(value - 0.5) < 0.01) {
            value = 0.5; // attract to mid value.
        }
        updateValue(minValue + value * (maxValue - minValue));
    }

    private void updateValue(double value) {
        setValue(value);
        if (onPress != null) {
            onPress.onPress(this);
        }
    }

    private void playDownSound() {
        super.playDownSound(Minecraft.getInstance().getSoundManager());
    }

    private double getResolvedValue(double inc) {
        double modifier;
        if (Screen.hasShiftDown()) {
            modifier = Screen.hasControlDown() ? 0.01 : 0.1;
        } else {
            modifier = Screen.hasControlDown() ? 10.0 : 1.0;
        }
        double newValue = currentValue + inc * modifier;
        if (Screen.hasAltDown()) {
            newValue = (int) (newValue / modifier) * modifier; // align to modifier
        }
        return newValue;
    }

    private int getDragState(int x, int width, double mouseX, double mouseY) {
        if (dragging) {
            return 1;
        }
        return getState(x, width, mouseX, mouseY);
    }

    private int getState(int x, int width, double mouseX, double mouseY) {
        if (mouseX >= x && mouseX <= (x + width) && mouseY >= y && mouseY <= (y + height)) {
            return 1;
        }
        return 0;
    }

}