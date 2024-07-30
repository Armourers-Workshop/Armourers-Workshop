package moe.plushie.armourers_workshop.utils;

import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;

public class ContainerChangeHandler implements ContainerListener {

    private final Runnable action;

    private boolean allowChanges = true;

    public ContainerChangeHandler(Runnable action) {
        this.action = action;
    }

    public void performWithoutChanges(Runnable runnable) {
        allowChanges = false;
        runnable.run();
        allowChanges = true;
    }

    public void setChanged() {
        if (allowChanges) {
            action.run();
        }
    }

    @Override
    public void containerChanged(Container container) {
        setChanged();
    }
}
