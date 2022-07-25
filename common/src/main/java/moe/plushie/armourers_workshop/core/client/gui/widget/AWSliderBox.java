package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Function;

@Environment(value = EnvType.CLIENT)
public class AWSliderBox extends Button {

    private final int iconWidth;
    private final int valueWidth;
    private final double minValue;
    private final double maxValue;
    private final Function<Double, Component> titleProvider;
    private final Font fontRenderer;

    private int lefIconX;
    private int rightIconX;
    private int contentLeft;
    private int contentWidth;
    private int valueX;

    private boolean dragging;
    private double stepValue = 1;
    private double currentValue;

    private boolean usingHands = true;

    private Button.OnPress endHandler;

    public AWSliderBox(int x, int y, int width, int height, Function<Double, Component> titleProvider, double minValue, double maxValue, Button.OnPress changeHandler) {
        super(x, y, width, height, TextComponent.EMPTY, changeHandler);
        this.titleProvider = titleProvider;
        this.iconWidth = 9;
        this.valueWidth = 8;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.fontRenderer = Minecraft.getInstance().font;
        this.layout();
    }

    public void setHands(boolean hands) {
        this.usingHands = hands;
        this.layout();
    }

    @Override
    public void setWidth(int p_230991_1_) {
        super.setWidth(p_230991_1_);
        this.layout();
    }

    protected void layout() {
        int handSpacing = 1;
        int handWidth = iconWidth;
        if (!usingHands) {
            handSpacing = 0;
            handWidth = 0;
        }
        this.lefIconX = x;
        this.rightIconX = x + width - handWidth;
        this.contentLeft = lefIconX + handWidth + handSpacing;
        this.contentWidth = (rightIconX - handSpacing) - contentLeft;
        this.setValue(currentValue);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        int leftState = getState(lefIconX, iconWidth, mouseX, mouseY);
        int rightState = getState(rightIconX, iconWidth, mouseX, mouseY);
        int contentState = getDragState(contentLeft, contentWidth, mouseX, mouseY);
        int leftV = 66 + leftState * 20;
        int rightV = 66 + rightState * 20;
        int contentV = 66 + (leftState | rightState | contentState) * 20;

        RenderUtils.bind(RenderUtils.TEX_WIDGETS);
        RenderUtils.tile(matrixStack, contentLeft, y, 0, 46, contentWidth, height, 200, 20, 2, 3, 2, 2);
        if (usingHands) {
            RenderUtils.tile(matrixStack, lefIconX, y, 0, leftV, iconWidth, height, 200, 20, 2, 3, 2, 2);
            RenderUtils.tile(matrixStack, rightIconX, y, 0, rightV, iconWidth, height, 200, 20, 2, 3, 2, 2);
        }
        RenderUtils.tile(matrixStack, valueX, y, 0, contentV, valueWidth, height, 200, 20, 2, 3, 2, 2);

        int color = 0xffffffff;
        if ((leftState | rightState | contentState) != 0) {
            color = 0xffffffa0;
        }
        drawCenteredString(matrixStack, fontRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // click dec button
        if (usingHands && getState(lefIconX, iconWidth, mouseX, mouseY) == 1) {
            updateValue(getResolvedValue(-stepValue));
            playDownSound();
            onPress();
            return true;
        }
        // click add button
        if (usingHands && getState(rightIconX, iconWidth, mouseX, mouseY) == 1) {
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

    public void setEndListener(Button.OnPress endHandler) {
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
        currentValue = MathUtils.clamp(value, minValue, maxValue);
        double progress = (currentValue - minValue) / (maxValue - minValue);
        valueX = contentLeft + (int) ((contentWidth - valueWidth) * progress);
        setMessage(titleProvider.apply(currentValue));
    }

    private void updateValueWithPos(double mouseX, double mouseY) {
        double value = (mouseX - contentLeft) / (double) contentWidth;
        if (Math.abs(value - 0.5) < 0.01) {
            value = 0.5; // attract to mid value.
        }
        double resolvedValue = value * (maxValue - minValue);
        resolvedValue = (int) (resolvedValue / stepValue) * stepValue;
        updateValue(minValue + resolvedValue);
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
