package moe.plushie.armourers_workshop.common.skin.data;

import java.util.Arrays;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class SkinDye implements ISkinDye {
    
    public static final int MAX_SKIN_DYES = 8;
    private static final String TAG_SKIN_DYE = "dyeData";
    private static final String TAG_DYE = "dye";
    private static final String TAG_NAME = "name";
    private static final String TAG_RED = "r";
    private static final String TAG_GREEN = "g";
    private static final String TAG_BLUE = "b";
    private static final String TAG_TYPE = "t";
    
    private byte[][] dyes;
    private boolean[] hasDye;
    private String[] names;
    
    public SkinDye() {
        dyes = new byte[MAX_SKIN_DYES][4];
        hasDye = new boolean[MAX_SKIN_DYES];
        names = new String[MAX_SKIN_DYES];
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
    public String getDyeName(int index) {
        return names[index];
    }
    
    @Override
    public boolean haveDyeInSlot(int index) {
        return hasDye[index];
    }
    
    @Override
    public boolean hasName(int index) {
        return !StringUtils.isNullOrEmpty(names[index]);
    }
    
    @Override
    public void addDye(byte[] rgbt, String name) {
        if (rgbt.length != 4) {
            ModLogger.log(Level.WARN, "Something tried to set an invalid dye colour.");
            Thread.dumpStack();
            return;
        }
        for (int i = 0; i < hasDye.length; i++) {
            if (!hasDye[i]) {
                dyes[i] = rgbt;
                hasDye[i] = true;
                names[i] = name;
                break;
            }
        }
    }
    
    @Override
    public void addDye(byte[] rgbt) {
        addDye(rgbt, null);
    }
    
    @Override
    public void addDye(int index, byte[] rgbt, String name) {
        if (rgbt.length != 4) {
            ModLogger.log(Level.WARN, "Something tried to set an invalid dye colour.");
            Thread.dumpStack();
            return;
        }
        dyes[index] = rgbt;
        hasDye[index] = true;
        names[index] = name;
    }
    
    @Override
    public void addDye(int index, byte[] rgbt) {
        addDye(index, rgbt, null);
    }
    
    @Override
    public void removeDye(int index) {
        dyes[index] = new byte[] {(byte)0, (byte)0, (byte)0, (byte)255};
        hasDye[index] = false;
        names[index] = null;
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
                if (!StringUtils.isNullOrEmpty(names[i])) {
                    buf.writeBoolean(true);
                    ByteBufUtils.writeUTF8String(buf, names[i]);
                } else {
                    buf.writeBoolean(false);
                }
            }
        }
    }
    
    @Override
    public void readFromBuf(ByteBuf buf) {
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            hasDye[i] = buf.readBoolean();
            if (hasDye[i]) {
                buf.readBytes(dyes[i]);
                if (buf.readBoolean()) {
                    names[i] = ByteBufUtils.readUTF8String(buf);
                }
            }
        }
    }
    
    public NBTTagCompound writeToCompound(NBTTagCompound compound) {
        NBTTagCompound dyeCompound = new NBTTagCompound();
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            if (hasDye[i]) {
                dyeCompound.setByte(TAG_DYE + i + TAG_RED, dyes[i][0]);
                dyeCompound.setByte(TAG_DYE + i + TAG_GREEN, dyes[i][1]);
                dyeCompound.setByte(TAG_DYE + i + TAG_BLUE, dyes[i][2]);
                dyeCompound.setByte(TAG_DYE + i + TAG_TYPE, dyes[i][3]);
                if (!StringUtils.isNullOrEmpty(names[i])) {
                    dyeCompound.setString(TAG_NAME + i, names[i]);
                }
            }
        }
        compound.setTag(TAG_SKIN_DYE, dyeCompound);
        return compound;
    }

    public void readFromCompound(NBTTagCompound compound) {
        NBTTagCompound dyeCompound = compound.getCompoundTag(TAG_SKIN_DYE);
        for (int i = 0; i < MAX_SKIN_DYES; i++) {
            // Load old dye code.
            if (dyeCompound.hasKey(TAG_DYE + i, Constants.NBT.TAG_BYTE_ARRAY)) {
                dyes[i] = dyeCompound.getByteArray(TAG_DYE + i);
                
                if (dyes[i].length == 4) {
                    hasDye[i] = true;
                } else {
                    dyes[i] = new byte[] {0,0,0,0};
                }
                if (dyeCompound.hasKey(TAG_NAME + i, NBT.TAG_STRING)) {
                    names[i] = dyeCompound.getString(TAG_NAME + i);
                }
            }
            // End old dye loading code.
            if (dyeCompound.hasKey(TAG_DYE + i + TAG_RED, Constants.NBT.TAG_BYTE)) {
                if (dyeCompound.hasKey(TAG_DYE + i + TAG_GREEN, Constants.NBT.TAG_BYTE)) {
                    if (dyeCompound.hasKey(TAG_DYE + i + TAG_BLUE, Constants.NBT.TAG_BYTE)) {
                        if (dyeCompound.hasKey(TAG_DYE + i + TAG_TYPE, Constants.NBT.TAG_BYTE)) {
                            dyes[i] = new byte[] {0,0,0,0};
                            hasDye[i] = true;
                            dyes[i][0] = dyeCompound.getByte(TAG_DYE + i + TAG_RED);
                            dyes[i][1] = dyeCompound.getByte(TAG_DYE + i + TAG_GREEN);
                            dyes[i][2] = dyeCompound.getByte(TAG_DYE + i + TAG_BLUE);
                            dyes[i][3] = dyeCompound.getByte(TAG_DYE + i + TAG_TYPE);
                            if (dyeCompound.hasKey(TAG_NAME + i, NBT.TAG_STRING)) {
                                names[i] = dyeCompound.getString(TAG_NAME + i);
                            }
                        }
                    }
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
        result = prime * result + Arrays.hashCode(names);
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
        if (!Arrays.equals(names, other.names))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SkinDye [dyes=" + Arrays.toString(dyes) + ", hasDye=" + Arrays.toString(hasDye) + ", names=" + Arrays.toString(names) + "]";
    }
}
