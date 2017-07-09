package riskyken.armourersWorkshop.common.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityHologramProjector extends AbstractTileEntityInventory {

    private static final int INVENTORY_SIZE = 1;
    
    private static final String TAG_OFFSET_X = "offsetX";
    private static final String TAG_OFFSET_Y = "offsetY";
    private static final String TAG_OFFSET_Z = "offsetZ";

    private static final String TAG_ANGLE_X = "angleX";
    private static final String TAG_ANGLE_Y = "angleY";
    private static final String TAG_ANGLE_Z = "angleZ";
    
    private static final String TAG_ROTATION_OFFSET_X = "rotationOffsetX";
    private static final String TAG_ROTATION_OFFSET_Y = "rotationOffsetY";
    private static final String TAG_ROTATION_OFFSET_Z = "rotationOffsetZ";
    
    private static final String TAG_ROTATION_SPEED_X = "rotationSpeedX";
    private static final String TAG_ROTATION_SPEED_Y = "rotationSpeedY";
    private static final String TAG_ROTATION_SPEED_Z = "rotationSpeedZ";
    
    private int offsetX = 0;
    private int offsetY = 16;
    private int offsetZ = 0;
    
    private int angleX = 0;
    private int angleY = 0;
    private int angleZ = 0;
    
    private int rotationOffsetX = 0;
    private int rotationOffsetY = 0;
    private int rotationOffsetZ = 0;
    
    private int rotationSpeedX = 0;
    private int rotationSpeedY = 0;
    private int rotationSpeedZ = 0;
    
    private static boolean showRotationPoint;
    
    public TileEntityHologramProjector() {
        super(INVENTORY_SIZE);
    }

    @Override
    public String getInventoryName() {
        return LibBlockNames.HOLOGRAM_PROJECTOR;
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }
    
    public void setOffset(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setAngle(int x, int y, int z) {
        this.angleX = x;
        this.angleY = y;
        this.angleZ = z;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setRotationOffset(int x, int y, int z) {
        this.rotationOffsetX = x;
        this.rotationOffsetY = y;
        this.rotationOffsetZ = z;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setRotationSpeed(int x, int y, int z) {
        this.rotationSpeedX = x;
        this.rotationSpeedY = y;
        this.rotationSpeedZ = z;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public void setShowRotationPoint(boolean showRotationPoint) {
        this.showRotationPoint = showRotationPoint;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public int getOffsetX() {
        return offsetX;
    }
    
    public int getOffsetY() {
        return offsetY;
    }
    
    public int getOffsetZ() {
        return offsetZ;
    }
    
    public int getAngleX() {
        return angleX;
    }
    
    public int getAngleY() {
        return angleY;
    }
    
    public int getAngleZ() {
        return angleZ;
    }
    
    public int getRotationOffsetX() {
        return rotationOffsetX;
    }
    
    public int getRotationOffsetY() {
        return rotationOffsetY;
    }
    
    public int getRotationOffsetZ() {
        return rotationOffsetZ;
    }
    
    public int getRotationSpeedX() {
        return rotationSpeedX;
    }
    
    public int getRotationSpeedY() {
        return rotationSpeedY;
    }
    
    public int getRotationSpeedZ() {
        return rotationSpeedZ;
    }
    
    public boolean isShowRotationPoint() {
        return showRotationPoint;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_OFFSET_X, offsetX);
        compound.setInteger(TAG_OFFSET_Y, offsetY);
        compound.setInteger(TAG_OFFSET_Z, offsetZ);
        
        compound.setInteger(TAG_ANGLE_X, angleX);
        compound.setInteger(TAG_ANGLE_Y, angleY);
        compound.setInteger(TAG_ANGLE_Z, angleZ);
        
        compound.setInteger(TAG_ROTATION_OFFSET_X, rotationOffsetX);
        compound.setInteger(TAG_ROTATION_OFFSET_Y, rotationOffsetY);
        compound.setInteger(TAG_ROTATION_OFFSET_Z, rotationOffsetZ);
        
        compound.setInteger(TAG_ROTATION_SPEED_X, rotationSpeedX);
        compound.setInteger(TAG_ROTATION_SPEED_Y, rotationSpeedY);
        compound.setInteger(TAG_ROTATION_SPEED_Z, rotationSpeedZ);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        offsetX = readIntFromCompound(compound, TAG_OFFSET_X, 0);
        offsetY = readIntFromCompound(compound, TAG_OFFSET_Y, 16);
        offsetZ = readIntFromCompound(compound, TAG_OFFSET_Z, 0);
        
        angleX = readIntFromCompound(compound, TAG_ANGLE_X, 0);
        angleY = readIntFromCompound(compound, TAG_ANGLE_Y, 0);
        angleZ = readIntFromCompound(compound, TAG_ANGLE_Z, 0);
        
        rotationOffsetX = readIntFromCompound(compound, TAG_ROTATION_OFFSET_X, 0);
        rotationOffsetY = readIntFromCompound(compound, TAG_ROTATION_OFFSET_Y, 0);
        rotationOffsetZ = readIntFromCompound(compound, TAG_ROTATION_OFFSET_Z, 0);
        
        rotationSpeedX = readIntFromCompound(compound, TAG_ROTATION_SPEED_X, 0);
        rotationSpeedY = readIntFromCompound(compound, TAG_ROTATION_SPEED_Y, 0);
        rotationSpeedZ = readIntFromCompound(compound, TAG_ROTATION_SPEED_Z, 0);
    }
    
    private int readIntFromCompound(NBTTagCompound compound, String key, int defaultValue) {
        if (compound.hasKey(key, NBT.TAG_INT)) {
            return compound.getInteger(key);
        }
        return defaultValue;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(-2, -2, -2, 3, 3, 3);
        //bb = AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 1, 1);
        ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
        bb.offset(xCoord, yCoord, zCoord);
        
        float scale = 0.0625F;
        
        switch (dir) {
        case UP:
            bb.offset(offsetX * scale,
                    offsetY * scale,
                    offsetZ * scale);
            break;
        case DOWN:
            bb.offset(-offsetX * scale,
                    -offsetY * scale,
                    offsetZ * scale);
            break;
        case EAST:
            bb.offset(offsetY * scale,
                    -offsetX * scale,
                    offsetZ * scale);
            break;
        case WEST:
            bb.offset(-offsetY * scale,
                    offsetX * scale,
                    offsetZ * scale);
            break;
        case NORTH:
            bb.offset(-offsetX * scale,
                    -offsetZ * scale,
                    -offsetY * scale);
            break;
        case SOUTH:
            bb.offset(-offsetX * scale,
                    offsetZ * scale,
                    offsetY * scale);
            break;
        default:
            break;
        }
        return bb;
    }
}
