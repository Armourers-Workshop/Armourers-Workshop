package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.inventory.AbstractContainerMenu;

public interface IMenuWindow<T extends AbstractContainerMenu> {

    default void menuDidChange() {
    }
}
