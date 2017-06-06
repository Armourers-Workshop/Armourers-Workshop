package riskyken.armourersWorkshop.common.skin.data;

import java.util.Arrays;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.utils.ModLogger;

public class SkinDye implements ISkinDye {
    
    public static final int MAX_SKIN_DYES = 8;
    private static final String TAG_SKIN_DYE = "dyeData";
    private static final String TAG_DYE= "dye";
    
    private byte[][] dyes;
    private boolean[] hasDye;
    
    public SkinDye() {
        dyes = new byte[MAX_SKIN_DYES][4];
        hasDye = new boolean[MAX_SKIN_DYES];
    }
    
    public SkinDye(ISkinDye skinDye) {
        this();
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            if(skinDye.haveDyeInSlot(i)) {
                addDye(i, skinDye.getDyeColour(i));
            }
        }
    }

    @Override
    public byte[] getDyeColour(int index) {
        return dyes[index];
    }
    
    @Override
    public boolean haveDyeInSlot(int index) {
        return hasDye[index];
    }
    
    @Override
    public void addDye(byte[] rgbt) {
        if (rgbt.length != 4) {
            ModLogger.log(Level.WARN, "Something tried to set an invalid dye colour.");
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < hasDye.length; i++) {
            if (!hasDye[i]) {
                dyes[i] = rgbt;
                hasDye[i] = true;
                break;
            }
        }
    }
    
    @Override
    public void addDye(int index, byte[] rgbt) {
        if (rgbt.length != 4) {
            ModLogger.log(Level.WARN, "Something tried to set an invalid dye colour.");
            Thread.dumpStack();
            return;
        }
        dyes[index] = rgbt;
        hasDye[index] = true;
    }
    
    @Override
    public void removeDye(int index) {
        dyes[index] = new byte[] {(byte)0, (byte)0, (byte)0, (byte)255};
        hasDye[index] = false;
    }

    @Override
    public int getNumberOfDyes() {
        int count = 0;
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            if (hasDye[i]) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public void writeToBuf(ByteBuf buf) {
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            buf.writeBoolean(hasDye[i]);
            if (hasDye[i]) {
                buf.writeBytes(dyes[i]);
            }
        }
    }
    
    @Override
    public void readFromBuf(ByteBuf buf) {
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            hasDye[i] = buf.readBoolean();
            if (hasDye[i]) {
                buf.readBytes(dyes[i]);
            }
        }
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        NBTTagCompound dyeCompound = new NBTTagCompound();
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            if (hasDye[i]) {
                dyeCompound.setByteArray(TAG_DYE + i, dyes[i]);
            }
        }
        compound.setTag(TAG_SKIN_DYE, dyeCompound);
    }

    public void readFromCompound(NBTTagCompound compound) {
        NBTTagCompound dyeCompound = compound.getCompoundTag(TAG_SKIN_DYE);
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            if (dyeCompound.hasKey(TAG_DYE + i, Constants.NBT.TAG_BYTE_ARRAY)) {
                dyes[i] = dyeCompound.getByteArray(TAG_DYE + i);
                if (dyes[i].length == 4) {
                    hasDye[i] = true;
                } else {
                    dyes[i] = new byte[] {0,0,0,0};
                }
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(dyes);
        result = prime * result + Arrays.hashCode(hasDye);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SkinDye other = (SkinDye) obj;
        if (!Arrays.deepEquals(dyes, other.dyes))
            return false;
        if (!Arrays.equals(hasDye, other.hasDye))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String returnString = "SkinDye [dyes=";
        returnString += Arrays.deepToString(dyes);
        returnString += Arrays.toString(hasDye);
        returnString += "]";
        return returnString;
    }
}
