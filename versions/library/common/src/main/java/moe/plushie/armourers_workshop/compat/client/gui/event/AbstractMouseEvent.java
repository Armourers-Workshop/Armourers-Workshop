package moe.plushie.armourers_workshop.compat.client.gui.event;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.impl.event.InputMouseEvent;
import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[1.16, 1.26)")
public class AbstractMouseEvent implements InputMouseEvent {

    public final double mouseX;
    public final double mouseY;

    private final CGPoint location;

    public AbstractMouseEvent(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.location = new CGPoint(mouseX, mouseY);
    }

    @Override
    public CGPoint location() {
        return location;
    }

    @Override
    public CGPoint delta() {
        return CGPoint.ZERO;
    }

    @Override
    public int button() {
        return 0;
    }

    @Override
    public int modifiers() {
        return 0;
    }
}
