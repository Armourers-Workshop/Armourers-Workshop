package moe.plushie.armourers_workshop.api.registry;

import moe.plushie.armourers_workshop.api.common.IMenuScreenProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public interface IMenuTypeBuilder<T extends AbstractContainerMenu> extends IEntryBuilder<IRegistryObject<MenuType<T>>> {

    <U extends Screen & MenuAccess<T>> IMenuTypeBuilder<T> bind(Supplier<IMenuScreenProvider<T, U>> provider);
}
