package moe.plushie.armourers_workshop.init.platform.forge.builder;

import com.apple.library.uikit.UIWindow;
import moe.plushie.armourers_workshop.api.common.IMenuProvider;
import moe.plushie.armourers_workshop.api.common.IMenuSerializer;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.event.EventBus;
import moe.plushie.armourers_workshop.api.registry.IMenuTypeBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryBinder;
import moe.plushie.armourers_workshop.compat.client.AbstractMenuWindowProvider;
import moe.plushie.armourers_workshop.compat.forge.builder.AbstractForgeMenuTypeBuilder;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.event.client.RegisterScreensEvent;
import moe.plushie.armourers_workshop.init.registry.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.function.Supplier;

public class MenuTypeBuilderImpl<T extends AbstractContainerMenu, V> implements IMenuTypeBuilder<T> {

    private IRegistryBinder<IMenuType<T>> binder;
    private final AbstractForgeMenuTypeBuilder<T, V> builder;

    public MenuTypeBuilderImpl(IMenuProvider<T, V> factory, IMenuSerializer<V> serializer) {
        this.builder = new AbstractForgeMenuTypeBuilder<>(factory, serializer);
    }

    @Override
    public <U extends UIWindow> IMenuTypeBuilder<T> bind(Supplier<AbstractMenuWindowProvider<T, U>> provider) {
        this.binder = () -> menuType -> {
            // here is safe call client registry.
            EventBus.register(RegisterScreensEvent.class, event -> {
                event.register(menuType.get().get(), provider.get()::createScreen);
            });
        };
        return this;
    }

    @Override
    public IRegistryHolder<IMenuType<T>> build(String name) {
        var entry = Registries.MENU_TYPES.register(name, builder::build);
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, IRegistryBinder.perform(binder, entry));
        return entry;
    }
}
