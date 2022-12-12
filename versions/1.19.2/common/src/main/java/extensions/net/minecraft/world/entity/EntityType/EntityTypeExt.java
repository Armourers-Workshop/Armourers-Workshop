package extensions.net.minecraft.world.entity.EntityType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

@Extension
public class EntityTypeExt<T> {

    public static <T extends Entity> T create(@This EntityType<T> entityType, ServerLevel serverLevel, CompoundTag compoundTag, Component component, BlockPos blockPos, MobSpawnType mobSpawnType, boolean bl, boolean bl2) {
        return entityType.create(serverLevel, compoundTag, component, null, blockPos, mobSpawnType, bl, bl2);
    }
}
