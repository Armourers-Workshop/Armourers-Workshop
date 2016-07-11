package riskyken.armourersWorkshop.common.blocks;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.common.items.block.ModItemBlockWithMetadata;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;
import riskyken.armourersWorkshop.utils.UtilBlocks;

public class BlockSkinLibrary extends AbstractModBlockContainer {
    
    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.<EnumType>create("type", EnumType.class);
    
    public BlockSkinLibrary() {
        super(LibBlockNames.ARMOUR_LIBRARY);
    }
    
    @Override
    protected ItemBlock getItemBlock() {
        return new ModItemBlockWithMetadata(this);
    }
    
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        UtilBlocks.dropInventoryBlocks(worldIn, pos);
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
            EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, ArmourersWorkshop.instance, LibGuiIds.ARMOUR_LIBRARY, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinLibrary();
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {TYPE});
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(TYPE) == EnumType.CREATIVE) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        switch (meta) {
        case 0:
            return this.blockState.getBaseState().withProperty(TYPE, EnumType.NORMAL);
        case 1:
            return this.blockState.getBaseState().withProperty(TYPE, EnumType.CREATIVE);
        default:
            return getDefaultState();
        }
    }
    
    public static enum EnumType implements IStringSerializable {
        NORMAL("normal"),
        CREATIVE("creative");

        private final String name;
        
        private EnumType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
    }
}
