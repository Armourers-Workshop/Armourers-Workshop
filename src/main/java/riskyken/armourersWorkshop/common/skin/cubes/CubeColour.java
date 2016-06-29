package riskyken.armourersWorkshop.common.skin.cubes;

import java.awt.Color;
import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;

public class CubeColour implements ICubeColour {

    private static final String TAG_RED = "r";
    private static final String TAG_GREEN = "g";
    private static final String TAG_BLUE = "b";
    private static final String TAG_TYPE = "t";
    
    private byte[] r;
    private byte[] g;
    private byte[] b;
    private byte[] t;
    
    public CubeColour() {
        initArray();
    }
    
    public CubeColour(ICubeColour cubeColour) {
        r = cubeColour.getRed().clone();
        g = cubeColour.getGreen().clone();
        b = cubeColour.getBlue().clone();
        t = cubeColour.getPaintType().clone();
    }
    
    public CubeColour(int colour) {
        t = new byte[6];
        r = new byte[6];
        g = new byte[6];
        b = new byte[6];
        for (int i = 0; i < 6; i++) {
            t[i] = (byte) 255;
            r[i] = (byte) (colour >> 16 & 0xff);
            g[i] = (byte) (colour >> 8 & 0xff);
            b[i] = (byte) (colour & 0xff);
        }
    }
    
    private void initArray() {
        t = new byte[6];
        r = new byte[6];
        g = new byte[6];
        b = new byte[6];
        for (int i = 0; i < 6; i++) {
            t[i] = (byte)255;
            r[i] = (byte)255;
            g[i] = (byte)255;
            b[i] = (byte)255;
        }
    }
    
    @Override
    public byte getRed(EnumFacing side) {
        return r[side.ordinal()];
    }

    @Override
    public byte getGreen(EnumFacing side) {
        return g[side.ordinal()];
    }

    @Override
    public byte getBlue(EnumFacing side) {
        return b[side.ordinal()];
    }
    
    @Override
    public byte getPaintType(EnumFacing side) {
        return t[side.ordinal()];
    }

    @Override
    public byte[] getRed() {
        return r;
    }

    @Override
    public byte[] getGreen() {
        return g;
    }

    @Override
    public byte[] getBlue() {
        return b;
    }
    
    @Override
    public byte[] getPaintType() {
        return t;
    }
    
    @Override
    public void setColour(int colour, EnumFacing side) {
        //t[side] = (byte) 255;
        r[side.ordinal()] = (byte) (colour >> 16 & 0xff);
        g[side.ordinal()] = (byte) (colour >> 8 & 0xff);
        b[side.ordinal()] = (byte) (colour & 0xff);
    }
    
    public int getColour(int side) {
        Color c = new Color(r[side] & 0xFF, g[side] & 0xFF, b[side] & 0xFF);
        return c.getRGB();
    }
    
    @Deprecated
    @Override
    public void setColour(int colour) {
        for (int i = 0; i < 6; i++) {
            //t[i] = (byte) 255;
            r[i] = (byte) (colour >> 16 & 0xff);
            g[i] = (byte) (colour >> 8 & 0xff);
            b[i] = (byte) (colour & 0xff);
        }
    }

    @Override
    public void setRed(byte red, EnumFacing side) {
        r[side.ordinal()] = red;
    }

    @Override
    public void setGreen(byte green, EnumFacing side) {
        g[side.ordinal()] = green;
    }

    @Override
    public void setBlue(byte blue, EnumFacing side) {
        b[side.ordinal()] = blue;
    }
    
    @Override
    public void setPaintType(byte type, EnumFacing side) {
        t[side.ordinal()] = type;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        for (int i = 0; i < 6; i++) {
            r[i] = compound.getByte(TAG_RED + i);
            g[i] = compound.getByte(TAG_GREEN + i);
            b[i] = compound.getByte(TAG_BLUE + i);
            if (compound.hasKey(TAG_TYPE + i)) {
                t[i] = compound.getByte(TAG_TYPE + i);
            } else {
                t[i] = (byte)255;
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        for (int i = 0; i < 6; i++) {
            compound.setByte(TAG_RED + i, r[i]);
            compound.setByte(TAG_GREEN + i, g[i]);
            compound.setByte(TAG_BLUE + i, b[i]);
            compound.setByte(TAG_TYPE + i, t[i]);
        }
    }

    @Override
    public String toString() {
        return "CubeColour [r=" + Arrays.toString(r) + ", g="
                + Arrays.toString(g) + ", b=" + Arrays.toString(b) + ", t="
                + Arrays.toString(t) + "]";
    }
}
