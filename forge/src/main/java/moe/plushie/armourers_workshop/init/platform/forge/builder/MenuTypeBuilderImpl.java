package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.*;
import moe.plushie.armourers_workshop.api.common.builder.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuScreen;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MenuTypeBuilderImpl<T extends AbstractContainerMenu, D> implements IMenuTypeBuilder<T> {

    private final IMenuProvider<T, D> factory;
    private final IPlayerDataSerializer<D> serializer;
    private Supplier<Consumer<MenuType<T>>> binder;

    public MenuTypeBuilderImpl(IMenuProvider<T, D> factory, IPlayerDataSerializer<D> serializer) {
        this.factory = factory;
        this.serializer = serializer;
    }

    @Override
    public <U extends UIWindow & IMenuWindow<T>> IMenuTypeBuilder<T> bind(Supplier<IMenuWindowProvider<T, U>> provider) {
        this.binder = () -> menuType -> {
            // here is safe call client registry.
            MenuScreens.register(menuType, MenuScreen.bind(provider.get())::createMenuScreen);
        };
        return this;
    }

    @Override
    public IRegistryKey<MenuType<T>> build(String name) {
        MenuType<?>[] menuTypes = {null};
        MenuType<T> menuType = createMenuType((id, inv, buf) -> factory.createMenu(menuTypes[0], id, inv, serializer.read(buf, inv.player)));
        IRegistryKey<MenuType<T>> object = Registry.MENU_TYPE.register(name, () -> menuType);
        MenuManager.registerMenuOpener(menuType, serializer, (player, title, value) -> {
            SimpleMenuProvider menuProvider = new SimpleMenuProvider((window, inv, player2) -> factory.createMenu(menuTypes[0], window, inv, value), title);
            NetworkHooks.openGui(player, menuProvider, buf -> serializer.write(buf, player, value));
            return true;
        });
        menuTypes[0] = menuType;
        EnvironmentExecutor.initOn(EnvironmentType.CLIENT, binder, object);
        return object;
    }

    private MenuType<T> createMenuType(IContainerFactory<T> factory) {
        //#if MC >= 11800
        //# return net.minecraftforge.common.extensions.IForgeMenuType.create(factory);
        //#else
        return net.minecraftforge.common.extensions.IForgeContainerType.create(factory);
        //#endif
    }
}
