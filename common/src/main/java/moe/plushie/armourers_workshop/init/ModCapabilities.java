package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.ICapabilityType;
import moe.plushie.armourers_workshop.api.registry.IEntryBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class ModCapabilities {

    public static final IRegistryObject<ICapabilityType<SkinWardrobe>> WARDROBE = normal(SkinWardrobe.class, SkinWardrobe::create).build("entity-skin-provider");

    private static <T> IEntryBuilder<IRegistryObject<ICapabilityType<T>>> normal(Class<T> type, Function<Entity, Optional<T>> provider) {
        return BuilderManager.getInstance().createCapabilityTypeBuilder(type, provider);
    }

    public static void init() {
    }
}
