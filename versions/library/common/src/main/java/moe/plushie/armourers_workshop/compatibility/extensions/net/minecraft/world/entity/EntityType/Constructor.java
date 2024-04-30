package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.EntityType;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.21, )")
public class Constructor {

    public static <T extends Entity> T create(@This EntityType<T> entityType, ServerLevel level, BlockPos pos, @Nullable ItemStack itemStack, MobSpawnType spawnType) {
        T entity = entityType.create(level);
        if (itemStack != null) {
            CustomData customData = itemStack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
            EntityType.updateCustomEntityTag(level, null, entity, customData);
        }
        return entity;
    }
}
