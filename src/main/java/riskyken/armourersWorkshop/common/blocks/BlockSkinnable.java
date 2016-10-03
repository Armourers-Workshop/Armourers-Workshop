package riskyken.armourersWorkshop.common.blocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
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
        super(name, Material.iron, soundTypeMetal, false);
    }
    
    @Override
    public Block setBlockName(String name) {
        GameRegistry.registerBlock(this, ModItemBlock.class, "block." + name);
        return super.setBlockName(name);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            Skin skin = SkinUtils.getSkinDetectSide(((TileEntitySkinnable)te).getSkinPointer(), true, true);
            if (skin != null) {
                if (skin.getProperties().getPropertyBoolean(Skin.KEY_BLOCK_SEAT, false)) {
                    
                    
                    List<Seat> seats = world.getEntitiesWithinAABB(Seat.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
                    if (seats.size() == 0) {
                        Seat seat = new Seat(world, x, y, z);
                        world.spawnEntityInWorld(seat);
                        player.mountEntity(seat);
                                              
                        return true;
                    }
                    
                }
            }
        }
        return false;
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        if (entity instanceof Seat) {
            return;
        }
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            Skin skin = SkinUtils.getSkinDetectSide(((TileEntitySkinnable)te).getSkinPointer(), true, true);
            if (skin != null) {
                if (skin.getProperties().getPropertyBoolean(Skin.KEY_BLOCK_NO_COLLISION, false)) {
                    return null;
                }
            }
        }
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            TileEntitySkinnable tes = (TileEntitySkinnable) te;
            tes.setBoundsOnBlock(this);
            return;
        }
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon cubeIcon;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(LibBlockResources.SKINNABLE);
        cubeIcon = register.registerIcon(LibBlockResources.CUBE);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta == 4) {
            return cubeIcon;
        }
        return blockIcon;
    }
    
    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                Skin skin = SkinUtils.getSkinDetectSide(skinPointer, true, true);
                if (skin != null) {
                    return skin.getProperties().getPropertyBoolean(Skin.KEY_BLOCK_LADDER, false);
                }
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d had no skin data.", x, y, z));
            }
        }
        return false;
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                ItemStack returnStack = new ItemStack(ModItems.equipmentSkin, 1);
                SkinNBTHelper.addSkinDataToStack(returnStack, skinPointer);
                return returnStack;
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d had no skin data.", x, y, z));
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<ItemStack>();
    }
    
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (!player.capabilities.isCreativeMode) {
            dropSkin(world, x, y, z);
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
    
    private void dropSkin(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            SkinPointer skinPointer = ((TileEntitySkinnable)te).getSkinPointer();
            if (skinPointer != null) {
                ItemStack skinStack = new ItemStack(ModItems.equipmentSkin, 1);
                SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
                UtilItems.spawnItemInWorld(world, x, y, z, skinStack);
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d had no skin data.", x, y, z));
            }
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnable();
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
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        if (world.isRemote) {
            return false;
        }
        int rotation = world.getBlockMetadata(x, y, z);
        rotation++;
        if (rotation > 3) {
            rotation = 0;
        }
        world.setBlockMetadataWithNotify(x, y, z, rotation, 2);
        return true;
    }
    
    public static class Seat extends Entity {

        private int noRiderTime = 0;
        
        public Seat(World world) {
            super(world);
        }
        
        public Seat(World world, int x, int y, int z) {
            super(world);
            setPosition(x, y, z);
            setSize(0F, 0F);
        }
        
        @Override
        public double getMountedYOffset() {
            return 1;
        }
        
        @Override
        protected void entityInit() {
        }
        
        @Override
        public void onUpdate() {
            super.onUpdate();
            
            if (!(worldObj.getBlock((int)posX, (int)posY, (int)posZ) instanceof BlockSkinnable)) {
                setDead();
                return;
            }
            if (riddenByEntity == null) {
                noRiderTime++;
                if (noRiderTime > 1) {
                    setDead();
                }
            } else {
                if (riddenByEntity.isSneaking()) {
                    riddenByEntity.setPosition(posX + 0.5F, posY + 2, posZ + 0.5F);
                    setDead();
                }
            }
        }
        
        @Override
        public void onCollideWithPlayer(EntityPlayer player) {
            if (player.ridingEntity != this) {
                super.onCollideWithPlayer(player);
            }
        }
        
        @Override
        public boolean shouldRenderInPass(int pass) {
            return false;
        }

        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {
        }

        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {
        }
    }
}
