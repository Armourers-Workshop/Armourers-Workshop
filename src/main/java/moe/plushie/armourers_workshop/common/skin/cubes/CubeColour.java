package moe.plushie.armourers_workshop.common.skin.cubes;

import java.awt.Color;
import java.util.Arrays;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import net.minecraft.nbt.NBTTagCompound;

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
    public byte getRed(int side) {
        return r[side];
    }

    @Override
    public byte getGreen(int side) {
        return g[side];
    }

    @Override
    public byte getBlue(int side) {
        return b[side];
    }
    
    @Override
    public byte getPaintType(int side) {
        return t[side];
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
    public void setColour(int colour, int side) {
        //t[side] = (byte) 255;
        r[side] = (byte) (colour >> 16 & 0xff);
        g[side] = (byte) (colour >> 8 & 0xff);
        b[side] = (byte) (colour & 0xff);
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
    public void setRed(byte red, int side) {
        r[side] = red;
    }

    @Override
    public void setGreen(byte green, int side) {
        g[side] = green;
    }

    @Override
    public void setBlue(byte blue, int side) {
        b[side] = blue;
    }
    
    @Override
    public void setPaintType(byte type, int side) {
        t[side] = type;
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
