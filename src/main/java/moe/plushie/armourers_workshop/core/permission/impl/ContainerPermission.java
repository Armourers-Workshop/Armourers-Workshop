package moe.plushie.armourers_workshop.core.permission.impl;

import moe.plushie.armourers_workshop.core.permission.Permission;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.TargetContext;

public class ContainerPermission extends Permission {

    public ContainerPermission(String name) {
        super(name);
        ModContainerTypes.forEach(this::add);
    }

    public boolean accept(ContainerType<?> type, Entity target, PlayerEntity player) {
        TargetContext context = new TargetContext(player, target);
        return eval(get(type), player, context);
    }

    public boolean accept(ContainerType<?> type, World world, BlockPos pos, PlayerEntity player) {
        BlockPosContext context = new BlockPosContext(player, pos, world.getBlockState(pos), null);
        return eval(get(type), player, context);
    }
}
