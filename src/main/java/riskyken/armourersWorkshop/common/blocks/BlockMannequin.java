package riskyken.armourersWorkshop.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ItemBlockMannequin;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMannequin extends AbstractModBlock implements ITileEntityProvider {

    public BlockMannequin() {
        super(LibBlockNames.MANNEQUIN, Material.rock, soundTypeMetal);
        setLightOpacity(0);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ItemBlockMannequin.class, "block." + name);
        return super.setBlockName(name);
    }

    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityMannequin) {
            int l = MathHelper.floor_double((double)(player.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
            ((TileEntityMannequin)te).setRotation(l);
        }
        world.setBlock(x, y + 1, z, this, 1, 2);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibModInfo.ID + ":" + "colourable");
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            setBlockBounds(0, 0, 0, 1, 2, 1);
        } else {
            setBlockBounds(0, -1, 0, 1, 1, 1);
        }
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && (stack.getItem() == ModItems.equipmentSkin | stack.getItem() == Items.name_tag)) {
            if (world.isRemote) { return true; }
            int meta = world.getBlockMetadata(x, y, z);
            TileEntity te;
            if (meta == 0) {
                te = world.getTileEntity(x, y, z);
            } else {
                te = world.getTileEntity(x, y - 1, z);
            }
            
            if (te instanceof TileEntityMannequin) {
                if (stack.getItem() == ModItems.equipmentSkin) {
                    ((TileEntityMannequin)te).setEquipment(player.getCurrentEquippedItem());
                } else {
                    ((TileEntityMannequin)te).setOwner(player.getCurrentEquippedItem());
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0) {
            if (world.getBlock(x, y + 1, z) != ModBlocks.mannequin) {
                world.setBlockToAir(x, y, z);
            }
        } else {
            if (world.getBlock(x, y - 1, z) != ModBlocks.mannequin) {
                world.setBlockToAir(x, y, z);
            }
        }
    }
    
    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        if (meta == 0) {
            return 1;
        }
        return 1;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return null;
    }
    
    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == 0) {
            return new TileEntityMannequin();
        } else {
            return null;
        }
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean isNormalCube() {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }
}
