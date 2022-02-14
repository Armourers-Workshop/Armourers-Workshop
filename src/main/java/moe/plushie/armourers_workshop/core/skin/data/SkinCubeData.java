package moe.plushie.armourers_workshop.core.skin.data;

import moe.plushie.armourers_workshop.core.api.ISkinCube;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.serialize.LegacyCubeHelper;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.Rectangle3i;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SkinCubeData {

    // 0 = down, 1 = up, 2 = south, 3 = north, 4 = west, 5 = east
    public final static byte[] DIRECTION_TO_SIDE = {
            0, // down(0)
            1, // up(1)
            3, // north(2)
            2, // south(3)
            4, // west(4)
            5, // east(5)
    };

    private byte[] cubeId;
    private byte[] cubeLocX;
    private byte[] cubeLocY;
    private byte[] cubeLocZ;
    private byte[][] cubeColourR;
    private byte[][] cubeColourG;
    private byte[][] cubeColourB;
    private byte[][] cubePaintType;

    public int getCubeCount() {
        return cubeId.length;
    }

    public void setCubeCount(int count) {
        cubeId = new byte[count];
        cubeLocX = new byte[count];
        cubeLocY = new byte[count];
        cubeLocZ = new byte[count];
        cubeColourR = new byte[count][6];
        cubeColourG = new byte[count][6];
        cubeColourB = new byte[count][6];
        cubePaintType = new byte[count][6];
    }

    public void setCubeId(int index, byte id) {
        cubeId[index] = id;
    }

    public byte getCubeId(int index) {
        return cubeId[index];
    }

    public ISkinCube getCube(int index) {
        return SkinCubes.byId(cubeId[index]);
    }

    public void setCubeColour(int index, int side, byte r, byte g, byte b) {
        cubeColourR[index][side] = r;
        cubeColourG[index][side] = g;
        cubeColourB[index][side] = b;
    }
//
//    public byte[] getCubeColour(int index, int side) {
//        return new byte[]{cubeColourR[index][side], cubeColourG[index][side], cubeColourB[index][side]};
//    }
//
//    public byte[] getCubeColourR(int index) {
//        return new byte[]{
//                cubeColourR[index][0],
//                cubeColourR[index][1],
//                cubeColourR[index][2],
//                cubeColourR[index][3],
//                cubeColourR[index][4],
//                cubeColourR[index][5],
//        };
//    }
//
//    public byte[] getCubeColourG(int index) {
//        return new byte[]{
//                cubeColourG[index][0],
//                cubeColourG[index][1],
//                cubeColourG[index][2],
//                cubeColourG[index][3],
//                cubeColourG[index][4],
//                cubeColourG[index][5],
//        };
//    }
//
//    public byte[] getCubeColourB(int index) {
//        return new byte[]{
//                cubeColourB[index][0],
//                cubeColourB[index][1],
//                cubeColourB[index][2],
//                cubeColourB[index][3],
//                cubeColourB[index][4],
//                cubeColourB[index][5],
//        };
//    }

    public void setCubeLocation(int index, byte x, byte y, byte z) {
        cubeLocX[index] = x;
        cubeLocY[index] = y;
        cubeLocZ[index] = z;
    }

    public byte[] getCubeLocation(int index) {
        return new byte[]{cubeLocX[index], cubeLocY[index], cubeLocZ[index]};
    }

    public byte getCubePosX(int index) {
        return cubeLocX[index];
    }

    public byte getCubePosY(int index) {
        return cubeLocY[index];
    }

    public byte getCubePosZ(int index) {
        return cubeLocZ[index];
    }


    @OnlyIn(Dist.CLIENT)
    public ColouredFace getCubeFace(int index, Direction dir) {
        byte side = DIRECTION_TO_SIDE[dir.get3DDataValue()];
        ISkinCube cube = getCube(index);
        ISkinPaintType paintType = SkinPaintTypes.byId(cubePaintType[index][side]);
        byte x = cubeLocX[index];
        byte y = cubeLocY[index];
        byte z = cubeLocZ[index];

        int color = 0x7f000000;
        color |= (cubeColourR[index][side] & 0xff) << 16;
        color |= (cubeColourG[index][side] & 0xff) << 8;
        color |= (cubeColourB[index][side] & 0xff);
        if (!cube.isGlass()) {
            color |= 0xff000000;
        }

        return new ColouredFace(x, y, z, new PaintColor(color, paintType), dir, cube);
    }

    public CustomVoxelShape getRenderShape() {
        if (cubeLocX == null) {
            return CustomVoxelShape.empty();
        }
        CustomVoxelShape shape = CustomVoxelShape.empty();
        int count = cubeLocX.length;
        if (count == 0) {
            return shape;
        }
        for (int i = 0; i < count; ++i) {
            byte x = cubeLocX[i];
            byte y = cubeLocY[i];
            byte z = cubeLocZ[i];
            shape.add(x, y, z, 1, 1, 1);
        }
        shape.optimize();
        return shape;
    }

    public Rectangle3i getBounds() {
        byte minX = 127;
        byte minY = 127;
        byte minZ = 127;
        byte maxX = -127;
        byte maxY = -127;
        byte maxZ = -127;
        int count = cubeLocX.length;
        for (int i = 0; i < count; ++i) {
            byte x = cubeLocX[i];
            byte y = cubeLocY[i];
            byte z = cubeLocZ[i];
            if (minX > x) minX = x;
            if (minY > y) minY = y;
            if (minZ > z) minZ = z;
            if (maxX < x) maxX = x;
            if (maxY < y) maxY = y;
            if (maxZ < z) maxZ = z;
        }
        return new Rectangle3i(minX, minY, minZ, maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);
    }


    public void setCubePaintType(int index, int side, byte paintType) {
        cubePaintType[index][side] = paintType;
    }

    public byte getCubePaintType(int index, int side) {
        return cubePaintType[index][side];
    }

    public byte[] getCubePaintType(int index) {
        return new byte[]{
                cubePaintType[index][0],
                cubePaintType[index][1],
                cubePaintType[index][2],
                cubePaintType[index][3],
                cubePaintType[index][4],
                cubePaintType[index][5],
        };
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
                stream.writeByte(cubePaintType[i][side]);
            }
        }
    }

    public void readFromStream(DataInputStream stream, int version, ISkinPartType skinPart) throws IOException, InvalidCubeTypeException {
        int size = stream.readInt();
        setCubeCount(size);
        for (int i = 0; i < getCubeCount(); i++) {
            if (version < 10) {
                LegacyCubeHelper.loadLegacyCubeData(this, i, stream, version, skinPart);
                for (int side = 0; side < 6; side++) {
                    cubePaintType[i][side] = (byte) 255;
                }
            } else {
                cubeId[i] = stream.readByte();
                cubeLocX[i] = stream.readByte();
                cubeLocY[i] = stream.readByte();
                cubeLocZ[i] = stream.readByte();
                for (int side = 0; side < 6; side++) {
                    cubeColourR[i][side] = stream.readByte();
                    cubeColourG[i][side] = stream.readByte();
                    cubeColourB[i][side] = stream.readByte();
                    cubePaintType[i][side] = stream.readByte();
                }
            }
            if (version < 11) {
                for (int side = 0; side < 6; side++) {
                    cubePaintType[i][side] = (byte) 255;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "SkinCubeData [cubeId=" + Arrays.toString(cubeId) + ", cubeLocX=" + Arrays.toString(cubeLocX)
                + ", cubeLocY=" + Arrays.toString(cubeLocY) + ", cubeLocZ=" + Arrays.toString(cubeLocZ)
                + ", cubeColourR=" + Arrays.deepToString(cubeColourR) + ", cubeColourG=" + Arrays.deepToString(cubeColourG)
                + ", cubeColourB=" + Arrays.deepToString(cubeColourB) + ", cubePaintType=" + Arrays.deepToString(cubePaintType)
                + "]";
    }
}
