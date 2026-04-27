package moe.plushie.armourers_workshop.compat.client.gui.event;

import com.apple.library.coregraphics.CGPoint;
import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[16, 26)")
public class AbstractMouseWheelEvent extends AbstractMouseEvent {

    public final double deltaX;
    public final double deltaY;

    private final CGPoint delta;

    public AbstractMouseWheelEvent(double mouseX, double mouseY, double deltaX, double deltaY) {
        super(mouseX, mouseY);
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.delta = new CGPoint(deltaX, deltaY);
    }

    @Override
    public CGPoint delta() {
        return delta;
    }
}
