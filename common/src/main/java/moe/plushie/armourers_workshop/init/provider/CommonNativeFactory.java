package moe.plushie.armourers_workshop.init.provider;

import moe.plushie.armourers_workshop.api.common.IResourceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

public interface CommonNativeFactory {

    IResourceManager createResourceManager(ResourceManager resourceManager);

    MutableComponent createTranslatableComponent(String key, Object... args);

    <T extends Entity> T createEntity(EntityType<T> entityType, ServerLevel level, BlockPos pos, @Nullable CompoundTag tag, MobSpawnType spawnType);
}
