package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public interface IMenuType<C extends AbstractContainerMenu> extends Supplier<MenuType<C>> {
}
