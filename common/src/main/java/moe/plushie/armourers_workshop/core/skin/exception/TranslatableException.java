package moe.plushie.armourers_workshop.core.skin.exception;

import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;

public class TranslatableException extends Exception {

    private final Component component;

    public TranslatableException(String key, Object... args) {
        this(TranslateUtils.title(key, args));
    }

    public TranslatableException(Component component) {
        super();
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public String getMessage() {
        return component.getString();
    }

    @Override
    public String getLocalizedMessage() {
        return component.getString();
    }
}
