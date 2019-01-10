package moe.plushie.armourers_workshop.common.blocks;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.lib.LibGuiIds;
import moe.plushie.armourers_workshop.common.permission.Permission;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.utils.ModLogger;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class BlockSkinnable extends AbstractModBlockContainer {

    public static final PropertyDirection STATE_FACING = BlockHorizontal.FACING;
    
    public BlockSkinnable() {
        this(LibBlockNames.SKINNABLE);
    }
    
    public BlockSkinnable(String name) {
        super(name, Material.IRON, SoundType.METAL, false);
        setDefaultState(this.blockState.getBaseState().withProperty(STATE_FACING, EnumFacing.NORTH));
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {STATE_FACING});
    }
    
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
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
    
    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        if (!world.isRemote) {
            TileEntitySkinnable te = getTileEntity(world, pos);
            if (te != null && te.getInventory() != null) {
                BlockUtils.dropInventoryBlocks(world, te.getInventory(), pos);
            }
            dropSkin(world, pos, false);
        }
        world.removeTileEntity(pos);
        super.onBlockExploded(world, pos, explosion);
    }
    
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntitySkinnable te = getTileEntity(worldIn, pos);
        if (te != null && te.getInventory() != null) {
            te.killChildren(worldIn);
            BlockUtils.dropInventoryBlocks(worldIn, te.getInventory(), pos);
        }
        super.breakBlock(worldIn, pos, state);
    }
    
    @Override
    public void registerItemBlock(IForgeRegistry<Item> registry) {}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(STATE_FACING).build());
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntitySkinnable te = getTileEntity(worldIn, pos);
        if (te == null) {
            return false;
        }
        TileEntitySkinnable parentTe = te.getParent();
        if (parentTe == null) {
            return false;
        }
        if (parentTe.hasLinkedBlock()) {
            BlockPos posLinked = parentTe.getLinkedBlock();
            IBlockState stateLinked = worldIn.getBlockState(posLinked);
            if (!(stateLinked.getBlock() instanceof BlockSkinnable)) {
                return stateLinked.getBlock().onBlockActivated(worldIn, posLinked, stateLinked, playerIn, hand, facing, hitX, hitY, hitZ);
            }
        }
        Skin skin = getSkin(worldIn, pos);
        if (skin == null) {
            return false;
        }
        if (SkinProperties.PROP_BLOCK_SEAT.getValue(skin.getProperties())) {
            //return sitOnSeat(world, parentTe.xCoord, parentTe.yCoord, parentTe.zCoord, player, skin);
        }
        if (SkinProperties.PROP_BLOCK_BED.getValue(skin.getProperties())) {
            //return sleepInBed(world, parentTe.xCoord, parentTe.yCoord, parentTe.zCoord, player, skin, te.getRotation(), te);
        }
        if (SkinProperties.PROP_BLOCK_INVENTORY.getValue(skin.getProperties()) | SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skin.getProperties())) {
            openGui(playerIn, LibGuiIds.SKINNABLE, worldIn, parentTe.getPos(), state, facing);
            return true;
        }
        return false;
    }
    /*
    private boolean sitOnSeat(World world, BlockPos pos, EntityPlayer player, Skin skin) {
        List<Seat> seats = world.getEntitiesWithinAABB(Seat.class, new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
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
            world.spawnEntity(seat);
            player.mountEntity(seat);                   
            return true;
        }
        return false;
    }
    */
    /*
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity) {
        if (entity instanceof Seat) {
            return;
        }
        if (entity != null && entity instanceof EntityPlayer) {
            if (((EntityPlayer)entity).isPlayerSleeping()) {
                
                Skin skin = getSkin(world, x, y, z);
                if (skin != null) {
                    Point3D point = null;
                    if (skin.getParts().get(0).getMarkerCount() > 0) {
                        point = skin.getParts().get(0).getMarker(0);
                    } else {
                        point = new Point3D(0, 0, 16);
                    }
                    float scale = 1F / 16F;
                    //list.add(AxisAlignedBB.getBoundingBox(x, y, z, x + 1F, y + 0.5F + -point.getY() * scale, z + 1F));
                    //ModLogger.log(-point.getY() * scale);
                } else {
                    //list.add(AxisAlignedBB.getBoundingBox(x, y, z, x + 1F, y + 0.5F, z + 1F));
                }
                list.add(AxisAlignedBB.getBoundingBox(x, y, z, x + 1F, y + 0.5F, z + 1F));
                return;
            }
        }
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
    }
    */
    
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        Skin skin = getSkin(worldIn, pos);
        if (skin != null) {
            if (SkinProperties.PROP_BLOCK_NO_COLLISION.getValue(skin.getProperties())) {
                return NULL_AABB;
            }
        }
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
    }
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntitySkinnable te = getTileEntity(source, pos);
        if (te != null) {
            EnumFacing dir = state.getValue(STATE_FACING);
            if (dir == EnumFacing.NORTH) {
                return te.getBoundsForBlock(this, 1, 0 ,0);
            }
            if (dir == EnumFacing.EAST) {
                return te.getBoundsForBlock(this, 0, 0, 1);
            }
            if (dir == EnumFacing.SOUTH) {
                return te.getBoundsForBlock(this, 1, 0, 2);
            }
            if (dir == EnumFacing.WEST) {
                return te.getBoundsForBlock(this, 2, 0, 1);
            }
        }
        return FULL_BLOCK_AABB;
    }
    
    @Override
    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
        if (!ArmourersWorkshop.isDedicated()) {
            return !checkCameraCollide();
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean checkCameraCollide() {
        if (Minecraft.getMinecraft().player != null) {
            if (!Minecraft.getMinecraft().player.isRiding()) {
                return false;
            }
        }

        int renderCount = 0;
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stack.length; i++) {
            StackTraceElement element = stack[i];
            if (element.getClassName().equals(EntityRenderer.class.getName())) {
                renderCount++;
            }
            if (renderCount == 4) {
                return true;
            }
        }
        return false;
    }
    
    private TileEntitySkinnable getTileEntity(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntitySkinnable) {
            return (TileEntitySkinnable) te;
        } else {
            ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d has no tile entity.", pos.getX(), pos.getY(), pos.getZ()));
        }
        return null;
    }
    
    private SkinDescriptor getSkinPointer(IBlockAccess world, BlockPos pos) {
        TileEntitySkinnable te = getTileEntity(world, pos);
        if (te != null) {
            return (SkinDescriptor) te.getSkinPointer();
        } else {
            ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d has no skin data.", pos.getX(), pos.getY(), pos.getZ()));
        }
        return null;
    }
    
    private Skin getSkin(IBlockAccess world, BlockPos pos) {
        SkinDescriptor skinPointer = getSkinPointer(world, pos);
        if (skinPointer != null) {
            return SkinUtils.getSkinDetectSide(skinPointer, true, true);
        }
        return null;
    }
    
    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        Skin skin = getSkin(world, pos);
        if (skin != null) {
            return SkinProperties.PROP_BLOCK_LADDER.getValue(skin.getProperties());
        }
        return false;
    }
    
    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        SkinDescriptor skinPointer = getSkinPointer(world, pos);
        if (skinPointer != null) {
            ItemStack returnStack = new ItemStack(ModItems.skin, 1);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinPointer);
            return returnStack;
        }
        return ItemStack.EMPTY;
    }
    
    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    }
    
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        dropSkin(world, pos, player.capabilities.isCreativeMode);
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
    
    private void dropSkin(World world, BlockPos pos, boolean isCreativeMode) {
        TileEntitySkinnable te = getTileEntity(world, pos);
        if (te != null) {
            ISkinDescriptor descriptor = te.getSkinPointer();
            if (descriptor != null) {
                if (!isCreativeMode) {
                    ItemStack skinStack = new ItemStack(ModItems.skin, 1);
                    SkinNBTHelper.addSkinDataToStack(skinStack, (SkinDescriptor) descriptor);
                    UtilItems.spawnItemInWorld(world, pos, skinStack);
                }
                te.killChildren(world);
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d had no skin data.", pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int p_149915_2_) {
        return new TileEntitySkinnable();
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
    
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        Skin skin = getSkin(world, pos);
        if (skin != null) {
            if (!SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skin.getProperties())) {
                return super.rotateBlock(world, pos, axis);
            }
        }
        return false;
    }
    
    @Override
    public void getPermissions(ArrayList<Permission> permissions) {
        super.getPermissions(permissions);
        permissions.add(new Permission(getPermissionName() + ".sit", DefaultPermissionLevel.ALL));
        permissions.add(new Permission(getPermissionName() + ".sleep", DefaultPermissionLevel.ALL));
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
        /*
        @Override
        public void updateRiderPosition() {
            if (this.riddenByEntity != null) {
                EnumFacing[] rotMatrix =  {EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST};
                float scale = 0.0625F;
                
                EnumFacing dir = rotMatrix[rotation & 3];
                
                float offsetX = (offset.getX() * scale) * dir.offsetZ + (-offset.getZ() * scale) * dir.offsetX;
                float offsetY = offset.getY() * scale;
                float offsetZ = (-offset.getZ() * scale) * dir.offsetZ + (-offset.getX() * scale) * dir.offsetX;
                
                this.riddenByEntity.setPosition(
                        this.posX + 0.5 - offsetX,
                        this.posY + this.riddenByEntity.getYOffset() + 0.5F - offsetY,
                        this.posZ + 0.5F - offsetZ);
            }
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
        */
        @Override
        public void onCollideWithPlayer(EntityPlayer player) {
            if (player.getRidingEntity() != this) {
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
