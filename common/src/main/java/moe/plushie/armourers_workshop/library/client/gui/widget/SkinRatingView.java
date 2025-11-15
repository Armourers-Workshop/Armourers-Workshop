package moe.plushie.armourers_workshop.library.client.gui.widget;


import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.init.ModTextures;

public class SkinRatingView extends UIControl {

    private int value;
    private int maxValue;

    private int hoveredValue = -1;

    public SkinRatingView(CGRect frame) {
        super(frame);
        this.setMaxValue(10);
        this.setValue(7);
    }

    @Override
    public void mouseEntered(UIEvent event) {
        super.mouseEntered(event);
        hoveredValue = getRatingAtPos(event.locationInView(this));
    }

    @Override
    public void mouseMoved(UIEvent event) {
        super.mouseMoved(event);
        hoveredValue = getRatingAtPos(event.locationInView(this));
    }

    @Override
    public void mouseExited(UIEvent event) {
        super.mouseExited(event);
        hoveredValue = 0;
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        int value = getRatingAtPos(event.locationInView(this));
        if (value >= 0 && value <= maxValue()) {
            setValue(value);
            sendEvent(Event.VALUE_CHANGED);
        }
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);

        for (int i = 0; i < (maxValue() / 2); i++) {
            context.drawImage(ModTextures.RATING, i * 16, 0, 16, 16, 32, 0, 256, 256);
        }

        int rating = value();
        if (isHighlighted()) {
            rating = hoveredValue;
        }

        int stars = OpenMath.floori(rating / 2F);
        int halfStar = rating % 2;
        for (int i = 0; i < stars; i++) {
            context.drawImage(ModTextures.RATING, i * 16, 0, 16, 16, 0, 0, 256, 256);
        }
        if (halfStar == 1) {
            context.drawImage(ModTextures.RATING, stars * 16, 0, 8, 16, 0, 0, 256, 256);
        }
    }

    public int value() {
        return value;
    }

    public void setValue(int value) {
        this.value = OpenMath.clamp(value, 0, maxValue());
    }

    public int maxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        var frame = frame();
        this.maxValue = maxValue;
        this.setFrame(new CGRect(frame.x, frame.y, maxValue * 8, frame.height));
    }

    private int getRatingAtPos(CGPoint point) {
        return OpenMath.clamp(OpenMath.floori((point.x + 8) / 8f), 0, maxValue);
    }
}
