package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.EntityType;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IEntitySpawnReason;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
@Available("[1.21, 1.22)")
public class Constructor {

    public static <T extends Entity> T create(@This EntityType<T> entityType, ServerLevel level, BlockPos pos, @Nullable ItemStack itemStack, IEntitySpawnReason reason) {
        T entity = entityType.create(level);
        if (itemStack != null) {
            var entityData = itemStack.get(DataComponents.ENTITY_DATA);
            if (entityData != null && !entityData.isEmpty()) {
                EntityType.updateCustomEntityTag(level, null, entity, entityData);
            }
        }
        return entity;
    }
}
