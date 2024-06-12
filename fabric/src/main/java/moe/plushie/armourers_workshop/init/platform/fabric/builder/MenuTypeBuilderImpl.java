package moe.plushie.armourers_workshop.init.platform.fabric.builder;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.compatibility.client.AbstractMenuWindowProvider;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricMenuType;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricRegistries;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EventManager;
import moe.plushie.armourers_workshop.init.platform.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.utils.TypedRegistry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MenuTypeBuilderImpl<T extends AbstractContainerMenu, D> implements IMenuTypeBuilder<T> {

    private final IMenuProvider<T, D> factory;
    private final IMenuSerializer<D> serializer;
    private IRegistryBinder<MenuType<T>> binder;

    public MenuTypeBuilderImpl(IMenuProvider<T, D> factory, IMenuSerializer<D> serializer) {
        this.factory = factory;
        this.serializer = serializer;
    }

    @Override
    public <U extends UIWindow> IMenuTypeBuilder<T> bind(Supplier<AbstractMenuWindowProvider<T, U>> provider) {
        this.binder = () -> menuType -> {
            // here is safe call client registry.
            EventManager.listen(RegisterScreensEvent.class, event -> {
                event.register(menuType.get(), provider.get()::createScreen);
            });
        };
        return this;
    }

    @Override
    public IRegistryHolder<IMenuType<T>> build(String name) {
        AbstractFabricMenuType<T> menuType = AbstractFabricMenuType.create(factory, serializer);
        IRegistryHolder<MenuType<T>> object = AbstractFabricRegistries.MENU_TYPES.register(name, menuType::getType);
        menuType.setRegistryName(object.getRegistryName());
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, object));
        return TypedRegistry.Entry.of(object.getRegistryName(), () -> menuType);
    }
}
