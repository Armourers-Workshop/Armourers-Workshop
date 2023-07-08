package extensions.net.minecraft.world.entity.EntityType;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.16, 1.20)")
public class Constructor {

    public static <T extends Entity> T create(@This EntityType<T> entityType, ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType) {
        return entityType.create(level, tag, null, null, pos, spawnType, true, true);
    }
}
