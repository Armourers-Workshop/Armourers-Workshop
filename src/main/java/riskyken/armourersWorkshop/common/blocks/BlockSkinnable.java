package riskyken.armourersWorkshop.common.blocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
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
    
    /*
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        
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
                        Point3D point = null;
                        if (skin.getParts().get(0).getMarkerCount() > 0) {
                            point = skin.getParts().get(0).getMarker(0);
                        } else {
                            point = new Point3D(0, 0, 0);
                        }
                        int rotation = world.getBlockMetadata(x, y, z);
                        skin.getParts().get(0).getMarker(0);
                        Seat seat = new Seat(world, x, y, z, point, rotation);
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
>>>>>>> 1.7.10/develop
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            TileEntitySkinnable tes = (TileEntitySkinnable) te;
            tes.setBoundsOnBlock(this);
            return;
        }
        setBlockBounds(0, 0, 0, 1, 1, 1);
        
        return super.getBoundingBox(state, source, pos);
    }
    */

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
    
    public static class Seat extends Entity implements IEntityAdditionalSpawnData {

        private int noRiderTime = 0;
        private Point3D offset;
        private int rotation;
        
        public Seat(World world, int x, int y, int z, Point3D offset, int rotation) {
            super(world);
            setPosition(x, y, z);
            setSize(0F, 0F);
            this.offset = offset;
            this.rotation = rotation;
        }
        
        public Seat(World world) {
            super(world);
            setSize(0F, 0F);
            this.offset = new Point3D(0, 0, 0);
        }
        
        @Override
        protected void entityInit() {
        }
        
        @Override
        public void updatePassenger(Entity passenger) {
            if (this.isPassenger(passenger)) {
                EnumFacing[] rotMatrix =  {EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST};
                float scale = 0.0625F;
                
                EnumFacing dir = rotMatrix[rotation];
                
                float offsetX = (offset.getX() * scale) * dir.getFrontOffsetZ() + (-offset.getZ() * scale) * dir.getFrontOffsetX();
                float offsetY = offset.getY() * scale;
                float offsetZ = (-offset.getZ() * scale) * dir.getFrontOffsetZ() + (-offset.getX() * scale) * dir.getFrontOffsetX();
                
                passenger.setPosition(
                        this.posX + 0.5 - offsetX,
                        this.posY + passenger.getYOffset() + 0.5F - offsetY,
                        this.posZ + 0.5F - offsetZ);
            }
        }
        
        @Override
        public void onUpdate() {
            super.onUpdate();
            
            if (!(worldObj.getBlockState(getPosition()).getBlock() instanceof BlockSkinnable)) {
                setDead();
                return;
            }
            
            
            if (getPassengers().size() == 0) {
                noRiderTime++;
                if (noRiderTime > 1) {
                    setDead();
                }
            } else {
                List<Entity> passengers = getPassengers();
                for (int i = 0; i < passengers.size(); i++) {
                    if (passengers.get(i).isSneaking()) {
                        passengers.get(i).dismountRidingEntity();
                    }
                }
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

        @Override
        public void writeSpawnData(ByteBuf buf) {
            buf.writeInt(offset.getX());
            buf.writeInt(offset.getY());
            buf.writeInt(offset.getZ());
            buf.writeInt(rotation);
        }

        @Override
        public void readSpawnData(ByteBuf buf) {
            offset = new Point3D(buf.readInt(), buf.readInt(), buf.readInt());
            rotation = buf.readInt();
        }
    }
}
