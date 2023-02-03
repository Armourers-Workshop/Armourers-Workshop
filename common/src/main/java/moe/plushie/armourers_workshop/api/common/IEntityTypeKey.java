package moe.plushie.armourers_workshop.api.common;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

public interface IEntityTypeKey<T extends Entity> extends IRegistryKey<EntityType<T>> {

    T create(ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType);
}
