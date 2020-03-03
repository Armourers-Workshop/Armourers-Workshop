package moe.plushie.armourers_workshop.common.init.items;

import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.init.blocks.BlockBoundingBox;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.sounds.ModSounds;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSoap extends AbstractModItem {

    public ItemSoap() {
        super(LibItemNames.SOAP);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() instanceof IPantableBlock) {
            IPantableBlock paintableBlock = (IPantableBlock) state.getBlock();
            // TODO This may make block sides transparent.
        }
        if (state.getBlock() == ModBlocks.BOUNDING_BOX) {
            BlockBoundingBox bb = (BlockBoundingBox) state.getBlock();
            if (!worldIn.isRemote) {
                bb.setColour(worldIn, pos, 0x00FFFFFF, facing);
                bb.setPaintType(worldIn, pos, PaintTypeRegistry.PAINT_TYPE_NONE, facing);
                worldIn.playSound(null, pos, ModSounds.PAINT, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.2F + 0.9F);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}
