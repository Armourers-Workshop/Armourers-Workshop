package moe.plushie.armourers_workshop.common.init.blocks;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityOutfitMaker;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOutfitMaker extends AbstractModBlockContainer {

    public static final PropertyDirection STATE_FACING = BlockHorizontal.FACING;
    
    public BlockOutfitMaker() {
        super(LibBlockNames.OUTFIT_MAKER);
        setDefaultState(this.blockState.getBaseState().withProperty(STATE_FACING, EnumFacing.NORTH));
        setSortPriority(150);
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {STATE_FACING});
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        boolean northSouthBit = getBitBool(meta, 0);
        boolean posNegBit = getBitBool(meta, 1);
        EnumFacing facing = EnumFacing.EAST;
        if (northSouthBit) {
            if (posNegBit) { facing = EnumFacing.SOUTH; } else { facing = EnumFacing.NORTH; }
        } else {
            if (posNegBit) { facing = EnumFacing.EAST; } else { facing = EnumFacing.WEST; }
        }
        return this.getDefaultState().withProperty(STATE_FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(STATE_FACING);
        int meta = 0;
        if (facing == EnumFacing.NORTH | facing == EnumFacing.SOUTH) {
            meta = setBit(meta, 0, true);
        }
        if (facing == EnumFacing.EAST | facing == EnumFacing.SOUTH) {
            meta = setBit(meta, 1, true);
        }
        return meta;
    }
    
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing enumfacing = placer.getHorizontalFacing().getOpposite();
        return getDefaultState().withProperty(STATE_FACING, enumfacing);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.canPlayerEdit(pos, facing, playerIn.getHeldItem(hand))) {
            return false;
        }
        openGui(playerIn, EnumGuiId.OUTFIT_MAKER, worldIn, pos, state, facing);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityOutfitMaker();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        if (!ConfigHandlerClient.useClassicBlockModels) {
            return BlockRenderLayer.CUTOUT_MIPPED;
        } else {
            return BlockRenderLayer.SOLID;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        if (!ConfigHandlerClient.useClassicBlockModels) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"));
        } else {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-classic"), "normal"));
        }
    }
}
