package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.chat.ClickEvent;

import java.net.URI;

@Available("[16, 26)")
public class AbstractOpenURLEvent extends ClickEvent {

    public AbstractOpenURLEvent(String url) {
        super(Action.OPEN_URL, url);
    }

    public URI uri() {
        return URI.create(getValue());
    }
}
