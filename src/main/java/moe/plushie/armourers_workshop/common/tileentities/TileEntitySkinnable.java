package moe.plushie.armourers_workshop.common.tileentities;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.blocks.BlockLocation;
import moe.plushie.armourers_workshop.common.blocks.BlockSkinnable;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.inventory.ModInventory;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.utils.ModConstants;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySkinnable extends ModTileEntity {

    private static final String TAG_HAS_SKIN = "hasSkin";
    private static final String TAG_RELATED_BLOCKS = "relatedBlocks";
    private static final String TAG_X = "x";
    private static final String TAG_Y = "y";
    private static final String TAG_Z = "z";
    private static final String TAG_BLOCK_INVENTORY = "blockInventory";
    private static final String TAG_BLOCK_INVENTORY_SIZE = "blockInventorySize";
    private static final String TAG_LINKED_BLOCK = "linkedBlock";
    private static final int NBT_VERSION = 1;

    private int nbtVersion;
    private SkinDescriptor skinPointer;
    private boolean haveBlockBounds = false;
    private ArrayList<BlockLocation> relatedBlocks;
    private boolean bedOccupied;
    private ModInventory inventory;
    private boolean blockInventory;
    private BlockLocation linkedBlock = null;
    
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

    public SkinDescriptor getSkinPointer() {
        return skinPointer;
    }

    public void setSkinPointer(Skin skin, SkinDescriptor skinPointer) {
        this.skinPointer = skinPointer;
        if (skin != null & isParent()) {
            SkinProperties skinProps = skin.getProperties();
            if (SkinProperties.PROP_BLOCK_INVENTORY.getValue(skin.getProperties())) {
                blockInventory = true;
                int size = SkinProperties.PROP_BLOCK_INVENTORY_WIDTH.getValue(skin.getProperties()) * SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT.getValue(skin.getProperties());
                inventory = new ModInventory(LibBlockNames.SKINNABLE, size, this);
            }
        }
        dirtySync();
    }
    
    public BlockLocation getLinkedBlock() {
        return linkedBlock;
    }
    
    public boolean hasLinkedBlock() {
        return linkedBlock != null;
    }
    
    public void setLinkedBlock(BlockLocation linkedBlock) {
        this.linkedBlock = linkedBlock;
        dirtySync();
    }
    
    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        haveBlockBounds = false;
    }

    public void setBoundsOnBlock(Block block, BlockPos offset) {
        if (haveBlockBounds) {
            //TODO change before release!!!
            //block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
            //return;
        }
        
        if (block != null && !(block instanceof BlockSkinnable)) {
            ModLogger.log(Level.ERROR, String.format("Tile entity at X:%d Y:%d Z:%d has an invalid block.", offset.getX(), offset.getY(), offset.getZ()));
            if (getWorld() != null) {
                getWorld().removeTileEntity(offset);
            }
            return;
        }
        
        if (hasSkin()) {
            BlockSkinnable blockSkinnable = (BlockSkinnable) block;
            Skin skin = null;
            skin = getSkin(skinPointer);
            if (skin != null) {
                EnumFacing dir = blockSkinnable.getFacingDirection(getBlockMetadata());
                float[] bounds = getBlockBounds(skin, offset.getX(), offset.getY(), offset.getZ(), dir);
                if (bounds != null) {
                    minX = bounds[0];
                    minY = bounds[1];
                    minZ = bounds[2];
                    maxX = bounds[3];
                    maxY = bounds[4];
                    maxZ = bounds[5];
                    haveBlockBounds = true;
                    //block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
                }
                return;
            }
        }
        if (haveBlockBounds) {
            //block.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
        } else {
            //block.setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }
    }
    
    public static float[] getBlockBounds(Skin skin, int gridX, int gridY, int gridZ, EnumFacing dir) {
        float[] bounds = new float[6];
        float scale = 0.0625F;
        SkinPart skinPart = skin.getParts().get(0);
        
        gridX = MathHelper.clamp(gridX, 0, 2);
        gridY = MathHelper.clamp(gridY, 0, 2);
        gridZ = MathHelper.clamp(gridZ, 0, 2);
        
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
    
    private static float[] rotateBlockBounds(float[] bounds, EnumFacing dir) {
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
    
    public Skin getSkin(ISkinDescriptor skinPointer) {
        if (getWorld().isRemote) {
            return getSkinClient(skinPointer);
        } else {
            return getSkinServer(skinPointer);
        }
    }

    @SideOnly(Side.CLIENT)
    private Skin getSkinClient(ISkinDescriptor skinPointer) {
        return ClientSkinCache.INSTANCE.getSkin(skinPointer);
    }

    private Skin getSkinServer(ISkinDescriptor skinPointer) {
        return CommonSkinCache.INSTANCE.getSkin(skinPointer);
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        NBTTagCompound compound = packet.getNbtCompound();
        readFromNBT(compound);
    }
    
    public void setRotation(EnumFacing rotation) {
        //worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, rotation.ordinal(), 2);
    }
    
    public EnumFacing getRotation() {
        if (getBlockType() instanceof BlockSkinnable) {
            return ((BlockSkinnable)getBlockType()).getFacingDirection(getWorld(), getPos());
        }
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean(TAG_HAS_SKIN, hasSkin());
        compound.setInteger(ModConstants.Tags.TAG_NBT_VERSION, NBT_VERSION);
        if (hasLinkedBlock()) {
            compound.setIntArray(TAG_LINKED_BLOCK, new int[] {linkedBlock.x, linkedBlock.y, linkedBlock.z});
        }
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
                compound.setInteger(TAG_BLOCK_INVENTORY_SIZE, inventory.getSizeInventory());
                inventory.saveItemsToNBT(compound);
            } else {
                compound.setBoolean(TAG_BLOCK_INVENTORY, false);
            }
        }
        return compound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        boolean hasSkin = compound.getBoolean(TAG_HAS_SKIN);
        nbtVersion = 0;
        if (compound.hasKey(ModConstants.Tags.TAG_NBT_VERSION, Constants.NBT.TAG_INT)) {
            nbtVersion = compound.getInteger(ModConstants.Tags.TAG_NBT_VERSION);
        }
        if (compound.hasKey(TAG_LINKED_BLOCK, Constants.NBT.TAG_INT_ARRAY)) {
            int[] loc = compound.getIntArray(TAG_LINKED_BLOCK);
            linkedBlock = new BlockLocation(loc[0], loc[1], loc[2]);
        } else {
            linkedBlock = null;
        }
        if (hasSkin) {
            skinPointer = new SkinDescriptor();
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
                    int size = 36;
                    if (compound.hasKey(TAG_BLOCK_INVENTORY_SIZE, NBT.TAG_INT)) {
                        size = compound.getInteger(TAG_BLOCK_INVENTORY_SIZE);
                    }
                    blockInventory = true;
                    inventory = new ModInventory(LibBlockNames.SKINNABLE, size, this);
                    inventory.loadItemsFromNBT(compound);
                }
            }
        } else {
            skinPointer = null;
            relatedBlocks = null;
            ModLogger.log(Level.WARN, String.format("Skinnable tile at X:%d Y:%d Z:%d has no skin data.", getPos().getX(), getPos().getY(), getPos().getZ()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (renderBounds == null) {
            int xCoord = getPos().getX();
            int yCoord = getPos().getY();
            int zCoord = getPos().getZ();
            if (hasSkin()) {
                Skin skin = getSkin(getSkinPointer());
                if (skin != null) {
                    if (SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skin.getProperties())) {
                        renderBounds = new AxisAlignedBB(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
                        EnumFacing dir = getRotation().getOpposite();
                        renderBounds.offset(dir.getXOffset(), 0, dir.getZOffset());
                    } else {
                        renderBounds = new AxisAlignedBB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
                    }
                } else {
                    return new AxisAlignedBB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
                } 
            } else {
                return new AxisAlignedBB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
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
                /*
                if (!(xCoord == loc.x & yCoord == loc.y & zCoord == loc.z)) {
                    ModLogger.log("Removing child: " + loc.toString());
                    world.setBlockToAir(loc.x, loc.y, loc.z);
                    world.removeTileEntity(loc.x, loc.y, loc.z);
                } else {
                    ModLogger.log("Skipping child: " + loc.toString());
                }
                */
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
