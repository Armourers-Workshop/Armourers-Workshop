package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface IEntityType<T extends Entity> extends Supplier<EntityType<T>> {

    T create(ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType);

    interface Serializer<T extends Entity> {
        T create(EntityType<T> var1, Level var2);
    }
}
