package moe.plushie.armourers_workshop.core.skin.cube.impl;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinCubeType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubeTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.LegacyCubeHelper;
import moe.plushie.armourers_workshop.core.skin.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.utils.math.Vector3i;
import net.minecraft.core.Direction;

import java.io.IOException;

public class SkinCubesV1 extends SkinCubes {

    private final int cubeTotal;
    private final BufferSlice bufferSlice;

    public SkinCubesV1(int count) {
        this.bufferSlice = new BufferSlice(count);
        this.cubeTotal = count;
    }

    @Override
    public SkinCube getCube(int index) {
        return bufferSlice.at(index);
    }

    @Override
    public int getCubeTotal() {
        return cubeTotal;
    }

    public static void writeToStream(SkinCubes cubes, IOutputStream stream) throws IOException {
        int count = cubes.getCubeTotal();
        stream.writeInt(cubes.getCubeTotal());
        if (cubes instanceof SkinCubesV1) {
            stream.write(((SkinCubesV1) cubes).bufferSlice.getBuffers());
            return;
        }
        // convert to this version.
        IPaintColor[] paintColors = new IPaintColor[6];
        for (int i = 0; i < count; ++i) {
            // id/x/y/z + r/g/b/t * 6
            SkinCube cube = cubes.getCube(i);
            Vector3i pos = cube.getPosition();
            stream.writeByte(cube.getType().getId());
            stream.writeByte(pos.getX());
            stream.writeByte(pos.getY());
            stream.writeByte(pos.getZ());
            for (Direction dir : Direction.values()) {
                IPaintColor paintColor = cube.getPaintColor(dir);
                paintColors[dir.get3DDataValue()] = paintColor;
            }
            for (int side = 0; side < 6; side++) {
                IPaintColor paintColor = paintColors[side];
                stream.writeInt(paintColor.getRawValue());
            }
        }
    }

    public static SkinCubesV1 readFromStream(IInputStream stream, int version, ISkinPartType skinPart) throws IOException, InvalidCubeTypeException {
        int size = stream.readInt();
        SkinCubesV1 cubes = new SkinCubesV1(size);
        BufferSlice bufferSlice = cubes.bufferSlice;
        if (version >= 10) {
            byte[] buffers = bufferSlice.getBuffers();
            stream.read(buffers, 0, size * bufferSlice.lineSize);
            for (int i = 0; i < size; i++) {
                BufferSlice slice = bufferSlice.at(i);
                if (version < 11) {
                    for (int side = 0; side < 6; side++) {
                        slice.setPaintType(side, (byte) 255);
                    }
                }
                cubes.usedCounter.addCube(slice.getId());
            }
            return cubes;
        }
        // 1 - 9
        for (int i = 0; i < size; i++) {
            BufferSlice slice = bufferSlice.at(i);
            LegacyCubeHelper.loadLegacyCubeData(cubes, slice, stream, version, skinPart);
            for (int side = 0; side < 6; side++) {
                slice.setPaintType(side, (byte) 255);
            }
        }
        return cubes;
    }

    public static class BufferSlice extends SkinCube {

        final int lineSize = 4 + 4 * 6; // id/x/y/z + r/g/b/t * 6
        final byte[] buffers;
        final Vector3i pos = new Vector3i(0, 0, 0);

        int writerIndex = 0;
        int readerIndex = 0;

        public BufferSlice(int count) {
            this.buffers = new byte[count * lineSize];
        }

        public BufferSlice at(int index) {
            this.writerIndex = index * lineSize;
            this.readerIndex = index * lineSize;
            return this;
        }

        public byte getId() {
            return getByte(0);
        }

        public void setId(byte id) {
            setByte(0, id);
        }

        public byte getX() {
            return getByte(1);
        }

        public void setX(byte value) {
            setByte(1, value);
        }

        public byte getY() {
            return getByte(2);
        }

        public void setY(byte value) {
            setByte(2, value);
        }

        public byte getZ() {
            return getByte(3);
        }

        public void setZ(byte value) {
            setByte(3, value);
        }

        public void setR(int side, byte value) {
            setByte(4 + side * 4, value);
        }

        public byte getR(int side) {
            return getByte(4 + side * 4);
        }

        public void setG(int side, byte value) {
            setByte(5 + side * 4, value);
        }

        public byte getG(int side) {
            return getByte(5 + side * 4);
        }

        public void setB(int side, byte value) {
            setByte(6 + side * 4, value);
        }

        public byte getB(int side) {
            return getByte(6 + side * 4);
        }

        public void setPaintType(int side, byte value) {
            setByte(7 + side * 4, value);
        }

        public byte getPaintType(int side) {
            return getByte(7 + side * 4);
        }

        public int getRGB(int side) {
            int color = 0;
            color |= (getR(side) & 0xff) << 16;
            color |= (getG(side) & 0xff) << 8;
            color |= (getB(side) & 0xff);
            return color;
        }

        public void setRGB(int side, int rgb) {
            int r = (rgb >> 16) & 0xff;
            int g = (rgb >> 8) & 0xff;
            int b = rgb & 0xff;
            setR(side, (byte) r);
            setG(side, (byte) g);
            setB(side, (byte) b);
        }

        public int getColor(int side) {
            int type = getPaintType(side);
            int rgb = getRGB(side);
            return (rgb & 0xffffff) | ((type & 0xff) << 24);
        }

        @Override
        public void setPosition(Vector3i pos) {
            setX((byte) pos.getX());
            setY((byte) pos.getY());
            setZ((byte) pos.getZ());
        }

        @Override
        public Vector3i getPosition() {
            pos.setX(getX());
            pos.setY(getY());
            pos.setZ(getZ());
            return pos;
        }

        @Override
        public void setType(ISkinCubeType type) {
            setId((byte) type.getId());
        }

        @Override
        public ISkinCubeType getType() {
            return SkinCubeTypes.byId(getId());
        }

        @Override
        public void setPaintColor(Direction dir, IPaintColor paintColor) {
            int side = dir.get3DDataValue();
            int type = paintColor.getPaintType().getId();
            int rgb = paintColor.getRGB();
            setPaintType(side, (byte) type);
            setRGB(side, rgb);
        }

        @Override
        public IPaintColor getPaintColor(Direction dir) {
            int side = dir.get3DDataValue();
            int type = getPaintType(side);
            int rgb = getRGB(side);
            return PaintColor.of(rgb, SkinPaintTypes.byId(type));
        }

        public void setByte(int offset, byte value) {
            buffers[writerIndex + offset] = value;
        }

        public byte getByte(int offset) {
            return buffers[readerIndex + offset];
        }

        public byte[] getBuffers() {
            return buffers;
        }
    }
}
