package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class AbstractFabricRegistry<T> implements IRegistry<T> {

    public static ArrayList<AbstractFabricRegistry<?>> INSTANCES = new ArrayList<>();

    private final Class<?> type;
    private final Registry<T> registry;
    private final ArrayList<IRegistryKey<? extends T>> entries = new ArrayList<>();
    private final String typeName;

    public AbstractFabricRegistry(Class<?> type, Registry<T> registry) {
        this.type = type;
        this.typeName = ObjectUtils.readableName(type);
        this.registry = registry;
        // we need found it.
        INSTANCES.add(this);
    }

    @Override
    public <I extends T> IRegistryKey<I> register(String name, Supplier<? extends I> provider) {
        I value = provider.get();
        ResourceLocation registryName = ModConstants.key(name);
        Registry.register(registry, registryName, value);
        IRegistryKey<I> entry = AbstractFabricRegistryEntry.of(registryName, value);
        entries.add(entry);
        ModLog.debug("Registering {} '{}'", typeName, registryName);
        return entry;
    }

    @Override
    public T getValue(ResourceLocation registryName) {
        return registry.get(registryName);
    }

    @Override
    public ResourceLocation getKey(T object) {
        return registry.getKey(object);
    }

    @Override
    public Collection<IRegistryKey<? extends T>> getEntries() {
        return entries;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}

