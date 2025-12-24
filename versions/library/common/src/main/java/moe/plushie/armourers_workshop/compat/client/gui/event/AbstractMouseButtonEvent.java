package moe.plushie.armourers_workshop.compat.client.gui.event;

import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[1.16, 1.26)")
public class AbstractMouseButtonEvent extends AbstractMouseEvent {

    public final int button;

    public AbstractMouseButtonEvent(double mouseX, double mouseY, int button) {
        super(mouseX, mouseY);
        this.button = button;
    }

    @Override
    public int button() {
        return button;
    }
}
