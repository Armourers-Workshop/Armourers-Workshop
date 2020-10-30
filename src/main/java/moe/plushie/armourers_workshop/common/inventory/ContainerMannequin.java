package moe.plushie.armourers_workshop.common.inventory;

import moe.plushie.armourers_workshop.common.init.blocks.BlockDoll;
import moe.plushie.armourers_workshop.common.init.blocks.BlockMannequin;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class ContainerMannequin extends ModTileContainer<TileEntityMannequin> implements IButtonPress {

    public ContainerMannequin(InventoryPlayer invPlayer, TileEntityMannequin tileEntity) {
        super(invPlayer, tileEntity);
    }

    @Override
    public void buttonPressed(EntityPlayerMP player, byte buttonId) {
        World world = getTileEntity().getWorld();
        if (buttonId == 0) {
            IBlockState blockState = world.getBlockState(getTileEntity().getPos());
            Block block = blockState.getBlock();
            if (block == ModBlocks.MANNEQUIN) {
                ((BlockMannequin)block).convertToEntity(world, getTileEntity().getPos());
            }
            if (block == ModBlocks.DOLL) {
                ((BlockDoll)block).convertToEntity(world, getTileEntity().getPos());
            }
        }
    }
}
