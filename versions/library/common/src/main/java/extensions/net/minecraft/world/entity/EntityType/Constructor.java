package extensions.net.minecraft.world.entity.EntityType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

@Extension
@Available("[1.20, )")
public class Constructor {

    public static <T extends Entity> T create(@This EntityType<T> entityType, ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType) {
        T entity = entityType.create(level);
        EntityType.updateCustomEntityTag(level, null, entity, tag);
        return entity;
    }
}
