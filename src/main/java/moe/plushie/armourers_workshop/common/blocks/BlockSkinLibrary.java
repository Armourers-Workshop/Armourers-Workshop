package moe.plushie.armourers_workshop.common.blocks;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.items.block.ModItemBlockWithMetadata;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinLibrary;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockSkinLibrary extends AbstractModBlockContainer {

    public static final PropertyDirection STATE_FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<EnumLibraryType> STATE_TYPE = PropertyEnum.<EnumLibraryType>create("type", EnumLibraryType.class);

    public BlockSkinLibrary() {
        super(LibBlockNames.ARMOUR_LIBRARY);
        this.setDefaultState(this.blockState.getBaseState().withProperty(STATE_FACING, EnumFacing.NORTH).withProperty(STATE_TYPE, EnumLibraryType.NORMAL));
        setSortPriority(198);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { STATE_FACING, STATE_TYPE });
    }

    public IBlockState getStateFromMeta(int meta) {
        boolean typeBit = getBitBool(meta, 0);
        boolean northSouthBit = getBitBool(meta, 1);
        boolean posNegBit = getBitBool(meta, 2);
        EnumLibraryType type = EnumLibraryType.NORMAL;
        if (typeBit) {
            type = EnumLibraryType.CREATIVE;
        }
        EnumFacing facing = EnumFacing.EAST;
        if (northSouthBit) {
            if (posNegBit) {
                facing = EnumFacing.SOUTH;
            } else {
                facing = EnumFacing.NORTH;
            }
        } else {
            if (posNegBit) {
                facing = EnumFacing.EAST;
            } else {
                facing = EnumFacing.WEST;
            }
        }
        return this.getDefaultState().withProperty(STATE_FACING, facing).withProperty(STATE_TYPE, type);
    }

    public int getMetaFromState(IBlockState state) {
        EnumLibraryType type = state.getValue(STATE_TYPE);
        EnumFacing facing = state.getValue(STATE_FACING);
        int meta = setBit(0, 0, type == EnumLibraryType.CREATIVE);
        if (facing == EnumFacing.NORTH | facing == EnumFacing.SOUTH) {
            meta = setBit(meta, 1, true);
        }
        if (facing == EnumFacing.EAST | facing == EnumFacing.SOUTH) {
            meta = setBit(meta, 2, true);
        }
        return meta;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing enumfacing = placer.getHorizontalFacing().getOpposite();
        EnumLibraryType type = EnumLibraryType.NORMAL;
        if (placer.getHeldItem(hand).getMetadata() == 1) {
            type = EnumLibraryType.CREATIVE;
        }
        return getDefaultState().withProperty(STATE_FACING, enumfacing).withProperty(STATE_TYPE, type);
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    public int damageDropped(IBlockState state) {
        if (state.getValue(STATE_TYPE) == EnumLibraryType.CREATIVE) {
            return 1;
        }
        return 0;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {
        registry.register(new ModItemBlockWithMetadata(this).setRegistryName(getRegistryName()).setHasSubtypes(true));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory-normal"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "inventory-creative"));
    }

    /*
     * 
     * @Override public int damageDropped(int meta) { return meta; }
     * 
     * @Override public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
     * BlockUtils.dropInventoryBlocks(world, x, y,z);
     * super.breakBlock(world, x, y, z, block, meta);
     * }
     * 
     */

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.getInstance(), LibGuiIds.ARMOUR_LIBRARY, worldIn, pos.getX(), pos.getY(), pos.getZ()); 
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinLibrary();
    }

    public static enum EnumLibraryType implements IStringSerializable {
        NORMAL, CREATIVE;

        @Override
        public String getName() {
            return toString().toLowerCase();
        }
    }
}
