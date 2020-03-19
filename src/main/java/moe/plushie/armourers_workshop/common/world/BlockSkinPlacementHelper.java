package moe.plushie.armourers_workshop.common.world;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.common.init.blocks.BlockSkinnable;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnableChild;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.PlayerUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class BlockSkinPlacementHelper {

    private BlockSkinPlacementHelper() {
    }
    
    public static boolean placeSkinAtLocation(World world, EntityPlayer player, EnumFacing sidePlacedOn, ItemStack stack, BlockPos pos, ISkinDescriptor descriptor) {
        if (descriptor != null && descriptor.getIdentifier().getSkinType() == SkinTypeRegistry.skinBlock) {
            Skin skin = SkinUtils.getSkinDetectSide(descriptor, false, true);
            if (skin != null) {
                IBlockState replaceBlock = world.getBlockState(pos.offset(sidePlacedOn));
                if (replaceBlock.getBlock().isReplaceable(world, pos.offset(sidePlacedOn))) {
                    return BlockSkinPlacementHelper.placeSkinAtLocation(world, player, sidePlacedOn, stack, pos.offset(sidePlacedOn), skin, descriptor);
                }
            }
        }
        return false;
    }
    
    public static boolean placeSkinAtLocation(World world, EntityPlayer player, EnumFacing sidePlacedOn, ItemStack stack, BlockPos pos, Skin skin, ISkinDescriptor descriptor) {
        if (!canPlaceSkinAtLocation(world, player, sidePlacedOn, stack, pos, descriptor)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        EnumFacing dir = PlayerUtils.getDirectionSide(player).getOpposite();
        
        BlockSkinnable targetBlock = (BlockSkinnable) ModBlocks.SKINNABLE;
        if (SkinProperties.PROP_BLOCK_GLOWING.getValue(skin.getProperties())) {
            targetBlock = (BlockSkinnable) ModBlocks.SKINNABLE_GLOWING;
        }
        
        boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skin.getProperties());
        ArrayList<BlockPos> relatedBlocks = new ArrayList<BlockPos>();
        if (multiblock) {
            if (!canPlaceChildren(world, player, sidePlacedOn, stack, pos, skin, descriptor, relatedBlocks)) {
                return false;
            }
            placeChildren(world, player, sidePlacedOn, pos, skin, descriptor, relatedBlocks);
        }
        
        IBlockState state = targetBlock.getDefaultState().withProperty(BlockSkinnable.STATE_FACING, dir);
        
        TileEntitySkinnable te = new TileEntitySkinnable();
        te.setSkinPointer(skin, descriptor);
        te.setRelatedBlocks(relatedBlocks);
        SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(state, pos, world).setTileEntity(te).setDelay(1));
        
        stack.shrink(1);
        
        SoundType soundType = state.getBlock().getSoundType(state, world, pos, player);
        world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        return true;
    }
    
    private static boolean canPlaceSkinAtLocation(World world, EntityPlayer player, EnumFacing sidePlacedOn, ItemStack stack, BlockPos pos, ISkinDescriptor descriptor) {
        if (!player.canPlayerEdit(pos, sidePlacedOn, stack)) {
            return false;
        }
        if (stack.getCount() == 0) {
            return false;
        }
        if (!world.isValid(pos)) {
            return false;
        }
        if (!world.mayPlace(world.getBlockState(pos).getBlock(), pos, true, sidePlacedOn, player)) {
            return false;
        }
        return true;
    }
    
    private static boolean canPlaceChildren(World world, EntityPlayer player, EnumFacing sidePlacedOn, ItemStack stack, BlockPos pos, Skin skin, ISkinDescriptor descriptor, ArrayList<BlockPos> relatedBlocks) {
        EnumFacing dir = PlayerUtils.getDirectionSide(player).getOpposite();
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
                    if (bounds != null) {
                        BlockPos childPos = pos.add(ix - 1 - dir.getXOffset() * 1, iy, iz - 1 - dir.getZOffset() * 1);
                        relatedBlocks.add(childPos);
                        
                        IBlockState replaceState = world.getBlockState(childPos);
                        if (!replaceState.getBlock().isReplaceable(world, childPos)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private static void placeChildren(World world, EntityPlayer player, EnumFacing sidePlacedOn, BlockPos pos, Skin skin, ISkinDescriptor descriptor, ArrayList<BlockPos> relatedBlocks) {
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    placeChild(world, player, sidePlacedOn, pos, ix, iy, iz, skin, descriptor, relatedBlocks);
                }
            }
        }
    }
    
    private static void placeChild(World world, EntityPlayer player, EnumFacing sidePlacedOn, BlockPos pos, int ix, int iy, int iz, Skin skin, ISkinDescriptor descriptor, ArrayList<BlockPos> relatedBlocks) {
        EnumFacing dir = PlayerUtils.getDirectionSide(player).getOpposite();
        
        BlockSkinnable targetBlock = (BlockSkinnable) ModBlocks.SKINNABLE_CHILD;
        
        IBlockState state = targetBlock.getDefaultState().withProperty(BlockSkinnable.STATE_FACING, dir);
        if (SkinProperties.PROP_BLOCK_GLOWING.getValue(skin.getProperties())) {
            targetBlock = (BlockSkinnable) ModBlocks.SKINNABLE_CHILD_GLOWING;
        }
        
        float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
        if (bounds != null) {
            BlockPos childPos = pos.add(ix - 1 - dir.getXOffset() * 1, iy, iz - 1 - dir.getZOffset() * 1);
            world.setBlockState(childPos, state, 2);
            TileEntitySkinnableChild te = new TileEntitySkinnableChild();
            te.setSkinPointer(skin, descriptor);
            te.setParentLocation(pos);
            te.setRelatedBlocks(relatedBlocks);
            world.setTileEntity(childPos, te);
        }
    }
}
