package riskyken.armourersWorkshop.common.blocks;

import java.util.List;

import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.block.ModItemBlockWithMetadata;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;
import riskyken.armourersWorkshop.utils.BlockUtils;

public class BlockSkinLibrary extends AbstractModBlockContainer {

    public BlockSkinLibrary() {
        super(LibBlockNames.ARMOUR_LIBRARY);
        setSortPriority(198);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlockWithMetadata.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < 2; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @Override
    public int damageDropped(int meta) {
        return meta;
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        BlockUtils.dropInventoryBlocks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.ARMOUR_LIBRARY, world, x, y, z);
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon blockIcon2;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconTop;
    @SideOnly(Side.CLIENT)
    private IIcon[] iconBottom;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_0_SIDE);
        blockIcon2 = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_1_SIDE);
        
        iconTop = new IIcon[2];
        iconBottom = new IIcon[2];
        
        iconTop[0] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_0_TOP);
        iconBottom[0] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_0_BOTTOM);
        iconTop[1] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_1_TOP);
        iconBottom[1] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_1_BOTTOM);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side > 1) {
            if (meta == 0) {
                return blockIcon;
            } else {
                return blockIcon2;
            }
        }
        
        if (side == 0 & meta == 0) {
            return iconBottom[0];
        }
        if (side == 0 & meta == 1) {
            return iconBottom[1];
        }
        
        if (side == 1 & meta == 0) {
            return iconTop[0];
        }
        if (side == 1 & meta == 1) {
            return iconTop[1];
        }
        
        return null;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinLibrary();
    }
}
