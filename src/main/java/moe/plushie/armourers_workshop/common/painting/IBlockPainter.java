package moe.plushie.armourers_workshop.common.painting;

import moe.plushie.armourers_workshop.common.blocks.BlockLocation;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IBlockPainter {
    
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side);
}
