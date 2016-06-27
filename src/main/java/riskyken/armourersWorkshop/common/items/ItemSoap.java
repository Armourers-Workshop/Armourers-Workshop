package riskyken.armourersWorkshop.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.common.blocks.BlockBoundingBox;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.painting.PaintType;

public class ItemSoap extends AbstractModItem {

    public ItemSoap() {
        super(LibItemNames.SOAP);
    }
    
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn,
            BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        
        IBlockState blockState = worldIn.getBlockState(pos);
        
        if (blockState.getBlock() instanceof IPantableBlock) {
            IPantableBlock paintableBlock = (IPantableBlock) blockState.getBlock();
            //DOTO This may make block sides transparent.
        }
        if (blockState.getBlock() == ModBlocks.boundingBox) {
            BlockBoundingBox bb = (BlockBoundingBox) blockState.getBlock();
            if (!worldIn.isRemote) {
                bb.setColour(worldIn, pos, 0x00FFFFFF, facing);
                bb.setPaintType(worldIn, pos, PaintType.NONE, facing);
                //worldIn.playSoundEffect(bl.x + 0.5D, bl.y + 0.5D, bl.z + 0.5D, LibSounds.PAINT, 1.0F, world.rand().nextFloat() * 0.1F + 0.9F);
            }
            return EnumActionResult.PASS;
        }
        return EnumActionResult.FAIL;
    }
}
