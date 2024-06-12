package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.ICapabilityType;
import moe.plushie.armourers_workshop.api.registry.IRegistryBuilder;
import moe.plushie.armourers_workshop.api.registry.IRegistryHolder;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public class ModCapabilities {

    public static final IRegistryHolder<ICapabilityType<SkinWardrobe>> WARDROBE = normal(SkinWardrobe.class, SkinWardrobe::create).build("entity-skin-provider");

    private static <T> IRegistryBuilder<ICapabilityType<T>> normal(Class<T> type, Function<Entity, Optional<T>> provider) {
        return BuilderManager.getInstance().createCapabilityTypeBuilder(type, provider);
    }

    public static void init() {
    }
}
