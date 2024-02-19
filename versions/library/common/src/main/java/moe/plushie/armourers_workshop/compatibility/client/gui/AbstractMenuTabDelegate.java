package moe.plushie.armourers_workshop.compatibility.client.gui;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.Nullable;

/**
 * se use own GUI rendering system,
 * but vanilla events are different.
 * so we need a proxy to forward the tab event.
 */
@Available("[1.20, )")
@Environment(EnvType.CLIENT)
public class AbstractMenuTabDelegate implements GuiEventListener, NarratableEntry {

    private final AbstractMenuScreenImpl<?> impl;

    public AbstractMenuTabDelegate(AbstractMenuScreenImpl<?> impl) {
        this.impl = impl;
    }

    @Override
    public void setFocused(boolean bl) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        if (focusNavigationEvent instanceof FocusNavigationEvent.TabNavigation) {
            boolean value = ((FocusNavigationEvent.TabNavigation) focusNavigationEvent).forward();
            if (impl.changeFocus(value)) {
                return ComponentPath.leaf(this);
            }
        }
        return null;
    }
}
