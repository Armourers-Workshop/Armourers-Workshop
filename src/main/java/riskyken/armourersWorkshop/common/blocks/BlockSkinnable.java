package riskyken.armourersWorkshop.common.blocks;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.client.lib.LibBlockResources;
import riskyken.armourersWorkshop.common.items.ItemDebugTool.IDebug;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.items.block.ModItemBlock;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.utils.BlockUtils;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.SkinUtils;
import riskyken.armourersWorkshop.utils.UtilItems;

public class BlockSkinnable extends AbstractModBlockContainer implements IDebug {

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
    public boolean canDropFromExplosion(Explosion explosion) {
        return false;
    }
    
    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        if (!world.isRemote) {
            TileEntitySkinnable te = getTileEntity(world, x, y, z);
            if (te != null && te.getInventory() != null) {
                for (EntityPlayer p : te.getViewers()) {
                    p.closeScreen();
                }
                te.getViewers().clear();
                BlockUtils.dropInventoryBlocks(world, te.getInventory(), x, y, z);
            }
            dropSkin(world, x, y, z, false);
        }
        world.removeTileEntity(x, y, z);
        super.onBlockExploded(world, x, y, z, explosion);
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te != null && te.getInventory() != null) {
            for (EntityPlayer p : te.getViewers()) {
                p.closeScreen();
            }
            te.getViewers().clear();
            BlockUtils.dropInventoryBlocks(world, te.getInventory(), x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xHit, float yHit, float zHit) {
        Skin skin = getSkin(world, x, y, z);
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te == null) {
            return false;
        }
        TileEntitySkinnable parentTe = te.getParent();
        if (parentTe == null) {
            return false;
        }
        if (parentTe.hasLinkedBlock()) {
            BlockLocation loc = parentTe.getLinkedBlock();
            Block block = world.getBlock(loc.x, loc.y, loc.z);
            if (!(block instanceof BlockSkinnable)) {
                return block.onBlockActivated(world, loc.x, loc.y, loc.z, player, side, xHit, yHit, zHit);
            }
        }
        
        if (skin == null) {
            return false;
        }
        
        if (SkinProperties.PROP_BLOCK_SEAT.getValue(skin.getProperties())) {
            return sitOnSeat(world, parentTe.xCoord, parentTe.yCoord, parentTe.zCoord, player, skin);
        }
        if (SkinProperties.PROP_BLOCK_BED.getValue(skin.getProperties())) {
            //return sleepInBed(world, parentTe.xCoord, parentTe.yCoord, parentTe.zCoord, player, skin, te.getRotation(), te);
        }
        if (SkinProperties.PROP_BLOCK_INVENTORY.getValue(skin.getProperties()) | SkinProperties.PROP_BLOCK_ENDER_INVENTORY.getValue(skin.getProperties())) {
            if (!world.isRemote) {
                te.getViewers().add(player);
                FMLNetworkHandler.openGui(player, ArmourersWorkshop.instance, LibGuiIds.SKINNABLE, world, parentTe.xCoord, parentTe.yCoord, parentTe.zCoord);
            }
            return true;
        }
        return false;
    }
    
    private boolean sitOnSeat(World world, int x, int y, int z, EntityPlayer player, Skin skin) {
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
        return false;
    }
    
    private boolean sleepInBed(World world, int x, int y, int z, EntityPlayer player, Skin skin, ForgeDirection direction, TileEntitySkinnable tileEntity) {
        if (world.isRemote) {
            return true;
        }
        
        Point3D point = null;
        if (skin.getParts().get(0).getMarkerCount() > 0) {
            point = skin.getParts().get(0).getMarker(0);
        } else {
            point = new Point3D(0, 0, 16);
        }
        
        int xBlockOffset = MathHelper.floor_double(((double)point.getX() + 8) / 16D);
        int zBlockOffset = MathHelper.floor_double(((double)point.getZ() + 8) / 16D);
        
        int xOffset = (point.getX() + 8) - x * 16;
        int zOffset = (point.getY() + 8) - y * 16;
        
        x -= xBlockOffset * direction.offsetZ + zBlockOffset * direction.offsetX;;
        z -= zBlockOffset * direction.offsetZ + xBlockOffset * direction.offsetX;
        
        float scale = 1F / 16F;
        
        EntityPlayer.EnumStatus enumstatus = player.sleepInBedAt(x, y, z);

        if (enumstatus == EntityPlayer.EnumStatus.OK) {
            tileEntity.setBedOccupied(true);
            ModLogger.log("sleeping!");
            
            player.field_71079_bU = 0.0F;
            player.field_71089_bV = 0.0F;
            
            player.setPosition(x + 10, y - 0.5F, z + 1);
            
            player.playerLocation = new ChunkCoordinates(x, y, z);
            world.updateAllPlayersSleepingFlag();
            
            return true;
        } else {
            if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW) {
                player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
            } else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE) {
                player.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
            }
            return true;
        }
    }
    /*
    private EntityPlayer.EnumStatus putPlayerToSleep(EntityPlayer player, int x, int y, int z) {
        PlayerSleepInBedEvent event = new PlayerSleepInBedEvent(player, x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.result != null) {
            return event.result;
        }
        
        if (!player.getEntityWorld().isRemote) {
            if (player.isPlayerSleeping() || !player.isEntityAlive()) {
                return EntityPlayer.EnumStatus.OTHER_PROBLEM;
            }
            if (!player.getEntityWorld().provider.isSurfaceWorld()) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_HERE;
            }
            if (player.getEntityWorld().isDaytime()) {
                return EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW;
            }
            if (Math.abs(player.posX - (double)x) > 3.0D || Math.abs(player.posY - (double)y) > 2.0D || Math.abs(player.posZ - (double)z) > 3.0D) {
                return EntityPlayer.EnumStatus.TOO_FAR_AWAY;
            }
            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = player.getEntityWorld().getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox((double)x - d0, (double)y - d1, (double)z - d0, (double)x + d0, (double)y + d1, (double)z + d0));
            if (!list.isEmpty()) {
                return EntityPlayer.EnumStatus.NOT_SAFE;
            }
        }
        
        if (player.isRiding()) {
            player.mountEntity((Entity)null);
        }

        player.width = 0.2F;
        player.height = 0.2F;
        player.yOffset = 0.2F;
        
        if (player.getEntityWorld().blockExists(x, y, z)) {
            int l = player.getEntityWorld().getBlock(x, y, z).getBedDirection(player.getEntityWorld(), x, y, z);
            float f1 = 0.5F;
            float f = 0.5F;

            switch (l) {
                case 0:
                    f = 0.9F;
                    break;
                case 1:
                    f1 = 0.1F;
                    break;
                case 2:
                    f = 0.1F;
                    break;
                case 3:
                    f1 = 0.9F;
            }

            //player.func_71013_b(l);
            player.setPosition((double)((float)x + f1), (double)((float)y + 0.9375F), (double)((float)z + f));
        } else {
            player.setPosition((double)((float)x + 0.5F), (double)((float)y + 0.9375F), (double)((float)z + 0.5F));
        }

        //player.sleeping = true;
        //player.sleepTimer = 0;
        player.playerLocation = new ChunkCoordinates(x, y, z);
        player.motionX = player.motionZ = player.motionY = 0.0D;

        if (!player.getEntityWorld().isRemote) {
            player.getEntityWorld().updateAllPlayersSleepingFlag();
        }

        return EntityPlayer.EnumStatus.OK;
    }
    */
    @Override
    public boolean isBed(IBlockAccess world, int x, int y, int z, EntityLivingBase player) {
        Skin skin = getSkin(world, x, y, z);
        if (skin != null) {
            return SkinProperties.PROP_BLOCK_BED.getValue(skin.getProperties());
        }
        return false;
    }
    
    @Override
    public void setBedOccupied(IBlockAccess world, int x, int y, int z, EntityPlayer player, boolean occupied) {
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te != null) {
            te.setBedOccupied(occupied);
        }
    }
    
    @Override
    public int getBedDirection(IBlockAccess world, int x, int y, int z) {
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te != null) {
            switch (te.getRotation()) {
            case NORTH:
                return 0;
            case EAST:
                return 1;
            case SOUTH:
                return 2;
            case WEST:
                return 3;
            default:
                break;
            }
        }
        return 0;
    }
    
    @Override
    public boolean isBedFoot(IBlockAccess world, int x, int y, int z) {
        Skin skin = getSkin(world, x, y, z);
        if (skin != null) {
            for (int i = 0; i <skin.getPartCount(); i++) {
                SkinPart part = skin.getParts().get(i);
                part.getMarkerCount();
            }
        }
        return false;
    }
    
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
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        Skin skin = getSkin(world, x, y, z);
        if (skin != null) {
            if (SkinProperties.PROP_BLOCK_NO_COLLISION.getValue(skin.getProperties())) {
                return null;
            }
        }
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    @Override
    public boolean canCollideCheck(int meta, boolean holdingBoat) {
        if (!ArmourersWorkshop.isDedicated()) {
            return !checkCameraCollide();
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean checkCameraCollide() {
        if (Minecraft.getMinecraft().thePlayer != null) {
            if (!Minecraft.getMinecraft().thePlayer.isRiding()) {
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
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te != null) {
            ForgeDirection dir = getFacingDirection(world, x, y, z);
            if (dir == ForgeDirection.NORTH) {
                te.setBoundsOnBlock(this, 1, 0, 0);
                return;
            }
            if (dir == ForgeDirection.EAST) {
                te.setBoundsOnBlock(this, 0, 0, 1);
                return;
            }
            if (dir == ForgeDirection.SOUTH) {
                te.setBoundsOnBlock(this, 1, 0, 2);
                return;
            }
            if (dir == ForgeDirection.WEST) {
                te.setBoundsOnBlock(this, 2, 0, 1);
                return;
            }
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
    
    private TileEntitySkinnable getTileEntity(IBlockAccess world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntitySkinnable) {
            return (TileEntitySkinnable) te;
        } else {
            ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d has no tile entity.", x, y, z));
        }
        return null;
    }
    
    private SkinPointer getSkinPointer(IBlockAccess world, int x, int y, int z) {
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te != null) {
            return te.getSkinPointer();
        } else {
            ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d has no skin data.", x, y, z));
        }
        return null;
    }
    
    private Skin getSkin(IBlockAccess world, int x, int y, int z) {
        SkinPointer skinPointer = getSkinPointer(world, x, y, z);
        if (skinPointer != null) {
            return SkinUtils.getSkinDetectSide(skinPointer, true, true);
        }
        return null;
    }
    
    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        Skin skin = getSkin(world, x, y, z);
        if (skin != null) {
            return SkinProperties.PROP_BLOCK_LADDER.getValue(skin.getProperties());
        }
        return false;
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        SkinPointer skinPointer = getSkinPointer(world, x, y, z);
        if (skinPointer != null) {
            ItemStack returnStack = new ItemStack(ModItems.equipmentSkin, 1);
            SkinNBTHelper.addSkinDataToStack(returnStack, skinPointer);
            return returnStack;
        }
        return null;
    }
    
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        return new ArrayList<ItemStack>();
    }
    
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        dropSkin(world, x, y, z, player.capabilities.isCreativeMode);
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }
    
    private void dropSkin(World world, int x, int y, int z, boolean isCreativeMode) {
        TileEntitySkinnable te = getTileEntity(world, x, y, z);
        if (te != null) {
            SkinPointer skinPointer = te.getSkinPointer();
            if (skinPointer != null) {
                if (!isCreativeMode) {
                    ItemStack skinStack = new ItemStack(ModItems.equipmentSkin, 1);
                    SkinNBTHelper.addSkinDataToStack(skinStack, skinPointer);
                    UtilItems.spawnItemInWorld(world, x, y, z, skinStack);
                }
                te.killChildren(world);
            } else {
                ModLogger.log(Level.WARN, String.format("Block skin at x:%d y:%d z:%d had no skin data.", x, y, z));
            }
        }
    }
    
    public ForgeDirection getFacingDirection(IBlockAccess world, int x, int y, int z) {
        return getFacingDirection(world.getBlockMetadata(x, y, z));
    }
    
    public ForgeDirection getFacingDirection(int metadata) {
        return convertMetadataToDirection(metadata);
    }
    
    public void setFacingDirection(World world, int x, int y, int z, ForgeDirection direction) {
        setFacingDirection(world, x, y, z, direction.ordinal());
    }
    
    public void setFacingDirection(World world, int x, int y, int z, int metadata) {
        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
    }
    
    public int convertDirectionToMetadata(ForgeDirection direction) {
        int meta = direction.ordinal();
        return meta == 2 ? 4 : (meta == 3 ? 2 : (meta == 4 ? 3 : (meta == 5 ? 5 : 2)));
    }
    
    public ForgeDirection convertMetadataToDirection(int metadata) {
        if (metadata == 5) {
            return ForgeDirection.EAST;
        }
        if (metadata == 4) {
            return ForgeDirection.NORTH;
        }
        if (metadata == 3) {
            return ForgeDirection.WEST;
        }
        if (metadata == 2) {
            return ForgeDirection.SOUTH;
        }
        return ForgeDirection.EAST;
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
        Skin skin = getSkin(world, x, y, z);
        if (skin == null) {
            return false;
        }
        
        if (SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skin.getProperties())) {
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
    

    @Override
    public void getDebugHoverText(World world, int x, int y, int z, ArrayList<String> textLines) {
        textLines.add("Direction: " + getFacingDirection(world, x, y, z));
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
        public void updateRiderPosition() {
            if (this.riddenByEntity != null) {
                ForgeDirection[] rotMatrix =  {ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST};
                float scale = 0.0625F;
                
                ForgeDirection dir = rotMatrix[rotation & 3];
                
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
