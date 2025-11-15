package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.AppearanceImpl;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIScreen;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.utils.Colors;
import moe.plushie.armourers_workshop.init.ModTextures;

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
        var bounds = bounds();
        var fixedBounds = bounds.insetBy(1, 1, 1, 1);
        context.drawImage(backgroundImage, bounds);
        // TODO: Refactoring
        var cx = fixedBounds.x;
        var cy = fixedBounds.y;
        var cw = fixedBounds.width;
        var ch = fixedBounds.height;
        var value = values[type.ordinal()];

        if (type == Type.SATURATION) {
            context.setBlendColor(hueColor);
            context.drawResizableImage(ModTextures.HUE, cx, cy, cw, ch, 0, 176, 256, 20, 256, 256);
            context.setBlendColor(brightnessColor);
            context.drawResizableImage(ModTextures.HUE, cx, cy, cw, ch, type.u, type.v, type.texWidth, type.texHeight, 256, 256);
            context.setBlendColor(UIColor.WHITE);
        } else {
            context.drawResizableImage(ModTextures.HUE, cx, cy, cw, ch, type.u, type.v, type.texWidth, type.texHeight, 256, 256);
        }

        context.addClipPath(UIScreen.convertRectFromView(fixedBounds, this));
        context.drawImage(ModTextures.HUE, ((bounds.width - 3) * value) - 2, 0, 7, 4, 0, 0, 256, 256);
        context.drawImage(ModTextures.HUE, ((bounds.width - 3) * value) - 2, bounds.height - 4, 7, 4, 7, 0, 256, 256);
        context.removeClipPath();
    }

    public void setValueWithComponents(float[] values) {
        System.arraycopy(values, 0, this.values, 0, this.values.length);
        if (type == Type.SATURATION) {
            this.hueColor = UIColor.of(Colors.HSBtoRGB(values[0], 1.0f, 1.0f));
            this.brightnessColor = UIColor.of(Colors.HSBtoRGB(0.0f, 0.0f, values[2]));
        }
    }

    public float value() {
        return values[type.ordinal()];
    }

    private void updateValueWithEvent(UIEvent event) {
        var point = event.locationInView(this);
        var rect = bounds().insetBy(1, 1, 1, 1);
        var value = (point.x - rect.x) / rect.width;
        values[type.ordinal()] = OpenMath.clamp(value, 0f, 1f);
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
