package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.client.texture.PlayerTexture;
import riskyken.armourersWorkshop.common.data.TextureType;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.utils.BlockUtils;

public class BlockArmourer extends AbstractModBlockContainer {

    public BlockArmourer() {
        super(LibBlockNames.ARMOURER_BRAIN);
        setSortPriority(200);
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof TileEntityArmourer) {
                ForgeDirection direction = BlockUtils.determineDirectionSide(entity).getOpposite();
                ((TileEntityArmourer)te).setDirection(ForgeDirection.NORTH);
                if (!world.isRemote) {
                    ((TileEntityArmourer)te).setTexture(new PlayerTexture(player.getCommandSenderName(), TextureType.USER));
                    ((TileEntityArmourer)te).onPlaced();
                }
            }
        }
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        BlockUtils.dropInventoryBlocks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null & te instanceof TileEntityArmourer) {
            ((TileEntityArmourer)te).preRemove();
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        if (!player.canPlayerEdit(x, y, z, side, player.getCurrentEquippedItem())) {
            return false;
        }
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.ARMOURER, world, x, y, z);
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityArmourer();
    }
}
