package moe.plushie.armourers_workshop.utils;

import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DataFixerUtils {

    public static void move(Container inventory, int src, int dest, int size, String reason) {
        int changes = 0;
        for (int i = size - 1; i >= 0; --i) {
            ItemStack itemStack = inventory.getItem(src + i);
            if (!itemStack.isEmpty()) {
                inventory.setItem(src + i, ItemStack.EMPTY);
                inventory.setItem(dest + i, itemStack);
                changes += 1;
            }
        }
        if (changes != 0) {
            ModLog.info("move {} items from {} - {}, to {} - {}, reason: {}", changes, src, src + size, dest, dest + size, reason);
        }
    }

    public static boolean noBlockEntitiesAround(Entity entity) {
        if (entity.level instanceof ServerLevel) {
            // FIXME: @SAGESSE
            //#if MC < 11800
            ServerLevel level = (ServerLevel) entity.level;
            AABB alignedBB = entity.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D);
            return level.getEntityCollisions(entity, alignedBB, e -> e instanceof MannequinEntity).allMatch(VoxelShape::isEmpty);
            //#endif
        }
        return true;
    }
}
