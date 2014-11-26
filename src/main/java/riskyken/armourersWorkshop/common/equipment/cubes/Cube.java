package riskyken.armourersWorkshop.common.equipment.cubes;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;

import org.apache.logging.log4j.Level;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.utils.ModLogger;

public class Cube {

    protected static final String TAG_X = "x";
    protected static final String TAG_Y = "y";
    protected static final String TAG_Z = "z";
    
    protected byte id = -1;
    
    public byte x;
    public byte y;
    public byte z;
    
    public boolean isGlowing() {
        return false;
    }
    
    public void setId(byte id) {
        if (this.id != -1) {
            ModLogger.log(Level.WARN, "Resetting cube id.");
        }
        this.id = id;
    }
    
    public byte getId() {
        return id;
    }
    
    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(x);
        buf.writeByte(y);
        buf.writeByte(z);
    }
    
    private void readFromBuf(ByteBuf buf) {
        x = buf.readByte();
        y = buf.readByte();
        z = buf.readByte();
    }
    
    public void writeToNBT(NBTTagCompound compound) {
        compound.setByte(TAG_X, x);
        compound.setByte(TAG_Y, y);
        compound.setByte(TAG_Z, z);
    }
    
    private void readFromNBT(NBTTagCompound compound) {
        x = compound.getByte(TAG_X);
        y = compound.getByte(TAG_Y);
        z = compound.getByte(TAG_Z);
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeByte(x);
        stream.writeByte(y);
        stream.writeByte(z);
    }
    
    private void readFromStream(DataInputStream stream, int version, EnumEquipmentPart part) throws IOException {
        x = stream.readByte();
        y = stream.readByte();
        z = stream.readByte();
        
        if (version < 2) {
            switch (part) {
            case WEAPON:
                y -= 1;
                break;
            case SKIRT:
                y -= 1;
                break;
            case LEFT_LEG:
                y -= 1;
                break;
            case RIGHT_LEG:
                y -= 1;
                break;
            case LEFT_FOOT:
                y -= 1;
                break;
            case RIGHT_FOOT:
                y -= 1;
                break;
            default:
                break;
            }
        }
    }
}
