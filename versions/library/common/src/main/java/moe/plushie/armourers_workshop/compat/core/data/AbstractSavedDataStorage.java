package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.Nullable;

@Available("[16, 26)")
public class AbstractSavedDataStorage {

    private final DimensionDataStorage storage;

    public AbstractSavedDataStorage(DimensionDataStorage storage) {
        this.storage = storage;
    }

    public static AbstractSavedDataStorage of(MinecraftServer server) {
        return new AbstractSavedDataStorage(server.overworld().getDataStorage());
    }

    public <T extends AbstractSavedData> T computeIfAbsent(AbstractSavedDataType<T> type) {
        return type.computeIfAbsent(storage);
    }

    @Nullable
    public <T extends AbstractSavedData> T get(AbstractSavedDataType<T> type) {
        return type.get(storage);
    }
}
