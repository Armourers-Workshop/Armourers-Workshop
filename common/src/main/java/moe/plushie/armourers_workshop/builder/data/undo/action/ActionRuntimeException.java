package moe.plushie.armourers_workshop.builder.data.undo.action;

import net.minecraft.network.chat.Component;

public class ActionRuntimeException extends RuntimeException {

    private final Component component;

    public ActionRuntimeException(Component component) {
        super();
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }
}
