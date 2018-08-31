package riskyken.armourers_workshop.common.painting;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourers_workshop.common.blocks.BlockLocation;

public interface IBlockPainter {
    
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side);
}
