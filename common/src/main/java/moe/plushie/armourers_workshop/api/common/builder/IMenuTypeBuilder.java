package moe.plushie.armourers_workshop.api.common.builder;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.IMenuScreenProvider;
import moe.plushie.armourers_workshop.api.common.IMenuWindow;
import moe.plushie.armourers_workshop.api.common.IMenuWindowProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public interface IMenuTypeBuilder<T extends AbstractContainerMenu> extends IEntryBuilder<IRegistryKey<MenuType<T>>> {

    <U extends UIWindow & IMenuWindow<T>> IMenuTypeBuilder<T> bind(Supplier<IMenuWindowProvider<T, U>> provider);
}
