package riskyken.armourersWorkshop.common.tileentities;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.blocks.BlockSkinnable;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.inventory.ModInventory;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.skin.cache.CommonSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.ModConstants;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class TileEntitySkinnable extends TileEntity {

    private static final String TAG_HAS_SKIN = "hasSkin";
    private static final String TAG_RELATED_BLOCKS = "relatedBlocks";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_BLOCK_INVENTORY = "blockInventory";
    private static final int NBT_VERSION = 1;

    private int nbtVersion;
    private SkinPointer skinPointer;
    private boolean haveBlockBounds = false;
    private ArrayList<BlockLocation> relatedBlocks;
    private boolean bedOccupied;
    private ModInventory inventory;
    private boolean blockInventory;
    
    @SideOnly(Side.CLIENT)
    private AxisAlignedBB renderBounds;
    
    //Bounds
    public float minX;
    public float minY;
    public float minZ;
    public float maxX;
    public float maxY;
    public float maxZ;

    public boolean hasSkin() {
        return skinPointer != null;
    }

    public SkinPointer getSkinPointer() {
        return skinPointer;
    }

    public void setSkinPointer(Skin skin, SkinPointer skinPointer) {
        this.skinPointer = skinPointer;
        if (skin != null & isParent()) {
            if (skin.getProperties().getPropertyBoolean(Skin.KEY_BLOCK_INVENTORY, false)) {
                blockInventory = true;
                inventory = new ModInventory(LibBlockNames.SKINNABLE, 36, this);
            }
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        haveBlockBounds = false;
    }

    public void setBoundsOnBlock(Block block, int xOffset, int yOffset, int zOffset) {
        if (haveBlockBounds) {
            //TODO change before release!!!
            //block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
            //return;
        }
        
        if (block != null && !(block instanceof BlockSkinnable)) {
            ModLogger.log(Level.ERROR, String.format("Tile entity at X:%d Y:%d Z:%d has an invalid block.", xOffset, yOffset, zOffset));
            if (worldObj != null) {
                worldObj.removeTileEntity(xOffset, yOffset, zOffset);
            }
            return;
        }
        
        if (hasSkin()) {
            BlockSkinnable blockSkinnable = (BlockSkinnable) block;
            Skin skin = null;
            skin = getSkin(skinPointer);
            if (skin != null) {
                ForgeDirection dir = blockSkinnable.getFacingDirection(getBlockMetadata());
                float[] bounds = getBlockBounds(skin, xOffset, yOffset, zOffset, dir);
                if (bounds != null) {
                    minX = bounds[0];
                    minY = bounds[1];
                    minZ = bounds[2];
                    maxX = bounds[3];
                    maxY = bounds[4];
                    maxZ = bounds[5];
                    haveBlockBounds = true;
                    block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                }
                return;
            }
        }
        if (haveBlockBounds) {
            block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        } else {
            block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }
    }
    
    public static float[] getBlockBounds(Skin skin, int gridX, int gridY, int gridZ, ForgeDirection dir) {
        float[] bounds = new float[6];
        float scale = 0.0625F;
        SkinPart skinPart = skin.getParts().get(0);
        
        gridX = MathHelper.clamp_int(gridX, 0, 2);
        gridY = MathHelper.clamp_int(gridY, 0, 2);
        gridZ = MathHelper.clamp_int(gridZ, 0, 2);
        
        Rectangle3D rec = skinPart.getBlockBounds(gridX, gridY, gridZ);
        switch (dir) {
        case NORTH:
            //rec = skinPart.getBlockBounds(gridX, gridY, 2 - gridZ);
            break;
        case EAST:
            rec = skinPart.getBlockBounds(2 - gridZ, gridY, gridX);
            break;
        case SOUTH:
            rec = skinPart.getBlockBounds(2 - gridX, gridY, 2 - gridZ);
            break; 
        case WEST:
            rec = skinPart.getBlockBounds(gridZ, gridY, 2 - gridX);
            break;
        default:
            break;
        }
        
        if (rec != null) {
            int x = 8 + rec.getX();
            int y = 8 - rec.getHeight() - rec.getY();
            int z = 8 - rec.getDepth() - rec.getZ();
            bounds[0] = x * scale;
            bounds[1] = y * scale;
            bounds[2] = z * scale;
            bounds[3] = (x + rec.getWidth()) * scale;
            bounds[4] = (y + rec.getHeight()) * scale;
            bounds[5] = (z + rec.getDepth()) * scale;
            bounds = rotateBlockBounds(bounds, dir);
        } else {
            return null;
        }
        
        return bounds;
    }
    
    private static float[] rotateBlockBounds(float[] bounds, ForgeDirection dir) {
        float[] rotatedBounds = new float[6];
        for (int i = 0; i < bounds.length; i++) {
            rotatedBounds[i] = bounds[i];
        }
        switch (dir) {
        case NORTH:
            rotatedBounds[0] = 1 - bounds[3];   //oldMaxZ - minX
            rotatedBounds[2] = 1 - bounds[5];   //oldMinX - minZ
            rotatedBounds[3] = 1 - bounds[0];   //oldMinZ - maxX
            rotatedBounds[5] = 1 - bounds[2];   //oldMaxX - maxZ
            break;
        case EAST:
            rotatedBounds[0] = bounds[2];       //oldMinZ - minX
            rotatedBounds[2] = 1 - bounds[3];   //oldMaxX - minZ
            rotatedBounds[3] = bounds[5];       //oldMaxZ - maxX
            rotatedBounds[5] = 1 - bounds[0];   //oldMinX - maxZ
            break;
        case WEST:
            rotatedBounds[0] = 1 - bounds[5];   //oldMaxZ - minX
            rotatedBounds[2] = bounds[0];       //oldMinX - minZ
            rotatedBounds[3] = 1 - bounds[2];   //oldMinZ - maxX
            rotatedBounds[5] = bounds[3];       //oldMaxX - maxZ
            break; 
        default:
            break;
        }
        
        return rotatedBounds;
    }
    
    public boolean isBedOccupied() {
        return bedOccupied;
    }
    
    public void setBedOccupied(boolean bedOccupied) {
        this.bedOccupied = bedOccupied;
    }
    
    private Skin getSkin(ISkinPointer skinPointer) {
        if (getWorldObj().isRemote) {
            return getSkinClient(skinPointer);
        } else {
            return getSkinServer(skinPointer);
        }
    }

    @SideOnly(Side.CLIENT)
    private Skin getSkinClient(ISkinPointer skinPointer) {
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
    }

    private Skin getSkinServer(ISkinPointer skinPointer) {
        return CommonSkinCache.INSTANCE.softGetSkin(skinPointer.getSkinId());
    }
    
    public void setRelatedBlocks(ArrayList<BlockLocation> relatedBlocks) {
        this.relatedBlocks = relatedBlocks;
    }
    
    public ArrayList<BlockLocation> getRelatedBlocks() {
        return relatedBlocks;
    }
    
    public TileEntitySkinnable getParent() {
        return this;
    }
    
    public boolean isParent() {
        return this.getClass() == TileEntitySkinnable.class;
    }
    
    public ModInventory getInventory() {
        return inventory;
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.func_148857_g();
        readFromNBT(compound);
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    public void setRotation(ForgeDirection rotation) {
        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, rotation.ordinal(), 2);
    }
    
    public ForgeDirection getRotation() {
        if (getBlockType() instanceof BlockSkinnable) {
            return ((BlockSkinnable)getBlockType()).getFacingDirection(getWorldObj(), xCoord, yCoord, zCoord);
        }
        return ForgeDirection.UNKNOWN;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(TAG_HAS_SKIN, hasSkin());
        compound.setInteger(ModConstants.Tags.TAG_NBT_VERSION, NBT_VERSION);
        if (hasSkin()) {
            skinPointer.writeToCompound(compound);
            if (relatedBlocks != null) {
                NBTTagList list = new NBTTagList();
                for (int i = 0; i < relatedBlocks.size(); i++) {
                    NBTTagCompound blockCompound = new NBTTagCompound();
                    BlockLocation blockLoc = relatedBlocks.get(i);
                    blockCompound.setInteger(TAG_X, blockLoc.x);
                    blockCompound.setInteger(TAG_Y, blockLoc.y);
                    blockCompound.setInteger(TAG_Z, blockLoc.z);
                    list.appendTag(blockCompound);
                }
                compound.setTag(TAG_RELATED_BLOCKS, list);
            }
            if (isParent() & blockInventory & inventory != null) {
                compound.setBoolean(TAG_BLOCK_INVENTORY, true);
                inventory.saveItemsToNBT(compound);
            } else {
                compound.setBoolean(TAG_BLOCK_INVENTORY, false);
            }
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        boolean hasSkin = compound.getBoolean(TAG_HAS_SKIN);
        nbtVersion = 0;
        if (compound.hasKey(ModConstants.Tags.TAG_NBT_VERSION, Constants.NBT.TAG_INT)) {
            nbtVersion = compound.getInteger(ModConstants.Tags.TAG_NBT_VERSION);
        }
        if (hasSkin) {
            skinPointer = new SkinPointer();
            skinPointer.readFromCompound(compound);
            if (compound.hasKey(TAG_RELATED_BLOCKS, Constants.NBT.TAG_LIST)) {
                NBTTagList list = compound.getTagList(TAG_RELATED_BLOCKS, Constants.NBT.TAG_COMPOUND);
                relatedBlocks = new ArrayList<BlockLocation>();
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound blockCompound = (NBTTagCompound)list.getCompoundTagAt(i);
                    int x = blockCompound.getInteger(TAG_X);
                    int y = blockCompound.getInteger(TAG_Y);
                    int z = blockCompound.getInteger(TAG_Z);
                    BlockLocation blockLoc = new BlockLocation(x, y, z);
                    relatedBlocks.add(blockLoc);
                }
            } else {
                relatedBlocks = null;
            }
            blockInventory = false;
            inventory = null;
            if (isParent()) {
                if (compound.hasKey(TAG_BLOCK_INVENTORY, Constants.NBT.TAG_BYTE) && compound.getBoolean(TAG_BLOCK_INVENTORY)) {
                    blockInventory = true;
                    inventory = new ModInventory(LibBlockNames.SKINNABLE, 36, this);
                    inventory.loadItemsFromNBT(compound);
                }
            }
        } else {
            skinPointer = null;
            relatedBlocks = null;
            ModLogger.log(Level.WARN, String.format("Skinnable tile at X:%d Y:%d Z:%d has no skin data.", xCoord, yCoord, zCoord));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (renderBounds == null) {
            if (hasSkin()) {
                Skin skin = getSkin(getSkinPointer());
                if (skin != null) {
                    if (skin.getProperties().getPropertyBoolean(Skin.KEY_BLOCK_MULTIBLOCK, false)) {
                        renderBounds = AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
                        ForgeDirection dir = getRotation().getOpposite();
                        renderBounds.offset(dir.offsetX, 0, dir.offsetZ);
                    } else {
                        renderBounds = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
                    }
                } else {
                    return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
                } 
            } else {
                return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
            }
        }
        return renderBounds;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return ConfigHandlerClient.blockSkinMaxRenderDistance;
    }

    public void killChildren(World world) {
        if (relatedBlocks != null) {
            for (int i = 0; i < relatedBlocks.size(); i++) {
                BlockLocation loc = relatedBlocks.get(i);
                if (!(xCoord == loc.x & yCoord == loc.y & zCoord == loc.z)) {
                    ModLogger.log("Removing child: " + loc.toString());
                    world.setBlockToAir(loc.x, loc.y, loc.z);
                    world.removeTileEntity(loc.x, loc.y, loc.z);
                } else {
                    ModLogger.log("Skipping child: " + loc.toString());
                }
            }
        }
    }

    public boolean hasCustomName() {
        if (hasSkin()) {
            Skin skin = getSkin(getSkinPointer());
            if (skin != null) {
                return !StringUtils.isNullOrEmpty(skin.getCustomName());
            }
        }
        return false;
    }

    public String getCustomName() {
        if (hasSkin()) {
            Skin skin = getSkin(getSkinPointer());
            if (skin != null) {
                return skin.getCustomName();
            }
        }
        return "";
    } 
}
