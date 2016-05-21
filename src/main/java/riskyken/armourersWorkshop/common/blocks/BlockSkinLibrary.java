package riskyken.armourersWorkshop.common.blocks;

import java.util.List;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import riskyken.armourersWorkshop.utils.UtilBlocks;

public class BlockSkinLibrary extends AbstractModBlockContainer {

    public BlockSkinLibrary() {
        super(LibBlockNames.ARMOUR_LIBRARY);
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
        UtilBlocks.dropInventoryBlocks(world, x, y, z);
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
    private IIcon[] sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon[] bottomIcon;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_TOP);
        
        sideIcon = new IIcon[2];
        bottomIcon = new IIcon[2];
        sideIcon[0] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_0_SIDE);
        bottomIcon[0] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_0_BOTTOM);
        sideIcon[1] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_1_SIDE);
        bottomIcon[1] = register.registerIcon(LibBlockResources.EQUIPMENT_LIBRARY_1_BOTTOM);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 1) {
            return blockIcon;
        }
        
        if (side == 0 & meta == 0) {
            return bottomIcon[0];
        }
        if (side == 0 & meta == 1) {
            return bottomIcon[1];
        }
        
        if (side > 1 & meta == 0) {
            return sideIcon[0];
        }
        if (side > 1 & meta == 1) {
            return sideIcon[1];
        }
        
        return null;
    }
    
    @Override
    public TileEntity getTileEntityCommon(World world, int metadata) {
        return new TileEntitySkinLibrary();
    }
}
