package moe.plushie.armourers_workshop.core.skin.geometry.collection;

import moe.plushie.armourers_workshop.api.skin.texture.ISkinPaintColor;
import moe.plushie.armourers_workshop.core.math.OpenRectangle3f;
import moe.plushie.armourers_workshop.core.math.OpenVector3i;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometrySet;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryType;
import moe.plushie.armourers_workshop.core.skin.geometry.SkinGeometryTypes;
import moe.plushie.armourers_workshop.core.skin.geometry.cube.SkinCube;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.serializer.exception.InvalidCubeTypeException;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.v12.LegacyCubeHelper;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTexturePos;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;

import java.io.IOException;

public class SkinGeometrySetV1 extends SkinGeometrySet<SkinCube> {

    private static final int STRIDE_SIZE = 4 + 4 * 6; // id/x/y/z + r/g/b/t * 6

    private final byte[] bytes;
    private final int cubeTotal;
    private final ThreadLocal<BufferSlice> bufferSlice;

    public SkinGeometrySetV1(int count) {
        this.bytes = new byte[count * STRIDE_SIZE];
        this.cubeTotal = count;
        this.bufferSlice = ThreadLocal.withInitial(() -> new BufferSlice(bytes, STRIDE_SIZE));
    }

    public static void writeToStream(SkinGeometrySet<?> geometries, IOutputStream stream) throws IOException {
        // fast encoder when geometries not any changes.
        if (geometries instanceof SkinGeometrySetV1 cubes1) {
            stream.writeInt(geometries.size());
            stream.write(cubes1.bytes);
            return;
        }
        // convert to this version.
        var paintColors = new ISkinPaintColor[6];
        stream.writeInt(geometries.size());
        for (var geometry : Collections.collect(geometries, SkinCube.class)) {
            // id/x/y/z + r/g/b/t * 6
            var blockPos = geometry.getBlockPos();
            stream.writeByte(geometry.getType().getId());
            stream.writeByte(blockPos.x());
            stream.writeByte(blockPos.y());
            stream.writeByte(blockPos.z());
            for (var dir : OpenDirection.values()) {
                var paintColor = geometry.getPaintColor(dir);
                paintColors[dir.get3DDataValue()] = paintColor;
            }
            for (int side = 0; side < 6; side++) {
                var paintColor = paintColors[side];
                stream.writeInt(paintColor.getRawValue());
            }
        }
    }

    public static SkinGeometrySetV1 readFromStream(IInputStream stream, int version, SkinPartType skinPart) throws IOException, InvalidCubeTypeException {
        var size = stream.readInt();
        var geometries = new SkinGeometrySetV1(size);
        var bufferSlice = geometries.bufferSlice.get();
        if (version >= 10) {
            var bytes = bufferSlice.getBytes();
            stream.read(bytes, 0, size * bufferSlice.stride);
            for (int i = 0; i < size; i++) {
                var slice = bufferSlice.at(i);
                if (version < 11) {
                    for (int side = 0; side < 6; side++) {
                        slice.setPaintType(side, (byte) 255);
                    }
                }
            }
            return geometries;
        }
        // 1 - 9
        for (int i = 0; i < size; i++) {
            var slice = bufferSlice.at(i);
            LegacyCubeHelper.loadLegacyCubeData(geometries, slice, stream, version, skinPart);
            for (int side = 0; side < 6; side++) {
                slice.setPaintType(side, (byte) 255);
            }
        }
        return geometries;
    }

    @Override
    public SkinCube get(int index) {
        return bufferSlice.get().at(index);
    }

    @Override
    public int size() {
        return cubeTotal;
    }

    public static class BufferSlice extends SkinCube {

        private final int stride;
        private final byte[] bytes;

        int writerIndex = 0;
        int readerIndex = 0;

        public BufferSlice(byte[] bytes, int stride) {
            this.bytes = bytes;
            this.stride = stride;
        }

        public BufferSlice at(int index) {
            this.writerIndex = index * stride;
            this.readerIndex = index * stride;
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
        public void setBoundingBox(OpenRectangle3f boundingBox) {
            var blockPos = new OpenVector3i(boundingBox.x(), boundingBox.y(), boundingBox.z());
            setX((byte) blockPos.x());
            setY((byte) blockPos.y());
            setZ((byte) blockPos.z());
        }

        @Override
        public OpenRectangle3f getBoundingBox() {
            float x = getX();
            float y = getY();
            float z = getZ();
            return new OpenRectangle3f(x, y, z, 1, 1, 1);
        }

        @Override
        public void setType(SkinGeometryType type) {
            setId((byte) type.getId());
        }

        @Override
        public SkinGeometryType getType() {
            return SkinGeometryTypes.byId(getId());
        }

        @Override
        public void setPaintColor(OpenDirection dir, SkinPaintColor paintColor) {
            int side = dir.get3DDataValue();
            int type = paintColor.getPaintType().getId();
            int rgb = paintColor.getRGB();
            setPaintType(side, (byte) type);
            setRGB(side, rgb);
        }

        @Override
        public SkinPaintColor getPaintColor(OpenDirection dir) {
            int side = dir.get3DDataValue();
            int type = getPaintType(side);
            int rgb = getRGB(side);
            return SkinPaintColor.of(rgb, SkinPaintTypes.byId(type));
        }

        @Override
        public SkinTexturePos getTexture(OpenDirection dir) {
            return null;
        }

        public void setByte(int offset, byte value) {
            bytes[writerIndex + offset] = value;
        }

        public byte getByte(int offset) {
            return bytes[readerIndex + offset];
        }

        public byte[] getBytes() {
            return bytes;
        }
    }
}
