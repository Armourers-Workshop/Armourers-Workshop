package moe.plushie.armourers_workshop.common.capability.paint;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPaintingTool {
    
    public void usedOnBlock(EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing facing);
}
