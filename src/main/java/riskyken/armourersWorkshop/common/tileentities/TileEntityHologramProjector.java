package riskyken.armourersWorkshop.common.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;

public class TileEntityHologramProjector extends AbstractTileEntityInventory {

    private static final int INVENTORY_SIZE = 1;
    
    private static final String TAG_OFFSET_X = "offsetX";
    private static final String TAG_OFFSET_Y = "offsetY";
    private static final String TAG_OFFSET_Z = "offsetZ";
    
    private static final String TAG_ROTATION_OFFSET_X = "rotationOffsetX";
    private static final String TAG_ROTATION_OFFSET_Y = "rotationOffsetY";
    private static final String TAG_ROTATION_OFFSET_Z = "rotationOffsetZ";
    
    private static final String TAG_ROTATION_SPEED_X = "rotationSpeedX";
    private static final String TAG_ROTATION_SPEED_Y = "rotationSpeedY";
    private static final String TAG_ROTATION_SPEED_Z = "rotationSpeedZ";
    
    private int offsetX = 0;
    private int offsetY = 16;
    private int offsetZ = 0;
    
    private int rotationOffsetX = 0;
    private int rotationOffsetY = 0;
    private int rotationOffsetZ = 0;
    
    private int rotationSpeedX = 0;
    private int rotationSpeedY = 0;
    private int rotationSpeedZ = 0;
    
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
    
    public int getOffsetX() {
        return offsetX;
    }
    
    public int getOffsetY() {
        return offsetY;
    }
    
    public int getOffsetZ() {
        return offsetZ;
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
    
    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(TAG_OFFSET_X, offsetX);
        compound.setInteger(TAG_OFFSET_Y, offsetY);
        compound.setInteger(TAG_OFFSET_Z, offsetZ);
        
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
}
