package riskyken.armourersWorkshop.common.blocks;

import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityColourMixer;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.BlockUtils;

public class BlockColourMixer extends AbstractModBlockContainer {

    public BlockColourMixer() {
        super(LibBlockNames.COLOUR_MIXER);
        setSortPriority(124);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
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
    
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;
    @SideOnly(Side.CLIENT)
    private IIcon sideOverlayIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.COLOUR_MIXER_SIDE);
        topIcon = register.registerIcon(LibBlockResources.COLOUR_MIXER_TOP);
        bottomIcon = register.registerIcon(LibBlockResources.COLOUR_MIXER_BOTTOM);
        sideOverlayIcon = register.registerIcon(LibBlockResources.COLOUR_MIXER_SIDE_OVERLAY);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0) { return bottomIcon; }
        if (side == 1) { return topIcon; }
        
        if (ClientProxy.renderPass == 0) {
            return sideOverlayIcon;
        }
        
        return blockIcon;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        if (ClientProxy.renderPass == 0) {
            TileEntity te = blockAccess.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityColourMixer) {
                return ((TileEntityColourMixer)te).getColour(0);
            }
        }
        return 0xFFFFFFFF;
    }
    
    @Override
    public int getRenderType() {
        return ArmourersWorkshop.proxy.getBlockRenderType(this);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.COLOUR_MIXER, world, x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntityColourMixer();
    }
}
