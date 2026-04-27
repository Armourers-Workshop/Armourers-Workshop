package moe.plushie.armourers_workshop.compat.client.gui.event;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.world.inventory.ClickType;

@Available("[16, 26)")
public class AbstractContainerInput {

    private final ClickType type;

    public AbstractContainerInput(ClickType type) {
        this.type = type;
    }

    public static AbstractContainerInput wrap(ClickType input) {
        return new AbstractContainerInput(input);
    }

    public static ClickType unwrap(AbstractContainerInput input) {
        return input.type;
    }
}
