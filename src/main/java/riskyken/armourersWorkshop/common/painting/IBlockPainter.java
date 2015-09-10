package riskyken.armourersWorkshop.common.painting;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.plushieWrapper.common.world.BlockLocation;

public interface IBlockPainter {
    
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side);
}
