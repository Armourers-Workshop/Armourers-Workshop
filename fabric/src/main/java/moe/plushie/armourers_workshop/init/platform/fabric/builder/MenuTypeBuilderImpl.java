package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuWindow;
import moe.plushie.armourers_workshop.api.common.IMenuWindowProvider;
import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.common.IRegistryBinder;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricMenuType;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuScreen;
import moe.plushie.armourers_workshop.core.registry.Registries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.MenuManager;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MenuTypeBuilderImpl<T extends AbstractContainerMenu, D> implements IMenuTypeBuilder<T> {

    private final IMenuProvider<T, D> factory;
    private final IPlayerDataSerializer<D> serializer;
    private IRegistryBinder<MenuType<T>> binder;

    public MenuTypeBuilderImpl(IMenuProvider<T, D> factory, IPlayerDataSerializer<D> serializer) {
        this.factory = factory;
        this.serializer = serializer;
    }

    @Override
    public <U extends UIWindow & IMenuWindow<T>> IMenuTypeBuilder<T> bind(Supplier<IMenuWindowProvider<T, U>> provider) {
        this.binder = () -> menuType -> {
            // here is safe call client registry.
            ScreenRegistry.register(menuType.get(), MenuScreen.bind(provider.get())::createMenuScreen);
        };
        return this;
    }

    @Override
    public IRegistryKey<MenuType<T>> build(String name) {
        MenuType<?>[] menuTypes = {null};
        MenuType<T> menuType = AbstractFabricMenuType.create((id, inv, buf) -> factory.createMenu(menuTypes[0], id, inv, serializer.read(buf, inv.player)));
        IRegistryKey<MenuType<T>> object = Registries.MENU_TYPE.register(name, () -> menuType);
        MenuManager.registerMenuOpener(menuType, serializer, (player, title, value) -> {
            SimpleMenuProvider menuProvider = new SimpleMenuProvider((window, inv, player2) -> factory.createMenu(menuTypes[0], window, inv, value), title);
            player.openMenu(new ExtendedScreenHandlerFactory() {

                @Override
                public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return menuProvider.createMenu(i, inventory, player);
                }

                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    serializer.write(buf, player, value);
                }

                @Override
                public Component getDisplayName() {
                    return menuProvider.getDisplayName();
                }
            });
            return true;
        });
        menuTypes[0] = menuType;
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, IRegistryBinder.of(binder, object));
        return object;
    }
}
