package moe.plushie.armourers_workshop.compat.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[16, 26)")
@Extension
public class SpawnItemExt {

    @Nullable
    public static ItemEntity spawnAtLocation(@This Entity entity, ServerLevel level, ItemStack itemStack) {
        return entity.spawnAtLocation(itemStack);
    }
}
