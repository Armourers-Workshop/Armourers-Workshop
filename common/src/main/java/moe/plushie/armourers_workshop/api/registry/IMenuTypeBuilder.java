package moe.plushie.armourers_workshop.api.registry;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.compatibility.client.AbstractMenuWindowProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public interface IMenuTypeBuilder<T extends AbstractContainerMenu> extends IRegistryBuilder<MenuType<T>> {

    <U extends UIWindow> IMenuTypeBuilder<T> bind(Supplier<AbstractMenuWindowProvider<T, U>> provider);
}
