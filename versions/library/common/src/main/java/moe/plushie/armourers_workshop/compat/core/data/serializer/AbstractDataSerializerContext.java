package moe.plushie.armourers_workshop.compat.core.data.serializer;

import com.mojang.serialization.DynamicOps;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

@Available("[1.21, )")
public class AbstractDataSerializerContext {

    private final Object context;
    private final HolderLookup.Provider provider;

    public AbstractDataSerializerContext(@Nullable Object context) {
        this.context = context;
        this.provider = unwrap(context);
    }

    public <V> DynamicOps<V> create(DynamicOps<V> ops) {
        if (provider != null) {
            return provider.createSerializationContext(ops);
        }
        return ops;
    }

    public Object context() {
        return context;
    }

    public HolderLookup.Provider provider() {
        return provider;
    }

    private static HolderLookup.Provider unwrap(@Nullable Object context) {
        if (context instanceof HolderLookup.Provider provider) {
            return provider;
        }
        if (context instanceof Level level) {
            return level.registryAccess();
        }
        if (context instanceof Entity entity) {
            return entity.registryAccess();
        }
        if (context instanceof BlockEntity blockEntity) {
            return unwrap(blockEntity.getLevel());
        }
        return null;
    }
}
