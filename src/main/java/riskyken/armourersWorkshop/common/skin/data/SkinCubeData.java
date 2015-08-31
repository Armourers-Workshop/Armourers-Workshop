package riskyken.armourersWorkshop.common.skin.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;

import riskyken.armourersWorkshop.common.exception.InvalidCubeTypeException;
import riskyken.armourersWorkshop.common.skin.cubes.CubeFactory;
import riskyken.armourersWorkshop.common.skin.cubes.ICube;
import riskyken.armourersWorkshop.common.skin.cubes.LegacyCubeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinCubeData {
    private byte[] cubeId;
    private byte[] cubeLocX;
    private byte[] cubeLocY;
    private byte[] cubeLocZ;
    private byte[][] cubeColourR;
    private byte[][] cubeColourG;
    private byte[][] cubeColourB;
    private byte[] cubePaintType;
    
    @SideOnly(Side.CLIENT)
    private BitSet[] faceFlags;
    
    @SideOnly(Side.CLIENT)
    public void setFaceFlags(int index, BitSet faceFlags) {
        this.faceFlags[index] = faceFlags;
    }
    
    @SideOnly(Side.CLIENT)
    public BitSet getFaceFlags(int index) {
        return faceFlags[index];
    }
    
    public void setupFaceFlags() {
        faceFlags = new BitSet[getCubeCount()];
    }
    
    private void setCubeCount(int count) {
        cubeId = new byte[count];
        cubeLocX = new byte[count];
        cubeLocY = new byte[count];
        cubeLocZ = new byte[count];
        cubeColourR = new byte[count][6];
        cubeColourG = new byte[count][6];
        cubeColourB = new byte[count][6];
        cubePaintType = new byte[count];
    }
    
    public int getCubeCount() {
        return cubeId.length;
    }
    
    public void setCubeId(int index, byte id) {
        cubeId[index] = id;
    }
    
    public byte getCubeId(int index) {
        return cubeId[index];
    }
    
    public ICube getCube(int index) {
        return CubeFactory.INSTANCE.getCubeInstanceFormId(cubeId[index]);
    }
    
    public void setCubeColour(int index, int side, byte r, byte g, byte b) {
        cubeColourR[index][side] = r;
        cubeColourG[index][side] = g;
        cubeColourB[index][side] = b;
    }
    
    public byte[] getCubeColour(int index, int side) {
        return new byte[] {cubeColourR[index][side], cubeColourG[index][side], cubeColourB[index][side]};
    }
    
    public byte[] getCubeColourR(int index) {
        return new byte[] {
                cubeColourR[index][0],
                cubeColourR[index][1],
                cubeColourR[index][2],
                cubeColourR[index][3],
                cubeColourR[index][4],
                cubeColourR[index][5],
                        };
    }
    
    public byte[] getCubeColourG(int index) {
        return new byte[] {
                cubeColourG[index][0],
                cubeColourG[index][1],
                cubeColourG[index][2],
                cubeColourG[index][3],
                cubeColourG[index][4],
                cubeColourG[index][5],
                        };
    }
    
    public byte[] getCubeColourB(int index) {
        return new byte[] {
                cubeColourB[index][0],
                cubeColourB[index][1],
                cubeColourB[index][2],
                cubeColourB[index][3],
                cubeColourB[index][4],
                cubeColourB[index][5],
                        };
    }
    
    public void setCubeLocation(int index, byte x, byte y, byte z) {
        cubeLocX[index] = x;
        cubeLocY[index] = y;
        cubeLocZ[index] = z;
    }
    
    public byte[] getCubeLocation(int index) {
        return new byte[] {cubeLocX[index], cubeLocY[index], cubeLocZ[index]};
    }
    
    public void setCubePaintType(int index, byte paintType) {
        cubePaintType[index] = paintType;
    }
    
    public byte getCubePaintType(int index) {
        return cubePaintType[index];
    }
    
    public void writeToStream(DataOutputStream stream) throws IOException {
        stream.writeInt(cubeId.length);
        for (int i = 0; i < getCubeCount(); i++) {
            stream.writeByte(cubeId[i]);
            stream.writeByte(cubeLocX[i]);
            stream.writeByte(cubeLocY[i]);
            stream.writeByte(cubeLocZ[i]);
            for (int side = 0; side < 6; side++) {
                stream.writeByte(cubeColourR[i][side]);
                stream.writeByte(cubeColourG[i][side]);
                stream.writeByte(cubeColourB[i][side]);
            }
            stream.writeByte(cubePaintType[i]);
        }
    }
    
    public void readFromStream(DataInputStream stream, int version, SkinPart skinPart) throws IOException, InvalidCubeTypeException {
        int size = stream.readInt();
        setCubeCount(size);
        for (int i = 0; i < getCubeCount(); i++) {
            if (version < 10) {
                LegacyCubeHelper.loadLegacyCubeData(this, i, stream, version, skinPart);
            } else {
                cubeId[i] = stream.readByte();
                cubeLocX[i] = stream.readByte();
                cubeLocY[i] = stream.readByte();
                cubeLocZ[i] = stream.readByte();
                for (int side = 0; side < 6; side++) {
                    cubeColourR[i][side] = stream.readByte();
                    cubeColourG[i][side] = stream.readByte();
                    cubeColourB[i][side] = stream.readByte();
                }
                cubePaintType[i] = stream.readByte();
            }
        }
    }

    @Override
    public String toString() {
        return "SkinCubeData [cubeId=" + Arrays.toString(cubeId) + ", cubeLocX=" + Arrays.toString(cubeLocX)
                + ", cubeLocY=" + Arrays.toString(cubeLocY) + ", cubeLocZ=" + Arrays.toString(cubeLocZ)
                + ", cubeColourR=" + Arrays.deepToString(cubeColourR) + ", cubeColourG=" + Arrays.deepToString(cubeColourG)
                + ", cubeColourB=" + Arrays.deepToString(cubeColourB) + ", cubePaintType=" + Arrays.toString(cubePaintType)
                + "]";
    }
}
