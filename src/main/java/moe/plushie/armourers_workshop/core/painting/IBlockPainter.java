package moe.plushie.armourers_workshop.core.painting;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockPainter {
    
    public void usedOnBlockSide(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Block block, Direction face, boolean spawnParticles);
}
