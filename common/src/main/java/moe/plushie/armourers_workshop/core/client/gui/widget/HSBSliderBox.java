package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class HSBSliderBox extends UIControl {

    private final Type type;
    private final float[] values = {1, 1, 1};

    private UIColor hueColor = UIColor.WHITE;
    private UIColor brightnessColor = UIColor.WHITE;

    private UIImage backgroundImage = AppearanceImpl.BUTTON_IMAGE.imageAtIndex(State.DISABLED);

    private boolean isEditing = false;

    public HSBSliderBox(Type type, CGRect frame) {
        super(frame);
        this.type = type;
        this.setValueWithComponents(values);
    }

    @Override
    public void mouseDown(UIEvent event) {
        beginEditing();
        updateValueWithEvent(event);
        super.mouseDown(event);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        updateValueWithEvent(event);
        super.mouseDragged(event);
    }

    @Override
    public void mouseUp(UIEvent event) {
        updateValueWithEvent(event);
        endEditing();
        super.mouseUp(event);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        CGRect bounds = bounds();
        CGRect fixedBounds = bounds.insetBy(1, 1, 1, 1);
        PoseStack matrixStack = context.poseStack;
        context.drawImage(backgroundImage, bounds);
        // TODO: Refactoring
        int cx = fixedBounds.x;
        int cy = fixedBounds.y;
        int cw = fixedBounds.width;
        int ch = fixedBounds.height;
        float value = values[type.ordinal()];

        RenderSystem.setShaderTexture(0, ModTextures.HUE);

        if (type == Type.SATURATION) {
            RenderSystem.setShaderColor(hueColor);
            RenderSystem.resize(matrixStack, cx, cy, 0, 176, cw, ch, 256, 20);
            RenderSystem.setShaderColor(brightnessColor);
            RenderSystem.resize(matrixStack, cx, cy, type.u, type.v, cw, ch, type.texWidth, type.texHeight);
            RenderSystem.setShaderColor(UIColor.WHITE);
        } else {
            RenderSystem.resize(matrixStack, cx, cy, type.u, type.v, cw, ch, type.texWidth, type.texHeight);
        }

        context.addClipRect(convertRectToView(fixedBounds, null));
        RenderSystem.blit(matrixStack, (int) ((bounds.width - 3) * value) - 2, 0, 0, 0, 7, 4);
        RenderSystem.blit(matrixStack, (int) ((bounds.width - 3) * value) - 2, bounds.height - 4, 7, 0, 7, 4);
        context.removeClipRect();
    }

    public void setValueWithComponents(float[] values) {
        System.arraycopy(values, 0, this.values, 0, this.values.length);
        if (type == Type.SATURATION) {
            this.hueColor = UIColor.getHSBColor(values[0], 1.0f, 1.0f);
            this.brightnessColor = UIColor.getHSBColor(0.0f, 0.0f, values[2]);
        }
    }

    public float getValue() {
        return values[type.ordinal()];
    }

    private void updateValueWithEvent(UIEvent event) {
        CGPoint point = convertPointFromView(event.locationInWindow(), null);
        CGRect rect = bounds().insetBy(1, 1, 1, 1);
        double value = ((double) point.x - rect.x) / rect.width;
        values[type.ordinal()] = MathUtils.clamp((float) value, 0f, 1f);
        valueDidChange();
    }

    private void valueDidChange() {
        sendEvent(Event.VALUE_CHANGED);
    }

    private void beginEditing() {
        isEditing = true;
        sendEvent(Event.EDITING_DID_BEGIN);
    }

    private void endEditing() {
        isEditing = false;
        sendEvent(Event.EDITING_DID_END);
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
