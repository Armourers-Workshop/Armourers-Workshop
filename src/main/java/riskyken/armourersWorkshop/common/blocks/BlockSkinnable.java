package riskyken.armourersWorkshop.common.blocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.SkinUtils;
import riskyken.armourersWorkshop.utils.UtilItems;

public class BlockSkinnable extends AbstractModBlockContainer {

    public BlockSkinnable() {
        this(LibBlockNames.SKINNABLE);
        
    }
    
    public BlockSkinnable(String name) {
        super(name, Material.IRON, SoundType.METAL, false);
        setLightOpacity(0);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        /*
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            TileEntitySkinnable tes = (TileEntitySkinnable) te;
            tes.setBoundsOnBlock(this);
            return;
        }
        setBlockBounds(0, 0, 0, 1, 1, 1);
        */
        return super.getBoundingBox(state, source, pos);
    }
    
    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                Skin skin = SkinUtils.getSkinDetectSide(skinPointer, true, true);
                if (skin != null) {
                    return skin.getProperties().getPropertyBoolean(Skin.KEY_BLOCK_LADDER, false);
                }
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at %s had no skin data.", pos.toString()));
            }
        }
        return false;
    }
    
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                ItemStack returnStack = new ItemStack(ModItems.equipmentSkin, 1);
                SkinNBTHelper.addSkinDataToStack(returnStack, skinPointer);
                return returnStack;
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at %s had no skin data.", pos.toString()));
            }
        }
        return null;
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return  new ArrayList<ItemStack>();
    }
    
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!player.capabilities.isCreativeMode) {
            dropSkin(world, pos);
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    private void dropSkin(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                ItemStack skinStack = new ItemStack(ModItems.equipmentSkin, 1);
                SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
                UtilItems.spawnItemInWorld(world, pos, skinStack);
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at %s had no skin data.", pos.toString()));
            }
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnable();
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
    
    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        // TODO Auto-generated method stub
        return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        /*
        if (world.isRemote) {
            return false;
        }
        int rotation = world.getBlockMetadata(x, y, z);
        rotation++;
        if (rotation > 3) {
            rotation = 0;
        }
        world.setBlockMetadataWithNotify(x, y, z, rotation, 2);
        */
        return true;
    }
    
}
