package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.registry.IRegistry;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class AbstractForgeRegistry<T> implements IRegistry<T> {

    public static ArrayList<AbstractForgeRegistry<?>> INSTANCES = new ArrayList<>();

    private final Class<?> type;
    private final ArrayList<IRegistryKey<? extends T>> entries = new ArrayList<>();
    private final String typeName;

    public AbstractForgeRegistry(Class<?> type, DeferredRegister<?> deferredRegistry) {
        this.type = type;
        this.typeName = ObjectUtils.readableName(type);
        // auto register
        deferredRegistry.register(FMLJavaModLoadingContext.get().getModEventBus());
        // we need found it.
        INSTANCES.add(this);
    }
    @Override
    public <I extends T> IRegistryKey<I> register(String name, Supplier<? extends I> provider) {
        ResourceLocation registryName = ModConstants.key(name);
        Supplier<I> object = deferredRegister(name, provider);
        IRegistryKey<I> entry = AbstractForgeRegistryEntry.of(registryName, object);
        entries.add(entry);
        ModLog.debug("Registering {} '{}'", typeName, registryName);
        return entry;
    }

    public abstract  <I extends T> Supplier<I> deferredRegister(String name, Supplier<? extends I> provider);

    @Override
    public abstract T getValue(ResourceLocation registryName);

    @Override
    public abstract ResourceLocation getKey(T object);

    @Override
    public ArrayList<IRegistryKey<? extends T>> getEntries() {
        return entries;
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}

