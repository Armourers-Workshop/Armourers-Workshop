package moe.plushie.armourers_workshop.compat.client.gui.event;

import com.apple.library.impl.event.InputKeyEvent;
import moe.plushie.armourers_workshop.api.annotation.Available;

@Available("[1.16, 1.22)")
public class AbstractCharacterEvent implements InputKeyEvent {

    public final char ch;
    public final int modifiers;

    public AbstractCharacterEvent(char ch, int modifiers) {
        this.ch = ch;
        this.modifiers = modifiers;
    }

    @Override
    public int code() {
        return ch;
    }

    @Override
    public int modifiers() {
        return modifiers;
    }
}
