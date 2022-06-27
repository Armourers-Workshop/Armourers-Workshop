package moe.plushie.armourers_workshop.core.permission.impl;

import moe.plushie.armourers_workshop.core.permission.Permission;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.PlayerContext;

import java.util.Arrays;

public class BlockPermission extends Permission {

    public BlockPermission(String name, Block... blocks) {
        super(name);
        Arrays.stream(blocks).forEach(this::add);
    }

    public boolean accept(PlayerEntity player) {
        if (player == null) {
            return true;
        }
        PlayerContext context = new PlayerContext(player);
        return getNodes().stream().allMatch(node -> eval(node, player, context));
    }

    public boolean accept(TileEntity tileEntity, PlayerEntity player) {
        BlockState state = tileEntity.getBlockState();
        BlockPosContext context = new BlockPosContext(player, tileEntity.getBlockPos(), state, null);
        return eval(get(state.getBlock()), player, context);
    }
}
