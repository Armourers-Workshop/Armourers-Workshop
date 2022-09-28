package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IItemStackRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IItemBuilder;
import moe.plushie.armourers_workshop.api.skin.ISkinDataProvider;
import moe.plushie.armourers_workshop.core.registry.Registry;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractForgeItemBuilder<T extends Item> implements IItemBuilder<T> {

    protected Item.Properties properties = new Item.Properties();
    protected Supplier<Consumer<T>> binder;
    protected final Function<Item.Properties, T> supplier;

    public AbstractForgeItemBuilder(Function<Item.Properties, T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public IItemBuilder<T> bind(Supplier<IItemStackRendererProvider> provider) {
        this.binder = () -> item -> register(item, provider);
        return this;
    }

    @Override
    public IRegistryKey<T> build(String name) {
        return Registry.ITEM.register(name, () -> {
            T value = supplier.apply(properties);
            EnvironmentExecutor.didInit(EnvironmentType.CLIENT, binder, () -> value);
            return value;
        });
    }

    private void register(T item, Supplier<IItemStackRendererProvider> provider) {
        BlockEntityWithoutLevelRenderer renderer = provider.get().getItemModelRenderer();
        ISkinDataProvider provider1 = ObjectUtils.unsafeCast(item);
        provider1.setSkinData(new IItemRenderProperties() {

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        });
    }
}

