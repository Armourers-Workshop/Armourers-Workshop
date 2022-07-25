package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@Environment(value = EnvType.CLIENT)
public class AWHSBSliderBox extends Button {

    private final Type type;
    private final float[] values = {1, 1, 1};

    private Color hueColor;
    private Color brightnessColor;
    private Button.OnPress endHandler;

    private boolean dragging = false;

    public AWHSBSliderBox(int x, int y, int width, int height, Type type, Button.OnPress changeHandler) {
        super(x, y, width, height, TextComponent.EMPTY, changeHandler);
        this.type = type;
        this.setValueWithComponents(values);
    }

    public void setEndListener(Button.OnPress endHandler) {
        this.endHandler = endHandler;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        RenderUtils.tile(matrixStack, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, RenderUtils.TEX_WIDGETS);

        int cx = x + 1;
        int cy = y + 1;
        int cw = width - 2;
        int ch = height - 2;
        float value = values[type.ordinal()];

        RenderUtils.bind(RenderUtils.TEX_HUE);

        if (type == Type.SATURATION) {
            GL11.glColor4f(hueColor.getRed() / 255f, hueColor.getGreen() / 255f, hueColor.getBlue() / 255f, 1.0f);
            RenderUtils.resize(matrixStack, cx, cy, 0, 176, cw, ch, 256, 20);
            GL11.glColor4f(brightnessColor.getRed() / 255f, brightnessColor.getGreen() / 255f, brightnessColor.getBlue() / 255f, 1.0f);
            RenderUtils.resize(matrixStack, cx, cy, type.u, type.v, cw, ch, type.texWidth, type.texHeight);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            RenderUtils.resize(matrixStack, cx, cy, type.u, type.v, cw, ch, type.texWidth, type.texHeight);
        }

        RenderUtils.enableScissor(cx, cy, cw, ch);
        RenderUtils.blit(matrixStack, x + (int) ((width - 3) * value) - 2, y, 0, 0, 7, 4);
        RenderUtils.blit(matrixStack, x + (int) ((width - 3) * value) - 2, y + height - 4, 7, 0, 7, 4);
        RenderUtils.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            dragging = true;
            updateValueWithPos(mouseX, mouseY);
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
            onPressEnd();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return dragging; // reduce conflict with other button.
    }

    @Override
    public void onPress() {
    }

    private void onPressEnd() {
        if (endHandler != null) {
            endHandler.onPress(this);
        }
    }

    public void setValueWithComponents(float[] values) {
        System.arraycopy(values, 0, this.values, 0, this.values.length);
        if (type == Type.SATURATION) {
            this.hueColor = Color.getHSBColor(values[0], 1.0f, 1.0f);
            this.brightnessColor = Color.getHSBColor(0.0f, 0.0f, values[2]);
        }
    }

    public float getValue() {
        return values[type.ordinal()];
    }

    private void updateValueWithPos(double mouseX, double mouseY) {
        double value = (mouseX - (x + 1)) / (width - 2);
        this.values[type.ordinal()] = MathUtils.clamp((float) value, 0f, 1f);
        if (this.onPress != null) {
            this.onPress.onPress(this);
        }
    }

    public enum Type {
        HUE(0, 236, 256, 20),
        SATURATION(0, 196, 231, 20),
        BRIGHTNESS(0, 216, 256, 20);

        final int u;
        final int v;
        final int texWidth;
        final int texHeight;

        Type(int u, int v, int texWidth, int texHeight) {
            this.u = u;
            this.v = v;
            this.texWidth = texWidth;
            this.texHeight = texHeight;
        }
    }
}
